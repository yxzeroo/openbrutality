package org.brutality.utils.hypixel;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.proxy.ProxyManager;
import org.lwjgl.opengl.GL11;

public class LoadAvatarHelper {
    private static final String text = "https://pitpanda.rocks/api/players/";
    private static final String text2 = "617c9c76-e5a5-4213-a6b9-aded74ae64c8";
    private static final String text3 = "https://minotar.net/avatar/";
    private static final long timestamp = 300L;
    private static final long timestamp2 = 12000L;
    private static final int count = 245;
    private static final int count2 = 95;
    private static final int count3 = 6;
    private static final int count4 = 64;
    private static final List<LoadAvatarHelper.Entry> entries = new CopyOnWriteArrayList<>();
    private static final ConcurrentLinkedDeque<LoadAvatarHelper.Entry> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
    private static volatile int count5 = 10;
    private static volatile boolean flag;
    private static volatile int count6 = 1;

    private static void handleEntry(LoadAvatarHelper.Entry entry, int n, int n2, CustomFontRenderer drawStringWithShadowComponent) {
        Minecraft mc = Minecraft.getMinecraft();
        Gui.drawRect(n, n2, n + 245, n2 + 95, -300410848);
        Gui.drawRect(n, n2, n + 3, n2 + 95, -48060);
        Gui.drawRect(n + 3, n2, n + 245, n2 + 1, -13421739);
        Gui.drawRect(n + 3, n2 + 95 - 1, n + 245, n2 + 95, -13421739);
        Gui.drawRect(n + 245 - 1, n2, n + 245, n2 + 95, -13421739);
        int n3 = n + 8;
        int n4 = n2 + 8;
        byte b = 48;
        if (entry.resourceLocation != null) {
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(entry.resourceLocation);
            Gui.drawScaledCustomSizeModalRect(n3, n4, 0.0F, 0.0F, b, b, b, b, b, b);
        } else {
            Gui.drawRect(n3, n4, n3 + b, n4 + b, -15066598);
            String str = "...";
            int textWidth = drawStringWithShadowComponent.getStringWidth(str);
            drawStringWithShadowComponent.drawString(str, n3 + (b - textWidth) / 2, n4 + b / 2 - 4, -10066330);
        }

        String str2 = "OFFLINE";
        int n5 = -48060;
        int n6 = drawStringWithShadowComponent.getStringWidth(str2) + 6;
        int n7 = n3 + (b - n6) / 2;
        int n8 = n4 + b + 3;
        Gui.drawRect(n7, n8, n7 + n6, n8 + 11, -13434880);
        drawStringWithShadowComponent.drawString(str2, n7 + 3, n8 + 2, n5);
        int n9 = n3 + b + 8;
        int n10 = n2 + 6;
        int n11 = drawStringWithShadowComponent.FONT_HEIGHT + 2;
        drawStringWithShadowComponent.drawStringWithShadow("§c" + entry.text, n9, n10, -1);
        int n12 = n10 + n11 + 1;
        if (entry.flag) {
            String str3 = entry.rank != null ? entry.rank : "NON";
            String str4 = entry.text2;
            String str5 = "§7Rank: " + str4 + str3;
            drawStringWithShadowComponent.drawString(str5, n9, n12, -1);
            n10 = n12 + n11;
            String str6 = "§7Level: " + (entry.formattedLevel != null ? entry.formattedLevel : "§7?");
            drawStringWithShadowComponent.drawString(str6, n9, n10, -1);
            n10 += n11;
            String str7 = "§7Gold: " + (entry.text3 != null ? "§6" + entry.text3 : "§7?");
            drawStringWithShadowComponent.drawString(str7, n9, n10, -1);
            n10 += n11;
            String str8 = "§7Renown: " + (entry.text4 != null ? "§b" + entry.text4 : "§7?");
            drawStringWithShadowComponent.drawString(str8, n9, n10, -1);
            n10 += n11;
            String str9 = "§7Played: " + (entry.text5 != null ? entry.text5 : "§7?");
            drawStringWithShadowComponent.drawString(str9, n9, n10, -1);
        } else if (entry.flag2) {
            drawStringWithShadowComponent.drawString("§cFailed to load stats", n9, n12, -1);
        } else {
            drawStringWithShadowComponent.drawString("§7Loading stats...", n9, n12, -1);
        }

        int n13 = n2 + 95 - 14;
        Gui.drawRect(n + 3, n13, n + 245, n2 + 95 - 1, -13434880);
        String str10 = "⚠ POSSIBLY BANNED OR WIPED";
        int textWidth2 = drawStringWithShadowComponent.getStringWidth(str10);
        drawStringWithShadowComponent.drawString(str10, n + 3 + (242 - textWidth2) / 2, n13 + 2, -43691);
    }

