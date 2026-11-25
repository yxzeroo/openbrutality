package org.brutality.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.AutoClicker;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.player.GetBlockStateHelper;
import org.brutality.utils.rotation.GetVectorForRotationHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.input.Mouse;

public class Clutch extends Module {
    private static final Map<String, Integer> lookupMap = new HashMap<>();
    private static final double amount = 0.3;
    private static final double[][] doubleValueArray = new double[][]{{-0.3, -0.3}, {0.3, -0.3}, {-0.3, 0.3}, {0.3, 0.3}};
    private final NumberSetting reachSetting = new NumberSetting("Reach", this, 4.5, 0.5, 4.5, 0.1);
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 8.0, 0.0, 100.0, 1.0);
    private final NumberSetting snapbackSpeedSetting = new NumberSetting("Snapback Speed", this, 12.0, 0.0, 100.0, 1.0);
    private final NumberSetting maxDistanceSetting = new NumberSetting("Max Distance", this, 10.0, 0.0, 20.0, 1.0);
    private final NumberSetting rotationToleranceSetting = new NumberSetting("Rotation Tolerance", this, 25.0, 20.0, 100.0, 1.0);
    private final BooleanSetting simulateFuturePositionSetting = new BooleanSetting("Simulate Future Position", this, true);
    private final BooleanSetting autoClutchSetting = new BooleanSetting("Auto Clutch", this, false);
    private final NumberSetting minimumFallDistanceSetting = new NumberSetting("Minimum Fall Distance", this, 10.0, 3.0, 20.0, 1.0);
    private final ModeSetting holdSneakSetting = new ModeSetting("Activation", this, "Always", Arrays.asList("Always", "Hold Sneak"));
    private BlockPos blockPos;
    private EnumFacing enumFacing;
    private Vec3 hitVec;
    private boolean flag;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private int count = -1;
    private int count2 = -1;
    private float factor;
    private float factor2;
    private BlockPos blockPos2;
    private EnumFacing enumFacing2;
    private boolean flag5;
    private boolean flag6;
    private BlockPos blockPos3;
    private int count3;
    private boolean flag7;
    private boolean flag8;
    private int count4;
    private boolean flag9;
    private int count5;
    private int count6 = -1;
    private static final long timestamp = -4120623077975392256L;

    @Override
    public void onEnable() {
        this.flag5 = false;
        this.flag6 = false;
        this.count3 = 0;
        this.flag7 = false;
        this.flag8 = false;
        this.count4 = 0;
        this.flag9 = false;
        this.count5 = 0;
        this.count6 = -1;
    }

    private boolean checkState() {
        Clutch.Entry3 instance = Clutch.Entry3.getInstance();

        for (int i = 0; i < 10; i++) {
            instance.handleFlag(true);
            if (!instance.onGround && instance.motionY < 0.0) {
                return true;
            }
        }

        return false;
    }

    private boolean checkState4() {
        BedAura instance = BedAura.getInstance();
        return instance != null && instance.checkState44() && instance.rotating;
    }

    private double computeDouble(Vec3 vec, BlockPos pos) {
        double d = Math.max((double)pos.getX(), Math.min(pos.getX() + 1.0, vec.xCoord));
        double d2 = Math.max((double)pos.getY(), Math.min(pos.getY() + 1.0, vec.yCoord));
        double d3 = Math.max((double)pos.getZ(), Math.min(pos.getZ() + 1.0, vec.zCoord));
        double d4 = vec.xCoord - d;
        double d5 = vec.yCoord - d2;
        double d6 = vec.zCoord - d3;
        return Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
    }

    private boolean checkState2() {
        return this.holdSneakSetting.matchesString2("Always") ? true : mc.gameSettings.keyBindSneak.isKeyDown();
    }

    private void handleFlag2(boolean flag) {
        if (this.flag3 && this.count != -1 && this.count != mc.thePlayer.inventory.currentItem) {
            mc.thePlayer.inventory.currentItem = this.count;
            this.flag3 = false;
        }

        this.blockPos2 = null;
        this.enumFacing2 = null;
        this.blockPos3 = null;
        this.count3 = 0;
        if (flag && this.flag5) {
            this.flag6 = true;
        }

        this.flag5 = false;
        this.count = -1;
    }

    private void onMouse(BasicEvent3 basicEvent3) {
        if ((this.flag2 || this.flag6 || this.flag5) && basicEvent3.count6 > -1) {
            basicEvent3.cancel();
        }
    }

    private void process6() {
        this.process5();
        if (this.flag) {
            this.flag = false;
            if (this.blockPos != null
                && this.enumFacing != null
                && this.hitVec != null
                && mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), this.blockPos, this.enumFacing, this.hitVec)) {
                if (this.enumFacing != EnumFacing.UP) {
                    this.count3++;
                }

                this.blockPos3 = this.blockPos;
                mc.thePlayer.swingItem();
            }
        }
    }

    private void process5() {
        if (mc.thePlayer.onGround) {
            this.count3 = 0;
        }

        int ticksExisted = mc.thePlayer.ticksExisted;
        this.handleInt(ticksExisted);
        boolean flag = this.checkState2() || this.flag7;
        if (mc.currentScreen == null && flag) {
            BlockPos pos = new BlockPos(
                MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.posY) - 1, MathHelper.floor_double(mc.thePlayer.posZ)
            );
            if (!this.matchesBlockPos(pos)) {
                this.handleFlag(false);
            } else {
                int n = this.computeInt();
                if (n == -1) {
                    this.handleFlag(false);
                } else {
                    this.count2 = n;
                    Clutch.Entry entry = this.clutchAim();
                    if (entry != null) {
                        this.blockPos2 = entry.movingObjectPosition.getBlockPos();
                        this.enumFacing2 = entry.movingObjectPosition.sideHit;
                        this.factor = entry.factor;
                        this.factor2 = entry.factor2;
                        this.flag5 = true;
                        this.flag6 = false;
                    }

                    if (this.flag5 && !this.flag2) {
                        this.process();
                    }

                    if (this.flag2 || this.flag6 || this.flag5) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        this.process4();
                    }
                }
            }
        } else {
            this.handleFlag2(true);
            this.handleFlag(false);
        }
    }

    private Clutch.Entry clutchAim() {
        Vec3 vec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 vec2 = vec;
        if (this.simulateFuturePositionSetting.isEnabled()) {
            Clutch.Entry3 instance = Clutch.Entry3.getInstance();

            for (int i = 0; i < 20; i++) {
                instance.handleFlag(false);
                if (instance.posY < vec.yCoord - 2.0 || instance.onGround) {
                    break;
                }
            }

            vec2 = instance.computeVec3();
        }

        int n = MathHelper.floor_double(vec.xCoord);
        int n2 = MathHelper.floor_double(vec.zCoord);
        int n3 = MathHelper.floor_double(vec.yCoord);
        int n4 = n - 5;
        int n5 = n + 4;
        int n6 = n2 - 5;
        int n7 = n2 + 4;
        int n8 = n3 - 1;
        int n9 = n3 - 4;
        ArrayList<Clutch.Entry2> entry2s = new ArrayList<>();

        for (int j = n8; j >= n9; j--) {
            for (int k = n4; k <= n5; k++) {
                for (int idx = n6; idx <= n7; idx++) {
                    BlockPos pos = new BlockPos(k, j, idx);
                    if (!this.matchesBlockPos(pos)) {
                        double d = this.computeDouble(vec, pos);
                        double d2 = this.computeDouble(vec2, pos);
                        double d3 = this.simulateFuturePositionSetting.isEnabled() ? d * 0.3 + d2 * 0.7 : d;
                        if (pos.equals(this.blockPos3)) {
                            d3 *= 0.95;
                        }

                        entry2s.add(new Clutch.Entry2(d3, pos));
                    }
                }
            }
        }

        entry2s.sort((entry2, entry22) -> Double.compare(entry2.amount, entry22.amount));
        ItemStack stack = this.count2 >= 0 && this.count2 <= 8 ? mc.thePlayer.inventory.mainInventory[this.count2] : null;

        for (Clutch.Entry2 entry23 : entry2s) {
            boolean flag = this.matchesBlockPos(entry23.blockPos, vec);
            Clutch.Entry bestRotationsToBlock = this.getBestRotationsToBlock(stack, entry23.blockPos, positionEyes, this.reachSetting.computeDouble(), flag);
            if (bestRotationsToBlock != null) {
                return bestRotationsToBlock;
            }
        }

        return null;
    }

    private static float computeFloat(float f, float f2) {
        return MathHelper.wrapAngleTo180_float(f2 - f);
    }

    private Clutch.Entry getBestRotationsToBlock(ItemStack stack, BlockPos pos, Vec3 vec, double d, boolean flag) {
        double d2 = 0.05;
        double d3 = 0.2;
        double d4 = d3 * 0.1;
        boolean flag2 = Math.abs(vec.zCoord - (pos.getZ() + 1)) < Math.abs(vec.zCoord - pos.getZ());
        boolean flag3 = Math.abs(vec.xCoord - (pos.getX() + 1)) < Math.abs(vec.xCoord - pos.getX());
        float f = computeFloat(OnJumpHelper.floatValueArray[0]);
        float f2 = OnJumpHelper.floatValueArray[1];
        int n = (int)Math.round(1.0 / d3);
        ArrayList<Clutch.Entry4> entry4s = new ArrayList<>();
        entry4s.add(new Clutch.Entry4(0.0, f, f2));

        for (int i = 0; i <= n; i++) {
            double d5 = computeDouble(i * d3 + computeDouble(-d4, d4));

            for (int j = 0; j <= n; j++) {
                double d6 = computeDouble(j * d3 + computeDouble(-d4, d4));
                if (flag) {
                    float[] floats = computeFloatArray(vec, pos.getX() + d6, pos.getY() + 1 - d2, pos.getZ() + d5);
                    double d7 = Math.abs(computeFloat(f, floats[0])) + Math.abs(floats[1] - f2);
                    entry4s.add(new Clutch.Entry4(d7, floats[0], floats[1]));
                }

                float[] floats2 = computeFloatArray(vec, pos.getX() + d6, pos.getY() + d5, flag2 ? pos.getZ() + 1 - d2 : pos.getZ() + d2);
                double d8 = Math.abs(computeFloat(f, floats2[0])) + Math.abs(floats2[1] - f2);
                entry4s.add(new Clutch.Entry4(d8, floats2[0], floats2[1]));
                float[] floats3 = computeFloatArray(vec, flag3 ? pos.getX() + 1 - d2 : pos.getX() + d2, pos.getY() + d5, pos.getZ() + d6);
                double d9 = Math.abs(computeFloat(f, floats3[0])) + Math.abs(floats3[1] - f2);
                entry4s.add(new Clutch.Entry4(d9, floats3[0], floats3[1]));
            }
        }

        entry4s.sort((entry4, entry42) -> Double.compare(entry4.amount, entry42.amount));

        for (Clutch.Entry4 entry43 : entry4s) {
            float f3 = computeFloat2(entry43.factor, OnJumpHelper.floatValueArray[0]);
            MovingObjectPosition hit = GetVectorForRotationHelper.computeMovingObjectPosition(d, f3, entry43.factor2);
            if (hit != null) {
                EnumFacing sideHit = hit.sideHit;
                if (sideHit != EnumFacing.DOWN
                    && (sideHit != EnumFacing.UP || flag)
                    && pos.equals(hit.getBlockPos())
                    && this.matchesItemStack(stack, hit.getBlockPos(), sideHit)) {
                    return new Clutch.Entry(hit, f3, entry43.factor2);
                }
            }
        }

        return null;
    }

    private static double computeDouble(double d, double d2) {
        return d + Math.random() * (d2 - d);
    }

    private void process3() {
        if (mc.currentScreen == null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), Mouse.isButtonDown(0));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), Mouse.isButtonDown(1));
        }

        if (this.flag4 && AutoClicker.instance != null && !AutoClicker.instance.checkState44()) {
            AutoClicker.instance.process28();
            this.flag4 = false;
        }
    }

    public static Minecraft getMc3() {
        return mc;
    }

    private void handleInt(int n) {
        if (this.autoClutchSetting.isEnabled()) {
            int hurtTime = mc.thePlayer.hurtTime;
            if (hurtTime > this.count6) {
                this.flag8 = true;
                this.count4 = 0;
                this.flag9 = false;
            }

            this.count6 = hurtTime;
            if (this.flag8 && !this.flag7 && !this.flag9) {
                if ((this.count4 == 0 || this.count4 % 3 == 0) && this.matchesDouble(this.minimumFallDistanceSetting.computeDouble())) {
                    this.flag7 = true;
                }

                this.count4++;
            }

            if (this.flag9) {
                boolean flag = n - this.count5 >= 10;
                boolean keyDown = mc.gameSettings.keyBindJump.isKeyDown();
                boolean flag2 = !mc.thePlayer.onGround && mc.thePlayer.motionY > 0.0;
                if (flag || keyDown || flag2) {
                    this.flag7 = false;
                    this.flag8 = false;
                    this.flag9 = false;
                }
            }

            if (this.flag7 && mc.thePlayer.onGround && mc.thePlayer.hurtTime < mc.thePlayer.maxHurtTime - 2 && !this.flag9) {
                this.flag9 = true;
                this.count5 = n;
                if (!this.checkState()) {
                    this.flag7 = false;
                    this.flag8 = false;
                    this.flag9 = false;
                }
            }

            if (!this.flag7 && !this.flag9 && mc.thePlayer.onGround && mc.thePlayer.hurtTime == 0) {
                this.flag8 = false;
                this.count4 = 0;
            }
        } else {
            this.flag7 = false;
            this.flag8 = false;
            this.flag9 = false;
            this.count6 = mc.thePlayer.hurtTime;
        }
    }

    private boolean matchesDouble(double d) {
        double y = mc.thePlayer.posY;
        Clutch.Entry3 instance = Clutch.Entry3.getInstance();

        for (int i = 0; i < 60; i++) {
            instance.handleFlag(false);
            if (instance.onGround) {
                return false;
            }

            double d2 = y - instance.posY;
            if (d2 > d) {
                return true;
            }
        }

        return false;
    }

    private void handleFlag(boolean flag) {
        if (this.flag2 || flag) {
            this.flag2 = false;
            this.count2 = -1;
            if ((flag || !this.flag5) && this.flag3 && this.count != -1 && this.count != mc.thePlayer.inventory.currentItem) {
                mc.thePlayer.inventory.currentItem = this.count;
                this.flag3 = false;
            }

            if (flag) {
                this.count = -1;
                this.process3();
            }
        }
    }

    private boolean matchesInt(int n) {
        if (n >= 0 && n <= 8) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[n];
            return stack != null && stack.stackSize > 0 && stack.getItem() instanceof ItemBlock;
        }
        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && GetCurrentScreenHelper.checkState2()) {
            if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                this.process6();
            } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE) {
                this.onEventPreMotion((EventPreMotion)event);
            } else if (event instanceof BasicEvent3) {
                this.onMouse((BasicEvent3)event);
            } else if (event instanceof EventPacketSend) {
                this.onEventPacketSend((EventPacketSend)event);
            }
        }
    }

    private boolean matchesBlockPos(BlockPos pos) {
        Block block = GetBlockStateHelper.getBlock(pos);
        if (block == null) {
            return true;
        }
        Material material = block.getMaterial();
        return material == Material.air || material == Material.water || material == Material.lava || block == Blocks.fire;
    }

    private void onEventPacketSend(EventPacketSend event) {
        if (this.flag2 || this.flag6 || this.flag5) {
            if (event.getPacket() instanceof C02PacketUseEntity) {
                event.setCancelled(true);
            }
        }
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (!this.checkState4()) {
            if (mc.currentScreen != null) {
                this.handleFlag(false);
            }

            float yaw = event.getYaw();
            float pitch = event.getPitch();
            if (this.flag6) {
                this.factor = mc.thePlayer.rotationYaw;
                this.factor2 = mc.thePlayer.rotationPitch;
                float[] floats = this.computeFloatArray(yaw, pitch, this.factor, this.factor2, true);
                if (Math.abs(MathHelper.wrapAngleTo180_float(floats[0] - this.factor)) < 0.5F && Math.abs(floats[1] - this.factor2) < 0.5F) {
                    this.flag6 = false;
                    this.process3();
                } else {
                    OnJumpHelper.flag2 = true;
                    event.setYaw(floats[0]);
                    event.setPitch(floats[1]);
                }
            } else if (this.flag5) {
                float[] floats2 = this.computeFloatArray(yaw, pitch, this.factor, this.factor2, false);
                if (this.flag2 && this.blockPos2 != null) {
                    MovingObjectPosition hit = GetVectorForRotationHelper.computeMovingObjectPosition(this.reachSetting.computeDouble(), floats2[0], floats2[1]);
                    if (hit != null && this.blockPos2.equals(hit.getBlockPos()) && this.enumFacing2 == hit.sideHit) {
                        int n = (int)this.maxDistanceSetting.computeDouble();
                        if (n == 0 || this.count3 < n) {
                            double d = this.rotationToleranceSetting.computeDouble();
                            if (Math.abs(MathHelper.wrapAngleTo180_float(floats2[0] - OnJumpHelper.floatValueArray[0])) <= d
                                && Math.abs(floats2[1] - OnJumpHelper.floatValueArray[1]) <= d) {
                                this.blockPos = hit.getBlockPos();
                                this.enumFacing = hit.sideHit;
                                this.hitVec = hit.hitVec;
                                this.flag = true;
                            }
                        }
                    }
                }

                OnJumpHelper.flag2 = true;
                event.setYaw(floats2[0]);
                event.setPitch(floats2[1]);
            }
        }
    }

    public static Minecraft getMc4() {
        return mc;
    }

    private static float[] computeFloatArray(Vec3 vec, double d, double d2, double d3) {
        double d4 = d - vec.xCoord;
        double d5 = d2 - vec.yCoord;
        double d6 = d3 - vec.zCoord;
        double dist = Math.sqrt(d4 * d4 + d6 * d6);
        float f = (float)Math.toDegrees(Math.atan2(d6, d4)) - 90.0F;
        float deg = (float)Math.toDegrees(-Math.atan2(d5, dist));
        return new float[]{computeFloat(f), GetVectorForRotationHelper.computeFloat(deg)};
    }

    private static float computeFloat2(float f, float f2) {
        return f2 + MathHelper.wrapAngleTo180_float(f - f2);
    }

    public Clutch() {
        super("Clutch", 0, Category.PLAYER);
        this.handleSettingArray(
            new Setting[]{
                this.reachSetting,
                this.speedSetting,
                this.snapbackSpeedSetting,
                this.maxDistanceSetting,
                this.rotationToleranceSetting,
                this.simulateFuturePositionSetting,
                this.autoClutchSetting,
                this.minimumFallDistanceSetting,
                this.holdSneakSetting
            }
        );
    }

    private void process() {
        if (!this.flag2) {
            this.flag2 = true;
            if (!this.flag3) {
                this.count = mc.thePlayer.inventory.currentItem;
            }

            this.flag4 = this.flag4 || AutoClicker.instance != null && AutoClicker.instance.checkState44();
            if (this.flag4 && AutoClicker.instance != null) {
                AutoClicker.instance.process28();
            }
        }
    }

    public static Minecraft getMc2() {
        return mc;
    }

    private boolean matchesItemStack(ItemStack stack, BlockPos pos, EnumFacing facing) {
        if (stack != null && stack.stackSize != 0 && stack.getItem() instanceof ItemBlock) {
            BlockPos pos2 = pos.offset(facing.getOpposite());
            Block block = GetBlockStateHelper.getBlock(pos2);
            return block != null && block.canCollideCheck(mc.theWorld.getBlockState(pos2), false);
        }
        return false;
    }

    private static double computeDouble(double d) {
        return d < 0.0 ? 0.0 : Math.min(d, 1.0);
    }

    static {
        lookupMap.put("obsidian", 0);
        lookupMap.put("end_stone", 1);
        lookupMap.put("planks", 2);
        lookupMap.put("log", 2);
        lookupMap.put("log2", 2);
        lookupMap.put("glass", 3);
        lookupMap.put("stained_glass", 3);
        lookupMap.put("hardened_clay", 4);
        lookupMap.put("stained_hardened_clay", 4);
        lookupMap.put("stone", 5);
        lookupMap.put("wool", 5);
    }

    private boolean matchesBlockPos(BlockPos pos, Vec3 vec) {
        if (pos.getY() >= MathHelper.floor_double(vec.yCoord)) {
            return false;
        }
        for (double[] doubles : doubleValueArray) {
            int n = MathHelper.floor_double(vec.xCoord + doubles[0]);
            int n2 = MathHelper.floor_double(vec.zCoord + doubles[1]);
            if (pos.getX() == n && pos.getZ() == n2) {
                return true;
            }
        }

        return false;
    }

    private void process4() {
        int currentItem = mc.thePlayer.inventory.currentItem;
        if (this.count2 != -1 && this.count2 != currentItem) {
            mc.thePlayer.inventory.currentItem = this.count2;
            this.flag3 = true;
        }
    }

    private float[] computeFloatArray(float f, float f2, float f3, float f4, boolean flag) {
        float f5 = f;
        float f6 = f2;
        float f7 = MathHelper.wrapAngleTo180_float(f3 - f);
        float f8 = f4 - f2;
        if (Math.abs(f7) < 0.1F) {
            f5 = f3;
        }

        if (Math.abs(f8) < 0.1F) {
            f6 = f4;
        }

        if (f5 == f3 && f6 == f4) {
            return new float[]{f5, GetVectorForRotationHelper.computeFloat(f6)};
        }
        float f9 = (float)(flag ? this.snapbackSpeedSetting.computeDouble() : this.speedSetting.computeDouble());
        float f10 = 1.0F - (float)computeDouble(0.0, 0.2);
        float f11 = f9 * f10;
        float f12 = Math.abs(f7) + Math.abs(f8);
        if (f12 <= f11) {
            f5 = f3;
            f6 = f4;
        } else if (f11 > 0.0F) {
            float f13 = f11 / f12;
            f5 += f7 * f13;
            f6 += f8 * f13;
        }

        return new float[]{f5, GetVectorForRotationHelper.computeFloat(f6)};
    }

    @Override
    public void onDisable() {
        this.handleFlag2(false);
        this.handleFlag(true);
        this.flag = false;
        this.flag7 = false;
        this.flag8 = false;
        this.flag9 = false;
    }

    private static float computeFloat(float f) {
        f = (f % 360.0F + 360.0F) % 360.0F;
        return f > 180.0F ? f - 360.0F : f;
    }

    private int computeInt() {
        boolean flag = GetCurrentScreenHelper.computeInt2() == 2;
        if (!flag) {
            int currentItem = mc.thePlayer.inventory.currentItem;
            if (this.matchesInt(currentItem)) {
                return currentItem;
            }
            for (int i = 8; i >= 0; i--) {
                if (this.matchesInt(i)) {
                    return i;
                }
            }

            return -1;
        }
        int n = -1;
        int n2 = (int)timestamp;

        for (int j = 8; j >= 0; j--) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[j];
            if (stack != null && stack.stackSize != 0 && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock)stack.getItem()).getBlock();
                ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(block);
                if (nameForObject != null) {
                    Integer integer = lookupMap.get(nameForObject.getResourcePath());
                    if (integer != null && integer > n2) {
                        n2 = integer;
                        n = j;
                    }
                }
            }
        }

        return n;
    }

    public static class Entry {
        public final MovingObjectPosition movingObjectPosition;
        public final float factor;
        public final float factor2;

        public Entry(MovingObjectPosition movingObjectPosition, float factor, float f) {
            this.movingObjectPosition = movingObjectPosition;
            this.factor = factor;
            this.factor2 = f;
        }
    }

    public static class Entry2 {
        public final double amount;
        public final BlockPos blockPos;

        public Entry2(double amount, BlockPos blockPos) {
            this.amount = amount;
            this.blockPos = blockPos;
        }
    }

    public static class Entry3 {
        private AxisAlignedBB axisAlignedBB;
        private double motionX;
        public double motionY;
        private double motionZ;
        public double posY;
        public boolean onGround;

        private void handleDouble(double d, double d2, double d3) {
            double d4 = d;
            double d5 = d2;
            double d6 = d3;
            var collidingBoundingBoxes = Clutch.mc.theWorld.getCollidingBoundingBoxes(Clutch.mc.thePlayer, this.axisAlignedBB.addCoord(d, d2, d3));

            for (AxisAlignedBB box : collidingBoundingBoxes) {
                d2 = box.calculateYOffset(this.axisAlignedBB, d2);
            }

            this.axisAlignedBB = this.axisAlignedBB.offset(0.0, d2, 0.0);

            for (AxisAlignedBB box2 : collidingBoundingBoxes) {
                d = box2.calculateXOffset(this.axisAlignedBB, d);
            }

            this.axisAlignedBB = this.axisAlignedBB.offset(d, 0.0, 0.0);

            for (AxisAlignedBB box3 : collidingBoundingBoxes) {
                d3 = box3.calculateZOffset(this.axisAlignedBB, d3);
            }

            this.axisAlignedBB = this.axisAlignedBB.offset(0.0, 0.0, d3);
            this.onGround = d5 != d2 && d5 < 0.0;
            this.posY = this.axisAlignedBB.minY;
            if (d4 != d) {
                this.motionX = 0.0;
            }

            if (d5 != d2) {
                this.motionY = 0.0;
            }

            if (d6 != d3) {
                this.motionZ = 0.0;
            }
        }

        public void handleFlag(boolean flag) {
            if (flag) {
                this.motionX = 0.0;
                this.motionZ = 0.0;
            }

            this.motionY -= 0.08;
            this.handleDouble(this.motionX, this.motionY, this.motionZ);
            this.motionY *= 0.98F;
            this.motionX *= 0.91;
            this.motionZ *= 0.91;
        }

        public Vec3 computeVec3() {
            return new Vec3(
                (this.axisAlignedBB.minX + this.axisAlignedBB.maxX) / 2.0, this.axisAlignedBB.minY, (this.axisAlignedBB.minZ + this.axisAlignedBB.maxZ) / 2.0
            );
        }

        private Entry3() {
        }

        public static Clutch.Entry3 getInstance() {
            Clutch.Entry3 entry3 = new Clutch.Entry3();
            entry3.axisAlignedBB = Clutch.mc.thePlayer.getEntityBoundingBox();
            entry3.motionX = Clutch.mc.thePlayer.motionX;
            entry3.motionY = Clutch.mc.thePlayer.motionY;
            entry3.motionZ = Clutch.mc.thePlayer.motionZ;
            entry3.posY = Clutch.mc.thePlayer.posY;
            entry3.onGround = Clutch.mc.thePlayer.onGround;
            return entry3;
        }
    }

    public static class Entry4 {
        public final double amount;
        public final float factor;
        public final float factor2;

        public Entry4(double amount, float factor, float f) {
            this.amount = amount;
            this.factor = factor;
            this.factor2 = f;
        }
    }
}
