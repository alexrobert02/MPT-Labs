package ex1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SimpleReadWriteLock {
    private final ReentrantReadWriteLock rwLock;
    private final Lock readLock;
    private final Lock writeLock;

    public SimpleReadWriteLock() {
        rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }
}
