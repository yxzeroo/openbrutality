package org.brutality.automation.hunting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.BlockPos;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.automation.navigation.NavigationHandler;

public final class HuntingHandler3 {
    private static final int count = 10;
    private static final int count2 = 10;
    public static final Random random;
    private static final Map<BlockPos, Boolean> lookupMap;
    private static final Map<BlockPos, Integer> lookupMap2;
    private static final int[][] intValueArray;
    private static final int[][] intValueArray2;
    private static final int[][] intValueArray3;
    private static final long timestamp;

    public static List<BlockPos> computeList(BlockPos blockPos, BlockPos blockPos2, int n, int n2) {
        int n3;
        if (lookupMap.size() > 10000) {
            HuntingHandler3.process();
        }
        if ((n3 = NavigationHandler.computeInt()) <= 2) {
            return HuntingHandler3.computeList(blockPos, blockPos2, n, n2, n3, n3);
        }
        List<BlockPos> list = HuntingHandler3.computeList(blockPos, blockPos2, n * 3 / 4, n2, 2, n3);
        if (!list.isEmpty() && list.size() < 50) {
            return list;
        }
        return HuntingHandler3.computeList(blockPos, blockPos2, n / 2, n2, n3, n3);
    }

    private static void handleMap(Map<BlockPos, Entry> map, BlockPos blockPos, int n) {
        Entry entry = map.get(blockPos);
        if (entry == null || n < entry.count) {
            map.put(blockPos, new Entry(blockPos, n));
        }
    }

    private static List<BlockPos> computeList(BlockPos blockPos, BlockPos blockPos2, int n, int n2, int n3, int n4) {
        Entry2 entry2 = new Entry2(blockPos, blockPos2, n, n2, n3, n4);
        while (!entry2.matchesInt(Integer.MAX_VALUE, timestamp)) {
        }
        return entry2.computeList();
    }

    public static boolean matchesBlockPos2(BlockPos blockPos) {
        for (int[] nArray : intValueArray3) {
            BlockPos blockPos2 = blockPos.add(nArray[0], 0, nArray[1]);
            if (!FenceHandler.matchesBlockPos5(blockPos2) || !FenceHandler.matchesBlockPos5(blockPos2.down()) || !FenceHandler.matchesBlockPos5(blockPos2.down(2))) continue;
            return true;
        }
        return false;
    }

    public static List<BlockPos> computeList(BlockPos blockPos, BlockPos blockPos2, int n) {
        return HuntingHandler3.computeList(blockPos, blockPos2, n, 0);
    }

    private static boolean matchesFlagArray(boolean[] blArray) {
        for (int i = 0; i < 4; ++i) {
            if (!blArray[i]) continue;
            int n = (i + 1) % 4;
            int n2 = (i + 3) % 4;
            if (!blArray[n] && !blArray[n2]) continue;
            return true;
        }
        return false;
    }

    public static BlockPos computeBlockPos(BlockPos blockPos) {
        if (HuntingHandler3.matchesBlockPos(blockPos)) {
            return blockPos;
        }
        for (int i = 1; i <= 10; ++i) {
            BlockPos blockPos2 = blockPos.up(i);
            if (HuntingHandler3.matchesBlockPos(blockPos2)) {
                return blockPos2;
            }
            BlockPos blockPos3 = blockPos.down(i);
            if (!HuntingHandler3.matchesBlockPos(blockPos3)) continue;
            return blockPos3;
        }
        return blockPos;
    }

    public static List<BlockPos> computeList(Entry3 entry3) {
        LinkedList<BlockPos> linkedList = new LinkedList<BlockPos>();
        Entry3 entry32 = entry3;
        while (entry32 != null) {
            linkedList.addFirst(entry32.blockPos);
            entry32 = entry32.entry3;
        }
        return linkedList;
    }

