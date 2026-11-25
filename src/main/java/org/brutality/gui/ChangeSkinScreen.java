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

public class ChangeSkinScreen extends CustomGuiScreen {
    private final GuiScreen guiScreen;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private String status = "";
    private GuiTextField guiTextField;
    private GuiTextField guiTextField2;

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField2.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.guiTextField = new SearchField(1, this.width / 2 - 100, this.height / 2, 200, 20);
        this.guiTextField.setMaxStringLength(200);
        this.guiTextField.setFocused(true);
        this.guiTextField2 = new SearchField(2, this.width / 2 - 100, this.height / 2 + 50, 200, 20);
        this.guiTextField2.setMaxStringLength(16);
        int n = -2838729;
        short s = -10496;
        this.buttonList.add(new GuiAltButton(100, this.width / 2 - 100, this.height / 2 + 80, 97, 20, "Change Skin", n, s));
        this.buttonList.add(new GuiAltButton(101, this.width / 2 + 3, this.height / 2 + 80, 97, 20, "From Username", n, s));
        this.buttonList.add(new GuiAltButton(102, this.width / 2 - 100, this.height / 2 + 105, 200, 20, "Back", -7829368, -3355444));
        super.initGui();
    }

    private String computeString(int n) {
        switch (n) {
            case 400:
                return "§4Error: Invalid skin URL!";
            case 401:
                return "§4Error: Invalid token!";
            case 429:
                return "§4Error: Too many requests!";
            default:
                return "§4An unknown error occurred! (Code: " + n + ")";
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) {
            String text = this.guiTextField.getText();
            if (text.isEmpty()) {
                this.status = "§4Please enter a skin URL!";
                return;
            }

            this.handleString(text);
        }

        if (button.id == 101) {
            String text2 = this.guiTextField2.getText();
            if (text2.isEmpty()) {
                this.status = "§4Please enter a username!";
                return;
            }

            String str = "https://minotar.net/skin/" + text2;
            this.handleString(str);
        }

        if (button.id == 102) {
            mc.displayGuiScreen(this.guiScreen);
        }

        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.guiTextField.textboxKeyTyped(typedChar, keyCode);
        this.guiTextField2.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.drawStringWithShadowComponent, "Skin Changer", this.width / 2, this.height / 2 - 70, Color.WHITE.getRGB());
        this.drawCenteredString(this.drawStringWithShadowComponent, this.status, this.width / 2, this.height / 2 - 50, Color.WHITE.getRGB());
        this.drawString(this.drawStringWithShadowComponent, "Skin URL:", this.width / 2 - 99, this.height / 2 - 15, Color.WHITE.getRGB());
        this.drawString(this.drawStringWithShadowComponent, "Or Username:", this.width / 2 - 99, this.height / 2 + 35, Color.WHITE.getRGB());
        this.guiTextField.drawTextBox();
        this.guiTextField2.drawTextBox();

        for (GuiButton button : this.buttonList) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    private void handleString(String str) {
        this.status = "§eChanging skin...";
        new Thread(() -> {
            try {
                Session session = SetSessionHelper.computeSession();
                String token = session.getToken();
                int n = AuthorizationHelper.computeInt2(str, token);
                if (n == 200) {
                    this.status = "§2Successfully changed skin!";
                } else {
                    this.status = this.computeString(n);
                }
            } catch (Exception e) {
                this.status = "§4An unknown error occurred!";
                e.printStackTrace();
            }
        }).start();
    }

    public ChangeSkinScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }
}
