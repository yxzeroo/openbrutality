package org.brutality.module.impl.visual;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.HeldItemCustomization;

public class Animations extends Module {
    public static Animations instance;
    public ModeSetting waveSetting = new ModeSetting(
        "Animations",
        this,
        "Virtue",
        Arrays.asList(
            "Virtue",
            "1.8",
            "1.7",
            "Slash",
            "Tap",
            "Spin",
            "Swong",
            "Push",
            "Stab",
            "Swank",
            "Exhibition",
            "Summer",
            "Autumn",
            "Winter",
            "Spring",
            "Punch",
            "Zoom",
            "Swing",
            "Slide",
            "Wave"
        )
    );
    public ModeSetting waveSetting2 = new ModeSetting(
        "Block Animation",
        this,
        "1.7",
        Arrays.asList(
            "1.7",
            "1.8",
            "Virtue",
            "Swong",
            "Push",
            "Stab",
            "Swank",
            "Exhibition",
            "Exhibition Tilt",
            "Interia",
            "Sigma",
            "Stella",
            "Smooth",
            "Up",
            "Claude",
            "Summer",
            "Winter",
            "Slash",
            "Tap",
            "Spin",
            "Autumn",
            "Spring",
            "Punch",
            "Zoom",
            "Swing",
            "Slide",
            "Wave"
        )
    );
    public BooleanSetting diamondShovelFakeBlockSetting = new BooleanSetting("Diamond Shovel Fake Block", this, false);
    public BooleanSetting allItemsSetting = new BooleanSetting("All Items", this, false);
    public BooleanSetting customScaleSetting = new BooleanSetting("Custom Scale", this, false);
    public NumberSetting itemScaleSetting = new NumberSetting("Item Scale", this, 1.0, 0.1, 3.0, 0.01);
    public NumberSetting scaleXSetting = new NumberSetting("Scale X", this, 1.0, 0.1, 3.0, 0.1);
    public NumberSetting scaleYSetting = new NumberSetting("Scale Y", this, 1.0, 0.1, 3.0, 0.1);
    public NumberSetting scaleZSetting = new NumberSetting("Scale Z", this, 1.0, 0.1, 3.0, 0.1);
    public BooleanSetting customPositionSetting = new BooleanSetting("Custom Position", this, false);
    public NumberSetting positionXSetting = new NumberSetting("Position X", this, 0.0, -2.0, 2.0, 0.01);
    public NumberSetting positionYSetting = new NumberSetting("Position Y", this, 0.0, -2.0, 2.0, 0.01);
    public NumberSetting positionZSetting = new NumberSetting("Position Z", this, 0.0, -2.0, 2.0, 0.01);
    public BooleanSetting customRotationSetting = new BooleanSetting("Custom Rotation", this, false);
    public NumberSetting rotationXSetting = new NumberSetting("Rotation X", this, 0.0, -180.0, 180.0, 1.0);
    public NumberSetting rotationYSetting = new NumberSetting("Rotation Y", this, 0.0, -180.0, 180.0, 1.0);
    public NumberSetting rotationZSetting = new NumberSetting("Rotation Z", this, 0.0, -180.0, 180.0, 1.0);
    public BooleanSetting customSpeedSetting = new BooleanSetting("Custom Speed", this, false);
    public NumberSetting animationSpeedSetting = new NumberSetting("Animation Speed", this, 1.0, 0.1, 5.0, 0.1);
    public BooleanSetting slowAttackSetting = new BooleanSetting("Slow Attack", this, false);
    public NumberSetting attackSpeedSetting = new NumberSetting("Attack Speed", this, 0.5, 0.1, 2.0, 0.1);
    public NumberSetting swingDurationSetting = new NumberSetting("Swing Duration", this, 6.0, 1.0, 40.0, 1.0);
    public BooleanSetting cancelReEquipSetting = new BooleanSetting("Cancel Re-Equip", this, false);
    public BooleanSetting rotationlessDrinkSetting = new BooleanSetting("Rotationless Drink", this, false);
    private final HeldItemCustomization heldItemCustomization = new HeldItemCustomization();
    public BooleanSetting blockOnlySetting = new BooleanSetting("Block Only", this, false);
    public BooleanSetting spinXAxisSetting = new BooleanSetting("Spin X Axis", this, false);
    public BooleanSetting spinYAxisSetting = new BooleanSetting("Spin Y Axis", this, true);
    public BooleanSetting spinZAxisSetting = new BooleanSetting("Spin Z Axis", this, false);
    public NumberSetting spinSpeedSetting = new NumberSetting("Spin Speed", this, 2.0, 0.1, 10.0, 0.1);
    public BooleanSetting rdPerson3Setting = new BooleanSetting("3rd Person", this, true);

    public String computeString() {
        return this.checkState44() ? this.waveSetting2.getText() : "1.7";
    }

    public float computeFloat() {
        return this.computeFloat(0.0F);
    }

    @Override
    public void process2() {
        this.heldItemCustomization.reset();
    }

    public void handleEntityLivingBase(EntityLivingBase living) {
        if (living == mc.thePlayer && this.matchesItemStack2(living.getHeldItem()) && this.checkState4()) {
            this.heldItemCustomization.swing(living.ticksExisted, this.computeInt());
        }
    }

