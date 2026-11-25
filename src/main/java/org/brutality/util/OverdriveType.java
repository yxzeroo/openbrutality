package org.brutality.util;

public enum OverdriveType {
    OVERDRIVE("OVERDRIVE"),
    BEAST("BEASTMODE"),
    HERMIT("HERMIT"),
    HIGHLANDER("HIGHLANDER"),
    MAGNUM_OPUS("MAGNUM"),
    TO_THE_MOON("TO"),
    UBERSTREAK("UBERSTREAK");

    private final String scoreboardToken;

    public String getScoreboardToken() {
        return this.scoreboardToken;
    }

    private static String computeString(String str) {
        return str.replaceAll("[ _-]", "").toUpperCase();
    }

    public static OverdriveType computeOverdriveType(String str) {
        String str2 = computeString(str);

        for (OverdriveType overdriveType : (OverdriveType[])values().clone()) {
            if (computeString(overdriveType.getScoreboardToken()).equals(str2)) {
                return overdriveType;
            }
        }

        return null;
    }

    private OverdriveType(String str) {
        this.scoreboardToken = str.toUpperCase();
    }

    private static OverdriveType[] computeOverdriveTypeArray() {
        return new OverdriveType[]{OVERDRIVE, BEAST, HERMIT, HIGHLANDER, MAGNUM_OPUS, TO_THE_MOON, UBERSTREAK};
    }

    public String getDisplayName() {
        String str = this.name();
        return String.valueOf(new StringBuilder().append(str.charAt(0)).append(str.substring(1).toLowerCase())).replace("_", " ");
    }
}
