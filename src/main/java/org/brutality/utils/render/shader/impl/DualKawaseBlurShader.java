package org.brutality.utils.render.shader.impl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.brutality.utils.render.BindTextureHelper;
import org.brutality.utils.render.DisableDepthHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;

public class DualKawaseBlurShader {
    private static final Minecraft g = Minecraft.getMinecraft();
    public static ShaderProgram shaderProgram = new ShaderProgram("kawaseDown");
    public static ShaderProgram shaderProgram2 = new ShaderProgram("kawaseUp");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static int count;
    private static final List<Framebuffer> entries = new ArrayList<>();

    public static void handleFloat2(float f) {
        shaderProgram.handleString("offset", f, f);
        shaderProgram2.handleString("offset", f, f);
    }

    private static void handleFramebuffer(Framebuffer framebuffer, int n, ShaderProgram shaderProgram, float f) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shaderProgram.init();
        BindTextureHelper.bindTexture(n);
        shaderProgram.handleString("offset", f, f);
        shaderProgram.handleString("inTexture", 0);
        shaderProgram.handleString("check", 0);
        shaderProgram.handleString("halfpixel", 1.0F / framebuffer.framebufferWidth, 1.0F / framebuffer.framebufferHeight);
        shaderProgram.handleString("iResolution", (float)framebuffer.framebufferWidth, (float)framebuffer.framebufferHeight);
        ShaderProgram.process();
        shaderProgram.unload();
    }

    private static void handleInt2(int n, int n2, int n3) {
        boolean flag = framebuffer.framebufferWidth != g.displayWidth || framebuffer.framebufferHeight != g.displayHeight;
        if (flag || n2 > count || entries.isEmpty()) {
            count = Math.max(n2, count);
            handleFloat(count);
        }

        handleFramebuffer(entries.get(1), g.getFramebuffer().framebufferTexture, shaderProgram, n3);

        for (int i = 1; i < n2; i++) {
            handleFramebuffer(entries.get(i + 1), entries.get(i).framebufferTexture, shaderProgram, n3);
        }

        for (int j = n2; j > 1; j--) {
            handleFramebuffer(entries.get(j - 1), entries.get(j).framebufferTexture, shaderProgram2, n3);
        }

        Framebuffer framebuffer2 = entries.get(0);
        framebuffer2.framebufferClear();
        framebuffer2.bindFramebuffer(false);
        shaderProgram2.init();
        shaderProgram2.handleString("offset", (float)n3, (float)n3);
        shaderProgram2.handleString("inTexture", 0);
        shaderProgram2.handleString("check", 1);
        shaderProgram2.handleString("textureToCheck", 16);
        shaderProgram2.handleString("halfpixel", 1.0F / framebuffer2.framebufferWidth, 1.0F / framebuffer2.framebufferHeight);
        shaderProgram2.handleString("iResolution", (float)framebuffer2.framebufferWidth, (float)framebuffer2.framebufferHeight);
        GlStateManager.setActiveTexture(34000);
        BindTextureHelper.bindTexture(n);
        GlStateManager.setActiveTexture(33984);
        BindTextureHelper.bindTexture(entries.get(1).framebufferTexture);
        ShaderProgram.process();
        shaderProgram2.unload();
        g.getFramebuffer().bindFramebuffer(true);
        BindTextureHelper.bindTexture(entries.get(0).framebufferTexture);
        BindTextureHelper.handleFloat(0.0F);
        DisableDepthHelper.process6();
        ShaderProgram.process();
        GlStateManager.bindTexture(0);
    }

    private static void handleFloat(float f) {
        for (Framebuffer framebuffer2 : entries) {
            framebuffer2.deleteFramebuffer();
        }

        entries.clear();
        entries.add(framebuffer = BindTextureHelper.computeFramebuffer(null));

        for (int i = 1; i <= f; i++) {
            Framebuffer framebuffer3 = new Framebuffer(
                Math.max(1, (int)(g.displayWidth / Math.pow(2.0, i))), Math.max(1, (int)(g.displayHeight / Math.pow(2.0, i))), false
            );
            framebuffer3.setFramebufferFilter(9729);
            GlStateManager.bindTexture(framebuffer3.framebufferTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 33648);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 33648);
            GlStateManager.bindTexture(0);
            entries.add(framebuffer3);
        }
    }

    public static void handleInt(int n, int n2, int n3) {
        if (OpenGlHelper.isFramebufferEnabled() && OpenGlHelper.shadersSupported) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.setActiveTexture(33984);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.depthMask(false);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                handleInt2(n, n2, n3);
            }
        }
    }
}
