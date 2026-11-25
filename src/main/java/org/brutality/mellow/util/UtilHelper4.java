package org.brutality.mellow.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.mint.ResourceTracker;

public class UtilHelper4 {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static boolean matchesInt(int n, boolean flag) {
        return !flag ? true : n != 0;
    }

    private static int computeInt(float f, float f2, int n, int n2, int n3, int n4, int n5, int n6, int n7, String str, String str2) {
        String str3 = computeString(n);
        String str4 = computeString(n2);
        String str5 = computeString(n3);
        if (n > 0 && n2 <= 0) {
            mc.fontRendererObj.drawStringWithShadow(str3, f, f2, n4);
            return mc.fontRendererObj.getStringWidth(str3);
        }
        if (n <= 0 && n2 > 0) {
            mc.fontRendererObj.drawStringWithShadow(str4, f, f2, n5);
            return mc.fontRendererObj.getStringWidth(str4);
        }
        if (n <= 0 && n2 <= 0) {
            mc.fontRendererObj.drawStringWithShadow("0", f, f2, n6);
            return mc.fontRendererObj.getStringWidth("0");
        }
        mc.fontRendererObj.drawStringWithShadow(str3, f, f2, n4);
        float f3 = f + mc.fontRendererObj.getStringWidth(str3);
        mc.fontRendererObj.drawStringWithShadow(str, f3, f2, n7);
        f3 += mc.fontRendererObj.getStringWidth(str);
        mc.fontRendererObj.drawStringWithShadow(str4, f3, f2, n5);
        f3 += mc.fontRendererObj.getStringWidth(str4);
        mc.fontRendererObj.drawStringWithShadow(str2, f3, f2, n7);
        f3 += mc.fontRendererObj.getStringWidth(str2);
        mc.fontRendererObj.drawStringWithShadow(str5, f3, f2, n6);
        f3 += mc.fontRendererObj.getStringWidth(str5);
        return (int)(f3 - f);
    }

    private static List<UtilHelper4.Entry> computeList(boolean flag, boolean flag2, boolean flag3, boolean flag4, boolean flag5, boolean flag6) {
        List list = new ArrayList();
        int n = ResourceTracker.count + ResourceTracker.count5;
        int n2 = ResourceTracker.count2 + ResourceTracker.count6;
        int n3 = ResourceTracker.count3 + ResourceTracker.count7;
        int n4 = ResourceTracker.count4 + ResourceTracker.count8;
        if (flag && matchesInt(n, flag6)) {
            list.add(new UtilHelper4.Entry(new ItemStack(Items.iron_ingot), ResourceTracker.count, ResourceTracker.count5, n));
        }

        if (flag2 && matchesInt(n2, flag6)) {
            list.add(new UtilHelper4.Entry(new ItemStack(Items.gold_ingot), ResourceTracker.count2, ResourceTracker.count6, n2));
        }

        if (flag3 && matchesInt(n3, flag6)) {
            list.add(new UtilHelper4.Entry(new ItemStack(Items.diamond), ResourceTracker.count3, ResourceTracker.count7, n3));
        }

        if (flag4 && matchesInt(n4, flag6)) {
            list.add(new UtilHelper4.Entry(new ItemStack(Items.emerald), ResourceTracker.count4, ResourceTracker.count8, n4));
        }

        return list;
    }

    private static int computeInt(
        int n,
        int n2,
        float f,
        float f2,
        int n3,
        int n4,
        UtilHelper4.Entry entry,
        boolean flag,
        int n5,
        int n6,
        int n7,
        int n8,
        String str,
        String str2
    ) {
        int FONT_HEIGHT = mc.fontRendererObj.FONT_HEIGHT;
        float f3 = 18.0F * f;
        float f4 = n2 + (f3 - 16.0F * f2) / 2.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)n, f4, 0.0F);
        GlStateManager.scale(f2, f2, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(entry.itemStack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        float f5 = n + n3 + n4;
        float f6 = n2 + (f3 - FONT_HEIGHT * f) / 2.0F;
        int n9 = flag ? n5 : 16777215;
        int n10 = flag ? n6 : 16777215;
        int n11 = flag ? n7 : 16777215;
        int n12 = flag ? n8 : 16777215;
        int n13 = computeInt(f5, f6, entry.count, entry.count2, entry.count3, n9, n10, n11, n12, str, str2);
        return n3 + n4 + n13;
    }

    public static void handleInt(
        int n,
        int n2,
        float f,
        boolean flag,
        boolean flag2,
        boolean flag3,
        boolean flag4,
        int n3,
        int n4,
        int n5,
        int n6,
        boolean flag5,
        boolean flag6,
        boolean flag7,
        boolean flag8,
        String str,
        String str2
    ) {
        if (mc != null && mc.fontRendererObj != null) {
            float f2 = f * 0.9F;
            int n7 = Math.round(18.0F * f);
            int n8 = Math.round(16.0F * f2);
            int n9 = Math.round(2.0F * f);
            int n10 = Math.round(3.0F * f);
            var list = computeList(flag5, flag6, flag7, flag8, flag2, flag3);
            if (!list.isEmpty()) {
                if (flag) {
                    int n11 = n;

                    for (int i = 0; i < list.size(); i++) {
                        UtilHelper4.Entry entry = (UtilHelper4.Entry)list.get(i);
                        int n12 = computeInt(n11, n2, f, f2, n8, n9, entry, flag4, n3, n4, n5, n6, str, str2);
                        n11 += n12;
                        if (i != list.size() - 1) {
                            n11 += n10;
                        }
                    }
                } else {
                    int n13 = 0;

                    for (UtilHelper4.Entry entry2 : list) {
                        computeInt(n, n2 + n13 * n7, f, f2, n8, n9, entry2, flag4, n3, n4, n5, n6, str, str2);
                        n13++;
                    }
                }
            }
        }
    }

    private static String computeString(int n) {
        if (n < 1000) {
            return String.valueOf(n);
        }
        double d = n / 1000.0;
        if (d < 10.0) {
            double d2 = Math.round(d * 10.0) / 10.0;
            String str = String.valueOf(d2);
            if (str.endsWith(".0")) {
                str = str.substring(0, str.length() - 2);
            }

            return str + "k";
        }
        long l = Math.round(d);
        return l + "k";
    }

    public static class Entry {
        public final ItemStack itemStack;
        public final int count;
        public final int count2;
        public final int count3;

        public Entry(ItemStack itemStack, int count, int n, int n2) {
            this.itemStack = itemStack;
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
        }
    }
}
