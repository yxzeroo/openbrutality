package org.brutality.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.core.SetScaleApi;
import org.brutality.core.SortSettingsHandler;

public final class RenderHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void handleSortSettingsHandler2(SortSettingsHandler sortSettingsHandler, String str, int n, int n2, int n3) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        handleSortSettingsHandler(sortSettingsHandler);
        fontRenderer.drawString(str, n, n2, n3);
        process();
    }

    public static void handleSortSettingsHandler(SortSettingsHandler sortSettingsHandler, String str, int n) {
        handleSortSettingsHandler(sortSettingsHandler, str, 0, 0, n);
    }

    public static int computeInt(SortSettingsHandler sortSettingsHandler, String str) {
        return sortSettingsHandler instanceof SetScaleApi
            ? (int)(mc.fontRendererObj.getStringWidth(str) * ((SetScaleApi)sortSettingsHandler).getScale())
            : mc.fontRendererObj.getStringWidth(str);
    }

    public static int computeInt(SortSettingsHandler sortSettingsHandler) {
        return sortSettingsHandler instanceof SetScaleApi ? (int)(mc.fontRendererObj.FONT_HEIGHT * ((SetScaleApi)sortSettingsHandler).getScale()) : mc.fontRendererObj.FONT_HEIGHT;
    }

    public static void process() {
        GlStateManager.popMatrix();
    }

    public static void handleSortSettingsHandler(SortSettingsHandler sortSettingsHandler, String str) {
        handleSortSettingsHandler(sortSettingsHandler, str, 0, 0, 16777215);
    }

    public static void handleSortSettingsHandler(SortSettingsHandler sortSettingsHandler, String str, int n, int n2) {
        handleSortSettingsHandler(sortSettingsHandler, str, n, n2, 16777215);
    }

    public static void handleSortSettingsHandler(SortSettingsHandler sortSettingsHandler, int n, int n2, int n3, int n4, int n5) {
        handleSortSettingsHandler(sortSettingsHandler);
        Gui.drawRect(n, n2, n3, n4, n5);
        process();
    }

    public static void handleSortSettingsHandler(SortSettingsHandler sortSettingsHandler) {
        GlStateManager.pushMatrix();
        if (sortSettingsHandler instanceof SetScaleApi setScaleApi) {
            float scale = setScaleApi.getScale();
            GlStateManager.translate((float)setScaleApi.getX(), (float)setScaleApi.getY(), 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
        }
    }

    public static void handleSortSettingsHandler(SortSettingsHandler sortSettingsHandler, String str, int n, int n2, int n3) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        handleSortSettingsHandler(sortSettingsHandler);
        fontRenderer.drawStringWithShadow(str, n, n2, n3);
        process();
    }

    private RenderHelper() {
    }
}
