package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class JumpPotionHUD extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static JumpPotionHUD instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 60.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private final BooleanSetting countdownSoundSetting = new BooleanSetting("Countdown Sound", this, true);
    private final ModeSetting noteHarpSetting = new ModeSetting(
        "Countdown Sound Type", this, "random.orb", Arrays.asList("random.orb", "note.pling", "note.harp")
    );
    private int count = -1;
    private static final long timestamp = -5531055374783741953L;

    @Generated
    public static JumpPotionHUD getInstance() {
        return instance;
    }

    private void handleFloat(float f, float f2, String str) {
        int[] ints = HudDragHelper.computeIntArray();
        int n = ints[0];
        int n2 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                int textWidth = computeFontRenderer(JumpPotionHUD.class).getStringWidth(str);
                int FONT_HEIGHT = computeFontRenderer(JumpPotionHUD.class).FONT_HEIGHT;
                if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
                    HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }

        if (HudDragHelper.matchesObject(this)) {
            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xPosSetting.handleDouble2(ints2[0]);
                this.yPosSetting.handleDouble2(ints2[1]);
            }
        }
    }

    public JumpPotionHUD() {
        super("Jump Potion HUD", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{this.xPosSetting, this.yPosSetting, this.showLabelSetting, this.modeSetting, this.countdownSoundSetting, this.noteHarpSetting}
        );
        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    private String computeString(double d) {
        return this.modeSetting.matchesString2("#")
            ? String.format(Locale.ROOT, "%.0f", Math.max(0.0, d))
            : String.format(Locale.ROOT, "%.1f", Math.max(0.0, d));
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            boolean potionActive = player.isPotionActive(Potion.jump);
            boolean flag = this.mc.currentScreen instanceof GuiChat;
            if (!potionActive && !flag) {
                this.count = -1;
                HudDragHelper.handleObject(this);
            } else {
                String str;
                if (potionActive) {
                    PotionEffect activePotionEffect = player.getActivePotionEffect(Potion.jump);
                    double d = activePotionEffect.getDuration() / 20.0;
                    int n = (int)Math.ceil(d);
                    if (this.countdownSoundSetting.isEnabled() && n <= 3 && n > 0 && n != this.count) {
                        player.playSound(this.noteHarpSetting.getText(), 1.0F, 1.0F);
                    }

                    this.count = n;
                    str = EnumChatFormatting.WHITE
                        + (this.showLabelSetting.isEnabled() ? "Jump: " : "")
                        + EnumChatFormatting.LIGHT_PURPLE
                        + this.computeString(d);
                } else {
                    str = EnumChatFormatting.WHITE
                        + (this.showLabelSetting.isEnabled() ? "Jump: " : "")
                        + EnumChatFormatting.LIGHT_PURPLE
                        + this.computeString(0.0);
                }

                if (flag) {
                    this.handleFloat(f, f2, str);
                    f = (float)this.xPosSetting.computeDouble();
                    f2 = (float)this.yPosSetting.computeDouble();
                }

                computeFontRenderer(JumpPotionHUD.class).drawStringWithShadow(str, f, f2, (int)timestamp);
            }
        }
    }
}
