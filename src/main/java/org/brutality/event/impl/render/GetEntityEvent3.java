package org.brutality.event.impl.render;

import net.minecraft.entity.Entity;
import org.brutality.event.Event;
import org.brutality.event.EventState;

public class GetEntityEvent3 extends Event {
    private final Entity entity;
    private final EventState state;

    public Entity getEntity() {
        return this.entity;
    }

    public EventState getState() {
        return this.state;
    }

    public GetEntityEvent3(Entity entity, EventState state) {
        this.entity = entity;
        this.state = state;
    }
}
