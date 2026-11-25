package org.brutality.service.itemsearch;

public enum PlanetPitType {
    PLANET_PIT,
    PITPANDA;

    private static PlanetPitType[] computePlanetPitTypeArray() {
        return new PlanetPitType[]{PLANET_PIT, PITPANDA};
    }
}
