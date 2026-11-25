package org.brutality.module.impl.misc;

import net.minecraft.network.play.server.S29PacketSoundEffect;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;

public class LightningTracker extends Module {
    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetPacketEvent && ((GetPacketEvent)event).getPacket() instanceof S29PacketSoundEffect) {
                    S29PacketSoundEffect packet = ((GetPacketEvent)event).getPacket();
                    if (packet.getSoundName().equals("ambient.weather.thunder")) {
                        String str = String.format("Lightning detected at (%d, %d, %d)", (int)packet.getX(), (int)packet.getY(), (int)packet.getZ());
                        MessageUtils.x(str);
                    }
                }
            }
        }
    }

    public LightningTracker() {
        super("Lightning Tracker", 0, Category.MISCELLANEOUS);
    }
}
