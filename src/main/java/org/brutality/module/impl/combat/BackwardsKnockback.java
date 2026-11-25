package org.brutality.module.impl.combat;

import org.brutality.event.Event;
import org.brutality.event.impl.velocity.GetEntityIdEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;

public class BackwardsKnockback extends Module {
    private final NumberSetting multiplierSetting = new NumberSetting("Multiplier", this, 1.25, 1.0, 2.0, 0.05);

    public BackwardsKnockback() {
        super("Backwards Knockback", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.multiplierSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof GetEntityIdEvent && mc.thePlayer != null) {
            if (mc.gameSettings.keyBindBack.isKeyDown() || !(mc.thePlayer.movementInput.moveForward >= 0.0F)) {
                GetEntityIdEvent getEntityIdEvent = (GetEntityIdEvent)event;
                double d = this.multiplierSetting.computeDouble();
                getEntityIdEvent.setX(getEntityIdEvent.getX() * d);
                getEntityIdEvent.setZ(getEntityIdEvent.getZ() * d);
            }
        }
    }
}
