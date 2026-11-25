package org.brutality.module.impl.misc;

import java.io.IOException;
import java.util.Arrays;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.ImplEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;

public class SoundPlayer extends Module {
    private final ModeSetting dogSetting = new ModeSetting(
        "Track",
        this,
        "Calm1",
        Arrays.asList(
            "Calm1",
            "Calm2",
            "Calm3",
            "Hal1",
            "Hal2",
            "Hal3",
            "Sweden",
            "MiceOnVenus",
            "DryHands",
            "WetHands",
            "SubwooferLullaby",
            "LivingMice",
            "Minecraft",
            "Clark",
            "Haggstrom",
            "Danny",
            "Mojang",
            "Biomes",
            "Taswell",
            "Dog"
        )
    );
    private final BooleanSetting monitorSoundsSetting = new BooleanSetting("Monitor Sounds", this, false);
    private final BooleanSetting debugSoundsSetting = new BooleanSetting("Debug Sounds", this, false);
    private ISound iSound;
    private String text;

    @Override
    public void onDisable() {
        this.stopMusic();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof ImplEvent.Entry2 entry2) {
                if (entry2.iSound == this.iSound) {
                    this.text = entry2.text;
                    if (this.monitorSoundsSetting.isEnabled()) {
                        String text = this.dogSetting.getText();
                        boolean flag = entry2.flag;
                        String text2 = entry2.text;
                        String str = text;
                        MessageUtils.x("Sound Player: " + str + " source=" + text2 + " streaming=" + flag);
                    }
                }

                if (this.debugSoundsSetting.isEnabled()) {
                    MessageUtils.x("Sound: " + entry2.iSound.getSoundLocation() + " pitch=" + entry2.iSound.getPitch());
                }
            } else if (event instanceof ImplEvent.Entry entry && entry.iSound == this.iSound) {
                this.iSound = null;
                this.text = null;
                MessageUtils.x("Sound Player: could not play " + this.dogSetting.getText() + " (" + entry.text + ").");
            }
        }
    }

    public void process() {
        mc.addScheduledTask(() -> {
            if (this.checkState44()) {
                this.process3();
            }
        });
    }

    public static String computeString(String str) {
        switch (str) {
            case "Calm1":
            case "Minecraft":
                return "music/game/calm1";
            case "Calm2":
            case "Clark":
                return "music/game/calm2";
            case "Calm3":
            case "Sweden":
                return "music/game/calm3";
            case "Hal1":
            case "SubwooferLullaby":
                return "music/game/hal1";
            case "Hal2":
            case "LivingMice":
                return "music/game/hal2";
            case "Hal3":
            case "Haggstrom":
                return "music/game/hal3";
            case "Danny":
                return "music/game/hal4";
            case "DryHands":
                return "music/game/piano1";
            case "WetHands":
                return "music/game/piano2";
            case "MiceOnVenus":
                return "music/game/piano3";
            case "Biomes":
                return "music/game/creative/creative1";
            case "Taswell":
                return "music/game/creative/creative6";
            case "Mojang":
                return "music/game/mojang";
            case "Dog":
                return "music/game/dog";
            default:
                return "music/game/calm1";
        }
    }

    public SoundPlayer() {
        super("Sound Player", 0, Category.MISCELLANEOUS);
        this.setRequiresInGame(false);
        this.handleSettingArray(new Setting[]{this.dogSetting, this.monitorSoundsSetting, this.debugSoundsSetting});
        this.dogSetting.setCallback(this::process);
    }

    public String computeString() {
        return this.iSound != null && this.text != null && mc.getSoundHandler() != null && mc.getSoundHandler().isSoundPlaying(this.iSound)
            ? "Playing: " + this.dogSetting.getText() + " (UUID: " + this.text + ")"
            : "No track playing";
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.process();
        }
    }

    private void process3() {
        this.stopMusic();
        if (mc.getSoundHandler() != null) {
            String str = computeString(this.dogSetting.getText());
            ResourceLocation location = new ResourceLocation("minecraft", "sounds/" + str + ".ogg");
            ResourceLocation location2 = new ResourceLocation("brutality", "music.track." + str.replace('/', '.'));

            try {
                mc.getSoundHandler().registerMusicTrack(location2, location);
            } catch (IOException e) {
                MessageUtils.x("Sound Player: " + this.dogSetting.getText() + " is unavailable in the active resources (" + location + ").");
                return;
            }

            this.iSound = PositionedSoundRecord.create(location2);
            this.text = null;
            mc.getSoundHandler().playSound(this.iSound);
        }
    }

    private void stopMusic() {
        ISound iSound = this.iSound;
        this.iSound = null;
        this.text = null;
        if (iSound != null) {
            mc.addScheduledTask(() -> {
                if (mc.getSoundHandler() != null) {
                    mc.getSoundHandler().stopSound(iSound);
                }
            });
        }
    }
}
