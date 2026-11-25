package org.brutality.utils.render.shader.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.utils.mcp.McpApi2;
import org.brutality.utils.render.shader.ShaderEffect;

public class MainMenuHorizonShader implements McpApi2 {
    private static final ShaderEffect shaderEffect = new ShaderEffect("mainmenu");

    public static void handleLong(long l) {
        ScaledResolution sr = new ScaledResolution(mc);
        shaderEffect.init();
        shaderEffect.handleString("TIME", (float)(System.currentTimeMillis() - l) / 1000.0F);
        shaderEffect.handleString(
            "RESOLUTION", (float)((double)sr.getScaledWidth() * sr.getScaleFactor()), (float)((double)sr.getScaledHeight() * sr.getScaleFactor())
        );
        drawHorizon();
        shaderEffect.unload();
    }

    public static void drawHorizon() {
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        float f = mc.gameSettings.renderDistanceChunks * 16;
        double d = f * 0.9238;
        double d2 = f * 0.3826;
        double d3 = -d2;
        double d4 = -d;
        double d5 = 256.0;
        double d6 = -256.0;
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d3, d6, d4).func_181675_d();
        worldRenderer.func_181662_b(d3, d5, d4).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d6, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d6, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d2).func_181675_d();
        worldRenderer.func_181662_b(d4, d6, d2).func_181675_d();
        worldRenderer.func_181662_b(d4, d6, d2).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d2).func_181675_d();
        worldRenderer.func_181662_b(d3, d5, d).func_181675_d();
        worldRenderer.func_181662_b(d3, d6, d).func_181675_d();
        worldRenderer.func_181662_b(d3, d6, d).func_181675_d();
        worldRenderer.func_181662_b(d3, d5, d).func_181675_d();
        worldRenderer.func_181662_b(d2, d5, d).func_181675_d();
        worldRenderer.func_181662_b(d2, d6, d).func_181675_d();
        worldRenderer.func_181662_b(d2, d6, d).func_181675_d();
        worldRenderer.func_181662_b(d2, d5, d).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d2).func_181675_d();
        worldRenderer.func_181662_b(d, d6, d2).func_181675_d();
        worldRenderer.func_181662_b(d, d6, d2).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d2).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d, d6, d3).func_181675_d();
        worldRenderer.func_181662_b(d, d6, d3).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d2, d5, d4).func_181675_d();
        worldRenderer.func_181662_b(d2, d6, d4).func_181675_d();
        worldRenderer.func_181662_b(d2, d6, d4).func_181675_d();
        worldRenderer.func_181662_b(d2, d5, d4).func_181675_d();
        worldRenderer.func_181662_b(d3, d5, d4).func_181675_d();
        worldRenderer.func_181662_b(d3, d6, d4).func_181675_d();
        worldRenderer.func_181662_b(d4, d6, d4).func_181675_d();
        worldRenderer.func_181662_b(d4, d6, d).func_181675_d();
        worldRenderer.func_181662_b(d, d6, d).func_181675_d();
        worldRenderer.func_181662_b(d, d6, d4).func_181675_d();
        Tessellator.getInstance().draw();
    }
}
