package org.brutality.module.impl.player;

import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.player.GetPitchHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class FreeLook extends Module {
    private boolean flag;

    private boolean matchesInt(int n) {
        if (n == 0) {
            return false;
        }
        if (n > 0) {
            return n < 256 ? Keyboard.isKeyDown(n) : false;
        }
        switch (n) {
            case -5:
                return Mouse.isButtonDown(4);
            case -4:
                return Mouse.isButtonDown(3);
            case -3:
                return Mouse.isButtonDown(2);
            case -2:
                return Mouse.isButtonDown(1);
            case -1:
                return Mouse.isButtonDown(0);
            default:
                if (n < -10) {
                    int n2 = ~(n - 1) - 10;
                    if (n2 >= 0 && n2 < Mouse.getButtonCount()) {
                        return Mouse.isButtonDown(n2);
                    }
                }

                return false;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                if (mc.thePlayer.ticksExisted < 10) {
                    this.stop();
                }

                if (this.matchesInt(this.getKey())) {
                    if (!this.flag) {
                        this.flag = true;
                        GetPitchHelper.enable();
                        GetPitchHelper.cameraYaw += 180.0F;
                        mc.gameSettings.thirdPersonView = 1;
                    }
                } else if (this.flag) {
                    this.stop();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.flag = false;
        GetPitchHelper.setFlag(false);
        mc.gameSettings.thirdPersonView = 0;
    }

    public FreeLook() {
        super("Free Look", 0, Category.PLAYER);
    }

    private void stop() {
        this.process28();
        GetPitchHelper.setFlag(false);
        this.flag = false;
        mc.gameSettings.thirdPersonView = 0;
    }
}
