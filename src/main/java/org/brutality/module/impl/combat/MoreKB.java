package org.brutality.module.impl.combat;

import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;

public class MoreKB extends Module {
    private ModeSetting doublePacketSetting = new ModeSetting(
        "Mode", this, "Legit", Arrays.asList("Legit", "Legit Fast", "Less Packet", "Packet", "Double Packet")
    );
    private BooleanSetting intelligentSetting = new BooleanSetting("Intelligent", this, false);
    private BooleanSetting onlyGroundSetting = new BooleanSetting("Only Ground", this, true);
    private BooleanSetting onlyMovingSetting = new BooleanSetting("Only Moving", this, false);
    private BooleanSetting auraOnlySetting = new BooleanSetting("Aura Only", this, false);
    private EntityLivingBase entityLivingBase;
    private boolean flag = false;

    public String computeString() {
        return this.doublePacketSetting.getText();
    }

    private boolean matchesEntityLivingBase(EntityLivingBase living) {
        double d = mc.thePlayer.posX - living.posX;
        double d2 = mc.thePlayer.posZ - living.posZ;
        float f = (float)(Math.atan2(d2, d) * 180.0 / Math.PI - 90.0);
        float f2 = Math.abs(MathHelper.wrapAngleTo180_float(f - living.rotationYawHead));
        return f2 <= 120.0F;
    }

    private void process() {
        String text = this.doublePacketSetting.getText();
        switch (text) {
            case "Legit":
                this.flag = true;
                if (mc.thePlayer.isSprinting()) {
                    mc.thePlayer.setSprinting(false);
                    mc.thePlayer.setSprinting(true);
                }

                this.flag = false;
            case "Legit Fast":
            default:
                break;
            case "Less Packet":
                if (mc.thePlayer.isSprinting()) {
                    mc.thePlayer.setSprinting(false);
                }

                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
                mc.thePlayer.setSprinting(true);
                break;
            case "Packet":
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
                mc.thePlayer.setSprinting(true);
                break;
            case "Double Packet":
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
                mc.thePlayer.setSprinting(true);
        }
    }

    private boolean checkState() {
        if (mc.thePlayer == null) {
            return false;
        }
        if (this.onlyGroundSetting.isEnabled() && !mc.thePlayer.onGround) {
            return false;
        }
        return this.onlyMovingSetting.isEnabled() && !GetSpeedHelper.isMoving() ? false : mc.thePlayer.isSprinting();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.entityLivingBase = null;
        this.flag = false;
    }

    private void process3() {
        EntityLivingBase living = this.computeEntityLivingBase();
        if (living != null) {
            if (this.checkState()) {
                if (!this.intelligentSetting.isEnabled() || this.matchesEntityLivingBase(living)) {
                    if (living.hurtTime == 10) {
                        this.process();
                    }
                }
            }
        }
    }

    private EntityLivingBase computeEntityLivingBase() {
        if (this.auraOnlySetting.isEnabled()) {
            return KillAura.entityLivingBase2 instanceof EntityLivingBase ? KillAura.entityLivingBase2 : null;
        }
        return mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
                && mc.objectMouseOver.entityHit instanceof EntityLivingBase
            ? (EntityLivingBase)mc.objectMouseOver.entityHit
            : null;
    }

    public MoreKB() {
        super("MoreKB", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{this.doublePacketSetting, this.intelligentSetting, this.onlyGroundSetting, this.onlyMovingSetting, this.auraOnlySetting}
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetDamageEvent) {
                    this.onGetDamageEvent((GetDamageEvent)event);
                } else if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    this.process3();
                }
            }
        }
    }

    private void onGetDamageEvent(GetDamageEvent getDamageEvent) {
        Entity entity2 = getDamageEvent.getEntity2();
        if (entity2 instanceof EntityLivingBase) {
            this.entityLivingBase = (EntityLivingBase)entity2;
            if (this.doublePacketSetting.matchesString2("Legit Fast")) {
                if (this.checkState()) {
                    mc.thePlayer.sprintingTicksLeft = 0;
                }

                this.entityLivingBase = null;
            }
        }
    }
}
