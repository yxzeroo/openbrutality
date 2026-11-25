package org.brutality.util;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.core.ConnectToServerHandler;

public class BEASTHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private static float factor = -1.0F;
    private static final int count = 1000;
    private static final long timestamp = 1000L;

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        if (player == mc.thePlayer) {
            return false;
        }
        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(player.getName());
        if (playerInfo == null) {
            return true;
        }
        if (player.getName().startsWith("§k")) {
            return player.isInvisible();
        }
        if (playerInfo.getResponseTime() < 1) {
            return true;
        }
        ScorePlayerTeam playerTeam = playerInfo.getPlayerTeam();
        if (playerTeam == null) {
            return false;
        }
        return !playerTeam.getTeamName().isEmpty() ? false : playerTeam.getColorPrefix().equals("§c");
    }

    public static int computeInt2(String str) {
        if (str != null && !str.isEmpty()) {
            String[] parts = str.split(" ");

            for (String str2 : parts) {
                if (str2.endsWith("g§r") && (str2.startsWith("§6§l") || str2.startsWith("§c§l") || str2.startsWith("§b§l"))) {
                    String str3 = ExecuteCommandHelper.computeString6(str2).replaceAll("g", "").trim();

                    try {
                        return Integer.parseInt(str3);
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                }
            }

            return -1;
        }
        return -1;
    }

    public static int computeInt() {
        if (mc.thePlayer == null) {
            return -1;
        }
        int n = GetOrderedTeamsHelper.computeInt(mc.theWorld.getScoreboard(), "Strength:");
        if (n == -1) {
            return -1;
        }
        String str = GetOrderedTeamsHelper.computeString(mc.theWorld.getScoreboard(), n);
        if (str == null) {
            return -1;
        }
        for (String str2 : str.split(" ")) {
            if (str2.startsWith("§c")) {
                String str3 = ExecuteCommandHelper.computeString6(str2);
                switch (str3) {
                    case "I":
                        return 1;
                    case "II":
                        return 2;
                    case "III":
                        return 3;
                    case "IV":
                        return 4;
                    case "V":
                        return 5;
                    default:
                        return -1;
                }
            }
        }

        return -1;
    }

    public static double computeDouble(EntityPlayer player) {
        if (player == null) {
            return -1.0;
        }
        BlockPos pos = computeBlockPos(player);
        return pos == null ? -1.0 : player.posY - pos.getY();
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        if (!ConnectToServerHandler.checkState7() || ConnectToServerHandler.getPizzaType() != PizzaType.BEAST) {
            return false;
        }
        if (player == null) {
            return false;
        }
        String text = player.getDisplayName().getFormattedText();
        return text == null ? false : text.replaceAll(" ", "").contains("§lBEAST");
    }

    public static double computeDouble() {
        double d = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double d2 = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        return Math.sqrt(d * d + d2 * d2);
    }

    public static void process() {
        KeyBinding[] keyBindings = new KeyBinding[]{
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSneak,
            mc.gameSettings.keyBindSprint,
            mc.gameSettings.keyBindAttack,
            mc.gameSettings.keyBindUseItem
        };

        for (KeyBinding keyBinding : keyBindings) {
            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
        }
    }

    public static int computeInt(EntityPlayer player) {
        return player != null && player.getDisplayName() != null ? computeInt(player.getDisplayName().getFormattedText()) : -1;
    }

    public static int computeInt3(EntityPlayer player) {
        if (player == null) {
            return -1;
        }
        return player.isPotionActive(Potion.moveSpeed) ? player.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }

    public static String computeString(EntityPlayer player) {
        if (player != null && player.getDisplayName() != null) {
            String text = player.getDisplayName().getFormattedText();
            String name = player.getName();
            String str = "§. " + name;
            if (text.matches(".*" + str + ".*")) {
                int n = text.indexOf(" " + name);
                return n < 2 ? null : text.substring(n - 2, n);
            }
            return null;
        }
        return null;
    }

    public static float computeFloat() {
        if (mc.thePlayer == null) {
            return -1.0F;
        }
        if (!utilHelper8.matchesLong(timestamp)) {
            return factor;
        }
        for (String str : GetOrderedTeamsHelper.computeList(mc.theWorld.getScoreboard())) {
            String str2 = ExecuteCommandHelper.computeString6(str).toLowerCase().trim();
            if (str2.startsWith("gold: ")) {
                int n = str2.indexOf(32);
                String str3 = str2.substring(n + 1);

                try {
                    factor = ExecuteCommandHelper.computeFloat2(str3);
                    return factor;
                } catch (NumberFormatException e) {
                    return -1.0F;
                }
            }
        }

        return -1.0F;
    }

    public static BlockPos computeBlockPos(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        BlockPos pos = new BlockPos(Math.floor(player.posX), Math.floor(player.getEntityBoundingBox().minY + 0.001), Math.floor(player.posZ));
        if (FenceHandler.matchesBlockPos7(pos)) {
            return pos;
        }
        while (FenceHandler.matchesBlockPos5(pos.down()) && pos.getY() > 0) {
            pos = pos.down();
        }

        return pos;
    }

    public static boolean matchesUUID(UUID uuid) {
        return uuid.version() == 1;
    }

    public static boolean checkState2() {
        return computeInt2() >= 120;
    }

    public static int computeInt(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = str.replaceAll("§.", "");
            if (str2.contains("[") && str2.contains("]")) {
                String str3 = str2.replaceAll("^.*\\[(\\d+)\\].*$", "$1");

                try {
                    return Integer.parseInt(str3);
                } catch (NumberFormatException e) {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static int computeInt2() {
        return mc.thePlayer == null ? -1 : mc.thePlayer.experienceLevel;
    }

    public static int computeInt2(EntityPlayer player) {
        return player != null && player.getDisplayName() != null ? computeInt2(player.getDisplayName().getFormattedText()) : -1;
    }

    public static boolean checkState() {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            return false;
        }
        if (!player.isSprinting()) {
            return false;
        }
        double d = player.posX - player.lastTickPosX;
        double d2 = player.posZ - player.lastTickPosZ;
        double dist = Math.sqrt(d * d + d2 * d2);
        return dist >= 0.2 && !player.onGround && Math.abs(player.motionY) <= 0.15 && computeDouble(player) >= 1.0;
    }

    public static float computeFloat(EntityPlayer player) {
        return player == null ? 0.0F : player.getHealth() + player.getAbsorptionAmount();
    }

    public static boolean matchesEntityPlayer3(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        if (player.getGameProfile() == null) {
            return false;
        }
        return player.getGameProfile().getId() == null ? false : player.getGameProfile().getId().version() == 1;
    }
}
