package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.brutality.Brutality;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.command.impl.BindCommand;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.script.ScriptModule;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.lwjgl.input.Mouse;

public class OpenApiDocsScreen extends CustomGuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String text = ""; // VENDOR-DISABLED: https://brutality-scripting-api-production.up.railway.app/
    private static final int count = 24;
    private final NewClickGUI newClickGUI;
    private final Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent = new Inter18ptSemiboldTtfComponent();
    private final List<OpenApiDocsScreen.Entry> entries = new ArrayList<>();
    private int count2;
    private NumberSetting numberSetting;
    private int count3;
    private int count4;
    private Module module;

    private void process() {
        try {
            File file = new File(Brutality.appdataHandler.computeFile().getParentFile(), "Scripts");
            if (!file.exists()) {
                file.mkdirs();
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        int n = this.width / 2 - 160;
        byte b = 40;
        this.inter18ptSemiboldTtfComponent
            .handleString3("§c§lScripts", this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt("Scripts") / 2, b - 26, -1);
        this.inter18ptSemiboldTtfComponent
            .handleString2(
                "§7Right-click to expand settings   |   Left-click to toggle",
                this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt3("Right-click to expand settings   |   Left-click to toggle") / 2,
                b - 8,
                -7829368
            );
        this.handleInt(n, b, 320, mouseX, mouseY);
        this.handleInt(n, b + 24 + 4, mouseX, mouseY);
        int n2 = b + 24 + 30;
        int n3 = this.computeInt();
        int n4 = this.height - n2 - 12;
        int n5 = Math.max(0, n3 - n4);
        this.count2 = Math.max(0, Math.min(this.count2, n5));
        int n6 = n2;

        for (OpenApiDocsScreen.Entry entry : this.entries) {
            int height = entry.getHeight();
            int n7 = n6 - this.count2;
            if (n7 + height >= n2 && n7 <= n2 + n4) {
                entry.handleInt(n, n7, 320, mouseX, mouseY, this.inter18ptSemiboldTtfComponent);
            }

            n6 += height + 2;
        }
    }

    private static boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n5 && n2 >= n4 && n2 <= n6;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            byte b = 40;
            int n = b + 24 + 30;
            int n2 = this.height - n - 12;
            int n3 = Math.max(0, this.computeInt() - n2);
            this.count2 = Math.max(0, Math.min(n3, this.count2 - (eventDWheel > 0 ? 18 : -18)));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int n = this.width / 2 - 160;
        byte b = 40;
        if (mouseButton == 0 && matchesInt(mouseX, mouseY, n, b, n + 320, b + 24)) {
            this.openApiDocs();
        } else {
            int n2 = b + 24 + 4;
            if (mouseButton == 0 && matchesInt(mouseX, mouseY, n, n2, n + 154, n2 + 20)) {
                this.process2();
            } else if (mouseButton == 0 && matchesInt(mouseX, mouseY, n + 162, n2, n + 320, n2 + 20)) {
                this.process();
            } else {
                int n3 = b + 24 + 30;
                int n4 = n3;

                for (OpenApiDocsScreen.Entry entry : this.entries) {
                    int height = entry.getHeight();
                    int n5 = n4 - this.count2;
                    if (entry.matchesInt(n, n5, 320, mouseX, mouseY, mouseButton)) {
                        if (entry.numberSetting != null) {
                            this.numberSetting = entry.numberSetting;
                            this.count3 = entry.count;
                            this.count4 = entry.count2;
                        }

                        if (entry.flag2) {
                            this.module = entry.scriptModule;
                            entry.flag2 = false;
                        }

                        return;
                    }

                    n4 += height + 2;
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.module != null) {
            if (keyCode == 1) {
                this.module = null;
            } else {
                this.module.handleInt4(keyCode == 0 ? 0 : keyCode);
                this.module = null;
            }
        } else if (keyCode == 1) {
            mc.displayGuiScreen(this.newClickGUI);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    private int computeInt() {
        int n = 0;

        for (OpenApiDocsScreen.Entry entry : this.entries) {
            n += entry.getHeight() + 2;
        }

        return n;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.numberSetting = null;
        super.mouseReleased(mouseX, mouseY, state);
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5) {
        boolean flag = matchesInt(n4, n5, n, n2, n + n3, n2 + 24);
        int n6 = flag ? new Color(44, 30, 58, 245).getRGB() : new Color(25, 25, 30, 220).getRGB();
        Gui.drawRect(n, n2, n + n3, n2 + 24, n6);
        Gui.drawRect(n, n2, n + n3, n2 + 1, new Color(150, 60, 220, 210).getRGB());
        String str = "Want to make your own scripts?  §nClick here for the API";
        this.inter18ptSemiboldTtfComponent
            .handleString2(str, n + (n3 - this.inter18ptSemiboldTtfComponent.computeInt3(str)) / 2, n2 + 6, flag ? -14081 : -2512641);
    }

    private void process2() {
        this.entries.clear();

        for (Module module : ModuleManager.getEntries2()) {
            if (module instanceof ScriptModule) {
                this.entries.add(new OpenApiDocsScreen.Entry((ScriptModule)module));
            }
        }
    }

    private void openApiDocs() {
        // VENDOR-DISABLED: Desktop.getDesktop().browse(...) to vendor scripting API host
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.numberSetting != null && this.count4 > 0) {
            double minValue = this.numberSetting.getMinValue();
            double maxValue = this.numberSetting.getMaxValue();
            double d = Math.max(0.0, Math.min(1.0, (double)(mouseX - this.count3) / this.count4));
            this.numberSetting.handleDouble2(minValue + d * (maxValue - minValue));
        }

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    public OpenApiDocsScreen(NewClickGUI newClickGUI) {
        this.newClickGUI = newClickGUI;
        this.process2();
    }

    private void handleInt(int n, int n2, int n3, String str, int n4, int n5) {
        boolean flag = matchesInt(n4, n5, n, n2, n + n3, n2 + 20);
        Gui.drawRect(n, n2, n + n3, n2 + 20, flag ? new Color(45, 35, 55, 240).getRGB() : new Color(25, 25, 30, 220).getRGB());
        this.inter18ptSemiboldTtfComponent.handleString2(str, n + (n3 - this.inter18ptSemiboldTtfComponent.computeInt3(str)) / 2, n2 + 6, -2512641);
    }

    private void handleInt(int n, int n2, int n3, int n4) {
        this.handleInt(n, n2, 154, "Reload", n3, n4);
        this.handleInt(n + 162, n2, 158, "Open Folder", n3, n4);
    }

    public static class Entry {
        public final ScriptModule scriptModule;
        private boolean flag;
        private final List<Setting> entries;
        public NumberSetting numberSetting;
        public int count;
        public int count2;
        public boolean flag2;

        public void handleInt(int n, int n2, int n3, int n4, int n5, Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent) {
            boolean flag = n4 >= n && n4 <= n + n3 && n5 >= n2 && n5 <= n2 + 24;
            int n6 = this.scriptModule.checkState44() ? new Color(60, 20, 20, 220).getRGB() : new Color(25, 25, 30, 220).getRGB();
            if (flag) {
                n6 = new Color(50, 50, 55, 230).getRGB();
            }

            Gui.drawRect(n, n2, n + n3, n2 + 24, n6);
            Gui.drawRect(n, n2, n + n3, n2 + 1, new Color(60, 0, 0, 200).getRGB());
            int n7 = this.scriptModule.checkState44() ? -43691 : -5592406;
            inter18ptSemiboldTtfComponent.handleString(this.scriptModule.getName(), n + 5, n2 + 4, n7);
            String str = BindCommand.computeString(this.scriptModule.getKey());
            inter18ptSemiboldTtfComponent.handleString2(str, n + n3 - inter18ptSemiboldTtfComponent.computeInt3(str) - 5, n2 + 8, -10066330);
            if (this.flag) {
                int n8 = n2 + 25;
                boolean flag2 = n4 >= n + 8 && n4 <= n + 68 && n5 >= n8 && n5 <= n8 + 14;
                Gui.drawRect(n + 8, n8, n + 68, n8 + 14, flag2 ? new Color(90, 90, 100, 230).getRGB() : new Color(45, 45, 52, 230).getRGB());
                inter18ptSemiboldTtfComponent.handleString2("Bind", n + 13, n8 + 3, flag2 ? -43691 : -5592406);
                int n9 = n8 + 16;

                for (Setting setting : this.entries) {
                    this.handleSetting(setting, n + 8, n9, n3 - 16, n4, n5, inter18ptSemiboldTtfComponent);
                    n9 += 16;
                }
            }
        }

        public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
            if (n4 < n || n4 > n + n3 || n5 < n2 || n5 > n2 + this.getHeight()) {
                return false;
            }
            if (n5 <= n2 + 24) {
                if (n6 == 1) {
                    this.flag = !this.flag;
                    return true;
                }
                if (n6 == 0) {
                    this.scriptModule.process28();
                    return true;
                }
                return false;
            }
            if (this.flag) {
                int n7 = n2 + 25;
                if (n4 >= n + 8 && n4 <= n + 68 && n5 >= n7 && n5 <= n7 + 14) {
                    if (n6 == 0) {
                        this.flag2 = true;
                    }

                    return true;
                }

                int n8 = n7 + 16;

                for (Setting setting : this.entries) {
                    if (n5 >= n8 && n5 <= n8 + 14 && this.matchesSetting(setting, n + 8, n8, n3 - 16, n4, n5, n6)) {
                        return true;
                    }

                    n8 += 16;
                }
            }

            return false;
        }

        public Entry(ScriptModule scriptModule) {
            this.scriptModule = scriptModule;
            List list = Brutality.settingsManager.computeList(scriptModule);
            this.entries = (List<Setting>)(list != null ? list : new ArrayList<>());
        }

        private boolean matchesSetting(Setting setting, int count, int n, int n2, int n3, int n4, int n5) {
            if (setting instanceof BooleanSetting) {
                if (n5 == 0) {
                    ((BooleanSetting)setting).r();
                    return true;
                }
            } else if (setting instanceof ModeSetting) {
                if (n5 == 0) {
                    ((ModeSetting)setting).process2();
                    return true;
                }

                if (n5 == 1) {
                    ((ModeSetting)setting).process3();
                    return true;
                }
            } else if (setting instanceof NumberSetting numberSetting) {
                if (n5 == 0) {
                    this.numberSetting = numberSetting;
                    this.count = count;
                    this.count2 = n2;
                    return true;
                }

                if (n5 == 1) {
                    numberSetting.handleDouble2(numberSetting.computeDouble() - numberSetting.getIncrement());
                    return true;
                }
            }

            return false;
        }

        public int getHeight() {
            int n = 24;
            if (this.flag) {
                n += 16 + this.entries.size() * 16;
            }

            return n;
        }

        private void handleSetting(Setting setting, int n, int n2, int n3, int n4, int n5, Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent) {
            boolean flag = n4 >= n && n4 <= n + n3 && n5 >= n2 && n5 <= n2 + 14;
            if (setting instanceof BooleanSetting boolSetting) {
                inter18ptSemiboldTtfComponent.handleString2(setting.getName(), n, n2 + 2, flag ? -1 : -5592406);
                String str = boolSetting.isEnabled() ? "§aON" : "§cOFF";
                inter18ptSemiboldTtfComponent.handleString2(str, n + n3 - inter18ptSemiboldTtfComponent.computeInt3(str), n2 + 2, boolSetting.isEnabled() ? -11141291 : -43691);
            } else if (setting instanceof ModeSetting modeSetting) {
                inter18ptSemiboldTtfComponent.handleString2(setting.getName(), n, n2 + 2, flag ? -1 : -5592406);
                inter18ptSemiboldTtfComponent.handleString2("§e" + modeSetting.getText(), n + n3 - inter18ptSemiboldTtfComponent.computeInt3(modeSetting.getText()), n2 + 2, -171);
            } else if (setting instanceof NumberSetting numberSetting) {
                inter18ptSemiboldTtfComponent.handleString2(setting.getName(), n, n2 + 2, flag ? -1 : -5592406);
                int n6 = n2 + 12;
                byte b = 3;
                Gui.drawRect(n, n6, n + n3, n6 + b, new Color(15, 15, 18, 230).getRGB());
                double minValue = numberSetting.getMinValue();
                double maxValue = numberSetting.getMaxValue();
                double d = maxValue == minValue ? 0.0 : (numberSetting.computeDouble() - minValue) / (maxValue - minValue);
                Gui.drawRect(n, n6, n + (int)(n3 * d), n6 + b, new Color(150, 35, 35, 240).getRGB());
                String str2 = numberSetting.toString();
                inter18ptSemiboldTtfComponent.handleString2("§7" + str2, n + n3 - inter18ptSemiboldTtfComponent.computeInt3(str2), n2 + 2, -3355444);
            } else if (setting instanceof StringSetting stringSetting) {
                inter18ptSemiboldTtfComponent.handleString2(setting.getName() + ": §7" + stringSetting.getValue(), n, n2 + 2, -5592406);
            } else {
                inter18ptSemiboldTtfComponent.handleString2(setting.getName() + ": §7" + setting.toString(), n, n2 + 2, -8947849);
            }
        }
    }
}
