package org.brutality.gui.font.atlas;

public final class ClassicFontRegistry {
    public static final AtlasFontRenderer drawStringWithShadowComponent = new AtlasFontRenderer(
        ArialComponent.computeFont("productsans.ttf", 18), true, false
    );
    public static final AtlasFontRenderer drawStringWithShadowComponent2 = new AtlasFontRenderer(
        ArialComponent.computeFont("productsans.ttf", 15), true, false
    );
    public static final AtlasFontRenderer drawStringWithShadowComponent3 = new AtlasFontRenderer(
        ArialComponent.computeFont("classic-bold.ttf", 18), true, false
    );
    public static final AtlasFontRenderer drawStringWithShadowComponent4 = new AtlasFontRenderer(
        ArialComponent.computeFont("classic-icons.ttf", 12), true, false
    );
    private static boolean initialized;

    private ClassicFontRegistry() {
    }

    public static void ensureInitialized() {
        if (!initialized) {
            drawStringWithShadowComponent.process2();
            drawStringWithShadowComponent2.process2();
            drawStringWithShadowComponent3.process2();
            drawStringWithShadowComponent4.process2();
            initialized = true;
        }
    }
}
