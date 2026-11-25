package org.brutality.module.impl.movement;

import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class JetPack extends Module {
    public JetPack() {
        super("Jet Pack", 0, Category.MOVEMENT);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate && mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.jump();
                }
            }
        }
    }
}
