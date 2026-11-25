package org.brutality.module.impl.pit.hypixel.utility;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.Brutality;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;

public final class ChunkOfVileHelper {
    private static final String[] stringArray = new String[]{
        "Chunk of Vile", "Fresh Pants", "Mystic Sword", "Mystic Bow", "Philosopher's Cactus", "Pants Bundle"
    };
    private static final int[] intValueArray = new int[]{1, 3, 9, 5, 10, 30};
    private static final Pattern pattern = Pattern.compile("(?i)^([0-9]{1,2})x\\s+(Red|Blue|Green|Yellow|Orange|Aqua)\\s*$");
    private static WeakReference<GuiContainer> weakReference = new WeakReference<>(null);
    private static int count;
    private static long[] longValueArray = new long[stringArray.length];
    private static long[] longValueArray2 = new long[stringArray.length];

    private static void handleItemStack(ItemStack stack) {
        if (stack != null && stack.stackSize > 0) {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            if (textWithoutFormattingCodes != null) {
                String str = textWithoutFormattingCodes.trim().toLowerCase(Locale.ROOT);

                int n = switch (str) {
                    case "chunk of vile" -> stack.getItem() == Items.coal ? 0 : -1;
                    case "fresh pants" -> 1;
                    case "mystic sword" -> 2;
                    case "mystic bow" -> 3;
                    case "philosopher's cactus" -> 4;
                    case "pants bundle" -> stack.getItem() == Items.minecart ? 5 : -1;
                    default -> str.startsWith("fresh ") && str.endsWith(" pants") ? 1 : -1;
                };
                if (n >= 0) {
                    longValueArray[n] = longValueArray[n] + stack.stackSize;
                    longValueArray2[n] = longValueArray2[n]
                        + (long)stack.stackSize * (n == 5 ? computeInt(stack) * intValueArray[1] : intValueArray[n]);
                }
            }
        }
    }

