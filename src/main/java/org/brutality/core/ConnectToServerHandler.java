package org.brutality.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import org.brutality.automation.hunting.HuntingHandler3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.pit.PitEvent11;
import org.brutality.event.impl.pit.PitEvent14;
import org.brutality.event.impl.pit.PitEvent15;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.GetOrderedTeamsHelper;
import org.brutality.util.GetScaleHelper;
import org.brutality.util.PizzaType;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.module.FourSeasonsType;
import org.brutality.utils.network.ServerUtils;

public class ConnectToServerHandler {
    private static boolean flag;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ConnectToServerHandler instance = new ConnectToServerHandler();
    private static World world = null;
    private static FourSeasonsType fourSeasonsType = FourSeasonsType.UNKNOWN;
    private static boolean flag2 = false;
    public static int count = 0;
    public static int count2 = 0;
    private static StopwatchTimer utilHelper8 = new StopwatchTimer();
    public static StopwatchTimer utilHelper82 = new StopwatchTimer();
    private static final StopwatchTimer utilHelper83 = new StopwatchTimer(0L);
    private static int count3 = 0;
    private static int count4 = 0;
    private static final StopwatchTimer utilHelper84 = new StopwatchTimer(0L);
    private static int count5 = 0;
    private static final StopwatchTimer utilHelper85 = new StopwatchTimer(0L);
    private static final StopwatchTimer utilHelper86 = new StopwatchTimer(0L);
    private static final int count6 = 40;
    private static int count7 = 0;
    private static boolean flag3 = false;
    public static String text = "Remaining";
    private static final StopwatchTimer utilHelper87 = new StopwatchTimer();
    private static PizzaType pizzaType = null;
    private static final StopwatchTimer utilHelper88 = new StopwatchTimer();
    public static boolean flag4 = false;
    private static final StopwatchTimer utilHelper89 = new StopwatchTimer();
    private static final StopwatchTimer utilHelper810 = new StopwatchTimer();
    private static final String[] stringArray = new String[]{
        "Please don't spam the command!",
        "You are sending commands too fast! Please slow down.",
        "Woah there, slow down!",
        "You are trying to do that too fast. Try again in a moment.",
        "COOLDOWN! You are queueing too fast!"
    };
    private static HashMap<FourSeasonsType, Integer> lookupMap = new ConnectToServerHandler.Entry();
    private static HashMap<FourSeasonsType, Integer> lookupMap2 = new ConnectToServerHandler.Entry2();

    public static void onEvent(Event event) {
        if (event instanceof EventTick) {
            instance.onClientTick((EventTick)event);
        } else if (event instanceof GetPacketEvent) {
            instance.onPacket((GetPacketEvent)event);
        } else {
            if (event instanceof PitEvent11) {
                return;
            }

            if (event instanceof ChatEvent) {
                instance.onClientChatReceived((ChatEvent)event);
            } else if (event instanceof RenderEvent4) {
                return;
            }
        }
    }

    private static void process() {
        count7 = 40;
    }

    private static void process3() {
        if (fourSeasonsType == FourSeasonsType.UNKNOWN && utilHelper86.matchesLong(1000L)) {
            utilHelper86.reset();
            FourSeasonsType fourSeasonsType2 = computeFourSeasonsType(mc.theWorld);
            if (fourSeasonsType2 != FourSeasonsType.UNKNOWN) {
                handleFourSeasonsType(fourSeasonsType2);
                flag4 = true;
            }
        }
    }

    public static boolean checkState7() {
        String str = GetScaleHelper.computeString(true);
        if (str != null && !str.isEmpty()) {
            String str2 = str.toLowerCase(Locale.ENGLISH);
            return str2.contains("major event") && !str2.contains("starting");
        }
        return false;
    }

    public static void process2() {
        connectToServer("mc.hypixel.net");
    }

    public static boolean checkState11() {
        return checkState2() && count7 > 0;
    }

    private static boolean matchesScoreObjective(ScoreObjective objective) {
        if (objective == null) {
            return false;
        }
        String str = computeString(objective.getName());
        String str2 = computeString(objective.getDisplayName());
        return str.contains("pit") || str2.contains("hypixel pit");
    }

    public static int getCount5() {
        return count5;
    }

    public static boolean checkState9() {
        return !utilHelper8.matchesLong(4500L);
    }

    public static boolean checkState2() {
        return flag3 || checkState3() || checkState();
    }

