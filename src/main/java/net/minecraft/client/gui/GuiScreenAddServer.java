package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import java.io.IOException;
import java.net.IDN;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.ServerResourceMode;
import net.minecraft.client.resources.I18n;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.lwjgl.input.Keyboard;

public class GuiScreenAddServer extends GuiScreen {
    private final GuiScreen parentScreen;
    private final ServerData serverData;
    private GuiTextField serverIPField;
    private GuiTextField serverNameField;
    private GuiButton autoNameButton;
    private String originalName;
    private static Boolean autoNameEnabled;
    private GuiButton serverResourcePacks;
    private Predicate<String> field_181032_r = new Predicate<String>() {
        public boolean apply(String p_apply_1_) {
            if (p_apply_1_.length() == 0) {
                return true;
            }
            String[] parts = p_apply_1_.split(":");
            if (parts.length == 0) {
                return true;
            }
            try {
                String str = IDN.toASCII(parts[0]);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    };

    public GuiScreenAddServer(GuiScreen p_i1033_1_, ServerData p_i1033_2_) {
        this.parentScreen = p_i1033_1_;
        this.serverData = p_i1033_2_;
    }

    @Override
    public void updateScreen() {
        this.serverNameField.updateCursorCounter();
        this.serverIPField.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("addServer.add")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, I18n.format("gui.cancel")));
        this.buttonList
            .add(
                this.serverResourcePacks = new GuiButton(
                    2,
                    this.width / 2 - 100,
                    this.height / 4 + 72,
                    I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()
                )
            );
        this.serverNameField = new SearchField(0, this.width / 2 - 100, 66, 136, 20).setText2("Server name");
        if (this.originalName == null) {
            this.originalName = this.serverData.serverName == null ? "" : this.serverData.serverName;
        }

        this.buttonList.add(this.autoNameButton = new GuiButton(3, this.width / 2 + 40, 66, 60, 20, autoNameLabel()));
        this.serverNameField.setFocused(true);
        this.serverNameField.setText(this.serverData.serverName);
        this.serverIPField = new SearchField(1, this.width / 2 - 100, 106, 200, 20).setText2("mc.example.net");
        this.serverIPField.setMaxStringLength(128);
        this.serverIPField.setText(this.serverData.serverIP);
        this.serverIPField.func_175205_a(this.field_181032_r);
        this.buttonList.get(0).enabled = this.serverIPField.getText().length() > 0
            && this.serverIPField.getText().split(":").length > 0
            && this.serverNameField.getText().length() > 0;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 3) {
                setAutoNameEnabled(!isAutoNameEnabled());
                this.autoNameButton.displayString = autoNameLabel();
            } else if (button.id == 2) {
                this.serverData
                    .setResourceMode(ServerResourceMode.values()[(this.serverData.getResourceMode().ordinal() + 1) % ServerResourceMode.values().length]);
                this.serverResourcePacks.displayString = I18n.format("addServer.resourcePack")
                    + ": "
                    + this.serverData.getResourceMode().getMotd().getFormattedText();
            } else if (button.id == 1) {
                this.parentScreen.confirmClicked(false, 0);
            } else if (button.id == 0) {
                this.serverData.serverName = this.willAutoName() ? autoName(this.serverIPField.getText()) : this.serverNameField.getText();
                this.serverData.serverIP = this.serverIPField.getText();
                this.parentScreen.confirmClicked(true, 0);
            }
        }
    }

    private boolean willAutoName() {
        String str = I18n.format("selectServer.defaultName");
        boolean flag = this.originalName.isEmpty() || this.originalName.equals(str) || this.originalName.equals("Minecraft Server");
        return isAutoNameEnabled() && flag && this.serverNameField.getText().equals(this.originalName);
    }

    private static String autoNameLabel() {
        return "Auto: " + (isAutoNameEnabled() ? "ON" : "OFF");
    }

    private static Path autoNameFile() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.mcDataDir != null ? mc.mcDataDir.toPath().resolve("Brutality/server-auto-name.txt") : null;
    }

    static boolean isAutoNameEnabled() {
        if (autoNameEnabled == null) {
            autoNameEnabled = Boolean.TRUE;
            Path path = autoNameFile();

            try {
                if (path != null && Files.isRegularFile(path)) {
                    autoNameEnabled = !"false".equals(new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim());
                }
            } catch (IOException ignored) {
            }
        }

        return autoNameEnabled;
    }

    private static void setAutoNameEnabled(boolean enabled) {
        autoNameEnabled = enabled;
        Path path = autoNameFile();
        if (path != null) {
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, Boolean.toString(enabled).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ignored) {
            }
        }
    }

    static String autoName(String address) {
        String str = address == null ? "" : address.trim().toLowerCase(Locale.ROOT);
        if (str.startsWith("[")) {
            return "Minecraft Server";
        }
        int n = str.length() - str.replace(":", "").length();
        if (n > 1) {
            return "Minecraft Server";
        }
        if (n == 1) {
            str = str.substring(0, str.indexOf(58));
        }

        while (str.endsWith(".")) {
            str = str.substring(0, str.length() - 1);
        }

        if (!str.isEmpty() && !str.matches("[0-9]{1,3}([.][0-9]{1,3}){3}") && !str.equals("localhost")) {
            String[] parts = str.split("[.]");
            if (parts.length < 2) {
                return capitalize(parts[0]);
            }
            int n2 = parts.length - 2;
            HashSet set = new HashSet<>(Arrays.asList("co", "com", "net", "org", "gov", "edu", "ac"));
            if (parts.length >= 3 && parts[parts.length - 1].length() == 2 && set.contains(parts[n2])) {
                n2--;
            }

            return capitalize(parts[n2]);
        }
        return "Minecraft Server";
    }

    private static String capitalize(String value) {
        return value != null && !value.isEmpty() ? Character.toUpperCase(value.charAt(0)) + value.substring(1) : "Minecraft Server";
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.serverNameField.textboxKeyTyped(typedChar, keyCode);
        this.serverIPField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 15) {
            this.serverNameField.setFocused(!this.serverNameField.isFocused());
            this.serverIPField.setFocused(!this.serverIPField.isFocused());
        }

        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.buttonList.get(0).enabled = this.serverIPField.getText().length() > 0
            && this.serverIPField.getText().split(":").length > 0
            && this.serverNameField.getText().length() > 0;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverNameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(24, true);
        CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(17, false);
        String str = I18n.format("addServer.title");
        drawStringWithShadowComponent.drawString(str, this.width / 2 - drawStringWithShadowComponent.getStringWidth(str) / 2, 17, -1117448);
        drawStringWithShadowComponent2.drawString(I18n.format("addServer.enterName"), this.width / 2 - 100, 53, -6642248);
        drawStringWithShadowComponent2.drawString(I18n.format("addServer.enterIp"), this.width / 2 - 100, 93, -6642248);
        this.serverNameField.drawTextBox();
        this.serverIPField.drawTextBox();
        if (this.willAutoName() && !this.serverIPField.getText().trim().isEmpty()) {
            String str2 = "Will be named: " + autoName(this.serverIPField.getText());
            drawStringWithShadowComponent2.drawString(str2, this.width / 2 - 100, 130, -8399192);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
