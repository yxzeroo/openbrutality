package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;

public class GetDataCPacket extends CPacket {
    public byte[] getData() {
        return new byte[0];
    }

    public String computeString() {
        return "";
    }

    public GetDataCPacket(Packet<?> packet) {
        super(packet);
    }
}
