package org.brutality.service.itemsearch;

import java.util.UUID;

public class ItemsearchHandler {
    public String username;
    public String text;
    public int count;
    public long timestamp;
    public UUID uuid;

    public ItemsearchHandler(PlanetPitType planetPitType, boolean flag, int count, boolean flag2, boolean flag3) {
        this.count = count;
    }
}
