package org.brutality.event.impl.pit;

import org.brutality.event.Event;

public class PitEvent extends Event {
    private final String description;
    private final long timestamp;

    public long getTimestamp() {
        return this.timestamp;
    }

    public PitEvent(long timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
