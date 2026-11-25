package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;

public class LavaFill extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 3.0, 1.0, 6.0, 0.5);
    private final BooleanSetting rotateSetting = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting placeAfterPickupSetting = new BooleanSetting("Place After Pickup", this, false);
    private final TimerUtil timerUtil = new TimerUtil();
    private static final long timestamp = 250L;
    private BlockPos blockPos = null;
    private EnumFacing enumFacing = null;
    private int count = -1;
    private int count2 = -1;
    private float[] floatValueArray = null;

    private void onEventPreMotion(EventPreMotion event) {
        if (this.rotateSetting.isEnabled() && OnJumpHelper.checkState()) {
            mc.thePlayer.rotationYawHead = OnJumpHelper.floatValueArray2[0];
            mc.thePlayer.rotationPitchHead = OnJumpHelper.floatValueArray2[1];
            mc.thePlayer.renderYawOffset = OnJumpHelper.floatValueArray2[0];
            event.setYaw(OnJumpHelper.floatValueArray2[0]);
            event.setPitch(OnJumpHelper.floatValueArray2[1]);
        }
    }

    private LavaFill.Entry computeEntry() {
        BlockPos pos = mc.thePlayer.getPosition().down();
        IBlockState blockState = mc.theWorld.getBlockState(pos);
        Material material = blockState.getBlock().getMaterial();
        return material != Material.air && material != Material.water && material != Material.lava ? new LavaFill.Entry(pos, EnumFacing.UP) : this.computeEntry2();
    }

    private LavaFill.Entry computeEntry2() {
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos pos = mc.thePlayer.getPosition();
        LavaFill.Entry entry = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (int i = -n; i <= n; i++) {
            for (int j = ~n + 1; j <= n; j++) {
                for (int k = -n; k <= n; k++) {
                    BlockPos pos2 = pos.add(i, j, k);
                    IBlockState blockState = mc.theWorld.getBlockState(pos2);
                    Block block = blockState.getBlock();
                    Material material = block.getMaterial();
                    if (material == Material.air) {
                        for (EnumFacing facing : EnumFacing.values()) {
                            BlockPos pos3 = pos2.offset(facing.getOpposite());
                            IBlockState blockState2 = mc.theWorld.getBlockState(pos3);
                            Block block2 = blockState2.getBlock();
                            Material material2 = block2.getMaterial();
                            if (material2 != Material.air && material2 != Material.water && material2 != Material.lava) {
                                double dist = mc.thePlayer.getDistance(pos3.getX() + 0.5, pos3.getY() + 0.5, pos3.getZ() + 0.5);
                                if (dist <= this.rangeSetting.computeDouble() && dist < MAX_VALUE) {
                                    entry = new LavaFill.Entry(pos3, facing.getOpposite());
                                    MAX_VALUE = dist;
                                }
                            }
                        }
                    }
                }
            }
        }

        return entry;
    }

    public LavaFill() {
        super("LavaFill", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.rangeSetting, this.rotateSetting, this.placeAfterPickupSetting});
    }

    private BlockPos computeBlockPos() {
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos pos = mc.thePlayer.getPosition();
        BlockPos pos2 = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (int i = -n; i <= n; i++) {
            for (int j = ~(n - 1); j <= n; j++) {
                for (int k = ~(n - 1); k <= n; k++) {
                    BlockPos pos3 = pos.add(i, j, k);
                    IBlockState blockState = mc.theWorld.getBlockState(pos3);
                    if (blockState.getBlock() == Blocks.lava && blockState.getBlock().getMetaFromState(blockState) == 0) {
                        double dist = mc.thePlayer.getDistance(pos3.getX() + 0.5, pos3.getY() + 0.5, pos3.getZ() + 0.5);
                        if (dist <= this.rangeSetting.computeDouble() && dist < MAX_VALUE) {
                            pos2 = pos3;
                            MAX_VALUE = dist;
                        }
                    }
                }
            }
        }

        return pos2;
    }

    private void process() {
        if (this.rotateSetting.isEnabled() && this.floatValueArray != null) {
            OnJumpHelper.setReach((float)this.rangeSetting.computeDouble());
            OnJumpHelper.handleFloat(this.floatValueArray[0], this.floatValueArray[1], 2);
            OnJumpHelper.floatValueArray2 = this.floatValueArray;
        }

        int currentItem = mc.thePlayer.inventory.currentItem;
        float yaw = mc.thePlayer.rotationYaw;
        float pitch = mc.thePlayer.rotationPitch;
        mc.thePlayer.inventory.currentItem = this.count;
        mc.playerController.updateController();
        if (this.floatValueArray != null) {
            mc.thePlayer.rotationYaw = this.floatValueArray[0];
            mc.thePlayer.rotationPitch = this.floatValueArray[1];
        }

        ItemStack currentItem2 = mc.thePlayer.inventory.getCurrentItem();
        if (currentItem2 != null) {
            mc.playerController
                .onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    currentItem2,
                    this.blockPos,
                    this.enumFacing,
                    new Vec3(this.blockPos.getX() + 0.5, this.blockPos.getY() + 0.5, this.blockPos.getZ() + 0.5)
                );
            mc.thePlayer.swingItem();
        }

        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;
        mc.thePlayer.inventory.currentItem = currentItem;
        mc.playerController.updateController();
        this.timerUtil.reset();
        this.blockPos = null;
        this.enumFacing = null;
        this.count = -1;
        this.count2 = -1;
        this.floatValueArray = null;
    }

    @SafeVarargs
    private final int computeInt(Item... items) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null) {
                for (Item item : items) {
                    if (stack.getItem() == item) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.blockPos = null;
        this.enumFacing = null;
        this.count = -1;
        this.floatValueArray = null;
        OnJumpHelper.disable();
    }

    @Override
    public void onUpdate() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.timerUtil.matchesLong(250L, false)) {
                int n = this.computeInt(Items.water_bucket, Items.lava_bucket);
                int n2 = this.computeInt(Items.bucket);
                if (this.count2 >= 0) {
                    ItemStack stack = mc.thePlayer.inventory.mainInventory[this.count2];
                    if (stack != null && stack.getItem() == Items.lava_bucket) {
                        LavaFill.Entry entry = this.computeEntry();
                        if (entry != null) {
                            this.blockPos = entry.blockPos;
                            this.enumFacing = entry.face;
                            this.count = this.count2;
                            this.floatValueArray = this.computeFloatArray(entry.blockPos);
                            this.process();
                            this.count2 = -1;
                        }
                    }
                } else {
                    if (n != -1) {
                        LavaFill.Entry entry2 = this.computeEntry2();
                        if (entry2 != null) {
                            this.blockPos = entry2.blockPos;
                            this.enumFacing = entry2.face;
                            this.count = n;
                            this.floatValueArray = this.computeFloatArray(entry2.blockPos);
                            this.count2 = -1;
                            this.process();
                            return;
                        }
                    }

                    if (n2 != -1) {
                        BlockPos pos = this.computeBlockPos();
                        if (pos != null) {
                            this.blockPos = pos;
                            this.enumFacing = EnumFacing.UP;
                            this.count = n2;
                            this.floatValueArray = this.computeFloatArray(pos);
                            this.count2 = this.placeAfterPickupSetting.isEnabled() ? n2 : -1;
                            this.process();
                        }
                    }
                }
            }
        }
    }

    private float[] computeFloatArray(BlockPos pos) {
        double d = pos.getX() + 0.5 - mc.thePlayer.posX;
        double d2 = pos.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double d3 = pos.getZ() + 0.5 - mc.thePlayer.posZ;
        double dist = Math.sqrt(d * d + d3 * d3);
        float f = MathHelper.wrapAngleTo180_float((float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F);
        float f2 = MathHelper.clamp_float((float)(-(Math.atan2(d2, dist) * 180.0 / Math.PI)), -90.0F, 90.0F);
        return new float[]{f, f2};
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                if (((EventUpdate)event).getState() != EventState.PRE) {
                    return;
                }

                this.onUpdate();
            } else if (event instanceof EventPreMotion) {
                if (((EventPreMotion)event).getState() != EventState.PRE) {
                    return;
                }

                this.onEventPreMotion((EventPreMotion)event);
            }
        }
    }

    public static class Entry {
        public BlockPos blockPos;
        public EnumFacing face;

        public Entry(BlockPos blockPos, EnumFacing face) {
            this.blockPos = blockPos;
            this.face = face;
        }
    }
}
