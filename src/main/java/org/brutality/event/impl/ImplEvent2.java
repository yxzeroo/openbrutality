package org.brutality.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;

public final class ImplEvent2 extends Event {
    public final EntityPlayer entityPlayer;
    public final Entity entity;

    public ImplEvent2(EntityPlayer entityPlayer, Entity entity) {
        this.entityPlayer = entityPlayer;
        this.entity = entity;
    }

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
