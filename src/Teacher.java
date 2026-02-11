public class Teacher implements Human{
    private final String NAME;
    private final int AGE;

    public Teacher(String name, int age) {
        this.NAME = name;
        this.AGE = age;
    }

    @Override
    public String toString() {
        return this.NAME;
    }

    @Override
    public void sayHi(Human other) {
        System.out.println("Hi " + other + ", I'm " + this.NAME + " the teacher.");
    }

    @Override
    public int getAge() {
        return this.AGE;
    }

    public void teach(Student[] students) {
        String result = "";
        for (Student student : students) {
            result += student + ", ";
            student.STUDYOMETER += 5;
        }

        System.out.println("Teacher " + this.NAME + " taught: " + result);
    }
}
