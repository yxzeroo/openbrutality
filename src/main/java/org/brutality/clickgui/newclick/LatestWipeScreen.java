package org.brutality.clickgui.newclick;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.gui.CustomGuiScreen;

public class LatestWipeScreen extends CustomGuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final GuiScreen guiScreen;
    private String status = "Loading...";
    private String text = "";
    private String text2 = "";

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);
        int n = sr.getScaledWidth() / 2;
        int n2 = sr.getScaledHeight() / 2 - 40;
        this.drawCenteredString(mc.fontRendererObj, "§c§lPit", n, n2 - 20, 16777215);
        this.drawCenteredString(mc.fontRendererObj, "§7Latest wipe", n, n2 - 6, 8947848);
        if (this.status != null) {
            this.drawCenteredString(mc.fontRendererObj, "§7" + this.status, n, n2 + 16, 13421772);
        } else {
            short s = 240;
            byte b = 44;
            int n3 = n - s / 2;
            int n4 = n2 + 14;
            Gui.drawRect(n3, n4, n3 + s, n4 + b, new Color(25, 25, 30, 230).getRGB());
            Gui.drawRect(n3, n4, n3 + s, n4 + 1, new Color(150, 30, 30, 220).getRGB());
            this.drawCenteredString(mc.fontRendererObj, "§f" + this.text, n, n4 + 6, 16777215);
            this.drawCenteredString(mc.fontRendererObj, "§7" + this.computeString(this.text2), n, n4 + 22, 11184810);
        }

        this.drawCenteredString(mc.fontRendererObj, "§7Press ESC to go back", n, n2 + 70, 5592405);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    private void process() {
        Thread thread = new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection)new URL("https://pitpal.rocks/api/ign/latest-wipe").openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                if (conn.getResponseCode() != 200) {
                    this.handleString("Failed to fetch (HTTP " + conn.getResponseCode() + ")");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String str;
                try {
                    while ((str = reader.readLine()) != null) {
                        sb.append(str);
                    }
                } catch (Throwable e) {
                    try {
                        reader.close();
                    } catch (Throwable e2) {
                        e.addSuppressed(e2);
                    }

                    throw e;
                }

                reader.close();
                JsonObject asJsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
                JsonObject json = asJsonObject.has("data") ? asJsonObject.getAsJsonObject("data") : null;
                if (json == null) {
                    this.handleString("No data");
                    return;
                }

                String str2 = json.has("ign") && !json.get("ign").isJsonNull() ? json.get("ign").getAsString() : "";
                String str3 = json.has("latestWipeAt") && !json.get("latestWipeAt").isJsonNull() ? json.get("latestWipeAt").getAsString() : "";
                mc.addScheduledTask(() -> {
                    this.text = str2;
                    this.text2 = str3;
                    this.status = null;
                });
            } catch (Exception e3) {
                this.handleString("Failed to fetch: " + e3.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private String computeString(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                Date date = simpleDateFormat.parse(str);
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                return simpleDateFormat2.format(date);
            } catch (Exception e) {
                return str;
            }
        } else {
            return "";
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public LatestWipeScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.process();
    }

    private void handleString(String status) {
        mc.addScheduledTask(() -> this.status = status);
    }
}
