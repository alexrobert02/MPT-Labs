package ex5c;

public class MutualExclusionLock {
    private final boolean[] flag;
    private final boolean[] access;
    private final int[] label;
    private final int n;
    private int maxLabel;

    public MutualExclusionLock(int numThreads) {
        n = numThreads;
        flag = new boolean[n];
        access = new boolean[n];
        label = new int[n];
        maxLabel = 0;
        init();
    }

    private void init() {
        for (int k = 0; k < n; k++) {
            flag[k] = false;
            access[k] = false;
            label[k] = k + 1;
        }
    }

    public void lock(int i) {
        flag[i] = true;
        do {
            access[i] = false;
            while (conditionIsMet(i)) ;
            access[i] = true;
        } while (existsAccess(i));
    }

    public void unlock(int i) {
        access[i] = false;
        flag[i] = false;
        maxLabel = label[i];

        for (int j = 0; j < n; j++) {
            if (j != i && label[j] > maxLabel) {
                maxLabel = label[j];
                label[j]--;
            }
        }

        label[i] = maxLabel;
    }

    private boolean conditionIsMet(int i) {
        for (int j = 0; j < n; j++) {
            if (j != i && (flag[j] || label[j] <= label[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean existsAccess(int i) {
        for (int j = 0; j < n; j++) {
            if (j != i && access[j]) {
                return true;
            }
        }
        return false;
    }
}
