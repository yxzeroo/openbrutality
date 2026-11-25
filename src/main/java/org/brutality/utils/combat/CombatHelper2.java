package org.brutality.utils.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.GetCurrentScreenHelper;

public final class CombatHelper2 {
    public static boolean matchesEntityPlayer(EntityPlayer player, double d, boolean flag) {
        if (player != null && player != GetCurrentScreenHelper.mc.thePlayer && !player.isDead && player.deathTime == 0) {
            if (GetCurrentScreenHelper.mc.thePlayer.getDistanceToEntity(player) > d) {
                return false;
            }
            return DefaultHelper.matchesString2(player.getName()) ? false : !flag || !GetCurrentScreenHelper.matchesEntity2(player);
        }
        return false;
    }

    private CombatHelper2() {
    }

    public static EntityPlayer computeEntityPlayer(double d, boolean flag) {
        if (!GetCurrentScreenHelper.checkState2()) {
            return null;
        }
        if (KillAura.entityLivingBase2 instanceof EntityPlayer player2 && matchesEntityPlayer(player2, d, flag)) {
            return player2;
        }
        EntityPlayer player = null;
        double d2 = d * d;

        for (Entity entity : GetCurrentScreenHelper.mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer player3 && matchesEntityPlayer(player3, d, flag)) {
                double dist = GetCurrentScreenHelper.mc.thePlayer.getDistanceSqToEntity(player3);
                if (dist < d2) {
                    player = player3;
                    d2 = dist;
                }
            }
        }

        return player;
    }
}
