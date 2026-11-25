package org.brutality.module.impl.movement;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.player.PlayerEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;

public final class Jesus extends Module {
    private final ModeSetting ncpSetting = new ModeSetting("Mode", this, "Vanilla", Arrays.asList("Vanilla", "Verus", "NCP"));
    public static Jesus instance;
    private boolean flag;
    private int count = 0;

    @Override
    public void onDisable() {
        super.onDisable();
        this.count = 0;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof PlayerEvent playerEvent && playerEvent.getBlock().getMaterial().isLiquid()) {
                    AxisAlignedBB box = AxisAlignedBB.fromBounds(-5.0, -1.0, -5.0, 5.0, 1.0, 5.0)
                        .offset(playerEvent.getBlockPos().getX(), playerEvent.getBlockPos().getY(), playerEvent.getBlockPos().getZ());
                    this.flag = true;
                    playerEvent.setBoundingBox(box);
                }

                if (event instanceof EventPreMotion) {
                    if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                        this.process();
                    }

                    if (this.flag) {
                        String str = this.ncpSetting.getText().toLowerCase();
                        byte b = -1;
                        switch (str.hashCode()) {
                            case 112097665:
                                if (str.equals("verus")) {
                                    b = 0;
                                }
                            default:
                                switch (b) {
                                    case 0:
                                        if (mc.thePlayer.ticksExisted % 5 == 0) {
                                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ);
                                        }
                                }
                        }
                    }

                    this.flag = false;
                }
            }
        }
    }

    @Generated
    public static Jesus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.count = 0;
        }
    }

    private void process() {
        if (this.ncpSetting.matchesString2("NCP")) {
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock() instanceof BlockLiquid) {
                mc.thePlayer.motionY = -0.015F;
                GetSpeedHelper.handleFloat2(0.135F);
                this.count++;
            }

            if (this.count >= 2) {
                mc.thePlayer.motionY = 0.046318803010859555;
                GetSpeedHelper.handleFloat2(0.155F);
                this.count = 0;
            }

            if (mc.thePlayer.isInWater()) {
                mc.thePlayer.motionY = 0.15F;
            }
        }
    }

    public Jesus() {
        super("Jesus", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.ncpSetting});
        instance = this;
    }
}
