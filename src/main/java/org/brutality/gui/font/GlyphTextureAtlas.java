package org.brutality.gui.font;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class GlyphTextureAtlas {
    public final boolean flag;
    private final List<GlyphTextureAtlas.Entry> entries = new ArrayList<>();
    public final int count;

    public void close() {
        for (GlyphTextureAtlas.Entry entry : this.entries) {
            GlStateManager.deleteTexture(entry.count);
        }

        this.entries.clear();
    }

    public GlyphTexture computeDeleteTextureComponent(BufferedImage image, Rectangle rect) {
        int n = this.flag ? 16 : 2;
        int n2 = 1;

        while (n2 < Math.max(image.getWidth(), image.getHeight()) + n * 2) {
            n2 <<= 1;
        }

        GlyphTextureAtlas.Entry entry = null;

        for (GlyphTextureAtlas.Entry entry2 : this.entries) {
            if (entry2.count2 == n2 && entry2.count8 < entry2.count3 * entry2.count4) {
                entry = entry2;
                break;
            }
        }

        int n3 = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        GlyphTexture deleteTextureComponent;
        try {
            if (entry == null) {
                entry = new GlyphTextureAtlas.Entry(n2);
                this.entries.add(entry);
            }

            int n4 = entry.count8++;
            int n5 = n4 % entry.count3 * n2;
            int n6 = n4 / entry.count3 * n2;
            GlStateManager.bindTexture(entry.count);
            if (this.flag) {
                int[] ints = new int[n2 * n2];

                for (int i = 0; i < image.getHeight(); i++) {
                    for (int j = 0; j < image.getWidth(); j++) {
                        ints[(i + n) * n2 + j + n] = image.getRGB(j, i) >>> 24;
                    }
                }

                int n7 = n2;

                for (int k = 0; k <= entry.count7; k++) {
                    ByteBuffer buffer = BufferUtils.createByteBuffer(ints.length);

                    for (int n8 : ints) {
                        buffer.put((byte)n8);
                    }

                    buffer.flip();
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, k, n5 >> k, n6 >> k, n7, n7, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, buffer);
                    if (k == entry.count7) {
                        break;
                    }

                    int n9 = n7 / 2;
                    int[] ints2 = new int[n9 * n9];

                    for (int idx = 0; idx < n9; idx++) {
                        for (int idx2 = 0; idx2 < n9; idx2++) {
                            int n10 = idx * 2 * n7 + idx2 * 2;
                            ints2[idx * n9 + idx2] = (ints[n10] + ints[n10 + 1] + ints[n10 + n7] + ints[n10 + n7 + 1] + 2) / 4;
                        }
                    }

                    ints = ints2;
                    n7 = n9;
                }
            } else {
                ByteBuffer buffer2 = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

                for (int idx3 = 0; idx3 < image.getHeight(); idx3++) {
                    for (int idx4 = 0; idx4 < image.getWidth(); idx4++) {
                        int rgb = image.getRGB(idx4, idx3);
                        buffer2.put((byte)(rgb >> 16)).put((byte)(rgb >> 8)).put((byte)rgb).put((byte)(rgb >>> 24));
                    }
                }

                buffer2.flip();
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, n5 + n, n6 + n, image.getWidth(), image.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer2);
            }

            deleteTextureComponent = new GlyphTexture(
                entry.count,
                image.getWidth(),
                image.getHeight(),
                rect,
                (float)(n5 + n) / entry.count5,
                (float)(n6 + n) / entry.count6,
                (float)(n5 + n + image.getWidth()) / entry.count5,
                (float)(n6 + n + image.getHeight()) / entry.count6
            );
        } finally {
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, n3);
        }

        return deleteTextureComponent;
    }

    public GlyphTextureAtlas(boolean flag) {
        this.flag = flag;
        this.count = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    public final class Entry {
        public final int count;
        public final int count2;
        public final int count3;
        public final int count4;
        public final int count5;
        public final int count6;
        public final int count7;
        public int count8;

        public Entry(int n) {
            if (n > GlyphTextureAtlas.this.count) {
                throw new IllegalArgumentException("Font glyph exceeds maximum texture size");
            }
            this.count2 = n;
            this.count3 = Math.min(16, GlyphTextureAtlas.this.count / n);
            this.count4 = Math.min(8, GlyphTextureAtlas.this.count / n);
            this.count5 = this.count3 * n;
            this.count6 = this.count4 * n;
            this.count7 = GlyphTextureAtlas.this.flag ? Math.min(4, Integer.numberOfTrailingZeros(n)) : 0;
            this.count = GL11.glGenTextures();
            GlStateManager.bindTexture(this.count);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GlyphTextureAtlas.this.flag ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GlyphTextureAtlas.this.flag ? GL11.GL_LINEAR : GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, this.count7);

            for (int i = 0; i <= this.count7; i++) {
                int n2 = this.count5 >> i;
                int n3 = this.count6 >> i;
                GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D,
                    i,
                    GlyphTextureAtlas.this.flag ? GL11.GL_ALPHA8 : GL11.GL_RGBA,
                    n2,
                    n3,
                    0,
                    GlyphTextureAtlas.this.flag ? GL11.GL_ALPHA : GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE,
                    BufferUtils.createByteBuffer(n2 * n3 * (GlyphTextureAtlas.this.flag ? 1 : 4))
                );
            }
        }
    }
}
