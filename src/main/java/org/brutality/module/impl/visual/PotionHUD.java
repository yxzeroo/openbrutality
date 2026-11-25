package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.lwjgl.input.Mouse;

public class PotionHUD extends Module {
    private final NumberSetting positionXSetting = new NumberSetting("Position X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting positionYSetting = new NumberSetting("Position Y", this, 10.0, 0.0, 1080.0, 1.0);
    private final ModeSetting horizontalSetting = new ModeSetting("Layout", this, "Vertical", Arrays.asList("Vertical", "Horizontal"));
    private final ModeSetting minimalSetting = new ModeSetting("Style", this, "Lunar", Arrays.asList("Lunar", "Classic", "Minimal"));
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 2.0, 0.1);
    private final BooleanSetting showIconSetting = new BooleanSetting("Show Icon", this, true);
    private final BooleanSetting showNameSetting = new BooleanSetting("Show Name", this, true);
    private final BooleanSetting showDurationSetting = new BooleanSetting("Show Duration", this, true);
    private final BooleanSetting showAmplifierSetting = new BooleanSetting("Show Amplifier", this, true);
    private final BooleanSetting backgroundSetting = new BooleanSetting("Background", this, true);
    private final BooleanSetting blinkExpiringSetting = new BooleanSetting("Blink Expiring", this, true);
    private final NumberSetting blinkThresholdSSetting = new NumberSetting("Blink Threshold (s)", this, 10.0, 3.0, 30.0, 1.0);
    private final BooleanSetting speedSetting = new BooleanSetting("Speed", this, true);
    private final BooleanSetting slownessSetting = new BooleanSetting("Slowness", this, true);
    private final BooleanSetting hasteSetting = new BooleanSetting("Haste", this, true);
    private final BooleanSetting miningFatigueSetting = new BooleanSetting("Mining Fatigue", this, true);
    private final BooleanSetting strengthSetting = new BooleanSetting("Strength", this, true);
    private final BooleanSetting jumpBoostSetting = new BooleanSetting("Jump Boost", this, true);
    private final BooleanSetting nauseaSetting = new BooleanSetting("Nausea", this, true);
    private final BooleanSetting regenerationSetting = new BooleanSetting("Regeneration", this, true);
    private final BooleanSetting resistanceSetting = new BooleanSetting("Resistance", this, true);
    private final BooleanSetting fireResistanceSetting = new BooleanSetting("Fire Resistance", this, true);
    private final BooleanSetting waterBreathingSetting = new BooleanSetting("Water Breathing", this, true);
    private final BooleanSetting invisibilitySetting = new BooleanSetting("Invisibility", this, true);
    private final BooleanSetting blindnessSetting = new BooleanSetting("Blindness", this, true);
    private final BooleanSetting nightVisionSetting = new BooleanSetting("Night Vision", this, true);
    private final BooleanSetting hungerSetting = new BooleanSetting("Hunger", this, true);
    private final BooleanSetting weaknessSetting = new BooleanSetting("Weakness", this, true);
    private final BooleanSetting poisonSetting = new BooleanSetting("Poison", this, true);
    private final BooleanSetting witherSetting = new BooleanSetting("Wither", this, true);
    private final BooleanSetting healthBoostSetting = new BooleanSetting("Health Boost", this, true);
    private final BooleanSetting absorptionSetting = new BooleanSetting("Absorption", this, true);
    private final BooleanSetting saturationSetting = new BooleanSetting("Saturation", this, true);
    private final KeybindListSetting effectFiltersSetting = new KeybindListSetting(
        "Effect Filters",
        this,
        this.speedSetting,
        this.slownessSetting,
        this.hasteSetting,
        this.miningFatigueSetting,
        this.strengthSetting,
        this.jumpBoostSetting,
        this.nauseaSetting,
        this.regenerationSetting,
        this.resistanceSetting,
        this.fireResistanceSetting,
        this.waterBreathingSetting,
        this.invisibilitySetting,
        this.blindnessSetting,
        this.nightVisionSetting,
        this.hungerSetting,
        this.weaknessSetting,
        this.poisonSetting,
        this.witherSetting,
        this.healthBoostSetting,
        this.absorptionSetting,
        this.saturationSetting
    );
    private boolean flag = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<Integer, BooleanSetting> r = new HashMap<>();
    private static final long timestamp = 5271436795186249727L;

