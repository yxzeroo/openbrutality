package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;

public final class PitDebugger extends Module {
    public static final String text = "unknown";
    private static PitDebugger instance;
    public static long tick;
    private final ModeSetting detailedSetting = new ModeSetting("Capture", this, "detailed", List.of("locations", "detailed"));
    private final ModeSetting outskirtsSetting = new ModeSetting("Zone", this, "spawn", List.of("spawn", "mid", "outskirts"));
    private final NumberSetting sessionMinutesSetting = new NumberSetting("Session Minutes", this, 5.0, 5.0, 10.0, 1.0);
    private final NumberSetting sampleEveryTicksSetting = new NumberSetting("Sample Every Ticks", this, 1.0, 1.0, 20.0, 1.0);
    private final BooleanSetting logPlayerMovementSetting = new BooleanSetting("Log Player Movement", this, true);
    private final BooleanSetting logChatSetting = new BooleanSetting("Log Chat", this, true);
    private final BooleanSetting logObservableActionsSetting = new BooleanSetting("Log Observable Actions", this, true);
    private final BooleanSetting logTabRosterSetting = new BooleanSetting("Log Tab Roster", this, true);
    private final BooleanSetting includeLocalPlayerSetting = new BooleanSetting("Include Local Player", this, true);
    public final Map<String, UUID> lookupMap = new HashMap<>();
    private final Map<UUID, PitDebugger.Entry> lookupMap2 = new HashMap<>();
    public final Set<UUID> itemSet = new HashSet<>();
    private final SampleTeamOrderHelper sampleTeamOrderHelper = new SampleTeamOrderHelper(this);
    private final ArrowSpawnHelper arrowSpawnHelper = new ArrowSpawnHelper(this);
    private final TrackHelper trackHelper = new TrackHelper(this);
    public PitSessionRecorder pitSessionRecorder;
    public WorldClient worldClient;
    public String text2 = "unknown";
    public String text3 = "unknown";
    public String text4 = "unknown";
    private String text5;
    private boolean flag;
    private boolean flag2;
    private String text6 = "unknown";
    private long timestamp;

    @Override
    public void onDisable() {
        this.handleString("disabled", true);
    }

    private JsonObject computeJsonObject(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        JsonArray jsonArray = new JsonArray();
        NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
        if (enchantmentTagList != null) {
            for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = enchantmentTagList.getCompoundTagAt(i);
                jsonArray.add(computeJsonArray(compoundTagAt.getShort("id"), compoundTagAt.getShort("lvl")));
            }
        }

