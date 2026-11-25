package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Arrays;
import java.util.Random;
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
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;

public class LobbyStalker extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static final long timestamp = 7500L;
    private static final long timestamp2 = 7500L;
    private static final long timestamp3 = 3000L;
    private static final long timestamp4 = 5000L;
    private volatile boolean flag = false;
    private volatile boolean flag2 = false;
    private static final Random random = new Random();
    private final StringSetting targetSetting = new StringSetting("Target", this, "");
    private final ModeSetting arcadeSetting = new ModeSetting(
        "Game Mode", this, "Random", Arrays.asList("Random", "Pit", "Bedwars", "Skywars", "UHC", "Duels", "Murder Mystery", "Arcade")
    );
    private final ModeSetting squadSetting = new ModeSetting("Bedwars Mode", this, "Solo", Arrays.asList("Solo", "Duo", "Trio", "Squad"));
    private final ModeSetting megaSetting = new ModeSetting("Skywars Mode", this, "Solo", Arrays.asList("Solo", "Duo", "Trio", "Mega"));
    private final ModeSetting trioSetting = new ModeSetting("UHC Mode", this, "Solo", Arrays.asList("Solo", "Duo", "Trio"));
    private final ModeSetting boxingSetting = new ModeSetting("Duels Mode", this, "Classic", Arrays.asList("Classic", "Bow", "Potion", "UHC", "Sumo", "Boxing"));

    private void process5() {
        scheduledExecutorService.schedule(() -> {
            if (this.flag && this.checkState44()) {
                if (this.checkState()) {
                    mc.thePlayer.sendChatMessage("/play pit");
                    MessageUtils.x("Joining Pit...");
                    scheduledExecutorService.schedule(() -> {
                        if (this.flag && this.checkState44()) {
                            if (this.checkState2()) {
                                this.flag2 = true;
                                MessageUtils.x("§aTarget found: " + this.targetSetting.getValue() + " is in Pit!");
                                MessageUtils.x("§aStopping lobby stalker - target located!");
                                this.process28();
                            } else {
                                if (!this.checkState()) {
                                    return;
                                }

                                MessageUtils.x("Target not in Pit, cycling...");
                                mc.thePlayer.sendChatMessage("/lobby");
                                scheduledExecutorService.schedule(() -> {
                                    if (this.flag && this.checkState44()) {
                                        this.process5();
                                    }
                                }, 5000L, TimeUnit.MILLISECONDS);
                            }
                        }
                    }, 5000L, TimeUnit.MILLISECONDS);
                }
            }
        }, 1000L, TimeUnit.MILLISECONDS);
    }

    public LobbyStalker() {
        super("LobbyStalker", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{this.targetSetting, this.arcadeSetting, this.squadSetting, this.megaSetting, this.trioSetting, this.boxingSetting}
        );
        this.process8();
    }

    private void process3() {
        this.flag = false;
        if (!this.flag2) {
            MessageUtils.x("Lobby stalking stopped.");
        }
    }

    private String computeString3() {
        String text = this.arcadeSetting.getText();
        if (text.equals("Random")) {
            return this.computeString5();
        }
        switch (text) {
            case "Pit":
                return "/play pit";
            case "Bedwars":
                return this.computeString2();
            case "Skywars":
                return this.computeString4();
            case "UHC":
                return this.computeString6();
            case "Duels":
                return this.computeString7();
            case "Murder Mystery":
                return "/play murder_classic";
            case "Arcade":
                return this.computeString();
            default:
                return "/play bedwars_four_four";
        }
    }

    private void process8() {
        this.squadSetting.setVisible(this.arcadeSetting.matchesString2("Bedwars"));
        this.megaSetting.setVisible(this.arcadeSetting.matchesString2("Skywars"));
        this.trioSetting.setVisible(this.arcadeSetting.matchesString2("UHC"));
        this.boxingSetting.setVisible(this.arcadeSetting.matchesString2("Duels"));
    }

    @Override
    public void onEnable() {
        if (this.checkState()) {
            if (this.targetSetting.getValue().trim().isEmpty()) {
                MessageUtils.x("Please set a target username first!");
                this.process28();
            } else {
                MessageUtils.x("Starting lobby stalking for: " + this.targetSetting.getValue() + " in " + this.arcadeSetting.getText() + " mode");
                this.process7();
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    private boolean checkState2() {
        if (mc.theWorld == null) {
            return false;
        }
        String str = this.targetSetting.getValue().trim().toLowerCase();

        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player != null && player.getName() != null && player.getName().toLowerCase().equals(str)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDisable() {
        this.process3();
    }

    private boolean checkState() {
        if (mc.theWorld == null || mc.thePlayer == null || FreshHelper.flag && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
            if (mc.thePlayer != null) {
                MessageUtils.x("Lobby stalking requires Pit spawn or a hub.");
            }

            this.handleFlag3(false);
            return false;
        }
        return true;
    }

    private String computeString5() {
        String[] parts = new String[]{
            "/play sb",
            "/play teams_insane",
            "/play tnt_pvprun",
            "/play bedwars_four_four",
            "/play arcade_hole_in_the_wall",
            "/play arcade_simon_says",
            "/play uhc_solo",
            "/play duels_potion_duel",
            "/play super_smash_2v2_normal",
            "/play murder_classic",
            "/play wool_wool_wars_two_four",
            "/play pit",
            "/play skywars_solo_normal"
        };
        return parts[random.nextInt(parts.length)];
    }

    private String computeString() {
        String[] parts = new String[]{
            "/play arcade_hole_in_the_wall",
            "/play arcade_simon_says",
            "/play arcade_party_games_1",
            "/play arcade_zombies_dead_end",
            "/play arcade_hypixel_says"
        };
        return parts[random.nextInt(parts.length)];
    }

    private String computeString4() {
        String text = this.megaSetting.getText();
        switch (text) {
            case "Solo":
                return "/play skywars_solo_normal";
            case "Duo":
                return "/play skywars_two_normal";
            case "Trio":
                return "/play skywars_three_normal";
            case "Mega":
                return "/play skywars_mega_normal";
            default:
                return "/play skywars_solo_normal";
        }
    }

    public void process4() {
        this.process8();
    }

    private String computeString6() {
        String text = this.trioSetting.getText();
        switch (text) {
            case "Solo":
                return "/play uhc_solo";
            case "Duo":
                return "/play uhc_duo";
            case "Trio":
                return "/play uhc_trio";
            default:
                return "/play uhc_solo";
        }
    }

    private void process7() {
        if (!this.flag) {
            this.flag = true;
            this.flag2 = false;
            mc.thePlayer.sendChatMessage("/p leave");
            if (FreshHelper.flag) {
                mc.thePlayer.sendChatMessage("/housing random");
                scheduledExecutorService.schedule(this::process6, 7500L, TimeUnit.MILLISECONDS);
            } else {
                this.process6();
            }
        } else {
            MessageUtils.x("Lobby stalking is already running.");
        }
    }

    private String computeString7() {
        String text = this.boxingSetting.getText();
        switch (text) {
            case "Classic":
                return "/play duels_classic_duel";
            case "Bow":
                return "/play duels_bow_duel";
            case "Potion":
                return "/play duels_potion_duel";
            case "UHC":
                return "/play duels_uhc_duel";
            case "Sumo":
                return "/play duels_sumo_duel";
            case "Boxing":
                return "/play duels_boxing_duel";
            default:
                return "/play duels_classic_duel";
        }
    }

    public void process() {
        if (this.targetSetting.getValue().trim().isEmpty()) {
            MessageUtils.x("No target username set!");
        } else {
            MessageUtils.x("Checking current lobby for: " + this.targetSetting.getValue());
            if (this.checkState2()) {
                MessageUtils.x("§aTarget " + this.targetSetting.getValue() + " is in this lobby!");
            } else {
                MessageUtils.x("§cTarget " + this.targetSetting.getValue() + " is not in this lobby.");
            }
        }
    }

    private String computeString2() {
        String text = this.squadSetting.getText();
        switch (text) {
            case "Solo":
                return "/play bedwars_eight_one";
            case "Duo":
                return "/play bedwars_eight_two";
            case "Trio":
                return "/play bedwars_four_three";
            case "Squad":
                return "/play bedwars_four_four";
            default:
                return "/play bedwars_four_four";
        }
    }

    private void process6() {
        if (this.flag && this.checkState44()) {
            String text = this.arcadeSetting.getText();
            if (text.equals("Pit")) {
                this.process5();
            } else {
                scheduledExecutorService.schedule(() -> {
                    if (this.flag && this.checkState44()) {
                        if (this.checkState()) {
                            String str = this.computeString3();
                            mc.thePlayer.sendChatMessage(str);
                            MessageUtils.x("Joining lobby: " + str);
                            scheduledExecutorService.schedule(() -> {
                                if (this.flag && this.checkState44()) {
                                    if (this.checkState2()) {
                                        this.flag2 = true;
                                        MessageUtils.x("§aTarget found: " + this.targetSetting.getValue() + " is in this lobby!");
                                        MessageUtils.x("§aStopping lobby stalker - target located!");
                                        this.process28();
                                    } else {
                                        MessageUtils.x("Target not found, continuing search...");
                                        this.process6();
                                    }
                                }
                            }, 3000L, TimeUnit.MILLISECONDS);
                        }
                    }
                }, 7500L, TimeUnit.MILLISECONDS);
            }
        }
    }
}
