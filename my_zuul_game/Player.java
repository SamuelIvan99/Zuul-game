import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Player{
    private String name;
    private ArrayList<String> backpack;
    private int capacity;
    private int experience;
    private int damage;
    private int hp;
    private Random rand;

    public Player(int capacity, int experience, int damage, int hp){
        rand = new Random();
        this.capacity = capacity;
        this.experience = experience;
        this.damage = damage;
        this.hp = hp;
        backpack = new ArrayList<>();
        addDefaultPackage();
    }

    private void addDefaultPackage (){
        backpack.add("weapon");
        backpack.add("weapon");
        backpack.add("food");
        backpack.add("pen");
    }

    public void inspectBackpack ()
    {
        backpack.forEach(item -> System.out.print(item + " "));
        System.out.println();
    }

    public String addItem (String item){
        String message = "You have no more space in your backpack.";
        if (item != null && backpack.size() < capacity) {
            backpack.add(item);
            message = item.toUpperCase() + " has been added to your backpack.";
            System.out.println("Your current inventory.");
            inspectBackpack();
        }
        return message;
    }

    public boolean useItem (String name){
        return backpack.remove(name);
    }

    public String getName() {
        return name;
    }

    public int getExperience() {
        return experience;
    }

    public int getDamage() {
        return damage;
    }

    public int getHp() {
        return hp;
    }

    public boolean backpackHasSpace(){
        return backpack.size() <= capacity;
    }

    public void setName() {
        System.out.println("Enter player's name: ");
        Scanner sc = new Scanner(System.in);
        name = sc.next();
    }

    public void increaseCapacity(int increase) {
        this.capacity += increase + (experience / 10);
    }

    public void increaseExperience(int increase) {
        this.experience += increase;
    }

    public void increaseDamage(int increase) {
        this.damage += increase + (experience / 10);
    }

    public void increaseHp(int increase) {
        this.hp += increase + (experience / 10);
    }

    public boolean decreaseHp (int damage){
        boolean dead = false;
        hp -= damage;
        if (hp <= 0)
            dead = true;
        return dead;
    }

    public int attack (){
        return damage + (rand.nextInt(6) + 1) + (experience / 10);
    }

    public void getStats(){
        System.out.printf("Player %s\nDamage: %d\nHP: %d\nExperience: %d\nCapacity (backpack): %d\n", name, damage, hp, experience, capacity);
        System.out.println("Backpack items: ");
        inspectBackpack();
    }
}