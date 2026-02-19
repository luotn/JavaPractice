package day2;

import static java.lang.Thread.sleep;

public class GPUCore implements Runnable{

    private int coreIndex;

    public GPUCore(int index) {
        coreIndex = index;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("hi from core" + coreIndex + " t=" + i);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
