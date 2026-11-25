package org.brutality.alt.altmanager.utils;

public class StatusToast {
    private final String message;
    private final long timestamp;
    private final long timestamp2;

    public String getMessage() {
        return this.message;
    }

    public StatusToast(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.timestamp2 = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return this.timestamp >= 0L && this.timestamp < System.currentTimeMillis() - this.timestamp2;
    }
}
