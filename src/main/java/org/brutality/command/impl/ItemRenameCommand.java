package org.brutality.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.command.Command;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class ItemRenameCommand extends Command {
    public ItemRenameCommand() {
        super("ItemRename", "Displays the Pit rename (first lore line) of the held item", ".itemrename", "itemrename");
    }

    private static String computeString(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("display");
            if (!compoundTag.hasKey("Lore")) {
                return "";
            }
            NBTTagList tagList = compoundTag.getTagList("Lore", 8);
            if (tagList.tagCount() == 0) {
                return "";
            }
            String stringTagAt = tagList.getStringTagAt(0);
            return EnumChatFormatting.getTextWithoutFormattingCodes(stringTagAt);
        }
        return "";
    }

    @Override
    public void onCommand(String[] args, String message) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null) {
            ModuleManager.handleString(MessageUtils.computeString("[B] - You are not holding any item!", MessageUtils.text13));
        } else {
            String str = computeString(heldItem);
            String str2 = heldItem.getDisplayName().replace('§', '&');
            String str3;
            if (str.isEmpty()) {
                str3 = String.format("%s%s (no Pit rename found)", MessageUtils.computeString("[B] ", MessageUtils.text7), str2);
            } else {
                str3 = String.format("%s%s -> %s", MessageUtils.computeString("[B] ", MessageUtils.text7), str2, str.replace('§', '&'));
            }

            ModuleManager.handleString(str3);
        }
    }
}
