package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class SetMessageCPacket extends CPacket {
    public void setMessage(String str) {
        if (this.packet instanceof C01PacketChatMessage) {
        }
    }

    public String getMessage() {
        return this.packet instanceof C01PacketChatMessage ? ((C01PacketChatMessage)this.packet).getMessage() : "";
    }

    public SetMessageCPacket(Packet<?> packet) {
        super(packet);
    }
}
