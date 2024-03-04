package ex2;

public class Main {
    private static final int ITERATIONS = 100000;

    public static void main(String[] args) {
        int n = 4;  // Number of incrementing threads
        int m = 4;  // Number of decrementing threads

        SharedCounter sharedCounter = new SharedCounter();

        Thread[] incThreads = new Thread[n];
        Thread[] decThreads = new Thread[m];

        long startTime = System.currentTimeMillis();

        // Creating and starting incrementing threads
        for (int i = 0; i < n; i++) {
            incThreads[i] = new Thread(new MyThreadInc(sharedCounter, ITERATIONS));
            incThreads[i].start();
        }

        // Creating and starting decrementing threads
        for (int i = 0; i < m; i++) {
            decThreads[i] = new Thread(new MyThreadDec(sharedCounter, ITERATIONS));
            decThreads[i].start();
        }

        // Waiting for all threads to finish
        for (int i = 0; i < n; i++) {
            try {
                incThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < m; i++) {
            try {
                decThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Valoarea contorului: " + sharedCounter.get());
        System.out.println("Durata execuției: " + duration + " milisecunde");
    }
}
