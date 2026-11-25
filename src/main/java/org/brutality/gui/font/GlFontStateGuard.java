package org.brutality.gui.font;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public final class GlFontStateGuard implements AutoCloseable {
    private static final ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
    private final boolean flag;
    private int count;
    private int count2;
    private int count3;

    public GlFontStateGuard() {
        int n = threadLocal.get();
        this.flag = n == 0;
        threadLocal.set(n + 1);
        if (this.flag) {
            this.count = GL11.glGetInteger(34016);
            int n2 = 24577;
            if (GL11.glIsEnabled(GL11.GL_COLOR_MATERIAL)) {
                n2 |= 64;
            }

            GL11.glPushAttrib(n2);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            this.count2 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            this.count3 = GL11.glGetTexEnvi(8960, 8704);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.invalidateFontStateCache(OpenGlHelper.defaultTexUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glTexEnvi(8960, 8704, 8448);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_COLOR_MATERIAL);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    @Override
    public void close() {
        threadLocal.set(threadLocal.get() - 1);
        if (this.flag) {
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.count2);
            GL11.glTexEnvi(8960, 8704, this.count3);
            GL11.glPopAttrib();
            OpenGlHelper.setActiveTexture(this.count);
            GlStateManager.invalidateFontStateCache(this.count);
        }
    }
}
