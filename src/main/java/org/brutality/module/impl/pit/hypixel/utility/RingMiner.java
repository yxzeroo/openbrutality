package org.brutality.module.impl.pit.hypixel.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.timer.TimerUtil;

public class RingMiner
extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 4.0, 1.0, 8.0, 0.5);
    private final NumberSetting minCPSSetting = new NumberSetting("Min CPS", this, 10.0, 1.0, 20.0, 1.0);
    private final NumberSetting maxCPSSetting = new NumberSetting("Max CPS", this, 20.0, 1.0, 20.0, 1.0);
    private final NumberSetting maxTargetsSetting = new NumberSetting("Max Targets", this, 4.0, 1.0, 16.0, 1.0);
    private final ModeSetting noneSetting = new ModeSetting("Rotation", (Module)this, "Silent", Arrays.asList("Silent", "None"));
    private final TimerUtil timerUtil = new TimerUtil();
    private final Random random = new Random();
    private BlockPos blockPos;
    private int count = -1;
    private long timestamp;

    private int computeInt() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = RingMiner.mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null || !(itemStack.getItem() instanceof ItemPickaxe) || !itemStack.getDisplayName().toLowerCase().contains("gold")) continue;
            return i;
        }
        return -1;
    }

    @Override
    public void onDisable() {
        this.blockPos = null;
    }

    public RingMiner() {
        super("Ring Miner", 0, Category.PIT);
        this.handleSettingArray(this.rangeSetting, this.minCPSSetting, this.maxCPSSetting, this.maxTargetsSetting, this.noneSetting);
    }

    @Override
    public void onEnable() {
        this.timestamp = this.computeLong();
        this.timerUtil.reset();
    }

    private BlockPos computeBlockPos() {
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos blockPos = RingMiner.mc.thePlayer.getPosition();
        List<BlockPos> list = this.findTargets();
        return list.isEmpty() ? null : list.get(0);
    }

    private void handleEventPreMotion(EventPreMotion eventPreMotion, BlockPos blockPos) {
        double d = (double)blockPos.getX() + 0.5 - RingMiner.mc.thePlayer.posX;
        double d2 = (double)blockPos.getY() + 0.5 - (RingMiner.mc.thePlayer.posY + (double)RingMiner.mc.thePlayer.getEyeHeight());
        double d3 = (double)blockPos.getZ() + 0.5 - RingMiner.mc.thePlayer.posZ;
        float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(-(Math.atan2(d2, Math.sqrt(d * d + d3 * d3)) * 180.0 / Math.PI));
        eventPreMotion.setYaw(f);
        eventPreMotion.setPitch(f2);
    }

    @Override
    public void onEvent(Event event) {
        if (!this.checkState44() || RingMiner.mc.thePlayer == null || RingMiner.mc.theWorld == null) {
            return;
        }
        if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
            this.tick();
        }
        if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE && this.blockPos != null && this.noneSetting.matchesString2("Silent")) {
            this.handleEventPreMotion((EventPreMotion)event, this.blockPos);
        }
    }

    private List<BlockPos> findTargets() {
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos blockPos2 = RingMiner.mc.thePlayer.getPosition();
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        for (int i = -n; i <= n; ++i) {
            for (int j = (n ^ 0xFFFFFFFF) + 1; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    BlockPos blockPos3 = blockPos2.add(i, j, k);
                    if (!(RingMiner.mc.thePlayer.getDistance((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.5, (double)blockPos3.getZ() + 0.5) <= this.rangeSetting.computeDouble()) || RingMiner.mc.theWorld.getBlockState(blockPos3).getBlock() != Blocks.obsidian) continue;
                    arrayList.add(blockPos3);
                }
            }
        }
        arrayList.sort(Comparator.comparingDouble(blockPos -> RingMiner.mc.thePlayer.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
        return arrayList;
    }

    private void tick() {
        this.count = this.computeInt();
        if (this.count < 0) {
            return;
        }
        RingMiner.mc.thePlayer.inventory.currentItem = this.count;
        if (this.blockPos == null || RingMiner.mc.theWorld.getBlockState(this.blockPos).getBlock() != Blocks.obsidian) {
            this.blockPos = this.computeBlockPos();
        }
        if (this.blockPos == null || !this.timerUtil.matchesLong(this.timestamp, false)) {
            return;
        }
        this.timerUtil.reset();
        if (this.noneSetting.matchesString2("None")) {
            List<BlockPos> list = this.findTargets();
            int n = Math.min(list.size(), (int)this.maxTargetsSetting.computeDouble());
            for (int i = 0; i < n; ++i) {
                RingMiner.mc.playerController.onPlayerDamageBlock(list.get(i), EnumFacing.UP);
            }
        } else {
            RingMiner.mc.playerController.onPlayerDamageBlock(this.blockPos, EnumFacing.UP);
        }
        RingMiner.mc.playerController.blockHitDelay = 0;
        this.timestamp = this.computeLong();
    }

    private long computeLong() {
        double d = this.minCPSSetting.computeDouble() + this.random.nextDouble() * Math.max(0.0, this.maxCPSSetting.computeDouble() - this.minCPSSetting.computeDouble());
        return Math.max(1L, (long)(1000.0 / d));
    }
}
