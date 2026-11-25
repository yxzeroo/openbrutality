package org.brutality.gui.mainmenu.theme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.shader.ShaderEffect;
import org.lwjgl.opengl.GL11;

public final class DisposeComponent extends MainMenuScene {
    private final String name;
    private final String description;
    private final int count;
    private static ShaderEffect shaderEffect;
    private static boolean flag;
    private final long timestamp = System.nanoTime();

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
    }

    private boolean checkState() {
        if (flag) {
            return false;
        }
        try {
            if (shaderEffect == null || !shaderEffect.isValid()) {
                shaderEffect = new ShaderEffect("brutality/shaders/menu-scenes.frag");
            }

            shaderEffect.init();
            shaderEffect.handleString("TIME", (float)(System.nanoTime() - this.timestamp) / 1.0E9F);
            shaderEffect.handleString("MODE", (float)this.count);
            shaderEffect.handleString("INTRO", 0.0F);
            return true;
        } catch (RuntimeException e) {
            flag = true;
            return false;
        }
    }

    @Override
    public void dispose() {
        if (shaderEffect != null) {
            shaderEffect.delete();
            shaderEffect = null;
        }

        flag = false;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        MainMenuIconRenderer.handleInt(n, n2, n3, n4, str, f, this.count == 17 ? new int[]{245, 165, 85} : new int[]{90, 190, 230});
    }

    public void handleInt(int n, int n2, float f) {
        if (this.count == 17) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                if (!this.checkState()) {
                    return;
                }

                GlStateManager.disableDepth();
                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                shaderEffect.handleString("INTRO", Math.max(0.001F, f + 0.001F));
                shaderEffect.handleString("ORIGIN", 0.0F, 0.0F);
                shaderEffect.handleString("RESOLUTION", (float)Minecraft.getMinecraft().displayWidth, (float)Minecraft.getMinecraft().displayHeight);
                ShaderEffect.handleFloat(0.0F, 0.0F, n, n2);
                shaderEffect.handleString("INTRO", 0.0F);
                shaderEffect.unload();
            }
        }
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, float f) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            if (this.checkState()) {
                ShaderEffect.handleShaderEffect(shaderEffect, n, n2, n3, n4);
            } else {
                Gui.drawRect(n, n2, n + n3, n2 + n4, this.count == 17 ? -14609630 : -15718856);
            }
        }
    }

    public DisposeComponent(String name, String description, int count) {
        this.name = name;
        this.description = description;
        this.count = count;
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            if (this.checkState()) {
                ShaderEffect.handleShaderEffect(shaderEffect, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            } else {
                Gui.drawRect(0, 0, n, n2, this.count == 17 ? -14609630 : -15718856);
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}
