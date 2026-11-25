package org.brutality.gui.mainmenu.theme;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.lwjgl.opengl.GL11;

public class NewYorkCityScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 70;
    private float[][] floatValueArray;
    private float[][] floatValueArray2;
    private float[][] floatValueArray3;
    private float[][] floatValueArray4;
    private long[] longValueArray;

    private void generateStars() {
        this.floatValueArray = new float[70][4];

        for (int i = 0; i < 70; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random() * 0.55F;
            this.floatValueArray[i][2] = 0.4F + (float)Math.random() * 0.9F;
            this.floatValueArray[i][3] = (float)Math.random() * 6.28F;
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
        GL11.glColor4f(0.039215688F, 0.047058824F, 0.09411765F, n6 / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        int n7 = 150 + (int)(105.0F * f);
        GL11.glColor4f(1.0F, 0.78431374F, 0.27450982F, n7 / 255.0F);
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
            int rgb = new Color(255, 235, 180).getRGB();
            int n8 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n9 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            this.drawStringWithShadowComponent.drawString(str, n8 + 1, n9 + 1, new Color(0, 0, 0, 160).getRGB());
            this.drawStringWithShadowComponent.drawString(str, n8, n9, rgb);
        }

        GlStateManager.disableBlend();
    }

    private void handleFloatArray(float[][] floatsArray, int n, int n2, float f, float[] floats, float f2, int n3, float f3, float f4, float f5) {
        int len = floatsArray.length;

        for (int i = 0; i < len; i++) {
            float f6 = floatsArray[i][0] * n + f5;
            float f7 = (i + 1 < len ? floatsArray[i + 1][0] : 1.0F) * n + f5;
            if (!(f7 < 0.0F) && !(f6 > n)) {
                float f8 = floatsArray[i][1] * n2 * f2;
                float f9 = f - f8;
                GL11.glColor4f(floats[0], floats[1], floats[2], 1.0F);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(f6, n2);
                GL11.glVertex2f(f7, n2);
                GL11.glVertex2f(f7, f9);
                GL11.glVertex2f(f6, f9);
                GL11.glEnd();
                int n4 = n3 + i;
                if (n4 < this.longValueArray.length) {
                    this.handleFloat(f6, f7, f9, f, this.longValueArray[n4], f3, f4, n4);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "New York City";
    }

    @Override
    public String getDescription() {
        return "Manhattan skyline lit up at night";
    }

    private void handleFloat(float f, float f2, float f3, float f4, long l, float f5, float f6, int n) {
        float f7 = 5.0F;
        float f8 = 7.0F;

        for (float i = f3 + 4.0F; i < f4 - 4.0F; i += f8) {
            for (float j = f + 3.0F; j < f2 - 3.0F; j += f7) {
                int n2 = (int)((j - f) / f7);
                int n3 = (int)((i - f3) / f8);
                float f9 = this.computeFloat(l, n2, n3);
                if (f9 > 1.0F - f5) {
                    float f10 = 0.85F + 0.15F * (float)Math.sin(f6 * 2.0F + n2 * 1.3F + n3 * 0.7F + n);
                    float f11;
                    float f12;
                    float f13;
                    if (f9 < 1.0F - f5 + f5 * 0.15F) {
                        f11 = 0.95F;
                        f12 = 0.97F;
                        f13 = 0.85F;
                    } else if (f9 > 1.0F - f5 * 0.15F) {
                        f11 = 1.0F;
                        f12 = 0.55F;
                        f13 = 0.2F;
                    } else {
                        f11 = 1.0F;
                        f12 = 0.85F;
                        f13 = 0.45F;
                    }

                    GL11.glColor4f(f11, f12, f13, 0.85F * f10);
                    GL11.glBegin(GL11.GL_QUADS);
                    GL11.glVertex2f(j, i);
                    GL11.glVertex2f(j + 2.5F, i);
                    GL11.glVertex2f(j + 2.5F, i + 3.0F);
                    GL11.glVertex2f(j, i + 3.0F);
                    GL11.glEnd();
                }
            }
        }
    }

    private void process() {
        this.floatValueArray2 = this.computeFloatArray(28, 0.04F, 0.1F);
        this.floatValueArray3 = this.computeFloatArray(20, 0.08F, 0.2F);
        this.floatValueArray4 = this.computeFloatArray(14, 0.14F, 0.34F);
        int n = this.floatValueArray2.length + this.floatValueArray3.length + this.floatValueArray4.length;
        this.longValueArray = new long[n];

        for (int i = 0; i < n; i++) {
            this.longValueArray[i] = (long)(Math.random() * 1000000.0);
        }
    }

    public NewYorkCityScene() {
        this.generateStars();
        this.process();
    }

    private float[][] computeFloatArray(int n, float f, float f2) {
        float[][] floatsArray = new float[n][2];
        float f3 = 0.0F;

        for (int i = 0; i < n; i++) {
            float f4 = 1.0F / n + (float)(Math.random() - 0.5) * 0.02F;
            floatsArray[i][0] = f3;
            floatsArray[i][1] = f + (float)Math.random() * (f2 - f);
            f3 += f4;
        }

        return floatsArray;
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

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.generateStars();
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
            GL11.glColor4f(0.04F, 0.05F, 0.12F, 1.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glVertex2f(n, 0.0F);
            GL11.glColor4f(0.32F, 0.18F, 0.3F, 1.0F);
            GL11.glVertex2f(n, n2 * 0.78F);
            GL11.glVertex2f(0.0F, n2 * 0.78F);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.32F, 0.18F, 0.3F, 1.0F);
            GL11.glVertex2f(0.0F, n2 * 0.78F);
            GL11.glVertex2f(n, n2 * 0.78F);
            GL11.glColor4f(0.1F, 0.05F, 0.1F, 1.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(0.0F, n2);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (int i = 0; i < 70; i++) {
                float f3 = this.floatValueArray[i][0] * n;
                float f4 = this.floatValueArray[i][1] * n2;
                float f5 = 0.45F + 0.55F * (float)Math.sin(f2 * 1.5F + this.floatValueArray[i][3]);
                GL11.glColor4f(1.0F, 0.95F, 0.85F, 0.75F * f5);
                this.handleFloat(f3, f4, this.floatValueArray[i][2], 6);
            }

            for (int j = 0; j < 6; j++) {
                float f6 = 0.06F * (1.0F - j / 6.0F);
                GL11.glColor4f(1.0F, 0.55F, 0.35F, f6);
                this.handleFloat(n * 0.5F, n2 * 0.78F, n2 * 0.3F - j * 8.0F, 48);
            }

            for (int k = 0; k < 4; k++) {
                float f7 = 0.04F * (1.0F - k / 4.0F);
                GL11.glColor4f(1.0F, 0.85F, 0.4F, f7);
                this.handleFloat(n * 0.5F, n2 * 0.78F, n2 * 0.18F - k * 6.0F, 48);
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float f8 = n2 * 0.8F;
            float f9 = f2 * 0.018F % 1.0F;
            float f10 = f2 * 0.028F % 1.0F;
            float f11 = f2 * 0.04F % 1.0F;
            float f12 = -f9 * n;
            float f13 = -f10 * n;
            float f14 = -f11 * n;
            this.handleFloatArray(this.floatValueArray2, n, n2, f8, new float[]{0.1F, 0.1F, 0.16F}, 0.55F, 0, 0.18F, f2, f12);
            this.handleFloatArray(this.floatValueArray2, n, n2, f8, new float[]{0.1F, 0.1F, 0.16F}, 0.55F, 0, 0.18F, f2, f12 + n);
            this.handleFloatArray(
                this.floatValueArray3, n, n2, f8, new float[]{0.06F, 0.06F, 0.12F}, 0.85F, this.floatValueArray2.length, 0.32F, f2, f13
            );
            this.handleFloatArray(
                this.floatValueArray3, n, n2, f8, new float[]{0.06F, 0.06F, 0.12F}, 0.85F, this.floatValueArray2.length, 0.32F, f2, f13 + n
            );
            this.handleFloatArray(
                this.floatValueArray4,
                n,
                n2,
                f8,
                new float[]{0.02F, 0.02F, 0.06F},
                1.0F,
                this.floatValueArray2.length + this.floatValueArray3.length,
                0.55F,
                f2,
                f14
            );
            this.handleFloatArray(
                this.floatValueArray4,
                n,
                n2,
                f8,
                new float[]{0.02F, 0.02F, 0.06F},
                1.0F,
                this.floatValueArray2.length + this.floatValueArray3.length,
                0.55F,
                f2,
                f14 + n
            );
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (int idx = 0; idx < 6; idx++) {
                float f15 = 0.05F * (1.0F - idx / 6.0F);
                GL11.glColor4f(1.0F, 0.55F, 0.35F, f15);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(0.0F, n2 - idx * 4 - 4);
                GL11.glVertex2f(n, n2 - idx * 4 - 4);
                GL11.glVertex2f(n, n2 - idx * 4);
                GL11.glVertex2f(0.0F, n2 - idx * 4);
                GL11.glEnd();
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            for (int idx2 = 0; idx2 < 6; idx2++) {
                float f16 = 0.05F * (idx2 / 6.0F);
                GL11.glColor4f(0.0F, 0.0F, 0.0F, f16);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(0.0F, idx2 * 3);
                GL11.glVertex2f(n, idx2 * 3);
                GL11.glVertex2f(n, idx2 * 3 + 3);
                GL11.glVertex2f(0.0F, idx2 * 3 + 3);
                GL11.glEnd();
            }

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

    private float computeFloat(long l, int n, int n2) {
        long l2 = l * 374761393L + n * 668265263L + n2 * 2147483647L;
        l2 = (l2 ^ l2 >> 13) * 1274126177L;
        return (float)((l2 ^ l2 >> 16) & 2147483647L) / 2.1474836E9F;
    }
}
