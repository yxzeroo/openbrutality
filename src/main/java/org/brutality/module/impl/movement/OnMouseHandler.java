package org.brutality.module.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.pit.hypixel.hud.Cake;
import org.brutality.module.impl.pit.hypixel.minorevent.DragonEgg;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.misc.ColorMathUtil;
import org.brutality.utils.rotation.OnJumpHelper;

public final class OnMouseHandler {
    private final Speed speed;
    public static OnMouseHandler instance;
    public static BooleanSetting booleanSetting;
    public static BooleanSetting booleanSetting2;
    public static BooleanSetting booleanSetting3;
    public static BooleanSetting booleanSetting4;
    public static BooleanSetting booleanSetting5;
    public static BooleanSetting booleanSetting6;
    public static BooleanSetting booleanSetting7;
    public static boolean rotating;
    private static float factor;
    private static float[] floatValueArray;

    public void process2() {
        booleanSetting = new BooleanSetting("Flying", this.speed, false).computeBooleanSetting(this.speed::checkState2);
        booleanSetting2 = new BooleanSetting("Holding blocks", this.speed, true).computeBooleanSetting(this.speed::checkState2);
        booleanSetting3 = new BooleanSetting("Holding bow", this.speed, true).computeBooleanSetting(this.speed::checkState2);
        booleanSetting4 = new BooleanSetting("Holding throwable", this.speed, true).computeBooleanSetting(this.speed::checkState2);
        booleanSetting5 = new BooleanSetting("Holding rod", this.speed, true).computeBooleanSetting(this.speed::checkState2);
        booleanSetting6 = new BooleanSetting("In liquid", this.speed, false).computeBooleanSetting(this.speed::checkState2);
        booleanSetting7 = new BooleanSetting("Targeting", this.speed, true).computeBooleanSetting(this.speed::checkState2);
    }

    public float[] computeFloatArray(float f, float f2) {
        Minecraft mc = Minecraft.getMinecraft();
        if (checkState()) {
            if (!rotating) {
                rotating = true;
                factor = 45.0F + ColorMathUtil.Entry5.computeFloat(-0.3F, 0.3F);
            }

            return new float[]{mc.thePlayer.rotationYaw + factor, mc.thePlayer.rotationPitch};
        }
        rotating = false;
        floatValueArray = null;
        return null;
    }

    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (this.speed.checkState() && RuntimeAuthGuard.checkState2() && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
        if (this.speed.checkState() && true && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                if (checkState2()) {
                    rotating = false;
                    floatValueArray = null;
                    return;
                }

                floatValueArray = this.computeFloatArray(computeMinecraft().thePlayer.rotationYaw, computeMinecraft().thePlayer.rotationPitch);
            } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE && computeFloatArray() != null) {
                ((EventPreMotion)event).handleFloat(floatValueArray[0], floatValueArray[1]);
            } else if (event instanceof BasicEvent3) {
                this.onMouse((BasicEvent3)event);
            }
        } else {
            rotating = false;
            floatValueArray = null;
        }
    }

    private static Minecraft computeMinecraft() {
        return Minecraft.getMinecraft();
    }

    private static boolean checkState() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer.onGround || !mc.thePlayer.isSprinting()) {
            return false;
        }
        if (!ColorMathUtil.Entry8.checkState2()) {
            return false;
        }
        float f = 0.0F;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            f++;
        }

        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            f--;
        }

        if (f != 0.0F) {
            return false;
        }
        if (booleanSetting.isEnabled() && mc.thePlayer.capabilities.isFlying) {
            return false;
        }
        if (booleanSetting6.isEnabled() && ColorMathUtil.Entry9.checkState5()) {
            return false;
        }
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        Item item = heldItem == null ? null : heldItem.getItem();
        if (item != null) {
            if (booleanSetting2.isEnabled() && ColorMathUtil.Entry3.matchesItemStack(heldItem)) {
                return false;
            }

            if (booleanSetting3.isEnabled() && item == Items.bow) {
                return false;
            }

            if (booleanSetting4.isEnabled() && ColorMathUtil.Entry3.matchesItemStack2(heldItem)) {
                return false;
            }

            if (booleanSetting5.isEnabled() && item == Items.fishing_rod) {
                return false;
            }
        }

        return !booleanSetting7.isEnabled() || !ColorMathUtil.Entry9.matchesInt(2);
    }

    private static boolean checkState2() {
        return ColorMathUtil.Entry9.checkState6()
            || Autoblockin.active
            || Displace.instance != null && Displace.instance.checkState6()
            || OnJumpHelper.checkState()
            || Cake.instance != null && Cake.instance.checkState44() && Cake.instance.checkState()
            || DragonEgg.instance != null && DragonEgg.instance.checkState44() && DragonEgg.instance.checkState4();
    }

    public static float[] computeFloatArray() {
        return instance != null && instance.speed.checkState() && rotating ? floatValueArray : null;
    }

    public void onDisable() {
        rotating = false;
        floatValueArray = null;
    }

    public void process() {
        rotating = false;
        floatValueArray = null;
    }

    public void onMouse(BasicEvent3 basicEvent3) {
        Minecraft mc = Minecraft.getMinecraft();
        if (rotating && basicEvent3.flag && (basicEvent3.count6 == 0 || basicEvent3.count6 == 1) && mc.entityRenderer != null) {
            float rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw;
            mc.entityRenderer.getMouseOver(1.0F);
            if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == MovingObjectType.MISS) {
                mc.thePlayer.rotationYawHead = rotationYawHead;
            }
        }
    }

    public OnMouseHandler(Speed speed) {
        this.speed = speed;
        instance = this;
        this.process2();
        speed.handleSettingArray(
            new Setting[]{booleanSetting, booleanSetting2, booleanSetting3, booleanSetting4, booleanSetting5, booleanSetting6, booleanSetting7}
        );
    }
}
