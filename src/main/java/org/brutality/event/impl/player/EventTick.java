package org.brutality.event.impl.player;

import lombok.Generated;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.module.impl.pit.hypixel.swap.FindHandler;
import org.brutality.module.impl.pit.hypixel.utility.SwapMacros;
import org.brutality.utils.player.ActiveHelper;
// VENDOR-DISABLED: import org.brutality.utils.player.PlaytimeTelemetry;

public class EventTick extends Event {
    private EventState state;

    @Generated
    public void setState(EventState state) {
        this.state = state;
    }

    @Override
    public void process() {
        super.process();
        if (this.state == EventState.PRE) {
            SwapMacros.process();
            ActiveHelper.process();
            FindHandler.tick();
        }

        // VENDOR-DISABLED: Brutality.proximityTracker.onEvent(this); PlaytimeTelemetry.tick();
    }

    public EventState getState() {
        return this.state;
    }

    public EventTick(EventState state) {
        this.state = state;
    }
}
