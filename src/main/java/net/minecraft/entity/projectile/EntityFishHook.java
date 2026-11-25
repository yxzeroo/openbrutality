package net.minecraft.entity.projectile;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityFishHook extends Entity {
    private static final List<WeightedRandomFishable> JUNK = Arrays.asList(
        new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10).setMaxDamagePercent(0.9F),
        new WeightedRandomFishable(new ItemStack(Items.leather), 10),
        new WeightedRandomFishable(new ItemStack(Items.bone), 10),
        new WeightedRandomFishable(new ItemStack(Items.potionitem), 10),
        new WeightedRandomFishable(new ItemStack(Items.string), 5),
        new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2).setMaxDamagePercent(0.9F),
        new WeightedRandomFishable(new ItemStack(Items.bowl), 10),
        new WeightedRandomFishable(new ItemStack(Items.stick), 5),
        new WeightedRandomFishable(new ItemStack(Items.dye, 10, EnumDyeColor.BLACK.getDyeDamage()), 1),
        new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10),
        new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10)
    );
    private static final List<WeightedRandomFishable> TREASURE = Arrays.asList(
        new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1),
        new WeightedRandomFishable(new ItemStack(Items.name_tag), 1),
        new WeightedRandomFishable(new ItemStack(Items.saddle), 1),
        new WeightedRandomFishable(new ItemStack(Items.bow), 1).setMaxDamagePercent(0.25F).setEnchantable(),
        new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1).setMaxDamagePercent(0.25F).setEnchantable(),
        new WeightedRandomFishable(new ItemStack(Items.book), 1).setEnchantable()
    );
    private static final List<WeightedRandomFishable> FISH = Arrays.asList(
        new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.COD.getMetadata()), 60),
        new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.SALMON.getMetadata()), 25),
        new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.CLOWNFISH.getMetadata()), 2),
        new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.PUFFERFISH.getMetadata()), 13)
    );
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile;
    private boolean inGround;
    public int shake;
    public EntityPlayer angler;
    private int ticksInGround;
    private int ticksInAir;
    private int ticksCatchable;
    private int ticksCaughtDelay;
    private int ticksCatchableDelay;
    private float fishApproachAngle;
    public Entity caughtEntity;
    private int fishPosRotationIncrements;
    private double fishX;
    private double fishY;
    private double fishZ;
    private double fishYaw;
    private double fishPitch;
    private double clientMotionX;
    private double clientMotionY;
    private double clientMotionZ;

    public static List<WeightedRandomFishable> func_174855_j() {
        return FISH;
    }

    public boolean isFishBiting() {
        return this.ticksCatchable > 0;
    }

    public EntityFishHook(World worldIn) {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
        this.ignoreFrustumCheck = true;
    }

    public EntityFishHook(World worldIn, double x, double y, double z, EntityPlayer anglerIn) {
        this(worldIn);
        this.setPosition(x, y, z);
        this.ignoreFrustumCheck = true;
        this.angler = anglerIn;
        anglerIn.fishEntity = this;
    }

    public EntityFishHook(World worldIn, EntityPlayer fishingPlayer) {
        super(worldIn);
        this.ignoreFrustumCheck = true;
        this.angler = fishingPlayer;
        this.angler.fishEntity = this;
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(fishingPlayer.posX, fishingPlayer.posY + fishingPlayer.getEyeHeight(), fishingPlayer.posZ, fishingPlayer.rotationYaw, fishingPlayer.rotationPitch);
        this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.posY -= 0.1F;
        this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        float f = 0.4F;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f;
        this.handleHookCasting(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
        if (Double.isNaN(d)) {
            d = 4.0;
        }

        d *= 64.0;
        return distance < d * d;
    }

    public void handleHookCasting(double p_146035_1_, double p_146035_3_, double p_146035_5_, float p_146035_7_, float p_146035_8_) {
        float f = MathHelper.sqrt_double(p_146035_1_ * p_146035_1_ + p_146035_3_ * p_146035_3_ + p_146035_5_ * p_146035_5_);
        p_146035_1_ /= f;
        p_146035_3_ /= f;
        p_146035_5_ /= f;
        p_146035_1_ += this.rand.nextGaussian() * 0.0075F * p_146035_8_;
        p_146035_3_ += this.rand.nextGaussian() * 0.0075F * p_146035_8_;
        p_146035_5_ += this.rand.nextGaussian() * 0.0075F * p_146035_8_;
        p_146035_1_ *= p_146035_7_;
        p_146035_3_ *= p_146035_7_;
        p_146035_5_ *= p_146035_7_;
        this.motionX = p_146035_1_;
        this.motionY = p_146035_3_;
        this.motionZ = p_146035_5_;
        float f2 = MathHelper.sqrt_double(p_146035_1_ * p_146035_1_ + p_146035_5_ * p_146035_5_);
        this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.func_181159_b(p_146035_1_, p_146035_5_) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.func_181159_b(p_146035_3_, f2) * 180.0 / Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        this.fishX = x;
        this.fishY = y;
        this.fishZ = z;
        this.fishYaw = yaw;
        this.fishPitch = pitch;
        this.fishPosRotationIncrements = posRotationIncrements;
        this.motionX = this.clientMotionX;
        this.motionY = this.clientMotionY;
        this.motionZ = this.clientMotionZ;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.clientMotionX = this.motionX = x;
        this.clientMotionY = this.motionY = y;
        this.clientMotionZ = this.motionZ = z;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.fishPosRotationIncrements > 0) {
            double d = this.posX + (this.fishX - this.posX) / this.fishPosRotationIncrements;
            double d2 = this.posY + (this.fishY - this.posY) / this.fishPosRotationIncrements;
            double d3 = this.posZ + (this.fishZ - this.posZ) / this.fishPosRotationIncrements;
            double d4 = MathHelper.wrapAngleTo180_double(this.fishYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d4 / this.fishPosRotationIncrements);
            this.rotationPitch = (float)(this.rotationPitch + (this.fishPitch - this.rotationPitch) / this.fishPosRotationIncrements);
            this.fishPosRotationIncrements--;
            this.setPosition(d, d2, d3);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else {
            if (!this.worldObj.isRemote) {
                ItemStack currentEquippedItem = this.angler.getCurrentEquippedItem();
                if (this.angler.isDead
                    || !this.angler.isEntityAlive()
                    || currentEquippedItem == null
                    || currentEquippedItem.getItem() != Items.fishing_rod
                    || this.getDistanceSqToEntity(this.angler) > 1024.0) {
                    this.setDead();
                    this.angler.fishEntity = null;
                    return;
                }

                if (this.caughtEntity != null) {
                    if (!this.caughtEntity.isDead) {
                        this.posX = this.caughtEntity.posX;
                        double height = this.caughtEntity.height;
                        this.posY = this.caughtEntity.getEntityBoundingBox().minY + height * 0.8;
                        this.posZ = this.caughtEntity.posZ;
                        return;
                    }

                    this.caughtEntity = null;
                }
            }

            if (this.shake > 0) {
                this.shake--;
            }

            if (this.inGround) {
                if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
                    this.ticksInGround++;
                    if (this.ticksInGround == 1200) {
                        this.setDead();
                    }

                    return;
                }

                this.inGround = false;
                this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
                this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
                this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else {
                this.ticksInAir++;
            }

            Vec3 vec = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 vec2 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition hit = this.worldObj.rayTraceBlocks(vec, vec2);
            vec = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 vec3 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (hit != null) {
                vec3 = new Vec3(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
            }

            Entity entity = null;
            List entitiesWithinAABBExcludingEntity = this.worldObj
                .getEntitiesWithinAABBExcludingEntity(
                    this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0)
                );
            double d5 = 0.0;

            for (int i = 0; i < entitiesWithinAABBExcludingEntity.size(); i++) {
                Entity entity2 = (Entity)entitiesWithinAABBExcludingEntity.get(i);
                if (entity2.canBeCollidedWith() && (entity2 != this.angler || this.ticksInAir >= 5)) {
                    float f = 0.3F;
                    AxisAlignedBB box = entity2.getEntityBoundingBox().expand(f, f, f);
                    MovingObjectPosition hit2 = box.calculateIntercept(vec, vec3);
                    if (hit2 != null) {
                        double d6 = vec.squareDistanceTo(hit2.hitVec);
                        if (d6 < d5 || d5 == 0.0) {
                            entity = entity2;
                            d5 = d6;
                        }
                    }
                }
            }

            if (entity != null) {
                hit = new MovingObjectPosition(entity);
            }

            if (hit != null) {
                if (hit.entityHit != null) {
                    if (hit.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 0.0F)) {
                        this.caughtEntity = hit.entityHit;
                    }
                } else {
                    this.inGround = true;
                }
            }

            if (!this.inGround) {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                this.rotationYaw = (float)(MathHelper.func_181159_b(this.motionX, this.motionZ) * 180.0 / Math.PI);
                this.rotationPitch = (float)(MathHelper.func_181159_b(this.motionY, f2) * 180.0 / Math.PI);

                while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
                    this.prevRotationPitch -= 360.0F;
                }

                while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                    this.prevRotationPitch += 360.0F;
                }

                while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                    this.prevRotationYaw -= 360.0F;
                }

                while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                    this.prevRotationYaw += 360.0F;
                }

                this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
                this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
                float f3 = 0.92F;
                if (this.onGround || this.isCollidedHorizontally) {
                    f3 = 0.5F;
                }

                byte b = 5;
                double d7 = 0.0;

                for (int j = 0; j < b; j++) {
                    AxisAlignedBB entityBoundingBox = this.getEntityBoundingBox();
                    double d8 = entityBoundingBox.maxY - entityBoundingBox.minY;
                    double d9 = entityBoundingBox.minY + d8 * j / b;
                    double d10 = entityBoundingBox.minY + d8 * (j + 1) / b;
                    AxisAlignedBB box2 = new AxisAlignedBB(entityBoundingBox.minX, d9, entityBoundingBox.minZ, entityBoundingBox.maxX, d10, entityBoundingBox.maxZ);
                    if (this.worldObj.isAABBInMaterial(box2, Material.water)) {
                        d7 += 1.0 / b;
                    }
                }

                if (!this.worldObj.isRemote && d7 > 0.0) {
                    WorldServer worldObj = (WorldServer)this.worldObj;
                    int n = 1;
                    BlockPos pos = new BlockPos(this).up();
                    if (this.rand.nextFloat() < 0.25F && this.worldObj.canLightningStrike(pos)) {
                        n = 2;
                    }

                    if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(pos)) {
                        n--;
                    }

                    if (this.ticksCatchable > 0) {
                        this.ticksCatchable--;
                        if (this.ticksCatchable <= 0) {
                            this.ticksCaughtDelay = 0;
                            this.ticksCatchableDelay = 0;
                        }
                    } else if (this.ticksCatchableDelay > 0) {
                        this.ticksCatchableDelay -= n;
                        if (this.ticksCatchableDelay <= 0) {
                            this.motionY -= 0.2F;
                            this.playSound("random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                            float f4 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
                            worldObj.spawnParticle(
                                EnumParticleTypes.WATER_BUBBLE,
                                this.posX,
                                f4 + 1.0F,
                                this.posZ,
                                (int)(1.0F + this.width * 20.0F),
                                this.width,
                                0.0,
                                this.width,
                                0.2F
                            );
                            worldObj.spawnParticle(
                                EnumParticleTypes.WATER_WAKE,
                                this.posX,
                                f4 + 1.0F,
                                this.posZ,
                                (int)(1.0F + this.width * 20.0F),
                                this.width,
                                0.0,
                                this.width,
                                0.2F
                            );
                            this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
                        } else {
                            this.fishApproachAngle = (float)(this.fishApproachAngle + this.rand.nextGaussian() * 4.0);
                            float f5 = this.fishApproachAngle * (float) (Math.PI / 180.0);
                            float sin = MathHelper.sin(f5);
                            float cos = MathHelper.cos(f5);
                            double d11 = this.posX + sin * this.ticksCatchableDelay * 0.1F;
                            double d12 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                            double d13 = this.posZ + cos * this.ticksCatchableDelay * 0.1F;
                            Block block = worldObj.getBlockState(new BlockPos((int)d11, (int)d12 - 1, (int)d13)).getBlock();
                            if (block == Blocks.water || block == Blocks.flowing_water) {
                                if (this.rand.nextFloat() < 0.15F) {
                                    worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d11, d12 - 0.1F, d13, 1, sin, 0.1, cos, 0.0);
                                }

                                float f6 = sin * 0.04F;
                                float f7 = cos * 0.04F;
                                worldObj.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d12, d13, 0, f7, 0.01, -f6, 1.0);
                                worldObj.spawnParticle(EnumParticleTypes.WATER_WAKE, d11, d12, d13, 0, -f7, 0.01, f6, 1.0);
                            }
                        }
                    } else if (this.ticksCaughtDelay > 0) {
                        this.ticksCaughtDelay -= n;
                        float f8 = 0.15F;
                        if (this.ticksCaughtDelay < 20) {
                            f8 = (float)(f8 + (20 - this.ticksCaughtDelay) * 0.05);
                        } else if (this.ticksCaughtDelay < 40) {
                            f8 = (float)(f8 + (40 - this.ticksCaughtDelay) * 0.02);
                        } else if (this.ticksCaughtDelay < 60) {
                            f8 = (float)(f8 + (60 - this.ticksCaughtDelay) * 0.01);
                        }

                        if (this.rand.nextFloat() < f8) {
                            float f9 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                            float f10 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
                            double d14 = this.posX + MathHelper.sin(f9) * f10 * 0.1F;
                            double d15 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                            double d16 = this.posZ + MathHelper.cos(f9) * f10 * 0.1F;
                            Block block2 = worldObj.getBlockState(new BlockPos((int)d14, (int)d15 - 1, (int)d16)).getBlock();
                            if (block2 == Blocks.water || block2 == Blocks.flowing_water) {
                                worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d14, d15, d16, 2 + this.rand.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                            }
                        }

                        if (this.ticksCaughtDelay <= 0) {
                            this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
                            this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                        }
                    } else {
                        this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
                        this.ticksCaughtDelay = this.ticksCaughtDelay - EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
                    }

                    if (this.ticksCatchable > 0) {
                        this.motionY = this.motionY - this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat() * 0.2;
                    }
                }

                double d17 = d7 * 2.0 - 1.0;
                this.motionY += 0.04F * d17;
                if (d7 > 0.0) {
                    f3 = (float)(f3 * 0.9);
                    this.motionY *= 0.8;
                }

                this.motionX *= f3;
                this.motionY *= f3;
                this.motionZ *= f3;
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("xTile", (short)this.xTile);
        tagCompound.setShort("yTile", (short)this.yTile);
        tagCompound.setShort("zTile", (short)this.zTile);
        ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(this.inTile);
        tagCompound.setString("inTile", nameForObject == null ? "" : nameForObject.toString());
        tagCompound.setByte("shake", (byte)this.shake);
        tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.xTile = tagCompund.getShort("xTile");
        this.yTile = tagCompund.getShort("yTile");
        this.zTile = tagCompund.getShort("zTile");
        if (tagCompund.hasKey("inTile", 8)) {
            this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
        } else {
            this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
        }

        this.shake = tagCompund.getByte("shake") & 255;
        this.inGround = tagCompund.getByte("inGround") == 1;
    }

    public int handleHookRetraction() {
        if (this.worldObj.isRemote) {
            return 0;
        }
        int n = 0;
        if (this.caughtEntity != null) {
            double d = this.angler.posX - this.posX;
            double d2 = this.angler.posY - this.posY;
            double d3 = this.angler.posZ - this.posZ;
            double d4 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
            double d5 = 0.1;
            this.caughtEntity.motionX += d * d5;
            this.caughtEntity.motionY = this.caughtEntity.motionY + (d2 * d5 + MathHelper.sqrt_double(d4) * 0.08);
            this.caughtEntity.motionZ += d3 * d5;
            n = 3;
        } else if (this.ticksCatchable > 0) {
            EntityItem entity = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.getFishingResult());
            double d6 = this.angler.posX - this.posX;
            double d7 = this.angler.posY - this.posY;
            double d8 = this.angler.posZ - this.posZ;
            double d9 = MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
            double d10 = 0.1;
            entity.motionX = d6 * d10;
            entity.motionY = d7 * d10 + MathHelper.sqrt_double(d9) * 0.08;
            entity.motionZ = d8 * d10;
            this.worldObj.spawnEntityInWorld(entity);
            this.angler
                .worldObj
                .spawnEntityInWorld(
                    new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5, this.angler.posZ + 0.5, this.rand.nextInt(6) + 1)
                );
            n = 1;
        }

        if (this.inGround) {
            n = 2;
        }

        this.setDead();
        this.angler.fishEntity = null;
        return n;
    }

    private ItemStack getFishingResult() {
        float f = this.worldObj.rand.nextFloat();
        int luckOfSeaModifier = EnchantmentHelper.getLuckOfSeaModifier(this.angler);
        int lureModifier = EnchantmentHelper.getLureModifier(this.angler);
        float f2 = 0.1F - luckOfSeaModifier * 0.025F - lureModifier * 0.01F;
        float f3 = 0.05F + luckOfSeaModifier * 0.01F - lureModifier * 0.01F;
        float f4 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
        float f5 = MathHelper.clamp_float(f3, 0.0F, 1.0F);
        if (f < f4) {
            this.angler.triggerAchievement(StatList.junkFishedStat);
            return WeightedRandom.getRandomItem(this.rand, JUNK).getItemStack(this.rand);
        }
        float f6 = f - f4;
        if (f6 < f5) {
            this.angler.triggerAchievement(StatList.treasureFishedStat);
            return WeightedRandom.getRandomItem(this.rand, TREASURE).getItemStack(this.rand);
        }
        float f7 = f6 - f5;
        this.angler.triggerAchievement(StatList.fishCaughtStat);
        return WeightedRandom.getRandomItem(this.rand, FISH).getItemStack(this.rand);
    }

    @Override
    public void setDead() {
        super.setDead();
        if (this.angler != null) {
            this.angler.fishEntity = null;
        }
    }
}
