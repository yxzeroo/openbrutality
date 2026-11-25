package org.brutality.utils.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public final class EnableTexture2DHelper {
    private static final FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(4);
    private static final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer floatBuffer3 = GLAllocation.createDirectFloatBuffer(16);
    private static final float[] floatValueArray = new float[3];

    public static void process() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void handleScaledResolution(ScaledResolution sr, int n, int n2, int n3, int n4) {
        int scaleFactor = sr.getScaleFactor();
        GL11.glScissor(n * scaleFactor, (sr.getScaledHeight() - (n2 + n4)) * scaleFactor, n3 * scaleFactor, n4 * scaleFactor);
    }

    public static void enableTexture2D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static float[] computeFloatArray(float f, float f2, float f3, int n) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, floatBuffer2);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, floatBuffer3);
        GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
        if (GLU.gluProject(f, f2, f3, floatBuffer2, floatBuffer3, intBuffer, floatBuffer)) {
            floatValueArray[0] = floatBuffer.get(0) / n;
            floatValueArray[1] = (Display.getHeight() - floatBuffer.get(1)) / n;
            floatValueArray[2] = floatBuffer.get(2);
            return floatValueArray;
        }
        return null;
    }

    public static void disableTexture2D() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void handleInt(int n) {
        GL11.glColor4ub(
            (byte)((0xFF | ~(n >> 16)) - ~(n >> 16)), (byte)((0xFF | ~(n >> 8)) - ~(n >> 8)), (byte)(n & 0xFF), (byte)(n >> 24 & 0xFF)
        );
    }

    public static void disableDepth() {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void handleInt(int n, int n2) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        handleInt(n);
        GL11.glBegin(n2);
    }

    private EnableTexture2DHelper() {
    }

    public static void process2() {
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void enableDepth() {
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
