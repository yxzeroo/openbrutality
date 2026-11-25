package org.brutality.module.impl.movement;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.client.settings.GameSettings;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.automation.AutoHunt;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.module.impl.player.ChestStealer;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.lwjgl.input.Keyboard;

public class Sprint extends Module {
    private final ModeSetting omnidirectionalSetting = new ModeSetting("Mode", this, "Legit", Arrays.asList("Legit", "Omnidirectional"));
    public static Sprint instance;

    private void process3() {
        if (mc.thePlayer.movementInput != null && mc.gameSettings != null) {
            if (mc.thePlayer.isRiding()) {
                this.handleFlag(Keyboard.isCreated() && GameSettings.isKeyDown(mc.gameSettings.keyBindSprint));
            } else if (!AutoGrinder.checkState28() && !AutoHunt.checkState13()) {
                if (StartHotbarMovesHelper.flag) {
                    this.handleFlag(false);
                } else if (Scaffold.getInstance() != null && Scaffold.getInstance().checkState44()) {
                    this.handleFlag(false);
                } else {
                    ChestStealer instance = ChestStealer.getInstance();
                    if ((instance == null || !instance.checkState44() || !instance.checkState2()) && (CarePackage.instance == null || !CarePackage.instance.checkState4())) {
                        if (KeepSprint.instance != null && KeepSprint.instance.checkState13()) {
                            this.handleFlag(false);
                            mc.thePlayer.resetSprintToggleTimer();
                            mc.thePlayer.setSprinting(false);
                        } else {
                            this.handleFlag(this.checkState2());
                        }
                    } else {
                        this.handleFlag(false);
                    }
                }
            }
        } else {
            if (mc.gameSettings != null) {
                this.handleFlag(false);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (this.checkState44() && RuntimeAuthGuard.checkState2() && mc.thePlayer != null) {
        if (this.checkState44() && true && mc.thePlayer != null) {
            if (event instanceof EventUpdate event2) {
                if (event2.getState() == EventState.PRE) {
                    this.process3();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.handleFlag(false);
        if (mc.thePlayer != null) {
            mc.thePlayer.setSprinting(false);
        }

        super.onDisable();
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
        }
    }

    @Generated
    public static Sprint getInstance() {
        return instance;
    }

    public Sprint() {
        super("Sprint", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.omnidirectionalSetting});
        instance = this;
        this.handleFlag3(true);
    }

    private boolean checkState() {
        return mc.thePlayer.getFoodStats().getFoodLevel() > 6 && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking();
    }

    public static void process() {
        // VENDOR-DISABLED: if (instance != null && instance.checkState44() && RuntimeAuthGuard.checkState2() && mc.thePlayer != null) {
        if (instance != null && instance.checkState44() && true && mc.thePlayer != null) {
            instance.process3();
        }
    }

    private boolean checkState2() {
        if (mc.thePlayer == null || mc.thePlayer.movementInput == null || mc.gameSettings == null || mc.thePlayer.isRiding()) {
            return false;
        }
        return !this.omnidirectionalSetting.matchesString2("Omnidirectional")
            ? true
            : this.checkState()
                && (
                    mc.gameSettings.keyBindForward.isKeyDown()
                        || mc.gameSettings.keyBindBack.isKeyDown()
                        || mc.gameSettings.keyBindLeft.isKeyDown()
                        || mc.gameSettings.keyBindRight.isKeyDown()
                );
    }

    private void handleFlag(boolean flag) {
        if (mc.gameSettings != null && mc.gameSettings.keyBindSprint != null) {
            mc.gameSettings.keyBindSprint.pressed = flag;
        }
    }
}
