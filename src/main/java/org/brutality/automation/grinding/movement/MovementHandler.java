package org.brutality.automation.grinding.movement;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class MovementHandler {
    public static Set<MovementHandler.MidMode> computeSet() {
        return EnumSet.of(MovementHandler.MidMode.GEYSER, MovementHandler.MidMode.SHIPWRECK, MovementHandler.MidMode.TEMPLE, MovementHandler.MidMode.SEAWEED);
    }

    public static MovementHandler.MidMode computeMidMode(Set<MovementHandler.MidMode> midModes, Map<MovementHandler.MidMode, Integer> map) {
        MovementHandler.MidMode midMode = null;
        int n = -1;

        for (MovementHandler.MidMode midMode2 : MovementHandler.MidMode.values()) {
            if (midModes.contains(midMode2)) {
                int n2 = map.containsKey(midMode2) ? (Integer)map.get(midMode2) : 0;
                if (n2 > n) {
                    midMode = midMode2;
                    n = n2;
                }
            }
        }

        return midMode;
    }

    public static MovementHandler.MidMode computeMidMode(double d, double d2, double d3) {
        if (Math.hypot(d, d2) < d3) {
            return MovementHandler.MidMode.MID;
        }
        if (d >= 0.0) {
            return d2 >= 0.0 ? MovementHandler.MidMode.GEYSER : MovementHandler.MidMode.TEMPLE;
        }
        return d2 >= 0.0 ? MovementHandler.MidMode.SHIPWRECK : MovementHandler.MidMode.SEAWEED;
    }

    public static MovementHandler.MidMode computeMidMode(Set<MovementHandler.MidMode> midModes, MovementHandler.MidMode midMode) {
        MovementHandler.MidMode[] midModes2 = MovementHandler.MidMode.values();
        int n = midMode == null ? 0 : midMode.ordinal() + 1;

        for (int i = 0; i < midModes2.length; i++) {
            MovementHandler.MidMode midMode2 = midModes2[(n + i) % midModes2.length];
            if (midMode2 != MovementHandler.MidMode.MID && midModes.contains(midMode2)) {
                return midMode2;
            }
        }

        return null;
    }

    private MovementHandler() {
    }

    public static Map<MovementHandler.MidMode, Integer> computeMap(Collection<MovementHandler.MidMode> midModes) {
        EnumMap enumMap = new EnumMap<>(MovementHandler.MidMode.class);

        for (MovementHandler.MidMode midMode : MovementHandler.MidMode.values()) {
            enumMap.put(midMode, 0);
        }

        for (MovementHandler.MidMode midMode2 : midModes) {
            enumMap.put(midMode2, (Integer)enumMap.get(midMode2) + 1);
        }

        return enumMap;
    }

    public static enum MidMode {
        MID("PIT", 0, 0),
        GEYSER("Geyser", 48, 48),
        SHIPWRECK("Shipwreck", -48, 48),
        TEMPLE("Temple", 48, -48),
        SEAWEED("Seaweed", -48, -48);

        private final String displayName2;
        private final int searchX;
        private final int searchZ;

        public String getDisplayName() {
            return this.displayName2;
        }

        private MidMode(String displayName2, int searchX, int searchZ) {
            this.displayName2 = displayName2;
            this.searchX = searchX;
            this.searchZ = searchZ;
        }

        private static MovementHandler.MidMode[] computeMidModeArray() {
            return new MovementHandler.MidMode[]{MID, GEYSER, SHIPWRECK, TEMPLE, SEAWEED};
        }

        public int getSearchX() {
            return this.searchX;
        }

        public int getSearchZ() {
            return this.searchZ;
        }
    }
}
