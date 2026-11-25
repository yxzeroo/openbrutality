package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.brutality.Brutality;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.extensions.APPDATAHandler;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.notification.old.ModuleEnabledHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class RefreshScreen extends CustomGuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NewClickGUI newClickGUI;
    private final Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent = new Inter18ptSemiboldTtfComponent();
    private final List<String> entries = new ArrayList<>();
    private final Set<String> itemSet = new HashSet<>();
    private String text = null;
    private String text2 = "";
    private boolean flag = false;
    private int count = 0;
    private static final int count2 = 20;
    private static final int count3 = 34;
    private static final int count4 = 2;

    private void handleInt(int n, int n2, int n3, String str, int n4, int n5, int n6) {
        boolean flag = matchesInt(n4, n5, n, n2, n + n3, n2 + 20);
        Gui.drawRect(n, n2, n + n3, n2 + 20, flag ? new Color(150, 40, 40, 235).getRGB() : new Color(120, 25, 25, 235).getRGB());
        this.inter18ptSemiboldTtfComponent.handleString2(str, n + (n3 - this.inter18ptSemiboldTtfComponent.computeInt3(str)) / 2, n2 + 4, -1);
    }

    public RefreshScreen(NewClickGUI newClickGUI) {
        this.newClickGUI = newClickGUI;
        this.refresh();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        int n = this.width / 2 - 160;
        short s = 320;
        int n2 = 40;
        this.inter18ptSemiboldTtfComponent
            .handleString3("§c§lConfigs", this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt("Configs") / 2, n2 - 26, -1);
        this.inter18ptSemiboldTtfComponent
            .handleString2(
                "§7Left-click to load   |   Right-click to expand",
                this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt3("Left-click to load   |   Right-click to expand") / 2,
                n2 - 8,
                -7829368
            );
        this.handleInt(n, n2, s, mouseX, mouseY);
        n2 += 30;
        int n3 = this.height - n2 - 12;
        int n4 = Math.max(0, this.computeInt() - n3);
        this.count = Math.max(0, Math.min(this.count, n4));
        int n5 = n2;

        for (String str : this.entries) {
            int n6 = this.computeInt(str);
            int n7 = n5 - this.count;
            if (n7 + n6 >= n2 && n7 <= n2 + n3) {
                this.handleInt(n, n7, s, str, mouseX, mouseY);
            }

            n5 += n6;
        }
    }

    private void handleInt(int n, int n2, int n3, String str, int n4, int n5) {
        boolean flag = this.itemSet.contains(str);
        boolean flag2 = matchesInt(n4, n5, n, n2, n + n3, n2 + 20);
        int rgb = new Color(25, 25, 30, 220).getRGB();
        if (flag2) {
            rgb = new Color(50, 50, 55, 230).getRGB();
        }

        Gui.drawRect(n, n2, n + n3, n2 + 20, rgb);
        Gui.drawRect(n, n2, n + n3, n2 + 1, new Color(60, 0, 0, 200).getRGB());
        this.inter18ptSemiboldTtfComponent.handleString(str, n + 5, n2 + 3, -3355444);
        String str2 = this.computeString(str);
        this.inter18ptSemiboldTtfComponent.handleString2(str2, n + n3 - this.inter18ptSemiboldTtfComponent.computeInt3(str2) - 5, n2 + 6, -10066330);
        if (flag) {
            int n6 = n2 + 20 + 1;
            boolean flag3 = matchesInt(n4, n5, n + 8, n6, n + n3 - 8, n6 + 14);
            Gui.drawRect(n + 8, n6, n + n3 - 8, n6 + 14, flag3 ? new Color(90, 90, 100, 230).getRGB() : new Color(45, 45, 52, 230).getRGB());
            String str3 = this.text != null && this.text.equals(str) ? "Press key..." : "Bind";
            this.inter18ptSemiboldTtfComponent.handleString2(str3, n + 13, n6 + 3, this.text != null && this.text.equals(str) ? -171 : -5592406);
            int n7 = n6 + 17;
            boolean flag4 = matchesInt(n4, n5, n + 8, n7, n + n3 - 8, n7 + 14);
            Gui.drawRect(n + 8, n7, n + n3 - 8, n7 + 14, flag4 ? new Color(140, 40, 40, 230).getRGB() : new Color(60, 25, 25, 230).getRGB());
            this.inter18ptSemiboldTtfComponent.handleString2("Delete", n + 13, n7 + 3, -34953);
        }
    }

    private int computeInt(String str) {
        return 20 + (this.itemSet.contains(str) ? 34 : 0) + 2;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            byte b = 40;
            int n = this.height - b - 30 - 12;
            int n2 = Math.max(0, this.computeInt() - n);
            this.count = Math.max(0, Math.min(n2, this.count - (eventDWheel > 0 ? 18 : -18)));
        }
    }

    private String computeString(String str) {
        var map = APPDATAHandler.computeMap();

        for (Entry entry : map.entrySet()) {
            if (((String)entry.getValue()).equalsIgnoreCase(str)) {
                return Keyboard.getKeyName((Integer)entry.getKey());
            }
        }

        return "";
    }

    private void refresh() {
        this.entries.clear();
        File file = Brutality.appdataHandler != null ? Brutality.appdataHandler.computeFile() : null;
        if (file != null && file.exists()) {
            File[] files = file.listFiles((file2, str) -> str.endsWith(".json"));
            if (files != null) {
                for (File file3 : files) {
                    this.entries.add(file3.getName().replace(".json", ""));
                }
            }
        }

        this.entries.sort(String.CASE_INSENSITIVE_ORDER);
    }

    private void handleString(String str) {
        if (Brutality.appdataHandler == null) {
            ModuleEnabledHandler.handleString2("Config Delete Failed", "Config manager unavailable", 3000L);
        } else {
            File file = new File(Brutality.appdataHandler.computeFile(), str + ".json");
            boolean flag = !file.exists() || file.delete();

            for (Entry entry : new ArrayList<>(APPDATAHandler.computeMap().entrySet())) {
                if (((String)entry.getValue()).equalsIgnoreCase(str)) {
                    Brutality.appdataHandler.matchesInt2((Integer)entry.getKey());
                }
            }

            this.itemSet.remove(str);
            this.refresh();
            if (flag) {
                ModuleEnabledHandler.handleString("Config Deleted", "Deleted " + str, 2500L);
            } else {
                ModuleEnabledHandler.handleString2("Config Delete Failed", "Could not delete " + str, 3000L);
            }
        }
    }

    private int computeInt() {
        int n = 0;

        for (String str : this.entries) {
            n += this.computeInt(str);
        }

        return n;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.text != null) {
            if (keyCode != 1 && keyCode != 0) {
                if (Brutality.appdataHandler.matchesString(this.text, keyCode)) {
                    this.text = null;
                }
            } else {
                this.text = null;
            }
        } else if (this.flag) {
            if (keyCode == 1) {
                this.flag = false;
            } else if (keyCode == 14) {
                if (!this.text2.isEmpty()) {
                    this.text2 = this.text2.substring(0, this.text2.length() - 1);
                }
            } else if (keyCode == 28) {
                this.process();
            } else {
                if (typedChar >= ' ' && typedChar != 167) {
                    this.text2 = this.text2 + typedChar;
                }
            }
        } else if (keyCode == 1) {
            mc.displayGuiScreen(this.newClickGUI);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int n = this.width / 2 - 160;
        short s = 320;
        int n2 = 40;
        int n3 = s - 170;
        if (matchesInt(mouseX, mouseY, n, n2, n + n3, n2 + 20)) {
            this.flag = true;
        } else {
            this.flag = false;
            int n4 = n + n3 + 6;
            if (matchesInt(mouseX, mouseY, n4, n2, n4 + 48, n2 + 20)) {
                this.process();
            } else if (matchesInt(mouseX, mouseY, n4 + 54, n2, n4 + 102, n2 + 20)) {
                this.refresh();
            } else if (matchesInt(mouseX, mouseY, n4 + 108, n2, n4 + 164, n2 + 20)) {
                this.process2();
            } else {
                int n5 = n2 + 30;
                int n6 = n5;

                for (String str : this.entries) {
                    int n7 = this.computeInt(str);
                    int n8 = n6 - this.count;
                    if (matchesInt(mouseX, mouseY, n, n8, n + s, n8 + 20)) {
                        if (mouseButton == 1) {
                            if (this.itemSet.contains(str)) {
                                this.itemSet.remove(str);
                            } else {
                                this.itemSet.add(str);
                            }

                            return;
                        }

                        if (mouseButton != 0) {
                            return;
                        }

                        if (Brutality.appdataHandler != null && Brutality.appdataHandler.matchesString(str)) {
                            ModuleEnabledHandler.handleString("Config Loaded", "Loaded " + str, 2500L);
                            mc.displayGuiScreen(this.newClickGUI);
                        } else {
                            ModuleEnabledHandler.handleString2("Config Load Failed", "Could not load " + str, 3000L);
                        }

                        return;
                    }

                    if (this.itemSet.contains(str)) {
                        int n9 = n8 + 20 + 1;
                        if (matchesInt(mouseX, mouseY, n + 8, n9, n + s - 8, n9 + 14)) {
                            this.text = str;
                            return;
                        }

                        int n10 = n9 + 17;
                        if (matchesInt(mouseX, mouseY, n + 8, n10, n + s - 8, n10 + 14)) {
                            this.handleString(str);
                            return;
                        }
                    }

                    n6 += n7;
                }
            }
        }
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5) {
        int n6 = n3 - 170;
        Gui.drawRect(n, n2, n + n6, n2 + 20, new Color(20, 20, 24, 230).getRGB());
        boolean flag = matchesInt(n4, n5, n, n2, n + n6, n2 + 20);
        if (flag) {
            Gui.drawRect(n, n2, n + n6, n2 + 1, new Color(150, 30, 30, 200).getRGB());
        }

        String str = this.flag ? this.text2 + "_" : (this.text2.isEmpty() ? "New config name..." : this.text2);
        this.inter18ptSemiboldTtfComponent.handleString2(str, n + 5, n2 + 4, this.flag ? -1 : (this.text2.isEmpty() ? -11184811 : -3355444));
        int n7 = n + n6 + 6;
        this.handleInt(n7, n2, 48, "Save", n4, n5, 0);
        this.handleInt(n7 + 54, n2, 48, "Reload", n4, n5, 1);
        this.handleInt(n7 + 108, n2, 56, "Folder", n4, n5, 2);
    }

    private void process() {
        String str = this.text2.trim();
        if (!str.isEmpty() && Brutality.appdataHandler != null) {
            if (Brutality.appdataHandler.matchesString2(str)) {
                this.text2 = "";
                this.flag = false;
                this.refresh();
            } else {
                ModuleEnabledHandler.handleString2("Config Save Failed", "Could not save " + str, 3000L);
            }
        } else {
            ModuleEnabledHandler.handleString2("Config Save Failed", "Enter a config name", 2500L);
        }
    }

    private static boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n5 && n2 >= n4 && n2 <= n6;
    }

    private void process2() {
        try {
            File file = Brutality.appdataHandler.computeFile();
            if (!file.exists()) {
                file.mkdirs();
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ignored) {
        }
    }
}
