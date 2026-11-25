package org.brutality.automation.grinding.movement;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.brutality.Brutality;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.automation.mining.MiningHandler2;
import org.brutality.core.OnClientChatReceivedHandler;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.PartyWarpType;
import org.brutality.util.PlaySoundHelper;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.UtilHelper6;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.contract.KillsType;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.MessageUtils;

public class ObsidianPlacerHandler {
    private static final double amount = 5.0;
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final Random random = new Random();
    private static ObsidianPlacerHandler.LeftMode leftMode = ObsidianPlacerHandler.LeftMode.getInstance();
    private static final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private static int count = 2500;
    private static final StopwatchTimer utilHelper82 = new StopwatchTimer();
    private static final StopwatchTimer utilHelper83 = new StopwatchTimer();
    private static final StopwatchTimer utilHelper84 = new StopwatchTimer();
    private static final StopwatchTimer utilHelper85 = new StopwatchTimer();
    private static boolean flag = false;
    public static boolean flag2 = false;
    private static final StopwatchTimer utilHelper86 = new StopwatchTimer();
    private static final org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler(0.75);

    public static void process4() {
        clearPathHandler.process();
    }

    private static void handleInt(int n, int n2) {
        if (mc.thePlayer != null) {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            double d = -x;
            double d2 = -z;
            double dist = Math.sqrt(d * d + d2 * d2);
            double d3;
            double d4;
            if (dist > 1.0E-6) {
                d3 = d / dist;
                d4 = d2 / dist;
            } else {
                d3 = 0.0;
                d4 = 0.0;
            }

            BuildTargetComparatorHelper.handleDouble(x + d3 * 0.25, y - 4.5, z + d4 * 0.25, n, n2);
        }
    }