    private static void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, String str, int n, int n2) {
        int n3 = str.indexOf(" / ");
        String str2 = n3 >= 0 ? str.substring(0, n3) : (str.startsWith("$") ? "" : str);
        String str3 = n3 >= 0 ? str.substring(n3) : (str.startsWith("$") ? str : "");
        int n4 = n - drawStringWithShadowComponent.getStringWidth(str);
        if (!str2.isEmpty()) {
            drawStringWithShadowComponent.drawString(str2, n4, n2, -867221);
        }

        if (!str3.isEmpty()) {
            drawStringWithShadowComponent.drawString(str3, n4 + drawStringWithShadowComponent.getStringWidth(str2), n2, -8399206);
        }
    }

    private ChunkOfVileHelper() {
    }

    public static void handleGuiContainer(GuiContainer gui, int n, int n2, int n3, int n4, int n5) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null
            && mc.theWorld != null
            && FreshHelper.flag
            && Brutality.moduleManager != null
            && Brutality.moduleManager.computeModule("PitUtils") instanceof PitUtils pitUtils
            && pitUtils.checkState44()
            && pitUtils.checkState9()) {
            boolean flag = gui instanceof GuiInventory;
            boolean flag2 = false;
            net.minecraft.inventory.IInventory iInventory;
            if (flag) {
                if (!pitUtils.checkState5()) {
                    return;
                }

                iInventory = mc.thePlayer.inventory;
            } else {
                if (!(gui instanceof GuiChest) || !(gui.inventorySlots instanceof ContainerChest containerChest)) {
                    return;
                }

                if (!pitUtils.checkState8()) {
                    return;
                }

                iInventory = containerChest.getLowerChestInventory();
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(iInventory.getDisplayName().getUnformattedText());
                boolean flag3 = textWithoutFormattingCodes != null && textWithoutFormattingCodes.toLowerCase(Locale.ROOT).contains("ender chest");
                boolean flag4 = textWithoutFormattingCodes == null || textWithoutFormattingCodes.trim().isEmpty();
                boolean flag5 = mc.objectMouseOver != null
                    && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK
                    && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.ender_chest;
                flag2 = !flag3 && !flag5 && !flag4;
                if (flag2 && !pitUtils.checkState2()) {
                    return;
                }
            }

            int ticksExisted = mc.thePlayer.ticksExisted;
            if (weakReference.get() != gui || ticksExisted != count) {
                weakReference = new WeakReference<>(gui);
                count = ticksExisted;
                longValueArray = new long[stringArray.length];
                longValueArray2 = new long[stringArray.length];
                int n6 = flag ? mc.thePlayer.inventory.mainInventory.length : iInventory.getSizeInventory();

                for (int i = 0; i < n6; i++) {
                    handleItemStack(iInventory.getStackInSlot(i));
                }
            }

            boolean flag6 = pitUtils.checkState6();
            boolean flag7 = pitUtils.checkState4();
            ArrayList<String[]> list = new ArrayList<>();
            long l = 0L;

            for (int j = 0; j < stringArray.length; j++) {
                l += longValueArray2[j];
                if (longValueArray[j] > 0L) {
                    list.add(new String[]{stringArray[j], "x" + longValueArray[j], computeString(longValueArray2[j], flag6, flag7)});
                }
            }

            if (l != 0L || !flag2) {
                String str = computeString(l, flag6, flag7);
                CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(15, false);
                CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(13, false);
                byte b = 8;
                int n7 = drawStringWithShadowComponent.FONT_HEIGHT + 3;
                byte b2 = 14;
                int n8 = 0;
                int textWidth = drawStringWithShadowComponent.getStringWidth(str);

                for (String[] parts : list) {
                    n8 = Math.max(n8, drawStringWithShadowComponent.getStringWidth(parts[0] + "  " + parts[1]));
                    textWidth = Math.max(textWidth, drawStringWithShadowComponent.getStringWidth(parts[2]));
                }

                n8 = Math.max(n8, Math.max(drawStringWithShadowComponent.getStringWidth("Total"), drawStringWithShadowComponent2.getStringWidth("PIT INVENTORY VALUE") - textWidth - b2));
                int n9 = b * 2 + n8 + b2 + textWidth;
                int n10 = b + drawStringWithShadowComponent2.FONT_HEIGHT + 6 + list.size() * n7 + (list.isEmpty() ? 0 : 5) + n7 + b - 3;
                int n11 = pitUtils.matchesFlag(flag) ? n + n3 + 6 : n - n9 - 6;
                int n12 = n2;
                if (pitUtils.checkState() || n11 < 4 || n11 + n9 > n4 - 4) {
                    n11 = (n4 - n9) / 2;
                    n12 = n2 + 5;
                }

                int n13 = Math.max(2, n11);
                int n14 = Math.max(2, Math.min(n12, n5 - n10 - 2));

                try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    GlStateManager.translate(0.0F, 0.0F, 300.0F);
                    if (OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect2 != null) {
                        OutlineThicknessHelper.handleFloat(n13, n14, n9, n10, 5.0F, 0.5F, new Color(8, 9, 13, 150), new Color(255, 255, 255, 26));
                    } else {
                        Gui.drawRect(n13, n14, n13 + n9, n14 + n10, -1777858034);
                    }

                    GlStateManager.enableTexture2D();
                    int n15 = n14 + b;
                    drawStringWithShadowComponent2.drawString("PIT INVENTORY VALUE", n13 + b, n15, -7695453);
                    int n16 = n15 + drawStringWithShadowComponent2.FONT_HEIGHT + 6;

                    for (String[] parts2 : ((Iterable<String[]>)(Object)(list))) {
                        drawStringWithShadowComponent.drawString(parts2[0], n13 + b, n16, -1644049);
                        drawStringWithShadowComponent.drawString(parts2[1], n13 + b + drawStringWithShadowComponent.getStringWidth(parts2[0] + "  "), n16, -8550762);
                        handleDrawStringWithShadowComponent(drawStringWithShadowComponent, parts2[2], n13 + n9 - b, n16);
                        n16 += n7;
                    }

                    if (!list.isEmpty()) {
                        Gui.drawRect(n13 + b, n16, n13 + n9 - b, n16 + 1, 587202559);
                        GlStateManager.enableTexture2D();
                        n16 += 5;
                    }

                    drawStringWithShadowComponent.drawString("Total", n13 + b, n16, -1);
                    handleDrawStringWithShadowComponent(drawStringWithShadowComponent, str, n13 + n9 - b, n16);
                }
            }
        }
    }

    private static int computeInt(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 10;
        }
        NBTTagList tagList = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
        int n = 0;

        for (int i = 0; i < tagList.tagCount(); i++) {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(tagList.getStringTagAt(i));
            Matcher matcher = pattern.matcher(textWithoutFormattingCodes.trim());
            if (matcher.matches()) {
                n += Integer.parseInt(matcher.group(1));
            }
        }

        return n == 0 ? 10 : Math.min(n, 10);
    }

    private static String computeString(long l, boolean flag, boolean flag2) {
        String str = flag ? l + " vile" : "";
        String str2 = flag2 ? String.format(Locale.ROOT, "$%.2f–$%.2f", l * 5.0 / 64.0, l * 6.0 / 64.0) : "";
        return str + (flag && flag2 ? " / " : "") + str2;
    }
}