    public static int computeInt(BlockPos blockPos) {
        boolean bl;
        int n;
        Integer n2 = lookupMap2.get(blockPos);
        if (n2 != null) {
            return n2;
        }
        int n3 = 0;
        boolean[] blArray = new boolean[intValueArray2.length];
        int n4 = 0;
        int n5 = 0;
        for (n = 0; n < intValueArray2.length; ++n) {
            BlockPos blockPos2 = blockPos.add(intValueArray2[n][0], 0, intValueArray2[n][1]);
            bl = FenceHandler.matchesBlockPos3(blockPos2) && !FenceHandler.matchesBlockPos6(blockPos2);
            boolean bl2 = FenceHandler.matchesBlockPos3(blockPos2.up()) && !FenceHandler.matchesBlockPos6(blockPos2.up());
            boolean bl3 = blArray[n] = bl || bl2;
            if (!blArray[n]) continue;
            if (n < 4) {
                ++n4;
                n3 += 15;
                continue;
            }
            ++n5;
            n3 += 8;
        }
        n = n4 + n5;
        boolean bl4 = blArray[0] && blArray[1] && !blArray[2] && !blArray[3];
        boolean bl5 = bl = blArray[2] && blArray[3] && !blArray[0] && !blArray[1];
        if ((bl4 || bl) && n == 2) {
            n3 /= 3;
        } else if (n >= 3) {
            n3 += 25;
            if (HuntingHandler3.matchesFlagArray(blArray)) {
                n3 += 12;
            }
        } else if (n == 2 && HuntingHandler3.matchesFlagArray(blArray)) {
            n3 += 12;
        }
        lookupMap2.put(blockPos, n3);
        return n3;
    }

    public static Entry2 computeEntry2(BlockPos blockPos, BlockPos blockPos2, int n, int n2) {
        int n3;
        if (lookupMap.size() > 10000) {
            HuntingHandler3.process();
        }
        Entry2 entry2 = new Entry2(blockPos, blockPos2, (n3 = NavigationHandler.computeInt()) > 2 ? n * 3 / 4 : n, n2, Math.min(n3, 2), n3);
        if (n3 > 2) {
            entry2.count = Math.max(1, n / 2);
        }
        return entry2;
    }

