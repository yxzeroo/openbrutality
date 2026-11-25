package org.brutality.module.impl.misc;

import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AntiTrap extends Module {
    private static AntiTrap instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting setAirSetting = new BooleanSetting("Set Air", this, true);
    private final BooleanSetting autoBreakSetting = new BooleanSetting("Auto Break", this, false);
    private final BooleanSetting notificationSetting = new BooleanSetting("Notification", this, true);
    private boolean flag = false;

    public boolean matchesWorld(World world, BlockPos pos) {
        if (world != null && this.mc.thePlayer != null) {
            IBlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block.getMaterial().isSolid() && block.isFullBlock()) {
                Vec3 vec = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight(), this.mc.thePlayer.posZ);
                AxisAlignedBB collisionBoundingBox = block.getCollisionBoundingBox(world, pos, blockState);
                return collisionBoundingBox == null ? false : collisionBoundingBox.isVecInside(vec);
            }
            return false;
        }
        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventTick) {
                    this.process();
                }
            }
        }
    }

    private void handleBlockPos(BlockPos pos, BlockPos pos2) {
        if (this.notificationSetting.isEnabled()) {
            this.mc.thePlayer.addChatMessage(new ChatComponentText("§c§l[AntiObbyTrap] §fDetected trap!"));
        }

        if (this.setAirSetting.isEnabled()) {
            if (this.matchesWorld(this.mc.theWorld, pos)) {
                this.mc.theWorld.setBlockToAir(pos);
            }

            if (this.matchesWorld(this.mc.theWorld, pos2)) {
                this.mc.theWorld.setBlockToAir(pos2);
            }
        }

        if (this.autoBreakSetting.isEnabled()) {
            if (this.matchesWorld(this.mc.theWorld, pos)) {
                this.mc.playerController.clickBlock(pos, EnumFacing.UP);
            }

            if (this.matchesWorld(this.mc.theWorld, pos2)) {
                this.mc.playerController.clickBlock(pos2, EnumFacing.UP);
            }
        }
    }

    public boolean matchesBlockPos(BlockPos pos) {
        if (this.mc.theWorld == null) {
            return false;
        }
        Block block = this.mc.theWorld.getBlockState(pos).getBlock();
        return block == Blocks.obsidian || block == Blocks.bedrock || block == Blocks.barrier || block == Blocks.command_block;
    }

    public BooleanSetting getAutoBreakSetting() {
        return this.autoBreakSetting;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.flag = false;
    }

    public BooleanSetting getSetAirSetting() {
        return this.setAirSetting;
    }

    @Generated
    public static AntiTrap getInstance() {
        return instance;
    }

    private void process() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            BlockPos pos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
            BlockPos pos2 = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0, this.mc.thePlayer.posZ);
            boolean flag = this.matchesWorld(this.mc.theWorld, pos) || this.matchesWorld(this.mc.theWorld, pos2);
            if (flag && !this.flag) {
                this.handleBlockPos(pos, pos2);
                this.flag = true;
            } else if (!flag) {
                this.flag = false;
            }
        }
    }

    public AntiTrap() {
        super("Anti Trap", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(new Setting[]{this.setAirSetting, this.autoBreakSetting, this.notificationSetting});
        instance = this;
    }
}
