package org.brutality.module.impl.visual;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.misc.DefaultType;
import org.brutality.utils.misc.ColorMathUtil;
import org.brutality.utils.render.ArrayListHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public class OnUpdateHandler {
    private static final Random random = new Random();
    public NumberSetting numberSetting;
    public NumberSetting numberSetting2;
    public BooleanSetting booleanSetting;
    public BooleanSetting booleanSetting2;
    public BooleanSetting booleanSetting3;
    public BooleanSetting booleanSetting4;
    public BooleanSetting of;
    public DefaultType defaultType = DefaultType.DEFAULT;
    private long timestamp = 1000L;
    private final long timestamp2 = 200L;
    private final Map<Integer, Float> g = new HashMap<>();
    private final List<OnUpdateHandler.Entry> entries = new java.util.ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    public final Module module;
    public final BooleanSetting healthAsHeartsSetting;
    public final BooleanSetting heartSymbolSetting;
    public final ModeSetting defaultValueSetting;
    public final ModeSetting customSetting;
    public final ColorSetting tagCustomColorSetting;
    private static final long timestamp3 = -5232733457574002689L;

    public static int computeInt() {
        return random.nextBoolean() ? 1 : -1;
    }

    public void onUpdate() {
        this.process3();
        long now = System.currentTimeMillis();
        HashSet set = new HashSet();

        for (Entity entity : this.mc.theWorld.loadedEntityList) {
            set.add(entity.getEntityId());
        }

        this.g.keySet().retainAll(set);

        for (Entity entity2 : this.mc.theWorld.loadedEntityList) {
            if (entity2 instanceof EntityLivingBase living
                && living != this.mc.thePlayer
                && (this.booleanSetting3.isEnabled() || !living.isInvisible())
                && !(this.mc.thePlayer.getDistanceSqToEntity(living) > 2500.0)
                && (
                    living instanceof EntityPlayer
                        ? !ColorMathUtil.Entry9.matchesEntity(living)
                        : this.booleanSetting4.isEnabled() && this.matchesEntity(living) && !ColorMathUtil.Entry9.matchesEntityLivingBase(living)
                )) {
                float f = living.getHealth() + living.getAbsorptionAmount();
                float orDefault = this.g.getOrDefault(living.getEntityId(), f);
                if (f != orDefault && ((DamageTags)this.module).matchesEntityLivingBase(living)) {
                    float f2 = f - orDefault;
                    this.entries
                        .add(
                            new OnUpdateHandler.Entry(
                                f2, living.posX, living.posY + living.getEyeHeight(), living.posZ, f2 < 0.0F && living.deathTime != 0, now, living
                            )
                        );
                }

                if (living.deathTime <= 0) {
                    this.g.put(living.getEntityId(), f);
                } else {
                    this.g.remove(living.getEntityId());
                }
            }
        }

        if (!this.entries.isEmpty()) {
            this.entries.removeIf(entry -> now - entry.timestamp > this.timestamp);
        }
    }

    public void onDisable() {
        this.g.clear();
        this.entries.clear();
    }

    public OnUpdateHandler(Module module) {
        this.module = module;
        this.healthAsHeartsSetting = new BooleanSetting("Health as hearts", module, true);
        this.heartSymbolSetting = new BooleanSetting("Heart symbol", module, true);
        this.defaultValueSetting = new ModeSetting("Tag theme", module, "Default", Arrays.asList(DefaultType.NAMES));
        this.customSetting = new ModeSetting("Tag Color", module, "Original", Arrays.asList("Original", "Theme", "ArrayList", "Custom"));
        this.tagCustomColorSetting = new ColorSetting("Tag Custom Color", module, 0.76F);
        this.process();
        module.handleSettingArray(
            this.numberSetting,
            this.numberSetting2,
            this.defaultValueSetting,
            this.customSetting,
            this.tagCustomColorSetting,
            this.booleanSetting,
            this.booleanSetting2,
            this.booleanSetting3,
            this.booleanSetting4,
            this.of,
            this.healthAsHeartsSetting,
            this.heartSymbolSetting
        );

        for (Setting setting : new Setting[]{
            this.numberSetting,
            this.numberSetting2,
            this.defaultValueSetting,
            this.booleanSetting,
            this.booleanSetting2,
            this.booleanSetting3,
            this.booleanSetting4,
            this.of,
            this.healthAsHeartsSetting,
            this.heartSymbolSetting
        }) {
            setting.setConditionalVisibility(() -> ((DamageTags)module).checkState());
        }

        this.customSetting.setConditionalVisibility(() -> ((DamageTags)module).checkState());
        this.tagCustomColorSetting.setConditionalVisibility(() -> ((DamageTags)module).checkState() && this.customSetting.matchesString2("Custom"));
        this.defaultValueSetting.setConditionalVisibility(() -> ((DamageTags)module).checkState() && this.customSetting.matchesString2("Original"));
        this.of.setConditionalVisibility(() -> ((DamageTags)module).checkState() && this.customSetting.matchesString2("Original"));
    }

    public static double computeDouble(double d, double d2) {
        return d + (d2 - d) * Math.random();
    }

    public static String computeString(double d) {
        double d2 = Math.round(Math.abs(d) * 10.0) / 10.0;
        return d2 == (long)d2 ? Long.toString((long)d2) : Double.toString(d2);
    }

    public void onEventRender3D(EventRender3D event) {
        if (ColorMathUtil.Entry2.checkState()) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                if (OpenGlHelper.shadersSupported) {
                    OpenGlHelper.glUseProgram(0);
                }

                long now = System.currentTimeMillis();

                for (OnUpdateHandler.Entry entry : this.entries) {
                    if (entry.checkState()) {
                        long l = now - entry.timestamp;
                        if (l >= 0L && l <= this.timestamp) {
                            double d = Math.max(0.0, Math.min(1.0, (double)l / this.timestamp));
                            double d2 = entry.amount * (1.0 - Math.pow(1.0 - d, 2.0));
                            RenderManager renderManager = this.mc.getRenderManager();
                            double d3 = entry.amount3 - renderManager.viewerPosX;
                            double d4 = entry.amount4 + d2 - renderManager.viewerPosY;
                            double d5 = entry.amount5 - renderManager.viewerPosZ;
                            int n = 255;
                            long l2 = this.timestamp - 200L;
                            if (l > l2 && 200L > 0L) {
                                double d6 = Math.max(0.0, Math.min(1.0, (l - l2) / 200.0));
                                n = (int)(255.0 * (1.0 - d6));
                            }

                            if (n > 4) {
                                float f = (float)this.numberSetting2.computeDouble();
                                GlStateManager.pushMatrix();
                                GlStateManager.translate((float)d3, (float)d4, (float)d5);
                                GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                                GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                                float f2 = -0.02266667F * f;
                                GlStateManager.scale(f2, f2, f2);
                                GlStateManager.depthMask(false);
                                GlStateManager.disableDepth();
                                GlStateManager.enableBlend();
                                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                                int n2 = this.module.computeFontRenderer2().getStringWidth(entry.text) / -2;
                                int n3 = entry.computeInt();
                                int n4 = ((int)timestamp3 | ~n3) - ~n3 | n << 24;
                                DamageTags.handleFontRenderer(this.module.computeFontRenderer2(), entry.text, n2, -3.0F, n4, true);
                                GlStateManager.disableBlend();
                                GlStateManager.enableDepth();
                                GlStateManager.depthMask(true);
                                ColorMathUtil.Entry10.resetColor();
                                GlStateManager.popMatrix();
                            }
                        }
                    }
                }
            }
        }
    }

    public void process3() {
        this.timestamp = (long)(this.numberSetting.computeDouble() * 1000.0);
        this.defaultType = DefaultType.BY_NAME.getOrDefault(this.defaultValueSetting.getText(), DefaultType.DEFAULT);
    }

    public void process() {
        this.numberSetting = new NumberSetting("Length", this.module, 1.0, 0.3, 5.0, 0.1);
        this.numberSetting2 = new NumberSetting("Tag scale", this.module, 1.0, 0.3, 3.0, 0.1);
        this.booleanSetting = new BooleanSetting("Show heal", this.module, true);
        this.booleanSetting2 = new BooleanSetting("Show hurt", this.module, true);
        this.booleanSetting3 = new BooleanSetting("Show invis", this.module, true);
        this.booleanSetting4 = new BooleanSetting("Show mobs", this.module, true);
        this.of = new BooleanSetting("Traditional health color", this.module, true);
    }

    public void process2() {
        this.onDisable();
    }

    public boolean matchesEntity(Entity entity) {
        return entity instanceof EntityMob || entity instanceof EntityGolem || entity instanceof EntitySlime;
    }

    public final class Entry {
        public final long timestamp;
        public final boolean flag;
        public final String text;
        public final double amount;
        public final double amount2;
        public final double amount3;
        public final double amount4;
        public final double amount5;
        public final EntityLivingBase entityLivingBase;

        public Entry(double amount2, double d, double d2, double d3, boolean flag, long timestamp, EntityLivingBase entityLivingBase) {
            this.entityLivingBase = entityLivingBase;
            this.amount2 = amount2;
            double d4 = 1.0;
            double d5 = OnUpdateHandler.computeInt() * OnUpdateHandler.computeDouble(0.15, 0.65) * d4;
            double d6 = -OnUpdateHandler.computeDouble(0.1, 0.45);
            double d7 = OnUpdateHandler.computeInt() * OnUpdateHandler.computeDouble(0.15, 0.65) * d4;
            this.amount3 = d + d5;
            this.amount4 = d2 + d6;
            this.amount5 = d3 + d7;
            this.flag = flag;
            this.timestamp = timestamp;
            this.amount = OnUpdateHandler.computeDouble(0.35, 0.7);
            if (OnUpdateHandler.this.healthAsHeartsSetting.isEnabled()) {
                amount2 /= 2.0;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(OnUpdateHandler.computeString(amount2));
            if (OnUpdateHandler.this.heartSymbolSetting.isEnabled()) {
                sb.append("❤");
            }

            this.text = sb.toString();
        }

        public boolean checkState() {
            return ((DamageTags)OnUpdateHandler.this.module).matchesEntityLivingBase(this.entityLivingBase)
                && (this.amount2 > 0.0 ? OnUpdateHandler.this.booleanSetting : OnUpdateHandler.this.booleanSetting2).isEnabled();
        }

        public int computeInt() {
            if (OnUpdateHandler.this.customSetting.matchesString2("Custom")) {
                return OnUpdateHandler.this.tagCustomColorSetting.computeColor().getRGB();
            }
            if (!OnUpdateHandler.this.customSetting.matchesString2("Original")) {
                return ArrayListHelper.computeColor(OnUpdateHandler.this.module, OnUpdateHandler.this.customSetting.getText()).getRGB();
            }
            if (OnUpdateHandler.this.of.isEnabled()) {
                if (this.amount2 > 0.0) {
                    return -14221530;
                }
                if (this.amount2 < -6.0 || this.flag) {
                    return -55770;
                }
                return this.amount2 < -3.0 ? -12250 : -218;
            }
            return OnUpdateHandler.this.defaultType.computeInt(0.0);
        }
    }
}
