package org.brutality.automation.grinding.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.brutality.automation.grinding.movement.ObsidianPlacerHandler;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.event.impl.pit.PitEvent16;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.FloorHelper;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.PartyWarpType;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.UtilHelper6;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.math.BuildTargetComparatorHelper;

public class OnSpireFloorChangeHandler {
    private final AutoGrinder autoGrinder;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final StopwatchTimer utilHelper82 = new StopwatchTimer();
    private int count;
    private boolean flag;
    private boolean flag2;
    private static final long timestamp = 3562051893104476460L;
    private static final long timestamp2 = 4000L;

    public OnSpireFloorChangeHandler(AutoGrinder autoGrinder) {
        this.count = (int)timestamp;
        this.autoGrinder = autoGrinder;
    }

    public void onSpireFloorChange(PitEvent16 pitEvent16) {
        this.count = 75 + (int)(Math.random() * 426.0);
        this.utilHelper82.reset();
        ExecuteCommandHelper.handleString6("Spire floor changed, re-enabling attacking");
    }

    public void process() {
        if (this.mc.thePlayer != null) {
            int n = ConnectToServerHandler.computeInt5();
            if (n == -1) {
                ExecuteCommandHelper.handleString6("Major event time remaining is -1, not oofing");
            }

            String str = FloorHelper.computeString();
            boolean flag = str != null && str.equalsIgnoreCase("two") && n > 100;
            if (flag) {
                if (this.utilHelper8.matchesLong(timestamp2)) {
                    this.utilHelper8.reset();
                    this.autoGrinder.process22();
                    this.autoGrinder.handleInt(4200);
                    PartyWarpType.OOF.matchesInt(0, "Oofing on floor 2 until 100 seconds remain");
                }

                KeybindStateUtil.process();
                this.flag = true;
            } else {
                boolean flag2 = ExecuteCommandHelper.computeString6(GetDisplayedTitleHelper.getDisplayedTitle().toLowerCase()).trim().contains("respawning");
                boolean flag3 = GetMysticEnchantLoreHelper.checkState5()
                    || this.mc.thePlayer.isPotionActive(14)
                    || flag2
                    || !this.utilHelper82.matchesLong(this.count);
                if (flag3) {
                    KeybindStateUtil.process();
                    this.autoGrinder.process22();
                    this.autoGrinder.handleInt(150);
                    this.flag2 = true;
                } else {
                    EntityPlayer player = this.autoGrinder.getPlayer();
                    double d = this.autoGrinder.computeDouble();
                    int n2 = (int)this.autoGrinder.yawSpeedSetting.computeDouble();
                    int n3 = (int)this.autoGrinder.pitchSpeedSetting.computeDouble();
                    double d2 = this.autoGrinder.computeDouble2();
                    if (player == null) {
                        KeybindStateUtil.process();
                    } else {
                        if (this.flag) {
                            this.autoGrinder.process40();
                            if (this.autoGrinder.checkState5()) {
                                this.autoGrinder.process41();
                            }

                            this.flag = false;
                        }

                        if (this.flag2) {
                            this.autoGrinder.process40();
                            if (this.autoGrinder.checkState5()) {
                                this.autoGrinder.process41();
                            }

                            this.flag2 = false;
                        }

                        float f = MathHelper.wrapAngleTo180_float(
                            org.brutality.util.math.MathHelper.computeFloatArray(player.posX, player.posY, player.posZ)[0] - this.mc.thePlayer.rotationYaw
                        );
                        double dist = this.mc.thePlayer.getDistanceToEntity(player);
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindLeft.getKeyCode(), false);
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindRight.getKeyCode(), false);
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindBack.getKeyCode(), false);
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSneak.getKeyCode(), false);
                        BuildTargetComparatorHelper.handleEntity(player, n2, n3);
                        ObsidianPlacerHandler.handleFlag(Math.random() < d2);
                        AutoGrinder.process46();
                        ObsidianPlacerHandler.handleEntityPlayer(player, d, f, dist);
                        if (dist >= 1.0 && dist <= 5.5 && UtilHelper6.matchesEntityPlayer(player, 40.0F, 22.0F)) {
                            ObsidianPlacerHandler.handleFlag(false);
                        }

                        if (dist <= 1.0) {
                            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), false);
                        }
                    }
                }
            }
        }
    }
}
