package ex1;

public class Main {
    public static void main(String[] args) {
        tasLockTest();
        ccasLockTest();
    }

    public static void tasLockTest() {
        int numThreads = 4;
        int counterLimit = 300000;
        Counter counter = new Counter(counterLimit);

        TASLock tasLock = new TASLock();
        CCASLock ccasLock = new CCASLock();

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new CounterThread(counter, ccasLock);
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("TASlock:");
        for (int i = 0; i < numThreads; i++) {
            if (threads[i] instanceof CounterThread) {
                CounterThread counterThread = (CounterThread) threads[i];
                System.out.println("Thread ID: " + i +
                        " Local Counter: " + counterThread.getLocalCounter());
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Numarul total de incrementari: " + counter.getCount());
        System.out.println("Timpul total de executie: " + (endTime - startTime) + " ms\n");
    }

    public static void ccasLockTest() {
        int numThreads = 4;
        int counterLimit = 300000;
        Counter counter = new Counter(counterLimit);

        TASLock tasLock = new TASLock();
        CCASLock ccasLock = new CCASLock();

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new CounterThread(counter, ccasLock);
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("CCASLock:");
        for (int i = 0; i < numThreads; i++) {
            if (threads[i] instanceof CounterThread) {
                CounterThread counterThread = (CounterThread) threads[i];
                System.out.println("Thread ID: " + i +
                        " Local Counter: " + counterThread.getLocalCounter());
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Numarul total de incrementari: " + counter.getCount());
        System.out.println("Timpul total de executie: " + (endTime - startTime) + " ms");
    }
}