        return computeJsonObject(
            "id",
            String.valueOf(Item.itemRegistry.getNameForObject(stack.getItem())),
            "meta",
            stack.getMetadata(),
            "count",
            stack.stackSize,
            "name",
            stack.hasDisplayName() ? this.sampleTeamOrderHelper.computeString2(stack.getDisplayName()) : null,
            "enchantments",
            jsonArray
        );
    }

    public static void handleEntityPlayer(EntityPlayer player, int n) {
        if (instance != null && instance.checkState() && player == instance.computeEntityPlayer() && instance.matchesEntityPlayer(player)) {
            instance.handleString(
                "bow_release_input", "local_item_use_end", false, computeJsonObject("player", instance.computeString(player.getUniqueID()), "drawTicks", n)
            );
        }
    }

    public static void handleList(List<NetworkPlayerInfo> playerInfos, int n) {
        if (instance != null && instance.checkState4()) {
            instance.sampleTeamOrderHelper.handleList(playerInfos, n);
        }
    }

    private void process3() {
        this.lookupMap2.clear();
        this.itemSet.clear();
        this.trackHelper.clear();
        this.arrowSpawnHelper.clear();
    }

    public EntityPlayer computeEntityPlayer() {
        return mc.thePlayer;
    }

    private PitDebugger.Entry frame(EntityPlayer player) {
        return this.lookupMap2
            .compute(player.getUniqueID(), (uuid, entry) -> (PitDebugger.Entry)(entry != null && entry.entityPlayer == player ? entry : new PitDebugger.Entry(player)));
    }

    public void handleEntityPlayer(EntityPlayer player, float f, String str) {
        PitDebugger.Entry entry = this.frame(player);
        if (Float.isFinite(f) && Float.compare(entry.health, f) != 0) {
            this.handleString(
                "visible_health",
                str,
                false,
                computeJsonObject(
                    "player", this.computeString(player.getUniqueID()), "health", f, "previous", Float.isFinite(entry.health) ? entry.health : null
                )
            );
            if (f > 0.0F && Float.isFinite(entry.health) && entry.health <= 0.0F && this.itemSet.remove(player.getUniqueID())) {
                this.handleString("respawn", "health_recovered_after_death", true, this.computeJsonObject(player));
            }

            entry.health = f;
        }
    }

    public static void handleEntity(Entity entity) {
        if (instance != null && instance.checkState() && entity instanceof EntityPlayer player && instance.matchesEntityPlayer(player)) {
            instance.trackHelper.handleEntityPlayer2(player);
        }
    }

    public static JsonArray computeJsonArray(long... ls) {
        JsonArray jsonArray = new JsonArray();

        for (long l : ls) {
            jsonArray.add(new JsonPrimitive(l));
        }

        return jsonArray;
    }

    private void process() {
        if (!"hub".equals(this.text4)) {
            String str = FreshHelper.computeString(this.worldClient);
            ScoreObjective objectiveInDisplaySlot = this.worldClient.getScoreboard().getObjectiveInDisplaySlot(1);
            if (str != null) {
                this.text5 = str;
                this.handleString(str, this.text3, "pit", "map_geometry", true);
            } else if (objectiveInDisplaySlot != null && "THE PIT".equalsIgnoreCase(SampleTeamOrderHelper.computeString(objectiveInDisplaySlot.getDisplayName()).trim())) {
                this.handleString(this.text2, this.text3, "pit", "sidebar_title", true);
            }
        }
    }

    @Override
    public void onEnable() {
        try {
            this.flag2 = "detailed".equals(this.detailedSetting.getText());
            this.pitSessionRecorder = new PitSessionRecorder(
                mc.mcDataDir.toPath(), this.flag2 ? this.outskirtsSetting.getText() : "lobby", (int)this.sessionMinutesSetting.computeDouble()
            );
            instance = this;
            this.lookupMap.clear();
            this.worldClient = null;
            this.flag = false;
            this.text6 = "unknown";
            this.timestamp = 0L;
            this.sampleTeamOrderHelper.timestamp4 = -1L;
            this.process10();
            this.handleString(
                "session_start",
                "client",
                false,
                computeJsonObject(
                    "zone",
                    this.pitSessionRecorder.text,
                    "minutes",
                    this.pitSessionRecorder.count,
                    "schema",
                    1,
                    "capture",
                    this.flag2 ? "detailed" : "locations",
                    "clientVersion",
                    "1.8.9",
                    "positionUnitsPerBlock",
                    1000,
                    "rotationUnitsPerDegree",
                    10
                )
            );
            this.process4();
            if (this.pitSessionRecorder != null) {
                MessageUtils.x("PitDebugger recording " + this.pitSessionRecorder.path.getFileName());
            }
        } catch (GeneralSecurityException | SecurityException | IOException e) {
            LogManager.getLogger(PitDebugger.class).warn("Could not start Pit recording", (Throwable)e);
            MessageUtils.x("PitDebugger could not start recording. See the client log.");
            this.handleFlag3(false);
        }
    }

    public static void handleScoreObjective(ScoreObjective objective, Collection<Score> scores) {
        if (instance != null) {
            instance.sampleTeamOrderHelper.handleScoreObjective(objective, scores);
        }
    }

    public String computeString(UUID uuid) {
        return this.pitSessionRecorder == null ? null : this.pitSessionRecorder.computeString(uuid);
    }

    public static void handleGuiChest(GuiChest gui, IInventory iInventory) {
        if (instance != null && instance.checkState4()) {
            instance.sampleTeamOrderHelper.handleGuiChest(gui, iInventory);
        }
    }

    private void process10() {
        this.text2 = this.text3 = this.text4 = "unknown";
        this.text5 = null;
        this.process3();
        this.sampleTeamOrderHelper.process();
    }

    public boolean checkState2() {
        return this.flag2 && this.logChatSetting.isEnabled();
    }

    private void handlePacket2(Packet<?> packet) {
        if (packet instanceof S01PacketJoinGame) {
            this.process10();
            this.lookupMap.clear();
            this.text6 = "unknown";
            this.flag = true;
            this.handleString("transition", "S01", false, computeJsonObject("type", "join"));
        } else {
            this.process4();
            if (packet instanceof S07PacketRespawn packet2) {
                EntityPlayerSP player = mc.thePlayer;
                boolean flag = player != null && player.dimension != packet2.getDimensionID();
                if (this.flag2) {
                    this.handleString("world_reset_packet", "S07", false, computeJsonObject("dimension", packet2.getDimensionID(), "dimensionChange", flag));
                }

                if (this.checkState()
                    && player != null
                    && this.matchesEntityPlayer(player)
                    && (this.itemSet.remove(player.getUniqueID()) || player.getHealth() <= 0.0F)) {
                    this.handleString("respawn", "S07_after_death", true, this.computeJsonObject(player));
                }

                this.sampleTeamOrderHelper.process3();
            } else if (packet instanceof S38PacketPlayerListItem packet3 && this.flag2) {
                this.sampleTeamOrderHelper.handleS38PacketPlayerListItem(packet3);
            } else if (packet instanceof S3EPacketTeams packet4 && this.checkState4()) {
                this.sampleTeamOrderHelper.handleS3EPacketTeams(packet4);
            } else if (packet instanceof S02PacketChat packet5) {
                this.sampleTeamOrderHelper.handleS02PacketChat(packet5);
            } else if (packet instanceof S47PacketPlayerListHeaderFooter packet6 && this.checkState4()) {
                this.sampleTeamOrderHelper.handleIChatComponent(packet6.getFooter());
            } else if (this.checkState()) {
                this.arrowSpawnHelper.handlePacket(packet);
            }
        }
    }

    public static void process7() {
        if (instance != null) {
            instance.process8();
        }
    }

    public void handleString(String str, String str2, boolean flag, JsonObject json) {
        if (this.pitSessionRecorder != null) {
            try {
                this.pitSessionRecorder.handleString(str, str2, flag, tick, this.text2, this.text3, json);
            } catch (IOException e) {
                this.handleIOException(e);
            }
        }
    }

    public void handleString(String str, String str2, String str3, String str4, boolean flag) {
        if (!this.text2.equals(str) || !this.text3.equals(str2) || !this.text4.equals(str3)) {
            boolean flag2 = !"unknown".equals(this.text6) && !"unknown".equals(str2) && !this.text6.equals(str2);
            this.sampleTeamOrderHelper.process2();
            if (flag2 || "hub".equals(str3)) {
                this.text5 = null;
            }

            if (flag2 || !this.text4.equals(str3)) {
                this.sampleTeamOrderHelper.process3();
                if (!"unknown".equals(this.text4)) {
                    this.process3();
                }
            }

            String str5 = "context_update";
            if ("hub".equals(str3) && !"hub".equals(this.text4)) {
                str5 = "return_to_hub";
            } else if (flag2) {
                str5 = "lobby_switch";
            } else if ("pit".equals(str3) && !"pit".equals(this.text4)) {
                str5 = "enter_pit";
            }

            String str6 = this.text6;
            if (!"unknown".equals(str2)) {
                this.text6 = str2;
            }

            this.text2 = str;
            this.text3 = str2;
            this.text4 = str3;
            this.handleString("transition", str4, flag, computeJsonObject("type", str5, "previousLobby", str6, "place", this.text4));
        }
    }

    private void handleEntityPlayer(EntityPlayer player, PitDebugger.Entry entry, int n, ItemStack stack, String str) {
        if (n >= 0 && n < entry.itemStackArray.length) {
            if (!entry.flagArray[n] || !ItemStack.areItemStacksEqual(entry.itemStackArray[n], stack)) {
                entry.itemStackArray[n] = stack == null ? null : stack.copy();
                entry.flagArray[n] = true;
                this.handleString(
                    n == 0 ? "held_item" : "armor",
                    str,
                    false,
                    computeJsonObject(
                        "player",
                        this.computeString(player.getUniqueID()),
                        "slot",
                        List.of("hand", "boots", "leggings", "chestplate", "helmet").get(n),
                        "item",
                        this.computeJsonObject(stack)
                    )
                );
            }
        }
    }

    public static long computeLong(double d) {
        return Math.round(d * 1000.0);
    }

    private void handleEntityPlayer2(EntityPlayer player, PitDebugger.Entry entry) {
        if (Double.isFinite(player.posX) && Double.isFinite(player.posY) && Double.isFinite(player.posZ)) {
            long l = computeLong(player.posX);
            long l2 = computeLong(player.posY);
            long l3 = computeLong(player.posZ);
            long l4 = l - entry.timestamp;
            long l5 = l2 - entry.timestamp2;
            long l6 = l3 - entry.timestamp3;
            boolean flag = entry.timestamp4 == Long.MIN_VALUE
                || tick - entry.timestamp4 >= 100L
                || Math.abs(l4) > 32000L
                || Math.abs(l5) > 32000L
                || Math.abs(l6) > 32000L;
            int n = Math.round(player.rotationYaw * 10.0F);
            int n2 = Math.round(player.rotationPitch * 10.0F);
            if (flag || l4 != 0L || l5 != 0L || l6 != 0L || n != entry.yaw || n2 != entry.pitch) {
                this.handleString(
                    "move",
                    "client_entity",
                    false,
                    computeJsonObject(
                        "player",
                        this.computeString(player.getUniqueID()),
                        flag ? "p" : "d",
                        flag ? computeJsonArray(l, l2, l3) : computeJsonArray(l4, l5, l6),
                        "r",
                        computeJsonArray(n, n2)
                    )
                );
                if (flag) {
                    entry.timestamp4 = tick;
                }

                entry.timestamp = l;
                entry.timestamp2 = l2;
                entry.timestamp3 = l3;
                entry.yaw = n;
                entry.pitch = n2;
            }
        }
    }

    public boolean checkState() {
        return this.flag2 && this.pitSessionRecorder != null && this.logObservableActionsSetting.isEnabled() && "pit".equals(this.text4);
    }

    private void handleEntityPlayer3(EntityPlayer player, PitDebugger.Entry entry) {
        Scoreboard scoreboard = this.worldClient.getScoreboard();
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(2);
        String str = objectiveInDisplaySlot == null ? "" : SampleTeamOrderHelper.computeString(objectiveInDisplaySlot.getDisplayName()).trim();
        if ((str.contains("❤") || "health".equalsIgnoreCase(str) || "hp".equalsIgnoreCase(str)) && scoreboard.entityHasObjective(player.getName(), objectiveInDisplaySlot)) {
            int scorePoints = scoreboard.getValueFromObjective(player.getName(), objectiveInDisplaySlot).getScorePoints();
            if (entry.integer == null || entry.integer != scorePoints) {
                this.handleString(
                    "visible_health_score",
                    "below_name_scoreboard",
                    false,
                    computeJsonObject(
                        "player",
                        this.computeString(player.getUniqueID()),
                        "score",
                        scorePoints,
                        "previous",
                        entry.integer,
                        "label",
                        this.sampleTeamOrderHelper.computeString2(str)
                    )
                );
                entry.integer = scorePoints;
            }
        } else {
            entry.integer = null;
        }
    }

    public static void process6() {
        tick++;
        PitDebugger pitDebugger = instance;
        if (pitDebugger != null && pitDebugger.computeLong() >= pitDebugger.pitSessionRecorder.count * 60000L) {
            pitDebugger.handleString("duration_reached", true);
            pitDebugger.handleFlag3(false);
        }
    }

    private void process4() {
        if (this.worldClient != mc.theWorld) {
            boolean flag = this.worldClient != null;
            String str = this.text3;
            this.worldClient = mc.theWorld;
            this.process10();
            if (!this.flag) {
                this.handleString(
                    "transition",
                    "world_changed",
                    false,
                    computeJsonObject("type", this.worldClient == null ? "disconnect" : (flag ? "world_switch" : "session_world"), "previousLobby", str)
                );
            }

            this.flag = false;
            if (this.worldClient != null) {
                this.process();
                if (this.flag2 && mc.getNetHandler() != null) {
                    this.sampleTeamOrderHelper.handleCollection(mc.getNetHandler().getPlayerInfoMap());
                }
            }
        }
    }

    public static JsonObject computeJsonObject(Object... objs) {
        JsonObject json = new JsonObject();

        for (int i = 0; i < objs.length; i += 2) {
            String str = (String)objs[i];
            Object obj = objs[i + 1];
            if (obj instanceof Number number) {
                json.addProperty(str, number);
            } else if (obj instanceof Boolean val) {
                json.addProperty(str, val);
            } else if (obj instanceof String str2) {
                json.addProperty(str, str2);
            } else {
                json.add(str, (JsonElement)obj);
            }
        }

        return json;
    }

    public PitDebugger() {
        super("PitDebugger", 0, Category.PIT);
        this.setRequiresInGame(false);
        this.handleSettingArray(
            new Setting[]{
                this.detailedSetting,
                this.outskirtsSetting,
                this.sessionMinutesSetting,
                this.sampleEveryTicksSetting,
                this.logPlayerMovementSetting,
                this.logChatSetting,
                this.logObservableActionsSetting,
                this.logTabRosterSetting,
                this.includeLocalPlayerSetting
            }
        );
    }

    public static void handleEntityPlayer(EntityPlayer player) {
        if (instance != null && instance.checkState() && instance.matchesEntityPlayer(player)) {
            instance.handleString("melee_swing", "local_swing_animation", false, instance.computeJsonObject(player));
        }
    }

    public JsonObject computeJsonObject(EntityPlayer player) {
        return computeJsonObject("player", this.computeString(player.getUniqueID()));
    }

    public long computeLong() {
        return this.pitSessionRecorder == null ? 0L : this.pitSessionRecorder.computeLong();
    }

    private void handleString(String str, boolean flag) {
        if (this.pitSessionRecorder != null) {
            PitSessionRecorder pitSessionRecorder = this.pitSessionRecorder;
            this.handleString("session_summary", "client", false, pitSessionRecorder.computeJsonObject(str));
            if (this.pitSessionRecorder != null) {
                this.pitSessionRecorder = null;
                instance = null;

                try {
                    pitSessionRecorder.close();
                    if (flag) {
                        MessageUtils.x("PitDebugger saved " + pitSessionRecorder.path.getFileName());
                    }
                } catch (IOException e) {
                    this.handleIOException(e);
                }
            }
        }
    }

    public void handleGameProfile(GameProfile gameProfile) {
        if (gameProfile != null && gameProfile.getId() != null && gameProfile.getName() != null) {
            this.lookupMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), gameProfile.getId());
        }
    }

    public boolean matchesEntityPlayer(EntityPlayer player) {
        return player != mc.thePlayer || this.includeLocalPlayerSetting.isEnabled();
    }

    public EntityPlayer computeEntityPlayer(int n) {
        return (this.worldClient == null ? null : this.worldClient.getEntityByID(n)) instanceof EntityPlayer player && this.matchesEntityPlayer(player)
            ? player
            : null;
    }

    public void handleEntityPlayer(EntityPlayer player, int n, ItemStack stack) {
        this.handleEntityPlayer(player, this.frame(player), n, stack, "S04");
    }

    @Override
    public void onEvent(Event event) {
    }

    public static void handlePacket(Packet<?> packet) {
        if (instance != null) {
            instance.handlePacket2(packet);
        }
    }

    private void handleEntityPlayer(EntityPlayer player, PitDebugger.Entry entry) {
        boolean flag = player.isEating() || player.isUsingItem();
        int n = player.onGround ? 1 : 0;
        int n2 = player.isSprinting() ? 2 : 0;
        n = n2 + (n & ~n2);
        n2 = player.isSneaking() ? 4 : 0;
        int n3 = n2 + (n & ~n2) | (player.isSwingInProgress ? 8 : 0) | (flag ? 16 : 0);
        if (entry.flags != n3) {
            this.handleString(
                "player_state",
                "client_entity",
                false,
                computeJsonObject(
                    "player",
                    this.computeString(player.getUniqueID()),
                    "ground",
                    player.onGround,
                    "sprinting",
                    player.isSprinting(),
                    "sneaking",
                    player.isSneaking(),
                    "swinging",
                    player.isSwingInProgress,
                    "usingItem",
                    flag
                )
            );
            entry.flags = n3;
        }

        ItemStack heldItem = player.getHeldItem();
        this.handleEntityPlayer(player, entry, 0, heldItem, "client_equipment");

        for (int i = 1; i <= 4; i++) {
            this.handleEntityPlayer(player, entry, i, player.getCurrentArmor(i - 1), "client_equipment");
        }

        this.handleEntityPlayer(player, player.getHealth(), "client_entity");
        this.handleEntityPlayer3(player, entry);
        boolean flag2 = flag && heldItem != null && heldItem.getItem() instanceof ItemBow;
        if (flag2 != entry.flag) {
            entry.flag = flag2;
            boolean flag3 = player == mc.thePlayer;
            this.handleString(flag2 ? "bow_draw" : "bow_draw_end", flag3 ? "local_item_use" : "item_use_flag_and_held_bow", !flag3, this.computeJsonObject(player));
        }
    }

    public static void process5() {
        if (instance != null) {
            instance.handleString("client_shutdown", false);
        }
    }

    private void handleIOException(IOException e) {
        PitSessionRecorder pitSessionRecorder = this.pitSessionRecorder;
        this.pitSessionRecorder = null;
        instance = null;
        if (pitSessionRecorder != null) {
            try {
                pitSessionRecorder.close();
            } catch (IOException e2) {
                e.addSuppressed(e2);
            }
        }

        LogManager.getLogger(PitDebugger.class).warn("Pit recording stopped", (Throwable)e);
        MessageUtils.x("PitDebugger write failed. See the client log.");
        this.handleFlag3(false);
    }

    public boolean checkState4() {
        return this.flag2 && this.logTabRosterSetting.isEnabled();
    }

    private void process8() {
        this.process4();
        if (this.pitSessionRecorder != null) {
            long l = this.computeLong();
            if (l - this.timestamp >= 1000L) {
                try {
                    this.pitSessionRecorder.flush();
                    this.timestamp = l;
                } catch (IOException e) {
                    this.handleIOException(e);
                    return;
                }
            }

            if (this.worldClient != null && mc.thePlayer != null && !mc.isGamePaused()) {
                this.sampleTeamOrderHelper.handleGuiScreen(mc.currentScreen);
                this.sampleTeamOrderHelper.sampleTeamOrder();
                if (tick % 20L == 0L) {
                    this.process();
                }

                boolean flag = tick % Math.max(1, (int)this.sampleEveryTicksSetting.computeDouble()) == 0L;
                if (this.pitSessionRecorder != null && flag && mc.getNetHandler() != null) {
                    this.sampleTeamOrderHelper.handleCollection(mc.getNetHandler().getPlayerInfoMap(), this.text5);
                }

                if (this.flag2 && "pit".equals(this.text4)) {
                    boolean flag2 = this.logPlayerMovementSetting.isEnabled() && flag;

                    for (EntityPlayer playerEntity : this.worldClient.playerEntities) {
                        if (this.pitSessionRecorder == null) {
                            return;
                        }

                        if (this.matchesEntityPlayer(playerEntity)) {
                            this.handleGameProfile(playerEntity.getGameProfile());
                            PitDebugger.Entry entry = this.frame(playerEntity);
                            if (this.logObservableActionsSetting.isEnabled()) {
                                this.handleEntityPlayer(playerEntity, entry);
                                this.trackHelper.handleEntityPlayer(playerEntity);
                            }

                            if (flag2) {
                                this.handleEntityPlayer2(playerEntity, entry);
                            }
                        }
                    }

                    this.lookupMap2.values().removeIf(entry2 -> this.worldClient.getEntityByID(entry2.entityPlayer.getEntityId()) != entry2.entityPlayer);
                    if (this.checkState()) {
                        this.trackHelper.process();
                        this.arrowSpawnHelper.process();
                    }
                }
            }
        }
    }

    public static final class Entry {
        public final EntityPlayer entityPlayer;
        public final ItemStack[] itemStackArray = new ItemStack[5];
        public final boolean[] flagArray = new boolean[5];
        public long timestamp;
        public long timestamp2;
        public long timestamp3;
        public long timestamp4;
        public int yaw;
        public int pitch;
        public int flags;
        public float health;
        public Integer integer;
        public boolean flag;
        private static final long timestamp5 = Long.MIN_VALUE;

        public Entry(EntityPlayer entityPlayer) {
            this.timestamp4 = timestamp5;
            this.flags = -1;
            this.health = Float.NaN;
            this.entityPlayer = entityPlayer;
        }
    }
}
