package org.brutality.module.impl.pit.hypixel.hud;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;

public abstract class HudModule extends Module {
    private static final String[] stringArray = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1000.0, 1.0);
    private final NumberSetting ySetting;
    private final String text;
    private final Potion potion;
    private final int count;

    public HudModule(String str, String text, Potion potion, int n, int count) {
        super(str, 0, Category.PIT);
        this.text = text;
        this.potion = potion;
        this.count = count;
        this.ySetting = new NumberSetting("Y", this, n, 0.0, 1000.0, 1.0);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xSetting, this.ySetting});
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender2D && mc.thePlayer != null) {
            PotionEffect activePotionEffect = mc.thePlayer.getActivePotionEffect(this.potion);
            if (activePotionEffect != null && activePotionEffect.getDuration() > 0) {
                int n = activePotionEffect.getAmplifier() + 1;
                String str = this.text + "-" + (n >= 1 && n <= stringArray.length ? stringArray[n - 1] : n) + ":";
                String str2 = Long.toString((activePotionEffect.getDuration() + 19L) / 20L);
                FontRenderer fontRenderer = this.computeFontRenderer2();
                float f = (float)this.xSetting.computeDouble();
                float f2 = (float)this.ySetting.computeDouble();
                fontRenderer.drawStringWithShadow(str, f, f2, -1);
                fontRenderer.drawStringWithShadow(str2, f + fontRenderer.getStringWidth(str) + 4.0F, f2, this.count);
            }
        }
    }
}
