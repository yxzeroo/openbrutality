package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import org.brutality.Brutality;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.EventChat;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.IsSafewalkEvent;
import org.brutality.event.impl.movement.IsSprintingEvent;
import org.brutality.event.impl.movement.MovementEvent2;
import org.brutality.event.impl.movement.MovementEvent4;
import org.brutality.event.impl.movement.MovementEvent6;
import org.brutality.event.impl.movement.MovementEvent8;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.combat.Velocity;
import org.brutality.module.impl.misc.ChatBypass;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.OnMouseHandler;
import org.brutality.module.impl.movement.Sprint;
import org.brutality.module.impl.pit.hypixel.utility.LauncherDebugger;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.rotation.GetRotationYawHeadHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.input.Keyboard;

public class EntityPlayerSP extends AbstractClientPlayer {
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter statWriter;
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;
    private float lastReportedYaw;
    private float lastReportedPitch;
    private Boolean sprintGroundAtMovementStart;
    private boolean lastMovementSprinting;
    private boolean previousMovementSprinting;
    private boolean serverSneakState;
    public boolean serverSprintState;
    private int positionUpdateTicks;
    private boolean hasValidHealth;
    private boolean combatDeathObserved;
    private String clientBrand;
    public MovementInput movementInput;
    protected Minecraft mc;
    protected int sprintToggleTimer;
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private int horseJumpPowerCounter;
    private float horseJumpPower;
    public int onGroundTicks;
    public float timeInPortal;
    public float prevTimeInPortal;

    public boolean isGroundedForSprintDecision() {
        return this.sprintGroundAtMovementStart == null ? this.onGround : this.sprintGroundAtMovementStart;
    }

    public boolean hasRecentSprintMovement() {
        return this.lastMovementSprinting || this.previousMovementSprinting;
    }

    public float[] getLastReportedRotations() {
        return new float[]{this.lastReportedYaw, this.lastReportedPitch};
    }

    public void resetSprintToggleTimer() {
        this.sprintToggleTimer = 0;
    }

    public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
        super(worldIn, netHandler.getGameProfile());
        this.sendQueue = netHandler;
        this.statWriter = statFile;
        this.mc = mcIn;
        this.dimension = 0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void heal(float healAmount) {
    }

