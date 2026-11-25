package org.brutality.event.impl.pit;

import org.brutality.event.Event;

public class PitEvent2 extends Event {
    public final String text;
    public final String text2;
    public final int count;

    public PitEvent2(String text, String str, int count) {
        this.text = text;
        this.text2 = str;
        this.count = count;
    }
}
