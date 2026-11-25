package org.brutality.util.inventory;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.EnumChatFormatting;

public enum VenomType {
    VENOM("Venom", "venom", true, "venoms", EnumChatFormatting.GREEN),
    NOSTALGIA("Nostalgia", "nostalgia", true, "nostalgia", EnumChatFormatting.AQUA),
    HEARTRIPPER("Heartripper", "heartripper", true, "heartripper", EnumChatFormatting.DARK_RED),
    GOLDEN_HANDCUFFS("Golden Handcuffs", "golden_handcuffs", true, "handcuffs", EnumChatFormatting.GOLD),
    MISERY("Misery", "misery", false, "misery", EnumChatFormatting.RED),
    SPITE("Spite", "spite", false, "spite", EnumChatFormatting.RED),
    NEEDLESS_SUFFERING("Needless Suffering", "needless_suffering", false, "needless", EnumChatFormatting.RED),
    GRIM_REAPER("Grim Reaper", "grim_reaper", false, "grim", EnumChatFormatting.DARK_PURPLE),
    HEDGE_FUND("Hedge Fund", "hedge_fund", false, "hedge", EnumChatFormatting.GOLD),
    SANGUISUGE("Sanguisuge", "sanguisuge", false, "sang", EnumChatFormatting.RED),
    MIND_ASSAULT("Mind Assault", "mind_assault", false, "minds", EnumChatFormatting.DARK_PURPLE),
    LYCANTHROPY("Lycanthropy", "lycanthropy", false, "lycan", EnumChatFormatting.DARK_PURPLE),
    REGULARITY("Regularity", "regularity", true, "reg", EnumChatFormatting.DARK_RED),
    THINK_OF_THE_PEOPLE("Think of the People", "think_of_the_people", true, "totp", EnumChatFormatting.YELLOW),
    NEW_DEAL("New Deal", "extra_loot_from_mobs", false, "nd", EnumChatFormatting.GOLD),
    DO_IT_LIKE_THE_FRENCH("Do It Like The French", "do_it_like_the_french", false, "french", EnumChatFormatting.GOLD),
    HEIGH_HO("Heigh Ho", "heigh_ho", false, "heigh", EnumChatFormatting.RED),
    SYBIL("Sybil", "sybil", false, "sybil", EnumChatFormatting.DARK_RED),
    SOLITUDE("Solitude", "solitude", true, "soli", EnumChatFormatting.BLUE),
    RGM("RGM", "rgm", true, "rgm", EnumChatFormatting.YELLOW),
    ESCAPE_POD("Escape Pod", "escape_pod", true, "pod", EnumChatFormatting.GREEN),
    SINGULARITY("Singularity", "singularity", true, "sing", EnumChatFormatting.LIGHT_PURPLE),
    ASSASSIN("Assassin", "sneak_teleport", true, "ass", EnumChatFormatting.DARK_GRAY),
    PHOENIX("Phoenix", "phoenix", true, "phx", EnumChatFormatting.RED),
    CRIT_FUNKY("Critically Funky", "power_against_crits", false, "cf", EnumChatFormatting.DARK_AQUA),
    PRICK("Prick", "thorns", false, "prick", EnumChatFormatting.DARK_GREEN),
    MIRROR("Mirror", "immune_true_damage", false, "mir", EnumChatFormatting.WHITE),
    PEROXIDE("Peroxide", "regen_when_hit", false, "pero", EnumChatFormatting.RED),
    FAST("Gotta Go Fast", "perma_speed", false, "fast", EnumChatFormatting.YELLOW),
    RESPAWN_ABS("Respawn Absorption", "respawn_with_absorption", false, "abs", EnumChatFormatting.GOLD),
    PAPARAZZI("Paparazzi", "paparazzi", true, "pap", EnumChatFormatting.LIGHT_PURPLE),
    PIT_MBA("PIT MBA", "pit_mba", false, "mba", EnumChatFormatting.GOLD),
    FRACTIONAL_RESERVE("Fractional Reserve", "fractional_reserve", false, "frac", EnumChatFormatting.GRAY),
    GAMBLE("Gamble", "melee_gamble", true, "gam", EnumChatFormatting.DARK_PURPLE),
    BILLIONAIRE("Billionaire", "melee_literally_p2w", true, "bil", EnumChatFormatting.GOLD),
    PERUN("Combo: Perun's Wrath", "melee_lightning", true, "perun", EnumChatFormatting.AQUA),
    EXECUTIONER("Executioner", "melee_execute", true, "exe", EnumChatFormatting.RED),
    COMBO_STUN("Combo: Stun", "melee_stun", true, "stun", EnumChatFormatting.GRAY),
    PUNCH("Punch", "melee_launch", true, "punch", EnumChatFormatting.LIGHT_PURPLE),
    HEMORRHAGE("Hemorrhage", "melee_bleed0", true, "hemo", EnumChatFormatting.RED),
    LIFESTEAL("Lifesteal", "melee_heal_on_hit", false, "ls", EnumChatFormatting.RED),
    STOMP("Stomp", "melee_damage_vs_diamond", false, "stomp", EnumChatFormatting.AQUA),
    COMBO_HEAL("Combo: Heal", "melee_combo_heal", false, "c-heal", EnumChatFormatting.YELLOW),
    COMBO_DAMAGE("Combo: Damage", "melee_combo_damage", false, "c-dmg", EnumChatFormatting.YELLOW),
    DUELIST("Duelist", "melee_strike_after_block", false, "duel", EnumChatFormatting.DARK_RED),
    PAIN_FOCUS("Pain Focus", "melee_damage_when_low", false, "pf", EnumChatFormatting.DARK_RED),
    BERSERKER("Berserker", "melee_crit_midair", false, "bers", EnumChatFormatting.GREEN),
    BOUNTY_REAPER("Bounty Reaper", "melee_damage_vs_bounties", false, "breaper", EnumChatFormatting.GOLD),
    FANCY_RAIDER("Fancy Raider", "melee_damage_vs_leather", false, "fancy", EnumChatFormatting.RED),
    KING_BUSTER("King Buster", "melee_damage_vs_high_hp", false, "kb", EnumChatFormatting.YELLOW),
    SHARP("Sharp", "plain_melee_damage", false, "sharp", EnumChatFormatting.RED),
    SHARK("Shark", "melee_damage_when_close_low_players", false, "shark", EnumChatFormatting.RED),
    BULLET_TIME("Bullet Time", "blocking_cancels_projectiles", false, "bt", EnumChatFormatting.GRAY),
    BRUISER("Bruiser", "increased_blocking", false, "bruiser", EnumChatFormatting.DARK_RED),
    MEGA_LONGBOW("Mega Longbow", "instant_shot", true, "mlb", EnumChatFormatting.GREEN),
    TELEBOW("Telebow", "telebow", true, "telebow", EnumChatFormatting.LIGHT_PURPLE),
    VOLLEY("Volley", "volley", true, "volley", EnumChatFormatting.LIGHT_PURPLE),
    TRUE_SHOT("True Shot", "bow_to_true_damage", true, "true", EnumChatFormatting.RED),
    PULLBOW("Pullbow", "pullbow", true, "pull", EnumChatFormatting.GRAY),
    EXPLOSIVE("Explosive", "explosive_bow", true, "explo", EnumChatFormatting.RED),
    DEVIL_CHICKS("Devil Chicks", "explosive_chickens", true, "chicks", EnumChatFormatting.DARK_RED),
    LUCKY_SHOT("Lucky Shot", "lucky_shot", true, "lucky", EnumChatFormatting.YELLOW),
    PIN_DOWN("Pin Down", "pin_down", false, "pin", EnumChatFormatting.GRAY),
    SPRINT_DRAIN("Sprint Drain", "bow_slow", false, "drain", EnumChatFormatting.YELLOW),
    FASTER_THAN_THEIR_SHADOW("Faster Than Their Shadow", "bow_combo_speed", false, "ftts", EnumChatFormatting.YELLOW),
    PARASITE("Parasite", "parasite", false, "para", EnumChatFormatting.RED),
    WASP("Wasp", "bow_weakness_on_hit", false, "wasp", EnumChatFormatting.RED),
    CHIPPING("Chipping", "arrow_true_damage", false, "chip", EnumChatFormatting.RED);

