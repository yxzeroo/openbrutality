package org.brutality.module.impl.client;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.client.gui.GuiScreen;
import org.brutality.Brutality;
import org.brutality.clickgui.classic.ConfirmScreen;
import org.brutality.clickgui.classic.DrawSettingLabelScreen;
// VENDOR-DISABLED: import org.brutality.clickgui.newclick.GuiCloudBrowser;
import org.brutality.clickgui.newclick.HudEditorGui;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.BrutalityHelper;

public class ClickGUI extends Module {
    public BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, true);
    public ModeSetting discordSetting = new ModeSetting("ClickGUI Mode", this, "Modern", Arrays.asList("Old", "Modern", "Discord"));
    public NumberSetting fontSizeSetting = new NumberSetting("Font Size", this, 20.0, 10.0, 30.0, 1.0);
    public NumberSetting scrollSpeedSetting = new NumberSetting("Scroll Speed", this, 17.0, 5.0, 60.0, 1.0);
    public ModeSetting universalThemeSetting = new ModeSetting("Universal Theme", this, BrutalityHelper.getInstance().getName(), BrutalityHelper::computeList);
    private BrutalityHelper brutalityHelper;
    public static ClickGUI instance;

    public ClickGUI() {
        super("ClickGUI", 54, Category.CLIENT);
        this.universalThemeSetting.setVisible(false);
        this.handleSettingArray(
            new Setting[]{this.discordSetting, this.customFontSetting, this.fontSizeSetting, this.scrollSpeedSetting, this.universalThemeSetting}
        );
        instance = this;
        this.process();
    }

    public void process() {
        String text = this.universalThemeSetting.getText();
        if (text != null && !text.isEmpty()) {
            this.brutalityHelper = BrutalityHelper.computeBrutalityHelper(text);
        } else {
            this.brutalityHelper = BrutalityHelper.getInstance();
        }
    }

    private boolean matchesGuiScreen(GuiScreen screen) {
        return screen != null
            && (
                screen == Brutality.newClickGUI
                    || screen == Brutality.discordGUI
                    // VENDOR-DISABLED: || screen instanceof GuiCloudBrowser
                    || screen instanceof HudEditorGui
                    || screen instanceof DrawSettingLabelScreen
                    || screen instanceof ConfirmScreen
            );
    }

    public void handleBrutalityHelper(BrutalityHelper brutalityHelper) {
        if (brutalityHelper != null) {
            this.brutalityHelper = brutalityHelper;
            this.universalThemeSetting.handleString(brutalityHelper.getName());
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D && (mc.currentScreen == null || !this.matchesGuiScreen(mc.currentScreen))) {
                this.process28();
            }
        }
    }

    public void onGuiClosed() {
        if (this.checkState44()) {
            this.process28();
        }
    }

    public BrutalityHelper computeBrutalityHelper() {
        this.process();
        return this.brutalityHelper;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.discordSetting.matchesString2("Discord")) {
            mc.displayGuiScreen(Brutality.discordGUI);
        } else if (this.discordSetting.matchesString2("Modern")) {
            mc.displayGuiScreen(new DrawSettingLabelScreen());
        } else {
            mc.displayGuiScreen(Brutality.newClickGUI);
        }
    }

    @Generated
    public static ClickGUI getInstance() {
        return instance;
    }

    public String computeString() {
        return this.customFontSetting.isEnabled() ? "Brutality" : null;
    }
}
