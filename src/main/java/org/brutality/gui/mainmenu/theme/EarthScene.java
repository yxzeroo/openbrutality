package org.brutality.gui.mainmenu.theme;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.selector.SelectorComponent;
import org.lwjgl.opengl.GL11;

public class EarthScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 220;
    private float[][] floatValueArray;

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.timestamp = System.currentTimeMillis();
        this.generateStars();
    }

    public EarthScene() {
        this.generateStars();
    }

    @Override
    public String getName() {
        return "Earth";
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, float f) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = sr.getScaleFactor();
        int scaledHeight = sr.getScaledHeight();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(n * scaleFactor, (scaledHeight - n2 - n4) * scaleFactor, n3 * scaleFactor, n4 * scaleFactor);
        this.handleInt(sr.getScaledWidth(), scaledHeight, f, 0, 0);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void handleFloat(float f, float f2, float f3, int n) {
        if (!(f3 <= 0.0F)) {
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(f, f2);

            for (int i = 0; i <= n; i++) {
                float f4 = (float)(i * 2 * Math.PI / n);
                GL11.glVertex2f(f + (float)Math.cos(f4) * f3, f2 + (float)Math.sin(f4) * f3);
            }

            GL11.glEnd();
        }
    }

    private void handleFloat(float f, float f2, float f3, float f4) {
        GL11.glColor4f(0.05F, 0.15F, 0.45F, 1.0F);
        this.handleFloat(f, f2, f3, 64);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.15F, 0.35F, 0.7F, 0.55F);
        this.handleFloat(f - f3 * 0.18F, f2 - f3 * 0.15F, f3 * 0.85F, 64);
        float f5 = f4 * 0.06F;

        for (int i = 0; i < 8; i++) {
            float f6 = f5 + i * 0.78F;
            float f7 = f + (float)Math.cos(f6) * f3 * 0.55F;
            float f8 = f2 + (float)Math.sin(f6) * f3 * 0.45F;
            float f9 = f3 * (0.12F + i % 3 * 0.05F);
            float cos = (float)Math.cos(f6);
            if (!(cos < -0.3F)) {
                float f10 = Math.max(0.0F, Math.min(1.0F, (cos + 0.3F) / 0.6F));
                if (i % 2 == 0) {
                    GL11.glColor4f(0.45F, 0.38F, 0.22F, 0.85F * f10);
                } else {
                    GL11.glColor4f(0.2F, 0.4F, 0.18F, 0.85F * f10);
                }

                this.handleFloat(f7, f8, f9, 32);
            }
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        for (int j = 0; j < 6; j++) {
            float f11 = -f5 * 1.4F + j * 1.05F;
            float f12 = f + (float)Math.cos(f11) * f3 * 0.6F;
            float f13 = f2 + (float)Math.sin(f11) * f3 * 0.5F;
            float cos2 = (float)Math.cos(f11);
            if (!(cos2 < -0.4F)) {
                float f14 = Math.max(0.0F, (cos2 + 0.4F) / 0.7F);
                float f15 = f3 * (0.1F + j % 4 * 0.04F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.18F * f14);
                this.handleFloat(f12, f13, f15, 28);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.1F * f14);
                this.handleFloat(f12 + f15 * 0.4F, f13 + f15 * 0.2F, f15 * 0.7F, 24);
            }
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (int k = 0; k < 5; k++) {
            float f16 = k / 5.0F;
            float f17 = 0.16F * (1.0F - f16);
            GL11.glColor4f(0.0F, 0.0F, 0.04F, f17);
            float f18 = -f3 * (0.45F + f16 * 0.18F);
            this.handleFloat(f + f18, f2, f3 * 0.94F, 64);
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glColor4f(0.7F, 0.85F, 1.0F, 0.45F);
        this.handleFloat(f, f2, f3, f3 * 0.97F, 64);
    }

    @Override
    public String getDescription() {
        return "View of the blue planet from low orbit";
    }

    private void generateStars() {
        this.floatValueArray = new float[220][4];

        for (int i = 0; i < 220; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random();
            this.floatValueArray[i][2] = 0.4F + (float)Math.random() * 1.2F;
            this.floatValueArray[i][3] = 0.3F + (float)Math.random() * 0.7F;
        }
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        int n6 = 150 + (int)(40.0F * f);
        GL11.glColor4f(0.03137255F, 0.08627451F, 0.19607843F, n6 / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        int n7 = 150 + (int)(105.0F * f);
        GL11.glColor4f(0.47058824F, 0.78431374F, 1.0F, n7 / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + 1);
        GL11.glVertex2f(n + n3, n2 + 1);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4 - 1);
        GL11.glVertex2f(n, n2 + n4 - 1);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + 1, n2 + n4);
        GL11.glVertex2f(n + 1, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n + n3 - 1, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n + n3 - 1, n2);
        GL11.glEnd();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        if (this.drawStringWithShadowComponent != null) {
            int rgb = new Color(220, 235, 255).getRGB();
            int n8 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n9 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            this.drawStringWithShadowComponent.drawString(str, n8 + 1, n9 + 1, new Color(0, 0, 0, 160).getRGB());
            this.drawStringWithShadowComponent.drawString(str, n8, n9, rgb);
        }

        GlStateManager.disableBlend();
    }

    private void handleFloat(float f, float f2, float f3, float f4, int n) {
        GL11.glBegin(GL11.GL_QUAD_STRIP);

        for (int i = 0; i <= n; i++) {
            float f5 = (float)(i * 2 * Math.PI / n);
            float cos = (float)Math.cos(f5);
            float sin = (float)Math.sin(f5);
            GL11.glVertex2f(f + cos * f3, f2 + sin * f3);
            GL11.glVertex2f(f + cos * f4, f2 + sin * f4);
        }

        GL11.glEnd();
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        float f2 = (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F;

        try {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GL11.glColor4f(0.0F, 0.0F, 0.005F, 1.0F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glVertex2f(n, 0.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(0.0F, n2);
            GL11.glEnd();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (int i = 0; i < 220; i++) {
                float f3 = this.floatValueArray[i][0] * n;
                float f4 = this.floatValueArray[i][1] * n2;
                float f5 = this.floatValueArray[i][3] * (0.7F + (float)Math.sin(f2 * 1.4F + i) * 0.3F);
                GL11.glColor4f(0.9F, 0.95F, 1.0F, f5);
                this.handleFloat(f3, f4, this.floatValueArray[i][2], 6);
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float f6 = n * 0.62F;
            float f7 = n2 * 0.52F;
            float f8 = Math.min(n, n2) * 0.42F;
            this.handleFloat(f6, f7, f8, f2);

            for (int j = 4; j >= 1; j--) {
                float f9 = 0.06F / j;
                GL11.glColor4f(0.4F, 0.7F, 1.0F, f9);
                this.handleFloat(f6, f7, f8 + j * 6, 64);
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            SelectorComponent.handleNearMissMode(SelectorComponent.NearMissMode.NEAR_MISS, n, n2, f6, f7, f8);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        } catch (Exception e) {
            try {
                GL11.glEnd();
            } catch (Exception ignored) {
            }
        } finally {
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glLineWidth(1.0F);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