    private void handleList3(List<PotionEffect> effects, int n, int n2) {
        int n3 = n2;
        int n4 = computeFontRenderer(PotionHUD.class).FONT_HEIGHT + 2;

        for (PotionEffect effect : effects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            boolean flag = this.blinkExpiringSetting.isEnabled() && effect.getDuration() / 20 <= this.blinkThresholdSSetting.computeDouble();
            boolean flag2 = !flag || System.currentTimeMillis() % 1000L < 700L;
            if (!flag2) {
                n3 += n4;
            } else {
                Color color = this.computeColor(potion);
                String str = this.computeString(potion);
                if (this.showAmplifierSetting.isEnabled() && effect.getAmplifier() > 0) {
                    str = str + " " + this.computeString(effect.getAmplifier() + 1);
                }

                String str2 = str;
                if (this.showDurationSetting.isEnabled()) {
                    str2 = str + " §7" + this.getDurationString(effect);
                }

                computeFontRenderer(PotionHUD.class).drawStringWithShadow(str2, n, n3, color.getRGB());
                n3 += n4;
            }
        }
    }

    private Color computeColor(Potion potion) {
        int liquidColor = potion.getLiquidColor();
        return new Color((0xFF | ~(liquidColor >> 16)) - ~(liquidColor >> 16), liquidColor >> 8 & 0xFF, liquidColor & 0xFF, 220);
    }

    private int computeInt(Potion potion, PotionEffect effect) {
        int n = 6;
        int n2 = 4;
        if (this.minimalSetting.matchesString2("Lunar")) {
            n += 3;
        }

        if (this.showIconSetting.isEnabled()) {
            n += 18 + n2;
        }

        if (this.showNameSetting.isEnabled()) {
            String str = this.computeString(potion, effect);
            n += computeFontRenderer(PotionHUD.class).getStringWidth(str) + n2;
            if (this.minimalSetting.matchesString2("Lunar") && this.showDurationSetting.isEnabled()) {
                String durationString = this.getDurationString(effect);
                int textWidth = computeFontRenderer(PotionHUD.class).getStringWidth(durationString);
                int textWidth2 = computeFontRenderer(PotionHUD.class).getStringWidth(str);
                n = Math.max(
                    n,
                    (this.minimalSetting.matchesString2("Lunar") ? 3 : 0)
                        + (this.showIconSetting.isEnabled() ? 18 + n2 : 0)
                        + Math.max(textWidth2, textWidth)
                        + n2
                        + 6
                );
            }
        } else if (this.showDurationSetting.isEnabled()) {
            String durationString2 = this.getDurationString(effect);
            n += computeFontRenderer(PotionHUD.class).getStringWidth(durationString2);
        }

        return Math.max(n, 50);
    }

