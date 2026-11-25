package org.brutality.automation.grinding.targeting;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import org.brutality.automation.grinding.movement.ObsidianPlacerHandler;
import org.brutality.automation.navigation.ClearPathHandler;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;

public class TargetingHandler {
    public static final int count = 75;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ClearPathHandler clearPathHandler = new ClearPathHandler();
    private static int count2 = 0;

    public static void handleAutoGrinder(AutoGrinder autoGrinder) {
        if (mc.thePlayer != null) {
            if (GetMysticEnchantLoreHelper.checkState5()) {
                KeybindStateUtil.process();
            } else {
                EntityPlayer player = autoGrinder.getPlayer();
                if (player == null) {
                    double d = autoGrinder.computeDouble2();
                    clearPathHandler.handleBlockPos(
                        ConnectToServerHandler.computeBlockPos(),
                        true,
                        (int)autoGrinder.yawSpeedSetting.computeDouble(),
                        (int)autoGrinder.pitchSpeedSetting.computeDouble(),
                        d
                    );
                    if (count2 == 1) {
                        mc.thePlayer.addChatMessage(new ChatComponentText("No target found, returning to PIT."));
                    }

                    count2 = 0;
                } else {
                    double d2 = autoGrinder.computeDouble();
                    ObsidianPlacerHandler.handleEntityPlayer(
                        player, false, autoGrinder.computeDouble2(), (int)autoGrinder.yawSpeedSetting.computeDouble(), (int)autoGrinder.pitchSpeedSetting.computeDouble(), d2
                    );
                    if (count2 == 0) {
                        mc.thePlayer.addChatMessage(new ChatComponentText(String.valueOf(new StringBuilder("Target acquired: ").append(player.getName()))));
                    }

                    count2 = 1;
                }
            }
        }
    }
}
