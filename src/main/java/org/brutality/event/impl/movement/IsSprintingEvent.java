package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class IsSprintingEvent extends Event {
    private boolean sprinting2;
    private boolean sneaking2;

    public boolean isSprinting() {
        return this.sprinting2;
    }

    public void setSneaking(boolean sneaking2) {
        this.sneaking2 = sneaking2;
    }

    public IsSprintingEvent(boolean sprinting2, boolean sneaking2) {
        this.sprinting2 = sprinting2;
        this.sneaking2 = sneaking2;
    }

    public void setSprinting(boolean sprinting2) {
        this.sprinting2 = sprinting2;
    }

    public boolean isSneaking() {
        return this.sneaking2;
    }
}
