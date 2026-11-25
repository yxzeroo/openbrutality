package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import org.brutality.alt.altmanager.gui.GuiAltManager;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.StyledGuiButton;
import org.brutality.gui.OceanComponent;
import org.brutality.proxy.ProxyConfig;

public class GuiDisconnected extends CustomGuiScreen {
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;
    private ServerData lastServerData;
    private ProxyConfig connectionProxy;

    public GuiDisconnected withConnectionProxy(ProxyConfig proxy) {
        this.connectionProxy = proxy;
        return this;
    }

    public ProxyConfig getConnectionProxy() {
        return this.connectionProxy;
    }

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        if (mc.getCurrentServerData() != null) {
            this.lastServerData = mc.getCurrentServerData();
        }

        int n = this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT;
        if (this.lastServerData != null) {
            this.buttonList.add(new StyledGuiButton(1, this.width / 2 - 100, n, "Reconnect"));
            n += 24;
        }

        this.buttonList.add(new StyledGuiButton(2, this.width / 2 - 100, n, "Account Manager"));
        n += 24;
        this.buttonList.add(new StyledGuiButton(0, this.width / 2 - 100, n, I18n.format("gui.toMenu")));
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1 && this.lastServerData != null) {
            ServerData lastServerData = this.lastServerData;
            mc.displayGuiScreen(new GuiConnecting(this.parentScreen, mc, lastServerData));
        } else if (button.id == 2) {
            mc.displayGuiScreen(new GuiAltManager(this.parentScreen));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        OceanComponent.handleGuiScreen(this, mouseX, mouseY, partialTicks);
        this.drawCenteredString(
            this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810
        );
        int n = this.height / 2 - this.field_175353_i / 2;
        if (this.multilineMessage != null) {
            for (String str : this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, str, this.width / 2, n, 16777215);
                n += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
