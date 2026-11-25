package org.brutality.utils.render;

import java.awt.Font;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.Dialog;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.utils.performance.InvalidateHelper;

public final class FontResourceLoader {
    private static final Map<String, Font> lookupMap = new HashMap<>();
    private static final Map<String, CustomFontRenderer> lookupMap2 = new HashMap<>();
    private static Dialog dialog;
    private static int count;

    private static Font computeFont(String str) {
        Font font = lookupMap.get(str);
        if (font != null) {
            return font;
        }
        try {
            font = Font.createFont(
                0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("brutality/font/" + str)).getInputStream()
            );
        } catch (Exception e) {
            font = new Font("Arial", 0, 18);
        }

        lookupMap.put(str, font);
        return font;
    }

    public static void process2() {
        count = Math.max(0, count - 1);
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent3(String str, float f) {
        if (IsChoiceHelper.matchesString(str)) {
            return computeDrawStringWithShadowComponent();
        }
        if (count > 0) {
            return computeDrawStringWithShadowComponent4(str, f);
        }
        String str2 = "file:" + str;
        String str3 = str2 + "@" + Math.round(f * 10.0F);
        CustomFontRenderer drawStringWithShadowComponent = lookupMap2.get(str3);
        if (drawStringWithShadowComponent != null) {
            return drawStringWithShadowComponent;
        }
        Font font = computeFont(str).deriveFont(f);
        drawStringWithShadowComponent = new CustomFontRenderer(font, true, true);
        lookupMap2.put(str3, drawStringWithShadowComponent);
        return drawStringWithShadowComponent;
    }

    public static void process() {
        count++;
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent5(String str, float f) {
        int n = Math.max(1, InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft()).getScaleFactor());
        int n2 = Math.max(6, Math.round(f * n / 2.0F));
        String str2 = IsChoiceHelper.computeString(str);
        String str3 = "screen:" + str2 + "@" + n2 + "x" + n;
        CustomFontRenderer drawStringWithShadowComponent = lookupMap2.get(str3);
        if (drawStringWithShadowComponent == null) {
            drawStringWithShadowComponent = new CustomFontRenderer(computeFont2(str).deriveFont(2.0F * n2 / n), n);
            lookupMap2.put(str3, drawStringWithShadowComponent);
        }

        return drawStringWithShadowComponent;
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent() {
        if (dialog == null || !dialog.matchesFontRenderer(Minecraft.getMinecraft().fontRendererObj)) {
            dialog = new Dialog(Minecraft.getMinecraft().fontRendererObj);
        }

        return dialog;
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent(String str, float f) {
        return computeDrawStringWithShadowComponent3(computeString(str), f);
    }

    private static String computeString(String str) {
        if (IsChoiceHelper.isChoice(str)) {
            return IsChoiceHelper.computeString(str);
        }
        if ("SF Pro".equalsIgnoreCase(str) || "SF-Pro".equalsIgnoreCase(str)) {
            return IsChoiceHelper.computeString("Brutality");
        }
        if (str == null) {
            return "brutality.ttf";
        }
        String str2 = str.toLowerCase(Locale.ROOT);
        if (str2.contains("bold")) {
            return "brutality-bold.ttf";
        }
        if (str2.contains("san francisco") || str2.equals("sf")) {
            return "sf.ttf";
        }
        if (str2.contains("comfortaa")) {
            return "comfortaa-light.ttf";
        }
        if (str2.contains("product")) {
            return str2.contains("sans") ? "product-sans.ttf" : "productsans.ttf";
        }
        if (str2.contains("inter")) {
            return "inter.18pt-semibold.ttf";
        }
        return str2.equals("brutality") ? "brutality.ttf" : "brutality.ttf";
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent4(String str, float f) {
        if (IsChoiceHelper.matchesString(str)) {
            return computeDrawStringWithShadowComponent();
        }
        String str2 = "world:" + str + "@" + Math.round(f * 10.0F);
        CustomFontRenderer drawStringWithShadowComponent = lookupMap2.get(str2);
        if (drawStringWithShadowComponent == null) {
            drawStringWithShadowComponent = new CustomFontRenderer(computeFont(str).deriveFont(f), true, true, 8);
            lookupMap2.put(str2, drawStringWithShadowComponent);
        }

        return drawStringWithShadowComponent;
    }

    private FontResourceLoader() {
    }

    public static Font computeFont2(String str, float f) {
        return computeFont(computeString(str), f);
    }

    public static Font computeFont(String str, float f) {
        return computeFont(str).deriveFont(f);
    }

    private static String computeString2(String str) {
        if (IsChoiceHelper.isChoice(str)) {
            return IsChoiceHelper.computeString(str);
        }
        if ("SF Pro".equalsIgnoreCase(str) || "SF-Pro".equalsIgnoreCase(str)) {
            return IsChoiceHelper.computeString("Brutality");
        }
        if ("Quan Pro".equalsIgnoreCase(str)) {
            return "quan-pro-regular.otf";
        }
        if ("inter.18pt-semibold".equalsIgnoreCase(str)) {
            return "inter.18pt-semibold.ttf";
        }
        if ("productsans".equalsIgnoreCase(str)) {
            return "productsans.ttf";
        }
        if ("brutality-bold".equalsIgnoreCase(str)) {
            return "brutality-bold.ttf";
        }
        if (str != null && !str.isEmpty()) {
            String str2 = str.toLowerCase(Locale.ROOT);
            return !str2.endsWith(".ttf") && !str2.endsWith(".otf") ? str + ".ttf" : str;
        }
        return "brutality.ttf";
    }

    private static Font computeFont2(String str) {
        return computeFont(computeString2(str));
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent2(String str, float f) {
        if (IsChoiceHelper.matchesString(str)) {
            return computeDrawStringWithShadowComponent();
        }
        if (count > 0) {
            return computeDrawStringWithShadowComponent4(computeString2(str), f);
        }
        String str2 = str + "@" + Math.round(f * 10.0F);
        CustomFontRenderer drawStringWithShadowComponent = lookupMap2.get(str2);
        if (drawStringWithShadowComponent != null) {
            return drawStringWithShadowComponent;
        }
        Font font = computeFont2(str).deriveFont(f);
        drawStringWithShadowComponent = new CustomFontRenderer(font, true, true);
        lookupMap2.put(str2, drawStringWithShadowComponent);
        return drawStringWithShadowComponent;
    }
}
