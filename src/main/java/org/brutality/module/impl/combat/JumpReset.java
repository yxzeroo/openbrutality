package org.brutality.module.impl.combat;

import lombok.Generated;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.player.GetSpeedHelper;
import org.brutality.utils.player.IsKeyDownHelper;

public class JumpReset extends Module {
    public final NumberSetting chanceSetting = new NumberSetting("Chance", this, 80.0, 0.0, 100.0, 1.0);
    public final BooleanSetting requireMouseDownSetting = new BooleanSetting("Require mouse down", this, false);
    public final BooleanSetting requireMovingForwardSetting = new BooleanSetting("Require moving forward", this, true);
    public final BooleanSetting requireAimSetting = new BooleanSetting("Require aim", this, true);
    private boolean flag;
    private boolean flag2;
    private int count;
    private double amount;
    public static JumpReset instance;

    @Override
    public String getSuffix() {
        return this.chanceSetting.computeDouble() == 100.0 ? "" : (int)this.chanceSetting.computeDouble() + "%";
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (mc.thePlayer != null && mc.currentScreen == null) {
                    if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                        this.process3();
                    } else if (event instanceof EventPostMotion) {
                        this.process();
                    }
                }
            }
        }
    }

    private static float computeFloat(double d, double d2) {
        return (float)(Math.atan2(d, d2) * (float) (180.0 / Math.PI) * -1.0);
    }

    private boolean checkState2() {
        PotionEffect activePotionEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        PotionEffect activePotionEffect2 = mc.thePlayer.getActivePotionEffect(Potion.poison);
        PotionEffect activePotionEffect3 = mc.thePlayer.getActivePotionEffect(Potion.wither);
        return activePotionEffect != null || activePotionEffect2 != null || activePotionEffect3 != null;
    }

    public JumpReset() {
        super("JumpReset", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.chanceSetting, this.requireMouseDownSetting, this.requireMovingForwardSetting, this.requireAimSetting});
        instance = this;
    }

    private void process3() {
        int hurtTime = mc.thePlayer.hurtTime;
        boolean onGround = mc.thePlayer.onGround;
        if (onGround && this.amount > 3.0 && !mc.thePlayer.capabilities.allowFlying) {
            this.flag2 = true;
        }

        if (hurtTime > this.count) {
            boolean flag = mc.gameSettings.keyBindAttack.isKeyDown() || !this.requireMouseDownSetting.isEnabled();
            boolean flag2 = !this.requireAimSetting.isEnabled() || this.checkState();
            boolean flag3 = mc.gameSettings.keyBindForward.isKeyDown() || !this.requireMovingForwardSetting.isEnabled();
            boolean flag4 = this.chanceSetting.computeDouble() == 100.0 || GetCurrentScreenHelper.computeDouble(0.0, 100.0) < this.chanceSetting.computeDouble();
            boolean flag5 = GetCurrentScreenHelper.matchesFloat(GetSpeedHelper.computeFloat(), 330.0F, computeFloat(mc.thePlayer.motionX, mc.thePlayer.motionZ));
            if (!this.flag2 && !mc.thePlayer.isBurning() && onGround && flag2 && flag3 && flag && flag4 && !this.checkState2() && flag5) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), this.flag = true);
            }

            this.flag2 = false;
        }

        this.count = hurtTime;
        this.amount = mc.thePlayer.fallDistance;
    }

    private void process() {
        if (this.flag && !IsKeyDownHelper.matchesInt(mc.gameSettings.keyBindJump.getKeyCode())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), this.flag = false);
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.flag = false;
            this.flag2 = false;
            this.count = 0;
            this.amount = 0.0;
        }
    }

    private boolean checkState() {
        MovingObjectPosition objectMouseOver = mc.objectMouseOver;
        return objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectType.ENTITY && objectMouseOver.entityHit instanceof EntityPlayer;
    }

    @Generated
    public static JumpReset getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.flag && !IsKeyDownHelper.matchesInt(mc.gameSettings.keyBindJump.getKeyCode())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
        }

        this.flag = false;
    }
}
