public class Westie implements Dog{

    private final String NAME;
    private final int AGE;
    private final Human OWNER;
    private int MOOD;

    public Westie(String name, int age, Human owner) {
        this.NAME = name;
        this.AGE = age;
        this.OWNER = owner;
        this.MOOD = 5;
    }

    @Override
    public String toString() {
        return this.NAME + " the Westie";
    }

    @Override
    public int getAge() {
        return this.AGE;
    }

    @Override
    public Human getOwner() {
        return this.OWNER;
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

    @Override
    public int getMood() {
        return this.MOOD;
    }
}
