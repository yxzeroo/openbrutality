package org.brutality.module.impl.player;

import net.minecraft.item.ItemBlock;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

public class FastPlace extends Module {
    private final NumberSetting ticksSetting = new NumberSetting("Ticks", this, 0.0, 0.0, 4.0, 1.0);
    private final BooleanSetting blocksOnlySetting = new BooleanSetting("Blocks Only", this, true);

    public FastPlace() {
        super("Fast Place", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.ticksSetting, this.blocksOnlySetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && this.checkState44()) {
                if (this.blocksOnlySetting.isEnabled()) {
                    if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
                        mc.rightClickDelayTimer = (int)this.ticksSetting.computeDouble();
                    }
                } else {
                    mc.rightClickDelayTimer = (int)this.ticksSetting.computeDouble();
                }
            }
        }
    }
}
