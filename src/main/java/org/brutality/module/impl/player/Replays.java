package org.brutality.module.impl.player;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;

public class Replays extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static final Random random = new Random();
    private volatile boolean isRunning = false;
    private final ModeSetting minemenSetting = new ModeSetting("Server", this, "Hypixel", Arrays.asList("Hypixel", "BlocksMC", "Minemen"));
    private final ModeSetting speedUHCSetting = new ModeSetting(
        "Game Mode",
        this,
        "Random",
        Arrays.asList(
            "Random",
            "Housing",
            "Bedwars",
            "Skywars",
            "Pit",
            "UHC",
            "Duels",
            "Murder Mystery",
            "TNT Games",
            "Arcade",
            "Blitz SG",
            "Mega Walls",
            "Warlords",
            "Speed UHC"
        )
    );
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 5.0, 1.0, 30.0, 1.0);
    private static final String[] stringArray = new String[]{
        "Housing", "Bedwars", "Skywars", "Pit", "UHC", "Duels", "Murder Mystery", "TNT Games", "Arcade", "Blitz SG", "Mega Walls", "Warlords", "Speed UHC"
    };
    private static final String[] stringArray2 = new String[]{"Skywars", "Bedwars", "Survival", "Creative", "Prison"};
    private static final String[] stringArray3 = new String[]{"NoDebuff", "Gapple", "BuildUHC", "Sumo", "Boxing"};

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    private String computeString(String str) {
        String str2 = str.toLowerCase();
        switch (str2) {
            case "skywars":
                return "/play skywars";
            case "bedwars":
                return "/play bedwars";
            case "survival":
                return "/play survival";
            case "creative":
                return "/play creative";
            case "prison":
                return "/play prison";
            default:
                return "/play skywars";
        }
    }

    private String computeString() {
        String text = this.minemenSetting.getText();
        String text2 = this.speedUHCSetting.getText();
        if (text2.equals("Random")) {
            switch (text) {
                case "Hypixel":
                    text2 = stringArray[random.nextInt(stringArray.length)];
                    break;
                case "BlocksMC":
                    text2 = stringArray2[random.nextInt(stringArray2.length)];
                    break;
                case "Minemen":
                    text2 = stringArray3[random.nextInt(stringArray3.length)];
            }
        }

        switch (text) {
            case "Hypixel":
                return this.computeString2(text2);
            case "BlocksMC":
                return this.computeString(text2);
            case "Minemen":
                return this.computeString3(text2);
            default:
                return "/play bedwars_four_four";
        }
    }

    private void process() {
        if (mc.thePlayer == null) {
            MessageUtils.x("§cPlayer is null! Cannot start replays.");
            this.process28();
        } else {
            this.isRunning = true;
            this.process5();
            this.process4();
        }
    }

    private void process4() {
        if (this.isRunning) {
            long l = (long)(this.delaySetting.computeDouble() * 1000.0);
            scheduledExecutorService.schedule(() -> {
                if (this.isRunning && mc.thePlayer != null) {
                    mc.thePlayer.sendChatMessage("/p leave");
                    scheduledExecutorService.schedule(() -> {
                        if (this.isRunning && mc.thePlayer != null) {
                            String str = this.computeString();
                            mc.thePlayer.sendChatMessage(str);
                            if (str.contains("housing")) {
                                scheduledExecutorService.schedule(() -> {
                                    if (this.isRunning && mc.thePlayer != null) {
                                        mc.thePlayer.sendChatMessage("/housing random");
                                    }
                                }, 2000L, TimeUnit.MILLISECONDS);
                            }

                            this.process4();
                        }
                    }, 1000L, TimeUnit.MILLISECONDS);
                }
            }, l, TimeUnit.MILLISECONDS);
        }
    }

    public Replays() {
        super("Replays", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.minemenSetting, this.speedUHCSetting, this.delaySetting});
    }

    private void process3() {
        this.isRunning = false;
        MessageUtils.x("§cReplays stopped.");
    }

    @Override
    public void onDisable() {
        this.process3();
    }

    private String computeString2(String str) {
        String str2 = str.toLowerCase();
        switch (str2) {
            case "housing":
                return "/play housing";
            case "bedwars":
                return "/play bedwars_four_four";
            case "skywars":
                return "/play skywars_solo_normal";
            case "pit":
                return "/play pit";
            case "uhc":
                return "/play uhc_solo";
            case "duels":
                return "/play duels_bow_duel";
            case "murder mystery":
                return "/play murder_classic";
            case "tnt games":
                return "/play tnt_pvprun";
            case "arcade":
                return "/play arcade_hole_in_the_wall";
            case "blitz sg":
                return "/play blitz_solo_normal";
            case "mega walls":
                return "/play walls3";
            case "warlords":
                return "/play battleground_ctf";
            case "speed uhc":
                return "/play speed_solo_normal";
            default:
                return "/play bedwars_four_four";
        }
    }

    private void process5() {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage("/replay clear");
            scheduledExecutorService.schedule(() -> {
                if (mc.thePlayer != null) {
                    String str = this.minemenSetting.getText().toLowerCase();
                    switch (str) {
                        case "hypixel":
                            mc.thePlayer.sendChatMessage("/replay deleteall");
                            break;
                        case "blocksmc":
                            mc.thePlayer.sendChatMessage("/clearreplays");
                            break;
                        case "minemen":
                            mc.thePlayer.sendChatMessage("/replay clear all");
                    }
                }
            }, 500L, TimeUnit.MILLISECONDS);
        }
    }

    private String computeString3(String str) {
        String str2 = str.toLowerCase();
        switch (str2) {
            case "nodebuff":
                return "/play nodebuff";
            case "gapple":
                return "/play gapple";
            case "builduhc":
                return "/play builduhc";
            case "sumo":
                return "/play sumo";
            case "boxing":
                return "/play boxing";
            default:
                return "/play nodebuff";
        }
    }

    @Override
    public void onEnable() {
        if (!this.isRunning) {
            this.process();
            String text = this.minemenSetting.getText();
            String text2 = this.speedUHCSetting.getText();
            double d = this.delaySetting.computeDouble();
            String str = text2;
            String str2 = text;
            MessageUtils.x("§aReplays started! Server: " + str2 + ", Mode: " + str + ", Delay: " + d + "s");
        } else {
            MessageUtils.x("§cReplays is already running!");
        }
    }
}
