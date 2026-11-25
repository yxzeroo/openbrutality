package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class GetPositionCPacket extends CPacket {
    public BlockPos getPosition() {
        return this.packet instanceof C08PacketPlayerBlockPlacement ? ((C08PacketPlayerBlockPlacement)this.packet).getPosition() : null;
    }

    public GetPositionCPacket(Packet<?> packet) {
        super(packet);
    }

    public EnumFacing getFacing() {
        return null;
    }
}
