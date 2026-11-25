package org.brutality.module.impl.pit.hypixel.swap;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.brutality.util.inventory.GetHoveredSlotHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.FindIdentityHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public final class FindHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Object object;
    private static Object object2;
    private static ModeModule modeModule;
    private static ItemStack itemStack;
    private static String text;
    private static boolean flag;
    private static boolean flag2;
    private static boolean flag3;
    private static boolean flag4;
    private static boolean flag5;
    private static boolean flag6;
    private static int count = -1;
    private static int count2 = -1;
    private static long timestamp;
    private static long timestamp2;
    private static long timestamp3;
    private static long timestamp4;
    private static long timestamp5;

    public static void handleModeModule(ModeModule modeModule2) {
        if (modeModule == modeModule2) {
            flag5 = true;
        }
    }

    private static void handleModeModule2(ModeModule modeModule2) {
        int n = find(modeModule2.flag, modeModule2.useInventorySetting.isEnabled());
        if (n >= 0 && !matchesItemStack(mc.thePlayer.getCurrentArmor(1), modeModule2.flag)) {
            if (modeModule == null) {
                itemStack = mc.thePlayer.getCurrentArmor(1) == null ? null : mc.thePlayer.getCurrentArmor(1).copy();
            }

            modeModule = modeModule2;
            text = FindIdentityHelper.computeString(FindIdentityHelper.computeItemStack(n));
            flag5 = false;
            flag4 = false;
            if (modeModule2.flag) {
                timestamp2 = System.currentTimeMillis();
            } else {
                timestamp = System.currentTimeMillis();
            }

            FindIdentityHelper.handleInt(
                2, n, modeModule2.silentSetting.matchesString2("Silent"), modeModule2.safeModeSetting.isEnabled(), (long)modeModule2.delaySetting.computeDouble(), null
            );
        }
    }

    public static void tick() {
        if (mc.thePlayer != object || mc.theWorld != object2) {
            reset();
            object = mc.thePlayer;
            object2 = mc.theWorld;
        }

        if (mc.thePlayer != null && mc.theWorld != null) {
            if (mc.thePlayer.inventory != null
                && (modeModule != null || matchesModeModule(PodSwap.getInstance()) || matchesModeModule(PheonixSwap.getInstance()))) {
                if (!mc.thePlayer.isDead && !(mc.thePlayer.getHealth() <= 0.0F)) {
                    boolean flag7 = GetHoveredSlotHelper.matchesGuiScreen10(mc.currentScreen);
                    if (flag7 && !flag6) {
                        flag2 = false;
                        flag = false;
                        count2 = -1;
                        count = -1;
                    }

                    flag6 = flag7;
                    ItemStack currentArmor = mc.thePlayer.getCurrentArmor(1);
                    PotionEffect activePotionEffect = mc.thePlayer.getActivePotionEffect(Potion.regeneration);
                    int n = activePotionEffect == null ? -1 : activePotionEffect.getAmplifier();
                    if (matchesItemStack(currentArmor, false) && n >= 1 && n <= 3 && n != count) {
                        handleFlag(false);
                    }

                    count = n;
                    int n2 = matchesItemStack(currentArmor, true) && currentArmor.hasTagCompound()
                        ? currentArmor.getTagCompound().getCompoundTag("ExtraAttributes").getInteger("Lives")
                        : -1;
                    if (count2 > 0 && n2 >= 0 && n2 < count2) {
                        handleFlag(true);
                    }

                    count2 = n2;
                    if (FindIdentityHelper.checkState()) {
                        boolean flag8 = FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer);
                        if (modeModule == null || !flag5 && modeModule.checkState44() && !flag8) {
                            PheonixSwap instance = PheonixSwap.getInstance();
                            PodSwap instance2 = PodSwap.getInstance();
                            if (flag3) {
                                if (!flag8 && matchesModeModule(instance) && !flag2 && !checkState() && find(true, instance.useInventorySetting.isEnabled()) >= 0) {
                                    handleModeModule2(instance);
                                    flag3 = false;
                                    return;
                                }

                                flag3 = false;
                            }

                            if (modeModule != null) {
                                long now = System.currentTimeMillis();
                                float f = computeFloat(modeModule);
                                if (flag4 && modeModule.swapBackOnProcSetting.isEnabled()) {
                                    process2();
                                } else {
                                    if (modeModule.healthSwapBackSetting.isEnabled()
                                        && f >= modeModule.swapBackHealthSetting.computeDouble() * 2.0
                                        && now - timestamp3 >= modeModule.healthRestoreCooldownSSetting.computeDouble() * 1000.0) {
                                        timestamp3 = now;
                                        process2();
                                    }
                                }
                            } else if (!flag8 && !checkState()) {
                                boolean flag9 = matchesModeModule(instance2) && !flag && matchesFlag(false, instance2.useInventorySetting.isEnabled());
                                if (flag9) {
                                    if (matchesModeModule(instance2, timestamp)) {
                                        handleModeModule2(instance2);
                                    }
                                } else if (matchesModeModule(instance) && !flag2 && matchesModeModule(instance, timestamp2)) {
                                    handleModeModule2(instance);
                                }
                            }
                        } else {
                            process2();
                        }
                    }
                } else {
                    reset();
                }
            }
        }
    }

    private static int find(boolean flag, boolean flag2) {
        return FindIdentityHelper.computeInt(stack -> matchesItemStack(stack, flag), flag2);
    }

    private static float computeFloat(ModeModule modeModule) {
        return mc.thePlayer.getHealth() + (modeModule.ignoreAbsorptionSetting.isEnabled() ? 0.0F : mc.thePlayer.getAbsorptionAmount());
    }

    private static boolean checkState() {
        return mc.thePlayer.isPotionActive(Potion.poison);
    }

    private FindHandler() {
    }

    private static void handleFlag(boolean flag5) {
        long now = System.currentTimeMillis();
        if (now - (flag5 ? timestamp5 : timestamp4) >= (flag5 ? 1000 : 1250)) {
            if (flag5) {
                flag2 = true;
                timestamp5 = now;
            } else {
                flag = true;
                timestamp4 = now;
                flag3 = true;
            }

            if (modeModule != null && modeModule.flag == flag5) {
                flag4 = true;
            }
        }
    }

    private static void process2() {
        ItemStack currentArmor = mc.thePlayer.getCurrentArmor(1);
        if (Objects.equals(text, FindIdentityHelper.computeString(currentArmor)) || flag4 && modeModule.flag && currentArmor != null && currentArmor.getItem() == Items.iron_leggings) {
            ModeModule modeModule2 = modeModule;
            if (itemStack == null) {
                if (mc.thePlayer.inventory.getFirstEmptyStack() < 0) {
                    return;
                }

                StartHotbarMovesHelper.startArmorRemoval(2, modeModule2.safeModeSetting.isEnabled(), FindHandler::process);
            } else {
                int n = FindIdentityHelper.findIdentity(FindIdentityHelper.computeString(itemStack), true);
                if (n < 0) {
                    process();
                    return;
                }

                FindIdentityHelper.handleInt(
                    2,
                    n,
                    modeModule2.silentSetting.matchesString2("Silent"),
                    modeModule2.safeModeSetting.isEnabled(),
                    (long)modeModule2.delaySetting.computeDouble(),
                    FindHandler::process
                );
            }
        } else {
            process();
        }
    }

    public static boolean matchesItemStack(ItemStack stack, boolean flag) {
        return FindIdentityHelper.matchesItemStack(stack) && FindIdentityHelper.matchesItemStack(stack, flag ? "phoenix" : "escape-pod");
    }

    private static boolean matchesModeModule(ModeModule modeModule, long l) {
        return computeFloat(modeModule) <= modeModule.healthTriggerSetting.computeDouble() * 2.0
            && System.currentTimeMillis() - l >= modeModule.swapCooldownSSetting.computeDouble() * 1000.0
            && find(modeModule.flag, modeModule.useInventorySetting.isEnabled()) >= 0;
    }

    public static boolean matchesFlag(boolean flag, boolean flag2) {
        return mc.thePlayer != null && (matchesItemStack(mc.thePlayer.getCurrentArmor(1), flag) || find(flag, flag2) >= 0);
    }

    private static boolean matchesModeModule(ModeModule modeModule) {
        return modeModule != null && modeModule.checkState44();
    }

    private static void reset() {
        process();
        flag6 = false;
        flag2 = false;
        flag = false;
        count2 = -1;
        count = -1;
        timestamp5 = 0L;
        timestamp4 = 0L;
        timestamp3 = 0L;
        timestamp2 = 0L;
        timestamp = 0L;
    }

    public static void handleString(String str) {
        if (str != null && mc.thePlayer != null && mc.thePlayer == object) {
            if (str.startsWith("DEATH!")) {
                reset();
            } else {
                if (str.startsWith("ASHES!") && matchesItemStack(mc.thePlayer.getCurrentArmor(1), true)) {
                    handleFlag(true);
                } else if (str.startsWith("ASHES!") && modeModule != null && modeModule.flag) {
                    handleFlag(true);
                }
            }
        }
    }

    private static void process() {
        modeModule = null;
        itemStack = null;
        text = null;
        flag3 = false;
        flag5 = false;
        flag4 = false;
    }

    public static String computeString(boolean flag3) {
        if (!matchesFlag(flag3, true)) {
            return "Not On Player";
        }
        if (flag3 ? !flag2 : !flag) {
            return matchesItemStack(mc.thePlayer.getCurrentArmor(1), flag3) ? "Active" : "Ready";
        }
        return "Used";
    }
}
