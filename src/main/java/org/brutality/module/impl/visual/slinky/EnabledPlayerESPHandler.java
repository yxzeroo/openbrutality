package org.brutality.module.impl.visual.slinky;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.ArialHelper;
import org.brutality.utils.render.InterfaceHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.input.Mouse;

public final class EnabledPlayerESPHandler {
    private static final long timestamp = 230L;
    private static final String text = "BRUTAL";
    private static final List<EnabledPlayerESPHandler.Entry> entries = new ArrayList<>();
    private static float factor = 12.0F;
    private static float factor2 = 12.0F;
    private static float factor3 = 12.0F;
    private static float factor4 = 12.0F;
    private static float factor5 = 12.0F;
    private static float factor6 = 12.0F;
    private static float factor7 = 12.0F;
    private static float factor8 = 12.0F;
    private static boolean dragging;
    private static float factor9;
    private static float factor10;
    private static boolean flag;

    public static void handleMinecraft(Minecraft mc, int n, long l, float f, int n2, boolean flag2) {
        if (mc.thePlayer != null) {
            long now = System.currentTimeMillis();
            boolean flag3 = mc.currentScreen instanceof GuiChat;
            boolean flag4 = flag3 && flag2;
            if (!entries.isEmpty()) {
                Iterator it = entries.iterator();

                while (it.hasNext()) {
                    EnabledPlayerESPHandler.Entry entry = (EnabledPlayerESPHandler.Entry)it.next();
                    if (entry.timestamp2 > 0L) {
                        if (now - entry.timestamp2 > 230L) {
                            it.remove();
                        }
                    } else if (now - entry.timestamp > l) {
                        entry.timestamp2 = now;
                    }
                }
            }

            if (!entries.isEmpty() || flag4) {
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
                int scaledWidth = sr.getScaledWidth();
                int scaledHeight = sr.getScaledHeight();
                CustomFontRenderer drawStringWithShadowComponent = ArialHelper.computeDrawStringWithShadowComponent(18.0F * f);
                float f2 = 0.78F * f;
                float f3 = 0.76F * f;
                float f4 = 5.0F * f;
                float f5 = 22.5F * f;
                float f6 = 13.5F * f;
                float f7 = f5 + 4.0F * f;
                boolean flag5 = n2 == 0 || n2 == 2;
                boolean flag6 = n2 == 0 || n2 == 1;
                String str = InterfaceHelper.computeString2(n);
                int n3 = InterfaceHelper.computeInt(str, 0L, now);
                int n4 = flag4 ? 1 : entries.size();

                for (int i = 0; i < n4; i++) {
                    String str2 = flag4 ? "Enabled Player ESP" : entries.get(i).text;
                    long l2 = flag4 ? now : entries.get(i).timestamp;
                    long l3 = now - l2;
                    float f8 = flag4 ? 1.0F : Math.min(1.0F, (float)l3 / 210.0F);
                    float f9 = 1.0F;
                    if (!flag4 && entries.get(i).timestamp2 > 0L) {
                        f9 = 1.0F - Math.min(1.0F, (float)(now - entries.get(i).timestamp2) / 230.0F);
                    }

                    float f10 = computeFloat(Math.min(f8, f9));
                    float f11 = drawStringWithShadowComponent.getStringWidth("BRUTAL") * f3 + 12.0F * f;
                    float f12 = drawStringWithShadowComponent.getStringWidth(str2) * f2;
                    float f13 = f4 + f11 + 2.5F * f + f12 + 8.0F * f;
                    float f14 = flag5 ? factor : factor3;
                    float f15 = flag6 ? factor2 : factor4;
                    if (n2 == 2) {
                        f14 = factor5;
                        f15 = factor6;
                    } else if (n2 == 3) {
                        f14 = factor7;
                        f15 = factor8;
                    }

                    float f16 = flag5 ? scaledWidth - f13 - f14 : f14;
                    float f17 = flag5 ? scaledWidth + f13 + 8.0F : -f13 - 8.0F;
                    float f18 = f17 + (f16 - f17) * f10;
                    float f19 = flag6 ? scaledHeight - f15 - f5 : f15;
                    float f20 = flag6 ? f19 - i * f7 : f19 + i * f7;
                    float f21 = f18 + f4;
                    float f22 = f20 + (f5 - f6) / 2.0F;
                    float f23 = f21 + f11 + 2.5F * f;
                    float f24 = f20 + (f5 - drawStringWithShadowComponent.getHeight() * f2) / 2.0F + 0.45F * f;
                    ResetColorHelper.handleFloat2(f18, f20, f18 + f13, f20 + f5, f5 / 2.0F, InterfaceHelper.computeInt2(-267909110, f10));
                    ResetColorHelper.handleFloat2(
                        f21, f22, f21 + f11, f22 + f6, Math.max(3.0F, f6 * 0.34F), InterfaceHelper.computeInt2(1428887051, f10)
                    );
                    drawStringWithShadowComponent.computeInt3(
                        "BRUTAL",
                        f21 + (f11 - drawStringWithShadowComponent.getStringWidth("BRUTAL") * f3) / 2.0F,
                        f22 + (f6 - drawStringWithShadowComponent.getHeight() * f3) / 2.0F,
                        InterfaceHelper.computeInt2(n3, f10)
                    );
                    drawStringWithShadowComponent.computeInt3(str2, f23, f24, InterfaceHelper.computeInt2(-2960686, f10));
                }

                if (flag4 && flag2 && Mouse.isButtonDown(0)) {
                    int[] ints = new int[]{Mouse.getX(), Mouse.getY()};
                    float f25 = ints[0] / sr.getScaleFactor();
                    float f26 = scaledHeight - ints[1] / sr.getScaleFactor();
                    if (!dragging && !flag) {
                        dragging = true;
                        factor9 = f25;
                        factor10 = f26;
                    }

                    flag = true;
                } else {
                    dragging = false;
                    flag = false;
                }
            }
        }
    }

    private static float computeFloat(float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        float f2 = 1.0F - f;
        return 1.0F - f2 * f2 * f2;
    }

    public static void handleString(String str, boolean flag) {
        handleString((flag ? "Enabled " : "Disabled ") + str);
    }

    private EnabledPlayerESPHandler() {
    }

    public static void handleString(String str) {
        EnabledPlayerESPHandler.Entry entry = new EnabledPlayerESPHandler.Entry();
        entry.text = str;
        entry.timestamp = System.currentTimeMillis();
        entries.add(0, entry);
        int n = 0;

        for (EnabledPlayerESPHandler.Entry entry2 : entries) {
            if (!entry2.flag) {
                n++;
            }
        }

        for (int i = entries.size() - 1; n > 7 && i >= 0; i--) {
            EnabledPlayerESPHandler.Entry entry3 = entries.get(i);
            if (!entry3.flag) {
                entry3.timestamp2 = System.currentTimeMillis();
                n--;
            }
        }
    }

    public static void clear() {
        entries.clear();
    }

    public static class Entry {
        public String text;
        public long timestamp;
        public boolean flag;
        public long timestamp2;
    }
}
