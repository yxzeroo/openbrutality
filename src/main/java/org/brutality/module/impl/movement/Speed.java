package org.brutality.module.impl.movement;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;
import org.brutality.utils.player.IsKeyDownHelper;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {
    private final ModeSetting vulcan2Setting = new ModeSetting(
        "Mode", this, "Watchdog", Arrays.asList("Watchdog", "Watchdog Sim", "Strafe", "Matrix", "HurtTime", "Vanilla", "BHop", "Verus", "Vulcan", "Vulcan2")
    );
    private final OnMouseHandler onMouseHandler;
    private boolean flag;
    private final BooleanSetting maximizeStrafeSetting = new BooleanSetting("Maximize Strafe", this, true);
    private final BooleanSetting stopMotionSetting = new BooleanSetting("Stop Motion", this, false);
    private final BooleanSetting antiRGMSetting = new BooleanSetting("Anti RGM", this, false);
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 2.0, 1.0, 5.0, 0.1);
    private final NumberSetting timerSetting = new NumberSetting("Timer", this, 1.0, 1.0, 5.0, 0.1);
    private final TimerUtil timerUtil = new TimerUtil();
    private final float factor = ThreadLocalRandom.current().nextFloat();
    private double amount;
    private double amount2;
    private double moveSpeed;
    private float factor2;
    private int count;
    private int count2;
    private boolean flag2;
    private boolean wasOnGround;
    private boolean flag3 = true;
    private int count3 = 0;
    private Entity entity = null;
    private boolean flag4 = false;
    public static Speed instance;

    public boolean checkState() {
        return this.checkState44() && this.checkState2();
    }

    public Speed() {
        super("Speed", 0, Category.MOVEMENT);
        this.handleSettingArray(
            new Setting[]{this.vulcan2Setting, this.maximizeStrafeSetting, this.stopMotionSetting, this.antiRGMSetting, this.speedSetting, this.timerSetting}
        );
        this.maximizeStrafeSetting.computeBooleanSetting(() -> !this.checkState2());
        this.stopMotionSetting.computeBooleanSetting(() -> !this.checkState2());
        this.antiRGMSetting.computeBooleanSetting(() -> !this.checkState2());
        this.speedSetting.computeNumberSetting(() -> !this.checkState2());
        this.timerSetting.computeNumberSetting(() -> !this.checkState2());
        this.onMouseHandler = new OnMouseHandler(this);
        instance = this;
    }

    @Override
    public void onDisable() {
        this.onMouseHandler.onDisable();
        this.flag = false;
        super.onDisable();
        if (mc.timer != null) {
            mc.timer.timerSpeed = 1.0F;
        }

        if (!this.checkState2() && mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
        }

        this.count3 = 0;
        this.entity = null;
        this.flag4 = false;
        if (!this.checkState2() && this.stopMotionSetting.isEnabled() && mc.thePlayer != null) {
            this.count2 = 0;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    public boolean checkState2() {
        return this.vulcan2Setting.matchesString2("Watchdog Sim");
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.onMouseHandler.onDisable();
            this.flag = false;
            this.amount = 1.5;
            this.timerUtil.reset();
            if (mc.thePlayer != null) {
                this.wasOnGround = mc.thePlayer.onGround;
            }

            this.count2 = 0;
            this.moveSpeed = 0.0;
            this.count = 0;
            this.count3 = 0;
            this.entity = null;
            this.flag4 = false;
        }
    }

    @Generated
    public static Speed getInstance() {
        return instance;
    }

    @Override
    public void process2() {
        this.onMouseHandler.process();
        this.flag = false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (this.checkState2()) {
                    if (!this.flag) {
                        if (mc.timer != null) {
                            mc.timer.timerSpeed = 1.0F;
                        }

                        if (mc.gameSettings != null && Keyboard.isCreated()) {
                            int keyCode = mc.gameSettings.keyBindJump.getKeyCode();
                            KeyBinding.setKeyBindState(keyCode, IsKeyDownHelper.matchesInt(keyCode));
                        }

                        this.flag = true;
                    }

                    this.onMouseHandler.onEvent(event);
                } else {
                    if (this.flag) {
                        this.onMouseHandler.onDisable();
                        this.flag = false;
                    }

                    if (event instanceof GetDamageEvent && this.antiRGMSetting.isEnabled()) {
                        GetDamageEvent getDamageEvent = (GetDamageEvent)event;
                        if (getDamageEvent.getEntity() == mc.thePlayer) {
                            if (!mc.thePlayer.onGround && getDamageEvent.getEntity2() == this.entity) {
                                this.count3++;
                            } else if (mc.thePlayer.onGround) {
                                this.count3 = 0;
                                this.flag4 = false;
                            } else if (getDamageEvent.getEntity2() != this.entity) {
                                this.count3 = 1;
                            }

                            this.entity = getDamageEvent.getEntity2();
                            if (this.count3 >= 3) {
                                this.flag4 = true;
                                this.count3 = 0;
                            }
                        }
                    }

                    if (event instanceof EventPreMotion event2) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                        if (this.flag3) {
                            mc.timer.timerSpeed = (float)this.timerSetting.computeDouble();
                        }

                        double d = event2.getX() - mc.thePlayer.prevPosX;
                        double d2 = event2.getZ() - mc.thePlayer.prevPosZ;
                        this.amount2 = Math.hypot(d, d2);
                        if (mc.thePlayer.onGround) {
                            this.count2 = 0;
                            if (this.flag4) {
                                this.flag4 = false;
                            }
                        } else {
                            this.count2++;
                        }

                        if (this.antiRGMSetting.isEnabled() && this.flag4 && !mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = -0.5;
                            return;
                        }

                        String text = this.vulcan2Setting.getText();
                        switch (text) {
                            case "Vulcan":
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.jump();
                                    if (GetSpeedHelper.isMoving()) {
                                        GetSpeedHelper.handleFloat2(0.48F);
                                    }
                                } else {
                                    if (GetSpeedHelper.isMoving() && GetSpeedHelper.getSpeed() < 0.22) {
                                        GetSpeedHelper.handleFloat2(0.22F);
                                    }

                                    if (mc.thePlayer.offGroundTicks > 3 && mc.thePlayer.offGroundTicks < 12) {
                                        mc.thePlayer.motionY = -0.3;
                                    }
                                }
                                break;
                            case "Vulcan2":
                                if (mc.thePlayer.onGround) {
                                    mc.thePlayer.jump();
                                    if (GetSpeedHelper.isMoving() && GetSpeedHelper.getSpeed() < 0.3) {
                                        GetSpeedHelper.handleFloat2(0.3F);
                                    }
                                } else if (GetSpeedHelper.isMoving() && GetSpeedHelper.getSpeed() < 0.23F) {
                                    GetSpeedHelper.handleFloat2((float)(0.22F + Math.random() * 1.0E-5));
                                }
                                break;
                            case "Matrix":
                                if (GetSpeedHelper.isMoving()) {
                                    if (mc.thePlayer.onGround && mc.thePlayer.motionY < 0.003 && !this.flag4) {
                                        mc.thePlayer.jump();
                                        mc.timer.timerSpeed = 1.0F;
                                    } else if (mc.thePlayer.motionY > 0.003) {
                                        double d3 = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
                                        if (d3 < 0.5) {
                                            mc.thePlayer.motionX = mc.thePlayer.motionX * (this.amount * this.speedSetting.computeDouble());
                                            mc.thePlayer.motionZ = mc.thePlayer.motionZ * (this.amount * this.speedSetting.computeDouble());
                                        }

                                        mc.timer.timerSpeed = 1.05F;
                                    }

                                    this.amount = 1.0012F;
                                }
                                break;
                            case "HurtTime":
                                if (GetSpeedHelper.isMoving()) {
                                    double d4;
                                    if (mc.thePlayer.hurtTime <= 0) {
                                        d4 = 1.001F * this.speedSetting.computeDouble();
                                    } else {
                                        d4 = 1.0294F * this.speedSetting.computeDouble();
                                    }

                                    double d5 = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
                                    if (d5 < 0.6) {
                                        mc.thePlayer.motionX *= d4;
                                        mc.thePlayer.motionZ *= d4;
                                    }

                                    if (mc.thePlayer.onGround && mc.thePlayer.motionY < 0.003 && !this.flag4) {
                                        mc.thePlayer.jump();
                                    }
                                }
                                break;
                            case "Vanilla":
                                if (GetSpeedHelper.isMoving()) {
                                    GetSpeedHelper.handleFloat2((float)(this.speedSetting.computeDouble() / 4.0));
                                }
                                break;
                            case "BHop":
                                if (GetSpeedHelper.isMoving()) {
                                    GetSpeedHelper.handleFloat2((float)(this.speedSetting.computeDouble() / 4.0));
                                    if (mc.thePlayer.onGround && !this.flag4) {
                                        mc.thePlayer.jump();
                                    }
                                }
                                break;
                            case "Verus":
                                if (GetSpeedHelper.isMoving()) {
                                    if (mc.thePlayer.onGround && !this.flag4) {
                                        mc.thePlayer.jump();
                                        this.wasOnGround = true;
                                    } else if (this.wasOnGround) {
                                        if (!mc.thePlayer.isCollidedHorizontally) {
                                            mc.thePlayer.motionY = -0.0784000015258789;
                                        }

                                        this.wasOnGround = false;
                                    }

                                    GetSpeedHelper.handleFloat2(0.33F * (float)this.speedSetting.computeDouble());
                                } else {
                                    mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
                                }
                                break;
                            case "Strafe":
                                if (GetSpeedHelper.isMoving()) {
                                    if (mc.thePlayer.onGround && !this.flag4) {
                                        mc.thePlayer.jump();
                                    } else {
                                        GetSpeedHelper.handleFloat2(GetSpeedHelper.getSpeed() * (float)this.speedSetting.computeDouble());
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}
