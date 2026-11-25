package org.brutality.module;

public enum Category {
    COMBAT,
    MOVEMENT,
    PLAYER,
    VISUAL,
    MISCELLANEOUS,
    EXPLOIT,
    PIT,
    CLIENT,
    SCRIPTS,
    HIDDEN;

    private static Category[] computeCategoryArray() {
        return new Category[]{COMBAT, MOVEMENT, PLAYER, VISUAL, MISCELLANEOUS, EXPLOIT, PIT, CLIENT, SCRIPTS, HIDDEN};
    }
}
