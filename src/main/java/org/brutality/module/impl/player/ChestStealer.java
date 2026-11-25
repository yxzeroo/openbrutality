package org.brutality.module.impl.player;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.EntityHelper;
import org.brutality.utils.player.ExtraAttributesHelper;
import org.brutality.utils.timer.TimerUtil;

public class ChestStealer extends Module {
    private static ChestStealer instance;
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 100.0, 0.0, 500.0, 1.0);
    private final NumberSetting startDelayTicksSetting = new NumberSetting("Start Delay Ticks", this, 0.0, 0.0, 20.0, 1.0);
    private final BooleanSetting ignoreTrashSetting = new BooleanSetting("Ignore Trash", this, true);
    private final BooleanSetting ignoreEnderChestsSetting = new BooleanSetting("Ignore Ender-Chests", this, true);
    private final BooleanSetting smartChestDetectionSetting = new BooleanSetting("Smart Chest Detection", this, true);
    private final TimerUtil timerUtil = new TimerUtil();
    private int count;
    private int count2;
    private boolean finished2;
    private long timestamp;
    private static final long timestamp2 = 1500L;

    public ChestStealer() {
        super("Chest Stealer", 0, Category.PLAYER);
        this.handleSettingArray(
            new Setting[]{
                this.delaySetting, this.startDelayTicksSetting, this.ignoreTrashSetting, this.ignoreEnderChestsSetting, this.smartChestDetectionSetting
            }
        );
        instance = this;
    }

    private boolean checkState() {
        return this.timestamp != 0L && System.currentTimeMillis() - this.timestamp < timestamp2;
    }

    public static ChestStealer getInstance() {
        return instance;
    }

    public boolean isFinished() {
        return this.finished2;
    }

    private boolean matchesIInventory(IInventory iInventory) {
        if (iInventory == null) {
            return false;
        }
        String text = iInventory.getDisplayName().getUnformattedText();
        if (text.equals("Ender Chest") && this.ignoreEnderChestsSetting.isEnabled()) {
            return false;
        }
        return !text.equals("Chest") && !text.equals("Double Chest") && !text.equals("Ender Chest")
            ? this.smartChestDetectionSetting.isEnabled() && this.checkState()
            : true;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (CarePackage.instance == null || !CarePackage.instance.checkState44() || !FreshHelper.flag) {
                if (mc.theWorld != null && mc.thePlayer != null) {
                    if (event instanceof EventUpdate) {
                        if (((EventUpdate)event).getState() == EventState.PRE) {
                            if (mc.currentScreen == null && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                                BlockPos blockPos = mc.objectMouseOver.getBlockPos();
                                if (blockPos != null) {
                                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                                    if (block == Blocks.chest || block == Blocks.trapped_chest || block == Blocks.ender_chest) {
                                        this.timestamp = System.currentTimeMillis();
                                    }
                                }
                            }

                            if (!(mc.currentScreen instanceof GuiChest)) {
                                this.count = 0;
                                this.count2 = 0;
                                this.finished2 = false;
                            } else {
                                ContainerChest openContainer = (ContainerChest)mc.thePlayer.openContainer;
                                IInventory lowerChestInventory = openContainer.getLowerChestInventory();
                                if (this.matchesIInventory(lowerChestInventory)) {
                                    this.count++;
                                    this.finished2 = false;
                                    if (!(this.count < this.startDelayTicksSetting.computeDouble())) {
                                        if (!EntityHelper.isFlag() && this.timerUtil.matchesLong8((int)this.delaySetting.computeDouble())) {
                                            ArrayList list = new ArrayList();

                                            for (int i = 0; i < lowerChestInventory.getSizeInventory(); i++) {
                                                ItemStack stack = lowerChestInventory.getStackInSlot(i);
                                                if (stack != null && (!this.ignoreTrashSetting.isEnabled() || ExtraAttributesHelper.matchesItemStack(stack))) {
                                                    list.add(i);
                                                }
                                            }

                                            if (!list.isEmpty()) {
                                                int n = (Integer)list.get(0);
                                                mc.playerController.windowClick(openContainer.windowId, n, 0, 1, mc.thePlayer);
                                                this.timerUtil.reset();
                                                this.count2 = 0;
                                            } else {
                                                this.count2++;
                                                if (this.count2 > 3) {
                                                    mc.thePlayer.closeScreen();
                                                    this.finished2 = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean checkState2() {
        return mc.currentScreen instanceof GuiChest && !this.finished2 && this.count >= (int)this.startDelayTicksSetting.computeDouble();
    }
}
