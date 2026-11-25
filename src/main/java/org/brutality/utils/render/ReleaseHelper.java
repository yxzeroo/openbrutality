package org.brutality.utils.render;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class ReleaseHelper {
    private int count;
    private int width;
    private int height;
    private boolean flag;
    private long timestamp;
    private static final long timestamp2 = 250000000L;

    public void release() {
        if (this.count != 0) {
            TextureUtil.deleteTexture(this.count);
        }

        this.count = this.width = this.height = 0;
        this.invalidate();
    }

    private void handleInt(int n, int n2, float f) {
        GlStateManager.viewport(0, 0, n, n2);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableCull();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
        GL11.glTexEnvi(8960, 8704, 8448);
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GL11.glOrtho(0.0, n, n2, 0.0, -1.0, 1.0);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(0.0F, n2);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(n, n2);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(n, 0.0F);
        GL11.glEnd();
    }

    public static float computeFloat(double d, long l) {
        return !(d <= 0.0) && l > 0L && l <= timestamp2 ? (float)Math.pow(Math.min(0.95, d), l / 1.6666666666666666E7) : 0.0F;
    }

    public void handleInt(int width, int height, double d, long timestamp, boolean flag) {
        if (width > 0 && height > 0) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.enableTexture2D();
                if (OpenGlHelper.shadersSupported) {
                    OpenGlHelper.glUseProgram(0);
                }

                if (this.count == 0 || this.width != width || this.height != height) {
                    this.release();
                    this.width = width;
                    this.height = height;
                    this.count = TextureUtil.glGenTextures();
                    GlStateManager.bindTexture(this.count);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
                }

                GlStateManager.bindTexture(this.count);
                float f = this.flag && !flag ? computeFloat(d, timestamp - this.timestamp) : 0.0F;
                if (f > 0.0F) {
                    this.handleInt(width, height, f);
                }

                GL11.glReadBuffer(GL11.glGetInteger(3073));
                GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, width, height);
                this.flag = true;
                this.timestamp = timestamp;
            }
        } else {
            this.invalidate();
        }
    }

    public void invalidate() {
        this.flag = false;
        this.timestamp = 0L;
    }
}
