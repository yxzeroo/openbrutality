package org.brutality.event.impl.pit;

import org.brutality.event.Event;
import org.brutality.util.OverdriveType;

public class PitEvent8 extends Event {
    public final OverdriveType overdriveType;
    public final String text;

    public PitEvent8(String text, OverdriveType overdriveType) {
        this.text = text;
        this.overdriveType = overdriveType;
    }
}
