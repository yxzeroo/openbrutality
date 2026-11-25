package org.brutality.module.impl.visual;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;

public class Notifications extends Module {
    private static Notifications instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    public ModeSetting brutalityOldSetting = new ModeSetting("Style", this, "Brutality", Arrays.asList("Brutality", "Legacy", "Brutality Old"));
    private final NumberSetting xSetting = new NumberSetting("X", this, 2.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 11.0, 0.0, 1080.0, 1.0);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, true);
    public final BooleanSetting spawnArmorCheckSetting = new BooleanSetting("Spawn Armor Check", this, true);
    public final BooleanSetting equippedSetting = new BooleanSetting("Equipped", this, true).computeBooleanSetting(() -> this.checkState9());
    public final BooleanSetting unequippedSetting = new BooleanSetting("Unequipped", this, true).computeBooleanSetting(() -> this.checkState9());
    public final BooleanSetting jumpedDownSetting = new BooleanSetting("Jumped Down", this, true).computeBooleanSetting(() -> this.checkState9());
    public final BooleanSetting joiningSetting = new BooleanSetting("Joining", this, true).computeBooleanSetting(() -> this.checkState9());
    public final BooleanSetting leavingSetting = new BooleanSetting("Leaving", this, true).computeBooleanSetting(() -> this.checkState9());
    private final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this)
        .computeModeSetting(() -> this.customFontSetting.isEnabled() && !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private CustomFontRenderer drawStringWithShadowComponent;
    private String text = "";

    public boolean checkState() {
        return this.matchesBooleanSetting(this.joiningSetting);
    }

    private void process3() {
        if (!(this.mc.currentScreen instanceof GuiChat)) {
            HudDragHelper.handleObject(this);
        } else {
            int[] ints = HudDragHelper.computeIntArray();
            int n = Math.max(110, this.computeFontRenderer().getStringWidth("Notification preview"));
            if (Mouse.isButtonDown(0)
                && !HudDragHelper.matchesObject(this)
                && HudDragHelper.matchesInt(ints[0], ints[1], this.xSetting.computeDouble(), this.ySetting.computeDouble(), n, 14)) {
                HudDragHelper.matchesObject(this, ints[0], ints[1], (int)this.xSetting.computeDouble(), (int)this.ySetting.computeDouble());
            } else if (!Mouse.isButtonDown(0)) {
                HudDragHelper.handleObject(this);
            }

            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xSetting.handleDouble2(ints2[0]);
                this.ySetting.handleDouble2(ints2[1]);
            }
        }
    }

    private boolean matchesBooleanSetting(BooleanSetting boolSetting) {
        return this.brutalityOldSetting == null || !this.checkState9() || boolSetting.isEnabled();
    }

    @Override
    public void process2() {
        NotificationFeedHandler.process4();
    }

    private FontRenderer computeFontRenderer() {
        if (!this.customFontSetting.isEnabled() && !ModuleSpecificHelper.matchesModeSetting(this.modeSetting)) {
            return this.mc.fontRendererObj;
        }
        String str;
        if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting)) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            str = brutalityHelper != null ? brutalityHelper.getText() : "Brutality";
        } else {
            str = this.fontSetting.getText();
        }

        if (this.drawStringWithShadowComponent == null || !str.equals(this.text)) {
            this.text = str;
            this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(str, 18.0F);
        }

        return (FontRenderer)(this.drawStringWithShadowComponent != null ? this.drawStringWithShadowComponent : this.mc.fontRendererObj);
    }

    public boolean checkState2() {
        return this.brutalityOldSetting.getText().equals("Brutality");
    }

    public String computeString() {
        return this.brutalityOldSetting.getText();
    }

    public boolean checkState4() {
        return this.brutalityOldSetting.getText().equals("Legacy");
    }

    public boolean checkState5() {
        return this.matchesBooleanSetting(this.equippedSetting);
    }

    @Generated
    public static Notifications getInstance() {
        return instance;
    }

    public boolean checkState6() {
        return this.matchesBooleanSetting(this.leavingSetting);
    }

    public Notifications() {
        super("Notifications", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.brutalityOldSetting,
                this.xSetting,
                this.ySetting,
                this.modeSetting,
                this.customFontSetting,
                this.fontSetting,
                this.spawnArmorCheckSetting,
                this.equippedSetting,
                this.unequippedSetting,
                this.jumpedDownSetting,
                this.joiningSetting,
                this.leavingSetting
            }
        );
        instance = this;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.process2();
            } else if (event instanceof EventUpdate) {
                if (((EventUpdate)event).getState() == EventState.PRE) {
                    NotificationFeedHandler.process2();
                }
            }
        }
    }

    public boolean checkState8() {
        return this.matchesBooleanSetting(this.unequippedSetting);
    }

    public boolean checkState9() {
        return this.brutalityOldSetting.getText().equals("Brutality Old");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        NotificationFeedHandler.process5();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        NotificationFeedHandler.process5();
        NotificationFeedHandler.process6();
    }

    public boolean checkState11() {
        return this.matchesBooleanSetting(this.jumpedDownSetting);
    }

    public void process() {
        if (this.checkState44()) {
            this.process3();

            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                NotificationFeedHandler.handleMinecraft(this.mc, this.computeFontRenderer(), (float)this.xSetting.computeDouble(), (float)this.ySetting.computeDouble());
            }
        }
    }
}
