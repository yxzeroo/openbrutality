package org.brutality.utils.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class HighShader {
    private final ShaderEffect shaderEffect = new ShaderEffect("hudBlur");
    private final ShaderEffect shaderEffect2 = new ShaderEffect("hudComposite");
    private Framebuffer framebuffer;
    private Framebuffer framebuffer2;
    private Framebuffer framebuffer3;

    public static int[] computeIntArray(int n, int n2, String str) {
        double d = "High".equals(str) ? 1.0 : ("Balanced".equals(str) ? 2.0 : 3.0);
        int n3 = "High".equals(str) ? 1920 : ("Balanced".equals(str) ? 1280 : 960);
        double d2 = Math.min(1.0 / d, (double)n3 / Math.max(1, n));
        return new int[]{Math.max(1, (int)Math.ceil(n * d2)), Math.max(1, (int)Math.ceil(n2 * d2))};
    }

    private Framebuffer computeFramebuffer(Framebuffer framebuffer, int n, int n2) {
        if (framebuffer != null && framebuffer.framebufferWidth == n && framebuffer.framebufferHeight == n2) {
            return framebuffer;
        }
        if (framebuffer != null) {
            framebuffer.deleteFramebuffer();
        }

        Framebuffer framebuffer2 = new Framebuffer(n, n2, false);
        framebuffer2.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        framebuffer2.setFramebufferFilter(9729);
        GlStateManager.bindTexture(framebuffer2.framebufferTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GlStateManager.bindTexture(0);
        return framebuffer2;
    }

    public static int computeInt(int n, String str) {
        return Math.max(1, Math.min(n, "High".equals(str) ? 3 : ("Balanced".equals(str) ? 2 : 1)));
    }

    public void handleSetShaderTypeEvent(SetShaderTypeEvent setShaderTypeEvent, String str, int n, float f, float f2) {
        Minecraft mc = Minecraft.getMinecraft();
        int[] ints = computeIntArray(mc.displayWidth, mc.displayHeight, str);
        this.framebuffer = this.computeFramebuffer(this.framebuffer, mc.displayWidth, mc.displayHeight);
        this.framebuffer2 = this.computeFramebuffer(this.framebuffer2, ints[0], ints[1]);
        this.framebuffer3 = this.computeFramebuffer(this.framebuffer3, ints[0], ints[1]);
        this.framebuffer.framebufferClear();
        this.framebuffer.bindFramebuffer(true);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        setShaderTypeEvent.process();
        boolean flag = setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.GLOW;
        int n2 = flag ? this.framebuffer.framebufferTexture : mc.getFramebuffer().framebufferTexture;

        for (int i = 0; i < computeInt(n, str); i++) {
            this.handleFramebuffer(this.framebuffer2, n2, f / this.framebuffer2.framebufferWidth, 0.0F, flag && i == 0);
            this.handleFramebuffer(this.framebuffer3, this.framebuffer2.framebufferTexture, 0.0F, f / this.framebuffer3.framebufferHeight, false);
            n2 = this.framebuffer3.framebufferTexture;
        }

        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.setActiveTexture(33984);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(flag ? GL11.GL_ONE : GL11.GL_SRC_ALPHA, flag ? GL11.GL_ONE : GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.shaderEffect2.init();
        this.shaderEffect2.handleString("inTexture", 0);
        this.shaderEffect2.handleString("maskTexture", 1);
        this.shaderEffect2.handleString("glow", flag ? 1 : 0);
        this.shaderEffect2.handleString("strength", f2);
        GlStateManager.setActiveTexture(33985);
        GlStateManager.bindTexture(this.framebuffer.framebufferTexture);
        GlStateManager.setActiveTexture(33984);
        GlStateManager.bindTexture(this.framebuffer3.framebufferTexture);
        ShaderEffect.process();
        this.shaderEffect2.unload();
        GlStateManager.bindTexture(0);
    }

    private void handleFramebuffer(Framebuffer framebuffer, int n, float f, float f2, boolean flag) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        GlStateManager.setActiveTexture(33984);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.shaderEffect.init();
        this.shaderEffect.handleString("inTexture", 0);
        this.shaderEffect.handleString("direction", f, f2);
        this.shaderEffect.handleString("premultiply", flag ? 1 : 0);
        GlStateManager.bindTexture(n);
        ShaderEffect.process();
        this.shaderEffect.unload();
    }

    public void process() {
        if (this.framebuffer != null) {
            this.framebuffer.deleteFramebuffer();
        }

        if (this.framebuffer2 != null) {
            this.framebuffer2.deleteFramebuffer();
        }

        if (this.framebuffer3 != null) {
            this.framebuffer3.deleteFramebuffer();
        }

        this.framebuffer = this.framebuffer2 = this.framebuffer3 = null;
    }
}
