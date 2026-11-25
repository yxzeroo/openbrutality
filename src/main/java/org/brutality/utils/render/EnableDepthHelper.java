package org.brutality.utils.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class EnableDepthHelper {
    private static final FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(4);
    private static final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer floatBuffer3 = GLAllocation.createDirectFloatBuffer(16);
    private static final float[] floatValueArray = new float[3];
    private static int[] intValueArray = new int[32];

    public static void process3() {
        for (int n : intValueArray) {
            GL11.glDisable(n);
        }
    }

    public static void process() {
        GlStateManager.popMatrix();
    }

    public static void enableDepth() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public static void handleInt(int n, Runnable task) {
        GL11.glBegin(n);
        task.run();
        GL11.glEnd();
    }

    public static void handleRunnable(Runnable task) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        task.run();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.disableBlend();
    }

    public static void process4() {
        handleFlag(true);
    }

    public static void handleFloat(float f, float f2, float f3) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(f, f2, 0.0F);
        GlStateManager.rotate(f3, 0.0F, 0.0F, -1.0F);
        GlStateManager.translate(-f, -f2, 0.0F);
    }

    public static void process2() {
        GlStateManager.enableTexture2D();
        process6();
    }

    public static void disableDepth() {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
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

    public static void handleIntArray(int... intValueArray2) {
        for (int n : intValueArray2) {
            GL11.glEnable(n);
        }

        intValueArray = intValueArray2;
    }

    public static void handleFlag(boolean flag) {
        if (flag) {
            process5();
        }

        GlStateManager.disableTexture2D();
    }

    public static void process6() {
        GlStateManager.disableBlend();
    }

    public static void process5() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
}
