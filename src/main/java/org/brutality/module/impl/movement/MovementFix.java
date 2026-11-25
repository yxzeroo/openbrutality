package org.brutality.module.impl.movement;

import java.io.PrintStream;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.GetYawEvent;
import org.brutality.event.impl.movement.GetYawEvent2;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.client.ClientSettings;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.pit.hypixel.hud.Cake;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.module.impl.pit.hypixel.minorevent.DragonEgg;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.module.impl.player.BedAura;
import org.brutality.module.impl.player.ChestAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.rotation.OnJumpHelper;

public final class MovementFix extends Module {
    public static MovementFix instance;
    private final BooleanSetting onlyWhenRotatingSetting = new BooleanSetting("Only When Rotating", this, true);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private static int count = Integer.MIN_VALUE;
    private static Object object;
    private static Object object2;
    private static boolean flag;
    private static boolean flag2 = false;
    private static boolean flag3 = false;
    private static Float floatValue = null;
    private static Float floatValue2 = null;
    private static Float floatValue3 = null;
    private static float factor = 0.0F;
    private static float factor2 = 0.0F;
    private static boolean flag4 = false;
    private static int count2 = 0;
    private static boolean flag5 = false;
    private static boolean flag6;

    private static void process4() {
        if (!checkState5()) {
            factor = 0.0F;
            factor2 = 0.0F;
        } else if (floatValue != null) {
            float moveForward = mc.thePlayer.movementInput.moveForward;
            float moveStrafe = mc.thePlayer.movementInput.moveStrafe;
            if (moveForward == 0.0F && moveStrafe == 0.0F) {
                factor = 0.0F;
                factor2 = 0.0F;
            } else {
                float f = MathHelper.wrapAngleTo180_float(floatValue);
                float f2 = computeFloat(mc.thePlayer.rotationYaw, moveForward, moveStrafe);
                float f3 = MathHelper.wrapAngleTo180_float(f2 - f + 22.5F);
                float f4 = 0.0F;
                float f5 = 0.0F;
                switch ((int)(f3 + 180.0F) / 45 % 8) {
                    case 0:
                        f4 = -1.0F;
                        f5 = 0.0F;
                        break;
                    case 1:
                        f4 = -1.0F;
                        f5 = 1.0F;
                        break;
                    case 2:
                        f4 = 0.0F;
                        f5 = 1.0F;
                        break;
                    case 3:
                        f4 = 1.0F;
                        f5 = 1.0F;
                        break;
                    case 4:
                        f4 = 1.0F;
                        f5 = 0.0F;
                        break;
                    case 5:
                        f4 = 1.0F;
                        f5 = -1.0F;
                        break;
                    case 6:
                        f4 = 0.0F;
                        f5 = -1.0F;
                        break;
                    case 7:
                        f4 = -1.0F;
                        f5 = -1.0F;
                }

                factor = f4;
                factor2 = f5;
                if (mc.thePlayer.movementInput.sneak) {
                    f4 *= 0.3F;
                    f5 *= 0.3F;
                }

                mc.thePlayer.movementInput.moveForward = f4;
                mc.thePlayer.movementInput.moveStrafe = f5;
            }
        }
    }

    private static boolean checkState() {
        return isEnabled();
    }

    public static void process3() {
        floatValue = null;
        floatValue2 = null;
        flag3 = false;
        OnJumpHelper.floatValue = null;
        OnJumpHelper.floatValue2 = null;
    }

    private static float computeFloat(float f, float f2, float f3) {
        if (f2 < 0.0F) {
            f += 180.0F;
        }

        if (f3 != 0.0F) {
            float f4 = f2 == 0.0F ? 1.0F : 0.5F * Math.signum(f2);
            f += -90.0F * f4 * Math.signum(f3);
        }

        return MathHelper.wrapAngleTo180_float(f);
    }

    public static void onEvent2(Event event) {
        if (event instanceof EventPreMotion
            || event instanceof EventUpdate
            || event instanceof MovementEvent5
            || event instanceof GetYawEvent
            || event instanceof GetYawEvent2) {
            process6();
            if (isEnabled()) {
                if (event instanceof EventPreMotion) {
                    onEventPreMotion((EventPreMotion)event);
                } else if (event instanceof EventUpdate) {
                    if (((EventUpdate)event).getState() == EventState.PRE) {
                        process();
                    }
                } else if (event instanceof MovementEvent5) {
                    onMovementEvent5((MovementEvent5)event);
                } else if (event instanceof GetYawEvent) {
                    onGetYawEvent((GetYawEvent)event);
                } else if (event instanceof GetYawEvent2) {
                    onGetYawEvent2((GetYawEvent2)event);
                }
            } else {
                if (flag3 || flag5 || floatValue3 != null) {
                    flag3 = false;
                    floatValue = null;
                    floatValue2 = null;
                    floatValue3 = null;
                    flag5 = false;
                    factor = 0.0F;
                    factor2 = 0.0F;
                }
            }
        }
    }

