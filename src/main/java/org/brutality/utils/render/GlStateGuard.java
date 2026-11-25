package org.brutality.utils.render;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class GlStateGuard implements AutoCloseable {
    private static final ThreadLocal<ArrayDeque<GlStateGuard.Entry>> threadLocal = ThreadLocal.withInitial(ArrayDeque::new);
    private final int count = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
    private final int count2 = GL11.glGetInteger(34016);
    private final int count3 = OpenGlHelper.framebufferSupported ? GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING) : 0;
    private final int count4 = OpenGlHelper.shadersSupported ? GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) : 0;
    private final float factor = OpenGlHelper.lastBrightnessX;
    private final float factor2 = OpenGlHelper.lastBrightnessY;
    private final int count5 = GL11.glGetInteger(2992);
    private final int count6 = GL11.glGetInteger(2980);
    private final int count7 = GL11.glGetInteger(2979);
    private final int count8;
    private final GlStateGuard.Entry entry;
    private boolean closed;

    public GlStateGuard() {
        ArrayDeque arrayDeque = threadLocal.get();
        this.entry = arrayDeque.isEmpty() ? new GlStateGuard.Entry() : (GlStateGuard.Entry)arrayDeque.pop();
        this.entry.floatBuffer.clear();
        this.entry.floatBuffer2.clear();
        this.entry.floatBuffer3.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, this.entry.floatBuffer);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, this.entry.floatBuffer2);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        this.count8 = GL11.glGetInteger(2981);
        GL11.glGetFloat(2984, this.entry.floatBuffer3);
        GlStateManager.invalidateStateCache();
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glMatrixMode(GL11.GL_TEXTURE);

            while (GL11.glGetInteger(2981) > this.count8) {
                GL11.glPopMatrix();
            }

            GL11.glLoadMatrix(this.entry.floatBuffer3);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);

            while (GL11.glGetInteger(2979) > this.count7) {
                GL11.glPopMatrix();
            }

            GL11.glLoadMatrix(this.entry.floatBuffer2);
            GL11.glMatrixMode(GL11.GL_PROJECTION);

            while (GL11.glGetInteger(2980) > this.count6) {
                GL11.glPopMatrix();
            }

            GL11.glLoadMatrix(this.entry.floatBuffer);
            if (OpenGlHelper.shadersSupported) {
                OpenGlHelper.glUseProgram(this.count4);
            }

            if (OpenGlHelper.framebufferSupported) {
                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.count3);
            }

            while (GL11.glGetInteger(2992) > this.count5) {
                GL11.glPopAttrib();
            }

            OpenGlHelper.setActiveTexture(this.count2);
            GL11.glMatrixMode(this.count);
            OpenGlHelper.lastBrightnessX = this.factor;
            OpenGlHelper.lastBrightnessY = this.factor2;
            GlStateManager.invalidateStateCache();
            threadLocal.get().push(this.entry);
        }
    }

    public static final class Entry {
        public final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        public final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
        public final FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(16);
    }
}
