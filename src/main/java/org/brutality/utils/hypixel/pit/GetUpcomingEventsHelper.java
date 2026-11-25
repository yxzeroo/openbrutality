package org.brutality.utils.hypixel.pit;

import java.util.ArrayList;
import java.util.List;

public class GetUpcomingEventsHelper {
    private static final long timestamp = 86400000L;
    private static final long timestamp2 = 1761299460000L;
    private static final Entry2[] entry2Array = new Entry2[]{new Entry2("Giant Cake", 0L, "minor"), new Entry2("Dragon Egg", 300000L, "minor"), new Entry2("Raffle", 420000L, "major"), new Entry2("2x Rewards", 1200000L, "minor"), new Entry2("Auction", 1800000L, "minor"), new Entry2("2x Rewards", 2580000L, "minor"), new Entry2("Quick Maths", 3060000L, "minor"), new Entry2("Giant Cake", 3540000L, "minor"), new Entry2("Squads", 3780000L, "major"), new Entry2("2x Rewards", 4740000L, "minor"), new Entry2("All bounty", 5160000L, "minor"), new Entry2("Care Package", 5340000L, "minor"), new Entry2("Auction", 5880000L, "minor"), new Entry2("2x Rewards", 6540000L, "minor"), new Entry2("Auction", 7080000L, "minor"), new Entry2("Rage Pit", 7440000L, "major"), new Entry2("Giant Cake", 8220000L, "minor"), new Entry2("KOTH", 8760000L, "minor"), new Entry2("2x Rewards", 9420000L, "minor"), new Entry2("KOTL", 10200000L, "minor"), new Entry2("Care Package", 10620000L, "minor"), new Entry2("KOTH", 10920000L, "minor"), new Entry2("Raffle", 11280000L, "major"), new Entry2("Care Package", 12060000L, "minor"), new Entry2("Auction", 12360000L, "minor"), new Entry2("KOTL", 13140000L, "minor"), new Entry2("Auction", 13740000L, "minor"), new Entry2("Pizza", 14100000L, "major"), new Entry2("2x Rewards", 15060000L, "minor"), new Entry2("Auction", 15780000L, "minor"), new Entry2("Dragon Egg", 16200000L, "minor"), new Entry2("Giant Cake", 16680000L, "minor"), new Entry2("Dragon Egg", 17100000L, "minor"), new Entry2("Squads", 17220000L, "major"), new Entry2("Care Package", 17940000L, "minor"), new Entry2("2x Rewards", 18420000L, "minor"), new Entry2("All bounty", 18960000L, "minor"), new Entry2("Giant Cake", 19440000L, "minor"), new Entry2("2x Rewards", 19860000L, "minor"), new Entry2("Auction", 20640000L, "minor"), new Entry2("Rage Pit", 21000000L, "major"), new Entry2("KOTL", 21960000L, "minor"), new Entry2("KOTH", 22680000L, "minor"), new Entry2("Quick Maths", 23100000L, "minor"), new Entry2("KOTH", 23460000L, "minor"), new Entry2("Giant Cake", 24120000L, "minor"), new Entry2("Beast", 24360000L, "major"), new Entry2("All bounty", 25140000L, "minor"), new Entry2("Dragon Egg", 25440000L, "minor"), new Entry2("2x Rewards", 25740000L, "minor"), new Entry2("Dragon Egg", 26280000L, "minor"), new Entry2("Giant Cake", 26640000L, "minor"), new Entry2("Auction", 27060000L, "minor"), new Entry2("KOTH", 27600000L, "minor"), new Entry2("All bounty", 28080000L, "minor"), new Entry2("Rage Pit", 28200000L, "major"), new Entry2("KOTH", 28980000L, "minor"), new Entry2("Quick Maths", 29520000L, "minor"), new Entry2("2x Rewards", 30000000L, "minor"), new Entry2("KOTL", 30720000L, "minor"), new Entry2("Dragon Egg", 31140000L, "minor"), new Entry2("Giant Cake", 31500000L, "minor"), new Entry2("Squads", 31740000L, "major"), new Entry2("Dragon Egg", 32460000L, "minor"), new Entry2("Care Package", 32700000L, "minor"), new Entry2("Dragon Egg", 33000000L, "minor"), new Entry2("KOTL", 33420000L, "minor"), new Entry2("2x Rewards", 34080000L, "minor"), new Entry2("Spire", 34440000L, "major"), new Entry2("KOTL", 35400000L, "minor"), new Entry2("Auction", 36120000L, "minor"), new Entry2("Care Package", 36540000L, "minor"), new Entry2("Giant Cake", 37020000L, "minor"), new Entry2("Team Deathmatch", 37260000L, "major"), new Entry2("KOTL", 38040000L, "minor"), new Entry2("2x Rewards", 38760000L, "minor"), new Entry2("KOTH", 39300000L, "minor"), new Entry2("2x Rewards", 39960000L, "minor"), new Entry2("Giant Cake", 40620000L, "minor"), new Entry2("2x Rewards", 41100000L, "minor"), new Entry2("Auction", 41700000L, "minor"), new Entry2("Blockhead", 42060000L, "major"), new Entry2("2x Rewards", 43080000L, "minor"), new Entry2("KOTH", 43800000L, "minor"), new Entry2("KOTL", 44400000L, "minor"), new Entry2("Giant Cake", 45000000L, "minor"), new Entry2("KOTH", 45660000L, "minor"), new Entry2("Squads", 46020000L, "major"), new Entry2("2x Rewards", 46980000L, "minor"), new Entry2("Dragon Egg", 47520000L, "minor"), new Entry2("All bounty", 47760000L, "minor"), new Entry2("Care Package", 47940000L, "minor"), new Entry2("KOTL", 48240000L, "minor"), new Entry2("Giant Cake", 48780000L, "minor"), new Entry2("Robbery", 49020000L, "major"), new Entry2("KOTL", 49740000L, "minor"), new Entry2("KOTH", 50400000L, "minor"), new Entry2("Dragon Egg", 50880000L, "minor"), new Entry2("Quick Maths", 51180000L, "minor"), new Entry2("2x Rewards", 51540000L, "minor"), new Entry2("Blockhead", 51900000L, "major"), new Entry2("KOTH", 52920000L, "minor"), new Entry2("Giant Cake", 53520000L, "minor"), new Entry2("Auction", 54060000L, "minor"), new Entry2("KOTH", 54780000L, "minor"), new Entry2("2x Rewards", 55560000L, "minor"), new Entry2("Spire", 55920000L, "major"), new Entry2("KOTL", 56880000L, "minor"), new Entry2("2x Rewards", 57360000L, "minor"), new Entry2("Care Package", 57900000L, "minor"), new Entry2("2x Rewards", 58200000L, "minor"), new Entry2("KOTH", 58800000L, "minor"), new Entry2("Care Package", 59340000L, "minor"), new Entry2("Pizza", 59460000L, "major"), new Entry2("2x Rewards", 60240000L, "minor"), new Entry2("KOTH", 60900000L, "minor"), new Entry2("Auction", 61560000L, "minor"), new Entry2("KOTL", 62160000L, "minor"), new Entry2("All bounty", 62580000L, "minor"), new Entry2("Spire", 62700000L, "major"), new Entry2("Care Package", 63360000L, "minor"), new Entry2("2x Rewards", 63780000L, "minor"), new Entry2("KOTH", 64440000L, "minor"), new Entry2("2x Rewards", 65220000L, "minor"), new Entry2("Dragon Egg", 65760000L, "minor"), new Entry2("Team Deathmatch", 65880000L, "major"), new Entry2("2x Rewards", 66840000L, "minor"), new Entry2("Giant Cake", 67440000L, "minor"), new Entry2("2x Rewards", 68100000L, "minor"), new Entry2("KOTH", 68760000L, "minor"), new Entry2("Beast", 69120000L, "major"), new Entry2("Auction", 69900000L, "minor"), new Entry2("Giant Cake", 70560000L, "minor"), new Entry2("Dragon Egg", 70920000L, "minor"), new Entry2("Care Package", 71100000L, "minor"), new Entry2("Auction", 71580000L, "minor"), new Entry2("Care Package", 72060000L, "minor"), new Entry2("Robbery", 72180000L, "major"), new Entry2("KOTH", 72900000L, "minor"), new Entry2("Auction", 73680000L, "minor"), new Entry2("Quick Maths", 74100000L, "minor"), new Entry2("Giant Cake", 74640000L, "minor"), new Entry2("Auction", 75240000L, "minor"), new Entry2("Rage Pit", 75600000L, "major"), new Entry2("KOTL", 76380000L, "minor"), new Entry2("Dragon Egg", 76860000L, "minor"), new Entry2("Care Package", 77100000L, "minor"), new Entry2("Quick Maths", 77280000L, "minor"), new Entry2("KOTH", 77580000L, "minor"), new Entry2("2x Rewards", 78300000L, "minor"), new Entry2("All bounty", 78720000L, "minor"), new Entry2("Care Package", 78900000L, "minor"), new Entry2("Blockhead", 79020000L, "major"), new Entry2("KOTH", 79800000L, "minor"), new Entry2("Care Package", 80220000L, "minor"), new Entry2("Auction", 80700000L, "minor"), new Entry2("KOTL", 81300000L, "minor"), new Entry2("All bounty", 81780000L, "minor"), new Entry2("Team Deathmatch", 81900000L, "major"), new Entry2("Auction", 82740000L, "minor"), new Entry2("Care Package", 83160000L, "minor"), new Entry2("KOTL", 83460000L, "minor"), new Entry2("Care Package", 83880000L, "minor"), new Entry2("Giant Cake", 84240000L, "minor"), new Entry2("KOTH", 84660000L, "minor"), new Entry2("Spire", 85020000L, "major"), new Entry2("Dragon Egg", 85740000L, "minor")};

