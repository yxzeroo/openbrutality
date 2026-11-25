package org.brutality.module.impl.visual.killeffects;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.BlockPos;
import org.brutality.module.impl.pit.hypixel.utility.PitUtils;
import org.brutality.module.impl.visual.KillEffects;

public class BloodExplosionHandler extends PitUtils {
    public void handleEntry2(KillEffects.Entry2 entry2) {
        if (entry2 != null) {
            String text = KillEffects.getInstance().lightningBoltSetting.getText();
            switch (text) {
                case "Blood Explosion":
                    this.handleDouble(entry2.amount, entry2.amount2, entry2.amount3, entry2.amount4, 152);
                    break;
                case "Lightning Bolt":
                    this.handleDouble(entry2.amount, entry2.amount2, entry2.amount3);
            }
        }
    }

    private void handleDouble(double d, double d2, double d3) {
        if (mc.theWorld != null) {
            EntityLightningBolt entity = new EntityLightningBolt(mc.theWorld, d, d2, d3);
            mc.theWorld.addWeatherEffect(entity);
            mc.theWorld.playSoundAtPos(new BlockPos(d, d2, d3), "ambient.weather.thunder", 5.0F, 1.0F, false);
        }
    }

    private void handleDouble(double d, double d2, double d3, double d4, int n) {
        if (mc.thePlayer != null && mc.renderGlobal != null) {
            mc.renderGlobal.playAuxSFX(mc.thePlayer, 2001, new BlockPos(d, d2 + d4, d3), n);
        }
    }
}
