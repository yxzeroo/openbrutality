package org.brutality.module.impl.combat;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.lag.apih.ApihHandler;
import org.brutality.lag.apih.InboundType;
import org.brutality.lag.timeout.TimeoutHandler2;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.combat.CombatHelper2;
import org.brutality.utils.modules.CountMovementHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.rotation.GetVectorForRotationHelper;
import org.lwjgl.opengl.GL11;

public final class LagRange extends Module {
    private static final double amount = 9.0;
    private static final long timestamp = 80L;
    private static final double amount2 = 1.0E-6;
    public static LagRange instance;
    public boolean flag = false;
    private final NumberSetting rangeSetting;
    private final NumberSetting maximumDelaySetting;
    private final BooleanSetting sprintResetSetting;
    private final BooleanSetting blockSwordSetting;
    private final BooleanSetting usedSplashPotionSetting;
    private final BooleanSetting holdingAWeaponSetting;
    private final BooleanSetting realPositionIndicatorSetting;
    private final BooleanSetting showInFirstPersonSetting;
    private final ColorSetting indicatorColorSetting;
    private final NumberSetting indicatorLineWidthSetting;
    private final BooleanSetting indicatorFilledSetting;
    private EntityPlayer entityPlayer;
    private double amount3 = -1.0;
    private int count;
    private int count2;
    private int count3;
    private boolean flag2;
    private boolean flag3;
    private ApihHandler apihHandler;
    private Vec3 vec3;
    private Vec3 vec32;
    private long timestamp2;

    private boolean isMoving() {
        return mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F;
    }

    @Override
    public void onDisable() {
        this.process();
        this.process4();
    }

    private static Vec3 computeVec3(Vec3 vec, Vec3 vec2, double d) {
        if (d <= 0.0) {
            return vec;
        }
        return d >= 1.0
            ? vec2
            : new Vec3(
                vec.xCoord + (vec2.xCoord - vec.xCoord) * d,
                vec.yCoord + (vec2.yCoord - vec.yCoord) * d,
                vec.zCoord + (vec2.zCoord - vec.zCoord) * d
            );
    }

    @Override
    public void onEnable() {
        this.process4();
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        return this.entityPlayer != null && player != null ? this.entityPlayer.getEntityId() == player.getEntityId() : this.entityPlayer == player;
    }

