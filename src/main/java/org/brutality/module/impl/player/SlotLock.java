package org.brutality.module.impl.player;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.player.HotbarHelper;

public class SlotLock extends Module {
    @Override
    public void onEnable() {
        super.onEnable();
        HotbarHelper.init();
    }

    public SlotLock() {
        super("Slot Lock", 0, Category.PLAYER);
        this.handleFlag3(true);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPacketSend && mc.thePlayer != null) {
                EventPacketSend event2 = (EventPacketSend)event;
                if (event2.getPacket() instanceof C07PacketPlayerDigging) {
                    C07PacketPlayerDigging packet = (C07PacketPlayerDigging)event2.getPacket();
                    if (HotbarHelper.matchesC07PacketPlayerDigging(packet)) {
                        event2.setCancelled(true);
                    }
                }
            }
        }
    }
}
