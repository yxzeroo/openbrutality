package org.brutality.utils.render;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.CustomFontRenderer;

public final class ArialHelper {
    private static final ResourceLocation resourceLocation = new ResourceLocation("brutality/font/inter.18pt-semibold.ttf");
    private static Font font;
    private static final Map<Integer, CustomFontRenderer> lookupMap = new HashMap<>();
    private static CustomFontRenderer drawStringWithShadowComponent;

    private ArialHelper() {
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent() {
        return drawStringWithShadowComponent != null ? drawStringWithShadowComponent : computeDrawStringWithShadowComponent2(18.0F);
    }

    private static Font computeFont() {
        if (font != null) {
            return font;
        }
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());
        } catch (Exception e) {
            font = new Font("Arial", 0, 18);
        }

        return font;
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent2(float f) {
        int n = Math.round(f * 10.0F);
        CustomFontRenderer drawStringWithShadowComponent2 = lookupMap.get(n);
        if (drawStringWithShadowComponent2 != null) {
            return drawStringWithShadowComponent2;
        }
        try {
            Font font = computeFont().deriveFont(f);
            drawStringWithShadowComponent2 = new CustomFontRenderer(font, true, true);
        } catch (Exception e) {
            drawStringWithShadowComponent2 = new CustomFontRenderer(new Font("Arial", 0, (int)f), true, true);
        }

        lookupMap.put(n, drawStringWithShadowComponent2);
        if (Math.abs(f - 18.0F) < 0.05F) {
            drawStringWithShadowComponent = drawStringWithShadowComponent2;
        }

        return drawStringWithShadowComponent2;
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent(float f) {
        float f2 = Math.round(f * 10.0F) / 10.0F;
        return computeDrawStringWithShadowComponent2(f2);
    }
}
