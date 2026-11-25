package org.brutality.event.impl.basic;

import org.brutality.event.Event;
import org.lwjgl.input.Mouse;

public class BasicEvent3 extends Event {
    public final int count = Mouse.getEventX();
    public final int count2 = Mouse.getEventY();
    public final int count3 = Mouse.getEventDX();
    public final int count4 = Mouse.getEventDY();
    public final int count5 = Mouse.getEventDWheel();
    public final int count6 = Mouse.getEventButton();
    public final boolean flag = Mouse.getEventButtonState();
    public final long timestamp = Mouse.getEventNanoseconds();
}
