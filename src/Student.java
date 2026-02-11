public class Student implements Human{
    private final String NAME;
    private final int AGE;
    public int STUDYOMETER;

    public Student(String name, int age) {
        this.NAME = name;
        this.AGE = age;
        this.STUDYOMETER = 0;
    }

    @Override
    public String toString() {
        return this.NAME;
    }

    @Override
    public void sayHi(Human other) {
        System.out.println("Hi " + other + "! I'm " + this.NAME);
    }

    @Override
    public int getAge() {
        return this.AGE;
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
