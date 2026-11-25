package org.brutality.module.impl.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.network.NetworkHelper;

public class SpeedMine extends Module {
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 1.0, 1.0, 3.0, 1.0);
    private EnumFacing facing;
    private BlockPos pos;
    private boolean flag;
    private float damage;

    public SpeedMine() {
        super("SpeedMine", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.speedSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPreMotion event2) {
                mc.playerController.blockHitDelay = 0;
                if (this.pos != null && this.flag) {
                    IBlockState blockState = mc.theWorld.getBlockState(this.pos);
                    if (blockState == null) {
                        return;
                    }

                    try {
                        this.damage = (float)(
                            this.damage
                                + blockState.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.pos) * this.speedSetting.computeDouble()
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    if (this.damage >= 1.0F) {
                        try {
                            mc.theWorld.setBlockState(this.pos, Blocks.air.getDefaultState(), 11);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            return;
                        }

                        NetworkHelper.handlePacket(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.pos, this.facing));
                        this.damage = 0.0F;
                        this.flag = false;
                    }
                }
            } else if (event instanceof EventPacketSend event3 && event3.getPacket() instanceof C07PacketPlayerDigging) {
                C07PacketPlayerDigging packet = (C07PacketPlayerDigging)event3.getPacket();
                if (packet.getStatus() == Action.START_DESTROY_BLOCK) {
                    this.flag = true;
                    this.pos = packet.getPosition();
                    this.facing = packet.getFacing();
                    this.damage = 0.0F;
                } else if (packet.getStatus() == Action.ABORT_DESTROY_BLOCK || packet.getStatus() == Action.STOP_DESTROY_BLOCK) {
                    this.flag = false;
                    this.pos = null;
                    this.facing = null;
                }
            }
        }
    }
}
