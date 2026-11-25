package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

public class ServerboundCPacket3 extends CPacket {
    public String computeString() {
        if (this.packet instanceof C16PacketClientStatus) {
            EnumState status = ((C16PacketClientStatus)this.packet).getStatus();
            return status.name();
        }
        return "";
    }

    public ServerboundCPacket3(Packet<?> packet) {
        super(packet);
    }
}
