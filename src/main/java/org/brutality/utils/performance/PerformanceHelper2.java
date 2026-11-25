package org.brutality.utils.performance;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.brutality.utils.render.ResetColorHelper;

public class PerformanceHelper2 {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static <T extends Entity> List<T> computeList(Entity entity, Class<T> clazz, double d) {
        if (entity != null && entity.worldObj != null) {
            AxisAlignedBB box = entity.getEntityBoundingBox().expand(d, d, d);
            return entity.worldObj.getEntitiesWithinAABB(clazz, box);
        }
        return new ArrayList<>();
    }

    public static List<EntityPlayer> computeList3(double d) {
        List list = computeList4(d);
        ArrayList list2 = new ArrayList(list.size());
        int n = 0;

        for (int i = list.size(); n < i; n++) {
            EntityPlayer player = (EntityPlayer)list.get(n);
            if (player != mc.thePlayer && player.isEntityAlive() && player.getHealth() > 0.0F) {
                list2.add(player);
            }
        }

        return list2;
    }

    public static List<EntityPlayer> computeList2(double d) {
        List list = computeList4(d);
        ArrayList list2 = new ArrayList(list.size());
        int n = 0;

        for (int i = list.size(); n < i; n++) {
            EntityPlayer player = (EntityPlayer)list.get(n);
            if (player != mc.thePlayer && ResetColorHelper.matchesEntity(player)) {
                list2.add(player);
            }
        }

        return list2;
    }

    public static List<EntityPlayer> computeList5(double d) {
        List list = computeList4(d);
        ArrayList list2 = new ArrayList(list.size());
        int n = 0;

        for (int i = list.size(); n < i; n++) {
            EntityPlayer player = (EntityPlayer)list.get(n);
            if (player != mc.thePlayer) {
                list2.add(player);
            }
        }

        return list2;
    }

    public static List<EntityPlayer> computeList4(double d) {
        return (List<EntityPlayer>)(mc.thePlayer != null && mc.theWorld != null ? computeList(mc.thePlayer, EntityPlayer.class, d) : new ArrayList<>());
    }

    public static double computeDouble(Entity entity) {
        if (mc.thePlayer != null && entity != null) {
            double d = entity.posX - mc.thePlayer.posX;
            double d2 = entity.posY - mc.thePlayer.posY;
            double d3 = entity.posZ - mc.thePlayer.posZ;
            return d * d + d2 * d2 + d3 * d3;
        }
        return Double.MAX_VALUE;
    }

    public static boolean matchesEntity(Entity entity, double d) {
        return mc.thePlayer != null && entity != null ? mc.thePlayer.getDistanceToEntity(entity) <= d : false;
    }

    public static <T extends Entity> List<T> computeList(List<T> list, double d) {
        ArrayList list2 = new ArrayList(list.size());
        int n = 0;

        for (int i = list.size(); n < i; n++) {
            Entity entity = (Entity)list.get(n);
            if (computeDouble(entity) <= d) {
                list2.add(entity);
            }
        }

        return list2;
    }

    public static List<EntityPlayer> computeList(double d) {
        List list = computeList4(d);
        ArrayList list2 = new ArrayList(list.size());
        int n = 0;

        for (int i = list.size(); n < i; n++) {
            EntityPlayer player = (EntityPlayer)list.get(n);
            if (player != mc.thePlayer && player.isEntityAlive() && player.getHealth() > 0.0F && ResetColorHelper.matchesEntity(player)) {
                list2.add(player);
            }
        }

        return list2;
    }
}
