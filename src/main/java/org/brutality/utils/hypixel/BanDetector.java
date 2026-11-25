package org.brutality.utils.hypixel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;

public class BanDetector {
    private static final Pattern pattern = Pattern.compile("§[0-9a-fA-FkKlLoOrRnN]");
    private static final Pattern pattern2 = Pattern.compile("Ban ID:\\s*#([A-Z0-9]+)", 2);
    private static final Pattern pattern3 = Pattern.compile("Block ID:\\s*#([A-Z0-9]+)", 2);
    private static final Pattern pattern4 = Pattern.compile("(?:(\\d+)d\\s*)?(?:(\\d+)h\\s*)?(?:(\\d+)m\\s*)?(?:(\\d+)s)?", 2);
    private static final Pattern pattern5 = Pattern.compile("temporarily banned for\\s+([\\d]+d\\s+[\\d]+h\\s+[\\d]+m\\s+[\\d]+s)\\s+from this server", 2);
    private static final Pattern pattern6 = Pattern.compile("Reason:\\s*(.+?)(?:\\s*Find out more|\\n|$)", 34);
    private static final Pattern pattern7 = Pattern.compile("temporarily blocked for\\s+([\\d]+d\\s+[\\d]+h\\s+[\\d]+m\\s+[\\d]+s)\\s+from this server", 2);
    private static final Pattern pattern8 = Pattern.compile("lifted on\\s+(.+?)\\s*(?:\\.|read more|https?://|\\n|$)", 34);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    public boolean flag = false;
    private BanDetector.Listener listener;
    private String text = "";
    private long timestamp = 0L;
    private static final long timestamp2 = 30000L;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File file = new File("Brutality/bans.json");
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String computeString(GuiDisconnected gui) {
        String[] parts = new String[]{"reason", "message", "multilineMessage", "disconnectReason", "chatMessage"};

        for (String str : parts) {
            try {
                Field declaredField = GuiDisconnected.class.getDeclaredField(str);
                declaredField.setAccessible(true);
                var obj = declaredField.get(gui);
                if (obj != null) {
                    if (obj instanceof List) {
                        StringBuilder sb = new StringBuilder();

                        for (Object obj2 : (List)obj) {
                            sb.append(obj2.toString()).append("\n");
                        }

                        String str2 = sb.toString();
                        if (!str2.trim().isEmpty()) {
                            return str2;
                        }
                    } else {
                        String str3 = obj.toString();
                        if (!str3.trim().isEmpty()) {
                            return str3;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        StringBuilder sb2 = new StringBuilder();

        for (Field declaredField2 : GuiDisconnected.class.getDeclaredFields()) {
            try {
                declaredField2.setAccessible(true);
                var obj3 = declaredField2.get(gui);
                if (obj3 instanceof String && ((String)obj3).length() > 0 && !((String)obj3).equals("disconnect.disconnected")) {
                    sb2.append(obj3).append("\n");
                } else if (obj3 instanceof List) {
                    for (Object obj4 : (List)obj3) {
                        if (obj4 != null) {
                            sb2.append(obj4.toString()).append("\n");
                        }
                    }
                }
            } catch (Exception ignored2) {
            }
        }

        return sb2.toString();
    }

    public void process() {
        this.flag = false;
        this.scheduledExecutorService.shutdown();
    }

    private String computeString(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group(1).toUpperCase() : "Unknown";
    }

    public void process3() {
        if (!this.flag) {
            this.flag = true;
            this.scheduledExecutorService.scheduleAtFixedRate(() -> {
                try {
                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc == null) {
                        return;
                    }

                    GuiScreen screen = mc.currentScreen;
                    if (screen != null) {
                        String str = this.computeString(screen);
                        if (str != null && !str.trim().isEmpty()) {
                            this.handleString2(str);
                        }
                    }

                    this.process2();
                } catch (Exception ignored) {
                }
            }, 0L, 1L, TimeUnit.SECONDS);
        }
    }

    public void shutdown() {
        this.process();
    }

    private void process4() {
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            if (!file.exists()) {
                try (FileWriter fileWriter = new FileWriter(file)) {
                    gson.toJson(new HashMap(), fileWriter);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public boolean checkState() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.currentScreen instanceof GuiDisconnected) {
                String str = this.stripColorCodes(this.computeString((GuiDisconnected)mc.currentScreen));
                return str != null && (str.toLowerCase().contains("banned") || str.toLowerCase().contains("blocked"));
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleString2(String str) {
        String str2 = this.stripColorCodes(str);
        String str3 = str2.toLowerCase();
        if (str3.contains("ban")
            || str3.contains("block")
            || str3.contains("cheating")
            || str3.contains("unfair game advantages")
            || str3.contains("recovery phase")
            || str3.contains("security appeal")
            || str3.contains("infraction")) {
            String str4 = this.computeString(str2, pattern2);
            if (str4.equals("Unknown")) {
                str4 = this.computeString(str2, pattern3);
            }

            if (str4.equals("Unknown")) {
                str4 = str2;
            }

            long now = System.currentTimeMillis();
            if (!str4.equals(this.text) || now - this.timestamp >= 30000L) {
                this.text = str4;
                this.timestamp = now;
                if (!this.matchesString(str2, str3)) {
                    if (!str3.contains("temporarily blocked") || !str3.contains("recovery phase") && !str3.contains("security appeal was processed")) {
                        if (str3.contains("your account has been blocked")) {
                            String str5 = this.computeString(str2, pattern3);
                            String str6 = this.computeString(str2);
                            HypixelBan hypixelBan = new HypixelBan(HypixelBan.SecurityAlertMode.SECURITY_ALERT, null);
                            this.handleString(str5, str6, BanDetector.TemporaryBanMode.ACCOUNT_BLOCKED, null);
                            if (this.listener != null) {
                                this.listener.handleString2(str5);
                                this.listener.handleHypixelBan2(hypixelBan, str5, str6, BanDetector.TemporaryBanMode.ACCOUNT_BLOCKED);
                            }
                        } else if (str3.contains("you are temporarily banned for")) {
                            String str7 = this.computeString(str2, pattern2);
                            String str8 = this.computeString2(str2, pattern5);
                            String str9 = this.computeString(str2);
                            HypixelBan.SecurityAlertMode securityAlertMode = this.computeSecurityAlertMode(str9);
                            HypixelBan hypixelBan2 = new HypixelBan(securityAlertMode, str8);
                            this.handleString(str7, str9, BanDetector.TemporaryBanMode.TEMPORARY_BAN, str8);
                            if (this.listener != null) {
                                this.listener.handleHypixelBan2(hypixelBan2, str7, str9, BanDetector.TemporaryBanMode.TEMPORARY_BAN);
                            }
                        } else if (str3.contains("banned") && str3.contains("reason:")) {
                            String str10 = this.computeString(str2, pattern2);
                            String str11 = this.computeString(str2);
                            String str12 = this.computeString2(str2, pattern5);
                            HypixelBan.SecurityAlertMode securityAlertMode2 = this.computeSecurityAlertMode(str11);
                            HypixelBan hypixelBan3 = new HypixelBan(securityAlertMode2, str12);
                            BanDetector.TemporaryBanMode temporaryBanMode = str12 == null
                                ? BanDetector.TemporaryBanMode.ACCOUNT_BLOCKED
                                : BanDetector.TemporaryBanMode.TEMPORARY_BAN;
                            this.handleString(str10, str11, temporaryBanMode, str12);
                            if (this.listener != null) {
                                this.listener.handleHypixelBan2(hypixelBan3, str10, str11, temporaryBanMode);
                            }
                        } else {
                            if (str3.contains("cheating") || str3.contains("unfair game advantages")) {
                                String str13 = this.computeString(str2, pattern2);
                                if (str13.equals("Unknown")) {
                                    str13 = this.computeString(str2, pattern3);
                                }

                                String str14 = this.computeString2(str2, pattern5);
                                String str15 = this.computeString(str2);
                                if (str15.equals("Unknown")) {
                                    str15 = "Cheating through the use of unfair game advantages";
                                }

                                HypixelBan.SecurityAlertMode CHEATING = HypixelBan.SecurityAlertMode.CHEATING;
                                HypixelBan hypixelBan4 = new HypixelBan(CHEATING, str14);
                                BanDetector.TemporaryBanMode temporaryBanMode2 = str14 == null
                                    ? BanDetector.TemporaryBanMode.ACCOUNT_BLOCKED
                                    : BanDetector.TemporaryBanMode.TEMPORARY_BAN;
                                this.handleString(str13, str15, temporaryBanMode2, str14);
                                if (this.listener != null) {
                                    this.listener.handleHypixelBan2(hypixelBan4, str13, str15, temporaryBanMode2);
                                }
                            }
                        }
                    } else {
                        String str16 = this.computeString2(str2, pattern7);
                        String str17 = "Security appeal processed - Recovery phase";
                        HypixelBan hypixelBan5 = new HypixelBan(HypixelBan.SecurityAlertMode.SECURITY_ALERT_PROCCESSED, str16);
                        this.handleString("RECOVERY", str17, BanDetector.TemporaryBanMode.RECOVERY_PHASE, str16);
                        if (this.listener != null) {
                            this.listener.handleString2(str16, str17);
                            this.listener.handleHypixelBan2(hypixelBan5, "RECOVERY", str17, BanDetector.TemporaryBanMode.RECOVERY_PHASE);
                        }
                    }
                }
            }
        }
    }

    private Map<String, BanDetector.Entry> computeMap() {
        try {
            if (!file.exists()) {
                return new HashMap<>();
            }
            Object obj;
            try (FileReader fileReader = new FileReader(file)) {
                Type type = (new TypeToken<Map<String, BanDetector.Entry>>() {}).getType();
                var obj2 = gson.fromJson(fileReader, type);
                obj = obj2 != null ? obj2 : new HashMap<>();
            }

            return (Map<String, BanDetector.Entry>)obj;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void handleString(String str, String str2, BanDetector.TemporaryBanMode temporaryBanMode, String str3) {
        try {
            String str4 = this.computeString();
            if (str4 == null) {
                return;
            }

            var map = this.computeMap();
            BanDetector.Entry orDefault = map.getOrDefault(str4, new BanDetector.Entry(str4));
            orDefault.handleEntry2(new BanDetector.Entry2(str, str2, temporaryBanMode, str3));
            map.put(str4, orDefault);

            try (FileWriter fileWriter = new FileWriter(file)) {
                gson.toJson(map, fileWriter);
            }
        } catch (Exception ignored) {
        }
    }

    private boolean matchesString(String str) {
        if (str != null && !str.trim().isEmpty()) {
            String str2 = str.toLowerCase();
            return str2.equals("disconnect.disconnected")
                || str2.equals("menu.disconnect")
                || str2.equals("gui.back")
                || str2.equals("gui.cancel")
                || str2.equals("gui.done")
                || str2.contains("texture")
                || str2.contains("font")
                || str.length() < 3;
        }
        return true;
    }

    private boolean matchesString(String str, String str2) {
        boolean flag = str2.contains("competitive ban")
            || str2.contains("competitive game ban")
            || (str2.contains("can't play pit") || str2.contains("cannot play pit") || str2.contains("can not play pit"))
                && str2.contains("previous infraction");
        if (!flag) {
            return false;
        }
        String str3 = null;
        long l = -1L;
        Matcher matcher = pattern8.matcher(str);
        if (matcher.find()) {
            str3 = matcher.group(1).trim();
            l = this.computeLong(str3);
        }

        if (this.listener != null) {
            this.listener.handleString(str3, l);
        }

        return true;
    }

    private String stripColorCodes(String str) {
        return str == null ? "" : pattern.matcher(str).replaceAll("");
    }

    public CompletableFuture<Boolean> checkConnectionStatus() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc != null && mc.getNetHandler() != null) {
                    try {
                        return mc.getNetHandler().getNetworkManager() != null && mc.getNetHandler().getNetworkManager().isChannelOpen();
                    } catch (Exception e) {
                        return mc.theWorld != null;
                    }
                } else {
                    return false;
                }
            } catch (Exception e2) {
                return false;
            }
        });
    }

    private String computeString(String str) {
        Matcher matcher = pattern6.matcher(str);
        if (matcher.find()) {
            return matcher.group(1).trim().replaceAll("\\.$", "");
        }
        String str2 = str.toLowerCase();
        if (str2.contains("cheating through the use of unfair game advantages")) {
            return "Cheating through the use of unfair game advantages";
        }
        if (str2.contains("unfair game advantages")) {
            return "Unfair game advantages";
        }
        if (str2.contains("cheating")) {
            return "Cheating";
        }
        if (str2.contains("security appeal was processed")) {
            return "Security appeal processed - Recovery phase";
        }
        return !str2.contains("security") && !str2.contains("suspicious activity") ? "Unknown" : "Security alert";
    }

    private String computeString() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return null;
            }
            if (mc.thePlayer != null && mc.thePlayer.getName() != null) {
                return mc.thePlayer.getName();
            }
            return mc.getSession() != null && mc.getSession().getUsername() != null ? mc.getSession().getUsername() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void process2() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return;
            }

            if (mc.getNetHandler() != null && mc.getNetHandler().getNetworkManager() != null && mc.getNetHandler().getNetworkManager().isChannelOpen()) {
            } else {
            }
        } catch (Exception ignored) {
        }
    }

