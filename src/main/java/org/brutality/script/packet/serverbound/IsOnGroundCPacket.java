package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class IsOnGroundCPacket extends CPacket {
    public boolean isOnGround() {
        return this.packet instanceof C03PacketPlayer ? ((C03PacketPlayer)this.packet).isOnGround() : false;
    }

    public IsOnGroundCPacket(Packet<?> packet) {
        super(packet);
    }

    public boolean isMoving() {
        return this.packet instanceof C03PacketPlayer.C04PacketPlayerPosition || this.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook;
    }

    public float getYaw() {
        return this.packet instanceof C03PacketPlayer ? ((C03PacketPlayer)this.packet).getYaw() : 0.0F;
    }

    public double getX() {
        return this.packet instanceof C03PacketPlayer ? ((C03PacketPlayer)this.packet).getPositionX() : 0.0;
    }

    public double getZ() {
        return this.packet instanceof C03PacketPlayer ? ((C03PacketPlayer)this.packet).getPositionZ() : 0.0;
    }

    public float getPitch() {
        return this.packet instanceof C03PacketPlayer ? ((C03PacketPlayer)this.packet).getPitch() : 0.0F;
    }

    public boolean checkState() {
        return this.packet instanceof C03PacketPlayer.C05PacketPlayerLook || this.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook;
    }

    public double getY() {
        return this.packet instanceof C03PacketPlayer ? ((C03PacketPlayer)this.packet).getPositionY() : 0.0;
    }
}
