package org.brutality.module.impl.movement.flight;

import net.minecraft.client.Minecraft;
import org.brutality.settings.impl.NumberSetting;

public class FlightHandler3 {
    private final Minecraft mc = Minecraft.getMinecraft();

    public void handleNumberSetting(NumberSetting numberSetting) {
        this.mc.thePlayer.capabilities.isFlying = true;
        if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
            this.mc.thePlayer.motionY += 0.2F;
        }

        if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
            this.mc.thePlayer.motionY -= 0.2F;
        }

        if (this.mc.gameSettings.keyBindForward.isKeyDown()) {
            this.mc.thePlayer.capabilities.setFlySpeed((float)numberSetting.computeDouble());
        }
    }
}
