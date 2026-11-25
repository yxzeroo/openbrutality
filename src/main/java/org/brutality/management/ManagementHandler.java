package org.brutality.management;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;

public class ManagementHandler {
    public boolean flag = false;
    public boolean flag2 = false;
    public boolean flag3 = false;
    public boolean flag4 = false;
    public boolean flag5 = false;

    public void handlePacket(Packet<?> packet) {
        if (packet instanceof C02PacketUseEntity) {
            this.flag = true;
        }

        if (packet instanceof C07PacketPlayerDigging) {
            this.flag2 = true;
        }

        if (packet instanceof C08PacketPlayerBlockPlacement) {
            this.flag3 = true;
        }

        if (packet instanceof C09PacketHeldItemChange) {
            this.flag4 = true;
        }

        if (packet instanceof C0APacketAnimation) {
            this.flag5 = true;
        }

        if (packet instanceof C03PacketPlayer) {
            this.flag = false;
            this.flag2 = false;
            this.flag3 = false;
            this.flag4 = false;
            this.flag5 = false;
        }
    }
}
