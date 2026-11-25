package org.brutality.module.impl.visual.arraylist;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public final class ArraylistHandler2 {
    public static void handleFloat(float f, float f2, float f3, float f4, int n) {
        if (f > f3) {
            float f5 = f;
            f = f3;
            f3 = f5;
        }

        if (f2 > f4) {
            float f6 = f2;
            f2 = f4;
            f4 = f6;
        }

        float f7 = (n >> 24 & 0xFF) / 255.0F;
        float f8 = (n >> 16 & 0xFF) / 255.0F;
        float f9 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f10 = (n & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f8, f9, f10, f7);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private ArraylistHandler2() {
    }
}