    private void handleList2(List<PotionEffect> effects, int n, int n2) {
        boolean flag = this.horizontalSetting.matchesString2("Vertical");
        byte b = 18;
        byte b2 = 3;
        byte b3 = 4;
        int n3 = 3;
        int n4 = n;
        int n5 = n2;

        for (PotionEffect effect : effects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            boolean flag2 = this.blinkExpiringSetting.isEnabled() && effect.getDuration() / 20 <= this.blinkThresholdSSetting.computeDouble();
            boolean flag3 = !flag2 || System.currentTimeMillis() % 1000L < 700L;
            if (!flag3) {
                if (flag) {
                    n5 += b + b3 * 2 + b2;
                } else {
                    n4 += this.computeInt(potion, effect) + b2;
                }
            } else {
                int n6 = this.computeInt(potion, effect);
                int n7 = b + b3 * 2;
                Color color = this.computeColor(potion);
                if (this.backgroundSetting.isEnabled()) {
                    Color color2 = new Color(20, 20, 20, 180);
                    OutlineThicknessHelper.handleFloat(n4, n5, n6, n7, 4.0F, color2, color2);
                    Color color3 = new Color(Math.max(0, color.getRed() - 40), Math.max(0, color.getGreen() - 40), Math.max(0, color.getBlue() - 40), 220);
                    OutlineThicknessHelper.handleFloat(n4, n5, n3, n7, 2.0F, color, color3);
                }

                int n8 = n4 + n3 + b3;
                int n9 = n5 + b3;
                if (this.showIconSetting.isEnabled()) {
                    this.handlePotion(potion, n8, n9, b);
                    n8 += b + b2;
                }

                if (this.showNameSetting.isEnabled()) {
                    String str = this.computeString(potion);
                    if (this.showAmplifierSetting.isEnabled()) {
                        int amplifier = effect.getAmplifier();
                        if (amplifier > 0) {
                            str = str + " " + this.computeString(amplifier + 1);
                        }
                    }

                    computeFontRenderer(PotionHUD.class).drawStringWithShadow(str, n8, n9 + 1, (int)timestamp);
                    if (this.showDurationSetting.isEnabled()) {
                        String durationString = this.getDurationString(effect);
                        int n10 = flag2 ? new Color(255, 85, 85).getRGB() : new Color(170, 170, 170).getRGB();
                        computeFontRenderer(PotionHUD.class)
                            .drawStringWithShadow(durationString, n8, n9 + computeFontRenderer(PotionHUD.class).FONT_HEIGHT + 1, n10);
                    }
                } else if (this.showDurationSetting.isEnabled()) {
                    String durationString2 = this.getDurationString(effect);
                    int n11 = flag2 ? new Color(255, 85, 85).getRGB() : new Color(200, 200, 200).getRGB();
                    computeFontRenderer(PotionHUD.class).drawStringWithShadow(durationString2, n8, n9 + 5, n11);
                }

                if (flag) {
                    n5 += n7 + b2;
                } else {
                    n4 += n6 + b2;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
        this.flag = false;
    }

    @Override
    public boolean onBlurMode(SetShaderTypeEvent.BlurMode blurMode) {
        return blurMode == SetShaderTypeEvent.BlurMode.GLOW;
    }

    private String computeString(Potion potion, PotionEffect effect) {
        String str = this.computeString(potion);
        int amplifier = effect.getAmplifier();
        if (amplifier > 0) {
            str = str + " " + this.computeString(amplifier + 1);
        }

        return str;
    }

    private void handleList(List<PotionEffect> effects, int n, int n2) {
        boolean flag = this.horizontalSetting.matchesString2("Vertical");
        byte b = 18;
        byte b2 = 4;
        byte b3 = 3;
        int n3 = n;
        int n4 = n2;

        for (PotionEffect effect : effects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            int n5 = this.computeInt(potion, effect);
            int n6 = b + b3 * 2;
            boolean flag2 = this.blinkExpiringSetting.isEnabled() && effect.getDuration() / 20 <= this.blinkThresholdSSetting.computeDouble();
            boolean flag3 = !flag2 || System.currentTimeMillis() % 1000L < 700L;
            if (!flag3) {
                if (flag) {
                    n4 += n6 + b2;
                } else {
                    n3 += n5 + b2;
                }
            } else {
                if (this.backgroundSetting.isEnabled()) {
                    Color color = new Color(35, 10, 10, 200);
                    Color color2 = new Color(80, 15, 15, 200);
                    OutlineThicknessHelper.handleFloat(n3, n4, n5, n6, 4.0F, color, color2);
                    Color color3 = new Color(200, 50, 50, 200);
                    OutlineThicknessHelper.handleFloat(n3, n4, n5, n6, 4.0F, 1.5F, new Color(0, 0, 0, 0), color3);
                }

                int n7 = n3 + b3;
                int n8 = n4 + b3;
                if (this.showIconSetting.isEnabled()) {
                    this.handlePotion(potion, n7, n8, b);
                    n7 += b + b2;
                }

                if (this.showNameSetting.isEnabled()) {
                    String str = this.computeString(potion, effect);
                    computeFontRenderer(PotionHUD.class).drawStringWithShadow(str, n7, n8 + 5, new Color(255, 200, 200).getRGB());
                    n7 += computeFontRenderer(PotionHUD.class).getStringWidth(str) + b2;
                }

                if (this.showDurationSetting.isEnabled()) {
                    String durationString = this.getDurationString(effect);
                    int n9 = flag2 ? new Color(255, 85, 85).getRGB() : new Color(200, 200, 255).getRGB();
                    computeFontRenderer(PotionHUD.class).drawStringWithShadow(durationString, n7, n8 + 5, n9);
                }

                if (flag) {
                    n4 += n6 + b2;
                } else {
                    n3 += n5 + b2;
                }
            }
        }
    }

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null) {
            var activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
            if (!activePotionEffects.isEmpty()) {
                java.util.ArrayList list = new java.util.ArrayList();

                for (PotionEffect effect : activePotionEffects) {
                    BooleanSetting boolSetting = this.r.get(effect.getPotionID());
                    if (boolSetting == null || boolSetting.isEnabled()) {
                        list.add(effect);
                    }
                }

                if (!list.isEmpty()) {
                    list.sort(Comparator.comparingInt(PotionEffect::getDuration));
                    this.handleList(list);
                    ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
                    int n = (int)Math.max(0.0, Math.min(this.positionXSetting.computeDouble(), (double)sr.getScaledWidth()));
                    int n2 = (int)Math.max(0.0, Math.min(this.positionYSetting.computeDouble(), (double)sr.getScaledHeight()));
                    float f = (float)this.scaleSetting.computeDouble();
                    if (f != 1.0F) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(f, f, 1.0F);
                        n = (int)(n / f);
                        n2 = (int)(n2 / f);
                    }

                    String text = this.minimalSetting.getText();
                    switch (text) {
                        case "Lunar":
                            this.handleList2(list, n, n2);
                            break;
                        case "Classic":
                            this.handleList(list, n, n2);
                            break;
                        case "Minimal":
                            this.handleList3(list, n, n2);
                    }

                    if (f != 1.0F) {
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    private int computeInt2(List<PotionEffect> effects) {
        if (this.horizontalSetting.matchesString2("Vertical")) {
            int n = 0;

            for (PotionEffect effect : effects) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                n = Math.max(n, this.computeInt(potion, effect));
            }

            return (int)(n * this.scaleSetting.computeDouble());
        }
        int n2 = 0;
        int n3 = 4;

        for (PotionEffect effect2 : effects) {
            Potion potion2 = Potion.potionTypes[effect2.getPotionID()];
            n2 += this.computeInt(potion2, effect2) + n3;
        }

        return (int)(n2 * this.scaleSetting.computeDouble());
    }

    private String computeString(Potion potion) {
        String name = potion.getName();
        if (name.startsWith("potion.")) {
            name = name.substring(7);
        }

        if (!name.isEmpty()) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        return name;
    }

    private void process() {
        this.r.put(Potion.moveSpeed.id, this.speedSetting);
        this.r.put(Potion.moveSlowdown.id, this.slownessSetting);
        this.r.put(Potion.digSpeed.id, this.hasteSetting);
        this.r.put(Potion.digSlowdown.id, this.miningFatigueSetting);
        this.r.put(Potion.damageBoost.id, this.strengthSetting);
        this.r.put(Potion.jump.id, this.jumpBoostSetting);
        this.r.put(Potion.confusion.id, this.nauseaSetting);
        this.r.put(Potion.regeneration.id, this.regenerationSetting);
        this.r.put(Potion.resistance.id, this.resistanceSetting);
        this.r.put(Potion.fireResistance.id, this.fireResistanceSetting);
        this.r.put(Potion.waterBreathing.id, this.waterBreathingSetting);
        this.r.put(Potion.invisibility.id, this.invisibilitySetting);
        this.r.put(Potion.blindness.id, this.blindnessSetting);
        this.r.put(Potion.nightVision.id, this.nightVisionSetting);
        this.r.put(Potion.hunger.id, this.hungerSetting);
        this.r.put(Potion.weakness.id, this.weaknessSetting);
        this.r.put(Potion.poison.id, this.poisonSetting);
        this.r.put(Potion.wither.id, this.witherSetting);
        this.r.put(Potion.healthBoost.id, this.healthBoostSetting);
        this.r.put(Potion.absorption.id, this.absorptionSetting);
        this.r.put(Potion.saturation.id, this.saturationSetting);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D) {
                this.onRender();
            } else if (event instanceof SetShaderTypeEvent setShaderTypeEvent && setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.GLOW) {
                this.process3();
            }
        }
    }

    private void handleList(List<PotionEffect> effects) {
        int[] ints = HudDragHelper.computeIntArray();
        int n = ints[0];
        int n2 = ints[1];
        int n3 = this.computeInt2(effects);
        int n4 = this.computeInt(effects);
        boolean flag = HudDragHelper.matchesInt(n, n2, this.positionXSetting.computeDouble(), this.positionYSetting.computeDouble(), n3, n4);
        if (Mouse.isButtonDown(0)) {
            if (flag
                && !this.flag
                && HudDragHelper.matchesObject(this, n, n2, (int)this.positionXSetting.computeDouble(), (int)this.positionYSetting.computeDouble())) {
                this.flag = true;
            }

            if (this.flag) {
                int[] ints2 = HudDragHelper.computeIntArray(this);
                if (ints2 != null) {
                    this.positionXSetting.handleDouble2(ints2[0]);
                    this.positionYSetting.handleDouble2(ints2[1]);
                }
            }
        } else if (this.flag) {
            HudDragHelper.handleObject(this);
            this.flag = false;
        }
    }

    private int computeInt(List<PotionEffect> effects) {
        int n = 24;
        int n2 = this.minimalSetting.matchesString2("Lunar") ? 3 : 4;
        if (this.minimalSetting.matchesString2("Minimal")) {
            n = computeFontRenderer(PotionHUD.class).FONT_HEIGHT + 2;
            n2 = 0;
        }

        return this.horizontalSetting.matchesString2("Vertical")
            ? (int)((n + n2) * effects.size() * this.scaleSetting.computeDouble())
            : (int)(n * this.scaleSetting.computeDouble());
    }

    public PotionHUD() {
        super("PotionHUD", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.positionXSetting,
                this.positionYSetting,
                this.horizontalSetting,
                this.minimalSetting,
                this.scaleSetting,
                this.showIconSetting,
                this.showNameSetting,
                this.showDurationSetting,
                this.showAmplifierSetting,
                this.backgroundSetting,
                this.blinkExpiringSetting,
                this.blinkThresholdSSetting,
                this.effectFiltersSetting
            }
        );
        this.process();
    }

    private void process3() {
        if (this.mc.thePlayer != null) {
            var activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
            if (!activePotionEffects.isEmpty()) {
                java.util.ArrayList list = new java.util.ArrayList();

                for (PotionEffect effect : activePotionEffects) {
                    BooleanSetting boolSetting = this.r.get(effect.getPotionID());
                    if (boolSetting == null || boolSetting.isEnabled()) {
                        list.add(effect);
                    }
                }

                if (!list.isEmpty()) {
                    if (this.minimalSetting.matchesString2("Classic")) {
                        boolean flag = this.horizontalSetting.matchesString2("Vertical");
                        byte b = 18;
                        byte b2 = 4;
                        byte b3 = 3;
                        int n = (int)this.positionXSetting.computeDouble();
                        int n2 = (int)this.positionYSetting.computeDouble();
                        int n3 = n;
                        int n4 = n2;
                        Color color = new Color(200, 50, 50, 255);

                        for (PotionEffect effect2 : ((Iterable<PotionEffect>)(Object)(list))) {
                            Potion potion = Potion.potionTypes[effect2.getPotionID()];
                            int n5 = this.computeInt(potion, effect2);
                            int n6 = b + b3 * 2;
                            OutlineThicknessHelper.handleFloat(n3, n4, n5, n6, 4.0F, 1.5F, new Color(0, 0, 0, 0), color);
                            if (flag) {
                                n4 += n6 + b2;
                            } else {
                                n3 += n5 + b2;
                            }
                        }
                    }
                }
            }
        }
    }

    private void handlePotion(Potion potion, int n, int n2, int n3) {
        if (potion.hasStatusIcon()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
            int statusIconIndex = potion.getStatusIconIndex();
            int n4 = statusIconIndex % 8 * 18;
            int n5 = 198 + statusIconIndex / 8 * 18;
            Gui.drawModalRectWithCustomSizedTexture(n, n2, n4, n5, n3, n3, 256.0F, 256.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private String getDurationString(PotionEffect effect) {
        int duration = effect.getDuration();
        int n = duration / 1200;
        int n2 = duration % 1200 / 20;
        return String.format("%d:%02d", n, n2);
    }

    private String computeString(int n) {
        String[] parts = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return n <= parts.length ? parts[n - 1] : String.valueOf(n);
    }
}
