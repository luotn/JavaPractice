package day1;

public abstract class Human {
    final String NAME;
    final int AGE;

    public Human(String name, int age) {
        this.NAME = name;
        this.AGE = age;
    }

    @Override
    public String toString() {
        return this.NAME;
    }

    public abstract void sayHi(Human other);

    public int getAge() {
        return this.AGE;
    }
}
