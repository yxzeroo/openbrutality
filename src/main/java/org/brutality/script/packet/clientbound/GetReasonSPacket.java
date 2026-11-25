package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S40PacketDisconnect;

public class GetReasonSPacket extends SPacket {
    public String computeString() {
        return this.packet instanceof S40PacketDisconnect ? ((S40PacketDisconnect)this.packet).getReason().getFormattedText() : "";
    }

    public String getReason() {
        return this.packet instanceof S40PacketDisconnect ? ((S40PacketDisconnect)this.packet).getReason().getUnformattedText() : "";
    }

    public GetReasonSPacket(Packet<?> packet) {
        super(packet);
    }
}
