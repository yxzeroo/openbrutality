package org.brutality.gui.font.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.brutality.gui.font.api.BrutalityFont;
import org.lwjgl.opengl.GL11;

public final class FilterComponent implements BrutalityFont {
    private static final int[] intValueArray = computeIntArray();
    private static final String text = "0123456789abcdefklmnor";
    private static final char charVal = 167;
    private static final int count = 256;
    private static final int buy = 512;
    private static final int count2 = 4096;
    private static Function<CharSequence, CharSequence> function = charSequence -> charSequence;
    private final Font font;
    private final boolean flag;
    private final boolean flag2;
    private final FilterComponent.Entry entry;
    private FilterComponent.Entry entry2;
    private FilterComponent.Entry entry3;
    private FilterComponent.Entry entry4;
    private final int height;

    public static void filter(Function<CharSequence, CharSequence> fn) {
        function = fn == null ? charSequence -> charSequence : fn;
    }

    @Override
    public int getHeight() {
        return (this.height - 8) / 2;
    }

    private int computeInt(Font font) {
        BufferedImage image = new BufferedImage(1, 1, 2);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setFont(font);
        this.handleGraphics2D(graphics);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int n = -1;

        for (int i = 0; i < 256; i++) {
            int height = fontMetrics.getStringBounds(String.valueOf((char)i), graphics).getBounds().height;
            if (height > n) {
                n = height;
            }
        }

        graphics.dispose();
        return n;
    }

    private static void handleFloat(float f, float f2, FilterComponent.Entry2 entry2, int n) {
        float f3 = (float)entry2.count / n;
        float f4 = (float)entry2.count2 / n;
        float f5 = (float)entry2.width / n;
        float f6 = (float)entry2.height / n;
        GL11.glTexCoord2f(f3 + f5, f4);
        GL11.glVertex2d(f + entry2.width, f2);
        GL11.glTexCoord2f(f3, f4);
        GL11.glVertex2d(f, f2);
        GL11.glTexCoord2f(f3, f4 + f6);
        GL11.glVertex2d(f, f2 + entry2.height);
        GL11.glTexCoord2f(f3, f4 + f6);
        GL11.glVertex2d(f, f2 + entry2.height);
        GL11.glTexCoord2f(f3 + f5, f4 + f6);
        GL11.glVertex2d(f + entry2.width, f2 + entry2.height);
        GL11.glTexCoord2f(f3 + f5, f4);
        GL11.glVertex2d(f + entry2.width, f2);
    }

    @Override
    public float computeFloat(char ch) {
        FilterComponent.Entry2 entry2 = ch < 256 ? this.entry.entry2Array[ch] : null;
        return entry2 == null ? 0.0F : (entry2.width - 8) / 2.0F;
    }

    @Override
    public int computeInt(CharSequence charSequence) {
        if (charSequence == null) {
            return 0;
        }
        CharSequence charSequence2 = function.apply(charSequence);
        if (charSequence2 == null) {
            return 0;
        }
        int n = 0;
        FilterComponent.Entry entry = this.entry;
        boolean flag = false;
        boolean flag2 = false;
        int n2 = 0;

        for (int i = charSequence2.length(); n2 < i; n2++) {
            char ch = charSequence2.charAt(n2);
            if (ch == 167 && n2 + 1 < i) {
                int n3 = "0123456789abcdefklmnor".indexOf(charSequence2.charAt(n2 + 1));
                if (n3 < 16) {
                    flag = false;
                    flag2 = false;
                    entry = this.entry;
                } else if (n3 == 17) {
                    flag = true;
                    entry = flag2 ? this.computeEntry3() : this.computeEntry2();
                } else if (n3 == 20) {
                    flag2 = true;
                    entry = flag ? this.computeEntry3() : this.computeEntry();
                } else if (n3 == 21) {
                    flag = false;
                    flag2 = false;
                    entry = this.entry;
                }

                n2++;
            } else if (ch < 256 && entry.entry2Array[ch] != null) {
                n += entry.entry2Array[ch].width - (ch == ' ' ? 8 : 9);
            }
        }

        return n / 2;
    }

