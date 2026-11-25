package org.brutality.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class DisableDepthHelper {
    public static int[] intValueArray = new int[32];

    public static void handleIntArray(int... intValueArray2) {
        for (int n : intValueArray2) {
            GL11.glEnable(n);
        }

        intValueArray = intValueArray2;
    }

    public static void process6() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void handleFloat(float f, float f2, float f3) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(f, f2, 0.0F);
        GlStateManager.rotate(f3, 0.0F, 0.0F, -1.0F);
        GlStateManager.translate(-f, -f2, 0.0F);
    }

    public static void process5() {
        GlStateManager.disableBlend();
    }

    public static void disableDepth() {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static void process4() {
        for (int n : intValueArray) {
            GL11.glDisable(n);
        }
    }

    public static void enableDepth() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public static void handleFlag(boolean flag) {
        if (flag) {
            process6();
        }

        GlStateManager.disableTexture2D();
    }

    public static void process3() {
        GlStateManager.enableTexture2D();
        process5();
    }

    public static void process2() {
        handleFlag(true);
    }

    public static void process() {
        GlStateManager.popMatrix();
    }
}
