package ex2;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        testOptimisticList();
        testVersionedOptimisticList();
    }
    public static void testOptimisticList() {
        // Create an optimistic list
        OptimisticList<Integer> optimisticList = new OptimisticList<>();

        // Add odd elements to the list
        for (int i = 1; i <= 9999; i += 2) {
            optimisticList.add(i);
        }

        // Print the initial list
        System.out.println("Initial OptimisticList:");
        optimisticList.printList();

        // Create and start multiple threads for removing even elements
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        Random random = new Random();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1250; j++) {
                    optimisticList.remove((random.nextInt(5000) + 1) * 2);
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        // Print the final list
        System.out.println("\nFinal OptimisticList:");
        optimisticList.printList();

        // Print the time taken for remove operation
        System.out.println("Time taken for remove operation: " + (endTime - startTime) + " milliseconds\n");
    }

    public static void testVersionedOptimisticList() {
        // Create a versioned optimistic list
        VersionedOptimisticList<Integer> versionedOptimisticList = new VersionedOptimisticList<>();

        // Add odd elements to the list
        for (int i = 1; i <= 9999; i += 2) {
            versionedOptimisticList.add(i);
        }

        // Print the initial list
        System.out.println("Initial VersionedOptimisticList:");
        versionedOptimisticList.printList();

        // Create and start multiple threads for removing even elements
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        Random random = new Random();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1250; j++) {
                    versionedOptimisticList.remove((random.nextInt(5000) + 1) * 2);
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        // Print the final list
        System.out.println("\nFinal VersionedOptimisticList:");
        versionedOptimisticList.printList();

        // Print the time taken for remove operation
        System.out.println("Time taken for remove operation: " + (endTime - startTime) + " milliseconds");
    }
}
