package org.brutality.utils.render.shader.impl;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.brutality.utils.mcp.McpApi2;
import org.brutality.utils.player.PlayerHelper5;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.render.shader.ShaderEffect;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class DropShadowShader implements McpApi2 {
    public static ShaderEffect shaderEffect = new ShaderEffect("shadow");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    public static float factor;

    public static void handleInt(int factor2, int n, int n2) {
        if (factor2 != factor) {
            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(256);

            for (int i = 0; i <= factor2; i++) {
                floatBuffer.put(PlayerHelper5.computeFloat2(i, factor2));
            }

            floatBuffer.rewind();
            shaderEffect.handleString("inTexture", 0);
            shaderEffect.handleString("textureToCheck", 16);
            shaderEffect.handleString("radius", (float)factor2);
            ARBShaderObjects.glUniform1ARB(shaderEffect.computeInt("weights"), floatBuffer);
            factor = factor2;
        }

        shaderEffect.handleString("texelSize", 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
        shaderEffect.handleString("direction", (float)n, (float)n2);
    }

    public static void handleInt2(int n, int n2, int n3) {
        framebuffer = ResetColorHelper.computeFramebuffer(framebuffer, false);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shaderEffect.init();
        handleInt(n2, n3, 0);
        GlStateManager.bindTexture(n);
        ShaderEffect.process();
        shaderEffect.unload();
        framebuffer.unbindFramebuffer();
        mc.getFramebuffer().bindFramebuffer(true);
        shaderEffect.init();
        handleInt(n2, 0, n3);
        GlStateManager.setActiveTexture(34000);
        GlStateManager.bindTexture(n);
        GlStateManager.setActiveTexture(33984);
        GlStateManager.bindTexture(framebuffer.framebufferTexture);
        ShaderEffect.process();
        shaderEffect.unload();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.bindTexture(0);
    }
}
