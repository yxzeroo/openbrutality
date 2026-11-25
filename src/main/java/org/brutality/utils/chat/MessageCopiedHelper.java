package org.brutality.utils.chat;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.impl.visual.Chat;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.lwjgl.opengl.GL11;

public final class MessageCopiedHelper {
    private static final long timestamp = 220L;
    private static final long timestamp2 = 2200L;
    private static final long timestamp3 = 420L;
    private static final int count = 22;
    private static final int count2 = 12;
    private static final int count3 = 10;
    private static final int count4 = 3;
    private static final float factor = 18.0F;
    private static String text;
    private static long timestamp4 = 0L;

    private MessageCopiedHelper() {
    }

    public static void handleString(String str) {
        if (str != null && !str.trim().isEmpty()) {
            String str2 = EnumChatFormatting.getTextWithoutFormattingCodes(str).trim();
            if (str2.length() > 48) {
                str2 = str2.substring(0, 45) + "...";
            }

            text = str2;
            timestamp4 = System.currentTimeMillis();
        } else {
            text = "Message copied";
            timestamp4 = System.currentTimeMillis();
        }
    }

    private static float computeFloat(float f) {
        return f * f * f;
    }

    public static void handleScaledResolution(ScaledResolution sr) {
        if (text != null) {
            long l = System.currentTimeMillis() - timestamp4;
            long l2 = 2840L;
            if (l >= l2) {
                text = null;
            } else {
                BrutalityHelper brutalityHelper = computeBrutalityHelper();
                String str = brutalityHelper != null ? brutalityHelper.getText() : "inter.18pt-semibold";
                CustomFontRenderer drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(str, 18.0F);
                CustomFontRenderer drawStringWithShadowComponent2 = FontResourceLoader.computeDrawStringWithShadowComponent2(str, 14.0F);
                String str2 = "Copied to clipboard";
                String str3 = text;
                int textWidth = drawStringWithShadowComponent.getStringWidth(str2);
                int textWidth2 = drawStringWithShadowComponent2.getStringWidth(str3);
                int n = Math.max(textWidth, textWidth2);
                int n2 = n + 20 + 3 + 6;
                int n3 = (int)drawStringWithShadowComponent.getFONT_HEIGHT() + (int)drawStringWithShadowComponent2.getFONT_HEIGHT() + 14;
                float f;
                int n4;
                if (l <= 220L) {
                    float f2 = (float)l / 220.0F;
                    f2 = computeFloat2(f2);
                    f = f2;
                    n4 = (int)(-22.0F * (1.0F - f2));
                } else if (l <= 2420L) {
                    f = 1.0F;
                    n4 = 0;
                } else {
                    long l3 = l - 2420L;
                    float f3 = Math.min(1.0F, (float)l3 / 420.0F);
                    f3 = computeFloat(f3);
                    f = 1.0F - f3;
                    n4 = (int)(12.0F * f3);
                }

                int n5 = 8 + n4;
                int n6 = sr.getScaledHeight() / 2 - n3 / 2 - 20;
                int n7 = Math.max(0, Math.min(255, (int)(f * 255.0F)));
                int n8 = brutalityHelper != null ? ModuleSpecificHelper.computeInt(brutalityHelper, BrutalityHelper::getColor5, n7) : n7 << 24 | 1053720;
                int n9 = brutalityHelper != null ? ModuleSpecificHelper.computeInt(brutalityHelper, BrutalityHelper::getColor, n7) : n7 << 24 | 5636061;
                int n10 = n7 * 120 / 255 << 24 | 16777215;
                int n11 = brutalityHelper != null ? ModuleSpecificHelper.computeInt(brutalityHelper, BrutalityHelper::getColor6, n7) : n7 << 24 | 15137023;
                int n12 = brutalityHelper != null ? ModuleSpecificHelper.computeInt(brutalityHelper, BrutalityHelper::getColor7, n7) : n7 << 24 | 10528696;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                Gui.drawRect(n5, n6, n5 + n2, n6 + n3, n8);
                Gui.drawRect(n5, n6, n5 + 3, n6 + n3, n9);
                Gui.drawRect(n5, n6, n5 + n2, n6 + 1, n10);
                Gui.drawRect(n5, n6 + n3 - 1, n5 + n2, n6 + n3, n10);
                int n13 = n5 + 3 + 10;
                int n14 = n6 + 6;
                int n15 = n14 + (int)drawStringWithShadowComponent.getFONT_HEIGHT() + 3;
                drawStringWithShadowComponent.drawStringWithShadow(str2, n13, n14, n11);
                drawStringWithShadowComponent2.drawStringWithShadow(str3, n13, n15, n12);
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }
        }
    }

    private static BrutalityHelper computeBrutalityHelper() {
        Chat instance = Chat.getInstance();
        return instance != null && instance.checkState44() && instance.checkState()
            ? ModuleSpecificHelper.computeBrutalityHelper()
            : ModuleSpecificHelper.computeBrutalityHelper();
    }

    private static float computeFloat2(float f) {
        float f2 = 1.0F - f;
        return 1.0F - f2 * f2 * f2;
    }
}
