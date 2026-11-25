package org.brutality.event.impl.velocity;

import org.brutality.event.Event;

public class GetEntityIdEvent extends Event {
    private double x2;
    private double y2;
    private double z2;
    private int entityId2;

    public double getY() {
        return this.y2;
    }

    public double getX() {
        return this.x2;
    }

    public void setY(double y2) {
        this.y2 = y2;
    }

    public GetEntityIdEvent(double x2, double y2, double z2, int entityId2) {
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.entityId2 = entityId2;
    }

    public void setZ(double z2) {
        this.z2 = z2;
    }

    public int getEntityId() {
        return this.entityId2;
    }

    public double getZ() {
        return this.z2;
    }

    public void setEntityId(int entityId2) {
        this.entityId2 = entityId2;
    }

    public void setX(double x2) {
        this.x2 = x2;
    }
}
