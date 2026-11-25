package org.brutality.module.impl.movement;

import java.util.Collections;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class FastLadder extends Module {
    public final ModeSetting aacSetting = new ModeSetting("Type", this, "AAC", Collections.singletonList("AAC"));

    public FastLadder() {
        super("Fast Ladder", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.aacSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate) {
                    if (((EventUpdate)event).getState() == EventState.POST) {
                        return;
                    }

                    if (mc.thePlayer.isOnLadder()) {
                        mc.thePlayer.motionY = 0.169;
                    }
                }
            }
        }
    }
}
