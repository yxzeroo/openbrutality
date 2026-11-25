package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings.GameType;
import org.brutality.utils.hypixel.pit.DOUBLEHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.render.RenderHelper3;

public final class SampleTeamOrderHelper {
    private static final Gson gson = new Gson();
    private static final Pattern pattern = Pattern.compile("(?i)\\b([0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}|[0-9a-f]{32})\\b|[A-Za-z0-9_]+");
    private static final Pattern pattern2 = Pattern.compile("^(?:\\[[^\\]\\r\\n]+\\]\\s*)*([A-Za-z0-9_]{1,16}):\\s");
    private static final Pattern pattern3 = Pattern.compile(
        "(?i)^(?:\\[(?:party|guild|officer|coop|staff|private)\\]|(?:party|guild|officer|co-op|from|to)\\s*[>»])"
    );
    private static final Pattern pattern4 = Pattern.compile("(?i)\\b(?:mini|mega|lobby|pit|hub)[0-9]+[a-z0-9-]*\\b");
    private static final Pattern pattern5 = Pattern.compile("(?i)\\bLobby\\s*#?\\s*(\\d+)\\b");
    private static final Pattern pattern6 = Pattern.compile(
        "(?i)\\b(?:players?(?: online| in (?:this )?lobby)?|online(?: players?)?)\\s*:\\s*([0-9][0-9,]{0,11})(?:\\s*/\\s*([0-9][0-9,]{0,11}))?"
    );
    private static final Pattern pattern7 = Pattern.compile("(?i)\\b([0-9][0-9,]{0,11})(?:\\s*/\\s*([0-9][0-9,]{0,11}))?\\s+players?\\b");
    private static final Pattern pattern8 = Pattern.compile(
        "(?i)\\b(?:lobby|server)\\b.*\\b(?:selector|select|switcher|switch|browser|list)\\b|\\b(?:select|switch|choose)\\b.*\\b(?:lobby|server)\\b|^lobbies$"
    );
    private final PitDebugger pitDebugger;
    private final Set<UUID> itemSet = new HashSet<>();
    private Map<UUID, SampleTeamOrderHelper.CSEntry> lookupMap;
    private final Map<String, String> lookupMap2 = new HashMap<>();
    private boolean flag = true;
    private JsonArray jsonArray;
    private JsonArray jsonArray2;
    private JsonObject jsonObject;
    private JsonObject jsonObject2;
    private GuiChest guiChest;
    private long timestamp = -1L;
    private long timestamp2 = -1L;
    private long timestamp3 = -1L;
    private int count;
    public long timestamp4 = -1L;

    private UUID computeUUID(String str) {
        return this.pitDebugger.lookupMap.get(str.toLowerCase(Locale.ROOT));
    }

    public void process3() {
        this.jsonArray2 = this.jsonArray = null;
        this.flag = true;
        this.jsonObject = this.jsonObject2 = null;
        this.timestamp = this.timestamp2 = this.timestamp3 = -1L;
    }

