package org.brutality.module.impl.movement.flight;

import net.minecraft.client.Minecraft;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.GetSpeedHelper;

public class FlightHandler4 {
    private final Minecraft mc = Minecraft.getMinecraft();

    public void handleNumberSetting(NumberSetting numberSetting) {
        if (this.mc.gameSettings.keyBindJump.isKeyDown() && this.mc.gameSettings.keyBindSneak.isKeyDown()) {
            this.mc.thePlayer.motionY = 0.0;
        } else if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.mc.thePlayer.motionY = (float)numberSetting.computeDouble();
        } else if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
            this.mc.thePlayer.motionY = -((float)numberSetting.computeDouble());
        } else {
            this.mc.thePlayer.motionY = 0.0;
        }

        if (!this.mc.gameSettings.keyBindForward.isKeyDown()
            && !this.mc.gameSettings.keyBindBack.isKeyDown()
            && !this.mc.gameSettings.keyBindLeft.isKeyDown()
            && !this.mc.gameSettings.keyBindRight.isKeyDown()) {
            this.mc.thePlayer.motionX = 0.0;
            this.mc.thePlayer.motionZ = 0.0;
        } else {
            GetSpeedHelper.handleFloat2((float)numberSetting.computeDouble());
        }
    }
}
