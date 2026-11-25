package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class ServerboundCPacket extends CPacket {
    public int computeInt() {
        return this.packet instanceof C09PacketHeldItemChange ? ((C09PacketHeldItemChange)this.packet).getSlotId() : -1;
    }

    public ServerboundCPacket(Packet<?> packet) {
        super(packet);
    }
}
