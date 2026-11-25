package org.brutality.module.impl.misc;

import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("Auto Respawn", 0, Category.MISCELLANEOUS);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetPacketEvent && mc.thePlayer != null && mc.thePlayer.isDead) {
                    mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.PERFORM_RESPAWN));
                }
            }
        }
    }
}
