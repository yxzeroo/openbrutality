package org.brutality.module.impl.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Dolphin extends Module {
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 0.2, 0.1, 1.0, 0.1);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE && (mc.thePlayer.isInWater() || mc.thePlayer.isInLava())) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY = mc.thePlayer.motionY + this.speedSetting.computeDouble();
                    }

                    if (mc.gameSettings.keyBindForward.isKeyDown()
                        || mc.gameSettings.keyBindLeft.isKeyDown()
                        || mc.gameSettings.keyBindRight.isKeyDown()
                        || mc.gameSettings.keyBindBack.isKeyDown()) {
                        EntityPlayerSP player = mc.thePlayer;
                        float yaw = player.rotationYaw;
                        double d = this.speedSetting.computeDouble();
                        if (mc.gameSettings.keyBindForward.isKeyDown()) {
                            player.motionX = player.motionX - Math.sin(Math.toRadians(yaw)) * d;
                            player.motionZ = player.motionZ + Math.cos(Math.toRadians(yaw)) * d;
                        }

                        if (mc.gameSettings.keyBindBack.isKeyDown()) {
                            player.motionX = player.motionX + Math.sin(Math.toRadians(yaw)) * d;
                            player.motionZ = player.motionZ - Math.cos(Math.toRadians(yaw)) * d;
                        }

                        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                            player.motionX = player.motionX + Math.sin(Math.toRadians(yaw - 90.0F)) * d;
                            player.motionZ = player.motionZ - Math.cos(Math.toRadians(yaw - 90.0F)) * d;
                        }

                        if (mc.gameSettings.keyBindRight.isKeyDown()) {
                            player.motionX = player.motionX + Math.sin(Math.toRadians(yaw + 90.0F)) * d;
                            player.motionZ = player.motionZ - Math.cos(Math.toRadians(yaw + 90.0F)) * d;
                        }
                    }
                }
            }
        }
    }

    public Dolphin() {
        super("Dolphin", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.speedSetting});
    }
}
