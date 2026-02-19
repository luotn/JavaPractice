package day1;

public class Westie extends Dog{

    public Westie(String name, int age, Human owner) {
        super(name, age, owner);
        this.MOOD = 6;
    }

    @Override
    public void playWith(Human human) {
        System.out.print(this + " played with " + human);
        if (human != this.OWNER) {
            System.out.println(" who is NOT their owner... :(");
            if (this.MOOD < 8) {
                this.MOOD += 1;
            }
        } else {
            System.out.println(" who IS their owner!!! :)");
            this.MOOD = 10;
        }
    }
}
