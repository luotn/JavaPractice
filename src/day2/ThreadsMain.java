package day2;

public class ThreadsMain {
    public static void main(String[] args) {
        System.out.println("Starting Thread raper!");
        for (int i = 0; i < 16; i++) {
            GPUCore core = new GPUCore(i);
            Thread thread = new Thread(core);
            thread.start();
        }
        throw new RuntimeException("Main died from cringe.'Go on my Goblins!' said main.");
    }
}
