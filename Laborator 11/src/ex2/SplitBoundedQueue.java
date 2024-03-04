package ex2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

public class SplitBoundedQueue<T> {
    ReentrantLock enqLock, deqLock;
    AtomicInteger enqSize, deqSize;
    int capacity;
    Node head, tail;
    Condition notFullCondition, notEmptyCondition;

    public SplitBoundedQueue(int capacity) {
        this.capacity = capacity;
        this.head = new Node(null);
        this.tail = head;
        this.enqSize = new AtomicInteger(0);
        this.deqSize = new AtomicInteger(0);
        this.enqLock = new ReentrantLock();
        this.notFullCondition = enqLock.newCondition();
        this.deqLock = new ReentrantLock();
        this.notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T x) {
        boolean mustWakeDequeuers = false;

        enqLock.lock();
        try {
            while (enqSize.get() + deqSize.get() == capacity) {
                notFullCondition.await();
            }
            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;
            if (enqSize.getAndIncrement() + deqSize.get() == 0) {
                mustWakeDequeuers = true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            enqLock.unlock();
        }

        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                enqSize.getAndAdd(deqSize.get());
                deqSize.set(0);
                notEmptyCondition.signalAll();

            } finally {
                deqLock.unlock();
            }
        }
    }

    public T deq(){
        boolean mustWakeEnqueuers = false;
        T v;

        deqLock.lock();
        try {
            while (head.next == null) {
                notEmptyCondition.await();
            }
            v = head.next.value;
            head = head.next;
            if (enqSize.get() + deqSize.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            deqLock.unlock();
        }

        if (mustWakeEnqueuers) {
            enqLock.lock();
            try {
                notFullCondition.signalAll();
            } finally {
                enqLock.unlock();
            }
        }

        return v;
    }

    protected class Node {
        public T value;
        public Node next;

        public Node(T x) {
            value = x;
            next = null;
        }
    }
}
