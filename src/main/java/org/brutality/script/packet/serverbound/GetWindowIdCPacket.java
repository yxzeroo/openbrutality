package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;

public class GetWindowIdCPacket extends CPacket {
    public int getWindowId() {
        return -1;
    }

    public GetWindowIdCPacket(Packet<?> packet) {
        super(packet);
    }
}
