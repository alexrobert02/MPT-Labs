package ex5c;

import ex5c.MutualExclusionLock;

public class MyThread extends Thread {
    private final int threadId;
    private final MutualExclusionLock mutualExclusionLock;
    private static final int MAX_ITERATIONS = 100;

    public MyThread(int threadId, MutualExclusionLock mutualExclusionLock) {
        this.threadId = threadId;
        this.mutualExclusionLock = mutualExclusionLock;
    }

    @Override
    public void run() {
        for (int iteration = 1; iteration <= MAX_ITERATIONS; iteration++) {
            try {
                mutualExclusionLock.lock(threadId);
                System.out.println("Thread " + threadId + " is in the critical section. Iteration: " + iteration);
            } finally {
                mutualExclusionLock.unlock(threadId);
            }
        }
    }
}
