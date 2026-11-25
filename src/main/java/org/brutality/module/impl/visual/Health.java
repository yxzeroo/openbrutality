package org.brutality.module.impl.visual;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Health extends Module {
    private final ModeSetting percentageSetting = new ModeSetting("Display Mode", this, "Hearts", Arrays.asList("Hearts", "Percentage"));
    private final BooleanSetting showHeartSymbolSetting = new BooleanSetting("Show Heart Symbol", this, true);
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 10.0, 0.0, 1080.0, 1.0);

    public Health() {
        super("Health", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.percentageSetting, this.showHeartSymbolSetting, this.xSetting, this.ySetting});
    }

    private void process() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof GuiChat) {
            int[] ints = HudDragHelper.computeIntArray();
            int n = ints[0];
            int n2 = ints[1];
            if (Mouse.isButtonDown(0)) {
                if (!HudDragHelper.matchesObject(this)) {
                    String str = "❤";
                    String str2 = String.format("%.1f", mc.thePlayer.getHealth() / 2.0F);
                    if (this.showHeartSymbolSetting.isEnabled()) {
                        str2 = str2 + " " + str;
                    }

                    int textWidth = computeFontRenderer(Health.class).getStringWidth(str2);
                    int FONT_HEIGHT = computeFontRenderer(Health.class).FONT_HEIGHT;
                    if (HudDragHelper.matchesInt(n, n2, this.xSetting.computeDouble(), this.ySetting.computeDouble(), textWidth, FONT_HEIGHT)) {
                        HudDragHelper.matchesObject(this, n, n2, (int)this.xSetting.computeDouble(), (int)this.ySetting.computeDouble());
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            if (HudDragHelper.matchesObject(this)) {
                int[] ints2 = HudDragHelper.computeIntArray(this);
                if (ints2 != null) {
                    this.xSetting.handleDouble2(ints2[0]);
                    this.ySetting.handleDouble2(ints2[1]);
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D) {
                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayerSP player = mc.thePlayer;
                if (player == null) {
                    return;
                }

                if (mc.currentScreen != null) {
                    this.process();
                }

                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                float f = health / maxHealth * 100.0F;
                int n = health / 2.0F >= 8.0F ? -11141291 : (health / 2.0F >= 4.0F ? -22016 : -43691);
                String str = "❤";
                String text = this.percentageSetting.getText();

                String str2 = switch (text) {
                    case "Hearts" -> String.format("%.1f", health / 2.0F);
                    case "Percentage" -> String.format("%.1f%%", f);
                    default -> "";
                };
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
                GL11.glPushMatrix();
                computeFontRenderer(Health.class).drawStringWithShadow(str2, (int)this.xSetting.computeDouble(), (int)this.ySetting.computeDouble(), n);
                if (this.showHeartSymbolSetting.isEnabled()) {
                    int textWidth = computeFontRenderer(Health.class).getStringWidth(str2);
                    computeFontRenderer(Health.class)
                        .drawStringWithShadow(str, (int)this.xSetting.computeDouble() + textWidth + 4, (int)this.ySetting.computeDouble(), -65536);
                }

                GL11.glPopMatrix();
            }
        }
    }
}
