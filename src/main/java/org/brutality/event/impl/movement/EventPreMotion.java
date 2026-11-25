package org.brutality.event.impl.movement;

import lombok.Generated;
import net.minecraft.client.entity.EntityPlayerSP;
import org.brutality.event.Event;
import org.brutality.event.EventState;

public class EventPreMotion extends Event {
    public double x2;
    public double y2;
    public double z2;
    public float yaw2;
    public float pitch2;
    public float lastTickYaw;
    public float lastTickPitch;
    public boolean ground2;
    public boolean forcedC06;
    private boolean movementYawApplied;
    public EventState state = EventState.PRE;
    public final EntityPlayerSP player;

    @Generated
    public void setX(double x2) {
        this.x2 = x2;
    }

    @Generated
    public void setForcedC06(boolean forcedC06) {
        this.forcedC06 = forcedC06;
    }

    @Generated
    public void setLastTickYaw(float lastTickYaw) {
        this.lastTickYaw = lastTickYaw;
    }

    @Generated
    public void setLastTickPitch(float lastTickPitch) {
        this.lastTickPitch = lastTickPitch;
    }

    @Generated
    public void setY(double y2) {
        this.y2 = y2;
    }

    @Generated
    public float getLastTickPitch() {
        return this.lastTickPitch;
    }

    public void handleFloat(float yaw2, float pitch2) {
        if (!this.movementYawApplied) {
            this.yaw2 = yaw2;
            this.pitch2 = pitch2;
        }
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
    public double getZ() {
        return this.z2;
    }

    @Generated
    public void setZ(double z2) {
        this.z2 = z2;
    }

    @Generated
    public float getYaw() {
        return this.yaw2;
    }

    @Generated
    public EntityPlayerSP getPlayer() {
        return this.player;
    }

    @Generated
    public EventState getState() {
        return this.state;
    }

    public void handleFloat(float yaw2) {
        this.yaw2 = yaw2;
        this.movementYawApplied = true;
    }

    @Generated
    public double getY() {
        return this.y2;
    }

    @Generated
    public void setState(EventState state) {
        this.state = state;
    }

    public EventPreMotion(double x2, double y2, double z2, float yaw2, float pitch2, float lastTickYaw, float lastTickPitch, boolean ground2, EntityPlayerSP player) {
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.yaw2 = yaw2;
        this.pitch2 = pitch2;
        this.lastTickYaw = lastTickYaw;
        this.lastTickPitch = lastTickPitch;
        this.ground2 = ground2;
        this.player = player;
    }

    @Generated
    public void setYaw(float yaw2) {
        this.yaw2 = yaw2;
    }

    @Generated
    public float getPitch() {
        return this.pitch2;
    }

    @Generated
    public boolean isMovementYawApplied() {
        return this.movementYawApplied;
    }

    @Generated
    public void setMovementYawApplied(boolean movementYawApplied) {
        this.movementYawApplied = movementYawApplied;
    }

    @Generated
    public boolean isForcedC06() {
        return this.forcedC06;
    }

    @Generated
    public boolean isGround() {
        return this.ground2;
    }

    @Generated
    public float getLastTickYaw() {
        return this.lastTickYaw;
    }

    @Generated
    public void setPitch(float pitch2) {
        this.pitch2 = pitch2;
    }
}
