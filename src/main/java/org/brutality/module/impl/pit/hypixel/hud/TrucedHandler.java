package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.ArrayListHelper;
import org.brutality.utils.render.RenderHelper2;
import org.lwjgl.opengl.GL11;

public final class TrucedHandler {
    public static final String[] stringArray = new String[]{"Player", "HP", "Location", "Relationship", "Pit Enchant", "Distance"};
    private static final int count = 5;
    private static final int count2 = 8;

    public static TrucedHandler.Entry computeEntry(RenderHelper2 renderHelper2, List<TrucedHandler.Entry3> entry3s, int n) {
        return computeEntry(renderHelper2, entry3s, n, 0.65F);
    }

    public static TrucedHandler.Entry computeEntry(RenderHelper2 renderHelper2, List<TrucedHandler.Entry3> entry3s, int n, float f) {
        float f2 = f / 0.65F;
        int n2 = Math.max(3, Math.round(5.0F * f2));
        int n3 = Math.max(3, Math.round(8.0F * f2));
        int n4 = Math.max(6, Math.round(10.0F * f));
        int n5 = n4 + Math.max(2, Math.round(4.0F * f));
        int[] ints = new int[6];
        int[] ints2 = new int[]{112, 46, 112, 100, 220, 70};

        for (int i = 0; i < 6; i++) {
            int n6 = i == 0 ? n5 : 0;
            ints[i] = renderHelper2.computeInt(stringArray[i]) + n6;

            for (TrucedHandler.Entry3 entry3 : entry3s) {
                ints[i] = Math.max(
                    ints[i], Math.min(i == 3 ? Integer.MAX_VALUE : Math.round(ints2[i] * f2), renderHelper2.computeInt(entry3.stringArray[i])) + n6
                );
            }
        }

        int n7 = n2 * 2 + n3 * 5;

        for (int n8 : ints) {
            n7 += n8;
        }

        for (int n9 : new int[]{4, 2, 0}) {
            int n10 = Math.min(Math.max(0, n7 - n), Math.max(0, ints[n9] - renderHelper2.computeInt(stringArray[n9]) - (n9 == 0 ? n5 : 0)));
            ints[n9] -= n10;
            n7 -= n10;
        }

        int n11 = Math.max(n4 + 4, (int)Math.ceil(renderHelper2.computeFloat()) + Math.max(3, Math.round(5.0F * f2)));
        int n12 = n11 * 2 + n2 * 2;
        int[] ints3 = new int[entry3s.size()];
        ArrayList list = new ArrayList();

        for (int j = 0; j < entry3s.size(); j++) {
            List list2 = computeList(renderHelper2, ((TrucedHandler.Entry3)entry3s.get(j)).stringArray[4], ints[4]);
            list.add(list2);
            ints3[j] = Math.max(n11, list2.size() * (n11 - 2) + 2);
            n12 += ints3[j];
        }

        if (entry3s.isEmpty()) {
            n12 += n11;
        }

        return new TrucedHandler.Entry(n7, n12, n11, ints, ints3, list, n2, n3, n4, n5);
    }

    public static int computeInt(boolean flag, boolean flag2, boolean flag3) {
        return flag2 ? -5636096 : (flag3 ? -16733696 : (flag ? -16777046 : -1));
    }

    public static String computeString(boolean flag, boolean flag2, boolean flag3) {
        ArrayList list = new ArrayList();
        if (flag) {
            list.add("§1Truced");
        }

        if (flag2) {
            list.add("§4KOS");
        }

        if (flag3) {
            list.add("§2Friends");
        }

        return list.isEmpty() ? "§7--" : String.join("§7/", list);
    }

    public static int computeInt(float f) {
        return f <= 20.0F ? -43691 : (f <= 30.0F ? -16733696 : -11141291);
    }

    public static void handleRenderHelper2(RenderHelper2 renderHelper2, TrucedHandler.Entry2 entry2, float f, float f2) {
        TrucedHandler.Entry entry = entry2.entry;
        ArrayListHelper.handleFloat(f, f2, entry.count, entry.count2, 3.0F, new Color(12, 14, 22, 185));
        renderHelper2.handleString(entry2.text2 + ": (" + entry2.entries.size() + ")", f + entry.count4, f2 + entry.count4, entry.count3, entry2.count);
        float f3 = f2 + entry.count4 + entry.count3;

        for (int i = 0; i < 6; i++) {
            renderHelper2.handleString(stringArray[i], f + entry.computeInt(i) + (i == 0 ? entry.count7 : 0), f3, entry.count3, -5392696);
        }

        float f4 = f3 + entry.count3;
        if (entry2.entries.isEmpty()) {
            renderHelper2.handleString("No matching players", f + entry.count4, f4, entry.count3, -7300692);
        } else {
            for (int j = 0; j < entry2.entries.size(); j++) {
                TrucedHandler.Entry3 entry3 = entry2.entries.get(j);
                int n = entry.intValueArray2[j];
                if ((j & 1) == 0) {
                    Gui.drawRect((int)f + 2, (int)f4, (int)f + entry.count - 2, (int)f4 + n, 352321535);
                }

                Minecraft mc = Minecraft.getMinecraft();
                if (mc.getTextureManager() != null) {
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(entry3.resourceLocation);
                    Gui.drawScaledCustomSizeModalRect(
                        (int)(f + entry.count4), (int)(f4 + (n - entry.count6) / 2), 8.0F, 8.0F, 8, 8, entry.count6, entry.count6, 64.0F, 64.0F
                    );
                    Gui.drawScaledCustomSizeModalRect(
                        (int)(f + entry.count4), (int)(f4 + (n - entry.count6) / 2), 40.0F, 8.0F, 8, 8, entry.count6, entry.count6, 64.0F, 64.0F
                    );
                }

                for (int k = 0; k < 6; k++) {
                    if (k == 4) {
                        int n2 = 0;

                        for (String str : entry.entries.get(j)) {
                            renderHelper2.handleString(str, f + entry.computeInt(k), f4 + n2++ * (entry.count3 - 2), entry.count3, entry3.intValueArray[k]);
                        }
                    } else {
                        renderHelper2.handleString(
                            renderHelper2.computeString(entry3.stringArray[k], entry.intValueArray[k] - (k == 0 ? entry.count7 : 0)),
                            f + entry.computeInt(k) + (k == 0 ? entry.count7 : 0),
                            f4,
                            n,
                            entry3.intValueArray[k]
                        );
                    }
                }

                f4 += n;
            }
        }
    }

