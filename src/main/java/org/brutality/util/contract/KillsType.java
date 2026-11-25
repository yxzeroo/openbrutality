package org.brutality.util.contract;

public enum KillsType {
    KILLS(1, "Kill [x] players.", true),
    CHAIN_KILLS(2, "Get a [x] kill.", true),
    KILL_STREAK(3, "Reach a [x] kills streak.", true),
    KILLS_SNEAKING(4, "Kill [x] players while sneaking on each hit.", true),
    KILLS_NAKED(5, "Kill [x] players without wearing any armor.", true),
    KILLS_DIAMOND_SWORD(6, "Kill [x] players with a diamond sword killing blow.", true),
    CLAIM_BOUNTIES(7, "Claim [x] bounties.", true),
    COLLECT_GOLD_INGOTS(8, "Collect [x] ingots.", true),
    EAT_GOLDEN_APPLES(9, "Eat [x] golden apples (no golden heads).", false),
    KILLS_ON_GRASS(10, "Kill [x] players while standing on grass.", false),
    KILLS_ON_OBSIDIAN(11, "Kill [x] players while standing on obsidian.", false),
    FISH_DIAMOND_SWORD(12, "Fish a Diamond Sword.", false),
    LAND_ARROW_SHOTS(13, "Land [x] arrow shots on enemies.", false),
    KILLS_AFTER_SHOOTING(14, "Kill [x] players after shooting an arrow in them.", false),
    KILLS_LAVA(15, "Kill [x] players using lava.", false),
    SINGLE_KILL_WITH_FIST(16, "Get a killing blow using your fist inside the small sea lantern circle of the pit.", false),
    KILLS_WITH_FIST(17, "Get [x] killing blows using your fist inside the small sea lantern circle of the pit.", false),
    KILLS_WITHOUT_PERKS(18, "Kill [x] players without any perks equipped.", false),
    UNKNOWN(99, "Unknown Task", false);

    private final String descriptionPattern;
    private final boolean automaticallyTrackable;
    private final int taskId;

    public static KillsType computeKillsType(String str) {
        String str2 = str.toLowerCase();

        for (KillsType killsType : values()) {
            boolean flag = true;
            String[] parts = killsType.descriptionPattern.toLowerCase().split(" ");

            for (String str3 : parts) {
                if (!str3.equals("[x]") && !str2.contains(str3)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                return killsType;
            }
        }

        return UNKNOWN;
    }

    public boolean isAutomaticallyTrackable() {
        return this.automaticallyTrackable;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public String getDescriptionPattern() {
        return this.descriptionPattern;
    }

    private static KillsType[] computeKillsTypeArray() {
        return new KillsType[]{
            KILLS,
            CHAIN_KILLS,
            KILL_STREAK,
            KILLS_SNEAKING,
            KILLS_NAKED,
            KILLS_DIAMOND_SWORD,
            CLAIM_BOUNTIES,
            COLLECT_GOLD_INGOTS,
            EAT_GOLDEN_APPLES,
            KILLS_ON_GRASS,
            KILLS_ON_OBSIDIAN,
            FISH_DIAMOND_SWORD,
            LAND_ARROW_SHOTS,
            KILLS_AFTER_SHOOTING,
            KILLS_LAVA,
            SINGLE_KILL_WITH_FIST,
            KILLS_WITH_FIST,
            KILLS_WITHOUT_PERKS,
            UNKNOWN
        };
    }

    private KillsType(int taskId, String descriptionPattern, boolean automaticallyTrackable) {
        this.taskId = taskId;
        this.descriptionPattern = descriptionPattern;
        this.automaticallyTrackable = automaticallyTrackable;
    }
}
