package org.brutality.module.impl.misc;

import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.lwjgl.input.Mouse;

public class MiscModule extends Module {
    private static int count = -1;
    private static int count2 = -1;
    private static boolean flag = false;
    private static boolean flag2 = false;
    private static final String text = "Mouse Save";

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            flag = mc.currentScreen != null;
        }
    }

    private void process3() {
        count = Mouse.getX();
        count2 = Mouse.getY();
        flag2 = true;
    }

    public MiscModule() {
        super(text, 0, Category.MISCELLANEOUS);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate) {
                    boolean flag3 = mc.currentScreen != null;
                    if (flag3 && !flag) {
                        if (flag2) {
                            this.process();
                        }
                    } else if (!flag3 && flag) {
                        this.process3();
                    } else if (flag3) {
                        this.process3();
                    }

                    flag = flag3;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        count = -1;
        count2 = -1;
        flag2 = false;
        flag = false;
    }

    private void process() {
        if (flag2 && count >= 0 && count2 >= 0) {
            int width = mc.displayWidth;
            int height = mc.displayHeight;
            int n = Math.max(0, Math.min(count, width - 1));
            int n2 = Math.max(0, Math.min(count2, height - 1));
            Mouse.setCursorPosition(n, n2);
        }
    }
}
