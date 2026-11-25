package org.brutality.proxy.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.MenuButtonRenderer;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.proxy.ProxyAccountStore;
import org.brutality.proxy.ProxyBanToast;
import org.brutality.proxy.ProxyConfig;
import org.brutality.proxy.ProxyManager;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiProxyManager extends CustomGuiScreen {
    private static final int count = -15920096;
    private static final int count2 = -15655893;
    private static final int count3 = -11890462;
    private static final int count4 = -1;
    private static final int count5 = -6575427;
    private static final int count6 = -15261134;
    private static final int count7 = -14666934;
    private static final int count8 = -16733696;
    private static final int count9 = -5636096;
    private static final int count10 = -22016;
    private final GuiScreen previousScreen;
    public CustomFontRenderer drawStringWithShadowComponent;
    private CustomFontRenderer drawStringWithShadowComponent2;
    private List<GuiProxyManager.Entry2> entries;
    private int count11 = 0;
    private int count12 = 0;
    public GuiProxyManager.Entry2 entry2 = null;
    private GuiProxyManager.Entry entry;
    private GuiProxyManager.Entry entry3;
    private GuiProxyManager.Entry entry4;
    private GuiProxyManager.Entry entry5;
    private GuiProxyManager.Entry entry6;
    private GuiProxyManager.Entry entry7;
    private GuiProxyManager.Entry entry8;
    private boolean flag = false;
    private String text = null;
    private boolean flag2 = false;
    private String text2 = null;

    public GuiProxyManager(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        this.drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(22, false);
        ProxyManager.load();
        ProxyAccountStore.load();
        ProxyAccountStore.process();
        this.process2();
        this.buttonList.clear();
        int n = Math.min(100, Math.max(40, (this.width - 70) / 6));
        byte b = 20;
        byte b2 = 10;
        int n2 = this.height - 35;
        int n3 = n * 6 + b2 * 5;
        int n4 = (this.width - n3) / 2;
        this.buttonList.add(this.entry = new GuiProxyManager.Entry(0, n4, n2, n, b, "Connect", -16733696, -16724992));
        this.buttonList.add(this.entry3 = new GuiProxyManager.Entry(1, n4 + n + b2, n2, n, b, "Disconnect", -5636096, -3407872));
        this.buttonList.add(this.entry7 = new GuiProxyManager.Entry(6, n4 + (n + b2) * 2, n2, n, b, "Test", -11890462, -10772238));
        this.buttonList.add(this.entry4 = new GuiProxyManager.Entry(2, n4 + (n + b2) * 3, n2, n, b, "Add Proxy", -11890462, -10772238));
        this.buttonList.add(this.entry5 = new GuiProxyManager.Entry(3, n4 + (n + b2) * 4, n2, n, b, "Edit", -22016, -13312));
        this.buttonList.add(this.entry6 = new GuiProxyManager.Entry(4, n4 + (n + b2) * 5, n2, n, b, "Delete", -5636096, -3407872));
        this.buttonList.add(this.entry8 = new GuiProxyManager.Entry(5, this.width - 80, 10, 70, 20, "Back", -6575427, -1));
        this.process4();
    }

    private void process2() {
        this.entries = new ArrayList<>();
        List list = ProxyManager.computeList();
        int n = Math.max(1, Math.min(3, (this.width - 15) / 215));
        int n2 = Math.min(200, this.width - 30);
        byte b = 112;
        byte b2 = 15;
        int n3 = n * n2 + (n - 1) * b2;
        int n4 = (this.width - n3) / 2;
        byte b3 = 80;

        for (int i = 0; i < list.size(); i++) {
            ProxyConfig proxyConfig = (ProxyConfig)list.get(i);
            int n5 = i / n;
            int n6 = i % n;
            int n7 = n4 + n6 * (n2 + b2);
            int n8 = b3 + n5 * (b + b2);
            GuiProxyManager.Entry2 entry2 = new GuiProxyManager.Entry2(proxyConfig, n7, n8, n2, b, i);
            this.entries.add(entry2);
        }

        this.process5();
        this.count11 = Math.max(0, Math.min(this.count12, this.count11));
    }

    private void process5() {
        if (!this.entries.isEmpty()) {
            GuiProxyManager.Entry2 entry2 = this.entries.get(this.entries.size() - 1);
            this.count12 = Math.max(0, entry2.y2 + entry2.height - (this.height - 80));
        } else {
            this.count12 = 0;
        }
    }

    private void process4() {
        boolean flag = this.entry2 != null;
        boolean flag2 = ProxyManager.checkState();
        boolean flag3 = this.checkState();
        this.entry.enabled = flag && !this.flag2 && !flag3;
        this.entry3.enabled = flag2 && !flag3;
        this.entry7.enabled = flag && !this.flag && !this.flag2;
        this.entry5.enabled = flag;
        this.entry6.enabled = flag;
    }

    public void handleInt(int index) {
        ProxyManager.handleInt(index);
        this.entry2 = null;
        this.process2();
        this.process4();
    }

    private String computeString(ProxyConfig proxyConfig) {
        if (proxyConfig == null) {
            return null;
        }
        for (Map.Entry entry : ProxyAccountStore.getLookupMap().entrySet()) {
            ProxyConfig val = (ProxyConfig)entry.getValue();
            if (val.computeString().equals(proxyConfig.computeString()) && val.getPort() == proxyConfig.getPort()) {
                return (String)entry.getKey();
            }
        }

        return null;
    }

    private boolean checkState() {
        try {
            if (mc != null && mc.getSession() != null) {
                String username = mc.getSession().getUsername();
                return username != null && !username.isEmpty() && ProxyAccountStore.matchesString(username);
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private String computeString() {
        try {
            if (mc != null && mc.getSession() != null) {
                String username = mc.getSession().getUsername();
                return username != null ? username : "";
            }
        } catch (Exception ignored) {
        }

        return "";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.process();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.process6();
        GlStateManager.pushMatrix();
        this.handleInt(mouseX, mouseY);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (GuiButton button : this.buttonList) {
            button.drawButton(mc, mouseX, mouseY);
        }

        this.process3();
        this.process7();
    }

    private void process() {
        MenuButtonRenderer.handleInt(this.width, this.height, 64, this.height - 80);
    }

    private void process6() {
        CustomFontRenderer drawStringWithShadowComponent = this.drawStringWithShadowComponent2 != null
            ? this.drawStringWithShadowComponent2
            : this.drawStringWithShadowComponent;
        String str = "Brutality ";
        String str2 = "Proxy Manager";
        int textWidth = drawStringWithShadowComponent.getStringWidth(str);
        int textWidth2 = drawStringWithShadowComponent.getStringWidth(str2);
        int n = textWidth + textWidth2;
        int n2 = this.width / 2 - n / 2;
        drawStringWithShadowComponent.drawString(str, n2, 15, -6575427);
        drawStringWithShadowComponent.drawString(str2, n2 + textWidth, 15, -1511690);
        String str3 = "Proxies: " + this.entries.size();
        this.drawCenteredString(this.drawStringWithShadowComponent, str3, this.width / 2, 45, -6575427);
    }

    private void handleInt(int n, int n2) {
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();

        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(0, mc.displayHeight - (this.height - 80) * scaleFactor, mc.displayWidth, Math.max(0, this.height - 145) * scaleFactor);

            for (GuiProxyManager.Entry2 entry2 : this.entries) {
                entry2.handleInt(n, n2, this.count11);
            }
        }
    }

    private void process3() {
        if (ProxyManager.checkState()) {
            ProxyConfig proxyConfig = ProxyManager.getProxyConfig();
            if (proxyConfig != null) {
                String str = "§aConnected: " + proxyConfig.getDisplayName();
                int textWidth = this.drawStringWithShadowComponent.getStringWidth(str);
                this.drawString(this.drawStringWithShadowComponent, str, this.width - textWidth - 10, this.height - 40, -1);
            }
        } else {
            String str2 = "§cNot connected";
            int textWidth2 = this.drawStringWithShadowComponent.getStringWidth(str2);
            this.drawString(this.drawStringWithShadowComponent, str2, this.width - textWidth2 - 10, this.height - 40, -1);
        }

        if (this.checkState()) {
            String str3 = this.computeString();
            String str4 = "§6Proxy locked to account §e" + str3;
            String str5 = "§7Unlock in Alt Manager or switch accounts to change proxy";
            this.drawCenteredString(this.drawStringWithShadowComponent, str4, this.width / 2, this.height - 58, -1);
            this.drawCenteredString(this.drawStringWithShadowComponent, str5, this.width / 2, this.height - 46, -1);
        }
    }

    private void process7() {
        int n = this.height - 20;
        if (this.flag2) {
            String str = "§eConnecting and testing proxy...";
            int textWidth = this.drawStringWithShadowComponent.getStringWidth(str);
            this.drawString(this.drawStringWithShadowComponent, str, this.width - textWidth - 10, n, -1);
        } else if (this.text2 != null) {
            int textWidth2 = this.drawStringWithShadowComponent.getStringWidth(this.text2);
            this.drawString(this.drawStringWithShadowComponent, this.text2, this.width - textWidth2 - 10, n, -1);
        } else if (this.flag) {
            String str2 = "§eTesting proxy...";
            int textWidth3 = this.drawStringWithShadowComponent.getStringWidth(str2);
            this.drawString(this.drawStringWithShadowComponent, str2, this.width - textWidth3 - 10, n, -1);
        } else if (this.text != null) {
            int textWidth4 = this.drawStringWithShadowComponent.getStringWidth(this.text);
            this.drawString(this.drawStringWithShadowComponent, this.text, this.width - textWidth4 - 10, n, -1);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                if (this.entry2 != null && !this.flag2 && !this.checkState()) {
                    String str = this.computeString(this.entry2.proxy);
                    if (str != null) {
                        ProxyBanToast.handleString2(this.entry2.proxy.getDisplayName(), str);
                    }

                    this.flag2 = true;
                    this.text2 = null;
                    this.process4();
                    ProxyManager.handleProxyConfig(this.entry2.proxy);
                    ProxyManager.testProxy(this.entry2.proxy).thenAccept(val -> {
                        this.flag2 = false;
                        if (val) {
                            this.text2 = "§aProxy connected and working!";
                        } else {
                            this.text2 = "§cProxy connected but may not be working properly";
                        }

                        this.process4();
                    });
                }
                break;
            case 1:
                ProxyManager.process4();
                this.text2 = null;
                this.process4();
                break;
            case 2:
                mc.displayGuiScreen(new GuiAddProxy(this, null, -1));
                break;
            case 3:
                if (this.entry2 != null) {
                    mc.displayGuiScreen(new GuiAddProxy(this, this.entry2.proxy, this.entry2.index));
                }
                break;
            case 4:
                if (this.entry2 != null) {
                    ArrayList list = new ArrayList();

                    for (Map.Entry entry : ProxyAccountStore.getLookupMap().entrySet()) {
                        ProxyConfig val2 = (ProxyConfig)entry.getValue();
                        if (val2.computeString().equals(this.entry2.proxy.computeString()) && val2.getPort() == this.entry2.proxy.getPort()) {
                            list.add((String)entry.getKey());
                        }
                    }

                    if (!list.isEmpty()) {
                        mc.displayGuiScreen(new GuiDeleteProxyConfirm(this, this.entry2.proxy, this.entry2.index));
                        this.entry2 = null;
                    } else {
                        ProxyManager.handleInt(this.entry2.index);
                        this.entry2 = null;
                        this.process2();
                        this.process4();
                    }
                }
                break;
            case 5:
                mc.displayGuiScreen(this.previousScreen);
                break;
            case 6:
                if (this.entry2 != null && !this.flag) {
                    this.flag = true;
                    this.text = null;
                    this.process4();
                    ProxyManager.testProxy(this.entry2.proxy).thenAccept(val3 -> {
                        this.flag = false;
                        this.text = val3 ? "§aProxy works!" : "§cProxy failed!";
                        this.process4();
                    });
                }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && mouseY >= 65 && mouseY < this.height - 80) {
            for (GuiProxyManager.Entry2 entry2 : this.entries) {
                if (entry2.matchesInt2(mouseX, mouseY, this.count11)) {
                    this.entry2 = entry2;
                    mc.displayGuiScreen(new GuiProxyAccountLocks(this, entry2.proxy));
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && mouseY >= 65 && mouseY < this.height - 80) {
            this.entry2 = null;

            for (GuiProxyManager.Entry2 entry22 : this.entries) {
                if (entry22.matchesInt(mouseX, mouseY, this.count11)) {
                    this.entry2 = entry22;
                    break;
                }
            }

            this.process4();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.previousScreen);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            this.count11 -= eventDWheel > 0 ? 30 : -30;
            this.count11 = Math.max(0, Math.min(this.count12, this.count11));
        }
    }

    public static class Entry extends GuiButton {
        private float factor;
        private long timestamp = System.nanoTime();
        private int baseColor2;
        private int hoverColor;
        private final CustomFontRenderer drawStringWithShadowComponent;

        public Entry(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int baseColor, int hoverColor) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.baseColor2 = baseColor;
            this.hoverColor = hoverColor;
            this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        }

        public void handleInt(int baseColor, int hoverColor) {
            this.baseColor2 = baseColor;
            this.hoverColor = hoverColor;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                long now = System.nanoTime();
                float f = Math.min(0.05F, (float)(now - this.timestamp) / 1.0E9F);
                this.timestamp = now;
                this.factor = this.factor + ((this.hovered && this.enabled ? 1.0F : 0.0F) - this.factor) * (1.0F - (float)Math.exp(-14.0F * f));
                MenuButtonRenderer.handleInt(
                    this.xPosition,
                    this.yPosition,
                    this.width,
                    this.height,
                    this.displayString,
                    this.drawStringWithShadowComponent,
                    this.baseColor2,
                    this.factor,
                    this.enabled
                );
            }
        }
    }

    public class Entry2 {
        public final ProxyConfig proxy;
        public int x2;
        public int y2;
        public final int width;
        public final int height;
        public final int index;
        public final CustomFontRenderer drawStringWithShadowComponent;
        public final List<String> entries;

        public Entry2(ProxyConfig proxy, int x2, int y2, int width, int height, int index) {
            this.proxy = proxy;
            this.x2 = x2;
            this.y2 = y2;
            this.width = width;
            this.height = height;
            this.index = index;
            this.drawStringWithShadowComponent = GuiProxyManager.this.drawStringWithShadowComponent;
            this.entries = ProxyAccountStore.computeList(proxy);
        }

        public void handleInt(int mouseX, int mouseY, int scrollOffset) {
            int n = this.y2 - scrollOffset;
            boolean flag = this.matchesInt(mouseX, mouseY, scrollOffset);
            boolean flag2 = GuiProxyManager.this.entry2 == this;
            ProxyConfig proxyConfig = ProxyManager.getProxyConfig();
            boolean flag3 = proxyConfig != null && proxyConfig.computeString().equals(this.proxy.computeString()) && proxyConfig.getPort() == this.proxy.getPort();
            MenuButtonRenderer.handleInt(this.x2, n, this.width, this.height, flag, flag2, flag3);
            int n2 = this.x2 + 8;
            int n3 = n + 8;
            int n4 = this.drawStringWithShadowComponent.FONT_HEIGHT + 2;
            String str = ProxyManager.hasBanWarning(this.proxy) ? " §c[Ban warning]" : "";
            String name = this.proxy.getName();
            String str2 = "§f" + name + str;
            this.drawStringWithShadowComponent.drawString(str2, n2, n3, -1);
            n3 += n4;
            String str3 = "§7" + this.proxy.computeString() + ":" + this.proxy.getPort();
            this.drawStringWithShadowComponent.drawString(str3, n2, n3, -1);
            n3 += n4;
            String str4 = "§b" + this.proxy.computeSocks4Mode().name();
            String str5 = this.proxy.hasAuth() ? " §aAuth" : " §7No Auth";
            this.drawStringWithShadowComponent.drawString(str4 + str5, n2, n3, -1);
            if (!this.entries.isEmpty()) {
                String str6 = this.entries.size() == 1 ? "Locked to an account" : "Locked to " + this.entries.size() + " accounts";
                this.drawStringWithShadowComponent.drawString(str6, n2, n + this.height - 46, -22016);
                int n5 = n + this.height - 29;
                boolean flag4 = this.matchesInt2(mouseX, mouseY, scrollOffset);
                MenuButtonRenderer.handleInt(this.x2 + 8, n5, this.width - 16, 21, -8144657, flag4 ? 1.0F : 0.0F, true);
                String str7 = "View accounts (" + this.entries.size() + ")";
                this.drawStringWithShadowComponent
                    .drawString(str7, this.x2 + (this.width - this.drawStringWithShadowComponent.getStringWidth(str7)) / 2, n5 + 6, -1);
            }

            if (flag3) {
                String str8 = "§a§lACTIVE";
                int textWidth = this.drawStringWithShadowComponent.getStringWidth(str8);
                this.drawStringWithShadowComponent.drawString(str8, this.x2 + this.width - textWidth - 8, n + 8, -16711936);
            }
        }

        public boolean matchesInt2(int mouseX, int mouseY, int scrollOffset) {
            int n = this.y2 - scrollOffset + this.height - 29;
            return !this.entries.isEmpty()
                && mouseY >= 65
                && mouseY < GuiProxyManager.this.height - 80
                && mouseX >= this.x2 + 8
                && mouseX < this.x2 + this.width - 8
                && mouseY >= n
                && mouseY < n + 21;
        }

        public boolean matchesInt(int mouseX, int mouseY, int scrollOffset) {
            int n = this.y2 - scrollOffset;
            return mouseY >= 65
                && mouseY < GuiProxyManager.this.height - 80
                && mouseX >= this.x2
                && mouseX <= this.x2 + this.width
                && mouseY >= n
                && mouseY <= n + this.height;
        }
    }
}
