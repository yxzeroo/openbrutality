package org.brutality.gui.font;

import java.awt.Font;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.brutality.utils.performance.InvalidateHelper;

public final class FontComponent {
    private static CustomFontRenderer drawStringWithShadowComponent;
    private static CustomFontRenderer drawStringWithShadowComponent2;
    private static CustomFontRenderer drawStringWithShadowComponent3;
    private static int count = -1;
    private static int baseSize = -1;
    private static int count2 = -1;
    private static final Map<Integer, CustomFontRenderer> lookupMap = new LinkedHashMap<>(32, 0.75F, true);
    private static final int count3 = 32;
    private static final String text = "SansSerif";

    public static CustomFontRenderer computeDrawStringWithShadowComponent2(int n, boolean flag) {
        int n2 = n * 2 + (flag ? 1 : 0);
        CustomFontRenderer drawStringWithShadowComponent = lookupMap.get(n2);
        if (drawStringWithShadowComponent != null) {
            return drawStringWithShadowComponent;
        }
        CustomFontRenderer drawStringWithShadowComponent2 = computeDrawStringWithShadowComponent(n, flag, true);
        if (drawStringWithShadowComponent2 != null) {
            lookupMap.put(n2, drawStringWithShadowComponent2);
            if (lookupMap.size() > 32) {
                Iterator it = lookupMap.values().iterator();
                CustomFontRenderer drawStringWithShadowComponent3 = (CustomFontRenderer)it.next();
                it.remove();
                drawStringWithShadowComponent3.process();
            }
        }

        return drawStringWithShadowComponent2;
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent3() {
        if (drawStringWithShadowComponent3 == null) {
            drawStringWithShadowComponent3 = computeDrawStringWithShadowComponent2(11, false);
        }

        return drawStringWithShadowComponent3;
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent(int n, boolean flag, boolean flag2) {
        try {
            return new CustomFontRenderer(new Font(text, flag ? 1 : 0, n), true, flag2);
        } catch (Exception e) {
            return null;
        }
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent2() {
        if (drawStringWithShadowComponent == null) {
            drawStringWithShadowComponent = computeDrawStringWithShadowComponent2(20, true);
        }

        return drawStringWithShadowComponent;
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent(int n, boolean flag) {
        return computeDrawStringWithShadowComponent(n, flag, true);
    }

    public static void handleInt(int count3, int baseSize2, int n) {
        if (drawStringWithShadowComponent == null || count3 != count) {
            drawStringWithShadowComponent = computeDrawStringWithShadowComponent2(count3, true);
            count = count3;
        }

        if (drawStringWithShadowComponent2 == null || baseSize2 != baseSize) {
            drawStringWithShadowComponent2 = computeDrawStringWithShadowComponent2(baseSize2, false);
            baseSize = baseSize2;
        }

        if (drawStringWithShadowComponent3 == null || n != count2) {
            drawStringWithShadowComponent3 = computeDrawStringWithShadowComponent2(n, false);
            count2 = n;
        }
    }

    private FontComponent() {
    }

    public static void handleInt(int n, int n2) {
        InvalidateHelper.invalidate();
        byte b = 15;
        byte b2 = 11;
        byte b3 = 19;
        handleInt(b3, b, b2);
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent() {
        if (drawStringWithShadowComponent2 == null) {
            drawStringWithShadowComponent2 = computeDrawStringWithShadowComponent2(15, false);
        }

        return drawStringWithShadowComponent2;
    }
}