    private final String internalId;
    private final String colorCode2;
    private final boolean featured;
    private final String shortName;
    private final String displayName2;
    private static final Map<String, VenomType> byInternalId = new HashMap<>();

    public boolean isFeatured() {
        return this.featured;
    }

    public String getDisplayName() {
        return this.displayName2;
    }

    public String getInternalId() {
        return this.internalId;
    }

    public String getShortName() {
        return this.shortName;
    }

    public static VenomType computeVenomType(String str) {
        return byInternalId.get(str);
    }

    private static VenomType[] computeVenomTypeArray() {
        return new VenomType[]{
            VENOM,
            NOSTALGIA,
            HEARTRIPPER,
            GOLDEN_HANDCUFFS,
            MISERY,
            SPITE,
            NEEDLESS_SUFFERING,
            GRIM_REAPER,
            HEDGE_FUND,
            SANGUISUGE,
            MIND_ASSAULT,
            LYCANTHROPY,
            REGULARITY,
            THINK_OF_THE_PEOPLE,
            NEW_DEAL,
            DO_IT_LIKE_THE_FRENCH,
            HEIGH_HO,
            SYBIL,
            SOLITUDE,
            RGM,
            ESCAPE_POD,
            SINGULARITY,
            ASSASSIN,
            PHOENIX,
            CRIT_FUNKY,
            PRICK,
            MIRROR,
            PEROXIDE,
            FAST,
            RESPAWN_ABS,
            PAPARAZZI,
            PIT_MBA,
            FRACTIONAL_RESERVE,
            GAMBLE,
            BILLIONAIRE,
            PERUN,
            EXECUTIONER,
            COMBO_STUN,
            PUNCH,
            HEMORRHAGE,
            LIFESTEAL,
            STOMP,
            COMBO_HEAL,
            COMBO_DAMAGE,
            DUELIST,
            PAIN_FOCUS,
            BERSERKER,
            BOUNTY_REAPER,
            FANCY_RAIDER,
            KING_BUSTER,
            SHARP,
            SHARK,
            BULLET_TIME,
            BRUISER,
            MEGA_LONGBOW,
            TELEBOW,
            VOLLEY,
            TRUE_SHOT,
            PULLBOW,
            EXPLOSIVE,
            DEVIL_CHICKS,
            LUCKY_SHOT,
            PIN_DOWN,
            SPRINT_DRAIN,
            FASTER_THAN_THEIR_SHADOW,
            PARASITE,
            WASP,
            CHIPPING
        };
    }

    private VenomType(String displayName2, String internalId, boolean featured, String shortName, EnumChatFormatting enumChatFormatting) {
        this.displayName2 = displayName2;
        this.internalId = internalId;
        this.featured = featured;
        this.shortName = shortName;
        this.colorCode2 = String.valueOf(new StringBuilder().append(enumChatFormatting));
    }

    public String getColorCode() {
        return this.colorCode2;
    }

    static {
        for (VenomType venomType : values()) {
            byInternalId.put(venomType.internalId, venomType);
        }
    }

    public String r() {
        return String.valueOf(new StringBuilder().append(this.colorCode2).append(this.shortName));
    }
}
