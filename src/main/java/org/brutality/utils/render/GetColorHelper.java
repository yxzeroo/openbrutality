package org.brutality.utils.render;

import java.awt.Color;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.utils.player.PlayerHelper5;
import org.brutality.utils.tuples.Pair;
import org.lwjgl.opengl.GL11;

public final class GetColorHelper {
    private static final Color color = new Color(255, 0, 0);
    private static final Color color2 = new Color(255, 255, 0);
    private static final Color color3 = new Color(0, 255, 0);
    public static final String text = String.valueOf('§');
    private final String text2;
    private final Pair<Color, Color> pair;
    private final boolean flag;

    public GetColorHelper(String str, Color color, Color color2) {
        this(str, color, color2, false);
    }

    public static int computeInt(Color color) {
        return computeInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static Color computeColor3(Color color, int n) {
        return new Color(color.getRed(), n, color.getBlue());
    }

    public static void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, String str, double d, double d2) {
        handleDrawStringWithShadowComponent(drawStringWithShadowComponent, str, d, d2, true);
    }

    public static Color computeColor(Color color, float f) {
        f = Math.min(1.0F, Math.max(0.0F, f));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f));
    }

    public static int computeInt(int n, int n2) {
        int n3 = (0xFF | ~(n >> 16)) - ~(n >> 16);
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        return computeInt(n3, n4, n5, MathHelper.clamp_int(n2, 0, 255));
    }

    public static Color computeColor3(Color color, float f) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        int n = (int)(1.0F / (1.0F - f));
        if (red == 0 && green == 0 && blue == 0) {
            return new Color(n, n, n, alpha);
        }
        if (red > 0 && red < n) {
            red = n;
        }

        if (green > 0 && green < n) {
            green = n;
        }

        if (blue > 0 && blue < n) {
            blue = n;
        }

        return new Color(Math.min((int)(red / f), 255), Math.min((int)(green / f), 255), Math.min((int)(blue / f), 255), alpha);
    }

    public static Pair<Color, Color> computePair2() {
        return Pair.computePair(Color.WHITE, Color.WHITE);
    }

    public static int computeInt(int n, int n2, int n3) {
        return computeInt(n, n2, n3, 255);
    }

    public static Color computeColor(int n) {
        double d = Math.ceil((System.currentTimeMillis() + n) / 10.0);
        d %= 360.0;
        return Color.getHSBColor((float)(d / 360.0), 0.6F, 1.0F);
    }

    public static int getColor(int n, int n2) {
        return computeInt(n, n, n, n2);
    }

    public static Color computeColor(Color color, Color color2, double d) {
        double d2 = 1.0 - d;
        int n = (int)(color.getRed() * d + color2.getRed() * d2);
        int n2 = (int)(color.getGreen() * d + color2.getGreen() * d2);
        int n3 = (int)(color.getBlue() * d + color2.getBlue() * d2);
        return new Color(n, n2, n3);
    }

    public GetColorHelper(String str, Color color, Color color2, boolean flag) {
        this.text2 = str;
        this.pair = Pair.computePair(color, color2);
        this.flag = flag;
    }

    public static String color(String str) {
        return str.replaceAll("&", "§");
    }

    public static int computeInt(Color color, Color color2, float f) {
        f = Math.min(1.0F, Math.max(0.0F, f));
        return computeColor(color, color2, f).getRGB();
    }

    public static void handleInt(int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GL11.glColor4f(f2, f3, f4, f);
    }

    public static Pair<Color, Color> computePair3() {
        Color color = new Color(255, 255, 255);
        Color color2 = new Color(250, 250, 250);
        return Pair.computePair(color, color2);
    }

    public static Color computeColor2(Color color, Color color2, float f) {
        f = Math.min(1.0F, Math.max(0.0F, f));
        float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float[] floats2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        Color hsbColor = Color.getHSBColor(
            PlayerHelper5.computeFloat(floats[0], floats2[0], f),
            PlayerHelper5.computeFloat(floats[1], floats2[1], f),
            PlayerHelper5.computeFloat(floats[2], floats2[2], f)
        );
        return computeColor(hsbColor, PlayerHelper5.computeInt(color.getAlpha(), color2.getAlpha(), f) / 255.0F);
    }

    public static int computeInt2(int n, int n2) {
        float f = (float)(Math.sin(System.currentTimeMillis() / 600.0) * 0.5 + 0.5);
        return computeInt(n, n2, f);
    }

    public static void handleColor(Color color) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public static Color computeColor(float f) {
        if (f >= 0.9F) {
            return color3;
        }
        if (f >= 0.55F) {
            return computeColor(color2, color3, (f - 0.55F) / 0.35F);
        }
        if (f >= 0.45F) {
            return color2;
        }
        return f >= 0.1F ? computeColor(color, color2, (f - 0.1F) / 0.35F) : color;
    }

    public static Color computeColor2(Color color, int n) {
        return n == color.getAlpha() ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)RenderHelper4.clamp(0.0, 255.0, n));
    }

    public static Color computeColor() {
        return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.5 + Math.random() / 2.0), (float)(0.5 + Math.random() / 2.0)));
    }

    public static Pair<Color, Color> computePair() {
        Color WHITE = Color.WHITE;
        Color color = new Color(240, 240, 240);
        return Pair.computePair(WHITE, color);
    }

    public static int computeInt(float f, float f2, float f3, long l) {
        float f4 = (float)((System.currentTimeMillis() + l) % (int)(f * 1000.0F)) / (f * 1000.0F);
        return Color.HSBtoRGB(f4, f2, f3);
    }

    public static Color computeColor(int n, int n2, Color color, Color color2, boolean flag) {
        int n3 = (int)((System.currentTimeMillis() / n + n2) % 360L);
        n3 = (n3 >= 180 ? 360 - n3 : n3) * 2;
        return flag ? computeColor2(color, color2, n3 / 360.0F) : computeColor(color, color2, n3 / 360.0F);
    }

    public static void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, String str, double d, double d2, boolean flag) {
        float f = 0.0F;
        char ch = '\uffff';

        for (int i = 0; i < str.length(); i++) {
            String str2 = String.valueOf(str.charAt(i));
            if (flag) {
                drawStringWithShadowComponent.drawStringWithShadow(str2, (int)(d + f), (int)d2, ch);
            } else {
                drawStringWithShadowComponent.drawString(str2, (int)(d + f), (int)d2, ch);
            }

            f += drawStringWithShadowComponent.getStringWidth(str2) + 0.5F;
        }
    }

    public static double computeDouble(RenderHelper11 renderHelper11) {
        return Math.sin(System.currentTimeMillis() / 600.0 + renderHelper11.getX() * 0.005 + renderHelper11.getY() * 0.06) * 0.5 + 0.5;
    }

    public static int computeInt(float f, float f2, float f3) {
        float f4 = (float)(System.currentTimeMillis() % (int)(f * 1000.0F)) / (f * 1000.0F);
        return Color.HSBtoRGB(f4, f2, f3);
    }

    public static int computeInt(int n, int n2, float f) {
        f = Math.min(1.0F, Math.max(0.0F, f));
        Color color = new Color(n);
        Color color2 = new Color(n2);
        return computeColor(color, color2, f).getRGB();
    }

    public static Color computeColor2(Color color, float f) {
        return new Color(
            Math.max((int)(color.getRed() * f), 0), Math.max((int)(color.getGreen() * f), 0), Math.max((int)(color.getBlue() * f), 0), color.getAlpha()
        );
    }

    public static int computeInt(int n, int n2, float f, float f2) {
        float f3 = (float)((System.currentTimeMillis() + n2) % n);
        f3 /= n;
        return Color.HSBtoRGB(f3, f, f2);
    }

    public static Color computeColor4(Color color, int n) {
        return new Color(color.getRed(), color.getGreen(), n);
    }

    public static int computeInt(Color color, int n) {
        return computeInt(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp_int(n, 0, 255));
    }

    public static Color computeColor(Color color, int n, float f) {
        float f2 = (float)(System.currentTimeMillis() % 8000L) + f;

        while (f2 > n) {
            f2 -= n;
        }

        float f3 = f2 / n;
        if (f3 > 0.5) {
            f3 = 0.5F - (f3 - 0.5F);
        }

        f2 = f3 + 0.5F;
        float[] floats = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(floats[0], floats[1], f2);
    }

    public static int computeInt(EntityLivingBase living) {
        float health = living.getHealth();
        float maxHealth = living.getMaxHealth();
        float f = Math.max(0.0F, Math.min(health, maxHealth) / maxHealth);
        return Color.HSBtoRGB(f / 3.0F, 0.75F, 1.0F) | 0xFF000000;
    }

    public static Color computeColor(Color color, int n) {
        return new Color(n, color.getGreen(), color.getBlue());
    }

    public static Color computeColor(Color color, Color color2, float f) {
        f = Math.min(1.0F, Math.max(0.0F, f));
        return new Color(
            PlayerHelper5.computeInt(color.getRed(), color2.getRed(), f),
            PlayerHelper5.computeInt(color.getGreen(), color2.getGreen(), f),
            PlayerHelper5.computeInt(color.getBlue(), color2.getBlue(), f),
            PlayerHelper5.computeInt(color.getAlpha(), color2.getAlpha(), f)
        );
    }

    public static int computeInt(int n, float f) {
        Color color = new Color(n);
        return computeColor(color, f).getRGB();
    }

    public static int getColor(int n) {
        return computeInt(n, n, n, 255);
    }

    public static int computeInt(int n, int n2, int n3, int n4) {
        int n5 = 0;
        int n6 = MathHelper.clamp_int(n4, 0, 255) << 24;
        n5 = n6 + (n5 & ~n6);
        n5 |= MathHelper.clamp_int(n, 0, 255) << 16;
        n5 |= MathHelper.clamp_int(n2, 0, 255) << 8;
        return n5 | MathHelper.clamp_int(n3, 0, 255);
    }
}
