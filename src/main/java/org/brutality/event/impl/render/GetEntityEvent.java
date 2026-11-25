package org.brutality.event.impl.render;

import net.minecraft.entity.Entity;
import org.brutality.event.Event;
import org.brutality.event.EventState;

public class GetEntityEvent extends Event {
    private final Entity entity;
    private final EventState state;
    private boolean cancel;
    private float factor = 1.0F;

    public float getFactor() {
        return this.factor;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public GetEntityEvent(Entity entity, EventState state) {
        this.entity = entity;
        this.state = state;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean cancel2() {
        return this.cancel;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public EventState getState() {
        return this.state;
    }
}
