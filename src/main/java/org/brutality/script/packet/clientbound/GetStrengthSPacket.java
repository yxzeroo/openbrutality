package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S27PacketExplosion;

public class GetStrengthSPacket extends SPacket {
    public float getStrength() {
        return this.packet instanceof S27PacketExplosion ? ((S27PacketExplosion)this.packet).getStrength() : 0.0F;
    }

    public double getX() {
        return this.packet instanceof S27PacketExplosion ? ((S27PacketExplosion)this.packet).func_149149_c() : 0.0;
    }

    public GetStrengthSPacket(Packet<?> packet) {
        super(packet);
    }

    public double getY() {
        return this.packet instanceof S27PacketExplosion ? ((S27PacketExplosion)this.packet).func_149144_d() : 0.0;
    }

    public double getZ() {
        return this.packet instanceof S27PacketExplosion ? ((S27PacketExplosion)this.packet).func_149147_e() : 0.0;
    }
}