    public void handleS02PacketChat(S02PacketChat packet) {
        IChatComponent chatComponent = packet.getChatComponent();
        if (chatComponent != null && packet.getType() != 2) {
            String str = computeString(chatComponent.getUnformattedText()).trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                this.handleString(str);
            } else if (!pattern3.matcher(str).find()) {
                Matcher matcher = pattern2.matcher(str);
                if (matcher.find()) {
                    UUID uuid = this.computeUUID(matcher.group(1));
                    if (uuid != null && packet.getType() == 0 && this.pitDebugger.checkState2()) {
                        long l = this.pitDebugger.computeLong();
                        this.pitDebugger
                            .handleString(
                                "public_chat",
                                "S02_public_format",
                                true,
                                PitDebugger.computeJsonObject(
                                    "player",
                                    this.pitDebugger.computeString(uuid),
                                    "formatted",
                                    this.computeString2(chatComponent.getFormattedText()),
                                    "sincePreviousMs",
                                    this.timestamp4 < 0L ? null : l - this.timestamp4
                                )
                            );
                        this.timestamp4 = l;
                    }
                } else if (this.pitDebugger.checkState()
                    && this.pitDebugger.computeEntityPlayer() != null
                    && this.pitDebugger.matchesEntityPlayer(this.pitDebugger.computeEntityPlayer())) {
                    String str2 = DOUBLEHelper.computeString2(str);
                    if (str2 != null) {
                        this.pitDebugger
                            .handleString(
                                "kill",
                                "pit_reward_message",
                                true,
                                PitDebugger.computeJsonObject(
                                    "killer",
                                    this.pitDebugger.computeString(this.pitDebugger.computeEntityPlayer().getUniqueID()),
                                    "victim",
                                    this.pitDebugger.computeString(this.computeUUID(str2))
                                )
                            );
                    }
                }
            }
        }
    }

    private static int computeInt(String str, StringBuilder sb, int[] ints, int n, int n2, int n3, String str2) {
        int n4 = ints[n2];
        int n5 = ints[n3 - 1] + 1;
        sb.append(str, n, n4).append('<').append(str2).append('>');

        for (int i = n4; i + 1 < n5; i++) {
            if (str.charAt(i) == 167) {
                sb.append(str.charAt(i)).append(str.charAt(++i));
            }
        }

        return n5;
    }

    public void handleS3EPacketTeams(S3EPacketTeams packet) {
        this.flag = true;
        JsonArray jsonArray = new JsonArray();
        int n = 0;

        for (String player : packet.getPlayers()) {
            UUID uuid = this.computeUUID(player);
            if (uuid == null) {
                n++;
            } else {
                jsonArray.add(this.computeJsonElement(uuid));
            }
        }

        this.pitDebugger
            .handleString(
                "team_packet",
                "S3E",
                false,
                PitDebugger.computeJsonObject(
                    "sortKey", this.computeString3(packet.getName()), "action", packet.getAction(), "members", jsonArray, "unresolvedMembers", n
                )
            );
    }

    public void sampleTeamOrder() {
        if (this.flag && this.pitDebugger.checkState4()) {
            this.flag = false;
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(new JsonPrimitive(""));
            this.pitDebugger
                .worldClient
                .getScoreboard()
                .getTeams()
                .stream()
                .map(ScorePlayerTeam::getRegisteredName)
                .filter(str -> !str.isEmpty())
                .sorted()
                .map(this::computeString3)
                .forEach(str2 -> jsonArray.add(new JsonPrimitive(str2)));
            if (!jsonArray.equals(this.jsonArray)) {
                this.jsonArray = jsonArray;
                this.pitDebugger.handleString("team_sort_order", "client_scoreboard", false, PitDebugger.computeJsonObject("keys", jsonArray));
            }
        }
    }

    public void process2() {
        this.lookupMap = null;
    }

    private JsonObject computeJsonObject(NetworkPlayerInfo playerInfo) {
        ScorePlayerTeam playerTeam = playerInfo.getPlayerTeam();
        return PitDebugger.computeJsonObject(
            "player",
            this.pitDebugger.computeString(playerInfo.getGameProfile().getId()),
            "sortKey",
            this.computeString3(playerTeam == null ? "" : playerTeam.getRegisteredName()),
            "spectator",
            playerInfo.getGameType() == GameType.SPECTATOR
        );
    }

    public void process() {
        this.itemSet.clear();
        this.lookupMap2.clear();
        this.guiChest = null;
        this.process2();
        this.process3();
    }

    private JsonElement computeJsonElement(UUID uuid) {
        String str = this.pitDebugger.computeString(uuid);
        return (JsonElement)(str == null ? JsonNull.INSTANCE : new JsonPrimitive(str));
    }

    public void handleCollection(Collection<NetworkPlayerInfo> playerInfos, String str) {
        LinkedHashMap<UUID, SampleTeamOrderHelper.CSEntry> map = new LinkedHashMap<>();

        for (NetworkPlayerInfo playerInfo : playerInfos) {
            UUID id = playerInfo.getGameProfile().getId();
            if (id != null) {
                map.put(id, new SampleTeamOrderHelper.CSEntry("unknown", true, false));
            }
        }

        for (EntityPlayer playerEntity : this.pitDebugger.worldClient.playerEntities) {
            UUID uniqueID = playerEntity.getUniqueID();
            String str2 = "unknown";
            if (str != null && Double.isFinite(playerEntity.posX) && Double.isFinite(playerEntity.posY) && Double.isFinite(playerEntity.posZ)) {
                str2 = computeString(FreshHelper.computeString(str, playerEntity));
            }

            map.put(uniqueID, new SampleTeamOrderHelper.CSEntry(str2, map.containsKey(uniqueID), true));
        }

        boolean flag = this.lookupMap == null;
        JsonArray jsonArray = new JsonArray();
        map.forEach(
            (uuid, csEntry) -> {
                if (flag || !csEntry.equals(this.lookupMap.get(uuid))) {
                    jsonArray.add(
                        PitDebugger.computeJsonObject(
                            "player", this.pitDebugger.computeString(uuid), "mapLocation", csEntry.cS(), "inTab", csEntry.fQ(), "tracked", csEntry.fR()
                        )
                    );
                }
            }
        );
        JsonArray jsonArray2 = new JsonArray();
        if (!flag) {
            for (UUID uuid2 : this.lookupMap.keySet()) {
                if (!map.containsKey(uuid2)) {
                    jsonArray2.add(this.computeJsonElement(uuid2));
                }
            }
        }

        this.lookupMap = map;
        if (flag || jsonArray.size() > 0 || jsonArray2.size() > 0) {
            this.pitDebugger
                .handleString(
                    "lobby_locations",
                    "client_tab_entities_and_pitutils",
                    true,
                    PitDebugger.computeJsonObject("full", flag, "players", jsonArray, "removed", jsonArray2, "count", map.size())
                );
        }
    }

    private String computeString3(String str) {
        return !str.isEmpty() && this.pitDebugger.pitSessionRecorder != null
            ? this.lookupMap2.computeIfAbsent(str, str2 -> this.pitDebugger.pitSessionRecorder.computeString("team:" + str2))
            : "";
    }

    private String computeString(IChatComponent component) {
        return component == null ? null : this.computeString2(component.getFormattedText());
    }

    public void handleScoreObjective(ScoreObjective objective, Collection<Score> scores) {
        if (this.timestamp2 != PitDebugger.tick) {
            this.timestamp2 = PitDebugger.tick;
            String str = computeString(objective.getDisplayName()).trim();
            boolean flag = "THE PIT".equalsIgnoreCase(str);
            String str2 = flag ? "pit" : this.pitDebugger.text4;
            String str3 = this.pitDebugger.text3;
            JsonArray jsonArray = new JsonArray();

            for (Score score : scores) {
                String playerName = score.getPlayerName();
                String str4 = computeString(ScorePlayerTeam.formatPlayerName(objective.getScoreboard().getPlayersTeam(playerName), playerName)).trim();
                this.handleJsonArray(jsonArray, str4, null);
                Matcher matcher = pattern4.matcher(str4);
                Matcher matcher2 = pattern5.matcher(str4);
                if (matcher.find()) {
                    str3 = matcher.group().toLowerCase(Locale.ROOT);
                    if (!flag && (str3.startsWith("lobby") || str3.startsWith("hub"))) {
                        str2 = "hub";
                    }
                } else if (matcher2.find()) {
                    str3 = "lobby" + matcher2.group(1);
                    if (!flag) {
                        str2 = "hub";
                    }
                }
            }

            this.pitDebugger.handleString("hub".equals(str2) ? "unknown" : this.pitDebugger.text2, str3, str2, "sidebar", true);
            if (this.pitDebugger.checkState4()) {
                JsonObject json = PitDebugger.computeJsonObject("title", this.computeString2(str), "counts", jsonArray);
                if (!json.equals(this.jsonObject)) {
                    this.jsonObject = RenderHelper3.computeJsonElement(json).getAsJsonObject();
                    this.pitDebugger.handleString("sidebar", "sidebar_render", false, json);
                }
            }
        }
    }

    public SampleTeamOrderHelper(PitDebugger pitDebugger) {
        this.pitDebugger = pitDebugger;
    }

    public void handleS38PacketPlayerListItem(S38PacketPlayerListItem packet) {
        for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packet.getEntries()) {
            this.pitDebugger.handleGameProfile(addPlayerData.getProfile());
        }

        JsonArray jsonArray = new JsonArray();

        for (S38PacketPlayerListItem.AddPlayerData addPlayerData2 : packet.getEntries()) {
            if (addPlayerData2.getProfile() != null && addPlayerData2.getProfile().getId() != null) {
                UUID id = addPlayerData2.getProfile().getId();
                if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                    this.itemSet.add(id);
                }

                if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                    this.itemSet.remove(id);
                }

                if (this.pitDebugger.checkState4()) {
                    JsonObject json = PitDebugger.computeJsonObject("player", this.pitDebugger.computeString(id));
                    switch (packet.getAction()) {
                        case ADD_PLAYER:
                            json.addProperty("pingMs", addPlayerData2.getPing());
                            json.addProperty("gameMode", addPlayerData2.getGameMode() == null ? null : addPlayerData2.getGameMode().name());
                            json.addProperty("display", this.computeString(addPlayerData2.getDisplayName()));
                            break;
                        case UPDATE_LATENCY:
                            json.addProperty("pingMs", addPlayerData2.getPing());
                            break;
                        case UPDATE_GAME_MODE:
                            json.addProperty("gameMode", addPlayerData2.getGameMode() == null ? null : addPlayerData2.getGameMode().name());
                            break;
                        case UPDATE_DISPLAY_NAME:
                            json.addProperty("display", this.computeString(addPlayerData2.getDisplayName()));
                        case REMOVE_PLAYER:
                    }

                    jsonArray.add(json);
                }
            }
        }

        if (this.pitDebugger.checkState4()) {
            this.pitDebugger
                .handleString(
                    "tab_packet",
                    "S38",
                    false,
                    PitDebugger.computeJsonObject("action", packet.getAction().name(), "entries", jsonArray, "clientRosterCount", this.itemSet.size())
                );
        }
    }

    public static String computeString(String str) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(str);
    }

    public void handleIChatComponent(IChatComponent component) {
        if (component != null) {
            JsonArray jsonArray = new JsonArray();

            for (String str : computeString(component.getUnformattedText()).split("\\R")) {
                this.handleJsonArray(jsonArray, str, null);
            }

            if (jsonArray.size() > 0) {
                this.pitDebugger.handleString("tab_footer_counts", "S47", false, PitDebugger.computeJsonObject("counts", jsonArray));
            }
        }
    }

    public void handleCollection(Collection<NetworkPlayerInfo> playerInfos) {
        JsonArray jsonArray = new JsonArray();

        for (NetworkPlayerInfo playerInfo : playerInfos) {
            GameProfile gameProfile = playerInfo.getGameProfile();
            this.pitDebugger.handleGameProfile(gameProfile);
            if (gameProfile.getId() != null) {
                this.itemSet.add(gameProfile.getId());
                if (this.pitDebugger.checkState4()) {
                    jsonArray.add(this.computeJsonObject(playerInfo));
                }
            }
        }

        if (this.pitDebugger.checkState4()) {
            this.pitDebugger.handleString("tab_baseline", "client_roster", false, PitDebugger.computeJsonObject("entries", jsonArray, "count", jsonArray.size()));
        }
    }

    public void handleList(List<NetworkPlayerInfo> playerInfos, int count) {
        if (this.timestamp != PitDebugger.tick) {
            this.timestamp = PitDebugger.tick;
            JsonArray jsonArray = new JsonArray();

            for (NetworkPlayerInfo playerInfo : playerInfos) {
                this.pitDebugger.handleGameProfile(playerInfo.getGameProfile());
                jsonArray.add(this.computeJsonObject(playerInfo));
            }

            if (!jsonArray.equals(this.jsonArray2) || count != this.count) {
                this.jsonArray2 = jsonArray;
                this.count = count;
                this.pitDebugger.handleString("tab_render", "tab_overlay", false, PitDebugger.computeJsonObject("order", jsonArray, "clientRosterCount", count));
            }
        }
    }

    private void handleJsonArray(JsonArray jsonArray, String str, JsonObject json) {
        Matcher matcher = pattern6.matcher(str);
        if (!matcher.find()) {
            matcher = pattern7.matcher(str);
            if (!matcher.find()) {
                return;
            }
        }

        JsonObject json2 = json == null ? new JsonObject() : RenderHelper3.computeJsonElement(json).getAsJsonObject();
        json2.addProperty("label", this.computeString2(str.trim()));
        json2.addProperty("players", Long.parseLong(matcher.group(1).replace(",", "")));
        if (matcher.group(2) != null) {
            json2.addProperty("capacity", Long.parseLong(matcher.group(2).replace(",", "")));
        }

        jsonArray.add(json2);
    }

    public void handleGuiScreen(GuiScreen screen) {
        if (screen != this.guiChest) {
            this.guiChest = null;
            this.jsonObject2 = null;
        }
    }

    public String computeString2(String str) {
        StringBuilder sb = new StringBuilder();
        int[] ints = new int[str.length()];

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 167 && i + 1 < str.length()) {
                i++;
            } else {
                ints[sb.length()] = i;
                sb.append(str.charAt(i));
            }
        }

        Matcher matcher = pattern.matcher(sb);
        StringBuilder sb2 = new StringBuilder(str.length());
        int n = 0;

        while (matcher.find()) {
            UUID uuid = this.computeUUID(matcher.group());
            String str2 = matcher.group(1) != null ? "uuid" : this.pitDebugger.computeString(uuid);
            if (str2 != null) {
                n = computeInt(str, sb2, ints, n, matcher.start(), matcher.end(), str2);
            }
        }

        return sb2.append(str, n, str.length()).toString();
    }

    public void handleGuiChest(GuiChest guiChest, IInventory iInventory) {
        String str = computeString(iInventory.getDisplayName().getUnformattedText()).trim();
        if (this.timestamp3 != PitDebugger.tick && pattern8.matcher(str).find() && this.pitDebugger.computeEntityPlayer() != null) {
            this.timestamp3 = PitDebugger.tick;
            if (this.guiChest != guiChest) {
                this.jsonObject2 = null;
            }

            this.guiChest = guiChest;
            JsonArray jsonArray = new JsonArray();
            JsonArray jsonArray2 = new JsonArray();

            for (int i = 0; i < iInventory.getSizeInventory(); i++) {
                ItemStack stack = iInventory.getStackInSlot(i);
                if (stack != null) {
                    JsonObject json = PitDebugger.computeJsonObject(
                        "slot", i, "entry", this.computeString2(stack.getDisplayName()), "stackCount", stack.stackSize
                    );
                    if (stack.stackSize > 1) {
                        jsonArray2.add(json);
                    }

                    for (String str2 : stack.getTooltip(this.pitDebugger.computeEntityPlayer(), false)) {
                        this.handleJsonArray(jsonArray, computeString(str2), json);
                    }
                }
            }

            JsonObject json2 = PitDebugger.computeJsonObject("title", this.computeString2(str), "counts", jsonArray, "stackLabels", jsonArray2);
            if (!json2.equals(this.jsonObject2)) {
                this.jsonObject2 = RenderHelper3.computeJsonElement(json2).getAsJsonObject();
                this.pitDebugger.handleString("lobby_menu", "open_menu_tooltip_data", false, json2);
            }
        }
    }

    private void handleString(String str) {
        try {
            JsonObject json = gson.fromJson(str, JsonObject.class);
            if (json == null || !json.has("server") || !json.has("gametype")) {
                return;
            }

            String str2 = json.get("server").getAsString().toLowerCase(Locale.ROOT);
            if (!pattern4.matcher(str2).matches()) {
                return;
            }

            String asString = json.get("gametype").getAsString();
            String str3 = json.has("mode") ? json.get("mode").getAsString() : "";
            String str4 = "LOBBY".equalsIgnoreCase(str3) || str2.startsWith("lobby") || str2.startsWith("hub")
                ? "hub"
                : (!"PIT".equalsIgnoreCase(asString) && !"PIT".equalsIgnoreCase(str3) ? "unknown" : "pit");
            String str5 = json.has("map") ? json.get("map").getAsString().toLowerCase(Locale.ROOT) : this.pitDebugger.text2;
            if (!str5.matches("[a-z0-9 _-]{1,40}") || "hub".equals(str4)) {
                str5 = "unknown";
            }

            this.pitDebugger.handleString(str5, str2, str4, "location_chat", true);
        } catch (UnsupportedOperationException | IllegalStateException | JsonParseException ignored) {
        }
    }

    public record CSEntry(String cS, boolean fQ, boolean fR) {
    }
}
