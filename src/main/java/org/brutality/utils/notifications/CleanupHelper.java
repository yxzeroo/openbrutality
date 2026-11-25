package org.brutality.utils.notifications;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CleanupHelper {
    private static final long timestamp = 1000L;
    private static final Map<UUID, CleanupHelper.Entry> lookupMap = new ConcurrentHashMap<>();
    private static long in = 0L;
    private static final List<String> entries = Arrays.asList(
        "Venom",
        "Misery",
        "Spite",
        "Needless Suffering",
        "Grim Reaper",
        "Nostalgia",
        "Hedge Fund",
        "Heartripper",
        "Sanguisuge",
        "Lycanthropy",
        "Mind Assault",
        "Golden Handcuffs"
    );
    private static final List<String> entries2 = Arrays.asList("Retro", "Mirror");

    public static void clear() {
        lookupMap.clear();
    }

    private static CleanupHelper.Entry computeEntry(EntityPlayer player) {
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        int totalArmorValue = player.getTotalArmorValue();

        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                        for (int j = 0; j < tagList.tagCount(); j++) {
                            String stringTagAt = tagList.getStringTagAt(j);
                            if (str == null) {
                                for (String str5 : entries) {
                                    if (stringTagAt.contains(str5)) {
                                        str = str5;
                                        break;
                                    }
                                }
                            }

                            if (str2 == null) {
                                for (String str6 : entries2) {
                                    if (stringTagAt.contains(str6)) {
                                        str2 = str6;
                                        break;
                                    }
                                }
                            }

                            if (str3 == null && stringTagAt.contains("Regularity")) {
                                str3 = "Regularity";
                            }

                            if (str4 == null && stringTagAt.contains("escape_pod")) {
                                str4 = "escape_pod";
                            }
                        }
                    }
                }
            }
        }

        return new CleanupHelper.Entry(str, str2, str3, str4, totalArmorValue);
    }

    public static void cleanup() {
        lookupMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public static CleanupHelper.Entry computeEntry2(EntityPlayer player) {
        UUID uniqueID = player.getUniqueID();
        CleanupHelper.Entry entry = lookupMap.get(uniqueID);
        if (entry != null && !entry.isExpired()) {
            return entry;
        }
        CleanupHelper.Entry entry2 = computeEntry(player);
        lookupMap.put(uniqueID, entry2);
        long now = System.currentTimeMillis();
        if (now - in > 5000L) {
            cleanup();
            in = now;
        }

        return entry2;
    }

    public static void handleUUID(UUID uuid) {
        lookupMap.remove(uuid);
    }

    public static class Entry {
        public final String text;
        public final String text2;
        public final String text3;
        public final String text4;
        public final long timestamp;
        public final int count;
        private static final long ip = 1000L;

        public Entry(String text, String str, String str2, String str3, int count) {
            this.text = text;
            this.text2 = str;
            this.text3 = str2;
            this.text4 = str3;
            this.count = count;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - this.timestamp > ip;
        }
    }
}
