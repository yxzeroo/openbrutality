package org.brutality.module.impl.movement;

import java.util.Arrays;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.lwjgl.input.Keyboard;

public class WTap extends Module {
    private boolean flag;
    private boolean flag2;
    private final ModeSetting legitSetting = new ModeSetting("Mode", this, "Legit", Arrays.asList("Legit"));
    private KillAura killAura;
    private EntityLivingBase entityLivingBase;
    private int count;

    public EntityLivingBase computeEntityLivingBase() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            this.entityLivingBase = (EntityLivingBase)mc.objectMouseOver.entityHit;
            return (EntityLivingBase)mc.objectMouseOver.entityHit;
        }
        if (this.entityLivingBase != null) {
            if (++this.count <= 10) {
                return this.entityLivingBase;
            }

            this.entityLivingBase = null;
        }

        return null;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.flag = false;
            this.flag2 = false;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventTick && this.legitSetting.matchesString2("Legit")) {
                    if (this.flag2) {
                        mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
                        this.flag2 = false;
                        return;
                    }

                    EntityLivingBase living = this.computeEntityLivingBase();
                    if (living != null) {
                        if (living.hurtTime < 2 || !mc.thePlayer.onGround || !mc.thePlayer.isSprinting()) {
                            this.flag = false;
                        } else if (!this.flag) {
                            mc.gameSettings.keyBindSprint.pressed = false;
                            mc.gameSettings.keyBindForward.pressed = false;
                            this.flag2 = true;
                            this.flag = true;
                        }
                    } else {
                        this.flag = false;
                    }
                }
            }
        }
    }

    public WTap() {
        super("WTap", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.legitSetting});
    }
}
