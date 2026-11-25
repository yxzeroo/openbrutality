package org.brutality.module.impl.movement;

import net.minecraft.item.ItemBlock;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Safewalk extends Module {
    public static boolean flag;
    private final BooleanSetting rightClickOnlySetting = new BooleanSetting("Right Click Only", this, false);
    private final BooleanSetting pitchCheckSetting = new BooleanSetting("Pitch Check", this, false);
    private final BooleanSetting blocksOnlySetting = new BooleanSetting("Blocks Only", this, false);
    private final BooleanSetting directionCheckSetting = new BooleanSetting("Direction Check", this, false);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion) {
                    flag = this.checkState();
                }
            }
        }
    }

    public Safewalk() {
        super("Safewalk", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.rightClickOnlySetting, this.pitchCheckSetting, this.blocksOnlySetting, this.directionCheckSetting});
    }

    private boolean checkState() {
        if (this.rightClickOnlySetting.isEnabled() && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
            return false;
        }
        if (this.blocksOnlySetting.isEnabled()) {
            if (mc.thePlayer.getHeldItem() == null) {
                return false;
            }

            if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
                return false;
            }
        }

        return this.pitchCheckSetting.isEnabled() && mc.thePlayer.rotationPitch <= 65.0F
            ? false
            : !this.directionCheckSetting.isEnabled() || !(mc.thePlayer.movementInput.moveForward > 0.0F);
    }

    @Override
    public void onDisable() {
        flag = false;
    }
}