    public static Float computeFloatType2() {
        return checkState5() ? floatValue : null;
    }

    public static boolean checkState6() {
        return checkState5();
    }

    public static Float computeFloatType3() {
        return checkState5() ? floatValue : null;
    }

    public static boolean isEnabled() {
        return flag6
            ? false
            : NoSlowDown.computeFloatArray() != null
                || OnMouseHandler.computeFloatArray() != null
                || RightClickBlockPlace.computeFloatArray() != null
                || (instance != null ? instance.checkState44() : ClientSettings.getInstance() != null && ClientSettings.checkState8());
    }

    private static void onEventPreMotion(EventPreMotion event) {
        Float val = floatValue3;
        floatValue3 = null;
        float[] floats = RightClickBlockPlace.computeFloatArray();
        if (floats != null) {
            event.setYaw(floats[0]);
            event.setPitch(floats[1]);
            handleEventPreMotion(event, val);
        } else if (!checkState5() && val == null) {
            handleEventPreMotion(event, val);
        } else if (Scaffold.computeFloatArray() != null) {
            handleEventPreMotion(event, val);
        } else {
            Float val2 = val != null ? val : floatValue;
            if (val2 != null && !val2.isNaN()) {
                if (val != null) {
                    event.handleFloat(val2);
                } else {
                    event.setYaw(val2);
                }
            }

            if (floatValue2 != null && !floatValue2.isNaN()) {
                event.setPitch(floatValue2);
            }

            handleEventPreMotion(event, val);
        }
    }

    private static void onGetYawEvent2(GetYawEvent2 getYawEvent2) {
        if (checkState5()) {
            if (floatValue != null) {
                getYawEvent2.setYaw(floatValue);
            }
        }
    }

    public static boolean matchesFlag(boolean flag) {
        return !checkState5() ? flag : factor >= 0.8F;
    }

    private static void process() {
        floatValue3 = null;
        boolean flag2 = flag3 && floatValue != null;
        if (!checkState()) {
            floatValue2 = null;
            floatValue = null;
            flag3 = false;
            flag5 = false;
            OnJumpHelper.floatValue = null;
            OnJumpHelper.floatValue2 = null;
            if (flag2) {
            }
        } else {
            Float val = null;
            Float val2 = null;
            float[] floats = Autoblockin.computeFloatArray();
            if (floats != null) {
                val = floats[0];
                val2 = floats[1];
            }

            float[] floats2 = BedAura.computeFloatArray();
            if (val == null && floats2 != null) {
                val = floats2[0];
                val2 = floats2[1];
            }

            if (val == null && Displace.instance != null && Displace.instance.checkState44() && Displace.instance.checkState6()) {
                Float val3 = Displace.instance.computeFloatType();
                if (val3 != null) {
                    val = val3;
                    val2 = Displace.instance.getFactor();
                }
            }

            float[] floats3 = OnMouseHandler.computeFloatArray();
            if (val == null && floats3 != null) {
                val = floats3[0];
                val2 = floats3[1];
            }

            float[] floats4 = RightClickBlockPlace.computeFloatArray();
            if (val == null && floats4 != null) {
                val = floats4[0];
                val2 = floats4[1];
            }

            if (val == null
                && KillAura.getInstance() != null
                && KillAura.getInstance().checkState44()
                && (OnJumpHelper.checkState() || KillAura.checkState48())
                && KillAura.getInstance().floatValueArray5 != null) {
                val = KillAura.getInstance().floatValueArray5[0];
                val2 = KillAura.getInstance().floatValueArray5[1];
            }

            if (val == null
                && Cake.instance != null
                && Cake.instance.checkState44()
                && Cake.instance.checkState()
                && Cake.instance.getFloatValueArray() != null) {
                float[] floatValueArray = Cake.instance.getFloatValueArray();
                val = floatValueArray[0];
                val2 = floatValueArray[1];
            }

            if (val == null && CarePackage.instance != null && CarePackage.instance.checkState44() && CarePackage.instance.checkState2()) {
                float[] floatValueArray2 = CarePackage.instance.getFloatValueArray();
                val = floatValueArray2[0];
                val2 = floatValueArray2[1];
            }

            if (val == null
                && ChestAura.instance != null
                && ChestAura.instance.checkState44()
                && ChestAura.instance.checkState2()
                && ChestAura.instance.getFloatValueArray() != null) {
                float[] floatValueArray3 = ChestAura.instance.getFloatValueArray();
                val = floatValueArray3[0];
                val2 = floatValueArray3[1];
            }

            if (val == null
                && DragonEgg.instance != null
                && DragonEgg.instance.checkState44()
                && DragonEgg.instance.checkState4()
                && DragonEgg.instance.getFloatValueArray() != null) {
                float[] floatValueArray4 = DragonEgg.instance.getFloatValueArray();
                val = floatValueArray4[0];
                val2 = floatValueArray4[1];
            }

            if (val == null) {
                float[] floats5 = Scaffold.computeFloatArray();
                if (floats5 != null) {
                    val = floats5[0];
                    val2 = floats5[1];
                }
            }

            if (val == null && OnJumpHelper.checkState() && OnJumpHelper.floatValueArray2 != null) {
                val = OnJumpHelper.floatValueArray2[0];
                val2 = OnJumpHelper.floatValueArray2[1];
            }

            if (val == null) {
                float[] floats6 = NoSlowDown.computeFloatArray();
                if (floats6 != null) {
                    val = floats6[0];
                    val2 = floats6[1];
                }
            }

            boolean flag6 = val != null && !flag2;
            flag = val == null && instance != null && !instance.onlyWhenRotatingSetting.isEnabled() && mc.thePlayer != null;
            if (flag) {
                val = mc.thePlayer.rotationYaw;
                val2 = mc.thePlayer.rotationPitch;
            }

            floatValue = val;
            floatValue2 = val2;
            flag3 = val != null;
            if (mc.thePlayer != null) {
                boolean sprinting = mc.thePlayer.isSprinting();
                if (sprinting != flag4) {
                    count2 = mc.thePlayer.ticksExisted;
                    flag4 = sprinting;
                }
            }

            flag5 = true;
            OnJumpHelper.floatValue = val;
            OnJumpHelper.floatValue2 = val2;
            if (flag6) {
            }
        }
    }

