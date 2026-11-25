package org.brutality.utils.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public final class EntityCache {
    private static final List<EntityPlayer> entries = new ArrayList<>(64);
    private static final List<EntityPlayer> entries2 = Collections.unmodifiableList(entries);
    private static final List<Entity> entries3 = new ArrayList<>(256);
    private static int count;

    private EntityCache() {
    }

    public static List<EntityPlayer> getEntries() {
        return entries;
    }

    public static List<EntityPlayer> getEntries2() {
        return entries2;
    }

    public static List<Entity> computeList() {
        return entries3.isEmpty() && Minecraft.getMinecraft().theWorld != null ? Minecraft.getMinecraft().theWorld.loadedEntityList : entries3;
    }

    public static void clear() {
        entries.clear();
        entries3.clear();
        count = 1;
    }

    public static void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            entries.clear();
            entries3.clear();
        } else {
            entries.clear();
            List playerEntities = mc.theWorld.playerEntities;
            int n = 0;

            for (int i = playerEntities.size(); n < i; n++) {
                EntityPlayer player = (EntityPlayer)playerEntities.get(n);
                if (player != null) {
                    entries.add(player);
                }
            }

            count++;
            if ((count & 1) == 0) {
                entries3.clear();
                List loadedEntityList = mc.theWorld.loadedEntityList;
                int n2 = 0;

                for (int j = loadedEntityList.size(); n2 < j; n2++) {
                    Entity entity = (Entity)loadedEntityList.get(n2);
                    if (entity != null) {
                        entries3.add(entity);
                    }
                }
            }
        }
    }
}
