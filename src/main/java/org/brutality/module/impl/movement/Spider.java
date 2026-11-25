package org.brutality.module.impl.movement;

import java.util.Arrays;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Spider extends Module {
    private final ModeSetting verusSetting = new ModeSetting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "Verus"));

    public Spider() {
        super("Spider", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.verusSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate && mc.thePlayer.isCollidedHorizontally) {
                    String text = this.verusSetting.getText();
                    switch (text) {
                        case "Vanilla":
                            if (mc.thePlayer.ticksExisted % 2 == 0) {
                                mc.thePlayer.motionY = 1.0;
                            }
                            break;
                        case "Verus":
                            if (mc.thePlayer.ticksExisted % 3 == 0) {
                                mc.thePlayer.motionY = 0.42F;
                            }
                    }
                }
            }
        }
    }
}
