package ex2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static void main(String[] args) {
        Map<String, Long> boundedQueueTimes = BoundedQueueTest();
        Map<String, Long> splitBoundedQueueTest = SplitBoundedQueueTest();

        System.out.println("Enqueue time ratio: " + ((float) splitBoundedQueueTest.get("enqTime") / boundedQueueTimes.get("enqTime")));
        System.out.println("Dequeue time ratio: " + ((float) splitBoundedQueueTest.get("deqTime") / boundedQueueTimes.get("deqTime")));
    }


    public static Map<String, Long> BoundedQueueTest() {
        int capacity = 1000;
        int totalEnqOperations = 100000;
        int totalDeqOperations = 99880;
        BoundedQueue<Integer> queue = new BoundedQueue<>(capacity);
        AtomicLong t_enq_size = new AtomicLong(0);
        AtomicLong t_deq_size = new AtomicLong(0);

        // Thread for enq operations
        Thread enqThread1 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = 0; i < totalEnqOperations / 2; i++) {
                queue.enq(i);
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_enq_size.addAndGet(duration);
        });

        Thread enqThread2 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = totalEnqOperations / 2; i < totalEnqOperations; i++) {
                queue.enq(i);
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_enq_size.addAndGet(duration);
        });

        // Thread for deq operations
        Thread deqThread1 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = 0; i < totalDeqOperations / 2; i++) {
                queue.deq();
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_deq_size.addAndGet(duration);
        });

        Thread deqThread2 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = totalDeqOperations / 2; i < totalDeqOperations; i++) {
                queue.deq();
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_deq_size.addAndGet(duration);
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
        System.out.println("BoundedQueue:");
        System.out.println("Expected final size: " + expectedFinalSize);
        System.out.println("Actual final size: " + actualFinalSize);
        System.out.println("Correctness Test: " + (expectedFinalSize == actualFinalSize ? "Passed" : "Failed"));
        System.out.println("Enqueue time: " + t_enq_size.get() + "ms");
        System.out.println("Dequeue time: " + t_deq_size.get() + "ms\n");

        // Return the times as a Map
        Map<String, Long> result = new HashMap<>();
        result.put("enqTime", t_enq_size.get());
        result.put("deqTime", t_deq_size.get());
        return result;
    }

    public static Map<String, Long> SplitBoundedQueueTest() {
        int capacity = 1000;
        int totalEnqOperations = 100000;
        int totalDeqOperations = 99880;
        SplitBoundedQueue<Integer> queue = new SplitBoundedQueue<>(capacity);
        AtomicLong t_enq_split = new AtomicLong(0);
        AtomicLong t_deq_split = new AtomicLong(0);

        // Thread for enq operations
        Thread enqThread1 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = 0; i < totalEnqOperations / 2; i++) {
                queue.enq(i);
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_enq_split.addAndGet(duration);
        });

        Thread enqThread2 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = totalEnqOperations / 2; i < totalEnqOperations; i++) {
                queue.enq(i);
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_enq_split.addAndGet(duration);
        });

        // Thread for deq operations
        Thread deqThread1 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = 0; i < totalDeqOperations / 2; i++) {
                queue.deq();
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_deq_split.addAndGet(duration);
        });

        Thread deqThread2 = new Thread(() -> {
            long startTime = System.nanoTime();
            for (int i = totalDeqOperations / 2; i < totalDeqOperations; i++) {
                queue.deq();
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // in milliseconds
            t_deq_split.addAndGet(duration);
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
        int actualFinalSize = queue.enqSize.get() + queue.deqSize.get();

        System.out.println("SplitBoundedQueue:");
        System.out.println("Expected final size: " + expectedFinalSize);
        System.out.println("Actual final size: " + actualFinalSize);
        System.out.println("Correctness Test: " + (expectedFinalSize == actualFinalSize ? "Passed" : "Failed"));
        System.out.println("Enqueue time: " + t_enq_split.get() + "ms");
        System.out.println("Dequeue time: " + t_deq_split.get() + "ms\n");

        // Return the times as a Map
        Map<String, Long> result = new HashMap<>();
        result.put("enqTime", t_enq_split.get());
        result.put("deqTime", t_deq_split.get());
        return result;
    }

}
