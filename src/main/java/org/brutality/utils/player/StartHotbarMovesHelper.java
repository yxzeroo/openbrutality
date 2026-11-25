package org.brutality.utils.player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.utils.MessageUtils;
import org.lwjgl.input.Mouse;

public class StartHotbarMovesHelper {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    public static volatile boolean flag = false;
    private static volatile long timestamp;
    private static volatile boolean flag2;
    private static volatile Runnable runnable = null;
    private static final Deque<StartHotbarMovesHelper.Entry2> deque = new ArrayDeque<>();
    private static volatile long timestamp2;

    public static void handleString(String str) {
        long l = Math.max(0L, System.currentTimeMillis() - timestamp);
        String str2 = MessageUtils.computeString(str, MessageUtils.text11);
        String str3 = MessageUtils.text9;
        String str4 = MessageUtils.text15;
        String str5 = MessageUtils.text9;
        MessageUtils.x(str2 + " " + str5 + "(" + str4 + l + str3 + "ms)");
    }

    private static void beginNormalSwap(StartHotbarMovesHelper.Entry2 entry2) {
        flag = true;
        flag2 = true;
        timestamp = System.currentTimeMillis();
        doSprintStopThen(
            entry2.flag,
            () -> openInventoryThen(
                entry2.flag,
                entry2.timestamp,
                () -> normalSwapSequence(entry2.entries, 0, entry2.timestamp, entry2.flag2, entry2.flag3, entry2.runnable),
                () -> closeNormalInventoryAndFinish(entry2.runnable)
            )
        );
    }

    public static void process2() {
        Runnable task = runnable;
        if (task != null) {
            if (Scaffold.checkState24()) {
                return;
            }

            runnable = null;
            task.run();
        }
    }

    public static void startCustomSwap(boolean flag3, long l, Runnable task) {
        if (!flag) {
            flag = true;
            flag2 = true;
            timestamp = System.currentTimeMillis();
            doSprintStopThen(flag3, () -> openInventoryThen(flag3, l, task, () -> flag = false));
        }
    }

    private static void doSprintStopThen(boolean flag, Runnable task) {
        if (!mc.isCallingFromMinecraftThread()) {
            runClient(() -> doSprintStopThen(flag, task));
        } else {
            process();
            if (flag && mc.thePlayer != null) {
                boolean sprinting = mc.thePlayer.isSprinting();
                if (sprinting) {
                    mc.thePlayer.setSprinting(false);
                }

                Runnable task2 = () -> handleRunnable(task);
                if (sprinting) {
                    scheduleClient(task2, 150L, TimeUnit.MILLISECONDS);
                } else {
                    task2.run();
                }
            } else {
                task.run();
            }
        }
    }

