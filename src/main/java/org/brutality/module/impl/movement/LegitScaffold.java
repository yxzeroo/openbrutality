package org.brutality.module.impl.movement;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.PlayerUtils;
import org.lwjgl.input.Mouse;

public class LegitScaffold extends Module {
    private final NumberSetting minDelaySetting = new NumberSetting("Min Delay", this, 2.0, 0.0, 10.0, 1.0);
    private final NumberSetting maxDelaySetting = new NumberSetting("Max Delay", this, 3.0, 0.0, 10.0, 1.0);
    private final BooleanSetting onlyGroundSetting = new BooleanSetting("Only Ground", this, true);
    private final BooleanSetting blocksOnlySetting = new BooleanSetting("Blocks Only", this, true);
    private final BooleanSetting pitchCheckSetting = new BooleanSetting("Pitch Check", this, true);
    private final BooleanSetting disableOnForwardSetting = new BooleanSetting("Disable On Forward", this, true);
    private final BooleanSetting rightClickCheckSetting = new BooleanSetting("Right Click Check", this, false);
    private final BooleanSetting requireSneakPressSetting = new BooleanSetting("Require Sneak Press", this, false);
    private int count = 0;
    private final Random random = new Random();

    @Override
    public String getSuffix() {
        int n = (int)this.minDelaySetting.computeDouble();
        int n2 = (int)this.maxDelaySetting.computeDouble();
        return n == n2 ? String.valueOf(n) : n + "-" + n2;
    }

    private boolean checkState2() {
        double d = mc.thePlayer.posX + mc.thePlayer.motionX;
        double d2 = mc.thePlayer.posZ + mc.thePlayer.motionZ;
        BlockPos pos = new BlockPos(d, mc.thePlayer.posY - 1.0, d2);
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return block.getMaterial() != Material.air && block.getMaterial() != Material.water && block.getMaterial() != Material.lava;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSneak.pressed = mc.currentScreen == null && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
        this.count = 0;
    }

    public LegitScaffold() {
        super("Legit Scaffold", 0, Category.PLAYER);
        this.handleSettingArray(
            new Setting[]{
                this.minDelaySetting,
                this.maxDelaySetting,
                this.onlyGroundSetting,
                this.blocksOnlySetting,
                this.pitchCheckSetting,
                this.disableOnForwardSetting,
                this.rightClickCheckSetting,
                this.requireSneakPressSetting
            }
        );
    }

    private boolean checkState() {
        if (this.disableOnForwardSetting.isEnabled() && mc.gameSettings.keyBindForward.isKeyDown()) {
            return false;
        }
        if (this.pitchCheckSetting.isEnabled() && mc.thePlayer.rotationPitch < 69.0F) {
            return false;
        }
        if (this.rightClickCheckSetting.isEnabled() && !Mouse.isButtonDown(1)) {
            return false;
        }
        return this.blocksOnlySetting.isEnabled() && !this.checkState4() ? false : !this.onlyGroundSetting.isEnabled() || mc.thePlayer.onGround;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    if (mc.thePlayer != null && mc.theWorld != null) {
                        boolean flag = mc.currentScreen == null && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
                        if (mc.currentScreen == null && (!this.requireSneakPressSetting.isEnabled() || flag)) {
                            if (!this.checkState()) {
                                mc.gameSettings.keyBindSneak.pressed = flag;
                                this.count = 0;
                            } else {
                                if (this.count > 0) {
                                    this.count--;
                                }

                                boolean flag2 = this.checkState2();
                                if (this.count == 0 && flag2) {
                                    int n = (int)this.minDelaySetting.computeDouble();
                                    int n2 = (int)this.maxDelaySetting.computeDouble();
                                    if (n > n2) {
                                        n = n2;
                                    }

                                    this.count = n + (n2 > n ? this.random.nextInt(n2 - n + 1) : 0);
                                }

                                boolean flag3 = PlayerUtils.checkState2() || this.count > 0 && !flag2;
                                mc.gameSettings.keyBindSneak.pressed = flag3 || flag && !this.requireSneakPressSetting.isEnabled();
                            }
                        } else {
                            mc.gameSettings.keyBindSneak.pressed = false;
                            this.count = 0;
                        }
                    }
                }
            }
        }
    }

    private boolean checkState4() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
    }
}
