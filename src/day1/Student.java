package day1;

public class Student extends Human{

    public int STUDYOMETER;

    public Student(String name, int age) {
        super(name, age);
        this.STUDYOMETER = 0;
    }

    @Override
    public void sayHi(Human other) {
        System.out.println("Hi " + other + "! I'm " + this.NAME);
    }

    public int getStudy() {
        return this.STUDYOMETER;
    }

    public void study(Student other) {
        if (other != null) {
            System.out.println(this.NAME + " studied with " + other);
            this.STUDYOMETER += 2;
            other.STUDYOMETER += 2;
        } else {
            System.out.println(this.NAME + " studied alone. :(");
            this.STUDYOMETER += 1;
        }
    }
}
