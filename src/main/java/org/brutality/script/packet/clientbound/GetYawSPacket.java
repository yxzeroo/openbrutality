package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;

public class GetYawSPacket extends SPacket {
    public byte getYaw() {
        return 0;
    }

    public int getEntityId() {
        return -1;
    }

    public GetYawSPacket(Packet<?> packet) {
        super(packet);
    }
}
