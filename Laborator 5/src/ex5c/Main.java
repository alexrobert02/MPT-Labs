package ex5c;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numThreads = 4;
        MutualExclusionLock mutualExclusionLock = new MutualExclusionLock(numThreads);
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new MyThread(i, mutualExclusionLock);
            threads[i] = thread;
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }
}