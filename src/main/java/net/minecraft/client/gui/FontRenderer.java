package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;
import net.optifine.render.GlBlendState;
import net.optifine.util.FontUtils;
import org.apache.commons.io.IOUtils;
import org.brutality.Brutality;
import org.brutality.module.impl.misc.AutoStrip;
import org.brutality.module.impl.visual.NickHider;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
    private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
    private final int[] charWidth = new int[256];
    public int FONT_HEIGHT = 9;
    public Random fontRandom = new Random();
    private byte[] glyphWidth = new byte[65536];
    private int[] colorCode = new int[32];
    private ResourceLocation locationFontTexture;
    private final TextureManager renderEngine;
    private float posX;
    private float posY;
    private boolean unicodeFlag;
    private boolean bidiFlag;
    private float red;
    private float blue;
    private float green;
    private float alpha;
    private int textColor;
    private boolean randomStyle;
    private boolean boldStyle;
    private boolean italicStyle;
    private boolean underlineStyle;
    private boolean strikethroughStyle;
    public GameSettings gameSettings;
    public ResourceLocation locationFontTextureBase;
    public float offsetBold = 1.0F;
    private float[] charWidthFloat = new float[256];
    private boolean blend = false;
    private GlBlendState oldBlendState = new GlBlendState();

    public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
        this.gameSettings = gameSettingsIn;
        this.locationFontTextureBase = location;
        this.locationFontTexture = location;
        this.renderEngine = textureManagerIn;
        this.unicodeFlag = unicode;
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
        this.bindTexture(this.locationFontTexture);

        for (int i = 0; i < 32; i++) {
            int n = (i >> 3 & 1) * 85;
            int n2 = (i >> 2 & 1) * 170 + n;
            int n3 = (i >> 1 & 1) * 170 + n;
            int n4 = (i >> 0 & 1) * 170 + n;
            if (i == 6) {
                n2 += 85;
            }

            if (gameSettingsIn.anaglyph) {
                int n5 = (n2 * 30 + n3 * 59 + n4 * 11) / 100;
                int n6 = (n2 * 30 + n3 * 70) / 100;
                int n7 = (n2 * 30 + n4 * 70) / 100;
                n2 = n5;
                n3 = n6;
                n4 = n7;
            }

            if (i >= 16) {
                n2 /= 4;
                n3 /= 4;
                n4 /= 4;
            }

            this.colorCode[i] = (n2 & 0xFF) << 16 | (n3 & 0xFF) << 8 | n4 & 0xFF;
        }

        this.readGlyphSizes();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

        for (int i = 0; i < unicodePageLocations.length; i++) {
            unicodePageLocations[i] = null;
        }

        this.readFontTexture();
        this.readGlyphSizes();
    }

    private void readFontTexture() {
        BufferedImage image;
        try {
            image = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Properties properties = FontUtils.readFontProperties(this.locationFontTexture);
        this.blend = FontUtils.readBoolean(properties, "blend", false);
        int width = image.getWidth();
        int height = image.getHeight();
        int n = width / 16;
        int n2 = height / 16;
        float f = width / 128.0F;
        float f2 = Config.limit(f, 1.0F, 2.0F);
        this.offsetBold = 1.0F / f2;
        float f3 = FontUtils.readFloat(properties, "offsetBold", -1.0F);
        if (f3 >= 0.0F) {
            this.offsetBold = f3;
        }

        int[] ints = new int[width * height];
        image.getRGB(0, 0, width, height, ints, 0, width);

        for (int i = 0; i < 256; i++) {
            int n3 = i % 16;
            int n4 = i / 16;
            int n5 = 0;

            for (n5 = n - 1; n5 >= 0; n5--) {
                int n6 = n3 * n + n5;
                boolean flag = true;

                for (int j = 0; j < n2 && flag; j++) {
                    int n7 = (n4 * n2 + j) * width;
                    int n8 = ints[n6 + n7];
                    int n9 = n8 >> 24 & 0xFF;
                    if (n9 > 16) {
                        flag = false;
                    }
                }

                if (!flag) {
                    break;
                }
            }

            if (i == 65) {
                i = i;
            }

            if (i == 32) {
                if (n <= 8) {
                    n5 = (int)(2.0F * f);
                } else {
                    n5 = (int)(1.5F * f);
                }
            }

            this.charWidthFloat[i] = (n5 + 1) / f + 1.0F;
        }

        FontUtils.readCustomCharWidths(properties, this.charWidthFloat);

        for (int k = 0; k < this.charWidth.length; k++) {
            this.charWidth[k] = Math.round(this.charWidthFloat[k]);
        }
    }

    private void readGlyphSizes() {
        InputStream in = null;

        try {
            in = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
            in.read(this.glyphWidth);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private float func_181559_a(char p_181559_1_, boolean p_181559_2_) {
        if (p_181559_1_ != ' ' && p_181559_1_ != 160) {
            int n = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                .indexOf(p_181559_1_);
            return n != -1 && !this.unicodeFlag ? this.renderDefaultChar(n, p_181559_2_) : this.renderUnicodeChar(p_181559_1_, p_181559_2_);
        }
        return !this.unicodeFlag ? this.charWidthFloat[p_181559_1_] : 4.0F;
    }

    private float renderDefaultChar(int p_78266_1_, boolean p_78266_2_) {
        int n = p_78266_1_ % 16 * 8;
        int n2 = p_78266_1_ / 16 * 8;
        int n3 = p_78266_2_ ? 1 : 0;
        this.bindTexture(this.locationFontTexture);
        float f = this.charWidthFloat[p_78266_1_];
        float f2 = 7.99F;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(n / 128.0F, n2 / 128.0F);
        GL11.glVertex3f(this.posX + n3, this.posY, 0.0F);
        GL11.glTexCoord2f(n / 128.0F, (n2 + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX - n3, this.posY + 7.99F, 0.0F);
        GL11.glTexCoord2f((n + f2 - 1.0F) / 128.0F, n2 / 128.0F);
        GL11.glVertex3f(this.posX + f2 - 1.0F + n3, this.posY, 0.0F);
        GL11.glTexCoord2f((n + f2 - 1.0F) / 128.0F, (n2 + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX + f2 - 1.0F - n3, this.posY + 7.99F, 0.0F);
        GL11.glEnd();
        return f;
    }

    private ResourceLocation getUnicodePageLocation(int p_111271_1_) {
        if (unicodePageLocations[p_111271_1_] == null) {
            unicodePageLocations[p_111271_1_] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", p_111271_1_));
            unicodePageLocations[p_111271_1_] = FontUtils.getHdFontLocation(unicodePageLocations[p_111271_1_]);
        }

        return unicodePageLocations[p_111271_1_];
    }

    private void loadGlyphTexture(int p_78257_1_) {
        this.bindTexture(this.getUnicodePageLocation(p_78257_1_));
    }

    private float renderUnicodeChar(char p_78277_1_, boolean p_78277_2_) {
        if (this.glyphWidth[p_78277_1_] == 0) {
            return 0.0F;
        }
        int n = p_78277_1_ / 256;
        this.loadGlyphTexture(n);
        int n2 = this.glyphWidth[p_78277_1_] >>> 4;
        int n3 = this.glyphWidth[p_78277_1_] & 15;
        float f = n2;
        float f2 = n3 + 1;
        float f3 = p_78277_1_ % 16 * 16 + f;
        float f4 = (p_78277_1_ & 255) / 16 * 16;
        float f5 = f2 - f - 0.02F;
        float f6 = p_78277_2_ ? 1.0F : 0.0F;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(f3 / 256.0F, f4 / 256.0F);
        GL11.glVertex3f(this.posX + f6, this.posY, 0.0F);
        GL11.glTexCoord2f(f3 / 256.0F, (f4 + 15.98F) / 256.0F);
        GL11.glVertex3f(this.posX - f6, this.posY + 7.99F, 0.0F);
        GL11.glTexCoord2f((f3 + f5) / 256.0F, f4 / 256.0F);
        GL11.glVertex3f(this.posX + f5 / 2.0F + f6, this.posY, 0.0F);
        GL11.glTexCoord2f((f3 + f5) / 256.0F, (f4 + 15.98F) / 256.0F);
        GL11.glVertex3f(this.posX + f5 / 2.0F - f6, this.posY + 7.99F, 0.0F);
        GL11.glEnd();
        return (f2 - f) / 2.0F + 1.0F;
    }

    private String applyModifications(String text) {
        if (text != null && !text.isEmpty()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer == null) {
                return text;
            }
            String str = text;
            AutoStrip autoStrip = (AutoStrip)Brutality.moduleManager.computeModule("Auto Strip");
            if (autoStrip != null && autoStrip.checkState44()) {
                str = autoStrip.computeString3(text);
            }

            String name = mc.thePlayer.getName();
            NickHider nickHider = (NickHider)Brutality.moduleManager.computeModule("NickHider");
            if (nickHider != null && nickHider.checkState44()) {
                String str2 = NickHider.computeString(mc.thePlayer);
                if (str2 != null && !str2.equals(name)) {
                    str = str.replace(name, str2);
                }
            }

            return str;
        }
        return text;
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        return this.drawString(text, x, y, color, true);
    }

    public int drawString(String text, int x, int y, int color) {
        return this.drawString(text, x, y, color, false);
    }

    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        String str = this.applyModifications(text);
        this.enableAlpha();
        if (this.blend) {
            GlStateManager.getBlendState(this.oldBlendState);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        this.resetStyles();
        int n;
        if (dropShadow) {
            n = this.renderString(str, x + 1.0F, y + 1.0F, color, true);
            n = Math.max(n, this.renderString(str, x, y, color, false));
        } else {
            n = this.renderString(str, x, y, color, false);
        }

        if (this.blend) {
            GlStateManager.setBlendState(this.oldBlendState);
        }

        return n;
    }

    private String bidiReorder(String p_147647_1_) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(p_147647_1_), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException e) {
            return p_147647_1_;
        }
    }

    private void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    private void renderStringAtPos(String p_78255_1_, boolean p_78255_2_) {
        for (int i = 0; i < p_78255_1_.length(); i++) {
            char ch = p_78255_1_.charAt(i);
            if (ch == 167 && i + 1 < p_78255_1_.length()) {
                int n = "0123456789abcdefklmnor".indexOf(p_78255_1_.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                if (n < 16) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    if (n < 0 || n > 15) {
                        n = 15;
                    }

                    if (p_78255_2_) {
                        n += 16;
                    }

                    int n2 = this.colorCode[n];
                    if (Config.isCustomColors()) {
                        n2 = CustomColors.getTextColor(n, n2);
                    }

                    this.textColor = n2;
                    this.setColor((n2 >> 16) / 255.0F, (n2 >> 8 & 0xFF) / 255.0F, (n2 & 0xFF) / 255.0F, this.alpha);
                } else if (n == 16) {
                    this.randomStyle = true;
                } else if (n == 17) {
                    this.boldStyle = true;
                } else if (n == 18) {
                    this.strikethroughStyle = true;
                } else if (n == 19) {
                    this.underlineStyle = true;
                } else if (n == 20) {
                    this.italicStyle = true;
                } else if (n == 21) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    this.setColor(this.red, this.blue, this.green, this.alpha);
                }

                i++;
            } else {
                int n3 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                    .indexOf(ch);
                if (this.randomStyle && n3 != -1) {
                    int charWidth = this.getCharWidth(ch);

                    char ch2;
                    do {
                        n3 = this.fontRandom
                            .nextInt(
                                "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                                    .length()
                            );
                        ch2 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                            .charAt(n3);
                    } while (charWidth != this.getCharWidth(ch2));

                    ch = ch2;
                }

                float f = n3 != -1 && !this.unicodeFlag ? this.offsetBold : 0.5F;
                boolean flag = (ch == 0 || n3 == -1 || this.unicodeFlag) && p_78255_2_;
                if (flag) {
                    this.posX -= f;
                    this.posY -= f;
                }

                float f2 = this.func_181559_a(ch, this.italicStyle);
                if (flag) {
                    this.posX += f;
                    this.posY += f;
                }

                if (this.boldStyle) {
                    this.posX += f;
                    if (flag) {
                        this.posX -= f;
                        this.posY -= f;
                    }

                    this.func_181559_a(ch, this.italicStyle);
                    this.posX -= f;
                    if (flag) {
                        this.posX += f;
                        this.posY += f;
                    }

                    f2 += f;
                }

                this.doDraw(f2);
            }
        }
    }

    protected void doDraw(float p_doDraw_1_) {
        if (this.strikethroughStyle) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
            worldRenderer.func_181662_b(this.posX, this.posY + this.FONT_HEIGHT / 2, 0.0).func_181675_d();
            worldRenderer.func_181662_b(this.posX + p_doDraw_1_, this.posY + this.FONT_HEIGHT / 2, 0.0).func_181675_d();
            worldRenderer.func_181662_b(this.posX + p_doDraw_1_, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).func_181675_d();
            worldRenderer.func_181662_b(this.posX, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).func_181675_d();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        if (this.underlineStyle) {
            Tessellator tessellator2 = Tessellator.getInstance();
            WorldRenderer worldRenderer2 = tessellator2.getWorldRenderer();
            GlStateManager.disableTexture2D();
            worldRenderer2.func_181668_a(7, DefaultVertexFormats.field_181705_e);
            int n = this.underlineStyle ? -1 : 0;
            worldRenderer2.func_181662_b(this.posX + n, this.posY + this.FONT_HEIGHT, 0.0).func_181675_d();
            worldRenderer2.func_181662_b(this.posX + p_doDraw_1_, this.posY + this.FONT_HEIGHT, 0.0).func_181675_d();
            worldRenderer2.func_181662_b(this.posX + p_doDraw_1_, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).func_181675_d();
            worldRenderer2.func_181662_b(this.posX + n, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).func_181675_d();
            tessellator2.draw();
            GlStateManager.enableTexture2D();
        }

        this.posX += p_doDraw_1_;
    }

    private int renderStringAligned(String text, int x, int y, int p_78274_4_, int color, boolean dropShadow) {
        if (this.bidiFlag) {
            int textWidth = this.getStringWidth(this.bidiReorder(text));
            x = x + p_78274_4_ - textWidth;
        }

        return this.renderString(text, x, y, color, dropShadow);
    }

    private int renderString(String text, float x, float y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        }
        if (this.bidiFlag) {
            text = this.bidiReorder(text);
        }

        if ((color & -67108864) == 0) {
            color |= -16777216;
        }

        if (dropShadow) {
            color = (color & 16579836) >> 2 | color & 0xFF000000;
        }

        this.red = (color >> 16 & 0xFF) / 255.0F;
        this.blue = (color >> 8 & 0xFF) / 255.0F;
        this.green = (color & 0xFF) / 255.0F;
        this.alpha = (color >> 24 & 0xFF) / 255.0F;
        this.setColor(this.red, this.blue, this.green, this.alpha);
        this.posX = x;
        this.posY = y;
        this.renderStringAtPos(text, dropShadow);
        return (int)this.posX;
    }

    public int getStringWidth(String text) {
        String str = this.applyModifications(text);
        if (str == null) {
            return 0;
        }
        float f = 0.0F;
        boolean flag = false;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            float charWidthFloat = this.getCharWidthFloat(ch);
            if (charWidthFloat < 0.0F && i < str.length() - 1) {
                ch = str.charAt(++i);
                if (ch == 'l' || ch == 'L') {
                    flag = true;
                } else if (ch == 'r' || ch == 'R') {
                    flag = false;
                }

                charWidthFloat = 0.0F;
            }

            f += charWidthFloat;
            if (flag && charWidthFloat > 0.0F) {
                f += this.unicodeFlag ? 1.0F : this.offsetBold;
            }
        }

        return Math.round(f);
    }

    public int getCharWidth(char character) {
        return Math.round(this.getCharWidthFloat(character));
    }

    private float getCharWidthFloat(char p_getCharWidthFloat_1_) {
        if (p_getCharWidthFloat_1_ == 167) {
            return -1.0F;
        }
        if (p_getCharWidthFloat_1_ != ' ' && p_getCharWidthFloat_1_ != 160) {
            int n = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                .indexOf(p_getCharWidthFloat_1_);
            if (p_getCharWidthFloat_1_ > 0 && n != -1 && !this.unicodeFlag) {
                return this.charWidthFloat[n];
            }
            if (this.glyphWidth[p_getCharWidthFloat_1_] != 0) {
                int n2 = this.glyphWidth[p_getCharWidthFloat_1_] >>> 4;
                int n3 = this.glyphWidth[p_getCharWidthFloat_1_] & 15;
                if (n3 > 7) {
                    n3 = 15;
                    n2 = 0;
                }

                n3++;
                return (n3 - n2) / 2 + 1;
            }
            return 0.0F;
        }
        return this.charWidthFloat[32];
    }

    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidth(String text, int width, boolean reverse) {
        StringBuilder sb = new StringBuilder();
        float f = 0.0F;
        int n = reverse ? text.length() - 1 : 0;
        int n2 = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag2 = false;

        for (int i = n; i >= 0 && i < text.length() && f < width; i += n2) {
            char ch = text.charAt(i);
            float charWidthFloat = this.getCharWidthFloat(ch);
            if (flag) {
                flag = false;
                if (ch == 'l' || ch == 'L') {
                    flag2 = true;
                } else if (ch == 'r' || ch == 'R') {
                    flag2 = false;
                }
            } else if (charWidthFloat < 0.0F) {
                flag = true;
            } else {
                f += charWidthFloat;
                if (flag2) {
                    f++;
                }
            }

            if (f > width) {
                break;
            }

            if (reverse) {
                sb.insert(0, ch);
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
        if (this.blend) {
            GlStateManager.getBlendState(this.oldBlendState);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        this.resetStyles();
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x, y, wrapWidth, false);
        if (this.blend) {
            GlStateManager.setBlendState(this.oldBlendState);
        }
    }

    private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
        for (String str2 : this.listFormattedStringToWidth(str, wrapWidth)) {
            this.renderStringAligned(str2, x, y, wrapWidth, this.textColor, addShadow);
            y += this.FONT_HEIGHT;
        }
    }

    public int splitStringWidth(String p_78267_1_, int p_78267_2_) {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
    }

    public void setUnicodeFlag(boolean unicodeFlagIn) {
        this.unicodeFlag = unicodeFlagIn;
    }

    public boolean getUnicodeFlag() {
        return this.unicodeFlag;
    }

    public void setBidiFlag(boolean bidiFlagIn) {
        this.bidiFlag = bidiFlagIn;
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    String wrapFormattedStringToWidth(String str, int wrapWidth) {
        if (str.length() <= 1) {
            return str;
        }
        int n = this.sizeStringToWidth(str, wrapWidth);
        if (str.length() <= n) {
            return str;
        }
        String str2 = str.substring(0, n);
        char ch = str.charAt(n);
        boolean flag = ch == ' ' || ch == '\n';
        String str3 = getFormatFromString(str2) + str.substring(n + (flag ? 1 : 0));
        return str2 + "\n" + this.wrapFormattedStringToWidth(str3, wrapWidth);
    }

    private int sizeStringToWidth(String str, int wrapWidth) {
        int len = str.length();
        float f = 0.0F;
        int n = 0;
        int n2 = -1;

        for (boolean i = false; n < len; n++) {
            char ch = str.charAt(n);
            switch (ch) {
                case '\n':
                    n--;
                    break;
                case ' ':
                    n2 = n;
                default:
                    f += this.getCharWidth(ch);
                    if (i) {
                        f++;
                    }
                    break;
                case '§':
                    if (n < len - 1) {
                        char ch2 = str.charAt(++n);
                        if (ch2 == 'l' || ch2 == 'L') {
                            i = true;
                        } else if (ch2 == 'r' || ch2 == 'R' || isFormatColor(ch2)) {
                            i = false;
                        }
                    }
            }

            if (ch == '\n') {
                n2 = ++n;
                break;
            }

            if (Math.round(f) > wrapWidth) {
                break;
            }
        }

        return n != len && n2 != -1 && n2 < n ? n2 : n;
    }

    private static boolean isFormatColor(char colorChar) {
        return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
    }

    private static boolean isFormatSpecial(char formatChar) {
        return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
    }

    public static String getFormatFromString(String text) {
        String str = "";
        int n = -1;
        int len = text.length();

        while ((n = text.indexOf(167, n + 1)) != -1) {
            if (n < len - 1) {
                char ch = text.charAt(n + 1);
                if (isFormatColor(ch)) {
                    str = "§" + ch;
                } else if (isFormatSpecial(ch)) {
                    str = str + "§" + ch;
                }
            }
        }

        return str;
    }

    public boolean getBidiFlag() {
        return this.bidiFlag;
    }

    public int getColorCode(char character) {
        int n = "0123456789abcdef".indexOf(character);
        if (n >= 0 && n < this.colorCode.length) {
            int n2 = this.colorCode[n];
            if (Config.isCustomColors()) {
                n2 = CustomColors.getTextColor(n, n2);
            }

            return n2;
        }
        return 16777215;
    }

    protected void setColor(float p_setColor_1_, float p_setColor_2_, float p_setColor_3_, float p_setColor_4_) {
        GlStateManager.color(p_setColor_1_, p_setColor_2_, p_setColor_3_, p_setColor_4_);
    }

    protected void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    protected void bindTexture(ResourceLocation p_bindTexture_1_) {
        this.renderEngine.bindTexture(p_bindTexture_1_);
    }

    protected InputStream getResourceInputStream(ResourceLocation p_getResourceInputStream_1_) throws IOException {
        return Minecraft.getMinecraft().getResourceManager().getResource(p_getResourceInputStream_1_).getInputStream();
    }

    public int getHeight() {
        return this.FONT_HEIGHT;
    }

    public void drawOutlinedString(String string, float x, float y, float width, int color, int outlineColor) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.drawOutlinedString(ResetColorHelper.stripColor(string), x - width, y, outlineColor);
        this.drawOutlinedString(ResetColorHelper.stripColor(string), x, y - width, outlineColor);
        this.drawOutlinedString(ResetColorHelper.stripColor(string), x + width, y, outlineColor);
        this.drawOutlinedString(ResetColorHelper.stripColor(string), x, y + width, outlineColor);
        this.drawOutlinedString(string, x, y, color);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }

    private void drawOutlinedString(String text, float x, float y, int color) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.bidiFlag) {
            text = this.bidiReorder(text);
        }

        if ((color & -67108864) == 0) {
            color |= -16777216;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.resetColor();
        this.red = (color >> 16 & 0xFF) / 255.0F;
        this.blue = (color >> 8 & 0xFF) / 255.0F;
        this.green = (color & 0xFF) / 255.0F;
        this.alpha = (color >> 24 & 0xFF) / 255.0F;
        GlStateManager.color(this.red, this.blue, this.green, this.alpha);
        this.posX = x;
        this.posY = y;
        this.renderStringAtPos(text, false);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
