package org.brutality.utils.hypixel.pit;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

public final class RichestHelper {
    private static final Pattern pattern = Pattern.compile("^Event:\\s*Pizza!?$", 2);
    private static final Pattern pattern2 = Pattern.compile("^Remaining:\\s*(\\d+:\\d{2})$", 2);
    private static final Pattern pattern3 = Pattern.compile("^Wallet:\\s*\\$\\s*([0-9][0-9,]*)$", 2);
    private static final Pattern pattern4 = Pattern.compile("^Total deliveries:\\s*([0-9,]+)\\s*/\\s*([0-9,]+)$", 2);
    private static final Pattern pattern5 = Pattern.compile("^Pizza!?\\s*\\$\\s*([0-9][0-9,]*)$", 2);
    private static final Pattern pattern6 = Pattern.compile("\\b\\d+\\s*P\\s*/\\s*\\$\\s*([0-9][0-9,]*)\\s*$", 2);
    private static final Pattern pattern7 = Pattern.compile("^Pizza\\s*#\\d+$", 2);
    private static final Pattern pattern8 = Pattern.compile("&[0-9a-fk-or]", 2);

    private static String computeString(String str) {
        return str == null ? "" : pattern8.matcher(EnumChatFormatting.getTextWithoutFormattingCodes(str)).replaceAll("").trim();
    }

    public static int computeInt(ItemStack stack) {
        if (stack != null && stack.getItem() == Items.skull && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagList tagList = tagCompound.getCompoundTag("display").getTagList("Lore", 8);

            for (int i = 0; i < tagList.tagCount(); i++) {
                if (pattern7.matcher(computeString(tagList.getStringTagAt(i))).matches()) {
                    return stack.stackSize;
                }
            }

            String str = computeString(stack.getDisplayName()).toLowerCase(Locale.ROOT);
            return !"EVENT_ITEM".equals(tagCompound.getCompoundTag("ExtraAttributes").getString("ITEM_MARKERS")) || !str.equals("pizza") && !str.endsWith(" pizza")
                ? 0
                : stack.stackSize;
        }
        return 0;
    }

    public static RichestHelper.ImEntry computeImEntry(List<String> list) {
        boolean flag = false;
        String str = "—";
        int n = -1;
        String str2 = "—";

        for (String str3 : list) {
            String str4 = computeString(str3);
            if (pattern.matcher(str4).matches()) {
                flag = true;
            } else {
                Matcher matcher = pattern2.matcher(str4);
                Matcher matcher2 = pattern3.matcher(str4);
                Matcher matcher3 = pattern4.matcher(str4);
                if (matcher.matches()) {
                    str = matcher.group(1);
                } else if (matcher2.matches()) {
                    n = computeInt(matcher2.group(1));
                } else if (matcher3.matches()) {
                    str2 = matcher3.group(1) + "/" + matcher3.group(2);
                }
            }
        }

        return flag ? new RichestHelper.ImEntry(str, n, str2) : null;
    }

    public static int computeInt2(String str) {
        Matcher matcher = pattern6.matcher(computeString(str));
        return matcher.find() ? computeInt(matcher.group(1)) : -1;
    }

    public static int computeInt3(String str) {
        Matcher matcher = pattern5.matcher(computeString(str));
        return matcher.matches() ? computeInt(matcher.group(1)) : -1;
    }

    private RichestHelper() {
    }

    private static int computeInt(String str) {
        try {
            return Integer.parseInt(str.replace(",", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static List<RichestHelper.L1I1Entry> richest(Collection<RichestHelper.L1I1Entry> l1I1Entries, int n) {
        return l1I1Entries.stream()
            .filter(l1I1Entry -> l1I1Entry.bJo() > 0)
            .sorted(
                Comparator.comparingInt(RichestHelper.L1I1Entry::bJo)
                    .reversed()
                    .thenComparing(RichestHelper.L1I1Entry::io, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(RichestHelper.L1I1Entry::l1I1)
            )
            .limit(Math.max(1, Math.min(10, n)))
            .toList();
    }

    public record ImEntry(String im, int bJn, String in2) {
        public String in() {
            return this.in2;
        }
    }

    public record L1I1Entry(UUID l1I1, String io, int bJo) {
    }
}
