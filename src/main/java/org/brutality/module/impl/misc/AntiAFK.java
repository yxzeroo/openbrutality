package org.brutality.module.impl.misc;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.client.settings.GameSettings;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AntiAFK extends Module {
    private final ModeSetting npcSetting = new ModeSetting("Mode", this, "NPC", Arrays.asList("Simple", "NPC"));
    private final NumberSetting idleSecondsSetting = new NumberSetting("Idle Seconds", this, 10.0, 3.0, 60.0, 1.0);
    private final BooleanSetting rotateHeadSetting = new BooleanSetting("Rotate Head", this, true);
    private final BooleanSetting randomWalkSetting = new BooleanSetting("Random Walk", this, true);
    private final BooleanSetting randomJumpSetting = new BooleanSetting("Random Jump", this, true);
    private int count;
    private final Random random = new Random();
    private float factor;
    private float factor2;
    private int count2;
    private int count3;
    private int count4;
    private int count5;

    private void process5() {
        this.count5++;
        if (this.rotateHeadSetting.isEnabled()) {
            this.process();
        }

        if (this.randomWalkSetting.isEnabled()) {
            this.process4();
        }

        if (this.randomJumpSetting.isEnabled() && this.count5 % (60 + this.random.nextInt(80)) == 0) {
            mc.gameSettings.keyBindJump.setPressed(true);
        } else if (mc.gameSettings.keyBindJump.isKeyDown() && this.count5 % 2 == 0) {
            mc.gameSettings.keyBindJump.setPressed(false);
        }
    }

    private void process6() {
        int ticksExisted = mc.thePlayer.ticksExisted;
        if (ticksExisted % 5 == 0) {
            mc.gameSettings.keyBindRight.setPressed(false);
            mc.gameSettings.keyBindLeft.setPressed(false);
            mc.gameSettings.keyBindJump.setPressed(false);
        }

        if (ticksExisted % 20 == 0) {
            if (ticksExisted % 40 == 0) {
                mc.gameSettings.keyBindRight.setPressed(true);
            } else {
                mc.gameSettings.keyBindLeft.setPressed(true);
            }
        }

        if (ticksExisted % 100 == 0) {
            mc.gameSettings.keyBindJump.setPressed(true);
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
            this.count2 = 0;
            this.count3 = 0;
            this.count4 = 40 + this.random.nextInt(60);
            this.count5 = 0;
        }
    }

    private void process4() {
        this.count3--;
        if (this.count3 <= 0) {
            this.process3();
            this.count4 = 20 + this.random.nextInt(60);
            if (this.random.nextFloat() < 0.6F) {
                this.count2 = 1 + this.random.nextInt(4);
                this.count3 = 10 + this.random.nextInt(30);
            } else {
                this.count2 = 0;
                this.count3 = 20 + this.random.nextInt(40);
            }
        }

        switch (this.count2) {
            case 1:
                mc.gameSettings.keyBindForward.setPressed(true);
                break;
            case 2:
                mc.gameSettings.keyBindBack.setPressed(true);
                break;
            case 3:
                mc.gameSettings.keyBindLeft.setPressed(true);
                break;
            case 4:
                mc.gameSettings.keyBindRight.setPressed(true);
        }
    }

    public AntiAFK() {
        super("Anti AFK", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(new Setting[]{this.npcSetting, this.idleSecondsSetting, this.rotateHeadSetting, this.randomWalkSetting, this.randomJumpSetting});
    }

    private void process3() {
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindBack.setPressed(false);
        mc.gameSettings.keyBindLeft.setPressed(false);
        mc.gameSettings.keyBindRight.setPressed(false);
        mc.gameSettings.keyBindJump.setPressed(false);
    }

    private void process() {
        if (this.count5 % (30 + this.random.nextInt(70)) == 0) {
            this.factor = mc.thePlayer.rotationYaw + (this.random.nextFloat() - 0.5F) * 120.0F;
            this.factor2 = -10.0F + this.random.nextFloat() * 30.0F;
        }

        float f = this.factor - mc.thePlayer.rotationYaw;
        float f2 = this.factor2 - mc.thePlayer.rotationPitch;
        float f3 = 2.0F + this.random.nextFloat() * 1.5F;
        f = Math.max(-f3, Math.min(f3, f));
        f2 = Math.max(-f3 * 0.5F, Math.min(f3 * 0.5F, f2));
        mc.thePlayer.rotationYaw += f;
        mc.thePlayer.rotationPitch += f2;
        mc.thePlayer.rotationPitch = Math.max(-90.0F, Math.min(90.0F, mc.thePlayer.rotationPitch));
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate) {
                    if (mc.thePlayer == null) {
                        return;
                    }

                    GameSettings settings = mc.gameSettings;
                    if (settings.keyBindJump.isPressed()
                        || settings.keyBindRight.isPressed()
                        || settings.keyBindForward.isPressed()
                        || settings.keyBindLeft.isPressed()
                        || settings.keyBindBack.isPressed()
                        || settings.keyBindAttack.isPressed()
                        || settings.keyBindUseItem.isPressed()) {
                        this.count = 0;
                        this.process3();
                        this.count2 = 0;
                        this.count3 = 0;
                        return;
                    }

                    this.count++;
                    int n = (int)(this.idleSecondsSetting.computeDouble() * 20.0);
                    if (this.count < n) {
                        return;
                    }

                    if (this.npcSetting.getText().equals("Simple")) {
                        this.process6();
                    } else {
                        this.process5();
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process3();
    }
}
