package ex1;

import java.util.concurrent.atomic.AtomicInteger;

public class PetersonLockExample {
    private static final int n = 2;
    private static final int counterLimit = 4000;
    private static int counter = 0;
    private static AtomicInteger[] level = new AtomicInteger[n];
    private static AtomicInteger[] victim = new AtomicInteger[n];
    private static int[] accesses = new int[n];

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[n];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n; i++) {
            level[i] = new AtomicInteger(0);
            victim[i] = new AtomicInteger(0);
            final int threadId = i;
            Thread thread = new Thread(() -> incrementCounter(threadId));
            threads[i] = thread;
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + " milliseconds");
        System.out.println("Final counter value: " + counter);
        System.out.println("Accesses per thread:");
        int sum=0;
        for (int i = 0; i < n; i++) {
            System.out.println("Thread " + i + ": " + accesses[i] + " accesses");
            sum+=accesses[i];
        }
        System.out.println(sum);
    }

    public static void lock(int i) {
        for (int L = 1; L < n; L++) {
            level[i].set(L);
            victim[L].set(i);
            while (existsHigherLevelThread(i, L) && victim[L].get() == i) {
                // Wait until it's your turn
            }
        }
    }

    public static void unlock(int i) {
        level[i].set(0);
    }

    public static void incrementCounter(int i) {
        while (counter < counterLimit) {
            lock(i);
            if (counter < counterLimit) {
                counter++;
                accesses[i]++;
            }
            unlock(i);
        }
    }

    public static boolean existsHigherLevelThread(int i, int L) {
        for (int k = 0; k < n; k++) {
            if (k != i && level[k].get() >= L) {
                return true;
            }
        }
        return false;
    }
}