    public static void handleBlockPos(BlockPos blockPos) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    BlockPos blockPos2 = blockPos.add(i, k, j);
                    lookupMap.remove(blockPos2);
                    lookupMap2.remove(blockPos2);
                }
            }
        }
    }

    private static boolean matchesBlockPos(BlockPos blockPos) {
        Boolean bl = lookupMap.get(blockPos);
        if (bl != null) {
            return bl;
        }
        boolean bl2 = FenceHandler.matchesBlockPos7(blockPos);
        lookupMap.put(blockPos, bl2);
        return bl2;
    }

    public static void process() {
        lookupMap.clear();
        lookupMap2.clear();
    }

    private HuntingHandler3() {
    }

    public static int computeInt(BlockPos blockPos, BlockPos blockPos2) {
        int n = Math.abs(blockPos.getX() - blockPos2.getX());
        int n2 = Math.abs(blockPos.getZ() - blockPos2.getZ());
        int n3 = Math.abs(blockPos.getY() - blockPos2.getY());
        int n4 = Math.min(n, n2);
        int n5 = Math.max(n, n2) - n4;
        return n4 * 14 + n5 * 10 + n3 * 20;
    }

    static {
        timestamp = Long.MAX_VALUE;
        random = new Random();
        lookupMap = new ConcurrentHashMap<BlockPos, Boolean>();
        lookupMap2 = new ConcurrentHashMap<BlockPos, Integer>();
        intValueArray = new int[][]{{1, 0, 10}, {-1, 0, 10}, {0, 1, 10}, {0, -1, 10}, {1, 1, 14}, {1, -1, 14}, {-1, 1, 14}, {-1, -1, 14}};
        intValueArray2 = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        intValueArray3 = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {2, 0}, {-2, 0}, {0, 2}, {0, -2}, {2, 2}, {2, -2}, {-2, 2}, {-2, -2}};
    }

    public static List<Entry> computeList(BlockPos blockPos, int n, int n2, int n3) {
        LinkedHashMap<BlockPos, Entry> linkedHashMap = new LinkedHashMap<BlockPos, Entry>();
        int n4 = 0;
        for (int[] nArray : intValueArray) {
            int n5;
            ++n4;
            int n6 = blockPos.getX() + nArray[0];
            int n7 = blockPos.getZ() + nArray[1];
            int n8 = nArray[2];
            LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<Integer>();
            linkedHashSet.add(blockPos.getY() - 1);
            linkedHashSet.add(blockPos.getY());
            for (n5 = 1; n5 <= Math.min(n2, n3); ++n5) {
                linkedHashSet.add(blockPos.getY() + n5);
            }
            if (n3 > n2) {
                if (n4 % 3 == 0) {
                    linkedHashSet.add(blockPos.getY() + (n3 + n2) / 2);
                    linkedHashSet.add(blockPos.getY() + n3);
                }
                if (n < 50 && n4 % 2 == 0) {
                    for (n5 = n2 + 1; n5 <= n3; n5 += 2) {
                        linkedHashSet.add(blockPos.getY() + n5);
                    }
                }
            }
            for (Integer n9 : linkedHashSet) {
                BlockPos blockPos2 = new BlockPos(n6, n9, n7);
                if (FenceHandler.matchesBlockPos(blockPos2)) {
                    for (int i = 1; i <= 10; ++i) {
                        BlockPos blockPos3 = blockPos2.down(i);
                        if (HuntingHandler3.matchesBlockPos(blockPos3)) {
                            HuntingHandler3.handleMap(linkedHashMap, blockPos3, n8 + i * 2);
                            break;
                        }
                        if (!FenceHandler.matchesBlockPos5(blockPos3)) break;
                    }
                }
                if (!HuntingHandler3.matchesBlockPos(blockPos2)) continue;
                HuntingHandler3.handleMap(linkedHashMap, blockPos2, n8);
            }
        }
        return new ArrayList<Entry>(linkedHashMap.values());
    }

    public static final class Entry2 {
        private final BlockPos blockPos;
        private final BlockPos blockPos2;
        public int count;
        private Entry2 entry2;
        private final int count2;
        private final int count3;
        private final int count4;
        private final int count5;
        private final PriorityQueue<Entry3> priorityQueue = new PriorityQueue<Entry3>(Comparator.comparingInt(Entry3::computeInt));
        private final Map<BlockPos, Integer> lookupMap = new HashMap<BlockPos, Integer>();
        private final Set<BlockPos> itemSet = new HashSet<BlockPos>();
        private Entry3 entry3;
        private int count6 = (int)timestamp;
        private int count7;
        private boolean finished;
        private static final String text;
        private static final long timestamp;

        public boolean matchesInt(int n, long l) {
            if (this.finished) {
                return true;
            }
            if (this.entry2 != null) {
                this.finished = this.entry2.matchesInt(n, l);
                return this.finished;
            }
            long l2 = System.nanoTime();
            int n2 = 0;
            while (!this.priorityQueue.isEmpty() && this.count7 < this.count2 && n2 < n) {
                Entry3 entry3 = this.priorityQueue.poll();
                ++this.count7;
                ++n2;
                int n3 = HuntingHandler3.computeInt(entry3.blockPos, this.blockPos2);
                if (n3 < this.count6) {
                    this.count6 = n3;
                    this.entry3 = entry3;
                }
                if (entry3.blockPos.equals(this.blockPos2)) {
                    this.entry3 = entry3;
                    this.finished = true;
                    return this.checkState();
                }
                if (!this.itemSet.add(entry3.blockPos)) continue;
                List<Entry> list = HuntingHandler3.computeList(entry3.blockPos, this.count7, this.count4, this.count5);
                if (this.count3 > 20) {
                    Collections.shuffle(list, random);
                }
                for (Entry entry : list) {
                    Integer n4;
                    int n5 = entry.blockPos.getY() - entry3.blockPos.getY();
                    if (n5 < -10 || n5 > this.count5 || this.itemSet.contains(entry.blockPos) || (n4 = this.lookupMap.get(entry.blockPos)) != null && entry3.count + entry.count >= n4 || !FenceHandler.matchesBlockPos(entry3.blockPos, entry.blockPos)) continue;
                    int n6 = HuntingHandler3.computeInt(entry.blockPos);
                    int n7 = HuntingHandler3.matchesBlockPos2(entry.blockPos) ? 30 : 0;
                    int n8 = this.count3 > 0 ? random.nextInt(this.count3 / 5 + 1) : 0;
                    int n9 = entry3.count + entry.count + n6 + n7 + n8;
                    Integer n10 = this.lookupMap.get(entry.blockPos);
                    if (n10 != null && n9 >= n10) continue;
                    this.lookupMap.put(entry.blockPos, n9);
                    int n11 = this.count3 > 0 ? random.nextInt(this.count3 / 2 + 1) - this.count3 / 4 : 0;
                    this.priorityQueue.add(new Entry3(entry.blockPos, n9, HuntingHandler3.computeInt(entry.blockPos, this.blockPos2) + n11, entry3));
                }
                if (System.nanoTime() - l2 < l) continue;
                break;
            }
            this.finished = this.priorityQueue.isEmpty() || this.count7 >= this.count2;
            return this.checkState();
        }

        public int computeInt() {
            return this.count7 + (this.entry2 == null ? 0 : this.entry2.computeInt());
        }

        private boolean checkState() {
            if (this.finished && this.count > 0 && (this.entry3 == null || HuntingHandler3.computeList(this.entry3).size() >= 50)) {
                this.entry2 = new Entry2(this.blockPos, this.blockPos2, this.count, this.count3, this.count5, this.count5);
                this.finished = false;
            }
            return this.finished;
        }

        public List<BlockPos> computeList() {
            if (!this.finished) {
                throw new IllegalStateException(text);
            }
            return this.entry2 != null ? this.entry2.computeList() : (this.entry3 == null ? Collections.emptyList() : HuntingHandler3.computeList(this.entry3));
        }

        public Entry2(BlockPos blockPos, BlockPos blockPos2, int n, int n2, int n3, int n4) {
            this.blockPos = blockPos;
            this.blockPos2 = HuntingHandler3.computeBlockPos(blockPos2);
            this.count2 = Math.max(1, n);
            this.count3 = n2;
            this.count4 = n3;
            this.count5 = n4;
            this.priorityQueue.add(new Entry3(blockPos, 0, HuntingHandler3.computeInt(blockPos, this.blockPos2), null));
            this.lookupMap.put(blockPos, 0);
        }

        static {
            timestamp = 4004784803525165055L;
            text = "Search is still pending";
        }

        public List<BlockPos> computeList2() {
            if (this.entry2 != null && this.entry2.entry3 != null) {
                return this.entry2.computeList2();
            }
            return this.entry3 == null ? Collections.emptyList() : HuntingHandler3.computeList(this.entry3);
        }
    }

    public static final class Entry {
        public final BlockPos blockPos;
        public final int count;

        public Entry(BlockPos blockPos, int n) {
            this.blockPos = blockPos;
            this.count = n;
        }
    }

    public static final class Entry3 {
        public final BlockPos blockPos;
        public final Entry3 entry3;
        public final int count;
        private final int count2;

        public Entry3(BlockPos blockPos, int n, int n2, Entry3 entry3) {
            this.blockPos = blockPos;
            this.count = n;
            this.count2 = n2;
            this.entry3 = entry3;
        }

        private int computeInt() {
            return this.count + this.count2;
        }
    }
}
