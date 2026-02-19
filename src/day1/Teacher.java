package day1;

public class Teacher extends Human{

    public Teacher(String name, int age) {
        super(name, age);
    }

    @Override
    public void sayHi(Human other) {
        System.out.println("Hi " + other + ", I'm " + this.NAME + " the teacher.");
    }

    public void teach(Student[] students) {
        String result = "";
        for (Student student : students) {
            result += student + ", ";
            student.STUDYOMETER += 5;
        }

        System.out.println("day1.Teacher " + this.NAME + " taught: " + result);
    }
}
