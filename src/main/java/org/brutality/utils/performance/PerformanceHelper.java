package org.brutality.utils.performance;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class PerformanceHelper {
    private static boolean flag = false;
    private static final List<PerformanceHelper.Listener> entries = new ArrayList<>();

    public static void process() {
        if (flag && !entries.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableDepth();

            for (PerformanceHelper.Listener listener : entries) {
                listener.process();
            }

            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
            flag = false;
            entries.clear();
        } else {
            flag = false;
            entries.clear();
        }
    }

    public static void handleFloat(float f, float f2, float f3, float f4, Color color, float f5) {
        if (flag) {
            entries.add(new PerformanceHelper.Entry2(f, f2, f3, f4, color, f5));
        } else {
            new PerformanceHelper.Entry2(f, f2, f3, f4, color, f5).process();
        }
    }

    public static void process2() {
        flag = true;
        entries.clear();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, Color color) {
        if (flag) {
            entries.add(new PerformanceHelper.Entry(f, f2, f3, f4, color));
        } else {
            new PerformanceHelper.Entry(f, f2, f3, f4, color).process();
        }
    }

    public static boolean isFlag() {
        return flag;
    }

    public static class Entry implements PerformanceHelper.Listener {
        private final float factor;
        private final float factor2;
        private final float factor3;
        private final float factor4;
        private final Color color;

        @Override
        public void process() {
            float f = this.color.getRed() / 255.0F;
            float f2 = this.color.getGreen() / 255.0F;
            float f3 = this.color.getBlue() / 255.0F;
            float f4 = this.color.getAlpha() / 255.0F;
            GlStateManager.color(f, f2, f3, f4);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(this.factor, this.factor2);
            GL11.glVertex2f(this.factor, this.factor4);
            GL11.glVertex2f(this.factor3, this.factor4);
            GL11.glVertex2f(this.factor3, this.factor2);
            GL11.glEnd();
        }

        public Entry(float factor, float f, float f2, float f3, Color color) {
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.factor4 = f3;
            this.color = color;
        }
    }

    public static class Entry2 implements PerformanceHelper.Listener {
        private final float factor;
        private final float factor2;
        private final float factor3;
        private final float factor4;
        private final Color color;
        private final float factor5;

        @Override
        public void process() {
            float f = this.color.getRed() / 255.0F;
            float f2 = this.color.getGreen() / 255.0F;
            float f3 = this.color.getBlue() / 255.0F;
            float f4 = this.color.getAlpha() / 255.0F;
            GlStateManager.color(f, f2, f3, f4);
            GL11.glLineWidth(this.factor5);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(this.factor, this.factor2);
            GL11.glVertex2f(this.factor3, this.factor4);
            GL11.glEnd();
        }

        public Entry2(float factor, float f, float f2, float f3, Color color, float f4) {
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.factor4 = f3;
            this.color = color;
            this.factor5 = f4;
        }
    }

    public interface Listener {
        void process();
    }
}
