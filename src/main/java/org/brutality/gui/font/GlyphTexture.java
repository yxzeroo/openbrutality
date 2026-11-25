package org.brutality.gui.font;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GlyphTexture {
    public int count;
    private float width;
    private float height;
    private Rectangle rectangle;
    private float factor;
    private float factor2;
    private float factor3 = 1.0F;
    private float factor4 = 1.0F;
    private boolean flag = true;

    public int getCount() {
        return this.count;
    }

    public static Rectangle computeRectangle(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int n = -1;
        int n2 = -1;

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (image.getRGB(j, i) >>> 24 >= 8) {
                    width = Math.min(width, j);
                    n = Math.max(n, j);
                    height = Math.min(height, i);
                    n2 = Math.max(n2, i);
                }
            }
        }

        return n < width ? new Rectangle() : new Rectangle(width, height, n - width + 1, n2 - height + 1);
    }

    public float getHeight() {
        return this.height;
    }

    public void handleFloat2(float f, float f2) {
        GlStateManager.bindTexture(this.count);
        GL11.glBegin(GL11.GL_QUADS);
        this.handleFloat(f, f2);
        GL11.glEnd();
    }

    public GlyphTexture(int n, float f, float f2, Rectangle rect, float factor, float f3, float f4, float f5) {
        this(n, f, f2, rect);
        this.factor = factor;
        this.factor2 = f3;
        this.factor3 = f4;
        this.factor4 = f5;
        this.flag = false;
    }

    public GlyphTexture(int n, float f, float f2, Rectangle rectangle) {
        this(n, f, f2);
        this.rectangle = rectangle;
    }

    public GlyphTexture(int count, float width, float height) {
        this.count = count;
        this.width = width;
        this.height = height;
        this.rectangle = new Rectangle(0, 0, (int)width, (int)height);
    }

    public void deleteTexture() {
        if (this.flag && this.count != 0) {
            GlStateManager.deleteTexture(this.count);
        }

        this.count = 0;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public void handleFloat(float f, float f2) {
        GL11.glTexCoord2f(this.factor, this.factor2);
        GL11.glVertex2f(f, f2);
        GL11.glTexCoord2f(this.factor, this.factor4);
        GL11.glVertex2f(f, f2 + this.height);
        GL11.glTexCoord2f(this.factor3, this.factor4);
        GL11.glVertex2f(f + this.width, f2 + this.height);
        GL11.glTexCoord2f(this.factor3, this.factor2);
        GL11.glVertex2f(f + this.width, f2);
    }

    public float getWidth() {
        return this.width;
    }

    public static final class Entry implements AutoCloseable {
        private int count = -1;

        public void handleDeleteTextureComponent(GlyphTexture deleteTextureComponent, float f, float f2) {
            if (this.count != deleteTextureComponent.count) {
                if (this.count != -1) {
                    GL11.glEnd();
                }

                this.count = deleteTextureComponent.count;
                GlStateManager.bindTexture(this.count);
                GL11.glBegin(GL11.GL_QUADS);
            }

            deleteTextureComponent.handleFloat(f, f2);
        }

        @Override
        public void close() {
            if (this.count != -1) {
                GL11.glEnd();
                this.count = -1;
            }
        }
    }
}
