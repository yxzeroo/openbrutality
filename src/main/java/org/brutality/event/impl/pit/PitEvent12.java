package org.brutality.event.impl.pit;

import org.brutality.event.Event;

public class PitEvent12 extends Event {
    public final boolean flag;
    public final String text;

    public PitEvent12(String text, boolean flag) {
        this.text = text;
        this.flag = flag;
    }
}
