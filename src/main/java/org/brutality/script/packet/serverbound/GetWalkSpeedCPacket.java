package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;

public class GetWalkSpeedCPacket extends CPacket {
    public float getWalkSpeed() {
        return 0.1F;
    }

    public boolean checkState2() {
        return this.packet instanceof C13PacketPlayerAbilities ? ((C13PacketPlayerAbilities)this.packet).isAllowFlying() : false;
    }

    public boolean isFlying() {
        return this.packet instanceof C13PacketPlayerAbilities ? ((C13PacketPlayerAbilities)this.packet).isFlying() : false;
    }

    public float getFlySpeed() {
        return 0.05F;
    }

    public boolean checkState() {
        return this.packet instanceof C13PacketPlayerAbilities ? ((C13PacketPlayerAbilities)this.packet).isInvulnerable() : false;
    }

    public GetWalkSpeedCPacket(Packet<?> packet) {
        super(packet);
    }
}
