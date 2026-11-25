package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;

public class DeadLobbyFinder extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static final long timestamp = 7500L;
    private static final long timestamp2 = 3000L;
    private static final long timestamp3 = 5000L;
    private volatile boolean flag = false;
    private volatile boolean flag2 = false;
    private final NumberSetting playersSetting = new NumberSetting("Players", this, 10.0, 1.0, 15.0, 1.0);
    private final ModeSetting exactlySetting = new ModeSetting(
        "Mode", this, "Less Than or Equal", Arrays.asList("Less Than or Equal", "Greater Than or Equal", "Exactly")
    );

    private void process3() {
        if (!this.flag) {
            this.flag = true;
            this.flag2 = false;
            mc.thePlayer.sendChatMessage("/p leave");
            if (FreshHelper.flag) {
                mc.thePlayer.sendChatMessage("/housing random");
                scheduledExecutorService.schedule(this::process5, 7500L, TimeUnit.MILLISECONDS);
            } else {
                this.process5();
            }
        } else {
            MessageUtils.x("Lobby search is already running.");
        }
    }

    public DeadLobbyFinder() {
        super("DeadLobbyFinder", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.playersSetting, this.exactlySetting});
    }

    private void process4() {
        this.flag = false;
        if (!this.flag2) {
            MessageUtils.x("Lobby search stopped.");
        }
    }

    private int computeInt() {
        if (mc.theWorld == null) {
            return 0;
        }
        int n = 0;

        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player != null && player.getName() != null) {
                n++;
            }
        }

        return n;
    }

    public void process() {
        int n = this.computeInt();
        MessageUtils.x("Current lobby has " + n + " players.");
    }

    private void process5() {
        if (this.flag) {
            scheduledExecutorService.schedule(() -> {
                if (this.flag) {
                    if (this.checkState()) {
                        mc.thePlayer.sendChatMessage("/play pit");
                        MessageUtils.x("Joining Pit lobby...");
                        scheduledExecutorService.schedule(() -> {
                            if (this.flag) {
                                int n = this.computeInt();
                                int n2 = (int)this.playersSetting.computeDouble();
                                String text = this.exactlySetting.getText();
                                MessageUtils.x("Current lobby has " + n + " players");
                                boolean flag = false;
                                switch (text) {
                                    case "Less Than or Equal":
                                        flag = n <= n2;
                                        break;
                                    case "Greater Than or Equal":
                                        flag = n >= n2;
                                        break;
                                    case "Exactly":
                                        flag = n == n2;
                                }

                                if (flag) {
                                    this.flag2 = true;
                                    MessageUtils.x("§aFound lobby with " + n + " players!");
                                    MessageUtils.x("§aStopping lobby finder - suitable lobby located!");
                                    this.process28();
                                } else {
                                    if (!this.checkState()) {
                                        return;
                                    }

                                    MessageUtils.x("Lobby doesn't match criteria, cycling...");
                                    mc.thePlayer.sendChatMessage("/lobby");
                                    scheduledExecutorService.schedule(() -> {
                                        if (this.flag) {
                                            this.process5();
                                        }
                                    }, 5000L, TimeUnit.MILLISECONDS);
                                }
                            }
                        }, 3000L, TimeUnit.MILLISECONDS);
                    }
                }
            }, 1000L, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onEnable() {
        if (this.checkState()) {
            MessageUtils.x(
                "Starting lobby search for lobbies with " + this.exactlySetting.getText() + " " + (int)this.playersSetting.computeDouble() + " players"
            );
            this.process3();
        }
    }

    @Override
    public void onDisable() {
        this.process4();
    }

    private boolean checkState() {
        if (mc.theWorld == null || mc.thePlayer == null || FreshHelper.flag && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
            if (mc.thePlayer != null) {
                MessageUtils.x("Lobby search requires Pit spawn or a hub.");
            }

            this.handleFlag3(false);
            return false;
        }
        return true;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }
}
