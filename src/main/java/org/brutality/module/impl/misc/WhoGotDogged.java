package org.brutality.module.impl.misc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.gui.ChangelogScreen;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.combat.ObserveHelper;
import org.brutality.utils.hypixel.LoadAvatarHelper;
import org.brutality.utils.hypixel.RequestHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class WhoGotDogged extends Module {
    private final ObserveHelper observeHelper = new ObserveHelper();
    private WorldClient worldClient;
    private NetHandlerPlayClient netHandlerPlayClient;
    private CompletableFuture<RequestHelper.Entry> completableFuture;
    private RequestHelper.Entry entry;
    private int count;
    private long timestamp;
    private final BooleanSetting autoLeaveSetting = new BooleanSetting("Auto Leave", this, true);
    private final BooleanSetting leaveOnlyIfInSpawnSetting = new BooleanSetting("Leave Only If In Spawn", this, true);
    private final BooleanSetting spawnBeforeLeaveSetting = new BooleanSetting("Spawn Before Leave", this, true);
    private final BooleanSetting banTypeStatsSetting = new BooleanSetting("Ban Type Stats", this, true);
    private final BooleanSetting autoCopyNameSetting = new BooleanSetting("Auto Copy Name", this, true);
    private final NumberSetting maximumNotificationsSetting = new NumberSetting("Maximum Notifications", this, 10.0, 1.0, 10.0, 1.0);
    private final BooleanSetting minimumPrestigeFilterSetting = new BooleanSetting("Minimum Prestige Filter", this, false);
    private final NumberSetting minimumPrestigeSetting = new NumberSetting("Minimum Prestige", this, 1.0, 1.0, 50.0, 1.0)
        .computeNumberSetting(() -> this.minimumPrestigeFilterSetting.isEnabled());
    private final ModeSetting bedwarsSetting = new ModeSetting("Exit Mode", this, "Disconnect", Arrays.asList("Disconnect", "Hub", "Lobby", "Bedwars"));
    private boolean flag = false;
    private static final long timestamp2 = 60000L;

    @Override
    public void onEnable() {
        super.onEnable();
        this.count++;
        this.process5();
        this.process4();
    }

    private void handleString2(String str) {
        MessageUtils.x(
            MessageUtils.computeString(str, MessageUtils.text7)
                + " left around the ban message (possible ban)."
                + (this.banTypeStatsSetting.isEnabled() ? " Loading ban statistics..." : "")
        );
        LoadAvatarHelper.handleString(str);
        if (this.autoCopyNameSetting.isEnabled() && !this.flag) {
            this.handleString(str);
            this.flag = true;
            MessageUtils.x("Copied " + MessageUtils.computeString(str, MessageUtils.text7) + " to clipboard.");
        }
    }

    private void process5() {
        this.observeHelper.handleMap(Collections.emptyMap());
        this.worldClient = null;
        this.netHandlerPlayClient = null;
        this.completableFuture = null;
        this.entry = null;
        this.flag = false;
        this.timestamp = 0L;
    }

    private void process6() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (this.autoLeaveSetting.isEnabled()) {
                boolean flag = FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer);
                if (this.leaveOnlyIfInSpawnSetting.isEnabled() && !flag) {
                    return;
                }

                if (this.spawnBeforeLeaveSetting.isEnabled() && !flag) {
                    if (mc.thePlayer != null) {
                        mc.thePlayer.sendChatMessage("/spawn");
                    }

                    return;
                }

                this.process3();
            }
        }
    }

    private void handleString(String str) {
        try {
            StringSelection stringSelection = new StringSelection(str);
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(stringSelection, null);
        } catch (Exception e) {
            MessageUtils.x(MessageUtils.computeString("Failed to copy to clipboard.", MessageUtils.text13));
        }
    }

    private void handleList(List<String> list, NetHandlerPlayClient netHandlerPlayClient) {
        if (this.banTypeStatsSetting.isEnabled() && this.completableFuture != null) {
            int count = this.count;
            RequestHelper.Entry entry = this.entry;
            String str = list.size() == 1 ? (String)list.get(0) : list.size() + " possible banned players";
            this.completableFuture
                .whenComplete(
                    (entry2, t) -> mc.addScheduledTask(
                        () -> {
                            if (this.checkState44()
                                && this.banTypeStatsSetting.isEnabled()
                                && count == this.count
                                && mc.thePlayer != null
                                && mc.getNetHandler() == netHandlerPlayClient) {
                                MessageUtils.x(
                                    str
                                        + " - ban type"
                                        + (list.size() == 1 ? "" : "s")
                                        + " unconfirmed. "
                                        + (t != null ? "Ban statistics unavailable (request failed)." : RequestHelper.computeString(entry, entry2))
                                );
                            }
                        }
                    )
                );
        }
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (this.checkState44() && RuntimeAuthGuard.checkState2()) {
        if (this.checkState44() && true) {
            this.process4();
            if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                if (mc.thePlayer == null) {
                    return;
                }

                this.process();
                if (this.banTypeStatsSetting.isEnabled()
                    && mc.theWorld != null
                    && mc.getNetHandler() != null
                    && System.currentTimeMillis() - this.timestamp >= timestamp2) {
                    this.timestamp = System.currentTimeMillis();
                    RequestHelper.request();
                }
            }

            if (event instanceof ChatEvent) {
                String text = ((ChatEvent)event).message.getUnformattedText();
                if (text.contains("A player has been removed from your game.")) {
                    this.process();
                    if (mc.thePlayer == null || mc.theWorld == null || mc.getNetHandler() == null) {
                        return;
                    }

                    boolean flag = !this.observeHelper.active();
                    this.observeHelper.handleLong(System.currentTimeMillis());
                    if (flag) {
                        this.flag = false;
                        this.entry = RequestHelper.getEntry();
                        this.completableFuture = this.banTypeStatsSetting.isEnabled() ? RequestHelper.request() : null;
                        MessageUtils.x(
                            "Ban detected - checking recent departures"
                                + (this.banTypeStatsSetting.isEnabled() ? " and loading staff/Watchdog statistics..." : "...")
                        );
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.count++;
        this.process5();
        super.onDisable();
    }

    private void process4() {
        LoadAvatarHelper.handleInt(
            (int)this.maximumNotificationsSetting.computeDouble(),
            this.minimumPrestigeFilterSetting.isEnabled(),
            (int)this.minimumPrestigeSetting.computeDouble()
        );
    }

    @Override
    public void process2() {
        this.process5();
    }

    private void process() {
        WorldClient world = mc.theWorld;
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        var map = this.computeMap();
        if (world != this.worldClient || netHandler != this.netHandlerPlayClient) {
            this.observeHelper.handleMap(map);
            this.worldClient = world;
            this.netHandlerPlayClient = netHandler;
            this.completableFuture = null;
            this.entry = null;
        } else if (world != null && netHandler != null) {
            long now = System.currentTimeMillis();
            boolean flag = this.observeHelper.active();
            this.observeHelper.observe(map, now);
            if (flag && !this.observeHelper.active()) {
                MessageUtils.x("Ban matching cancelled: the tab list was reset.");
                this.completableFuture = null;
                this.entry = null;
            } else if (this.observeHelper.matchesLong(now)) {
                var list = this.observeHelper.computeList();
                if (mc.thePlayer != null) {
                    list.remove(mc.thePlayer.getName());
                }

                if (list.isEmpty()) {
                    MessageUtils.x("Can't match the ban message to a recent departure - the player may have been limboed.");
                } else {
                    this.handleList(list, netHandler);

                    for (String str : list) {
                        this.handleString2(str);
                    }

                    this.process6();
                }
            }
        } else {
            this.observeHelper.handleMap(Collections.emptyMap());
        }
    }

    public WhoGotDogged() {
        super("WhoGotDogged", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(
            new Setting[]{
                this.autoLeaveSetting,
                this.leaveOnlyIfInSpawnSetting,
                this.spawnBeforeLeaveSetting,
                this.autoCopyNameSetting,
                this.bedwarsSetting,
                this.maximumNotificationsSetting,
                this.minimumPrestigeFilterSetting,
                this.minimumPrestigeSetting,
                this.banTypeStatsSetting
            }
        );
    }

    private Map<UUID, String> computeMap() {
        HashMap map = new HashMap();
        if (mc.getNetHandler() != null) {
            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                if (playerInfo.getGameProfile().getId() != null && playerInfo.getGameProfile().getName() != null) {
                    map.put(playerInfo.getGameProfile().getId(), playerInfo.getGameProfile().getName());
                }
            }
        }

        return map;
    }

    private void process3() {
        if (mc.thePlayer != null) {
            String text = this.bedwarsSetting.getText();
            String str = text.toLowerCase();
            switch (str) {
                case "hub":
                    MessageUtils.x("Going to hub...");
                    mc.thePlayer.sendChatMessage("/hub");
                    break;
                case "lobby":
                    MessageUtils.x("Going to lobby...");
                    mc.thePlayer.sendChatMessage("/lobby");
                    break;
                case "bedwars":
                    MessageUtils.x("Joining Bedwars...");
                    mc.thePlayer.sendChatMessage("/bedwars_eight_one");
                    break;
                case "disconnect":
                default:
                    mc.displayGuiScreen(new ChangelogScreen());
            }
        }
    }
}
