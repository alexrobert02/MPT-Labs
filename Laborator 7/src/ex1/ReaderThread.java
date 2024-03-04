package ex1;

import java.util.concurrent.locks.Lock;

class ReaderThread implements Runnable {
    private final SimpleReadWriteLock rwLock;
    private final int expectedValue;
    private int readCount;

    public ReaderThread(SimpleReadWriteLock rwLock, int expectedValue) {
        this.rwLock = rwLock;
        this.expectedValue = expectedValue;
        this.readCount = 0;
    }

    @Override
    public void run() {
        while (true) {
            Lock readLock = rwLock.readLock();
            readLock.lock();
            try {
                // acces read la resursa partajata
                int sharedCounter = SharedCounter.getCounter();
                readCount++;
                if (sharedCounter >= expectedValue) {
                    break;
                }
            } finally {
                readLock.unlock();
            }
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " finished reading. Read count: " + readCount);
    }
}