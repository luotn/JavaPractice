package day1;

public abstract class Dog {
    final String NAME;
    final int AGE;
    final Human OWNER;
    int MOOD;

    public Dog(String name, int age, Human owner) {
        this.NAME = name;
        this.AGE = age;
        this.OWNER = owner;
    }

    @Override
    public String toString() {
        return this.NAME + " the day1.Westie";
    }

    public int getAge() {
        return this.AGE;
    }

    public Human getOwner() {
        return this.OWNER;
    }
    abstract void playWith(Human human);
    public int getMood() {
        return this.MOOD;
    }
}
