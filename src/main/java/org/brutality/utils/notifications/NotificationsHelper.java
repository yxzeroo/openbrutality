package org.brutality.utils.notifications;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class NotificationsHelper {
    private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
    private static final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
    private static final FloatBuffer floatBuffer3 = BufferUtils.createFloatBuffer(3);

    public static float[] computeFloatArray(float f, float f2, float f3) {
        try {
            floatBuffer.clear();
            floatBuffer2.clear();
            intBuffer.clear();
            floatBuffer3.clear();
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, floatBuffer);
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, floatBuffer2);
            GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
            boolean flag = GLU.gluProject(f, f2, f3, floatBuffer, floatBuffer2, intBuffer, floatBuffer3);
            if (flag) {
                Minecraft mc = Minecraft.getMinecraft();
                ScaledResolution sr = new ScaledResolution(mc);
                return new float[]{
                    floatBuffer3.get(0) / sr.getScaleFactor(), (intBuffer.get(3) - floatBuffer3.get(1)) / sr.getScaleFactor(), floatBuffer3.get(2)
                };
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public static float[] computeFloatArray2(float f, float f2, float f3) {
        try {
            floatBuffer.clear();
            floatBuffer2.clear();
            intBuffer.clear();
            floatBuffer3.clear();
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, floatBuffer);
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, floatBuffer2);
            GL11.glGetInteger(GL11.GL_VIEWPORT, intBuffer);
            boolean flag = GLU.gluProject(f, f2, f3, floatBuffer, floatBuffer2, intBuffer, floatBuffer3);
            if (flag) {
                return new float[]{floatBuffer3.get(0), intBuffer.get(3) - floatBuffer3.get(1), floatBuffer3.get(2)};
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
