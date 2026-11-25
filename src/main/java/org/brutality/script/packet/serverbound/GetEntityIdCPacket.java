package org.brutality.script.packet.serverbound;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class GetEntityIdCPacket extends CPacket {
    public int getEntityId() {
        return -1;
    }

    public String computeString() {
        if (this.packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity.Action action = ((C02PacketUseEntity)this.packet).getAction();
            return action.name();
        }
        return "";
    }

    public GetEntityIdCPacket(Packet<?> packet) {
        super(packet);
    }

    public Entity computeEntity(Minecraft mc) {
        return mc.theWorld != null ? mc.theWorld.getEntityByID(this.getEntityId()) : null;
    }
}
