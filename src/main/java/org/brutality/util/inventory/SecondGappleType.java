package org.brutality.util.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public enum SecondGappleType {
    SECOND_GAPPLE("Second Gapple", Items.golden_apple),
    EXPLICIOUS("Explicious", Items.dye),
    R_AND_R("R&R", Items.golden_carrot),
    ARQUEBUSIER("Arquebusier", Items.arrow),
    KHANATE("Khanate", Items.golden_helmet),
    LEECH("Leech", Items.spawn_egg),
    TOUGH_SKIN("Tough Skin", Items.leather_chestplate),
    FIGHT_OR_FLIGHT("Fight or Flight", Items.fire_charge),
    PUNGENT("Pungent", Items.potionitem),
    HEROS_HASTE("Hero's Haste", Items.enchanted_book),
    RUSH("Rush", Items.sugar),
    FEAST("Feast", Items.mutton),
    COUNTER_STRIKE("Counter-Strike", Items.iron_horse_armor),
    GOLD_NANO_FACTORY("Gold Nano-factory", Items.gold_nugget),
    TACTICAL_RETREAT("Tactical Retreat", Item.getItemFromBlock(Blocks.double_plant)),
    GLASS_PICKAXE("Glass Pickaxe", Items.diamond_pickaxe),
    ASSURED_STRIKE("Assured Strike", Items.cooked_rabbit),
    AURA_OF_PROTECTION("Aura of Protection", Items.slime_ball),
    ICE_CUBE("Ice Cube", Item.getItemFromBlock(Blocks.ice)),
    SUPER_STREAKER("Super Streaker", Items.bread),
    GOLD_STACK("Gold Stack", Item.getItemFromBlock(Blocks.gold_ore)),
    XP_STACK("XP Stack", Item.getItemFromBlock(Blocks.diamond_ore)),
    MONSTER("Monster", Items.apple),
    SPONGESTEVE("Spongesteve", Item.getItemFromBlock(Blocks.sponge)),
    APOSTLE_TO_RNGESUS("Apostle to RNGesus", Item.getItemFromBlock(Blocks.stone_slab)),
    GO_BACK_BUTTON("Go Back", Items.arrow),
    NO_KILLSTREAK_BUTTON("No killstreak", Item.getItemFromBlock(Blocks.gold_block));

    private final Item item2;
    private final String displayName2;

    public Item getItem() {
        return this.item2;
    }

    public String getDisplayName() {
        return this.displayName2;
    }

    private static SecondGappleType[] computeSecondGappleTypeArray() {
        return new SecondGappleType[]{
            SECOND_GAPPLE,
            EXPLICIOUS,
            R_AND_R,
            ARQUEBUSIER,
            KHANATE,
            LEECH,
            TOUGH_SKIN,
            FIGHT_OR_FLIGHT,
            PUNGENT,
            HEROS_HASTE,
            RUSH,
            FEAST,
            COUNTER_STRIKE,
            GOLD_NANO_FACTORY,
            TACTICAL_RETREAT,
            GLASS_PICKAXE,
            ASSURED_STRIKE,
            AURA_OF_PROTECTION,
            ICE_CUBE,
            SUPER_STREAKER,
            GOLD_STACK,
            XP_STACK,
            MONSTER,
            SPONGESTEVE,
            APOSTLE_TO_RNGESUS,
            GO_BACK_BUTTON,
            NO_KILLSTREAK_BUTTON
        };
    }

    private SecondGappleType(String displayName2, Item item2) {
        this.displayName2 = displayName2;
        this.item2 = item2;
    }
}
