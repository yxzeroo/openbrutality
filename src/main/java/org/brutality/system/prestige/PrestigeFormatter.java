package org.brutality.system.prestige;

public final class PrestigeFormatter {
    public final boolean flag;
    public final int count;
    public final String text;
    public final String text2;
    public final String text3;

    private PrestigeFormatter(boolean flag, int count, String text, String str, String str2) {
        this.flag = flag;
        this.count = count;
        this.text = text;
        this.text2 = str;
        this.text3 = str2;
    }

    public static PrestigeFormatter computePrestigeFormatter(int totalPrestiges, String discordId, String brutalityUsername) {
        return new PrestigeFormatter(true, totalPrestiges, discordId, brutalityUsername, null);
    }

    public static PrestigeFormatter computePrestigeFormatter(String error) {
        return new PrestigeFormatter(false, 0, null, null, error);
    }
}
