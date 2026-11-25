package org.brutality.automation.grinding.purchases;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import org.brutality.core.ExtraAttributesHandler;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.GetNonBotPlayersHelper;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.inventory.GetHoveredSlotHelper;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.inventory.HelmetHelper;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.MessageUtils;

public class ClearPathHandler {
    private static final double amount = 1.75;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private static int count = 0;
    private static final StopwatchTimer utilHelper82 = new StopwatchTimer(0L);
    private static final org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();

    public static void clearPath() {
        clearPathHandler.clearPath();
    }

    public static void handleAutoGrinder(AutoGrinder autoGrinder) {
        BlockPos pos = GetNonBotPlayersHelper.computeBlockPos5();
        if (pos != null) {
            if (mc.thePlayer != null) {
                GuiScreen screen = mc.currentScreen;
                if (!GetMysticEnchantLoreHelper.checkState2()) {
                    if (utilHelper8.matchesLong(1000L)) {
                        if (GetHoveredSlotHelper.matchesGuiScreen3(screen)) {
                            MessageUtils.x("Prestige container 1!");
                            HelmetHelper.handleInt2(
                                !GetHoveredSlotHelper.matchesGuiScreen3(mc.currentScreen) ? -1 : GetHoveredSlotHelper.computeInt("prestige")
                            );
                            utilHelper8.reset();
                        } else if (GetHoveredSlotHelper.matchesGuiScreen12(screen)) {
                            if (!utilHelper8.matchesLong(7500L)) {
                                return;
                            }

                            MessageUtils.x("Prestige container 2!");
                            int n = GetHoveredSlotHelper.computeInt("are you sure?");
                            MessageUtils.x(String.valueOf(new StringBuilder("Clicking slot ID: ").append(n)));
                            HelmetHelper.handleInt2(n);
                            utilHelper8.reset();
                            autoGrinder.flag8 = true;
                            autoGrinder.process7();
                            int n2 = count + 1;
                            count = n2;
                            if (n2 >= 3) {
                                autoGrinder.handleString5("Hit max prestige attempts, assuming we can't prestige. Will try again in 45 minutes.");
                                utilHelper82.reset();
                                autoGrinder.flag8 = true;
                            }
                        } else if (GetMysticEnchantLoreHelper.checkState5()) {
                            mc.thePlayer.closeScreen();
                            utilHelper8.reset();
                            return;
                        }

                        if (mc.currentScreen == null) {
                            EntityPlayerSP player = mc.thePlayer;
                            double x = pos.getX();
                            double y = pos.getY();
                            double dist = player.getDistance(x, y, pos.getZ());
                            if (dist <= 1.75) {
                                BuildTargetComparatorHelper.handleBlockPos(pos, 40, 17);
                                AutoGrinder.process27();
                                clearPathHandler.process2();
                                utilHelper8.reset();
                            } else {
                                clearPathHandler.handleBlockPos(pos, 40, 17, 0.3F);
                                ExtraAttributesHandler.process2();
                                if (dist <= 5.0) {
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean checkState() {
        return !utilHelper82.matchesLong(2700000L);
    }

    public static void process() {
        count = 0;
        utilHelper82.clear();
    }
}