    @Override
    public void mountEntity(Entity entityIn) {
        Entity ridingEntity = this.ridingEntity;
        super.mountEntity(entityIn);
        if (ridingEntity != this.ridingEntity) {
            PitDebugger.handleEntity(this);
            LauncherDebugger.handleEntityPlayerSP(this);
        }

        if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
        }
    }

    @Override
    public void moveEntity(double x, double y, double z) {
        IsSafewalkEvent isSafewalkEvent = new IsSafewalkEvent(x, y, z, this.safeWalk);
        isSafewalkEvent.process();
        if (!isSafewalkEvent.cancel2()) {
            x = isSafewalkEvent.getX();
            y = isSafewalkEvent.getY();
            z = isSafewalkEvent.getZ();
            super.moveEntity(x, y, z);
        }
    }

    @Override
    public void onUpdate() {
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
            this.sprintGroundAtMovementStart = this.onGround;

            try {
                this.onGroundTicks = !this.mc.thePlayer.onGround ? 0 : this.onGroundTicks + 1;
                if (Displace.instance != null) {
                    Displace.instance.process7();
                }

                new EventUpdate(EventState.PRE).process();
                super.onUpdate();
                new EventUpdate(EventState.POST).process();
                this.sendMovementUpdates();
            } finally {
                this.sprintGroundAtMovementStart = null;
            }
        }
    }

    private void sendMovementUpdates() {
        StartHotbarMovesHelper.process2();
        if (this.isRiding()) {
            this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
        } else {
            this.onUpdateWalkingPlayer();
        }
    }

    public void resetSprintForPlayerReplacement() {
        this.setSprinting(false);
        this.serverSprintState = false;
        this.sprintToggleTimer = 0;
        this.sprintingTicksLeft = 0;
        this.jumpMovementFactor = 0.02F;
        this.lastMovementSprinting = false;
        this.previousMovementSprinting = false;
    }

    public void onUpdateWalkingPlayer() {
        IsSprintingEvent isSprintingEvent = new IsSprintingEvent(this.isSprinting(), this.isSneaking());
        isSprintingEvent.process();
        boolean sprinting = isSprintingEvent.isSprinting();
        KeepSprint instance = KeepSprint.instance;
        if (instance != null && instance.checkState14()) {
            sprinting = false;
            this.setSprinting(false);
        }

        if (sprinting != this.serverSprintState) {
            if (sprinting) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.START_SPRINTING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.STOP_SPRINTING));
            }

            this.serverSprintState = sprinting;
        }

        boolean sneaking = isSprintingEvent.isSneaking();
        if (sneaking != this.serverSneakState) {
            if (sneaking) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.START_SNEAKING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.STOP_SNEAKING));
            }

            this.serverSneakState = sneaking;
        }

        EventPreMotion event = new EventPreMotion(
            this.posX,
            this.posY,
            this.posZ,
            this.rotationYaw,
            this.rotationPitch,
            this.lastReportedYaw,
            this.lastReportedPitch,
            this.onGround,
            this.mc.thePlayer
        );
        event.setState(EventState.PRE);
        event.process();
        float yaw = event.getYaw();
        float pitch = event.getPitch();
        float lastReportedYaw = this.lastReportedYaw;
        float lastReportedPitch = this.lastReportedPitch;
        Displace instance2 = Displace.instance;
        boolean flag = instance2 != null && instance2.checkState6() && instance2.computeFloatType() != null;
        float[] floats = Autoblockin.computeFloatArray();
        float[] floats2 = OnMouseHandler.computeFloatArray();
        if (floats != null && !event.isMovementYawApplied()) {
            yaw = floats[0];
            pitch = floats[1];
            event.setYaw(yaw);
            event.setPitch(pitch);
        } else if (flag && !event.isMovementYawApplied()) {
            yaw = instance2.computeFloatType();
            pitch = instance2.getFactor();
            event.setYaw(yaw);
            event.setPitch(pitch);
            this.rotationYawHead = yaw;
            this.renderYawOffset = yaw;
            this.prevRenderYawOffset = yaw;
        } else if (floats2 != null) {
            event.handleFloat(floats2[0], floats2[1]);
            yaw = event.getYaw();
            pitch = event.getPitch();
            this.rotationYawHead = yaw;
            this.rotationPitchHead = pitch;
            this.renderYawOffset = yaw;
        } else if (OnJumpHelper.checkState()) {
            float[] floatValueArray2 = OnJumpHelper.floatValueArray2;
            float[] floatValueArray = OnJumpHelper.floatValueArray;
            if (floatValueArray2 != null && floatValueArray != null) {
                event.handleFloat(floatValueArray2[0], floatValueArray2[1]);
                yaw = event.getYaw();
                pitch = event.getPitch();
            }
        }

        if (yaw != this.rotationYaw && !event.isMovementYawApplied()) {
            yaw = OnJumpHelper.computeFloat4(yaw, this.lastReportedYaw);
        }

        event.setYaw(yaw);
        if (this.isCurrentViewEntity()) {
            double d = event.getX() - this.lastReportedPosX;
            double d2 = event.getY() - this.lastReportedPosY;
            double d3 = event.getZ() - this.lastReportedPosZ;
            double d4 = yaw - lastReportedYaw;
            double d5 = pitch - lastReportedPitch;
            boolean flag2 = d * d + d2 * d2 + d3 * d3 > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean flag3 = d4 != 0.0 || d5 != 0.0;
            if (this.ridingEntity == null) {
                if (flag2 && flag3) {
                    this.sendQueue
                        .addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), yaw, pitch, event.isGround()));
                } else if (flag2) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(event.getX(), event.getY(), event.getZ(), event.isGround()));
                } else if (flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, event.isGround()));
                } else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(event.isGround()));
                }
            } else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, yaw, pitch, event.isGround()));
                flag2 = false;
            }

            this.previousMovementSprinting = this.lastMovementSprinting;
            this.lastMovementSprinting = sprinting;
            this.positionUpdateTicks++;
            if (flag2) {
                this.lastReportedPosX = event.getX();
                this.lastReportedPosY = event.getY();
                this.lastReportedPosZ = event.getZ();
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = yaw;
                this.lastReportedPitch = pitch;
                OnJumpHelper.floatValueArray = new float[]{yaw, pitch};
            }

            GetRotationYawHeadHelper.handleEntityPlayerSP(this, yaw, pitch);
            EventPostMotion event2 = new EventPostMotion(event.getX(), event.getY(), event.getZ(), yaw, pitch, event.isGround(), this.mc.thePlayer);
            event2.process();
        }
    }

    @Override
    public EntityItem dropOneItem(boolean dropAll) {
        net.minecraft.network.play.client.C07PacketPlayerDigging.Action action = dropAll
            ? net.minecraft.network.play.client.C07PacketPlayerDigging.Action.DROP_ALL_ITEMS
            : net.minecraft.network.play.client.C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
        return null;
    }

    @Override
    public void joinEntityItemWithWorld(EntityItem itemIn) {
    }

    public void sendChatMessage(String message) {
        if (!message.startsWith("/") && !message.startsWith(".") && ChatBypass.getInstance().checkState44()) {
            message = message.replace("a", "á").replace("i", "í").replace("e", "é");
        }

        EventChat event = new EventChat(message);
        event.process();
        Brutality.cmdManager.onEventChat(event);
        if (!event.cancel2()) {
            this.sendQueue.addToSendQueue(new C01PacketChatMessage(event.getMessage()));
        }
    }

    @Override
    public void swingItem() {
        super.swingItem();
        PitDebugger.handleEntityPlayer(this);
        this.sendQueue.addToSendQueue(new C0APacketAnimation());
    }

    @Override
    public void respawnPlayer() {
        this.sendQueue.addToSendQueue(new C16PacketClientStatus(EnumState.PERFORM_RESPAWN));
    }

    @Override
    public void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) {
            this.setHealth(this.getHealth() - damageAmount);
        }
    }

    @Override
    public void closeScreen() {
        this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenAndDropStack();
    }

    public void closeScreenAndDropStack() {
        this.inventory.setItemStack((ItemStack)null);
        super.closeScreen();
        this.mc.displayGuiScreen((GuiScreen)null);
    }

    @Override
    public void onDataWatcherUpdate(int dataId) {
        super.onDataWatcherUpdate(dataId);
        if (dataId == 6) {
            boolean flag = this.getHealth() <= 0.0F;
            if (flag && !this.combatDeathObserved) {
                KillAura.handleEntityPlayerSP(this);
            }

            this.combatDeathObserved = flag;
        }
    }

    public void setPlayerSPHealth(float health) {
        if (this.hasValidHealth) {
            float f = this.getHealth() - health;
            if (f <= 0.0F) {
                this.setHealth(health);
                if (f < 0.0F) {
                    this.hurtResistantTime = this.maxHurtResistantTime / 2;
                }
            } else {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity(DamageSource.generic, f);
                this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }

    @Override
    public void addStat(StatBase stat, int amount) {
        if (stat != null && stat.isIndependent) {
            super.addStat(stat, amount);
        }
    }

    @Override
    public void sendPlayerAbilities() {
        this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
    }

    @Override
    public boolean isUser() {
        return true;
    }

    protected void sendHorseJump() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0F)));
    }

    public void sendHorseInventory() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, Action.OPEN_INVENTORY));
    }

    public void setClientBrand(String brand) {
        this.clientBrand = brand;
    }

    public String getClientBrand() {
        return this.clientBrand;
    }

    public StatFileWriter getStatFileWriter() {
        return this.statWriter;
    }

    @Override
    public void addChatComponentMessage(IChatComponent chatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
    }

    @Override
    public boolean pushOutOfBlocks(double x, double y, double z) {
        MovementEvent6 movementEvent6 = new MovementEvent6();
        movementEvent6.process();
        if (!this.noClip && !movementEvent6.cancel2()) {
            BlockPos pos = new BlockPos(x, y, z);
            double d = x - pos.getX();
            double d2 = z - pos.getZ();
            int n = Math.max((int)Math.ceil(this.height), 1);
            if (!this.isHeadspaceFree(pos, n)) {
                byte b = -1;
                double d3 = 9999.0;
                if (this.isHeadspaceFree(pos.west(), n) && d < d3) {
                    d3 = d;
                    b = 0;
                }

                if (this.isHeadspaceFree(pos.east(), n) && 1.0 - d < d3) {
                    d3 = 1.0 - d;
                    b = 1;
                }

                if (this.isHeadspaceFree(pos.north(), n) && d2 < d3) {
                    d3 = d2;
                    b = 4;
                }

                if (this.isHeadspaceFree(pos.south(), n) && 1.0 - d2 < d3) {
                    b = 5;
                }

                float f = 0.1F;
                if (b == 0) {
                    this.motionX = -f;
                }

                if (b == 1) {
                    this.motionX = f;
                }

                if (b == 4) {
                    this.motionZ = -f;
                }

                if (b == 5) {
                    this.motionZ = f;
                }
            }
        }

        return false;
    }

    private boolean isHeadspaceFree(BlockPos pos, int height) {
        for (int i = 0; i < height; i++) {
            if (!this.isOpenBlockSpace(pos.add(0, i, 0))) {
                return false;
            }
        }

        return true;
    }

    private boolean isOpenBlockSpace(BlockPos pos) {
        return !this.worldObj.getBlockState(pos).getBlock().isNormalCube();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        KeepSprint instance = KeepSprint.instance;
        if (sprinting && instance != null && instance.checkState14()) {
            sprinting = false;
        }

        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    public void setXPStats(float currentXP, int maxXP, int level) {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }

    @Override
    public void addChatMessage(IChatComponent component) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return permLevel <= 0;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
    }

    @Override
    public void playSound(String name, float volume, float pitch) {
        this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
    }

    @Override
    public boolean isServerWorld() {
        return true;
    }

    public boolean isRidingHorse() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
    }

    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }

    @Override
    public void openEditSign(TileEntitySign signTile) {
        this.mc.displayGuiScreen(new GuiEditSign(signTile));
    }

    @Override
    public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
        this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
    }

    @Override
    public void displayGUIBook(ItemStack bookStack) {
        Item item = bookStack.getItem();
        if (item == Items.writable_book) {
            this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
        }
    }

    @Override
    public void displayGUIChest(IInventory chestInventory) {
        String str = chestInventory instanceof IInteractionObject ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";
        if ("minecraft:chest".equals(str)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        } else if ("minecraft:hopper".equals(str)) {
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
        } else if ("minecraft:furnace".equals(str)) {
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
        } else if ("minecraft:brewing_stand".equals(str)) {
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
        } else if ("minecraft:beacon".equals(str)) {
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
        } else if (!"minecraft:dispenser".equals(str) && !"minecraft:dropper".equals(str)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        } else {
            this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
        }
    }

    @Override
    public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
    }

    @Override
    public void displayGui(IInteractionObject guiOwner) {
        String guiID = guiOwner.getGuiID();
        if ("minecraft:crafting_table".equals(guiID)) {
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
        } else if ("minecraft:enchanting_table".equals(guiID)) {
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
        } else if ("minecraft:anvil".equals(guiID)) {
            this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
        }
    }

    @Override
    public void displayVillagerTradeGui(IMerchant villager) {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
    }

    @Override
    public void onCriticalHit(Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
    }

    @Override
    public void onEnchantmentCritical(Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
    }

    @Override
    public boolean isSneaking() {
        boolean flag = this.movementInput != null ? this.movementInput.sneak : false;
        return flag && !this.sleeping;
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (this.isCurrentViewEntity()) {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            boolean flag = Velocity.matchesEntityPlayerSP(this);
            this.isJumping = this.isJumping || flag;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float)(this.renderArmPitch + (this.rotationPitch - this.renderArmPitch) * 0.5);
            this.renderArmYaw = (float)(this.renderArmYaw + (this.rotationYaw - this.renderArmYaw) * 0.5);
        }
    }

    protected boolean isCurrentViewEntity() {
        return this.mc.getRenderViewEntity() == this;
    }

    @Override
    public void onLivingUpdate() {
        if (this.sprintingTicksLeft > 0) {
            this.sprintingTicksLeft--;
            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }

        if (this.sprintToggleTimer > 0) {
            this.sprintToggleTimer--;
        }

        this.prevTimeInPortal = this.timeInPortal;
        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
                this.mc.displayGuiScreen((GuiScreen)null);
            }

            if (this.timeInPortal == 0.0F) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
            }

            this.timeInPortal += 0.0125F;
            if (this.timeInPortal >= 1.0F) {
                this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
        } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667F;
            if (this.timeInPortal > 1.0F) {
                this.timeInPortal = 1.0F;
            }
        } else {
            if (this.timeInPortal > 0.0F) {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
        }

        boolean jump = this.movementInput.jump;
        boolean sneak = this.movementInput.sneak;
        float f = 0.8F;
        boolean flag = this.movementInput.moveForward >= f;
        this.movementInput.updatePlayerMoveState();
        MovementEvent4 movementEvent4 = new MovementEvent4();
        movementEvent4.process();
        this.preserveMountedDismountInput(this.mc.currentScreen == null && Keyboard.isCreated() && GameSettings.isKeyDown(this.mc.gameSettings.keyBindSneak));
        MovementEvent2 movementEvent2 = new MovementEvent2(0.2F, 0.2F);
        movementEvent2.process();
        boolean flag2 = false;
        if (!this.isRiding()) {
            KillAura instance = KillAura.getInstance();
            boolean flag3 = instance != null && instance.checkState53() && GetCurrentScreenHelper.checkState6();
            boolean flag4 = instance != null && instance.checkState36();
            boolean flag5 = instance != null && instance.checkState9();
            boolean flag6 = instance != null && instance.checkState44() && GetCurrentScreenHelper.checkState6() && instance.checkState14();
            boolean flag7 = NoSlowDown.checkState11();
            flag2 = flag3 && flag4 || flag7;
            if (flag7) {
                this.movementInput.moveStrafe *= 0.2F;
                this.movementInput.moveForward *= 0.2F;
                this.sprintToggleTimer = 0;
            } else if (!this.isUsingItem() || flag4 && (flag3 || flag6)) {
                if (flag3 && !flag5) {
                    if (flag4) {
                        this.movementInput.moveStrafe *= 0.2F;
                        this.movementInput.moveForward *= 0.2F;
                    } else {
                        this.movementInput.moveStrafe = this.movementInput.moveStrafe * movementEvent2.getFactor();
                        this.movementInput.moveForward = this.movementInput.moveForward * movementEvent2.getFactor2();
                    }

                    this.sprintToggleTimer = 0;
                }
            } else {
                this.movementInput.moveStrafe = this.movementInput.moveStrafe * movementEvent2.getFactor();
                this.movementInput.moveForward = this.movementInput.moveForward * movementEvent2.getFactor2();
                this.sprintToggleTimer = 0;
            }
        }

        this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
        this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
        this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
        this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
        MovementEvent8 movementEvent8 = new MovementEvent8();
        movementEvent8.process();
        if (movementEvent8.isFlag()) {
            this.sprintToggleTimer = 0;
            this.setSprinting(false);
        }

        this.updateSprintState(sneak, flag, movementEvent8.cancel2(), flag2);
        if (!movementEvent8.cancel2()) {
            NoSlowDown.process5();
        }

        if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            } else if (!jump && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }

        if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
                this.motionY = this.motionY - this.capabilities.getFlySpeed() * 3.0F;
            }

            if (this.movementInput.jump) {
                this.motionY = this.motionY + this.capabilities.getFlySpeed() * 3.0F;
            }
        }

        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                this.horseJumpPowerCounter++;
                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0F;
                }
            }

            if (jump && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            } else if (!jump && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0F;
            } else if (jump) {
                this.horseJumpPowerCounter++;
                if (this.horseJumpPowerCounter < 10) {
                    this.horseJumpPower = this.horseJumpPowerCounter * 0.1F;
                } else {
                    this.horseJumpPower = 0.8F + 2.0F / (this.horseJumpPowerCounter - 9) * 0.1F;
                }
            }
        } else {
            this.horseJumpPower = 0.0F;
        }

        super.onLivingUpdate();
        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }

    private void preserveMountedDismountInput(boolean physicalSneak) {
        if (this.isRiding() && physicalSneak && !this.movementInput.sneak) {
            this.movementInput.sneak = true;
            this.movementInput.moveForward *= 0.3F;
            this.movementInput.moveStrafe *= 0.3F;
        }
    }

    private void updateSprintState(boolean previouslySneaking, boolean previouslyForward, boolean sprintCancelled, boolean blockingSlowdown) {
        Sprint.process();
        KeepSprint instance = KeepSprint.instance;
        if (instance != null && instance.checkState13()) {
            this.sprintToggleTimer = 0;
            this.setSprinting(false);
        } else {
            boolean flag = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
            boolean flag2 = !this.movementInput.sneak && !blockingSlowdown && this.movementInput.moveForward >= 0.8F;
            boolean flag3 = flag2 && !sprintCancelled;
            if (this.onGround && !previouslySneaking && !previouslyForward && flag3 && !this.isSprinting() && flag && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
                if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                    this.sprintToggleTimer = 7;
                } else {
                    this.setSprinting(true);
                }
            }

            if (!this.isSprinting()
                && flag3
                && flag
                && !this.isUsingItem()
                && !this.isPotionActive(Potion.blindness)
                && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.setSprinting(true);
            }

            if (this.isSprinting() && (!flag2 || this.isCollidedHorizontally || !flag)) {
                this.setSprinting(false);
            }
        }
    }

    public void swap(int inventorySlot, int hotbarSlot) {
        this.mc.playerController.windowClick(this.inventoryContainer.windowId, inventorySlot, hotbarSlot, 2, this);
    }
}