    public static List<Entry> getUpcomingEvents(long l, int n) {
        long l2;
        ArrayList<Entry> arrayList = new ArrayList<Entry>();
        long l3 = l - 1761299460000L;
        block0: for (long i = l2 = l3 / 86400000L; i <= l2 + 2L && arrayList.size() < n; ++i) {
            long l4 = 1761299460000L + i * 86400000L;
            for (Entry2 entry22 : entry2Array) {
                long l5 = l4 + entry22.offset;
                if (l5 <= l) continue;
                arrayList.add(new Entry(entry22.name, l5 - l, l5, entry22.type));
                if (arrayList.size() >= n) continue block0;
            }
        }
        arrayList.sort((entry, entry2) -> Long.compare(entry.getTimestamp(), entry2.getTimestamp()));
        return arrayList.subList(0, Math.min(n, arrayList.size()));
    }

    public static Entry computeEntry(long l) {
        long l2;
        long l3 = l - 1761299460000L;
        for (long i = l2 = l3 / 86400000L; i <= l2 + 1L; ++i) {
            long l4 = 1761299460000L + i * 86400000L;
            for (Entry2 entry2 : entry2Array) {
                long l5;
                if (!entry2.type.equals("major") || (l5 = l4 + entry2.offset) <= l) continue;
                return new Entry(entry2.name, l5 - l, l5, entry2.type);
            }
        }
        return null;
    }

    public static class Entry2 {
        public String name;
        public long offset;
        public String type;

        public Entry2(String string, long l, String string2) {
            this.name = string;
            this.offset = l;
            this.type = string2;
        }
    }

    public static class Entry {
        private final String text;
        private long timestamp;
        private final long timestamp2;
        private final String type;

        public String getType() {
            return this.type;
        }

        public Entry(String string, long l, long l2, String string2) {
            this.text = string;
            this.timestamp = l;
            this.timestamp2 = l2;
            this.type = string2;
        }

        public String getText() {
            return this.text;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public long getTimestamp2() {
            return this.timestamp2;
        }

        public boolean checkState() {
            return this.type.equals("major");
        }

        public void handleLong(long l) {
            this.timestamp = this.timestamp2 - l;
        }
    }
}
