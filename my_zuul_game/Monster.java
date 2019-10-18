import java.util.Random;

public class Monster{
    private int damage;
    private int hp;
    private boolean hasEaten;
    private Random rand;

    public Monster(int damage, int hp){
        this.damage = damage;
        this.hp = hp;
        hasEaten = false;
        rand = new Random();
    }

    public int getDamage() {
        return damage;
    }

    public int getHp() {
        return hp;
    }

    public void decreaseHp (int damage){
        hp -= damage;
    }

    public int attack (){
        return damage + (rand.nextInt(11) - 5);
    }

    public boolean getHasEaten() {
    return hasEaten;
}

    public void setHasEaten(boolean value){
        hasEaten = value;
    }

    public void getStats(){
        System.out.printf("Monster\nDamage: %d\nHP: %d\n", damage, hp);
    }

}