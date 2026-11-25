package org.brutality.event.impl.packet;

import lombok.Generated;
import net.minecraft.network.Packet;
import org.brutality.event.Event;

public class EventPacketReceive extends Event {
    private Packet<?> packet2;

    @Generated
    public Packet<?> getPacket() {
        return this.packet2;
    }

    public void setPacket2(Packet<?> packet2) {
        this.packet2 = packet2;
    }

    public EventPacketReceive(Packet<?> packet2) {
        this.packet2 = packet2;
    }
}
