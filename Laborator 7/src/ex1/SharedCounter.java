package ex1;

class SharedCounter {
    private static int counter = 0;

    public static int getCounter() {
        return counter;
    }
    public static void increment() {
        counter++;
    }
}