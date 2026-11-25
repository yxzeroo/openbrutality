package org.brutality.module.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockNote;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.core.CoreHandler2;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.event.impl.movement.MovementEvent8;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.GetSpeedHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Scaffold extends Module {
    private static final EnumFacing[] enumFacingArray = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
    private final ModeSetting tellySetting = new ModeSetting("Mode", this, "Telly", Arrays.asList("Normal", "Telly")) {
        @Override
        public boolean matchesString(String str) {
            return "Smooth".equalsIgnoreCase(str) || super.matchesString(str);
        }

        @Override
        public void handleString(String str) {
            super.handleString("Smooth".equalsIgnoreCase(str) ? "Normal" : str);
        }
    };
    private final BooleanSetting towerSetting = new BooleanSetting("Tower", this, false);
    private final BooleanSetting towerCenterSetting = new BooleanSetting("Tower Center", this, false);
    private final ModeSetting standingSetting = new ModeSetting("Tower When", this, "Always", Arrays.asList("Always", "Moving", "Standing"));
    private final ModeSetting hypixelVerticalSetting = new ModeSetting("Tower Mode", this, "Vanilla", Arrays.asList("Vanilla", "HypixelVertical"));
    private final NumberSetting towerSpeedSetting = new NumberSetting("Tower Speed", this, 1.0, 0.1, 1.0, 0.01);
    private final ModeSetting nonUpwardsSetting = new ModeSetting("Telly Mode", this, "Non Upwards", Arrays.asList("Normal", "Non Upwards"))
        .computeModeSetting(() -> this.tellySetting.matchesString2("Telly"));
    private final ModeSetting noneSetting = new ModeSetting(
        "Rotation Mode", this, "Hypixel", Arrays.asList("Hypixel", "Raycast", "Simple", "Backwards", "None")
    );
    private final ModeSetting veryLowSetting = new ModeSetting(
        "Raycast Speed", this, "Adaptive", Arrays.asList("Adaptive", "Instant", "Half", "Quarter", "Very High", "High", "Medium", "Low", "Very Low")
    );
    private final BooleanSetting smoothRotsSetting = new BooleanSetting("Smooth Rots", this, false);
    private final ModeSetting linearSetting = new ModeSetting("Rotation Curve", this, "Smooth", Arrays.asList("Smooth", "Linear"));
    private final NumberSetting maxRotationSpeedSetting = new NumberSetting("Max Rotation Speed", this, 34.5, 0.0, 180.0, 0.01);
    private final NumberSetting minRotationSpeedSetting = new NumberSetting("Min Rotation Speed", this, 34.5, 0.0, 180.0, 0.01);
    private final NumberSetting preRotationLeadSetting = new NumberSetting("Pre Rotation Lead", this, 2.0, 0.0, 2.0, 0.01);
    private final BooleanSetting snapRotationSetting = new BooleanSetting("Snap Rotation", this, false);
    private final BooleanSetting noPlaceWhenSnappingSetting = new BooleanSetting("No Place when Snapping", this, false);
    private final BooleanSetting noHitCheckSetting = new BooleanSetting("No Hit Check", this, false);
    private final BooleanSetting tellyAfterPlaceNoHitCheckSetting = new BooleanSetting("Telly After Place No Hit Check", this, false)
        .computeBooleanSetting(() -> this.tellySetting.matchesString2("Telly"));
    private final NumberSetting searchRangeSetting = new NumberSetting("Search Range", this, 3.0, 0.0, 5.0, 1.0);
    private final BooleanSetting customTimerSetting = new BooleanSetting("Custom Timer", this, false);
    private final NumberSetting towerTimerSpeedSetting = new NumberSetting("Tower Timer Speed", this, 1.5, 0.1, 10.0, 0.01);
    private final NumberSetting normalTimerSpeedSetting = new NumberSetting("Normal Timer Speed", this, 1.5, 0.1, 10.0, 0.01);
    private final BooleanSetting keepYSetting = new BooleanSetting("KeepY", this, true);
    private final BooleanSetting keepYOnlySpeedSetting = new BooleanSetting("KeepY Only Speed", this, true);
    private final BooleanSetting intelligentPickerSetting = new BooleanSetting("Intelligent Picker", this, true);
    private final BooleanSetting noSprintSetting = new BooleanSetting("No Sprint", this, true);
    private final ModeSetting autoInputFixSetting = new ModeSetting("Edge Safe", this, "None", Arrays.asList("None", "Safewalk", "Sneak", "Auto Input Fix"));
    private final BooleanSetting downSetting = new BooleanSetting("Down", this, false);
    private final BooleanSetting jumpBoostOnlySetting = new BooleanSetting("Jump Boost Only", this, false)
        .computeBooleanSetting(() -> this.tellySetting.matchesString2("Normal"));
    private final ModeSetting bedrockSetting = new ModeSetting("Blocks", this, "Any", Arrays.asList("Any", "Obsidian", "Bedrock"));
    private final BooleanSetting blockCounterSetting = new BooleanSetting("Block Counter", this, true);
    private final List<BlockPos> entries = new ArrayList<>(64);
    private final HashSet<BlockPos> itemSet = new HashSet<>(64);
    private Scaffold.Entry2 entry2;
    private int count = Integer.MIN_VALUE;
    private boolean flag;
    private Scaffold.Entry2 entry22;
    private Scaffold.Entry3 entry3;
    private BlockPos blockPos;
    private Vec3 vec3;
    private int count2;
    private BlockPos blockPos2;
    private Integer integer;
    private Integer integer2;
    private int count3 = -1;
    private int count4;
    private int count5;
    private int count6;
    private int count7;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private boolean flag5;
    private boolean flag6;
    private boolean flag7;
    private boolean flag8;
    private boolean flag9;
    private boolean flag10;
    private boolean flag11;
    private boolean flag12;
    private boolean flag13;
    private boolean flag14;
    private boolean flag15;
    private double amount;
    private boolean flag16;
    private Scaffold.Entry4 entry4;
    private Scaffold.InactiveMode inactiveMode = Scaffold.InactiveMode.INACTIVE;
    private Scaffold.Entry4 entry42;
    private Scaffold.Entry4 entry43;
    private Scaffold.Entry4 entry44;
    private Scaffold.Entry4 entry45;
    private Scaffold.Entry4 entry46;
    private int count8 = Integer.MIN_VALUE;
    private int count9;
    private int count10;
    private float g = 180.0F;
    private float factor = 180.0F;
    private EntityPlayerSP entityPlayerSP;
    private float factor2;
    private float factor3;
    private float factor4;
    private float factor5;
    private float factor6;
    private boolean flag17;
    public int lastSlot;
    public static Scaffold instance;

    private void onEventUpdate(EventUpdate event) {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null && mc.theWorld != null) {
            this.flag7 = false;
            this.flag8 = false;
            this.flag11 = false;
            this.flag13 = false;
            this.process();
            this.count5 = player.onGround ? 0 : this.count5 + 1;
            if (player.onGround) {
                this.flag15 = this.checkState14();
            }

            this.handleEntityPlayerSP7(player);
            if (player.onGround || !this.checkState18()) {
                this.flag5 = false;
            }

            if (this.count4 <= 0) {
                this.process7();
            } else {
                if (this.integer2 == null) {
                    this.integer2 = player.inventory.currentItem;
                }

                if (this.count3 >= 0 && this.matchesItemStack(player.inventory.getStackInSlot(this.count3))) {
                    player.inventory.currentItem = this.count3;
                } else {
                    int n = this.computeInt();
                    if (n >= 0) {
                        this.handleInt(n);
                    }
                }

                this.handleEntityPlayerSP8(player);
                if (this.noSprintSetting.isEnabled() && !this.matchesEntityPlayerSP5(player)) {
                    player.setSprinting(false);
                }

                this.handleEntityPlayerSP6(player);
                this.updateTimer();
                this.handleEntityPlayerSP4(player);
                this.handleEntityPlayerSP(player);
                this.handleEntityPlayerSP2(player);
                if (!this.flag11
                    && !this.matchesEntityPlayerSP(player)
                    && (!this.jumpBoostOnlySetting.isEnabled() || player.isPotionActive(Potion.jump))
                    && this.entry2 != null
                    && this.matchesBlockPos2(this.entry2.blockPos)
                    && this.matchesEntry2(this.entry2)) {
                    this.handleEntry22(this.entry2);
                }
            }
        }
    }

    private boolean checkState6() {
        return this.checkState5() && this.nonUpwardsSetting.matchesString2("Non Upwards");
    }

    private boolean matchesBlockPos(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return !(block instanceof BlockAir) && !block.isReplaceable(mc.theWorld, pos) && !block.getMaterial().isLiquid();
    }

    private void process4() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (this.hypixelVerticalSetting.matchesString2("Vanilla") && this.checkState16()) {
                mc.thePlayer.motionY = -0.4;
            } else if (this.hypixelVerticalSetting.matchesString2("Hypixel")) {
                this.integer = MathHelper.floor_double(mc.thePlayer.posY) - 1;
            }
        }
    }

    private static Vec3 computeVec3(Scaffold.Entry4 entry4) {
        float cos = MathHelper.cos(-entry4.factor * (float) Math.PI / 180.0F - (float) Math.PI);
        float sin = MathHelper.sin(-entry4.factor * (float) Math.PI / 180.0F - (float) Math.PI);
        float f = -MathHelper.cos(-entry4.factor2 * (float) Math.PI / 180.0F);
        float sin2 = MathHelper.sin(-entry4.factor2 * (float) Math.PI / 180.0F);
        return new Vec3(sin * f, sin2, cos * f);
    }

    private static Scaffold.Entry4 computeEntry42(Scaffold.Entry4 entry4, Scaffold.Entry4 entry42, float f, float f2) {
        if (entry4 == null) {
            return entry42;
        }
        float f3 = MathHelper.wrapAngleTo180_float(entry42.factor - entry4.factor);
        float f4 = entry42.factor2 - entry4.factor2;
        float f5 = entry4.factor + MathHelper.clamp_float(f3, -f, f);
        float f6 = entry4.factor2 + MathHelper.clamp_float(f4, -f2, f2);
        return new Scaffold.Entry4(f5, MathHelper.clamp_float(f6, -90.0F, 90.0F));
    }

    private double computeDouble(BlockPos pos, BlockPos pos2, BlockPos pos3) {
        if (this.blockPos2 != null
            && this.blockPos2.getY() == pos.getY()
            && computeInt(pos, this.blockPos2) <= this.computeInt2() + 1
            && this.matchesBlockPos(this.blockPos2)) {
            int n = computeInt(this.blockPos2, pos);
            if (n <= 1) {
                return pos3.equals(this.blockPos2) ? -1.0 : 0.0;
            }
            int n2 = computeInt(this.blockPos2, pos2);
            int n3 = n2 + computeInt(pos2, pos) - n;
            return (Math.max(0, n2 - 1) + Math.max(0, n3)) * 0.25 - (pos3.equals(this.blockPos2) ? 1.0 : 0.0);
        }
        return 0.0;
    }

    private boolean matchesAxisAlignedBB(AxisAlignedBB box) {
        if (!this.matchesAxisAlignedBB2(box)) {
            return false;
        }
        BlockPos pos = new BlockPos((box.minX + box.maxX) * 0.5, (double)computeInt(this.integer.intValue()), (box.minZ + box.maxZ) * 0.5);
        if (!this.matchesBlockPos2(pos)) {
            return false;
        }
        Scaffold.Entry2 entry2 = this.computeEntry2(pos, true);
        return entry2 == null || !this.matchesEntry2(entry2);
    }

    private boolean checkState11() {
        return this.noneSetting.matchesString2("Hypixel") || this.noneSetting.matchesString2("Simple") || this.noneSetting.matchesString2("Backwards");
    }

    private boolean checkState14() {
        return matchesInt(mc.gameSettings.keyBindJump.getKeyCode());
    }

    private Scaffold.Entry2 computeEntry2(BlockPos pos) {
        Scaffold.Entry2 entry22 = this.entry22;
        int n = entry22 != null ? computeInt(pos, entry22.blockPos) : Integer.MAX_VALUE;
        if (entry22 == null
            || entry22.blockPos.getY() != pos.getY()
            || n > this.computeInt2()
            || !this.matchesBlockPos2(entry22.blockPos)
            || !this.matchesBlockPos(entry22.blockPos2)
            || this.computeVec32().squareDistanceTo(entry22.vec3) > computeDouble2() + 1.0E-4) {
            return null;
        }
        if (!matchesMovingObjectPosition(this.computeMovingObjectPosition(this.computeEntry4(entry22.vec3)), entry22.blockPos2, entry22.enumFacing)) {
            return null;
        }
        return n == entry22.count ? entry22 : new Scaffold.Entry2(entry22.blockPos, entry22.blockPos2, entry22.vec3, entry22.enumFacing, n);
    }

    private boolean checkState16() {
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

        for (int i = 0; i > -2; i--) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (this.matchesBlockPos(pos.add(j, i, k))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void process11() {
        this.entry2 = null;
        this.entry22 = null;
        this.entry3 = null;
        this.blockPos = null;
        this.vec3 = null;
        this.count2 = 0;
        this.blockPos2 = null;
        this.integer = null;
        this.integer2 = null;
        this.count3 = -1;
        this.count4 = 0;
        this.count5 = 0;
        this.count6 = 0;
        this.count7 = 0;
        this.flag2 = false;
        this.flag3 = false;
        this.flag4 = false;
        this.flag5 = false;
        this.flag6 = false;
        this.flag7 = false;
        this.flag8 = false;
        this.flag9 = false;
        this.flag10 = false;
        this.flag11 = false;
        this.flag12 = false;
        this.flag13 = false;
        this.flag14 = false;
        this.flag15 = false;
        this.amount = 0.0;
        this.entry4 = null;
        this.inactiveMode = Scaffold.InactiveMode.INACTIVE;
        this.entry42 = null;
        this.entry43 = null;
        this.entry44 = null;
        this.entry45 = null;
        this.entry46 = null;
        this.count8 = Integer.MIN_VALUE;
        this.count9 = 0;
        this.count10 = 0;
        this.g = 180.0F;
        this.factor = 180.0F;
        this.process13();
    }

    private Scaffold.Entry2 computeEntry2(BlockPos pos, Scaffold.Entry2 entry2, Scaffold.Entry2 entry22) {
        if (entry2 == null) {
            return entry22;
        }
        if (entry22 == null) {
            return entry2;
        }
        if (matchesEntry2(entry2, entry22)) {
            return entry2;
        }
        if (entry2.count != entry22.count) {
            return entry22.count < entry2.count ? entry22 : entry2;
        }
        boolean flag = this.computeMovingObjectPosition(entry2) != null;
        boolean flag2 = this.computeMovingObjectPosition(entry22) != null;
        if (flag != flag2) {
            return flag ? entry2 : entry22;
        }
        return this.computeDouble(pos, entry22) + 10.0 < this.computeDouble(pos, entry2) ? entry22 : entry2;
    }

    private static double computeDouble2() {
        double d = computeDouble();
        return d * d;
    }

    private static float computeFloat(BlockPos pos, BlockPos pos2, float f) {
        int n = pos2.getX() - pos.getX();
        int n2 = pos2.getZ() - pos.getZ();
        if (n == 0 && n2 == 0) {
            return 0.0F;
        }
        float f2 = (float)((Math.toDegrees(Math.atan2(-n, n2)) + 360.0) % 360.0);
        return Math.abs(MathHelper.wrapAngleTo180_float(f2 - f));
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.stackSize > 0 && stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            return !matchesBlock(block)
                ? false
                : this.bedrockSetting.matchesString2("Any")
                    || this.bedrockSetting.matchesString2("Obsidian") && block == Blocks.obsidian
                    || this.bedrockSetting.matchesString2("Bedrock") && block == Blocks.bedrock;
        }
        return false;
    }

    private static float computeFloat2(float f, float f2) {
        float f3 = MathHelper.clamp_float(f2, 0.0F, 180.0F);
        float f4 = !(Math.abs(f) <= 1.0F) && !(f3 >= 180.0F) ? (float)(f * f3 / Math.sqrt(f * f + f3 * f3)) : f;
        return f3 == 0.0F ? 0.0F : MathHelper.clamp_float(f4, -f3, f3);
    }

    private static double computeDouble(EnumFacing facing) {
        if (facing == EnumFacing.UP) {
            return 0.0;
        }
        return facing != EnumFacing.NORTH && facing != EnumFacing.SOUTH && facing != EnumFacing.WEST && facing != EnumFacing.EAST ? 0.1 : 0.05;
    }

    private boolean matchesBlockPos(BlockPos pos, ItemStack stack, ItemBlock item) {
        for (BlockPos pos2 : this.buildCandidates(pos)) {
            if (this.matchesBlockPos2(pos2) && !this.matchesBlockPos3(pos2)) {
                for (EnumFacing facing : this.computeEnumFacingArray(pos, pos2)) {
                    BlockPos pos3 = pos2.offset(facing);
                    EnumFacing opposite = facing.getOpposite();
                    if (this.matchesBlockPos(pos3) && item.canPlaceBlockOnSide(mc.theWorld, pos3, opposite, mc.thePlayer, stack)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkState22() {
        return this.snapRotationSetting.isEnabled() && (this.noneSetting.matchesString2("Hypixel") || this.noneSetting.matchesString2("Simple"));
    }

    private void process5() {
        this.entry4 = null;
        this.inactiveMode = Scaffold.InactiveMode.INACTIVE;
        this.entry42 = null;
        this.entry43 = null;
        this.entry44 = null;
        this.entry45 = null;
        this.entry46 = null;
        this.count8 = Integer.MIN_VALUE;
        this.count10 = 0;
    }

    private static double computeDouble(Scaffold.Entry4 entry4, Scaffold.Entry4 entry42) {
        float f = Math.abs(MathHelper.wrapAngleTo180_float(entry4.factor - entry42.factor));
        float f2 = Math.abs(entry4.factor2 - entry42.factor2);
        return Math.max(f, f2) + (f + f2) * 0.01;
    }

    private static boolean matchesInt(int n) {
        return n < 0 ? Mouse.isCreated() && Mouse.isButtonDown(n + 100) : n > 0 && Keyboard.isCreated() && Keyboard.isKeyDown(n);
    }

    private void handleEntry22(Scaffold.Entry2 entry2) {
        if (!this.checkState8() && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
            int n = this.computeInt();
            if (n >= 0 && n <= 8) {
                this.handleInt(n);
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(n);
                if (this.matchesItemStack(stack) && !this.matchesBlockPos3(entry2.blockPos)) {
                    if (this.matchesBlockPos2(entry2.blockPos) && this.matchesBlockPos(entry2.blockPos2)) {
                        Vec3 vec3 = entry2.vec3;
                        if (!this.noneSetting.matchesString2("None") && !this.matchesEntry24(entry2) && !this.matchesEntry22(entry2)) {
                            MovingObjectPosition hit = this.computeMovingObjectPosition(entry2);
                            if (hit == null) {
                                return;
                            }

                            vec3 = hit.hitVec;
                        }

                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, entry2.blockPos2, entry2.enumFacing, vec3)) {
                            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                            this.flag3 = true;
                            this.flag = true;
                            this.blockPos2 = entry2.blockPos;
                            if (this.checkState18() && !mc.thePlayer.onGround) {
                                this.flag5 = true;
                            }

                            this.entry22 = null;
                            this.entry3 = null;
                            this.blockPos = null;
                            this.vec3 = null;
                            this.count2 = 0;
                            if (this.checkState22()) {
                                this.flag4 = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public void process6() {
        this.tellySetting.handleString("Telly");
        this.towerSetting.setEnabled(false);
        this.towerCenterSetting.setEnabled(false);
        this.standingSetting.handleString("Always");
        this.hypixelVerticalSetting.handleString("Vanilla");
        this.towerSpeedSetting.handleDouble2(1.0);
        this.nonUpwardsSetting.handleString("Non Upwards");
        this.noneSetting.handleString("Hypixel");
        this.smoothRotsSetting.setEnabled(false);
        this.maxRotationSpeedSetting.handleDouble2(34.5);
        this.minRotationSpeedSetting.handleDouble2(34.5);
        this.preRotationLeadSetting.handleDouble2(2.0);
        this.snapRotationSetting.setEnabled(false);
        this.noPlaceWhenSnappingSetting.setEnabled(false);
        this.noHitCheckSetting.setEnabled(false);
        this.tellyAfterPlaceNoHitCheckSetting.setEnabled(false);
        this.searchRangeSetting.handleDouble2(3.0);
        this.customTimerSetting.setEnabled(false);
        this.towerTimerSpeedSetting.handleDouble2(1.5);
        this.normalTimerSpeedSetting.handleDouble2(1.5);
        this.keepYSetting.setEnabled(true);
        this.keepYOnlySpeedSetting.setEnabled(true);
        this.intelligentPickerSetting.setEnabled(true);
        this.noSprintSetting.setEnabled(true);
        this.autoInputFixSetting.handleString("None");
        this.downSetting.setEnabled(false);
    }

    private static float computeFloat(float f, float f2, float f3) {
        float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
        return f + MathHelper.clamp_float(f4, -f3, f3);
    }

    private boolean matchesEntry22(Scaffold.Entry2 entry2) {
        return this.noneSetting.matchesString2("Simple") || this.noneSetting.matchesString2("Backwards") && (entry2 == null || entry2.count < 2);
    }

    private static boolean matchesMovingObjectPosition(MovingObjectPosition hit, BlockPos pos, EnumFacing facing) {
        return hit != null && hit.typeOfHit == MovingObjectType.BLOCK && pos.equals(hit.getBlockPos()) && hit.sideHit == facing;
    }

    private Scaffold.Entry computeEntry(BlockPos pos, EnumFacing facing) {
        Vec3 vec = this.computeVec32();
        Vec3 vec2 = computeVec3(pos, facing);
        Scaffold.Entry4 entry4 = this.computeEntry42();
        Scaffold.Entry4 entry42 = this.computeEntry4(vec2);
        MovingObjectPosition hit = this.computeMovingObjectPosition(entry4);
        Scaffold.Entry entry = matchesMovingObjectPosition(hit, pos, facing) ? new Scaffold.Entry(hit.hitVec, 0.0) : null;
        if (matchesMovingObjectPosition(hit, pos, facing) && computeDouble(entry42, entry4) <= 6.0) {
            return entry;
        }
        MovingObjectPosition hit2 = this.computeMovingObjectPosition(entry42);
        if (matchesMovingObjectPosition(hit2, pos, facing)) {
            return new Scaffold.Entry(hit2.hitVec, this.computeDouble(hit2.hitVec, entry42, entry4, vec2, facing, entry != null));
        }
        Vec3 vec3 = null;
        double MAX_VALUE = Double.MAX_VALUE;
        int n = computeInt(facing, EnumFacing.EAST, EnumFacing.WEST);
        int n2 = computeInt(facing, EnumFacing.UP, EnumFacing.DOWN);
        int n3 = computeInt(facing, EnumFacing.SOUTH, EnumFacing.NORTH);

        for (int i = 0; i < n; i++) {
            double d = computeDouble(facing, EnumFacing.EAST, EnumFacing.WEST, i);

            for (int j = 0; j < n2; j++) {
                double d2 = computeDouble(facing, EnumFacing.UP, EnumFacing.DOWN, j);

                for (int k = 0; k < n3; k++) {
                    double d3 = computeDouble(facing, EnumFacing.SOUTH, EnumFacing.NORTH, k);
                    Vec3 vec4 = new Vec3(pos.getX() + d, pos.getY() + d2, pos.getZ() + d3);
                    if (!(vec.squareDistanceTo(vec4) > computeDouble2())) {
                        Scaffold.Entry4 entry43 = this.computeEntry4(vec4);
                        MovingObjectPosition hit3 = this.computeMovingObjectPosition(entry43);
                        if (matchesMovingObjectPosition(hit3, pos, facing)) {
                            double d4 = this.computeDouble(hit3.hitVec, entry43, entry4, vec2, facing, entry != null);
                            if (d4 < MAX_VALUE) {
                                MAX_VALUE = d4;
                                vec3 = hit3.hitVec;
                            }
                        }
                    }
                }
            }
        }

        return vec3 != null ? new Scaffold.Entry(vec3, MAX_VALUE) : entry;
    }

    private void handleBlockPos(BlockPos pos) {
        if (this.itemSet.add(pos)) {
            this.entries.add(pos);
        }
    }

    private Scaffold.Entry2 computeEntry22(BlockPos pos) {
        if (!this.noneSetting.matchesString2("Hypixel")) {
            return null;
        }
        Vec3 vec = this.computeVec3();
        double dist = Math.sqrt(vec.xCoord * vec.xCoord + vec.zCoord * vec.zCoord);
        if (dist <= 1.0E-6) {
            return null;
        }
        double d = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
        double d2 = MathHelper.clamp_double(Math.max(0.35, d * 2.0 + 0.15), 0.35, 0.85);
        double d3 = d2 * MathHelper.clamp_double(this.preRotationLeadSetting.computeDouble(), 0.0, 2.0);
        double d4 = mc.thePlayer.posX + vec.xCoord / dist * d3;
        double d5 = mc.thePlayer.posZ + vec.zCoord / dist * d3;
        BlockPos pos2 = new BlockPos(d4, (double)computeInt(this.integer.intValue()), d5);
        return !pos2.equals(pos) && this.matchesBlockPos2(pos2) ? this.computeEntry2(pos2, true) : null;
    }

    private boolean matchesEntityPlayerSP10(EntityPlayerSP player) {
        return !this.checkState14() ? false : !this.tellySetting.matchesString2("Telly") || player == null || player.onGround || this.flag15;
    }

    private Scaffold.Entry4 computeEntry4(Scaffold.Entry2 entry2) {
        float f = entry2 != null ? this.computeEntry4(entry2.vec3).factor2 : 85.0F;
        return new Scaffold.Entry4(MathHelper.wrapAngleTo180_float(this.computeFloat4() - 180.0F), f);
    }

    private boolean matchesEntry2(Scaffold.Entry2 entry2) {
        if (this.noneSetting.matchesString2("None") || this.matchesEntry24(entry2)) {
            return true;
        }
        if (this.inactiveMode == Scaffold.InactiveMode.INACTIVE || this.count10 < 2) {
            return false;
        }
        if (this.matchesEntry22(entry2)) {
            Scaffold.Entry4 entry4 = this.noneSetting.matchesString2("Simple") ? this.computeEntry4() : this.computeEntry4(entry2);
            Scaffold.Entry4 entry42 = this.computeEntry42();
            return Math.abs(MathHelper.wrapAngleTo180_float(entry42.factor - entry4.factor)) <= 5.0F && Math.abs(entry42.factor2 - entry4.factor2) <= 5.0F;
        }
        return this.computeMovingObjectPosition(entry2) != null;
    }

    private Scaffold.Entry4 computeEntry4(Vec3 vec) {
        Vec3 vec2 = this.computeVec32();
        double d = vec.xCoord - vec2.xCoord;
        double d2 = vec.yCoord - vec2.yCoord;
        double d3 = vec.zCoord - vec2.zCoord;
        double dist = Math.sqrt(d * d + d3 * d3);
        float deg = (float)Math.toDegrees(Math.atan2(-d, d3));
        float f = (float)(-Math.toDegrees(Math.atan2(d2, dist)));
        return new Scaffold.Entry4(MathHelper.wrapAngleTo180_float(deg), MathHelper.clamp_float(f, -90.0F, 90.0F));
    }

    public static Float computeFloatType(Entity entity, float f) {
        if (entity == mc.thePlayer && entity instanceof EntityPlayerSP && entity.ridingEntity == null) {
            Scaffold scaffold = instance;
            if (scaffold != null && scaffold.checkState44()) {
                float f2 = MathHelper.clamp_float(f, 0.0F, 1.0F);
                if (scaffold.flag17 && scaffold.entityPlayerSP == entity && scaffold.smoothRotsSetting.isEnabled() && scaffold.noneSetting.matchesString2("Hypixel")) {
                    return scaffold.factor3 + MathHelper.wrapAngleTo180_float(scaffold.factor2 - scaffold.factor3) * f2;
                }
                if (scaffold.inactiveMode != Scaffold.InactiveMode.INACTIVE && scaffold.entry43 != null) {
                    EntityPlayerSP player = (EntityPlayerSP)entity;
                    return player.prevRotationYawHead + MathHelper.wrapAngleTo180_float(scaffold.entry43.factor - player.prevRotationYawHead) * f2;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private void process7() {
        this.process10();
        this.process3();
        this.entry2 = null;
        this.entry22 = null;
        this.entry3 = null;
        this.blockPos = null;
        this.vec3 = null;
        this.count2 = 0;
        this.count6 = 0;
        this.count7 = 0;
        this.flag3 = false;
        this.blockPos2 = null;
        this.flag4 = false;
        this.flag5 = false;
        this.flag6 = false;
        this.flag7 = false;
        this.flag8 = false;
        this.flag9 = false;
        this.flag10 = false;
        this.flag15 = false;
        this.entry4 = null;
        this.process5();
        if (this.flag16) {
            mc.timer.timerSpeed = 1.0F;
            this.flag16 = false;
        }
    }

    private static boolean onMovementEvent54(MovementEvent5 movementEvent5) {
        return movementEvent5.getFactor() != 0.0F || movementEvent5.getFactor2() != 0.0F;
    }

    private void process8() {
        if (mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), this.checkState14());
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), this.checkState12());
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), matchesInt(mc.gameSettings.keyBindSprint.getKeyCode()));
        }
    }

    private List<BlockPos> buildCandidates(BlockPos pos) {
        this.entries.clear();
        this.itemSet.clear();
        this.handleBlockPos(pos);
        int n = this.computeInt2();
        EnumFacing[] facings = this.orderedHorizontalDirections(this.computeFloat5());
        if (n >= 1) {
            for (EnumFacing facing : facings) {
                this.handleBlockPos(pos.offset(facing));
            }
        }

        if (n >= 2) {
            for (EnumFacing facing2 : facings) {
                for (EnumFacing facing3 : facings) {
                    if (facing3 != facing2.getOpposite()) {
                        this.handleBlockPos(pos.offset(facing2).offset(facing3));
                    }
                }
            }
        }

        for (int i = 3; i <= n; i++) {
            ArrayList<BlockPos> list = new ArrayList<>();

            for (int j = -i; j <= i; j++) {
                for (int k = -i; k <= i; k++) {
                    if (Math.abs(j) + Math.abs(k) == i) {
                        list.add(pos.add(j, 0, k));
                    }
                }
            }

            float f = this.computeFloat5();
            Collections.sort(list, (pos2, pos3) -> {
                int n2 = Float.compare(computeFloat(pos, pos2, f), computeFloat(pos, pos3, f));
                if (n2 != 0) {
                    return n2;
                }
                n2 = Integer.compare(Math.abs(pos2.getX() - pos.getX()), Math.abs(pos3.getX() - pos.getX()));
                return n2 != 0 ? n2 : Integer.compare(Math.abs(pos2.getZ() - pos.getZ()), Math.abs(pos3.getZ() - pos.getZ()));
            });

            for (BlockPos pos4 : list) {
                this.handleBlockPos(pos4);
            }
        }

        return this.entries;
    }

    private float computeFloat2() {
        float f = (float)Math.min(this.minRotationSpeedSetting.computeDouble(), this.maxRotationSpeedSetting.computeDouble());
        float f2 = (float)Math.max(this.minRotationSpeedSetting.computeDouble(), this.maxRotationSpeedSetting.computeDouble());
        return f + (float)Math.random() * (f2 - f);
    }

    private static Scaffold.Entry4 computeEntry4(Scaffold.Entry4 entry4, Scaffold.Entry4 entry42, float f, float f2) {
        float f3 = MathHelper.wrapAngleTo180_float(entry42.factor - entry4.factor);
        float f4 = entry42.factor2 - entry4.factor2;
        return new Scaffold.Entry4(entry4.factor + computeFloat2(f3, f), MathHelper.clamp_float(entry4.factor2 + computeFloat2(f4, f2), -90.0F, 90.0F));
    }

    private int computeInt2() {
        if (this.checkState26()) {
            return 1;
        }
        return this.checkState14()
                && Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) <= 1.0E-4
                && this.matchesBlockPos(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ))
            ? 0
            : (int)this.searchRangeSetting.computeDouble();
    }

    private boolean matchesEntityPlayerSP5(EntityPlayerSP player) {
        return this.matchesEntityPlayerSP(player) && !this.matchesEntityPlayerSP6(player) || this.flag7;
    }

    private boolean checkState12() {
        return matchesInt(mc.gameSettings.keyBindSneak.getKeyCode());
    }

    @Override
    public String getSuffix() {
        return this.tellySetting.getText();
    }

    private void handleEntityPlayerSP8(EntityPlayerSP player) {
        if (!this.tellySetting.matchesString2("Telly") || !this.checkState9() || this.count4 <= 0 || this.matchesEntityPlayerSP9(player)) {
            this.count6 = 0;
            this.count7 = 0;
            this.flag9 = false;
            this.flag8 = false;
        } else if (player.onGround) {
            if (this.count6 > 0) {
                this.count6--;
            } else if (this.count6 < 0) {
                this.count6++;
            }

            if (this.count6 == 0) {
                this.count6 = 1;
            }

            this.handleEntry2(null);
            if (this.flag9 && player.onGround && !this.checkState14()) {
                this.flag9 = false;
            }
        }
    }

    private Scaffold.Entry3 findRaycastRotation(BlockPos pos) {
        int n = this.computeInt();
        if (n >= 0 && n <= 8) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(n);
            if (!this.matchesItemStack(stack)) {
                return null;
            }
            ItemBlock item = (ItemBlock)stack.getItem();
            Scaffold.Entry3 entry3 = this.computeEntry3(pos, stack, item);
            if (entry3 != null) {
                return entry3;
            }
            if (pos.equals(this.blockPos)
                && this.vec3 != null
                && this.vec3.squareDistanceTo(this.computeVec32()) <= 0.0025
                && mc.thePlayer.ticksExisted < this.count2) {
                return null;
            }
            if (!this.matchesBlockPos(pos, stack, item)) {
                this.handleBlockPos(pos, 1);
                return null;
            }
            Scaffold.Entry4 entry4 = this.computeEntry42();
            float f = computeFloat(entry4.factor, this.computeFloat5(), 175.0F);
            float f2 = entry4.factor2;
            Scaffold.Entry2 entry2 = null;
            Scaffold.Entry4 entry42 = null;
            double MAX_VALUE = Double.MAX_VALUE;
            float MAX_VALUE2 = Float.MAX_VALUE;
            float MAX_VALUE3 = Float.MAX_VALUE;
            ArrayList<Float> list = new ArrayList<>(180);

            for (float i = -180.0F; i < 180.0F; i += 2.0F) {
                list.add(i);
            }

            Collections.sort(list, (val, val2) -> Float.compare(computeFloat(f, val), computeFloat(f, val2)));

            for (float f3 : list) {
                float f4 = computeFloat(f, f3);
                if (MAX_VALUE == 0.0 && f4 > MAX_VALUE2) {
                    break;
                }

                for (float j = 30.0F; j < 90.0F; j += 2.0F) {
                    float f5 = Math.abs(f2 - j);
                    Scaffold.Entry4 entry43 = new Scaffold.Entry4(f3, j);
                    MovingObjectPosition hit = this.computeMovingObjectPosition(entry43);
                    Scaffold.Entry2 entry22 = this.computeEntry2(pos, hit, stack, item);
                    if (entry22 != null) {
                        double d = entry22.blockPos.getX() - pos.getX();
                        double d2 = entry22.blockPos.getY() - pos.getY();
                        double d3 = entry22.blockPos.getZ() - pos.getZ();
                        double dist = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                        boolean flag = dist < MAX_VALUE
                            || Double.compare(dist, MAX_VALUE) == 0 && (f4 < MAX_VALUE2 || Float.compare(f4, MAX_VALUE2) == 0 && f5 < MAX_VALUE3);
                        if (flag) {
                            MAX_VALUE = dist;
                            MAX_VALUE2 = f4;
                            MAX_VALUE3 = f5;
                            entry2 = entry22;
                            entry42 = new Scaffold.Entry4(f3, j);
                        }
                    }
                }
            }

            if (entry2 == null) {
                this.handleBlockPos(pos, 3);
                this.entry3 = null;
            } else {
                this.blockPos = null;
                this.vec3 = null;
                this.count2 = 0;
                this.entry3 = new Scaffold.Entry3(pos, entry2, entry42);
            }

            return this.entry3;
        }
        return null;
    }

    private static Vec3 computeVec3(BlockPos pos, EnumFacing facing) {
        return computeVec3(pos).addVector(facing.getFrontOffsetX() * 0.5, facing.getFrontOffsetY() * 0.5, facing.getFrontOffsetZ() * 0.5);
    }

    private void handleBlockPos(BlockPos pos, Scaffold.Entry2 entry2, Scaffold.Entry2 entry22, boolean flag) {
        if (this.noneSetting.matchesString2("None")) {
            this.process5();
        } else if (this.checkState()) {
            this.handleEntry2(entry2 != null ? entry2 : entry22);
        } else if (this.noneSetting.matchesString2("Raycast")) {
            Scaffold.Entry3 entry3 = flag ? this.findRaycastRotation(pos) : null;
            this.entry2 = entry3 != null ? entry3.entry2 : null;
            if (entry3 != null) {
                this.handleEntry42(entry3.entry4);
            } else {
                this.process5();
            }
        } else if (this.noneSetting.matchesString2("Simple")) {
            if (!this.checkState22() || flag && entry2 != null) {
                this.handleEntry4(this.computeEntry4());
            }
        } else if (this.noneSetting.matchesString2("Backwards")) {
            if (flag && entry2 != null && entry2.count >= 2) {
                this.handleEntry23(entry2);
            } else {
                this.handleEntry4(this.computeEntry4(entry2));
            }
        } else {
            if (flag && entry2 != null) {
                this.handleEntry23(entry2);
            } else if (entry22 != null) {
                this.handleEntry23(entry22);
            } else if (!this.flag3 && !this.checkState22()) {
                this.handleEntry4(this.computeEntry4());
            }
        }
    }

    private int computeInt() {
        int n = -1;
        int n2 = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (this.matchesItemStack(stack)) {
                if (!this.intelligentPickerSetting.isEnabled()) {
                    return i;
                }

                if (stack.stackSize > n2) {
                    n2 = stack.stackSize;
                    n = i;
                }
            }
        }

        return n;
    }

    private float computeFloat3() {
        return !this.tellySetting.matchesString2("Telly") || !this.noneSetting.matchesString2("Simple") && !this.noneSetting.matchesString2("Backwards")
            ? this.computeFloat4()
            : MathHelper.wrapAngleTo180_float(this.computeFloat4());
    }

    private void handleBlockPos(BlockPos blockPos, int n) {
        this.blockPos = blockPos;
        this.vec3 = this.computeVec32();
        this.count2 = mc.thePlayer.ticksExisted + n;
    }

    private boolean checkState17() {
        if (!this.flag4) {
            return false;
        }
        this.flag4 = false;
        return this.noPlaceWhenSnappingSetting.isEnabled();
    }

    private void handleEntry2(Scaffold.Entry2 entry2) {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null && !this.noneSetting.matchesString2("None") && !this.matchesEntityPlayerSP3(player)) {
            float f = this.computeFloat3();
            Scaffold.Entry4 entry4 = this.computeEntry42();
            if (this.noneSetting.matchesString2("Raycast")) {
                float f2 = (float)ThreadLocalRandom.current().nextDouble(85.0, 90.0);
                this.handleEntry4(new Scaffold.Entry4(f, f2), Scaffold.InstantMode.INSTANT.computeFloat2(), Scaffold.InstantMode.INSTANT.computeFloat());
            } else if (this.noneSetting.matchesString2("Hypixel") && !this.checkState22()) {
                float f3 = entry2 != null ? this.computeEntry4(entry2.vec3).factor2 : this.computeEntry4().factor2;
                this.handleEntry4(new Scaffold.Entry4(f, f3), 180.0F, this.computeFloat2());
            } else {
                this.handleEntry4(new Scaffold.Entry4(f, entry4.factor2), 180.0F, 180.0F);
            }
        }
    }

    private void onEventRender2D(EventRender2D event) {
        if (this.blockCounterSetting.isEnabled()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                ScaledResolution scaledResolution = event.getScaledResolution();
                int scaledWidth = scaledResolution.getScaledWidth();
                int scaledHeight = scaledResolution.getScaledHeight();
                ItemStack stack = this.computeItemStack();
                String str = "Blocks: " + this.count4;
                byte b = 16;
                int n = 4;
                int textWidth = computeFontRenderer(Scaffold.class).getStringWidth(str);
                int n2 = (stack != null ? b + n : 0) + textWidth;
                int n3 = scaledWidth / 2 - n2 / 2;
                int n4 = scaledHeight - 64;
                if (stack != null) {
                    mc.getRenderItem().renderItemIntoGUI(stack, n3, n4 - 4);
                }

                computeFontRenderer(Scaffold.class).drawStringWithShadow(str, n3 + (stack != null ? b + n : 0), n4, -1);
            }
        }
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (this.inactiveMode != Scaffold.InactiveMode.INACTIVE && this.entry43 != null && this.entry45 != null) {
            if (this.inactiveMode == Scaffold.InactiveMode.RETURNING) {
                this.entry43 = computeEntry42(this.entry43, this.entry42, this.g, this.factor);
            }

            float factor = this.entry43.factor;

            while (factor - this.entry45.factor > 180.0F) {
                factor -= 360.0F;
            }

            while (factor - this.entry45.factor < -180.0F) {
                factor += 360.0F;
            }

            float f = factor - this.entry45.factor;
            float f2 = this.entry43.factor2 - this.entry45.factor2;
            Scaffold.Entry4 entry4 = new Scaffold.Entry4(this.entry45.factor + f, this.entry45.factor2 + f2);
            float[] floats = OnJumpHelper.computeFloatArray(new float[]{entry4.factor, entry4.factor2}, new float[]{this.entry45.factor, this.entry45.factor2});
            if (!Float.isNaN(floats[0]) && !Float.isNaN(floats[1])) {
                event.setYaw(floats[0]);
                event.setPitch(floats[1]);
            } else {
                event.setYaw(this.entry43.factor);
                event.setPitch(this.entry43.factor2);
            }

            mc.thePlayer.rotationYawHead = this.entry43.factor;
            mc.thePlayer.rotationPitchHead = this.entry43.factor2;
            if (this.inactiveMode == Scaffold.InactiveMode.AIMING && mc.thePlayer.ticksExisted - this.count9 > 20) {
                this.inactiveMode = Scaffold.InactiveMode.RETURNING;
                this.entry44 = new Scaffold.Entry4(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                this.entry42 = this.entry44;
                this.g = 40.0F;
                this.factor = 40.0F;
            } else if (this.inactiveMode == Scaffold.InactiveMode.RETURNING && matchesEntry4(this.entry43, this.entry44, 0.1F)) {
                this.inactiveMode = Scaffold.InactiveMode.INACTIVE;
                this.entry42 = null;
                this.entry43 = null;
                this.entry44 = null;
                this.count10 = 0;
            }

            this.entry45 = new Scaffold.Entry4(event.getYaw(), event.getPitch());
            this.count10++;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                    this.process28();
                } else if (!(event instanceof BasicEvent2) && !(event instanceof BasicEvent4)) {
                    if (event instanceof EventPostMotion) {
                        this.flag = false;
                    }

                    if (this.checkState8()) {
                        this.entry2 = null;
                        this.process5();
                        this.process10();
                        if (this.flag16 && mc.timer != null) {
                            mc.timer.timerSpeed = 1.0F;
                        }

                        this.flag16 = false;
                    } else {
                        if (event instanceof EventUpdate event2) {
                            if (event2.getState() == EventState.PRE) {
                                this.onEventUpdate(event2);
                            }
                        } else if (event instanceof MovementEvent5) {
                            this.onMovementEvent55((MovementEvent5)event);
                            this.onMovementEvent52((MovementEvent5)event);
                        } else if (event instanceof MovementEvent8) {
                            if (this.checkState4() || this.noSprintSetting.isEnabled() && !this.matchesEntityPlayerSP5(mc.thePlayer)) {
                                ((MovementEvent8)event).process2();
                            }
                        } else if (event instanceof EventPreMotion event3) {
                            if (event3.getState() == EventState.PRE) {
                                this.onEventPreMotion(event3);
                            }
                        } else if (event instanceof EventRender2D) {
                            this.onEventRender2D((EventRender2D)event);
                        }
                    }
                } else {
                    event.cancel();
                }
            }
        }
    }

    private boolean matchesEntityPlayerSP6(EntityPlayerSP player) {
        return this.towerSetting.isEnabled()
            && this.tellySetting.matchesString2("Telly")
            && player != null
            && player.isCollidedHorizontally
            && this.checkState14()
            && this.checkState9();
    }

    private Vec3 computeVec32() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    private EnumFacing[] computeEnumFacingArray(BlockPos pos, BlockPos pos2) {
        List<EnumFacing> facings = new ArrayList<>();
        EnumFacing facing = this.computeEnumFacing(pos, pos2);
        if (facing != null) {
            facings.add(facing);
        }

        for (EnumFacing facing2 : this.orderedHorizontalDirections(this.computeFloat5())) {
            if (facing2 != facing) {
                facings.add(facing2);
            }
        }

        facings.add(EnumFacing.DOWN);
        facings.add(EnumFacing.UP);
        return facings.toArray(new EnumFacing[0]);
    }

    public Scaffold() {
        super("Scaffold", 0, Category.MOVEMENT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.tellySetting});
        this.handleSettingArray(
            new Setting[]{
                this.towerSetting,
                this.towerCenterSetting,
                this.standingSetting,
                this.hypixelVerticalSetting,
                this.towerSpeedSetting,
                this.nonUpwardsSetting,
                this.noneSetting,
                this.veryLowSetting,
                this.smoothRotsSetting,
                this.linearSetting,
                this.maxRotationSpeedSetting,
                this.minRotationSpeedSetting,
                this.preRotationLeadSetting,
                this.snapRotationSetting,
                this.noPlaceWhenSnappingSetting,
                this.noHitCheckSetting,
                this.tellyAfterPlaceNoHitCheckSetting,
                this.searchRangeSetting,
                this.customTimerSetting,
                this.towerTimerSpeedSetting,
                this.normalTimerSpeedSetting,
                this.keepYSetting,
                this.keepYOnlySpeedSetting,
                this.intelligentPickerSetting,
                this.noSprintSetting,
                this.autoInputFixSetting,
                this.downSetting,
                this.jumpBoostOnlySetting,
                this.bedrockSetting,
                this.blockCounterSetting
            }
        );
        this.tellySetting.setCallback(() -> {
            this.entry22 = null;
            this.entry3 = null;
            this.entry2 = null;
            this.process5();
            this.process13();
        });
        this.noneSetting.setCallback(() -> {
            this.entry22 = null;
            this.entry3 = null;
            this.blockPos = null;
            this.vec3 = null;
            this.count2 = 0;
            this.entry4 = null;
            this.process5();
        });
        instance = this;
    }

    public static ItemStack computeItemStack(Entity entity, ItemStack stack) {
        if (!(entity instanceof EntityPlayerSP)) {
            return stack;
        }
        Scaffold scaffold = instance;
        return scaffold != null && scaffold.checkState44() && scaffold.integer2 != null && scaffold.integer2 >= 0 && scaffold.integer2 <= 8 && scaffold.count3 >= 0
            ? ((EntityPlayerSP)entity).inventory.getStackInSlot(scaffold.integer2)
            : stack;
    }

    private void handleEntityPlayerSP(EntityPlayerSP player, boolean flag) {
        double d = Math.floor(player.posY);
        double d2 = MathHelper.clamp_double(0.32 + this.towerSpeedSetting.computeDouble() * 0.1, 0.33, 0.42);
        if (this.amount == 0.0 || player.onGround || d < this.amount) {
            this.amount = d;
            if (player.onGround) {
                player.motionY = d2;
            }
        } else if (d > this.amount) {
            this.amount = d;
            player.motionY = d2;
        }

        if (flag) {
            double d3 = Math.hypot(player.motionX, player.motionZ);
            double d4 = 0.18;
            if (player.isPotionActive(Potion.moveSpeed)) {
                d4 *= 1.0 + 0.2 * (player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
            }

            if (!(d3 <= d4) && !(d3 <= 1.0E-6)) {
                double d5 = d4 / d3;
                player.motionX *= d5;
                player.motionZ *= d5;
            }
        }
    }

    private double computeDouble(Vec3 vec, Scaffold.Entry4 entry4, Scaffold.Entry4 entry42, Vec3 vec2, EnumFacing facing, boolean flag) {
        double d = computeDouble(vec, vec2, facing);
        double d2 = computeDouble(entry4, entry42) + this.computeVec32().squareDistanceTo(vec) * 0.001 + d * 0.02;
        if (flag) {
            d2 += d * 1.5;
        }

        return d2;
    }

    private float computeFloat() {
        float f = (float)Math.min(this.minRotationSpeedSetting.computeDouble(), this.maxRotationSpeedSetting.computeDouble());
        float f2 = Math.min((float)Math.max(this.minRotationSpeedSetting.computeDouble(), this.maxRotationSpeedSetting.computeDouble()), 18.0F);
        if (f2 <= 0.0F) {
            return 0.0F;
        }
        float f3;
        if (f > 18.0F) {
            f3 = Math.min(12.0F, f2);
        } else {
            f3 = Math.min(f, f2);
        }

        f3 = Math.max(1.0F, Math.min(f3, f2));
        return f3 + (float)Math.random() * (f2 - f3);
    }

    private boolean matchesBlockPos2(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock().isReplaceable(mc.theWorld, pos);
    }

    private static float computeFloat(float f, float f2) {
        return Math.abs(MathHelper.wrapAngleTo180_float(f - f2));
    }

    private void handleEntry4(Scaffold.Entry4 entry4) {
        this.handleEntry4(entry4, this.computeFloat2(), this.computeFloat2());
    }

    private Scaffold.Entry2 computeEntry2(BlockPos pos, boolean flag) {
        if (this.noneSetting.matchesString2("Hypixel") && !this.checkState20()) {
            Scaffold.Entry2 entry2 = flag ? this.computeEntry2(pos) : null;
            Scaffold.Entry2 entry22 = null;
            double MAX_VALUE = Double.MAX_VALUE;

            for (BlockPos pos2 : this.buildCandidates(pos)) {
                int n = computeInt(pos, pos2);
                if (entry22 != null && n > entry22.count) {
                    break;
                }

                Scaffold.Entry2 entry23 = this.computeEntry2(pos, pos2, n, true);
                if (entry23 != null) {
                    double d = this.computeDouble(pos, entry23);
                    if (d < MAX_VALUE) {
                        entry22 = entry23;
                        MAX_VALUE = d;
                    }
                }
            }

            Scaffold.Entry2 entry24 = this.computeEntry2(pos, entry2, entry22);
            if (flag) {
                this.entry22 = entry24;
            }

            return entry24;
        }
        if (flag) {
            this.entry22 = null;
        }

        for (BlockPos pos3 : this.buildCandidates(pos)) {
            Scaffold.Entry2 entry25 = this.computeEntry2(pos, pos3, computeInt(pos, pos3), false);
            if (entry25 != null) {
                return entry25;
            }
        }

        return null;
    }

    public static void handlePacket(Packet packet) {
        if (instance != null && mc.thePlayer != null) {
            if (packet instanceof C03PacketPlayer) {
                instance.flag = false;
            }

            if (packet instanceof C0EPacketClickWindow
                || packet instanceof C0DPacketCloseWindow
                || packet instanceof C16PacketClientStatus && ((C16PacketClientStatus)packet).getStatus() == EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                instance.count = mc.thePlayer.ticksExisted;
            }
        }
    }

    private static boolean matchesEntityPlayerSP4(EntityPlayerSP player) {
        return (player.getFoodStats().getFoodLevel() > 6.0F || player.capabilities.allowFlying)
            && !player.isUsingItem()
            && !player.isPotionActive(Potion.blindness)
            && !player.isCollidedHorizontally
            && player.movementInput.moveForward >= 0.8F;
    }

    public static boolean checkState19() {
        return instance != null && instance.checkState44();
    }

    private void handleEntityPlayerSP4(EntityPlayerSP player) {
        boolean flag = this.keepYSetting.isEnabled()
            && this.tellySetting.matchesString2("Telly")
            && player.onGround
            && this.checkState13()
            && !this.matchesEntityPlayerSP9(player)
            && !this.flag10;
        this.flag10 = player.onGround;
        if (this.tellySetting.matchesString2("Telly") && this.matchesEntityPlayerSP10(player) && !this.flag2) {
            int n = MathHelper.floor_double(player.posY);
            this.integer = !player.onGround && this.integer != null ? Math.max(this.integer, n) : n;
        } else if (flag) {
            this.integer = MathHelper.floor_double(player.posY);
        } else if (!this.matchesEntityPlayerSP8(player)) {
            if (this.keepYSetting.isEnabled()) {
                if (this.keepYOnlySpeedSetting.isEnabled() && (!this.checkState15() || this.matchesEntityPlayerSP10(player)) && (this.flag2 || this.count5 >= 4)) {
                    this.integer = MathHelper.floor_double(player.posY);
                }
            } else if (this.flag2 || this.count5 >= 4) {
                this.integer = MathHelper.floor_double(player.posY);
            }
        }

        if (this.integer == null || Math.abs(player.posY - this.integer.intValue()) > Math.sqrt(15.0)) {
            this.integer = MathHelper.floor_double(player.posY);
        }

        if (this.checkState26() && !this.flag2) {
            this.integer = MathHelper.floor_double(player.posY) - 1;
        }
    }

    private static int computeInt(EnumFacing facing, EnumFacing facing2, EnumFacing facing3) {
        return facing != facing2 && facing != facing3 ? 16 : 1;
    }

    private boolean checkState23() {
        return this.flag5 && this.checkState18() && mc.thePlayer != null && !mc.thePlayer.onGround;
    }

    private void handleEntityPlayerSP5(EntityPlayerSP player) {
        if (this.tellySetting.matchesString2("Telly") && player != null) {
            if (player.onGround) {
                this.flag15 = this.checkState14();
            }
        } else {
            this.flag15 = false;
        }
    }

    private boolean checkState26() {
        return this.downSetting.isEnabled() && this.checkState12();
    }

    private boolean checkState() {
        EntityPlayerSP player = mc.thePlayer;
        if (this.matchesEntityPlayerSP9(player)) {
            return false;
        }
        return this.matchesEntityPlayerSP(player)
            ? !this.flag2
            : player != null && this.tellySetting.matchesString2("Telly") && this.checkState9() && this.count4 > 0 && this.count5 <= 0 && !this.flag2;
    }

    private void updateTimer() {
        if (this.towerSetting.isEnabled() && this.hypixelVerticalSetting.matchesString2("HypixelVertical")) {
            if (this.flag16) {
                mc.timer.timerSpeed = 1.0F;
            }

            this.flag16 = false;
        } else {
            if (this.customTimerSetting.isEnabled()) {
                mc.timer.timerSpeed = (float)(this.flag2 ? this.towerTimerSpeedSetting.computeDouble() : this.normalTimerSpeedSetting.computeDouble());
                this.flag16 = true;
            } else if (this.flag16) {
                mc.timer.timerSpeed = 1.0F;
                this.flag16 = false;
            }
        }
    }

    private void handleEntityPlayerSP3(EntityPlayerSP player) {
        if (this.checkState5() && !this.matchesEntityPlayerSP9(player)) {
            if (this.flag9 && player != null && player.onGround && !this.checkState6()) {
                this.flag9 = false;
            }
        } else {
            this.count7 = 0;
            this.flag9 = false;
        }
    }

    private boolean matchesEntityPlayerSP7(EntityPlayerSP player) {
        return this.towerSetting.isEnabled()
            && this.checkState14()
            && !this.checkState9()
            && mc.currentScreen == null
            && mc.inGameHasFocus
            && Math.hypot(player.motionX, player.motionZ) <= 0.01
            && !player.isCollidedHorizontally
            && !player.isInWater()
            && !player.isInLava()
            && this.count4 > 0;
    }

    private void handleFlag(boolean flag) {
        if (this.flag2) {
            this.process4();
        }

        this.flag14 = false;
        this.flag2 = false;
        this.amount = 0.0;
        if (flag) {
            this.flag12 = false;
        }
    }

    private boolean matchesEntry23(Scaffold.Entry2 entry2) {
        if (!this.noneSetting.matchesString2("Backwards")) {
            return false;
        }
        float factor = this.computeEntry4(entry2.vec3).factor;
        float factor2 = this.computeEntry42().factor;
        return Math.abs(MathHelper.wrapAngleTo180_float(factor - factor2)) >= 90.0F;
    }

    private static double computeDouble(EnumFacing facing, EnumFacing facing2, EnumFacing facing3, int n) {
        if (facing == facing2) {
            return 1.0;
        }
        return facing == facing3 ? 0.0 : ((n << 1) + 1) * 0.03125;
    }

    private void onMovementEvent52(MovementEvent5 movementEvent5) {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null && mc.theWorld != null) {
            this.handleMovementEvent5(movementEvent5, player);
            if (this.flag14) {
                movementEvent5.setJump(false);
            }

            if (this.autoInputFixSetting.matchesString2("Auto Input Fix") && this.onMovementEvent5(movementEvent5)) {
                movementEvent5.setFactor(0.0F);
                movementEvent5.setFactor2(0.0F);
            }
        }
    }

    private void handleEntry23(Scaffold.Entry2 entry2) {
        Scaffold.Entry4 entry4 = this.computeEntry4(entry2.vec3);
        float f = this.computeFloat2();
        float f2 = this.noneSetting.matchesString2("Hypixel") && !this.checkState26() ? this.computeFloat() : f;
        this.handleEntry4(entry4, f, f2);
    }

    private boolean matchesEntityPlayerSP3(EntityPlayerSP player) {
        return this.matchesEntityPlayerSP2(player) || this.flag8 && this.checkState5() && player != null;
    }

    private boolean matchesEntityPlayerSP9(EntityPlayerSP player) {
        if (this.tellySetting.matchesString2("Telly") && player != null) {
            AxisAlignedBB box = player.getEntityBoundingBox().offset(0.0, 0.42, 0.0);
            boolean flag = player.onGround && !mc.theWorld.getCollidingBoundingBoxes(player, box).isEmpty();
            return flag || this.flag6 && !player.onGround;
        }
        return false;
    }

    private void handleMovementEvent5(MovementEvent5 movementEvent5, EntityPlayerSP player) {
        if (this.tellySetting.matchesString2("Telly")) {
            this.handleEntityPlayerSP5(player);
            this.handleEntityPlayerSP7(player);
            this.handleEntityPlayerSP3(player);
            if (!this.matchesEntityPlayerSP9(player)) {
                if (this.matchesEntityPlayerSP6(player)) {
                    movementEvent5.setJump(false);
                } else {
                    boolean flag = this.matchesEntityPlayerSP2(player);
                    boolean flag2 = this.checkState4();
                    if ((flag || flag2) && player.isSprinting()) {
                        player.setSprinting(false);
                    }

                    if (this.checkState5() && !flag && !flag2 && matchesEntityPlayerSP4(player)) {
                        player.setSprinting(true);
                    }

                    if (this.matchesEntityPlayerSP(player)) {
                        if (this.count6 <= 0) {
                            movementEvent5.setJump(false);
                        } else {
                            this.handleEntry2(null);
                            if (flag) {
                                player.setSprinting(false);
                                this.flag9 = true;
                                this.flag8 = true;
                            } else {
                                if (!player.isSprinting() && !matchesEntityPlayerSP4(player)) {
                                    movementEvent5.setJump(false);
                                    return;
                                }

                                player.setSprinting(true);
                                this.flag9 = false;
                                this.flag8 = false;
                            }

                            movementEvent5.setJump(true);
                            this.flag7 = true;
                            this.process12();
                        }
                    }
                }
            }
        }
    }

    private boolean checkState2() {
        boolean flag = matchesInt(mc.gameSettings.keyBindForward.getKeyCode());
        boolean flag2 = matchesInt(mc.gameSettings.keyBindBack.getKeyCode());
        boolean flag3 = matchesInt(mc.gameSettings.keyBindLeft.getKeyCode());
        boolean flag4 = matchesInt(mc.gameSettings.keyBindRight.getKeyCode());
        return flag != flag2 || flag3 != flag4;
    }

    private boolean checkState4() {
        return this.checkState5() && !this.matchesEntityPlayerSP9(mc.thePlayer) && this.flag9;
    }

    private boolean checkState5() {
        return !this.nonUpwardsSetting.matchesString2("Normal")
            && this.tellySetting.matchesString2("Telly")
            && !this.towerSetting.isEnabled()
            && this.checkState14()
            && this.checkState9()
            && this.count4 > 0;
    }

    private boolean matchesEntityPlayerSP2(EntityPlayerSP player) {
        return this.checkState6() && this.matchesEntityPlayerSP(player) && this.count6 > 0;
    }

    public static float[] computeFloatArray() {
        Scaffold scaffold = instance;
        return scaffold != null && scaffold.checkState44() && scaffold.inactiveMode != Scaffold.InactiveMode.INACTIVE && scaffold.entry43 != null
            ? new float[]{scaffold.entry43.factor, scaffold.entry43.factor2}
            : null;
    }

    private static boolean matchesEntry4(Scaffold.Entry4 entry4, Scaffold.Entry4 entry42, float f) {
        float f2 = Math.abs(MathHelper.wrapAngleTo180_float(entry4.factor - entry42.factor));
        float f3 = Math.abs(entry4.factor2 - entry42.factor2);
        return f2 < f && f3 < f;
    }

    private AxisAlignedBB computeAxisAlignedBB(MovementEvent5 movementEvent5, double d) {
        float f = this.inactiveMode != Scaffold.InactiveMode.INACTIVE && this.entry43 != null ? this.entry43.factor : mc.thePlayer.rotationYaw;
        float factor = movementEvent5.getFactor();
        float factor2 = movementEvent5.getFactor2();
        if (factor != 0.0F) {
            f += factor2 > 0.0F ? (factor > 0.0F ? -45.0F : 45.0F) : (factor2 < 0.0F ? (factor > 0.0F ? 45.0F : -45.0F) : 0.0F);
            factor2 = 0.0F;
            factor = factor > 0.0F ? 1.0F : -1.0F;
        }

        double rad = Math.toRadians(f + 90.0F);
        double d2 = factor * d * Math.cos(rad) + factor2 * d * Math.sin(rad);
        double d3 = factor * d * Math.sin(rad) - factor2 * d * Math.cos(rad);
        return mc.thePlayer.getEntityBoundingBox().offset(d2, 0.0, d3);
    }

    private static float computeFloat(float f) {
        return (f % 360.0F + 360.0F) % 360.0F;
    }

    @Override
    public void onDisable() {
        this.flag = false;
        this.process10();
        this.process3();
        if (this.flag16 && mc.timer != null) {
            mc.timer.timerSpeed = 1.0F;
        }

        this.flag16 = false;
        this.process8();
        this.process11();
        this.process5();
        this.lastSlot = -1;
    }

    private boolean checkState8() {
        return mc.currentScreen != null
            || StartHotbarMovesHelper.flag
            || CoreHandler2.checkState()
            || mc.thePlayer != null && this.count == mc.thePlayer.ticksExisted;
    }

    private boolean matchesBlockPos3(BlockPos pos) {
        AxisAlignedBB box = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        return box.intersectsWith(mc.thePlayer.getEntityBoundingBox().contract(1.0E-7, 1.0E-7, 1.0E-7));
    }

    private boolean checkState9() {
        return mc.thePlayer != null && this.checkState2();
    }

    public static Scaffold getInstance() {
        return instance;
    }

    private boolean checkState13() {
        return matchesInt(mc.gameSettings.keyBindForward.getKeyCode())
            || matchesInt(mc.gameSettings.keyBindBack.getKeyCode())
            || matchesInt(mc.gameSettings.keyBindLeft.getKeyCode())
            || matchesInt(mc.gameSettings.keyBindRight.getKeyCode());
    }

    private float computeFloat5() {
        double d = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
        float f;
        if (d > 1.0E-4) {
            f = (float)Math.toDegrees(Math.atan2(-mc.thePlayer.motionX, mc.thePlayer.motionZ));
        } else {
            f = this.computeFloat4();
        }

        return computeFloat(f + 180.0F);
    }

    private Scaffold.Entry3 computeEntry3(BlockPos pos, ItemStack stack, ItemBlock item) {
        Scaffold.Entry3 entry3 = this.entry3;
        if (entry3 != null && entry3.blockPos.equals(pos)) {
            MovingObjectPosition hit = this.computeMovingObjectPosition(entry3.entry4);
            Scaffold.Entry2 entry2 = this.computeEntry2(pos, hit, stack, item);
            if (!matchesEntry2(entry3.entry2, entry2)) {
                this.entry3 = null;
                return null;
            }
            return entry3;
        }
        return null;
    }

    private boolean checkState18() {
        return this.tellyAfterPlaceNoHitCheckSetting.isEnabled()
            && this.tellySetting.matchesString2("Telly")
            && this.noneSetting.matchesString2("Hypixel")
            && !this.noHitCheckSetting.isEnabled();
    }

    private void process10() {
        this.handleFlag(true);
    }

    private void process12() {
        if (this.checkState5()) {
            this.count7++;
        } else {
            this.count7 = 0;
        }
    }

    private static float computeFloat(EnumFacing facing, float f) {
        float f2;
        if (facing == EnumFacing.SOUTH) {
            f2 = 0.0F;
        } else if (facing == EnumFacing.WEST) {
            f2 = 90.0F;
        } else if (facing == EnumFacing.NORTH) {
            f2 = 180.0F;
        } else {
            f2 = 270.0F;
        }

        return Math.abs(MathHelper.wrapAngleTo180_float(f2 - f));
    }

    private static double computeDouble() {
        return mc.playerController != null ? mc.playerController.getBlockReachDistance() : 4.5;
    }

    private boolean onMovementEvent53(MovementEvent5 movementEvent5) {
        if (mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.isRiding() && onMovementEvent54(movementEvent5)) {
            double d = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
            double d2 = MathHelper.clamp_double(Math.max(0.1 + d, mc.thePlayer.isSprinting() ? 0.45 : 0.4), 0.1, 0.6);
            return this.matchesAxisAlignedBB2(mc.thePlayer.getEntityBoundingBox()) || this.matchesAxisAlignedBB2(this.computeAxisAlignedBB(movementEvent5, d2));
        }
        return false;
    }

    private Scaffold.InstantMode computeInstantMode() {
        String text = this.veryLowSetting.getText();
        switch (text) {
            case "Instant":
                return Scaffold.InstantMode.INSTANT;
            case "Half":
                return Scaffold.InstantMode.HALF;
            case "Quarter":
                return Scaffold.InstantMode.QUARTER;
            case "Very High":
                return Scaffold.InstantMode.VERY_HIGH;
            case "High":
                return Scaffold.InstantMode.HIGH;
            case "Medium":
                return Scaffold.InstantMode.MEDIUM;
            case "Low":
                return Scaffold.InstantMode.LOW;
            case "Very Low":
                return Scaffold.InstantMode.VERY_LOW;
            default:
                return this.count5 <= 2 ? Scaffold.InstantMode.HIGH : (this.count5 <= 5 ? Scaffold.InstantMode.MEDIUM : Scaffold.InstantMode.LOW);
        }
    }

    private Scaffold.Entry2 computeEntry2(BlockPos pos, MovingObjectPosition hit, ItemStack stack, ItemBlock item) {
        if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK && hit.sideHit != null) {
            BlockPos blockPos = hit.getBlockPos();
            EnumFacing sideHit = hit.sideHit;
            BlockPos pos2 = blockPos.offset(sideHit);
            int n = computeInt(pos, pos2);
            if (pos2.getY() == pos.getY()
                && n <= this.computeInt2()
                && this.matchesBlockPos2(pos2)
                && this.matchesBlockPos(blockPos)
                && !this.matchesBlockPos3(pos2)) {
                if (this.computeVec32().squareDistanceTo(hit.hitVec) > computeDouble2() + 1.0E-4) {
                    return null;
                }
                return !item.canPlaceBlockOnSide(mc.theWorld, blockPos, sideHit, mc.thePlayer, stack)
                    ? null
                    : new Scaffold.Entry2(pos2, blockPos, hit.hitVec, sideHit, n);
            }
            return null;
        }
        return null;
    }

    private boolean matchesAxisAlignedBB2(AxisAlignedBB box) {
        AxisAlignedBB box2 = box.contract(0.05, 0.0, 0.05).offset(0.0, -0.6, 0.0);
        return mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, box2).isEmpty();
    }

    @Override
    public void onEnable() {
        this.process11();
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                this.process28();
            } else {
                this.integer = MathHelper.floor_double(mc.thePlayer.posY);
                this.integer2 = mc.thePlayer.inventory.currentItem;
                this.lastSlot = this.integer2;
                this.flag10 = mc.thePlayer.onGround;
                this.flag15 = this.tellySetting.matchesString2("Telly") && mc.thePlayer.onGround && this.checkState14();
                this.process();
                this.entry4 = new Scaffold.Entry4(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            }
        }
    }

    public static int computeInt(EntityPlayerSP player) {
        Scaffold scaffold = instance;
        return scaffold != null && scaffold.checkState44() && scaffold.integer2 != null && scaffold.count3 >= 0 ? scaffold.integer2 : player.inventory.currentItem;
    }

    private boolean matchesEntityPlayerSP(EntityPlayerSP player) {
        return player != null
            && this.tellySetting.matchesString2("Telly")
            && this.count4 > 0
            && player.onGround
            && this.checkState9()
            && !this.matchesEntityPlayerSP9(player);
    }

    private boolean checkState20() {
        return this.checkState26() || this.checkState23() || this.checkState11() && this.noHitCheckSetting.isEnabled();
    }

    private Scaffold.Entry2 computeEntry2(BlockPos pos, BlockPos pos2, int n, boolean flag) {
        if (this.matchesBlockPos2(pos2) && !this.matchesBlockPos3(pos2)) {
            Vec3 vec = this.computeVec32();
            Vec3 vec2 = computeVec3(pos2);
            Scaffold.Entry2 entry2 = null;
            double MAX_VALUE = Double.MAX_VALUE;

            for (EnumFacing facing : this.computeEnumFacingArray(pos, pos2)) {
                BlockPos pos3 = pos2.offset(facing);
                EnumFacing opposite = facing.getOpposite();
                if (this.matchesBlockPos(pos3)) {
                    Vec3 vec3 = computeVec3(pos3);
                    if (flag || opposite == EnumFacing.DOWN && this.checkState26() || !(vec.squareDistanceTo(vec2) >= vec.squareDistanceTo(vec3))) {
                        Vec3 vec4 = computeVec3(pos3, opposite);
                        Scaffold.Entry2 entry22 = new Scaffold.Entry2(pos2, pos3, vec4, opposite, n);
                        Scaffold.Entry entry;
                        if (!this.matchesEntry24(entry22) && !this.matchesEntry22(entry22)) {
                            entry = this.computeEntry(pos3, opposite);
                        } else {
                            entry = vec.squareDistanceTo(vec4) <= computeDouble2() ? new Scaffold.Entry(vec4, 0.0) : null;
                        }

                        if (entry != null) {
                            Scaffold.Entry2 entry23 = new Scaffold.Entry2(pos2, pos3, entry.vec3, opposite, n);
                            if (!this.matchesEntry23(entry23)) {
                                if (!flag) {
                                    return entry23;
                                }

                                double d = entry.amount
                                    + vec.squareDistanceTo(entry.vec3) * 0.01
                                    + Math.abs(vec.squareDistanceTo(vec2) - vec.squareDistanceTo(vec3)) * 0.001
                                    + computeDouble(opposite)
                                    + this.computeDouble(pos, pos2, pos3);
                                if (d < MAX_VALUE) {
                                    entry2 = entry23;
                                    MAX_VALUE = d;
                                }
                            }
                        }
                    }
                }
            }

            return entry2;
        }
        return null;
    }

    private static Vec3 computeVec3(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    private void handleEntityPlayerSP6(EntityPlayerSP player) {
        if (this.tellySetting.matchesString2("Telly") && this.checkState9()) {
            this.flag14 = false;
            this.flag2 = false;
            this.amount = 0.0;
            this.flag12 = false;
        } else if (this.towerSetting.isEnabled() && this.hypixelVerticalSetting.matchesString2("HypixelVertical")) {
            if (!this.checkState9() && Math.hypot(player.motionX, player.motionZ) <= 0.3) {
                player.motionX = 0.0;
                player.motionZ = 0.0;
            }

            this.flag14 = this.matchesEntityPlayerSP7(player);
            if (!this.flag14) {
                this.handleFlag(false);
            } else {
                this.flag2 = true;
                if (player.onGround) {
                    player.motionY = 0.42F;
                } else if (player.motionY <= 0.0 && player.motionY >= -0.09) {
                    player.motionY = -0.38;
                }
            }
        } else {
            this.flag14 = false;
            boolean flag = this.checkState9();
            if (this.matchesEntityPlayerSP5(player) && this.flag2) {
                this.flag2 = false;
                this.amount = 0.0;
            }

            boolean flag2 = this.standingSetting.matchesString2("Always")
                || this.standingSetting.matchesString2("Moving") && flag
                || this.standingSetting.matchesString2("Standing") && !flag;
            boolean flag3 = this.hypixelVerticalSetting.matchesString2("Hypixel");
            Speed instance = Speed.getInstance();
            boolean flag4 = !flag3 || !player.isPotionActive(Potion.jump) && (instance == null || !instance.checkState44()) || player.isPotionActive(Potion.moveSpeed);
            boolean flag5 = this.towerSetting.isEnabled() && flag4 && this.matchesEntityPlayerSP10(player) && flag2 && !this.matchesEntityPlayerSP5(player);
            if (!flag5) {
                this.handleFlag(false);
            } else {
                if (!flag3 || player.onGround) {
                    this.flag2 = true;
                }

                if (!this.checkState16()) {
                    if (this.flag2) {
                        this.process4();
                    }
                } else if (this.flag2) {
                    if (this.standingSetting.matchesString2("Always") && !flag || this.standingSetting.matchesString2("Standing")) {
                        player.motionX = 0.0;
                        player.motionZ = 0.0;
                    }

                    if (this.towerCenterSetting.isEnabled() && !flag) {
                        player.setPosition(MathHelper.floor_double(player.posX) + 0.5, player.posY, MathHelper.floor_double(player.posZ) + 0.5);
                    }

                    if (this.hypixelVerticalSetting.matchesString2("Vanilla")) {
                        this.handleEntityPlayerSP(player, flag);
                    } else if (this.hypixelVerticalSetting.matchesString2("Hypixel") && flag) {
                        if (player.onGround) {
                            this.flag12 = true;
                        }

                        if (!this.flag12) {
                            return;
                        }

                        if (this.count5 >= 18) {
                            this.flag2 = false;
                            this.flag13 = true;
                            return;
                        }

                        if (this.count5 % 3 == 0) {
                            double d = 0.22;
                            if (player.isPotionActive(Potion.moveSpeed)) {
                                int amplifier = player.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                                d += (amplifier + 1) * (amplifier == 0 ? 0.036 : 0.042);
                            }

                            GetSpeedHelper.setSpeed(d);
                            player.motionY = 0.42;
                        } else if (this.count5 % 3 == 1) {
                            player.motionY = 0.33;
                        } else {
                            player.motionY = MathHelper.floor_double(player.posY) + 1.0 - player.posY;
                        }
                    }
                }
            }
        }
    }

    private void onMovementEvent55(MovementEvent5 movementEvent5) {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null && mc.theWorld != null) {
            boolean flag = this.checkState26();
            if (flag) {
                movementEvent5.setSneak(false);
            }

            if (!flag && this.autoInputFixSetting.matchesString2("Sneak") && this.onMovementEvent53(movementEvent5)) {
                movementEvent5.setSneak(true);
            }

            if (this.matchesEntityPlayerSP(player) || this.flag2) {
                movementEvent5.setJump(false);
            } else if (this.matchesEntityPlayerSP10(player) && !this.checkState15()) {
                movementEvent5.setJump(true);
            }

            if (this.matchesEntityPlayerSP6(player)) {
                movementEvent5.setJump(false);
            }
        }
    }

    public static Float computeFloatType2(Entity entity, float f) {
        if (entity == mc.thePlayer && entity instanceof EntityPlayerSP && entity.ridingEntity == null) {
            Scaffold scaffold = instance;
            if (scaffold != null && scaffold.checkState44()) {
                float f2 = MathHelper.clamp_float(f, 0.0F, 1.0F);
                if (scaffold.flag17 && scaffold.entityPlayerSP == entity && scaffold.smoothRotsSetting.isEnabled() && scaffold.noneSetting.matchesString2("Hypixel")) {
                    return scaffold.factor5 + (scaffold.factor4 - scaffold.factor5) * f2;
                }
                if (scaffold.inactiveMode != Scaffold.InactiveMode.INACTIVE && scaffold.entry43 != null) {
                    EntityPlayerSP player = (EntityPlayerSP)entity;
                    return player.prevRotationPitchHead + (scaffold.entry43.factor2 - player.prevRotationPitchHead) * f2;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private MovingObjectPosition computeMovingObjectPosition(Scaffold.Entry2 entry2) {
        MovingObjectPosition hit = this.computeMovingObjectPosition(this.computeEntry42());
        return matchesMovingObjectPosition(hit, entry2.blockPos2, entry2.enumFacing) ? hit : null;
    }

    private void handleEntityPlayerSP2(EntityPlayerSP entityPlayerSP) {
        if (this.smoothRotsSetting.isEnabled() && this.noneSetting.matchesString2("Hypixel") && entityPlayerSP != null && entityPlayerSP.ridingEntity == null && this.count4 > 0) {
            long now = System.currentTimeMillis();
            double d = Math.hypot(entityPlayerSP.motionX, entityPlayerSP.motionZ);
            float f;
            if (this.flag14 && this.entry2 != null) {
                f = this.computeEntry4(this.entry2.vec3).factor;
            } else if (d > 0.05) {
                f = MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(-entityPlayerSP.motionX, entityPlayerSP.motionZ)) + 180.0F);
            } else {
                f = MathHelper.wrapAngleTo180_float(entityPlayerSP.rotationYaw + 180.0F);
            }

            float f2 = MathHelper.wrapAngleTo180_float(f + (float)Math.sin(now * 0.002) * 0.8F + (float)Math.cos(now * 0.0035) * 0.6F);
            float f3 = this.entry2 == null ? 77.0F : MathHelper.clamp_float(this.computeEntry4(this.entry2.vec3).factor2, 72.0F, 87.0F);
            if (this.flag17 && this.entityPlayerSP == entityPlayerSP) {
                this.factor3 = this.factor2;
                this.factor5 = this.factor4;
                this.factor2 = f2;
                float f4 = f3 - this.factor6;
                float f5 = Math.abs(f4) > 15.0F ? this.factor6 + Math.signum(f4) * 15.0F : f3;
                this.factor4 = MathHelper.clamp_float(f5 + (float)Math.sin(now * 0.005) * 0.3F, 72.0F, 87.0F);
                this.factor6 = this.factor4;
            } else {
                this.entityPlayerSP = entityPlayerSP;
                this.factor3 = entityPlayerSP.renderYawOffset;
                this.factor5 = entityPlayerSP.rotationPitch;
                this.factor6 = entityPlayerSP.rotationPitch;
                this.factor2 = f2;
                float f6 = f3 - this.factor6;
                this.factor4 = MathHelper.clamp_float(
                    this.factor6 + (Math.abs(f6) > 15.0F ? Math.signum(f6) * 15.0F : f6) + (float)Math.sin(now * 0.005) * 0.3F, 72.0F, 87.0F
                );
                this.factor6 = this.factor4;
                this.flag17 = true;
            }
        } else {
            this.process13();
        }
    }

    private static int computeInt(BlockPos pos, BlockPos pos2) {
        return Math.abs(pos.getX() - pos2.getX()) + Math.abs(pos.getZ() - pos2.getZ());
    }

    private EnumFacing[] orderedHorizontalDirections(float f) {
        List<EnumFacing> facings = new ArrayList<>();
        Collections.addAll(facings, enumFacingArray);
        Collections.sort(facings, (facing, facing2) -> Float.compare(computeFloat(facing, f), computeFloat(facing2, f)));
        return facings.toArray(new EnumFacing[0]);
    }

    private void handleEntityPlayerSP7(EntityPlayerSP player) {
        if (this.tellySetting.matchesString2("Telly") && !player.onGround) {
            if (player.motionY <= -0.65 || this.integer != null && Math.abs(player.posY - this.integer.intValue()) >= 4.0 || player.hurtTime != 0) {
                this.flag6 = true;
            }
        } else {
            this.flag6 = false;
        }
    }

    private boolean matchesEntityPlayerSP8(EntityPlayerSP player) {
        return this.tellySetting.matchesString2("Telly") && !this.matchesEntityPlayerSP9(player) && !this.matchesEntityPlayerSP10(player)
            ? this.checkState9() || !this.checkState13()
            : false;
    }

    private boolean checkState15() {
        Speed instance = Speed.getInstance();
        return instance != null && instance.checkState44();
    }

    @Override
    public void process2() {
        this.flag = false;
        this.count = Integer.MIN_VALUE;
        this.process11();
        this.process5();
    }

    public static boolean checkState24() {
        return instance != null && instance.checkState44() && instance.flag;
    }

    private void process3() {
        if (mc.thePlayer != null && this.integer2 != null && this.integer2 >= 0 && this.integer2 < 9) {
            mc.thePlayer.inventory.currentItem = this.integer2;
            if (mc.playerController != null && mc.getNetHandler() != null) {
                mc.playerController.syncCurrentPlayItem();
            }
        }

        this.integer2 = null;
        this.count3 = -1;
    }

    private Vec3 computeVec3() {
        if (Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) > 0.01) {
            return new Vec3(mc.thePlayer.motionX, 0.0, mc.thePlayer.motionZ);
        }
        double rad = Math.toRadians(this.computeFloat4());
        return this.checkState2() ? new Vec3(-Math.sin(rad), 0.0, Math.cos(rad)) : new Vec3(0.0, 0.0, 0.0);
    }

    private ItemStack computeItemStack() {
        if (mc.thePlayer == null) {
            return null;
        }
        if (this.count3 >= 0 && this.count3 < 9) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(this.count3);
            if (this.matchesItemStack(stack)) {
                return stack;
            }
        }

        int n = this.computeInt();
        return n >= 0 ? mc.thePlayer.inventory.getStackInSlot(n) : null;
    }

    private static boolean matchesEntry2(Scaffold.Entry2 entry2, Scaffold.Entry2 entry22) {
        return entry2 != null
            && entry22 != null
            && entry2.blockPos.equals(entry22.blockPos)
            && entry2.blockPos2.equals(entry22.blockPos2)
            && entry2.enumFacing == entry22.enumFacing;
    }

    private Scaffold.Entry4 computeEntry42() {
        if (this.entry43 != null) {
            return this.entry43;
        }
        return mc.thePlayer != null ? new Scaffold.Entry4(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch) : new Scaffold.Entry4(0.0F, 0.0F);
    }

    private boolean onMovementEvent5(MovementEvent5 movementEvent5) {
        if (this.count4 > 0 && mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.isRiding() && !mc.thePlayer.isSpectator()) {
            double d = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
            double d2 = MathHelper.clamp_double(Math.max(d * 2.0 + 0.15, mc.thePlayer.isSprinting() ? 0.55 : 0.4), 0.25, 0.85);
            AxisAlignedBB entityBoundingBox = mc.thePlayer.getEntityBoundingBox();
            if (this.matchesAxisAlignedBB(entityBoundingBox)) {
                return true;
            }
            if (onMovementEvent54(movementEvent5) && this.matchesAxisAlignedBB(this.computeAxisAlignedBB(movementEvent5, d2))) {
                return true;
            }
            if (d <= 1.0E-4) {
                return false;
            }
            double d3 = MathHelper.clamp_double(0.1 + d, 0.25, d2);
            AxisAlignedBB box = entityBoundingBox.offset(mc.thePlayer.motionX / d * d3, 0.0, mc.thePlayer.motionZ / d * d3);
            return this.matchesAxisAlignedBB(box);
        }
        return false;
    }

    private static double computeDouble(Vec3 vec, Vec3 vec2, EnumFacing facing) {
        double d = (vec.xCoord - vec2.xCoord) * 2.0;
        double d2 = (vec.yCoord - vec2.yCoord) * 2.0;
        double d3 = (vec.zCoord - vec2.zCoord) * 2.0;
        if (facing.getAxis() == Axis.X) {
            return d2 * d2 + d3 * d3;
        }
        return facing.getAxis() == Axis.Y ? d * d + d3 * d3 : d * d + d2 * d2;
    }

    private void handleEntry42(Scaffold.Entry4 entry4) {
        Scaffold.InstantMode instantMode = this.computeInstantMode();
        this.handleEntry4(entry4, instantMode.computeFloat2(), instantMode.computeFloat());
    }

    public boolean checkState27() {
        return !this.noneSetting.matchesString2("None");
    }

    private float computeFloat4() {
        float yaw = mc.thePlayer.rotationYaw;
        float f = matchesInt(mc.gameSettings.keyBindForward.getKeyCode()) ? 1.0F : 0.0F;
        f -= matchesInt(mc.gameSettings.keyBindBack.getKeyCode()) ? 1.0F : 0.0F;
        float f2 = matchesInt(mc.gameSettings.keyBindLeft.getKeyCode()) ? 1.0F : 0.0F;
        f2 -= matchesInt(mc.gameSettings.keyBindRight.getKeyCode()) ? 1.0F : 0.0F;
        float f3;
        if (f < 0.0F) {
            yaw += 180.0F;
            f3 = -0.5F;
        } else if (f > 0.0F) {
            f3 = 0.5F;
        } else {
            f3 = 1.0F;
        }

        if (f2 > 0.0F) {
            yaw -= 90.0F * f3;
        }

        if (f2 < 0.0F) {
            yaw += 90.0F * f3;
        }

        return computeFloat(yaw);
    }

    private MovingObjectPosition computeMovingObjectPosition(Scaffold.Entry4 entry4) {
        Vec3 vec = this.computeVec32();
        Vec3 vec2 = computeVec3(entry4);
        return mc.theWorld
            .rayTraceBlocks(
                vec, vec.addVector(vec2.xCoord * computeDouble(), vec2.yCoord * computeDouble(), vec2.zCoord * computeDouble()), false, false, false
            );
    }

    private void handleEntry4(Scaffold.Entry4 entry4, float f, float f2) {
        this.entry4 = entry4;
        if (mc.thePlayer != null && entry4 != null) {
            this.entry42 = entry4;
            this.g = Math.max(0.0F, f);
            this.factor = Math.max(0.0F, f2);
            this.count9 = mc.thePlayer.ticksExisted;
            if (this.inactiveMode == Scaffold.InactiveMode.INACTIVE || this.entry45 == null) {
                this.entry45 = new Scaffold.Entry4(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                this.entry43 = this.entry45;
                this.entry44 = new Scaffold.Entry4(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                this.count10 = 0;
            }

            if (this.entry46 == null || this.count8 != mc.thePlayer.ticksExisted) {
                this.entry46 = this.entry43;
                this.count8 = mc.thePlayer.ticksExisted;
            }

            Scaffold.Entry4 entry42 = this.linearSetting.matchesString2("Smooth")
                ? computeEntry4(this.entry46, entry4, this.g, this.factor)
                : computeEntry42(this.entry46, entry4, this.g, this.factor);
            float[] floats = OnJumpHelper.computeFloatArray(new float[]{entry42.factor, entry42.factor2}, new float[]{this.entry45.factor, this.entry45.factor2});
            this.entry43 = !Float.isNaN(floats[0]) && !Float.isNaN(floats[1]) ? new Scaffold.Entry4(floats[0], floats[1]) : entry42;
            this.inactiveMode = Scaffold.InactiveMode.AIMING;
        }
    }

    private boolean matchesEntry24(Scaffold.Entry2 entry2) {
        return this.checkState20() || this.noneSetting.matchesString2("Backwards") && entry2 != null && entry2.count >= 2;
    }

    private void handleEntityPlayerSP(EntityPlayerSP player) {
        if (this.integer == null) {
            this.integer = MathHelper.floor_double(player.posY);
        }

        double d = computeInt(this.integer.intValue());
        if (!this.tellySetting.matchesString2("Telly") && !player.onGround && player.motionY > 0.0) {
            d = computeInt(player.posY);
            this.integer = MathHelper.floor_double(player.posY);
        }

        BlockPos pos = new BlockPos(player.posX, (double)((int)Math.round(d)), player.posZ);
        boolean flag = this.checkState17();
        this.flag11 = flag || this.flag13;
        boolean flag2 = this.matchesBlockPos2(pos);
        this.entry2 = flag2 && !this.noneSetting.matchesString2("Raycast") ? this.computeEntry2(pos, true) : null;
        Scaffold.Entry2 entry2 = !flag && !this.checkState22() && this.entry2 == null ? this.computeEntry22(pos) : null;
        if (!flag) {
            this.handleBlockPos(pos, this.entry2, entry2, flag2);
        }
    }

    private static int computeInt(double d) {
        return MathHelper.floor_double(d - 0.75);
    }

    public ModeSetting getNoneSetting() {
        return this.noneSetting;
    }

    private EnumFacing computeEnumFacing(BlockPos pos, BlockPos pos2) {
        if (this.blockPos2 == null) {
            return null;
        }
        if (pos2.getY() != this.blockPos2.getY()) {
            return null;
        }
        if (computeInt(pos, this.blockPos2) > this.computeInt2() + 1) {
            return null;
        }
        if (!this.matchesBlockPos(this.blockPos2)) {
            return null;
        }
        int n = this.blockPos2.getX() - pos2.getX();
        int n2 = this.blockPos2.getZ() - pos2.getZ();
        if (Math.abs(n) + Math.abs(n2) != 1) {
            return null;
        }
        if (n > 0) {
            return EnumFacing.EAST;
        }
        if (n < 0) {
            return EnumFacing.WEST;
        }
        return n2 > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
    }

    public static boolean matchesBlock(Block block) {
        if (block == null || block instanceof BlockAir || block == Blocks.tnt) {
            return false;
        }
        return !(block instanceof BlockChest)
                && !(block instanceof BlockWeb)
                && !(block instanceof BlockCake)
                && !(block instanceof BlockBrewingStand)
                && !(block instanceof BlockEnderChest)
                && !(block instanceof BlockFurnace)
                && !(block instanceof BlockWorkbench)
                && !(block instanceof BlockAnvil)
                && !(block instanceof BlockBeacon)
                && !(block instanceof BlockDragonEgg)
                && !(block instanceof BlockLever)
                && !(block instanceof BlockEnchantmentTable)
                && !(block instanceof BlockButton)
                && !(block instanceof BlockNote)
                && !(block instanceof BlockFenceGate)
                && !(block instanceof BlockDoor)
                && !(block instanceof BlockTrapDoor)
                && !(block instanceof BlockSign)
                && !(block instanceof BlockRedstoneDiode)
                && !(block instanceof BlockDispenser)
                && !(block instanceof BlockCommandBlock)
                && !(block instanceof BlockHopper)
                && !(block instanceof BlockBed)
                && !(block instanceof BlockFence)
                && !(block instanceof BlockSlab)
                && !(block instanceof BlockBasePressurePlate)
                && !(block instanceof BlockWall)
                && !(block instanceof BlockStairs)
                && !(block instanceof BlockLadder)
                && !(block instanceof BlockCarpet)
                && !(block instanceof BlockRailBase)
                && !(block instanceof BlockHugeMushroom)
                && !(block instanceof BlockSkull)
            ? block != Blocks.torch
                && block != Blocks.redstone_torch
                && block != Blocks.redstone_wire
                && block != Blocks.tripwire
                && block != Blocks.tripwire_hook
                && block != Blocks.wheat
                && block != Blocks.melon_stem
                && block != Blocks.pumpkin_stem
                && block != Blocks.iron_bars
            : false;
    }

    public static boolean matchesEntity(Entity entity) {
        if (!(entity instanceof EntityPlayerSP)) {
            return false;
        }
        Scaffold scaffold = instance;
        return scaffold != null && scaffold.checkState44() && scaffold.autoInputFixSetting.matchesString2("Safewalk") && !scaffold.checkState26();
    }

    private double computeDouble(BlockPos pos, Scaffold.Entry2 entry2) {
        Scaffold.Entry4 entry4 = this.computeEntry42();
        Scaffold.Entry4 entry42 = this.computeEntry4(entry2.vec3);
        double d = computeDouble(entry42, entry4) + entry2.count * 4.0 + computeDouble(entry2.enumFacing) + this.computeDouble(pos, entry2.blockPos, entry2.blockPos2);
        if (this.tellySetting.matchesString2("Telly")
            && this.noneSetting.matchesString2("Hypixel")
            && !this.matchesEntityPlayerSP9(mc.thePlayer)
            && !this.checkState26()
            && !entry2.blockPos.equals(pos)) {
            Vec3 vec = this.computeVec3();
            double d2 = Math.hypot(vec.xCoord, vec.zCoord);
            double d3 = entry2.blockPos.getX() - pos.getX();
            double d4 = entry2.blockPos.getZ() - pos.getZ();
            double d5 = Math.hypot(d3, d4);
            double d6;
            if (d2 <= 1.0E-6) {
                d6 = -1.0;
            } else if (d5 <= 1.0E-6) {
                d6 = 1.0;
            } else {
                d6 = (d3 * vec.xCoord + d4 * vec.zCoord) / (d5 * d2);
            }

            d += entry2.count * 3.0 + (1.0 - MathHelper.clamp_double(d6, -1.0, 1.0)) * 8.0;
        }

        return d;
    }

    private void process13() {
        this.entityPlayerSP = null;
        this.factor2 = 0.0F;
        this.factor3 = 0.0F;
        this.factor4 = 0.0F;
        this.factor5 = 0.0F;
        this.factor6 = 0.0F;
        this.flag17 = false;
    }

    private void process() {
        this.count4 = 0;
        if (mc.thePlayer != null) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (this.matchesItemStack(stack)) {
                    this.count4 = this.count4 + stack.stackSize;
                }
            }
        }
    }

    private Scaffold.Entry4 computeEntry4() {
        float f = Math.round((this.computeFloat4() - 180.0F) / 45.0F) * 45.0F;
        float f2 = mc.thePlayer.onGround ? 70.0F : 80.0F;
        return new Scaffold.Entry4(f, f2);
    }

    private void handleInt(int n) {
        if (n >= 0 && n <= 8 && mc.thePlayer != null) {
            if (this.integer2 == null) {
                this.integer2 = mc.thePlayer.inventory.currentItem;
            }

            this.lastSlot = this.integer2;
            mc.thePlayer.inventory.currentItem = n;
            this.count3 = n;
        }
    }

    public static class Entry {
        public final Vec3 vec3;
        public final double amount;

        public Entry(Vec3 vec3, double amount) {
            this.vec3 = vec3;
            this.amount = amount;
        }
    }

    public static class Entry2 {
        public final BlockPos blockPos;
        public final BlockPos blockPos2;
        public final Vec3 vec3;
        public final EnumFacing enumFacing;
        public final int count;

        public Entry2(BlockPos blockPos, BlockPos blockPos2, Vec3 vec3, EnumFacing enumFacing, int count) {
            this.blockPos = blockPos;
            this.blockPos2 = blockPos2;
            this.vec3 = vec3;
            this.enumFacing = enumFacing;
            this.count = count;
        }
    }

    public static class Entry3 {
        public final BlockPos blockPos;
        public final Scaffold.Entry2 entry2;
        public final Scaffold.Entry4 entry4;

        public Entry3(BlockPos blockPos, Scaffold.Entry2 entry2, Scaffold.Entry4 entry4) {
            this.blockPos = blockPos;
            this.entry2 = entry2;
            this.entry4 = entry4;
        }
    }

    public static class Entry4 {
        public final float factor;
        public final float factor2;

        public Entry4(float factor, float f) {
            this.factor = factor;
            this.factor2 = f;
        }
    }

    public static enum InactiveMode {
        INACTIVE,
        AIMING,
        RETURNING;

        private static Scaffold.InactiveMode[] computeInactiveModeArray() {
            return new Scaffold.InactiveMode[]{INACTIVE, AIMING, RETURNING};
        }
    }

    public static enum InstantMode {
        INSTANT(360.0F, 360.0F, 180.0F, 180.0F),
        HALF(180.0F, 180.0F, 90.0F, 90.0F),
        QUARTER(90.0F, 90.0F, 45.0F, 45.0F),
        VERY_HIGH(50.0F, 55.0F, 50.0F, 55.0F),
        HIGH(40.0F, 45.0F, 40.0F, 45.0F),
        MEDIUM(30.0F, 35.0F, 35.0F, 40.0F),
        LOW(20.0F, 25.0F, 25.0F, 30.0F),
        VERY_LOW(10.0F, 15.0F, 15.0F, 20.0F);

        private final float minYawDelta;
        private final float maxYawDelta;
        private final float minPitchDelta;
        private final float maxPitchDelta;

        private InstantMode(float minYawDelta, float maxYawDelta, float minPitchDelta, float maxPitchDelta) {
            this.minYawDelta = minYawDelta;
            this.maxYawDelta = maxYawDelta;
            this.minPitchDelta = minPitchDelta;
            this.maxPitchDelta = maxPitchDelta;
        }

        public float computeFloat2() {
            return computeFloat(this.minYawDelta, this.maxYawDelta);
        }

        private static float computeFloat(float f, float f2) {
            return f == f2 ? f : (float)ThreadLocalRandom.current().nextDouble(f, f2);
        }

        public float computeFloat() {
            return computeFloat(this.minPitchDelta, this.maxPitchDelta);
        }

        private static Scaffold.InstantMode[] computeInstantModeArray() {
            return new Scaffold.InstantMode[]{INSTANT, HALF, QUARTER, VERY_HIGH, HIGH, MEDIUM, LOW, VERY_LOW};
        }
    }
}
