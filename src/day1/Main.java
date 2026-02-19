package day1;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!!! Good to be back!!!");

        System.out.println("Setting up run env for Java is annoying just as I remembered it!");

        System.out.println("Enough bitching around, let's practice some basic shit");

        System.out.println("--- A Fking Resizeable Tree ---");

        int treeSize = 5;

        System.out.println("Right side of the tree");

        System.out.println("Left side of the tree");

        System.out.println("Now put them together, and we get...");

        System.out.println("It needs a steam, otherwise it would be a Minecraft tree...");
        printTree(treeSize);

        System.out.println("PS: You don't get to see my bs progress because I'm not proud of it");

        System.out.println("--- OOP time! ---");

        Student steve = new Student("Steve", 16);
        Student alex = new Student("Alex", 15);
        steve.sayHi(alex);
        alex.sayHi(steve);
        steve.study(null);
        alex.study(steve);
        System.out.println("Steve studied: " + steve.getStudy());
        System.out.println("Alex studies: " + alex.getStudy());
        Student[] students = {steve, alex};
        Teacher jerry = new Teacher("Jerry", 28);
        jerry.sayHi(steve);
        jerry.teach(students);
        System.out.println("Steve studied: " + steve.getStudy());
        System.out.println("Alex studies: " + alex.getStudy());

        Westie wulu = new Westie("Wulu", 3, jerry);
        wulu.playWith(steve);
        System.out.println("Wulu's mood is now " + wulu.getMood());
        wulu.playWith(alex);
        System.out.println("Wulu's mood is now " + wulu.getMood());
        wulu.playWith(jerry);
        System.out.println("Wulu's mood is now " + wulu.getMood());

    }

    private static void printTree(int size) {
        for (int i = 0; i < size; i++) {
            for (int j = size - 1; j > i; j--) {
                System.out.print(' ');
            }
            for (int j = 0; j <= i * 2; j++) {
                System.out.print('#');
            }
            System.out.println();
        }
        for (int i = 0; i < (size * 2 - 1) / 2; i++) {
            System.out.print(' ');
        }
        System.out.println("|");
        for (int i = 0; i < (size * 2 - 1) / 2; i++) {
            System.out.print(' ');
        }
        System.out.println("|");
    }
}