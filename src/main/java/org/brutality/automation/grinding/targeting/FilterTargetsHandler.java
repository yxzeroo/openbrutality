package org.brutality.automation.grinding.targeting;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.core.DisplayNameHandler;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.hud.TDM;
import org.brutality.util.BEASTHelper;
import org.brutality.util.PizzaType;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.network.ServerUtils;

public final class FilterTargetsHandler {
    private static final Map<String, String> lookupMap = DisplayNameHandler.lookupMap8;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean matchesEntityPlayer(EntityPlayer player, AutoGrinder.YawMode yawMode, boolean flag, boolean flag2) {
        if (!GetMysticEnchantLoreHelper.matchesEntityPlayer10(player)) {
            return true;
        }
        if (flag) {
            return false;
        }
        if (WHelper.matchesString(player.getName()) && yawMode == AutoGrinder.YawMode.HUNT_ENEMIES) {
            return true;
        }
        if (ConnectToServerHandler.checkState6() && ConnectToServerHandler.getPizzaType() == PizzaType.BEAST) {
            return true;
        }
        return flag2 ? BuildTargetComparatorHelper.matchesEntityPlayer(player) : true;
    }

    public static List<EntityPlayer> filterTargets(AutoGrinder autoGrinder, AutoGrinder.YawMode yawMode, boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        double d = Double.parseDouble(lookupMap.getOrDefault("allowedYOffset", "2.5"));
        return mc.theWorld
            .playerEntities
            .stream()
            .filter(player -> autoGrinder.checkState17() || player.posY < mc.thePlayer.posY + d)
            .filter(player2 -> player2.posY > mc.thePlayer.posY - 75.0)
            .filter(player3 -> player3 != mc.thePlayer)
            .filter(player4 -> !TDM.matchesEntityPlayer3(player4))
            .filter(player5 -> !ServerUtils.checkState5() || player5.getName().toUpperCase(Locale.ROOT).contains("CIT"))
            .filter(player6 -> !player6.isDead && !player6.isInvisible())
            .filter(player7 -> matchesEntityPlayer(player7, yawMode, flag2, flag3))
            .filter(player8 -> !flag4 || !BEASTHelper.matchesEntityPlayer2(player8))
            .filter(player9 -> !FreshHelper.matchesWorld3(player9.worldObj, player9))
            .filter(player10 -> !flag || !DefaultHelper.matchesString(player10.getName()))
            .collect(Collectors.toList());
    }

    private FilterTargetsHandler() {
    }
}
