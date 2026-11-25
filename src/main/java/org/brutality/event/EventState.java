package org.brutality.event;

import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.player.EventTick;

public enum EventState {
    PRE,
    POST;

    public static boolean onEvent(Event event) {
        if (event instanceof EventUpdate) {
            return ((EventUpdate)event).getState() == PRE;
        }
        return event instanceof EventTick ? ((EventTick)event).getState() == PRE : false;
    }

    public static boolean onEvent2(Event event) {
        if (event instanceof EventUpdate) {
            return ((EventUpdate)event).getState() == POST;
        }
        return event instanceof EventTick ? ((EventTick)event).getState() == POST : false;
    }

    private static EventState[] computeEventStateArray() {
        return new EventState[]{PRE, POST};
    }
}
