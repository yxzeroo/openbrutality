package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

public class GetTypeSPacket extends SPacket {
    public byte getType() {
        return this.packet instanceof S02PacketChat ? ((S02PacketChat)this.packet).getType() : 0;
    }

    public GetTypeSPacket(Packet<?> packet) {
        super(packet);
    }

    public boolean checkState() {
        return this.getType() == 1;
    }

    public String getMessage() {
        return this.packet instanceof S02PacketChat ? ((S02PacketChat)this.packet).getChatComponent().getUnformattedText() : "";
    }

    public String computeString() {
        return this.packet instanceof S02PacketChat ? ((S02PacketChat)this.packet).getChatComponent().getFormattedText() : "";
    }

    public boolean checkState2() {
        return this.getType() == 0;
    }
}
