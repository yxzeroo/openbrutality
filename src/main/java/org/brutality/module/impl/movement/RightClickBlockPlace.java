package org.brutality.module.impl.movement;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.input.Mouse;

public final class RightClickBlockPlace extends Module {
    private final ModeSetting anyBlockSetting = new ModeSetting("Block Type", this, "Obsidian", Arrays.asList("Obsidian", "Wool", "Any Block"));
    private static RightClickBlockPlace instance;
    private int count = -1;
    private int count2 = -1;
    private BlockPos blockPos;
    private BlockPos blockPos2;
    private EnumFacing enumFacing;
    private Vec3 hitVec;
    private float yaw;
    private float pitch;
    private boolean flag;
    private boolean flag2;

    @Override
    public void process2() {
        this.process3();
        this.blockPos = this.blockPos2 = null;
        this.flag2 = false;
    }

    public RightClickBlockPlace() {
        super("Right Click Block Place", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.anyBlockSetting});
        instance = this;
    }

    @Override
    public void onDisable() {
        this.process3();
        this.blockPos = this.blockPos2 = null;
        this.flag2 = false;
    }

    private boolean isActive() {
        return mc.currentScreen == null
            && Mouse.isButtonDown(1)
            && (KillAura.instance == null || !KillAura.instance.checkState44() || KillAura.entityLivingBase2 == null);
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock)stack.getItem()).getBlock();
                if ((!this.anyBlockSetting.matchesString2("Obsidian") || block == Blocks.obsidian)
                    && (!this.anyBlockSetting.matchesString2("Wool") || block == Blocks.wool)) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static float[] computeFloatArray() {
        RightClickBlockPlace rightClickBlockPlace = instance;
        return rightClickBlockPlace != null && rightClickBlockPlace.checkState44() && rightClickBlockPlace.flag ? new float[]{rightClickBlockPlace.yaw, rightClickBlockPlace.pitch} : null;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && mc.thePlayer != null && mc.theWorld != null) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                this.process();
            } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE && this.flag) {
                ((EventPreMotion)event).setYaw(this.yaw);
                ((EventPreMotion)event).setPitch(this.pitch);
            } else if (event instanceof EventPostMotion && this.flag2) {
                this.flag2 = false;
                this.process4();
            } else if (event instanceof BasicEvent4 && this.isActive()) {
                event.cancel();
            }
        }
    }

    private boolean matchesBlockPos2(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return block.isReplaceable(mc.theWorld, pos);
    }

    private boolean matchesBlockPos(BlockPos pos) {
        Material material = mc.theWorld.getBlockState(pos).getBlock().getMaterial();
        return material.isSolid() && material != Material.air;
    }

    private boolean matchesBlockPos(BlockPos blockPos, EntityPlayerSP player) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = blockPos.offset(facing.getOpposite());
            if (this.matchesBlockPos(pos)) {
                Vec3 vec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                this.hitVec = vec.addVector(facing.getFrontOffsetX() * 0.5, facing.getFrontOffsetY() * 0.5, facing.getFrontOffsetZ() * 0.5);
                double d = player.getPositionEyes(1.0F).distanceTo(this.hitVec);
                if (!(d > mc.playerController.getBlockReachDistance())) {
                    this.blockPos = blockPos;
                    this.blockPos2 = pos;
                    this.enumFacing = facing;
                    return true;
                }
            }
        }

        return false;
    }

    private void process() {
        this.flag = false;
        this.blockPos = this.blockPos2 = null;
        this.enumFacing = null;
        this.hitVec = null;
        this.flag2 = false;
        if (this.isActive() && Scaffold.computeFloatArray() == null && !mc.thePlayer.isRiding()) {
            EntityPlayerSP player = mc.thePlayer;
            if (this.count < 0) {
                this.count = player.inventory.currentItem;
            }

            this.count2 = this.computeInt();
            if (this.count2 < 0) {
                this.process3();
            } else {
                if (player.inventory.currentItem != this.count2) {
                    player.inventory.currentItem = this.count2;
                    mc.playerController.updateController();
                }

                float moveForward = player.movementInput.moveForward;
                float moveStrafe = player.movementInput.moveStrafe;
                if (moveForward == 0.0F && moveStrafe == 0.0F) {
                    this.process3();
                } else {
                    int n = MathHelper.floor_double(player.posY) - 1;
                    BlockPos pos = new BlockPos(player.posX, (double)n, player.posZ);
                    if (this.matchesBlockPos2(pos) && this.matchesBlockPos(pos, player)) {
                        Vec3 vec = new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                        double d = this.hitVec.xCoord - vec.xCoord;
                        double d2 = this.hitVec.yCoord - vec.yCoord;
                        double d3 = this.hitVec.zCoord - vec.zCoord;
                        double dist = Math.sqrt(d * d + d3 * d3);
                        this.yaw = (float)Math.toDegrees(Math.atan2(d3, d)) - 90.0F;
                        this.pitch = (float)(-Math.toDegrees(Math.atan2(d2, dist)));
                        this.yaw = OnJumpHelper.computeFloat4(this.yaw, player.getLastReportedRotations()[0]);
                        this.flag = true;
                        MovementFix.handleFloat(this.yaw, this.pitch);
                        this.flag2 = true;
                    }
                }
            }
        } else {
            this.process3();
        }
    }

    private void process4() {
        if (this.isActive()
            && this.flag
            && this.blockPos != null
            && this.blockPos2 != null
            && this.matchesBlockPos2(this.blockPos)
            && this.matchesBlockPos(this.blockPos2)) {
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (heldItem != null && heldItem.getItem() instanceof ItemBlock) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, heldItem, this.blockPos2, this.enumFacing, this.hitVec)) {
                    mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                }
            }
        }
    }

    public static boolean checkState() {
        return computeFloatArray() != null;
    }

    private void process3() {
        if (this.count >= 0 && mc.thePlayer != null && mc.thePlayer.inventory.currentItem != this.count) {
            mc.thePlayer.inventory.currentItem = this.count;
            mc.playerController.updateController();
        }

        this.count = this.count2 = -1;
        this.flag = false;
    }
}
