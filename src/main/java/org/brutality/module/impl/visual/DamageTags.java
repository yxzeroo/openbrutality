package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public class DamageTags extends Module {
    private final ModeSetting brutalityNewSetting = new ModeSetting("Style", this, "Brutality Old", Arrays.asList("Brutality Old", "Brutality New"));
    private final OnUpdateHandler onUpdateHandler;
    private String text;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting showHealingSetting = new BooleanSetting("Show Healing", this, true);
    private final BooleanSetting showDamageSetting = new BooleanSetting("Show Damage", this, true);
    private final ModeSetting healthPointsSetting = new ModeSetting("Mode", this, "Hearts", Arrays.asList("Hearts", "Health Points"));
    private final ModeSetting teamSetting = new ModeSetting("Color", this, "RAG", Arrays.asList("RAG", "Team"));
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 3.0, 0.05);
    private final NumberSetting durationSetting = new NumberSetting("Duration", this, 1.5, 0.0, 5.0, 1.0);
    private final NumberSetting yOffsetSetting = new NumberSetting("Y Offset", this, 1.8, -2.0, 3.0, 1.0);
    private final BooleanSetting depthSetting = new BooleanSetting("Depth", this, true);
    private final BooleanSetting boxSetting = new BooleanSetting("Box", this, true);
    private final BooleanSetting shadowSetting = new BooleanSetting("Shadow", this, true);
    private final BooleanSetting killAuraTargetOnlySetting = new BooleanSetting("KillAura Target Only", this, false);
    private final Map<String, Float> lookupMap = new HashMap<>();
    private final List<DamageTags.Entry> entries = new java.util.ArrayList<>();
    private static final int count = new Color(0, 255, 0).getRGB();
    private static final int count2 = new Color(255, 0, 0).getRGB();
    private static final double amount = 2.5;
    private static final double amount2 = 0.7;
    private static final long timestamp = 150L;
    private static final long timestamp2 = -5554420658298945537L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                if (!this.brutalityNewSetting.getText().equals(this.text)) {
                    this.process2();
                    this.text = this.brutalityNewSetting.getText();
                }

                if (!this.checkState()) {
                    if (event instanceof EventUpdate) {
                        this.handleUpdate();
                    } else if (event instanceof EventRender3D) {
                        this.handleFloat(((EventRender3D)event).getFactor());
                    }
                } else {
                    if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                        this.onUpdateHandler.onUpdate();
                    } else if (event instanceof EventRender3D) {
                        this.onUpdateHandler.onEventRender3D((EventRender3D)event);
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.onUpdateHandler.onDisable();
        this.lookupMap.clear();
        this.entries.clear();
    }

    private void handleUpdate() {
        HashSet set = new HashSet();

        for (EntityPlayer player : EntityCache.getEntries()) {
            if (!player.isDead) {
                set.add(player.getEntityId() + player.getUniqueID().toString());
            }
        }

        this.lookupMap.keySet().retainAll(set);
        Vec3 vec = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
        long now = System.currentTimeMillis();

        for (EntityPlayer player2 : EntityCache.getEntries()) {
            if (player2 != this.mc.thePlayer) {
                String str = player2.getEntityId() + player2.getUniqueID().toString();
                float f = player2.getHealth() + player2.getAbsorptionAmount();
                float f2 = f / (this.healthPointsSetting.matchesString2("Hearts") ? 2.0F : 1.0F);
                float orDefault = this.lookupMap.getOrDefault(str, f);
                float f3 = orDefault / (this.healthPointsSetting.matchesString2("Hearts") ? 2.0F : 1.0F);
                if (!player2.isDead) {
                    this.lookupMap.put(str, f);
                    if (this.matchesEntityLivingBase(player2)
                        && player2.ticksExisted >= 2
                        && f2 != f3
                        && (this.showDamageSetting.isEnabled() || !(f2 < f3))
                        && (this.showHealingSetting.isEnabled() || !(f2 > f3))) {
                        float f4 = f2 - f3;
                        int n = f2 > f3 ? count : count2;
                        String str2;
                        if (this.teamSetting.matchesString2("Team")) {
                            String text = player2.getDisplayName().getFormattedText();
                            if (text.length() >= 2) {
                                String str3 = text.substring(0, 2);
                                str2 = str3 + this.computeString(Math.round(f4 * 10.0) / 10.0);
                            } else {
                                str2 = this.computeString(Math.round(Math.abs(f4) * 10.0) / 10.0);
                            }
                        } else {
                            str2 = this.computeString(Math.round(Math.abs(f4) * 10.0) / 10.0);
                        }

                        if (!str2.endsWith("0") || !str2.contains(".")) {
                            Vec3 vec2 = new Vec3(player2.posX, player2.posY + this.yOffsetSetting.computeDouble(), player2.posZ);
                            double d = vec.distanceTo(vec2);
                            DamageTags.Entry entry = new DamageTags.Entry(vec2, now, n, str2, d, player2);
                            this.entries.add(entry);
                        }
                    }
                }
            }
        }

        long l = (long)(this.durationSetting.computeDouble() * 1000.0);
        Iterator it = this.entries.iterator();

        while (it.hasNext()) {
            DamageTags.Entry entry2 = (DamageTags.Entry)it.next();
            long l2 = now - entry2.time;
            if (l2 > l + 150L) {
                it.remove();
            } else {
                entry2.amount2 = entry2.amount;
                entry2.amount = vec.distanceTo(entry2.position);
            }
        }

        this.entries.sort((entry3, entry4) -> Double.compare(entry4.amount, entry3.amount));
    }

    private void handleString(String str, Vec3 vec, float f, int n, float f2) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            if (OpenGlHelper.shadersSupported) {
                OpenGlHelper.glUseProgram(0);
            }

            RenderManager renderManager = this.mc.getRenderManager();
            FontRenderer fontRenderer = computeFontRenderer(DamageTags.class);
            double d = vec.xCoord - renderManager.viewerPosX;
            double d2 = vec.yCoord - renderManager.viewerPosY;
            double d3 = vec.zCoord - renderManager.viewerPosZ;
            float f3 = this.mc.thePlayer.prevRotationYaw + (this.mc.thePlayer.rotationYaw - this.mc.thePlayer.prevRotationYaw) * f2;
            float f4 = this.mc.thePlayer.prevRotationPitch + (this.mc.thePlayer.rotationPitch - this.mc.thePlayer.prevRotationPitch) * f2;
            GlStateManager.pushMatrix();
            GlStateManager.translate(d, d2, d3);
            GlStateManager.rotate(-f3, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f4, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f / 25.0F, -f / 25.0F, f / 25.0F);
            GlStateManager.disableLighting();
            if (this.depthSetting.isEnabled()) {
                GlStateManager.depthMask(false);
            } else {
                GlStateManager.disableDepth();
            }

            int textWidth = fontRenderer.getStringWidth(str);
            int n2 = textWidth / 2;
            if (this.boxSetting.isEnabled()) {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.disableTexture2D();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
                int n3 = n >> 24 & 0xFF;
                int n4 = n3 / 2 << 24;
                worldRenderer.func_181662_b(-n2 - 1, -1.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
                worldRenderer.func_181662_b(~(n2 - 1) - 1, 8.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
                worldRenderer.func_181662_b(n2 + 1, 8.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
                worldRenderer.func_181662_b(n2 + 1, -1.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
                tessellator.draw();
                GlStateManager.enableTexture2D();
            }

            handleFontRenderer(fontRenderer, str, ~(n2 - 1), 0.0F, n, this.shadowSetting.isEnabled());
            if (this.depthSetting.isEnabled()) {
                GlStateManager.depthMask(true);
            } else {
                GlStateManager.enableDepth();
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void handleFontRenderer(FontRenderer fontRenderer, String str, float f, float f2, int n, boolean flag) {
        if (flag) {
            fontRenderer.drawStringWithShadow(str, f, f2, n);
        } else {
            fontRenderer.drawString(str, f, f2, n, false);
        }
    }

    private void handleFloat(float f) {
        if (!this.entries.isEmpty()) {
            long now = System.currentTimeMillis();
            long l = (long)(this.durationSetting.computeDouble() * 1000.0);
            Iterator it = this.entries.iterator();

            while (it.hasNext()) {
                DamageTags.Entry entry = (DamageTags.Entry)it.next();
                long l2 = now - entry.time;
                if (l2 > l + 150L) {
                    it.remove();
                } else {
                    double amount = entry.amount;
                    if (this.matchesEntityLivingBase(entry.entityLivingBase) && !(amount > 25.0)) {
                        double d = entry.amount2 + (amount - entry.amount2) * f;
                        int n = 255;
                        if (l2 > l) {
                            double d2 = 1.0 - (l2 - l) / 150.0;
                            n = (int)(230.0 * d2 + 25.0);
                        }

                        if (n < 26) {
                            it.remove();
                        } else {
                            if (d < 2.5) {
                                double d3 = (d - 0.7) / 1.8;
                                int n2 = (int)(25.0 + 230.0 * Math.max(d3, 0.0));
                                n = Math.min(n, n2);
                            }

                            int n3 = entry.color & (int)timestamp2 | n << 24;
                            this.handleString(entry.dx, entry.position, (float)this.scaleSetting.computeDouble(), n3, f);
                        }
                    }
                }
            }
        }
    }

    public boolean matchesEntityLivingBase(EntityLivingBase living) {
        return living != null
            && (
                !this.killAuraTargetOnlySetting.isEnabled()
                    || KillAura.instance != null && KillAura.instance.checkState44() && KillAura.entityLivingBase2 == living
            );
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.onUpdateHandler.onDisable();
        this.lookupMap.clear();
        this.entries.clear();
    }

    @Override
    public void process2() {
        this.onUpdateHandler.onDisable();
        this.lookupMap.clear();
        this.entries.clear();
    }

    public boolean checkState() {
        return this.brutalityNewSetting.matchesString2("Brutality New");
    }

    private String computeString(double d) {
        return d == (long)d ? Long.toString((long)d) : Double.toString(d);
    }

    public DamageTags() {
        super("DamageTags", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.brutalityNewSetting,
                this.killAuraTargetOnlySetting,
                this.showHealingSetting,
                this.showDamageSetting,
                this.healthPointsSetting,
                this.teamSetting,
                this.scaleSetting,
                this.durationSetting,
                this.yOffsetSetting,
                this.depthSetting,
                this.boxSetting,
                this.shadowSetting
            }
        );

        for (Setting setting : new Setting[]{
            this.showHealingSetting,
            this.showDamageSetting,
            this.healthPointsSetting,
            this.teamSetting,
            this.scaleSetting,
            this.durationSetting,
            this.yOffsetSetting,
            this.depthSetting,
            this.boxSetting,
            this.shadowSetting
        }) {
            setting.setConditionalVisibility(() -> !this.checkState());
        }

        this.onUpdateHandler = new OnUpdateHandler(this);
        this.text = this.brutalityNewSetting.getText();
    }

    public static class Entry {
        public Vec3 position;
        public long time;
        public int color;
        public String dx;
        public double amount;
        public double amount2;
        public final EntityLivingBase entityLivingBase;

        public Entry(Vec3 position, long time, int color, String dx, double amount, EntityLivingBase entityLivingBase) {
            this.entityLivingBase = entityLivingBase;
            this.position = position;
            this.time = time;
            this.color = color;
            this.dx = dx;
            this.amount = amount;
            this.amount2 = amount;
        }
    }
}
