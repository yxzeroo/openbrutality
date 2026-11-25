package org.brutality.module.impl.movement;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.potion.PotionEffect;
import org.brutality.clickgui.classic.ConfirmScreen;
import org.brutality.clickgui.classic.DrawSettingLabelScreen;
// VENDOR-DISABLED: import org.brutality.clickgui.newclick.GuiCloudBrowser;
import org.brutality.clickgui.newclick.NewClickGUI;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.MovementEvent3;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.module.impl.player.ChestStealer;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.lwjgl.input.Keyboard;

public class InventoryMove extends Module {
    private ModeSetting packetSpamSetting = new ModeSetting(
        "Mode", this, "Vanilla", Arrays.asList("Vanilla", "Stop Movement", "Motion", "Only Menus", "Packet Spam")
    );
    private final BooleanSetting pauseDuringChestStealerSetting = new BooleanSetting("Pause During Chest Stealer", this, true);
    private int count;
    private boolean flag;
    private boolean flag2;
    private static InventoryMove instance;

    private void process4() {
        if (!mc.thePlayer.onGround) {
            this.handleDouble(0.56, 0.0);
        } else if (mc.thePlayer.isSprinting()) {
            if (this.computeInt() == 0) {
                this.handleDouble(0.72, 0.03);
            } else {
                this.handleDouble(0.64, 0.03);
            }
        } else {
            this.handleDouble(0.97, 0.03);
        }
    }

    private boolean checkState() {
        return mc.currentScreen instanceof NewClickGUI
            // VENDOR-DISABLED: || mc.currentScreen instanceof GuiCloudBrowser
            || mc.currentScreen instanceof DrawSettingLabelScreen
            || mc.currentScreen instanceof ConfirmScreen
            || mc.currentScreen instanceof GuiIngameMenu;
    }

    public InventoryMove() {
        super("Inventory Move", 0, Category.MOVEMENT);
        instance = this;
        this.handleSettingArray(new Setting[]{this.packetSpamSetting, this.pauseDuringChestStealerSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (GetCurrentScreenHelper.checkState2()) {
                    if (event instanceof EventPacketSend event2 && event2.getPacket() instanceof C0EPacketClickWindow) {
                        this.flag2 = true;
                        this.count = 0;
                    }

                    if (event instanceof EventUpdate event3) {
                        if (event3.getState() != EventState.PRE) {
                            return;
                        }

                        this.process();
                    }

                    if (event instanceof MovementEvent3 && this.checkState2() && !this.checkState() && this.packetSpamSetting.matchesString2("Motion")) {
                        mc.thePlayer.movementInput.jump = false;
                    }

                    if (event instanceof EventTick) {
                        this.process6();
                    }
                }
            }
        }
    }

    public boolean isActive() {
        return this.checkState44() && this.checkState2() && !this.checkState() && this.packetSpamSetting.matchesString2("Motion");
    }

    private void process() {
        if (!this.checkState2()) {
            this.reset();
        } else {
            if (this.pauseDuringChestStealerSetting.isEnabled()) {
                ChestStealer instance = ChestStealer.getInstance();
                if (instance != null && instance.checkState44() && instance.checkState2() || CarePackage.instance != null && CarePackage.instance.checkState4()) {
                    this.handleFlag(false);
                    return;
                }
            }

            if (this.flag2) {
                this.count++;
                if (this.count >= 10) {
                    this.count = 0;
                    this.flag2 = false;
                }
            }

            String text = this.packetSpamSetting.getText();
            switch (text) {
                case "Vanilla":
                    this.handleFlag(true);
                    break;
                case "Stop Movement":
                    if (!this.checkState()) {
                        if (this.flag2) {
                            this.handleFlag(false);
                        } else {
                            this.handleFlag(true);
                        }
                    } else {
                        this.handleFlag(true);
                    }
                    break;
                case "Motion":
                    if (!this.checkState()) {
                        this.handleFlag(true);
                        this.process4();
                    } else {
                        this.handleFlag(true);
                    }
                    break;
                case "Only Menus":
                    if (!this.checkState()) {
                        this.handleFlag(false);
                    } else {
                        this.handleFlag(true);
                    }
                    break;
                case "Packet Spam":
                    this.handleFlag(true);
                    if (!this.checkState()) {
                        this.process3();
                    }
            }

            this.process5();
        }
    }

    @Generated
    public static InventoryMove getInstance() {
        return instance;
    }

    private void reset() {
        this.count = 0;
        this.flag2 = false;
        if (!this.flag) {
            this.handleFlag(true);
        }
    }

    private void process5() {
        if (StartHotbarMovesHelper.flag) {
            mc.thePlayer.setSprinting(false);
        } else {
            boolean flag = mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying;
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())
                && mc.thePlayer.movementInput.moveForward >= 0.8F
                && flag
                && !mc.thePlayer.isSprinting()) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }

    private int computeInt() {
        for (PotionEffect activePotionEffect : mc.thePlayer.getActivePotionEffects()) {
            if (activePotionEffect.getEffectName().equals("potion.moveSpeed")) {
                return activePotionEffect.getAmplifier() + 1;
            }
        }

        return 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    private void handleDouble(double d, double d2) {
        mc.thePlayer.motionX = mc.thePlayer.motionX * (mc.thePlayer.moveStrafing == 0.0F ? d : d - d2);
        mc.thePlayer.motionZ = mc.thePlayer.motionZ * (mc.thePlayer.moveStrafing == 0.0F ? d : d - d2);
    }

    private void process3() {
        if (mc.thePlayer.openContainer != null) {
            int windowId = mc.thePlayer.openContainer.windowId;
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(windowId));
        }
    }

    private void handleFlag(boolean flag) {
        if (flag) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), GetCurrentScreenHelper.checkState4());
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
            this.flag = true;
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            if (mc.thePlayer != null) {
                mc.thePlayer.setSprinting(false);
            }

            this.flag = false;
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.reset();
        }
    }

    private boolean checkState2() {
        return mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat);
    }

    private void process6() {
        if (this.checkState2()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && mc.thePlayer.rotationPitch < 90.0F) {
                mc.thePlayer.rotationPitch++;
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_UP) && mc.thePlayer.rotationPitch > -90.0F) {
                mc.thePlayer.rotationPitch--;
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                mc.thePlayer.rotationYaw++;
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                mc.thePlayer.rotationYaw--;
            }
        }
    }
}
