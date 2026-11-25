package org.brutality.alt.altmanager.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.brutality.Brutality;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.AccountStatusStore;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.alt.altmanager.auth.MicrosoftAuthenticator;
import org.brutality.alt.altmanager.auth.SessionHolder;
import org.brutality.alt.altmanager.backup.PitPandaBackup;
import org.brutality.alt.altmanager.utils.StatusToast;
import org.brutality.gui.ChangelogScreen;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.MenuButtonRenderer;
import org.brutality.gui.SearchField;
import org.brutality.gui.chat.AvatarComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.module.impl.visual.Watermark;
import org.brutality.proxy.ProxyAccountStore;
import org.brutality.proxy.ProxyManager;
import org.brutality.proxy.ProxyAccountStore.NoneMode;
import org.brutality.utils.hypixel.BanManager;
import org.brutality.utils.hypixel.pit.NormalizeHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAltManager extends CustomGuiScreen {
    public static final String text = String.valueOf('§');
    private static CustomFontRenderer drawStringWithShadowComponent;
    private static CustomFontRenderer drawStringWithShadowComponent2;
    private static CustomFontRenderer drawStringWithShadowComponent3;
    private static final int count = -15920096;
    private static final int count2 = -15655893;
    private static final int count3 = -11890462;
    private static final int count4 = -1;
    private static final int count5 = -6575427;
    private static final int count6 = -15261134;
    private static final int count7 = -14666934;
    private static final int count8 = -22016;
    private static final int count9 = -5636096;
    private static final String text2 = "https://pitpanda.rocks/api/players/";
    private static final String text3 = "617c9c76-e5a5-4213-a6b9-aded74ae64c8";
    private static final String text4 = "https://minotar.net/avatar/";
    private static final String text5 = "https://api.mojang.com/users/profiles/minecraft/";
    private final GuiScreen guiScreen;
    private StatusToast statusToast;
    private List<GuiAltManager.Entry> entries = new ArrayList<>();
    private List<GuiAltManager.Entry> entries2 = new ArrayList<>();
    public int count10 = 0;
    public double amount;
    public int count11;
    private long timestamp;
    private int count12 = 0;
    public GuiAltManager.Entry entry = null;
    private GuiAltManager.AZMode azMode = GuiAltManager.AZMode.A_Z;
    private GuiAltManager.AllMode2 allMode2 = GuiAltManager.AllMode2.ALL;
    private static Set<String> itemSet = new HashSet<>();
    public static Set<String> itemSet2 = new HashSet<>();
    private static String text6 = null;
    private static Set<String> itemSet3 = new HashSet<>();
    private GuiAltManager.PitMode pitMode = GuiAltManager.PitMode.PIT;
    private int count13 = -1;
    private GuiTextField searchField;
    private String text7 = "";
    private GuiAltManager.Entry3 entry3;
    private GuiAltManager.Entry3 entry32;
    private GuiAltManager.Entry3 entry33;
    private GuiAltManager.Entry3 entry34;
    private GuiAltManager.Entry3 entry35;
    private GuiAltManager.Entry3 entry36;
    private GuiAltManager.Entry3 entry37;
    private GuiAltManager.Entry3 entry38;
    private GuiAltManager.Entry3 entry39;
    private GuiAltManager.Entry3 entry310;
    private GuiAltManager.Entry3 entry311;
    private static int count14 = -1;
    private static final String[] stringArray = new String[]{"Card: Prestige + Level", "Card: Prestige", "Card: Plain"};
    private GuiAltManager.AllMode allMode = GuiAltManager.AllMode.ALL;
    private GuiAltManager.Entry3 entry312;
    private GuiAltManager.Entry3 entry313;
    private GuiAltManager.Entry3 entry314;
    private GuiAltManager.Entry3 entry315;
    private GuiAltManager.Entry3 entry316;
    private GuiAltManager.Entry3 entry317;
    private GuiAltManager.Entry3 entry318;
    public static Map<String, ResourceLocation> lookupMap = new ConcurrentHashMap<>();
    private static Map<String, DynamicTexture> lookupMap2 = new ConcurrentHashMap<>();
    public static Map<String, GuiAltManager.Entry4> lookupMap3 = new ConcurrentHashMap<>();
    private static Map<String, String> lookupMap4 = new ConcurrentHashMap<>();
    private static Set<String> itemSet4 = new HashSet<>();
    private ExecutorService executor = null;
    private CompletableFuture<Void> completableFuture = null;
    private boolean flag = false;
    private long lastClickTime = 0L;
    private GuiAltManager.Entry entry2 = null;
    private static final long timestamp2 = 500L;
    private final GuiAltManager.Entry2 entry22 = new GuiAltManager.Entry2();
    private static final Pattern pattern = Pattern.compile("\\[(?:([IVXLCDM]+)[-–])?(\\d{1,3})\\]", 2);
    private static final int[] intValueArray = new int[]{
        0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 5635925, 5636095, 16733525, 16733695, 16777045, 16777215
    };

    public void handleString3(String str) {
        if (str != null) {
            try {
                GuiAltManager.Entry4 entry4 = lookupMap3.get(str.toLowerCase());
                if (entry4 != null) {
                    boolean flag = false;
                    if (Brutality.getBanManager() != null) {
                        flag = Brutality.getBanManager().matchesString2(str);
                    }

                    if (entry4.flag3 != flag) {
                        GuiAltManager.Entry4 entry42 = new GuiAltManager.Entry4(
                            entry4.text2,
                            entry4.text3,
                            entry4.count,
                            entry4.count2,
                            entry4.text4,
                            entry4.text5,
                            entry4.text6,
                            entry4.text7,
                            entry4.count3,
                            entry4.text8,
                            entry4.text9,
                            entry4.flag,
                            entry4.flag2,
                            entry4.text10,
                            entry4.timestamp2,
                            entry4.amount,
                            entry4.count4,
                            flag,
                            entry4.flag4,
                            entry4.longValue
                        );
                        lookupMap3.put(str.toLowerCase(), entry42);
                        this.handleString(str, entry42);
                        if (!flag && entry4.flag3) {
                            this.statusToast = new StatusToast(String.format("§a%s is now UNBANNED!§r", str), 5000L);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public String computeString2() {
        try {
            if (mc != null && mc.getSession() != null && mc.getSession().getUsername() != null) {
                return mc.getSession().getUsername();
            }
            AccountSession accountSession = AccountManager.getAccountSession();
            return accountSession != null && accountSession.getUsername() != null ? accountSession.getUsername() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private int computeInt(String str) {
        if (str == null) {
            return 0;
        }
        Matcher matcher = pattern.matcher(EnumChatFormatting.getTextWithoutFormattingCodes(str));
        return matcher.find() && matcher.group(1) != null ? this.computeInt2(matcher.group(1).toUpperCase(Locale.ROOT)) : 0;
    }

    public static String computeString(int n) {
        if (n <= 0) {
            return "Any";
        }
        if (n > 50) {
            return String.valueOf(n);
        }
        String[] parts = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        String[] parts2 = new String[]{"", "X", "XX", "XXX", "XL", "L"};
        return parts2[n / 10] + parts[n % 10];
    }

    public static boolean matchesEntry(BanManager.Entry entry) {
        if (entry == null) {
            return false;
        }
        String str = entry.z();
        String str2 = Objects.toString(entry.getReason(), "").toLowerCase(Locale.ROOT);
        return "block".equalsIgnoreCase(str)
            || "recovery".equalsIgnoreCase(str)
            || str2.contains("suspicious activity")
            || str2.contains("security alert")
            || str2.contains("account security");
    }

    private String computeString(AccountSession accountSession) {
        GuiAltManager.Entry4 entry4 = lookupMap3.get(accountSession.getUsername().toLowerCase());
        String str = entry4 != null && entry4.flag ? entry4.computeString5() : accountSession.getUsername();
        return str.replaceAll(text + "[0-9a-fk-or]", "");
    }

    private AccountSession computeAccountSession() {
        if (text6 == null) {
            return null;
        }
        for (AccountSession accountSession : AccountManager.computeArrayList()) {
            if (accountSession.getUsername().equalsIgnoreCase(text6)) {
                return accountSession;
            }
        }

        return null;
    }

    private static int[] computeIntArray(String str) {
        if (str == null) {
            return null;
        }
        int n = str.indexOf(91);
        if (n < 0) {
            return null;
        }
        int n2 = computeInt(str, n);
        int n3 = Math.max(n, str.lastIndexOf(45));
        int n4 = -1;

        for (int i = n3; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i)) && (i == 0 || str.charAt(i - 1) != 167)) {
                n4 = i;
                break;
            }
        }

        int n5 = n4 < 0 ? n2 : computeInt(str, n4);
        if (n2 < 0) {
            n2 = n5;
        }

        return n2 < 0 ? null : new int[]{n2, n5 < 0 ? n2 : n5};
    }

    private void process4() {
        List<AccountSession> accountSessions = (List<AccountSession>)(Object)(AccountManager.computeArrayList());
        LinkedHashMap map = new LinkedHashMap();
        HashSet<String> set = new HashSet<>();

        for (AccountSession accountSession : accountSessions) {
            String str = accountSession.getUsername().toLowerCase();
            AccountSession accountSession2 = (AccountSession)map.get(str);
            if (accountSession2 != null && accountSession.getTimestamp() <= accountSession2.getTimestamp()) {
                set.add(accountSession.getUsername());
            } else {
                if (accountSession2 != null) {
                    set.add(accountSession2.getUsername());
                }

                map.put(str, accountSession);
            }
        }

        int n = accountSessions.size() - map.size();
        if (n == 0) {
            this.statusToast = new StatusToast("§eNo duplicates found§r", 3000L);
        } else {
            try {
                if (Brutality.getBanManager() != null) {
                    for (String str2 : set) {
                        Brutality.getBanManager().handleString(str2);
                    }
                }
            } catch (Exception ignored) {
            }

            AccountManager.clear();

            for (AccountSession accountSession3 : ((Iterable<AccountSession>)(Object)(map.values()))) {
                AccountManager.handleAccountSession(accountSession3);
            }

            AccountManager.save();
            this.entry = null;
            AccountManager.load();
            this.process12();
            this.process14();
            this.statusToast = new StatusToast(String.format("§aRemoved %d duplicate(s)§r", n), 4000L);
        }
    }

    private int computeInt() {
        return this.height - (this.width < 600 ? 89 : (this.width < 940 ? 130 : 75));
    }

    private boolean matchesEntry(GuiAltManager.Entry entry) {
        GuiAltManager.Entry4 entry4 = lookupMap3.get(entry.accountSession.getUsername().toLowerCase());
        if (this.text7 != null && !this.text7.isEmpty()) {
            String str = entry.accountSession.getUsername().toLowerCase();
            String str2 = this.text7.toLowerCase();
            if (!str.contains(str2)) {
                return false;
            }
        }

        switch (this.allMode2) {
            case ALL:
            default:
                break;
            case HERESIES:
                if (entry4 == null || !entry4.isFlag4()) {
                    return false;
                }
                break;
            case UNBANNED:
                if (entry4 == null || entry4.computeBannedMode() != GuiAltManager.BannedMode.UNBANNED) {
                    return false;
                }
                break;
            case BANNED:
                BanManager.Entry entry2 = computeEntry(entry.accountSession.getUsername());
                if (entry2 == null && (entry4 == null || entry4.computeBannedMode() != GuiAltManager.BannedMode.BANNED)) {
                    return false;
                }

                String str3 = computeString(entry2, entry4 == null ? null : entry4.text8);
                if (this.allMode == GuiAltManager.AllMode.SECURITY && !"Security Banned".equals(str3)) {
                    return false;
                }

                if (this.allMode == GuiAltManager.AllMode.TEMPORARY && !"Temporarily Banned".equals(str3)) {
                    return false;
                }
                break;
            case EXPIRED:
                if (entry4 == null || entry4.computeBannedMode() != GuiAltManager.BannedMode.EXPIRED) {
                    return false;
                }
                break;
            case COMPETITIVE:
                if (!AccountStatusStore.matchesString(entry.accountSession.getUsername())) {
                    return false;
                }
                break;
            case BHOPPING:
                if (entry4 == null || entry4.count == 0) {
                    return false;
                }
                break;
            case WIPED:
                if (entry4 == null || !entry4.checkState()) {
                    return false;
                }
        }

        if (this.count13 == 0) {
            if (entry4 == null || entry4.count != 0) {
                return false;
            }
        } else if (this.count13 >= 1 && (entry4 == null || entry4.count != this.count13)) {
            return false;
        }

        return true;
    }

    private void handleInt(int n, int n2) {
        for (GuiAltManager.Entry entry : this.entries2) {
            int n3 = entry.count2 - this.count10;
            if (n3 + entry.count4 > this.getWidth() && n3 < this.computeInt()) {
                entry.handleInt(n, n2, this.count10);
            }
        }

        if (this.entries2.isEmpty() && this.computeInt() > this.getWidth() + 25) {
            this.drawCenteredString(computeDrawStringWithShadowComponent2(), "No accounts match these filters", this.width / 2, this.getWidth() + 16, -6575427);
        }

        if (this.count12 > 0 && this.computeInt() > this.getWidth()) {
            int n4 = this.computeInt() - this.getWidth();
            int n5 = Math.min(n4, Math.max(12, n4 * n4 / (n4 + this.count12)));
            int n6 = this.getWidth() + (int)((double)(this.count10 * (n4 - n5)) / this.count12);
            drawRect(this.width - 5, n6, this.width - 3, n6 + n5, -11890462);
        }
    }

    private void loadProfileImage(String str) {
        if (!lookupMap.containsKey(str.toLowerCase()) && !itemSet4.contains(str.toLowerCase())) {
            CompletableFuture.<BufferedImage>supplyAsync(() -> {
                try {
                    URL url = new URL("https://minotar.net/avatar/" + str + "/64");
                    BufferedImage image = ImageIO.read(url);
                    if (image != null) {
                        return image;
                    }
                } catch (Exception ignored) {
                }

                return null;
            }).thenAcceptAsync(image2 -> {
                if (image2 != null) {
                    mc.addScheduledTask(() -> {
                        try {
                            DynamicTexture dynamicTexture = new DynamicTexture(image2);
                            ResourceLocation dynamicTextureLocation = mc.getTextureManager().getDynamicTextureLocation("pit_profile_" + str.toLowerCase(), dynamicTexture);
                            lookupMap.put(str.toLowerCase(), dynamicTextureLocation);
                            lookupMap2.put(str.toLowerCase(), dynamicTexture);
                        } catch (Exception e) {
                            itemSet4.add(str.toLowerCase());
                        }
                    });
                } else {
                    itemSet4.add(str.toLowerCase());
                }
            });
        }
    }

    public boolean matchesString(String str) {
        return itemSet3.contains(str.toLowerCase());
    }

    public static BanManager.Entry computeEntry(String str) {
        BanManager banManager = Brutality.getBanManager();
        return banManager == null ? null : banManager.computeList(str).stream().filter(BanManager.Entry::isActive).findFirst().orElse(null);
    }

    private String computeString(String str) {
        try {
            String str2 = str.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
            URL url = new URL("https://api.mojang.com/user/profiles/" + str2 + "/names");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                String str3;
                while ((str3 = reader.readLine()) != null) {
                    sb.append(str3);
                }

                reader.close();
                JsonElement element = new JsonParser().parse(sb.toString());
                JsonArray asJsonArray = element.getAsJsonArray();
                if (asJsonArray.size() > 0) {
                    JsonObject asJsonObject = asJsonArray.get(asJsonArray.size() - 1).getAsJsonObject();
                    return asJsonObject.get("name").getAsString();
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private void process10() {
        try {
            File file = new File("brutality", "loggedin_accounts.txt");
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(new FileWriter(file));

            for (String str : itemSet3) {
                writer.println(str);
            }

            writer.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            this.count11 = Math.max(0, Math.min(this.count12, this.count11 - eventDWheel * 36 / 120));
        }
    }

    private GuiAltManager.Entry4 computeEntry4(JsonObject json, String str, String str2) {
        JsonObject json2 = null;

        try {
            if (json != null && json.has("doc") && !json.get("doc").isJsonNull()) {
                json2 = json.getAsJsonObject("doc");
            }
        } catch (Exception ignored) {
        }

        String str3 = "";

        try {
            str3 = json != null && json.has("formattedLevel") ? json.get("formattedLevel").getAsString() : "";
        } catch (Exception ignored2) {
        }

        String str4 = str;

        try {
            str4 = json != null && json.has("formattedName") ? json.get("formattedName").getAsString() : str;
        } catch (Exception ignored3) {
        }

        String str5 = "";

        try {
            if (json2 != null && json2.has("formattedRank")) {
                str5 = json2.get("formattedRank").getAsString();
            }
        } catch (Exception ignored4) {
        }

        String str6 = "";

        try {
            if (json2 != null && json2.has("rank")) {
                str6 = json2.get("rank").getAsString();
            }
        } catch (Exception ignored5) {
        }

        int n = 0;

        try {
            n = json != null && json.has("playtime") ? json.get("playtime").getAsInt() : 0;
        } catch (Exception ignored6) {
        }

        String str7 = text + "c" + text + "lSOON";

        try {
            if (json != null && json.has("status")) {
                str7 = json.get("status").getAsString();
            }
        } catch (Exception ignored7) {
        }

        String str8 = text + "c" + text + "lSOON";
        int n2 = 0;

        try {
            n2 = json != null && json.has("level") ? json.get("level").getAsInt() : 0;
        } catch (Exception ignored8) {
        }

        long l = 0L;

        try {
            l = json != null && json.has("lastLogout") ? json.get("lastLogout").getAsLong() : 0L;
        } catch (Exception ignored9) {
        }

        double d = 0.0;

        try {
            d = json != null && json.has("currentGold") ? json.get("currentGold").getAsDouble() : 0.0;
        } catch (Exception ignored10) {
        }

        int n3 = 0;

        try {
            if (json2 != null && json2.has("renown")) {
                n3 = json2.get("renown").getAsInt();
            }
        } catch (Exception ignored11) {
        }

        int n4 = 0;

        try {
            if (json2 != null && json2.has("prestige")) {
                n4 = json2.get("prestige").getAsInt();
            } else {
                n4 = this.computeInt(str3);
            }
        } catch (Exception e) {
            n4 = this.computeInt(str3);
        }

        String str9 = NormalizeHelper.computeString(json);
        boolean flag = str9 != null;
        if (str9 == null) {
            str9 = "";
        }

        boolean flag2 = NormalizeHelper.matchesJsonObject(json);
        boolean flag3 = false;

        try {
            if (Brutality.getBanManager() != null) {
                flag3 = Brutality.getBanManager().matchesString2(str);
            }

            if (!flag3 && str7 != null && !str7.equals(text + "c" + text + "lSOON")) {
                String str10 = str7.toLowerCase().replaceAll("§.", "");
                if (str10.contains("security") || str10.contains("block") || str10.contains("banned") || str10.contains("suspended")) {
                    flag3 = true;
                }
            }
        } catch (Exception e2) {
            flag3 = false;
        }

        int n5 = computeInt2(str3, n2);
        boolean flag4 = computeInt2(str3, -1) >= 0;
        boolean flag5 = matchesJsonObject(json, "playtime") && (matchesJsonObject(json, "level") || flag4) && (matchesJsonObject(json2, "prestige") || flag4);
        Long val = null;

        try {
            GuiAltManager.Entry4 entry4 = this.computeEntry4(str);
            if (entry4 != null) {
                if (!flag5) {
                    return entry4;
                }

                val = entry4.longValue;
                if (val == null) {
                    boolean flag6 = flag5 && n4 == 0 && n5 == 0 && n == 0;
                    boolean flag7 = entry4.count > 0 && n4 < entry4.count;
                    boolean flag8 = entry4.count3 > 0 && n == 0 && n5 == 0 && n4 == 0;
                    if (flag6 && (flag7 || flag8)) {
                        val = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception ignored12) {
        }

        boolean flag9 = json != null && !json.isJsonNull();
        return new GuiAltManager.Entry4(
            str, str2, n4, n5, str3, str4, str5, str6, n, str7, str8, true, flag, str9, l, d, n3, flag3, flag2, flag9, val
        );
    }

    private static int computeInt(String str, int n) {
        for (int i = Math.min(n, str.length()) - 2; i >= 0; i--) {
            if (str.charAt(i) == 167) {
                int n2 = "0123456789abcdef".indexOf(Character.toLowerCase(str.charAt(i + 1)));
                if (n2 >= 0) {
                    return intValueArray[n2];
                }
            }
        }

        return -1;
    }

    private void process11() {
        if (Brutality.getBanManager() != null) {
            try {
                for (AccountSession accountSession : AccountManager.computeArrayList()) {
                    String username = accountSession.getUsername();
                    if (username != null && !username.isEmpty()) {
                        this.handleString3(username);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void process7() {
        AccountSession accountSession = this.computeAccountSession();
        Rectangle rect = this.computeRectangle(accountSession);
        if (rect != null) {
            CustomFontRenderer drawStringWithShadowComponent = computeDrawStringWithShadowComponent2();
            int n = Mouse.getX() * this.width / mc.displayWidth;
            int n2 = this.height - Mouse.getY() * this.height / mc.displayHeight - 1;
            boolean flag = rect.contains(n, n2);
            this.handleInt(rect.x, rect.y, rect.width, rect.height, accountSession.getUsername(), flag, false);
            int n3 = 20;
            int n4 = rect.x + 6;
            int n5 = rect.y + (rect.height - n3) / 2;
            this.loadProfileImage(accountSession.getUsername());
            ResourceLocation location = lookupMap.get(accountSession.getUsername().toLowerCase());
            if (location != null) {
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(location);
                Gui.drawScaledCustomSizeModalRect(n4, n5, 0.0F, 0.0F, 64, 64, n3, n3, 64.0F, 64.0F);
            } else {
                Gui.drawRect(n4, n5, n4 + n3, n5 + n3, -15064528);
            }

            int n6 = n4 + n3 + 8;
            int fonT_HEIGHT = (int)drawStringWithShadowComponent.getFONT_HEIGHT();
            drawStringWithShadowComponent.drawString("LAST ACCOUNT  ·  click to login", n6, rect.y + 3, flag ? -4602154 : -6575427);
            drawStringWithShadowComponent.drawString(this.computeString(accountSession), n6, rect.y + rect.height - fonT_HEIGHT - 2, -1);
        }
    }

    public GuiAltManager(GuiScreen screen) {
        this(screen, null);
    }

    private void process15() {
        MenuButtonRenderer.handleInt(this.width, this.height, this.getWidth() - 5, this.computeInt());
    }

    private void process20() {
        if (text6 != null) {
            try {
                File file = new File("brutality", "lastaccount.txt");
                file.getParentFile().mkdirs();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(text6);
                fileWriter.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.searchField != null) {
            this.searchField.updateCursorCounter();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mc.currentScreen == this) {
            if (this.searchField != null) {
                this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
            }

            if (mouseButton == 0 && text6 != null) {
                AccountSession accountSession = this.computeAccountSession();
                Rectangle rect = this.computeRectangle(accountSession);
                if (rect != null && rect.contains(mouseX, mouseY)) {
                    this.handleAccountSession(accountSession);
                    return;
                }
            }

            if (!this.flag) {
                if (mouseButton == 0 && mouseY >= this.getWidth() && mouseY < this.computeInt()) {
                    long now = System.currentTimeMillis();

                    for (GuiAltManager.Entry entry : this.entries2) {
                        if (entry.matchesInt7(mouseX, mouseY, this.count10)) {
                            if (entry.matchesInt3(mouseX, mouseY, this.count10)) {
                                mc.displayGuiScreen(
                                    new GuiAltManager.SubScreen(this, entry.accountSession, lookupMap3.get(entry.accountSession.getUsername().toLowerCase()))
                                );
                                return;
                            }

                            if (entry.matchesInt4(mouseX, mouseY, this.count10)) {
                                mc.displayGuiScreen(new GuiAltManager.SubScreen3(this, entry.accountSession.getUsername()));
                                return;
                            }

                            if (entry.matchesInt5(mouseX, mouseY, this.count10)) {
                                GuiAltManager.Entry4 entry4 = lookupMap3.get(entry.accountSession.getUsername().toLowerCase());
                                if (entry4 != null && entry4.checkState()) {
                                    mc.displayGuiScreen(new GuiAltManager.SubScreen7(this, entry.accountSession.getUsername(), entry4));
                                }

                                return;
                            }

                            if (this.entry2 == entry && now - this.lastClickTime <= 500L) {
                                this.handleAccountSession(entry.accountSession);
                                this.entry2 = null;
                                this.lastClickTime = 0L;
                            } else {
                                this.entry = entry;
                                this.process14();
                                this.entry2 = entry;
                                this.lastClickTime = now;
                            }

                            return;
                        }
                    }

                    this.entry = null;
                    this.process14();
                    this.entry2 = null;
                    this.lastClickTime = 0L;
                }
            }
        }
    }

    private Comparator<GuiAltManager.Entry> getComparator() {
        switch (this.azMode) {
            case A_Z:
                return Comparator.comparing(entry -> entry.accountSession.getUsername().toLowerCase());
            case LENGTH:
                return Comparator.comparing(entry2 -> entry2.accountSession.getUsername().length());
            case HIGHEST_PRESTIGE:
                return new Comparator<GuiAltManager.Entry>() {
                    public int compare(GuiAltManager.Entry entry3, GuiAltManager.Entry entry4) {
                        GuiAltManager.Entry4 entry42 = GuiAltManager.lookupMap3.get(entry3.accountSession.getUsername().toLowerCase());
                        GuiAltManager.Entry4 entry43 = GuiAltManager.lookupMap3.get(entry4.accountSession.getUsername().toLowerCase());
                        int n = entry42 != null ? entry42.count : 0;
                        int n2 = entry43 != null ? entry43.count : 0;
                        int n3 = entry42 != null ? GuiAltManager.computeInt2(entry42.text4, entry42.count2) : 0;
                        int n4 = entry43 != null ? GuiAltManager.computeInt2(entry43.text4, entry43.count2) : 0;
                        int n5 = Integer.compare(n2, n);
                        if (n5 != 0) {
                            return n5;
                        }
                        int n6 = Integer.compare(n4, n3);
                        return n6 != 0 ? n6 : entry3.accountSession.getUsername().compareToIgnoreCase(entry4.accountSession.getUsername());
                    }
                };
            case BHOPPING:
                return new Comparator<GuiAltManager.Entry>() {
                    public int compare(GuiAltManager.Entry entry5, GuiAltManager.Entry entry6) {
                        GuiAltManager.Entry4 entry44 = GuiAltManager.lookupMap3.get(entry5.accountSession.getUsername().toLowerCase());
                        GuiAltManager.Entry4 entry45 = GuiAltManager.lookupMap3.get(entry6.accountSession.getUsername().toLowerCase());
                        int n7 = entry44 != null ? entry44.count : 0;
                        int n8 = entry45 != null ? entry45.count : 0;
                        int n9 = entry44 != null ? entry44.count2 : 0;
                        int n10 = entry45 != null ? entry45.count2 : 0;
                        int n11 = Integer.compare(n7, n8);
                        return n11 != 0 ? n11 : Integer.compare(n9, n10);
                    }
                };
            default:
                return Comparator.comparing(entry7 -> entry7.accountSession.getUsername().toLowerCase());
        }
    }

    private int computeInt2(String str) {
        if (str != null && !str.isEmpty()) {
            HashMap<Character, Integer> map = new HashMap<>();
            map.put('I', 1);
            map.put('V', 5);
            map.put('X', 10);
            map.put('L', 50);
            map.put('C', 100);
            int n = 0;

            for (int i = 0; i < str.length(); i++) {
                int orDefault = map.getOrDefault(str.charAt(i), 0);
                int n2 = i + 1 < str.length() ? map.getOrDefault(str.charAt(i + 1), 0) : 0;
                if (n2 > orDefault) {
                    n += n2 - orDefault;
                    i++;
                } else {
                    n += orDefault;
                }
            }

            return n;
        }
        return 0;
    }

    public void handleInt(int n, int n2, int n3, int n4, String str, boolean flag, boolean flag2) {
        int n5 = computeInt2();
        GuiAltManager.Entry4 entry4 = lookupMap3.get(str.toLowerCase(Locale.ROOT));
        int[] ints = n5 != 2 && entry4 != null && entry4.flag ? computeIntArray(entry4.text4) : null;
        int n6 = -8877150;
        int n7 = n6;
        if (ints != null) {
            n6 = ints[0];
            n7 = n5 == 0 ? ints[1] : Watermark.computeColor(new Color(n6), Color.WHITE, 0.22).getRGB();
        }

        if (flag2) {
            n6 = -9972828;
            if (ints == null) {
                n7 = n6;
            }
        }

        MenuButtonRenderer.handleInt(n, n2, n3, n4, flag, n6, n7);
    }

    public void handleInt(int n) {
        this.count13 = n;
        this.process();
        this.process14();
    }

    private int getWidth() {
        return this.width < 600 ? 119 : 105;
    }

    private String computeString() {
        if (this.count13 < 0) {
            return "Prestige: Any";
        }
        return this.count13 == 0 ? "Prestige: None" : "Prestige: " + computeString(this.count13);
    }

    @Override
    public void onGuiClosed() {
        if (this.completableFuture != null && !this.completableFuture.isDone()) {
            this.completableFuture.cancel(true);
        }

        super.onGuiClosed();
    }

    private void process6() {
        GuiAltManager.PitMode[] pitModes = GuiAltManager.PitMode.values();
        int n = Arrays.asList(pitModes).indexOf(this.pitMode);
        this.pitMode = pitModes[(n - 1 + pitModes.length) % pitModes.length];
        this.process();
        this.process14();
    }

    private void process18() {
        int n = Math.max(1, Math.min(4, (this.width - 10) / 190));
        int n2 = 180;
        int n3 = 120;
        int n4 = 10;
        int n5 = n * n2 + (n - 1) * n4;
        int n6 = (this.width - n5) / 2;
        int width = this.getWidth();

        for (int i = 0; i < this.entries2.size(); i++) {
            GuiAltManager.Entry entry = this.entries2.get(i);
            int n7 = i / n;
            int n8 = i % n;
            entry.count = n6 + n8 * (n2 + n4);
            entry.count2 = width + n7 * (n3 + n4);
        }
    }

    private static void process19() {
        try {
            Path path = computePath();
            if (path == null) {
                return;
            }

            Files.createDirectories(path.getParent());
            Files.write(path, Integer.toString(count14).getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    private static int computeInt2() {
        if (count14 < 0) {
            count14 = 0;

            try {
                Path path = computePath();
                if (path != null && Files.isRegularFile(path)) {
                    count14 = Math.max(
                        0, Math.min(stringArray.length - 1, Integer.parseInt(new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim()))
                    );
                }
            } catch (Exception ignored) {
            }
        }

        return count14;
    }

    private void process13() {
        String str = this.azMode.getDisplayName() + " | " + this.allMode2.getDisplayName();
        computeDrawStringWithShadowComponent2().drawString(str, 10, this.height - 42, -6575427);
    }

    private void process21() {
        GuiAltManager.PitMode[] pitModes = GuiAltManager.PitMode.values();
        int n = Arrays.asList(pitModes).indexOf(this.pitMode);
        this.pitMode = pitModes[(n + 1) % pitModes.length];
        this.process();
        this.process14();
    }

    private void process17() {
        if (!this.entries2.isEmpty()) {
            GuiAltManager.Entry entry = this.entries2.get(this.entries2.size() - 1);
            this.count12 = Math.max(0, entry.count2 + entry.count4 + 10 - this.computeInt());
        } else {
            this.count12 = 0;
        }
    }

    private void process9() {
        itemSet.clear();
        itemSet2.clear();
        lookupMap3.clear();
        itemSet4.clear();
        this.process12();
        this.process11();
    }

    public GuiAltManager(GuiScreen guiScreen, StatusToast statusToast) {
        this.guiScreen = guiScreen;
        this.statusToast = statusToast;
    }

    private GuiAltManager.Entry4 computeEntry4(String str) {
        try {
            PitPandaBackup pitPandaBackup = Brutality.getPitPandaBackup();
            if (pitPandaBackup != null && pitPandaBackup.matchesString(str)) {
                PitPandaBackup.Entry entry = pitPandaBackup.computeEntry(str);
                return new GuiAltManager.Entry4(
                    str,
                    entry.getCurrentUsername(),
                    entry.getPrestige(),
                    entry.getLevel(),
                    entry.getFormattedLevel(),
                    entry.getFormattedName(),
                    entry.getFormattedRank(),
                    entry.getRank(),
                    entry.getPlaytime(),
                    entry.getStatus(),
                    entry.getPunishments(),
                    true,
                    entry.isScammerTagged(),
                    entry.getScammerReason(),
                    entry.getLastLogout(),
                    entry.getCurrentGold(),
                    entry.getCurrentRenown(),
                    entry.isBanned(),
                    entry.isHeresyUnlocked(),
                    entry.getWipeDetectedDate()
                );
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private void process16() {
        try {
            File file = new File("brutality", "lastaccount.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                text6 = reader.readLine();
                reader.close();
            }
        } catch (Exception ignored) {
        }
    }

    private void handleString4(String str) {
        text6 = str;
        itemSet3.add(str.toLowerCase());
        this.process20();
        this.process10();
    }

    public static int computeInt2(String str, int n) {
        if (str == null) {
            return n;
        }
        String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(str);
        Matcher matcher = pattern.matcher(textWithoutFormattingCodes);
        return matcher.find() ? Integer.parseInt(matcher.group(2)) : n;
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent() {
        return drawStringWithShadowComponent2 != null ? drawStringWithShadowComponent2 : computeDrawStringWithShadowComponent2();
    }

    private void process8() {
        try {
            File file = new File("brutality", "loggedin_accounts.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String str;
                while ((str = reader.readLine()) != null) {
                    itemSet3.add(str.toLowerCase());
                }

                reader.close();
            }
        } catch (Exception ignored) {
        }
    }

    private void process3() {
        drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(16, false);
        drawStringWithShadowComponent3 = FontComponent.computeDrawStringWithShadowComponent2(28, false);
    }

    private static boolean matchesJsonObject(JsonObject json, String str) {
        if (json != null && json.has(str) && json.get(str).isJsonPrimitive()) {
            try {
                json.get(str).getAsInt();
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private Rectangle computeRectangle(AccountSession accountSession) {
        if (this.width >= 600 && accountSession != null) {
            CustomFontRenderer drawStringWithShadowComponent = computeDrawStringWithShadowComponent2();
            String str = this.computeString(accountSession);
            int n = Math.max(drawStringWithShadowComponent.getStringWidth("LAST ACCOUNT  ·  click to login"), drawStringWithShadowComponent.getStringWidth(str));
            int n2 = 26;
            int n3 = 34 + n + 12;
            int n4 = this.getWidth() - 5;
            return new Rectangle(this.width - n3 - 10, n4 - 4 - n2, n3, n2);
        }
        return null;
    }

    private static Path computePath() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.mcDataDir != null ? mc.mcDataDir.toPath().resolve("Brutality/altmanager-card-tint.txt") : null;
    }

    public int getCount13() {
        return this.count13;
    }

    private void process2() {
        if (this.statusToast != null && !this.statusToast.isExpired()) {
            String message = this.statusToast.getMessage();
            CustomFontRenderer drawStringWithShadowComponent = computeDrawStringWithShadowComponent2();
            int n = 50;
            int n2;
            if (this.width >= 600 && this.searchField != null) {
                int n3 = this.searchField.xPosition - drawStringWithShadowComponent.getStringWidth("Search:") - 15;
                int n4 = 452;
                int n5 = Math.max(30, n3 - n4);
                if (drawStringWithShadowComponent.getStringWidth(message) > n5) {
                    while (message.length() > 1 && drawStringWithShadowComponent.getStringWidth(message + "...") > n5) {
                        message = message.substring(0, message.length() - 1);
                    }

                    message = message + "...";
                }

                n2 = n3 - drawStringWithShadowComponent.getStringWidth(message);
            } else {
                n2 = this.width - drawStringWithShadowComponent.getStringWidth(message) - 10;
            }

            int textWidth = drawStringWithShadowComponent.getStringWidth(message);
            drawRect(n2 - 5, n - 2, n2 + textWidth + 5, n + (int)computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 2, Integer.MIN_VALUE);
            computeDrawStringWithShadowComponent2().drawString(message, n2, n, -1);
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.process3();
        AccountManager.load();
        ProxyAccountStore.load();
        this.process16();
        this.process8();
        itemSet.clear();
        this.process12();
        this.process11();
        boolean flag = this.width < 600;
        int n = Math.min(120, (this.width - 40) / 3);
        int n2 = flag ? 19 : 25;
        int n3 = 10;
        int n4 = flag ? (this.width - 32) / 3 : 100;
        int n5 = n4 + (flag ? 6 : 10);
        this.buttonList.add(this.entry38 = new GuiAltManager.Entry3(10, 10, 45, n4, 20, "Sort: " + this.azMode.getDisplayName(), -22016, -13312));
        this.buttonList.add(this.entry39 = new GuiAltManager.Entry3(11, 10 + n5, 45, n4, 20, "Show: " + this.allMode2.getDisplayName(), -22016, -13312));
        this.buttonList.add(this.entry312 = new GuiAltManager.Entry3(12, 10 + n5 * 2, 45, flag ? n4 : 80, 20, "Refresh", -13447886, -14513374));
        this.buttonList
            .add(this.entry313 = new GuiAltManager.Entry3(18, flag ? 10 : 320, flag ? 70 : 45, flag ? n4 : 120, 20, "Unbanned Soon", -39424, -30720));
        this.buttonList
            .add(this.entry314 = new GuiAltManager.Entry3(13, flag ? 10 + n5 : 10, 70, flag ? n4 : 120, 20, this.computeString(), -22016, -13312));
        this.buttonList
            .add(this.entry310 = new GuiAltManager.Entry3(20, flag ? 10 + n5 * 2 : 140, 70, flag ? n4 : 132, 20, this.allMode.label, -35717, -27751));
        this.entry310.visible = this.allMode2 == GuiAltManager.AllMode2.BANNED;
        this.buttonList.add(this.entry311 = new GuiAltManager.Entry3(21, 280, 70, 170, 20, stringArray[computeInt2()], -5210896, -3562753));
        this.entry311.visible = !flag;
        int n6 = computeDrawStringWithShadowComponent2().getStringWidth("Search:") + 5;
        int n7 = flag ? 10 : this.width - 110 - n6;
        this.searchField = new SearchField(99, n7 + n6, flag ? 94 : 45, 100, 20).setText2("Name...");
        this.searchField.setMaxStringLength(32);
        this.searchField.setFocused(false);
        this.searchField.setText(this.text7);
        int n8 = this.height - (flag ? 63 : (this.width < 940 ? 90 : 35));
        int n9 = n * 3 + n3 * 2;
        int n10 = (this.width - n9) / 2;
        this.buttonList.add(this.entry3 = new GuiAltManager.Entry3(0, n10, n8, n, n2, "Add", -11890462, -10772238));
        this.buttonList.add(this.entry32 = new GuiAltManager.Entry3(1, n10 + n + n3, n8, n, n2, "Direct Login", -11890462, -10772238));
        this.buttonList.add(this.entry33 = new GuiAltManager.Entry3(2, n10 + (n + n3) * 2, n8, n, n2, "Login", -11890462, -10772238));
        int n11 = this.height - (flag ? 85 : (this.width < 940 ? 120 : 65));
        this.buttonList.add(this.entry34 = new GuiAltManager.Entry3(3, n10, n11, n, n2, "Remove", -7667712, -2354116));
        this.buttonList.add(this.entry35 = new GuiAltManager.Entry3(4, n10 + n + n3, n11, n, n2, "Clear List", -7667712, -2354116));
        this.buttonList.add(this.entry36 = new GuiAltManager.Entry3(5, n10 + (n + n3) * 2, n11, n, n2, "Random Alt", -14513374, -13447886));
        int n12 = flag ? (this.width - 26) / 2 : 140;
        this.buttonList
            .add(this.entry315 = new GuiAltManager.Entry3(14, 10, this.height - (flag ? 41 : 30), n12, flag ? 18 : 20, "Account Summary", -5601280, -13312));
        this.buttonList
            .add(
                this.entry316 = new GuiAltManager.Entry3(
                    15, flag ? 16 + n12 : 160, this.height - (flag ? 41 : 30), flag ? n12 : 120, flag ? 18 : 20, "Remove Dupes", -8701952, -39424
                )
            );
        this.buttonList
            .add(
                this.entry317 = new GuiAltManager.Entry3(
                    16,
                    flag ? 10 : this.width - 285,
                    this.height - (flag ? 21 : 30),
                    flag ? n12 : 135,
                    flag ? 18 : 20,
                    "Lock Proxy to Account",
                    -16759672,
                    -16750900
                )
            );
        this.buttonList
            .add(
                this.entry318 = new GuiAltManager.Entry3(
                    17,
                    flag ? 16 + n12 : this.width - 145,
                    this.height - (flag ? 21 : 30),
                    flag ? n12 : 135,
                    flag ? 18 : 20,
                    "Unlock Proxy",
                    -12312064,
                    -7846912
                )
            );
        this.buttonList.add(this.entry37 = new GuiAltManager.Entry3(6, this.width - 80, 10, 70, 20, "Back", -6575427, -1));
        this.process14();
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent2() {
        return drawStringWithShadowComponent != null ? drawStringWithShadowComponent : FontComponent.computeDrawStringWithShadowComponent();
    }

    public static String computeString(BanManager.Entry entry, String str) {
        if (matchesEntry(entry)) {
            return "Security Banned";
        }
        if (entry != null && "temporary".equalsIgnoreCase(entry.z())) {
            return "Temporarily Banned";
        }
        if (entry != null && "permanent".equalsIgnoreCase(entry.z())) {
            return "Permanently Banned";
        }
        String str2 = Objects.toString(str, "").toLowerCase(Locale.ROOT);
        if (str2.contains("security") || str2.contains("account block")) {
            return "Security Banned";
        }
        return str2.contains("temp") && str2.contains("ban") ? "Temporarily Banned" : "Banned";
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiMicrosoftAuth(this));
                break;
            case 1:
                mc.displayGuiScreen(new GuiTokenLogin(this));
                break;
            case 2:
                if (this.entry != null) {
                    this.handleAccountSession(this.entry.accountSession);
                }
                break;
            case 3:
                if (this.entry != null) {
                    String username = this.entry.accountSession.getUsername();
                    AccountManager.remove(this.entry.count5);
                    AccountManager.save();

                    try {
                        PitPandaBackup pitPandaBackup = Brutality.getPitPandaBackup();
                        if (pitPandaBackup != null) {
                            pitPandaBackup.handleString(username);
                        }
                    } catch (Exception ignored) {
                    }

                    try {
                        if (Brutality.getBanManager() != null) {
                            Brutality.getBanManager().handleString(username);
                        }
                    } catch (Exception ignored2) {
                    }

                    this.entry = null;
                    AccountManager.load();
                    this.process12();
                    this.process14();
                }
                break;
            case 4:
                mc.displayGuiScreen(new GuiAltManager.SubScreen4(this));
                break;
            case 5:
                if (!this.entries2.isEmpty()) {
                    int n = (int)(Math.random() * this.entries2.size());
                    this.handleAccountSession(this.entries2.get(n).accountSession);
                }
                break;
            case 6:
                mc.displayGuiScreen(new ChangelogScreen());
            case 7:
            case 8:
            case 9:
            case 19:
            default:
                break;
            case 10:
                this.handleString("Sort accounts", GuiAltManager.AZMode.values(), GuiAltManager.AZMode::getDisplayName, azMode -> this.azMode = azMode);
                break;
            case 11:
                this.handleString("Show accounts", GuiAltManager.AllMode2.values(), GuiAltManager.AllMode2::getDisplayName, allMode2 -> this.allMode2 = allMode2);
                break;
            case 12:
                this.process9();
                break;
            case 13:
                mc.displayGuiScreen(new GuiAltManager.SubScreen5(this));
                break;
            case 14:
                mc.displayGuiScreen(new GuiAltManager.SubScreen2(this));
                break;
            case 15:
                this.process4();
                break;
            case 16:
                if (this.entry != null && ProxyManager.checkState()) {
                    String username2 = this.entry.accountSession.getUsername();
                    ProxyAccountStore.handleString(username2);
                    this.statusToast = new StatusToast(text + "aProxy locked to " + username2 + "!" + text + "r", 3000L);
                    this.process14();
                }
                break;
            case 17:
                if (this.entry != null) {
                    String username3 = this.entry.accountSession.getUsername();
                    ProxyAccountStore.handleString4(username3);
                    String str = text;
                    this.statusToast = new StatusToast(text + "eProxy unlocked from " + username3 + str + "r", 3000L);
                    this.process14();
                }
                break;
            case 18:
                mc.displayGuiScreen(new GuiAltManager.SubScreen6(this));
                break;
            case 20:
                this.handleString("Ban category", GuiAltManager.AllMode.values(), allMode -> allMode.label, allMode3 -> this.allMode = allMode3);
                break;
            case 21:
                count14 = (computeInt2() + 1) % stringArray.length;
                process19();
                this.entry311.displayString = stringArray[count14];
        }
    }

    public void handleString2(String str) {
        if (!lookupMap3.containsKey(str.toLowerCase()) || itemSet2.contains(str.toLowerCase())) {
            CompletableFuture.<GuiAltManager.Entry4>supplyAsync(() -> {
                try {
                    itemSet2.remove(str.toLowerCase());
                    URL url = new URL("https://pitpanda.rocks/api/players/" + str + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        StringBuilder sb = new StringBuilder();

                        String str2;
                        while ((str2 = reader.readLine()) != null) {
                            sb.append(str2);
                        }

                        reader.close();
                        JsonElement element = new JsonParser().parse(sb.toString());
                        JsonObject asJsonObject = element.getAsJsonObject();
                        if (asJsonObject.has("data") && !asJsonObject.get("data").isJsonNull()) {
                            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("data");
                            GuiAltManager.Entry4 entry4 = this.computeEntry4(asJsonObject2, str, null);
                            this.handleString(str, entry4);
                            return entry4;
                        }
                    }

                    String str3 = this.computeString2(str);
                    if (str3 != null) {
                        url = new URL("https://pitpanda.rocks/api/players/" + str3 + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
                        conn = (HttpURLConnection)url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                        conn.connect();
                        if (conn.getResponseCode() == 200) {
                            BufferedReader reader2 = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                            StringBuilder sb2 = new StringBuilder();

                            String str4;
                            while ((str4 = reader2.readLine()) != null) {
                                sb2.append(str4);
                            }

                            reader2.close();
                            JsonElement element2 = new JsonParser().parse(sb2.toString());
                            JsonObject asJsonObject3 = element2.getAsJsonObject();
                            if (asJsonObject3.has("data") && !asJsonObject3.get("data").isJsonNull()) {
                                JsonObject asJsonObject4 = asJsonObject3.getAsJsonObject("data");
                                String str5 = this.computeString(str3);
                                GuiAltManager.Entry4 entry42 = this.computeEntry4(asJsonObject4, str, str5);
                                this.handleString(str, entry42);
                                return entry42;
                            }
                        }
                    }

                    GuiAltManager.Entry4 entry43 = this.computeEntry4(str);
                    if (entry43 != null) {
                        return entry43;
                    }
                    itemSet2.add(str.toLowerCase());
                    return GuiAltManager.Entry4.computeEntry4(str);
                } catch (Exception e) {
                    e.printStackTrace();
                    GuiAltManager.Entry4 entry44 = this.computeEntry4(str);
                    if (entry44 != null) {
                        return entry44;
                    }
                    itemSet2.add(str.toLowerCase());
                    return GuiAltManager.Entry4.computeEntry4(str);
                }
            }).thenAcceptAsync(entry45 -> mc.addScheduledTask(() -> {
                lookupMap3.put(str.toLowerCase(), entry45);
                this.process();
            }));
        }
    }

    public void handleString(String str) {
        if (str != null) {
            try {
                lookupMap3.remove(str.toLowerCase());
                itemSet2.remove(str.toLowerCase());
                this.handleString2(str);
            } catch (Exception ignored) {
            }
        }
    }

    private String computeString2(String str) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + str);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                String str2;
                while ((str2 = reader.readLine()) != null) {
                    sb.append(str2);
                }

                reader.close();
                JsonElement element = new JsonParser().parse(sb.toString());
                JsonObject asJsonObject = element.getAsJsonObject();
                if (asJsonObject.has("id")) {
                    String asString = asJsonObject.get("id").getAsString();
                    lookupMap4.put(str.toLowerCase(), asString);
                    return asString;
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private <T> void handleString(final String str, final T[] vals, final Function<T, String> fn, final Consumer<T> callback) {
        mc.displayGuiScreen(
            new CustomGuiScreen() {
                private int left;
                private int top;
                private int count;
                private int count2;

                @Override
                public void drawScreen(int mouseX, int mouseY, float partialTicks) {
                    GuiAltManager.this.drawScreen(-1, -1, partialTicks);
                    drawRect(0, 0, this.width, this.height, -1476395008);
                    MenuButtonRenderer.handleInt2(this.left, this.top, this.count, 38 + (vals.length + 1) * this.count2);
                    GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str, this.left + 12, this.top + 11, -1);
                    super.drawScreen(mouseX, mouseY, partialTicks);
                }

                @Override
                public void initGui() {
                    this.buttonList.clear();
                    this.count = Math.min(292, this.width - 16);
                    this.count2 = Math.max(14, Math.min(24, (this.height - 55) / (vals.length + 1)));
                    this.left = (this.width - this.count) / 2;
                    this.top = Math.max(4, (this.height - (40 + (vals.length + 1) * this.count2)) / 2);

                    for (int i = 0; i < vals.length; i++) {
                        this.buttonList
                            .add(
                                new GuiAltManager.Entry3(
                                    i,
                                    this.left + 12,
                                    this.top + 30 + i * this.count2,
                                    this.count - 24,
                                    this.count2 - 3,
                                    (String)fn.apply(vals[i]),
                                    -11890462,
                                    -10772238
                                )
                            );
                    }

                    this.buttonList
                        .add(
                            new GuiAltManager.Entry3(
                                vals.length,
                                this.left + 12,
                                this.top + 30 + vals.length * this.count2,
                                this.count - 24,
                                this.count2 - 3,
                                "Back",
                                -11890462,
                                -10772238
                            )
                        );
                }

                @Override
                public void keyTyped(char typedChar, int keyCode) throws IOException {
                    if (keyCode == 1) {
                        mc.displayGuiScreen(GuiAltManager.this);
                    } else {
                        super.keyTyped(typedChar, keyCode);
                    }
                }

                @Override
                public void actionPerformed(GuiButton button) {
                    if (button.id < vals.length) {
                        callback.accept(vals[button.id]);
                        GuiAltManager.this.count11 = GuiAltManager.this.count10 = 0;
                        GuiAltManager.this.amount = 0.0;
                    }

                    mc.displayGuiScreen(GuiAltManager.this);
                }
            }
        );
    }

    private void handleString(String str, GuiAltManager.Entry4 entry4) {
        if (entry4.flag) {
            try {
                PitPandaBackup pitPandaBackup = Brutality.getPitPandaBackup();
                if (pitPandaBackup != null) {
                    PitPandaBackup.Entry entry = new PitPandaBackup.Entry(
                        str,
                        entry4.text3,
                        entry4.count,
                        entry4.count2,
                        entry4.text4,
                        entry4.text5,
                        entry4.text6,
                        entry4.text7,
                        entry4.count3,
                        entry4.text8,
                        entry4.text9,
                        entry4.flag2,
                        entry4.text10,
                        entry4.timestamp2,
                        entry4.amount,
                        entry4.count4,
                        entry4.flag3,
                        entry4.flag4,
                        entry4.longValue
                    );
                    pitPandaBackup.handleString(str, entry);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.nanoTime();
        double d = this.timestamp == 0L ? 0.016666666666666666 : Math.min(0.05, (now - this.timestamp) / 1.0E9);
        this.timestamp = now;
        this.amount = this.amount + (this.count11 - this.amount) * (1.0 - Math.exp(-20.0 * d));
        this.count10 = (int)Math.round(this.amount);
        this.flag = false;

        for (GuiButton button : this.buttonList) {
            if (button.visible && mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height) {
                this.flag = true;
                break;
            }
        }

        this.process15();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.process5();
        this.process7();
        if (this.searchField != null) {
            int n = this.searchField.xPosition - computeDrawStringWithShadowComponent2().getStringWidth("Search:") - 5;
            byte b = 50;
            computeDrawStringWithShadowComponent2().drawString("Search:", n, b, -6575427);
            this.searchField.drawTextBox();
        }

        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        int width = this.getWidth();
        int n2 = this.computeInt();
        GL11.glScissor(0, mc.displayHeight - n2 * scaleFactor, mc.displayWidth, Math.max(0, n2 - width) * scaleFactor);
        this.handleInt(mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (GuiButton button2 : this.buttonList) {
            button2.drawButton(mc, mouseX, mouseY);
        }

        if (this.statusToast != null) {
            this.process2();
        }

        this.process13();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.searchField != null && this.searchField.isFocused()) {
            this.searchField.textboxKeyTyped(typedChar, keyCode);
            String text = this.searchField.getText();
            if (!text.equals(this.text7)) {
                this.text7 = text;
                this.process();
            }
        } else {
            if (keyCode == 1) {
                mc.displayGuiScreen(new ChangelogScreen());
            }
        }
    }

    public void process12() {
        List list = AccountManager.computeArrayList();
        if (list.size() > this.entries.size()) {
            for (int i = this.entries.size(); i < list.size(); i++) {
                AccountSession accountSession = (AccountSession)list.get(i);
                int n = Math.max(1, Math.min(4, (this.width - 10) / 190));
                int n2 = 180;
                int n3 = 120;
                int n4 = 10;
                int n5 = n * n2 + (n - 1) * n4;
                int n6 = (this.width - n5) / 2;
                byte b = 105;
                int n7 = i / n;
                int n8 = i % n;
                int n9 = n6 + n8 * (n2 + n4);
                int n10 = b + n7 * (n3 + n4);
                GuiAltManager.Entry entry = new GuiAltManager.Entry(accountSession, n9, n10, n2, n3, i);
                this.entries.add(entry);
                this.handleString2(accountSession.getUsername());
                this.loadProfileImage(accountSession.getUsername());
                itemSet.add(accountSession.getUsername().toLowerCase());
            }
        } else if (list.size() < this.entries.size()) {
            this.entries.clear();
            int n11 = Math.max(1, Math.min(4, (this.width - 10) / 190));
            short s = 180;
            byte b2 = 120;
            int n12 = 10;
            int n13 = n11 * s + (n11 - 1) * n12;
            int n14 = (this.width - n13) / 2;
            byte b3 = 105;

            for (int j = 0; j < list.size(); j++) {
                AccountSession accountSession2 = (AccountSession)list.get(j);
                int n15 = j / n11;
                int n16 = j % n11;
                int n17 = n14 + n16 * (s + n12);
                int n18 = b3 + n15 * (b2 + n12);
                GuiAltManager.Entry entry2 = new GuiAltManager.Entry(accountSession2, n17, n18, s, b2, j);
                this.entries.add(entry2);
                if (!itemSet.contains(accountSession2.getUsername().toLowerCase())
                    || itemSet2.contains(accountSession2.getUsername().toLowerCase())
                    || !lookupMap3.containsKey(accountSession2.getUsername().toLowerCase())
                    || !lookupMap.containsKey(accountSession2.getUsername().toLowerCase())) {
                    this.handleString2(accountSession2.getUsername());
                    this.loadProfileImage(accountSession2.getUsername());
                    itemSet.add(accountSession2.getUsername().toLowerCase());
                }
            }
        } else {
            int n19 = Math.max(1, Math.min(4, (this.width - 10) / 190));
            short s2 = 180;
            byte b4 = 120;
            byte b5 = 10;
            int n20 = n19 * s2 + (n19 - 1) * b5;
            int n21 = (this.width - n20) / 2;
            byte b6 = 105;

            for (int k = 0; k < this.entries.size(); k++) {
                GuiAltManager.Entry entry3 = this.entries.get(k);
                int n22 = k / n19;
                int n23 = k % n19;
                entry3.x = n21 + n23 * (s2 + b5);
                entry3.y = b6 + n22 * (b4 + b5);
                if (!itemSet.contains(entry3.accountSession.getUsername().toLowerCase())
                    || itemSet2.contains(entry3.accountSession.getUsername().toLowerCase())
                    || !lookupMap3.containsKey(entry3.accountSession.getUsername().toLowerCase())
                    || !lookupMap.containsKey(entry3.accountSession.getUsername().toLowerCase())) {
                    this.handleString2(entry3.accountSession.getUsername());
                    this.loadProfileImage(entry3.accountSession.getUsername());
                    itemSet.add(entry3.accountSession.getUsername().toLowerCase());
                }
            }
        }

        this.process();
    }

    private void process5() {
        CustomFontRenderer drawStringWithShadowComponent = drawStringWithShadowComponent3 != null ? drawStringWithShadowComponent3 : computeDrawStringWithShadowComponent2();
        int textWidth = drawStringWithShadowComponent.getStringWidth("Brutality Account Manager");
        int n = this.width < 600 ? 10 : this.width / 2 - textWidth / 2;
        this.entry22.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, n, 15);
        String str = this.computeString2();
        if (str != null && !str.isEmpty()) {
            String str2 = "Logged in: " + str;
            this.drawCenteredString(computeDrawStringWithShadowComponent2(), str2, this.width / 2, 30, -6575427);
        } else {
            this.drawCenteredString(computeDrawStringWithShadowComponent2(), "No account logged in", this.width / 2, 30, -6575427);
        }
    }

    private void handleAccountSession(AccountSession accountSession) {
        if (this.completableFuture != null && !this.completableFuture.isDone()) {
            this.completableFuture.cancel(true);
        }

        this.executor = ForkJoinPool.commonPool();
        AccountManager.setAccountSession(accountSession);
        String str = accountSession.getUsername() != null ? accountSession.getUsername() : "???";
        String accessToken = accountSession.getAccessToken();
        String refreshToken = accountSession.getRefreshToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            AtomicReference atomicReference = new AtomicReference<>("");
            AtomicReference atomicReference2 = new AtomicReference<>("");
            this.statusToast = new StatusToast(String.format("Fetching your Minecraft profile... (%s)", str), -1L);
            this.completableFuture = MicrosoftAuthenticator.login(accessToken, this.executor).handle((session, t) -> {
                if (session != null) {
                    accountSession.setUsername(session.getUsername());
                    AccountManager.save();
                    SessionHolder.setSession(session);

                    try {
                        Field declaredField = Minecraft.class.getDeclaredField("session");
                        declaredField.setAccessible(true);
                        declaredField.set(mc, session);
                    } catch (Exception ignored) {
                    }

                    this.handleString4(accountSession.getUsername());
                    ProxyAccountStore.handleString2(accountSession.getUsername());
                    this.statusToast = new StatusToast(String.format("Successful login! (%s)", accountSession.getUsername()), 5000L);
                    return true;
                }
                return false;
            }).thenComposeAsync(val -> {
                if (val) {
                    throw new NoSuchElementException();
                }
                this.statusToast = new StatusToast(String.format("Refreshing Microsoft access tokens... (%s)", str), -1L);
                return MicrosoftAuthenticator.refreshMSAccessTokens(refreshToken, this.executor);
            }).thenComposeAsync(map -> {
                this.statusToast = new StatusToast(String.format("Acquiring Xbox access token... (%s)", str), -1L);
                atomicReference.set(map.get("refresh_token"));
                return MicrosoftAuthenticator.acquireXboxAccessToken(map.get("access_token"), this.executor);
            }).thenComposeAsync(str2 -> {
                this.statusToast = new StatusToast(String.format("Acquiring Xbox XSTS token... (%s)", str), -1L);
                return MicrosoftAuthenticator.acquireXboxXstsToken(str2, this.executor);
            }).thenComposeAsync(map2 -> {
                this.statusToast = new StatusToast(String.format("Acquiring Minecraft access token... (%s)", str), -1L);
                return MicrosoftAuthenticator.acquireMCAccessToken(map2.get("Token"), map2.get("uhs"), this.executor);
            }).thenComposeAsync(str3 -> {
                this.statusToast = new StatusToast(String.format("Fetching your Minecraft profile... (%s)", str), -1L);
                atomicReference2.set(str3);
                return MicrosoftAuthenticator.login(str3, this.executor);
            }).thenAccept(session2 -> {
                accountSession.setRefreshToken2((String)atomicReference.get());
                accountSession.setAccessToken((String)atomicReference2.get());
                accountSession.setUsername(session2.getUsername());
                accountSession.setTimestamp(System.currentTimeMillis());
                AccountManager.save();
                SessionHolder.setSession(session2);

                try {
                    Field declaredField2 = Minecraft.class.getDeclaredField("session");
                    declaredField2.setAccessible(true);
                    declaredField2.set(mc, session2);
                } catch (Exception ignored2) {
                }

                this.handleString4(accountSession.getUsername());
                ProxyAccountStore.handleString2(accountSession.getUsername());
                this.statusToast = new StatusToast(String.format("Successful login! (%s)", accountSession.getUsername()), 5000L);
            }).exceptionally(t2 -> {
                if (!(t2.getCause() instanceof NoSuchElementException)) {
                    String message = t2.getMessage();
                    if (message == null && t2.getCause() != null) {
                        message = t2.getCause().getMessage();
                    }

                    if (message == null) {
                        message = "Unknown authentication error";
                    }

                    this.statusToast = new StatusToast(String.format(text + "cLogin failed: %s (%s)" + text + "r", message, accountSession.getUsername()), 5000L);
                }

                return null;
            });
        } else {
            try {
                Session session3 = new Session(str, "", "", "legacy");
                SessionHolder.setSession(session3);

                try {
                    Field declaredField3 = Minecraft.class.getDeclaredField("session");
                    declaredField3.setAccessible(true);
                    declaredField3.set(mc, session3);
                } catch (Exception ignored3) {
                }

                this.handleString4(str);
                ProxyAccountStore.handleString2(str);
                String str4 = text + "aLogged in as " + str + " (Offline)" + text + "r";
                this.statusToast = new StatusToast(str4, 3000L);
            } catch (Exception e) {
                String str5 = e.getMessage() != null ? e.getMessage() : "Unknown error: " + e.getClass().getSimpleName();
                String str6 = text;
                this.statusToast = new StatusToast(text + "cFailed to login: " + str5 + str6 + "r", 5000L);
            }
        }
    }

    public void process14() {
        boolean flag = this.entry != null;
        boolean flag2 = !this.entries2.isEmpty();
        this.entry34.enabled = flag;
        this.entry33.enabled = flag;
        this.entry35.enabled = flag2;
        this.entry36.enabled = flag2;
        if (this.entry317 != null) {
            this.entry317.enabled = flag && ProxyManager.checkState();
        }

        if (this.entry318 != null) {
            this.entry318.enabled = flag && this.entry != null && ProxyAccountStore.matchesString(this.entry.accountSession.getUsername());
        }

        this.entry38.displayString = "Sort: " + this.azMode.getDisplayName();
        this.entry39.displayString = "Show: " + this.allMode2.getDisplayName();
        if (this.entry310 != null) {
            this.entry310.visible = this.allMode2 == GuiAltManager.AllMode2.BANNED;
            this.entry310.displayString = this.allMode.label;
        }

        if (this.entry314 != null) {
            this.entry314.displayString = this.computeString();
        }
    }

    private void process() {
        this.entries2.clear();

        for (GuiAltManager.Entry entry : this.entries) {
            if (this.matchesEntry(entry)) {
                this.entries2.add(entry);
            }
        }

        Collections.sort(this.entries2, this.getComparator());
        this.process18();
        this.process17();
        this.count11 = Math.min(this.count11, this.count12);
        this.amount = Math.min(this.amount, (double)this.count12);
        this.count10 = (int)Math.round(this.amount);
    }

    public static enum AZMode {
        A_Z("A-Z"),
        LENGTH("Length"),
        HIGHEST_PRESTIGE("Highest Prestige"),
        BHOPPING("Bhopping");

        private final String displayName2;

        private static GuiAltManager.AZMode[] computeAZModeArray() {
            return new GuiAltManager.AZMode[]{A_Z, LENGTH, HIGHEST_PRESTIGE, BHOPPING};
        }

        public String getDisplayName() {
            return this.displayName2;
        }

        private AZMode(String displayName2) {
            this.displayName2 = displayName2;
        }
    }

    public static enum AllMode {
        ALL("Any Ban"),
        TEMPORARY("Temporarily Banned"),
        SECURITY("Security Banned");

        public final String label;

        private static GuiAltManager.AllMode[] computeAllModeArray() {
            return new GuiAltManager.AllMode[]{ALL, TEMPORARY, SECURITY};
        }

        private AllMode(String label) {
            this.label = label;
        }
    }

    public static enum AllMode2 {
        ALL("All"),
        UNBANNED("Unbanned"),
        COMPETITIVE("Competitive"),
        BANNED("Banned"),
        EXPIRED("Expired"),
        HERESIES("Heresies"),
        BHOPPING("Bhopping"),
        WIPED("Wiped");

        private final String displayName2;

        private static GuiAltManager.AllMode2[] computeAllMode2Array() {
            return new GuiAltManager.AllMode2[]{ALL, UNBANNED, COMPETITIVE, BANNED, EXPIRED, HERESIES, BHOPPING, WIPED};
        }

        private AllMode2(String displayName2) {
            this.displayName2 = displayName2;
        }

        public String getDisplayName() {
            return this.displayName2;
        }
    }

    public static enum BannedMode {
        BANNED("Banned", -48060),
        UNBANNED("Unbanned", -12255420),
        EXPIRED("Expired", -16751616),
        COMPETITIVE("Comp", -22016),
        INCONCLUSIVE("Inconclusive", -5592406),
        UNSURE("Unsure", -5592406),
        UNKNOWN("Unknown", -7829368);

        private final String displayName2;
        private final int color;

        public int getColor() {
            return this.color;
        }

        public String getDisplayName() {
            return this.displayName2;
        }

        private static GuiAltManager.BannedMode[] computeBannedModeArray() {
            return new GuiAltManager.BannedMode[]{BANNED, UNBANNED, EXPIRED, COMPETITIVE, INCONCLUSIVE, UNSURE, UNKNOWN};
        }

        private BannedMode(String displayName2, int color) {
            this.displayName2 = displayName2;
            this.color = color;
        }
    }

    public class Entry {
        public final AccountSession accountSession;
        public int x;
        public int y;
        public int count;
        public int count2;
        public final int count3;
        public final int count4;
        public final int count5;

        public void handleInt(int n, int n2, int n3) {
            int n4 = this.count2 - n3;
            boolean flag = this.matchesInt7(n, n2, n3);
            String str = GuiAltManager.this.computeString2();
            boolean flag2 = str != null && str.equals(this.accountSession.getUsername());
            CustomFontRenderer drawStringWithShadowComponent = GuiAltManager.computeDrawStringWithShadowComponent();
            GuiAltManager.this.handleInt(this.count, n4, this.count3, this.count4, this.accountSession.getUsername(), flag, flag2);
            int n5 = 64;
            int n6 = this.count + 8;
            int n7 = n4 + 8;
            ResourceLocation location = GuiAltManager.lookupMap.get(this.accountSession.getUsername().toLowerCase());
            if (location != null) {
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GuiAltManager.mc.getTextureManager().bindTexture(location);
                Gui.drawScaledCustomSizeModalRect(n6, n7, 0.0F, 0.0F, n5, n5, n5, n5, n5, n5);
            } else {
                Gui.drawRect(n6, n7, n6 + n5, n7 + n5, -15066598);
                String str2 = "...";
                int textWidth = drawStringWithShadowComponent.getStringWidth(str2);
                drawStringWithShadowComponent.drawString(str2, n6 + (n5 - textWidth) / 2, n7 + (n5 - (int)drawStringWithShadowComponent.getFONT_HEIGHT()) / 2, -6575427);
            }

            String username = this.accountSession.getUsername();
            boolean flag3 = ProxyAccountStore.matchesString(username);
            NoneMode noneMode = ProxyAccountStore.computeNoneMode(username);
            String str3;
            int n8;
            if (flag3) {
                if (noneMode == NoneMode.SHARED_PROXY) {
                    str3 = GuiAltManager.text + "6PROXY SHARED";
                    n8 = -22016;
                } else {
                    str3 = GuiAltManager.text + "aLOCKED";
                    n8 = -16711936;
                }
            } else {
                str3 = GuiAltManager.text + "cNO PROXY";
                n8 = -43691;
            }

            CustomFontRenderer drawStringWithShadowComponent2 = GuiAltManager.computeDrawStringWithShadowComponent();
            String str4 = str3.replaceAll(GuiAltManager.text + "[0-9a-fk-or]", "");
            int n12 = drawStringWithShadowComponent2.getStringWidth(str4) + 8;
            int n13 = 12;
            int n14 = n6 + (n5 - n12) / 2;
            int n15 = n7 + n5 - n13 - 2;
            MenuButtonRenderer.handleInt(n14, n15, n12, n13, n8, 0.0F, true);
            drawStringWithShadowComponent2.drawString(str3, n14 + 4, n15 + 2, n8);
            int var43y = n7 + n5 + 5;
            byte b = 15;
            boolean flag4 = this.matchesInt(n, n2, n3);
            n8 = flag4 ? -10855846 : -15261134;
            MenuButtonRenderer.handleInt(n6, var43y, n5, b, -8144657, flag4 ? 1.0F : 0.0F, true);
            String str5 = "INFO";
            int textWidth2 = drawStringWithShadowComponent.getStringWidth(str5);
            drawStringWithShadowComponent.drawString(str5, n6 + (n5 - textWidth2) / 2, var43y + (b - (int)drawStringWithShadowComponent.getFONT_HEIGHT()) / 2 + 1, -1);
            n12 = n6 + n5 + 8;
            n13 = n4 + 8;
            n14 = (int)drawStringWithShadowComponent.getFONT_HEIGHT() + 2;
            GuiAltManager.Entry4 entry4 = GuiAltManager.lookupMap3.get(this.accountSession.getUsername().toLowerCase());
            if (entry4 != null && entry4.flag) {
                String str6 = entry4.computeString5();
                drawStringWithShadowComponent.drawString(str6, n12, n13, -1);
                int n16 = n13 + n14;
                String str7 = GuiAltManager.text + "7Rank: ";
                String str8 = entry4.computeString7();
                if (str8.equals("NON")) {
                    str7 = str7 + GuiAltManager.text + "7NON";
                } else {
                    str7 = str7 + entry4.computeString9() + str8;
                }

                drawStringWithShadowComponent.drawString(str7, n12, n16, -1);
                n13 = n16 + n14;
                String str9 = GuiAltManager.text
                    + "7Level: "
                    + (entry4.text4 != null && !entry4.text4.isEmpty() ? entry4.text4 : GuiAltManager.text + "7Unknown");
                drawStringWithShadowComponent.drawString(str9, n12, n13, -1);
                n13 += n14;
                String str10 = GuiAltManager.text + "7Gold: " + entry4.computeString8();
                drawStringWithShadowComponent.drawString(str10, n12, n13, -1);
                n13 += n14;
                String str11 = GuiAltManager.text + "7Renown: " + entry4.computeString3();
                drawStringWithShadowComponent.drawString(str11, n12, n13, -1);
                n13 += n14;
                String str12 = GuiAltManager.text + "7Played: " + entry4.computeString10();
                drawStringWithShadowComponent.drawString(str12, n12, n13, -1);
                n13 += n14;
                String str13 = GuiAltManager.text + "7Last Seen: " + entry4.computeString2();
                drawStringWithShadowComponent.drawString(str13, n12, n13, -1);
                n13 += n14;
                boolean flag5 = GuiAltManager.this.matchesString(this.accountSession.getUsername());
                GuiAltManager.BannedMode bannedMode = entry4.computeBannedMode(flag5);
                String str14 = GuiAltManager.text + "7Status: " + entry4.computeString(flag5);
                drawStringWithShadowComponent.drawString(str14, n12, n13, -1);
                n13 += n14;
                this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, n12, n4, n, n2, n3, bannedMode.getColor());
                if (entry4.checkState()) {
                    int n17 = n7 + n5 + 5 + 15 + 3;
                    byte b2 = 64;
                    byte b3 = 15;
                    boolean flag6 = this.matchesInt2(n, n2, n3);
                    short s = -22016;
                    MenuButtonRenderer.handleInt(n6, n17, b2, b3, "WIPED", drawStringWithShadowComponent, s, flag6 ? 1.0F : 0.0F);
                }
            } else if (entry4 != null && !entry4.flag) {
                drawStringWithShadowComponent.drawString(this.accountSession.getUsername(), n12, n13, -1);
                n13 += n14;
                String str15 = GuiAltManager.text + "cFailed to load API";
                drawStringWithShadowComponent.drawString(str15, n12, n13, -1);
            } else {
                drawStringWithShadowComponent.drawString(this.accountSession.getUsername(), n12, n13, -1);
                n13 += n14;
                String str16 = GuiAltManager.text + "7Loading data...";
                drawStringWithShadowComponent.drawString(str16, n12, n13, -1);
            }

            if (entry4 == null || !entry4.flag) {
                BanManager.Entry entry = GuiAltManager.computeEntry(this.accountSession.getUsername());
                if (entry != null) {
                    drawStringWithShadowComponent.drawString(
                        GuiAltManager.text + "7Status: " + GuiAltManager.text + "c" + GuiAltManager.computeString(entry, null),
                        n12,
                        n13 + n14,
                        GuiAltManager.BannedMode.BANNED.getColor()
                    );
                    this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, n12, n4, n, n2, n3, GuiAltManager.BannedMode.BANNED.getColor());
                }
            }
        }

        private void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, int n, int n2, int n3, int n4, int n5, int n6) {
            MenuButtonRenderer.handleInt(n, this.computeInt(n2), 70, 15, "BAN INFO", drawStringWithShadowComponent, n6, this.matchesInt6(n3, n4, n5) ? 1.0F : 0.0F);
        }

        public boolean matchesInt4(int n, int n2, int n3) {
            return this.matchesInt6(n, n2, n3);
        }

        public boolean matchesInt5(int n, int n2, int n3) {
            return this.matchesInt2(n, n2, n3);
        }

        private int computeInt(int n) {
            return n + 8 + ((int)GuiAltManager.computeDrawStringWithShadowComponent().getFONT_HEIGHT() + 2) * 8 - 2;
        }

        public boolean matchesInt(int n, int n2, int n3) {
            int n4 = this.count2 - n3;
            int n5 = this.count + 8;
            int n6 = n4 + 8;
            int n7 = n6 + 64 + 5;
            int n8 = 64;
            byte b = 15;
            return n >= n5 && n <= n5 + n8 && n2 >= n7 && n2 <= n7 + b;
        }

        public boolean matchesInt2(int n, int n2, int n3) {
            GuiAltManager.Entry4 entry4 = GuiAltManager.lookupMap3.get(this.accountSession.getUsername().toLowerCase());
            if (entry4 != null && entry4.checkState()) {
                int n4 = this.count2 - n3;
                int n5 = 64;
                int n6 = this.count + 8;
                int n7 = n4 + 8;
                int n8 = 15;
                int n9 = n7 + n5 + 5 + n8 + 3;
                int n10 = 64;
                int n11 = 15;
                return n >= n6 && n <= n6 + n10 && n2 >= n9 && n2 <= n9 + n11;
            }
            return false;
        }

        public boolean matchesInt3(int n, int n2, int n3) {
            return this.matchesInt(n, n2, n3);
        }

        public Entry(AccountSession accountSession, int count, int n, int n2, int n3, int n4) {
            this.accountSession = accountSession;
            this.x = count;
            this.y = n;
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
            this.count4 = n3;
            this.count5 = n4;
        }

        public boolean matchesInt7(int n, int n2, int n3) {
            int n4 = this.count2 - n3;
            return n >= this.count && n <= this.count + this.count3 && n2 >= n4 && n2 <= n4 + this.count4;
        }

        public boolean matchesInt6(int n, int n2, int n3) {
            GuiAltManager.Entry4 entry4 = GuiAltManager.lookupMap3.get(this.accountSession.getUsername().toLowerCase());
            if ((entry4 == null || !entry4.flag) && GuiAltManager.computeEntry(this.accountSession.getUsername()) == null) {
                return false;
            }
            int n4 = this.count2 - n3;
            int n5 = this.count + 8 + 64 + 8;
            int n6 = this.computeInt(n4);
            int n7 = 70;
            byte b = 15;
            byte b2 = 2;
            return n >= n5 - b2 && n <= n5 + n7 + b2 && n2 >= n6 - b2 && n2 <= n6 + b + b2;
        }
    }

    public static class Entry2 {
        private long lastUpdateTime = System.currentTimeMillis();
        private int count = 0;
        private int count2 = 0;
        private long timestamp = 0L;
        private boolean flag = false;
        private int count3 = 0;
        private static final String text = "Brutality Account Manager";
        private static final int count4 = 150;
        private static final int count5 = 200;
        private static final Color color = new Color(11141120);
        private static final Color color2 = new Color(16733525);
        private static final Color color3 = new Color(16755200);

        private void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, int n, int n2, Color color, Color color2, Color color3, int n3) {
            int n4 = n;

            for (int i = 0; i < "Brutality Account Manager".length(); i++) {
                String str = String.valueOf("Brutality Account Manager".charAt(i));
                Color color4;
                if (i == n3 && n3 < "Brutality Account Manager".length()) {
                    color4 = color3;
                } else {
                    color4 = i < n3 ? color : color2;
                }

                int textWidth = drawStringWithShadowComponent.getStringWidth(str);
                drawStringWithShadowComponent.drawString(str, n4 + 1, n2 + 1, new Color(color4.getRed() / 4, color4.getGreen() / 4, color4.getBlue() / 4, 160).getRGB());
                drawStringWithShadowComponent.drawString(str, n4, n2, -16777216 + (color4.getRGB() & ~-16777216));
                n4 += textWidth;
            }
        }

        public void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, int n, int n2) {
            if (drawStringWithShadowComponent != null) {
                long now = System.currentTimeMillis();
                if (this.flag) {
                    if (now - this.timestamp >= 200L) {
                        this.count3++;
                        if (this.count3 >= 4) {
                            this.flag = false;
                            this.count3 = 0;
                            this.count2 = 0;
                            this.count = 0;
                            this.lastUpdateTime = now;
                        } else {
                            this.timestamp = now;
                        }
                    }

                    Color color4 = this.count3 % 2 == 0 ? color2 : color;
                    this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, n, n2, color4, color4, color4, "Brutality Account Manager".length());
                } else {
                    if (now - this.lastUpdateTime >= 150L) {
                        this.count++;
                        if (this.count > "Brutality Account Manager".length()) {
                            this.count = 0;
                            this.count2++;
                            if (this.count2 >= 2) {
                                this.flag = true;
                                this.timestamp = now;
                                this.count3 = 0;
                                this.count2 = 0;
                                this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, n, n2, color2, color2, color3, "Brutality Account Manager".length());
                                return;
                            }
                        }

                        this.lastUpdateTime = now;
                    }

                    Color color5 = this.count2 == 0 ? color : color2;
                    Color color6 = this.count2 == 0 ? color2 : color;
                    this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, n, n2, color5, color6, color3, this.count);
                }
            }
        }
    }

    public static class Entry3 extends GuiButton {
        private float factor;
        private long timestamp = System.nanoTime();
        private final int count;
        private final int count2;

        public Entry3(int n, int n2, int n3, int n4, int n5, String str, int count, int n6) {
            super(n, n2, n3, n4, n5, str);
            this.count = count;
            this.count2 = n6;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                long now = System.nanoTime();
                float f = Math.min(0.05F, (float)(now - this.timestamp) / 1.0E9F);
                this.timestamp = now;
                this.factor = this.factor + ((this.hovered && this.enabled ? 1.0F : 0.0F) - this.factor) * (1.0F - (float)Math.exp(-14.0F * f));
                MenuButtonRenderer.handleInt(
                    this.xPosition,
                    this.yPosition,
                    this.width,
                    this.height,
                    this.displayString,
                    GuiAltManager.computeDrawStringWithShadowComponent2(),
                    this.count,
                    this.factor,
                    this.enabled
                );
            }
        }
    }

    public static class Entry4 {
        private static final String text = String.valueOf('§');
        public final String text2;
        public final String text3;
        public final int count;
        public final int count2;
        public final String text4;
        public final String text5;
        public final String text6;
        public final String text7;
        public final int count3;
        public final String text8;
        public final String text9;
        public final boolean flag;
        public final long timestamp;
        public final boolean flag2;
        public final String text10;
        public final long timestamp2;
        public final double amount;
        public final int count4;
        public final boolean flag3;
        public final boolean flag4;
        public final boolean flag5;
        public final Long longValue;

        public boolean checkState2() {
            return this.flag && this.longValue != null;
        }

        public Entry4(
            String str,
            String str2,
            int n,
            int n2,
            String str3,
            String str4,
            String str5,
            String str6,
            int n3,
            String str7,
            String str8,
            boolean flag,
            boolean flag2,
            String str9,
            long l,
            double d,
            int n4,
            boolean flag3,
            boolean flag4,
            Long val
        ) {
            this(str, str2, n, n2, str3, str4, str5, str6, n3, str7, str8, flag, flag2, str9, l, d, n4, flag3, flag4, false, val);
        }

        public Entry4(
            String str,
            String str2,
            int count,
            int n,
            String str3,
            String str4,
            String str5,
            String str6,
            int n2,
            String str7,
            String str8,
            boolean flag,
            boolean flag2,
            String str9,
            long l,
            double amount,
            int n3,
            boolean flag3,
            boolean flag4,
            boolean flag5,
            Long longValue
        ) {
            this.text2 = str;
            this.text3 = str2;
            this.count = count;
            this.count2 = n;
            this.text4 = str3;
            this.text5 = str4;
            this.text6 = str5;
            this.text7 = str6;
            this.count3 = n2;
            this.text8 = str7;
            this.text9 = str8;
            this.flag = flag;
            this.timestamp = System.currentTimeMillis();
            this.flag2 = flag2;
            this.text10 = str9;
            this.timestamp2 = l;
            this.amount = amount;
            this.count4 = n3;
            this.flag3 = flag3;
            this.flag4 = flag4;
            this.flag5 = flag5;
            this.longValue = longValue;
        }

        public String computeString9() {
            String str = this.computeString7();
            switch (str) {
                case "YOUTUBE":
                    return text + "c";
                case "SUPERSTAR":
                    return text + "6";
                case "MVP_PLUS":
                    return text + "b";
                case "MVP":
                    return text + "b";
                case "VIP_PLUS":
                    return text + "a";
                case "VIP":
                    return text + "a";
                default:
                    return text + "7";
            }
        }

        public String computeString5() {
            if (this.text5 != null && !this.text5.isEmpty()) {
                String str = this.text5.replaceAll("\\[[^\\]]*\\]\\s*", "");
                if (this.text3 != null && !this.text3.equals(this.text2)) {
                    return str + " " + text + "7(" + this.text2 + ")";
                }
                return str.isEmpty() ? this.computeString9() + this.text2 : str;
            }
            return this.computeString9() + this.text2;
        }

        public String computeString7() {
            if (this.text7 != null && !this.text7.isEmpty()) {
                return this.text7;
            }
            if (this.text6 == null || this.text6.isEmpty()) {
                return "NON";
            }
            if (this.text6.contains("[YOUTUBE]")) {
                return "YOUTUBE";
            }
            if (this.text6.contains("[MVP++]")) {
                return "SUPERSTAR";
            }
            if (this.text6.contains("[MVP+]")) {
                return "MVP_PLUS";
            }
            if (this.text6.contains("[MVP]")) {
                return "MVP";
            }
            if (this.text6.contains("[VIP+]")) {
                return "VIP_PLUS";
            }
            return this.text6.contains("[VIP]") ? "VIP" : "NON";
        }

        public String computeString2() {
            if (this.timestamp2 <= 0L) {
                return text + "7Unknown";
            }
            long now = System.currentTimeMillis();
            long l = now - this.timestamp2;
            long l2 = l / 86400000L;
            if (l2 == 0L) {
                return text + "aToday";
            }
            String str;
            if (l2 <= 90L) {
                str = text + "a";
            } else if (l2 <= 180L) {
                str = text + "e";
            } else if (l2 >= 365L) {
                str = text + "c";
            } else {
                str = text + "6";
            }

            return str + l2 + "d ago";
        }

        public String computeString8() {
            if (this.amount <= 0.0) {
                return text + "60g";
            }
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");
            return text + "6" + decimalFormat.format(this.amount) + "g";
        }

        public String computeString10() {
            if (this.count3 <= 0) {
                return text + "f0h 0m";
            }
            int n = this.count3 / 60;
            int n2 = this.count3 % 60;
            return String.format(text + "f%dh %dm", n, n2);
        }

        public static GuiAltManager.Entry4 computeEntry4(String str) {
            return new GuiAltManager.Entry4(
                str, null, 0, 0, "", "", "", "", 0, text + "c" + text + "lSOON", text + "c" + text + "lSOON", false, false, "", 0L, 0.0, 0, false, false, null
            );
        }

        public String computeString3() {
            return text + "e" + this.count4;
        }

        public String computeString() {
            if (this.longValue == null) {
                return text + "7Unknown";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return text + "e" + simpleDateFormat.format(new Date(this.longValue));
        }

        public boolean checkState() {
            if (!this.flag) {
                return false;
            }
            return this.longValue != null ? true : this.count == 0 && this.count2 == 0 && this.count3 == 0 && this.amount <= 0.0 && this.count4 > 0;
        }

        public String getDisplayName() {
            return this.computeString5();
        }

        public String computeString4() {
            return this.checkState() ? text + "cWIPED!" : null;
        }

        public GuiAltManager.BannedMode computeBannedMode(boolean flag) {
            if (this.flag3 || GuiAltManager.computeEntry(this.text2) != null) {
                return GuiAltManager.BannedMode.BANNED;
            }
            if (AccountStatusStore.matchesString(this.text2)) {
                return GuiAltManager.BannedMode.COMPETITIVE;
            }
            if (AccountStatusStore.matchesString2(this.text2)) {
                return GuiAltManager.BannedMode.UNBANNED;
            }
            try {
                BanManager banManager = Brutality.getBanManager();
                if (banManager != null && banManager.matchesString(this.text2)) {
                    return GuiAltManager.BannedMode.EXPIRED;
                }
            } catch (Exception ignored) {
            }

            return GuiAltManager.BannedMode.INCONCLUSIVE;
        }

        public String computeString6() {
            return this.flag3 ? text + "cBanned" : text + "aUnbanned";
        }

        public String computeString(boolean flag) {
            GuiAltManager.BannedMode bannedMode = this.computeBannedMode(flag);
            switch (bannedMode) {
                case BANNED:
                    return text + "c" + GuiAltManager.computeString(GuiAltManager.computeEntry(this.text2), this.text8);
                case UNBANNED:
                    return text + "a" + bannedMode.getDisplayName();
                case EXPIRED:
                    return text + "2" + bannedMode.getDisplayName();
                case COMPETITIVE:
                    return text + "6" + bannedMode.getDisplayName();
                case INCONCLUSIVE:
                case UNSURE:
                case UNKNOWN:
                default:
                    return text + "7" + bannedMode.getDisplayName();
            }
        }

        public boolean isFlag4() {
            return this.flag4;
        }

        public GuiAltManager.BannedMode computeBannedMode() {
            return this.computeBannedMode(false);
        }

        public Entry4(
            String str,
            String str2,
            int n,
            int n2,
            String str3,
            String str4,
            String str5,
            String str6,
            int n3,
            String str7,
            String str8,
            boolean flag,
            boolean flag2,
            String str9,
            long l,
            double d,
            int n4,
            boolean flag3,
            boolean flag4
        ) {
            this(str, str2, n, n2, str3, str4, str5, str6, n3, str7, str8, flag, flag2, str9, l, d, n4, flag3, flag4, false, null);
        }
    }

    public static enum PitMode {
        PIT("Pit"),
        BEDWARS("Bedwars"),
        SKYWARS("Skywars"),
        SKYBLOCK("Skyblock");

        private final String displayName2;

        private PitMode(String displayName2) {
            this.displayName2 = displayName2;
        }

        public String getDisplayName() {
            return this.displayName2;
        }

        private static GuiAltManager.PitMode[] computePitModeArray() {
            return new GuiAltManager.PitMode[]{PIT, BEDWARS, SKYWARS, SKYBLOCK};
        }
    }

    public static class SubScreen extends CustomGuiScreen {
        private final GuiScreen guiScreen;
        private final AccountSession accountSession;
        private final GuiAltManager.Entry4 entry4;
        private int count;
        private int count2;

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            this.count = Math.max(0, Math.min(this.count2, this.count - Mouse.getEventDWheel() * 20 / 120));
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        private String computeString(long l) {
            if (l <= 0L) {
                return "0m";
            }
            long l2 = l / 86400000L;
            long l3 = l % 86400000L / 3600000L;
            long l4 = l % 3600000L / 60000L;
            StringBuilder sb = new StringBuilder();
            if (l2 > 0L) {
                sb.append(l2).append("d ");
            }

            if (l3 > 0L) {
                sb.append(l3).append("h ");
            }

            if (l4 > 0L || sb.length() == 0) {
                sb.append(l4).append("m");
            }

            return sb.toString().trim();
        }

        public SubScreen(GuiScreen guiScreen, AccountSession accountSession, GuiAltManager.Entry4 entry4) {
            this.guiScreen = guiScreen;
            this.accountSession = accountSession;
            this.entry4 = entry4;
        }

        @Override
        public void actionPerformed(GuiButton button) {
            if (button.id == 0) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.guiScreen != null) {
                this.guiScreen.drawScreen(mouseX, mouseY, partialTicks);
            }

            drawRect(0, 0, this.width, this.height, -1342177280);
            int n = Math.min(350, this.width - 16);
            int n2 = Math.min(this.height - 68, 370);
            int n3 = (this.width - n) / 2;
            int n4 = (this.height - n2) / 2;
            MenuButtonRenderer.handleInt2(n3, n4, n, n2);
            String str = "Account Info: " + this.accountSession.getUsername();
            int textWidth = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str);
            GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str, n3 + (n - textWidth) / 2, n4 + 10, -1);
            int n5 = n4 + 30;
            int n6 = n4 + n2 - 9;
            int scaleFactor = new ScaledResolution(mc).getScaleFactor();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(n3 * scaleFactor, mc.displayHeight - n6 * scaleFactor, n * scaleFactor, Math.max(0, n6 - n5) * scaleFactor);
            int n7 = n5 - this.count;
            int n8 = (int)GuiAltManager.computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 3;
            if (this.entry4 != null && this.entry4.flag) {
                String str2 = GuiAltManager.text + "7Heresy: ";
                if (this.entry4.flag4) {
                    str2 = str2 + GuiAltManager.text + "aTrue";
                } else {
                    str2 = str2 + GuiAltManager.text + "cFalse";
                }

                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str2, n3 + 10, n7, -1);
                int n9 = n7 + n8;
                String str3 = GuiAltManager.text + "7Scammer: ";
                if (!this.entry4.flag2) {
                    str3 = str3 + GuiAltManager.text + "cFalse";
                } else {
                    str3 = str3 + GuiAltManager.text + "c" + (this.entry4.text10 != null && !this.entry4.text10.isEmpty() ? this.entry4.text10 : "True");
                }

                for (String str4 : this.computeList(str3, n - 20)) {
                    GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str4, n3 + 10, n9, -1);
                    n9 += n8;
                }

                n7 = n9 + n8;
                String str5 = GuiAltManager.text
                    + "7Level: "
                    + (this.entry4.text4 != null && !this.entry4.text4.isEmpty() ? this.entry4.text4 : GuiAltManager.text + "7Unknown");
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str5, n3 + 10, n7, -1);
                int n10 = n7 + n8;
                String str6 = GuiAltManager.text + "7Rank: ";
                String str7 = this.entry4.computeString7();
                if (str7.equals("NON")) {
                    str6 = str6 + GuiAltManager.text + "7NON";
                } else {
                    str6 = str6 + this.entry4.computeString9() + str7;
                }

                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str6, n3 + 10, n10, -1);
                n7 = n10 + n8;
                String str8 = GuiAltManager.text + "7Gold: " + this.entry4.computeString8();
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str8, n3 + 10, n7, -1);
                n7 += n8;
                String str9 = GuiAltManager.text + "7Renown: " + this.entry4.computeString3();
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str9, n3 + 10, n7, -1);
                n7 += n8;
                String str10 = GuiAltManager.text + "7Playtime: " + this.entry4.computeString10();
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str10, n3 + 10, n7, -1);
                n7 += n8;
                String str11 = GuiAltManager.text + "7Last Seen: " + this.entry4.computeString2();
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str11, n3 + 10, n7, -1);
                int n11 = n7 + n8;
                PitPandaBackup.Entry entry = GuiAltManager.SubScreen7.computeEntry(this.accountSession.getUsername());
                if (entry != null) {
                    String str12 = "Before wipe: P" + entry.getPrestige() + " / Lv " + GuiAltManager.computeInt2(entry.getFormattedLevel(), entry.getLevel());
                    GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str12, n3 + 10, n11, -22016);
                    n11 += n8;
                }

                String str13 = GuiAltManager.text + "7Status: " + this.entry4.computeString(false);
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str13, n3 + 10, n11, -1);
                int n12 = n11 + n8;
                String username = this.accountSession.getUsername();
                if (AccountStatusStore.matchesString(username)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    GuiAltManager.computeDrawStringWithShadowComponent2()
                        .drawString(GuiAltManager.text + "6Competitive Ban: " + GuiAltManager.text + "cActive", n3 + 10, n12, -1);
                    n7 = n12 + n8;
                    long l = AccountStatusStore.computeLong2(username);
                    String str14 = l > 0L ? simpleDateFormat.format(new Date(l)) : "Unknown";
                    CustomFontRenderer drawStringWithShadowComponent = GuiAltManager.computeDrawStringWithShadowComponent2();
                    String text = GuiAltManager.text;
                    String text2 = GuiAltManager.text;
                    drawStringWithShadowComponent.drawString("  " + text2 + "7Comp banned: " + text + "f" + str14, n3 + 10, n7, -1);
                    int n13 = n7 + n8;
                    long l2 = AccountStatusStore.computeLong(username);
                    String str15 = AccountStatusStore.computeString(username);
                    String str16;
                    if (str15 != null && !str15.isEmpty()) {
                        str16 = str15;
                    } else if (l2 > 0L) {
                        str16 = simpleDateFormat.format(new Date(l2));
                    } else {
                        str16 = "Unknown";
                    }

                    drawStringWithShadowComponent = GuiAltManager.computeDrawStringWithShadowComponent2();
                    String text3 = GuiAltManager.text;
                    String text4 = GuiAltManager.text;
                    drawStringWithShadowComponent.drawString("  " + text4 + "7Uncomped on: " + text3 + "a" + str16, n3 + 10, n13, -1);
                    n12 = n13 + n8;
                    if (l2 > 0L) {
                        long l3 = l2 - System.currentTimeMillis();
                        String str17 = l3 > 0L ? this.computeString(l3) : "Any moment now";
                        drawStringWithShadowComponent = GuiAltManager.computeDrawStringWithShadowComponent2();
                        String text5 = GuiAltManager.text;
                        String text6 = GuiAltManager.text;
                        drawStringWithShadowComponent.drawString("  " + text6 + "7Time left: " + text5 + "e" + str17, n3 + 10, n12, -1);
                        n12 += n8;
                    }
                }

                BanManager banManager = Brutality.getBanManager();
                var list = banManager != null ? banManager.computeList(this.accountSession.getUsername()) : new ArrayList<>();
                String str18 = GuiAltManager.text + "7Bans: " + GuiAltManager.text + "f" + list.size();
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str18, n3 + 10, n12, -1);
                n7 = n12 + n8;
                if (!list.isEmpty()) {
                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    for (int i = 0; i < list.size(); i++) {
                        BanManager.Entry entry2 = (BanManager.Entry)list.get(i);
                        String str19 = GuiAltManager.text + "6Ban " + (i + 1) + ":";
                        String str20 = simpleDateFormat2.format(new Date(entry2.getTimestamp()));
                        String str21 = entry2.getTimestamp2() > 0L ? simpleDateFormat2.format(new Date(entry2.getTimestamp2())) : "Never";
                        String str22 = entry2.x() != null ? "#" + entry2.x() : "Unknown";
                        String str23 = entry2.isActive() ? GuiAltManager.text + "cActive" : GuiAltManager.text + "aExpired";
                        CustomFontRenderer drawStringWithShadowComponent2 = GuiAltManager.computeDrawStringWithShadowComponent2();
                        String text7 = GuiAltManager.text;
                        String text8 = GuiAltManager.text;
                        String text9 = GuiAltManager.text;
                        drawStringWithShadowComponent2.drawString(str19 + " " + text9 + "7" + str20 + " " + text8 + "> " + text7 + "7" + str21 + "  " + str23, n3 + 10, n7, -1);
                        n7 += n8;
                        drawStringWithShadowComponent2 = GuiAltManager.computeDrawStringWithShadowComponent2();
                        String text10 = GuiAltManager.text;
                        String text11 = GuiAltManager.text;
                        drawStringWithShadowComponent2.drawString("  " + text11 + "7ID: " + text10 + "f" + str22, n3 + 10, n7, -1);
                        n7 += n8;
                    }
                }
            } else {
                String str24 = GuiAltManager.text + "cNo data available";
                GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str24, n3 + 10, n7, -1);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            this.count2 = Math.max(0, n7 + this.count - n6 + 8);
            this.count = Math.min(this.count, this.count2);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            this.buttonList.add(new GuiAltManager.Entry3(0, this.width / 2 - 40, this.height - 29, 80, 20, "Close", -7667712, -2354116));
        }

        private List<String> computeList(String str, int n) {
            List list = new ArrayList();
            String[] parts = str.split(" ");
            StringBuilder sb = new StringBuilder();

            for (String str2 : parts) {
                String str3 = sb.length() == 0 ? str2 : sb + " " + str2;
                int textWidth = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str3);
                if (textWidth <= n) {
                    sb.append(sb.length() == 0 ? str2 : " " + str2);
                } else if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb = new StringBuilder(str2);
                } else {
                    list.add(str2);
                }
            }

            if (sb.length() > 0) {
                list.add(sb.toString());
            }

            return list;
        }
    }

    public static class SubScreen2 extends CustomGuiScreen {
        private static final int count = 400;
        private static final int count2 = 16;
        private static final int count3 = 140;
        private final GuiScreen guiScreen;
        private final List<String[]> entries = new ArrayList<>();
        private int count4 = 0;

        @Override
        public void initGui() {
            this.buttonList.clear();
            this.buttonList.add(new GuiAltManager.Entry3(0, this.width / 2 - 55, this.height - 30, 110, 20, "Close", -7667712, -2354116));
            this.process();
        }

        @Override
        public void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 0) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        private void process() {
            this.entries.clear();
            int n = (int)GuiAltManager.computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 4;
            List<AccountSession> accountSessions = (List<AccountSession>)(Object)(AccountManager.computeArrayList());
            int size = accountSessions.size();
            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            var lookupMap3 = GuiAltManager.lookupMap3;

            for (AccountSession accountSession : accountSessions) {
                GuiAltManager.Entry4 entry4 = (GuiAltManager.Entry4)lookupMap3.get(accountSession.getUsername().toLowerCase());
                if (entry4 != null && entry4.flag) {
                    if (entry4.flag4) {
                        n2++;
                    }

                    if (entry4.flag3) {
                        n3++;
                    } else {
                        n4++;
                    }

                    if (entry4.checkState()) {
                        n5++;
                    }

                    if (entry4.count == 0) {
                        n6++;
                    }
                }
            }

            this.entries.add(new String[]{null, null, null});
            this.entries.add(new String[]{"", GuiAltManager.text + "6── Account Summary ──", String.valueOf(-13312)});
            this.entries.add(new String[]{null, null, null});
            this.entries.add(new String[]{"Total Accounts", String.valueOf(size), String.valueOf(-11141121)});
            this.entries.add(new String[]{"Heresies", String.valueOf(n2), String.valueOf(-5635926)});
            this.entries.add(new String[]{null, null, null});
            this.entries.add(new String[]{"", GuiAltManager.text + "7Show Only Breakdown", String.valueOf(-6575427)});
            this.entries.add(new String[]{null, null, null});
            this.entries.add(new String[]{"All", String.valueOf(size), String.valueOf(-11141121)});
            this.entries.add(new String[]{"Unbanned", String.valueOf(n4), String.valueOf(-11141291)});
            this.entries.add(new String[]{"Competitive", "N/A", String.valueOf(-7829368)});
            this.entries.add(new String[]{"Banned", String.valueOf(n3), String.valueOf(-48060)});
            this.entries.add(new String[]{"Heresies", String.valueOf(n2), String.valueOf(-5635926)});
            this.entries.add(new String[]{"Bhopping", String.valueOf(n6), String.valueOf(-3368602)});
            this.entries.add(new String[]{"Wiped", String.valueOf(n5), String.valueOf(-22016)});
            this.count4 = 0;

            for (String[] parts : this.entries) {
                this.count4 = this.count4 + (parts[0] == null ? 6 : n);
            }
        }

        public SubScreen2(GuiScreen guiScreen) {
            this.guiScreen = guiScreen;
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.guiScreen != null) {
                this.guiScreen.drawScreen(mouseX, mouseY, partialTicks);
            }

            drawRect(0, 0, this.width, this.height, -1342177280);
            int n = (int)GuiAltManager.computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 4;
            int n2 = 24;
            int n3 = this.height - 60;
            int n4 = Math.min(this.count4 + n2 + 16, n3);
            int n5 = (this.width - 400) / 2;
            int n6 = (this.height - n4) / 2;
            Gui.drawRect(n5, n6, n5 + 400, n6 + n4, -12961222);
            Gui.drawRect(n5, n6, n5 + 400, n6 + 2, -22016);
            Gui.drawRect(n5, n6 + n4 - 2, n5 + 400, n6 + n4, -22016);
            Gui.drawRect(n5, n6, n5 + 2, n6 + n4, -22016);
            Gui.drawRect(n5 + 400 - 2, n6, n5 + 400, n6 + n4, -22016);
            String str = GuiAltManager.text + "6Account Summary";
            int textWidth = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str);
            GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str, n5 + (400 - textWidth) / 2, n6 + 8, -1);
            Gui.drawRect(n5 + 2, n6 + n2 - 2, n5 + 400 - 2, n6 + n2 - 1, -11184811);
            int n7 = n6 + n2 + 6;
            int n8 = n5 + 16;
            int n9 = n8 + 140;

            for (String[] parts : this.entries) {
                if (parts[0] == null) {
                    Gui.drawRect(n5 + 16, n7 + 1, n5 + 400 - 16, n7 + 2, -11184811);
                    n7 += 6;
                } else {
                    String str2 = parts[0];
                    String str3 = parts[1];
                    int n10 = Integer.parseInt(parts[2]);
                    if (!str2.isEmpty()) {
                        GuiAltManager.computeDrawStringWithShadowComponent2().drawString(GuiAltManager.text + "7" + str2 + ":", n8, n7, -6575427);
                    }

                    int n11 = str2.isEmpty() && str3.startsWith(GuiAltManager.text + "6──")
                        ? n5 + (400 - GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str3)) / 2
                        : n9;
                    GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str3, n11, n7, n10);
                    n7 += n;
                }
            }

            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public static class SubScreen3 extends CustomGuiScreen {
        private int count;
        private static final int count2 = 12;
        private static final int count3 = 72;
        private final GuiScreen guiScreen;
        private final String text;
        private int count4 = 0;
        private int count5 = 0;
        private final List<String[]> entries = new ArrayList<>();
        private int count6 = 0;

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        private List<String> computeList(String str, int n) {
            List list = new ArrayList();
            if (str != null && !str.isEmpty()) {
                String[] parts = str.split(" ");
                StringBuilder sb = new StringBuilder();

                for (String str2 : parts) {
                    String str3 = sb.length() == 0 ? str2 : sb + " " + str2;
                    int textWidth = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str3);
                    if (textWidth <= n) {
                        if (sb.length() > 0) {
                            sb.append(" ");
                        }

                        sb.append(str2);
                    } else {
                        if (sb.length() > 0) {
                            list.add(sb.toString());
                        }

                        sb = new StringBuilder(str2);
                    }
                }

                if (sb.length() > 0) {
                    list.add(sb.toString());
                }

                if (list.isEmpty()) {
                    list.add("");
                }

                return list;
            }
            list.add("");
            return list;
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            int eventDWheel = Mouse.getEventDWheel();
            if (eventDWheel != 0) {
                this.count4 -= eventDWheel > 0 ? 15 : -15;
                this.count4 = Math.max(0, Math.min(this.count5, this.count4));
            }
        }

        private void process() {
            this.entries.clear();
            int n = (int)GuiAltManager.computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 3;
            BanManager banManager = Brutality.getBanManager();
            if (banManager == null) {
                this.entries.add(new String[]{null, null, null});
                this.entries.add(new String[]{"", GuiAltManager.text + "cBan manager not available", String.valueOf(-43691)});
                this.count6 = n * 2;
            } else {
                var list = banManager.computeList(this.text);
                if (list.isEmpty()) {
                    this.entries.add(new String[]{"", GuiAltManager.text + "7No bans recorded for " + this.text, String.valueOf(-6575427)});
                    this.count6 = n;
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    for (int i = 0; i < list.size(); i++) {
                        BanManager.Entry entry = (BanManager.Entry)list.get(i);
                        if (i > 0) {
                            this.entries.add(new String[]{null, null, null});
                        }

                        this.entries.add(new String[]{"", GuiAltManager.text + "6── Ban " + (i + 1) + " ──", String.valueOf(-22016)});
                        boolean flag = GuiAltManager.matchesEntry(entry);
                        String str = flag && entry.w() != null ? entry.w() : entry.x();
                        String str2 = str != null && !str.isBlank() ? "#" + str.replaceFirst("^#", "") : "Unknown";
                        this.entries.add(new String[]{flag ? "Block ID" : "Ban ID", str2, String.valueOf(-1)});
                        String str3 = entry.getReason() != null ? entry.getReason().replaceAll(GuiAltManager.text + ".", "") : "Unknown";
                        int n2 = this.count - 24 - 72 - 4;
                        var list2 = this.computeList(str3, n2);
                        this.entries.add(new String[]{"Reason", (String)list2.get(0), String.valueOf(-1)});

                        for (int j = 1; j < list2.size(); j++) {
                            this.entries.add(new String[]{"", (String)list2.get(j), String.valueOf(-6575427)});
                        }

                        String str4 = entry.z() != null ? entry.z() : "unknown";
                        String str5 = str4.toLowerCase();
                        String str6;
                        int n3;
                        switch (str5) {
                            case "temporary":
                                str6 = "Temporarily Banned";
                                n3 = -22016;
                                break;
                            case "permanent":
                                str6 = "Permanent";
                                n3 = -48060;
                                break;
                            case "block":
                                str6 = "Security Banned";
                                n3 = -39322;
                                break;
                            case "recovery":
                                str6 = "Recovery Phase";
                                n3 = -11162881;
                                break;
                            default:
                                str6 = str4;
                                n3 = -6575427;
                        }

                        this.entries.add(new String[]{"Type", str6, String.valueOf(n3)});
                        str5 = entry.y() != null && !entry.y().isEmpty() ? entry.y() : "Unknown";
                        this.entries.add(new String[]{"Duration", str5, String.valueOf(str5.equalsIgnoreCase("permanent") ? -48060 : -1)});
                        this.entries.add(new String[]{"Banned", simpleDateFormat.format(new Date(entry.getTimestamp())), String.valueOf(-6575427)});
                        if (entry.getTimestamp2() > 0L) {
                            this.entries.add(new String[]{"Expires", simpleDateFormat.format(new Date(entry.getTimestamp2())), String.valueOf(-6575427)});
                        } else {
                            this.entries.add(new String[]{"Expires", "Never (Permanent)", String.valueOf(-48060)});
                        }

                        boolean active = entry.isActive();
                        this.entries
                            .add(
                                new String[]{
                                    "Status",
                                    active ? GuiAltManager.text + "cActive" : GuiAltManager.text + "aExpired / Completed",
                                    String.valueOf(active ? -48060 : -11141291)
                                }
                            );
                    }

                    this.count6 = 0;

                    for (String[] parts : this.entries) {
                        this.count6 = this.count6 + (parts[0] == null ? 6 : n);
                    }
                }
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.guiScreen != null) {
                this.guiScreen.drawScreen(mouseX, mouseY, partialTicks);
            }

            drawRect(0, 0, this.width, this.height, -1342177280);
            int n = (int)GuiAltManager.computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 3;
            int n2 = 24;
            int n3 = this.height - 60;
            int n4 = Math.min(this.count6 + n2 + 16, n3);
            int n5 = (this.width - this.count) / 2;
            int n6 = (this.height - n4) / 2;
            MenuButtonRenderer.handleInt2(n5, n6, this.count, n4);
            String str = GuiAltManager.text + "6Ban Info: " + GuiAltManager.text + "f" + this.text;
            int textWidth = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str);
            GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str, n5 + (this.count - textWidth) / 2, n6 + 8, -1);
            Gui.drawRect(n5 + 2, n6 + n2 - 2, n5 + this.count - 2, n6 + n2 - 1, -11184811);
            int n7 = n6 + n2;
            int n8 = n6 + n4 - 2;
            int n9 = n8 - n7;
            this.count5 = Math.max(0, this.count6 - n9 + 8);
            this.count4 = Math.max(0, Math.min(this.count4, this.count5));
            ScaledResolution sr = new ScaledResolution(mc);
            int scaleFactor = sr.getScaleFactor();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(n5 * scaleFactor, mc.displayHeight - n8 * scaleFactor, this.count * scaleFactor, n9 * scaleFactor);
            int n10 = n7 + 6 - this.count4;
            int n11 = n5 + 12;
            int n12 = n11 + 72;

            for (String[] parts : this.entries) {
                if (parts[0] == null) {
                    Gui.drawRect(n5 + 12, n10 + 1, n5 + this.count - 12, n10 + 2, -11184811);
                    n10 += 6;
                } else {
                    String str2 = parts[0];
                    String str3 = parts[1];
                    int n13 = Integer.parseInt(parts[2]);
                    if (!str2.isEmpty()) {
                        GuiAltManager.computeDrawStringWithShadowComponent2().drawString(GuiAltManager.text + "7" + str2 + ":", n11, n10, -6575427);
                    }

                    int textWidth2 = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str3);
                    int n14 = str2.isEmpty() && str3.startsWith(GuiAltManager.text + "6──") ? n5 + (this.count - textWidth2) / 2 : n12;
                    GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str3, n14, n10, n13);
                    n10 += n;
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            if (this.count5 > 0) {
                int n15 = Math.max(16, n9 * n9 / (this.count6 + 8));
                int n16 = n7 + (int)((float)this.count4 / this.count5 * (n9 - n15));
                Gui.drawRect(n5 + this.count - 5, n7, n5 + this.count - 3, n8, -11184811);
                Gui.drawRect(n5 + this.count - 5, n16, n5 + this.count - 3, n16 + n15, -5592406);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            this.count = Math.min(420, this.width - 16);
            this.buttonList.add(new GuiAltManager.Entry3(0, this.width / 2 - 55, this.height - 30, 100, 20, "Close", -7667712, -2354116));
            this.buttonList.add(new GuiAltManager.Entry3(1, this.width / 2 + 55, this.height - 30, 110, 20, "Clear Data", -11184896, -7829504));
            this.process();
        }

        public SubScreen3(GuiScreen guiScreen, String text) {
            this.guiScreen = guiScreen;
            this.text = text;
        }

        @Override
        public void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 0) {
                mc.displayGuiScreen(this.guiScreen);
            } else if (button.id == 1) {
                BanManager banManager = Brutality.getBanManager();
                if (banManager != null) {
                    banManager.handleString(this.text);
                }

                if (this.guiScreen instanceof GuiAltManager gui) {
                    GuiAltManager.lookupMap3.remove(this.text.toLowerCase());
                    GuiAltManager.itemSet2.remove(this.text.toLowerCase());
                    gui.handleString2(this.text);
                }

                this.process();
                this.count4 = 0;
            }
        }
    }

    public static class SubScreen4 extends CustomGuiScreen {
        private final GuiScreen guiScreen;

        public SubScreen4(GuiScreen guiScreen) {
            this.guiScreen = guiScreen;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.guiScreen != null) {
                this.guiScreen.drawScreen(mouseX, mouseY, partialTicks);
            }

            drawRect(0, 0, this.width, this.height, -1342177280);
            int n = 250;
            int n2 = 100;
            int n3 = (this.width - n) / 2;
            int n4 = (this.height - n2) / 2;
            Gui.drawRect(n3, n4, n3 + n, n4 + n2, -15261134);
            Gui.drawRect(n3, n4, n3 + n, n4 + 2, -22016);
            Gui.drawRect(n3, n4 + n2 - 2, n3 + n, n4 + n2, -22016);
            Gui.drawRect(n3, n4, n3 + 2, n4 + n2, -22016);
            Gui.drawRect(n3 + n - 2, n4, n3 + n, n4 + n2, -22016);
            String str = "Clear All Accounts?";
            String str2 = "This action cannot be undone.";
            int textWidth = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str);
            int textWidth2 = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str2);
            GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str, n3 + (n - textWidth) / 2, n4 + 15, -1);
            GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str2, n3 + (n - textWidth2) / 2, n4 + 35, -6575427);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            this.buttonList.add(new GuiAltManager.Entry3(0, this.width / 2 - 80, this.height / 2 + 20, 75, 20, "Yes", -7667712, -2354116));
            this.buttonList.add(new GuiAltManager.Entry3(1, this.width / 2 + 5, this.height / 2 + 20, 75, 20, "No", -13447886, -14513374));
        }

        @Override
        public void actionPerformed(GuiButton button) {
            if (button.id == 0) {
                AccountManager.clear();
                AccountManager.save();

                try {
                    PitPandaBackup pitPandaBackup = Brutality.getPitPandaBackup();
                    if (pitPandaBackup != null) {
                        pitPandaBackup.process();
                    }
                } catch (Exception ignored) {
                }

                try {
                    if (Brutality.getBanManager() != null) {
                        Brutality.getBanManager().process2();
                    }
                } catch (Exception ignored2) {
                }

                if (this.guiScreen instanceof GuiAltManager gui) {
                    gui.entry = null;
                    AccountManager.load();
                    gui.process12();
                    gui.process14();
                }

                mc.displayGuiScreen(this.guiScreen);
            } else if (button.id == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }
    }

    public static class SubScreen5 extends CustomGuiScreen {
        private static final int count = 52;
        private static final int count2 = 5;
        private static final int count3 = 14;
        private static final int count4 = 6;
        private static final int count5 = 20;
        private static final int count6 = 26;
        private final GuiAltManager guiAltManager;
        private int count7 = 0;
        private int count8 = 0;

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiAltManager);
            }
        }

        private GuiAltManager.SubScreen5.BorEntry computeBorEntry() {
            int n = Math.max(1, Math.min(420, this.width - 24));
            int n2 = Math.max(1, Math.min(378, this.height - 20));
            return new GuiAltManager.SubScreen5.BorEntry((this.width - n) / 2, (this.height - n2) / 2, n, n2);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.guiAltManager != null) {
                this.guiAltManager.drawScreen(-1, -1, partialTicks);
            } else {
                this.drawDefaultBackground();
            }

            drawRect(0, 0, this.width, this.height, -1728053248);
            GuiAltManager.SubScreen5.BorEntry borEntry = this.computeBorEntry();
            MenuButtonRenderer.handleInt2(borEntry.bor(), borEntry.bos(), borEntry.bot(), borEntry.bou());
            CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(20, true);
            CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(16, false);
            drawStringWithShadowComponent.drawString("Prestige", borEntry.bor() + 14, borEntry.bos() + 9, -1);
            drawStringWithShadowComponent2.drawString("Filter accounts by exact prestige", borEntry.bor() + 14, borEntry.bos() + 31, -6575427);
            int n = 11;
            int n2 = n * 26 - 6;
            int n3 = Math.max(0, borEntry.computeInt() - borEntry.bos());
            this.count8 = Math.max(0, n2 - n3);
            this.count7 = Math.max(0, Math.min(this.count7, this.count8));
            ScaledResolution sr = new ScaledResolution(mc);
            int scaleFactor = sr.getScaleFactor();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(borEntry.bor() * scaleFactor, mc.displayHeight - borEntry.computeInt() * scaleFactor, borEntry.bot() * scaleFactor, n3 * scaleFactor);
            int n4 = this.guiAltManager != null ? this.guiAltManager.getCount13() : -1;

            for (int i = 0; i < 52; i++) {
                int n5 = borEntry.computeInt(i);
                int n6 = borEntry.computeInt(i, this.count7);
                if (n6 + 20 > borEntry.bos() && n6 < borEntry.computeInt()) {
                    String str = i == 0 ? "Any" : (i == 1 ? "None" : GuiAltManager.computeString(i - 1));
                    float f = borEntry.matchesInt(i, this.count7, mouseX, mouseY) ? 1.0F : 0.0F;
                    if (i - 1 == n4) {
                        MenuButtonRenderer.handleInt(n5, n6, borEntry.bot(), 20, str, drawStringWithShadowComponent2, -22016, f);
                    } else {
                        MainMenuIconRenderer.handleInt(n5, n6, borEntry.bot(), 20, str, drawStringWithShadowComponent2, f, 1.0F);
                    }
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            if (this.count8 > 0 && n3 > 0) {
                int n7 = borEntry.bor() + borEntry.bot() - 8;
                int n8 = Math.min(n3, Math.max(10, n3 * n3 / n2));
                int n9 = borEntry.bos() + (n3 - n8) * this.count7 / this.count8;
                Gui.drawRect(n7, borEntry.bos(), n7 + 2, borEntry.computeInt(), 620756991);
                Gui.drawRect(n7, n9, n7 + 2, n9 + n8, -1711298048);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 0) {
                mc.displayGuiScreen(this.guiAltManager);
            }
        }

        public SubScreen5(GuiAltManager guiAltManager) {
            this.guiAltManager = guiAltManager;
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            int eventDWheel = Mouse.getEventDWheel();
            if (eventDWheel != 0) {
                this.count7 = Math.max(0, Math.min(this.count8, this.count7 - Integer.signum(eventDWheel) * 20));
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            if (mouseButton == 0) {
                GuiAltManager.SubScreen5.BorEntry borEntry = this.computeBorEntry();

                for (int i = 0; i < 52; i++) {
                    if (borEntry.matchesInt(i, this.count7, mouseX, mouseY)) {
                        if (this.guiAltManager != null) {
                            this.guiAltManager.handleInt(i - 1);
                        }

                        mc.displayGuiScreen(this.guiAltManager);
                        return;
                    }
                }
            }

            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            GuiAltManager.SubScreen5.BorEntry borEntry = this.computeBorEntry();
            this.buttonList.add(new GuiAltManager.Entry3(0, borEntry.bor() + borEntry.bot() - 14 - 84, borEntry.bos() + borEntry.bou() - 29, 84, 19, "Done", -22016, -13312));
        }

        public record BorEntry(int bor, int bos, int bot, int bou) {
            public boolean matchesInt(int n, int n2, int n3, int n4) {
                int n5 = this.computeInt(n);
                int n6 = this.computeInt(n, n2);
                return n3 >= n5 && n3 < n5 + this.bot() && n4 >= Math.max(n6, this.bos()) && n4 < Math.min(n6 + 20, this.computeInt());
            }

            public int computeInt(int n, int n2) {
                return this.bos() + n / 5 * 26 - n2;
            }

            public int bos() {
                return this.bos + 49;
            }

            public int computeInt() {
                return this.bos + this.bou - 38;
            }

            public int computeInt(int n) {
                return this.bor + 14 + n % 5 * (this.bot() + 6);
            }

            public int bot() {
                return (this.bot - 28 - 24) / 5;
            }
        }
    }

    public static class SubScreen6 extends CustomGuiScreen {
        private static final int count = 500;
        private static final int bow = 16;
        private static final int box = -22016;
        private static final int boy = -1;
        private static final int count2 = -6575427;
        private final GuiScreen guiScreen;
        private final List<GuiAltManager.SubScreen6.Entry> entries = new ArrayList<>();
        private int count3 = 0;
        private int count4 = 0;

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        private void loadAccountsToUnban() {
            this.entries.clear();
            if (Brutality.getBanManager() != null) {
                try {
                    long now = System.currentTimeMillis();
                    long l = 604800000L;

                    for (AccountSession accountSession : AccountManager.computeArrayList()) {
                        String username = accountSession.getUsername();
                        if (username != null && !username.isEmpty()) {
                            BanManager.Entry entry = Brutality.getBanManager().computeEntry(username);
                            if (entry != null && entry.isActive()) {
                                long timestamp2 = entry.getTimestamp2();
                                long l2 = timestamp2 - now;
                                if (l2 > 0L && l2 <= l) {
                                    this.entries.add(new GuiAltManager.SubScreen6.Entry(username, timestamp2, l2, entry.x()));
                                }
                            }
                        }
                    }

                    this.entries.sort((entry2, entry3) -> Long.compare(entry2.timestamp2, entry3.timestamp2));
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.guiScreen != null) {
                this.guiScreen.drawScreen(-1, -1, partialTicks);
            }

            drawRect(0, 0, this.width, this.height, -1728053248);
            CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(20, true);
            CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(16, false);
            CustomFontRenderer drawStringWithShadowComponent3 = FontComponent.computeDrawStringWithShadowComponent2(14, false);
            int n = 26;
            int n2 = 4;
            int n3 = Math.min(500, this.width - 24);
            int n4 = this.entries.isEmpty() ? 40 : Math.min(this.entries.size() * (n + n2) - n2, this.height - 160);
            int n5 = 48 + n4 + 14 + 20 + 14;
            int n6 = (this.width - n3) / 2;
            int n7 = (this.height - n5) / 2;
            int n8 = n7 + 14 + 22 + 12;
            this.count4 = Math.max(0, this.entries.size() * (n + n2) - n2 - n4);
            this.count3 = Math.max(0, Math.min(this.count3, this.count4));
            AvatarComponent.handleFloat(n6, n7, n3, n5, 8.0F);
            MainMenuIconRenderer.handleInt(n6, n7, n3, n5, 8.0F, 1.2F);
            GlStateManager.enableTexture2D();
            drawStringWithShadowComponent.drawString("Unbanning Soon", n6 + 16, n7 + 13, -1);
            String str = this.entries.size() + (this.entries.size() == 1 ? " account" : " accounts");
            drawStringWithShadowComponent3.drawString(str, n6 + n3 - 16 - drawStringWithShadowComponent3.getStringWidth(str), n7 + 18, -6575427);
            if (this.entries.isEmpty()) {
                String str2 = "No accounts are unbanning soon.";
                drawStringWithShadowComponent2.drawString(str2, n6 + (n3 - drawStringWithShadowComponent2.getStringWidth(str2)) / 2, n8 + 14, -6575427);
            } else {
                int scaleFactor = new ScaledResolution(mc).getScaleFactor();
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor(n6 * scaleFactor, mc.displayHeight - (n8 + n4) * scaleFactor, n3 * scaleFactor, n4 * scaleFactor);

                for (int i = 0; i < this.entries.size(); i++) {
                    int n9 = n8 + i * (n + n2) - this.count3;
                    if (n9 + n >= n8 && n9 <= n8 + n4) {
                        GuiAltManager.SubScreen6.Entry entry = this.entries.get(i);
                        int n10 = n6 + 14;
                        int n11 = n3 - 28;
                        boolean flag = mouseX >= n10 && mouseX < n10 + n11 && mouseY >= n9 && mouseY < n9 + n;
                        OutlineThicknessHelper.handleFloat(n10, n9, n11, n, 5.0F, new Color(255, 255, 255, flag ? 20 : 10));
                        int n12 = this.computeInt(entry.timestamp2);
                        OutlineThicknessHelper.handleFloat(n10, n9 + 6, 2.0F, n - 12, 1.0F, new Color(n12, false));
                        GlStateManager.enableTexture2D();
                        drawStringWithShadowComponent2.drawString(entry.text, n10 + 12, n9 + 4, -1);
                        String str3 = new SimpleDateFormat("EEE d MMM, HH:mm").format(new Date(entry.timestamp));
                        drawStringWithShadowComponent3.drawString(str3, n10 + 12, n9 + 15, -6575427);
                        String str4 = this.computeString(entry.timestamp2).replaceAll("§.", "").replace("Unbanning in ", "");
                        int n13 = drawStringWithShadowComponent3.getStringWidth(str4) + 14;
                        int n14 = n10 + n11 - 10 - n13;
                        int n15 = n9 + (n - 14) / 2;
                        Color color = new Color(n12, false);
                        OutlineThicknessHelper.handleFloat(n14, n15, n13, 14.0F, 7.0F, new Color(color.getRed(), color.getGreen(), color.getBlue(), 46));
                        OutlineThicknessHelper.handleFloat(
                            n14, n15, n13, 14.0F, 7.0F, 0.5F, new Color(0, 0, 0, 0), new Color(color.getRed(), color.getGreen(), color.getBlue(), 150)
                        );
                        GlStateManager.enableTexture2D();
                        drawStringWithShadowComponent3.drawString(str4, n14 + 7, n15 + 3, 0xFF000000 | n12);
                    }
                }

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                if (this.count4 > 0) {
                    int n16 = Math.max(14, n4 * n4 / (n4 + this.count4));
                    int n17 = n8 + (n4 - n16) * this.count3 / this.count4;
                    drawRect(n6 + n3 - 7, n17, n6 + n3 - 5, n17 + n16, -1862270977);
                }
            }

            for (GuiButton button : this.buttonList) {
                if (button.id == 0) {
                    button.width = 90;
                    button.height = 20;
                    button.xPosition = n6 + n3 - 16 - 90;
                    button.yPosition = n7 + n5 - 14 - 20;
                }

                button.drawButton(mc, mouseX, mouseY);
            }
        }

        private String computeString(long l) {
            long l2 = l / 86400000L;
            long l3 = l % 86400000L / 3600000L;
            long l4 = l % 3600000L / 60000L;
            if (l2 > 0L) {
                return "§eUnbanning in " + l2 + "d " + l3 + "h";
            }
            return l3 > 0L ? "§aUnbanning in " + l3 + "h " + l4 + "m" : "§aUnbanning in " + l4 + "m";
        }

        private int computeInt(long l) {
            long l2 = l / 3600000L;
            if (l2 < 1L) {
                return -16711936;
            }
            if (l2 < 6L) {
                return -7799040;
            }
            if (l2 < 24L) {
                return -256;
            }
            return l2 < 48L ? -30720 : -39424;
        }

        @Override
        public void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 0) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        public SubScreen6(GuiScreen guiScreen) {
            this.guiScreen = guiScreen;
            this.loadAccountsToUnban();
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            this.buttonList.add(new GuiAltManager.Entry3(0, this.width / 2 - 60, this.height - 30, 120, 20, "Close", -7667712, -2354116));
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            int eventDWheel = Mouse.getEventDWheel();
            if (eventDWheel != 0) {
                this.count3 -= eventDWheel > 0 ? 20 : -20;
                this.count3 = Math.max(0, Math.min(this.count4, this.count3));
            }
        }

        public static class Entry {
            public final String text;
            public final long timestamp;
            public final long timestamp2;
            public final String text2;

            public Entry(String text, long timestamp, long l, String str) {
                this.text = text;
                this.timestamp = timestamp;
                this.timestamp2 = l;
                this.text2 = str;
            }
        }
    }

    public static class SubScreen7 extends CustomGuiScreen {
        private int count;
        private static final int count2 = 12;
        private static final int count3 = 72;
        private final GuiScreen guiScreen;
        private final String text;
        private final GuiAltManager.Entry4 entry4;
        private int count4 = 0;
        private int count5 = 0;
        private final List<String[]> entries = new ArrayList<>();
        private int count6 = 0;

        private void process() {
            this.entries.clear();
            int n = (int)GuiAltManager.computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 3;
            if (this.entry4 == null) {
                this.entries.add(new String[]{null, null, null});
                this.entries.add(new String[]{"", GuiAltManager.text + "cNo wipe data available", String.valueOf(-43691)});
                this.count6 = n * 2;
            } else {
                PitPandaBackup.Entry entry = computeEntry(this.text);
                if (entry == null) {
                    this.entries.add(new String[]{"", "No pre-wipe snapshot was retained for this account.", String.valueOf(-6575427)});
                    this.entries.add(new String[]{"Detected", this.entry4.computeString(), String.valueOf(-1)});
                    this.count6 = n * 2;
                } else {
                    this.entries.add(new String[]{null, null, null});
                    this.entries.add(new String[]{"", GuiAltManager.text + "6── Account Stats Before Wipe ──", String.valueOf(-13312)});
                    this.entries.add(new String[]{null, null, null});
                    this.entries.add(new String[]{"Username", entry.getUsername(), String.valueOf(-6575427)});
                    this.entries.add(new String[]{"Rank", entry.getFormattedRank(), String.valueOf(-11141291)});
                    String str = this.computeString(entry.getPrestige());
                    this.entries.add(new String[]{"Prestige", str, String.valueOf(-5635926)});
                    this.entries
                        .add(
                            new String[]{
                                "Level", String.valueOf(GuiAltManager.computeInt2(entry.getFormattedLevel(), entry.getLevel())), String.valueOf(-11141121)
                            }
                        );
                    this.entries.add(new String[]{"Gold", String.format("%,.0f", entry.getCurrentGold()), String.valueOf(-13312)});
                    this.entries.add(new String[]{"Renown", String.valueOf(entry.getCurrentRenown()), String.valueOf(-5636096)});
                    if (entry.isHeresyUnlocked()) {
                        this.entries.add(new String[]{"Heresy", GuiAltManager.text + "aUnlocked", String.valueOf(-11141291)});
                    } else {
                        this.entries.add(new String[]{"Heresy", GuiAltManager.text + "cLocked", String.valueOf(-48060)});
                    }

                    int n2 = entry.getPlaytime() / 60;
                    int n3 = entry.getPlaytime() % 60;
                    this.entries.add(new String[]{"Playtime", String.format("%dh %dm", n2, n3), String.valueOf(-6575427)});
                    this.entries.add(new String[]{null, null, null});
                    this.entries.add(new String[]{"", GuiAltManager.text + "6── Wipe Detection ──", String.valueOf(-13312)});
                    this.entries.add(new String[]{null, null, null});
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String str2 = this.entry4.longValue != null ? simpleDateFormat.format(new Date(this.entry4.longValue)) : "Unknown";
                    this.entries.add(new String[]{"Detected", str2, String.valueOf(-22016)});
                    this.entries.add(new String[]{"Status", GuiAltManager.text + "cAccount wiped to Level 0", String.valueOf(-48060)});
                    this.count6 = 0;

                    for (String[] parts : this.entries) {
                        this.count6 = this.count6 + (parts[0] == null ? 6 : n);
                    }
                }
            }
        }

        private String computeString(int n) {
            if (n <= 0) {
                return "None";
            }
            String[] parts = new String[]{
                "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX"
            };
            return n < parts.length ? GuiAltManager.text + "e" + parts[n] : GuiAltManager.text + "e" + n;
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        @Override
        public void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 0) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (this.guiScreen != null) {
                this.guiScreen.drawScreen(mouseX, mouseY, partialTicks);
            }

            drawRect(0, 0, this.width, this.height, -1342177280);
            int n = (int)GuiAltManager.computeDrawStringWithShadowComponent2().getFONT_HEIGHT() + 3;
            int n2 = 24;
            int n3 = this.height - 60;
            int n4 = Math.min(this.count6 + n2 + 16, n3);
            int n5 = (this.width - this.count) / 2;
            int n6 = (this.height - n4) / 2;
            MenuButtonRenderer.handleInt2(n5, n6, this.count, n4);
            String str = GuiAltManager.text + "eWipe Info: " + GuiAltManager.text + "f" + this.text;
            int textWidth = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str);
            GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str, n5 + (this.count - textWidth) / 2, n6 + 8, -1);
            Gui.drawRect(n5 + 2, n6 + n2 - 2, n5 + this.count - 2, n6 + n2 - 1, -11184811);
            int n7 = n6 + n2;
            int n8 = n6 + n4 - 2;
            int n9 = n8 - n7;
            this.count5 = Math.max(0, this.count6 - n9 + 8);
            this.count4 = Math.max(0, Math.min(this.count4, this.count5));
            ScaledResolution sr = new ScaledResolution(mc);
            int scaleFactor = sr.getScaleFactor();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(n5 * scaleFactor, mc.displayHeight - n8 * scaleFactor, this.count * scaleFactor, n9 * scaleFactor);
            int n10 = n7 + 6 - this.count4;
            int n11 = n5 + 12;
            int n12 = n11 + 72;

            for (String[] parts : this.entries) {
                if (parts[0] == null) {
                    Gui.drawRect(n5 + 12, n10 + 1, n5 + this.count - 12, n10 + 2, -11184811);
                    n10 += 6;
                } else {
                    String str2 = parts[0];
                    String str3 = parts[1];
                    int n13 = Integer.parseInt(parts[2]);
                    if (!str2.isEmpty()) {
                        GuiAltManager.computeDrawStringWithShadowComponent2().drawString(GuiAltManager.text + "7" + str2 + ":", n11, n10, -6575427);
                    }

                    int textWidth2 = GuiAltManager.computeDrawStringWithShadowComponent2().getStringWidth(str3);
                    int n14 = str2.isEmpty() && str3.startsWith(GuiAltManager.text + "6──") ? n5 + (this.count - textWidth2) / 2 : n12;
                    GuiAltManager.computeDrawStringWithShadowComponent2().drawString(str3, n14, n10, n13);
                    n10 += n;
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            if (this.count5 > 0) {
                int n15 = Math.max(16, n9 * n9 / (this.count6 + 8));
                int n16 = n7 + (int)((float)this.count4 / this.count5 * (n9 - n15));
                Gui.drawRect(n5 + this.count - 5, n7, n5 + this.count - 3, n8, -11184811);
                Gui.drawRect(n5 + this.count - 5, n16, n5 + this.count - 3, n16 + n15, -5592406);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        public static PitPandaBackup.Entry computeEntry(String str) {
            PitPandaBackup pitPandaBackup = Brutality.getPitPandaBackup();
            PitPandaBackup.Entry entry = pitPandaBackup == null ? null : pitPandaBackup.computeEntry(str);
            return entry == null ? null : entry.getBeforeWipe();
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            int eventDWheel = Mouse.getEventDWheel();
            if (eventDWheel != 0) {
                this.count4 -= eventDWheel > 0 ? 15 : -15;
                this.count4 = Math.max(0, Math.min(this.count5, this.count4));
            }
        }

        public SubScreen7(GuiScreen guiScreen, String text, GuiAltManager.Entry4 entry4) {
            this.guiScreen = guiScreen;
            this.text = text;
            this.entry4 = entry4;
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            this.count = Math.min(420, this.width - 16);
            this.buttonList.add(new GuiAltManager.Entry3(0, this.width / 2 - 55, this.height - 30, 110, 20, "Close", -7667712, -2354116));
            this.process();
        }
    }
}
