package org.brutality.module.impl.pit.hypixel.hud;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class TDM extends Module {
    private static TDM instance;
    private final PitCategory pitCategory = PitCategory.MAJOR_EVENT;
    private static final Pattern pattern = Pattern.compile("\\b(?:tdm|team\\s+deathmatch)\\b", 2);
    private World world;
    private EntityPlayer entityPlayer;
    private int count = Integer.MIN_VALUE;
    private Integer integer;
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final Map<String, Long> lookupMap = new HashMap<>();
    private long timestamp;
    private long timestamp2;
    private long timestamp3;
    private String text = "<none>";
    private String text2 = "unknown";
    private static final Pattern pattern2 = Pattern.compile("[^\\x20-\\x7E§]");

    public static String computeString(EntityPlayer player) {
        TDM instance = getInstance();
        if (instance != null && player != null && instance.computeInteger() != null) {
            Integer integer = instance.computeInteger(player);
            return Integer.valueOf(14).equals(integer) ? "Red" : (Integer.valueOf(11).equals(integer) ? "Blue" : "Unknown");
        }
        return "Unknown";
    }

    private String computeString2(EntityPlayer player) {
        if (player != null && player.inventory != null) {
            ItemStack stack = player.inventory.armorInventory[3];
            String str;
            if (stack == null) {
                str = "<none>";
            } else {
                str = computeString(stack.getDisplayName());
                int idFromItem = Item.getIdFromItem(stack.getItem());
                int itemDamage = stack.getItemDamage();
                Integer integer = this.computeInteger(stack);
                int n = itemDamage;
                int n2 = idFromItem;
                str = str + "/item=" + n2 + "/meta=" + n + "/wool=" + integer;
            }

            return str;
        }
        return "<none>";
    }

    private String computeString() {
        Integer integer = this.computeInteger();
        boolean flag = this.checkState44();
        boolean flag2 = FreshHelper.flag;
        String str = computeString(this.text);
        String str2 = this.text2;
        Integer integer2 = this.computeInteger2(mc.thePlayer);
        String str3 = this.computeString2(mc.thePlayer);
        String str4 = str2;
        String str5 = str;
        boolean flag3 = flag2;
        boolean flag4 = flag;
        return "enabled=" + flag4 + " pit=" + flag3 + " event=[" + str5 + "] own=" + integer + " source=" + str4 + " ownHelmet=" + str3 + " inventoryWool=" + integer2;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.world = null;
                this.entityPlayer = null;
                this.integer = null;
                this.lookupMap.clear();
            } else if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE && mc.theWorld != null && mc.thePlayer != null) {
                this.computeInteger();
                if (this.debugSetting.isEnabled() && System.currentTimeMillis() - this.timestamp >= 2000L) {
                    this.timestamp = System.currentTimeMillis();
                    MessageUtils.x("§d[TDM Debug] §f" + this.computeString());
                }
            }
        }
    }

    private static String computeString(String str) {
        return str == null ? "<none>" : str.replace('§', '&');
    }

    private Integer computeInteger2(EntityPlayer player) {
        if (player != null && player.inventory != null) {
            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                Integer integer = this.computeInteger(stack);
                if (integer != null) {
                    return integer;
                }
            }

            return null;
        }
        return null;
    }

    private static TDM getInstance() {
        return instance;
    }

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        TDM instance = getInstance();
        return instance != null && instance.checkState44() && matchesEntityPlayer3(player);
    }

    public static void handleEntityPlayer(EntityPlayer player, String str, boolean flag, boolean flag2) {
        TDM instance = getInstance();
        if (instance != null && instance.debugSetting != null && instance.debugSetting.isEnabled() && mc.thePlayer != null && mc.theWorld != null && player != null) {
            if (!str.startsWith("filter:") || !(mc.thePlayer.getDistanceSqToEntity(player) > 36.0)) {
                String str2 = str + ":" + player.getEntityId() + ":" + flag2 + ":" + flag;
                long now = System.currentTimeMillis();
                boolean flag3 = str.startsWith("attack");
                if (now - (flag3 ? instance.timestamp3 : instance.timestamp2) >= 250L) {
                    Long val = instance.lookupMap.get(str2);
                    if (val == null || now - val >= 3000L) {
                        if (instance.lookupMap.size() >= 128) {
                            Iterator it = instance.lookupMap.keySet().iterator();
                            it.next();
                            it.remove();
                        }

                        instance.lookupMap.put(str2, now);
                        if (flag3) {
                            instance.timestamp3 = now;
                        } else {
                            instance.timestamp2 = now;
                        }

                        NetworkPlayerInfo playerInfo = mc.getNetHandler() == null ? null : mc.getNetHandler().getPlayerInfo(player.getUniqueID());
                        String str3 = playerInfo != null && playerInfo.getDisplayName() != null ? playerInfo.getDisplayName().getFormattedText() : "<none>";
                        ScorePlayerTeam playersTeam = mc.theWorld.getScoreboard().getPlayersTeam(player.getName());
                        String str4 = flag2 ? "BLOCK_TEAM" : "PASS_TEAM_CHECK";
                        String name = player.getName();
                        String str5 = instance.computeString2(player);
                        String str6 = playersTeam == null ? "<none>" : playersTeam.getTeamName() + "/prefix=" + computeString(playersTeam.getColorPrefix());
                        String str7 = computeString(player.getDisplayName().getFormattedText());
                        String str8 = computeString(str3);
                        String str9 = instance.computeString();
                        String str10 = str8;
                        String str11 = str7;
                        String str12 = str6;
                        String str13 = str5;
                        String str14 = name;
                        String str15 = str4;
                        MessageUtils.x(
                            "§d[TDM Debug] §fstage="
                                + str
                                + " decision="
                                + str15
                                + " Teams="
                                + flag
                                + " target="
                                + str14
                                + " helmet="
                                + str13
                                + " team="
                                + str12
                                + " name=["
                                + str11
                                + "] tab=["
                                + str10
                                + "] "
                                + str9
                        );
                    }
                }
            }
        }
    }

    private Integer computeInteger(EntityPlayer player) {
        return player != null && player.inventory != null ? this.computeInteger(player.inventory.armorInventory[3]) : null;
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        TDM instance = getInstance();
        return instance != null && instance.checkState44() && player != null && instance.computeInteger() != null && instance.computeInteger(player) != null;
    }

    private static Integer computeInteger(String str) {
        if (str == null) {
            return null;
        }
        String str2 = pattern2.matcher(str).replaceAll("");
        String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(str2);
        if (!textWithoutFormattingCodes.trim().toLowerCase(Locale.ROOT).startsWith("event:")) {
            return null;
        }
        Matcher matcher = pattern.matcher(textWithoutFormattingCodes);
        if (!matcher.find()) {
            return null;
        }
        char ch = 'r';
        int n = 0;

        for (int i = 0; i < str2.length(); i++) {
            char ch2 = str2.charAt(i);
            if (ch2 == 167 && i + 1 < str2.length()) {
                char str3 = Character.toLowerCase(str2.charAt(++i));
                if ("0123456789abcdefr".indexOf(str3) >= 0) {
                    ch = str3;
                }
            } else if (n++ == matcher.start()) {
                break;
            }
        }

        return ch == 99 ? 14 : (ch == 57 ? 11 : -1);
    }

    private Integer computeInteger() {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return null;
        }
        if (this.world == mc.theWorld && this.entityPlayer == mc.thePlayer && this.count == mc.thePlayer.ticksExisted) {
            return this.integer;
        }
        this.world = mc.theWorld;
        this.entityPlayer = mc.thePlayer;
        this.count = mc.thePlayer.ticksExisted;
        this.integer = null;
        this.text = "<none>";
        this.text2 = "unknown";
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
        if (objectiveInDisplaySlot == null) {
            return null;
        }
        for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
            String playerName = score.getPlayerName();
            if (playerName != null && !playerName.startsWith("#")) {
                String str = ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName);
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(str);
                if (textWithoutFormattingCodes != null && textWithoutFormattingCodes.trim().toLowerCase(Locale.ROOT).startsWith("event:")) {
                    this.text = str;
                }

                Integer integer = computeInteger(str);
                if (integer != null) {
                    this.text = str;
                    this.integer = integer == -1 ? this.computeInteger(mc.thePlayer) : integer;
                    this.text2 = integer == -1 ? "helmet" : "event-label";
                    if (this.integer == null) {
                        this.integer = this.computeInteger2(mc.thePlayer);
                        this.text2 = "inventory-wool";
                    }
                    break;
                }
            }
        }

        return this.integer;
    }

    @Override
    public void onDisable() {
        this.world = null;
        this.entityPlayer = null;
        this.integer = null;
        this.count = Integer.MIN_VALUE;
        this.lookupMap.clear();
        super.onDisable();
    }

    public TDM() {
        super("TDM", 0, Category.PIT);
        instance = this;
        this.handleSettingArray(new Setting[]{this.debugSetting});
    }

    public static boolean matchesEntityPlayer4(EntityPlayer player) {
        TDM instance = getInstance();
        if (instance != null && instance.checkState44() && mc.thePlayer != null && player != null) {
            Integer integer = instance.computeInteger();
            Integer integer2 = instance.computeInteger(player);
            return integer != null && integer2 != null && !integer.equals(integer2);
        }
        return false;
    }

    public static boolean matchesEntityPlayer3(EntityPlayer player) {
        TDM instance = getInstance();
        if (instance != null && mc.thePlayer != null && player != null) {
            Integer integer = instance.computeInteger();
            Integer integer2 = instance.computeInteger(player);
            return integer != null && integer.equals(integer2);
        }
        return false;
    }

    private Integer computeInteger(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() == Blocks.wool) {
            int itemDamage = stack.getItemDamage();
            return itemDamage != 14 && itemDamage != 11 ? null : itemDamage;
        }
        return null;
    }
}
