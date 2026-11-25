package org.brutality.clickgui.newclick;

public enum HypixelPitType {
    HYPIXEL_PIT("Hypixel Pit"),
    ORION_PIT("Orion Pit"),
    HYPIT("Hypit"),
    CLASSIC_PIT("Classic"),
    HARRYS_PIT("Harrys Pit"),
    JUNGLE_PIT("Jungle Pit"),
    GODFIGHTS_PIT("GodfightsPit"),
    WEASEL_PIT("Weasel Pit");

    private final String label;

    public String getLabel() {
        return this.label;
    }

    private HypixelPitType(String label) {
        this.label = label;
    }

    private static HypixelPitType[] computeHypixelPitTypeArray() {
        return new HypixelPitType[]{HYPIXEL_PIT, ORION_PIT, HYPIT, CLASSIC_PIT, HARRYS_PIT, JUNGLE_PIT, GODFIGHTS_PIT, WEASEL_PIT};
    }
}
