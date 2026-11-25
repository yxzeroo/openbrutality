package org.brutality.module.impl.visual;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.ReleaseHelper;

public final class MotionBlur extends Module {
    private static MotionBlur instance;
    private final NumberSetting strengthSetting = new NumberSetting("Strength", this, 50.0, 1.0, 90.0, 1.0);
    private final BooleanSetting onlyWhileMovingSetting = new BooleanSetting("Only While Moving", this, false);
    private final ReleaseHelper releaseHelper = new ReleaseHelper();
    private Object object;
    private Entity entity;
    private double amount;
    private double amount2;
    private double amount3;
    private float factor;
    private float factor2;
    private int count;

    @Override
    public void onEvent(Event event) {
        if (event instanceof RenderEvent4) {
            this.process2();
        }
    }

    public static void handleFloat2(float f) {
        MotionBlur motionBlur = instance;
        if (motionBlur != null && motionBlur.checkState44()) {
            motionBlur.handleFloat(f);
        }
    }

    @Override
    public void process2() {
        this.releaseHelper.release();
        this.object = null;
        this.entity = null;
    }

    @Override
    public void onDisable() {
        this.process2();
    }

    @Override
    public void onEnable() {
        this.releaseHelper.invalidate();
        this.entity = null;
    }

    public MotionBlur() {
        super("Motion Blur", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.strengthSetting, this.onlyWhileMovingSetting});
        instance = this;
    }

    private void handleFloat(float f) {
        Object renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity == null) {
            renderViewEntity = mc.thePlayer;
        }

        if (mc.theWorld != null && mc.thePlayer != null && renderViewEntity != null && mc.currentScreen == null) {
            double d = ((Entity)renderViewEntity).lastTickPosX + (((Entity)renderViewEntity).posX - ((Entity)renderViewEntity).lastTickPosX) * f;
            double d2 = ((Entity)renderViewEntity).lastTickPosY + (((Entity)renderViewEntity).posY - ((Entity)renderViewEntity).lastTickPosY) * f;
            double d3 = ((Entity)renderViewEntity).lastTickPosZ + (((Entity)renderViewEntity).posZ - ((Entity)renderViewEntity).lastTickPosZ) * f;
            float f2 = ((Entity)renderViewEntity).prevRotationYaw + MathHelper.wrapAngleTo180_float(((Entity)renderViewEntity).rotationYaw - ((Entity)renderViewEntity).prevRotationYaw) * f;
            float f3 = ((Entity)renderViewEntity).prevRotationPitch + (((Entity)renderViewEntity).rotationPitch - ((Entity)renderViewEntity).prevRotationPitch) * f;
            double d4 = d - this.amount;
            double d5 = d2 - this.amount2;
            double d6 = d3 - this.amount3;
            double d7 = d4 * d4 + d5 * d5 + d6 * d6;
            boolean flag = this.object != mc.theWorld || this.entity != renderViewEntity || this.count != mc.gameSettings.thirdPersonView || d7 > 64.0;
            boolean flag2 = d7 > 1.0E-8 || Math.abs(MathHelper.wrapAngleTo180_float(f2 - this.factor)) > 0.001 || Math.abs(f3 - this.factor2) > 0.001;
            this.object = mc.theWorld;
            this.entity = (Entity)renderViewEntity;
            this.count = mc.gameSettings.thirdPersonView;
            this.amount = d;
            this.amount2 = d2;
            this.amount3 = d3;
            this.factor = f2;
            this.factor2 = f3;
            if (this.onlyWhileMovingSetting.isEnabled() && !flag2) {
                this.releaseHelper.invalidate();
            } else {
                this.releaseHelper.handleInt(mc.displayWidth, mc.displayHeight, this.strengthSetting.computeDouble() / 100.0, System.nanoTime(), flag);
            }
        } else {
            this.releaseHelper.invalidate();
            this.entity = null;
        }
    }
}
