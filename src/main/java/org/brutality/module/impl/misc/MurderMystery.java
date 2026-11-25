package org.brutality.module.impl.misc;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;

public class MurderMystery extends Module {
    public static int[] itemIds = new int[]{288, 396, 412, 398, 75, 50};
    public static Item[] itemArray = new Item[]{
        Items.fishing_rod,
        Items.diamond_hoe,
        Items.golden_hoe,
        Items.iron_hoe,
        Items.stone_hoe,
        Items.wooden_hoe,
        Items.stone_sword,
        Items.diamond_sword,
        Items.golden_sword,
        ItemBlock.getItemFromBlock(Blocks.sponge),
        Items.iron_sword,
        Items.wooden_sword,
        Items.diamond_axe,
        Items.golden_axe,
        Items.iron_axe,
        Items.stone_axe,
        Items.diamond_pickaxe,
        Items.wooden_axe,
        Items.golden_pickaxe,
        Items.iron_pickaxe,
        Items.stone_pickaxe,
        Items.wooden_pickaxe,
        Items.stone_shovel,
        Items.diamond_shovel,
        Items.golden_shovel,
        Items.iron_shovel,
        Items.wooden_shovel
    };
    public static ArrayList<EntityPlayer> entries = new ArrayList<>();

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventTick) {
                    for (Entity entity : mc.theWorld.loadedEntityList) {
                        if (entity instanceof EntityLivingBase living && living instanceof EntityPlayer && entity != mc.thePlayer) {
                            EntityPlayer player = (EntityPlayer)living;
                            if (player.inventory.getCurrentItem() != null) {
                                if (!entries.contains(player)) {
                                    if (this.matchesItem(player.inventory.getCurrentItem().getItem())) {
                                        MessageUtils.x("[MurderDetector] " + player.getName() + " is Killer!");
                                        NotificationFeedHandler.handleString("§c[Murder Detector] " + player.getName() + " is the killer!");
                                        entries.add(player);
                                    }
                                } else if (!this.matchesItem(player.inventory.getCurrentItem().getItem())) {
                                    entries.remove(player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean matchesItem(Item item) {
        for (int itemId : itemIds) {
            Item itemById = Item.getItemById(itemId);
            if (item == itemById) {
                return true;
            }
        }

        for (Item item2 : itemArray) {
            if (item == item2) {
                return true;
            }
        }

        return false;
    }

    public MurderMystery() {
        super("Murder Mystery", 0, Category.MISCELLANEOUS);
    }
}
