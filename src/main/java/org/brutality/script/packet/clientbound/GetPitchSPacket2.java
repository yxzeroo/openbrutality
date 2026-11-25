package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class GetPitchSPacket2 extends SPacket {
    public double getZ() {
        return this.packet instanceof S29PacketSoundEffect ? ((S29PacketSoundEffect)this.packet).getZ() : 0.0;
    }

    public double getX() {
        return this.packet instanceof S29PacketSoundEffect ? ((S29PacketSoundEffect)this.packet).getX() : 0.0;
    }

    public int getPitch() {
        return this.packet instanceof S29PacketSoundEffect ? (int)((S29PacketSoundEffect)this.packet).getPitch() : 0;
    }

    public float getVolume() {
        return this.packet instanceof S29PacketSoundEffect ? ((S29PacketSoundEffect)this.packet).getVolume() : 0.0F;
    }

    public double getY() {
        return this.packet instanceof S29PacketSoundEffect ? ((S29PacketSoundEffect)this.packet).getY() : 0.0;
    }

    public String getSoundName() {
        return this.packet instanceof S29PacketSoundEffect ? ((S29PacketSoundEffect)this.packet).getSoundName() : "";
    }

    public GetPitchSPacket2(Packet<?> packet) {
        super(packet);
    }
}