    public void onWorldLoad(RenderEvent4 renderEvent4) {
        if (mc.thePlayer != null && renderEvent4.world != null) {
            world = mc.theWorld;
            process4();
            utilHelper82.reset();
        }
    }

    public void onClientTick(EventTick event) {
        if (event.getState() == EventState.POST && mc.thePlayer != null && mc.theWorld != null) {
            if (world != mc.theWorld) {
                world = mc.theWorld;
                process4();
            }

            if (flag3 && count7 > 0) {
                count7--;
            }

            if (utilHelper85.matchesLong(0L)) {
                utilHelper85.reset();
                boolean flag2 = flag3;
                flag3 = checkState3() || checkState();
                if (flag3 && (!flag2 || !flag4)) {
                    utilHelper8.reset();
                    if (!flag2) {
                        process();
                    }

                    new PitEvent11().process();
                } else if (!flag3 && flag2) {
                    flag = false;
                    flag4 = false;
                    count7 = 0;
                    handleFourSeasonsType(FourSeasonsType.UNKNOWN);
                }
            }

            if (flag3) {
                process3();
                if (utilHelper83.matchesLong(500L)) {
                    utilHelper83.reset();
                    int n = 0;
                    int n2 = 0;

                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        if (GetDisplayedTitleHelper.matchesEntityPlayer(playerEntity)) {
                            n++;
                            if (!GetMysticEnchantLoreHelper.matchesEntityPlayer10(playerEntity)) {
                                n2++;
                            }
                        }
                    }

                    count3 = n;
                    count4 = n2;
                }

                if (utilHelper84.matchesLong(500L)) {
                    utilHelper84.reset();
                    int n3 = 0;

                    for (EntityPlayer playerEntity2 : mc.theWorld.playerEntities) {
                        if (AutoGrinder.matchesEntityPlayer3(playerEntity2)) {
                            n3++;
                        }
                    }

                    count5 = n3;
                }

                boolean flag5 = GetDisplayedTitleHelper.matchesEntityLivingBase3(mc.thePlayer);
                if (flag5 && !flag) {
                    new PitEvent14().process();
                } else if (!flag5 && flag) {
                    new PitEvent15().process();
                }

                flag = flag5;
                if (checkState12()) {
                    utilHelper88.reset();
                }

                if (utilHelper87.matchesLong(1000L)) {
                    utilHelper87.reset();
                    pizzaType = null;

                    for (PizzaType pizzaType2 : PizzaType.values()) {
                        String str = pizzaType2.getIdentifier().toUpperCase(Locale.US);
                        if (GetOrderedTeamsHelper.computeInt(mc.theWorld.getScoreboard(), str) != -1) {
                            pizzaType = pizzaType2;
                            return;
                        }
                    }
                }
            }
        }
    }

    public static int getCount3() {
        return count3;
    }

    public static int computeInt2() {
        int n = GetOrderedTeamsHelper.computeInt2(mc.theWorld.getScoreboard(), "#");
        if (n == -1) {
            return -1;
        }
        String str = GetOrderedTeamsHelper.computeString(mc.theWorld.getScoreboard(), n);
        return str != null && !str.isEmpty() ? computeInt(str) : -1;
    }

    public void onPacket(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getDirection() == PacketDirection.INCOMING) {
            Packet packet = getPacketEvent.getPacket();
            if (packet instanceof S23PacketBlockChange) {
                HuntingHandler3.handleBlockPos(((S23PacketBlockChange)packet).getBlockPosition());
            }

            if (packet instanceof S22PacketMultiBlockChange) {
                BlockUpdateData[] changedBlocks = ((S22PacketMultiBlockChange)packet).getChangedBlocks();
                int len = changedBlocks.length;

                for (int i = 0; i < len; i++) {
                    HuntingHandler3.handleBlockPos(changedBlocks[i].getPos());
                }
            }
        }
    }

    public static void connectToServer(String str) {
        mc.addScheduledTask(() -> {
            if (mc.theWorld == null) {
                mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, new ServerData("Server", str, false)));
            }
        });
    }

    public static boolean checkState6() {
        return !utilHelper88.matchesLong(2500L);
    }

    public static int computeInt(String str) {
        String[] parts = ExecuteCommandHelper.computeString6(str).split("#");
        if (parts.length < 2) {
            return -1;
        }
        String str2 = parts[1];
        if (str2.length() < 2) {
            return -1;
        }
        char ch = str2.charAt(0);
        char ch2 = str2.charAt(1);
        if (!Character.isDigit(ch)) {
            return -1;
        }
        int numericValue = Character.getNumericValue(ch);
        return Character.isDigit(ch2) ? numericValue * 10 + Character.getNumericValue(ch2) : numericValue;
    }

    public static boolean checkState8() {
        return !utilHelper810.matchesLong(30000L) ? true : !utilHelper89.matchesLong(10000L);
    }

    public static PizzaType getPizzaType() {
        return pizzaType;
    }

    public static boolean checkState5() {
        String str = GetScaleHelper.computeString(true);
        if (str != null && !str.isEmpty()) {
            String str2 = str.toLowerCase();
            if (str2.contains("koth") && str2.contains("ending in")) {
                String[] parts = str2.split("ending in ");
                if (parts.length < 2) {
                    return false;
                }
                String str3 = parts[1].trim();
                return !str3.equals("00:00") && !str3.equals("00:01");
            }
            return false;
        }
        return false;
    }

    public void onPitStateUpdate(PitEvent11 pitEvent11) {
        flag4 = true;
        if (mc.theWorld != null) {
            FourSeasonsType fourSeasonsType2 = computeFourSeasonsType(mc.theWorld);
            if (fourSeasonsType2 != FourSeasonsType.UNKNOWN && fourSeasonsType2 != fourSeasonsType) {
                handleFourSeasonsType(fourSeasonsType2);
            }
        }

        HuntingHandler3.process();
    }

    public static int computeInt4() {
        if (count2 == 0) {
            count2 = lookupMap2.get(fourSeasonsType);
        }

        return count2;
    }

    private static void process4() {
        flag3 = false;
        flag = false;
        flag4 = false;
        process();
        handleFourSeasonsType(FourSeasonsType.UNKNOWN);
    }

    public static int getCount4() {
        return count4;
    }

    public static void handleFourSeasonsType(FourSeasonsType fourSeasonsType2) {
        fourSeasonsType = fourSeasonsType2;
        count = 0;
        count2 = 0;
    }

    private static boolean checkState3() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            for (ScoreObjective scoreObjective : mc.theWorld.getScoreboard().getScoreObjectives()) {
                if (matchesScoreObjective(scoreObjective)) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    public static boolean checkState12() {
        return computeInt5() > 1;
    }

    public static int computeInt3() {
        if (mc.thePlayer == null) {
            return -1;
        }
        return mc.theWorld == null ? -1 : (int)(mc.theWorld.getWorldTime() / 24000L);
    }

    private static boolean checkState() {
        return fourSeasonsType != FourSeasonsType.UNKNOWN ? true : mc.theWorld != null && computeFourSeasonsType(mc.theWorld) != FourSeasonsType.UNKNOWN;
    }

    public static BlockPos computeBlockPos() {
        return new BlockPos(0, computeInt4(), 0);
    }

    public static FourSeasonsType getFourSeasonsType() {
        return fourSeasonsType;
    }

    public void onClientChatReceived(ChatEvent chatEvent) {
        String text = chatEvent.message.getUnformattedText();
        String str = ExecuteCommandHelper.computeString6(text);
        if (str.startsWith("PIT! Latest update:")) {
            flag3 = true;
            process();
            new PitEvent11().process();
            utilHelper8.reset();
        }

        if (utilHelper89.matchesLong(10000L)) {
            for (String str2 : stringArray) {
                if (str.startsWith(str2)) {
                    long l = System.currentTimeMillis() - utilHelper89.getTimestamp();
                    if (l < 15000L) {
                        utilHelper810.reset();
                    }

                    utilHelper89.reset();
                }
            }
        }
    }

    public static boolean checkState4() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            Collection scoreObjectives = mc.theWorld.getScoreboard().getScoreObjectives();
            BlockPos pos = new BlockPos(-22, 31, 21);
            return scoreObjectives.isEmpty() && mc.theWorld.getBlockState(pos).getBlock() == Blocks.carpet;
        }
        return false;
    }

    public static int computeInt6() {
        return checkState2() ? count7 : 0;
    }

    public static FourSeasonsType computeFourSeasonsType(World world) {
        try {
            if (ServerUtils.checkState5()
                && world.getBlockState(new BlockPos(150, 100, 143)).getBlock() == Blocks.enchanting_table
                && world.getBlockState(new BlockPos(152, 99, 148)).getBlock() == Blocks.ender_chest) {
                return FourSeasonsType.FOUR_SEASONS;
            }
            BlockPos pos = new BlockPos(-13, 115, 0);
            BlockPos pos2 = new BlockPos(-14, 115, 0);
            BlockPos pos3 = new BlockPos(-13, 114, 7);
            BlockPos pos4 = new BlockPos(-12, 114, 6);
            BlockPos pos5 = new BlockPos(-14, 96, 0);
            BlockPos pos6 = new BlockPos(-17, 87, 0);
            if (world.getBlockState(pos).getBlock() == Blocks.enchanting_table && world.getBlockState(pos3).getBlock() == Blocks.ender_chest) {
                return FourSeasonsType.ELEMENTS;
            }
            if (world.getBlockState(pos2).getBlock() == Blocks.enchanting_table) {
                return FourSeasonsType.FOUR_SEASONS;
            }
            if (world.getBlockState(pos).getBlock() == Blocks.enchanting_table && world.getBlockState(pos4).getBlock() == Blocks.ender_chest) {
                return FourSeasonsType.ABYSS;
            }
            if (world.getBlockState(pos5).getBlock() == Blocks.enchanting_table) {
                return FourSeasonsType.CASTLE;
            }
            return world.getBlockState(pos6).getBlock() == Blocks.enchanting_table ? FourSeasonsType.GENESIS : FourSeasonsType.UNKNOWN;
        } catch (Exception e) {
            ExecuteCommandHelper.handleString5(String.valueOf(new StringBuilder("Failed to get map from world: ").append(e.getMessage())));
            return FourSeasonsType.UNKNOWN;
        }
    }

    public static void handleString(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.getNetHandler() != null) {
            mc.getNetHandler().getNetworkManager().closeChannel(new ChatComponentText(str));
            mc.displayGuiScreen(new GuiDisconnected(new GuiMainMenu(), "disconnect.lost", new ChatComponentText(str)));
        }
    }

    public static int computeInt5() {
        int n = GetOrderedTeamsHelper.computeInt(mc.theWorld.getScoreboard(), text);
        if (n == -1) {
            return -1;
        }
        String str = GetOrderedTeamsHelper.computeString(mc.theWorld.getScoreboard(), n);
        if (str != null && !str.isEmpty()) {
            String[] parts = ExecuteCommandHelper.computeString6(str).split(" ");
            if (parts.length < 2) {
                return -1;
            }
            String str2 = parts[1];
            if (str2.startsWith("IT")) {
                return -1;
            }
            try {
                String[] parts2 = str2.split(":");
                if (parts2.length < 2) {
                    return -1;
                }
                int n2 = Integer.parseInt(parts2[0]);
                int n3 = Integer.parseInt(parts2[1]);
                return n3 + n2 * 60;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ExecuteCommandHelper.handleString6(e.getMessage());
                return -1;
            }
        } else {
            return -1;
        }
    }

    private static String computeString(String str) {
        return str == null ? "" : ExecuteCommandHelper.computeString6(str).trim().toLowerCase(Locale.ENGLISH);
    }

    public static boolean checkState10() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (checkState2()) {
                return false;
            }
            for (ScoreObjective scoreObjective : mc.theWorld.getScoreboard().getScoreObjectives()) {
                if (computeString(scoreObjective.getName()).contains("mainscoreboard")) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    public static int computeInt() {
        if (count == 0) {
            count = lookupMap.get(fourSeasonsType);
        }

        return count;
    }

    public static class Entry extends HashMap<FourSeasonsType, Integer> {
        public Entry() {
            this.put(FourSeasonsType.CASTLE, 95);
            this.put(FourSeasonsType.ABYSS, 114);
            this.put(FourSeasonsType.ELEMENTS, 114);
            this.put(FourSeasonsType.FOUR_SEASONS, 114);
            this.put(FourSeasonsType.GENESIS, 86);
            this.put(FourSeasonsType.UNKNOWN, 0);
        }
    }

    public static class Entry2 extends HashMap<FourSeasonsType, Integer> {
        public Entry2() {
            this.put(FourSeasonsType.CASTLE, 71);
            this.put(FourSeasonsType.ABYSS, 82);
            this.put(FourSeasonsType.ELEMENTS, 82);
            this.put(FourSeasonsType.FOUR_SEASONS, 82);
            this.put(FourSeasonsType.GENESIS, 43);
            this.put(FourSeasonsType.UNKNOWN, 0);
        }
    }
}
