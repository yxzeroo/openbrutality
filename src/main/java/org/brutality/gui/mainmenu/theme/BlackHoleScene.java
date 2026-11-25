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

public class BlackHoleScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 250;
    private float[][] floatValueArray;

    private void generateStars() {
        this.floatValueArray = new float[250][4];

        for (int i = 0; i < 250; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random();
            this.floatValueArray[i][2] = 0.3F + (float)Math.random() * 0.8F;
            this.floatValueArray[i][3] = (float)Math.random() * 6.28F;
        }
    }

    private void handleFloat(float f, float f2, float f3, float f4, int n, float f5) {
        GL11.glBegin(GL11.GL_QUAD_STRIP);

        for (int i = 0; i <= n; i++) {
            float f6 = (float)(i * 2 * Math.PI / n);
            float cos = (float)Math.cos(f6);
            float sin = (float)Math.sin(f6);
            GL11.glVertex2f(f + cos * f3, f2 + sin * f3 * f5);
            GL11.glVertex2f(f + cos * f4, f2 + sin * f4 * f5);
        }

        GL11.glEnd();
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

    public BlackHoleScene() {
        this.generateStars();
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
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        int n6 = 160 + (int)(40.0F * f);
        GL11.glColor4f(0.0F, 0.0F, 0.02F, n6 / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        int n7 = 140 + (int)(115.0F * f);
        GL11.glColor4f(0.627451F, 0.23529412F, 1.0F, n7 / 255.0F);
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
            int rgb = new Color(210, 170, 255).getRGB();
            int n8 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n9 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            this.drawStringWithShadowComponent.drawString(str, n8 + 1, n9 + 1, new Color(0, 0, 0, 160).getRGB());
            this.drawStringWithShadowComponent.drawString(str, n8, n9, rgb);
        }

        GlStateManager.disableBlend();
    }

    @Override
    public String getName() {
        return "Black Hole";
    }

    @Override
    public String getDescription() {
        return "Supermassive black hole with accretion disk";
    }

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.generateStars();
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        float f2 = (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F;
        float f3 = n * 0.5F;
        float f4 = n2 * 0.5F;
        float f5 = Math.min(n, n2) * 0.11F;

        try {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glVertex2f(n, 0.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(0.0F, n2);
            GL11.glEnd();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (int i = 0; i < 250; i++) {
                float f6 = this.floatValueArray[i][0] * n;
                float f7 = this.floatValueArray[i][1] * n2;
                float f8 = f6 - f3;
                float f9 = f7 - f4;
                float dist = (float)Math.sqrt(f8 * f8 + f9 * f9);
                float f10 = Math.max(0.1F, Math.min(1.0F, (dist - f5 * 2.0F) / (f5 * 6.0F)));
                float f11 = 0.5F + 0.5F * (float)Math.sin(f2 * 1.3F + this.floatValueArray[i][3]);
                GL11.glColor4f(0.85F, 0.9F, 1.0F, 0.7F * f11 * f10);
                this.handleFloat(f6, f7, this.floatValueArray[i][2], 5);
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            for (int j = 8; j >= 0; j--) {
                float f12 = f5 * (5.0F + j * 1.8F);
                float f13 = 0.025F * (1.0F - j / 8.0F);
                GL11.glColor4f(0.55F, 0.15F, 0.9F, f13);
                this.handleFloat(f3, f4, f12, 64);
            }

            float f14 = 0.82F + 0.18F * (float)Math.sin(f2 * 7.3F);
            float f15 = 0.78F + 0.22F * (float)Math.sin(f2 * 11.1F + 1.2F);
            float f16 = f5 * 0.22F;
            float f17 = Math.max(n2 * 0.45F, f5 * 3.5F);

            for (int k = 4; k >= 0; k--) {
                float f18 = f16 * (2.0F + k);
                float f19 = 0.05F * (1.0F - k / 4.0F) * f14;
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glColor4f(0.4F, 0.7F, 1.0F, f19);
                GL11.glVertex2f(f3 - f18, f4 - f5 * 0.9F);
                GL11.glVertex2f(f3 + f18, f4 - f5 * 0.9F);
                GL11.glColor4f(0.2F, 0.5F, 1.0F, 0.0F);
                GL11.glVertex2f(f3 + f18 * 0.05F, f4 - f5 * 0.9F - f17);
                GL11.glVertex2f(f3 - f18 * 0.05F, f4 - f5 * 0.9F - f17);
                GL11.glEnd();
            }

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.85F, 0.95F, 1.0F, 0.85F * f14);
            GL11.glVertex2f(f3 - f16, f4 - f5 * 0.9F);
            GL11.glVertex2f(f3 + f16, f4 - f5 * 0.9F);
            GL11.glColor4f(0.5F, 0.75F, 1.0F, 0.0F);
            GL11.glVertex2f(f3 + f16 * 0.04F, f4 - f5 * 0.9F - f17);
            GL11.glVertex2f(f3 - f16 * 0.04F, f4 - f5 * 0.9F - f17);
            GL11.glEnd();

            for (int idx = 4; idx >= 0; idx--) {
                float f20 = f16 * (2.0F + idx);
                float f21 = 0.05F * (1.0F - idx / 4.0F) * f15;
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glColor4f(0.4F, 0.7F, 1.0F, f21);
                GL11.glVertex2f(f3 - f20, f4 + f5 * 0.9F);
                GL11.glVertex2f(f3 + f20, f4 + f5 * 0.9F);
                GL11.glColor4f(0.2F, 0.5F, 1.0F, 0.0F);
                GL11.glVertex2f(f3 + f20 * 0.05F, f4 + f5 * 0.9F + f17);
                GL11.glVertex2f(f3 - f20 * 0.05F, f4 + f5 * 0.9F + f17);
                GL11.glEnd();
            }

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.85F, 0.95F, 1.0F, 0.85F * f15);
            GL11.glVertex2f(f3 - f16, f4 + f5 * 0.9F);
            GL11.glVertex2f(f3 + f16, f4 + f5 * 0.9F);
            GL11.glColor4f(0.5F, 0.75F, 1.0F, 0.0F);
            GL11.glVertex2f(f3 + f16 * 0.04F, f4 + f5 * 0.9F + f17);
            GL11.glVertex2f(f3 - f16 * 0.04F, f4 + f5 * 0.9F + f17);
            GL11.glEnd();
            float f22 = f2 * 0.2F;
            float f23 = f5 * 1.55F;
            float f24 = f5 * 3.9F;
            float f25 = 0.32F;

            for (int idx2 = 10; idx2 >= 0; idx2--) {
                float f26 = f24 + idx2 * f5 * 0.35F;
                float f27 = 0.04F * (1.0F - idx2 / 10.0F);
                GL11.glBegin(GL11.GL_QUAD_STRIP);
                byte b = 64;

                for (int idx3 = 0; idx3 <= b; idx3++) {
                    float f28 = (float)(idx3 * 2 * Math.PI / b) + f22;
                    float cos = (float)Math.cos(f28);
                    float sin = (float)Math.sin(f28);
                    float f29 = (cos + 1.0F) * 0.5F;
                    float f30 = 0.95F + 0.05F * f29;
                    float f31 = 0.35F + 0.25F * f29;
                    float f32 = 0.0F;
                    GL11.glColor4f(f30, f31, f32, f27);
                    GL11.glVertex2f(f3 + cos * (f26 + f5 * 0.2F), f4 + sin * f25 * (f26 + f5 * 0.2F));
                    GL11.glVertex2f(f3 + cos * f26, f4 + sin * f25 * f26);
                }

                GL11.glEnd();
            }

            short s = 128;
            GL11.glBegin(GL11.GL_QUAD_STRIP);

            for (int idx4 = 0; idx4 <= s; idx4++) {
                float f33 = (float)(idx4 * 2 * Math.PI / s) + f22;
                float cos2 = (float)Math.cos(f33);
                float sin2 = (float)Math.sin(f33);
                float f34 = (cos2 + 1.0F) * 0.5F;
                float f35 = 0.35F + 0.65F * f34;
                float f36 = 1.0F * f35;
                float f37 = (0.15F + 0.65F * f34) * f35;
                float f38 = 0.05F * f34 * f35;
                float f39 = 0.3F + 0.7F * f34;
                GL11.glColor4f(f36, f37, f38, f39);
                GL11.glVertex2f(f3 + cos2 * f24, f4 + sin2 * f25 * f24);
                GL11.glVertex2f(f3 + cos2 * f23, f4 + sin2 * f25 * f23);
            }

            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUAD_STRIP);

            for (int idx5 = 0; idx5 <= s; idx5++) {
                float f40 = (float)(idx5 * 2 * Math.PI / s) + f22 * 1.6F;
                float cos3 = (float)Math.cos(f40);
                float sin3 = (float)Math.sin(f40);
                float f41 = (cos3 + 1.0F) * 0.5F;
                float f42 = 0.5F + 0.5F * f41;
                GL11.glColor4f(1.0F, 0.85F + 0.15F * f41, 0.5F + 0.5F * f41, f42);
                GL11.glVertex2f(f3 + cos3 * (f23 + f5 * 0.12F), f4 + sin3 * f25 * (f23 + f5 * 0.12F));
                GL11.glVertex2f(f3 + cos3 * f23, f4 + sin3 * f25 * f23);
            }

            GL11.glEnd();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.85F);
            this.handleFloat(f3, f4, f5 * 1.45F, 64);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (int idx6 = 5; idx6 >= 0; idx6--) {
                float f43 = f5 * (1.0F + idx6 * 0.1F);
                float f44 = 0.18F * (1.0F - idx6 / 5.0F);
                float f45 = 0.5F + 0.5F * (float)Math.sin(f2 * 0.5F + idx6);
                GL11.glColor4f(f45 * 0.7F + 0.3F, f45 * 0.4F, f45 * 0.9F + 0.1F, f44);
                this.handleFloat(f3, f4, f43 + 1.0F, f43, 64, f25);
            }

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
            this.handleFloat(f3, f4, f5, 64);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            float f46 = 0.6F + 0.4F * (float)Math.sin(f2 * 3.7F);
            GL11.glColor4f(0.8F, 0.5F, 1.0F, 0.35F * f46);
            this.handleFloat(f3, f4, f5 + 1.5F, f5, 64, 1.0F);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            SelectorComponent.handleNearMissMode(SelectorComponent.NearMissMode.NEAR_MISS, n, n2, f3, f4, f5);
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
