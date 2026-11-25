package org.brutality.module.impl.movement;

import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AutoWalk extends Module {
    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate) {
                    mc.gameSettings.keyBindForward.pressed = true;
                }
            }
        }
    }

    public AutoWalk() {
        super("Auto Walk", 0, Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindForward.pressed = false;
        super.onDisable();
    }
}
