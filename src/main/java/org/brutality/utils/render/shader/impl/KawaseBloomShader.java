package org.brutality.utils.render.shader.impl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.brutality.utils.mcp.McpApi2;
import org.brutality.utils.render.EnableDepthHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.render.shader.ShaderEffect;
import org.lwjgl.opengl.GL11;

public class KawaseBloomShader implements McpApi2 {
    public static ShaderEffect shaderEffect = new ShaderEffect("kawaseDownBloom");
    public static ShaderEffect shaderEffect2 = new ShaderEffect("kawaseUpBloom");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, true);
    private static int count;
    private static final List<Framebuffer> entries = new ArrayList<>();

    private static void handleFloat(float f) {
        for (Framebuffer framebuffer2 : entries) {
            framebuffer2.deleteFramebuffer();
        }

        entries.clear();
        entries.add(framebuffer = ResetColorHelper.computeFramebuffer(null, true));

        for (int i = 1; i <= f; i++) {
            Framebuffer framebuffer3 = new Framebuffer((int)(mc.displayWidth / Math.pow(2.0, i)), (int)(mc.displayHeight / Math.pow(2.0, i)), true);
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

    private static void handleInt2(int n, int n2, int n3) {
        if (n2 > count || entries.isEmpty() || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            count = Math.max(n2, count);
            handleFloat(count);
        }

        ResetColorHelper.handleFloat2(0.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        handleFramebuffer(entries.get(1), n, shaderEffect, n3);

        for (int i = 1; i < n2; i++) {
            handleFramebuffer(entries.get(i + 1), entries.get(i).framebufferTexture, shaderEffect, n3);
        }

        for (int j = n2; j > 1; j--) {
            handleFramebuffer(entries.get(j - 1), entries.get(j).framebufferTexture, shaderEffect2, n3);
        }

        Framebuffer framebuffer2 = entries.get(0);
        framebuffer2.framebufferClear();
        framebuffer2.bindFramebuffer(false);
        shaderEffect2.init();
        shaderEffect2.handleString("offset", (float)n3, (float)n3);
        shaderEffect2.handleString("inTexture", 0);
        shaderEffect2.handleString("check", 1);
        shaderEffect2.handleString("textureToCheck", 1);
        shaderEffect2.handleString("halfpixel", 1.0F / framebuffer2.framebufferWidth, 1.0F / framebuffer2.framebufferHeight);
        shaderEffect2.handleString("iResolution", (float)framebuffer2.framebufferWidth, (float)framebuffer2.framebufferHeight);
        GlStateManager.setActiveTexture(33985);
        ResetColorHelper.bindTexture(n);
        GlStateManager.setActiveTexture(33984);
        ResetColorHelper.bindTexture(entries.get(1).framebufferTexture);
        ShaderEffect.process();
        shaderEffect2.unload();
        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        mc.getFramebuffer().bindFramebuffer(true);
        ResetColorHelper.bindTexture(entries.get(0).framebufferTexture);
        ResetColorHelper.handleFloat2(0.0F);
        EnableDepthHelper.process5();
        ShaderEffect.process();
        GlStateManager.bindTexture(0);
        ResetColorHelper.process10();
    }

    private static void handleFramebuffer(Framebuffer framebuffer, int n, ShaderEffect shaderEffect, float f) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shaderEffect.init();
        ResetColorHelper.bindTexture(n);
        shaderEffect.handleString("offset", f, f);
        shaderEffect.handleString("inTexture", 0);
        shaderEffect.handleString("check", 0);
        shaderEffect.handleString("halfpixel", 1.0F / framebuffer.framebufferWidth, 1.0F / framebuffer.framebufferHeight);
        shaderEffect.handleString("iResolution", (float)framebuffer.framebufferWidth, (float)framebuffer.framebufferHeight);
        ShaderEffect.process();
        shaderEffect.unload();
    }
}
