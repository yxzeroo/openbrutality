package org.brutality.proxy.gui;

import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.proxy.ProxyConfig;
import org.brutality.proxy.ProxyManager;
import org.lwjgl.input.Keyboard;

public class GuiAddProxy extends CustomGuiScreen {
    private static final int count = -13948117;
    private static final int count2 = -1;
    private static final int count3 = -3355444;
    private static final int count4 = -16733696;
    private static final int count5 = -5636096;
    private static final int count6 = -22016;
    private static final int count7 = -11890462;
    private final GuiScreen parentScreen;
    private final ProxyConfig proxy;
    private final int index;
    private final boolean flag;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private GuiTextField nameField;
    private GuiTextField guiTextField;
    private GuiTextField guiTextField2;
    private GuiTextField guiTextField3;
    private GuiTextField guiTextField4;
    private GuiTextField guiTextField5;
    private GuiButton guiButton;
    private GuiButton guiButton2;
    private GuiButton guiButton3;
    private GuiButton guiButton4;
    private ProxyConfig.Socks4Mode socks4Mode = ProxyConfig.Socks4Mode.SOCKS5;
    private boolean hasAuthentication = false;

    public GuiAddProxy(GuiScreen parentScreen, ProxyConfig proxy, int index) {
        this.parentScreen = parentScreen;
        this.proxy = proxy;
        this.index = index;
        this.flag = proxy != null;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        if (this.flag) {
            this.socks4Mode = proxy.computeSocks4Mode();
            this.hasAuthentication = proxy.hasAuth();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        int n = this.width / 2;
        int n2 = this.height / 2;
        FontRenderer fontRenderer = mc.fontRendererObj;
        this.nameField = new SearchField(0, n - 100, n2 - 90, 200, 20);
        this.guiTextField = new SearchField(1, n - 100, n2 - 50, 200, 20);
        this.guiTextField2 = new SearchField(2, n - 100, n2 - 10, 200, 20);
        this.guiTextField3 = new SearchField(3, n - 100, n2 + 60, 200, 20);
        this.guiTextField4 = new SearchField(4, n - 100, n2 + 100, 200, 20).setFlag(true);
        this.guiTextField5 = new SearchField(5, n - 100, n2 + 60, 200, 20);
        this.nameField.setMaxStringLength(32);
        this.nameField.setFocused(true);
        this.guiTextField.setMaxStringLength(64);
        this.guiTextField2.setMaxStringLength(5);
        this.guiTextField3.setMaxStringLength(64);
        this.guiTextField4.setMaxStringLength(64);
        this.guiTextField5.setMaxStringLength(64);
        if (this.flag) {
            this.nameField.setText(this.proxy.getName());
            this.guiTextField.setText(this.proxy.computeString());
            this.guiTextField2.setText(String.valueOf(this.proxy.getPort()));
            this.guiTextField3.setText(this.proxy.getUsername());
            this.guiTextField4.setText(this.proxy.getPassword());
            this.guiTextField5.setText(this.proxy.computeString2());
        } else {
            this.guiTextField2.setText("1080");
        }

        this.guiButton = new GuiProxyManager.Entry(0, n - 100, n2 + 20, 96, 20, "Type: " + this.socks4Mode.name(), -11890462, -10772238);
        this.buttonList.add(this.guiButton);
        String str = this.hasAuthentication ? "Auth: ON" : "Auth: OFF";
        this.guiButton2 = new GuiProxyManager.Entry(
            1, n + 4, n2 + 20, 96, 20, str, this.hasAuthentication ? -16733696 : -5636096, this.hasAuthentication ? -16724992 : -3407872
        );
        this.buttonList.add(this.guiButton2);
        this.guiButton3 = new GuiProxyManager.Entry(2, n - 80, n2 + 150, 75, 20, this.flag ? "Save" : "Add", -16733696, -16724992);
        this.buttonList.add(this.guiButton3);
        this.guiButton4 = new GuiProxyManager.Entry(3, n + 5, n2 + 150, 75, 20, "Cancel", -5636096, -3407872);
        this.buttonList.add(this.guiButton4);
    }

    private void process2() {
        this.guiButton.displayString = "Type: " + this.socks4Mode.name();
        String str = this.hasAuthentication ? "Auth: ON" : "Auth: OFF";
        this.guiButton2.displayString = str;
        if (this.guiButton2 instanceof GuiProxyManager.Entry) {
            ((GuiProxyManager.Entry)this.guiButton2).handleInt(this.hasAuthentication ? -16733696 : -5636096, this.hasAuthentication ? -16724992 : -3407872);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.parentScreen != null) {
            this.parentScreen.drawScreen(-1, -1, partialTicks);
        }

        drawRect(0, 0, this.width, this.height, -1610612736);
        boolean flag = this.socks4Mode == ProxyConfig.Socks4Mode.SOCKS4;
        int n = Math.min(this.height - 10, 360);
        short s = 320;
        int n2 = (this.width - s) / 2;
        int n3 = (this.height - n) / 2;
        Gui.drawRect(n2, n3, n2 + s, n3 + n, -12566464);
        Gui.drawRect(n2, n3, n2 + s, n3 + 2, -22016);
        Gui.drawRect(n2, n3 + n - 2, n2 + s, n3 + n, -22016);
        Gui.drawRect(n2, n3, n2 + 2, n3 + n, -22016);
        Gui.drawRect(n2 + s - 2, n3, n2 + s, n3 + n, -22016);
        String str = this.flag ? "Edit Proxy" : "Add Proxy";
        this.drawStringWithShadowComponent.drawString(str, (this.width - this.drawStringWithShadowComponent.getStringWidth(str)) / 2, n3 + 10, -1);
        int n4 = this.width / 2;
        int n5 = this.height / 2;
        this.drawString(this.drawStringWithShadowComponent, "Name:", n4 - 100, n5 - 102, -3355444);
        this.drawString(this.drawStringWithShadowComponent, "IP Address:", n4 - 100, n5 - 62, -3355444);
        this.drawString(this.drawStringWithShadowComponent, "Port:", n4 - 100, n5 - 22, -3355444);
        this.nameField.drawTextBox();
        this.guiTextField.drawTextBox();
        this.guiTextField2.drawTextBox();
        if (flag) {
            this.drawString(this.drawStringWithShadowComponent, "User ID:", n4 - 100, n5 + 48, -3355444);
            this.guiTextField5.drawTextBox();
        } else if (this.hasAuthentication) {
            this.drawString(this.drawStringWithShadowComponent, "Username:", n4 - 100, n5 + 48, -3355444);
            this.drawString(this.drawStringWithShadowComponent, "Password:", n4 - 100, n5 + 88, -3355444);
            this.guiTextField3.drawTextBox();
            this.guiTextField4.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.socks4Mode = this.socks4Mode == ProxyConfig.Socks4Mode.SOCKS5 ? ProxyConfig.Socks4Mode.SOCKS4 : ProxyConfig.Socks4Mode.SOCKS5;
                this.process2();
                break;
            case 1:
                this.hasAuthentication = !this.hasAuthentication;
                this.process2();
                break;
            case 2:
                if (this.checkState()) {
                    this.process();
                    mc.displayGuiScreen(this.parentScreen);
                }
                break;
            case 3:
                mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.parentScreen);
        } else if (keyCode == 15) {
            this.process3();
        } else {
            this.nameField.textboxKeyTyped(typedChar, keyCode);
            this.guiTextField.textboxKeyTyped(typedChar, keyCode);
            this.guiTextField2.textboxKeyTyped(typedChar, keyCode);
            boolean flag = this.socks4Mode == ProxyConfig.Socks4Mode.SOCKS4;
            if (flag) {
                this.guiTextField5.textboxKeyTyped(typedChar, keyCode);
            } else if (this.hasAuthentication) {
                this.guiTextField3.textboxKeyTyped(typedChar, keyCode);
                this.guiTextField4.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    private void process3() {
        boolean flag = this.socks4Mode == ProxyConfig.Socks4Mode.SOCKS4;
        if (this.nameField.isFocused()) {
            this.nameField.setFocused(false);
            this.guiTextField.setFocused(true);
        } else if (this.guiTextField.isFocused()) {
            this.guiTextField.setFocused(false);
            this.guiTextField2.setFocused(true);
        } else if (this.guiTextField2.isFocused()) {
            this.guiTextField2.setFocused(false);
            if (flag) {
                this.guiTextField5.setFocused(true);
            } else if (this.hasAuthentication) {
                this.guiTextField3.setFocused(true);
            } else {
                this.nameField.setFocused(true);
            }
        } else if (this.guiTextField5.isFocused()) {
            this.guiTextField5.setFocused(false);
            this.nameField.setFocused(true);
        } else if (this.guiTextField3.isFocused()) {
            this.guiTextField3.setFocused(false);
            this.guiTextField4.setFocused(true);
        } else if (this.guiTextField4.isFocused()) {
            this.guiTextField4.setFocused(false);
            this.nameField.setFocused(true);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField2.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField5.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField3.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField4.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.nameField.updateCursorCounter();
        this.guiTextField.updateCursorCounter();
        this.guiTextField2.updateCursorCounter();
        this.guiTextField5.updateCursorCounter();
        this.guiTextField3.updateCursorCounter();
        this.guiTextField4.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private boolean checkState() {
        if (this.nameField.getText().trim().isEmpty()) {
            return false;
        }
        if (this.guiTextField.getText().trim().isEmpty()) {
            return false;
        }
        String str = this.guiTextField2.getText().trim();
        if (str.isEmpty()) {
            return false;
        }
        try {
            int n = Integer.parseInt(str);
            return n >= 1 && n <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void process() {
        String str = this.nameField.getText().trim();
        String str2 = this.guiTextField.getText().trim();
        int n = Integer.parseInt(this.guiTextField2.getText().trim());
        boolean flag = this.socks4Mode == ProxyConfig.Socks4Mode.SOCKS4;
        String str3 = flag ? "" : (this.hasAuthentication ? this.guiTextField3.getText().trim() : "");
        String str4 = flag ? "" : (this.hasAuthentication ? this.guiTextField4.getText() : "");
        String str5 = flag ? this.guiTextField5.getText().trim() : "";
        ProxyConfig proxyConfig = new ProxyConfig(str, str2, n, this.socks4Mode, str3, str4, str5);
        if (this.flag) {
            ProxyManager.handleInt(this.index, proxyConfig);
        } else {
            ProxyManager.handleProxyConfig2(proxyConfig);
        }
    }
}
