package org.brutality.module.impl.player;

import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;

public class DelayRemovers extends Module {
    public BooleanSetting noJumpDelaySetting = new BooleanSetting("NoJumpDelay", this, true);
    public BooleanSetting noLeftClickDelaySetting = new BooleanSetting("NoLeftClickDelay", this, true);

    public DelayRemovers() {
        super("Delay Removers", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.noJumpDelaySetting, this.noLeftClickDelaySetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && this.checkState44()) {
                if (this.noJumpDelaySetting.isEnabled()) {
                    mc.thePlayer.jumpTicks = 0;
                }

                if (this.noLeftClickDelaySetting.isEnabled()) {
                    mc.leftClickCounter = 0;
                }
            }
        }
    }
}
