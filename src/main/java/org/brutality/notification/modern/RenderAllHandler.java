package org.brutality.notification.modern;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.brutality.module.impl.visual.Notifications;
import org.brutality.notification.InfoHandler;
import org.brutality.notification.old.InfoType;
import org.lwjgl.opengl.GL11;

public class RenderAllHandler {
    private static final ResourceLocation resourceLocation = new ResourceLocation("brutality/notifications/warning.png");
    private static final ResourceLocation resourceLocation2 = new ResourceLocation("brutality/notifications/info.png");
    private static final ResourceLocation resourceLocation3 = new ResourceLocation("brutality/notifications/error.png");
    private static final ResourceLocation resourceLocation4 = new ResourceLocation("brutality/notifications/tick.png");
    private static final ResourceLocation resourceLocation5 = new ResourceLocation("brutality/notifications/cross.png");
    private static final List<RenderAllHandler> entries = new ArrayList<>();
    private static final int count = 5;
    private static final int count2 = 5;
    private static boolean flag = false;
    private InfoType infoType;
    private String title;
    private String message;
    private long timestamp;
    private int width;
    private int height2 = 42;
    private int yOffset = 0;
    private long timestamp2;
    private long timestamp3;
    private long timestamp4;

    public static void renderAll() {
        if (!flag) {
            process2();
        }

        process();
        Iterator it = entries.iterator();

        while (it.hasNext()) {
            try {
                RenderAllHandler renderAllHandler = (RenderAllHandler)it.next();
                renderAllHandler.render();
                if (!renderAllHandler.checkState()) {
                    it.remove();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void process3() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        this.width = InfoHandler.computeInt(this.title, this.message, Math.min(340, sr.getScaledWidth() - 16));
        this.height2 = InfoHandler.computeInt(this.message, this.width);
        long l = this.computeLong();
        float f = this.computeFloat(l);
        if (!(f <= 0.0F)) {
            float f2 = sr.getScaledWidth() - this.width - 8 + (1.0F - f) * 40.0F;
            float f3 = sr.getScaledHeight() - this.height2 - 12 - this.yOffset;
            InfoHandler.InfoMode infoMode;
            int n;
            switch (this.infoType) {
                case WARNING:
                    infoMode = InfoHandler.InfoMode.WARNING;
                    n = 16106818;
                    break;
                case ERROR:
                    infoMode = InfoHandler.InfoMode.ERROR;
                    n = 16735340;
                    break;
                case SUCCESS:
                    infoMode = InfoHandler.InfoMode.SUCCESS;
                    n = 6280847;
                    break;
                case FAILURE:
                    infoMode = InfoHandler.InfoMode.ERROR;
                    n = 16735340;
                    break;
                default:
                    infoMode = InfoHandler.InfoMode.INFO;
                    n = 16777215;
            }

            long l2 = Math.max(0L, l - this.timestamp2);
            float f4 = 1.0F - Math.min(1.0F, (float)l2 / (float)(this.timestamp3 - this.timestamp2));
            float f5 = Math.max(0.0F, (float)(this.timestamp3 - Math.max(l, this.timestamp2)) / 1000.0F);
            InfoHandler.handleFloat(f2, f3, this.width, this.title, this.message, infoMode, n, f, f4, f5);
        }
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    private void handleDouble(double d, double d2, double d3, double d4, int n) {
        try {
            if (d < d3) {
                double d5 = d;
                d = d3;
                d3 = d5;
            }

            if (d2 < d4) {
                double d6 = d2;
                d2 = d4;
                d4 = d6;
            }

            float f = (n >> 24 & 0xFF) / 255.0F;
            float f2 = (n >> 16 & 0xFF) / 255.0F;
            float f3 = (n >> 8 & 0xFF) / 255.0F;
            float f4 = (n & 0xFF) / 255.0F;
            GlStateManager.pushMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.color(f2, f3, f4, f);
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
            worldRenderer.func_181662_b(d, d4, 0.0).func_181675_d();
            worldRenderer.func_181662_b(d3, d4, 0.0).func_181675_d();
            worldRenderer.func_181662_b(d3, d2, 0.0).func_181675_d();
            worldRenderer.func_181662_b(d, d2, 0.0).func_181675_d();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        } catch (Exception ignored) {
        }
    }

    public int getHeight() {
        return this.height2;
    }

    public void process5() {
        this.timestamp = System.currentTimeMillis();
        if (!flag) {
            process2();
        }

        entries.add(this);
    }

    private void handleDouble(double d, double d2, double d3, double d4, int n, int n2) {
        try {
            float f = (n >> 24 & 0xFF) / 255.0F;
            float f2 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F;
            float f3 = (n >> 8 & 0xFF) / 255.0F;
            float f4 = ((0xFF | ~n) - ~n) / 255.0F;
            float f5 = ((0xFF | ~(n2 >> 24)) - ~(n2 >> 24)) / 255.0F;
            float f6 = (n2 >> 16 & 0xFF) / 255.0F;
            float f7 = ((0xFF | ~(n2 >> 8)) - ~(n2 >> 8)) / 255.0F;
            float f8 = (n2 & 0xFF) / 255.0F;
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            worldRenderer.func_181662_b(d3, d2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
            worldRenderer.func_181662_b(d, d2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
            worldRenderer.func_181662_b(d, d4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
            worldRenderer.func_181662_b(d3, d4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
            tessellator.draw();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        } catch (Exception ignored) {
        }
    }

    private long computeLong() {
        return System.currentTimeMillis() - this.timestamp;
    }

    private void process4() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        long l = this.computeLong();
        double d = this.computeFloat(l) * this.width;
        int n = (int)(sr.getScaledWidth() - d);
        int n2 = sr.getScaledHeight() - 45 - this.yOffset;

        Color color = switch (this.infoType) {
            case WARNING -> new Color(255, 193, 7, 200);
            case ERROR, FAILURE -> new Color(220, 53, 69, 200);
            case SUCCESS -> new Color(40, 167, 69, 200);
            default -> new Color(23, 162, 184, 200);
        };
        this.handleDouble(n, n2, n + this.width, n2 + this.height2, color.getRGB());
        Color color2 = new Color(color.getRed() - 30, color.getGreen() - 30, color.getBlue() - 30, 255);
        this.handleDouble(n, n2, n + this.width, n2 + 1, color2.getRGB());
        this.handleDouble(n, n2 + this.height2 - 1, n + this.width, n2 + this.height2, color2.getRGB());
        this.handleDouble(n, n2, n + 1, n2 + this.height2, color2.getRGB());
        this.handleDouble(n + this.width - 1, n2, n + this.width, n2 + this.height2, color2.getRGB());

        ResourceLocation location = switch (this.infoType) {
            case WARNING -> resourceLocation;
            case ERROR -> resourceLocation3;
            case SUCCESS -> resourceLocation4;
            case FAILURE -> resourceLocation5;
            default -> resourceLocation2;
        };
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        try {
            mc.getTextureManager().bindTexture(location);
            this.handleDouble(n + 6, n2 + 5, 0.0F, 0.0F, 15.0, 15.0, 15.0F, 15.0F);
        } catch (Exception e) {
            Color color3 = new Color(255, 255, 255);
            this.handleDouble(n + 6, n2 + 5, n + 21, n2 + 20, color3.getRGB());
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        FontRenderer fontRenderer = mc.fontRendererObj;
        if (fontRenderer != null) {
            int rgb = new Color(255, 255, 255).getRGB();
            int rgb2 = new Color(240, 240, 240).getRGB();
            fontRenderer.drawStringWithShadow(this.title, n + 26, n2 + 4, rgb);
            fontRenderer.drawStringWithShadow(this.message, n + 26, n2 + 15, rgb2);
        }
    }

    private float computeFloat(long l) {
        if (l <= 0L || l >= this.timestamp4) {
            return 0.0F;
        }
        if (l < this.timestamp2) {
            float f = (float)l / (float)this.timestamp2;
            return 1.0F - (1.0F - f) * (1.0F - f) * (1.0F - f);
        }
        if (l > this.timestamp3) {
            float f2 = (float)(l - this.timestamp3) / (float)(this.timestamp4 - this.timestamp3);
            return 1.0F - f2 * f2 * f2;
        }
        return 1.0F;
    }

    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            try {
                Notifications notifications = this.computeNotifications();
                String str = notifications != null ? notifications.computeString() : "Brutality";
                if (str.equals("Brutality")) {
                    this.process3();
                } else {
                    this.process4();
                }
            } catch (Exception e) {
                this.process3();
            }
        }
    }

    public boolean checkState() {
        return this.computeLong() <= this.timestamp4;
    }

    private static void process2() {
        if (!flag) {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc != null && mc.getTextureManager() != null) {
                    mc.getTextureManager().bindTexture(resourceLocation);
                    mc.getTextureManager().bindTexture(resourceLocation2);
                    mc.getTextureManager().bindTexture(resourceLocation3);
                    mc.getTextureManager().bindTexture(resourceLocation4);
                    mc.getTextureManager().bindTexture(resourceLocation5);
                    flag = true;
                }
            } catch (Exception ignored) {
            }
        }
    }

    private Notifications computeNotifications() {
        try {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static void process() {
        int n = 0;

        for (int i = entries.size() - 1; i >= 0; i--) {
            RenderAllHandler renderAllHandler = entries.get(i);
            renderAllHandler.yOffset = n;
            n += renderAllHandler.height2 + 5;
        }
    }

    private void handleDouble(double d, double d2, float f, float f2, double d3, double d4, float f3, float f4) {
        try {
            float f5 = 1.0F / f3;
            float f6 = 1.0F / f4;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            worldRenderer.func_181662_b(d, d2 + d4, 0.0).func_181673_a(f * f5, (f2 + (float)d4) * f6).func_181675_d();
            worldRenderer.func_181662_b(d + d3, d2 + d4, 0.0).func_181673_a((f + (float)d3) * f5, (f2 + (float)d4) * f6).func_181675_d();
            worldRenderer.func_181662_b(d + d3, d2, 0.0).func_181673_a((f + (float)d3) * f5, f2 * f6).func_181675_d();
            worldRenderer.func_181662_b(d, d2, 0.0).func_181673_a(f * f5, f2 * f6).func_181675_d();
            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        } catch (Exception ignored) {
        }
    }

    static {
        process2();
    }

    public RenderAllHandler(InfoType infoType, String title, String message, int n, long l) {
        this.infoType = infoType;
        this.title = title;
        this.message = message;
        this.width = 40 + (n - 1) * 10 + message.length() * 5;
        this.timestamp2 = 220L;
        this.timestamp3 = this.timestamp2 + 3000L;
        this.timestamp4 = this.timestamp3 + 220L;
    }
}
