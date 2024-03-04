package ex1;

import java.util.concurrent.atomic.AtomicInteger;

public class CCASLock {
    private AtomicInteger state = new AtomicInteger(0);

    public void lock() {
        while (true) {
            while (state.get() == 1) {

            }
            if (state.compareAndSet(0, 1)) {
                return;
            }
        }
    }

    public void unlock() {
        state.set(0);
    }
}
