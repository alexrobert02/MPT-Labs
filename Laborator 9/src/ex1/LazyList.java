package ex1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LazyList<T> {
    private Node head;

    public LazyList() {
        this.head  = new Node(Integer.MIN_VALUE);
        this.head.next = new Node(Integer.MAX_VALUE);
    }

    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head;
            Node current = pred.next;
            while (current.key < key) {
                pred = current; current = current.next;
            }
            pred.lock();
            try {
                current.lock();
                try {
                    if (validate(pred, current)) {
                        if (current.key == key) {
                            return false;
                        } else {
                            Node entry = new Node(item);
                            entry.next = current;
                            pred.next = entry;
                            return true;
                        }
                    }
                }
                finally {
                    current.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head;
            Node current = pred.next;
            while (current.key < key) {
                pred = current; current = current.next;
            }
            pred.lock();
            try {
                current.lock();
                try {
                    if (validate(pred, current)) {
                        if (current.key == key) {
                            current.marked = true;
                            pred.next = current.next;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                finally {
                    current.unlock();
                }

            } finally {
                pred.unlock();
            }
        }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        Node current = head;
        while (current.key < key) {
            current = current.next;
        }
        return current.key == key && !current.marked;
    }

    private boolean validate(Node pred, Node current) {
        return !pred.marked && !current.marked && pred.next == current;
    }

    private class Node {
        T item;
        int key;
        Node next;
        Lock lock;
        boolean marked;
        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
            lock = new ReentrantLock();
        }
        Node(int key) {
            this.key = key;
            lock = new ReentrantLock();
        }
        void lock() {lock.lock();}
        void unlock() {lock.unlock();}
    }
}
