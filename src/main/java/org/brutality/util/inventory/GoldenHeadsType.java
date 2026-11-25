package org.brutality.util.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public enum GoldenHeadsType {
    GOLDEN_HEADS("Golden Heads", Items.skull),
    FISHING_ROD("Fishing Rod", Items.fishing_rod),
    LAVA_BUCKET("Lava Bucket", Items.lava_bucket),
    STRENGTH_CHAINING("Strength Chain", Items.redstone),
    SAFETY_FIRST("Safety First", Items.chainmail_helmet),
    BARBARIAN("Barbarian", Items.iron_axe),
    MINEMAN("Mineman", Item.getItemFromBlock(Blocks.cobblestone)),
    BONK("Bonk", Item.getItemFromBlock(Blocks.anvil)),
    TRICKLE_DOWN("Trickle Down", Items.gold_ingot),
    LUCKY_DIAMONDS("Lucky Diamond", Items.diamond),
    SPAMMER("Spammer", Items.bow),
    BOUNTY_HUNTER("Bounty Hunter", Items.golden_leggings),
    STREAKER("Streaker", Items.wheat),
    ASSISTANT_STREAKER("Assistant Streaker", Item.getItemFromBlock(Blocks.spruce_fence)),
    CO_OP_CAT("Co-Op Cat", Items.spawn_egg),
    CONGLOMERATE("Conglomerate", Item.getItemFromBlock(Blocks.hay_block)),
    GLADIATOR("Gladiator", Items.bone),
    VAMPIRE("Vampire", Items.fermented_spider_eye),
    RECON("Recon", Items.ender_eye),
    OVERHEAL("Overheal", Items.bread),
    RAMBO("Rambo", Items.stick),
    OLYMPUS("Olympus", Items.potionitem),
    DIRTY("Dirty", Item.getItemFromBlock(Blocks.dirt)),
    FIRST_STRIKE("First Strike", Items.cooked_chicken),
    SOUP("Soup", Items.mushroom_stew),
    MARATHON("Marathon", Items.leather_boots),
    THICK("Thick", Items.apple),
    KUNG_FU_KNOWLEDGE("Kung Fu Knowledge", Items.beef),
    NO_PERK_SLOT("No Perk Slot", Item.getItemFromBlock(Blocks.diamond_block), false),
    GO_BACK_SLOT("Go Back Slot", Items.arrow, false);

    private final boolean selectable;
    private final String displayName2;
    private final Item item2;

    private GoldenHeadsType(String displayName2, Item item2) {
        this.displayName2 = displayName2;
        this.item2 = item2;
        this.selectable = true;
    }

    public String getDisplayName() {
        return this.displayName2;
    }

    public static GoldenHeadsType computeGoldenHeadsType(Item item) {
        for (GoldenHeadsType goldenHeadsType : (GoldenHeadsType[])values().clone()) {
            if (goldenHeadsType.getItem() == item) {
                return goldenHeadsType;
            }
        }

        return null;
    }

    private GoldenHeadsType(String displayName2, Item item2, boolean selectable) {
        this.displayName2 = displayName2;
        this.item2 = item2;
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

    public Item getItem() {
        return this.item2;
    }

    private static GoldenHeadsType[] computeGoldenHeadsTypeArray() {
        return new GoldenHeadsType[]{
            GOLDEN_HEADS,
            FISHING_ROD,
            LAVA_BUCKET,
            STRENGTH_CHAINING,
            SAFETY_FIRST,
            BARBARIAN,
            MINEMAN,
            BONK,
            TRICKLE_DOWN,
            LUCKY_DIAMONDS,
            SPAMMER,
            BOUNTY_HUNTER,
            STREAKER,
            ASSISTANT_STREAKER,
            CO_OP_CAT,
            CONGLOMERATE,
            GLADIATOR,
            VAMPIRE,
            RECON,
            OVERHEAL,
            RAMBO,
            OLYMPUS,
            DIRTY,
            FIRST_STRIKE,
            SOUP,
            MARATHON,
            THICK,
            KUNG_FU_KNOWLEDGE,
            NO_PERK_SLOT,
            GO_BACK_SLOT
        };
    }
}
