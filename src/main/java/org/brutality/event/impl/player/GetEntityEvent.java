package org.brutality.event.impl.player;

import lombok.Generated;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.brutality.event.Event;

public class GetEntityEvent extends Event {
    private final EntityLivingBase entity;
    private final DamageSource source;

    @Generated
    public EntityLivingBase getEntity() {
        return this.entity;
    }

    @Generated
    public GetEntityEvent(EntityLivingBase entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
    }

    @Generated
    public DamageSource getSource() {
        return this.source;
    }
}
