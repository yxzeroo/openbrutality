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

public class SolarSystemScene extends MainMenuScene {
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final SolarSystemScene.Entry[] entryArray = new SolarSystemScene.Entry[]{
        new SolarSystemScene.Entry(0.12F, 0.008F, 2.5F, -7638187, false, 0.0F, 0.0F),
        new SolarSystemScene.Entry(0.18F, 0.012F, 1.8F, -1652614, false, 0.0F, 0.0F),
        new SolarSystemScene.Entry(0.25F, 0.013F, 1.2F, -9727018, false, 0.0F, 0.0F),
        new SolarSystemScene.Entry(0.32F, 0.01F, 0.9F, -4111346, false, 0.0F, 0.0F),
        new SolarSystemScene.Entry(0.45F, 0.035F, 0.5F, -2841228, true, 0.4F, 1.8F),
        new SolarSystemScene.Entry(0.58F, 0.03F, 0.35F, -1386824, true, 0.3F, 2.2F),
        new SolarSystemScene.Entry(0.7F, 0.02F, 0.25F, -3020825, false, 0.0F, 0.0F),
        new SolarSystemScene.Entry(0.82F, 0.019F, 0.18F, -10789423, false, 0.0F, 0.0F)
    };
    private static final int count = 300;
    private float[][] floatValueArray;
    private static final int count2 = 3;
    private float[][] floatValueArray2;

