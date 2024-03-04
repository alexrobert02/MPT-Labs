package ex3;

public class BoundedQueueTest {

    public static void main(String[] args) {
        int capacity = 1000;
        int totalEnqOperations = 100000;
        int totalDeqOperations = 99880;
        BoundedQueue<Integer> queue = new BoundedQueue<>(capacity);

        // Thread for enq operations
        Thread enqThread1 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = 0; i < totalEnqOperations/2; i++) {
                queue.enq(i);
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            System.out.println("Enq Thread 1 duration: " + duration + " ms");
        });

        Thread enqThread2 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = totalEnqOperations/2; i < totalEnqOperations; i++) {
                queue.enq(i);
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            System.out.println("Enq Thread 2 duration: " + duration + " ms");
        });

        // Thread for deq operations
        Thread deqThread1 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = 0; i < totalDeqOperations/2; i++) {
                queue.deq();
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            System.out.println("Deq Thread 1 duration: " + duration + " ms");
        });

        Thread deqThread2 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = totalDeqOperations/2; i < totalDeqOperations; i++) {
                queue.deq();
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            System.out.println("Deq Thread 2 duration: " + duration + " ms");
        });

        enqThread1.start();
        enqThread2.start();
        deqThread1.start();
        deqThread2.start();

        try {
            enqThread1.join();
            enqThread2.join();
            deqThread1.join();
            deqThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Perform correctness test
        int expectedFinalSize = totalEnqOperations - totalDeqOperations;
        int actualFinalSize = queue.size.get();
        System.out.println("Expected final size: " + expectedFinalSize);
        System.out.println("Actual final size: " + actualFinalSize);
        System.out.println("Correctness Test: " + (expectedFinalSize == actualFinalSize ? "Passed" : "Failed"));

    }
}
