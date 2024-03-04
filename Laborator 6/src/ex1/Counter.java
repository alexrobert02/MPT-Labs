package ex1;

public class Counter {
    private int count = 0;
    private final int limit;

    public Counter(int limit) {
        this.limit = limit;
    }

    public int getCount() {
        return count;
    }

    public void increment() {
        count++;
    }

    public boolean isComplete() {
        return count >= limit;
    }
}