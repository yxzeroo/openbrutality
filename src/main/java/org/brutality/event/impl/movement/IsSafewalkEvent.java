package org.brutality.event.impl.movement;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;

public class IsSafewalkEvent extends Event {
    private double x2;
    private double y2;
    private double z2;
    public boolean safewalk = false;

    @Generated
    public double getZ() {
        return this.z2;
    }

    public void setY(double y2) {
        Minecraft.getMinecraft().thePlayer.motionY = y2;
        this.y2 = y2;
    }

    public void process2() {
        this.setX(0.0);
        this.setZ(0.0);
        this.setY(0.0);
    }

    public void setZ(double z2) {
        Minecraft.getMinecraft().thePlayer.motionZ = z2;
        this.z2 = z2;
    }

    @Generated
    public double getX() {
        return this.x2;
    }

    @Generated
    public double getY() {
        return this.y2;
    }

    public void process3() {
        this.setX(0.0);
        this.setZ(0.0);
    }

    public void setX(double x2) {
        Minecraft.getMinecraft().thePlayer.motionX = x2;
        this.x2 = x2;
    }

    @Generated
    public IsSafewalkEvent(double x2, double y2, double z2, boolean safewalk) {
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.safewalk = safewalk;
    }

    @Generated
    public boolean isSafewalk() {
        return this.safewalk;
    }
}
