package org.brutality.util;

import net.minecraft.client.Minecraft;
import org.brutality.core.ConnectToServerHandler;

public class FloorHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int computeInt(PizzaType pizzaType, int n) {
        if (n == -1) {
            return -1;
        }
        switch (pizzaType) {
            case PIZZA:
            case SPIRE:
            case ROBBERY:
            case TDM:
            case RAGE_PIT:
            case BEAST:
                if (n <= 3) {
                    return 2;
                }

                return n <= 20 ? 1 : 0;
            case RAFFLE:
                return 0;
            case SQUADS:
                if (n <= 3) {
                    return 2;
                }

                return n <= 7 ? 1 : 0;
            case BLOCKHEAD:
                if (n <= 5) {
                    return 2;
                }

                return n <= 15 ? 1 : 0;
            default:
                return -1;
        }
    }

    public static String computeString() {
        if (ConnectToServerHandler.getPizzaType() != PizzaType.SPIRE) {
            return null;
        }
        int n = GetOrderedTeamsHelper.computeInt(mc.theWorld.getScoreboard(), "Floor");
        if (n == -1) {
            return null;
        }
        String str = GetOrderedTeamsHelper.computeString(mc.theWorld.getScoreboard(), n);
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return null;
        }
        String[] parts = ExecuteCommandHelper.computeString6(str).trim().split(" ");
        return parts.length < 2 ? null : parts[1].trim().toLowerCase();
    }
}
