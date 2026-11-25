package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0EPacketClickWindow;

public class GetModeCPacket extends CPacket {
    public int getMode() {
        return this.packet instanceof C0EPacketClickWindow ? ((C0EPacketClickWindow)this.packet).getMode() : -1;
    }

    public GetModeCPacket(Packet<?> packet) {
        super(packet);
    }

    public int getSlotId() {
        return this.packet instanceof C0EPacketClickWindow ? ((C0EPacketClickWindow)this.packet).getSlotId() : -1;
    }

    public int getUsedButton() {
        return this.packet instanceof C0EPacketClickWindow ? ((C0EPacketClickWindow)this.packet).getUsedButton() : -1;
    }

    public int getWindowId() {
        return this.packet instanceof C0EPacketClickWindow ? ((C0EPacketClickWindow)this.packet).getWindowId() : -1;
    }
}
