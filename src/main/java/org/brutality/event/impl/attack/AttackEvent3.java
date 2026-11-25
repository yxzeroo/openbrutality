package org.brutality.event.impl.attack;

import org.brutality.event.Event;

public class AttackEvent3 extends Event {
    private final float factor;

    public float getFactor() {
        return this.factor;
    }

    public AttackEvent3(float factor) {
        this.factor = factor;
    }
}
