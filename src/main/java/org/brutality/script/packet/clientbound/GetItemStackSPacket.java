package org.brutality.script.packet.clientbound;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;

public class GetItemStackSPacket extends SPacket {
    public GetItemStackSPacket(Packet<?> packet) {
        super(packet);
    }

    public ItemStack getItemStack() {
        return null;
    }

    public int getEntityId() {
        return this.packet instanceof S04PacketEntityEquipment ? ((S04PacketEntityEquipment)this.packet).getEntityID() : -1;
    }

    public int getEquipmentSlot() {
        return this.packet instanceof S04PacketEntityEquipment ? ((S04PacketEntityEquipment)this.packet).getEquipmentSlot() : -1;
    }
}
