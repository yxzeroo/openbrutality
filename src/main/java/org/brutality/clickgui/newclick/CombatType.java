package org.brutality.clickgui.newclick;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
// VENDOR-DISABLED: import org.brutality.system.EntitlementState;

public enum CombatType {
    COMBAT("Combat", Category.COMBAT, null, null, CombatType.MainMode.MAIN, false),
    MOVEMENT("Movement", Category.MOVEMENT, null, null, CombatType.MainMode.MAIN, false),
    PLAYER("Player", Category.PLAYER, null, null, CombatType.MainMode.MAIN, false),
    VISUAL("Visual", Category.VISUAL, null, null, CombatType.MainMode.MAIN, false),
    MISCELLANEOUS("MISCELLANEOUS", Category.MISCELLANEOUS, null, null, CombatType.MainMode.MAIN, false),
    EXPLOIT("Exploit", Category.EXPLOIT, null, null, CombatType.MainMode.MAIN, false),
    PIT("Pit", null, null, null, CombatType.MainMode.MAIN, true),
    CLOUD("Cloud", null, null, null, CombatType.MainMode.MAIN, true),
    CLIENT("Client", Category.CLIENT, null, null, CombatType.MainMode.MAIN, false),
    PIT_AUTOMATION("Automation", Category.PIT, PitCategory.AUTOMATION, null, CombatType.MainMode.PIT, false),
    PIT_HUD("HUD", Category.PIT, PitCategory.HUD, null, CombatType.MainMode.PIT, false),
    PIT_SWAP("Swap", Category.PIT, PitCategory.SWAP, null, CombatType.MainMode.PIT, false),
    PIT_UTILITY("Utility", Category.PIT, PitCategory.UTILITY, null, CombatType.MainMode.PIT, false),
    PIT_MAJOR_EVENT("Major Events", Category.PIT, PitCategory.MAJOR_EVENT, null, CombatType.MainMode.PIT, false),
    PIT_MINOR_EVENT("Minor Events", Category.PIT, PitCategory.MINOR_EVENT, null, CombatType.MainMode.PIT, false),
    PIT_PREMIUM("Premium", Category.PIT, PitCategory.PREMIUM, null, CombatType.MainMode.PIT, false),
    BACK("Back", null, null, null, CombatType.MainMode.PIT, true);

    private final String label;
    private final Category baseCategory;
    private final PitCategory pitCategory;
    private final String moduleNameKeyword;
    private final CombatType.MainMode page;
    private final boolean navigationOnly;

    public boolean checkState3() {
        return this == CLOUD;
    }

    public List<Module> getModules(String str, boolean flag, HypixelPitType hypixelPitType) {
        if (this.navigationOnly) {
            return Collections.emptyList();
        }
        String str2 = str == null ? "" : str.trim().toLowerCase(Locale.ROOT);
        return ModuleManager.getEntries2()
            .stream()
            .filter(module -> module.getCategory() != Category.HIDDEN)
            .filter(module2 -> this.matchesModule(module2, flag))
            .filter(module3 -> matchesModule(module3, hypixelPitType))
            .filter(
                module4 -> str2.isEmpty() || module4.getName().toLowerCase(Locale.ROOT).contains(str2) || this.label.toLowerCase(Locale.ROOT).contains(str2)
            )
            .sorted(Comparator.comparing(Module::getName))
            .collect(Collectors.toList());
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isNavigationOnly() {
        return this.navigationOnly;
    }

    private boolean matchesModule(Module module, boolean flag) {
        if (this.baseCategory == null) {
            return false;
        }
        if (module.getCategory() != this.baseCategory) {
            return false;
        }
        if (this.baseCategory == Category.PIT && flag) {
            if (this.moduleNameKeyword != null && !module.getName().toLowerCase(Locale.ROOT).contains(this.moduleNameKeyword)) {
                return false;
            }
            return this.pitCategory == null ? true : module.getPitCategory() == this.pitCategory;
        }
        return true;
    }

    private static boolean matchesModule(Module module, HypixelPitType hypixelPitType) {
        return module.getCategory() != Category.PIT ? true : module.getHypixelPitType() == hypixelPitType;
    }

    private static CombatType[] computeCombatTypeArray() {
        return new CombatType[]{
            COMBAT,
            MOVEMENT,
            PLAYER,
            VISUAL,
            MISCELLANEOUS,
            EXPLOIT,
            PIT,
            CLOUD,
            CLIENT,
            PIT_AUTOMATION,
            PIT_HUD,
            PIT_SWAP,
            PIT_UTILITY,
            PIT_MAJOR_EVENT,
            PIT_MINOR_EVENT,
            PIT_PREMIUM,
            BACK
        };
    }

    public boolean checkState2() {
        return this == PIT;
    }

    public static List<CombatType> forPage(CombatType.MainMode mainMode) {
        List list = Arrays.stream(values())
            .filter(combatType -> combatType.page == mainMode)
            .filter(combatType2 -> combatType2 != PIT_PREMIUM || true) // VENDOR-DISABLED: EntitlementState.checkState()
            .collect(Collectors.toList());
        if (mainMode == CombatType.MainMode.MAIN) {
            list.sort((obj, obj2) -> {
                List list2 = Arrays.asList(COMBAT, MOVEMENT, PLAYER, VISUAL, MISCELLANEOUS, EXPLOIT, CLIENT, PIT, CLOUD);
                return Integer.compare(list2.indexOf(obj), list2.indexOf(obj2));
            });
        }

        return list;
    }

    public boolean checkState() {
        return this == BACK;
    }

    public static String computeString(Module module) {
        if (module == null) {
            return "Unknown";
        }
        for (CombatType combatType : values()) {
            if (combatType.baseCategory == module.getCategory() && !combatType.navigationOnly && combatType.page == CombatType.MainMode.MAIN) {
                return combatType.getLabel();
            }
        }

        String str = module.getCategory().name().toLowerCase(Locale.ROOT);
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private CombatType(String label, Category baseCategory, PitCategory pitCategory, String moduleNameKeyword, CombatType.MainMode page, boolean navigationOnly) {
        this.label = label;
        this.baseCategory = baseCategory;
        this.pitCategory = pitCategory;
        this.moduleNameKeyword = moduleNameKeyword;
        this.page = page;
        this.navigationOnly = navigationOnly;
    }

    public static enum MainMode {
        MAIN,
        PIT;

        private static CombatType.MainMode[] computeMainModeArray() {
            return new CombatType.MainMode[]{MAIN, PIT};
        }
    }
}
