package org.brutality.event.impl.chat;

import net.minecraft.util.IChatComponent;
import org.brutality.event.Event;

public class ChatEvent extends Event {
    public IChatComponent message;
    public final byte byteVal;

    public ChatEvent(byte byteVal, IChatComponent message) {
        this.byteVal = byteVal;
        this.message = message;
    }
}
