package org.brutality.module.impl.combat;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.GetYawEvent2;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public final class Criticals extends Module {
    private final ModeSetting jumpSetting = new ModeSetting("Mode", this, "Packet", Arrays.asList("Packet", "NCP", "Verus", "Jump"));
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 20.0, 0.0, 20.0, 1.0);
    private final NumberSetting jumpHeightSetting = new NumberSetting("Jump Height", this, 0.42, 0.1, 0.42, 0.01);
    private final NumberSetting jumpRangeSetting = new NumberSetting("Jump Range", this, 4.0, 1.0, 6.0, 0.1);
    private final BooleanSetting onlyWithTargetsSetting = new BooleanSetting("Only With Targets", this, true);
    public static Criticals instance;
    private int count = 0;
    private boolean flag = false;
    private static final long timestamp = 3734242453465595914L;

    private void process3() {
        if (mc.objectMouseOver.entityHit != null && mc.thePlayer.onGround && mc.objectMouseOver.entityHit.hurtResistantTime > this.delaySetting.computeDouble()
            )
         {
            for (double d : new double[]{0.006253453, 0.002253453, 0.001253453}) {
                mc.thePlayer
                    .sendQueue
                    .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.count = 0;
        this.flag = false;
    }

    public Criticals() {
        super("Criticals", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.jumpSetting, this.delaySetting, this.jumpHeightSetting, this.jumpRangeSetting, this.onlyWithTargetsSetting});
        instance = this;
        this.jumpHeightSetting.setConditionalVisibility(() -> this.jumpSetting.getText().equals("Jump"));
        this.jumpRangeSetting.setConditionalVisibility(() -> this.jumpSetting.getText().equals("Jump"));
        this.onlyWithTargetsSetting.setConditionalVisibility(() -> this.jumpSetting.getText().equals("Jump"));
    }

    private void onGetYawEvent2(GetYawEvent2 getYawEvent2) {
        if (this.flag) {
            getYawEvent2.setFactor((float)this.jumpHeightSetting.computeDouble());
            getYawEvent2.setMotionY(this.jumpHeightSetting.computeDouble());
            this.flag = false;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion event2) {
                    String text = this.jumpSetting.getText();
                    switch (text) {
                        case "Packet":
                            this.process3();
                            break;
                        case "NCP":
                            this.onEventPreMotion(event2);
                            break;
                        case "Verus":
                            this.onEventPreMotion2(event2);
                    }
                } else if (event instanceof EventUpdate && this.jumpSetting.getText().equals("Jump")) {
                    this.process();
                } else if (event instanceof GetYawEvent2 && this.jumpSetting.getText().equals("Jump")) {
                    this.onGetYawEvent2((GetYawEvent2)event);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.count = 0;
            this.flag = false;
        }
    }

    private void onEventPreMotion2(EventPreMotion event) {
        if (KillAura.entityLivingBase2 != null && event.isGround()) {
            switch (KillAura.entityLivingBase2.hurtResistantTime) {
                case 17:
                case 19:
                    event.setGround(false);
                    event.setY(event.getY() + ThreadLocalRandom.current().nextDouble(0.001, 0.0011));
                    break;
                case 18:
                case 20:
                    event.setGround(false);
                    event.setY(event.getY() + 0.03 + ThreadLocalRandom.current().nextDouble(0.001, 0.0011));
            }
        }
    }

    private void process() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (!this.onlyWithTargetsSetting.isEnabled() || this.checkState()) {
                if (mc.thePlayer.onGround && this.checkState()) {
                    this.flag = true;
                    mc.thePlayer.jump();
                }
            }
        }
    }

    private boolean checkState() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            double d = this.jumpRangeSetting.computeDouble();

            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase && entity != mc.thePlayer && !entity.isDead) {
                    EntityLivingBase living = (EntityLivingBase)entity;
                    double dist = mc.thePlayer.getDistanceToEntity(living);
                    if (!(dist > d) && this.matchesEntityLivingBase(living)) {
                        return true;
                    }
                }
            }

            return false;
        }
        return false;
    }

    private boolean matchesEntityLivingBase(EntityLivingBase living) {
        if (!(living instanceof EntityPlayer player)) {
            return false;
        }
        return player.isInvisible() ? false : !player.isDead && player != mc.thePlayer;
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (mc.thePlayer.hurtTime > 0) {
            this.count = (int)timestamp;
        }

        if (this.count > 0 && mc.thePlayer.onGround && mc.thePlayer.posY % 1.0 == 0.0) {
            mc.thePlayer.motionY = 0.027;
        }

        this.count--;
    }

    @Generated
    public static Criticals getInstance() {
        return instance;
    }
}