    private static void handleEventPreMotion(EventPreMotion event, Float val) {
        if (instance != null && instance.debugSetting.isEnabled() && mc.thePlayer != null) {
            int ticksExisted = mc.thePlayer.ticksExisted;
            if (count == Integer.MIN_VALUE || ticksExisted < count || ticksExisted - count >= 40) {
                count = ticksExisted;
                IAttributeInstance entityAttribute = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                PotionEffect activePotionEffect = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
                String str = entityAttribute == null ? "?" : entityAttribute.func_111122_c().toString();
                PrintStream out = System.out;
                boolean flag2 = checkState5();
                boolean flag3 = flag;
                float yaw = mc.thePlayer.rotationYaw;
                float yaw2 = event.getYaw();
                boolean sprinting = mc.thePlayer.isSprinting();
                var val2 = entityAttribute == null ? "?" : entityAttribute.getAttributeValue();
                int n = activePotionEffect == null ? 0 : activePotionEffect.getAmplifier() + 1;
                var val3 = val2;
                boolean flag4 = sprinting;
                float f = yaw2;
                float f2 = yaw;
                boolean flag5 = flag3;
                boolean flag6 = flag2;
                out.println(
                    "[MovementFix] tick="
                        + ticksExisted
                        + " active="
                        + flag6
                        + " cameraFallback="
                        + flag5
                        + " cameraYaw="
                        + f2
                        + " moveYaw="
                        + val
                        + " packetYaw="
                        + f
                        + " sprint="
                        + flag4
                        + " speed="
                        + val3
                        + " speedPotion="
                        + n
                        + " modifiers="
                        + str
                );
            }
        }
    }