    public static void render() {
        entries.removeIf(LoadAvatarHelper.Entry::checkState);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scaledHeight = sr.getScaledHeight();
        int n = Math.min(count5, Math.max(1, (scaledHeight - 20) / 101));
        entries.removeIf(entry -> flag && entry.flag && entry.prestige >= 0 && entry.prestige < count6);

        while (entries.size() > n) {
            entries.remove(entries.size() - 1);
        }

        while (concurrentLinkedDeque.size() > count5) {
            concurrentLinkedDeque.pollLast();
        }

        entries.sort((entry2, entry3) -> Integer.compare(entry3.prestige, entry2.prestige));

        while (entries.size() < n && !concurrentLinkedDeque.isEmpty()) {
            LoadAvatarHelper.Entry entry4 = concurrentLinkedDeque.poll();
            if (entry4 != null && (!flag || !entry4.flag || entry4.prestige < 0 || entry4.prestige >= count6)) {
                entry4.process();
                entries.add(entry4);
            }
        }

        if (!entries.isEmpty()) {
            CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
            if (drawStringWithShadowComponent != null) {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                int n2 = 10;

                for (LoadAvatarHelper.Entry entry5 : entries) {
                    float f = entry5.computeFloat();
                    int n3 = (int)(-250.0F * (1.0F - f));
                    handleEntry(entry5, n3, n2, drawStringWithShadowComponent);
                    n2 += 101;
                }

                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private static void loadAvatar(LoadAvatarHelper.Entry entry) {
        CompletableFuture.runAsync(
            () -> {
                try {
                    Proxy proxy = ProxyManager.computeProxy();
                    URL url = new URL("https://minotar.net/avatar/" + entry.text + "/48");
                    HttpURLConnection conn = (HttpURLConnection)(proxy != null ? url.openConnection(proxy) : url.openConnection());
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    BufferedImage image = ImageIO.read(conn.getInputStream());
                    conn.disconnect();
                    if (image == null) {
                        return;
                    }

                    Minecraft.getMinecraft()
                        .addScheduledTask(
                            () -> {
                                try {
                                    DynamicTexture dynamicTexture = new DynamicTexture(image);
                                    entry.resourceLocation = Minecraft.getMinecraft()
                                        .getTextureManager()
                                        .getDynamicTextureLocation("ban_sighting_" + entry.text.toLowerCase(), dynamicTexture);
                                } catch (Exception ignored) {
                                }
                            }
                        );
                } catch (Exception ignored2) {
                }
            }
        );
    }

    private static void fetchData(LoadAvatarHelper.Entry entry) {
        CompletableFuture.runAsync(() -> {
            try {
                String str = computeString("https://pitpanda.rocks/api/players/" + entry.text + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
                if (str == null) {
                    entry.flag2 = true;
                    return;
                }

                String str2 = computeString2(str, "data");
                if (str2 == null) {
                    entry.flag2 = true;
                    return;
                }

                entry.formattedLevel = computeString(str2, "formattedLevel");
                entry.prestige = computeInt(str2, "prestige");
                entry.rank = computeString(str2, "rank");
                entry.text3 = computeString(computeDouble(str2, "gold"));
                entry.text4 = computeString(computeInt(str2, "renown"));
                entry.text5 = computeString2(computeInt(str2, "playtime"));
                entry.text2 = computeString2(entry.rank);
                entry.flag = true;
            } catch (Exception e) {
                entry.flag2 = true;
            }
        });
    }

    private static String computeString2(String str) {
        if (str == null) {
            return "§7";
        }
        String str2 = str.toUpperCase();
        switch (str2) {
            case "VIP":
                return "§a";
            case "VIP+":
                return "§a";
            case "MVP":
                return "§b";
            case "MVP+":
                return "§b";
            case "YOUTUBER":
            case "YT":
                return "§c";
            case "HELPER":
            case "MOD":
            case "ADMIN":
                return "§2";
            default:
                return "§7";
        }
    }

    public static void handleString(String str) {
        if (str != null && !str.isEmpty()) {
            LoadAvatarHelper.Entry entry = new LoadAvatarHelper.Entry(str);
            fetchData(entry);
            loadAvatar(entry);
            concurrentLinkedDeque.add(entry);
        }
    }

    public static void handleInt(int n, boolean flag2, int n2) {
        count5 = Math.max(1, Math.min(10, n));
        flag = flag2;
        count6 = Math.max(1, Math.min(50, n2));
    }

    private static String computeString(String str, String str2) {
        String str3 = "\"" + str2 + "\":\"";
        int n = str.indexOf(str3);
        if (n < 0) {
            return null;
        }
        int n2 = n + str3.length();
        int n3 = str.indexOf(34, n2);
        return n3 > n2 ? str.substring(n2, n3) : null;
    }

    private static String computeString(int n) {
        if (n >= 1000000) {
            return String.format("%.1fM", n / 1000000.0);
        }
        return n >= 1000 ? String.format("%.1fK", n / 1000.0) : String.valueOf(n);
    }

    private static String computeString(String str) {
        try {
            Proxy proxy = ProxyManager.computeProxy();
            URL url = new URL(str);
            HttpURLConnection conn = (HttpURLConnection)(proxy != null ? url.openConnection(proxy) : url.openConnection());
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.setRequestProperty("User-Agent", "BrutalityClient/1.0");
            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();

            String str2;
            while ((str2 = reader.readLine()) != null) {
                sb.append(str2);
            }

            reader.close();
            conn.disconnect();
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static double computeDouble(String str, String str2) {
        String str3 = "\"" + str2 + "\":";
        int n = str.indexOf(str3);
        if (n < 0) {
            return 0.0;
        }
        int n2 = n + str3.length();
        int n3 = n2;

        while (n3 < str.length() && (Character.isDigit(str.charAt(n3)) || str.charAt(n3) == '.' || str.charAt(n3) == '-')) {
            n3++;
        }

        try {
            return Double.parseDouble(str.substring(n2, n3));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static int computeInt(String str, String str2) {
        return (int)computeDouble(str, str2);
    }

    private static String computeString2(int n) {
        if (n <= 0) {
            return "?";
        }
        int n2 = n / 60;
        int n3 = n % 60;
        return n2 > 0 ? n2 + "h " + n3 + "m" : n3 + "m";
    }

    private static String computeString(double d) {
        if (d >= 1000000.0) {
            return String.format("%.1fM", d / 1000000.0);
        }
        return d >= 1000.0 ? String.format("%.1fK", d / 1000.0) : String.format("%.0f", d);
    }

    private static String computeString2(String str, String str2) {
        String str3 = "\"" + str2 + "\"";
        int n = str.indexOf(str3);
        if (n < 0) {
            return null;
        }
        int n2 = str.indexOf(123, n);
        if (n2 < 0) {
            return null;
        }
        int n3 = 0;

        for (int i = n2; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '{') {
                n3++;
            } else if (ch == '}') {
                if (--n3 == 0) {
                    return str.substring(n2, i + 1);
                }
            }
        }

        return null;
    }

    public static class Entry {
        public final String text;
        public volatile long timestamp = -1L;
        public volatile String formattedLevel = null;
        public volatile String rank = null;
        public volatile String text2 = "§7";
        public volatile String text3 = null;
        public volatile String text4 = null;
        public volatile String text5 = null;
        public volatile boolean flag = false;
        public volatile boolean flag2 = false;
        public volatile int prestige = -1;
        public volatile ResourceLocation resourceLocation = null;

        public boolean checkState() {
            return this.timestamp >= 0L && this.computeLong() > this.computeLong2();
        }

        public long computeLong2() {
            return 12600L;
        }

        public Entry(String text) {
            this.text = text;
        }

        public float computeFloat() {
            long l = this.computeLong();
            float f;
            if (l < 300L) {
                f = (float)l / 300.0F;
            } else if (l < 12300L) {
                f = 1.0F;
            } else {
                f = 1.0F - (float)(l - 300L - 12000L) / 300.0F;
            }

            f = Math.max(0.0F, Math.min(1.0F, f));
            double d = Math.tanh(2.5);
            return (float)(Math.tanh(f * 5.0 - 2.5) / d * 0.5 + 0.5);
        }

        public void process() {
            this.timestamp = System.currentTimeMillis();
        }

        public long computeLong() {
            return this.timestamp < 0L ? 0L : System.currentTimeMillis() - this.timestamp;
        }
    }
}
