package org.brutality.module.impl.pit.hypixel.swap;

import java.util.Arrays;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.StartHotbarMovesHelper;

public abstract class ModeModule extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    public final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    public final NumberSetting delaySetting = new NumberSetting("Delay", this, 50.0, 0.0, 500.0, 10.0);
    public final NumberSetting healthTriggerSetting;
    public final NumberSetting swapBackHealthSetting;
    public final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    public final BooleanSetting useInventorySetting = new BooleanSetting("Use Inventory", this, true);
    public final BooleanSetting ignoreAbsorptionSetting = new BooleanSetting("Ignore Absorption", this, true);
    public final BooleanSetting healthSwapBackSetting = new BooleanSetting("Health Swap Back", this, true);
    public final BooleanSetting swapBackOnProcSetting = new BooleanSetting("Swap Back On Proc", this, true);
    public final NumberSetting swapCooldownSSetting = new NumberSetting("Swap Cooldown (s)", this, 7.0, 2.5, 30.0, 0.5);
    public final NumberSetting healthRestoreCooldownSSetting = new NumberSetting("Health Restore Cooldown (s)", this, 10.0, 3.0, 30.0, 1.0);
    public final boolean flag;

    @Override
    public void onEvent(Event event) {
        StartHotbarMovesHelper.onEvent3(event);
        if (event instanceof ChatEvent) {
            FindHandler.handleString(((ChatEvent)event).message.getUnformattedText());
        }
    }

    public ModeModule(String str, boolean flag) {
        super(str, 0, Category.PIT);
        this.flag = flag;
        this.healthTriggerSetting = new NumberSetting("Health Trigger", this, flag ? 5.0 : 4.5, 0.5, 16.0, 0.5);
        this.swapBackHealthSetting = new NumberSetting("Swap Back Health", this, flag ? 7.5 : 7.0, 1.0, 20.0, 0.5);
        this.handleSettingArray(
            new Setting[]{
                this.silentSetting,
                this.delaySetting,
                this.healthTriggerSetting,
                this.swapBackHealthSetting,
                this.safeModeSetting,
                this.useInventorySetting,
                this.ignoreAbsorptionSetting,
                this.healthSwapBackSetting,
                this.swapBackOnProcSetting,
                this.swapCooldownSSetting,
                this.healthRestoreCooldownSSetting
            }
        );
    }

    @Override
    public void onDisable() {
        FindHandler.handleModeModule(this);
    }

    public String computeString() {
        return FindHandler.computeString(this.flag);
    }
}
