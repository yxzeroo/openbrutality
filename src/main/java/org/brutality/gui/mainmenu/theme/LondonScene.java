package org.brutality.gui.mainmenu.theme;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.lwjgl.opengl.GL11;

public class LondonScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 80;
    private float[][] floatValueArray;

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

    @Override
    public String getDescription() {
        return "Tower Bridge over the Thames at sunset";
    }

    private void handleInt(int n, int n2, float f, float f2) {
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        for (int i = 0; i < 40; i++) {
            float f3 = (float)(i * 0.025) * n;
            float f4 = (float)Math.sin(f2 * 1.5 + i * 0.4) * 1.5F;
            float f5 = f + n2 * 0.1F + f4;
            float f6 = n2 - n2 * 0.04F;
            GL11.glColor4f(0.95F, 0.55F, 0.2F, 0.18F + (float)Math.sin(f2 * 2.0F + i) * 0.08F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(f3, f5);
            GL11.glVertex2f(f3 + n * 0.04F, f5);
            GL11.glVertex2f(f3 + n * 0.04F, f5 + 2.0F);
            GL11.glVertex2f(f3, f5 + 2.0F);
            GL11.glEnd();
        }

        for (int j = 0; j < 30; j++) {
            float f7 = j / 30.0F * n + (float)Math.sin(f2 + j) * 4.0F;
            float f8 = f + n2 * 0.05F;
            float f9 = f + n2 * 0.3F;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(1.0F, 0.8F, 0.4F, 0.18F);
            GL11.glVertex2f(f7, f8);
            GL11.glVertex2f(f7 + 1.0F, f8);
            GL11.glColor4f(1.0F, 0.8F, 0.4F, 0.0F);
            GL11.glVertex2f(f7 + 1.0F, f9);
            GL11.glVertex2f(f7, f9);
            GL11.glEnd();
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        float f2 = (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F;

        try {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            float f3 = n2 * 0.55F;
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.04F, 0.06F, 0.18F, 1.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glVertex2f(n, 0.0F);
            GL11.glColor4f(0.45F, 0.2F, 0.4F, 1.0F);
            GL11.glVertex2f(n, f3);
            GL11.glVertex2f(0.0F, f3);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.45F, 0.2F, 0.4F, 1.0F);
            GL11.glVertex2f(0.0F, f3);
            GL11.glVertex2f(n, f3);
            GL11.glColor4f(0.95F, 0.5F, 0.18F, 1.0F);
            GL11.glVertex2f(n, f3 + n2 * 0.1F);
            GL11.glVertex2f(0.0F, f3 + n2 * 0.1F);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.95F, 0.5F, 0.18F, 1.0F);
            GL11.glVertex2f(0.0F, f3 + n2 * 0.1F);
            GL11.glVertex2f(n, f3 + n2 * 0.1F);
            GL11.glColor4f(0.05F, 0.06F, 0.14F, 1.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(0.0F, n2);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableAlpha();
            float f4 = f2 * 0.022F % 1.0F;
            float f5 = -f4 * n;
            this.handleInt(n, n2, f3, f2, f5);
            this.handleInt(n, n2, f3, f2, f5 + n);
            this.handleInt(n, n2, f3);
            this.handleInt(n, n2, f3, f2);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (int i = 0; i < 80; i++) {
                float f6 = this.floatValueArray[i][0] * n;
                float f7 = f3 - 5.0F - this.floatValueArray[i][1] * n2 * 0.2F;
                float f8 = this.floatValueArray[i][2] * (0.7F + (float)Math.sin(f2 * this.floatValueArray[i][3] + i) * 0.3F);
                GL11.glColor4f(1.0F, 0.85F, 0.55F, f8 * 0.85F);
                this.handleFloat(f6, f7, 1.0F, 6);
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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

    private void handleFloat(float f, float f2, float f3, float f4, int n) {
        float f5 = (f + f3) * 0.5F;
        float f6 = Math.max(f2, f4) + Math.abs(f3 - f) * 0.12F;
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i <= n; i++) {
            float f7 = (float)i / n;
            float f8 = 1.0F - f7;
            float f9 = f8 * f8 * f + 2.0F * f8 * f7 * f5 + f7 * f7 * f3;
            float f10 = f8 * f8 * f2 + 2.0F * f8 * f7 * f6 + f7 * f7 * f4;
            GL11.glVertex2f(f9, f10);
        }

        GL11.glEnd();
    }

    @Override
    public String getName() {
        return "London";
    }

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.process();
    }

    private void handleInt(int n, int n2, float f) {
        float f2 = f - n2 * 0.04F;
        float f3 = f - n2 * 0.32F;
        float f4 = n * 0.3F;
        float f5 = n * 0.7F;
        float f6 = n * 0.075F;
        GL11.glColor4f(0.03F, 0.02F, 0.06F, 1.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(f4 + f6 * 0.5F, f2);
        GL11.glVertex2f(f5 - f6 * 0.5F, f2);
        GL11.glVertex2f(f5 - f6 * 0.5F, f2 + n2 * 0.025F);
        GL11.glVertex2f(f4 + f6 * 0.5F, f2 + n2 * 0.025F);
        GL11.glEnd();
        GL11.glColor4f(0.04F, 0.03F, 0.08F, 1.0F);
        GL11.glLineWidth(2.0F);
        this.handleFloat(n * 0.06F, f2, f4, f3 + n2 * 0.06F, 18);
        this.handleFloat(f4, f3 + n2 * 0.06F, f5, f3 + n2 * 0.06F, 18);
        this.handleFloat(f5, f3 + n2 * 0.06F, n * 0.94F, f2, 18);
        GL11.glLineWidth(1.0F);
        this.handleFloat(f4, f3, f6, f - f3);
        this.handleFloat(f5, f3, f6, f - f3);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        for (int i = 0; i < 2; i++) {
            float f7 = i == 0 ? f4 : f5;

            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 3; k++) {
                    float f8 = f7 - f6 * 0.3F + k * f6 * 0.3F;
                    float f9 = f3 + n2 * 0.04F + j * n2 * 0.045F;
                    GL11.glColor4f(1.0F, 0.85F, 0.45F, 0.7F);
                    this.handleFloat(f8, f9, 1.2F, 6);
                }
            }
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public LondonScene() {
        this.process();
    }

    private void process() {
        this.floatValueArray = new float[80][4];

        for (int i = 0; i < 80; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random();
            this.floatValueArray[i][2] = 0.5F + (float)Math.random() * 0.5F;
            this.floatValueArray[i][3] = 0.5F + (float)Math.random() * 2.0F;
        }
    }

    private void handleInt(int n, int n2, float f, float f2, float f3) {
        GL11.glColor4f(0.02F, 0.02F, 0.06F, 0.85F);
        GL11.glBegin(GL11.GL_QUADS);
        byte b = 28;

        for (int i = 0; i < b; i++) {
            float f4 = (float)i / b * n + f3;
            float f5 = (float)(i + 1) / b * n + f3;
            if (!(f5 < 0.0F) && !(f4 > n)) {
                float f6 = (float)(Math.sin(i * 1.7) * 0.5 + Math.sin(i * 3.1) * 0.3 + 0.5);
                float f7 = (float)(Math.sin((i + 1) * 1.7) * 0.5 + Math.sin((i + 1) * 3.1) * 0.3 + 0.5);
                float f8 = f - n2 * 0.04F - f6 * n2 * 0.16F;
                float f9 = f - n2 * 0.04F - f7 * n2 * 0.16F;
                GL11.glVertex2f(f4, f);
                GL11.glVertex2f(f5, f);
                GL11.glVertex2f(f5, f9);
                GL11.glVertex2f(f4, f8);
            }
        }

        GL11.glEnd();
    }

    private void handleFloat(float f, float f2, float f3, float f4) {
        float f5 = f3 * 0.5F;
        GL11.glColor4f(0.04F, 0.03F, 0.07F, 1.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(f - f5, f2 + f4);
        GL11.glVertex2f(f + f5, f2 + f4);
        GL11.glVertex2f(f + f5, f2 + f4 * 0.15F);
        GL11.glVertex2f(f - f5, f2 + f4 * 0.15F);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(f - f5, f2 + f4 * 0.15F);
        GL11.glVertex2f(f + f5, f2 + f4 * 0.15F);
        GL11.glVertex2f(f, f2);
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
        int n6 = 150 + (int)(40.0F * f);
        GL11.glColor4f(0.078431375F, 0.05490196F, 0.1254902F, n6 / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        int n7 = 150 + (int)(105.0F * f);
        GL11.glColor4f(0.9607843F, 0.54901963F, 0.27450982F, n7 / 255.0F);
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
            int rgb = new Color(255, 220, 180).getRGB();
            int n8 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n9 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            this.drawStringWithShadowComponent.drawString(str, n8 + 1, n9 + 1, new Color(0, 0, 0, 160).getRGB());
            this.drawStringWithShadowComponent.drawString(str, n8, n9, rgb);
        }

        GlStateManager.disableBlend();
    }
}
