import java.util.HashMap;

/**
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.
 * 
 * This class holds an enumeration of all command words known to the game.
 * It is used to recognise commands as they are typed in.
 *
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class CommandWords
{
    // A mapping between a command word and the CommandWord
    // associated with it.
    private HashMap<String, CommandWord> validCommands;

    /**
     * Constructor - initialise the command words.
     */
    public CommandWords()
    {
        validCommands = new HashMap<>();
        for(CommandWord command : CommandWord.values()) {
            if(command != CommandWord.UNKNOWN) {
                validCommands.put(command.toString(), command);
            }
        }
    }

    /**
     * Find the CommandWord associated with a command word.
     * @param commandWord The word to look up.
     * @return The CommandWord correspondng to commandWord, or UNKNOWN
     *         if it is not a valid command word.
     */
    public CommandWord getCommandWord(String commandWord)
    {
        CommandWord command = validCommands.get(commandWord);
        if(command != null) {
            return command;
        }
        else {
            return CommandWord.UNKNOWN;
        }
    }
    
    /**
     * Check whether a given String is a valid command word. 
     * @return true if it is, false if it isn't.
     */
    public boolean isCommand(String aString)
    {
        return validCommands.containsKey(aString);
    }

    /**
     * Print all valid commands to System.out.
     */
    public void showAll() 
    {
        for(String command : validCommands.keySet()) {
            System.out.print(command + "  ");
        }
        System.out.println();
    }

    public void commandsDesc (){
        for(String command : validCommands.keySet()) {
            System.out.println(command.toString());
            switch (command) {
                case "help":
                    System.out.println("List the commands.");
                    break;

                case "morehelp":
                    System.out.println("List the commands with their descriptions.");
                    break;

                case "go":
                    System.out.println("Move to the room specified.\nSecond word specifies the room.");
                    break;

                case "look":
                    System.out.println("List the exits from the current room.");
                    break;

                case "die":
                    System.out.println("Kill command.\nYou will find out the true meaning of the command after you use it.");
                    break;

                case "inspect":
                    System.out.println("Without second word inspects the room. Otherwise inspects the box specified.\n" +
                            "Can use INSPECT BACKPACK to see what you currently have in your possession.");
                    break;

                case "take":
                    System.out.println("Takes the item specified from the box.\nNeed to inspect the box before you use this command.");
                    break;

                case "use":
                    System.out.println("By using this command you can USE items from your backpack. Second word specifies an item to use.");

                case "back":
                    System.out.println("Go back to the room you came from.");
                    break;

                case "quit":
                    System.out.println("Quits the game.");
                    break;
            }
            System.out.println("----------------------------------------------------------");
        }
    }
}