    private static List<String> computeList(RenderHelper2 renderHelper2, String str, int n) {
        ArrayList list = new ArrayList();
        String str2 = "";

        for (String str3 : str.split("§7/", -1)) {
            String str4 = str2.isEmpty() ? str3 : str2 + "§7/" + str3;
            if (renderHelper2.computeInt(str4) <= n) {
                str2 = str4;
            } else {
                if (!str2.isEmpty()) {
                    list.add(str2);
                    str2 = "";
                }

                String str5 = str3.startsWith("§") && str3.length() > 1 ? str3.substring(0, 2) : "";
                String str6 = str5.isEmpty() ? str3 : str3.substring(2);

                while (renderHelper2.computeInt(str5 + str6) > n && str6.length() > 1) {
                    int n2 = 1;

                    while (n2 < str6.length() && renderHelper2.computeInt(str5 + str6.substring(0, n2 + 1)) <= n) {
                        n2++;
                    }

                    int n3 = str6.lastIndexOf(32, n2);
                    if (n3 > 0) {
                        n2 = n3;
                    }

                    list.add(str5 + str6.substring(0, n2));
                    str6 = str6.substring(n2).trim();
                }

                str2 = str5 + str6;
            }
        }

        if (!str2.isEmpty()) {
            list.add(str2);
        }

        if (list.isEmpty()) {
            list.add("§7None");
        }

        return list;
    }

    private TrucedHandler() {
    }

    public static String computeString(String str) {
        if (str != null && !str.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch == 167 && i + 1 < str.length()) {
                    char ch2 = str.charAt(++i);
                    if (ch2 != 'l' && ch2 != 'L') {
                        sb.append('§').append(ch2);
                    }
                } else {
                    sb.append(Character.toUpperCase(ch));
                }
            }

            return sb.toString().replaceAll("\\bMID\\b", "PIT");
        }
        return "§7UNKNOWN";
    }

    public static String computeString(float f, float f2, float f3) {
        String str = f >= 16.0F ? "§a" : (f >= 8.0F ? "§6" : "§c");
        String str2 = String.format(Locale.ROOT, "%.0f", Math.max(0.0F, f) / Math.max(1.0F, f3) * 100.0F);
        return f2 > 0.0F ? str + str2 + "§7/§e" + String.format(Locale.ROOT, "%.0f%%", f2 / Math.max(1.0F, f3) * 100.0F) : str + str2 + "%";
    }

    public static final class Entry {
        public final int count;
        public final int count2;
        public final int count3;
        public final int count4;
        public final int count5;
        public final int count6;
        public final int count7;
        public final int[] intValueArray;
        public final int[] intValueArray2;
        public final List<List<String>> entries;

        public Entry(int count, int n, int n2, int[] intValueArray, int[] intValueArray2, List<List<String>> entries, int n3, int n4, int n5, int n6) {
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
            this.intValueArray = intValueArray;
            this.intValueArray2 = intValueArray2;
            this.entries = entries;
            this.count4 = n3;
            this.count5 = n4;
            this.count6 = n5;
            this.count7 = n6;
        }

        public int computeInt(int n) {
            int n2 = this.count4;

            for (int i = 0; i < n; i++) {
                n2 += this.intValueArray[i] + this.count5;
            }

            return n2;
        }
    }

    public static final class Entry2 {
        public final String text;
        public final String text2;
        public final int count;
        public final NumberSetting numberSetting;
        public final NumberSetting numberSetting2;
        public final List<TrucedHandler.Entry3> entries;
        public final TrucedHandler.Entry entry;

        public Entry2(String text, String str, int count, NumberSetting numberSetting, NumberSetting numberSetting2, List<TrucedHandler.Entry3> entries, TrucedHandler.Entry entry) {
            this.text = text;
            this.text2 = str;
            this.count = count;
            this.numberSetting = numberSetting;
            this.numberSetting2 = numberSetting2;
            this.entries = entries;
            this.entry = entry;
        }
    }

    public static final class Entry3 {
        public final String[] stringArray;
        public final int[] intValueArray;
        public final ResourceLocation resourceLocation;

        public Entry3(String[] stringArray, int[] intValueArray, ResourceLocation resourceLocation) {
            this.stringArray = stringArray;
            this.intValueArray = intValueArray;
            this.resourceLocation = resourceLocation;
        }

        public Entry3(String[] parts, int[] ints) {
            this(parts, ints, new ResourceLocation("textures/entity/steve.png"));
        }
    }
}
