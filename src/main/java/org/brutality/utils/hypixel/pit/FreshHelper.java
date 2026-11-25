package org.brutality.utils.hypixel.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.brutality.alt.altmanager.AccountStatusStore;

public class FreshHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean flag = false;

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        if (player == null) {
            return true;
        }
        for (int i = 0; i < 3; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (i == 1) {
                    if ((stack.getItem() != Items.leather_leggings || !stack.getDisplayName().contains("Fresh"))
                        && item.getArmorMaterial() != ArmorMaterial.IRON) {
                    }
                } else if (item.getArmorMaterial() != ArmorMaterial.IRON && item.getArmorMaterial() != ArmorMaterial.CHAIN) {
                    return false;
                }
            }
        }

        return true;
    }

    public static String computeString(String str, Entity entity) {
        return computeString(entity.posX, entity.posY, entity.posZ, str);
    }

    public static boolean matchesWorld3(World world, Entity entity) {
        return world != null && entity != null ? matchesWorld(world, entity.posX, entity.posY, entity.posZ) : false;
    }

    public static boolean matchesWorld(World world, Entity entity) {
        if (world != null && entity != null) {
            String str = computeString(world);
            double x = entity.posX;
            double y = entity.posY;
            double z = entity.posZ;
            return matchesString(str, x - 0.75, y, z)
                && matchesString(str, x + 0.75, y, z)
                && matchesString(str, x, y + 0.5, z)
                && matchesString(str, x, y, z - 0.75)
                && matchesString(str, x, y, z + 0.75);
        }
        return false;
    }

    private static boolean checkState3() {
        try {
            if (mc.getCurrentServerData() != null) {
                String str = mc.getCurrentServerData().serverIP.toLowerCase();
                if ((str.contains("hypixel") || str.contains("pit")) && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchesWorld4(World world) {
        return world.getBlockState(new BlockPos(-14, 96, 0)).getBlock().toString().contains("enchanting_table");
    }

    private static boolean matchesWorld5(World world) {
        return world.getBlockState(new BlockPos(-14, 115, 0)).getBlock().toString().contains("enchanting_table");
    }

    private static boolean matchesWorld2(World world) {
        return world.getBlockState(new BlockPos(-13, 115, 0)).getBlock().toString().contains("enchanting_table")
            && world.getBlockState(new BlockPos(-12, 114, 6)).getBlock().toString().contains("ender_chest");
    }

    private static boolean matchesWorld(World world) {
        return world.getBlockState(new BlockPos(-17, 87, 0)).getBlock().toString().contains("enchanting_table");
    }

    public static void handleString(String str) {
        if (str != null && mc.thePlayer != null) {
            String str2 = str.replaceAll("(?i)§[0-9a-fk-or]", "").toUpperCase();
            if (str2.contains("SERVER FOUND!") || str2.contains("PIT!")) {
                flag = true;

                try {
                    AccountStatusStore.handleString2(mc.thePlayer.getName());
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static boolean matchesDouble2(double d, double d2, double d3) {
        return d >= 60.0 && d <= 82.0 && d2 >= 62.0 && d2 <= 80.0 && d3 >= -82.0 && d3 <= -58.0;
    }

    public static boolean matchesWorld(World world, Entity entity, double d) {
        if (entity == null) {
            return false;
        }
        double dist = entity.getDistance(0.5, entity.posY, 0.5);
        return dist < d && entity.posY > 42.0 && entity.posY < 95.0;
    }

    private static boolean matchesEntityPlayer(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (item.getArmorMaterial() == ArmorMaterial.DIAMOND) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String computeString(double d, double d2, double d3, World world) {
        return computeString(d, d2, d3, computeString(world));
    }

    public static boolean matchesWorld5(World world, Entity entity) {
        double dist = entity.getDistance(0.5, entity.posY, 0.5);
        String str = computeString(world);
        if (!"elemental".equals(str) && !"seasons".equals(str) && !"corals".equals(str)) {
            return !"kings".equals(str) && !"genesis".equals(str) ? false : dist < 33.0 && entity.posY > 120.0;
        }
        return dist < 33.0 && entity.posY > 140.0;
    }

    public static String computeString(Entity entity, World world) {
        return computeString(entity.posX, entity.posY, entity.posZ, world);
    }

    public static boolean checkState() {
        try {
            WorldClient world = mc.theWorld;
            if (world == null) {
                return false;
            }
            double diameter = world.getWorldBorder().getDiameter();
            return (diameter == 400.0 || diameter == 401.0) && Math.abs(world.getSpawnPoint().getX()) < 5 && Math.abs(world.getSpawnPoint().getZ()) < 5;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchesWorld3(World world) {
        try {
            BlockPos pos = new BlockPos(0, 0, 0);
            if (world.getBlockState(pos).getBlock().toString().contains("bedrock")) {
                BlockPos pos2 = new BlockPos(32, 100, 0);
                BlockPos pos3 = new BlockPos(-32, 100, 0);
                BlockPos pos4 = new BlockPos(0, 100, 32);
                BlockPos pos5 = new BlockPos(0, 100, -32);
                if (world.getBlockState(pos2).getBlock().toString().contains("barrier")
                    || world.getBlockState(pos3).getBlock().toString().contains("barrier")
                    || world.getBlockState(pos4).getBlock().toString().contains("barrier")
                    || world.getBlockState(pos5).getBlock().toString().contains("barrier")) {
                    return true;
                }

                BlockPos pos6 = new BlockPos(0, -1, 0);
                if (world.isAirBlock(pos6)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkState2() {
        if (mc.theWorld == null || mc.thePlayer == null || mc.isGamePaused()) {
            return false;
        }
        if (computeString(mc.theWorld) != null) {
            return true;
        }
        if (matchesWorld3(mc.theWorld)) {
            return true;
        }
        return checkState4() ? true : checkState3();
    }

    public static int computeInt() {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        if (world == null) {
            return 0;
        }
        int n = 0;

        for (Entity entity : world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer && matchesWorld4(world, entity)) {
                n++;
            }
        }

        return n;
    }

    public static boolean matchesWorld4(World world, Entity entity) {
        if (entity == null) {
            return false;
        }
        double dist = entity.getDistance(0.5, entity.posY, 0.5);
        return dist < 19.0 && entity.posY > 42.0 && entity.posY < 95.0;
    }

    public static boolean matchesWorld(World world, double d, double d2, double d3) {
        if (world == null) {
            return false;
        }
        String str = computeString(world);
        return matchesString(str, d, d2, d3);
    }

    public static int computeInt2() {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        if (world == null) {
            return 0;
        }
        int n = 0;

        for (Entity entity : world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer && matchesWorld4(world, entity)) {
                EntityPlayer player = (EntityPlayer)entity;
                if (!matchesEntityPlayer(player)) {
                    n++;
                }
            }
        }

        return n;
    }

    private static boolean checkState5() {
        if (mc.theWorld != null && mc.thePlayer != null && !mc.isGamePaused()) {
            try {
                if (computeString(mc.theWorld) != null) {
                    return true;
                }

                if (checkState4()) {
                    return true;
                }
            } catch (Exception ignored) {
            }

            return false;
        }
        return false;
    }

    public static boolean matchesWorld2(World world, Entity entity) {
        return matchesWorld3(world, entity);
    }

    private static boolean matchesWorld6(World world) {
        return world.getBlockState(new BlockPos(-13, 115, 0)).getBlock().toString().contains("enchanting_table")
            && world.getBlockState(new BlockPos(-13, 114, 7)).getBlock().toString().contains("ender_chest");
    }

    private static boolean checkState4() {
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            if (scoreboard == null) {
                return false;
            }
            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (objectiveInDisplaySlot == null) {
                return false;
            }
            String str = objectiveInDisplaySlot.getDisplayName().toLowerCase();
            if (!str.contains("pit") && !str.contains("hypixel pit") && !str.contains("the pit")) {
                for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                    String str2 = score.getPlayerName().toLowerCase();
                    if (str2.contains("gold") && str2.contains("g")
                        || str2.contains("prestige")
                        || str2.contains("bounty")
                        || str2.contains("killstreak")
                        || str2.contains("mystic")
                        || str2.contains("pit.hypixel.net")) {
                        return true;
                    }
                }

                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void process() {
        boolean flag2 = checkState2();
        flag = flag2;
        if (mc.thePlayer != null && checkState5()) {
            try {
                AccountStatusStore.handleString2(mc.thePlayer.getName());
            } catch (Exception ignored) {
            }
        }
    }

    private static boolean matchesDouble(double d, double d2, double d3) {
        return d >= -82.0 && d <= -60.0 && d2 >= 62.0 && d2 <= 75.0 && d3 >= 60.0 && d3 <= 82.0;
    }

    public static boolean matchesString(String str, double d, double d2, double d3) {
        if ("genesis".equals(str)) {
            return matchesDouble2(d, d2, d3) || matchesDouble(d, d2, d3) || matchesDouble3(d, d2, d3);
        }
        int n = computeInt(str);
        if (n <= 0) {
            return false;
        }
        return !(d > 23.0) && !(d < -23.0) && !(d3 > 23.0) && !(d3 < -23.0) ? d2 >= n && d2 < n + 12.0 : false;
    }

    private static String computeString(double d, double d2, double d3, String str) {
        if (str == null) {
            return EnumChatFormatting.GOLD + "Outskirts";
        }
        if (matchesString(str, d, d2, d3)) {
            return "" + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + "Spawn";
        }
        if (Math.hypot(d - 0.5, d3 - 0.5) < 19.0 && d2 > 42.0 && d2 < 95.0) {
            return "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "PIT";
        }
        switch (str) {
            case "elemental":
                if (d <= 0.0 && d >= -200.0 && d3 <= 0.0 && d3 >= -200.0) {
                    return "" + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + "Water";
                } else if (d >= 0.0 && d <= 200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Lava";
                } else if (d <= 0.0 && d >= -200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + "Sky";
                } else {
                    if (d >= 0.0 && d <= 200.0 && d3 <= 0.0 && d3 >= -200.0) {
                        return "" + EnumChatFormatting.DARK_GREEN + EnumChatFormatting.BOLD + "Mountain";
                    }

                    return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Outskirts";
                }
            case "seasons":
                if (d <= 0.0 && d >= -200.0 && d3 <= 0.0 && d3 >= -200.0) {
                    return "" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + "Summer";
                } else if (d >= 0.0 && d <= 200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + "Winter";
                } else if (d <= 0.0 && d >= -200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.LIGHT_PURPLE + EnumChatFormatting.BOLD + "Spring";
                } else {
                    if (d >= 0.0 && d <= 200.0 && d3 <= 0.0 && d3 >= -200.0) {
                        return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Autumn";
                    }

                    return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Outskirts";
                }
            case "corals":
                if (d <= 0.0 && d >= -200.0 && d3 <= 0.0 && d3 >= -200.0) {
                    return "" + EnumChatFormatting.DARK_GREEN + EnumChatFormatting.BOLD + "Seaweed";
                } else if (d >= 0.0 && d <= 200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Geyser";
                } else if (d <= 0.0 && d >= -200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + "Shipwreck";
                } else {
                    if (d >= 0.0 && d <= 200.0 && d3 <= 0.0 && d3 >= -200.0) {
                        return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Temple";
                    }

                    return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Outskirts";
                }
            case "kings":
                if (d <= 0.0 && d >= -200.0 && d3 <= 0.0 && d3 >= -200.0) {
                    return "" + EnumChatFormatting.DARK_GREEN + EnumChatFormatting.BOLD + "Jungle";
                } else if (d >= 0.0 && d <= 200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    if (d >= 2.0 && d <= 119.0 && d2 >= 54.0 && d2 <= 62.0 && d3 <= 84.0 && d3 >= 11.0) {
                        return "" + EnumChatFormatting.DARK_AQUA + EnumChatFormatting.BOLD + "Sewers";
                    }

                    return "" + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + "City";
                } else if (d <= 0.0 && d >= -200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Port";
                } else {
                    if (d >= 0.0 && d <= 200.0 && d3 <= 0.0 && d3 >= -200.0) {
                        return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Farms";
                    }

                    return EnumChatFormatting.GOLD + "Outskirts";
                }
            case "genesis":
                if (d <= 0.0 && d >= -200.0 && d3 <= 0.0 && d3 >= -200.0) {
                    return "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Badlands";
                } else if (d >= 0.0 && d <= 200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.DARK_GREEN + EnumChatFormatting.BOLD + "Garden";
                } else if (d <= 0.0 && d >= -200.0 && d3 >= 0.0 && d3 <= 200.0) {
                    return "" + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + "Palace";
                } else {
                    if (d >= 0.0 && d <= 200.0 && d3 <= 0.0 && d3 >= -200.0) {
                        return "" + EnumChatFormatting.DARK_RED + EnumChatFormatting.BOLD + "Fortress";
                    }

                    return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Outskirts";
                }
            default:
                return "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Outskirts";
        }
    }

    private static int computeInt(String str) {
        if ("kings".equals(str)) {
            return 95;
        }
        if ("genesis".equals(str)) {
            return 86;
        }
        return str != null ? 114 : -1;
    }

    private static boolean matchesDouble3(double d, double d2, double d3) {
        return d >= -20.0 && d <= 20.0 && d2 >= 86.0 && d2 <= 101.0 && d3 >= -20.0 && d3 <= 20.0;
    }

    public static String computeString(World world) {
        if (world == null) {
            return null;
        }
        try {
            if (matchesWorld6(world)) {
                return "elemental";
            }

            if (matchesWorld5(world)) {
                return "seasons";
            }

            if (matchesWorld2(world)) {
                return "corals";
            }

            if (matchesWorld4(world)) {
                return "kings";
            }

            if (matchesWorld(world)) {
                return "genesis";
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
