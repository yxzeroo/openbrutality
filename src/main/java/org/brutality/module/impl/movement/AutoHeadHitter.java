package org.brutality.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoHeadHitter extends Module {
    private final BooleanSetting onlyWhileJumpingSetting = new BooleanSetting("Only while jumping", this, true);
    private final BooleanSetting ignoreIfSneakingSetting = new BooleanSetting("Ignore if sneaking", this, true);
    private final NumberSetting jumpsPerSecondSetting = new NumberSetting("Jumps per second", this, 1.0, 1.0, 20.0, 1.0);
    private final TimerUtil timerUtil = new TimerUtil();

    public AutoHeadHitter() {
        super("AutoHeadHitter", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.onlyWhileJumpingSetting, this.ignoreIfSneakingSetting, this.jumpsPerSecondSetting});
    }

    private boolean checkState() {
        BlockPos pos = new BlockPos(Math.floor(mc.thePlayer.posX), (double)((int)mc.thePlayer.posY + 2), Math.floor(mc.thePlayer.posZ));
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (block == Blocks.air) {
            return false;
        }
        BlockPos pos2 = new BlockPos(Math.floor(mc.thePlayer.posX), (double)((int)mc.thePlayer.posY + 3), Math.floor(mc.thePlayer.posZ));
        Block block2 = mc.theWorld.getBlockState(pos2).getBlock();
        return (mc.theWorld.isBlockFullCube(pos) || mc.theWorld.isBlockNormalCube(pos, false))
            && block != Blocks.air
            && (mc.theWorld.isBlockFullCube(pos2) || mc.theWorld.isBlockNormalCube(pos2, false))
            && block2 != Blocks.air;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion event2) {
                    if (this.onlyWhileJumpingSetting.isEnabled() && !mc.gameSettings.keyBindJump.isKeyDown()
                        || this.ignoreIfSneakingSetting.isEnabled() && mc.thePlayer.isSneaking()
                        || mc.currentScreen != null
                        || !mc.thePlayer.onGround
                        || !GetSpeedHelper.isMoving()) {
                        return;
                    }

                    if (this.checkState()) {
                        if (this.timerUtil.matchesDouble2(1000.0 / this.jumpsPerSecondSetting.computeDouble())) {
                            mc.thePlayer.motionY = 0.42F;
                            mc.thePlayer.isAirBorne = true;
                            this.timerUtil.reset();
                        }

                        return;
                    }

                    if (!this.checkState2()) {
                        return;
                    }

                    if (this.timerUtil.matchesDouble2(1000.0 / this.jumpsPerSecondSetting.computeDouble())) {
                        mc.thePlayer.jump();
                        this.timerUtil.reset();
                    }
                }
            }
        }
    }

    private boolean checkState2() {
        BlockPos pos = new BlockPos(Math.floor(mc.thePlayer.posX), (double)((int)mc.thePlayer.posY + 2), Math.floor(mc.thePlayer.posZ));
        return (mc.theWorld.isBlockFullCube(pos) || mc.theWorld.isBlockNormalCube(pos, false)) && mc.theWorld.getBlockState(pos).getBlock() != Blocks.air;
    }
}
