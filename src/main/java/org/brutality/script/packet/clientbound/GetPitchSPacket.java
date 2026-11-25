package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class GetPitchSPacket extends SPacket {
    public byte computeInt() {
        return 0;
    }

    public GetPitchSPacket(Packet<?> packet) {
        super(packet);
    }

    public double getY() {
        return this.packet instanceof S08PacketPlayerPosLook ? ((S08PacketPlayerPosLook)this.packet).getY() : 0.0;
    }

    public float getPitch() {
        return this.packet instanceof S08PacketPlayerPosLook ? ((S08PacketPlayerPosLook)this.packet).getPitch() : 0.0F;
    }

    public double getZ() {
        return this.packet instanceof S08PacketPlayerPosLook ? ((S08PacketPlayerPosLook)this.packet).getZ() : 0.0;
    }

    public float getYaw() {
        return this.packet instanceof S08PacketPlayerPosLook ? ((S08PacketPlayerPosLook)this.packet).getYaw() : 0.0F;
    }

    public double getX() {
        return this.packet instanceof S08PacketPlayerPosLook ? ((S08PacketPlayerPosLook)this.packet).getX() : 0.0;
    }
}
