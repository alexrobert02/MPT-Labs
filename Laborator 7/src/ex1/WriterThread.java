package ex1;

import java.util.concurrent.locks.Lock;

class WriterThread implements Runnable {
    private final SimpleReadWriteLock rwLock;
    private int writeCount;

    public WriterThread(SimpleReadWriteLock rwLock) {
        this.rwLock = rwLock;
        this.writeCount = 0;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100000; i++) {
            Lock writeLock = rwLock.writeLock();
            writeLock.lock();
            try {
                // acces write la resursa partajata
                SharedCounter.increment();
                writeCount++;
            } finally {
                writeLock.unlock();
            }
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " finished writing. Write count: " + writeCount);
    }
    public int getWriteCount() {
        return writeCount;
    }
}