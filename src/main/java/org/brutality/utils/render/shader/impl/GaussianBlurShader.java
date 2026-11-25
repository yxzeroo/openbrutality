package org.brutality.utils.render.shader.impl;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.brutality.utils.mcp.McpApi2;
import org.brutality.utils.player.PlayerHelper5;
import org.brutality.utils.render.BindTextureHelper2;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.render.shader.ShaderEffect;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class GaussianBlurShader implements McpApi2 {
    private static final ShaderEffect shaderEffect = new ShaderEffect("gaussianBlur");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void process() {
        BindTextureHelper2.process4();
    }

    public static void handleFramebuffer(Framebuffer framebuffer) {
        if (framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            framebuffer.createBindFramebuffer(mc.displayWidth, mc.displayHeight);
        }
    }

    private static void handleFloat(float f, float f2, float f3) {
        shaderEffect.handleString("textureIn", 0);
        shaderEffect.handleString("texelSize", 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
        shaderEffect.handleString("direction", f, f2);
        shaderEffect.handleString("radius", f3);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(256);

        for (int i = 0; i <= f3; i++) {
            floatBuffer.put(PlayerHelper5.computeFloat2(i, f3 / 2.0F));
        }

        floatBuffer.rewind();
        ARBShaderObjects.glUniform1ARB(shaderEffect.computeInt("weights"), floatBuffer);
    }

    public static void handleFloat(float f) {
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        handleFramebuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shaderEffect.init();
        handleFloat(1.0F, 0.0F, f);
        ResetColorHelper.bindTexture(mc.getFramebuffer().framebufferTexture);
        ShaderEffect.process();
        framebuffer.unbindFramebuffer();
        shaderEffect.unload();
        mc.getFramebuffer().bindFramebuffer(false);
        shaderEffect.init();
        handleFloat(0.0F, 1.0F, f);
        ResetColorHelper.bindTexture(framebuffer.framebufferTexture);
        ShaderEffect.process();
        shaderEffect.unload();
        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void handleFloat(float f, float f2) {
        BindTextureHelper2.handleInt2(1);
        framebuffer = ResetColorHelper.computeFramebuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shaderEffect.init();
        handleFloat(f2, 0.0F, f);
        ResetColorHelper.bindTexture(mc.getFramebuffer().framebufferTexture);
        ShaderEffect.process();
        framebuffer.unbindFramebuffer();
        shaderEffect.unload();
        mc.getFramebuffer().bindFramebuffer(false);
        shaderEffect.init();
        handleFloat(0.0F, f2, f);
        ResetColorHelper.bindTexture(framebuffer.framebufferTexture);
        ShaderEffect.process();
        shaderEffect.unload();
        BindTextureHelper2.process();
        ResetColorHelper.resetColor();
        GlStateManager.bindTexture(0);
    }
}
