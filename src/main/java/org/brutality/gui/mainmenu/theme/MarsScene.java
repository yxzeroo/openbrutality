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

public class MarsScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 200;
    private float[][] floatValueArray;
    private static final int count2 = 22;
    private float[][] floatValueArray2;

    @Override
    public String getName() {
        return "Mars";
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        float f2 = (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F;

        try {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GL11.glColor4f(0.005F, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glVertex2f(n, 0.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(0.0F, n2);
            GL11.glEnd();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (int i = 0; i < 200; i++) {
                float f3 = this.floatValueArray[i][0] * n;
                float f4 = this.floatValueArray[i][1] * n2;
                float f5 = this.floatValueArray[i][3] * (0.7F + (float)Math.sin(f2 * 1.2F + i) * 0.3F);
                GL11.glColor4f(1.0F, 0.9F, 0.85F, f5);
                this.handleFloat(f3, f4, this.floatValueArray[i][2], 6);
            }

            float f6 = n * 0.55F;
            float f7 = n2 * 0.5F;
            float f8 = Math.min(n, n2) * 0.4F;
            this.handleFloat(f6, f7, f8, f2);

            for (int j = 4; j >= 1; j--) {
                float f9 = 0.04F / j;
                GL11.glColor4f(1.0F, 0.55F, 0.35F, f9);
                this.handleFloat(f6, f7, f8 + j * 5, 64);
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
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public MarsScene() {
        this.generateStars();
        this.process();
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

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.generateStars();
        this.process();
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
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        int n6 = 145 + (int)(40.0F * f);
        GL11.glColor4f(0.10980392F, 0.03137255F, 0.015686275F, n6 / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        int n7 = 150 + (int)(105.0F * f);
        GL11.glColor4f(0.88235295F, 0.43137255F, 0.23529412F, n7 / 255.0F);
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
            int rgb = new Color(255, 220, 200).getRGB();
            int n8 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n9 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            this.drawStringWithShadowComponent.drawString(str, n8 + 1, n9 + 1, new Color(0, 0, 0, 160).getRGB());
            this.drawStringWithShadowComponent.drawString(str, n8, n9, rgb);
        }

        GlStateManager.disableBlend();
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

    private void generateStars() {
        this.floatValueArray = new float[200][4];

        for (int i = 0; i < 200; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random();
            this.floatValueArray[i][2] = 0.4F + (float)Math.random() * 1.0F;
            this.floatValueArray[i][3] = 0.4F + (float)Math.random() * 0.5F;
        }
    }

    private void process() {
        this.floatValueArray2 = new float[22][4];

        for (int i = 0; i < 22; i++) {
            this.floatValueArray2[i][0] = (float)(Math.random() * Math.PI * 2.0);
            this.floatValueArray2[i][1] = (float)(Math.random() * 0.85);
            this.floatValueArray2[i][2] = 0.04F + (float)Math.random() * 0.1F;
            this.floatValueArray2[i][3] = 0.5F + (float)Math.random() * 0.5F;
        }
    }

    private void handleFloat(float f, float f2, float f3, float f4) {
        GL11.glColor4f(0.62F, 0.3F, 0.18F, 1.0F);
        this.handleFloat(f, f2, f3, 64);
        float f5 = f4 * 0.04F;

        for (int i = 0; i < 7; i++) {
            float f6 = f5 + i * 0.95F;
            float cos = (float)Math.cos(f6);
            if (!(cos < -0.2F)) {
                float f7 = Math.max(0.0F, (cos + 0.2F) / 0.7F);
                float f8 = f + (float)Math.cos(f6) * f3 * 0.55F;
                float f9 = f2 + (float)Math.sin(f6) * f3 * 0.45F;
                float f10 = f3 * (0.18F + i % 3 * 0.06F);
                GL11.glColor4f(0.82F, 0.5F, 0.32F, 0.55F * f7);
                this.handleFloat(f8, f9, f10, 32);
            }
        }

        for (int j = 0; j < 5; j++) {
            float f11 = f5 * 0.8F + j * 1.4F + 0.4F;
            float cos2 = (float)Math.cos(f11);
            if (!(cos2 < -0.2F)) {
                float f12 = Math.max(0.0F, (cos2 + 0.2F) / 0.7F);
                float f13 = f + (float)Math.cos(f11) * f3 * 0.5F;
                float f14 = f2 + (float)Math.sin(f11) * f3 * 0.4F;
                float f15 = f3 * (0.12F + j % 4 * 0.04F);
                GL11.glColor4f(0.36F, 0.16F, 0.08F, 0.65F * f12);
                this.handleFloat(f13, f14, f15, 28);
            }
        }

        for (int k = 0; k < 22; k++) {
            float f16 = this.floatValueArray2[k][0] + f5;
            float cos3 = (float)Math.cos(f16);
            if (!(cos3 < 0.0F)) {
                float f17 = this.floatValueArray2[k][1];
                float f18 = f + (float)Math.cos(f16) * f3 * f17;
                float f19 = f2 + (float)Math.sin(f16) * f3 * f17 * 0.95F;
                float f20 = f3 * this.floatValueArray2[k][2];
                GL11.glColor4f(0.3F, 0.12F, 0.06F, 0.7F * cos3);
                this.handleFloat(f18, f19, f20, 16);
                GL11.glColor4f(0.85F, 0.55F, 0.35F, 0.55F * cos3);
                this.handleFloat(f18, f19, f20, f20 * 0.85F, 16);
            }
        }

        float f21 = f2 + f3 * 0.78F;
        GL11.glColor4f(1.0F, 0.98F, 0.95F, 0.8F);
        this.handleFloat(f + f3 * 0.05F, f21, f3 * 0.18F, 32);

        for (int idx = 0; idx < 5; idx++) {
            float f22 = idx / 5.0F;
            float f23 = 0.15F * (1.0F - f22);
            GL11.glColor4f(0.04F, 0.0F, 0.0F, f23);
            float f24 = -f3 * (0.5F + f22 * 0.18F);
            this.handleFloat(f + f24, f2, f3 * 0.94F, 64);
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glColor4f(1.0F, 0.7F, 0.5F, 0.4F);
        this.handleFloat(f, f2, f3, f3 * 0.97F, 64);
    }

    @Override
    public String getDescription() {
        return "Rust-red surface of the fourth planet";
    }
}
