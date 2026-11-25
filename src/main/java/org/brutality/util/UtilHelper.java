package org.brutality.util;

public class UtilHelper {
    private long timestamp = 0L;

    public void clear() {
        this.timestamp = 0L;
    }

    public void handleInt(int n) {
        this.timestamp = System.currentTimeMillis() + n;
    }

    public boolean checkState() {
        return (System.currentTimeMillis() > this.timestamp ? 1 : (System.currentTimeMillis() == this.timestamp ? 0 : -1)) < 0;
    }
}
