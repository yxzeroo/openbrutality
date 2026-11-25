package org.brutality.anticheat.check;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.anticheat.data.DataHandler;

public class NoSlowCheck extends Check {
    private static final long timestamp = -2499676399405629430L;

    @Override
    public void onUpdate(EntityPlayer player) {
        if (player != mc.thePlayer) {
            DataHandler dataHandler = this.computeDataHandler(player);
            if (dataHandler != null) {
                boolean sprinting = player.isSprinting();
                boolean usingItem = player.isUsingItem();
                boolean flag = player.ridingEntity != null;
                ItemStack heldItem = player.getHeldItem();
                if (usingItem && sprinting && !flag) {
                    String str = "unknown";
                    if (heldItem != null) {
                        Item item = heldItem.getItem();
                        if (item instanceof ItemFood) {
                            str = "food";
                        } else if (item instanceof ItemPotion) {
                            str = "potion";
                        } else if (item instanceof ItemBow) {
                            str = "bow";
                        } else if (item instanceof ItemSword) {
                            str = "sword";
                        }
                    }

                    double d = this.computeDouble(dataHandler.computeDouble2(), dataHandler.computeDouble3());
                    this.handleEntityPlayer(player, 1.0, String.format("no-slowdown | item=%s | speed=%.2f | sprinting=true", str, d));
                } else if (this.computeDouble(player) > 0.0) {
                    this.handleEntityPlayer(player, 0.3);
                }
            }
        }
    }

    public NoSlowCheck() {
        super("NoSlow", "Detects illegal movement while using items");
        this.count = (int)timestamp;
    }
}