    private String computeString2(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        Pattern pattern2 = Pattern.compile("(\\d+d\\s+\\d+h\\s+\\d+m\\s+\\d+s)", 2);
        Matcher matcher2 = pattern2.matcher(str);
        return matcher2.find() ? matcher2.group(1).trim() : null;
    }

    private long computeLong(String str) {
        if (str != null && !str.trim().isEmpty()) {
            String str2 = str.trim().replaceAll("(?i)(\\d+)(st|nd|rd|th)", "$1").replaceAll(",", "").replaceAll("\\s+", " ").trim();
            String[] parts = new String[]{"yyyy-MM-dd", "yyyy/MM/dd", "MM/dd/yyyy", "dd/MM/yyyy", "MMMM d yyyy", "MMM d yyyy", "d MMMM yyyy", "d MMM yyyy"};

            for (String str3 : parts) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str3, Locale.ENGLISH);
                    simpleDateFormat.setLenient(false);
                    Date date = simpleDateFormat.parse(str2);
                    if (date != null) {
                        return date.getTime() + 86400000L - 1000L;
                    }
                } catch (Exception ignored) {
                }
            }

            return -1L;
        }
        return -1L;
    }

    private HypixelBan.SecurityAlertMode computeSecurityAlertMode(String str) {
        if (str == null) {
            return HypixelBan.SecurityAlertMode.MISC;
        }
        String str2 = str.toLowerCase();
        if (str2.contains("cheating") || str2.contains("unfair game advantages")) {
            return HypixelBan.SecurityAlertMode.CHEATING;
        }
        if (str2.contains("security appeal") || str2.contains("recovery phase")) {
            return HypixelBan.SecurityAlertMode.SECURITY_ALERT_PROCCESSED;
        }
        return !str2.contains("security") && !str2.contains("suspicious activity")
            ? HypixelBan.SecurityAlertMode.MISC
            : HypixelBan.SecurityAlertMode.SECURITY_ALERT;
    }

    private String computeString(GuiScreen screen) {
        StringBuilder sb = new StringBuilder();
        if (screen instanceof GuiDisconnected) {
            String str = this.computeString((GuiDisconnected)screen);
            if (str != null && !str.trim().isEmpty()) {
                sb.append(str).append("\n");
            }
        }

        try {
            for (Class i = screen.getClass(); i != null && i != Object.class; i = i.getSuperclass()) {
                for (Field declaredField : i.getDeclaredFields()) {
                    try {
                        declaredField.setAccessible(true);
                        var obj = declaredField.get(screen);
                        if (obj instanceof String str2) {
                            if (!this.matchesString(str2)) {
                                sb.append(str2).append("\n");
                            }
                        } else if (obj instanceof List) {
                            for (Object obj2 : (List)obj) {
                                if (obj2 != null && !this.matchesString(obj2.toString())) {
                                    sb.append(obj2.toString()).append("\n");
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored2) {
        }

        return sb.toString();
    }

    public void handleString(String str) {
        try {
            if (str == null || str.trim().isEmpty()) {
                return;
            }

            String str2 = this.stripColorCodes(str);
            String str3 = str2.toLowerCase();
            if (!str3.contains("competitive") && !str3.contains("infraction")) {
                return;
            }

            if (str3.contains("read more about competitive")) {
                return;
            }

            long now = System.currentTimeMillis();
            if (str2.equals(this.text) && now - this.timestamp < 30000L) {
                return;
            }

            if (this.matchesString(str2, str3)) {
                this.text = str2;
                this.timestamp = now;
            }
        } catch (Exception ignored) {
        }
    }

    public BanDetector(BanDetector.Listener listener) {
        this.listener = listener;
        this.process4();
    }

    public static class Entry {
        private String username2;
        private List<BanDetector.Entry2> banEvents;

        public Entry(String username2) {
            this.username2 = username2;
            this.banEvents = new ArrayList<>();
        }

        public void handleEntry2(BanDetector.Entry2 entry2) {
            this.banEvents.add(entry2);
        }

        public List<BanDetector.Entry2> getBanEvents() {
            return this.banEvents;
        }

        public String getUsername() {
            return this.username2;
        }
    }

    public static class Entry2 {
        private final String text;
        private final String reason;
        private final BanDetector.TemporaryBanMode temporaryBanMode;
        private final String text2;
        private final long timestamp;
        private final String text3;
        private Long longValue;
        private String text4;
        private String text5;

        public String getText4() {
            return this.text4;
        }

        public String getText5() {
            return this.text5;
        }

        public void handleString(String str) {
            this.longValue = System.currentTimeMillis();
            this.text4 = str;
            this.text5 = BanDetector.simpleDateFormat.format(new Date(this.longValue));
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public BanDetector.TemporaryBanMode getTemporaryBanMode() {
            return this.temporaryBanMode;
        }

        public String getReason() {
            return this.reason;
        }

        public String x() {
            return this.text;
        }

        public Long getLongValue() {
            return this.longValue;
        }

        public String getText3() {
            return this.text3;
        }

        public String y() {
            return this.text2;
        }

        public Entry2(String text, String reason, BanDetector.TemporaryBanMode temporaryBanMode, String str) {
            this.text = text;
            this.reason = reason;
            this.temporaryBanMode = temporaryBanMode;
            this.text2 = str;
            this.timestamp = System.currentTimeMillis();
            this.text3 = BanDetector.simpleDateFormat.format(new Date(this.timestamp));
        }
    }

    public interface Listener {
        void handleHypixelBan2(HypixelBan ban, String banId, String reason, BanDetector.TemporaryBanMode banType);

        void handleString2(String duration, String reason);

        void handleString(String liftDateText, long liftEpoch);

        void handleString2(String blockId);

        void process3();
    }

    public static enum TemporaryBanMode {
        TEMPORARY_BAN("§c", "Temporarily Banned", 16733525),
        ACCOUNT_BLOCKED("§4", "Account Blocked", 11141120),
        RECOVERY_PHASE("§6", "Recovery Phase", 16755200);

        private final String colorCode2;
        private final String displayName2;
        private final int color;

        public String getColorCode() {
            return this.colorCode2;
        }

        private TemporaryBanMode(String colorCode2, String displayName2, int color) {
            this.colorCode2 = colorCode2;
            this.displayName2 = displayName2;
            this.color = color;
        }

        public String getDisplayName() {
            return this.displayName2;
        }

        public int getColor() {
            return this.color;
        }

        private static BanDetector.TemporaryBanMode[] computeTemporaryBanModeArray() {
            return new BanDetector.TemporaryBanMode[]{TEMPORARY_BAN, ACCOUNT_BLOCKED, RECOVERY_PHASE};
        }
    }
}
