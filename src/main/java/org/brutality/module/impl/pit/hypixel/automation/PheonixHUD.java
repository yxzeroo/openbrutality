package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.swap.PheonixSwap;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class PheonixHUD extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 90.0, 0.0, 1080.0, 1.0);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    @Override
    public void onRender() {
        PheonixSwap instance = PheonixSwap.getInstance();
        if (instance != null && this.mc.thePlayer != null) {
            if (!instance.checkState()) {
                HudDragHelper.handleObject(this);
            } else {
                String str = instance.computeString();
                String str2 = str == null ? "" : str.toLowerCase();
                String str3;
                if (str2.contains("ready")) {
                    str3 = "§a" + str;
                } else if (str2.contains("active")) {
                    str3 = "§e" + str;
                } else if (str2.contains("used")) {
                    str3 = "§7" + str;
                } else if (str2.contains("not")) {
                    str3 = "§c" + str;
                } else {
                    str3 = str;
                }

                String str4 = "§fPhoenix: " + str3;
                float f = (float)this.xPosSetting.computeDouble();
                float f2 = (float)this.yPosSetting.computeDouble();
                if (!(this.mc.currentScreen instanceof GuiChat)) {
                    HudDragHelper.handleObject(this);
                }

                if (this.mc.currentScreen instanceof GuiChat && Mouse.isButtonDown(0)) {
                    int[] ints = HudDragHelper.computeIntArray();
                    if (!HudDragHelper.matchesObject(this)
                        && HudDragHelper.matchesInt(
                            ints[0],
                            ints[1],
                            f,
                            f2,
                            computeFontRenderer(PheonixHUD.class).getStringWidth(str4),
                            computeFontRenderer(PheonixHUD.class).FONT_HEIGHT
                        )) {
                        HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f, (int)f2);
                    }
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

                computeFontRenderer(PheonixHUD.class).drawStringWithShadow(str4, f, f2, -1);
            }
        }
    }

    public PheonixHUD() {
        super("PheonixHUD", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting});
    }
}
