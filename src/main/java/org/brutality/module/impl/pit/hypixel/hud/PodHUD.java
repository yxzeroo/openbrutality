package org.brutality.module.impl.pit.hypixel.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.swap.PodSwap;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class PodHUD extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 70.0, 0.0, 1080.0, 1.0);

    private static String computeString(String str) {
        if (str == null) {
            return "";
        }
        String str2 = str.toLowerCase();
        if (str2.contains("ready")) {
            return "§a" + str;
        }
        if (str2.contains("active")) {
            return "§e" + str;
        }
        if (str2.contains("used")) {
            return "§7" + str;
        }
        return str2.contains("not") ? "§c" + str : str;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    public PodHUD() {
        super("PodHUD", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting});
    }

    private void handleString(String str) {
        float f = (float)this.xPosSetting.computeDouble();
        float f2 = (float)this.yPosSetting.computeDouble();
        if (this.mc.currentScreen instanceof GuiChat && Mouse.isButtonDown(0)) {
            int[] ints = HudDragHelper.computeIntArray();
            if (!HudDragHelper.matchesObject(this)
                && HudDragHelper.matchesInt(
                    ints[0], ints[1], f, f2, computeFontRenderer(PodHUD.class).getStringWidth(str), computeFontRenderer(PodHUD.class).FONT_HEIGHT
                )) {
                HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f, (int)f2);
            }
        } else if (!(this.mc.currentScreen instanceof GuiChat)) {
            HudDragHelper.handleObject(this);
        }

        if (HudDragHelper.matchesObject(this)) {
            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xPosSetting.handleDouble2(ints2[0]);
                this.yPosSetting.handleDouble2(ints2[1]);
                f = ints2[0];
                f2 = ints2[1];
            }
        }

        computeFontRenderer(PodHUD.class).drawStringWithShadow(str, f, f2, -1);
    }

    @Override
    public void onRender() {
        PodSwap instance = PodSwap.getInstance();
        if (instance != null && this.mc.thePlayer != null) {
            if (!instance.checkState()) {
                HudDragHelper.handleObject(this);
            } else {
                String str = instance.computeString();
                String str2 = "§fEscape Pod: " + computeString(str);
                this.handleString(str2);
            }
        }
    }
}
