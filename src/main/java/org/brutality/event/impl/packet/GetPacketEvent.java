package org.brutality.event.impl.packet;

import lombok.Generated;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDirection;
import org.brutality.event.Event;

public class GetPacketEvent extends Event {
    private Packet packet2;
    private final PacketDirection direction;

    public <T extends Packet> T getPacket() {
        return (T)this.packet2;
    }

    @Generated
    public GetPacketEvent(Packet packet2, PacketDirection direction) {
        this.packet2 = packet2;
        this.direction = direction;
    }

    public void setPacket2(Packet<?> packet2) {
        this.packet2 = packet2;
    }

    @Generated
    public PacketDirection getDirection() {
        return this.direction;
    }
}
