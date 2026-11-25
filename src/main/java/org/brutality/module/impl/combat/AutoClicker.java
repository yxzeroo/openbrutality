package org.brutality.module.impl.combat;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.player.PlayerEvent3;
import org.brutality.event.impl.render.RenderEvent3;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.swap.AutoLifesteal;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.lwjgl.input.Mouse;

public class AutoClicker extends Module {
    public static AutoClicker instance;
    public static boolean y;
    private final NumberSetting targetCPSSetting = new NumberSetting("Target CPS", this, 10.0, 1.0, 20.0, 0.5);
    private final BooleanSetting simulateExhaustSetting = new BooleanSetting("Simulate Exhaust", this, true);
    private final BooleanSetting notUsingItemSetting = new BooleanSetting("Not Using Item", this, false);
    private final BooleanSetting breakBlocksSetting = new BooleanSetting("Break Blocks", this, false);
    private final BooleanSetting weaponOnlySetting = new BooleanSetting("Weapon Only", this, false);
    private final BooleanSetting disableInCreativeSetting = new BooleanSetting("Disable In Creative", this, false);
    private final BooleanSetting inventorySetting = new BooleanSetting("Inventory", this, false);
    private final NumberSetting startDelaySetting = new NumberSetting("Start Delay", this, 100.0, 0.0, 250.0, 10.0);
    private long timestamp;
    private long timestamp2;
    private boolean z;
    private Random rand = new Random();
    private double amount;

    public boolean checkState5() {
        if (this.checkState44() && this.inventorySetting.isEnabled() && mc.currentScreen instanceof GuiContainer) {
            return true;
        }
        this.timestamp2 = 0L;
        return false;
    }

