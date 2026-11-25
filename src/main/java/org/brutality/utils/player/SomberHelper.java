package org.brutality.utils.player;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.module.impl.pit.hypixel.hud.TelebowTimer;
import org.brutality.utils.hypixel.pit.FreshHelper;

public final class SomberHelper {
    private SomberHelper() {
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        return matchesEntityPlayer2(player) && !player.isPotionActive(Potion.poison);
    }

    public static void handleEntityPlayer(EntityPlayer player) {
        if (player != null) {
            if (player == Minecraft.getMinecraft().thePlayer) {
                ObserveAttemptHelper.instance.handleUUID2(player.getUniqueID());
            } else {
                ObserveAttemptHelper.instance.handleUUID(player.getUniqueID());
            }

            TelebowTimer instance = TelebowTimer.getInstance();
            if (instance != null) {
                instance.handleEntityPlayer(player);
            }
        }
    }

    public static boolean matchesEntityPlayer3(EntityPlayer player) {
        ItemStack stack = player != null && player.inventory != null ? player.inventory.armorInventory[1] : null;
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().toString().toLowerCase(Locale.ROOT).contains("somber");
    }

    public static void handleEntityPlayer(EntityPlayer player, double d, double d2, double d3, double d4, double d5, double d6) {
        Minecraft mc = Minecraft.getMinecraft();
        long now = System.currentTimeMillis();
        if (player != null && ObserveAttemptHelper.instance.matchesObject2(mc.theWorld, player.getUniqueID(), now)) {
            if (player.isEntityAlive() && !FreshHelper.matchesWorld(mc.theWorld, d4, d5, d6)) {
                if (ObserveAttemptHelper.instance
                    .matchesObject(mc.theWorld, player.getUniqueID(), mc.theWorld.getTotalWorldTime(), now, d, d2, d3, d4, d5, d6)) {
                    TelebowTimer instance = TelebowTimer.getInstance();
                    if (instance != null) {
                        instance.handleEntityPlayer(player, ObserveAttemptHelper.instance.computeLong2(mc.theWorld, player.getUniqueID(), now), now);
                    }
                }
            } else {
                ObserveAttemptHelper.instance.handleUUID2(player.getUniqueID());
            }
        }
    }

    public static boolean checkState() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld != null && mc.thePlayer != null && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer);
    }

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        return checkState() && player != null && player.isEntityAlive() && !matchesEntityPlayer3(player) && !FreshHelper.matchesWorld3(mc.theWorld, player);
    }

    public static boolean matchesString(String str) {
        if (str == null) {
            return false;
        }
        String str2 = EnumChatFormatting.getTextWithoutFormattingCodes(str).strip().toLowerCase(Locale.ROOT);
        return str2.startsWith("nope!") && str2.contains("teleport");
    }

    public static void handleEntityPlayer(EntityPlayer player, int n, BlockPos pos, long l) {
        if (pos != null && matchesEntityPlayer(player)) {
            Minecraft mc = Minecraft.getMinecraft();
            ObserveAttemptHelper.instance
                .observeAttempt(
                    mc.theWorld,
                    player.getUniqueID(),
                    mc.theWorld.getTotalWorldTime(),
                    l,
                    TelebowTimer.computeLong(n),
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5
                );
        }
    }
}
