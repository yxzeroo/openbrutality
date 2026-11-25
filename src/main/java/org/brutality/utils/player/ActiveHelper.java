package org.brutality.utils.player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public final class ActiveHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Set<ActiveHelper> itemSet = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Integer, ActiveHelper.Entry> lookupMap = new LinkedHashMap<>();
    private final Map<Integer, ActiveHelper.Entry> lookupMap2 = new LinkedHashMap<>();
    private final Set<Integer> itemSet2 = new HashSet<>();
    private Object object;
    private Object object2;
    private boolean flag;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private long timestamp;
    private Runnable runnable;
    private Deque<Runnable> deque = new ArrayDeque<>();

    public void handleInt(int n) {
        this.itemSet2.add(n);
    }

    public boolean active() {
        this.process5();
        return !this.lookupMap.isEmpty() || !this.lookupMap2.isEmpty();
    }

    private int computeInt(ActiveHelper.Entry entry) {
        if (entry.text != null) {
            return FindIdentityHelper.findIdentity(entry.text, true);
        }
        if (FindIdentityHelper.computeItemStack(entry.count) == null) {
            return entry.count;
        }
        int firstEmptyStack = mc.thePlayer.inventory.getFirstEmptyStack();
        return firstEmptyStack < 0 ? -1 : (firstEmptyStack < 9 ? 36 + firstEmptyStack : firstEmptyStack);
    }

    private void process3() {
        itemSet.remove(this);
        List<Runnable> tasks = new ArrayList<>();
        List list = new ArrayList<>(this.lookupMap2.entrySet());
        Collections.reverse(list);

        for (Map.Entry entry : ((Iterable<Map.Entry>)(Object)(list))) {
            int key = (Integer)entry.getKey();
            ActiveHelper.Entry val = (ActiveHelper.Entry)entry.getValue();
            tasks.add(
                () -> {
                    if (!Objects.equals(val.text2, FindIdentityHelper.computeString(mc.thePlayer.inventory.mainInventory[key]))) {
                        this.process2();
                    } else {
                        int n = this.computeInt(val);
                        if (n < 0) {
                            this.process2();
                        } else {
                            StartHotbarMovesHelper.startHotbarMoves(
                                Collections.singletonList(new StartHotbarMovesHelper.Entry(n, key)), this.flag4, this.timestamp, this::process2
                            );
                        }
                    }
                }
            );
        }

        for (Map.Entry entry2 : this.lookupMap.entrySet()) {
            int key2 = (Integer)entry2.getKey();
            ActiveHelper.Entry val2 = (ActiveHelper.Entry)entry2.getValue();
            if (!this.itemSet2.contains(key2)) {
                tasks.add(() -> {
                    if (!Objects.equals(val2.text2, FindIdentityHelper.computeString(mc.thePlayer.inventory.armorInventory[3 - key2]))) {
                        this.process2();
                    } else if (val2.text == null) {
                        if (mc.thePlayer.inventory.getFirstEmptyStack() < 0) {
                            this.process2();
                        } else {
                            StartHotbarMovesHelper.startArmorRemoval(key2, this.flag4, this::process2);
                        }
                    } else {
                        int n2 = FindIdentityHelper.findIdentity(val2.text, true);
                        if (n2 < 0) {
                            this.process2();
                        } else {
                            FindIdentityHelper.handleInt(key2, n2, this.flag3, this.flag4, this.timestamp, this::process2);
                        }
                    }
                });
            }
        }

        this.flag2 = true;
        this.handleList(tasks);
    }

    public void process4() {
        if (this.active()) {
            itemSet.add(this);
        }
    }

    public void handleList(
        List<StartHotbarMovesHelper.Entry3> entry3s, List<StartHotbarMovesHelper.Entry> list, boolean flag, boolean flag2, long timestamp, Runnable runnable
    ) {
        this.process5();
        if (!this.flag && !this.active() && FindIdentityHelper.matchesFlag(true)) {
            this.itemSet2.clear();
            this.object = mc.thePlayer;
            this.object2 = mc.theWorld;
            this.flag3 = flag;
            this.flag4 = flag2;
            this.timestamp = timestamp;
            this.runnable = runnable;
            List<Runnable> tasks = new ArrayList<>();

            for (StartHotbarMovesHelper.Entry3 entry3 : entry3s) {
                ItemStack stack = FindIdentityHelper.computeItemStack(entry3.count2);
                if (stack != null) {
                    ActiveHelper.Entry entry = new ActiveHelper.Entry(mc.thePlayer.inventory.armorInventory[3 - entry3.count], stack, entry3.count2);
                    this.lookupMap.put(entry3.count, entry);
                    tasks.add(() -> {
                        int n = FindIdentityHelper.findIdentity(entry.text2, true);
                        if (n < 0) {
                            this.process2();
                        } else {
                            FindIdentityHelper.handleInt(entry3.count, n, flag, flag2, timestamp, this::process2);
                        }
                    });
                }
            }

            for (StartHotbarMovesHelper.Entry entry2 : list) {
                ItemStack stack2 = FindIdentityHelper.computeItemStack(entry2.count);
                if (stack2 != null && entry2.count != 36 + entry2.count2 && !this.lookupMap2.containsKey(entry2.count2)) {
                    ActiveHelper.Entry entry4 = new ActiveHelper.Entry(mc.thePlayer.inventory.mainInventory[entry2.count2], stack2, entry2.count);
                    this.lookupMap2.put(entry2.count2, entry4);
                    tasks.add(
                        () -> {
                            int n2 = FindIdentityHelper.findIdentity(entry4.text2, true);
                            if (n2 < 0) {
                                this.process2();
                            } else if (n2 == 36 + entry2.count2) {
                                this.lookupMap2.remove(entry2.count2);
                                this.process2();
                            } else {
                                entry4.text = FindIdentityHelper.computeString(mc.thePlayer.inventory.mainInventory[entry2.count2]);
                                entry4.count = n2;
                                StartHotbarMovesHelper.startHotbarMoves(
                                    Collections.singletonList(new StartHotbarMovesHelper.Entry(n2, entry2.count2)), flag2, timestamp, this::process2
                                );
                            }
                        }
                    );
                }
            }

            this.handleList(tasks);
        }
    }

    private void process5() {
        if (this.object != null && (this.object != mc.thePlayer || this.object2 != mc.theWorld)) {
            this.clear();
        }
    }

    public void clear() {
        itemSet.remove(this);
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.itemSet2.clear();
        this.deque.clear();
        this.runnable = null;
        this.flag = this.flag2 = false;
        this.object = this.object2 = null;
    }

    public boolean checkState() {
        this.process5();
        return this.flag;
    }

    public void handleList(List<StartHotbarMovesHelper.Entry3> entry3s, List<StartHotbarMovesHelper.Entry> list, boolean flag, boolean flag2, long l) {
        this.handleList(entry3s, list, flag, flag2, l, null);
    }

    private void handleList(List<Runnable> tasks) {
        this.deque = new ArrayDeque<>(tasks);
        this.flag = true;
        this.process2();
    }

    private void process2() {
        if (mc.thePlayer != this.object || mc.theWorld != this.object2) {
            this.clear();
        } else if (this.deque.isEmpty()) {
            this.flag = false;
            Runnable runnable = this.runnable;
            this.runnable = null;
            if (this.flag2) {
                this.clear();
            } else if (runnable != null) {
                runnable.run();
            }
        } else {
            this.deque.removeFirst().run();
        }
    }

    public static void process() {
        for (ActiveHelper activeHelper : new ArrayList<>(itemSet)) {
            if (activeHelper.object != mc.thePlayer || activeHelper.object2 != mc.theWorld || mc.thePlayer == null || mc.thePlayer.isDead) {
                activeHelper.clear();
            } else if (!activeHelper.flag && FindIdentityHelper.matchesFlag(true)) {
                activeHelper.process3();
            }
        }
    }

    public static final class Entry {
        public String text;
        public final String text2;
        public int count;

        public Entry(ItemStack stack, ItemStack stack2, int count) {
            this.text = FindIdentityHelper.computeString(stack);
            this.text2 = FindIdentityHelper.computeString(stack2);
            this.count = count;
        }
    }
}
