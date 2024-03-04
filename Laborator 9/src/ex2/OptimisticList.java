package ex2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OptimisticList<T> {

    private Node head;

    public OptimisticList() {
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
            pred.lock(); current.lock();
            try {
                if (validate(pred, current)) {
                    if (current.key == key) { // present
                        return false;
                    } else {               // not present
                        Node entry = new Node(item);
                        entry.next = current;
                        pred.next = entry;
                        return true;
                    }
                }
            } finally {                // always unlock
                pred.unlock(); current.unlock();
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
            pred.lock(); current.lock();
            try {
                if (validate(pred, current)) {
                    if (current.key == key) { // present in list
                        pred.next = current.next;
                        return true;
                    } else {               // not present in list
                        return false;
                    }
                }
            } finally {                // always unlock
                pred.unlock(); current.unlock();
            }
        }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = this.head; // sentinel node;
            Node current = pred.next;
            while (current.key < key) {
                pred = current; current = current.next;
            }
            try {
                pred.lock(); current.lock();
                if (validate(pred, current)) {
                    return (current.key == key);
                }
            } finally {                // always unlock
                pred.unlock(); current.unlock();
            }
        }
    }

    private boolean validate(Node pred, Node current) {
        Node entry = head;
        while (entry.key <= pred.key) {
            if (entry == pred)
                return pred.next == current;
            entry = entry.next;
        }
        return false;
    }

    public void printList() {
        OptimisticList.Node current = head.next;
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