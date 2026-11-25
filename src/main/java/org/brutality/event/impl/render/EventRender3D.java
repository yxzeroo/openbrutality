package org.brutality.event.impl.render;

import org.brutality.event.Event;

public class EventRender3D extends Event {
    private float factor;

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public EventRender3D(float factor) {
        this.factor = factor;
    }

    public float getFactor() {
        return this.factor;
    }
}
