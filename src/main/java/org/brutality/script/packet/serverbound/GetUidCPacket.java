package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class GetUidCPacket extends CPacket {
    public boolean checkState() {
        return false;
    }

    public GetUidCPacket(Packet<?> packet) {
        super(packet);
    }

    public short getUid() {
        return this.packet instanceof C0FPacketConfirmTransaction ? ((C0FPacketConfirmTransaction)this.packet).getUid() : -1;
    }

    public int getWindowId() {
        return this.packet instanceof C0FPacketConfirmTransaction ? ((C0FPacketConfirmTransaction)this.packet).getWindowId() : -1;
    }
}
