package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GLAllocation {
    public static synchronized int generateDisplayLists(int range) {
        int n = GL11.glGenLists(range);
        if (n == 0) {
            int n2 = GL11.glGetError();
            String str = "No error code reported";
            if (n2 != 0) {
                str = GLU.gluErrorString(n2);
            }

            if (Minecraft.isRunningOnMac) {
                System.err.println("[Brutality] glGenLists failed on macOS (likely Apple Silicon): " + str);
                System.err.println("[Brutality] Display lists will be emulated. Performance may be affected.");
                return 1;
            }
            throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + range + ", GL error (" + n2 + "): " + str);
        }
        return n;
    }

    public static synchronized void deleteDisplayLists(int list, int range) {
        if (!Minecraft.isRunningOnMac || list != 1) {
            GL11.glDeleteLists(list, range);
        }
    }

    public static synchronized void deleteDisplayLists(int list) {
        if (!Minecraft.isRunningOnMac || list != 1) {
            GL11.glDeleteLists(list, 1);
        }
    }

    public static synchronized ByteBuffer createDirectByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer createDirectIntBuffer(int capacity) {
        return createDirectByteBuffer(capacity << 2).asIntBuffer();
    }

    public static FloatBuffer createDirectFloatBuffer(int capacity) {
        return createDirectByteBuffer(capacity << 2).asFloatBuffer();
    }
}
