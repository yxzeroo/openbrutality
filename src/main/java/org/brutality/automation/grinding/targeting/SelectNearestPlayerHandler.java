package org.brutality.automation.grinding.targeting;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.math.BuildTargetComparatorHelper;

public class SelectNearestPlayerHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayer selectNearestPlayer(List<EntityPlayer> players) {
        List list = players.stream()
            .filter(player -> (player.getDistanceToEntity(mc.thePlayer) > 75.0F ? 1 : (player.getDistanceToEntity(mc.thePlayer) == 75.0F ? 0 : -1)) <= 0)
            .sorted(
                Comparator.<EntityPlayer, Boolean>comparing(player2 -> GetDisplayedTitleHelper.matchesEntityPlayer(player2, 3.0F))
                    .thenComparingInt(player3 -> {
                        double dist = player3.getDistanceToEntity(mc.thePlayer);
                        if (dist <= 4.5) {
                            return 0;
                        }
                        if (dist <= 10.0) {
                            return 1;
                        }
                        if (dist <= 20.0) {
                            return 2;
                        }
                        if (dist <= 35.0) {
                            return 3;
                        }
                        return (dist > 55.0 ? 1 : (dist == 55.0 ? 0 : -1)) <= 0 ? 4 : 5;
                    })
                    .thenComparingInt(player4 -> GetMysticEnchantLoreHelper.computeInt(player4))
                    .thenComparing(player5 -> BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, player5), Comparator.reverseOrder())
            )
            .collect(Collectors.toList());
        return list.isEmpty() ? null : (EntityPlayer)list.get(0);
    }
}