    private void process4() {
        if (!(mc.currentScreen instanceof GuiContainer)) {
            this.timestamp2 = 0L;
        } else if (!Mouse.isButtonDown(0)) {
            this.timestamp2 = 0L;
        } else {
            GuiContainer screen = (GuiContainer)mc.currentScreen;
            long now = System.currentTimeMillis();
            if (this.timestamp2 == 0L) {
                this.timestamp2 = now + (long)this.startDelaySetting.computeDouble();
            }

            int n = now >= this.timestamp2 ? 1 : 0;
            if (n > 0) {
                long l = this.computeLong();
                this.timestamp2 += l;
                if (this.timestamp2 <= now) {
                    this.timestamp2 = now + l;
                }
            }

            if (n > 0) {
                Slot theSlot = screen.getTheSlot();
                if (theSlot != null && theSlot.slotNumber >= 0) {
                    int windowId = screen.inventorySlots.windowId;
                    int slotNumber = theSlot.slotNumber;
                    int n2 = GuiScreen.isShiftKeyDown() ? 1 : 0;
                    if (mc.playerController != null && mc.thePlayer != null) {
                        for (int i = 0; i < n; i++) {
                            mc.playerController.windowClick(windowId, slotNumber, 0, n2, mc.thePlayer);
                        }
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

    private void process3() {
        this.amount = 0.0;
        this.timestamp = 0L;
        this.timestamp2 = 0L;
        if (mc.gameSettings != null && this.z) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }

        this.z = false;
    }

    public String getDisplayName() {
        return this.checkState44() ? this.getName() + " " + this.targetCPSSetting.computeDouble() : this.getName();
    }

    private boolean checkState4() {
        if (mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        Item item = mc.thePlayer.getHeldItem().getItem();
        return item instanceof ItemSword || item instanceof ItemAxe;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.rand = new Random();
            this.process3();
        }
    }

    private long computeLong() {
        double d = 1000.0 / Math.max(1.0, this.targetCPSSetting.computeDouble());
        if (!this.simulateExhaustSetting.isEnabled()) {
            return Math.max(33L, Math.round(d + this.rand.nextInt(21) - 10.0));
        }
        this.amount = Math.max(-0.18, Math.min(0.25, this.amount * 0.96 + this.rand.nextGaussian() * 0.025));
        double d2 = Math.max(0.55, Math.min(2.2, Math.exp(this.rand.nextGaussian() * 0.32 - 0.0512)));
        double d3 = d * (1.0 + this.amount) * d2;
        if (this.rand.nextDouble() < 0.025) {
            d3 += d * (0.5 + this.rand.nextDouble());
        }

        return Math.max(33L, Math.min(4000L, Math.round(d3)));
    }

    private void handleInt(int n) {
        if (n > 0) {
            if (mc.entityRenderer == null) {
                this.process3();
            } else {
                mc.entityRenderer.getMouseOver(1.0F);
                if (AutoGrinder.checkState30()) {
                    AutoLifesteal.checkState();

                    for (int i = 0; i < n; i++) {
                        mc.leftClickCounter = 0;
                        mc.clickMouse();
                    }
                }
            }
        }
    }

    private int computeInt(long l) {
        if (this.timestamp == 0L) {
            this.timestamp = l + this.computeLong();
        }

        if (l < this.timestamp) {
            return 0;
        }
        long l2 = this.computeLong();
        this.timestamp += l2;
        if (this.timestamp <= l) {
            this.timestamp = l + l2;
        }

        return 1;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof PlayerEvent3) {
                    if (mc.thePlayer == null || mc.theWorld == null || !this.checkState7() && this.checkState()) {
                        this.process3();
                        return;
                    }

                    if (mc.currentScreen == null && mc.inGameHasFocus) {
                        this.process();
                    } else {
                        this.timestamp = 0L;
                        this.handleInt2(mc.gameSettings.keyBindAttack.getKeyCode());
                    }
                } else if (event instanceof RenderEvent3) {
                    if (this.inventorySetting.isEnabled() && mc.thePlayer != null && mc.theWorld != null) {
                        this.process4();
                    } else {
                        this.timestamp2 = 0L;
                    }
                }
            }
        }
    }

    @Override
    public boolean checkState7() {
        return AutoGrinder.matchesModule2(this);
    }

    private void process() {
        int keyCode = mc.gameSettings.keyBindAttack.getKeyCode();
        boolean flag = this.checkState7() || AutoGrinder.checkState6();
        if (flag && !AutoGrinder.checkState2()) {
            this.process3();
        } else if (!Mouse.isButtonDown(0) && !y) {
            this.process3();
        } else {
            long now = System.currentTimeMillis();
            int n = this.computeInt(now);
            if (flag) {
                if (mc.thePlayer.isUsingItem()) {
                    if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
                        return;
                    }

                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                }

                this.handleInt(n);
            } else if (!this.notUsingItemSetting.isEnabled() || !mc.thePlayer.isUsingItem()) {
                if (!this.disableInCreativeSetting.isEnabled() || !mc.thePlayer.capabilities.isCreativeMode) {
                    if (!this.weaponOnlySetting.isEnabled() || this.checkState4()) {
                        if (this.breakBlocksSetting.isEnabled()) {
                            if (!mc.thePlayer.capabilities.allowEdit) {
                                this.handleInt2(keyCode);
                            } else {
                                if (this.checkState2()) {
                                    this.handleInt3(keyCode);
                                    return;
                                }

                                if (this.z) {
                                    this.handleInt2(keyCode);
                                    return;
                                }
                            }
                        } else {
                            this.handleInt2(keyCode);
                        }

                        if (n > 0) {
                            for (int i = 0; i < n; i++) {
                                KeyBinding.onTick(keyCode);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleInt2(int n) {
        if (this.z) {
            KeyBinding.setKeyBindState(n, false);
            this.z = false;
        }
    }

    public AutoClicker() {
        super("Auto Clicker", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.targetCPSSetting,
                this.simulateExhaustSetting,
                this.notUsingItemSetting,
                this.breakBlocksSetting,
                this.weaponOnlySetting,
                this.disableInCreativeSetting,
                this.inventorySetting,
                this.startDelaySetting
            }
        );
        instance = this;
    }

    private boolean checkState2() {
        if (mc.objectMouseOver == null) {
            return false;
        }
        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
        if (blockPos == null) {
            return false;
        }
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        return block != Blocks.air && !(block instanceof BlockLiquid);
    }

    private void handleInt3(int n) {
        if (!this.z) {
            KeyBinding.setKeyBindState(n, true);
            this.z = true;
        }
    }

    private boolean checkState() {
        return KillAura.instance != null && KillAura.instance.checkState44() && KillAura.entityLivingBase2 != null;
    }
}
