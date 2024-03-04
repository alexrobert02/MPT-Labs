package ex2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseList<T> {
    private Node head;
    private Node tail;
    private Lock lock = new ReentrantLock();
    public CoarseList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = this.tail;
    }
    public boolean add(T item) {

        Node pred, current;
        int key = item.hashCode();

        lock.lock();
        try {
            pred = head;
            current = pred.next;
            while (current.key < key) {
                pred = current;
                current = current.next;
            }
            if (key == current.key) {
                return false;
            } else {
                Node node = new Node(item);
                node.next = current;
                pred.next = node;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }
    public boolean remove(T item) {
        Node pred, current;
        int key = item.hashCode();

        lock.lock();
        try {
            pred = this.head;
            current = pred.next;
            while (current.key < key) {
                pred = current;
                current = current.next;
            }
            if (key == current.key) {
                pred.next = current.next;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(T item) {
        Node pred, current;
        int key = item.hashCode();
        lock.lock();
        try {
            pred = head;
            current = pred.next;
            while (current.key < key) {
                pred = current;
                current = current.next;
            }
            return (key == current.key);
        } finally {
            lock.unlock();
        }
    }

    private class Node {
        T item;
        int key;
        Node next;
        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }
        Node(int key) {
            this.item = null;
            this.key = key;
        }
    }
}