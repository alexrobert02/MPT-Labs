package ex3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> {
    ReentrantLock enqLock, deqLock;
    AtomicInteger size;
    Node head, tail;
    int capacity;
    Condition notFullCondition, notEmptyCondition;

    public BoundedQueue(int capacity) {
        this.capacity = capacity;
        this.head = new Node(null);
        this.tail = head;
        this.size = new AtomicInteger(0);
        this.enqLock = new ReentrantLock();
        this.notFullCondition = enqLock.newCondition();
        this.deqLock = new ReentrantLock();
        this.notEmptyCondition = deqLock.newCondition();
    }


    public void enq(T x){
        boolean mustWakeDequeuers = false;

        enqLock.lock();
        try {
            while (size.get() == capacity) {
                notFullCondition.await();
            }
            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;
            if (size.getAndIncrement() == 0) {
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
            if (size.getAndDecrement() == capacity) {
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