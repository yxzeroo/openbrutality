package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;

public class GetEntityIdCPacket2 extends CPacket {
    public int getEntityId() {
        return -1;
    }

    public String computeString() {
        if (this.packet instanceof C0BPacketEntityAction) {
            Action action = ((C0BPacketEntityAction)this.packet).getAction();
            return action.name();
        }
        return "";
    }

    public GetEntityIdCPacket2(Packet<?> packet) {
        super(packet);
    }
}
