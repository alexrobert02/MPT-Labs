package ex1;

import java.util.concurrent.Semaphore;

public class SharedCounter {
    int counter = 0;
    Semaphore semaphore = new Semaphore(1);

    public int getCounter() {
        return counter;
    }

    public void setCounter(int localCounter) {
        counter = localCounter;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }
}
