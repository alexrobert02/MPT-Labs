package ex1;

import java.util.concurrent.atomic.AtomicBoolean;

public class TASLock {

    AtomicBoolean state = new AtomicBoolean(false);

    void lock() {
        while (state.getAndSet(true)) {
        }
    }

    void unlock() {
        state.set(false);
    }
}
