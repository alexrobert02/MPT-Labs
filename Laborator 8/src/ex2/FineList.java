package ex2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineList<T> {
    private Node head;
    private Node tail;
    private Lock lock = new ReentrantLock();

    public FineList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = this.tail;
    }

    public boolean add(T item) {
        int key = item.hashCode();
        Node pred, current;
        pred = head;
        pred.lock.lock();
        try {
            current = pred.next;
            current.lock.lock();
            try {
                while (current.key <= key) {
                    if (key == current.key) {
                        return false;
                    }
                    pred.lock.unlock();
                    pred = current;
                    current = current.next;
                    current.lock.lock();
                }
                Node newNode = new Node(item);
                pred.next = newNode;
                newNode.next = current;
                return true;
            } finally {
                current.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    public boolean remove(T item) {

        int key = item.hashCode();
        Node pred, current;
        pred = head;
        pred.lock.lock();
        try {
            current = pred.next;
            current.lock.lock();
            try {
                while (current.key <= key) {
                    if (key == current.key) {
                        pred.next = current.next;
                        return true;
                    }
                    pred.lock.unlock();
                    pred = current;
                    current = current.next;
                    current.lock.lock();
                }
                return false;
            } finally {
                current.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    private class Node {
        T item;
        int key;
        Node next;
        Lock lock;

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
            this.lock = new ReentrantLock();
        }

        Node(int key) {
            this.item = null;
            this.key = key;
            this.lock = new ReentrantLock();
        }
    }
}