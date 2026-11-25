package org.brutality.module.impl.pit.hypixel.utility;

import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;

public class AntiVenom extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;

    public AntiVenom() {
        super("AntiVenom", 0, Category.PIT);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof EventPacketReceive && mc.thePlayer != null) {
            if (((EventPacketReceive)event).getPacket() instanceof S1DPacketEntityEffect) {
                S1DPacketEntityEffect packet = (S1DPacketEntityEffect)((EventPacketReceive)event).getPacket();
                if (packet.getEntityId() == mc.thePlayer.getEntityId() && (packet.getEffectId() & 255) == Potion.poison.getId()) {
                    event.cancel();
                }
            }
        }
    }
}
