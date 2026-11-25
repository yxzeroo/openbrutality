package org.brutality.util.module;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FourSeasonsType {
    FOUR_SEASONS,
    ELEMENTS,
    GENESIS,
    CASTLE,
    ABYSS,
    UNKNOWN;

    private static FourSeasonsType[] computeFourSeasonsTypeArray() {
        return new FourSeasonsType[]{FOUR_SEASONS, ELEMENTS, GENESIS, CASTLE, ABYSS, UNKNOWN};
    }

    public static FourSeasonsType computeFourSeasonsType(String str) {
        FourSeasonsType[] fourSeasonsTypes;
        int len = (fourSeasonsTypes = values()).length;

        for (int i = 0; i < len; i++) {
            FourSeasonsType fourSeasonsType;
            if ((fourSeasonsType = fourSeasonsTypes[i]).name().equalsIgnoreCase(str)) {
                return fourSeasonsType;
            }
        }

        return null;
    }

    public static List<String> getMapNames() {
        return Stream.of(values()).map(fourSeasonsType -> fourSeasonsType.name().toLowerCase()).collect(Collectors.toList());
    }
}
