package org.brutality.util.inventory;

public enum PerkSlot1Type {
    PERK_SLOT_1(12),
    PERK_SLOT_2(13),
    PERK_SLOT_3(14),
    PERK_SLOT_4(15),
    XP_BOOST(28),
    GOLD_BOOST(29),
    MELEE_DAMAGE(30),
    BOW_DAMAGE(31),
    DAMAGE_REDUCTION(32),
    BUILD_BATTLER(33),
    EL_GATO(34);

    private final int menuSlot;

    private PerkSlot1Type(int menuSlot) {
        this.menuSlot = menuSlot;
    }

    public int getMenuSlot() {
        return this.menuSlot;
    }

    private static PerkSlot1Type[] computePerkSlot1TypeArray() {
        return new PerkSlot1Type[]{
            PERK_SLOT_1, PERK_SLOT_2, PERK_SLOT_3, PERK_SLOT_4, XP_BOOST, GOLD_BOOST, MELEE_DAMAGE, BOW_DAMAGE, DAMAGE_REDUCTION, BUILD_BATTLER, EL_GATO
        };
    }
}
