package org.brutality.module.impl.pit.hypixel.swap;

import java.util.List;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.ActiveHelper;
import org.brutality.utils.player.FindIdentityHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public final class DivineSwap extends Module {
    private final NumberSetting equipBelowHPSetting = new NumberSetting("Equip Below HP", this, 6.0, 1.0, 40.0, 0.5);
    private final NumberSetting restoreAboveHPSetting = new NumberSetting("Restore Above HP", this, 12.0, 1.0, 40.0, 0.5);
    private final ActiveHelper activeHelper = new ActiveHelper();
    private static final long timestamp = 100L;

    @Override
    public void onEvent(Event event) {
        StartHotbarMovesHelper.onEvent3(event);
        if (event instanceof RenderEvent4) {
            this.activeHelper.clear();
        } else if (event instanceof EventUpdate event2 && event2.getState() == EventState.PRE && mc.thePlayer != null) {
            if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0F) {
                this.activeHelper.clear();
            } else if (FindIdentityHelper.checkState() && !this.activeHelper.checkState()) {
                float health = mc.thePlayer.getHealth();
                if (this.activeHelper.active()) {
                    if (health > Math.max(this.equipBelowHPSetting.computeDouble(), this.restoreAboveHPSetting.computeDouble())) {
                        this.activeHelper.process4();
                    }
                } else if (!(health > this.equipBelowHPSetting.computeDouble())
                    && !FindIdentityHelper.matchesItemStack(mc.thePlayer.getCurrentArmor(1), "divine miracle")) {
                    int n = FindIdentityHelper.computeInt(
                        stack -> FindIdentityHelper.matchesItemStack(stack) && FindIdentityHelper.matchesItemStack(stack, "divine miracle"), true
                    );
                    if (n >= 0) {
                        this.activeHelper.handleList(List.of(new StartHotbarMovesHelper.Entry3(2, n)), List.of(), false, true, timestamp);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.activeHelper.process4();
        super.onDisable();
    }

    public DivineSwap() {
        super("DivineSwap", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.equipBelowHPSetting, this.restoreAboveHPSetting});
    }
}
