package org.brutality.module.impl.misc;

import java.util.Arrays;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.PlayerHelper6;

public class ChatMute extends Module {
    private static ChatMute instance;
    private final BooleanSetting playSoundSetting = new BooleanSetting("Play Sound", this, true);
    private final ModeSetting popSetting = new ModeSetting("Sound", this, "Pling", Arrays.asList("Pling", "Click", "Orb", "Pop"));
    private final NumberSetting soundVolumeSetting = new NumberSetting("Sound Volume", this, 0.8, 0.1, 1.0, 0.05);
    private final NumberSetting soundPitchSetting = new NumberSetting("Sound Pitch", this, 1.0, 0.5, 2.0, 0.05);
    private final BooleanSetting magnifierIconSetting = new BooleanSetting("Magnifier Icon", this, true);
    private final ModeSetting blockedSetting = new ModeSetting(
        "Hidden Text", this, "Said something", Arrays.asList("Said something", "Muted message", "Blocked")
    );
    private long timestamp;
    private static final long timestamp2 = 80L;

    @Override
    public void process28() {
    }

    public static void process4() {
        ChatMute instance = getInstance();
        if (instance != null) {
            instance.process();
        } else {
            process3();
        }
    }

    @Override
    public void onDisable() {
        this.toggled = true;
    }

    @Override
    public boolean checkState3() {
        return true;
    }

    public ChatComponentText computeChatComponentText(String str) {
        String text = this.blockedSetting.getText();

        return new ChatComponentText("§8§o\ud83d\udd07 §7" + str + " §8" + switch (text) {
            case "Muted message" -> "sent a muted message";
            case "Blocked" -> "is blocked in chat";
            default -> "said something";
        } + " §8§o(hover to reveal)");
    }

    public ChatMute() {
        super("ChatMute", 0, Category.MISCELLANEOUS);
        instance = this;
        this.setRequiresInGame(false);
        this.toggled = true;
        this.handleSettingArray(
            new Setting[]{
                this.playSoundSetting, this.popSetting, this.soundVolumeSetting, this.soundPitchSetting, this.magnifierIconSetting, this.blockedSetting
            }
        );
    }

    private String computeString() {
        String text = this.popSetting.getText();
        switch (text) {
            case "Click":
                return "random.click";
            case "Orb":
                return "random.orb";
            case "Pop":
                return "random.pop";
            case "Pling":
            default:
                return "note.pling";
        }
    }

    private static void process3() {
        if (mc != null && mc.getSoundHandler() != null) {
            mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("note.pling"), 0.8F, 1.0F, 0.0F, 0.0F, 0.0F));
        }
    }

    public void process() {
        if (this.playSoundSetting.isEnabled()) {
            if (mc != null && mc.getSoundHandler() != null) {
                long now = System.currentTimeMillis();
                if (now - this.timestamp >= timestamp2) {
                    this.timestamp = now;
                    String str = this.computeString();
                    float f = (float)this.soundVolumeSetting.computeDouble();
                    float f2 = (float)this.soundPitchSetting.computeDouble();
                    mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(str), f, f2, 0.0F, 0.0F, 0.0F));
                }
            }
        }
    }

    @Override
    public void handleFlag3(boolean flag) {
        this.toggled = true;
    }

    @Override
    public boolean checkState44() {
        return true;
    }

    public static ChatMute getInstance() {
        if (instance != null) {
            return instance;
        }
        if (Brutality.moduleManager != null) {
            Module module = Brutality.moduleManager.computeModule("ChatMute");
            if (module instanceof ChatMute) {
                instance = (ChatMute)module;
            }
        }

        return instance;
    }

    @Override
    public void onEnable() {
        this.toggled = true;
    }

    public boolean checkState() {
        return this.magnifierIconSetting.isEnabled();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof RenderEvent4) {
            PlayerHelper6.clear();
        }
    }
}
