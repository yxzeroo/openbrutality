package org.brutality.notification;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public final class InfoHandler {
    public static final String text = "brutality/notifications/";
    private static final int count = 8;
    private static final int count2 = 16;
    private static final int count3 = 33;
    private static final ResourceLocation resourceLocation = computeResourceLocation("info");
    private static final ResourceLocation resourceLocation2 = computeResourceLocation("warning");

    public static ResourceLocation computeResourceLocation(String str) {
        return new ResourceLocation("brutality/notifications/" + str + ".png");
    }

    public static int computeInt(FontRenderer fontRenderer, String str, int n) {
        return computeInt(str, n);
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent2() {
        return FontComponent.computeDrawStringWithShadowComponent2(17, true);
    }

    private static List<String> computeList(CustomFontRenderer drawStringWithShadowComponent, String str, int n) {
        ArrayList list = new ArrayList();
        String str2 = "";

        for (String str3 : (str == null ? "" : str).split(" ")) {
            String str4 = str2.isEmpty() ? str3 : str2 + " " + str3;
            if (!str2.isEmpty() && drawStringWithShadowComponent.getStringWidth(str4) > n) {
                list.add(str2);
                str2 = str3;
            } else {
                str2 = str4;
            }
        }

        if (!str2.isEmpty()) {
            list.add(str2);
        }

        if (list.isEmpty()) {
            list.add("");
        }

        return list;
    }

    public static void handleFontRenderer(
        FontRenderer fontRenderer, float f, float f2, int n, String str, String str2, ResourceLocation location, int n2, float f3, float f4
    ) {
        handleFloat(f, f2, n, str, str2, computeInfoMode(location), n2, f3, f4, -1.0F);
    }

    private static void handleFloat2(float f, float f2, float f3) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(f, f2);

        for (int i = 0; i <= 40; i++) {
            double d = i * Math.PI * 2.0 / 40.0;
            GL11.glVertex2f(f + (float)Math.cos(d) * f3, f2 + (float)Math.sin(d) * f3);
        }

        GL11.glEnd();
    }

    private static void handleFloat(float f, float f2, float f3, float f4, int n) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        handleInt(n);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(f, f4);
        GL11.glVertex2f(f3, f4);
        GL11.glVertex2f(f3, f2);
        GL11.glVertex2f(f, f2);
        GL11.glEnd();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static InfoHandler.InfoMode computeInfoMode(ResourceLocation location) {
        String str = location == null ? "" : location.getResourcePath();
        if (str.contains("warning")) {
            return InfoHandler.InfoMode.WARNING;
        }
        if (str.contains("tick")) {
            return InfoHandler.InfoMode.SUCCESS;
        }
        return !str.contains("error") && !str.contains("cross") ? InfoHandler.InfoMode.INFO : InfoHandler.InfoMode.ERROR;
    }

    private InfoHandler() {
    }

    private static int computeInt(int n, float f) {
        return Math.max(0, Math.min(255, (int)(255.0F * f))) << 24 | n & 16777215;
    }

    public static void handleFloat(
        float f, float f2, int n, String str, String str2, InfoHandler.InfoMode infoMode, int n2, float f3, float f4, float f5
    ) {
        if (!(f3 < 0.02F)) {
            int n3 = computeInt(str2, n);
            CustomFontRenderer drawStringWithShadowComponent = computeDrawStringWithShadowComponent2();
            CustomFontRenderer drawStringWithShadowComponent2 = computeDrawStringWithShadowComponent();

            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                Color color = new Color(8, 9, 13, (int)(178.0F * f3));
                if (OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect != null) {
                    OutlineThicknessHelper.handleFloat(f, f2, n, n3, 3.0F, color);
                } else {
                    handleFloat(f, f2, f + n, f2 + n3, color.getRGB());
                }

                float f6 = f2 + n3 / 2.0F - 1.0F;
                handleInfoMode(infoMode, f + 8.0F, f6 - 8.0F, 16.0F, n2, f3);
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                drawStringWithShadowComponent.computeInt3(str, f + 33.0F, f2 + 6.0F, computeInt(16777215, f3));
                List list = computeList(drawStringWithShadowComponent2, str2, n - 33 - 8);
                int n4 = (int)f2 + 19;

                for (int i = 0; i < Math.min(2, list.size()); i++) {
                    String str3 = (String)list.get(i);
                    if (i == 1 && list.size() > 2) {
                        while (str3.length() > 3 && drawStringWithShadowComponent2.getStringWidth(str3 + "...") > n - 33 - 8) {
                            str3 = str3.substring(0, str3.length() - 1);
                        }

                        str3 = str3 + "...";
                    }

                    drawStringWithShadowComponent2.computeInt3(str3, f + 33.0F, n4, computeInt(13817566, f3));
                    if (i == Math.min(2, list.size()) - 1 && f5 >= 0.0F) {
                        String str4 = String.format(Locale.ROOT, " (%.1fs)", Math.max(0.0F, f5));
                        drawStringWithShadowComponent2.computeInt3(str4, f + 33.0F + drawStringWithShadowComponent2.getStringWidth(str3), n4, computeInt(9212574, f3));
                    }

                    n4 += drawStringWithShadowComponent2.FONT_HEIGHT + 1;
                }

                float f7 = (n - 8) * Math.max(0.0F, Math.min(1.0F, f4));
                if (f7 > 0.5F) {
                    handleFloat(f + 4.0F, f2 + n3 - 2.0F, f + 4.0F + f7, f2 + n3 - 1.0F, computeInt(n2, f3));
                }
            }
        }
    }

    private static void handleFloat(float f, float f2, float f3) {
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i < 40; i++) {
            double d = i * Math.PI * 2.0 / 40.0;
            GL11.glVertex2f(f + (float)Math.cos(d) * f3, f2 + (float)Math.sin(d) * f3);
        }

        GL11.glEnd();
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent() {
        return FontComponent.computeDrawStringWithShadowComponent2(15, false);
    }

    private static void handleInt(int n) {
        GlStateManager.color(((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, (n >>> 24) / 255.0F);
    }

    private static void handleInfoMode(InfoHandler.InfoMode infoMode, float f, float f2, float f3, int n, float f4) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        float f5 = f + f3 / 2.0F;
        float f6 = f2 + f3 / 2.0F;
        float f7 = f3 / 2.0F - 0.5F;
        int n2 = computeInt(infoMode == InfoHandler.InfoMode.INFO ? 16777215 : n, f4);
        if (infoMode != InfoHandler.InfoMode.INFO && infoMode != InfoHandler.InfoMode.WARNING) {
            switch (infoMode) {
                case WARNING:
                    handleFloat(1.6F);
                    handleInt(n2);
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    GL11.glVertex2f(f5, f2 + 1.0F);
                    GL11.glVertex2f(f + f3 - 0.5F, f2 + f3 - 1.5F);
                    GL11.glVertex2f(f + 0.5F, f2 + f3 - 1.5F);
                    GL11.glEnd();
                    handleFloat(f5 - 0.75F, f2 + 5.5F, f5 + 0.75F, f2 + 10.5F, n2);
                    handleFloat(f5 - 0.75F, f2 + 12.0F, f5 + 0.75F, f2 + 13.5F, n2);
                    break;
                case INFO:
                    handleInt(n2);
                    handleFloat2(f5, f6, f7);
                    int n3 = computeInt(1053208, f4);
                    handleFloat(f5 - 0.9F, f6 - 1.0F, f5 + 0.9F, f6 + 4.5F, n3);
                    handleFloat(f5 - 0.9F, f6 - 4.5F, f5 + 0.9F, f6 - 2.6F, n3);
                    break;
                case SUCCESS:
                    handleFloat(1.5F);
                    handleInt(n2);
                    handleFloat(f5, f6, f7);
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    GL11.glVertex2f(f5 - 3.5F, f6 + 0.2F);
                    GL11.glVertex2f(f5 - 1.0F, f6 + 2.8F);
                    GL11.glVertex2f(f5 + 3.8F, f6 - 2.6F);
                    GL11.glEnd();
                    break;
                default:
                    handleFloat(1.5F);
                    handleInt(n2);
                    handleFloat(f5, f6, f7);
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex2f(f5 - 2.8F, f6 - 2.8F);
                    GL11.glVertex2f(f5 + 2.8F, f6 + 2.8F);
                    GL11.glVertex2f(f5 + 2.8F, f6 - 2.8F);
                    GL11.glVertex2f(f5 - 2.8F, f6 + 2.8F);
                    GL11.glEnd();
            }

            GL11.glLineWidth(1.0F);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
        } else {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            handleResourceLocation(infoMode == InfoHandler.InfoMode.INFO ? resourceLocation : resourceLocation2, f, f2, f3, n2);
        }
    }

    private static void handleResourceLocation(ResourceLocation location, float f, float f2, float f3, int n) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        handleInt(n);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(f, f2);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(f, f2 + f3);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(f + f3, f2 + f3);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(f + f3, f2);
        GL11.glEnd();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static int computeInt(String str, String str2, int n) {
        int n2 = Math.max(
            computeDrawStringWithShadowComponent2().getStringWidth(str), computeDrawStringWithShadowComponent().getStringWidth(str2 + "  (0.0s)")
        );
        return Math.max(170, Math.min(n, 33 + n2 + 8 + 2));
    }

    public static int computeInt(String str, int n) {
        int n2 = Math.min(2, Math.max(1, computeList(computeDrawStringWithShadowComponent(), str, n - 33 - 8).size()));
        return 22 + n2 * (computeDrawStringWithShadowComponent().FONT_HEIGHT + 1) + 4;
    }

    private static void handleFloat(float f) {
        int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        GL11.glLineWidth(Math.max(1.0F, f * scaleFactor));
    }

    public static enum InfoMode {
        INFO,
        WARNING,
        SUCCESS,
        ERROR;

        private static InfoHandler.InfoMode[] computeInfoModeArray() {
            return new InfoHandler.InfoMode[]{INFO, WARNING, SUCCESS, ERROR};
        }
    }
}
