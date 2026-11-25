package org.brutality.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.brutality.command.Command;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class ItemNameCommand extends Command {
    public ItemNameCommand() {
        super("ItemName", "Prints the held item's name, registry id, and NBT", ".itemname", "itemname", "item");
    }

    @Override
    public void onCommand(String[] args, String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            ModuleManager.handleString(MessageUtils.computeString("[B] You must be in-game to use this command!", MessageUtils.text13));
        } else {
            ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
            if (currentItem == null) {
                ModuleManager.handleString(MessageUtils.computeString("[B] You are not holding any item!", MessageUtils.text13));
            } else {
                String str = currentItem.getDisplayName().replace('§', '&');
                ResourceLocation nameForObject = Item.itemRegistry.getNameForObject(currentItem.getItem());
                String str2 = nameForObject != null ? nameForObject.toString() : "unknown";
                String str3 = currentItem.hasTagCompound() ? currentItem.getTagCompound().toString().replace('§', '&') : "{}";
                ModuleManager.handleString(
                    MessageUtils.computeString("[B] ", MessageUtils.text7) + str + MessageUtils.computeString(" (" + str2 + ")", MessageUtils.text8)
                );
                ModuleManager.handleString(MessageUtils.computeString("[B] NBT: ", MessageUtils.text7) + MessageUtils.computeString(str3, MessageUtils.text8));
            }
        }
    }
}
