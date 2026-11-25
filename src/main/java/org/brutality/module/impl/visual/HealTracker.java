package org.brutality.module.impl.visual;

import java.util.Locale;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public final class HealTracker extends Module {
    private static final int count = 80;
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 0.0, -2000.0, 2000.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 20.0, -2000.0, 2000.0, 1.0);
    private final BooleanSetting trackRegenerationSetting = new BooleanSetting("Track Regeneration", this, true);
    private float lastHealth = -1.0F;
    private float factor = 1.0F;
    private int count2;
    private int count3 = -1;
    private int count4 = -1;
    private int count5 = -1;
    private int count6 = -1;
    private boolean flag;

    private void reset() {
        this.lastHealth = mc.thePlayer == null ? -1.0F : mc.thePlayer.getHealth();
        this.factor = 1.0F;
        this.count2 = 0;
        this.count3 = -1;
        this.count4 = -1;
        this.count5 = -1;
        this.count6 = -1;
        this.flag = false;
    }

    public HealTracker() {
        super("Heal Tracker", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.trackRegenerationSetting});
    }

    @Override
    public void onEnable() {
        this.reset();
    }

    @Override
    public void onRender() {
        if (mc.thePlayer == null) {
            HudDragHelper.handleObject(this);
        } else {
            boolean active = EditableModulesHelper.isActive();
            String str = computeString(
                mc.thePlayer.getHealth(),
                mc.thePlayer.getMaxHealth(),
                mc.thePlayer.getFoodStats().getFoodLevel(),
                mc.theWorld != null && mc.theWorld.getGameRules().getGameRuleBooleanValue("naturalRegeneration"),
                this.flag,
                this.count3,
                this.count2,
                this.factor,
                this.trackRegenerationSetting.isEnabled(),
                this.count5,
                this.count6
            );
            if (str.isEmpty() && !active) {
                HudDragHelper.handleObject(this);
            } else {
                if (str.isEmpty()) {
                    str = "Heal tracker: waiting";
                }

                FontRenderer fontRenderer = this.computeFontRenderer2();
                if (fontRenderer == null) {
                    fontRenderer = computeFontRenderer(HealTracker.class);
                }

                ScaledResolution sr = new ScaledResolution(mc);
                int textWidth = fontRenderer.getStringWidth(str);
                int FONT_HEIGHT = fontRenderer.FONT_HEIGHT;
                float f = (sr.getScaledWidth() - textWidth) / 2.0F;
                float f2 = (sr.getScaledHeight() - FONT_HEIGHT) / 2.0F;
                float f3 = f + (float)this.xPosSetting.computeDouble();
                float f4 = f2 + (float)this.yPosSetting.computeDouble();
                if (active) {
                    int[] ints = HudDragHelper.computeIntArray();
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(ints[0], ints[1], f3, f4, textWidth, FONT_HEIGHT)) {
                            HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f3, (int)f4);
                        }

                        int[] ints2 = HudDragHelper.computeIntArray(this);
                        if (ints2 != null) {
                            f3 = Math.max(0, Math.min(ints2[0], sr.getScaledWidth() - textWidth));
                            f4 = Math.max(0, Math.min(ints2[1], sr.getScaledHeight() - FONT_HEIGHT));
                            this.xPosSetting.handleDouble2(f3 - f);
                            this.yPosSetting.handleDouble2(f4 - f2);
                        }
                    } else {
                        HudDragHelper.handleObject(this);
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                fontRenderer.drawStringWithShadow(str, f3, f4, -1);
            }
        }
    }

    public static String computeString(
        float f, float f2, int n, boolean flag, boolean flag2, int n2, int n3, float f3, boolean flag3, int n4, int n5
    ) {
        if (!(f <= 0.0F) && !(f >= f2)) {
            String str = String.format(Locale.ROOT, "+%.1f hearts", f3 / 2.0F);
            boolean flag4 = flag && n >= 18;
            String str2 = "";
            if (flag4) {
                if (flag2 && n2 >= 0) {
                    str2 = "Natural ~" + String.format(Locale.ROOT, "%.1fs", Math.max(0, n2 - n3) / 20.0);
                } else {
                    str2 = "Natural ~4.0s";
                }
            }

            String str3 = "";
            if (flag3 && n4 >= 0 && n5 >= 0) {
                str3 = "Regen " + (n4 + 1) + " ~" + String.format(Locale.ROOT, "%.1fs", n5 / 20.0);
            }

            if (str2.isEmpty() && str3.isEmpty()) {
                return "";
            }
            String str4 = str2.isEmpty() ? str3 : (str3.isEmpty() ? str2 : str2 + " | " + str3);
            return str4 + " / " + str;
        }
        return "";
    }

    @Override
    public void onDisable() {
        this.reset();
        HudDragHelper.handleObject(this);
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
            EntityPlayerSP player = mc.thePlayer;
            if (player != null && mc.theWorld != null) {
                this.count2++;
                float health = player.getHealth();
                boolean flag = mc.theWorld.getGameRules().getGameRuleBooleanValue("naturalRegeneration")
                    && player.getFoodStats().getFoodLevel() >= 18
                    && health > 0.0F
                    && health < player.getMaxHealth();
                PotionEffect effect = this.trackRegenerationSetting.isEnabled() ? player.getActivePotionEffect(Potion.regeneration) : null;
                if (effect != null && health > 0.0F && health < player.getMaxHealth()) {
                    this.count5 = effect.getAmplifier();
                    int n = Math.max(1, 50 >> Math.min(30, this.count5));
                    this.count6 = effect.getDuration() % n;
                } else {
                    this.count5 = -1;
                    this.count6 = -1;
                }

                if (!flag) {
                    this.flag = false;
                    this.count3 = -1;
                    this.count4 = -1;
                } else if (!this.flag) {
                    this.flag = true;
                    this.count3 = this.count2 + 80;
                }

                if (this.lastHealth >= 0.0F && health > this.lastHealth + 0.001F) {
                    this.factor = health - this.lastHealth;
                    if (flag && effect == null && this.count4 >= 0) {
                        int n2 = this.count2 - this.count4;
                        if (n2 > 0) {
                            this.count3 = this.count2 + n2;
                        }
                    } else if (flag && effect == null) {
                        this.count3 = this.count2 + 80;
                    }

                    this.count4 = effect == null ? this.count2 : -1;
                } else if (flag && effect == null && this.count3 >= 0 && this.count2 > this.count3 + 20) {
                    this.count3 = this.count2 + 80;
                }

                this.lastHealth = health;
            } else {
                this.reset();
            }
        }
    }
}
