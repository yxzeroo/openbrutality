package org.brutality.event.impl.chat;

import org.brutality.event.Event;

public class EventChat extends Event {
    public String message2;

    public void setMessage(String message2) {
        this.message2 = message2;
    }

    public String getMessage() {
        return this.message2;
    }

    public EventChat(String message2) {
        this.message2 = message2;
    }
}
