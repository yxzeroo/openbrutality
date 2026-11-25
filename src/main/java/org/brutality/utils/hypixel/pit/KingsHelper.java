package org.brutality.utils.hypixel.pit;

public final class KingsHelper {
    private static final KingsHelper.Entry[] entryArray = new KingsHelper.Entry[]{
        computeEntry("kings", "§cPIT", -23, 0, -23, 23, 255, 23),
        computeEntry("kings", "§3Sewers", 11, 49, 26, 80, 60, 87, 53, 52, 11, 117, 61, 60),
        computeEntry("kings", "§cPort", -130, 0, 8, -23, 255, 130, -23, 0, 23, 0, 255, 130),
        computeEntry("kings", "§6King's Hallway", -160, 0, -6, -20, 255, 8),
        computeEntry("kings", "§aForest", -23, 0, -160, 0, 255, -23, -160, 0, -160, -23, 255, -6),
        computeEntry("kings", "§bCity", 0, 0, 23, 23, 255, 160, 23, 0, 0, 160, 255, 160),
        computeEntry("kings", "§6Farms", 23, 0, -23, 160, 255, 0, 0, 0, -160, 160, 255, -23),
        computeEntry("corals", "§cPIT", -20, 0, -20, 20, 255, 20),
        computeEntry("corals", "§eShipwreck", -160, 0, 0, -20, 255, 160, -20, 0, 20, 0, 255, 160),
        computeEntry("corals", "§cGeyser", 0, 0, 20, 20, 255, 160, 20, 0, 0, 160, 255, 160),
        computeEntry("corals", "§6Temple", 20, 0, -20, 160, 255, 0, 0, 0, -160, 160, 255, -20),
        computeEntry("corals", "§2Seaweed", -160, 0, -160, -20, 255, 0, -20, 0, -160, 0, 255, -20),
        computeEntry("elemental", "§cPIT", -20, 0, -20, 20, 255, 20),
        computeEntry("elemental", "§fSky", -160, 0, 0, -20, 255, 160, -20, 0, 20, 0, 255, 160),
        computeEntry("elemental", "§cLava", 0, 0, 20, 20, 255, 160, 20, 0, 0, 160, 255, 160),
        computeEntry("elemental", "§7Mountains", 20, 0, -20, 160, 255, 0, 0, 0, -160, 160, 255, -20),
        computeEntry("elemental", "§bWater", -160, 0, -160, -20, 255, 0, -20, 0, -160, 0, 255, -20),
        computeEntry("seasons", "§cPIT", -20, 0, -20, 20, 255, 20),
        computeEntry("seasons", "§dSpring", -160, 0, 0, -20, 255, 160, -20, 0, 20, 0, 255, 160),
        computeEntry("seasons", "§fWinter", 0, 0, 20, 20, 255, 160, 20, 0, 0, 160, 255, 160),
        computeEntry("seasons", "§6Autumn", 20, 0, -20, 160, 255, 0, 0, 0, -160, 160, 255, -20),
        computeEntry("seasons", "§eSummer Cave", -300, 0, -300, 35, 100, -120),
        computeEntry("seasons", "§eSummer", -160, 0, -160, -20, 255, 0, -20, 0, -160, 0, 255, -20),
        computeEntry("genesis", "§cPIT", -20, 0, -20, 20, 255, 20),
        computeEntry("genesis", "§fPalace", -160, 0, 0, -20, 255, 160, -20, 0, 20, 0, 255, 160),
        computeEntry("genesis", "§aGarden", 0, 0, 20, 20, 255, 160, 20, 0, 0, 160, 255, 160),
        computeEntry("genesis", "§cFortress", 20, 0, -20, 160, 255, 0, 0, 0, -160, 160, 255, -20),
        computeEntry("genesis", "§6Badlands", -160, 0, -160, -20, 255, 0, -20, 0, -160, 0, 255, -20)
    };

    public static String computeString(String str, double d, double d2, double d3) {
        if (str != null && Double.isFinite(d) && Double.isFinite(d2) && Double.isFinite(d3)) {
            int n = (int)Math.floor(d);
            int n2 = (int)Math.floor(d2);
            int n3 = (int)Math.floor(d3);

            for (KingsHelper.Entry entry : entryArray) {
                if (entry.text.equals(str)) {
                    for (int i = 0; i < entry.intValueArray.length; i += 6) {
                        int[] intValueArray = entry.intValueArray;
                        if (n >= intValueArray[i]
                            && n2 >= intValueArray[i + 1]
                            && n3 >= intValueArray[i + 2]
                            && n <= intValueArray[i + 3]
                            && n2 <= intValueArray[i + 4]
                            && n3 <= intValueArray[i + 5]) {
                            return entry.text2.substring(0, 2) + "§l" + entry.text2.substring(2);
                        }
                    }
                }
            }

            return "§6§lOutskirts";
        }
        return "§fUnknown";
    }

    private static KingsHelper.Entry computeEntry(String str, String str2, int... ints) {
        return new KingsHelper.Entry(str, str2, ints);
    }

    private KingsHelper() {
    }

    public static final class Entry {
        public final String text;
        public final String text2;
        public final int[] intValueArray;

        public Entry(String text, String str, int[] intValueArray) {
            this.text = text;
            this.text2 = str;
            this.intValueArray = intValueArray;
        }
    }
}
