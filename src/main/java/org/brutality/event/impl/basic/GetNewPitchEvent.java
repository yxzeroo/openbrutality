package org.brutality.event.impl.basic;

import lombok.Generated;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.EventState;

public class GetNewPitchEvent extends Event {
    private final EventState state;
    private final float originalYaw;
    private final float originalPitch;
    private final float lastReportedYaw;
    private final float lastReportedPitch;
    private float newYaw;
    private float newPitch2;
    private float prevYaw;
    private int lastPriority = -1;
    private int priority2 = -1;
    private boolean rotated2 = false;

    @Generated
    public float getNewPitch() {
        return this.newPitch2;
    }

    @Generated
    public float getOriginalPitch() {
        return this.originalPitch;
    }

    public void handleFloat(float newYaw, float newPitch2, int lastPriority) {
        if (this.state == EventState.PRE && this.lastPriority <= lastPriority) {
            this.newYaw = newYaw;
            this.newPitch2 = newPitch2;
            this.lastPriority = lastPriority;
            this.rotated2 = true;
        }
    }

    @Generated
    public void setLastPriority(int lastPriority) {
        this.lastPriority = lastPriority;
    }

    @Generated
    public float getLastReportedYaw() {
        return this.lastReportedYaw;
    }

    @Generated
    public void setNewPitch(float newPitch2) {
        this.newPitch2 = newPitch2;
    }

    public void handleFloat(float prevYaw, int priority2) {
        if (this.state == EventState.PRE && this.priority2 < priority2) {
            this.prevYaw = prevYaw;
            this.priority2 = priority2;
            this.rotated2 = true;
        }
    }

    @Generated
    public void setRotated(boolean rotated2) {
        this.rotated2 = rotated2;
    }

    @Generated
    public float getLastReportedPitch() {
        return this.lastReportedPitch;
    }

    @Generated
    public void setPriority(int priority2) {
        this.priority2 = priority2;
    }

    @Generated
    public float getOriginalYaw() {
        return this.originalYaw;
    }

    @Generated
    public float getPrevYaw() {
        return this.prevYaw;
    }

    @Generated
    public int getPriority() {
        return this.priority2;
    }

    public float computeFloat2() {
        return MathHelper.wrapAngleTo180_float(this.newYaw - this.lastReportedYaw);
    }

    @Generated
    public float getNewYaw() {
        return this.newYaw;
    }

    @Generated
    public void setPrevYaw(float prevYaw) {
        this.prevYaw = prevYaw;
    }

    @Generated
    public EventState getState() {
        return this.state;
    }

    public float computeFloat() {
        return MathHelper.wrapAngleTo180_float(this.newPitch2 - this.lastReportedPitch);
    }

    @Generated
    public int getLastPriority() {
        return this.lastPriority;
    }

    public boolean isRotated() {
        return this.rotated2;
    }

    @Generated
    public void setNewYaw(float newYaw) {
        this.newYaw = newYaw;
    }

    public GetNewPitchEvent(EventState state, float lastReportedYaw, float lastReportedPitch, float originalYaw, float originalPitch) {
        this.state = state;
        this.lastReportedYaw = lastReportedYaw;
        this.lastReportedPitch = lastReportedPitch;
        this.originalYaw = originalYaw;
        this.originalPitch = originalPitch;
        this.newYaw = originalYaw;
        this.newPitch2 = originalPitch;
        this.prevYaw = originalYaw;
    }

    public int getPriority2() {
        return this.priority2;
    }
}
