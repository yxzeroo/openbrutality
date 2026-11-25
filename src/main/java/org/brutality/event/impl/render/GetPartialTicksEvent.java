package org.brutality.event.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import org.brutality.event.Event;

public class GetPartialTicksEvent extends Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public GetPartialTicksEvent(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }
}
