package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;

public class SPacket {
    protected final Packet<?> packet;

    public Packet<?> getPacket() {
        return this.packet;
    }

    @Override
    public String toString() {
        return "SPacket(" + this.computeString2() + ")";
    }

    public String computeString2() {
        return this.packet.getClass().getSimpleName();
    }

    public SPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public boolean matchesString(String str) {
        return this.computeString2().equals(str);
    }
}
