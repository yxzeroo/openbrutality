package org.brutality.proxy;

import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;

public class ProxyBanToast {
    private static final long timestamp = 250L;
    private static final long timestamp2 = 3000L;
    private static final int count = 8;
    private static final int count2 = -16724924;
    private static final int count3 = -48060;
    private static final int count4 = -22016;
    private final String line1;
    private final String line2;
    private final int accentColor;
    private long timestamp3 = -1L;
    private static final Queue<ProxyBanToast> queue = new ArrayDeque<>();
    private static ProxyBanToast instance = null;

    private ProxyBanToast(String line1, String line2, int accentColor) {
        this.line1 = line1;
        this.line2 = line2;
        this.accentColor = accentColor;
    }

    public static void handleString2(String proxyName) {
        handleProxyBanToast(new ProxyBanToast("Connected to " + proxyName, null, -16724924));
    }

    public static void handleString(String proxyName, String reason) {
        handleProxyBanToast(new ProxyBanToast("Disconnected from " + proxyName, "Due to: " + reason, -48060));
    }

    public static void handleString2(String proxyName, String lockedToAccount) {
        handleProxyBanToast(new ProxyBanToast("⚠ Proxy locked to: " + lockedToAccount, "Chain-ban risk — " + proxyName + " is shared", -22016));
    }

    public static void handleString(String proxyName) {
        handleProxyBanToast(new ProxyBanToast("Previously used on a banned connection", proxyName, -22016));
    }

    private static void handleProxyBanToast(ProxyBanToast toast) {
        synchronized (queue) {
            queue.add(toast);
        }
    }

    private long computeLong() {
        return this.timestamp3 < 0L ? 0L : System.currentTimeMillis() - this.timestamp3;
    }

    private long computeLong2() {
        return 3500L;
    }

    private boolean isExpired() {
        return this.timestamp3 >= 0L && this.computeLong() > this.computeLong2();
    }

    public static void render() {
        synchronized (queue) {
            if (instance == null || instance.isExpired()) {
                instance = queue.poll();
                if (instance != null) {
                    instance.timestamp3 = System.currentTimeMillis();
                    process();
                }
            }
        }

        if (instance != null && !instance.isExpired()) {
            instance.draw();
        } else {
            instance = null;
        }
    }

    private void draw() {
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        if (drawStringWithShadowComponent != null) {
            boolean flag = this.line2 != null;
            int n = drawStringWithShadowComponent.FONT_HEIGHT + 2;
            int n2 = 16 + (flag ? n * 2 + 2 : n);
            int textWidth = drawStringWithShadowComponent.getStringWidth(this.line1);
            int n3 = flag ? drawStringWithShadowComponent.getStringWidth(this.line2) : 0;
            int n4 = Math.max(textWidth, n3) + 16 + 6;
            long l = this.computeLong();
            float f;
            if (l < 250L) {
                f = (float)l / 250.0F;
            } else if (l < 3250L) {
                f = 1.0F;
            } else {
                f = 1.0F - (float)(l - 250L - 3000L) / 250.0F;
            }

            f = Math.max(0.0F, Math.min(1.0F, f));
            double d = Math.tanh(2.5);
            float f2 = (float)(Math.tanh(f * 5.0 - 2.5) / d * 0.5 + 0.5);
            int n5 = (int)(-n4 * (1.0F - f2));
            byte b = 10;
            Gui.drawRect(n5, b, n5 + n4, b + n2, -300674014);
            Gui.drawRect(n5, b, n5 + 3, b + n2, this.accentColor);
            Gui.drawRect(n5 + 3, b, n5 + n4, b + 1, -13421739);
            Gui.drawRect(n5 + 3, b + n2 - 1, n5 + n4, b + n2, -13421739);
            Gui.drawRect(n5 + n4 - 1, b, n5 + n4, b + n2, -13421739);
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int n6 = n5 + 8 + 3;
            if (flag) {
                drawStringWithShadowComponent.drawStringWithShadow(this.line1, n6, b + 8, -1);
                drawStringWithShadowComponent.drawStringWithShadow(this.line2, n6, b + 8 + n + 2, -3355444);
            } else {
                drawStringWithShadowComponent.drawStringWithShadow(this.line1, n6, b + (n2 - drawStringWithShadowComponent.FONT_HEIGHT) / 2, -1);
            }

            long l2 = Math.max(0L, l - 250L);
            float f3 = 1.0F - Math.min(1.0F, (float)l2 / 3000.0F);
            int n7 = (int)(n4 * f3);
            if (n7 > 0) {
                Gui.drawRect(n5, b + n2 - 2, n5 + n7, b + n2, this.accentColor);
            }
        }
    }

    private static void process() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.getSoundHandler() != null) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.pop"), 1.0F));
            }
        } catch (Exception ignored) {
        }
    }
}