    private void handleInt2(int n, int n2, float f) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        float f2 = Math.min(n, n2) * 0.08F;
        float f3 = n * 0.15F;
        float f4 = n2 * 0.25F;
        this.handleFloat2(f3, f4, f2 * 4.0F, 1.0F, 0.9F, 0.6F, 0.15F + (float)Math.sin(f * 2.0F) * 0.02F);
        this.handleFloat2(f3, f4, f2 * 2.5F, 1.0F, 0.8F, 0.4F, 0.25F + (float)Math.sin(f * 3.0F) * 0.03F);
        this.handleFloat2(f3, f4, f2 * 1.5F, 1.0F, 0.95F, 0.7F, 0.4F);
        this.handleFloat(f3, f4, f2, 1.0F, 1.0F, 0.9F, 0.95F);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
    }

    private void generateStars() {
        this.floatValueArray = new float[300][8];

        for (int i = 0; i < 300; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random();
            this.floatValueArray[i][2] = 0.5F + (float)Math.random() * 1.5F;
            this.floatValueArray[i][3] = 0.3F + (float)Math.random() * 0.7F;
            this.floatValueArray[i][4] = 0.5F + (float)Math.random() * 2.0F;
            float f = Math.random() < 0.85 ? 1.0F : (Math.random() < 0.5 ? 0.7F : 1.0F);
            float f2 = Math.random() < 0.85 ? 1.0F : (Math.random() < 0.5 ? 0.8F : 0.7F);
            float f3 = Math.random() < 0.85 ? 1.0F : (Math.random() < 0.5 ? 1.0F : 0.7F);
            this.floatValueArray[i][5] = f;
            this.floatValueArray[i][6] = f2;
            this.floatValueArray[i][7] = f3;
        }
    }

    private void handleInt(int n, int n2, float f) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        for (int i = 0; i < 300; i++) {
            float f2 = this.floatValueArray[i][0] * n;
            float f3 = this.floatValueArray[i][1] * n2;
            float f4 = this.floatValueArray[i][2];
            float f5 = this.floatValueArray[i][3];
            float f6 = this.floatValueArray[i][4];
            float f7 = f5 * ((float)Math.sin(f * f6 + i) * 0.3F + 0.7F);
            float f8 = this.floatValueArray[i][5];
            float f9 = this.floatValueArray[i][6];
            float f10 = this.floatValueArray[i][7];
            this.handleFloat(f2, f3, f4 * 3.0F, f8, f9, f10, f7 * 0.3F);
            this.handleFloat(f2, f3, f4, f8, f9, f10, f7);
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        float f2 = (140.0F + 40.0F * f) / 255.0F;
        GL11.glColor4f(0.039215688F, 0.05882353F, 0.09803922F, f2);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n2 + n4);
        GL11.glVertex2f(n + n3, n2 + n4);
        GL11.glVertex2f(n + n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        if (f > 0.1F) {
            float f3 = (60.0F + 150.0F * f) / 255.0F;
            GL11.glColor4f(0.39215687F, 0.78431374F, 1.0F, f3);
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
            int n6 = Math.max(2, n4 / 5);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.39215687F, 0.78431374F, 1.0F, (30.0F + 50.0F * f) / 255.0F);
            GL11.glVertex2f(n, n2);
            GL11.glVertex2f(n + n3, n2);
            GL11.glColor4f(0.39215687F, 0.78431374F, 1.0F, 0.0F);
            GL11.glVertex2f(n + n3, n2 + n6);
            GL11.glVertex2f(n, n2 + n6);
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }

        if (f > 0.2F) {
            long now = System.currentTimeMillis();
            int n7 = (int)(f * 6.0F) + 2;

            for (int i = 0; i < n7; i++) {
                float f4 = (float)(now / (500.0 + i * 60) + i * 1.5 + n5 * 0.3);
                float f5 = f4 % 1.0F;
                int n8 = n + (int)(f5 * (n3 - 16)) + 8;
                int n9 = n2 + (int)((Math.sin(f4 * 2.5 + i) * 0.5 + 0.5) * (n4 - 8)) + 4;
                float f6 = f * 0.86F * (1.0F - Math.abs(f5 - 0.5F) * 2.0F);
                this.handleFloat(n8, n9, 2.0F, 0.7058824F, 0.8627451F, 1.0F, f6);
            }
        }

        GlStateManager.enableTexture2D();
        if (this.drawStringWithShadowComponent != null) {
            int rgb = new Color(180, 210, 230, (int)((0.71F + 0.29F * f) * 255.0F)).getRGB();
            int n10 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
            int n11 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
            this.drawStringWithShadowComponent.drawString(str, n10, n11, rgb);
        }

        GlStateManager.disableBlend();
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (!(f3 <= 0.0F)) {
            GL11.glColor4f(f4, f5, f6, f7);
            int n = Math.max(16, (int)(f3 * 2.0F));
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(f, f2);

            for (int i = 0; i <= n; i++) {
                float f8 = (float)(i * 2 * Math.PI / n);
                GL11.glVertex2f(f + (float)Math.cos(f8) * f3, f2 + (float)Math.sin(f8) * f3);
            }

            GL11.glEnd();
        }
    }

    private void handleFloat2(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (!(f3 <= 0.0F)) {
            for (int i = 0; i < 3; i++) {
                this.handleFloat(f, f2, f3 * (1.0F - i / 3.0F), f4, f5, f6, f7 / 3.0F);
            }
        }
    }

    private void handleInt4(int n, int n2, float f) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        float f2 = Math.min(n, n2);
        this.handleFloat2(
            n * 0.7F + (float)Math.sin(f * 0.1) * 20.0F, n2 * 0.3F + (float)Math.cos(f * 0.15) * 15.0F, f2 * 0.4F, 0.3F, 0.2F, 0.5F, 0.08F
        );
        this.handleFloat2(
            n * 0.3F - (float)Math.sin(f * 0.08) * 25.0F, n2 * 0.7F + (float)Math.cos(f * 0.12) * 20.0F, f2 * 0.32F, 0.5F, 0.2F, 0.4F, 0.06F
        );
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
    }

    @Override
    public String getName() {
        return "Solar System";
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        float f2 = (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F;

        try {
            this.handleInt2(n, n2);
            this.handleInt(n, n2, f2);
            this.handleInt4(n, n2, f2);
            this.handleInt5(n, n2, f2);
            this.handleInt2(n, n2, f2);
            this.handleInt3(n, n2, f2);
            this.handleInt6(n, n2, f2);
            SelectorComponent.handleNearMissMode(SelectorComponent.NearMissMode.PASS_BY, n, n2, 0.0F, 0.0F, 0.0F);
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

    private void handleFloat(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        GL11.glLineWidth(1.0F);
        GL11.glColor4f(f5, f6, f7, f8);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i < 64; i++) {
            float f9 = (float)(i * 2 * Math.PI / 64.0);
            GL11.glVertex2f(f + (float)Math.cos(f9) * f3, f2 + (float)Math.sin(f9) * f4);
        }

        GL11.glEnd();
    }

    private void handleInt3(int n, int n2, float f) {
        float f2 = n * 0.15F;
        float f3 = n2 * 0.25F;
        float f4 = Math.min(n, n2) * 0.4F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();

        for (int i = 0; i < entryArray.length; i++) {
            SolarSystemScene.Entry entry = entryArray[i];
            float f5 = f * entry.factor3 + i * 1.5F;
            float f6 = entry.factor * f4;
            float f7 = f2 + (float)Math.cos(f5) * f6;
            float f8 = f3 + (float)Math.sin(f5) * f6 * 0.6F;
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.handleFloat(f2, f3, f6, f6 * 0.6F, 1.0F, 1.0F, 1.0F, 0.05F);
            Color color = new Color(entry.count);
            float f9 = entry.factor2 * Math.min(n, n2);
            this.handleFloat(f7, f8, f9, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
            float angle = (float)Math.atan2(f3 - f8, f2 - f7);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            this.handleFloat(
                f7 + (float)Math.cos(angle) * f9 * 0.3F,
                f8 + (float)Math.sin(angle) * f9 * 0.3F,
                f9 * 0.7F,
                Math.min(1.0F, color.getRed() / 255.0F + 0.3F),
                Math.min(1.0F, color.getGreen() / 255.0F + 0.3F),
                Math.min(1.0F, color.getBlue() / 255.0F + 0.3F),
                0.4F
            );
            this.handleFloat2(
                f7, f8, f9 * 1.3F, color.getRed() / 255.0F * 0.5F, color.getGreen() / 255.0F * 0.5F, color.getBlue() / 255.0F * 0.7F, 0.15F
            );
            if (entry.flag) {
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.handleFloat(f7, f8, f9, entry.factor4, entry.factor5);
            }
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
    }

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.timestamp = System.currentTimeMillis();
        this.generateStars();
        this.process();
    }

    private void process() {
        this.floatValueArray2 = new float[3][5];

        for (int i = 0; i < 3; i++) {
            this.floatValueArray2[i][0] = (float)Math.random();
            this.floatValueArray2[i][1] = (float)Math.random() * 0.5F;
            this.floatValueArray2[i][2] = 8.0E-4F + (float)Math.random() * 6.0E-4F;
            this.floatValueArray2[i][3] = (float)(Math.random() * 6.0);
            this.floatValueArray2[i][4] = 4.0F + (float)Math.random() * 8.0F;
        }
    }

    public SolarSystemScene() {
        this.generateStars();
        this.process();
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, float f) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = sr.getScaleFactor();
        int scaledHeight = sr.getScaledHeight();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(n * scaleFactor, (scaledHeight - n2 - n4) * scaleFactor, n3 * scaleFactor, n4 * scaleFactor);
        this.handleInt(sr.getScaledWidth(), sr.getScaledHeight(), f, 0, 0);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public String getDescription() {
        return "Realistic space environment with orbiting planets";
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5) {
        float f6 = f3 * 1.4F;
        float f7 = f3 * f5;
        GL11.glLineWidth(1.2F);

        for (int i = 0; i < 4; i++) {
            float f8 = i / 3.0F;
            float f9 = f6 + (f7 - f6) * f8;
            GL11.glColor4f(0.85F, 0.8F, 0.75F, 0.25F - f8 * 0.12F);
            GL11.glBegin(GL11.GL_LINE_LOOP);

            for (int j = 0; j < 60; j++) {
                float f10 = (float)(j * 2 * Math.PI / 60.0);
                GL11.glVertex2f(f + (float)Math.cos(f10) * f9, f2 + (float)Math.sin(f10) * f9 * (float)Math.sin(f4));
            }

            GL11.glEnd();
        }

        GL11.glLineWidth(1.0F);
    }

    private void handleInt5(int n, int n2, float f) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        for (int i = 0; i < 3; i++) {
            float f2 = this.floatValueArray2[i][2];
            float f3 = (f * f2 * 100.0F + this.floatValueArray2[i][3]) % 8.0F;
            if (!(f3 > 1.6F)) {
                float f4 = f3 / 1.6F;
                float f5 = this.floatValueArray2[i][0] * n;
                float f6 = this.floatValueArray2[i][1] * n2;
                float f7 = -0.8F + i * 0.3F;
                float f8 = Math.min(n, n2) * 0.6F * f4;
                float f9 = f5 + (float)Math.cos(f7) * f8;
                float f10 = f6 + (float)Math.sin(f7) * f8;
                float f11 = this.floatValueArray2[i][4];
                float sin = (float)Math.sin(f4 * Math.PI);

                for (int j = 0; j < 12; j++) {
                    float f12 = j / 12.0F;
                    float f13 = f9 - (float)Math.cos(f7) * f11 * f12 * 4.0F;
                    float f14 = f10 - (float)Math.sin(f7) * f11 * f12 * 4.0F;
                    this.handleFloat(f13, f14, (1.4F - f12) * 1.3F, 1.0F, 0.95F, 0.8F, sin * (1.0F - f12) * 0.6F);
                }

                this.handleFloat(f9, f10, 2.3F, 1.0F, 1.0F, 0.9F, sin);
                this.handleFloat(f9, f10, 5.0F, 1.0F, 0.9F, 0.7F, sin * 0.35F);
            }
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
    }

    private void handleInt2(int n, int n2) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(0.019607844F, 0.019607844F, 0.05882353F, 1.0F);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glVertex2f(n, 0.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.019607844F, 1.0F);
        GL11.glVertex2f(n, n2);
        GL11.glVertex2f(0.0F, n2);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private void handleInt6(int n, int n2, float f) {
        float f2 = n * 0.15F;
        float f3 = n2 * 0.25F;
        float f4 = Math.min(n, n2) * 0.38F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < 80; i++) {
            float f5 = (float)(i * 2 * Math.PI / 80.0 + f * 0.1 + i * 0.5);
            float f6 = (float)Math.sin(i * 13.0) * 0.1F + 1.0F;
            float f7 = f2 + (float)Math.cos(f5) * f4 * f6;
            float f8 = f3 + (float)Math.sin(f5) * f4 * 0.6F * f6;
            float f9 = 0.5F + (float)Math.sin(i * 7.0) * 0.3F + 0.5F;
            this.handleFloat(f7, f8, f9, 0.6F, 0.5F, 0.5F, 0.4F);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static final class Entry {
        public final float factor;
        public final float factor2;
        public final float factor3;
        public final float factor4;
        public final float factor5;
        public final int count;
        public final boolean flag;

        public Entry(float factor, float f, float f2, int count, boolean flag, float f3, float f4) {
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.count = count;
            this.flag = flag;
            this.factor4 = f3;
            this.factor5 = f4;
        }
    }
}
