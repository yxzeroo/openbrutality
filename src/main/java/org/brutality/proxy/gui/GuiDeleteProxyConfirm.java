package org.brutality.proxy.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.proxy.ProxyAccountStore;
import org.brutality.proxy.ProxyConfig;

public class GuiDeleteProxyConfirm extends CustomGuiScreen {
    private static final int count = -872415232;
    private static final int count2 = -13750738;
    private static final int count3 = -22016;
    private static final int count4 = -1;
    private static final int count5 = -3355444;
    private static final int count6 = -43691;
    private static final int count7 = -16755456;
    private static final int count8 = -11206656;
    private static final int count9 = -16746752;
    private static final int count10 = -8978432;
    private final GuiScreen parentScreen;
    private final ProxyConfig proxyToDelete;
    private final int proxyIndex;
    private final List<String> b;
    private CustomFontRenderer drawStringWithShadowComponent;
    private CustomFontRenderer drawStringWithShadowComponent2;
    private GuiButton guiButton;
    private GuiButton guiButton2;

    public GuiDeleteProxyConfirm(GuiScreen parentScreen, ProxyConfig proxyToDelete, int proxyIndex) {
        this.parentScreen = parentScreen;
        this.proxyToDelete = proxyToDelete;
        this.proxyIndex = proxyIndex;
        this.b = this.computeList(proxyToDelete);
    }

    private List<String> computeList(ProxyConfig proxyConfig) {
        ArrayList list = new ArrayList();

        for (Map.Entry entry : ProxyAccountStore.getLookupMap().entrySet()) {
            ProxyConfig val = (ProxyConfig)entry.getValue();
            if (val.computeString().equals(proxyConfig.computeString()) && val.getPort() == proxyConfig.getPort()) {
                list.add((String)entry.getKey());
            }
        }

        return list;
    }

    @Override
    public void initGui() {
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(14, false);
        this.drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(18, false);
        int n = this.width / 2;
        int n2 = this.height / 2;
        byte b = 100;
        byte b2 = 22;
        byte b3 = 20;
        this.buttonList.clear();
        this.buttonList.add(this.guiButton = new GuiDeleteProxyConfirm.Entry(0, n - b3 / 2 - b, n2 + 75, b, b2, "Cancel", -11206656, -8978432));
        this.buttonList.add(this.guiButton2 = new GuiDeleteProxyConfirm.Entry(1, n + b3 / 2, n2 + 75, b, b2, "Delete", -16755456, -16746752));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, this.width, this.height, -872415232);
        short s = 380;
        byte b = 40;
        byte b2 = 14;
        int n = this.b.size() * b2;
        int n2 = 30 + n;
        int n3 = b + n2 + 60;
        int n4 = this.width / 2;
        int n5 = this.height / 2;
        int n6 = n4 - s / 2;
        int n7 = n5 - n3 / 2;
        Gui.drawRect(n6, n7, n6 + s, n7 + n3, -13750738);
        byte b3 = 2;
        Gui.drawRect(n6, n7, n6 + s, n7 + b3, -22016);
        Gui.drawRect(n6, n7 + n3 - b3, n6 + s, n7 + n3, -22016);
        Gui.drawRect(n6, n7, n6 + b3, n7 + n3, -22016);
        Gui.drawRect(n6 + s - b3, n7, n6 + s, n7 + n3, -22016);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawRect(n6 + b3, n7 + b3, n6 + s - b3, n7 + 26, -12967424);
        String str = "⚠  Delete Proxy: " + this.proxyToDelete.getDisplayName();
        this.drawCenteredString(
            this.drawStringWithShadowComponent2 != null ? this.drawStringWithShadowComponent2 : this.drawStringWithShadowComponent,
            this.computeString(str, s - 20),
            n4,
            n7 + 10,
            -43691
        );
        int n8 = n7 + 35;
        String str2 = this.b.size() == 1 ? "This proxy is locked to 1 account:" : "This proxy is locked to " + this.b.size() + " accounts:";
        this.drawCenteredString(
            (FontRenderer)(this.drawStringWithShadowComponent != null ? this.drawStringWithShadowComponent : mc.fontRendererObj), str2, n4, n8, -1
        );
        int n9 = n8 + b2 + 8;

        for (String str3 : this.b) {
            String str4 = "• " + str3;
            this.drawCenteredString(
                (FontRenderer)(this.drawStringWithShadowComponent != null ? this.drawStringWithShadowComponent : mc.fontRendererObj),
                str4,
                n4,
                n9,
                -43691
            );
            n9 += b2;
        }

        n8 = n9 + 12;
        String str5 = "Deleting will remove the lock from these accounts.";
        this.drawCenteredString(
            (FontRenderer)(this.drawStringWithShadowComponent != null ? this.drawStringWithShadowComponent : mc.fontRendererObj), str5, n4, n8, -3355444
        );
        n8 += b2;
        String str6 = "Proceed with deletion?";
        this.drawCenteredString(
            (FontRenderer)(this.drawStringWithShadowComponent != null ? this.drawStringWithShadowComponent : mc.fontRendererObj), str6, n4, n8, -3355444
        );

        for (GuiButton button : this.buttonList) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(this.parentScreen);
                break;
            case 1:
                for (String str : this.b) {
                    ProxyAccountStore.handleString4(str);
                }

                if (this.parentScreen instanceof GuiProxyManager) {
                    ((GuiProxyManager)this.parentScreen).handleInt(this.proxyIndex);
                }

                mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.parentScreen);
        }
    }

    private String computeString(String str, int n) {
        if (this.drawStringWithShadowComponent == null) {
            return str;
        }
        if (this.drawStringWithShadowComponent.getStringWidth(str) <= n) {
            return str;
        }
        while (this.drawStringWithShadowComponent.getStringWidth(str + "...") > n && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }

        return str + "...";
    }

    public static class Entry extends GuiButton {
        private final int count;
        private final int count2;
        private static final long timestamp = -3489636816060311143L;

        public Entry(int n, int n2, int n3, int n4, int n5, String str, int count, int n6) {
            super(n, n2, n3, n4, n5, str);
            this.count = count;
            this.count2 = n6;
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
                Minecraft.getMinecraft()
                    .fontRendererObj
                    .drawString(
                        this.displayString,
                        this.xPosition + (this.width - Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString)) / 2,
                        this.yPosition + (this.height - 8) / 2,
                        n2
                    );
            }
        }
    }
}
