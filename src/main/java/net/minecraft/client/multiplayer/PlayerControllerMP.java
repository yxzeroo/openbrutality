package net.minecraft.client.multiplayer;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import org.brutality.event.impl.ImplEvent2;
import org.brutality.event.impl.player.PlayerEvent2;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.combat.Reach;
import org.brutality.utils.performance.WorldQueryCache;

public class PlayerControllerMP {
    private final Minecraft mc;
    private final NetHandlerPlayClient netClientHandler;
    private BlockPos currentBlock = new BlockPos(-1, -1, -1);
    private ItemStack currentItemHittingBlock;
    private float curBlockDamageMP;
    private float stepSoundTickCounter;
    public int blockHitDelay;
    public boolean isHittingBlock;
    private GameType currentGameType = GameType.SURVIVAL;
    private int currentPlayerItem;
    private float originalBlockReachDistance = 4.5F;
    private float originalCreativeReachDistance = 5.0F;

    public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient p_i45062_2_) {
        this.mc = mcIn;
        this.netClientHandler = p_i45062_2_;
    }

    public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP p_178891_1_, BlockPos p_178891_2_, EnumFacing p_178891_3_) {
        if (!mcIn.theWorld.extinguishFire(mcIn.thePlayer, p_178891_2_, p_178891_3_)) {
            p_178891_1_.onPlayerDestroyBlock(p_178891_2_, p_178891_3_);
        }
    }

    public void setPlayerCapabilities(EntityPlayer p_78748_1_) {
        this.currentGameType.configurePlayerCapabilities(p_78748_1_.capabilities);
    }

    public boolean isSpectator() {
        return this.currentGameType == GameType.SPECTATOR;
    }

    public void setGameType(GameType p_78746_1_) {
        this.currentGameType = p_78746_1_;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    public void flipPlayer(EntityPlayer playerIn) {
        playerIn.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD() {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean onPlayerDestroyBlock(BlockPos pos, EnumFacing side) {
        if (this.currentGameType.isAdventure()) {
            if (this.currentGameType == GameType.SPECTATOR) {
                return false;
            }

            if (!this.mc.thePlayer.isAllowEdit()) {
                Block block = this.mc.theWorld.getBlockState(pos).getBlock();
                ItemStack currentEquippedItem = this.mc.thePlayer.getCurrentEquippedItem();
                if (currentEquippedItem == null) {
                    return false;
                }

                if (!currentEquippedItem.canDestroy(block)) {
                    return false;
                }
            }
        }

        if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            return false;
        }
        WorldClient world = this.mc.theWorld;
        IBlockState blockState = world.getBlockState(pos);
        Block block2 = blockState.getBlock();
        if (block2.getMaterial() == Material.air) {
            return false;
        }
        world.playAuxSFX(2001, pos, Block.getStateId(blockState));
        boolean flag = world.setBlockToAir(pos);
        if (flag) {
            block2.onBlockDestroyedByPlayer(world, pos, blockState);
        }

        this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
        if (!this.currentGameType.isCreative()) {
            ItemStack currentEquippedItem2 = this.mc.thePlayer.getCurrentEquippedItem();
            if (currentEquippedItem2 != null) {
                currentEquippedItem2.onBlockDestroyed(world, block2, pos, this.mc.thePlayer);
                if (currentEquippedItem2.stackSize == 0) {
                    this.mc.thePlayer.destroyCurrentEquippedItem();
                }
            }
        }

        return flag;
    }

    public boolean clickBlock(BlockPos loc, EnumFacing face) {
        if (KillAura.matchesBlockPos(loc)) {
            return false;
        }
        if (this.currentGameType.isAdventure()) {
            if (this.currentGameType == GameType.SPECTATOR) {
                return false;
            }

            if (!this.mc.thePlayer.isAllowEdit()) {
                Block block = this.mc.theWorld.getBlockState(loc).getBlock();
                ItemStack currentEquippedItem = this.mc.thePlayer.getCurrentEquippedItem();
                if (currentEquippedItem == null) {
                    return false;
                }

                if (!currentEquippedItem.canDestroy(block)) {
                    return false;
                }
            }
        }

        if (!this.mc.theWorld.getWorldBorder().contains(loc)) {
            return false;
        }
        if (this.currentGameType.isCreative()) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, loc, face));
            clickBlockCreative(this.mc, this, loc, face);
            this.blockHitDelay = 5;
        } else if (!this.isHittingBlock || !this.isHittingPosition(loc)) {
            if (this.isHittingBlock) {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
            }

            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, loc, face));
            Block block2 = this.mc.theWorld.getBlockState(loc).getBlock();
            boolean flag = block2.getMaterial() != Material.air;
            if (flag && this.curBlockDamageMP == 0.0F) {
                block2.onBlockClicked(this.mc.theWorld, loc, this.mc.thePlayer);
            }

            if (flag && block2.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, loc) >= 1.0F) {
                this.onPlayerDestroyBlock(loc, face);
            } else {
                this.isHittingBlock = true;
                this.currentBlock = loc;
                this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
                this.curBlockDamageMP = 0.0F;
                this.stepSoundTickCounter = 0.0F;
                this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            }
        }

        return true;
    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
        if (KillAura.matchesBlockPos(posBlock)) {
            return false;
        }
        this.syncCurrentPlayItem();
        if (this.blockHitDelay > 0) {
            this.blockHitDelay--;
            return true;
        }
        if (this.currentGameType.isCreative() && this.mc.theWorld.getWorldBorder().contains(posBlock)) {
            this.blockHitDelay = 5;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, posBlock, directionFacing));
            clickBlockCreative(this.mc, this, posBlock, directionFacing);
            return true;
        }
        if (this.isHittingPosition(posBlock)) {
            Block block = this.mc.theWorld.getBlockState(posBlock).getBlock();
            if (block.getMaterial() == Material.air) {
                this.isHittingBlock = false;
                return false;
            }
            this.curBlockDamageMP = this.curBlockDamageMP + block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, posBlock);
            if (this.stepSoundTickCounter % 4.0F == 0.0F) {
                this.mc
                    .getSoundHandler()
                    .playSound(
                        new PositionedSoundRecord(
                            new ResourceLocation(block.stepSound.getStepSound()),
                            (block.stepSound.getVolume() + 1.0F) / 8.0F,
                            block.stepSound.getFrequency() * 0.5F,
                            posBlock.getX() + 0.5F,
                            posBlock.getY() + 0.5F,
                            posBlock.getZ() + 0.5F
                        )
                    );
            }

            this.stepSoundTickCounter++;
            if (this.curBlockDamageMP >= 1.0F) {
                this.isHittingBlock = false;
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
                this.onPlayerDestroyBlock(posBlock, directionFacing);
                this.curBlockDamageMP = 0.0F;
                this.stepSoundTickCounter = 0.0F;
                this.blockHitDelay = 5;
            }

            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            return true;
        }
        return this.clickBlock(posBlock, directionFacing);
    }

    public float getBlockReachDistance() {
        if (Reach.getInstance() != null && Reach.getInstance().checkState()) {
            return Math.min((float)Reach.getInstance().getAmount(), this.currentGameType.isCreative() ? 6.0F : 5.0F);
        }
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

    public float getEntityReachDistance() {
        if (Reach.getInstance() != null && Reach.getInstance().checkState()) {
            return (float)Reach.getInstance().getAmount();
        }
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

    public boolean canAttackEntity(Entity entity, double distance) {
        if (Reach.getInstance() != null && Reach.getInstance().checkState44()) {
            return Reach.getInstance().matchesEntity(entity, distance);
        }
        float entityReachDistance = this.getEntityReachDistance();
        return distance <= entityReachDistance;
    }

    public void updateController() {
        this.syncCurrentPlayItem();
        if (this.netClientHandler.getNetworkManager().isChannelOpen()) {
            this.netClientHandler.getNetworkManager().processReceivedPackets();
        } else {
            this.netClientHandler.getNetworkManager().checkDisconnected();
        }
    }

    private boolean isHittingPosition(BlockPos pos) {
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        boolean flag = this.currentItemHittingBlock == null && heldItem == null;
        if (this.currentItemHittingBlock != null && heldItem != null) {
            flag = heldItem.getItem() == this.currentItemHittingBlock.getItem()
                && ItemStack.areItemStackTagsEqual(heldItem, this.currentItemHittingBlock)
                && (heldItem.isItemStackDamageable() || heldItem.getMetadata() == this.currentItemHittingBlock.getMetadata());
        }

        return pos.equals(this.currentBlock) && flag;
    }

    public void syncCurrentPlayItem() {
        int currentItem = this.mc.thePlayer.inventory.currentItem;
        if (currentItem != this.currentPlayerItem) {
            this.currentPlayerItem = currentItem;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
        }
    }

    public void onServerHeldSlot(int slot) {
        this.currentPlayerItem = slot;
    }

    public boolean onPlayerRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos, EnumFacing side, Vec3 hitVec) {
        if (KillAura.matchesEntityPlayer(player, heldStack)) {
            return false;
        }
        this.syncCurrentPlayItem();
        float f = (float)(hitVec.xCoord - hitPos.getX());
        float f2 = (float)(hitVec.yCoord - hitPos.getY());
        float f3 = (float)(hitVec.zCoord - hitPos.getZ());
        boolean flag = false;
        if (!this.mc.theWorld.getWorldBorder().contains(hitPos)) {
            return false;
        }
        if (this.currentGameType != GameType.SPECTATOR) {
            IBlockState blockState = worldIn.getBlockState(hitPos);
            if ((!player.isSneaking() || player.getHeldItem() == null)
                && blockState.getBlock().onBlockActivated(worldIn, hitPos, blockState, player, side, f, f2, f3)) {
                flag = true;
            }

            if (!flag && heldStack != null && heldStack.getItem() instanceof ItemBlock) {
                ItemBlock item = (ItemBlock)heldStack.getItem();
                if (!item.canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack)) {
                    return false;
                }
            }
        }

        this.netClientHandler
            .addToSendQueue(new C08PacketPlayerBlockPlacement(hitPos, side.getIndex(), player.inventory.getCurrentItem(), f, f2, f3));
        if (flag || this.currentGameType == GameType.SPECTATOR) {
            return true;
        }
        if (heldStack == null) {
            return false;
        }
        if (this.currentGameType.isCreative()) {
            int metadata = heldStack.getMetadata();
            int stackSize = heldStack.stackSize;
            boolean flag2 = heldStack.onItemUse(player, worldIn, hitPos, side, f, f2, f3);
            heldStack.setItemDamage(metadata);
            heldStack.stackSize = stackSize;
            return flag2;
        }
        return heldStack.onItemUse(player, worldIn, hitPos, side, f, f2, f3);
    }

    public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
        if (this.currentGameType == GameType.SPECTATOR) {
            return false;
        }
        if (KillAura.matchesEntityPlayer(playerIn, itemStackIn)) {
            return false;
        }
        this.syncCurrentPlayItem();
        boolean flag = itemStackIn != null
            && itemStackIn.getItem() instanceof ItemSword
            && KillAura.getInstance() != null
            && KillAura.getInstance().checkState44()
            && (KillAura.getInstance().verusSetting.matchesString2("Watchdog") || KillAura.getInstance().verusSetting.matchesString2("Grim"))
            && (KillAura.getInstance().checkState58() || KillAura.getInstance().checkState14());
        if (!flag) {
            this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(playerIn.inventory.getCurrentItem()));
        }

        int stackSize = itemStackIn.stackSize;
        ItemStack stack = itemStackIn.useItemRightClick(worldIn, playerIn);
        if (stack != itemStackIn || stack != null && stack.stackSize != stackSize) {
            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = stack;
            if (stack.stackSize == 0) {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
            }

            return true;
        }
        return false;
    }

    public EntityPlayerSP func_178892_a(World worldIn, StatFileWriter p_178892_2_) {
        return new EntityPlayerSP(this.mc, worldIn, this.netClientHandler, p_178892_2_);
    }

    public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
        if (!WorldQueryCache.isActive()) {
            this.syncCurrentPlayItem();
            if (this.isHittingBlock) {
                this.resetBlockRemoving();
            }

            if (!KillAura.checkState19()) {
                ImplEvent2 implEvent2 = new ImplEvent2(playerIn, targetEntity);
                implEvent2.process();
                if (!implEvent2.cancel2()) {
                    this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));
                    if (this.currentGameType != GameType.SPECTATOR) {
                        playerIn.attackTargetEntityWithCurrentItem(targetEntity);
                    }
                }
            }
        }
    }

    public boolean interactWithEntitySendPacket(EntityPlayer playerIn, Entity targetEntity) {
        if (KillAura.matchesEntityPlayer(playerIn, playerIn.getHeldItem())) {
            return false;
        }
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.INTERACT));
        return this.currentGameType != GameType.SPECTATOR && playerIn.interactWith(targetEntity);
    }

    public boolean func_178894_a(EntityPlayer p_178894_1_, Entity p_178894_2_, MovingObjectPosition p_178894_3_) {
        if (KillAura.matchesEntityPlayer(p_178894_1_, p_178894_1_.getHeldItem())) {
            return false;
        }
        this.syncCurrentPlayItem();
        Vec3 hitVec = p_178894_3_.hitVec;
        ViaLoadingBase instance = ViaLoadingBase.getInstance();
        if (instance != null && instance.getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
            AxisAlignedBB entityBoundingBox = p_178894_2_.getEntityBoundingBox();
            double d = 1.0E-4;
            hitVec = new Vec3(
                Math.max(entityBoundingBox.minX + d, Math.min(entityBoundingBox.maxX - d, hitVec.xCoord)),
                Math.max(entityBoundingBox.minY + d, Math.min(entityBoundingBox.maxY - d, hitVec.yCoord)),
                Math.max(entityBoundingBox.minZ + d, Math.min(entityBoundingBox.maxZ - d, hitVec.zCoord))
            );
        }

        Vec3 vec = new Vec3(hitVec.xCoord - p_178894_2_.posX, hitVec.yCoord - p_178894_2_.posY, hitVec.zCoord - p_178894_2_.posZ);
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(p_178894_2_, vec));
        return this.currentGameType != GameType.SPECTATOR && p_178894_2_.interactAt(p_178894_1_, vec);
    }

    public ItemStack windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn) {
        Container openContainer = playerIn.openContainer;
        if (openContainer != null && openContainer.windowId == windowId) {
            short nextTransactionID = openContainer.getNextTransactionID(playerIn.inventory);
            ItemStack stack = openContainer.slotClick(slotId, mouseButtonClicked, mode, playerIn);
            this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, stack, nextTransactionID));
            return stack;
        }
        return null;
    }

    public void sendEnchantPacket(int p_78756_1_, int p_78756_2_) {
        this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(p_78756_1_, p_78756_2_));
    }

    public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
        if (this.currentGameType.isCreative()) {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(slotId, itemStackIn));
        }
    }

    public void sendPacketDropItem(ItemStack itemStackIn) {
        if (this.currentGameType.isCreative() && itemStackIn != null) {
            this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, itemStackIn));
        }
    }

    public void onStoppedUsingItem(EntityPlayer playerIn) {
        PlayerEvent2 playerEvent2 = new PlayerEvent2();
        playerEvent2.process();
        if (!playerEvent2.cancel2()) {
            this.syncCurrentPlayItem();
            ItemStack itemInUse = playerIn.getItemInUse();
            boolean flag = itemInUse != null
                && itemInUse.getItem() instanceof ItemSword
                && KillAura.getInstance() != null
                && KillAura.getInstance().checkState44()
                && (KillAura.getInstance().verusSetting.matchesString2("Watchdog") || KillAura.getInstance().verusSetting.matchesString2("Grim"))
                && KillAura.getInstance().checkState14();
            if (!flag) {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }

            playerIn.stopUsingItem();
        }
    }

    public boolean gameIsSurvivalOrAdventure() {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean isNotCreative() {
        return !this.currentGameType.isCreative();
    }

    public boolean isInCreativeMode() {
        return this.currentGameType.isCreative();
    }

    public boolean extendedReach() {
        return Reach.getInstance() != null && Reach.getInstance().checkState() ? true : this.currentGameType.isCreative();
    }

    public boolean isRidingHorse() {
        return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
    }

    public boolean isSpectatorMode() {
        return this.currentGameType == GameType.SPECTATOR;
    }

    public GameType getCurrentGameType() {
        return this.currentGameType;
    }

    public boolean func_181040_m() {
        return this.isHittingBlock;
    }

    public void setBlockReachDistance(float distance) {
        this.originalBlockReachDistance = distance;
    }

    public void setCreativeReachDistance(float distance) {
        this.originalCreativeReachDistance = distance;
    }

    public boolean isReachModified() {
        return Reach.getInstance() != null && Reach.getInstance().checkState44() && Reach.getInstance().checkState();
    }

    public void resetReachDistances() {
        this.originalBlockReachDistance = 4.5F;
        this.originalCreativeReachDistance = 5.0F;
    }

    public float getVanillaBlockReachDistance() {
        return this.currentGameType.isCreative() ? this.originalCreativeReachDistance : this.originalBlockReachDistance;
    }

    public float getBlockDamageProgress() {
        return this.curBlockDamageMP;
    }

    public BlockPos getActiveMiningBlock() {
        return this.isHittingBlock ? this.currentBlock : null;
    }

    public boolean getIsHittingBlock() {
        return this.isHittingBlock;
    }
}
