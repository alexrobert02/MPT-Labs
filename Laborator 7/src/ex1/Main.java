package ex1;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SimpleReadWriteLock rwLock = new SimpleReadWriteLock();

        long startTime = System.currentTimeMillis();

        int readCount = 0;
        int writeCount = 0;
        int totalThreads = 8;

        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < totalThreads; i++) {
            if (i % 2 == 0 && readCount < totalThreads / 2) {
                Thread readerThread = new Thread(new ReaderThread(rwLock, 400000));
                readerThread.start();
                threadList.add(readerThread);
                readCount++;
            } else if (writeCount < totalThreads / 2) {
                Thread writerThread = new Thread(new WriterThread(rwLock));
                writerThread.start();
                threadList.add(writerThread);
                writeCount++;
            }
        }

        // Așteaptă ca toate firele să se termine
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Timpul total de execuție: " + executionTime + " milisecunde.");
    }
}
