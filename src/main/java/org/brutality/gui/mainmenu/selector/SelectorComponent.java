package org.brutality.gui.mainmenu.selector;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public final class SelectorComponent {
    private static final long timestamp = 300000L;
    private static final long timestamp2 = 5000L;

    public static void handleNearMissMode(SelectorComponent.NearMissMode nearMissMode, int n, int n2, float f, float f2, float f3) {
        long now = System.currentTimeMillis();
        long l = now % 300000L;
        if (l <= 5000L) {
            float f4 = (float)l / 5000.0F;
            float sin = (float)Math.sin(Math.min(1.0F, f4 * 1.15F) * Math.PI);
            float f5;
            float f6;
            if (nearMissMode == SelectorComponent.NearMissMode.NEAR_MISS) {
                float f7 = -n * 0.15F;
                float f8 = -n2 * 0.1F;
                float f9 = f - f3 * 1.35F;
                float f10 = f2 - f3 * 0.85F;
                float f11 = f4 < 0.82F ? f4 / 0.82F : 1.0F;
                float f12 = f4 > 0.82F ? (f4 - 0.82F) / 0.18F : 0.0F;
                f5 = computeFloat(f7, f9, f11) + f12 * f3 * 0.9F;
                f6 = computeFloat(f8, f10, f11) + f12 * f3 * 0.55F;
            } else {
                float f13 = ~(n - 1) * 0.12F;
                float f14 = n2 * 0.72F;
                float f15 = n * 1.12F;
                float f16 = n2 * 0.18F;
                f5 = computeFloat(f13, f15, f4);
                f6 = computeFloat(f14, f16, f4);
            }

            handleFloat(f5, f6, sin, nearMissMode == SelectorComponent.NearMissMode.NEAR_MISS && f4 > 0.78F);
        }
    }

    private static void handleFloat(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        GL11.glColor4f(f4, f5, f6, f7);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(f, f2);

        for (int i = 0; i <= 20; i++) {
            double d = (Math.PI * 2) * i / 20.0;
            GL11.glVertex2f(f + (float)Math.cos(d) * f3, f2 + (float)Math.sin(d) * f3);
        }

        GL11.glEnd();
    }

    private static float computeFloat(float f, float f2, float f3) {
        return f + (f2 - f) * Math.max(0.0F, Math.min(1.0F, f3));
    }

    private static void handleFloat(float f, float f2, float f3, boolean flag) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        float f4 = flag ? 5.5F : 4.0F;
        handleFloat(f, f2, f4 * 2.8F, 1.0F, 0.55F, 0.35F, f3 * 0.18F);
        handleFloat(f, f2, f4, 0.75F, 0.72F, 0.7F, f3 * 0.85F);
        handleFloat(f - f4 * 0.4F, f2 + f4 * 0.2F, f4 * 0.55F, 0.55F, 0.5F, 0.48F, f3 * 0.7F);
        handleFloat(f + f4 * 0.35F, f2 - f4 * 0.15F, f4 * 0.45F, 0.5F, 0.48F, 0.45F, f3 * 0.65F);
        float f5 = flag ? 28.0F : 40.0F;

        for (int i = 0; i < 10; i++) {
            float f6 = i / 10.0F;
            float f7 = f - f5 * f6 * (flag ? 0.85F : 1.1F);
            float f8 = f2 + f5 * f6 * 0.35F;
            handleFloat(f7, f8, f4 * (0.9F - f6 * 0.7F), 1.0F, 0.7F, 0.45F, f3 * (1.0F - f6) * 0.35F);
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
    }

    private SelectorComponent() {
    }

    public static enum NearMissMode {
        NEAR_MISS,
        PASS_BY;

        private static SelectorComponent.NearMissMode[] computeNearMissModeArray() {
            return new SelectorComponent.NearMissMode[]{NEAR_MISS, PASS_BY};
        }
    }
}
