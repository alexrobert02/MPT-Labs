package ex1;

public class SharedCounter {
    int counter = 0;

    public synchronized int get() {
        return counter;
    }

    public synchronized void set(int localCounter) {
        counter = localCounter;
    }
}
