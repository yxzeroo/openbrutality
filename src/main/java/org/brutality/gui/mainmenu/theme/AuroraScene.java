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

public class AuroraScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 180;
    private float[][] floatValueArray;

    @Override
    public String getName() {
        return "Aurora";
    }

    private void generateStars() {
        this.floatValueArray = new float[180][4];

        for (int i = 0; i < 180; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random() * 0.7F;
            this.floatValueArray[i][2] = 0.3F + (float)Math.random() * 0.7F;
            this.floatValueArray[i][3] = 0.5F + (float)Math.random() * 2.0F;
        }
    }

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.generateStars();
    }

    public AuroraScene() {
        this.generateStars();
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
            GL11.glColor4f(0.007843138F, 0.03137255F, 0.07058824F, 1.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glVertex2f(n, 0.0F);
            GL11.glColor4f(0.0F, 0.011764706F, 0.039215688F, 1.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(0.0F, n2);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            this.handleInt(n, n2, f2);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            this.handleInt(n, n2, f2 * 0.18F, n2 * 0.32F, n2 * 0.1F, 0.05F, 0.85F, 0.35F, 0.22F);
            this.handleInt(n, n2, f2 * 0.13F + 1.2F, n2 * 0.38F, n2 * 0.08F, 0.1F, 0.75F, 0.6F, 0.15F);
            this.handleInt(n, n2, f2 * 0.22F + 2.5F, n2 * 0.25F, n2 * 0.07F, 0.2F, 0.9F, 0.8F, 0.12F);
            this.handleInt(n, n2, f2 * 0.1F + 4.0F, n2 * 0.42F, n2 * 0.06F, 0.45F, 0.25F, 0.75F, 0.1F);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float f3 = Math.min(n, n2) * 0.14F;
            SelectorComponent.handleNearMissMode(SelectorComponent.NearMissMode.NEAR_MISS, n, n2, n * 0.5F, n2 * 0.38F, f3);
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

    private void handleInt(int n, int n2, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        byte b = 40;
        GL11.glBegin(GL11.GL_QUADS);

        for (int i = 0; i < b; i++) {
            float f8 = (float)i / b * n;
            float f9 = (float)(i + 1) / b * n;
            float f10 = f2
                + (float)(Math.sin(f8 / n * Math.PI * 3.0 + f) * f3 * 0.6 + Math.sin(f8 / n * Math.PI * 1.5 + f * 0.7) * f3 * 0.4);
            float f11 = f2
                + (float)(Math.sin(f9 / n * Math.PI * 3.0 + f) * f3 * 0.6 + Math.sin(f9 / n * Math.PI * 1.5 + f * 0.7) * f3 * 0.4);
            GL11.glColor4f(f4, f5, f6, 0.0F);
            GL11.glVertex2f(f8, f10 - f3);
            GL11.glVertex2f(f9, f11 - f3);
            GL11.glColor4f(f4, f5, f6, f7);
            GL11.glVertex2f(f9, f11);
            GL11.glVertex2f(f8, f10);
        }

        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);

        for (int j = 0; j < b; j++) {
            float f12 = (float)j / b * n;
            float f13 = (float)(j + 1) / b * n;
            float f14 = f2
                + (float)(Math.sin(f12 / n * Math.PI * 3.0 + f) * f3 * 0.6 + Math.sin(f12 / n * Math.PI * 1.5 + f * 0.7) * f3 * 0.4);
            float f15 = f2
                + (float)(Math.sin(f13 / n * Math.PI * 3.0 + f) * f3 * 0.6 + Math.sin(f13 / n * Math.PI * 1.5 + f * 0.7) * f3 * 0.4);
            GL11.glColor4f(f4, f5, f6, f7);
            GL11.glVertex2f(f12, f14);
            GL11.glVertex2f(f13, f15);
            GL11.glColor4f(f4, f5, f6, 0.0F);
            GL11.glVertex2f(f13, f15 + f3);
            GL11.glVertex2f(f12, f14 + f3);
        }

        GL11.glEnd();
    }

    private void handleInt(int n, int n2, float f) {
        for (int i = 0; i < 180; i++) {
            float f2 = this.floatValueArray[i][0] * n;
            float f3 = this.floatValueArray[i][1] * n2;
            float f4 = this.floatValueArray[i][2] * ((float)Math.sin(f * this.floatValueArray[i][3] + i) * 0.3F + 0.7F);
            GL11.glColor4f(0.8F, 0.9F, 1.0F, f4 * 0.9F);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(f2, f3);
            float f5 = 0.7F + i % 3 * 0.4F;

            for (int j = 0; j <= 6; j++) {
                float f6 = (float)(j * 2 * Math.PI / 6.0);
                GL11.glVertex2f(f2 + (float)Math.cos(f6) * f5, f3 + (float)Math.sin(f6) * f5);
            }

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
        float f2 = 0.44F + 0.18F * f;
        GL11.glColor4f(0.007843138F, 0.07058824F, 0.047058824F, f2);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        float f3 = 0.3F + 0.7F * f;
        GL11.glColor4f(0.3137255F, 0.9019608F, 0.5882353F, f3);
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
        if (f > 0.05F) {
            int n6 = Math.max(3, n4 / 4);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.19607843F, 0.8627451F, 0.47058824F, 0.12F * f);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(n + n3, n2);
            GL11.glColor4f(0.19607843F, 0.8627451F, 0.47058824F, 0.0F);
            GL11.glVertex2f(n + n3, n2 + n6);
            GL11.glVertex2f(n, n2 + n6);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }

        if (f > 0.2F) {
            long now = System.currentTimeMillis();
            int n7 = (int)(f * 6.0F) + 2;

            for (int i = 0; i < n7; i++) {
                float f4 = (float)(now / (600.0 + i * 40) + i * 1.3 + n5 * 0.5);
                float f5 = f4 % 1.0F;
                int n8 = n + (int)(f5 * (n3 - 12)) + 6;
                int n9 = n2 + (int)((Math.sin(f4 * 2.2 + i) * 0.5 + 0.5) * (n4 - 6)) + 3;
                float f6 = f * 0.7F * (1.0F - Math.abs(f5 - 0.5F) * 2.0F);
                if (i % 2 == 0) {
                    GL11.glColor4f(0.3137255F, 0.9411765F, 0.627451F, f6);
                } else {
                    GL11.glColor4f(0.23529412F, 0.78431374F, 0.8627451F, f6);
                }

                GL11.glBegin(GL11.GL_TRIANGLE_FAN);
                GL11.glVertex2f(n8, n9);

                for (int j = 0; j <= 6; j++) {
                    float f7 = (float)(j * 2 * Math.PI / 6.0);
                    GL11.glVertex2f(n8 + (float)Math.cos(f7) * 2.0F, n9 + (float)Math.sin(f7) * 2.0F);
                }

                GL11.glEnd();
            }
        }

        GlStateManager.enableTexture2D();
        if (this.drawStringWithShadowComponent != null) {
            float f8 = f * 0.7F;
            int n10 = (int)(200.0F + 55.0F * f8);
            int n11 = (int)(230.0F + 25.0F * f8);
            int n12 = (int)(210.0F + 45.0F * f8);
            int rgb = new Color(n10, n11, n12, 255).getRGB();
            int n13 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n14 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            this.drawStringWithShadowComponent.drawString(str, n13 + 1, n14 + 1, new Color(0, 0, 0, 70).getRGB());
            this.drawStringWithShadowComponent.drawString(str, n13, n14, rgb);
        }

        GlStateManager.disableBlend();
    }

    @Override
    public String getDescription() {
        return "Northern lights dancing in the arctic sky";
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
}
