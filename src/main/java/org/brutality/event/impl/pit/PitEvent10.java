package org.brutality.event.impl.pit;

import org.brutality.event.Event;

public class PitEvent10 extends Event {
    public final int count;
    public final int count2;

    public PitEvent10(int n, int count) {
        this.count2 = n;
        this.count = count;
    }
}
