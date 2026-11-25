package org.brutality.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.packet.EventPacketSend;

public final class PlayerHelper9 {
    private PlayerHelper9() {
    }

    public static ItemStack onEvent(Event event) {
        if (event.cancel2()) {
            return null;
        }
        if (event instanceof BasicEvent4) {
            Minecraft mc = Minecraft.getMinecraft();
            return mc.thePlayer == null ? null : mc.thePlayer.getHeldItem();
        }
        if (event instanceof EventPacketSend event2) {
            if (!event2.isCancelled() && event2.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement)event2.getPacket();
                return packet.getPlacedBlockDirection() == 255 ? packet.getStack() : null;
            }
            return null;
        }
        return null;
    }
}