    @Deprecated
    public static int computeInt2(ItemStack stack, int n) {
        if (stack != null && mc.thePlayer != null) {
            for (int i = 9; i < 45; i++) {
                ItemStack stack2 = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack2 != null && stack2.getItem() instanceof ItemArmor && ((ItemArmor)stack2.getItem()).armorType == n && areItemStacksEqual(stack2, stack)) {
                    return i;
                }
            }

            return -1;
        }
        return -1;
    }

    public static int computeInt3(ItemStack stack, int n) {
        if (stack != null && mc.thePlayer != null) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack2 = mc.thePlayer.inventory.mainInventory[i];
                if (stack2 != null && stack2.getItem() instanceof ItemArmor && ((ItemArmor)stack2.getItem()).armorType == n && areItemStacksEqual(stack2, stack)) {
                    return 36 + i;
                }
            }

            for (int j = 9; j < 36; j++) {
                ItemStack stack3 = mc.thePlayer.inventory.mainInventory[j];
                if (stack3 != null && stack3.getItem() instanceof ItemArmor && ((ItemArmor)stack3.getItem()).armorType == n && areItemStacksEqual(stack3, stack)) {
                    return j;
                }
            }

            return -1;
        }
        return -1;
    }

    private static boolean onEvent(Event event) {
        return event instanceof BasicEvent3 basicEvent3 ? basicEvent3.count6 < 0 || basicEvent3.flag : event instanceof BasicEvent4 || event instanceof BasicEvent2;
    }

    public static boolean onEvent2(Event event) {
        boolean flag3 = onEvent(event);
        if (!flag3 && !(event instanceof BasicEvent3)) {
            return false;
        }
        if (flag2 && !flag && !Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            flag2 = false;
        }

        return flag3 && (flag || flag2);
    }

    public static void handleList(List<StartHotbarMovesHelper.Entry3> entry3s, boolean flag2, long l, boolean flag3, boolean flag4, Runnable task) {
        if (entry3s != null && !entry3s.isEmpty()) {
            Runnable task2 = withSwapInventoryLock(task);
            StartHotbarMovesHelper.Entry2 entry2 = new StartHotbarMovesHelper.Entry2(new ArrayList<>(entry3s), flag2, l, flag3, flag4, task2);
            if (flag) {
                synchronized (deque) {
                    deque.addLast(entry2);
                }
            } else {
                beginNormalSwap(entry2);
            }
        }
    }

    public static int computeInt2(int n) {
        if (mc.thePlayer == null) {
            return -1;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == n) {
                return 36 + i;
            }
        }

        for (int j = 9; j < 36; j++) {
            ItemStack stack2 = mc.thePlayer.inventory.mainInventory[j];
            if (stack2 != null && stack2.getItem() instanceof ItemArmor && ((ItemArmor)stack2.getItem()).armorType == n) {
                return j;
            }
        }

        return -1;
    }

    public static int computeInt(int n) {
        switch (n) {
            case 0:
                return 5;
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 8;
            default:
                return 7;
        }
    }

    public static void closeInventoryAndFinish(Runnable task) {
        runClient(() -> {
            if (mc.thePlayer != null) {
                mc.thePlayer.closeScreen();
            }

            scheduleClient(() -> {
                flag = false;
                if (task != null) {
                    task.run();
                }
            }, 50L, TimeUnit.MILLISECONDS);
        });
    }

    @Deprecated
    public static int computeInt(ItemStack stack, int n) {
        if (stack != null && mc.thePlayer != null) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack2 = mc.thePlayer.inventory.mainInventory[i];
                if (stack2 != null && stack2.getItem() instanceof ItemArmor && ((ItemArmor)stack2.getItem()).armorType == n && areItemStacksEqual(stack2, stack)) {
                    return i;
                }
            }

            return -1;
        }
        return -1;
    }

    private static ItemStack computeItemStack(int n) {
        if (mc.thePlayer == null) {
            return null;
        }
        if (n >= 36 && n <= 44) {
            return mc.thePlayer.inventory.mainInventory[n - 36];
        }
        if (n >= 9 && n <= 35) {
            return mc.thePlayer.inventory.mainInventory[n];
        }
        return n >= 0 && n < mc.thePlayer.inventoryContainer.inventorySlots.size() ? mc.thePlayer.inventoryContainer.getSlot(n).getStack() : null;
    }

    private static void handleEntry3(
        StartHotbarMovesHelper.Entry3 entry3,
        List<StartHotbarMovesHelper.Entry3> entry3s,
        int n,
        long l,
        boolean flag,
        boolean flag2,
        Runnable task,
        boolean flag3
    ) {
        if (mc.thePlayer != null) {
            Container container = mc.thePlayer.inventoryContainer;
            int n2 = computeInt();
            ItemStack stack = computeItemStack(entry3.count2).copy();
            mc.playerController.windowClick(container.windowId, entry3.count2, n2, 2, mc.thePlayer);
            scheduleStagedEquip(entry3, entry3s, n, l, flag, flag2, task, flag3, container, n2, stack);
        }
    }

    public static void process3() {
        timestamp2++;
        runnable = null;
        flag = false;
        flag2 = false;
        HotbarHelper.process();
        synchronized (deque) {
            deque.clear();
        }
    }

    public static void swapBackLeggings(ItemStack stack, long l, boolean flag2, boolean flag3, Runnable task) {
        if (stack != null && mc.thePlayer != null) {
            long l2 = Math.max(l, 0L);
            Runnable task2 = () -> runClient(() -> {
                if (!(mc.currentScreen instanceof GuiInventory)) {
                    flag = false;
                    if (task != null) {
                        task.run();
                    }
                } else {
                    int n = computeInt3(stack, 2);
                    if (n != -1) {
                        handleInt(n);
                        if (flag2) {
                            mc.thePlayer.playSound("random.click", 1.0F, 1.0F);
                        }

                        if (flag3) {
                            handleString("Original Leggings Restored");
                        }
                    } else {
                        if (flag2) {
                            mc.thePlayer.playSound("mob.villager.no", 1.0F, 0.7F);
                        }

                        if (flag3) {
                            handleString2("Original Leggings Not Found");
                        }
                    }

                    closeInventoryAndFinish(task);
                }
            });
            if (l2 > 0L) {
                scheduleClient(task2, l2, TimeUnit.MILLISECONDS);
            } else {
                task2.run();
            }
        } else {
            closeInventoryAndFinish(task);
        }
    }

    private static void handleFlag(boolean flag2, Runnable task) {
        handleString2("Swap stopped because the inventory changed");
        if (flag2) {
            closeNormalInventoryAndFinish(task);
        } else {
            flag = false;
            if (task != null) {
                task.run();
            }
        }
    }

    private static void handleInt2(int n) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, 0, 1, mc.thePlayer);
    }

    private static void hotbarMoveSequence(List<StartHotbarMovesHelper.Entry> list, int n, long l, Runnable task) {
        runnable = () -> {
            if (!checkState()) {
                flag = false;
                if (task != null) {
                    task.run();
                }
            } else if (n >= list.size()) {
                flag = false;
                if (task != null) {
                    task.run();
                }
            } else {
                StartHotbarMovesHelper.Entry entry = (StartHotbarMovesHelper.Entry)list.get(n);
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, entry.count, entry.count2, 2, mc.thePlayer);
                int n2 = n + 1;
                if (n2 < list.size()) {
                    long l2 = Math.max(l, 1L);
                    scheduleClient(() -> hotbarMoveSequence(list, n2, l, task), l2, TimeUnit.MILLISECONDS);
                } else {
                    flag = false;
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };
    }

    private static void silentSwapSequence(List<StartHotbarMovesHelper.Entry3> entry3s, int n, long l, boolean flag2, boolean flag3, Runnable task) {
        runnable = () -> {
            if (!checkState()) {
                flag = false;
                if (task != null) {
                    task.run();
                }
            } else if (n >= entry3s.size()) {
                flag = false;
                if (task != null) {
                    task.run();
                }
            } else {
                StartHotbarMovesHelper.Entry3 entry3 = (StartHotbarMovesHelper.Entry3)entry3s.get(n);
                ItemStack stack = computeItemStack(entry3.count2);
                boolean flag4 = stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == entry3.count;
                if (!flag4) {
                    if (flag2) {
                        mc.thePlayer.playSound("mob.villager.no", 1.0F, 0.7F);
                    }

                    if (flag3) {
                        handleString2("No " + computeString(entry3.count) + " found");
                    }
                } else {
                    int n2 = computeInt(entry3.count);
                    if (entry3.count2 < 36 || entry3.count2 > 44) {
                        handleEntry3(entry3, entry3s, n, l, flag2, flag3, task, false);
                        return;
                    }

                    int n3 = entry3.count2 - 36;
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n2, n3, 2, mc.thePlayer);
                    if (flag2) {
                        mc.thePlayer.playSound("random.click", 1.0F, 1.2F);
                    }

                    if (flag3) {
                        handleString("Swapped " + computeString(entry3.count));
                    }
                }

                int n4 = n + 1;
                if (n4 < entry3s.size()) {
                    long l2 = Math.max(l, 1L);
                    scheduleClient(() -> silentSwapSequence(entry3s, n4, l, flag2, flag3, task), l2, TimeUnit.MILLISECONDS);
                } else {
                    flag = false;
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };
    }

    private static void normalSwapSequence(List<StartHotbarMovesHelper.Entry3> entry3s, int n, long l, boolean flag, boolean flag2, Runnable task) {
        runnable = () -> {
            if (!(mc.currentScreen instanceof GuiInventory) || !checkState()) {
                closeNormalInventoryAndFinish(task);
            } else if (n >= entry3s.size()) {
                closeNormalInventoryAndFinish(task);
            } else {
                StartHotbarMovesHelper.Entry3 entry3 = (StartHotbarMovesHelper.Entry3)entry3s.get(n);
                ItemStack stack = computeItemStack(entry3.count2);
                boolean flag3 = stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == entry3.count;
                if (!flag3) {
                    if (flag) {
                        mc.thePlayer.playSound("mob.villager.no", 1.0F, 0.7F);
                    }

                    if (flag2) {
                        handleString2("No " + computeString(entry3.count) + " found");
                    }
                } else {
                    int n2 = computeInt(entry3.count);
                    if (entry3.count2 < 36 || entry3.count2 > 44) {
                        handleEntry3(entry3, entry3s, n, l, flag, flag2, task, true);
                        return;
                    }

                    int n3 = entry3.count2 - 36;
                    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, n2, n3, 2, mc.thePlayer);
                    if (flag) {
                        mc.thePlayer.playSound("random.click", 1.0F, 1.2F);
                    }

                    if (flag2) {
                        handleString("Swapped " + computeString(entry3.count));
                    }
                }

                int n4 = n + 1;
                if (n4 < entry3s.size()) {
                    long l2 = Math.max(l, 1L);
                    scheduleClient(() -> normalSwapSequence(entry3s, n4, l, flag, flag2, task), l2, TimeUnit.MILLISECONDS);
                } else {
                    closeNormalInventoryAndFinish(task);
                }
            }
        };
    }

    private static int computeInt() {
        int currentItem = mc.thePlayer.inventory.currentItem;

        for (int i = 0; i < 9; i++) {
            if (i != currentItem && mc.thePlayer.inventory.mainInventory[i] == null) {
                return i;
            }
        }

        for (int j = 0; j < 9; j++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[j];
            if (j != currentItem && stack != null && !(stack.getItem() instanceof ItemSword)) {
                return j;
            }
        }

        return currentItem == 8 ? 7 : 8;
    }

    private static void closeNormalInventoryAndFinish(Runnable task) {
        runClient(() -> {
            if (mc.currentScreen instanceof GuiInventory && checkState()) {
                mc.thePlayer.closeScreen();
            }

            scheduleClient(() -> {
                flag = false;
                if (task != null) {
                    task.run();
                }

                StartHotbarMovesHelper.Entry2 entry2;
                synchronized (deque) {
                    entry2 = deque.pollFirst();
                }

                if (entry2 != null) {
                    beginNormalSwap(entry2);
                }
            }, 50L, TimeUnit.MILLISECONDS);
        });
    }

    private static boolean checkState() {
        return mc.thePlayer != null
            && mc.thePlayer.inventoryContainer != null
            && mc.thePlayer.openContainer == mc.thePlayer.inventoryContainer
            && mc.thePlayer.inventory.getItemStack() == null;
    }

    private static boolean matchesFlag(boolean flag, Container container) {
        return checkState() && mc.thePlayer.inventoryContainer == container && (!flag || mc.currentScreen instanceof GuiInventory);
    }

    private static void openInventoryThen(boolean flag, long l, Runnable task, Runnable task2) {
        runClient(() -> {
            if (checkState() && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                if (flag && mc.thePlayer.isSprinting()) {
                    mc.thePlayer.setSprinting(false);
                }

                if (mc.currentScreen == null) {
                    mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                }

                if (Mouse.isCreated()) {
                    Mouse.setGrabbed(true);
                }

                long l2 = Math.max(l, 0L);
                if (l2 > 0L) {
                    scheduleClient(task, l2, TimeUnit.MILLISECONDS);
                } else {
                    task.run();
                }
            } else {
                task2.run();
            }
        });
    }

    public static void startArmorRemoval(int n, boolean flag3, Runnable task) {
        if (!flag && mc.thePlayer != null && mc.thePlayer.inventory.getFirstEmptyStack() >= 0) {
            Runnable task2 = withSwapInventoryLock(task);
            flag = true;
            flag2 = true;
            timestamp = System.currentTimeMillis();
            EntityPlayerSP player = mc.thePlayer;
            doSprintStopThen(flag3, () -> runnable = () -> {
                try {
                    if (mc.thePlayer == player && checkState() && player.inventory.getFirstEmptyStack() >= 0) {
                        mc.playerController.windowClick(player.inventoryContainer.windowId, computeInt(n), 0, 1, player);
                    }
                } finally {
                    flag = false;
                    task2.run();
                }
            });
        }
    }

    public static void handleInt2(int n, int n2, boolean flag, long l, boolean flag2, boolean flag3, Runnable task) {
        startSwap(Collections.singletonList(new StartHotbarMovesHelper.Entry3(n, n2)), flag, l, flag2, flag3, task);
    }

    public static void startSwap(List<StartHotbarMovesHelper.Entry3> entry3s, boolean flag3, long l, boolean flag4, boolean flag5, Runnable task) {
        if (!flag && entry3s != null && !entry3s.isEmpty()) {
            Runnable task2 = withSwapInventoryLock(task);
            flag = true;
            flag2 = true;
            timestamp = System.currentTimeMillis();
            doSprintStopThen(flag3, () -> silentSwapSequence(entry3s, 0, l, flag4, flag5, task2));
        }
    }

    public static String computeString(int n) {
        switch (n) {
            case 0:
                return "Helmet";
            case 1:
                return "Chestplate";
            case 2:
                return "Leggings";
            case 3:
                return "Boots";
            default:
                return "Armor";
        }
    }

    public static void handleInt(int n, int n2, boolean flag, long l, boolean flag2, boolean flag3, Runnable task) {
        handleList(Collections.singletonList(new StartHotbarMovesHelper.Entry3(n, n2)), flag, l, flag2, flag3, task);
    }

    private static void scheduleStagedEquip(
        StartHotbarMovesHelper.Entry3 entry3,
        List<StartHotbarMovesHelper.Entry3> entry3s,
        int n,
        long l,
        boolean flag2,
        boolean flag3,
        Runnable task,
        boolean flag4,
        Container container,
        int n2,
        ItemStack stack
    ) {
        scheduleClient(
            () -> runnable = () -> {
                if (matchesFlag(flag4, container) && ItemStack.areItemStacksEqual(computeItemStack(36 + n2), stack)) {
                    int n3 = computeInt(entry3.count);
                    ItemStack stack2 = computeItemStack(n3);
                    ItemStack stack3 = stack2 == null ? null : stack2.copy();
                    ItemStack stack4 = computeItemStack(entry3.count2);
                    ItemStack stack5 = stack4 == null ? null : stack4.copy();
                    mc.playerController.windowClick(container.windowId, n3, n2, 2, mc.thePlayer);
                    scheduleClient(
                        () -> runnable = () -> {
                            if (matchesFlag(flag4, container)
                                && ItemStack.areItemStacksEqual(computeItemStack(36 + n2), stack3)
                                && ItemStack.areItemStacksEqual(computeItemStack(entry3.count2), stack5)) {
                                mc.playerController.windowClick(container.windowId, entry3.count2, n2, 2, mc.thePlayer);
                                if (flag2) {
                                    mc.thePlayer.playSound("random.click", 1.0F, 1.2F);
                                }

                                if (flag3) {
                                    handleString("Swapped " + computeString(entry3.count));
                                }

                                int n4 = n + 1;
                                if (n4 < entry3s.size()) {
                                    scheduleClient(() -> {
                                        if (flag4) {
                                            normalSwapSequence(entry3s, n4, l, flag2, flag3, task);
                                        } else {
                                            silentSwapSequence(entry3s, n4, l, flag2, flag3, task);
                                        }
                                    }, Math.max(l, 1L), TimeUnit.MILLISECONDS);
                                } else if (flag4) {
                                    closeNormalInventoryAndFinish(task);
                                } else {
                                    flag = false;
                                    if (task != null) {
                                        task.run();
                                    }
                                }
                            } else {
                                handleFlag(flag4, task);
                            }
                        },
                        Math.max(l, 1L),
                        TimeUnit.MILLISECONDS
                    );
                } else {
                    handleFlag(flag4, task);
                }
            },
            Math.max(l, 1L),
            TimeUnit.MILLISECONDS
        );
    }

    public static void startHotbarMoves(List<StartHotbarMovesHelper.Entry> list, boolean flag3, long l, Runnable task) {
        if (!flag && list != null && !list.isEmpty()) {
            Runnable task2 = withSwapInventoryLock(task);
            flag = true;
            flag2 = true;
            timestamp = System.currentTimeMillis();
            doSprintStopThen(flag3, () -> hotbarMoveSequence(list, 0, l, task2));
        } else {
            if (task != null) {
                task.run();
            }
        }
    }

    public static void onEvent3(Event event) {
        if (flag) {
            if (onEvent(event)) {
                event.cancel();
                if (mc.currentScreen instanceof GuiContainer && mc.thePlayer != null) {
                    mc.thePlayer.closeScreen();
                }
            }
        }
    }

    private static void normalSwapEquip(int n, List<StartHotbarMovesHelper.Entry3> entry3s, int n2, long l, boolean flag, boolean flag2, Runnable task) {
        runnable = () -> {
            if (mc.currentScreen instanceof GuiInventory && mc.thePlayer != null) {
                handleInt(n);
                if (flag) {
                    mc.thePlayer.playSound("random.click", 1.0F, 1.2F);
                }

                if (flag2) {
                    handleString("Swapped " + computeString(((StartHotbarMovesHelper.Entry3)entry3s.get(n2)).count));
                }

                int n3 = n2 + 1;
                if (n3 < entry3s.size()) {
                    long l2 = Math.max(l, 1L);
                    scheduleClient(() -> normalSwapSequence(entry3s, n3, l, flag, flag2, task), l2, TimeUnit.MILLISECONDS);
                } else {
                    closeNormalInventoryAndFinish(task);
                }
            } else {
                closeNormalInventoryAndFinish(task);
            }
        };
    }

    public static void handleString2(String str) {
        MessageUtils.x(MessageUtils.computeString(str, MessageUtils.text13));
    }

    private static void runClient(Runnable task) {
        long l = timestamp2;
        mc.addScheduledTask(() -> {
            if (l == timestamp2) {
                task.run();
            }
        });
    }

    private static Runnable withSwapInventoryLock(Runnable task) {
        HotbarHelper.process3();
        return () -> {
            try {
                if (task != null) {
                    task.run();
                }
            } finally {
                HotbarHelper.process4();
            }
        };
    }

    private static void handleRunnable(final Runnable task) {
        final long l = System.currentTimeMillis() + 3000L;
        Runnable task2 = new Runnable() {
            private static final long timestamp = 50L;

            @Override
            public void run() {
                if (StartHotbarMovesHelper.mc.thePlayer == null) {
                    StartHotbarMovesHelper.flag = false;
                } else {
                    if (StartHotbarMovesHelper.mc.thePlayer.isRiding() || !StartHotbarMovesHelper.mc.thePlayer.onGround && System.currentTimeMillis() < l) {
                        StartHotbarMovesHelper.scheduleClient(this, timestamp, TimeUnit.MILLISECONDS);
                    } else {
                        task.run();
                    }
                }
            }
        };
        task2.run();
    }

    public static void scheduleClient(Runnable task, long l, TimeUnit timeUnit) {
        long l2 = timestamp2;
        scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
            if (l2 == timestamp2) {
                task.run();
            }
        }), l, timeUnit);
    }

    public static boolean areItemStacksEqual(ItemStack stack, ItemStack stack2) {
        if (stack != null && stack2 != null) {
            if (stack.getItem() != stack2.getItem()) {
                return false;
            }
            if (stack.getMetadata() != stack2.getMetadata()) {
                return false;
            }
            String str = stack.hasDisplayName() ? stack.getDisplayName() : "";
            String str2 = stack2.hasDisplayName() ? stack2.getDisplayName() : "";
            return str.equals(str2);
        }
        return false;
    }

    private static void silentSwapEquip(int n, List<StartHotbarMovesHelper.Entry3> entry3s, int n2, long l, boolean flag2, boolean flag3, Runnable task) {
        runnable = () -> {
            if (mc.thePlayer != null) {
                handleInt2(n);
                if (flag2) {
                    mc.thePlayer.playSound("random.click", 1.0F, 1.2F);
                }

                if (flag3) {
                    handleString("Swapped " + computeString(((StartHotbarMovesHelper.Entry3)entry3s.get(n2)).count));
                }
            }

            int n3 = n2 + 1;
            if (n3 < entry3s.size()) {
                long l2 = Math.max(l, 1L);
                scheduleClient(() -> silentSwapSequence(entry3s, n3, l, flag2, flag3, task), l2, TimeUnit.MILLISECONDS);
            } else {
                flag = false;
                if (task != null) {
                    task.run();
                }
            }
        };
    }

    private static void handleInt(int n) {
        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, n, 0, 1, mc.thePlayer);
    }

    public static void process() {
        KeyBinding keyBinding = mc.gameSettings == null ? null : mc.gameSettings.keyBindUseItem;
        if (keyBinding != null) {
            keyBinding.pressed = false;
            keyBinding.pressTime = 0;
        }

        if (mc.thePlayer != null && mc.thePlayer.isUsingItem() && mc.playerController != null) {
            mc.playerController.onStoppedUsingItem(mc.thePlayer);
        }
    }

    public static class Entry {
        public final int count;
        public final int count2;

        public Entry(int count, int n) {
            this.count = count;
            this.count2 = n;
        }
    }

    public static final class Entry2 {
        public final List<StartHotbarMovesHelper.Entry3> entries;
        public final boolean flag;
        public final long timestamp;
        public final boolean flag2;
        public final boolean flag3;
        public final Runnable runnable;

        public Entry2(List<StartHotbarMovesHelper.Entry3> entries, boolean flag, long timestamp, boolean flag2, boolean flag3, Runnable runnable) {
            this.entries = entries;
            this.flag = flag;
            this.timestamp = timestamp;
            this.flag2 = flag2;
            this.flag3 = flag3;
            this.runnable = runnable;
        }
    }

    public static class Entry3 {
        public final int count;
        public final int count2;

        public Entry3(int count, int n) {
            this.count = count;
            this.count2 = n;
        }
    }
}
