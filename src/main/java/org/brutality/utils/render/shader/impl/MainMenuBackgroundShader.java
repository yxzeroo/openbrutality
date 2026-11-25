package org.brutality.utils.render.shader.impl;

import net.minecraft.client.gui.ScaledResolution;
import org.brutality.utils.mcp.McpApi2;
import org.brutality.utils.render.shader.ShaderEffect;

public class MainMenuBackgroundShader implements McpApi2 {
    private static final ShaderEffect shaderEffect = new ShaderEffect("mainmenu");

    public static void handleLong(long l) {
        ScaledResolution sr = new ScaledResolution(mc);
        shaderEffect.init();
        shaderEffect.handleString("TIME", (float)(System.currentTimeMillis() - l) / 1000.0F);
        shaderEffect.handleString(
            "RESOLUTION", (float)((double)sr.getScaledWidth() * sr.getScaleFactor()), (float)((double)sr.getScaledHeight() * sr.getScaleFactor())
        );
        ShaderEffect.process2();
        shaderEffect.unload();
    }
}
