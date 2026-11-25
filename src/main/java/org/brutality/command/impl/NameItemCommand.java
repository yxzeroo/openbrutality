package org.brutality.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.brutality.command.Command;
import org.brutality.module.ModuleManager;
import org.brutality.system.ItemNameManager;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.modules.AttemptDenickWithRetriesHelper;

public class NameItemCommand extends Command {
    public NameItemCommand() {
        super(
            "NameItem",
            "Renames the held Pit item by nonce (supports & color codes)",
            ".nameitem <name...> | .nameitem clear",
            "nameitem",
            "rename",
            "setcustomname"
        );
    }

    @Override
    public void onCommand(String[] args, String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            ModuleManager.handleString(MessageUtils.computeString("You must be in-game to use this command!", MessageUtils.text13));
        } else if (args.length == 0) {
            ModuleManager.handleString(MessageUtils.computeString("Usage: .nameitem <name...>  or  .nameitem clear", MessageUtils.text13));
        } else {
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (heldItem == null) {
                ModuleManager.handleString(MessageUtils.computeString("You are not holding any item!", MessageUtils.text13));
            } else {
                Integer integer = heldItem.hasTagCompound() ? AttemptDenickWithRetriesHelper.computeInteger(heldItem.getTagCompound()) : null;
                if (integer == null) {
                    ModuleManager.handleString(MessageUtils.computeString("This item has no Pit nonce — rename won't persist.", MessageUtils.text13));
                } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                    ItemNameManager.handleItemStack2(heldItem);
                    if (heldItem.hasDisplayName()) {
                        heldItem.clearCustomName();
                    }

                    ModuleManager.handleString(MessageUtils.computeString("Cleared rename for nonce " + integer, MessageUtils.text11));
                } else {
                    String str = String.join(" ", args);
                    String str2 = str.replace('&', '§');
                    ItemNameManager.handleItemStack(heldItem, str2);
                    heldItem.setStackDisplayName(str2);
                    ModuleManager.handleString(MessageUtils.computeString("Renamed nonce " + integer + " to: ", MessageUtils.text11) + str2);
                }
            }
        }
    }
}