    private void process5() {
        if (GetCurrentScreenHelper.checkState2() && !mc.thePlayer.isDead && mc.theWorld != null) {
            if (!this.checkState2() && !this.checkState()) {
                if (mc.currentScreen != null) {
                    if (this.flag) {
                        this.process();
                    }

                    this.process4();
                } else {
                    double d = this.rangeSetting.computeDouble() * this.rangeSetting.computeDouble();
                    boolean moving = this.isMoving();
                    EntityPlayer player = CombatHelper2.computeEntityPlayer(this.rangeSetting.computeDouble(), true);
                    if (!this.matchesEntityPlayer(player)) {
                        if (this.flag) {
                            this.process();
                        }

                        this.amount3 = -1.0;
                        this.count3 = -1;
                        this.count2 = player != null ? player.hurtTime : 0;
                    }

                    this.entityPlayer = player;
                    if (this.entityPlayer != null) {
                        double d2 = GetVectorForRotationHelper.computeDouble(this.entityPlayer);
                        if (this.flag) {
                            if (d2 > d) {
                                this.process();
                                this.amount3 = d2;
                                this.count3 = -1;
                                this.count2 = this.entityPlayer.hurtTime;
                                return;
                            }

                            if (this.amount3 >= 0.0 && d2 >= this.amount3) {
                                boolean flag = this.count3 == this.entityPlayer.getEntityId() && d2 <= 9.0 && mc.thePlayer.hurtTime == 0;
                                if (!flag) {
                                    this.process();
                                    this.amount3 = d2;
                                    this.count2 = this.entityPlayer.hurtTime;
                                    return;
                                }
                            }

                            int hurtTime = mc.thePlayer.hurtTime;
                            if (hurtTime > this.count) {
                                this.process();
                                this.count3 = -1;
                                this.count = hurtTime;
                                this.amount3 = d2;
                                this.count2 = this.entityPlayer.hurtTime;
                                return;
                            }

                            this.count = hurtTime;
                            if (Brutality.getLagHandler() != null) {
                                Brutality.getLagHandler().handleInboundType(InboundType.OUTBOUND, (long)this.maximumDelaySetting.computeDouble());
                            }

                            if (this.holdingAWeaponSetting.isEnabled() && !GetCurrentScreenHelper.checkState()) {
                                this.process();
                                this.amount3 = d2;
                                this.count2 = this.entityPlayer.hurtTime;
                                return;
                            }

                            if (this.sprintResetSetting.isEnabled()) {
                                boolean sprinting = mc.thePlayer.isSprinting();
                                if (sprinting && !this.flag2) {
                                    this.process();
                                    this.flag2 = sprinting;
                                    this.amount3 = d2;
                                    this.count2 = this.entityPlayer.hurtTime;
                                    return;
                                }

                                this.flag2 = sprinting;
                            }

                            if (this.blockSwordSetting.isEnabled()) {
                                boolean blocking = mc.thePlayer.isBlocking();
                                if (blocking && !this.flag3) {
                                    this.process();
                                    this.flag3 = blocking;
                                    this.amount3 = d2;
                                    this.count2 = this.entityPlayer.hurtTime;
                                    return;
                                }

                                this.flag3 = blocking;
                            }

                            if (this.usedSplashPotionSetting.isEnabled() && mc.thePlayer.isUsingItem()) {
                                ItemStack heldItem = mc.thePlayer.getHeldItem();
                                if (heldItem != null && heldItem.getItem() instanceof ItemPotion && ItemPotion.isSplash(heldItem.getMetadata())) {
                                    this.process();
                                    this.amount3 = d2;
                                    this.count2 = this.entityPlayer.hurtTime;
                                    return;
                                }
                            }

                            this.amount3 = d2;
                            this.count2 = this.entityPlayer.hurtTime;
                            return;
                        }

                        int hurtTime2 = mc.thePlayer.hurtTime;
                        if (hurtTime2 > this.count) {
                            this.count3 = -1;
                        }

                        this.count = hurtTime2;
                        this.flag2 = mc.thePlayer.isSprinting();
                        this.flag3 = mc.thePlayer.isBlocking();
                        if (hurtTime2 == 0 && this.count2 == 0 && this.entityPlayer.hurtTime > 0) {
                            this.count3 = this.entityPlayer.getEntityId();
                        }

                        this.count2 = this.entityPlayer.hurtTime;
                        boolean flag2 = this.amount3 >= 0.0 && d2 < this.amount3;
                        boolean flag3 = d2 > 9.0;
                        boolean flag4 = !this.holdingAWeaponSetting.isEnabled() || GetCurrentScreenHelper.checkState();
                        boolean flag5 = this.count3 == this.entityPlayer.getEntityId();
                        boolean flag6 = flag5 && d2 <= 9.0 && hurtTime2 == 0 && moving && flag4;
                        this.amount3 = d2;
                        if (hurtTime2 == 0 && flag4 && moving && (flag2 && flag3 || flag6)) {
                            this.process3();
                        }
                    } else {
                        if (this.flag) {
                            this.process();
                        }

                        this.amount3 = -1.0;
                        this.count3 = -1;
                        this.count2 = 0;
                    }
                }
            } else {
                if (this.flag) {
                    this.process();
                }
            }
        } else {
            if (this.flag) {
                this.process();
            }

            this.process4();
        }
    }

    private static boolean matchesVec3(Vec3 vec, Vec3 vec2) {
        return Math.abs(vec.xCoord - vec2.xCoord) > 1.0E-6 || Math.abs(vec.yCoord - vec2.yCoord) > 1.0E-6 || Math.abs(vec.zCoord - vec2.zCoord) > 1.0E-6;
    }

