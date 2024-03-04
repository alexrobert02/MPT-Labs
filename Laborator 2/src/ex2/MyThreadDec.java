package ex2;

public class MyThreadDec extends Thread {

    private final int iterations;
    SharedCounter counter;

    public MyThreadDec(SharedCounter counter, int iterations) {
        this.counter = counter;
        this.iterations = iterations;
    }

    public void run() {
        for (int i = 0; i < iterations; i++) {
            synchronized (counter) {
                int localCounter = counter.get();
                localCounter--;
                counter.set(localCounter);
            }
        }

    }
}