    private float computeFloat2(CharSequence charSequence, double d, double d2, int n, boolean flag) {
        if (charSequence == null) {
            return 0.0F;
        }
        CharSequence charSequence2 = function.apply(charSequence);
        if (charSequence2 == null) {
            return 0.0F;
        }
        d--;
        if (n == 553648127) {
            n = 16777215;
        }

        if ((n & -67108864) == 0) {
            n |= -16777216;
        }

        if (flag) {
            n = (n & 16579836) >> 2 | n & 0xFF000000;
        }

        FilterComponent.Entry entry = this.entry;
        float f = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
        double d3 = d * 2.0;
        double d4 = (d2 - 3.0) * 2.0;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, f);
        GlStateManager.color((n >> 16 & 0xFF) / 255.0F, ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F, (n & 0xFF) / 255.0F, f);
        GlStateManager.enableTexture2D();
        handleEntry(entry);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 9729.0F);
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean flag5 = false;
        boolean flag6 = false;
        int n2 = 0;

        for (int i = charSequence2.length(); n2 < i; n2++) {
            char ch = charSequence2.charAt(n2);
            if (ch == 167 && n2 + 1 < i) {
                int n3 = "0123456789abcdefklmnor".indexOf(charSequence2.charAt(n2 + 1));
                if (flag6) {
                    GL11.glEnd();
                    flag6 = false;
                }

                if (n3 < 16) {
                    flag5 = false;
                    flag4 = false;
                    flag2 = false;
                    flag3 = false;
                    entry = this.entry;
                    handleEntry(entry);
                    if (n3 < 0) {
                        n3 = 15;
                    }

                    if (flag) {
                        n3 += 16;
                    }

                    int n4 = intValueArray[n3];
                    GlStateManager.color((n4 >> 16 & 0xFF) / 255.0F, (n4 >> 8 & 0xFF) / 255.0F, (n4 & 0xFF) / 255.0F, f);
                } else if (n3 == 17) {
                    flag5 = true;
                    entry = flag4 ? this.computeEntry3() : this.computeEntry2();
                    handleEntry(entry);
                } else if (n3 == 18) {
                    flag3 = true;
                } else if (n3 == 19) {
                    flag2 = true;
                } else if (n3 == 20) {
                    flag4 = true;
                    entry = flag5 ? this.computeEntry3() : this.computeEntry();
                    handleEntry(entry);
                } else if (n3 == 21) {
                    flag5 = false;
                    flag4 = false;
                    flag2 = false;
                    flag3 = false;
                    entry = this.entry;
                    handleEntry(entry);
                    GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, ((0xFF | ~n) - ~n) / 255.0F, f);
                }

                n2++;
            } else if (ch < 256 && entry.entry2Array[ch] != null) {
                FilterComponent.Entry2 entry2 = entry.entry2Array[ch];
                if (!flag6) {
                    GL11.glBegin(GL11.GL_TRIANGLES);
                    flag6 = true;
                }

                handleFloat((float)d3, (float)d4, entry2, entry.size);
                if (flag3 || flag2) {
                    GL11.glEnd();
                    flag6 = false;
                    if (flag3) {
                        handleDouble(d3, d4 + entry2.height / 2.0F, d3 + entry2.width - 8.0, d4 + entry2.height / 2.0F);
                    }

                    if (flag2) {
                        handleDouble(d3, d4 + entry2.height - 2.0, d3 + entry2.width - 8.0, d4 + entry2.height - 2.0);
                    }

                    handleEntry(entry);
                }

                d3 += entry2.width - (ch == ' ' ? 8 : 9);
            }
        }

        if (flag6) {
            GL11.glEnd();
        }

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 9728.0F);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glPopMatrix();
        return (float)d3 / 2.0F;
    }

    private void handleGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, this.flag ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        );
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.flag ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS, this.flag2 ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        );
    }

    private static void handleDouble(double d, double d2, double d3, double d4) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(d, d2);
        GL11.glVertex2d(d3, d4);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private BufferedImage computeBufferedImage(Font font, FilterComponent.Entry entry, int n) {
        BufferedImage image = new BufferedImage(n, n, 2);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setFont(font);
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, n, n);
        graphics.setColor(Color.WHITE);
        this.handleGraphics2D(graphics);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int n2 = 0;
        int n3 = 0;
        int n4 = 1;

        for (int i = 0; i < 256; i++) {
            char ch = (char)i;
            FilterComponent.Entry2 entry2 = new FilterComponent.Entry2();
            Rectangle2D stringBounds = fontMetrics.getStringBounds(String.valueOf(ch), graphics);
            entry2.width = stringBounds.getBounds().width + 8;
            entry2.height = stringBounds.getBounds().height;
            if (entry2.width >= n) {
                graphics.dispose();
                return null;
            }

            if (n3 + entry2.width >= n) {
                n3 = 0;
                n4 += n2;
                n2 = 0;
            }

            if (entry2.height > n2) {
                n2 = entry2.height;
            }

            if (n4 + entry2.height >= n) {
                graphics.dispose();
                return null;
            }

            entry2.count = n3;
            entry2.count2 = n4;
            if (entry2.height > entry.height) {
                entry.height = entry2.height;
            }

            entry.entry2Array[i] = entry2;
            graphics.drawString(String.valueOf(ch), n3 + 2, n4 + fontMetrics.getAscent());
            n3 += entry2.width;
        }

        graphics.dispose();
        return image;
    }

    private static int[] computeIntArray() {
        int[] ints = new int[32];

        for (int i = 0; i < 32; i++) {
            int n = (i >> 3 & 1) * 85;
            int n2 = (i >> 2 & 1) * 170 + n;
            int n3 = (i >> 1 & 1) * 170 + n;
            int n4 = (i & 1) * 170 + n;
            if (i == 6) {
                n2 += 85;
            }

            if (i >= 16) {
                n2 >>= 2;
                n3 >>= 2;
                n4 >>= 2;
            }

            ints[i] = (n4 & 0xFF) + (((n2 & 0xFF) << 16 | (0xFF | ~n3) - ~n3 << 8) & ~(n4 & 0xFF));
        }

        return ints;
    }

    public static BrutalityFont computeApiApi(Font font) {
        return computeApiApi(font, true, true);
    }

    @Override
    public boolean isFlag2() {
        return this.flag2;
    }

    private static void handleEntry(FilterComponent.Entry entry) {
        if (entry.texture != null) {
            GlStateManager.bindTexture(entry.texture.getGlTextureId());
            GlStateManager.bindTexture(entry.texture.getGlTextureId());
        }
    }

    private FilterComponent.Entry computeEntry() {
        if (this.entry3 == null) {
            this.entry3 = this.computeEntry(this.font.deriveFont(2));
        }

        return this.entry3;
    }

    private FilterComponent(Font font, boolean flag, boolean flag2) {
        this.font = font;
        this.flag = flag;
        this.flag2 = flag2;
        this.entry = this.computeEntry(font);
        this.height = Math.max(
            Math.max(this.computeInt(font), this.computeInt(font.deriveFont(1))),
            Math.max(this.computeInt(font.deriveFont(2)), this.computeInt(font.deriveFont(3)))
        );
    }

    @Override
    public float computeFloat(CharSequence charSequence, double d, double d2, int n, boolean flag) {
        if (flag) {
            float f = this.computeFloat2(charSequence, d + 0.5, d2 + 0.5, n, true);
            return Math.max(f, this.computeFloat2(charSequence, d, d2, n, false));
        }
        return this.computeFloat2(charSequence, d, d2, n, false);
    }

    private FilterComponent.Entry computeEntry3() {
        if (this.entry4 == null) {
            this.entry4 = this.computeEntry(this.font.deriveFont(3));
        }

        return this.entry4;
    }

    public static BrutalityFont computeApiApi(Font font, boolean flag, boolean flag2) {
        return new FilterComponent(font, flag, flag2);
    }

    private FilterComponent.Entry computeEntry(Font font) {
        FilterComponent.Entry entry = new FilterComponent.Entry();

        for (int i = 512; i <= 4096; i *= 2) {
            BufferedImage image = this.computeBufferedImage(font, entry, i);
            if (image != null) {
                entry.size = i;
                entry.texture = new DynamicTexture(image);
                return entry;
            }
        }

        return entry;
    }

    @Override
    public String computeString(CharSequence charSequence, int n, boolean flag) {
        StringBuilder sb = new StringBuilder();
        float f = 0.0F;
        int n2 = flag ? charSequence.length() - 1 : 0;
        int n3 = flag ? -1 : 1;
        boolean flag2 = false;
        boolean flag3 = false;

        for (int i = n2; i >= 0 && i < charSequence.length() && f < n; i += n3) {
            char ch = charSequence.charAt(i);
            float f2 = this.computeInt(String.valueOf(ch));
            if (flag2) {
                flag2 = false;
                if (ch == 'l' || ch == 'L') {
                    flag3 = true;
                } else if (ch == 'r' || ch == 'R') {
                    flag3 = false;
                }
            } else if (f2 < 0.0F) {
                flag2 = true;
            } else {
                f += f2;
                if (flag3) {
                    f++;
                }
            }

            if (f > n) {
                break;
            }

            if (flag) {
                sb.insert(0, ch);
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    @Override
    public boolean checkState() {
        return this.flag;
    }

    @Override
    public String getName() {
        return this.font.getFamily();
    }

    private FilterComponent.Entry computeEntry2() {
        if (this.entry2 == null) {
            this.entry2 = this.computeEntry(this.font.deriveFont(1));
        }

        return this.entry2;
    }

    public static final class Entry {
        public final FilterComponent.Entry2[] entry2Array = new FilterComponent.Entry2[256];
        public DynamicTexture texture;
        public int size;
        public int height;
        private static final long timestamp = -3155580178235653632L;

        public Entry() {
            this.size = (int)timestamp;
            this.height = -1;
        }
    }

    public static final class Entry2 {
        public int width;
        public int height;
        public int count;
        public int count2;
    }
}
