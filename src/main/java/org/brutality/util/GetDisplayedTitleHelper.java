package org.brutality.util;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.util.module.FourSeasonsType;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class GetDisplayedTitleHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static String[] stringArray = new String[]{"OVRDRV", "BEAST", "HERMIT", "HIGH", "MOON", "UBER"};

    public static boolean checkState() {
        Iterator it = mc.thePlayer.getActivePotionEffects().iterator();

        while (it.hasNext()) {
            if (((PotionEffect)it.next()).getPotionID() == Potion.poison.id) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkState2() {
        return matchesEntityPlayer(mc.thePlayer, 0.0F);
    }

    public static boolean checkState3() {
        return matchesEntityPlayer2(mc.thePlayer);
    }

    public static double computeDouble(EntityPlayer player) {
        return player.getDistance(0.0, player.posY, 0.0);
    }

    private static boolean matchesEntityLivingBase(EntityLivingBase living) {
        if (!(living.posX <= 82.0)) {
            return false;
        }
        if (!(living.posX >= 60.0)) {
            return false;
        }
        if (!(living.posY >= 62.0)) {
            return false;
        }
        if (!(living.posY <= 80.0)) {
            return false;
        }
        return living.posZ <= -58.0 ? (living.posZ > -82.0 ? 1 : (living.posZ == -82.0 ? 0 : -1)) >= 0 : false;
    }

    public static String getDisplayedTitle() {
        return mc.ingameGUI == null ? "" : mc.ingameGUI.getDisplayedTitle();
    }

    public static void process() {
        int keyCode = Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode();
        KeyBinding.setKeyBindState(keyCode, true);
        KeyBinding.onTick(keyCode);
        KeyBinding.setKeyBindState(keyCode, false);
    }

    public static int computeInt(int n, int n2) {
        return (int)(Math.random() * (n2 - n + 1) + n);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        return matchesEntityPlayer(player, 0.0F);
    }

    public static boolean matchesEntityLivingBase3(EntityLivingBase living) {
        return living != null && FreshHelper.matchesWorld3(living.worldObj, living);
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, float f) {
        if (mc.thePlayer == null) {
            return false;
        }
        if (mc.theWorld == null) {
            return false;
        }
        if (!ConnectToServerHandler.flag4) {
            return false;
        }
        if (player == null) {
            return false;
        }
        double d = UtilHelper5.computeInt() + f;
        return (
                    player.getDistanceSq(0.0, player.getPosition().getY(), 0.0) > d * d
                        ? 1
                        : (player.getDistanceSq(0.0, player.getPosition().getY(), 0.0) == d * d ? 0 : -1)
                )
                < 0
            && player.getPosition().getY() < ConnectToServerHandler.computeInt();
    }

    public static boolean matchesEntityPlayer3(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        if (player.getHeldItem() == null) {
            return false;
        }
        if (player.getHeldItem().getItem() == null) {
            return false;
        }
        String[] parts = new String[]{"helmet", "chestplate", "leggings", "boots"};
        int len = parts.length;

        for (int i = 0; i < 4; i++) {
            if (player.getHeldItem().getItem().getUnlocalizedName().contains(parts[i])) {
                return true;
            }
        }

        return false;
    }

    public static float computeFloat(EntityPlayer player) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player);
    }

    public static String computeString() {
        return mc.ingameGUI == null ? "" : mc.ingameGUI.getDisplayedSubTitle();
    }

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        if (mc.thePlayer == null) {
            return false;
        }
        if (mc.theWorld == null) {
            return false;
        }
        if (!ConnectToServerHandler.flag4) {
            return false;
        }
        if (player == null) {
            return false;
        }
        int n = UtilHelper5.computeInt() - 2;
        return (
                    player.getDistanceSq(0.0, player.getPosition().getY(), 0.0) > n * n
                        ? 1
                        : (player.getDistanceSq(0.0, player.getPosition().getY(), 0.0) == n * n ? 0 : -1)
                )
                < 0
            && player.getPosition().getY() < ConnectToServerHandler.computeInt();
    }

    public static boolean matchesString(String str) {
        if (str.contains("[") && str.contains("]")) {
            return true;
        }
        String str2 = str.toLowerCase();
        String[] parts = stringArray;
        int len = parts.length;

        for (int i = 0; i < len; i++) {
            if (str2.contains(parts[i].toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkState4() {
        return matchesEntityLivingBase3(mc.thePlayer);
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase living, int n, int n2) {
        if (ConnectToServerHandler.getFourSeasonsType() == FourSeasonsType.GENESIS) {
            if (matchesEntityLivingBase(living)) {
                return true;
            }

            if (matchesEntityLivingBase2(living)) {
                return true;
            }
        }

        if (!(living.posX <= n2)) {
            return false;
        }
        if (!(living.posX >= ~n2 + 1)) {
            return false;
        }
        if (!(living.posZ <= n2)) {
            return false;
        }
        if (!(living.posZ >= ~(n2 - 1))) {
            return false;
        }
        return living.posY > n - 2 ? (living.posY > n + 12 ? 1 : (living.posY == n + 12 ? 0 : -1)) < 0 : false;
    }

    private static boolean matchesEntityLivingBase2(EntityLivingBase living) {
        if (!(living.posX <= -60.0)) {
            return false;
        }
        if (!(living.posX >= -82.0)) {
            return false;
        }
        if (!(living.posY >= 62.0)) {
            return false;
        }
        if (!(living.posY <= 75.0)) {
            return false;
        }
        return living.posZ <= 82.0 ? (living.posZ > 60.0 ? 1 : (living.posZ == 60.0 ? 0 : -1)) >= 0 : false;
    }
}
