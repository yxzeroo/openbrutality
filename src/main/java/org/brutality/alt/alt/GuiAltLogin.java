package org.brutality.alt.alt;

import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.brutality.alt.alt.cookie.CookieAuth;
import org.brutality.alt.altmanager.Account;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.SearchField;
import org.lwjgl.input.Keyboard;

public final class GuiAltLogin extends CustomGuiScreen {
    private GuiPasswordField guiPasswordField;
    private final GuiScreen guiScreen;
    private AltLoginThread altLoginThread;
    private GuiTextField guiTextField;

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.altLoginThread = new AltLoginThread(this.guiTextField.getText(), this.guiPasswordField.getText());
                this.altLoginThread.start();
                break;
            case 1:
                mc.displayGuiScreen(this.guiScreen);
                break;
            case 2:
                new Thread(() -> {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    JDialog dialog = new JDialog();
                    dialog.setAlwaysOnTop(true);
                    JFileChooser jFileChooser = new JFileChooser();
                    FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Text Files", "txt");
                    jFileChooser.setFileFilter(fileNameExtensionFilter);
                    dialog.add(jFileChooser);
                    int n = jFileChooser.showOpenDialog(null);
                    if (n == 0) {
                        try {
                            Account account = CookieAuth.instance.computeAccount(jFileChooser.getSelectedFile());
                            if (account != null) {
                                mc.setSession(new Session(account.username, account.text3, account.text, "legacy"));
                                return;
                            }
                        } catch (Exception e2) {
                            throw new RuntimeException(e2);
                        } finally {
                            dialog.dispose();
                        }
                    }
                }).start();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiPasswordField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.guiTextField.updateCursorCounter();
        this.guiPasswordField.updateCursorCounter();
    }

    @Override
    public void initGui() {
        int n = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, n + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, n + 72 + 12 + 24, "Back"));
        this.guiTextField = new SearchField(n, this.width / 2 - 100, 60, 200, 20);
        this.guiPasswordField = new GuiPasswordField(Minecraft.getFontRenderer(), this.width / 2 - 100, 100, 200, 20);
        this.guiTextField.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.guiTextField.drawTextBox();
        this.guiPasswordField.drawTextBox();
        this.drawCenteredString(Minecraft.getFontRenderer(), "Alt Login", this.width / 2, 20, -1);
        this.drawCenteredString(
            Minecraft.getFontRenderer(),
            this.altLoginThread == null ? EnumChatFormatting.GRAY + "Idle..." : this.altLoginThread.getStatus(),
            this.width / 2,
            29,
            -1
        );
        if (this.guiTextField.getText().isEmpty()) {
            this.drawString(Minecraft.getFontRenderer(), "Username / E-Mail", this.width / 2 - 96, 66, -7829368);
        }

        if (this.guiPasswordField.getText().isEmpty()) {
            this.drawString(Minecraft.getFontRenderer(), "Password", this.width / 2 - 96, 106, -7829368);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (typedChar == '\t') {
            if (!this.guiTextField.isFocused() && !this.guiPasswordField.isFocused()) {
                this.guiTextField.setFocused(true);
            } else {
                this.guiTextField.setFocused(this.guiPasswordField.isFocused());
                this.guiPasswordField.setFocused(!this.guiTextField.isFocused());
            }
        }

        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.guiTextField.textboxKeyTyped(typedChar, keyCode);
        this.guiPasswordField.textboxKeyTyped(typedChar, keyCode);
    }

    public GuiAltLogin(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }
}
