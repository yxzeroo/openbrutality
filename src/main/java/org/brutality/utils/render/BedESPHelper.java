package org.brutality.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import org.brutality.Brutality;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.BedESP;

public final class BedESPHelper {
    private static final Minecraft b = Minecraft.getMinecraft();

    public static void handlePacket(Packet<?> packet) {
        OnBlockChangeHelper.getInstance().handlePacket(packet);
    }

    private BedESPHelper() {
    }

    public static void onEventUpdate(EventUpdate event) {
        if (event.getState() == EventState.PRE && b.theWorld != null && b.thePlayer != null) {
            OnBlockChangeHelper instance = OnBlockChangeHelper.getInstance();
            if (instance.isFlag()) {
                int n = 0;
                if (Brutality.moduleManager != null) {
                    Module module = Brutality.moduleManager.computeModule("BedESP");
                    if (module instanceof BedESP && module.checkState44()) {
                        n = Math.max(n, ((BedESP)module).computeInt2());
                    }
                }

                if (n > 0) {
                    instance.handleInt(n);
                }
            }
        }
    }
}
