package org.brutality.gui.chat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.utils.render.BindTextureHelper;
import org.brutality.utils.render.LocationHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.shader.impl.DualKawaseBlurShader;
import org.lwjgl.opengl.GL11;

public final class AvatarComponent {
    private static Framebuffer framebuffer;
    private static final Map<String, ResourceLocation> lookupMap = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> lookupMap2 = new ConcurrentHashMap<>();

    public static ResourceLocation avatar(String str) {
        String str2 = str.toLowerCase(Locale.ROOT);
        ResourceLocation location = lookupMap.get(str2);
        if (location == null && lookupMap2.putIfAbsent(str2, Boolean.TRUE) == null) {
            CompletableFuture.<BufferedImage>supplyAsync(() -> {
                    try {
                        HttpURLConnection conn = (HttpURLConnection)new URL("https://minotar.net/avatar/" + str + "/64").openConnection();
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                        conn.setConnectTimeout(6000);
                        conn.setReadTimeout(8000);

                        BufferedImage image;
                        try (InputStream inputStream = conn.getInputStream()) {
                            image = ImageIO.read(inputStream);
                        }

                        return image;
                    } catch (Exception e) {
                        lookupMap2.remove(str2);
                        return null;
                    }
                })
                .thenAccept(
                    image2 -> {
                        if (image2 != null) {
                            Minecraft.getMinecraft()
                                .addScheduledTask(
                                    () -> lookupMap.put(
                                        str2,
                                        Minecraft.getMinecraft()
                                            .getTextureManager()
                                            .getDynamicTextureLocation("brutality_avatar_" + str2, new DynamicTexture(image2))
                                    )
                                );
                        }
                    }
                );
            return null;
        }
        return location;
    }

    public static void handleInt(
        int n, int n2, int n3, int n4, String str, CustomFontRenderer drawStringWithShadowComponent, float f, boolean flag, boolean flag2
    ) {
        Color color = OceanComponent.computeColor();
        Color color2;
        Color color3;
        if (flag2) {
            color2 = MainMenuIconRenderer.computeColor(color, 175 + (int)(60.0F * f));
            color3 = new Color(255, 255, 255, 50 + (int)(40.0F * f));
        } else if (flag) {
            color2 = MainMenuIconRenderer.computeColor(color, 60 + (int)(40.0F * f));
            color3 = MainMenuIconRenderer.computeColor(color, 150 + (int)(70.0F * f));
        } else {
            color2 = new Color(30, 31, 38, 150 + (int)(50.0F * f));
            color3 = new Color(255, 255, 255, 22 + (int)(30.0F * f));
        }

        OutlineThicknessHelper.handleFloat(n, n2, n3, n4, 3.0F, color2);
        OutlineThicknessHelper.handleFloat(n, n2, n3, n4, 3.0F, 0.5F, new Color(0, 0, 0, 0), color3);
        int n5 = flag2 ? 255 : 205 + (int)(50.0F * f);
        int n6 = flag2 && (0.2126F * color.getRed() + 0.7152F * color.getGreen() + 0.0722F * color.getBlue()) / 255.0F > 0.6F
            ? -15395300
            : (n5 << 8) + ((0xFF000000 | n5 << 16) & ~(n5 << 8)) | n5;
        GlStateManager.enableTexture2D();
        drawStringWithShadowComponent.drawString(str, n + (n3 - drawStringWithShadowComponent.getStringWidth(str)) / 2, n2 + (n4 - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1, n6);
    }

    public static void handleFloat(float f, int n, int n2) {
        GlStateManager.pushMatrix();
        float f2 = 0.94F + 0.06F * f;
        GlStateManager.translate((float)n, (float)n2, 0.0F);
        GlStateManager.scale(f2, f2, 1.0F);
        GlStateManager.translate((float)(~n + 1), (float)(-n2), 0.0F);
    }

    public static float computeFloat(long l) {
        return computeFloat(Math.min(1.0F, (float)(System.nanoTime() - l) / 2.2E8F));
    }

    public static void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, String str, int n, int n2, int n3) {
        drawStringWithShadowComponent.drawString(str.toUpperCase(Locale.ROOT), n, n2, -7695194);
        int n4 = n + drawStringWithShadowComponent.getStringWidth(str.toUpperCase(Locale.ROOT)) + 8;
        Gui.drawRect(n4, n2 + drawStringWithShadowComponent.FONT_HEIGHT / 2, n + n3, n2 + drawStringWithShadowComponent.FONT_HEIGHT / 2 + 1, 536870911);
    }

    private static ResourceLocation computeResourceLocation(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.thePlayer.getName().equalsIgnoreCase(str)) {
            return mc.thePlayer.getLocationSkin();
        }
        if (mc.getNetHandler() == null) {
            return null;
        }
        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(str);
        return playerInfo == null ? null : playerInfo.getLocationSkin();
    }

    public static float computeFloat(float f) {
        return f >= 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0F * f);
    }

    public static void handleString(String str, int n, int n2, int n3) {
        ResourceLocation location = computeResourceLocation(str);
        if (location != null) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(location);
            Gui.drawScaledCustomSizeModalRect(n, n2, 8.0F, 8.0F, 8, 8, n3, n3, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(n, n2, 40.0F, 8.0F, 8, 8, n3, n3, 64.0F, 64.0F);
        } else {
            ResourceLocation location2 = avatar(str);
            if (location2 == null) {
                Gui.drawRect(n, n2, n + n3, n2 + n3, -14671060);
            } else {
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(location2);
                Gui.drawScaledCustomSizeModalRect(n, n2, 0.0F, 0.0F, 64, 64, n3, n3, 64.0F, 64.0F);
                Gui.drawRect(n, n2, n + n3, n2 + 1, 587202559);
            }
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, float f) {
        Color color = OceanComponent.computeColor();
        handleFloat(n, n2, n3, n4, 8.0F);
        MainMenuIconRenderer.handleInt(n, n2, n3, n4, 8.0F, 1.18F);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5) {
        if (OpenGlHelper.isFramebufferEnabled() && OpenGlHelper.shadersSupported) {
            Minecraft mc = Minecraft.getMinecraft();

            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.colorMask(true, true, true, true);
                framebuffer = BindTextureHelper.computeFramebuffer(framebuffer);
                framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
                framebuffer.framebufferClear();
                framebuffer.bindFramebuffer(false);
                LocationHelper.handleFloat2(f, f2, f3, f4, f5, Color.WHITE);
                framebuffer.unbindFramebuffer();
                mc.getFramebuffer().bindFramebuffer(true);
                DualKawaseBlurShader.handleInt(framebuffer.framebufferTexture, 2, 2);
                BindTextureHelper.resetColor();
            } catch (Exception ignored) {
            }
        }
    }

    private AvatarComponent() {
    }
}
