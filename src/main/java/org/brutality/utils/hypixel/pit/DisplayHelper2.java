package org.brutality.utils.hypixel.pit;

import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class DisplayHelper2 {
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
    private static final List<String> entries2 = Arrays.asList(
        "Ace of Spades",
        "Brakes!",
        "Combo: Breaching Charge",
        "Do it like the French",
        "Heigh-Ho",
        "New Deal",
        "Really Toxic",
        "Sybil",
        "Regularity",
        "Think of the People"
    );

    public static DisplayHelper2.NoneMode computeNoneMode(EntityPlayer player) {
        if (player == null) {
            return DisplayHelper2.NoneMode.NONE;
        }
        if (matchesEntityPlayer(player, entries2)) {
            return DisplayHelper2.NoneMode.RAGE;
        }
        return matchesEntityPlayer(player, entries) ? DisplayHelper2.NoneMode.DARK : DisplayHelper2.NoneMode.NONE;
    }

    private static boolean matchesEntityPlayer(EntityPlayer player, List<String> list) {
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

                            for (String str : list) {
                                if (stringTagAt.contains(str)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private DisplayHelper2() {
    }

    public static enum NoneMode {
        NONE,
        DARK,
        RAGE;

        private static DisplayHelper2.NoneMode[] computeNoneModeArray() {
            return new DisplayHelper2.NoneMode[]{NONE, DARK, RAGE};
        }
    }
}
