package org.brutality.util;

public enum PizzaType {
    PIZZA("pizza"),
    RAFFLE("raffle"),
    SQUADS("squads"),
    BLOCKHEAD("blockhead"),
    SPIRE("spire"),
    ROBBERY("robbery"),
    TDM("tdm"),
    RAGE_PIT("rage pit"),
    BEAST("beast");

    private final String identifier;

    private static PizzaType[] computePizzaTypeArray() {
        return new PizzaType[]{PIZZA, RAFFLE, SQUADS, BLOCKHEAD, SPIRE, ROBBERY, TDM, RAGE_PIT, BEAST};
    }

    private PizzaType(String identifier) {
        this.identifier = identifier;
    }

    public String getDisplayName() {
        String[] parts = this.identifier.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String str : parts) {
            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
        }

        return String.valueOf(sb).trim();
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
