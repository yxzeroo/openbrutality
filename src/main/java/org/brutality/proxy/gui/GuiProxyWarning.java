package org.brutality.proxy.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.proxy.ProxyAccountStore;

public class GuiProxyWarning extends CustomGuiScreen {
    private static final int count = -13750738;
    private static final int count2 = -22016;
    private static final int count3 = -3355444;
    private static final int count4 = -43691;
    private static final int count5 = -16755456;
    private static final int count6 = -11206656;
    private static final int count7 = -16746752;
    private static final int count8 = -8978432;
    private final GuiScreen parentScreen;
    private final ServerData serverData;
    private final ProxyAccountStore.NoneMode warningType;
    private final String currentUsername;
    private final String text;
    private CustomFontRenderer drawStringWithShadowComponent;
    private CustomFontRenderer drawStringWithShadowComponent2;
    private GuiButton guiButton;
    private GuiButton guiButton2;

    public GuiProxyWarning(GuiScreen parentScreen, ServerData serverData, ProxyAccountStore.NoneMode warningType, String currentUsername) {
        this.parentScreen = parentScreen;
        this.serverData = serverData;
        this.warningType = warningType;
        this.currentUsername = currentUsername;
        this.text = warningType == ProxyAccountStore.NoneMode.SHARED_PROXY ? ProxyAccountStore.computeString(currentUsername) : null;
    }

    @Override
    public void initGui() {
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2();
        int n = this.width / 2;
        int n2 = this.height / 2;
        byte b = 100;
        byte b2 = 22;
        byte b3 = 20;
        this.buttonList.clear();
        this.buttonList.add(this.guiButton = new GuiProxyWarning.Entry(0, n - b3 / 2 - b, n2 + 55, b, b2, "← Back", -11206656, -8978432));
        this.buttonList.add(this.guiButton2 = new GuiProxyWarning.Entry(1, n + b3 / 2, n2 + 55, b, b2, "Proceed →", -16755456, -16746752));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.parentScreen != null) {
            this.parentScreen.drawScreen(mouseX, mouseY, partialTicks);
        } else {
            OceanComponent.handleGuiScreen(this, mouseX, mouseY, partialTicks);
        }

        drawRect(0, 0, this.width, this.height, -1728053248);
        short s = 360;
        short s2 = 160;
        int n = this.width / 2;
        int n2 = this.height / 2;
        int n3 = n - s / 2;
        int n4 = n2 - s2 / 2;
        Gui.drawRect(n3, n4, n3 + s, n4 + s2, -13750738);
        byte b = 2;
        Gui.drawRect(n3, n4, n3 + s, n4 + b, -22016);
        Gui.drawRect(n3, n4 + s2 - b, n3 + s, n4 + s2, -22016);
        Gui.drawRect(n3, n4, n3 + b, n4 + s2, -22016);
        Gui.drawRect(n3 + s - b, n4, n3 + s, n4 + s2, -22016);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        CustomFontRenderer drawStringWithShadowComponent = this.drawStringWithShadowComponent != null
            ? this.drawStringWithShadowComponent
            : FontComponent.computeDrawStringWithShadowComponent();
        CustomFontRenderer drawStringWithShadowComponent2 = this.drawStringWithShadowComponent2 != null ? this.drawStringWithShadowComponent2 : drawStringWithShadowComponent;
        Gui.drawRect(n3 + b, n4 + b, n3 + s - b, n4 + 26, -12967424);
        handleDrawStringWithShadowComponent(drawStringWithShadowComponent2, "⚠  " + this.getTitle(), n, n4 + 10, -43691);
        int n5 = n4 + 35;
        int n6 = Math.max(12, (int)drawStringWithShadowComponent.getFONT_HEIGHT() + 2);

        for (String str : this.computeStringArray()) {
            handleDrawStringWithShadowComponent(drawStringWithShadowComponent, str, n, n5, -3355444);
            n5 += n6;
        }

        String str2 = "Server: " + this.serverData.serverIP;
        handleDrawStringWithShadowComponent(drawStringWithShadowComponent, str2, n, n5 + 4, -43691);

        for (GuiButton button : this.buttonList) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    private static void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, String str, int n, int n2, int n3) {
        if (drawStringWithShadowComponent != null && str != null) {
            drawStringWithShadowComponent.drawStringWithShadow(str, n - drawStringWithShadowComponent.getStringWidth(str) / 2.0F, n2, n3);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(this.parentScreen);
                break;
            case 1:
                mc.displayGuiScreen(new GuiConnecting(this.parentScreen, mc, this.serverData));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.parentScreen);
        }
    }

    private String getTitle() {
        switch (this.warningType) {
            case NO_PROXY:
                return "No Proxy Configured";
            case SHARED_PROXY:
                return "Shared Proxy Detected";
            default:
                return "Proxy Warning";
        }
    }

    private String[] computeStringArray() {
        switch (this.warningType) {
            case NO_PROXY:
                return new String[]{
                    "Account \"" + this.currentUsername + "\" has no proxy locked to it.",
                    "You will connect with your real IP address.",
                    "Do you wish to proceed?"
                };
            case SHARED_PROXY:
                String str = this.text != null ? this.text : "another account";
                return new String[]{
                    "This proxy is also used by: " + str,
                    "To avoid getting chain banned, we recommend",
                    "using a different proxy for this account.",
                    "Proceed anyway?"
                };
            default:
                return new String[]{"Unknown proxy warning."};
        }
    }

    public static class Entry extends GuiButton {
        private final int count;
        private final int count2;
        private final CustomFontRenderer drawStringWithShadowComponent;
        private static final long timestamp = -4186964317509674599L;

        public Entry(int n, int n2, int n3, int n4, int n5, String str, int count, int n6) {
            super(n, n2, n3, n4, n5, str);
            this.count = count;
            this.count2 = n6;
            this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                boolean flag = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
                int n = flag ? this.count2 : this.count;
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, n);
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, -1);
                Gui.drawRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, -1);
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, -1);
                Gui.drawRect(this.xPosition + this.width - 1, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -1);
                int n2 = this.enabled ? -1 : (int)timestamp;
                CustomFontRenderer drawStringWithShadowComponent = this.drawStringWithShadowComponent != null
                    ? this.drawStringWithShadowComponent
                    : FontComponent.computeDrawStringWithShadowComponent();
                if (drawStringWithShadowComponent != null) {
                    int n3 = this.xPosition + (this.width - drawStringWithShadowComponent.getStringWidth(this.displayString)) / 2;
                    int n4 = this.yPosition + (this.height - (int)drawStringWithShadowComponent.getFONT_HEIGHT()) / 2;
                    drawStringWithShadowComponent.drawString(this.displayString, n3, n4, n2);
                } else {
                    FontRenderer fontRenderer = mc.fontRendererObj;
                    fontRenderer.drawString(
                        this.displayString,
                        this.xPosition + (this.width - fontRenderer.getStringWidth(this.displayString)) / 2,
                        this.yPosition + (this.height - 8) / 2,
                        n2
                    );
                }
            }
        }
    }
}
