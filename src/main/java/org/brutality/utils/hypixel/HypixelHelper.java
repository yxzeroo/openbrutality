package org.brutality.utils.hypixel;

import org.brutality.utils.hypixel.pit.PitHelper2;

public final class HypixelHelper {
    private long timestamp = -1L;
    private long timestamp2 = -1L;
    private String text;

    public void handleString(String text, long timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public String computeString(int n, int n2, long l) {
        if (PitHelper2.matchesInt(n, n2)) {
            this.timestamp2 = l;
        }

        if (this.text != null
            && this.timestamp2 >= 0L
            && l - this.timestamp <= 1500L
            && l - this.timestamp2 <= 1500L
            && Math.abs(this.timestamp - this.timestamp2) <= 1500L) {
            String text = this.text;
            this.text = null;
            this.timestamp = this.timestamp2 = -1L;
            return text;
        }
        return null;
    }

    public void clear() {
        this.timestamp = this.timestamp2 = -1L;
        this.text = null;
    }
}
