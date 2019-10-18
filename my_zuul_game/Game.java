import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game 
{
    private Random rand = new Random();
    private Parser parser;
    private Player player;
    private Room currentRoom;
    // stores rooms in ArrayList so player can go back where they came from
    private Stack<Room> memory;
    private ArrayList<String> notes;
    // score that counts number of coins taken
    private int score;
    private int level;
    private int guessCount;
    private int roomNo;
    private int countCommands;
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        roomNo = 1;
        level = 1;
        currentRoom = createRoom();
        memory = new Stack<>();
        memory.add(currentRoom);
        createNotes();
        parser = new Parser();
        player = new Player(10, 1, 60, 100);
        player.setName();
        score = 0;
        guessCount = 0;
        countCommands = 0;
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private Room createRoom()
    {
        int numberOfExits = rand.nextInt(3) + 1;
        String roomName = "room" + roomNo;
        Room room = new Room(roomName, "in the " + roomName, level * 100, level * 10);
        for (int i = 1; i <= numberOfExits; i++) {
            room.setExit("exit" + i, null);
        }
        roomNo++;
        return room;
    }

    private void createNotes (){
        notes = new ArrayList<>();
        addNote("If you have some food left, you can use it to FEED MONSTER.\n" +
                "Then it will let you be for a while.");
        addNote("If you find box with no items, it is not a box, but a secret door.\n" +
                "USE KEY to unlock the door.");
        addNote("Every time you open a secret door, you enter another level of the game and your character upgrades.\n" +
                "But higher the level, harder the game.");
    }

    private void addNote (String note){
        if (note != null && note.length() > 0)
            notes.add(note);
    }

    public static void main (String[] args){
        Game game = new Game();
        game.play();
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    private void play()
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            if (guessCount == 5) {
                System.out.println("Dead dead dead dead. You are dead.");
                break;
            }
            Command command = parser.getCommand();
            finished = processCommand(command);
            if (countCommands > 1 && currentRoom.getMonster() != null && !currentRoom.getMonster().getHasEaten()){
                boolean dead = playerTakesDamage();
                if (dead)
                    break;
            }
        }
        System.out.println("You died.\nGAME OVER.");
        player.getStats();
        System.out.printf("\nScore: %d\n", score);
        System.out.println("Thank you for playing. Bey bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.\n\nUpgrades made by Samuel Ivan\n");
        System.out.println("Type '" + CommandWord.HELP.toString().toUpperCase() + " or " + CommandWord.HELPMORE.toString().toUpperCase() + "' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;

            case HELP:
                printHelp();
                break;

            case HELPMORE:
                printMoreHelp();
                break;

            case GO:
                // if monster is not feed, attack player
                countCommands++;
                wantToQuit = goRoom(command);
                break;

            case LOOK:
                countCommands++;
                look();
                break;

            case DIE:
                countCommands++;
                wantToQuit = die();
                break;

            case INSPECT:
                countCommands++;
                inspect(command);
                break;

            case TAKE:
                countCommands++;
                if (player.backpackHasSpace())
                    takeItem(command);
                else{
                    System.out.println("Your backpack is full.\nUSE some of the items in the backpack in order to TAKE more items.");
                    player.inspectBackpack();
                }
                break;

            case USE:
                countCommands++;
                wantToQuit = useItem(command);
                break;

            case BACK:
                goBack(command);
                break;

            case FEED:
                countCommands++;
                feedMonster(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
        System.out.println("Use INSPECT BACKPACK to look what you have in your backpack.");
        System.out.println("Type all commands in lower case.");
    }

    private void printMoreHelp (){
        parser.showCommandsInfo();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private boolean goRoom(Command command)
    {
        boolean dead = false;
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return false;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Boolean hasDoor = currentRoom.getExit(direction);

        if (!hasDoor) {
            System.out.println("There is no door!");
        }
        else {
            if (currentRoom.getMonster() != null && !currentRoom.getMonster().getHasEaten()) {
                dead = playerTakesDamage();
            }
            Room nextRoom = currentRoom.checkRoomCreated(direction);
            if (nextRoom == null) {
                nextRoom = createRoom();
                currentRoom.updateExit(direction, nextRoom);
                player.increaseExperience(1);
            }
            if (currentRoom.getMonster() != null)
                currentRoom.getMonster().setHasEaten(false);
            countCommands = 0;
            currentRoom = nextRoom;
            memory.push(currentRoom);
            System.out.println(currentRoom.getLongDescription());
        }
        return dead;
    }

    private void goBack(Command command) {
        if (command.hasSecondWord())
            System.out.println("You do not need to define second parameter.");
        if (memory.size() > 1) {
            if (currentRoom.getMonster() != null)
                currentRoom.getMonster().setHasEaten(false);
            memory.pop();
            currentRoom = memory.peek();
            System.out.println(currentRoom.getLongDescription());
            countCommands = 0;
        }
        else
            System.out.println("You can't go back anymore.");
    }

    private void guessWarning (){
        guessCount++;
        System.out.println("This is not a guessing game. Guessing is gonna get you killed.\n" +
                "Use HELP, read some NOTES or just type DIE if you do not know what to do.");
        if (guessCount == 4){
            System.out.println("One more try and you are dead. So tread carefully.");
        }
    }

    private void inspect(Command command){
        if (command.hasSecondWord()){
            String word = command.getSecondWord();
            if (word.contains("box")) {
                currentRoom.setCurrentBox(currentRoom.inspectRoomBox(command.getSecondWord()));
                if (currentRoom.getCurrentBox() == null)
                    guessWarning();
            }
            else if (word.equals("backpack"))
                player.inspectBackpack();
            else
                guessWarning();
        }
        else
            currentRoom.inspectRoom();
    }

    private void takeItem(Command command){
        String itemName;
        if (command.hasSecondWord()){
            itemName = currentRoom.takeItemFromTheBox(currentRoom.getCurrentBox(), command.getSecondWord());
            if (itemName == null) {
                guessWarning();
            }
            else if (itemName.equals("note")){
                System.out.printf("Note\nRead Carefully!\nSometimes you can find here something useful.\n%s\n", notes.get(rand.nextInt(notes.size())));
            }
            else if (itemName.equals("coin")) {
                score += level + (player.getExperience() / 10);
                player.getStats();
                System.out.printf("\nYou have taken %d coins that has been added to your score.\nScore: %d\n", level, score);
            }
            else
                System.out.println(player.addItem(itemName));
        }
        else
            System.out.println("What do you want to take?");
    }

    private void feedMonster(Command command){
        if (command.hasSecondWord()) {
            if (command.getSecondWord().equals("monster")) {
                boolean isValid = player.useItem("food");
                if (isValid) {
                    System.out.println("You have used FOOD");
                    if (currentRoom.getMonster() != null)
                        currentRoom.getMonster().setHasEaten(true);
                    else
                        System.out.println("No monsters in the room.");
                }
                else
                    System.out.println("You do not have FOOD in your backpack.");
            }
            else
                guessWarning();
        }
        else
            System.out.println("FEED what?");
    }

    private boolean useItem (Command command){
        String itemName;
        Scanner sc = new Scanner(System.in);
        if (command.hasSecondWord()){
            itemName = command.getSecondWord();
            boolean isValid = player.useItem(itemName);
            System.out.println("You have used " + itemName.toUpperCase());
            if (isValid) {
                System.out.println("Once you use an item you can't get it back.");
                if (itemName.equals("pen")) {
                    System.out.println("Enter a message you want to leave for future adventurers... or any last words you have before you die in this labyrinth.");
                    String message = sc.next();
                    addNote(message);
                } else if (itemName.equals("weapon")) {
                    int playerDamage = player.attack();
                    int damage = currentRoom.killMonster(playerDamage);
                    // System.out.printf("\nYou have attacked monster with damage %d.\n", playerDamage);
                    countCommands = 1;
                    if (damage == 0){
                        player.increaseExperience(1);
                        System.out.println("There are no monsters in the room.");
                    }
                    else {
                        System.out.printf("\nYou have attacked monster with damage %d.\n", playerDamage);
                        System.out.printf("\nMonster has attacked you with damage %d.\n", damage);
                        return player.decreaseHp(damage);
                    }
                    player.getStats();
                }
                else if (itemName.equals("key")){
                    if (currentRoom.getCurrentBox().isSecretDoor()) {
                        level++;
                        player.increaseCapacity(5);
                        player.increaseHp(100);
                        player.increaseDamage(10);
                        player.increaseExperience(10);
                        player.getStats();
                        memory.clear();
                        currentRoom = createRoom();
                        memory.push(currentRoom);
                    }
                }
            }
            else
                System.out.printf("\nYou do not have %s in your backpack.\n", itemName.toUpperCase());
        }
        else
            System.out.println("What do you want to use?");
        return false;
    }

    private void look (){
        System.out.println(currentRoom.getLongDescription());
    }

    private boolean die () {
        Scanner sc = new Scanner(System.in);
        String input;
        // quiz game?? in order to die
        System.out.println("Type password (if you want to use this command): ");
        input = sc.next();

        if (input.equals("password")) {
            System.out.println("You randomly run into walls with your head. After few tries you are finally dead.\n" +
                    "That was a spectacular finish of this game, player!!");
            score = Integer.MIN_VALUE;
            return true;
        }
        else
            System.out.println("Wrong password. Try again later.");
        return false;
    }

    private boolean playerTakesDamage (){
        int damage = 0;
        if (currentRoom.getMonster() != null) {
            damage = currentRoom.getMonster().attack();
            System.out.printf("\nMonster has attacked you with damage %d.\n", damage);
        }
        return player.decreaseHp(damage);
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