    private void onEventRender3D(EventRender3D event) {
        if (GetCurrentScreenHelper.checkState2()) {
            if (!this.flag) {
                this.process6();
            } else if (this.realPositionIndicatorSetting.isEnabled()) {
                if (mc.gameSettings.thirdPersonView != 0 || this.showInFirstPersonSetting.isEnabled()) {
                    if (Brutality.getLagHandler() != null) {
                        Vec3 vec3 = Brutality.getLagHandler().getVec3();
                        if (vec3 == null) {
                            this.process6();
                        } else {
                            long now = System.currentTimeMillis();
                            if (this.vec32 == null) {
                                this.vec3 = vec3;
                                this.vec32 = vec3;
                                this.timestamp2 = now;
                            } else if (matchesVec3(vec3, this.vec32)) {
                                double d = Math.min(1.0, (now - this.timestamp2) / 80.0);
                                this.vec3 = computeVec3(this.vec3, this.vec32, d);
                                this.vec32 = vec3;
                                this.timestamp2 = now;
                            }

                            double d2 = Math.min(1.0, (now - this.timestamp2) / 80.0);
                            Vec3 vec = computeVec3(this.vec3, this.vec32, d2);
                            float f = mc.thePlayer.width / 2.0F;
                            float height = mc.thePlayer.height;
                            AxisAlignedBB box = new AxisAlignedBB(
                                vec.xCoord - f, vec.yCoord, vec.zCoord - f, vec.xCoord + f, vec.yCoord + height, vec.zCoord + f
                            );
                            Vec3 vec2 = new Vec3(mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ);
                            if (!box.isVecInside(vec2)) {
                                AxisAlignedBB box2 = box.offset(
                                    -mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ
                                );
                                Color color = this.indicatorColorSetting.computeColor();
                                float f2 = color.getRed() / 255.0F;
                                float f3 = color.getGreen() / 255.0F;
                                float f4 = color.getBlue() / 255.0F;
                                float f5 = color.getAlpha() / 255.0F;
                                GL11.glPushMatrix();
                                GlStateManager.enableBlend();
                                GlStateManager.disableTexture2D();
                                GlStateManager.disableDepth();
                                GlStateManager.depthMask(false);
                                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                                if (this.indicatorFilledSetting.isEnabled()) {
                                    GL11.glColor4f(f2, f3, f4, f5);
                                    ResetColorHelper.handleAxisAlignedBB(box2);
                                }

                                GL11.glLineWidth((float)this.indicatorLineWidthSetting.computeDouble());
                                GL11.glColor4f(f2, f3, f4, f5);
                                ResetColorHelper.handleAxisAlignedBB2(box2);
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                GlStateManager.enableDepth();
                                GlStateManager.depthMask(true);
                                GlStateManager.enableTexture2D();
                                GlStateManager.disableBlend();
                                GL11.glPopMatrix();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkState() {
        KillAura instance = KillAura.getInstance();
        return instance != null && instance.checkState44() ? instance.flag30 || instance.checkState50() || instance.checkState() : false;
    }

    public LagRange() {
        super("LagRange", 0, Category.COMBAT);
        this.rangeSetting = new NumberSetting("Range", this, 6.0, 3.0, 10.0, 0.1);
        this.maximumDelaySetting = new NumberSetting("Maximum delay", this, 200.0, 50.0, 1000.0, 10.0);
        this.sprintResetSetting = new BooleanSetting("Sprint reset", this, true);
        this.blockSwordSetting = new BooleanSetting("Block sword", this, true);
        this.usedSplashPotionSetting = new BooleanSetting("Used splash potion", this, true);
        this.holdingAWeaponSetting = new BooleanSetting("Holding a weapon", this, true);
        this.realPositionIndicatorSetting = new BooleanSetting("Real position indicator", this, true);
        this.showInFirstPersonSetting = new BooleanSetting("Show in first person", this, false);
        this.indicatorColorSetting = new ColorSetting("Indicator color", this, 0.0F, 1.0F, 1.0F, 0.39F);
        this.indicatorLineWidthSetting = new NumberSetting("Indicator line width", this, 2.0, 1.0, 5.0, 0.5);
        this.indicatorFilledSetting = new BooleanSetting("Indicator filled", this, false);
        this.handleSettingArray(
            new Setting[]{
                this.rangeSetting,
                this.maximumDelaySetting,
                this.sprintResetSetting,
                this.blockSwordSetting,
                this.usedSplashPotionSetting,
                this.holdingAWeaponSetting,
                this.realPositionIndicatorSetting,
                this.showInFirstPersonSetting,
                this.indicatorColorSetting,
                this.indicatorLineWidthSetting,
                this.indicatorFilledSetting
            }
        );
        instance = this;
    }

    private void process3() {
        if (Brutality.getLagHandler() != null) {
            this.apihHandler = new ApihHandler(InboundType.ONLY_OUTBOUND, new TimeoutHandler2(this));
            Brutality.getLagHandler().handleApihHandler(this.apihHandler);
            this.flag = true;
        }
    }

    private void process() {
        if (this.flag) {
            if (this.apihHandler != null) {
                this.apihHandler.getTimeoutHandler().process();
                this.apihHandler = null;
            }

            this.flag = false;
            this.process6();
        }
    }

    private void process4() {
        this.entityPlayer = null;
        this.amount3 = -1.0;
        this.flag = false;
        this.count = 0;
        this.count2 = 0;
        this.count3 = -1;
        this.flag2 = false;
        this.flag3 = false;
        this.apihHandler = null;
        this.process6();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                this.process5();
            } else if (event instanceof GetDamageEvent getDamageEvent) {
                if (this.flag && getDamageEvent.getEntity() == mc.thePlayer) {
                    this.process();
                }
            } else if (event instanceof EventRender3D) {
                this.onEventRender3D((EventRender3D)event);
            }
        }
    }

    private boolean checkState2() {
        return CountMovementHelper.getInstance().isFlag();
    }

    private void process6() {
        this.vec3 = null;
        this.vec32 = null;
        this.timestamp2 = 0L;
    }
}
