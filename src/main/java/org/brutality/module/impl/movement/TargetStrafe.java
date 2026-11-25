package org.brutality.module.impl.movement;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.MovementEvent4;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.WHelper;
import org.brutality.utils.player.GetSpeedHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public final class TargetStrafe extends Module {
    public static TargetStrafe instance;
    private final BooleanSetting requireSpaceSetting = new BooleanSetting("Require Space", this, false);
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final BooleanSetting drawCircleSetting = new BooleanSetting("Draw Circle", this, true);
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 2.0, 1.0, 5.0, 0.1);
    private final ModeSetting rightSetting = new ModeSetting("Direction", this, "Auto", Arrays.asList("Auto", "Left", "Right"));
    public float angle = 0.0F;
    private int count = 1;

    private void handleDouble(double d, double d2, double d3, double d4) {
        byte b = 64;
        float f = 2.0F;
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(f);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i < b; i++) {
            double d5 = (Math.PI * 2) * i / b;
            double d6 = d4 * Math.cos(d5);
            double d7 = d4 * Math.sin(d5);
            if (this.count == 1) {
                GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.8F);
            } else {
                GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.8F);
            }

            GL11.glVertex3d(d + d6, d2 + 0.01, d3 + d7);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }

    public TargetStrafe() {
        super("TargetStrafe", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.rangeSetting, this.rightSetting, this.drawCircleSetting, this.requireSpaceSetting, this.kosOnlySetting});
        instance = this;
    }

    private void process3() {
        if (this.drawCircleSetting.isEnabled()) {
            EntityLivingBase entityLivingBase2 = KillAura.entityLivingBase2;
            if (entityLivingBase2 != null) {
                double d = this.rangeSetting.computeDouble();
                double d2 = entityLivingBase2.lastTickPosX + (entityLivingBase2.posX - entityLivingBase2.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
                double d3 = entityLivingBase2.lastTickPosY + (entityLivingBase2.posY - entityLivingBase2.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
                double d4 = entityLivingBase2.lastTickPosZ + (entityLivingBase2.posZ - entityLivingBase2.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;
                this.handleDouble(d2, d3, d4, d);
            }
        }
    }

    public boolean checkState() {
        return this.checkState44() && this.angle > 0.0F;
    }

    @Generated
    public static TargetStrafe getInstance() {
        return instance;
    }

    public void process() {
        Module module = Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule("AutoHunt");
        if (module != null && module.checkState44()) {
            this.angle = 0.0F;
        } else {
            EntityLivingBase entityLivingBase2 = KillAura.entityLivingBase2;
            boolean keyDown = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
            this.angle = 0.0F;
            if (entityLivingBase2 != null && mc.thePlayer != null && GetSpeedHelper.isMoving()) {
                if (!this.kosOnlySetting.isEnabled() || entityLivingBase2 instanceof EntityPlayer && WHelper.matchesString(entityLivingBase2.getName())) {
                    if (!this.requireSpaceSetting.isEnabled() || keyDown) {
                        this.angle = 1.0F;
                        float f = (float)this.rangeSetting.computeDouble() - 0.25F;
                        double d = entityLivingBase2.posX - mc.thePlayer.posX;
                        double d2 = entityLivingBase2.posZ - mc.thePlayer.posZ;
                        if (!(Math.sqrt(d * d + d2 * d2) < 0.01)) {
                            double d3 = Math.abs(Math.sqrt(d * d + d2 * d2) - f) > 0.5
                                ? Math.sqrt(d * d + d2 * d2)
                                : Math.sqrt(d * d + d2 * d2) - f;
                            if (this.rightSetting.matchesString2("Left")) {
                                this.count = -1;
                            } else if (this.rightSetting.matchesString2("Right")) {
                                this.count = 1;
                            }

                            double rad = Math.toRadians(Math.toDegrees(Math.atan2(d2, d)) + this.count * 90);
                            double rad2 = Math.toRadians(Math.toDegrees(Math.atan2(d2, d)));
                            double d4 = Math.cos(rad) + d3 * Math.cos(rad2);
                            double d5 = Math.sin(rad) + d3 * Math.sin(rad2);
                            float rad3 = (float)Math.toRadians(mc.thePlayer.rotationYaw);
                            float f2 = (float)(d5 * Math.cos(rad3) - d4 * Math.sin(rad3));
                            float f3 = (float)(d4 * Math.cos(rad3) + d5 * Math.sin(rad3));
                            mc.thePlayer.movementInput.moveForward = f2;
                            mc.thePlayer.movementInput.moveStrafe = f3;
                            if (mc.thePlayer.isCollidedHorizontally) {
                                this.count = this.count == 1 ? -1 : 1;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventRender3D) {
                    this.process3();
                } else if (event instanceof MovementEvent4) {
                    this.process();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.angle = 0.0F;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.angle = 0.0F;
        this.count = 1;
    }
}
