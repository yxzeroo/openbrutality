package org.brutality.module.impl.player;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.misc.ColorMathUtil;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Autoblockin extends Module {
    public static Autoblockin instance;
    public static NumberSetting numberSetting;
    public static BooleanSetting booleanSetting;
    public static BooleanSetting booleanSetting2;
    public static BooleanSetting booleanSetting3;
    public static BooleanSetting booleanSetting4;
    public static BooleanSetting booleanSetting5;
    public static BooleanSetting booleanSetting6;
    public static BooleanSetting booleanSetting7;
    public static BooleanSetting booleanSetting8;
    public static BooleanSetting booleanSetting9;
    public static boolean active;
    private static float[] floatValueArray;
    public static String text;
    private static int count;
    private static float factor;
    private static float factor2;
    private static long timestamp;
    private static long timestamp2;
    private static boolean gl;
    private static boolean flag;
    private static int count2 = -1;
    private static int count3 = -1;
    private static BlockPos blockPos;
    private static EnumFacing face;
    private static BlockPos blockPos2;
    private static BlockPos blockPos3;
    private static EnumFacing enumFacing;
    private static List<BlockPos> entries;
    private static final Deque<BlockPos> deque = new ArrayDeque<>();
    private static final Set<BlockPos> itemSet = new HashSet<>();
    private static final Set<BlockPos> itemSet2 = new HashSet<>();
    private static EnumFacing[] enumFacingArray;
    private static EnumFacing enumFacing2;
    private static final EnumFacing[] enumFacingArray2 = new EnumFacing[]{
        EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.UP, EnumFacing.DOWN
    };
    private static final EnumFacing[] enumFacingArray3 = new EnumFacing[]{
        EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN
    };
    private static final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
    private static float factor3;
    private static float factor4;
    private static Float floatValue;
    private static Float floatValue2;
    private static Float floatValue3;
    private static Float floatValue4;
    private static Float floatValue5;
    private static int steps;
    private static int count4;
    private static boolean flag2 = true;

    public static Autoblockin getInstance() {
        return instance;
    }

    private static float[] computeFloatArray(float f, float f2, float f3, float f4) {
        float[] floats = ColorMathUtil.Entry11.computeFloatArray(f, f2, f3, f4);
        floatValue3 = floats[0];
        floatValue4 = floats[1];
        if (floatValue5 == null) {
            floatValue5 = floatValue3;
        }

        floatValue5 = computeFloat(floatValue3, floatValue5);
        return new float[]{floatValue3, floatValue4};
    }

    private static void process() {
        blockPos = null;
        face = null;
        blockPos2 = null;
    }

    private static void process4() {
        double d = numberSetting.computeDouble();
        if (d % 1.0 == 0.5) {
            d = Math.floor(d);
            if (flag2) {
                d++;
            }

            flag2 = !flag2;
        }

        steps = (int)d;
        count4 = 0;
    }

    private static void process3() {
        floatValue = null;
        floatValue2 = null;
        factor3 = 0.0F;
        factor4 = 0.0F;
        blockPos3 = null;
        enumFacing = null;
    }

    private static EnumFacing[] facings() {
        return enumFacingArray != null ? enumFacingArray : new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH};
    }

    @Override
    public void onUpdate() {
        Minecraft mc = computeMinecraft();
        if (floatValue3 != null && !booleanSetting7.isEnabled()) {
            mc.thePlayer.rotationYaw = floatValue3;
            mc.thePlayer.rotationPitch = floatValue4;
        }

        if (active) {
            int n = MathHelper.floor_double(mc.thePlayer.posX);
            int n2 = MathHelper.floor_double(mc.thePlayer.posY);
            int n3 = MathHelper.floor_double(mc.thePlayer.posZ);
            factor2 = computeFloat(n, n2, n3);
            int n4 = Math.round(factor2 * 100.0F);
            text = n4 + "%";
            count = n4 != 100 ? -1 : Color.green.getRGB();
            factor = computeFontRenderer(Autoblockin.class).getStringWidth(text) / 2.0F - 2.0F;
            if (n4 == 100 && booleanSetting3.isEnabled()) {
                if (!booleanSetting4.isEnabled()) {
                    this.handleFlag3(false);
                } else {
                    stop();
                    flag = true;
                }
            }
        }
    }

    private static Minecraft computeMinecraft() {
        return Minecraft.getMinecraft();
    }

    private static float computeFloat(float f, float f2, float f3) {
        return f + (f2 - f) * f3;
    }

    private static boolean matchesItemStack(ItemStack stack) {
        if (!ColorMathUtil.Entry.matchesItemStack(stack)) {
            return false;
        }
        Block block = ColorMathUtil.Entry.computeBlock(stack);
        if (!ColorMathUtil.Entry.matchesBlock(block)) {
            return false;
        }
        return block != Blocks.pumpkin && (!booleanSetting2.isEnabled() || block != Blocks.obsidian) ? stack.stackSize > 0 : false;
    }

    private static boolean checkState() {
        return blockPos != null
            && face != null
            && blockPos2 != null
            && !ColorMathUtil.Entry.matchesBlockPos5(blockPos2)
            && ColorMathUtil.Entry.matchesBlockPos2(blockPos2);
    }

    private static List<BlockPos> computeList(BlockPos pos, BlockPos pos2, int n, int n2, int n3) {
        HashSet set = new HashSet();
        HashSet set2 = new HashSet();
        ColorMathUtil.Entry.handleSet(set, set2, itemSet, pos2, n, n2, n3);
        if (set2.isEmpty()) {
            return null;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            if (set.contains(pos.offset(facing.getOpposite()))) {
                return null;
            }
        }

        ArrayList<BlockPos> list = new ArrayList<>();
        ColorMathUtil.Entry.handleList(list, set, set2, pos2);
        if (list.isEmpty()) {
            return null;
        }
        HashSet set3 = new HashSet();

        for (EnumFacing facing2 : EnumFacing.values()) {
            BlockPos pos3 = pos.offset(facing2.getOpposite());
            if (set2.contains(pos3) && !itemSet.contains(pos3)) {
                set3.add(pos3);
            }
        }

        if (set3.isEmpty()) {
            return null;
        }
        int size = set2.size();
        int n4 = Math.min(8000, Math.max(500, size * 2));
        PriorityQueue<Autoblockin.Entry> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(entry -> entry.count));
        HashMap map = new HashMap(size);
        HashMap map2 = new HashMap(size);

        for (BlockPos pos4 : list) {
            int n5 = ColorMathUtil.Entry.computeInt(pos4, set3);
            map.put(pos4, 0);
            priorityQueue.add(new Autoblockin.Entry(pos4, n5, 0));
            map2.put(pos4, null);
        }

        HashSet set4 = new HashSet();
        int n6 = 0;
        BlockPos pos5 = null;

        while (!priorityQueue.isEmpty()) {
            Autoblockin.Entry entry2 = (Autoblockin.Entry)priorityQueue.poll();
            BlockPos blockPos = entry2.blockPos;
            if (!set4.contains(blockPos)) {
                set4.add(blockPos);
                if (set3.contains(blockPos)) {
                    pos5 = blockPos;
                    break;
                }

                if (++n6 > n4) {
                    break;
                }

                int n7 = (Integer)map.get(blockPos);

                for (EnumFacing facing3 : EnumFacing.values()) {
                    BlockPos pos6 = blockPos.offset(facing3);
                    if (set2.contains(pos6) && !set4.contains(pos6)) {
                        int n8 = 1 + (blockPos.getY() != pos6.getY() ? 1 : 0);
                        int n9 = n7 + n8;
                        Integer integer = (Integer)map.get(pos6);
                        if (integer == null || n9 < integer) {
                            map.put(pos6, n9);
                            map2.put(pos6, blockPos);
                            int n10 = ColorMathUtil.Entry.computeInt(pos6, set3);
                            priorityQueue.add(new Autoblockin.Entry(pos6, n9 + n10, n9));
                        }
                    }
                }
            }
        }

        if (pos5 == null) {
            return null;
        }
        ArrayList<BlockPos> list2 = new ArrayList<>();

        for (BlockPos i = pos5; i != null; i = (BlockPos)map2.get(i)) {
            list2.add(i);
        }

        Collections.reverse(list2);
        ArrayList list3 = new ArrayList();

        for (BlockPos pos7 : list2) {
            if (!ColorMathUtil.Entry.matchesBlock4(ColorMathUtil.Entry.getBlock(pos7)) && !ColorMathUtil.Entry.matchesBlockPos5(pos7)) {
                list3.add(pos7);
            }
        }

        return list3;
    }

    private static boolean matchesEntry(ColorMathUtil.Entry11.Entry entry, ItemStack stack) {
        if (!ColorMathUtil.Entry.matchesItemStack(stack)) {
            return false;
        }
        if (blockPos != null && face != null && blockPos2 != null) {
            double blockReachDistance = computeMinecraft().playerController.getBlockReachDistance();
            MovingObjectPosition hit = ColorMathUtil.Entry9.computeMovingObjectPosition(blockPos, face, blockReachDistance, entry.factor, entry.factor2);
            return hit != null && ColorMathUtil.Entry.matchesMovingObjectPosition(hit, stack);
        }
        return false;
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (this.checkState44() && RuntimeAuthGuard.checkState2() && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
        if (this.checkState44() && true && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                float[] floatValueArray2 = OnJumpHelper.floatValueArray;
                floatValueArray = this.computeFloatArray(
                    floatValueArray2 == null ? computeMinecraft().thePlayer.rotationYaw : floatValueArray2[0], floatValueArray2 == null ? computeMinecraft().thePlayer.rotationPitch : floatValueArray2[1]
                );
                this.onUpdate();
            } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE && computeFloatArray() != null) {
                ((EventPreMotion)event).setYaw(floatValueArray[0]);
                ((EventPreMotion)event).setPitch(floatValueArray[1]);
            } else if (event instanceof BasicEvent3) {
                this.onMouse((BasicEvent3)event);
            } else if (event instanceof EventRender2D) {
                this.onEventRender2D((EventRender2D)event);
            }
        }
    }

    private static void handleFloat(float floatValue3, float floatValue22) {
        floatValue = floatValue3;
        floatValue2 = floatValue22;
    }

    private static int computeInt() {
        int n = -1;
        int n2 = 0;
        float f = -1.0F;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = computeMinecraft().thePlayer.inventory.getStackInSlot(i);
            if (matchesItemStack(stack)) {
                Block block = ColorMathUtil.Entry.computeBlock(stack);
                int stackSize = stack.stackSize;
                if (booleanSetting5.isEnabled()) {
                    float f2 = ColorMathUtil.Entry.computeFloat(block, null, true);
                    if (!(f2 < 0.0F) && (f2 > f || f2 == f && stackSize > n2)) {
                        f = f2;
                        n2 = stackSize;
                        n = i;
                    }
                } else if (stackSize > n2) {
                    n2 = stackSize;
                    n = i;
                }
            }
        }

        return n;
    }

    private static boolean matchesBlockPos(BlockPos pos, EnumFacing facing) {
        BlockPos pos2 = ColorMathUtil.Entry.computeBlockPos();
        return pos.equals(pos2) && (facing == EnumFacing.UP || facing == EnumFacing.DOWN);
    }

    private static boolean matchesBlockPos2(BlockPos pos) {
        return itemSet2.contains(pos) || ColorMathUtil.Entry.matchesBlockPos5(pos) || booleanSetting9.isEnabled() && ColorMathUtil.Entry.matchesBlockPos(pos);
    }

    public static float[] computeFloatArray() {
        return instance != null && instance.checkState44() && active ? floatValueArray : null;
    }

    private static void handleBlockPos(BlockPos pos) {
        if (deque.size() >= 3) {
            BlockPos pos2 = deque.pollFirst();
            if (pos2 != null) {
                itemSet.remove(pos2);
            }
        }

        deque.addLast(pos);
        itemSet.add(pos);
    }

    @Override
    public void process2() {
        reset();
        flag = false;
        text = null;
    }

    private static float computeFloat(int n, int n2, int n3) {
        int n4 = 0;
        if (matchesInt(n, n2 + 2, n3)) {
            n4++;
        }

        if (matchesInt(n + 1, n2, n3)) {
            n4++;
        }

        if (matchesInt(n - 1, n2, n3)) {
            n4++;
        }

        if (matchesInt(n, n2, n3 + 1)) {
            n4++;
        }

        if (matchesInt(n, n2, n3 - 1)) {
            n4++;
        }

        if (matchesInt(n + 1, n2 + 1, n3)) {
            n4++;
        }

        if (matchesInt(n - 1, n2 + 1, n3)) {
            n4++;
        }

        if (matchesInt(n, n2 + 1, n3 + 1)) {
            n4++;
        }

        if (matchesInt(n, n2 + 1, n3 - 1)) {
            n4++;
        }

        return n4 / 9.0F;
    }

    private static void process7() {
        EnumFacing facing = enumFacing2 = computeMinecraft().thePlayer.getHorizontalFacing();
        enumFacingArray = new EnumFacing[]{facing, facing.rotateY(), facing.getOpposite(), facing.rotateYCCW()};
    }

    public Autoblockin() {
        super("Autoblockin", 0, Category.PLAYER);
        this.process18();
        instance = this;
        this.process6();
        this.handleSettingArray(
            new Setting[]{
                numberSetting,
                booleanSetting,
                booleanSetting2,
                booleanSetting3,
                booleanSetting4,
                booleanSetting5,
                booleanSetting6,
                booleanSetting7,
                booleanSetting8,
                booleanSetting9
            }
        );
    }

    private static boolean matchesBlockPos(BlockPos pos) {
        BlockPos pos2 = ColorMathUtil.Entry.computeBlockPos();
        if (!pos.equals(pos2.north()) && !pos.equals(pos2.south())) {
            BlockPos pos3 = pos2.down();
            return !pos.equals(pos3.east()) && !pos.equals(pos3.west()) && !pos.equals(pos3.north()) && !pos.equals(pos3.south())
                ? matchesBlockPos(pos, enumFacingArray2)
                : matchesBlockPos3(pos);
        }
        return false;
    }

    private static boolean matchesBlockPos3(BlockPos pos) {
        return ColorMathUtil.Entry.matchesBlockPos5(pos) ? false : matchesBlockPos(pos, enumFacingArray3);
    }

    public void onMouse(BasicEvent3 basicEvent3) {
        if (basicEvent3.flag && basicEvent3.count6 != 0 && active) {
            basicEvent3.cancel();
        }
    }

    @Override
    public void onDisable() {
        if (computeMinecraft().thePlayer != null && count2 >= 0) {
            ColorMathUtil.Entry9.handleInt(count2, false);
        }

        reset();
        flag = false;
        text = null;
        super.onDisable();
    }

    private static float computeFloat(float f) {
        return f < 0.0F ? 0.0F : Math.min(f, 1.0F);
    }

    private static void reset() {
        floatValueArray = null;
        if (active) {
            active = false;
            timestamp2 = System.currentTimeMillis();
        }

        gl = false;
        flag2 = true;
        count2 = -1;
        count3 = -1;
        entries = null;
        deque.clear();
        itemSet.clear();
        itemSet2.clear();
        count4 = 0;
        process3();
        floatValue4 = null;
        floatValue3 = null;
        floatValue5 = null;
        process();
        enumFacing2 = null;
        enumFacingArray = null;
    }

    private static boolean checkState4() {
        int size = deque.size();

        while (size-- > 0 && !deque.isEmpty()) {
            BlockPos pos = deque.pollFirst();
            if (pos != null) {
                if (!matchesBlockPos2(pos) && ColorMathUtil.Entry.matchesBlockPos2(pos)) {
                    if (matchesBlockPos(pos)) {
                        itemSet.remove(pos);
                        return true;
                    }

                    deque.addLast(pos);
                } else {
                    itemSet.remove(pos);
                }
            }
        }

        return false;
    }

    public void process6() {
        numberSetting = new NumberSetting("Delay", this, 1.0, 0.0, 20.0, 0.5);
        booleanSetting = new BooleanSetting("Disable in creative", this, true);
        booleanSetting2 = new BooleanSetting("Disable obsidian", this, true);
        booleanSetting3 = new BooleanSetting("Disable when complete", this, false);
        booleanSetting4 = new BooleanSetting("Only while middle clicking", this, true);
        booleanSetting5 = new BooleanSetting("Prioritize stronger blocks", this, false);
        booleanSetting6 = new BooleanSetting("Show percentage", this, true);
        booleanSetting7 = new BooleanSetting("Silent aim", this, true);
        booleanSetting8 = new BooleanSetting("Silent swing", this, false);
        booleanSetting9 = new BooleanSetting("Skip near bed", this, true);
    }

    private static void process5() {
        if (blockPos2 != null) {
            itemSet2.add(blockPos2);
            if (entries != null && !entries.isEmpty() && blockPos2.equals(entries.get(0))) {
                if (!itemSet.contains(blockPos2)) {
                    handleBlockPos(blockPos2);
                }

                entries = null;
            }

            if (!itemSet.contains(blockPos2) && !ColorMathUtil.Entry.matchesBlockPos5(blockPos2)) {
                handleBlockPos(blockPos2);
            }
        }
    }

    public void onEventRender2D(EventRender2D event) {
        Minecraft mc = computeMinecraft();
        if (booleanSetting6.isEnabled() && text != null && ColorMathUtil.Entry6.checkState2()) {
            long now = System.currentTimeMillis();
            int n;
            if (!active) {
                long l = now - timestamp2;
                if (l >= 125L) {
                    n = 0;
                    text = null;
                } else {
                    n = (int)(240.0F - (float)l / 125.0F * 240.0F);
                }
            } else {
                long l2 = now - timestamp;
                if (l2 >= 125L) {
                    n = 240;
                } else {
                    n = (int)((float)l2 / 125.0F * 240.0F);
                }
            }

            if (n > 0) {
                float f = event.computeFloat() / 2.0F - factor;
                float f2 = event.computeFloat2() / 2.0F + 11.0F;
                if (ColorMathUtil.Entry9.id()) {
                    f2 += 10.0F;
                }

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                computeFontRenderer(Autoblockin.class)
                    .drawString(text, f, f2, ColorMathUtil.Entry10.computeInt(count, ColorMathUtil.Entry10.computeInt(n)), true);
                GlStateManager.disableBlend();
            }
        }
    }

    private static boolean matchesInt(int n, int n2, int n3) {
        mutableBlockPos.func_181079_c(n, n2, n3);
        return ColorMathUtil.Entry.matchesBlock4(ColorMathUtil.Entry.getBlock(mutableBlockPos));
    }

    public float[] computeFloatArray(float f, float f2) {
        Minecraft mc = computeMinecraft();
        if (ColorMathUtil.Entry2.checkState()
            && mc.inGameHasFocus
            && !ColorMathUtil.Entry9.isAdventure()
            && (mc.currentScreen == null || ColorMathUtil.Entry6.checkState())
            && ColorMathUtil.Entry8.checkState()
            && !ColorMathUtil.Entry9.checkState6()
            && (!booleanSetting.isEnabled() || !ColorMathUtil.Entry9.isCreative())) {
            if (booleanSetting4.isEnabled() && flag) {
                if (Mouse.isButtonDown(2)) {
                    return null;
                }

                flag = false;
            }

            boolean flag2 = !booleanSetting4.isEnabled() || Mouse.isButtonDown(2);
            if (!flag2 && gl) {
                stop();
            }

            gl = flag2;
            if (!flag2) {
                return null;
            }
            if (!active) {
                count3 = computeInt();
                if (count3 == -1) {
                    return null;
                }

                count2 = mc.thePlayer.inventory.currentItem;
                if (ColorMathUtil.Entry9.computeInt() != -1) {
                    count2 = ColorMathUtil.Entry9.computeInt();
                }

                ColorMathUtil.Entry9.handleInt(count3, false);
                active = true;
                timestamp = System.currentTimeMillis();
                process4();
                process3();
                process7();
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }

            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem);
            if (active && !matchesItemStack(stack)) {
                int n = computeInt();
                if (n == -1) {
                    stop();
                    return null;
                }

                count3 = n;
                ColorMathUtil.Entry9.handleInt(count3, false);
            }

            itemSet2.clear();
            ColorMathUtil.Entry11.Entry entry = null;

            for (int i = 0; i < 5; i++) {
                if (!checkState() && !checkState2() && !checkState4()) {
                    process();
                    break;
                }

                ColorMathUtil.Entry11.Entry entry2 = ColorMathUtil.Entry11.computeEntry(blockPos, face);
                if (entry2 == null) {
                    process5();
                    process();
                } else if (!matchesEntry(entry2, stack)) {
                    process5();
                    process();
                } else {
                    handleEntry(entry2);
                    if (steps > 0 && count4 < steps) {
                        float f3 = computeFloat((count4 + 1.0F) / steps);
                        float f4 = floatValue != null
                            ? floatValue
                            : (
                                floatValue3 != null
                                    ? computeFloat(floatValue3, floatValue5 != null ? floatValue5 : mc.thePlayer.rotationYaw)
                                    : mc.thePlayer.rotationYaw
                            );
                        float f5 = floatValue2 != null ? floatValue2 : (floatValue4 != null ? floatValue4 : mc.thePlayer.rotationPitch);
                        float f6 = computeFloat(f4, factor3, f3);
                        float f7 = computeFloat(f5, factor4, f3);
                        count4++;
                        return computeFloatArray(f6, f7, f, f2);
                    }

                    double blockReachDistance = mc.playerController.getBlockReachDistance();
                    float f8 = MathHelper.wrapAngleTo180_float(factor3);
                    float f9 = factor4;
                    MovingObjectPosition hit = ColorMathUtil.Entry9.computeMovingObjectPosition(blockPos, face, blockReachDistance, f8, f9);
                    if (hit != null && mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, blockPos, face, entry2.vec3)) {
                        ColorMathUtil.Entry9.handleFlag(booleanSetting8.isEnabled(), true);
                        if (entries != null && !entries.isEmpty() && entries.get(0).equals(blockPos2)) {
                            entries.remove(0);
                        }

                        process();
                        process4();
                        float f10 = floatValue3 != null ? floatValue3 : f8;
                        if (floatValue5 == null) {
                            floatValue5 = f10;
                        }

                        float f11 = computeFloat(f10, floatValue5);
                        handleFloat(f11, floatValue4 != null ? floatValue4 : f9);
                        return computeFloatArray(factor3, factor4, f, f2);
                    }

                    entry = entry2;
                    process5();
                    process();
                }
            }

            if (floatValue == null && floatValue3 == null) {
                return entry == null ? null : computeFloatArray(entry.factor, entry.factor2, f, f2);
            }
            int n2 = numberSetting != null ? (int)numberSetting.computeDouble() : 0;
            if (n2 > 0 && count4 < n2) {
                float f12 = computeFloat((count4 + 1.0F) / n2);
                float f13 = floatValue != null
                    ? floatValue
                    : (
                        floatValue3 != null
                            ? computeFloat(floatValue3, floatValue5 != null ? floatValue5 : mc.thePlayer.rotationYaw)
                            : mc.thePlayer.rotationYaw
                    );
                float f14 = floatValue2 != null ? floatValue2 : (floatValue4 != null ? floatValue4 : mc.thePlayer.rotationPitch);
                float f15 = computeFloat(f13, factor3, f12);
                float f16 = computeFloat(f14, factor4, f12);
                count4++;
                return computeFloatArray(f15, f16, f, f2);
            }
            return computeFloatArray(factor3, factor4, f, f2);
        }
        if (gl) {
            stop();
        } else if (active) {
            reset();
        }

        return null;
    }

    private static boolean matchesBlockPos(BlockPos blockPos22, EnumFacing[] facings) {
        if (ColorMathUtil.Entry.matchesBlockPos5(blockPos22)) {
            return false;
        }
        for (EnumFacing facing : facings) {
            BlockPos pos = blockPos22.offset(facing.getOpposite());
            if (!matchesBlockPos(pos, facing)) {
                Block block = ColorMathUtil.Entry.getBlock(pos);
                if (ColorMathUtil.Entry.matchesBlock4(block)) {
                    blockPos = pos;
                    face = facing;
                    blockPos2 = blockPos22;
                    return true;
                }
            }
        }

        return false;
    }

    private static void stop() {
        if (count2 >= 0 && count2 < 9) {
            ColorMathUtil.Entry9.handleInt(count2, false);
        }

        reset();
    }

    private static boolean checkState2() {
        BlockPos pos = ColorMathUtil.Entry.computeBlockPos();
        BlockPos pos2 = pos.up(2);
        if (entries != null && !entries.isEmpty()) {
            while (!entries.isEmpty() && (itemSet.contains(entries.get(0)) || ColorMathUtil.Entry.matchesBlockPos5(entries.get(0)))) {
                entries.remove(0);
            }

            BlockPos pos3;
            if (!entries.isEmpty() && !matchesBlockPos2(pos3 = entries.get(0)) && matchesBlockPos(pos3)) {
                return true;
            }
        }

        if (ColorMathUtil.Entry.matchesBlockPos2(pos2) && ColorMathUtil.Entry.matchesBlockPos3(pos2) && !matchesBlockPos2(pos2) && matchesBlockPos(pos2)) {
            return true;
        }
        List list;
        if (ColorMathUtil.Entry.matchesBlockPos2(pos2) && (list = computeList(pos2, pos, 8, -2, 6)) != null && !list.isEmpty()) {
            entries = list;

            while (!entries.isEmpty() && ColorMathUtil.Entry.matchesBlockPos5(entries.get(0))) {
                entries.remove(0);
            }

            BlockPos pos4;
            if (!entries.isEmpty() && !matchesBlockPos2(pos4 = entries.get(0)) && matchesBlockPos(pos4)) {
                return true;
            }
        }

        for (EnumFacing facing : facings()) {
            BlockPos pos5 = pos.down().offset(facing);
            BlockPos pos6 = pos.offset(facing);
            BlockPos pos7 = pos6.up();
            if (ColorMathUtil.Entry.matchesBlockPos2(pos5)) {
                if (ColorMathUtil.Entry.matchesBlockPos3(pos5)) {
                    if (!matchesBlockPos2(pos5) && matchesBlockPos3(pos5)) {
                        return true;
                    }
                } else {
                    list = computeList(pos5, pos, Math.max(3, 4), -6, 0);
                    if (list != null && !list.isEmpty()) {
                        entries = list;

                        while (!entries.isEmpty() && ColorMathUtil.Entry.matchesBlockPos5(entries.get(0))) {
                            entries.remove(0);
                        }

                        BlockPos pos8;
                        if (!entries.isEmpty() && !matchesBlockPos2(pos8 = entries.get(0)) && matchesBlockPos(pos8)) {
                            return true;
                        }
                    }
                }
            }

            if (ColorMathUtil.Entry.matchesBlockPos2(pos6)) {
                if (!matchesBlockPos2(pos6) && matchesBlockPos(pos6, pos, facing)) {
                    return true;
                }

                if (ColorMathUtil.Entry.matchesBlockPos3(pos6)) {
                    if (!matchesBlockPos2(pos6) && matchesBlockPos(pos6, enumFacingArray2)) {
                        return true;
                    }
                } else {
                    list = computeList(pos6, pos, Math.max(2, 4), -3, 1);
                    if (list != null && !list.isEmpty()) {
                        entries = list;

                        while (!entries.isEmpty() && ColorMathUtil.Entry.matchesBlockPos5(entries.get(0))) {
                            entries.remove(0);
                        }

                        BlockPos pos9;
                        if (!entries.isEmpty() && !matchesBlockPos2(pos9 = entries.get(0)) && matchesBlockPos(pos9)) {
                            return true;
                        }
                    }
                }
            }

            if (ColorMathUtil.Entry.matchesBlockPos2(pos7)) {
                if (ColorMathUtil.Entry.matchesBlockPos3(pos7)) {
                    if (!matchesBlockPos2(pos7) && matchesBlockPos(pos7, enumFacingArray2)) {
                        return true;
                    }
                } else {
                    list = computeList(pos7, pos, Math.max(2, 4), -2, 2);
                    if (list != null && !list.isEmpty()) {
                        entries = list;

                        while (!entries.isEmpty() && ColorMathUtil.Entry.matchesBlockPos5(entries.get(0))) {
                            entries.remove(0);
                        }

                        BlockPos pos10;
                        if (!entries.isEmpty() && !matchesBlockPos2(pos10 = entries.get(0)) && matchesBlockPos(pos10)) {
                            return true;
                        }
                    }
                }
            }
        }

        return ColorMathUtil.Entry.matchesBlockPos2(pos2) && ColorMathUtil.Entry.matchesBlockPos3(pos2) && !matchesBlockPos2(pos2) && matchesBlockPos(pos2);
    }

    private static void handleEntry(ColorMathUtil.Entry11.Entry entry) {
        Minecraft mc = computeMinecraft();
        boolean flag = blockPos3 == null || !blockPos3.equals(blockPos) || enumFacing != face;
        float f = Math.abs(computeFloat2(entry.factor - factor3));
        float f2 = Math.abs(entry.factor2 - factor4);
        boolean flag2 = f > 1.0F || f2 > 1.0F;
        if (!flag && !flag2 && floatValue != null) {
            float f3 = floatValue5 != null ? floatValue5 : mc.thePlayer.rotationYaw;
            factor3 = computeFloat(entry.factor, f3);
            factor4 = entry.factor2;
        } else {
            float f4 = floatValue3 != null ? floatValue3 : mc.thePlayer.rotationYaw;
            float f5 = floatValue4 != null ? floatValue4 : mc.thePlayer.rotationPitch;
            if (floatValue5 == null) {
                floatValue5 = f4;
            }

            float f6 = computeFloat(f4, floatValue5);
            float f7 = computeFloat(entry.factor, floatValue5);
            handleFloat(f6, f5);
            factor3 = f7;
            factor4 = entry.factor2;
            blockPos3 = blockPos;
            enumFacing = face;
        }
    }

    private static float computeFloat(float f, float f2) {
        float f3 = computeFloat2(f - f2);
        return f2 + f3;
    }

    private static boolean matchesBlockPos(BlockPos blockPos22, BlockPos blockPos3, EnumFacing face2) {
        if (ColorMathUtil.Entry.matchesBlockPos5(blockPos22)) {
            return false;
        }
        if (blockPos22.getY() == blockPos3.getY()) {
            if (!matchesBlockPos(blockPos3, face2) && ColorMathUtil.Entry.matchesBlock4(ColorMathUtil.Entry.getBlock(blockPos3))) {
                blockPos = blockPos3;
                face = face2;
                blockPos2 = blockPos22;
                return true;
            }

            BlockPos pos = blockPos22.down();
            if (!matchesBlockPos(pos, EnumFacing.UP) && ColorMathUtil.Entry.matchesBlock4(ColorMathUtil.Entry.getBlock(pos))) {
                blockPos = pos;
                face = EnumFacing.UP;
                blockPos2 = blockPos22;
                return true;
            }
        }

        return matchesBlockPos(blockPos22, enumFacingArray2);
    }

    private static float computeFloat2(float f) {
        float f2 = f % 360.0F;
        if (f2 >= 180.0F) {
            f2 -= 360.0F;
        }

        if (f2 < -180.0F) {
            f2 += 360.0F;
        }

        return f2;
    }

    public static final class Entry {
        public final BlockPos blockPos;
        public final int count;
        public final int count2;

        public Entry(BlockPos blockPos, int count, int n) {
            this.blockPos = blockPos;
            this.count = count;
            this.count2 = n;
        }
    }
}
