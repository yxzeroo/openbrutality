package org.brutality.gui.mainmenu.theme;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.lwjgl.opengl.GL11;

public class CyberpunkScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 60;
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

    public CyberpunkScene() {
        this.process();
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        float f2 = (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F;

        try {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.015686275F, 0.007843138F, 0.03137255F, 1.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glVertex2f(n, 0.0F);
            GL11.glColor4f(0.007843138F, 0.003921569F, 0.023529412F, 1.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(0.0F, n2);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            this.handleInt2(n, n2, f2);
            this.handleInt(n, n2, f2);
            this.handleInt3(n, n2, f2);
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

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.process();
    }

    private void handleInt(int n, int n2, float f) {
        for (int i = 0; i < 60; i++) {
            this.floatValueArray[i][1] = this.floatValueArray[i][1] + this.floatValueArray[i][2];
            if (this.floatValueArray[i][1] > 1.0F) {
                this.floatValueArray[i][1] = -((float)Math.random()) * 0.3F;
                this.floatValueArray[i][0] = (float)Math.random();
            }

            float f2 = this.floatValueArray[i][0] * n;
            float f3 = this.floatValueArray[i][1] * n2;
            float f4 = this.floatValueArray[i][3] * n2;
            boolean flag = this.floatValueArray[i][4] > 0.5F;
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_QUADS);
            if (flag) {
                GL11.glColor4f(1.0F, 0.19607843F, 0.8627451F, 0.9F);
            } else {
                GL11.glColor4f(0.0F, 0.9411765F, 1.0F, 0.9F);
            }

            GL11.glVertex2f(f2 - 1.0F, f3);
            GL11.glVertex2f(f2 + 1.0F, f3);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glVertex2f(f2 + 1.0F, f3 - f4);
            GL11.glVertex2f(f2 - 1.0F, f3 - f4);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }
    }

    @Override
    public String getDescription() {
        return "Neon grid, digital rain & retro-future vibes";
    }

    private void process() {
        this.floatValueArray = new float[60][5];

        for (int i = 0; i < 60; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random();
            this.floatValueArray[i][2] = 3.0E-4F + (float)Math.random() * 8.0E-4F;
            this.floatValueArray[i][3] = 0.04F + (float)Math.random() * 0.12F;
            this.floatValueArray[i][4] = i % 3 == 0 ? 1.0F : 0.0F;
        }
    }

    private void handleInt2(int n, int n2, float f) {
        float f2 = n2 * 0.5F;
        float f3 = 0.12F + (float)Math.sin(f * 0.5) * 0.02F;
        byte b = 16;

        for (int i = 0; i < b; i++) {
            float f4 = (float)i / b;
            float f5 = f2 + (n2 - f2) * (f4 * f4);
            float f6 = f3 * (1.0F - f4 * 0.6F);
            GL11.glColor4f(0.0F, 0.78431374F, 1.0F, f6);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(0.0F, f5);
            GL11.glVertex2f(n, f5);
            GL11.glEnd();
        }

        int n3 = n / 2;
        byte b2 = 20;

        for (int j = 0; j <= b2; j++) {
            float f7 = (float)j / b2 * n;
            float f8 = f3 * (1.0F - Math.abs(j - b2 / 2.0F) / (b2 / 2.0F) * 0.5F);
            GL11.glColor4f(0.0F, 0.78431374F, 1.0F, f8);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(n3, f2);
            GL11.glVertex2f(f7, n2);
            GL11.glEnd();
        }

        GL11.glColor4f(0.78431374F, 0.0F, 1.0F, 0.025F);

        for (int k = 0; k < n2; k += 4) {
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(0.0F, k);
            GL11.glVertex2f(n, k);
            GL11.glEnd();
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
        float f2 = 0.55F + 0.2F * f;
        GL11.glColor4f(0.03137255F, 0.015686275F, 0.07058824F, f2);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        long now = System.currentTimeMillis();
        float f3;
        float f4;
        float f5;
        if (n5 % 2 == 0) {
            f3 = 0.0F;
            f4 = 0.8627451F;
            f5 = 1.0F;
        } else {
            f3 = 1.0F;
            f4 = 0.19607843F;
            f5 = 0.78431374F;
        }

        float f6 = 0.4F + 0.6F * f;
        GL11.glColor4f(f3, f4, f5, f6);
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
        if (f > 0.1F) {
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            int n6 = Math.max(2, n4 / 4);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(f3, f4, f5, 0.15F * f);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(n + n3, n2);
            GL11.glColor4f(f3, f4, f5, 0.0F);
            GL11.glVertex2f(n + n3, n2 + n6);
            GL11.glVertex2f(n, n2 + n6);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float f7 = (float)(now / 120.0 + n5 * 0.8);
            int n7 = n2 + (int)(f7 % 1.0F * n4);
            GL11.glColor4f(f3, f4, f5, f * 0.4F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(n, n7);
            GL11.glVertex2f(n + n3, n7);
            GL11.glVertex2f(n + n3, n7 + 1);
            GL11.glVertex2f(n, n7 + 1);
            GL11.glEnd();
        }

        GlStateManager.enableTexture2D();
        if (this.drawStringWithShadowComponent != null) {
            float f8 = f * 0.8F;
            int n8;
            int n9;
            int n10;
            if (n5 % 2 == 0) {
                n9 = (int)(170.0F + 85.0F * f8);
                n10 = (int)(230.0F + 25.0F * f8);
                n8 = 255;
            } else {
                n9 = 255;
                n10 = (int)(160.0F + 40.0F * f8);
                n8 = (int)(220.0F + 35.0F * f8);
            }

            int rgb = new Color(n9, n10, n8, 255).getRGB();
            int n11 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n12 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            if (f > 0.2F) {
                int n13 = (int)(f * 60.0F);
                this.drawStringWithShadowComponent
                    .drawString(str, n11 - 1, n12, new Color((int)(f3 * 255.0F), (int)(f4 * 255.0F), (int)(f5 * 255.0F), n13).getRGB());
                this.drawStringWithShadowComponent
                    .drawString(str, n11 + 1, n12, new Color((int)(f3 * 255.0F), (int)(f4 * 255.0F), (int)(f5 * 255.0F), n13).getRGB());
            }

            this.drawStringWithShadowComponent.drawString(str, n11 + 1, n12 + 1, new Color(0, 0, 0, 80).getRGB());
            this.drawStringWithShadowComponent.drawString(str, n11, n12, rgb);
        }

        GlStateManager.disableBlend();
    }

    @Override
    public String getName() {
        return "Cyberpunk";
    }

    private void handleInt3(int n, int n2, float f) {
        float f2 = 0.5F + (float)Math.sin(f * 2.0F) * 0.3F;
        int n3 = Math.min(n, n2) / 8;
        byte b = 2;
        GL11.glColor4f(0.0F, 0.8627451F, 1.0F, f2 * 0.85F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glVertex2f(n3, 0.0F);
        GL11.glVertex2f(n3, b);
        GL11.glVertex2f(0.0F, b);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glVertex2f(b, 0.0F);
        GL11.glVertex2f(b, n3);
        GL11.glVertex2f(0.0F, n3);
        GL11.glEnd();
        GL11.glColor4f(1.0F, 0.19607843F, 0.78431374F, f2 * 0.85F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n - n3, 0.0F);
        GL11.glVertex2f(n, 0.0F);
        GL11.glVertex2f(n, b);
        GL11.glVertex2f(n - n3, b);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n - b, 0.0F);
        GL11.glVertex2f(n, 0.0F);
        GL11.glVertex2f(n, n3);
        GL11.glVertex2f(n - b, n3);
        GL11.glEnd();
        GL11.glColor4f(0.0F, 0.8627451F, 1.0F, f2 * 0.85F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0.0F, n2 - b);
        GL11.glVertex2f(n3, n2 - b);
        GL11.glVertex2f(n3, n2);
        GL11.glVertex2f(0.0F, n2);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0.0F, n2 - n3);
        GL11.glVertex2f(b, n2 - n3);
        GL11.glVertex2f(b, n2);
        GL11.glVertex2f(0.0F, n2);
        GL11.glEnd();
        GL11.glColor4f(1.0F, 0.19607843F, 0.78431374F, f2 * 0.85F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n - n3, n2 - b);
        GL11.glVertex2f(n, n2 - b);
        GL11.glVertex2f(n, n2);
        GL11.glVertex2f(n - n3, n2);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n - b, n2 - n3);
        GL11.glVertex2f(n, n2 - n3);
        GL11.glVertex2f(n, n2);
        GL11.glVertex2f(n - b, n2);
        GL11.glEnd();
    }
}
