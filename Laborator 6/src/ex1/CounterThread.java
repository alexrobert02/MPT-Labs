package ex1;

public class CounterThread extends Thread {
    private Counter counter;
    private int localCounter;
    private CCASLock lock;

    public CounterThread(Counter counter, CCASLock lock) {
        this.counter = counter;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (!counter.isComplete()) {
            lock.lock();
            if (!counter.isComplete()) {
                localCounter++;
                counter.increment();
            }
            lock.unlock();
        }
    }

    public int getLocalCounter() {
        return this.localCounter;
    }
}