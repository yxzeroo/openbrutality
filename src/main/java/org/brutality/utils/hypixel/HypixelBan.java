package org.brutality.utils.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import lombok.Generated;

public class HypixelBan {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("D'd' H'h' m'm' s's'");
    @Expose
    @SerializedName("reason")
    private final HypixelBan.SecurityAlertMode reason;
    @Expose
    @SerializedName("unbanDate")
    private final long unbanDate;

    public String computeString() {
        if (this.isUnbanDate()) {
            return "Permanent";
        }
        if (this.isExpired()) {
            return "Expired";
        }
        long l = this.computeLong();
        long l2 = l / 86400000L;
        l %= 86400000L;
        long l3 = l / 3600000L;
        l %= 3600000L;
        long l4 = l / 60000L;
        l %= 60000L;
        long l5 = l / 1000L;
        return String.format("%dd %dh %dm %ds", l2, l3, l4, l5);
    }

    public boolean isExpired() {
        return this.isUnbanDate() ? false : System.currentTimeMillis() >= this.unbanDate;
    }

    public HypixelBan(HypixelBan.SecurityAlertMode reason, String str) {
        this.reason = reason;
        if (str == null) {
            this.unbanDate = 0L;
        } else {
            long now = System.currentTimeMillis();
            String[] parts = str.split(" ");
            long l = 0L;

            for (String str2 : parts) {
                try {
                    long l2 = Long.parseLong(str2.substring(0, str2.length() - 1));
                    if (str2.endsWith("d")) {
                        l += l2 * 24L * 60L * 60L * 1000L;
                    }

                    if (str2.endsWith("h")) {
                        l += l2 * 60L * 60L * 1000L;
                    }

                    if (str2.endsWith("m")) {
                        l += l2 * 60L * 1000L;
                    }

                    if (str2.endsWith("s")) {
                        l += l2 * 1000L;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            this.unbanDate = now + l;
        }
    }

    public long computeLong() {
        return !this.isUnbanDate() && !this.isExpired() ? this.unbanDate - System.currentTimeMillis() : 0L;
    }

    public boolean isUnbanDate() {
        return this.unbanDate == 0L;
    }

    @Generated
    public long getUnbanDate() {
        return this.unbanDate;
    }

    @Generated
    public HypixelBan.SecurityAlertMode getReason() {
        return this.reason;
    }

    @Generated
    @Override
    public String toString() {
        long unbanDate = this.getUnbanDate();
        HypixelBan.SecurityAlertMode reason = this.getReason();
        return "HypixelBan(reason=" + reason + ", unbanDate=" + unbanDate + ")";
    }

    public static enum SecurityAlertMode {
        @Expose
        @SerializedName("security_alert")
        SECURITY_ALERT("Security Alert", "§c"),
        @Expose
        @SerializedName("security_alert_processed")
        SECURITY_ALERT_PROCCESSED("Security Alert (Processed)", "§6"),
        @Expose
        @SerializedName("cheating")
        CHEATING("Cheating", "§4"),
        @Expose
        @SerializedName("misc")
        MISC("Miscellaneous", "§7");

        private final String name2;
        private final String colorCode2;

        @Generated
        private SecurityAlertMode(String name2, String colorCode2) {
            this.name2 = name2;
            this.colorCode2 = colorCode2;
        }

        @Generated
        public String getColorCode() {
            return this.colorCode2;
        }

        private static HypixelBan.SecurityAlertMode[] computeSecurityAlertModeArray() {
            return new HypixelBan.SecurityAlertMode[]{SECURITY_ALERT, SECURITY_ALERT_PROCCESSED, CHEATING, MISC};
        }

        @Generated
        public String getName() {
            return this.name2;
        }
    }
}
