package org.brutality.event.impl.player;

import org.brutality.event.Event;

public class EventKey extends Event {
    private int key2;

    public EventKey(int key2) {
        this.key2 = key2;
    }

    public int getKey() {
        return this.key2;
    }
}
