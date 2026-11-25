package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.brutality.Brutality;
import org.brutality.event.impl.movement.GetYawEvent;
import org.brutality.event.impl.movement.MovementEvent7;
import org.brutality.event.impl.movement.MovementEvent9;
import org.brutality.module.impl.movement.Safewalk;
import org.brutality.module.impl.movement.Scaffold;

public abstract class Entity implements ICommandSender {
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static int nextEntityID;
    public double lastLastGroundY;
    public boolean safeWalk = false;
    private int entityId;
    public double renderDistanceWeight;
    public int ticksSinceStep;
    public boolean preventEntitySpawning;
    public Entity riddenByEntity;
    public Entity ridingEntity;
    public boolean forceSpawn;
    public World worldObj;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;
    public double posX;
    public double posY;
    public double posZ;
    public double motionX;
    public double motionY;
    public double motionZ;
    public float rotationYaw;
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;
    public AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean isCollidedHorizontally;
    public boolean isCollidedVertically;
    public boolean isCollided;
    public boolean velocityChanged;
    public boolean isInWeb;
    private boolean isOutsideBorder;
    public boolean isDead;
    public float width;
    public float height;
    public float prevDistanceWalkedModified;
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;
    public float fallDistance;
    private int nextStepDistance;
    public double lastTickPosX;
    public double lastTickPosY;
    public double lastTickPosZ;
    public float stepHeight;
    public boolean noClip;
    public float entityCollisionReduction;
    protected Random rand;
    public int ticksExisted;
    public int fireResistance;
    private int fire;
    public boolean inWater;
    public int hurtResistantTime;
    protected boolean firstUpdate;
    protected boolean isImmuneToFire;
    protected DataWatcher dataWatcher;
    private double entityRiderPitchDelta;
    private double entityRiderYawDelta;
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;
    public boolean ignoreFrustumCheck;
    public boolean isAirBorne;
    public int timeUntilPortal;
    protected boolean inPortal;
    protected int portalCounter;
    public int dimension;
    protected BlockPos field_181016_an;
    protected Vec3 field_181017_ao;
    protected EnumFacing field_181018_ap;
    private boolean invulnerable;
    protected UUID entityUniqueID;
    private final CommandResultStats cmdResultStats;
    public float movementYaw;
    public float velocityYaw;
    public float lastMovementYaw;
    public int offGroundTicks;
    public double lastGroundY;

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int id) {
        this.entityId = id;
    }

    public void onKillCommand() {
        this.setDead();
    }

    public Entity(World worldIn) {
        this.entityId = nextEntityID++;
        this.renderDistanceWeight = 1.0;
        this.boundingBox = ZERO_AABB;
        this.width = 0.6F;
        this.height = 1.8F;
        this.nextStepDistance = 1;
        this.rand = new Random();
        this.fireResistance = 1;
        this.firstUpdate = true;
        this.entityUniqueID = MathHelper.getRandomUuid(this.rand);
        this.cmdResultStats = new CommandResultStats();
        this.worldObj = worldIn;
        this.setPosition(0.0, 0.0, 0.0);
        if (worldIn != null) {
            this.dimension = worldIn.provider.getDimensionId();
        }

        this.dataWatcher = new DataWatcher(this);
        this.dataWatcher.addObject(0, (byte)0);
        this.dataWatcher.addObject(1, (short)300);
        this.dataWatcher.addObject(3, (byte)0);
        this.dataWatcher.addObject(2, "");
        this.dataWatcher.addObject(4, (byte)0);
        this.entityInit();
    }

    protected abstract void entityInit();

    public DataWatcher getDataWatcher() {
        return this.dataWatcher;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return p_equals_1_ instanceof Entity ? ((Entity)p_equals_1_).entityId == this.entityId : false;
    }

    @Override
    public int hashCode() {
        return this.entityId;
    }

    public void preparePlayerToSpawn() {
        if (this.worldObj != null) {
            while (true) {
                if (this.posY > 0.0 && this.posY < 256.0) {
                    this.setPosition(this.posX, this.posY, this.posZ);
                    if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                        this.posY++;
                        continue;
                    }
                }

                this.motionX = this.motionY = this.motionZ = 0.0;
                this.rotationPitch = 0.0F;
                break;
            }
        }
    }

    public void setDead() {
        this.isDead = true;
    }

    public void setSize(float width, float height) {
        if (width != this.width || height != this.height) {
            float width2 = this.width;
            this.width = width;
            this.height = height;
            this.setEntityBoundingBox(
                new AxisAlignedBB(
                    this.getEntityBoundingBox().minX,
                    this.getEntityBoundingBox().minY,
                    this.getEntityBoundingBox().minZ,
                    this.getEntityBoundingBox().minX + this.width,
                    this.getEntityBoundingBox().minY + this.height,
                    this.getEntityBoundingBox().minZ + this.width
                )
            );
            if (this.width > width2 && !this.firstUpdate && !this.worldObj.isRemote) {
                this.moveEntity(width2 - this.width, 0.0, width2 - this.width);
            }
        }
    }

    protected void setRotation(float yaw, float pitch) {
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = pitch % 360.0F;
    }

    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float height = this.height;
        this.setEntityBoundingBox(new AxisAlignedBB(x - f, y, z - f, x + f, y + height, z + f));
    }

    public void setAngles(float yaw, float pitch) {
        float pitch2 = this.rotationPitch;
        float yaw2 = this.rotationYaw;
        this.rotationYaw = (float)(this.rotationYaw + yaw * 0.15);
        this.rotationPitch = (float)(this.rotationPitch - pitch * 0.15);
        this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
        this.prevRotationPitch = this.prevRotationPitch + (this.rotationPitch - pitch2);
        this.prevRotationYaw = this.prevRotationYaw + (this.rotationYaw - yaw2);
    }

    public void onUpdate() {
        this.onEntityUpdate();
    }

    public void onEntityUpdate() {
        this.worldObj.theProfiler.startSection("entityBaseTick");
        if (this.ridingEntity != null && this.ridingEntity.isDead) {
            this.ridingEntity = null;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer minecraftServer = ((WorldServer)this.worldObj).getMinecraftServer();
            int maxInPortalTime = this.getMaxInPortalTime();
            if (this.inPortal) {
                if (minecraftServer.getAllowNether()) {
                    if (this.ridingEntity == null && this.portalCounter++ >= maxInPortalTime) {
                        this.portalCounter = maxInPortalTime;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte b;
                        if (this.worldObj.provider.getDimensionId() == -1) {
                            b = 0;
                        } else {
                            b = -1;
                        }

                        this.travelToDimension(b);
                    }

                    this.inPortal = false;
                }
            } else {
                if (this.portalCounter > 0) {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0) {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0) {
                this.timeUntilPortal--;
            }

            this.worldObj.theProfiler.endSection();
        }

        this.spawnRunningParticles();
        this.handleWaterMovement();
        if (this.worldObj.isRemote) {
            this.fire = 0;
        } else if (this.fire > 0) {
            if (this.isImmuneToFire) {
                this.fire -= 4;
                if (this.fire < 0) {
                    this.fire = 0;
                }
            } else {
                if (this.fire % 20 == 0) {
                    this.attackEntityFrom(DamageSource.onFire, 1.0F);
                }

                this.fire--;
            }
        }

        if (this.isInLava()) {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0) {
            this.kill();
        }

        if (!this.worldObj.isRemote) {
            this.setFlag(0, this.fire > 0);
        }

        this.firstUpdate = false;
        this.worldObj.theProfiler.endSection();
    }

    public int getMaxInPortalTime() {
        return 0;
    }

    protected void setOnFireFromLava() {
        if (!this.isImmuneToFire) {
            this.attackEntityFrom(DamageSource.lava, 4.0F);
            this.setFire(15);
        }
    }

    public void setFire(int seconds) {
        int n = seconds * 20;
        int fireTimeForEntity = EnchantmentProtection.getFireTimeForEntity(this, n);
        if (this.fire < fireTimeForEntity) {
            this.fire = fireTimeForEntity;
        }
    }

    public void extinguish() {
        this.fire = 0;
    }

    protected void kill() {
        this.setDead();
    }

    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        AxisAlignedBB box = this.getEntityBoundingBox().offset(x, y, z);
        return this.isLiquidPresentInAABB(box);
    }

    private boolean isLiquidPresentInAABB(AxisAlignedBB bb) {
        return this.worldObj.getCollidingBoundingBoxes(this, bb).isEmpty() && !this.worldObj.isAnyLiquid(bb);
    }

    public void moveEntity(double x, double y, double z) {
        if (this.noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        } else {
            this.worldObj.theProfiler.startSection("move");
            double x2 = this.posX;
            double y2 = this.posY;
            double z2 = this.posZ;
            if (this.isInWeb) {
                this.isInWeb = false;
                x *= 0.25;
                y *= 0.05F;
                z *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }

            double d = x;
            double d2 = y;
            double d3 = z;
            MovementEvent7 movementEvent7 = new MovementEvent7();
            movementEvent7.amount = -1.0;
            movementEvent7.process();
            double amount = movementEvent7.amount;
            boolean flag = (
                    this.onGround && this.isSneaking()
                        || Safewalk.flag && Brutality.moduleManager.computeModule("Safewalk").checkState44()
                        || Scaffold.matchesEntity(this)
                )
                && this instanceof EntityPlayer;
            if (flag) {
                double d4;
                for (d4 = 0.05;
                    x != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0, 0.0)).isEmpty();
                    d = x
                ) {
                    if (x < d4 && x >= -d4) {
                        x = 0.0;
                    } else if (x > 0.0) {
                        x -= d4;
                    } else {
                        x += d4;
                    }
                }

                for (;
                    z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0, -1.0, z)).isEmpty();
                    d3 = z
                ) {
                    if (z < d4 && z >= -d4) {
                        z = 0.0;
                    } else if (z > 0.0) {
                        z -= d4;
                    } else {
                        z += d4;
                    }
                }

                for (;
                    x != 0.0 && z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0, z)).isEmpty();
                    d3 = z
                ) {
                    if (x < d4 && x >= -d4) {
                        x = 0.0;
                    } else if (x > 0.0) {
                        x -= d4;
                    } else {
                        x += d4;
                    }

                    d = x;
                    if (z < d4 && z >= -d4) {
                        z = 0.0;
                    } else if (z > 0.0) {
                        z -= d4;
                    } else {
                        z += d4;
                    }
                }
            }

            var collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x, y, z));
            AxisAlignedBB entityBoundingBox = this.getEntityBoundingBox();

            for (AxisAlignedBB box : collidingBoundingBoxes) {
                y = box.calculateYOffset(this.getEntityBoundingBox(), y);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
            if (this instanceof EntityPlayerSP) {
                this.ticksSinceStep = 0;
                MovementEvent9 movementEvent9 = new MovementEvent9();
                movementEvent9.amount = this.getEntityBoundingBox().minY - this.posY;
                movementEvent9.process();
            }

            boolean flag2 = this.onGround || d2 != y && d2 < 0.0;

            for (AxisAlignedBB box2 : collidingBoundingBoxes) {
                x = box2.calculateXOffset(this.getEntityBoundingBox(), x);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));

            for (AxisAlignedBB box3 : collidingBoundingBoxes) {
                z = box3.calculateZOffset(this.getEntityBoundingBox(), z);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
            if (this.stepHeight > 0.0F && flag2 && (d != x || d3 != z)) {
                double d5 = x;
                double d6 = y;
                double d7 = z;
                AxisAlignedBB entityBoundingBox2 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(entityBoundingBox);
                y = this.stepHeight;
                var collidingBoundingBoxes2 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(d, y, d3));
                AxisAlignedBB entityBoundingBox3 = this.getEntityBoundingBox();
                AxisAlignedBB box4 = entityBoundingBox3.addCoord(d, 0.0, d3);
                double d8 = y;

                for (AxisAlignedBB box5 : collidingBoundingBoxes2) {
                    d8 = box5.calculateYOffset(box4, d8);
                }

                AxisAlignedBB box6 = entityBoundingBox3.offset(0.0, d8, 0.0);
                double d9 = d;

                for (AxisAlignedBB box7 : collidingBoundingBoxes2) {
                    d9 = box7.calculateXOffset(box6, d9);
                }

                entityBoundingBox3 = box6.offset(d9, 0.0, 0.0);
                double d10 = d3;

                for (AxisAlignedBB box8 : collidingBoundingBoxes2) {
                    d10 = box8.calculateZOffset(entityBoundingBox3, d10);
                }

                AxisAlignedBB box9 = entityBoundingBox3.offset(0.0, 0.0, d10);
                AxisAlignedBB entityBoundingBox4 = this.getEntityBoundingBox();
                double d11 = y;

                for (AxisAlignedBB box10 : collidingBoundingBoxes2) {
                    d11 = box10.calculateYOffset(entityBoundingBox4, d11);
                }

                AxisAlignedBB box11 = entityBoundingBox4.offset(0.0, d11, 0.0);
                double d12 = d;

                for (AxisAlignedBB box12 : collidingBoundingBoxes2) {
                    d12 = box12.calculateXOffset(box11, d12);
                }

                entityBoundingBox4 = box11.offset(d12, 0.0, 0.0);
                double d13 = d3;

                for (AxisAlignedBB box13 : collidingBoundingBoxes2) {
                    d13 = box13.calculateZOffset(entityBoundingBox4, d13);
                }

                AxisAlignedBB box14 = entityBoundingBox4.offset(0.0, 0.0, d13);
                double d14 = d9 * d9 + d10 * d10;
                double d15 = d12 * d12 + d13 * d13;
                if (d14 > d15) {
                    x = d9;
                    z = d10;
                    y = -d8;
                    this.setEntityBoundingBox(box9);
                } else {
                    x = d12;
                    z = d13;
                    y = -d11;
                    this.setEntityBoundingBox(box14);
                }

                for (AxisAlignedBB box15 : collidingBoundingBoxes2) {
                    y = box15.calculateYOffset(this.getEntityBoundingBox(), y);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
                if (d5 * d5 + d7 * d7 >= x * x + z * z) {
                    x = d5;
                    y = d6;
                    z = d7;
                    this.setEntityBoundingBox(entityBoundingBox2);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.resetPositionToBB();
            this.isCollidedHorizontally = d != x || d3 != z;
            this.isCollidedVertically = d2 != y;
            this.onGround = this.isCollidedVertically && d2 < 0.0;
            if (this.onGround) {
                this.offGroundTicks = 0;
            } else {
                this.offGroundTicks++;
            }

            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.posY - 0.2F);
            int n3 = MathHelper.floor_double(this.posZ);
            BlockPos pos = new BlockPos(n, n2, n3);
            Block block = this.worldObj.getBlockState(pos).getBlock();
            if (block.getMaterial() == Material.air) {
                Block block2 = this.worldObj.getBlockState(pos.down()).getBlock();
                if (block2 instanceof BlockFence || block2 instanceof BlockWall || block2 instanceof BlockFenceGate) {
                    block = block2;
                    pos = pos.down();
                }
            }

            this.updateFallState(y, this.onGround, block, pos);
            if (d != x) {
                this.motionX = 0.0;
            }

            if (d3 != z) {
                this.motionZ = 0.0;
            }

            if (d2 != y) {
                block.onLanded(this.worldObj, this);
            }

            if (this.canTriggerWalking() && !flag && this.ridingEntity == null) {
                double d16 = this.posX - x2;
                double d17 = this.posY - y2;
                double d18 = this.posZ - z2;
                if (block != Blocks.ladder) {
                    d17 = 0.0;
                }

                if (block != null && this.onGround) {
                    block.onEntityCollidedWithBlock(this.worldObj, pos, this);
                }

                this.distanceWalkedModified = (float)(this.distanceWalkedModified + MathHelper.sqrt_double(d16 * d16 + d18 * d18) * 0.6);
                this.distanceWalkedOnStepModified = (float)(
                    this.distanceWalkedOnStepModified + MathHelper.sqrt_double(d16 * d16 + d17 * d17 + d18 * d18) * 0.6
                );
                if (this.distanceWalkedOnStepModified > this.nextStepDistance && block.getMaterial() != Material.air) {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
                    if (this.isInWater()) {
                        float f = MathHelper.sqrt_double(
                                this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F
                            )
                            * 0.35F;
                        if (f > 1.0F) {
                            f = 1.0F;
                        }

                        this.playSound(this.getSwimSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.playStepSound(pos, block);
                }
            }

            try {
                this.doBlockCollisions();
            } catch (Throwable e) {
                CrashReport crashReport = CrashReport.makeCrashReport(e, "Checking entity block collision");
                CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashReportCategory);
                throw new ReportedException(crashReport);
            }

            boolean wet = this.isWet();
            if (this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001, 0.001, 0.001))) {
                this.dealFireDamage(1);
                if (!wet) {
                    this.fire++;
                    if (this.fire == 0) {
                        this.setFire(8);
                    }
                }
            } else if (this.fire <= 0) {
                this.fire = -this.fireResistance;
            }

            if (wet && this.fire > 0) {
                this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }

            this.worldObj.theProfiler.endSection();
        }
    }

    private void resetPositionToBB() {
        this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0;
        this.posY = this.getEntityBoundingBox().minY;
        this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0;
    }

    protected String getSwimSound() {
        return "game.neutral.swim";
    }

    protected void doBlockCollisions() {
        BlockPos pos = new BlockPos(
            this.getEntityBoundingBox().minX + 0.001, this.getEntityBoundingBox().minY + 0.001, this.getEntityBoundingBox().minZ + 0.001
        );
        BlockPos pos2 = new BlockPos(
            this.getEntityBoundingBox().maxX - 0.001, this.getEntityBoundingBox().maxY - 0.001, this.getEntityBoundingBox().maxZ - 0.001
        );
        if (this.worldObj.isAreaLoaded(pos, pos2)) {
            for (int i = pos.getX(); i <= pos2.getX(); i++) {
                for (int j = pos.getY(); j <= pos2.getY(); j++) {
                    for (int k = pos.getZ(); k <= pos2.getZ(); k++) {
                        BlockPos pos3 = new BlockPos(i, j, k);
                        IBlockState blockState = this.worldObj.getBlockState(pos3);

                        try {
                            blockState.getBlock().onEntityCollidedWithBlock(this.worldObj, pos3, blockState, this);
                        } catch (Throwable e) {
                            CrashReport crashReport = CrashReport.makeCrashReport(e, "Colliding entity with block");
                            CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being collided with");
                            CrashReportCategory.addBlockInfo(crashReportCategory, pos3, blockState);
                            throw new ReportedException(crashReport);
                        }
                    }
                }
            }
        }
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        Block.SoundType stepSound = blockIn.stepSound;
        if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
            stepSound = Blocks.snow_layer.stepSound;
            this.playSound(stepSound.getStepSound(), stepSound.getVolume() * 0.15F, stepSound.getFrequency());
        } else if (!blockIn.getMaterial().isLiquid()) {
            this.playSound(stepSound.getStepSound(), stepSound.getVolume() * 0.15F, stepSound.getFrequency());
        }
    }

    public void playSound(String name, float volume, float pitch) {
        if (!this.isSilent()) {
            this.worldObj.playSoundAtEntity(this, name, volume, pitch);
        }
    }

    public boolean isSilent() {
        return this.dataWatcher.getWatchableObjectByte(4) == 1;
    }

    public void setSilent(boolean isSilent) {
        this.dataWatcher.updateObject(4, (byte)(isSilent ? 1 : 0));
    }

    protected boolean canTriggerWalking() {
        return true;
    }

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
        if (onGroundIn) {
            if (this.fallDistance > 0.0F) {
                if (blockIn != null) {
                    blockIn.onFallenUpon(this.worldObj, pos, this, this.fallDistance);
                } else {
                    this.fall(this.fallDistance, 1.0F);
                }

                this.fallDistance = 0.0F;
            }
        } else if (y < 0.0) {
            this.fallDistance = (float)(this.fallDistance - y);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    protected void dealFireDamage(int amount) {
        if (!this.isImmuneToFire) {
            this.attackEntityFrom(DamageSource.inFire, amount);
        }
    }

    public final boolean isImmuneToFire() {
        return this.isImmuneToFire;
    }

    public void fall(float distance, float damageMultiplier) {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.fall(distance, damageMultiplier);
        }
    }

    public boolean isWet() {
        return this.inWater
            || this.worldObj.canLightningStrike(new BlockPos(this.posX, this.posY, this.posZ))
            || this.worldObj.canLightningStrike(new BlockPos(this.posX, this.posY + this.height, this.posZ));
    }

    public boolean isInWater() {
        return this.inWater;
    }

    public boolean handleWaterMovement() {
        if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0, -0.4F, 0.0).contract(0.001, 0.001, 0.001), Material.water, this)) {
            if (!this.inWater && !this.firstUpdate) {
                this.resetHeight();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    public void resetHeight() {
        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F) * 0.2F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        this.playSound(this.getSplashSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
        float f2 = MathHelper.floor_double(this.getEntityBoundingBox().minY);

        for (int i = 0; i < 1.0F + this.width * 20.0F; i++) {
            float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            this.worldObj
                .spawnParticle(
                    EnumParticleTypes.WATER_BUBBLE,
                    this.posX + f3,
                    f2 + 1.0F,
                    this.posZ + f4,
                    this.motionX,
                    this.motionY - this.rand.nextFloat() * 0.2F,
                    this.motionZ
                );
        }

        for (int j = 0; j < 1.0F + this.width * 20.0F; j++) {
            float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            float f6 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            this.worldObj
                .spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + f5, f2 + 1.0F, this.posZ + f6, this.motionX, this.motionY, this.motionZ);
        }
    }

    public void spawnRunningParticles() {
        if (this.isSprinting() && !this.isInWater()) {
            this.createRunningParticles();
        }
    }

    protected void createRunningParticles() {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.posY - 0.2F);
        int n3 = MathHelper.floor_double(this.posZ);
        BlockPos pos = new BlockPos(n, n2, n3);
        IBlockState blockState = this.worldObj.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.getRenderType() != -1) {
            this.worldObj
                .spawnParticle(
                    EnumParticleTypes.BLOCK_CRACK,
                    this.posX + (this.rand.nextFloat() - 0.5) * this.width,
                    this.getEntityBoundingBox().minY + 0.1,
                    this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
                    -this.motionX * 4.0,
                    1.5,
                    -this.motionZ * 4.0,
                    Block.getStateId(blockState)
                );
        }
    }

    protected String getSplashSound() {
        return "game.neutral.swim.splash";
    }

    public boolean isInsideOfMaterial(Material materialIn) {
        double d = this.posY + this.getEyeHeight();
        BlockPos pos = new BlockPos(this.posX, d, this.posZ);
        IBlockState blockState = this.worldObj.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.getMaterial() == materialIn) {
            float f = BlockLiquid.getLiquidHeightPercent(blockState.getBlock().getMetaFromState(blockState)) - 0.11111111F;
            float f2 = pos.getY() + 1 - f;
            boolean flag = d < f2;
            return !flag && this instanceof EntityPlayer ? false : flag;
        }
        return false;
    }

    public boolean isInLava() {
        return this.worldObj.isMaterialInBB(this.getEntityBoundingBox().expand(-0.1F, -0.4F, -0.1F), Material.lava);
    }

    public void moveFlying(float strafe, float forward, float friction) {
        float yaw = this.rotationYaw;
        if (this == Minecraft.getMinecraft().thePlayer) {
            GetYawEvent getYawEvent = new GetYawEvent(yaw, strafe, forward, friction, this.movementYaw);
            getYawEvent.process();
            if (getYawEvent.cancel2()) {
                return;
            }

            yaw = getYawEvent.getYaw();
            strafe = getYawEvent.getFactor();
            forward = getYawEvent.getFactor2();
            friction = getYawEvent.getFactor3();
        }

        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4F) {
            float f2 = MathHelper.sqrt_float(f);
            if (f2 < 1.0F) {
                f2 = 1.0F;
            }

            f = friction / f2;
            strafe *= f;
            forward *= f;
            float f3 = MathHelper.sinVanilla(yaw * (float) Math.PI / 180.0F);
            float f4 = MathHelper.cosVanilla(yaw * (float) Math.PI / 180.0F);
            this.motionX += strafe * f4 - forward * f3;
            this.motionZ += forward * f4 + strafe * f3;
        }
    }

    public int getBrightnessForRender(float partialTicks) {
        BlockPos pos = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
        return this.worldObj.isBlockLoaded(pos) ? this.worldObj.getCombinedLight(pos, 0) : 0;
    }

    public float getBrightness(float partialTicks) {
        BlockPos pos = new BlockPos(this.posX, this.posY + this.getEyeHeight(), this.posZ);
        return this.worldObj.isBlockLoaded(pos) ? this.worldObj.getLightBrightness(pos) : 0.0F;
    }

    public void setWorld(World worldIn) {
        this.worldObj = worldIn;
    }

    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;
        this.prevRotationYaw = this.rotationYaw = yaw;
        this.prevRotationPitch = this.rotationPitch = pitch;
        double d = this.prevRotationYaw - yaw;
        if (d < -180.0) {
            this.prevRotationYaw += 360.0F;
        }

        if (d >= 180.0) {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(yaw, pitch);
    }

    public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
        this.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, rotationYawIn, rotationPitchIn);
    }

    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public float getDistanceToEntity(Entity entityIn) {
        float f = (float)(this.posX - entityIn.posX);
        float f2 = (float)(this.posY - entityIn.posY);
        float f3 = (float)(this.posZ - entityIn.posZ);
        return MathHelper.sqrt_float(f * f + f2 * f2 + f3 * f3);
    }

    public double getDistanceSq(double x, double y, double z) {
        double d = this.posX - x;
        double d2 = this.posY - y;
        double d3 = this.posZ - z;
        return d * d + d2 * d2 + d3 * d3;
    }

    public double getDistanceSq(BlockPos pos) {
        return pos.distanceSq(this.posX, this.posY, this.posZ);
    }

    public double getDistanceSqToCenter(BlockPos pos) {
        return pos.distanceSqToCenter(this.posX, this.posY, this.posZ);
    }

    public double getDistance(double x, double y, double z) {
        double d = this.posX - x;
        double d2 = this.posY - y;
        double d3 = this.posZ - z;
        return MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
    }

    public double getDistanceSqToEntity(Entity entityIn) {
        double d = this.posX - entityIn.posX;
        double d2 = this.posY - entityIn.posY;
        double d3 = this.posZ - entityIn.posZ;
        return d * d + d2 * d2 + d3 * d3;
    }

    public void onCollideWithPlayer(EntityPlayer entityIn) {
    }

    public void applyEntityCollision(Entity entityIn) {
        if (entityIn.riddenByEntity != this && entityIn.ridingEntity != this && !entityIn.noClip && !this.noClip) {
            double d = entityIn.posX - this.posX;
            double d2 = entityIn.posZ - this.posZ;
            double d3 = MathHelper.abs_max(d, d2);
            if (d3 >= 0.01F) {
                d3 = MathHelper.sqrt_double(d3);
                double d4 = d / d3;
                double d5 = d2 / d3;
                double d6 = 1.0 / d3;
                if (d6 > 1.0) {
                    d6 = 1.0;
                }

                d = d4 * d6;
                d2 = d5 * d6;
                d *= 0.05F;
                d2 *= 0.05F;
                double d7 = d * (1.0F - this.entityCollisionReduction);
                double d8 = d2 * (1.0F - this.entityCollisionReduction);
                if (this.riddenByEntity == null) {
                    this.addVelocity(-d7, 0.0, -d8);
                }

                if (entityIn.riddenByEntity == null) {
                    entityIn.addVelocity(d7, 0.0, d8);
                }
            }
        }
    }

    public void addVelocity(double x, double y, double z) {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
        this.isAirBorne = true;
    }

    protected void setBeenAttacked() {
        this.velocityChanged = true;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        this.setBeenAttacked();
        return false;
    }

    public Vec3 getLook(float partialTicks) {
        if (partialTicks == 1.0F) {
            return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
        }
        float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
        float f2 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks;
        return this.getVectorForRotation(f, f2);
    }

    public final Vec3 getVectorForRotation(float pitch, float yaw) {
        float cos = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
        return new Vec3(sin * f, sin2, cos * f);
    }

    public Vec3 getPositionEyes(float partialTicks) {
        if (partialTicks == 1.0F) {
            return new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ);
        }
        double d = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
        double d2 = this.prevPosY + (this.posY - this.prevPosY) * partialTicks + this.getEyeHeight();
        double d3 = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
        return new Vec3(d, d2, d3);
    }

    public MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks) {
        Vec3 positionEyes = this.getPositionEyes(partialTicks);
        Vec3 look = this.getLook(partialTicks);
        Vec3 vec = positionEyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
        return this.worldObj.rayTraceBlocks(positionEyes, vec, false, false, true);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean canBePushed() {
        return false;
    }

    public void addToPlayerScore(Entity entityIn, int amount) {
    }

    public boolean isInRangeToRender3d(double x, double y, double z) {
        double d = this.posX - x;
        double d2 = this.posY - y;
        double d3 = this.posZ - z;
        double d4 = d * d + d2 * d2 + d3 * d3;
        return this.isInRangeToRenderDist(d4);
    }

    public boolean isInRangeToRenderDist(double distance) {
        double averageEdgeLength = this.getEntityBoundingBox().getAverageEdgeLength();
        if (Double.isNaN(averageEdgeLength)) {
            averageEdgeLength = 1.0;
        }

        averageEdgeLength = averageEdgeLength * 64.0 * this.renderDistanceWeight;
        return distance < averageEdgeLength * averageEdgeLength;
    }

    public boolean writeMountToNBT(NBTTagCompound tagCompund) {
        String entityString = this.getEntityString();
        if (!this.isDead && entityString != null) {
            tagCompund.setString("id", entityString);
            this.writeToNBT(tagCompund);
            return true;
        }
        return false;
    }

    public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
        String entityString = this.getEntityString();
        if (!this.isDead && entityString != null && this.riddenByEntity == null) {
            tagCompund.setString("id", entityString);
            this.writeToNBT(tagCompund);
            return true;
        }
        return false;
    }

    public void writeToNBT(NBTTagCompound tagCompund) {
        try {
            tagCompund.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY, this.posZ));
            tagCompund.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
            tagCompund.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
            tagCompund.setFloat("FallDistance", this.fallDistance);
            tagCompund.setShort("Fire", (short)this.fire);
            tagCompund.setShort("Air", (short)this.getAir());
            tagCompund.setBoolean("OnGround", this.onGround);
            tagCompund.setInteger("Dimension", this.dimension);
            tagCompund.setBoolean("Invulnerable", this.invulnerable);
            tagCompund.setInteger("PortalCooldown", this.timeUntilPortal);
            tagCompund.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
            tagCompund.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
            if (this.getCustomNameTag() != null && this.getCustomNameTag().length() > 0) {
                tagCompund.setString("CustomName", this.getCustomNameTag());
                tagCompund.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
            }

            this.cmdResultStats.writeStatsToNBT(tagCompund);
            if (this.isSilent()) {
                tagCompund.setBoolean("Silent", this.isSilent());
            }

            this.writeEntityToNBT(tagCompund);
            if (this.ridingEntity != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                if (this.ridingEntity.writeMountToNBT(nbt)) {
                    tagCompund.setTag("Riding", nbt);
                }
            }
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.makeCrashReport(e, "Saving entity NBT");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being saved");
            this.addEntityCrashInfo(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    public void readFromNBT(NBTTagCompound tagCompund) {
        try {
            NBTTagList tagList = tagCompund.getTagList("Pos", 6);
            NBTTagList tagList2 = tagCompund.getTagList("Motion", 6);
            NBTTagList tagList3 = tagCompund.getTagList("Rotation", 5);
            this.motionX = tagList2.getDoubleAt(0);
            this.motionY = tagList2.getDoubleAt(1);
            this.motionZ = tagList2.getDoubleAt(2);
            if (Math.abs(this.motionX) > 10.0) {
                this.motionX = 0.0;
            }

            if (Math.abs(this.motionY) > 10.0) {
                this.motionY = 0.0;
            }

            if (Math.abs(this.motionZ) > 10.0) {
                this.motionZ = 0.0;
            }

            this.prevPosX = this.lastTickPosX = this.posX = tagList.getDoubleAt(0);
            this.prevPosY = this.lastTickPosY = this.posY = tagList.getDoubleAt(1);
            this.prevPosZ = this.lastTickPosZ = this.posZ = tagList.getDoubleAt(2);
            this.prevRotationYaw = this.rotationYaw = tagList3.getFloatAt(0);
            this.prevRotationPitch = this.rotationPitch = tagList3.getFloatAt(1);
            this.setRotationYawHead(this.rotationYaw);
            this.func_181013_g(this.rotationYaw);
            this.fallDistance = tagCompund.getFloat("FallDistance");
            this.fire = tagCompund.getShort("Fire");
            this.setAir(tagCompund.getShort("Air"));
            this.onGround = tagCompund.getBoolean("OnGround");
            this.dimension = tagCompund.getInteger("Dimension");
            this.invulnerable = tagCompund.getBoolean("Invulnerable");
            this.timeUntilPortal = tagCompund.getInteger("PortalCooldown");
            if (tagCompund.hasKey("UUIDMost", 4) && tagCompund.hasKey("UUIDLeast", 4)) {
                this.entityUniqueID = new UUID(tagCompund.getLong("UUIDMost"), tagCompund.getLong("UUIDLeast"));
            } else if (tagCompund.hasKey("UUID", 8)) {
                this.entityUniqueID = UUID.fromString(tagCompund.getString("UUID"));
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
            if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
                this.setCustomNameTag(tagCompund.getString("CustomName"));
            }

            this.setAlwaysRenderNameTag(tagCompund.getBoolean("CustomNameVisible"));
            this.cmdResultStats.readStatsFromNBT(tagCompund);
            this.setSilent(tagCompund.getBoolean("Silent"));
            this.readEntityFromNBT(tagCompund);
            if (this.shouldSetPosAfterLoading()) {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.makeCrashReport(e, "Loading entity NBT");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being loaded");
            this.addEntityCrashInfo(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    protected boolean shouldSetPosAfterLoading() {
        return true;
    }

    protected final String getEntityString() {
        return EntityList.getEntityString(this);
    }

    protected abstract void readEntityFromNBT(NBTTagCompound tagCompund);

    protected abstract void writeEntityToNBT(NBTTagCompound tagCompound);

    public void onChunkLoad() {
    }

    protected NBTTagList newDoubleNBTList(double... numbers) {
        NBTTagList tagList = new NBTTagList();

        for (double d : numbers) {
            tagList.appendTag(new NBTTagDouble(d));
        }

        return tagList;
    }

    protected NBTTagList newFloatNBTList(float... numbers) {
        NBTTagList tagList = new NBTTagList();

        for (float f : numbers) {
            tagList.appendTag(new NBTTagFloat(f));
        }

        return tagList;
    }

    public EntityItem dropItem(Item itemIn, int size) {
        return this.dropItemWithOffset(itemIn, size, 0.0F);
    }

    public EntityItem dropItemWithOffset(Item itemIn, int size, float offsetY) {
        return this.entityDropItem(new ItemStack(itemIn, size, 0), offsetY);
    }

    public EntityItem entityDropItem(ItemStack itemStackIn, float offsetY) {
        if (itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
            EntityItem entity = new EntityItem(this.worldObj, this.posX, this.posY + offsetY, this.posZ, itemStackIn);
            entity.setDefaultPickupDelay();
            this.worldObj.spawnEntityInWorld(entity);
            return entity;
        }
        return null;
    }

    public boolean isEntityAlive() {
        return !this.isDead;
    }

    public boolean isEntityInsideOpaqueBlock() {
        if (this.noClip) {
            return false;
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (int i = 0; i < 8; i++) {
            int n = MathHelper.floor_double(this.posY + ((i >> 0) % 2 - 0.5F) * 0.1F + this.getEyeHeight());
            int n2 = MathHelper.floor_double(this.posX + ((i >> 1) % 2 - 0.5F) * this.width * 0.8F);
            int n3 = MathHelper.floor_double(this.posZ + ((i >> 2) % 2 - 0.5F) * this.width * 0.8F);
            if (mutablePos.getX() != n2 || mutablePos.getY() != n || mutablePos.getZ() != n3) {
                mutablePos.func_181079_c(n2, n, n3);
                if (this.worldObj.getBlockState(mutablePos).getBlock().isVisuallyOpaque()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean interactFirst(EntityPlayer playerIn) {
        return false;
    }

    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    public void updateRidden() {
        if (this.ridingEntity.isDead) {
            this.ridingEntity = null;
        } else {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.onUpdate();
            if (this.ridingEntity != null) {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta = this.entityRiderYawDelta + (this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);
                this.entityRiderPitchDelta = this.entityRiderPitchDelta + (this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch);

                while (this.entityRiderYawDelta >= 180.0) {
                    this.entityRiderYawDelta -= 360.0;
                }

                while (this.entityRiderYawDelta < -180.0) {
                    this.entityRiderYawDelta += 360.0;
                }

                while (this.entityRiderPitchDelta >= 180.0) {
                    this.entityRiderPitchDelta -= 360.0;
                }

                while (this.entityRiderPitchDelta < -180.0) {
                    this.entityRiderPitchDelta += 360.0;
                }

                double d = this.entityRiderYawDelta * 0.5;
                double d2 = this.entityRiderPitchDelta * 0.5;
                float f = 10.0F;
                if (d > f) {
                    d = f;
                }

                if (d < -f) {
                    d = -f;
                }

                if (d2 > f) {
                    d2 = f;
                }

                if (d2 < -f) {
                    d2 = -f;
                }

                this.entityRiderYawDelta -= d;
                this.entityRiderPitchDelta -= d2;
            }
        }
    }

    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    public double getYOffset() {
        return 0.0;
    }

    public double getMountedYOffset() {
        return this.height * 0.75;
    }

    public void mountEntity(Entity entityIn) {
        this.entityRiderPitchDelta = 0.0;
        this.entityRiderYawDelta = 0.0;
        if (entityIn == null) {
            if (this.ridingEntity != null) {
                this.setLocationAndAngles(
                    this.ridingEntity.posX,
                    this.ridingEntity.getEntityBoundingBox().minY + this.ridingEntity.height,
                    this.ridingEntity.posZ,
                    this.rotationYaw,
                    this.rotationPitch
                );
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        } else {
            if (this.ridingEntity != null) {
                this.ridingEntity.riddenByEntity = null;
            }

            if (entityIn != null) {
                for (Entity i = entityIn.ridingEntity; i != null; i = i.ridingEntity) {
                    if (i == this) {
                        return;
                    }
                }
            }

            this.ridingEntity = entityIn;
            entityIn.riddenByEntity = this;
        }
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
        var collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().contract(0.03125, 0.0, 0.03125));
        if (!collidingBoundingBoxes.isEmpty()) {
            double d = 0.0;

            for (AxisAlignedBB box : collidingBoundingBoxes) {
                if (box.maxY > d) {
                    d = box.maxY;
                }
            }

            y += d - this.getEntityBoundingBox().minY;
            this.setPosition(x, y, z);
        }
    }

    public float getCollisionBorderSize() {
        return 0.1F;
    }

    public Vec3 getLookVec() {
        return null;
    }

    public void func_181015_d(BlockPos p_181015_1_) {
        if (this.timeUntilPortal > 0) {
            this.timeUntilPortal = this.getPortalCooldown();
        } else {
            if (!this.worldObj.isRemote && !p_181015_1_.equals(this.field_181016_an)) {
                this.field_181016_an = p_181015_1_;
                PatternHelper patternHelper = Blocks.portal.func_181089_f(this.worldObj, p_181015_1_);
                double d = patternHelper.getFinger().getAxis() == Axis.X ? patternHelper.func_181117_a().getZ() : patternHelper.func_181117_a().getX();
                double d2 = patternHelper.getFinger().getAxis() == Axis.X ? this.posZ : this.posX;
                d2 = Math.abs(
                    MathHelper.func_181160_c(
                        d2 - (patternHelper.getFinger().rotateY().getAxisDirection() == AxisDirection.NEGATIVE ? 1 : 0), d, d - patternHelper.func_181118_d()
                    )
                );
                double d3 = MathHelper.func_181160_c(this.posY - 1.0, patternHelper.func_181117_a().getY(), patternHelper.func_181117_a().getY() - patternHelper.func_181119_e());
                this.field_181017_ao = new Vec3(d2, d3, 0.0);
                this.field_181018_ap = patternHelper.getFinger();
            }

            this.inPortal = true;
        }
    }

    public int getPortalCooldown() {
        return 300;
    }

    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void handleHealthUpdate(byte id) {
    }

    public void performHurtAnimation() {
    }

    public ItemStack[] getInventory() {
        return null;
    }

    public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
    }

    public boolean isBurning() {
        boolean flag = this.worldObj != null && this.worldObj.isRemote;
        return !this.isImmuneToFire && (this.fire > 0 || flag && this.getFlag(0));
    }

    public boolean isRiding() {
        return this.ridingEntity != null;
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public void setSneaking(boolean sneaking) {
        this.setFlag(1, sneaking);
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean sprinting) {
        this.setFlag(3, sprinting);
    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return player.isSpectator() ? false : this.isInvisible();
    }

    public void setInvisible(boolean invisible) {
        this.setFlag(5, invisible);
    }

    public boolean isEating() {
        return this.getFlag(4);
    }

    public void setEating(boolean eating) {
        this.setFlag(4, eating);
    }

    protected boolean getFlag(int flag) {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << flag) != 0;
    }

    protected void setFlag(int flag, boolean set) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(0);
        if (set) {
            this.dataWatcher.updateObject(0, (byte)(watchableObjectByte | 1 << flag));
        } else {
            this.dataWatcher.updateObject(0, (byte)(watchableObjectByte & ~(1 << flag)));
        }
    }

    public int getAir() {
        return this.dataWatcher.getWatchableObjectShort(1);
    }

    public void setAir(int air) {
        this.dataWatcher.updateObject(1, (short)air);
    }

    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        this.attackEntityFrom(DamageSource.lightningBolt, 5.0F);
        this.fire++;
        if (this.fire == 0) {
            this.setFire(8);
        }
    }

    public void onKillEntity(EntityLivingBase entityLivingIn) {
    }

    protected boolean pushOutOfBlocks(double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y, z);
        double d = x - pos.getX();
        double d2 = y - pos.getY();
        double d3 = z - pos.getZ();
        var boxs = this.worldObj.func_147461_a(this.getEntityBoundingBox());
        if (boxs.isEmpty() && !this.worldObj.isBlockFullCube(pos)) {
            return false;
        }
        byte b = 3;
        double d4 = 9999.0;
        if (!this.worldObj.isBlockFullCube(pos.west()) && d < d4) {
            d4 = d;
            b = 0;
        }

        if (!this.worldObj.isBlockFullCube(pos.east()) && 1.0 - d < d4) {
            d4 = 1.0 - d;
            b = 1;
        }

        if (!this.worldObj.isBlockFullCube(pos.up()) && 1.0 - d2 < d4) {
            d4 = 1.0 - d2;
            b = 3;
        }

        if (!this.worldObj.isBlockFullCube(pos.north()) && d3 < d4) {
            d4 = d3;
            b = 4;
        }

        if (!this.worldObj.isBlockFullCube(pos.south()) && 1.0 - d3 < d4) {
            d4 = 1.0 - d3;
            b = 5;
        }

        float f = this.rand.nextFloat() * 0.2F + 0.1F;
        if (b == 0) {
            this.motionX = -f;
        }

        if (b == 1) {
            this.motionX = f;
        }

        if (b == 3) {
            this.motionY = f;
        }

        if (b == 4) {
            this.motionZ = -f;
        }

        if (b == 5) {
            this.motionZ = f;
        }

        return true;
    }

    public void setInWeb() {
        this.isInWeb = true;
        this.fallDistance = 0.0F;
    }

    @Override
    public String getCommandSenderName() {
        if (this.hasCustomName()) {
            return this.getCustomNameTag();
        }
        String entityString = EntityList.getEntityString(this);
        if (entityString == null) {
            entityString = "generic";
        }

        return StatCollector.translateToLocal("entity." + entityString + ".name");
    }

    public Entity[] getParts() {
        return null;
    }

    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn;
    }

    public float getRotationYawHead() {
        return 0.0F;
    }

    public void setRotationYawHead(float rotation) {
    }

    public void func_181013_g(float p_181013_1_) {
    }

    public boolean canAttackWithItem() {
        return true;
    }

    public boolean hitByEntity(Entity entityIn) {
        return false;
    }

    @Override
    public String toString() {
        return String.format(
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
            this.getClass().getSimpleName(),
            this.getCommandSenderName(),
            this.entityId,
            this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(),
            this.posX,
            this.posY,
            this.posZ
        );
    }

    public boolean isEntityInvulnerable(DamageSource source) {
        return this.invulnerable && source != DamageSource.outOfWorld && !source.isCreativePlayer();
    }

    public void copyLocationAndAnglesFrom(Entity entityIn) {
        this.setLocationAndAngles(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.rotationYaw, entityIn.rotationPitch);
    }

    public void copyDataFromOld(Entity entityIn) {
        NBTTagCompound nbt = new NBTTagCompound();
        entityIn.writeToNBT(nbt);
        this.readFromNBT(nbt);
        this.timeUntilPortal = entityIn.timeUntilPortal;
        this.field_181016_an = entityIn.field_181016_an;
        this.field_181017_ao = entityIn.field_181017_ao;
        this.field_181018_ap = entityIn.field_181018_ap;
    }

    public void travelToDimension(int dimensionId) {
        if (!this.worldObj.isRemote && !this.isDead) {
            this.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer server = MinecraftServer.getServer();
            int dimension = this.dimension;
            WorldServer world = server.worldServerForDimension(dimension);
            WorldServer world2 = server.worldServerForDimension(dimensionId);
            this.dimension = dimensionId;
            if (dimension == 1 && dimensionId == 1) {
                world2 = server.worldServerForDimension(0);
                this.dimension = 0;
            }

            this.worldObj.removeEntity(this);
            this.isDead = false;
            this.worldObj.theProfiler.startSection("reposition");
            server.getConfigurationManager().transferEntityToWorld(this, dimension, world, world2);
            this.worldObj.theProfiler.endStartSection("reloading");
            Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), world2);
            if (entity != null) {
                entity.copyDataFromOld(this);
                if (dimension == 1 && dimensionId == 1) {
                    BlockPos topSolidOrLiquidBlock = this.worldObj.getTopSolidOrLiquidBlock(world2.getSpawnPoint());
                    entity.moveToBlockPosAndAngles(topSolidOrLiquidBlock, entity.rotationYaw, entity.rotationPitch);
                }

                world2.spawnEntityInWorld(entity);
            }

            this.isDead = true;
            this.worldObj.theProfiler.endSection();
            world.resetUpdateEntityTick();
            world2.resetUpdateEntityTick();
            this.worldObj.theProfiler.endSection();
        }
    }

    public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
        return blockStateIn.getBlock().getExplosionResistance(this);
    }

    public boolean verifyExplosion(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float p_174816_5_) {
        return true;
    }

    public int getMaxFallHeight() {
        return 3;
    }

    public Vec3 func_181014_aG() {
        return this.field_181017_ao;
    }

    public EnumFacing func_181012_aH() {
        return this.field_181018_ap;
    }

    public boolean doesEntityNotTriggerPressurePlate() {
        return false;
    }

    public void addEntityCrashInfo(CrashReportCategory category) {
        category.addCrashSectionCallable("Entity Type", new Callable<String>() {
            public String call() throws Exception {
                return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }
        });
        category.addCrashSection("Entity ID", this.entityId);
        category.addCrashSectionCallable("Entity Name", new Callable<String>() {
            public String call() throws Exception {
                return Entity.this.getCommandSenderName();
            }
        });
        category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
        category.addCrashSection(
            "Entity's Block location",
            CrashReportCategory.getCoordinateInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
        );
        category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
        category.addCrashSectionCallable("Entity's Rider", new Callable<String>() {
            public String call() throws Exception {
                return Entity.this.riddenByEntity.toString();
            }
        });
        category.addCrashSectionCallable("Entity's Vehicle", new Callable<String>() {
            public String call() throws Exception {
                return Entity.this.ridingEntity.toString();
            }
        });
    }

    public boolean canRenderOnFire() {
        return this.isBurning();
    }

    public UUID getUniqueID() {
        return this.entityUniqueID;
    }

    public boolean isPushedByWater() {
        return true;
    }

    @Override
    public IChatComponent getDisplayName() {
        ChatComponentText chatComponentText = new ChatComponentText(this.getCommandSenderName());
        chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
        chatComponentText.getChatStyle().setInsertion(this.getUniqueID().toString());
        return chatComponentText;
    }

    public void setCustomNameTag(String name) {
        this.dataWatcher.updateObject(2, name);
    }

    public String getCustomNameTag() {
        return this.dataWatcher.getWatchableObjectString(2);
    }

    public boolean hasCustomName() {
        return this.dataWatcher.getWatchableObjectString(2).length() > 0;
    }

    public void setAlwaysRenderNameTag(boolean alwaysRenderNameTag) {
        this.dataWatcher.updateObject(3, (byte)(alwaysRenderNameTag ? 1 : 0));
    }

    public boolean getAlwaysRenderNameTag() {
        return this.dataWatcher.getWatchableObjectByte(3) == 1;
    }

    public void setPositionAndUpdate(double x, double y, double z) {
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
    }

    public boolean getAlwaysRenderNameTagForRender() {
        return this.getAlwaysRenderNameTag();
    }

    public void onDataWatcherUpdate(int dataID) {
    }

    public EnumFacing getHorizontalFacing() {
        return EnumFacing.getHorizontal(MathHelper.floor_double(this.rotationYaw * 4.0F / 360.0F + 0.5) & 3);
    }

    protected HoverEvent getHoverEvent() {
        NBTTagCompound nbt = new NBTTagCompound();
        String entityString = EntityList.getEntityString(this);
        nbt.setString("id", this.getUniqueID().toString());
        if (entityString != null) {
            nbt.setString("type", entityString);
        }

        nbt.setString("name", this.getCommandSenderName());
        return new HoverEvent(Action.SHOW_ENTITY, new ChatComponentText(nbt.toString()));
    }

    public boolean isSpectatedByPlayer(EntityPlayerMP player) {
        return true;
    }

    public AxisAlignedBB getEntityBoundingBox() {
        return this.boundingBox;
    }

    public void setEntityBoundingBox(AxisAlignedBB bb) {
        this.boundingBox = bb;
    }

    public float getEyeHeight() {
        return this.height * 0.85F;
    }

    public boolean isOutsideBorder() {
        return this.isOutsideBorder;
    }

    public void setOutsideBorder(boolean outsideBorder) {
        this.isOutsideBorder = outsideBorder;
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        return false;
    }

    @Override
    public void addChatMessage(IChatComponent component) {
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return true;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX, this.posY + 0.5, this.posZ);
    }

    @Override
    public Vec3 getPositionVector() {
        return new Vec3(this.posX, this.posY, this.posZ);
    }

    @Override
    public World getEntityWorld() {
        return this.worldObj;
    }

    @Override
    public Entity getCommandSenderEntity() {
        return this;
    }

    @Override
    public boolean sendCommandFeedback() {
        return false;
    }

    @Override
    public void setCommandStat(Type type, int amount) {
        this.cmdResultStats.func_179672_a(this, type, amount);
    }

    public CommandResultStats getCommandStats() {
        return this.cmdResultStats;
    }

    public void func_174817_o(Entity entityIn) {
        this.cmdResultStats.func_179671_a(entityIn.getCommandStats());
    }

    public NBTTagCompound getNBTTagCompound() {
        return null;
    }

    public void clientUpdateEntityNBT(NBTTagCompound compound) {
    }

    public boolean interactAt(EntityPlayer player, Vec3 targetVec3) {
        return false;
    }

    public boolean isImmuneToExplosions() {
        return false;
    }

    protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            EnchantmentHelper.applyThornEnchantments((EntityLivingBase)entityIn, entityLivingBaseIn);
        }

        EnchantmentHelper.applyArthropodEnchantments(entityLivingBaseIn, entityIn);
    }

    public String getName() {
        if (this.hasCustomName()) {
            return this.getCustomNameTag();
        }
        String entityString = EntityList.getEntityString(this);
        if (entityString == null) {
            entityString = "generic";
        }

        return StatCollector.translateToLocal("entity." + entityString + ".name");
    }
}
