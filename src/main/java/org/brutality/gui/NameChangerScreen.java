package org.brutality.gui;

import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.brutality.alt.altmanager.gui.GuiAltButton;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.utils.misc.AuthorizationHelper;
import org.brutality.utils.network.SetSessionHelper;
import org.lwjgl.input.Keyboard;

public class NameChangerScreen extends CustomGuiScreen {
    private final GuiScreen guiScreen;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private String status = "";
    private GuiTextField nameField;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.drawStringWithShadowComponent, "Name Changer", this.width / 2, this.height / 2 - 50, Color.WHITE.getRGB());
        this.drawCenteredString(this.drawStringWithShadowComponent, this.status, this.width / 2, this.height / 2 - 30, Color.WHITE.getRGB());
        this.drawString(this.drawStringWithShadowComponent, "New Name:", this.width / 2 - 99, this.height / 2 - 15, Color.WHITE.getRGB());
        this.nameField.drawTextBox();

        for (GuiButton button : this.buttonList) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.nameField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.nameField = new SearchField(1, this.width / 2 - 100, this.height / 2, 200, 20);
        this.nameField.setMaxStringLength(16);
        this.nameField.setFocused(true);
        int n = -2838729;
        short s = -10496;
        this.buttonList.add(new GuiAltButton(100, this.width / 2 - 100, this.height / 2 + 30, 200, 20, "Change Name", n, s));
        this.buttonList.add(new GuiAltButton(101, this.width / 2 - 100, this.height / 2 + 55, 200, 20, "Back", -7829368, -3355444));
        super.initGui();
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) {
            String text = this.nameField.getText();
            if (text.isEmpty()) {
                this.status = "§4Please enter a name!";
                return;
            }

            this.status = "§eChanging name...";
            new Thread(() -> {
                try {
                    Session session = SetSessionHelper.computeSession();
                    String token = session.getToken();
                    int n = AuthorizationHelper.computeInt(text, token);
                    if (n == 200) {
                        this.status = "§2Successfully changed name!";
                        Session session2 = new Session(text, session.getPlayerID(), session.getToken(), session.getSessionType().name().toLowerCase());
                        SetSessionHelper.setSession(session2);
                    } else {
                        this.status = this.computeString(n);
                    }
                } catch (Exception e) {
                    this.status = "§4An unknown error occurred!";
                    e.printStackTrace();
                }
            }).start();
        }

        if (button.id == 101) {
            mc.displayGuiScreen(this.guiScreen);
        }

        super.actionPerformed(button);
    }

    private String computeString(int n) {
        switch (n) {
            case 400:
                return "§4Error: Invalid name!";
            case 401:
                return "§4Error: Invalid token!";
            case 403:
                return "§4Error: Name is unavailable or changed recently!";
            case 429:
                return "§4Error: Too many requests!";
            default:
                return "§4An unknown error occurred! (Code: " + n + ")";
        }
    }

    public NameChangerScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