    private static void handleAutoGrinder(AutoGrinder autoGrinder, int n, int n2, boolean flag) {
        handleFlag2(flag);
        if (!flag2) {
            autoGrinder.handleString5("Started placing " + EnumChatFormatting.LIGHT_PURPLE + "obsidian");
        }

        flag2 = true;
        handleInt((int)Math.min(n * 1.25F, 40.0F), (int)Math.min(n2 * 1.25F, 36.0F));
        AutoGrinder.process46();
        handleFlag(true);
        process3();
        int keyCode = mc.gameSettings.keyBindUseItem.getKeyCode();
        if (!utilHelper86.matchesLong(75L)) {
            KeyBinding.setKeyBindState(keyCode, false);
        } else {
            utilHelper86.reset();
            KeyBinding.setKeyBindState(keyCode, true);
            KeyBinding.onTick(keyCode);
        }
    }

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack heldItem = player.getHeldItem();
        return heldItem != null && heldItem.getItem() == Items.bow;
    }

    public static void handleFlag(boolean flag) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), flag);
    }

    private static int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.obsidian)) {
                return i;
            }
        }

        return -1;
    }

    private static void process3() {
        int n = computeInt();
        if (n != -1 && mc.thePlayer.inventory.currentItem != n) {
            mc.thePlayer.inventory.currentItem = n;
        }
    }

    public static void handleString(String str) {
        if (utilHelper84.matchesLong(1500L)) {
            utilHelper84.reset();
            AutoGrinder.handleString2(str);
            PartyWarpType.SPAWN.process();
        }
    }

    private static void handleFlag2(boolean flag) {
        if (!flag2) {
            if (flag) {
                Brutality.moduleManager.computeModule("FastPlace").process28();
                MessageUtils.x(
                    String.valueOf(
                        new StringBuilder("Disabled ")
                            .append(EnumChatFormatting.GREEN)
                            .append("Fast Place")
                            .append(EnumChatFormatting.RESET)
                            .append(" to avoid issues with automatic obsidian placement.")
                    )
                );
            }
        }
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        return matchesEntityPlayer2(player) ? player.isUsingItem() : false;
    }

    public static void handleFlag(boolean flag2, double d) {
        if (d >= 2.7 && d <= 5.5 && !GetDisplayedTitleHelper.checkState3()) {
            handleFlag(false);
        }

        if (!GetDisplayedTitleHelper.matchesEntityPlayer(mc.thePlayer, 5.0F) && d <= 5.5) {
            handleFlag(false);
        }

        if (flag2 && flag && GetDisplayedTitleHelper.checkState3()) {
            handleFlag(false);
        }

        if (!GetDisplayedTitleHelper.checkState2() && FenceHandler.checkState()) {
            handleFlag(true);
        }
    }

    public static boolean matchesFlag(boolean flag, String str) {
        if (!utilHelper83.matchesLong(1500L)) {
            return false;
        }
        utilHelper83.reset();
        AutoGrinder.handleString2(str);
        PartyWarpType.OOF.process();
        if (flag) {
            PlaySoundHelper.process20();
        }

        return true;
    }

    private static void process2() {
        if (utilHelper85.matchesLong(250L)) {
            utilHelper85.reset();
            int n = (int)(mc.thePlayer.posY + 2.0);
            int n2 = (int)(mc.thePlayer.posX - 2.0);
            int n3 = (int)(mc.thePlayer.posX + 2.0);
            int n4 = (int)(mc.thePlayer.posZ - 2.0);
            int n5 = (int)(mc.thePlayer.posZ + 2.0);
            boolean flag2 = false;

            label26:
            for (int i = n2; i <= n3; i++) {
                for (int j = n4; j <= n5; j++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(i, n, j)).getBlock();
                    if (block == Blocks.obsidian) {
                        flag2 = true;
                        break label26;
                    }
                }
            }

            flag = flag2;
        }
    }

    private static boolean checkState() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (computeInt() != -1 && !GetDisplayedTitleHelper.matchesEntityPlayer2(mc.thePlayer)) {
                Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ)).getBlock();
                return block != Blocks.obsidian;
            }
            return false;
        }
        return false;
    }

    public static void process() {
        clearPathHandler.clearPath();
        flag2 = false;
        flag = false;
        leftMode = ObsidianPlacerHandler.LeftMode.getInstance();
        utilHelper8.clear();
        utilHelper82.clear();
        utilHelper85.clear();
    }

    private static void handleBlockPos(BlockPos pos, int n, int n2, double d) {
        double d2 = Math.hypot(pos.getX() - mc.thePlayer.posX, pos.getZ() - mc.thePlayer.posZ);
        if (d2 <= 8.0) {
            clearPathHandler.handleFlag2(false);
            KeybindStateUtil.process();
        } else {
            clearPathHandler.handleFlag2(true);
            clearPathHandler.handleBlockPos(pos, true, n, n2, d, 0.0);
        }
    }

    public static void handleEntityPlayer(EntityPlayer player, boolean flag, double d, int n, int n2, double d2) {
        float f = MathHelper.wrapAngleTo180_float(
            org.brutality.util.math.MathHelper.computeFloatArray(player.posX, player.posY, player.posZ)[0] - mc.thePlayer.rotationYaw
        );
        double dist = mc.thePlayer.getDistanceToEntity(player);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        boolean flag2 = GetDisplayedTitleHelper.matchesEntityPlayer2(mc.thePlayer) && GetDisplayedTitleHelper.matchesEntityPlayer2(player);
        boolean flag3 = (dist < 5.0 || flag2)
            && BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, player)
            && MiningHandler2.matchesBlockPos(player.getPosition());
        boolean flag4 = !flag3 || !(dist < 5.0) && !flag2;
        if (flag4) {
            handleFlag(flag, dist);
            clearPathHandler.handleFlag2(true);
            clearPathHandler.handleEntity(player, (int)(n * 0.7F), (int)(n2 * 0.8F), d, d2);
        } else {
            clearPathHandler.handleFlag2(false);
            clearPathHandler.process();
            BuildTargetComparatorHelper.handleEntity(player, n, n2);
            handleFlag(Math.random() < d);
            AutoGrinder.process46();
            handleEntityPlayer(player, d2, f, dist);
            handleFlag(flag, dist);
        }

        if (dist <= 1.0) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        }

        if (OnClientChatReceivedHandler.computeKillsType() == KillsType.KILLS_SNEAKING
            && dist <= 5.0
            && BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, player)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    public static void handleEntityPlayer(EntityPlayer player, double d, double d2, double d3) {
        if (Math.random() < d && utilHelper8.matchesLong(2500L)) {
            utilHelper8.reset();
            count = 500 + random.nextInt(2001);
        }

        if (utilHelper82.matchesLong(700L)) {
            leftMode = ObsidianPlacerHandler.LeftMode.getInstance();
            utilHelper82.reset();
        }

        if (matchesEntityPlayer(player)) {
            leftMode.process();
            handleFlag(false);
        }

        boolean flag = UtilHelper6.matchesEntityPlayer(player, 50.0F, 45.0F);
        if (!utilHelper8.matchesLong(count) && d3 <= 7.0 && Math.abs(d2) < 90.0 && flag) {
            leftMode.process();
            if (d3 <= 4.5 && !GetDisplayedTitleHelper.matchesEntityPlayer(mc.thePlayer)) {
                handleFlag(false);
            }
        }
    }

    public static void handleAutoGrinder(
        AutoGrinder autoGrinder,
        EntityPlayer player,
        int n,
        int n2,
        double d,
        boolean flag,
        boolean flag3,
        boolean flag4,
        boolean flag5,
        boolean flag6,
        boolean flag7,
        boolean flag8,
        boolean flag9,
        boolean flag10,
        boolean flag11
    ) {
        if (mc.thePlayer != null) {
            if (autoGrinder.checkState44()) {
                if (!mc.thePlayer.isRiding()) {
                    if (GetMysticEnchantLoreHelper.checkState5()) {
                        KeybindStateUtil.process();
                    } else {
                        process2();
                        boolean flag12 = autoGrinder.checkState17();
                        if (flag12 && GetDisplayedTitleHelper.checkState2() && flag3) {
                            KeybindStateUtil.process();
                            matchesFlag(flag6, "Oofing to go to KOTH.");
                        } else {
                            boolean flag13 = flag9 && checkState() && !flag5 && !flag11 && !flag12;
                            if (player == null) {
                                BlockPos pos = autoGrinder.computeBlockPos();
                                if (pos != null) {
                                    handleBlockPos(pos, n, n2, d);
                                    flag2 = false;
                                } else if (autoGrinder.getAverageMidPlayerCount() <= 1.0) {
                                    KeybindStateUtil.process();
                                    flag2 = false;
                                } else if (flag7 && flag13) {
                                    handleAutoGrinder(autoGrinder, n, n2, flag10);
                                } else {
                                    if (flag7 && flag3) {
                                        KeybindStateUtil.process();
                                        matchesFlag(flag6, "Oofing since no target was found for a while.");
                                    }

                                    flag2 = false;
                                }
                            } else {
                                if (flag4) {
                                    if (flag13) {
                                        handleAutoGrinder(autoGrinder, n, n2, flag10);
                                        return;
                                    }

                                    boolean flag14 = GetDisplayedTitleHelper.checkState2();
                                    boolean flag15 = !flag14 && (!flag11 || flag5) || flag14 && !flag11 && flag5;
                                    if (flag3 && autoGrinder.checkState27() && flag15 && matchesFlag(flag6, "Oofing due to being stuck for a while.")) {
                                        KeybindStateUtil.process();
                                        return;
                                    }
                                }

                                flag2 = false;
                                handleEntityPlayer(player, flag8, d, n, n2, autoGrinder.computeDouble());
                                if (clearPathHandler.checkState() && !GetDisplayedTitleHelper.checkState3() && autoGrinder.checkState27()) {
                                    if (flag3) {
                                        matchesFlag(flag6, "Spawning: no viable path to target.");
                                    }

                                    clearPathHandler.process();
                                    KeybindStateUtil.process();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void clearPath() {
        clearPathHandler.clearPath();
    }

    public static enum LeftMode {
        LEFT,
        RIGHT;

        public static ObsidianPlacerHandler.LeftMode getInstance() {
            return values()[ObsidianPlacerHandler.random.nextInt(values().length)];
        }

        private static ObsidianPlacerHandler.LeftMode[] computeLeftModeArray() {
            return new ObsidianPlacerHandler.LeftMode[]{LEFT, RIGHT};
        }

        public void process() {
            if (this == LEFT) {
                KeyBinding.setKeyBindState(ObsidianPlacerHandler.mc.gameSettings.keyBindLeft.getKeyCode(), true);
                KeyBinding.setKeyBindState(ObsidianPlacerHandler.mc.gameSettings.keyBindRight.getKeyCode(), false);
            } else {
                KeyBinding.setKeyBindState(ObsidianPlacerHandler.mc.gameSettings.keyBindRight.getKeyCode(), true);
                KeyBinding.setKeyBindState(ObsidianPlacerHandler.mc.gameSettings.keyBindLeft.getKeyCode(), false);
            }
        }
    }
}
