package org.brutality.event.impl.movement;

import lombok.Generated;
import net.minecraft.client.entity.EntityPlayerSP;
import org.brutality.event.Event;

public class EventPostMotion extends Event {
    private double x2;
    private double y2;
    private double z2;
    private float yaw2;
    private float pitch2;
    private boolean ground2;
    private final EntityPlayerSP player;

    @Generated
    public void setX(double x2) {
        this.x2 = x2;
    }

    @Generated
    public double getY() {
        return this.y2;
    }

    public EventPostMotion(double x2, double y2, double z2, float yaw2, float pitch2, boolean ground2, EntityPlayerSP player) {
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.yaw2 = yaw2;
        this.pitch2 = pitch2;
        this.ground2 = ground2;
        this.player = player;
    }

    @Generated
    public void setY(double y2) {
        this.y2 = y2;
    }

    @Generated
    public float getPitch() {
        return this.pitch2;
    }

    @Generated
    public void setGround(boolean ground2) {
        this.ground2 = ground2;
    }

    @Generated
    public double getX() {
        return this.x2;
    }

    @Generated
    public boolean isGround() {
        return this.ground2;
    }

    @Generated
    public float getYaw() {
        return this.yaw2;
    }

    @Generated
    public void setYaw(float yaw2) {
        this.yaw2 = yaw2;
    }

    @Generated
    public void setZ(double z2) {
        this.z2 = z2;
    }

    @Generated
    public EntityPlayerSP getPlayer() {
        return this.player;
    }

    @Generated
    public void setPitch(float pitch2) {
        this.pitch2 = pitch2;
    }

    @Generated
    public double getZ() {
        return this.z2;
    }
}
