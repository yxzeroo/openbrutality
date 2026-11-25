package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

public class GetSlotIdCPacket extends CPacket {
    public GetSlotIdCPacket(Packet<?> packet) {
        super(packet);
    }

    public int getSlotId() {
        return this.packet instanceof C10PacketCreativeInventoryAction ? ((C10PacketCreativeInventoryAction)this.packet).getSlotId() : -1;
    }
}
