package org.brutality.automation.grinding.lifecycle;

public enum OtherType {
    OTHER,
    LIMBO,
    HUB,
    SPAWN_EQUIPPING_MYSTIC_PANTS(true),
    SPAWN_MOVING_MYSTICS(true),
    SPAWN_PRESTIGING(true),
    SPAWN_TAKING_OFF_ARMOR(true),
    SPAWN_PICKING_PERKS(true),
    SPAWN_PICKING_CONTRACT(true),
    SPAWN_BUYING(true),
    SPAWN(true),
    DOWN_MID,
    DOWN_MYSTIC_PATHFINDING,
    DOWN_OUTSIDE,
    SPAWN_SPIRE(true),
    DOWN_SPIRE,
    SPAWN_RAFFLE(true),
    DOWN_RAFFLE,
    SPAWN_BLOCKHEAD(true),
    DOWN_BLOCKHEAD,
    SPAWN_SQUADS(true),
    DOWN_SQUADS;

    public final boolean usesMovement;

    private OtherType(boolean usesMovement) {
        this.usesMovement = usesMovement;
    }

    private OtherType() {
        this.usesMovement = false;
    }

    private static OtherType[] computeOtherTypeArray() {
        return new OtherType[]{
            OTHER,
            LIMBO,
            HUB,
            SPAWN_EQUIPPING_MYSTIC_PANTS,
            SPAWN_MOVING_MYSTICS,
            SPAWN_PRESTIGING,
            SPAWN_TAKING_OFF_ARMOR,
            SPAWN_PICKING_PERKS,
            SPAWN_PICKING_CONTRACT,
            SPAWN_BUYING,
            SPAWN,
            DOWN_MID,
            DOWN_MYSTIC_PATHFINDING,
            DOWN_OUTSIDE,
            SPAWN_SPIRE,
            DOWN_SPIRE,
            SPAWN_RAFFLE,
            DOWN_RAFFLE,
            SPAWN_BLOCKHEAD,
            DOWN_BLOCKHEAD,
            SPAWN_SQUADS,
            DOWN_SQUADS
        };
    }
}
