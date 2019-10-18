import java.util.ArrayList;
import java.util.Random;

public class Box{
    private String name;
    private ArrayList<String> items;
    private ArrayList<String> defaultItems;
    private boolean isSecretDoor;

    public Box (String name){
        this.name = name;
        items = new ArrayList<>();
        defaultItems = new ArrayList<>();
        addDefaultItems();
        setItems();
    }

    public String getName() {
        return name;
    }

    public boolean isSecretDoor() {
        return isSecretDoor;
    }

    private void addDefaultItems () {
        defaultItems.add("weapon");
        defaultItems.add("food");
        defaultItems.add("coin");
        defaultItems.add("key");
        defaultItems.add("note");
        defaultItems.add("pen");
    }

    private void setItems (){
        Random rand = new Random();
        int num = 2;
        int randNum = rand.nextInt(5) + 1;

        if (num == randNum)
            isSecretDoor = true;

        if (!isSecretDoor) {
            for (int i = 0; i < 3; i++) {
                int index = rand.nextInt(defaultItems.size());
                items.add(defaultItems.get(index));
                defaultItems.remove(index);
            }
        }
    }

    public void inspectBox (){
        if (items.size() > 0) {
            items.forEach(item -> System.out.print(item + " "));
            System.out.println();
        }
        else
            System.out.println("That's strange. No items in the box.");
    }

    public String takeItem (String name){
        if (items.contains(name)) {
            for (String item : items) {
                if (item.equals(name)) {
                    System.out.println("Beware you can only take one item from the box.");
                    return item;
                }
            }
        }
        return null;
    }
}