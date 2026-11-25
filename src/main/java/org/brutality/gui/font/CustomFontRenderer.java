package org.brutality.gui.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.brutality.utils.mcp.McpApi3;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CustomFontRenderer extends FontRenderer implements McpApi3 {
    private static final String text = "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzあいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン";
    private static final String text2 = "0123456789abcdefklmnor";
    private static final Color color = new Color(255, 255, 255, 0);
    private static final char charVal = 167;
    private static final int count = 21;
    private static final int count2 = 17;
    private static final int[] intValueArray = new int[32];
    private static final int count3 = 256;
    private static final int count4 = 4;
    private static final int count5 = 255;
    private static final String[] stringArray = new String[]{
        "Segoe UI Symbol",
        "Segoe UI",
        "Segoe UI Emoji",
        "Arial Unicode MS",
        "DejaVu Sans",
        "Symbola",
        "Noto Sans Symbols",
        "Lucida Sans Unicode",
        "Dialog",
        "SansSerif"
    };
    private static final String text3 = "Ag";
    private final Font font;
    private final boolean flag;
    private final float factor;
    private final GlyphTexture[] deleteTextureComponentArray = new GlyphTexture[256];
    private final GlyphTexture[] deleteTextureComponentArray2 = new GlyphTexture[256];
    private final Map<Character, GlyphTexture> lookupMap = new HashMap<>();
    private final Map<Character, GlyphTexture> lookupMap2 = new HashMap<>();
    private final Set<Character> itemSet = new HashSet<>();
    private boolean flag2 = true;
    private int count6 = 2;
    private int count7;
    private boolean flag3;
    private GlyphTextureAtlas fontComponent3;
    private Map<String, Integer> lookupMap3;
    private Map<String, CustomFontRenderer.Entry> lookupMap4;
    private boolean flag4;

    public CustomFontRenderer(Font font, int n) {
        this(font, false, true, n);
        this.flag3 = true;
    }

    public int computeInt3(String str, float f, float f2, int n) {
        return this.drawString(str, f, f2, n, false);
    }

    private static void handleInt(int n) {
        GL11.glColor4f(
            ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F, ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F, (n & 0xFF) / 255.0F, (n >>> 24) / 255.0F
        );
    }

    public float getFONT_HEIGHT() {
        return this.FONT_HEIGHT;
    }

    public void handleGraphics2D(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        if (this.flag2) {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS, this.flag ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        );
    }

    private static int computeInt(int n, int n2) {
        return n2 << 24 | (n & 16579836) >>> 2;
    }

    public int computeInt4(String str, float f, float f2, int n) {
        return this.drawString(str, f - this.getStringWidth(str) / 2.0F, f2 - this.getFONT_HEIGHT() / 2.0F, n, false);
    }

    public int computeInt(String str, float f, float f2, int n) {
        return this.drawString(str, f - this.getStringWidth(str) / 2.0F, f2, n, false);
    }

    static {
        process2();
    }

    public CustomFontRenderer(Font font) {
        this(font, true);
    }

    public CustomFontRenderer.Entry computeEntry(String str) {
        this.process3();
        if (this.lookupMap4 == null) {
            this.lookupMap4 = new LinkedHashMap<>();
        }

        CustomFontRenderer.Entry entry = this.lookupMap4.get(str);
        if (entry != null) {
            return entry;
        }
        boolean flag = false;
        boolean flag2 = false;
        float f = 0.0F;
        float MAX_VALUE = Float.MAX_VALUE;
        float MAX_VALUE2 = Float.MAX_VALUE;
        float f2 = -Float.MAX_VALUE;
        float f3 = -Float.MAX_VALUE;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 167 && i + 1 < str.length()) {
                char str2 = Character.toLowerCase(str.charAt(++i));
                if (str2 == 'l') {
                    flag = true;
                } else if (str2 == 'r' || "0123456789abcdef".indexOf(str2) >= 0) {
                    flag = false;
                }
            } else {
                GlyphTexture deleteTextureComponent = ch < 256
                    ? this.computeDeleteTextureComponent(ch, flag)
                    : this.computeDeleteTextureComponent2(ch, flag);
                if (deleteTextureComponent != null) {
                    Rectangle rectangle = deleteTextureComponent.getRectangle();
                    if (!rectangle.isEmpty()) {
                        flag2 = true;
                        float f4 = f + (float)(rectangle.x - 4) / this.count6;
                        float f5 = 2.0F + (float)(rectangle.y - 4) / this.count6;
                        MAX_VALUE = Math.min(MAX_VALUE, f4);
                        MAX_VALUE2 = Math.min(MAX_VALUE2, f5);
                        f2 = Math.max(f2, f4 + (float)rectangle.width / this.count6);
                        f3 = Math.max(f3, f5 + (float)rectangle.height / this.count6);
                    }

                    f += (deleteTextureComponent.getWidth() - 8.0F) / this.count6;
                }
            }
        }

        CustomFontRenderer.Entry entry2 = new CustomFontRenderer.Entry(
            f, flag2 ? new java.awt.geom.Rectangle2D.Float(MAX_VALUE, MAX_VALUE2, f2 - MAX_VALUE, f3 - MAX_VALUE2) : new java.awt.geom.Rectangle2D.Float()
        );
        if (this.lookupMap4.size() >= 1024) {
            this.lookupMap4.clear();
        }

        this.lookupMap4.put(str, entry2);
        return entry2;
    }

    @Override
    public int drawStringWithShadow(String str, float f, float f2, int n) {
        if (str != null && !str.isEmpty()) {
            this.process3();

            int n2;
            try (GlFontStateGuard fontComponent2 = new GlFontStateGuard()) {
                this.drawString(str, f + 0.5F, f2 + 0.5F, n, true);
                n2 = this.computeInt3(str, f, f2, n);
            }

            return n2;
        }
        return 0;
    }

    public int computeInt6(String str, float f, float f2, int n) {
        boolean flag = this.flag4;
        this.flag4 = true;

        int n2;
        try {
            n2 = this.drawStringWithShadow(str, f, f2, n);
        } finally {
            this.flag4 = flag;
        }

        return n2;
    }

    private int computeInt() {
        if (this.flag3) {
            return Math.max(1, this.count7);
        }
        if (this.count7 > 0) {
            return this.count7;
        }
        try {
            return InvalidateHelper.computeScaledResolution(mc).getScaleFactor();
        } catch (Exception e) {
            return Math.max(this.count7, 2);
        }
    }

    public static void process2() {
        for (int i = 0; i < 32; i++) {
            int n = (i >> 3 & 1) * 85;
            int n2 = (i >> 2 & 1) * 170 + n;
            int n3 = (i >> 1 & 1) * 170 + n;
            int n4 = ((1 | ~i) - ~i) * 170 + n;
            if (i == 6) {
                n2 += 85;
            }

            if (i >= 16) {
                n2 /= 4;
                n3 /= 4;
                n4 /= 4;
            }

            intValueArray[i] = (0xFF | ~n2) - ~n2 << 16 | (n3 & 0xFF) << 8 | n4 & 0xFF;
        }
    }

    private GlyphTexture computeDeleteTextureComponent(char ch, Font font) {
        BufferedImage image = new BufferedImage(1, 1, 2);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        Rectangle2D stringBounds = fontMetrics.getStringBounds(String.valueOf(ch), graphics);
        int n = Math.max(1, MathHelper.ceiling_float_int((float)stringBounds.getWidth()) + 8);
        int n2 = Math.max(1, MathHelper.ceiling_float_int((float)stringBounds.getHeight() + 5.0F));
        BufferedImage image2 = new BufferedImage(n, n2, 2);
        Graphics2D graphics2 = (Graphics2D)image2.getGraphics();
        graphics2.setFont(font);
        int width = image2.getWidth();
        int height = image2.getHeight();
        graphics2.setColor(color);
        graphics2.fillRect(0, 0, width, height);
        this.handleGraphics2D(graphics2);
        graphics2.drawString(String.valueOf(ch), 4, font.getSize());
        Rectangle rect = GlyphTexture.computeRectangle(image2);
        graphics.dispose();
        graphics2.dispose();
        if (this.fontComponent3 == null) {
            this.fontComponent3 = new GlyphTextureAtlas(!this.flag3);
        }

        return this.fontComponent3.computeDeleteTextureComponent(image2, rect);
    }

    public CustomFontRenderer(Font font, boolean flag) {
        super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), mc.isUnicode());
        this.font = font;
        this.flag = flag;
        this.factor = (float)(
            font.getStringBounds(
                        "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzあいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン",
                        new FontRenderContext(new AffineTransform(), true, flag)
                    )
                    .getHeight()
                / 2.0
        );
        this.FONT_HEIGHT = (int)this.factor;
    }

    public void process() {
        if (this.fontComponent3 != null) {
            this.fontComponent3.close();
            this.fontComponent3 = null;
        }

        if (this.lookupMap3 != null) {
            this.lookupMap3.clear();
        }

        if (this.lookupMap4 != null) {
            this.lookupMap4.clear();
        }

        for (int i = 0; i < this.deleteTextureComponentArray.length; i++) {
            if (this.deleteTextureComponentArray[i] != null) {
                this.deleteTextureComponentArray[i].deleteTexture();
            }

            this.deleteTextureComponentArray[i] = null;
        }

        for (int j = 0; j < this.deleteTextureComponentArray2.length; j++) {
            if (this.deleteTextureComponentArray2[j] != null) {
                this.deleteTextureComponentArray2[j].deleteTexture();
            }

            this.deleteTextureComponentArray2[j] = null;
        }

        for (GlyphTexture deleteTextureComponent : this.lookupMap.values()) {
            deleteTextureComponent.deleteTexture();
        }

        for (GlyphTexture deleteTextureComponent2 : this.lookupMap2.values()) {
            deleteTextureComponent2.deleteTexture();
        }

        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.itemSet.clear();
    }

    @Override
    public int getStringWidth(String str) {
        if (str != null && !str.isEmpty()) {
            this.process3();
            if (this.lookupMap3 == null) {
                this.lookupMap3 = new LinkedHashMap<>();
            }

            Integer integer = this.lookupMap3.get(str);
            if (integer != null) {
                return integer;
            }
            GlyphTexture[] deleteTextureComponentArray = this.deleteTextureComponentArray;
            int len = str.length();
            char ch = '.';
            float f = 0.0F;

            for (int i = 0; i < len; i++) {
                char ch2 = str.charAt(i);
                if (ch != 167) {
                    if (ch2 == 167 && i + 1 < len) {
                        int n = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(str.charAt(i + 1)));
                        if ((n < 0 || n >= 16) && n != 21) {
                            if (n == 17) {
                                deleteTextureComponentArray = this.deleteTextureComponentArray2;
                            }
                        } else {
                            deleteTextureComponentArray = this.deleteTextureComponentArray;
                        }
                    } else if (deleteTextureComponentArray.length > ch2) {
                        f += this.computeDeleteTextureComponent(ch2, deleteTextureComponentArray == this.deleteTextureComponentArray2).getWidth() - 8.0F;
                    } else {
                        GlyphTexture deleteTextureComponent = this.computeDeleteTextureComponent2(ch2, deleteTextureComponentArray == this.deleteTextureComponentArray2);
                        if (deleteTextureComponent != null) {
                            f += deleteTextureComponent.getWidth() - 8.0F;
                        }
                    }
                }

                ch = ch2;
            }

            int n2 = (int)(f / this.count6);
            if (this.lookupMap3.size() >= 1024) {
                this.lookupMap3.clear();
            }

            this.lookupMap3.put(str, n2);
            return n2;
        }
        return 0;
    }

    private void handleInt(int n, BufferedImage image, int n2, int n3) {
        GlStateManager.bindTexture(n);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        int[] ints = new int[n2 * n3];

        for (int i = 0; i < n3; i++) {
            for (int j = 0; j < n2; j++) {
                ints[i * n2 + j] = image.getRGB(j, i) >>> 24;
            }
        }

        int n4 = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        try {
            int n5 = 0;

            while (true) {
                ByteBuffer buffer = BufferUtils.createByteBuffer(ints.length);

                for (int n6 : ints) {
                    buffer.put((byte)n6);
                }

                buffer.flip();
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, n5, GL11.GL_ALPHA8, n2, n3, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, buffer);
                if (n2 == 1 && n3 == 1) {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, n5);
                    return;
                }

                int n7 = Math.max(1, n2 / 2);
                int n8 = Math.max(1, n3 / 2);
                int[] ints2 = new int[n7 * n8];

                for (int k = 0; k < n8; k++) {
                    for (int idx = 0; idx < n7; idx++) {
                        int n9 = 0;
                        int n10 = 0;

                        for (int idx2 = k * n3 / n8; idx2 < (k + 1) * n3 / n8; idx2++) {
                            for (int idx3 = idx * n2 / n7; idx3 < (idx + 1) * n2 / n7; idx3++) {
                                n9 += ints[idx2 * n2 + idx3];
                                n10++;
                            }
                        }

                        ints2[k * n7 + idx] = (n9 + n10 / 2) / n10;
                    }
                }

                ints = ints2;
                n2 = n7;
                n3 = n8;
                n5++;
            }
        } finally {
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, n4);
        }
    }

    public void handleDeleteTextureComponentArray(GlyphTexture[] deleteTextureComponents, int n) {
        Font font = this.computeFont(n);

        for (int i = 0; i < deleteTextureComponents.length; i++) {
            deleteTextureComponents[i] = this.computeDeleteTextureComponent((char)i, font);
        }
    }

    @Override
    public int drawString(String str, float f, float f2, int n, boolean flag) {
        if (str != null && !str.isEmpty()) {
            float f3 = f2 + 2.0F;
            this.process3();
            GlyphTexture[] deleteTextureComponentArray = this.deleteTextureComponentArray;
            double d = f;

            int n2;
            try (GlFontStateGuard fontComponent2 = new GlFontStateGuard()) {
                GL11.glPushMatrix();

                try {
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    this.handleString(str);
                    GL11.glScalef(1.0F / this.count6, 1.0F / this.count6, 1.0F / this.count6);
                    f -= 4.0F / this.count6;
                    f2 = f3 - 4.0F / this.count6;
                    float f4 = f * this.count6;
                    float f5 = f2 * this.count6;
                    if (this.count7 == 0) {
                        f5 -= this.factor / 5.0F;
                    }

                    if (this.flag4) {
                        f4 = Math.round(f4);
                        f5 = Math.round(f5);
                    }

                    int n3 = n >> 24 & 0xFF;
                    if (n3 == 0) {
                        n3 = 255;
                    }

                    int n4 = Math.max(0, (int)(n3 * 0.45F));
                    int n5 = n3 << 24 | (16777215 | ~n) - ~n;
                    float f6 = f4;
                    int len = str.length();
                    handleInt(flag ? computeInt(n5, n4) : n5);
                    char ch = '.';

                    try (GlyphTexture.Entry entry = new GlyphTexture.Entry()) {
                        for (int i = 0; i < len; i++) {
                            char ch2 = str.charAt(i);

                            try {
                                if (ch2 == '\n') {
                                    f4 = f6;
                                    f5 += this.getFONT_HEIGHT() * this.count6;
                                    continue;
                                }

                                if (ch != 167) {
                                    if (ch2 == 167 && i + 1 < len) {
                                        int n6 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(str.charAt(i + 1)));
                                        if (n6 >= 0 && n6 < 16) {
                                            deleteTextureComponentArray = this.deleteTextureComponentArray;
                                            n5 = (intValueArray[n6] & 16777215) + (n3 << 24 & ~(intValueArray[n6] & 16777215));
                                            handleInt(flag ? computeInt(n5, n4) : n5);
                                        } else if (n6 == 17) {
                                            deleteTextureComponentArray = this.deleteTextureComponentArray2;
                                        } else if (n6 == 21) {
                                            deleteTextureComponentArray = this.deleteTextureComponentArray;
                                            n5 = n3 << 24 | n & 16777215;
                                            handleInt(flag ? computeInt(n5, n4) : n5);
                                        }
                                    } else if (deleteTextureComponentArray.length > ch2) {
                                        GlyphTexture deleteTextureComponent = this.computeDeleteTextureComponent(ch2, deleteTextureComponentArray == this.deleteTextureComponentArray2);
                                        entry.handleDeleteTextureComponent(deleteTextureComponent, f4, f5);
                                        f4 += deleteTextureComponent.getWidth() - 8.0F;
                                    } else {
                                        GlyphTexture deleteTextureComponent2 = this.computeDeleteTextureComponent2(ch2, deleteTextureComponentArray == this.deleteTextureComponentArray2);
                                        if (deleteTextureComponent2 != null) {
                                            entry.handleDeleteTextureComponent(deleteTextureComponent2, f4, f5);
                                            f4 += deleteTextureComponent2.getWidth() - 8.0F;
                                        }
                                    }
                                }
                            } catch (Exception ignored) {
                            }

                            ch = ch2;
                        }
                    }

                    n2 = (int)(f4 - d);
                } finally {
                    GL11.glPopMatrix();
                }
            }

            return n2;
        }
        return 0;
    }

    public void handleInt2(int n, BufferedImage image, int n2, int n3) {
        if (this.count7 > 0) {
            this.handleInt(n, image, n2, n3);
        } else {
            int[] rgb = image.getRGB(0, 0, n2, n3, new int[n2 * n3], 0, n2);
            ByteBuffer buffer = BufferUtils.createByteBuffer(n2 * n3 * 4);

            for (int i = 0; i < n3; i++) {
                for (int j = 0; j < n2; j++) {
                    int n4 = rgb[j + i * n2];
                    buffer.put((byte)(n4 >> 16 & 0xFF));
                    buffer.put((byte)(n4 >> 8 & 0xFF));
                    buffer.put((byte)(n4 & 0xFF));
                    buffer.put((byte)((0xFF | ~(n4 >> 24)) - ~(n4 >> 24)));
                }
            }

            buffer.flip();
            GlStateManager.bindTexture(n);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, n2, n3, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        }
    }

    public int computeInt(String str, float f, float f2, int n, boolean flag) {
        boolean flag2 = this.flag4;
        this.flag4 = true;

        int n2;
        try {
            n2 = flag ? this.drawStringWithShadow(str, f, f2, n) : this.computeInt3(str, f, f2, n);
        } finally {
            this.flag4 = flag2;
        }

        return n2;
    }

    private Font computeFont(Font font) {
        FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), this.flag2, this.flag);
        float height = this.computeFont(font.getStyle()).getLineMetrics("Ag", fontRenderContext).getHeight();
        float height2 = font.getLineMetrics("Ag", fontRenderContext).getHeight();
        return !(height <= 0.0F) && !(height2 <= 0.0F) && !(Math.abs(height - height2) < 0.5F) ? font.deriveFont(font.getSize2D() * (height / height2)) : font;
    }

    private GlyphTexture computeDeleteTextureComponent2(char ch, boolean flag) {
        if (this.itemSet.contains(ch)) {
            return null;
        }
        Map map = flag ? this.lookupMap2 : this.lookupMap;
        GlyphTexture deleteTextureComponent = (GlyphTexture)map.get(ch);
        if (deleteTextureComponent == null) {
            int n = flag ? 1 : 0;
            Font font = this.computeFont(ch, n);
            if (font == null) {
                this.itemSet.add(ch);
                return null;
            }

            deleteTextureComponent = this.computeDeleteTextureComponent(ch, font);
            map.put(ch, deleteTextureComponent);
        }

        return deleteTextureComponent;
    }

    private void handleString(String str) {
        boolean flag = false;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 167 && i + 1 < str.length()) {
                char str2 = Character.toLowerCase(str.charAt(++i));
                if (str2 == 'l') {
                    flag = true;
                } else if (str2 == 'r' || "0123456789abcdef".indexOf(str2) >= 0) {
                    flag = false;
                }
            } else if (ch != '\n') {
                if (ch < 256) {
                    this.computeDeleteTextureComponent(ch, flag);
                } else {
                    this.computeDeleteTextureComponent2(ch, flag);
                }
            }
        }
    }

    private Font computeFont(char ch, int n) {
        if (this.font.canDisplay(ch)) {
            return this.computeFont(n);
        }
        float f = this.font.getSize2D() * this.count6 / 2.0F;

        for (String str : stringArray) {
            Font font = new Font(str, n, 1);
            if (font.canDisplay(ch)) {
                return this.computeFont(font.deriveFont(f));
            }
        }

        return null;
    }

    public int computeInt5(String str, float f, float f2, int n) {
        return this.drawStringWithShadow(str, f - this.getStringWidth(str) / 2.0F, f2, n);
    }

    private void process3() {
        int n = this.computeInt();
        if (n <= 0) {
            n = 2;
        }

        if (n != this.count6) {
            this.count6 = n;
            this.process();
        }
    }

    public int computeInt2(String str, float f, float f2, int n) {
        return this.drawStringWithShadow(str, f - this.getStringWidth(str) / 2.0F, f2 - this.getFONT_HEIGHT() / 2.0F, n);
    }

    private GlyphTexture computeDeleteTextureComponent(char ch, boolean flag) {
        GlyphTexture[] deleteTextureComponents = flag ? this.deleteTextureComponentArray2 : this.deleteTextureComponentArray;
        GlyphTexture deleteTextureComponent = deleteTextureComponents[ch];
        if (deleteTextureComponent == null) {
            Font font = this.computeFont(flag ? 1 : 0);
            if (this.flag3
                && this.font.getFontName(Locale.ROOT).toLowerCase(Locale.ROOT).contains("demo")
                && !Character.isLetter(ch)
                && !Character.isWhitespace(ch)) {
                font = new Font("Arial", flag ? 1 : 0, font.getSize());
            }

            deleteTextureComponent = this.computeDeleteTextureComponent(ch, font);
            deleteTextureComponents[ch] = deleteTextureComponent;
        }

        return deleteTextureComponent;
    }

    public CustomFontRenderer(Font font, boolean flag, boolean flag2) {
        super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), mc.isUnicode());
        this.flag2 = flag2;
        this.font = font;
        this.flag = flag;
        this.factor = (float)(
            font.getStringBounds(
                        "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzあいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんアイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン",
                        new FontRenderContext(new AffineTransform(), flag2, flag)
                    )
                    .getHeight()
                / 2.0
        );
        this.FONT_HEIGHT = (int)this.factor;
    }

    private Font computeFont(int n) {
        float f = this.font.getSize2D() * this.count6 / 2.0F;
        return this.font.deriveFont(n, f);
    }

    public CustomFontRenderer(Font font, boolean flag, boolean flag2, int n) {
        this(font, flag, flag2);
        this.count7 = n;
    }

    public static final class Entry {
        public final float factor;
        public final java.awt.geom.Rectangle2D.Float floatValue;

        public Entry(float factor, java.awt.geom.Rectangle2D.Float floatValue) {
            this.factor = factor;
            this.floatValue = floatValue;
        }
    }
}
