package org.brutality.event.impl.attack;

import net.minecraft.entity.EntityLivingBase;
import org.brutality.event.Event;

public final class AttackEvent2 extends Event {
    private final EntityLivingBase entityLivingBase;

    public AttackEvent2(EntityLivingBase entityLivingBase) {
        this.entityLivingBase = entityLivingBase;
    }

    public EntityLivingBase getEntityLivingBase() {
        return this.entityLivingBase;
    }
}
