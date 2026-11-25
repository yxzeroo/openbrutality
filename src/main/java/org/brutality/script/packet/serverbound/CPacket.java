package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;

public class CPacket {
    protected final Packet<?> packet;

    public boolean matchesString(String str) {
        return this.computeString2().equals(str);
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public CPacket(Packet<?> packet) {
        this.packet = packet;
    }

    @Override
    public String toString() {
        return "CPacket(" + this.computeString2() + ")";
    }

    public String computeString2() {
        return this.packet.getClass().getSimpleName();
    }
}
