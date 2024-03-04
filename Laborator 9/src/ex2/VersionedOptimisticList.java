package ex2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VersionedOptimisticList<T> {
    private Node head;

    public VersionedOptimisticList() {
        this.head = new Node(Integer.MIN_VALUE, 0);
        this.head.next = new Node(Integer.MAX_VALUE, 0);
    }

    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head;
            Node current = pred.next;
            while (current.key < key) {
                pred = current;
                current = current.next;
            }
            pred.lock();
            current.lock();
            try {
                if (validate(pred, current)) {
                    if (current.key == key) {
                        return false;  // present
                    } else {
                        Node entry = new Node(item);
                        entry.next = current;
                        pred.next = entry;
                        pred.version++;
                        return true;   // not present
                    }
                }
            } finally {
                pred.unlock();
                current.unlock();
            }
        }
    }

    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head;
            Node current = pred.next;
            while (current.key < key) {
                pred = current;
                current = current.next;
            }
            pred.lock();
            current.lock();
            try {
                if (validate(pred, current)) {
                    if (current.key == key) {
                        pred.next = current.next;
                        pred.version++;
                        return true;   // present in list
                    } else {
                        return false;  // not present in list
                    }
                }
            } finally {
                pred.unlock();
                current.unlock();
            }
        }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head;
            Node current = pred.next;
            while (current.key < key) {
                pred = current;
                current = current.next;
            }
            try {
                pred.lock();
                current.lock();
                if (validate(pred, current)) {
                    return (current.key == key);
                }
            } finally {
                pred.unlock();
                current.unlock();
            }
        }
    }

    private boolean validate(Node pred, Node current) {
        return pred.next == current && pred.next.version == current.version;
    }

    public void printList() {
        Node current = head.next;
        int nr = 0;
        while (current != null && current.key != Integer.MAX_VALUE) {
            System.out.print(current.item + " ");
            if (current.key % 2 == 1) nr++;
            current = current.next;
        }
        System.out.println();
        System.out.println(nr);
    }

    private class Node {
        T item;
        int key;
        Node next;
        Lock lock;
        int version;

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
            this.lock = new ReentrantLock();
            this.version = 0;
        }

        Node(int key, int version) {
            this.key = key;
            this.lock = new ReentrantLock();
            this.version = version;
        }

        void lock() {
            lock.lock();
        }

        void unlock() {
            lock.unlock();
        }
    }
}