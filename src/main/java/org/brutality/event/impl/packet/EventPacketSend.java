package org.brutality.event.impl.packet;

import net.minecraft.network.Packet;
import org.brutality.event.Event;

public class EventPacketSend extends Event {
    private Packet<?> packet2;
    private boolean cancelled2;

    public EventPacketSend(Packet<?> packet2) {
        this.packet2 = packet2;
        this.cancelled2 = false;
    }

    public boolean isCancelled() {
        return this.cancelled2;
    }

    public void setPacket2(Packet<?> packet2) {
        this.packet2 = packet2;
    }

    public Packet<?> getPacket() {
        return this.packet2;
    }

    public void setCancelled(boolean cancelled2) {
        this.cancelled2 = cancelled2;
    }
}
