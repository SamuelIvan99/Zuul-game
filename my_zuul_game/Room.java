import java.util.*;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Room 
{
    private String name;
    private String description;
    private HashMap<String, Room> exits;
    private ArrayList<Box> boxes;
    private Monster monster;
    private Box currentBox;
    private Random rand;
    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String name, String description, int monsterHp, int monsterDamage)
    {
        rand = new Random();
        this.name = name;
        this.description = description;
        createMonster(monsterDamage, monsterHp);
        exits = new HashMap<>();
        boxes = new ArrayList<>();
        currentBox = null;
        setBoxes();
    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    public void updateExit(String direction, Room neighbor)
    {
        exits.replace(direction, neighbor);
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    private void setBoxes (){
        int numOfBoxes = rand.nextInt(3) + 1;
        for (int i = 1; i <= numOfBoxes; i++){
            Box box = new Box("box" + i);
            boxes.add(box);
        }
    }

    public void inspectRoom (){
        if (boxes.size() > 0) {
            boxes.forEach(box -> System.out.print(box.getName() + " "));
            System.out.println();
        }
        else
            System.out.println("No boxes in the room.");
        if (monster != null)
            System.out.println("monster");
    }

    public Box inspectRoomBox(String name){
        boolean found = false;
        Box box = null;
        Iterator<Box> it = boxes.iterator();
        while (it.hasNext() && !found){
            Box element = it.next();
            if (element.getName().equals(name)) {
                found = true;
                box = element;
            }
        }
        if (box != null)
            box.inspectBox();
        return box;
    }

    public String takeItemFromTheBox (Box box, String itemName){
        String returnItem = null;
        for (int i = 0; i < boxes.size(); i++){
            if (boxes.get(i).equals(box)) {
                returnItem = boxes.get(i).takeItem(itemName);
                if (returnItem != null)
                    removeBox(boxes.get(i));
                break;
            }
        }
        return returnItem;
    }

    private void removeBox (Box box){
        if (box != null)
            boxes.remove(box);
    }

    public Box getCurrentBox() {
        return currentBox;
    }

    public void setCurrentBox(Box currentBox) {
        this.currentBox = currentBox;
    }

    public Monster getMonster() {
        return monster;
    }

    private void createMonster (int damage, int hp){
        int num = 1;
        int chance = rand.nextInt(2);
        if (num == chance)
            monster = new Monster(damage, hp);
        else
            monster = null;
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return name.toUpperCase() + "\nYou are " + description + ".\n" + getExitString();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public boolean getExit(String direction) {
        return exits.containsKey(direction);
    }

    public Room checkRoomCreated (String direction){
        return exits.get(direction);
    }

    public int killMonster (int damage){
        if (monster != null) {
            monster.decreaseHp(damage);
            getMonsterStats();
            if (monster.getHp() > 0)
                return monster.attack();
            else
                monster = null;
        }
        return 0;
    }

    private void getMonsterStats (){
        if (monster != null) {
            System.out.println(name);
            monster.getStats();
        }
        else
            System.out.println("Monster has been killed.");
    }
}
