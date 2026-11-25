package org.brutality.module.impl.misc;

import net.minecraft.network.play.server.S02PacketChat;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public final class TPATrap extends Module {
    public TPATrap() {
        super("TPA Trap", 0, Category.EXPLOIT);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPacketReceive event2 && event2.getPacket() instanceof S02PacketChat && mc.thePlayer != null) {
                    S02PacketChat packet = (S02PacketChat)event2.getPacket();
                    String str = packet.getChatComponent().getUnformattedText().toLowerCase();
                    if (str.contains("tpa") || str.contains("request")) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 6.0, mc.thePlayer.posZ);
                        mc.thePlayer.sendChatMessage("/tpa accept");
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 6.0, mc.thePlayer.posZ);
                        }
                    }
                }
            }
        }
    }
}
