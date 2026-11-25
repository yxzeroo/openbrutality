package org.brutality.module.impl.visual;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;

public class Ambience extends Module {
    private final ModeSetting customSetting = new ModeSetting("Time Mode", this, "Custom", Arrays.asList("Day", "Sunset", "Night", "Midnight", "Custom"));
    private final NumberSetting customTimeSetting = new NumberSetting("Custom Time", this, 24000.0, 0.0, 24000.0, 100.0);
    private final ModeSetting donTChangeSetting = new ModeSetting(
        "Weather", this, "Rain", Arrays.asList("Rain", "Thunder", "Clear", "Snow", "Fog", "Acid Rain", "Don't Change")
    );
    private final BooleanSetting rainbowSkySetting = new BooleanSetting("Rainbow Sky", this, false);
    private final BooleanSetting frequentLightningSetting = new BooleanSetting("Frequent Lightning", this, false);
    public static boolean flag = false;
    private final int count = (300 + new Random().nextInt(600)) * 20;

    public Ambience() {
        super("Ambience", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{this.customSetting, this.customTimeSetting, this.donTChangeSetting, this.rainbowSkySetting, this.frequentLightningSetting}
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick && mc.theWorld != null) {
                WorldInfo worldInfo = mc.theWorld.getWorldInfo();
                if (mc.isSingleplayer()) {
                    WorldServer world = MinecraftServer.getServer().worldServers[0];
                    worldInfo = world.getWorldInfo();
                }

                String text = this.customSetting.getText();

                mc.theWorld.setWorldTime(switch (text) {
                    case "Day" -> 1000L;
                    case "Sunset" -> 12000L;
                    case "Night" -> 14000L;
                    case "Midnight" -> 18000L;
                    default -> (long)this.customTimeSetting.computeDouble();
                });
                text = this.donTChangeSetting.getText();
                switch (text) {
                    case "Clear":
                        worldInfo.setCleanWeatherTime(this.count);
                        worldInfo.setRainTime(0);
                        worldInfo.setThunderTime(0);
                        worldInfo.setRaining(false);
                        worldInfo.setThundering(false);
                        break;
                    case "Rain":
                        worldInfo.setRainTime(Integer.MAX_VALUE);
                        worldInfo.setThunderTime(Integer.MAX_VALUE);
                        worldInfo.setRaining(true);
                        worldInfo.setThundering(false);
                        break;
                    case "Thunder":
                        worldInfo.setCleanWeatherTime(0);
                        worldInfo.setRainTime(Integer.MAX_VALUE);
                        worldInfo.setThunderTime(Integer.MAX_VALUE);
                        worldInfo.setRaining(true);
                        worldInfo.setThundering(true);
                        break;
                    case "Snow":
                        worldInfo.setCleanWeatherTime(0);
                        worldInfo.setRainTime(Integer.MAX_VALUE);
                        worldInfo.setThunderTime(Integer.MAX_VALUE);
                        worldInfo.setRaining(true);
                        worldInfo.setThundering(false);
                    case "Fog":
                    case "Don't Change":
                    default:
                        break;
                    case "Acid Rain":
                        worldInfo.setRainTime(Integer.MAX_VALUE);
                        worldInfo.setThunderTime(0);
                        worldInfo.setRaining(true);
                        worldInfo.setThundering(false);
                }

                flag = this.donTChangeSetting.matchesString2("Snow");
                if (this.frequentLightningSetting.isEnabled()) {
                }

                if (this.rainbowSkySetting.isEnabled()) {
                }
            }

            if (event instanceof EventPacketReceive && ((EventPacketReceive)event).getPacket() instanceof S03PacketTimeUpdate) {
                event.cancel();
            }
        }
    }

    @Override
    public void onDisable() {
        flag = false;
        super.onDisable();
    }
}
