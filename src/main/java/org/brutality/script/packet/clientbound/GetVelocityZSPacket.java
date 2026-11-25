package org.brutality.script.packet.clientbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class GetVelocityZSPacket extends SPacket {
    public int getVelocityZ() {
        return this.packet instanceof S12PacketEntityVelocity ? ((S12PacketEntityVelocity)this.packet).getMotionZ() : 0;
    }

    public GetVelocityZSPacket(Packet<?> packet) {
        super(packet);
    }

    public int getVelocityY() {
        return this.packet instanceof S12PacketEntityVelocity ? ((S12PacketEntityVelocity)this.packet).getMotionY() : 0;
    }

    public int getEntityId() {
        return this.packet instanceof S12PacketEntityVelocity ? ((S12PacketEntityVelocity)this.packet).getEntityID() : -1;
    }

    public int getVelocityX() {
        return this.packet instanceof S12PacketEntityVelocity ? ((S12PacketEntityVelocity)this.packet).getMotionX() : 0;
    }
}
