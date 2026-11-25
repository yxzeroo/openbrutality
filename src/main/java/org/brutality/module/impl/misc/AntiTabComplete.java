package org.brutality.module.impl.misc;

import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AntiTabComplete extends Module {
    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPacketSend && ((EventPacketSend)event).getPacket() instanceof C14PacketTabComplete) {
                    event.cancel();
                }

                if (event instanceof GetPacketEvent && ((GetPacketEvent)event).getPacket() instanceof S3APacketTabComplete) {
                    event.cancel();
                }
            }
        }
    }

    public AntiTabComplete() {
        super("AntiTabComplete", 0, Category.MISCELLANEOUS);
    }
}
