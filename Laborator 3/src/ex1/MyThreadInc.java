package ex1;

public class MyThreadInc extends Thread {

    private final int iterations;
    SharedCounter counter;

    public MyThreadInc(SharedCounter counter, int iterations) {
        this.counter = counter;
        this.iterations = iterations;
    }

    public void run() {
        for (int i = 0; i < iterations; i++) {
            try {
                counter.getSemaphore().acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int localCounter = counter.getCounter();
            localCounter++;
            counter.setCounter(localCounter);
            counter.getSemaphore().release();
        }
    }
}