    public static boolean isFlag6() {
        return flag6;
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onDisable() {
        process5();
    }

    private static void process6() {
        if (mc == null || mc.thePlayer == null || mc.theWorld == null || object != mc.thePlayer || object2 != mc.theWorld) {
            process5();
        }
    }

    private static void onGetYawEvent(GetYawEvent getYawEvent) {
        if (flag && mc.thePlayer != null) {
            floatValue = mc.thePlayer.rotationYaw;
            floatValue2 = mc.thePlayer.rotationPitch;
        }

        if (checkState5()) {
            if (floatValue != null) {
                getYawEvent.setYaw(floatValue);
                floatValue3 = floatValue;
            }
        }
    }

    public static boolean checkState4() {
        return count2 == mc.thePlayer.ticksExisted;
    }

    @Override
    public void process2() {
        process5();
    }

    public static MovementFix getInstance() {
        return instance;
    }

    public static void process5() {
        process3();
        floatValue3 = null;
        flag5 = false;
        flag6 = false;
        factor2 = 0.0F;
        factor = 0.0F;
        flag4 = false;
        count2 = -1;
        flag2 = false;
        flag = false;
        count = Integer.MIN_VALUE;
        object = mc == null ? null : mc.thePlayer;
        object2 = mc == null ? null : mc.theWorld;
    }

    public static double computeDouble(float f, double d, double d2) {
        if (d < 0.0) {
            f += 180.0F;
        }

        float f2 = 1.0F;
        if (d < 0.0) {
            f2 = -0.5F;
        } else if (d > 0.0) {
            f2 = 0.5F;
        }

        if (d2 > 0.0) {
            f -= 90.0F * f2;
        }

        if (d2 < 0.0) {
            f += 90.0F * f2;
        }

        return Math.toRadians(f);
    }

    private static void onMovementEvent5(MovementEvent5 movementEvent5) {
        if (flag && mc.thePlayer != null) {
            floatValue = mc.thePlayer.rotationYaw;
            floatValue2 = mc.thePlayer.rotationPitch;
        }

        if (!checkState5()) {
            factor = movementEvent5.getFactor();
            factor2 = movementEvent5.getFactor2();
        } else if (floatValue != null && (movementEvent5.getFactor() != 0.0F || movementEvent5.getFactor2() != 0.0F)) {
            float factor3 = movementEvent5.getFactor();
            float factor22 = movementEvent5.getFactor2();
            if (flag) {
                factor = factor3;
                factor2 = factor22;
            } else {
                float f = Math.max(Math.abs(factor3), Math.abs(factor22));
                float f2 = MathHelper.wrapAngleTo180_float(floatValue);
                float f3 = computeFloat(mc.thePlayer.rotationYaw, factor3, factor22);
                float f4 = MathHelper.wrapAngleTo180_float(f3 - f2 + 22.5F);
                float f5 = 0.0F;
                float f6 = 0.0F;
                switch ((int)(f4 + 180.0F) / 45 % 8) {
                    case 0:
                        f5 = -1.0F;
                        break;
                    case 1:
                        f5 = -1.0F;
                        f6 = 1.0F;
                        break;
                    case 2:
                        f6 = 1.0F;
                        break;
                    case 3:
                        f5 = 1.0F;
                        f6 = 1.0F;
                        break;
                    case 4:
                        f5 = 1.0F;
                        break;
                    case 5:
                        f5 = 1.0F;
                        f6 = -1.0F;
                        break;
                    case 6:
                        f6 = -1.0F;
                        break;
                    case 7:
                        f5 = -1.0F;
                        f6 = -1.0F;
                }

                factor = f5 * f;
                factor2 = f6 * f;
                movementEvent5.setFactor(factor);
                movementEvent5.setFactor2(factor2);
            }
        } else {
            factor = movementEvent5.getFactor();
            factor2 = movementEvent5.getFactor2();
        }
    }

    private static boolean checkState5() {
        process6();
        if (!isEnabled()) {
            return false;
        }
        if (!flag3) {
            return false;
        }
        if (floatValue == null) {
            return false;
        }
        if (!Float.isFinite(floatValue)) {
            return false;
        }
        return instance != null && !instance.onlyWhenRotatingSetting.isEnabled()
            ? true
            : BedAura.computeFloatArray() != null
                || NoSlowDown.computeFloatArray() != null
                || Autoblockin.computeFloatArray() != null
                || OnMouseHandler.computeFloatArray() != null
                || RightClickBlockPlace.computeFloatArray() != null
                || Displace.instance != null && Displace.instance.checkState44() && Displace.instance.checkState6()
                || KillAura.getInstance() != null
                    && KillAura.getInstance().checkState44()
                    && (OnJumpHelper.checkState() || KillAura.checkState48())
                    && KillAura.getInstance().floatValueArray5 != null
                || Cake.instance != null && Cake.instance.checkState44() && Cake.instance.checkState()
                || CarePackage.instance != null && CarePackage.instance.checkState44() && CarePackage.instance.checkState2()
                || ChestAura.instance != null && ChestAura.instance.checkState44() && ChestAura.instance.checkState2()
                || DragonEgg.instance != null && DragonEgg.instance.checkState44() && DragonEgg.instance.checkState4()
                || Scaffold.computeFloatArray() != null
                || OnJumpHelper.checkState();
    }

    public static void handleFlag(boolean flag) {
        flag6 = flag;
        flag2 = flag;
    }

    public static void handleFloat(float floatValue3, float floatValue22) {
        if (isEnabled()) {
            floatValue = floatValue3;
            floatValue2 = floatValue22;
            flag3 = true;
            if (flag5) {
                OnJumpHelper.floatValue = floatValue3;
                OnJumpHelper.floatValue2 = floatValue22;
            }
        }
    }

    public static boolean checkState2() {
        return checkState5();
    }

    public static Float computeFloatType() {
        return checkState5() ? floatValue2 : null;
    }

    public MovementFix() {
        super("MovementFix", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.onlyWhenRotatingSetting, this.debugSetting});
        instance = this;
        this.toggled = true;
    }
}
