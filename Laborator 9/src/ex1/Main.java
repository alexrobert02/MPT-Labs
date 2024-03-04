package ex1;

import java.util.Random;

public class Main {
    private static final int OPERATIONS_PER_THREAD = 20000;
    private static final int MAX_RANDOM_VALUE = 10000;

    public static void main(String[] args) {
        testOptimisticList(4);
        testOptimisticList(8);

        testLazyList(4);
        testLazyList(8);
    }

    private static void testOptimisticList(int numThreads) {
        OptimisticList<Integer> list = new OptimisticList<>();
        Thread[] threads = new Thread[numThreads];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                Random random = new Random();
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int randomValue = random.nextInt(MAX_RANDOM_VALUE) + 1;
                    if (j % 2 == 0) {
                        list.add(randomValue);
                    } else {
                        list.remove(randomValue);
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Număr de thread-uri: " + numThreads);
        System.out.println("Tipul de listă: OptimisticList");
        System.out.println("Timpul de execuție: " + executionTime + " ms");
        System.out.println("---------------------------------------");
    }

    private static void testLazyList(int numThreads) {
        LazyList<Integer> list = new LazyList<>();
        Thread[] threads = new Thread[numThreads];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                Random random = new Random();
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int randomValue = random.nextInt(MAX_RANDOM_VALUE) + 1;
                    if (j % 2 == 0) {
                        list.add(randomValue);
                    } else {
                        list.remove(randomValue);
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Număr de thread-uri: " + numThreads);
        System.out.println("Tipul de listă: LazyList");
        System.out.println("Timpul de execuție: " + executionTime + " ms");
        System.out.println("---------------------------------------");
    }
}