package org.brutality.event.impl.player;

import lombok.Generated;
import net.minecraft.entity.EntityLivingBase;
import org.brutality.event.Event;

public class GetHealthEvent extends Event {
    private final EntityLivingBase entity;
    private final float previousHealth;
    private final float health;

    @Generated
    public float getHealth() {
        return this.health;
    }

    public GetHealthEvent(EntityLivingBase entity, float previousHealth) {
        this.entity = entity;
        this.previousHealth = previousHealth;
        this.health = entity.getHealth();
    }

    @Generated
    public float getPreviousHealth() {
        return this.previousHealth;
    }

    public GetHealthEvent(EntityLivingBase living) {
        this(living, Float.NaN);
    }

    @Generated
    public EntityLivingBase getEntity() {
        return this.entity;
    }
}
