package org.brutality.event.impl.player;

import org.brutality.event.Event;

public class PlayerEvent4 extends Event {
    private final int count;
    private final int count2;

    public int computeInt(int n) {
        return this.count >= 0 && this.count <= 8 ? this.count : Math.floorMod(n - this.count2, 9);
    }

    public PlayerEvent4(int count, int n) {
        this.count = count;
        this.count2 = Math.min(Math.max(n, -1), 1);
    }

    public int getCount() {
        return this.count;
    }
}
