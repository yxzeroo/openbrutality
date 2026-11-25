package org.brutality.script.packet.serverbound;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class GetFacingCPacket extends CPacket {
    public EnumFacing getFacing() {
        return this.packet instanceof C07PacketPlayerDigging ? ((C07PacketPlayerDigging)this.packet).getFacing() : null;
    }

    public String computeString() {
        if (this.packet instanceof C07PacketPlayerDigging) {
            Action status = ((C07PacketPlayerDigging)this.packet).getStatus();
            return status.name();
        }
        return "";
    }

    public BlockPos getPosition() {
        return this.packet instanceof C07PacketPlayerDigging ? ((C07PacketPlayerDigging)this.packet).getPosition() : null;
    }

    public GetFacingCPacket(Packet<?> packet) {
        super(packet);
    }
}
