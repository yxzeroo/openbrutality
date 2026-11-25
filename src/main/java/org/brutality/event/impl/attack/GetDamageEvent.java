package org.brutality.event.impl.attack;

import net.minecraft.entity.Entity;
import org.brutality.event.Event;

public class GetDamageEvent extends Event {
    public final Entity entity;
    public final Entity entity2;
    public final float damage;
    public final boolean flag;

    public boolean isFlag() {
        return this.flag;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public float getDamage() {
        return this.damage;
    }

    public GetDamageEvent(Entity entity, Entity entity2, float damage, boolean flag) {
        this.entity = entity;
        this.entity2 = entity2;
        this.damage = damage;
        this.flag = flag;
    }

    public Entity getEntity2() {
        return this.entity2;
    }
}
