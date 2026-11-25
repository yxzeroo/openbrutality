package org.brutality.event.impl.render;

import lombok.Generated;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.brutality.event.Event;

public class GetEntityEvent2 extends Event {
    private final EntityLivingBase entity;
    private final RendererLivingEntity<?> renderer;
    private final float partialTicks;
    private final double x;
    private final double y;
    private final double z;

    @Generated
    public GetEntityEvent2(EntityLivingBase entity, RendererLivingEntity<?> renderer, float partialTicks, double d, double d2, double d3) {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTicks = partialTicks;
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    @Generated
    public EntityLivingBase getEntity() {
        return this.entity;
    }

    @Generated
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Generated
    public double getZ() {
        return this.z;
    }

    @Generated
    public double getY() {
        return this.y;
    }

    @Generated
    public RendererLivingEntity<?> getRenderer() {
        return this.renderer;
    }

    @Generated
    public double getX() {
        return this.x;
    }
}
