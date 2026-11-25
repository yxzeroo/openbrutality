package org.brutality.event.impl.player;

import net.minecraft.entity.Entity;
import org.brutality.event.Event;

public class GetEntityEvent2 extends Event {
    private final Entity entity;

    public GetEntityEvent2(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
