package org.brutality.module.impl.player;

import lombok.Generated;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.DefaultHelper;

public class Friends extends Module {
    private final BooleanSetting removeOnHitSetting = new BooleanSetting("Remove On Hit", this, false);
    private static Friends instance;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Generated
    public static Friends getInstance() {
        return instance;
    }

    public Friends() {
        super("Friends", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.removeOnHitSetting});
        instance = this;
        this.handleFlag3(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.removeOnHitSetting.isEnabled()) {
                if (event instanceof GetDamageEvent getDamageEvent && getDamageEvent.getEntity2() == mc.thePlayer && getDamageEvent.getEntity() instanceof EntityPlayer) {
                    EntityPlayer entity = (EntityPlayer)getDamageEvent.getEntity();
                    if (DefaultHelper.matchesString(entity.getName())) {
                        DefaultHelper.handleString(entity.getName());
                    }
                }
            }
        }
    }
}
