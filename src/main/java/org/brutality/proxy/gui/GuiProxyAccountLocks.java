package org.brutality.proxy.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.proxy.ProxyAccountStore;
import org.brutality.proxy.ProxyConfig;
import org.lwjgl.input.Mouse;

public final class GuiProxyAccountLocks extends CustomGuiScreen {
    private final GuiScreen parent;
    private final ProxyConfig proxy;
    private List<String> entries;
    private int count;
    private int count2;
    private int count3;
    private int count4;
    private int count5;
    private int count6;

    public GuiProxyAccountLocks(GuiScreen parent, ProxyConfig proxy) {
        this.parent = parent;
        this.proxy = proxy;
    }

    @Override
    public void initGui() {
        this.entries = ProxyAccountStore.computeList(this.proxy);
        this.count4 = Math.min(380, this.width - 20);
        this.count6 = Math.max(1, Math.min(this.entries.size(), (this.height - 140) / 18));
        this.count5 = 108 + this.count6 * 18;
        this.count2 = (this.width - this.count4) / 2;
        this.count3 = Math.max(5, (this.height - this.count5) / 2);
        this.count = Math.max(0, Math.min(this.count, Math.max(0, this.entries.size() - this.count6)));
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 50, this.count3 + this.count5 - 29, 100, 20, "Back"));

        for (int i = 0; i < this.count6 && this.count + i < this.entries.size(); i++) {
            this.buttonList.add(new GuiButton(i + 1, this.count2 + this.count4 - 64, this.count3 + 58 + i * 18, 54, 16, "Remove"));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, this.width, this.height, -14474461);
        drawRect(this.count2, this.count3, this.count2 + this.count4, this.count3 + this.count5, -13421773);
        drawRect(this.count2, this.count3, this.count2 + this.count4, this.count3 + 2, -22016);
        this.drawCenteredString(this.fontRendererObj, "Locked accounts", this.width / 2, this.count3 + 12, -22016);
        this.drawCenteredString(
            this.fontRendererObj, this.fontRendererObj.trimStringToWidth(this.proxy.getName(), this.count4 - 24), this.width / 2, this.count3 + 28, -1
        );
        String str = this.entries.size() == 1 ? " account" : " accounts";
        int size = this.entries.size();
        String str2 = "Locked to " + size + str;
        this.drawCenteredString(this.fontRendererObj, str2, this.width / 2, this.count3 + 43, -3355444);

        for (int i = 0; i < this.count6 && this.count + i < this.entries.size(); i++) {
            int n = this.count3 + 61 + i * 18;
            drawRect(this.count2 + 10, n - 3, this.count2 + this.count4 - 10, n + 12, -12566464);
            String str3 = this.entries.get(this.count + i);
            this.drawString(this.fontRendererObj, this.fontRendererObj.trimStringToWidth(str3, this.count4 - 88), this.count2 + 16, n, -1);
        }

        if (this.entries.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "No linked accounts", this.width / 2, this.count3 + 61, -5592406);
        }

        if (this.entries.size() > this.count6) {
            this.drawCenteredString(this.fontRendererObj, "Scroll to see all accounts", this.width / 2, this.count3 + this.count5 - 42, -5592406);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            this.count = Math.max(0, Math.min(Math.max(0, this.entries.size() - this.count6), this.count + (eventDWheel > 0 ? -1 : 1)));
            this.initGui();
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(this.parent);
        } else {
            int n = this.count + button.id - 1;
            if (n >= 0 && n < this.entries.size()) {
                ProxyAccountStore.handleString4(this.entries.get(n));
                this.initGui();
            }
        }
    }

    @Override
    public void keyTyped(char ch, int key) throws IOException {
        if (key == 1) {
            mc.displayGuiScreen(this.parent);
        } else {
            super.keyTyped(ch, key);
        }
    }
}