    public float computeFloat3() {
        return this.slowAttackSetting.isEnabled() ? (float)this.attackSpeedSetting.computeDouble() : 1.0F;
    }

    public int computeInt() {
        return Math.max(1, Math.round((float)this.swingDurationSetting.computeDouble() / this.computeFloat2()));
    }

    public Animations() {
        super("Animations", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.waveSetting,
                this.waveSetting2,
                this.allItemsSetting,
                this.diamondShovelFakeBlockSetting,
                this.customScaleSetting,
                this.itemScaleSetting,
                this.scaleXSetting,
                this.scaleYSetting,
                this.scaleZSetting,
                this.customPositionSetting,
                this.positionXSetting,
                this.positionYSetting,
                this.positionZSetting,
                this.customRotationSetting,
                this.rotationXSetting,
                this.rotationYSetting,
                this.rotationZSetting,
                this.customSpeedSetting,
                this.animationSpeedSetting,
                this.slowAttackSetting,
                this.attackSpeedSetting,
                this.swingDurationSetting,
                this.cancelReEquipSetting,
                this.rotationlessDrinkSetting,
                this.blockOnlySetting,
                this.spinXAxisSetting,
                this.spinYAxisSetting,
                this.spinZAxisSetting,
                this.spinSpeedSetting,
                this.rdPerson3Setting
            }
        );
        instance = this;
    }

    @Generated
    public static Animations getInstance() {
        return instance;
    }

    public boolean checkState() {
        return this.blockOnlySetting.isEnabled();
    }

    public boolean checkState2() {
        return this.allItemsSetting.isEnabled();
    }

    public boolean matchesItemStack(ItemStack stack) {
        KillAura instance = KillAura.instance;
        EntityLivingBase entityLivingBase2 = KillAura.entityLivingBase2;
        return this.checkState44()
            && this.diamondShovelFakeBlockSetting.isEnabled()
            && stack != null
            && stack.getItem() == Items.diamond_shovel
            && instance != null
            && instance.checkState44()
            && entityLivingBase2 != null
            && entityLivingBase2.isEntityAlive();
    }

    public void process3() {
    }

    private boolean checkState4() {
        return this.customSpeedSetting.isEnabled() || this.slowAttackSetting.isEnabled() || this.swingDurationSetting.computeDouble() != 6.0;
    }

    public float computeFloat(EntityLivingBase living, float f, float f2) {
        return living == mc.thePlayer && this.matchesItemStack2(living.getHeldItem()) && this.checkState4()
            ? this.heldItemCustomization.progress(living.ticksExisted, f)
            : f2;
    }

    public boolean checkState5() {
        return this.rdPerson3Setting.isEnabled();
    }

    public boolean matchesItemStack2(ItemStack stack) {
        return this.checkState44()
            && stack != null
            && !(stack.getItem() instanceof ItemMap)
            && (
                this.allItemsSetting.isEnabled()
                    || stack.getItem() instanceof ItemSword
                    || this.diamondShovelFakeBlockSetting.isEnabled() && stack.getItem() == Items.diamond_shovel
            );
    }

    public void process() {
        if (this.checkState44()) {
            float f = this.customScaleSetting.isEnabled() ? (float)this.itemScaleSetting.computeDouble() : 1.0F;
            HeldItemCustomization.apply(
                this.customPositionSetting.isEnabled() ? (float)this.positionXSetting.computeDouble() : 0.0F,
                this.customPositionSetting.isEnabled() ? (float)this.positionYSetting.computeDouble() : 0.0F,
                this.customPositionSetting.isEnabled() ? (float)this.positionZSetting.computeDouble() : 0.0F,
                this.customRotationSetting.isEnabled() ? (float)this.rotationXSetting.computeDouble() : 0.0F,
                this.customRotationSetting.isEnabled() ? (float)this.rotationYSetting.computeDouble() : 0.0F,
                this.customRotationSetting.isEnabled() ? (float)this.rotationZSetting.computeDouble() : 0.0F,
                f * (this.customScaleSetting.isEnabled() ? (float)this.scaleXSetting.computeDouble() : 1.0F),
                f * (this.customScaleSetting.isEnabled() ? (float)this.scaleYSetting.computeDouble() : 1.0F),
                f * (this.customScaleSetting.isEnabled() ? (float)this.scaleZSetting.computeDouble() : 1.0F)
            );
        }
    }

    @Override
    public void onDisable() {
        this.heldItemCustomization.reset();
        super.onDisable();
    }

    public float computeFloat(float f) {
        return mc.thePlayer == null ? 0.0F : (float)((mc.thePlayer.ticksExisted + f) * this.spinSpeedSetting.computeDouble() % 360.0);
    }

    public float computeFloat2() {
        float f = this.customSpeedSetting.isEnabled() ? (float)this.animationSpeedSetting.computeDouble() : 1.0F;
        if (this.slowAttackSetting.isEnabled()) {
            f *= (float)this.attackSpeedSetting.computeDouble();
        }

        return f;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof RenderEvent4) {
            this.process2();
        }
    }
}
