package org.brutality.gui.font.atlas;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.gui.font.api.DrawStringWithShadowApi;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class AtlasFontRenderer implements DrawStringWithShadowApi {
    private static final char charVal = 167;
    private static final Random random = new Random();
    private static final int count = 24581;
    private static final int count2 = 6;
    private static final int count3 = 2;
    private static final int count4 = 512;
    private static final int count5 = 2;
    private final Font font;
    private final float factor;
    private final boolean flag;
    private final boolean flag2;
    private final AtlasFontRenderer.Entry[] entryArray = new AtlasFontRenderer.Entry[256];
    private final int[] intValueArray = new int[32];
    private int count6 = -1;
    private int atlasHeight;
    private int count7 = 2;
    private static final String text = "0123456789ABCDEFKLMNOR";

    @Override
    public float computeFloat(String str) {
        if (str != null && !str.isEmpty()) {
            float f = 0.0F;
            int n = 0;

            for (int i = str.length(); n < i; n++) {
                char ch = str.charAt(n);
                if (ch != 167 && (n > 0 ? str.charAt(n - 1) : 46) != 167 && this.matchesChar(ch)) {
                    AtlasFontRenderer.Entry entry = this.entryArray[ch];
                    if (entry != null) {
                        f += (float)entry.count4 / this.count7;
                    }
                }
            }

            return f;
        }
        return 0.0F;
    }

    private boolean matchesChar(char ch) {
        return ch > '\n' && ch < 256 && ch != 127;
    }

    private int computeInt() {
        try {
            return new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        } catch (Exception e) {
            return this.count7;
        }
    }

    @Override
    public int computeInt(String str, float f, float f2, int n) {
        this.process3();
        this.handleString2(str, f, f2, n, false);
        return 0;
    }

    private void handleString2(String str, float f, float f2, int n, boolean flag) {
        if (str != null && !str.isEmpty() && this.count6 != -1) {
            if ((-67108864 | ~n) - ~n == 0) {
                n |= -16777216;
            }

            if (n == 553648127) {
                n = -5263441;
            }

            int n2 = this.count7;
            GL11.glPushMatrix();
            GL11.glPushAttrib(24581);
            int n3 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glScaled(1.0 / n2, 1.0 / n2, 1.0);
            float f3 = f * n2 - 6.0F;
            float f4 = (f2 - 2.0F) * n2;
            float f5 = flag ? 0.25F : 1.0F;
            float f6 = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
            float f7 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F * f5;
            float f8 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F * f5;
            float f9 = ((0xFF | ~n) - ~n) / 255.0F * f5;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;
            float f10 = f7;
            float f11 = f8;
            float f12 = f9;
            java.util.List<float[]> list = (java.util.List<float[]>)(Object)(null);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.count6);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(f7, f8, f9, f6);
            int n4 = 0;

            for (int i = str.length(); n4 < i; n4++) {
                char ch = str.charAt(n4);
                char ch2 = n4 > 0 ? str.charAt(n4 - 1) : 46;
                if (ch2 != 167) {
                    if (ch == 167 && n4 + 1 < i) {
                        int n5 = text.indexOf(Character.toUpperCase(str.charAt(n4 + 1)));
                        if (n5 < 0) {
                            n5 = 21;
                        }

                        if (n5 < 16) {
                            flag2 = false;
                            flag3 = false;
                            flag4 = false;
                            int n6 = this.intValueArray[flag ? n5 + 16 : n5];
                            f10 = (n6 >> 16 & 0xFF) / 255.0F;
                            f11 = ((0xFF | ~(n6 >> 8)) - ~(n6 >> 8)) / 255.0F;
                            f12 = (n6 & 0xFF) / 255.0F;
                            GL11.glColor4f(f10, f11, f12, f6);
                        } else if (n5 == 16) {
                            flag4 = true;
                        } else if (n5 == 18) {
                            flag3 = true;
                        } else if (n5 == 19) {
                            flag2 = true;
                        } else if (n5 == 21) {
                            flag2 = false;
                            flag3 = false;
                            flag4 = false;
                            f10 = f7;
                            f11 = f8;
                            f12 = f9;
                            GL11.glColor4f(f7, f8, f9, f6);
                        }
                    } else if (this.matchesChar(ch)) {
                        char ch3 = ch;
                        if (flag4) {
                            ch3 = this.computeChar(ch);
                        }

                        AtlasFontRenderer.Entry entry = this.entryArray[ch3];
                        if (entry != null) {
                            this.handleEntry(entry, f3, f4);
                            if (flag3 || flag2) {
                                if (list == null) {
                                    list = new ArrayList();
                                }

                                if (flag3) {
                                    list.add(
                                        new float[]{
                                            f3, f4 + entry.count3 / 2.0F, f3 + entry.count2, f4 + entry.count3 / 2.0F, f10, f11, f12, f6
                                        }
                                    );
                                }

                                if (flag2) {
                                    list.add(
                                        new float[]{
                                            f3, f4 + entry.count3 - 15.0F, f3 + entry.count2, f4 + entry.count3 - 15.0F, f10, f11, f12, f6
                                        }
                                    );
                                }
                            }

                            f3 += entry.count4;
                        }
                    }
                }
            }

            GL11.glEnd();
            if (list != null) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glLineWidth(3.0F);
                GL11.glBegin(GL11.GL_LINES);

                for (float[] floats : list) {
                    GL11.glColor4f(floats[4], floats[5], floats[6], floats[7]);
                    GL11.glVertex2f(floats[0], floats[1]);
                    GL11.glVertex2f(floats[2], floats[3]);
                }

                GL11.glEnd();
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, n3);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private char computeChar(char ch) {
        for (int i = 0; i < 8; i++) {
            char ch2 = (char)(ch + random.nextInt(Math.max(1, 256 - ch)));
            if (this.matchesChar(ch2) && this.entryArray[ch2] != null) {
                return ch2;
            }
        }

        return ch;
    }

    private int computeInt(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] rgb = image.getRGB(0, 0, width, height, null, 0, width);
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int n : rgb) {
            buffer.put((byte)(n >> 16 & 0xFF));
            buffer.put((byte)(n >> 8 & 0xFF));
            buffer.put((byte)(n & 0xFF));
            buffer.put((byte)(n >> 24 & 0xFF));
        }

        buffer.flip();
        int n2 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int n3 = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, n3);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, n2);
        return n3;
    }

    private void handleGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, this.flag ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        );
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.flag ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS, this.flag2 ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        );
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public void handleString(String str, float f, float f2, int n, boolean flag) {
        this.process3();
        this.handleString2(str, f, f2, n, flag);
    }

    @Override
    public int drawStringWithShadow(String str, float f, float f2, int n) {
        this.process3();
        this.handleString2(str, f + 0.5F, f2 + 0.5F, n, true);
        this.handleString2(str, f, f2, n, false);
        return 0;
    }

    public void process2() {
        this.handleInt(this.computeInt());
    }

    private void process() {
        for (int i = 0; i < 32; i++) {
            int n = (i >> 3 & 1) * 85;
            int n2 = (i >> 2 & 1) * 170 + n;
            int n3 = (i >> 1 & 1) * 170 + n;
            int n4 = (i & 1) * 170 + n;
            if (i == 6) {
                n2 += 85;
            }

            if (i >= 16) {
                n2 /= 4;
                n3 /= 4;
                n4 /= 4;
            }

            this.intValueArray[i] = (0xFF | ~n4) - ~n4 + (((0xFF | ~n2) - ~n2 << 16 | (0xFF | ~n3) - ~n3 << 8) & ~((0xFF | ~n4) - ~n4));
        }
    }

    @Override
    public float computeFloat2(String str) {
        if (str != null && !str.isEmpty()) {
            float f = 0.0F;
            int n = 0;

            for (int i = str.length(); n < i; n++) {
                char ch = str.charAt(n);
                if (ch != 167 && (n > 0 ? str.charAt(n - 1) : 46) != 167 && this.matchesChar(ch)) {
                    AtlasFontRenderer.Entry entry = this.entryArray[ch];
                    if (entry != null) {
                        f = Math.max(f, (float)entry.count3);
                    }
                }
            }

            return (f - 6.0F) / this.count7;
        }
        return 0.0F;
    }

    private void handleEntry(AtlasFontRenderer.Entry entry, float f, float f2) {
        float f3 = entry.bus / 512.0F;
        float f4 = (float)entry.count / this.atlasHeight;
        float f5 = (entry.bus + entry.count2) / 512.0F;
        float f6 = (float)(entry.count + entry.count3) / this.atlasHeight;
        GL11.glTexCoord2f(f3, f4);
        GL11.glVertex2f(f, f2);
        GL11.glTexCoord2f(f3, f6);
        GL11.glVertex2f(f, f2 + entry.count3);
        GL11.glTexCoord2f(f5, f6);
        GL11.glVertex2f(f + entry.count2, f2 + entry.count3);
        GL11.glTexCoord2f(f5, f4);
        GL11.glVertex2f(f + entry.count2, f2);
    }

    private void handleInt(int n) {
        if (n <= 0) {
            n = 2;
        }

        Font font = this.font.deriveFont(this.factor * n / 2.0F);
        BufferedImage image = new BufferedImage(1, 1, 2);
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        this.handleGraphics2D(g2d);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int ascent = fontMetrics.getAscent();
        AtlasFontRenderer.Entry[] entries = new AtlasFontRenderer.Entry[256];
        int n2 = 2;
        int n3 = 2;
        int n4 = 0;

        for (int i = 0; i < 256; i++) {
            char ch = (char)i;
            if (this.matchesChar(ch)) {
                Rectangle2D stringBounds = fontMetrics.getStringBounds(String.valueOf(ch), g2d);
                int n5 = (int)StrictMath.ceil(stringBounds.getWidth()) + 12;
                int n6 = (int)StrictMath.ceil(stringBounds.getHeight());
                if (n5 > 0 && n6 > 0) {
                    if (n2 + n5 + 2 > 512) {
                        n2 = 2;
                        n3 += n4 + 2;
                        n4 = 0;
                    }

                    entries[ch] = new AtlasFontRenderer.Entry(n2, n3, n5, n6);
                    n2 += n5 + 2;
                    n4 = Math.max(n4, n6);
                }
            }
        }

        g2d.dispose();
        int n7 = nextPowerOfTwo(n3 + n4 + 2);
        BufferedImage image2 = new BufferedImage(512, n7, 2);
        Graphics2D g2d2 = image2.createGraphics();
        g2d2.setFont(font);
        this.handleGraphics2D(g2d2);
        g2d2.setColor(Color.WHITE);

        for (int j = 0; j < 256; j++) {
            AtlasFontRenderer.Entry entry = entries[j];
            if (entry != null) {
                g2d2.drawString(String.valueOf((char)j), entry.bus + 6, entry.count + ascent);
            }
        }

        g2d2.dispose();
        int n8 = this.computeInt(image2);
        int n9 = this.count6;
        System.arraycopy(entries, 0, this.entryArray, 0, 256);
        this.atlasHeight = n7;
        this.count6 = n8;
        this.count7 = n;
        if (n9 != -1) {
            GL11.glDeleteTextures(n9);
        }
    }

    private static int nextPowerOfTwo(int n) {
        int n2 = 1;

        while (n2 < n) {
            n2 <<= 1;
        }

        return n2;
    }

    private void process3() {
        int n = this.computeInt();
        if (n > 0 && n != this.count7) {
            this.handleInt(n);
        }
    }

    public AtlasFontRenderer(Font font, boolean flag, boolean flag2) {
        this.font = font;
        this.factor = font.getSize2D();
        this.flag = flag;
        this.flag2 = flag2;
        this.process();
    }

    public static final class Entry {
        public final int bus;
        public final int count;
        public final int count2;
        public final int count3;
        public final int count4;

        public Entry(int bus, int count, int n, int n2) {
            this.bus = bus;
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
            this.count4 = n - 12;
        }
    }
}
