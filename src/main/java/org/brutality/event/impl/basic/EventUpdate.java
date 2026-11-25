package org.brutality.event.impl.basic;

import org.brutality.event.Event;
import org.brutality.event.EventState;

public class EventUpdate extends Event {
    private final EventState state;

    public EventUpdate(EventState state) {
        this.state = state;
    }

    public EventState getState() {
        return this.state;
    }

    @Override
    public void process() {
        super.process();
    }
}
