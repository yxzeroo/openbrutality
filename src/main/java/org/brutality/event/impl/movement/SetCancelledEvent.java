package org.brutality.event.impl.movement;

import org.brutality.event.Event;

public class SetCancelledEvent extends Event {
    private boolean sneaking2;
    private boolean cancelled2;

    public void setCancelled(boolean cancelled2) {
        this.cancelled2 = cancelled2;
        this.cancel = cancelled2;
    }

    public boolean isCancelled() {
        return this.cancelled2;
    }

    public SetCancelledEvent(boolean sneaking2) {
        this.sneaking2 = sneaking2;
    }

    public void setSneaking(boolean sneaking2) {
        this.sneaking2 = sneaking2;
    }

    public boolean isSneaking() {
        return this.sneaking2;
    }
}
