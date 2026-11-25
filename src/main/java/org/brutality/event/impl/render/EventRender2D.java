package org.brutality.event.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import org.brutality.event.Event;

public class EventRender2D extends Event {
    private final ScaledResolution scaledResolution;
    public float partialTicks2;

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public float computeFloat() {
        return this.scaledResolution.getScaledWidth();
    }

    public float getPartialTicks2() {
        return this.partialTicks2;
    }

    public float getPartialTicks() {
        return this.partialTicks2;
    }

    public float computeFloat2() {
        return this.scaledResolution.getScaledHeight();
    }

    public EventRender2D(ScaledResolution scaledResolution, float partialTicks2) {
        this.scaledResolution = scaledResolution;
        this.partialTicks2 = partialTicks2;
    }
}
