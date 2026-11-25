package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class MovementEvent8 extends Event {
    private boolean flag;

    public void process2() {
        this.flag = true;
        this.cancel();
    }

    public boolean isFlag() {
        return this.flag;
    }
}
