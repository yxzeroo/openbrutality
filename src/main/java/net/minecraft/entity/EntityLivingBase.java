package net.minecraft.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.brutality.event.impl.movement.GetYawEvent2;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.player.GetEntityEvent2;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.module.impl.visual.Animations;
import org.brutality.utils.player.SomberHelper;

public abstract class EntityLivingBase extends Entity {
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(
            sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.3F, 2
        )
        .setSaved(false);
    public float prevRotationPitchHead;
    public float rotationPitchHead;
    private BaseAttributeMap attributeMap;
    private final CombatTracker _combatTracker = new CombatTracker(this);
    private final Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();
    private final ItemStack[] previousEquipment = new ItemStack[5];
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;
    public int hurtTime;
    public int maxHurtTime;
    public float attackedAtYaw;
    public int deathTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float field_70769_ao;
    public float field_70770_ap;
    public float renderYawOffset;
    public float prevRenderYawOffset;
    public float rotationYawHead;
    public float prevRotationYawHead;
    public float jumpMovementFactor = 0.02F;
    protected EntityPlayer attackingPlayer;
    protected int recentlyHit;
    protected boolean dead;
    protected int entityAge;
    protected float prevOnGroundSpeedFactor;
    protected float onGroundSpeedFactor;
    protected float movedDistance;
    protected float prevMovedDistance;
    protected float field_70741_aB;
    protected int scoreValue;
    protected float lastDamage;
    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;
    protected int newPosRotationIncrements;
    protected double newPosX;
    protected double newPosY;
    protected double newPosZ;
    protected double newRotationYaw;
    protected double newRotationPitch;
    private boolean potionsNeedUpdate = true;
    private EntityLivingBase entityLivingToAttack;
    private int revengeTimer;
    private EntityLivingBase lastAttacker;
    private int lastAttackerTime;
    private float landMovementFactor;
    public int jumpTicks;
    private float absorptionAmount;
    private int ticksSinceLastDamage;

    @Override
    public void onKillCommand() {
        this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
    }

    public EntityLivingBase(World worldIn) {
        super(worldIn);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.field_70770_ap = (float)((Math.random() + 1.0) * 0.01F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.field_70769_ao = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
        this.rotationYawHead = this.rotationYaw;
        this.prevRotationPitchHead = this.rotationPitchHead;
        this.stepHeight = 0.6F;
    }

    @Override
    public void entityInit() {
        this.dataWatcher.addObject(7, 0);
        this.dataWatcher.addObject(8, (byte)0);
        this.dataWatcher.addObject(9, (byte)0);
        this.dataWatcher.addObject(6, 1.0F);
    }

    public void applyEntityAttributes() {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
    }

    @Override
    public void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
        if (!this.isInWater()) {
            this.handleWaterMovement();
        }

        if (!this.worldObj.isRemote && this.fallDistance > 3.0F && onGroundIn) {
            IBlockState blockState = this.worldObj.getBlockState(pos);
            Block block = blockState.getBlock();
            float f = MathHelper.ceiling_float_int(this.fallDistance - 3.0F);
            if (block.getMaterial() != Material.air) {
                double d = Math.min(0.2F + f / 15.0F, 10.0F);
                if (d > 2.5) {
                    d = 2.5;
                }

                int n = (int)(150.0 * d);
                ((WorldServer)this.worldObj)
                    .spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, n, 0.0, 0.0, 0.0, 0.15F, Block.getStateId(blockState));
            }
        }

        super.updateFallState(y, onGroundIn, blockIn, pos);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    public void onEntityUpdate() {
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("livingEntityBaseTick");
        boolean flag = this instanceof EntityPlayer;
        if (this.isEntityAlive()) {
            if (this.isEntityInsideOpaqueBlock()) {
                this.attackEntityFrom(DamageSource.inWall, 1.0F);
            } else if (flag && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox())) {
                double d = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();
                if (d < 0.0) {
                    this.attackEntityFrom(DamageSource.inWall, Math.max(1, MathHelper.floor_double(-d * this.worldObj.getWorldBorder().getDamageAmount())));
                }
            }
        }

        if (this.isImmuneToFire() || this.worldObj.isRemote) {
            this.extinguish();
        }

        boolean flag2 = flag && ((EntityPlayer)this).capabilities.disableDamage;
        if (this.isEntityAlive()) {
            if (this.isInsideOfMaterial(Material.water)) {
                if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !flag2) {
                    this.setAir(this.decreaseAirSupply(this.getAir()));
                    if (this.getAir() == -20) {
                        this.setAir(0);

                        for (int i = 0; i < 8; i++) {
                            float f = this.rand.nextFloat() - this.rand.nextFloat();
                            float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                            float f3 = this.rand.nextFloat() - this.rand.nextFloat();
                            this.worldObj
                                .spawnParticle(
                                    EnumParticleTypes.WATER_BUBBLE,
                                    this.posX + f,
                                    this.posY + f2,
                                    this.posZ + f3,
                                    this.motionX,
                                    this.motionY,
                                    this.motionZ
                                );
                        }

                        this.attackEntityFrom(DamageSource.drown, 2.0F);
                    }
                }

                if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
                    this.mountEntity((Entity)null);
                }
            } else {
                this.setAir(300);
            }
        }

        if (this.isEntityAlive() && this.isWet()) {
            this.extinguish();
        }

        this.prevCameraPitch = this.cameraPitch;
        if (this.hurtTime > 0) {
            this.hurtTime--;
        }

        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
            this.hurtResistantTime--;
        }

        if (this.getHealth() <= 0.0F) {
            this.onDeathUpdate();
        }

        if (this.recentlyHit > 0) {
            this.recentlyHit--;
        } else {
            this.attackingPlayer = null;
        }

        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
            this.lastAttacker = null;
        }

        if (this.entityLivingToAttack != null) {
            if (!this.entityLivingToAttack.isEntityAlive()) {
                this.setRevengeTarget((EntityLivingBase)null);
            } else if (this.ticksExisted - this.revengeTimer > 100) {
                this.setRevengeTarget((EntityLivingBase)null);
            }
        }

        this.updatePotionEffects();
        this.prevMovedDistance = this.movedDistance;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationPitchHead = this.rotationPitchHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
        GetEntityEvent2 getEntityEvent2 = new GetEntityEvent2(this.entityLivingToAttack);
        getEntityEvent2.process();
    }

    public boolean isChild() {
        return false;
    }

    protected void onDeathUpdate() {
        this.deathTime++;
        if (this.deathTime == 20) {
            if (!this.worldObj.isRemote
                && (this.recentlyHit > 0 || this.isPlayer())
                && this.canDropLoot()
                && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                int experiencePoints = this.getExperiencePoints(this.attackingPlayer);

                while (experiencePoints > 0) {
                    int xpSplit = EntityXPOrb.getXPSplit(experiencePoints);
                    experiencePoints -= xpSplit;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xpSplit));
                }
            }

            this.setDead();

            for (int i = 0; i < 20; i++) {
                double d = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = this.rand.nextGaussian() * 0.02;
                this.worldObj
                    .spawnParticle(
                        EnumParticleTypes.EXPLOSION_NORMAL,
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        d,
                        d2,
                        d3
                    );
            }
        }
    }

    protected boolean canDropLoot() {
        return !this.isChild();
    }

    protected int decreaseAirSupply(int p_70682_1_) {
        int respiration = EnchantmentHelper.getRespiration(this);
        return respiration > 0 && this.rand.nextInt(respiration + 1) > 0 ? p_70682_1_ : p_70682_1_ - 1;
    }

    protected int getExperiencePoints(EntityPlayer player) {
        return 0;
    }

    protected boolean isPlayer() {
        return false;
    }

    public Random getRNG() {
        return this.rand;
    }

    public EntityLivingBase getAITarget() {
        return this.entityLivingToAttack;
    }

    public int getRevengeTimer() {
        return this.revengeTimer;
    }

    public void setRevengeTarget(EntityLivingBase livingBase) {
        this.entityLivingToAttack = livingBase;
        this.revengeTimer = this.ticksExisted;
    }

    public EntityLivingBase getLastAttacker() {
        return this.lastAttacker;
    }

    public int getLastAttackerTime() {
        return this.lastAttackerTime;
    }

    public void setLastAttacker(Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            this.lastAttacker = (EntityLivingBase)entityIn;
        } else {
            this.lastAttacker = null;
        }

        this.lastAttackerTime = this.ticksExisted;
    }

    public int getAge() {
        return this.entityAge;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("HealF", this.getHealth());
        tagCompound.setShort("Health", (short)Math.ceil(this.getHealth()));
        tagCompound.setShort("HurtTime", (short)this.hurtTime);
        tagCompound.setInteger("HurtByTimestamp", this.revengeTimer);
        tagCompound.setShort("DeathTime", (short)this.deathTime);
        tagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

        for (ItemStack stack : this.getInventory()) {
            if (stack != null) {
                this.attributeMap.removeAttributeModifiers(stack.getAttributeModifiers());
            }
        }

        tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

        for (ItemStack stack2 : this.getInventory()) {
            if (stack2 != null) {
                this.attributeMap.applyAttributeModifiers(stack2.getAttributeModifiers());
            }
        }

        if (!this.activePotionsMap.isEmpty()) {
            NBTTagList tagList = new NBTTagList();

            for (PotionEffect effect : this.activePotionsMap.values()) {
                tagList.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            tagCompound.setTag("ActiveEffects", tagList);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));
        if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
            SharedMonsterAttributes.func_151475_a(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
        }

        if (tagCompund.hasKey("ActiveEffects", 9)) {
            NBTTagList tagList = tagCompund.getTagList("ActiveEffects", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(compoundTagAt);
                if (effect != null) {
                    this.activePotionsMap.put(effect.getPotionID(), effect);
                }
            }
        }

        if (tagCompund.hasKey("HealF", 99)) {
            this.setHealth(tagCompund.getFloat("HealF"));
        } else {
            NBTBase tag = tagCompund.getTag("Health");
            if (tag == null) {
                this.setHealth(this.getMaxHealth());
            } else if (tag.getId() == 5) {
                this.setHealth(((NBTTagFloat)tag).getFloat());
            } else if (tag.getId() == 2) {
                this.setHealth(((NBTTagShort)tag).getShort());
            }
        }

        this.hurtTime = tagCompund.getShort("HurtTime");
        this.deathTime = tagCompund.getShort("DeathTime");
        this.revengeTimer = tagCompund.getInteger("HurtByTimestamp");
    }

    protected void updatePotionEffects() {
        Iterator it = this.activePotionsMap.keySet().iterator();

        while (it.hasNext()) {
            Integer integer = (Integer)it.next();
            PotionEffect effect = this.activePotionsMap.get(integer);
            if (!effect.onUpdate(this)) {
                if (!this.worldObj.isRemote) {
                    it.remove();
                    this.onFinishedPotionEffect(effect);
                }
            } else if (effect.getDuration() % 600 == 0) {
                this.onChangedPotionEffect(effect, false);
            }
        }

        if (this.potionsNeedUpdate) {
            if (!this.worldObj.isRemote) {
                this.updatePotionMetadata();
            }

            this.potionsNeedUpdate = false;
        }

        int watchableObjectInt = this.dataWatcher.getWatchableObjectInt(7);
        boolean flag = this.dataWatcher.getWatchableObjectByte(8) > 0;
        if (watchableObjectInt > 0) {
            boolean flag2 = false;
            if (!this.isInvisible()) {
                flag2 = this.rand.nextBoolean();
            } else {
                flag2 = this.rand.nextInt(15) == 0;
            }

            if (flag) {
                flag2 &= this.rand.nextInt(5) == 0;
            }

            if (flag2 && watchableObjectInt > 0) {
                double d = (watchableObjectInt >> 16 & 0xFF) / 255.0;
                double d2 = (watchableObjectInt >> 8 & 0xFF) / 255.0;
                double d3 = (watchableObjectInt >> 0 & 0xFF) / 255.0;
                this.worldObj
                    .spawnParticle(
                        flag ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB,
                        this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                        this.posY + this.rand.nextDouble() * this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                        d,
                        d2,
                        d3
                    );
            }
        }
    }

    public void updatePotionMetadata() {
        if (this.activePotionsMap.isEmpty()) {
            this.resetPotionEffectMetadata();
            this.setInvisible(false);
        } else {
            int n = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
            this.dataWatcher.updateObject(8, (byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0));
            this.dataWatcher.updateObject(7, n);
            this.setInvisible(this.isPotionActive(Potion.invisibility.id));
        }
    }

    protected void resetPotionEffectMetadata() {
        this.dataWatcher.updateObject(8, (byte)0);
        this.dataWatcher.updateObject(7, 0);
    }

    public void clearActivePotions() {
        Iterator it = this.activePotionsMap.keySet().iterator();

        while (it.hasNext()) {
            Integer integer = (Integer)it.next();
            PotionEffect effect = this.activePotionsMap.get(integer);
            if (!this.worldObj.isRemote) {
                it.remove();
                this.onFinishedPotionEffect(effect);
            }
        }
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(int potionId) {
        return this.activePotionsMap.containsKey(potionId);
    }

    public boolean isPotionActive(Potion potionIn) {
        return this.activePotionsMap.containsKey(potionIn.id);
    }

    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return this.activePotionsMap.get(potionIn.id);
    }

    public void addPotionEffect(PotionEffect potioneffectIn) {
        if (this.isPotionApplicable(potioneffectIn)) {
            if (this.activePotionsMap.containsKey(potioneffectIn.getPotionID())) {
                this.activePotionsMap.get(potioneffectIn.getPotionID()).combine(potioneffectIn);
                this.onChangedPotionEffect(this.activePotionsMap.get(potioneffectIn.getPotionID()), true);
            } else {
                this.activePotionsMap.put(potioneffectIn.getPotionID(), potioneffectIn);
                this.onNewPotionEffect(potioneffectIn);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            int potionID = potioneffectIn.getPotionID();
            if (potionID == Potion.regeneration.id || potionID == Potion.poison.id) {
                return false;
            }
        }

        return true;
    }

    public boolean isEntityUndead() {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    public void removePotionEffectClient(int potionId) {
        this.activePotionsMap.remove(potionId);
    }

    public void removePotionEffect(int potionId) {
        PotionEffect effect = this.activePotionsMap.remove(potionId);
        if (effect != null) {
            this.onFinishedPotionEffect(effect);
        }
    }

    public void onNewPotionEffect(PotionEffect id) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    public void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
        this.potionsNeedUpdate = true;
        if (p_70695_2_ && !this.worldObj.isRemote) {
            Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
            Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    public void onFinishedPotionEffect(PotionEffect p_70688_1_) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[p_70688_1_.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), p_70688_1_.getAmplifier());
        }
    }

    public void heal(float healAmount) {
        float health = this.getHealth();
        if (health > 0.0F) {
            this.setHealth(health + healAmount);
        }
    }

    public final float getHealth() {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float health) {
        this.dataWatcher.updateObject(6, MathHelper.clamp_float(health, 0.0F, this.getMaxHealth()));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (this.worldObj.isRemote) {
            return false;
        }
        this.entityAge = 0;
        if (this.getHealth() <= 0.0F) {
            return false;
        }
        if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
            return false;
        }
        if ((source == DamageSource.anvil || source == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
            this.getEquipmentInSlot(4).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
            amount *= 0.75F;
        }

        this.limbSwingAmount = 1.5F;
        boolean flag = true;
        if (this.hurtResistantTime > this.maxHurtResistantTime / 2.0F) {
            if (amount <= this.lastDamage) {
                return false;
            }

            this.damageEntity(source, amount - this.lastDamage);
            this.lastDamage = amount;
            flag = false;
        } else {
            this.lastDamage = amount;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(source, amount);
            this.hurtTime = this.maxHurtTime = 10;
            this.ticksSinceLastDamage = 0;
        }

        this.attackedAtYaw = 0.0F;
        Entity entity = source.getEntity();
        if (entity != null) {
            if (entity instanceof EntityLivingBase) {
                this.setRevengeTarget((EntityLivingBase)entity);
            }

            if (entity instanceof EntityPlayer) {
                this.recentlyHit = 100;
                this.attackingPlayer = (EntityPlayer)entity;
            } else if (entity instanceof EntityWolf entity2 && entity2.isTamed()) {
                this.recentlyHit = 100;
                this.attackingPlayer = null;
            }
        }

        if (flag) {
            this.worldObj.setEntityState(this, (byte)2);
            if (source != DamageSource.drown) {
                this.setBeenAttacked();
            }

            if (entity != null) {
                double d = entity.posX - this.posX;

                double d2;
                for (d2 = entity.posZ - this.posZ; d * d + d2 * d2 < 1.0E-4; d2 = (Math.random() - Math.random()) * 0.01) {
                    d = (Math.random() - Math.random()) * 0.01;
                }

                this.attackedAtYaw = (float)(MathHelper.func_181159_b(d2, d) * 180.0 / Math.PI - this.rotationYaw);
                this.knockBack(entity, amount, d, d2);
            } else {
                this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
            }
        }

        if (this.getHealth() <= 0.0F) {
            String deathSound = this.getDeathSound();
            if (flag && deathSound != null) {
                this.playSound(deathSound, this.getSoundVolume(), this.getSoundPitch());
            }

            this.onDeath(source);
        } else {
            String hurtSound = this.getHurtSound();
            if (flag && hurtSound != null) {
                this.playSound(hurtSound, this.getSoundVolume(), this.getSoundPitch());
            }
        }

        return true;
    }

    public void renderBrokenItemStack(ItemStack stack) {
        this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

        for (int i = 0; i < 5; i++) {
            Vec3 vec = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec = vec.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec = vec.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            double d = -this.rand.nextFloat() * 0.6 - 0.3;
            Vec3 vec2 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec2 = vec2.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec2 = vec2.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            vec2 = vec2.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
            this.worldObj
                .spawnParticle(
                    EnumParticleTypes.ITEM_CRACK,
                    vec2.xCoord,
                    vec2.yCoord,
                    vec2.zCoord,
                    vec.xCoord,
                    vec.yCoord + 0.05,
                    vec.zCoord,
                    Item.getIdFromItem(stack.getItem())
                );
        }
    }

    public EntityLivingBase getAttackingEntity() {
        return (EntityLivingBase)(this._combatTracker.func_94550_c() != null
            ? this._combatTracker.func_94550_c()
            : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
    }

    public void onDeath(DamageSource cause) {
        Entity entity = cause.getEntity();
        EntityLivingBase attackingEntity = this.getAttackingEntity();
        if (this.scoreValue >= 0 && attackingEntity != null) {
            attackingEntity.addToPlayerScore(this, this.scoreValue);
        }

        if (this.worldObj != null && this.worldObj.isRemote && this instanceof EntityPlayer) {
            SomberHelper.handleEntityPlayer((EntityPlayer)this);
        }

        GetEntityEvent getEntityEvent = new GetEntityEvent(this, cause);
        getEntityEvent.process();
        if (entity != null) {
            entity.onKillEntity(this);
        }

        this.dead = true;
        this.getCombatTracker().reset();
        if (!this.worldObj.isRemote) {
            int n = 0;
            if (entity instanceof EntityPlayer) {
                n = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
            }

            if (this.canDropLoot() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                this.dropFewItems(this.recentlyHit > 0, n);
                this.dropEquipment(this.recentlyHit > 0, n);
                if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + n * 0.01F) {
                    this.addRandomDrop();
                }
            }
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

    protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {
    }

    public void knockBack(Entity entityIn, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
            this.isAirBorne = true;
            float f = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
            float f2 = 0.4F;
            this.motionX /= 2.0;
            this.motionY /= 2.0;
            this.motionZ /= 2.0;
            this.motionX -= p_70653_3_ / f * f2;
            this.motionY += f2;
            this.motionZ -= p_70653_5_ / f * f2;
            if (this.motionY > 0.4F) {
                this.motionY = 0.4F;
            }
        }
    }

    protected String getHurtSound() {
        return "game.neutral.hurt";
    }

    protected String getDeathSound() {
        return "game.neutral.die";
    }

    protected void addRandomDrop() {
    }

    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
    }

    public boolean isOnLadder() {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
        int n3 = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlockState(new BlockPos(n, n2, n3)).getBlock();
        return (block == Blocks.ladder || block == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
    }

    @Override
    public boolean isEntityAlive() {
        return !this.isDead && this.getHealth() > 0.0F;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier);
        PotionEffect activePotionEffect = this.getActivePotionEffect(Potion.jump);
        float f = activePotionEffect != null ? activePotionEffect.getAmplifier() + 1 : 0.0F;
        int n = MathHelper.ceiling_float_int((distance - 3.0F - f) * damageMultiplier);
        if (n > 0) {
            this.playSound(this.getFallSoundString(n), 1.0F, 1.0F);
            this.attackEntityFrom(DamageSource.fall, n);
            int n2 = MathHelper.floor_double(this.posX);
            int n3 = MathHelper.floor_double(this.posY - 0.2F);
            int n4 = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlockState(new BlockPos(n2, n3, n4)).getBlock();
            if (block.getMaterial() != Material.air) {
                Block.SoundType stepSound = block.stepSound;
                this.playSound(stepSound.getStepSound(), stepSound.getVolume() * 0.5F, stepSound.getFrequency() * 0.75F);
            }
        }
    }

    protected String getFallSoundString(int damageValue) {
        return damageValue > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    @Override
    public void performHurtAnimation() {
        this.hurtTime = this.maxHurtTime = 10;
        this.ticksSinceLastDamage = 0;
        this.attackedAtYaw = 0.0F;
    }

    public int getTotalArmorValue() {
        int n = 0;

        for (ItemStack stack : this.getInventory()) {
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                int damageReduceAmount = ((ItemArmor)stack.getItem()).damageReduceAmount;
                n += damageReduceAmount;
            }
        }

        return n;
    }

    protected void damageArmor(float p_70675_1_) {
    }

    protected float applyArmorCalculations(DamageSource source, float damage) {
        if (!source.isUnblockable()) {
            int n = 25 - this.getTotalArmorValue();
            float f = damage * n;
            this.damageArmor(damage);
            damage = f / 25.0F;
        }

        return damage;
    }

    public float applyPotionDamageCalculations(DamageSource source, float damage) {
        if (source.isDamageAbsolute()) {
            return damage;
        }
        if (this.isPotionActive(Potion.resistance) && source != DamageSource.outOfWorld) {
            int n = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
            int n2 = 25 - n;
            float f = damage * n2;
            damage = f / 25.0F;
        }

        if (damage <= 0.0F) {
            return 0.0F;
        }
        int enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), source);
        if (enchantmentModifierDamage > 20) {
            enchantmentModifierDamage = 20;
        }

        if (enchantmentModifierDamage > 0 && enchantmentModifierDamage <= 20) {
            int n3 = 25 - enchantmentModifierDamage;
            float f2 = damage * n3;
            damage = f2 / 25.0F;
        }

        return damage;
    }

    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) {
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            float f = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAmount - f));
            if (f != 0.0F) {
                float health = this.getHealth();
                this.setHealth(health - f);
                this.getCombatTracker().trackDamage(damageSrc, health, f);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - f);
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this._combatTracker;
    }

    public EntityLivingBase func_94060_bK() {
        return (EntityLivingBase)(this._combatTracker.func_94550_c() != null
            ? this._combatTracker.func_94550_c()
            : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
    }

    public final float getMaxHealth() {
        return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    public final int getArrowCountInEntity() {
        return this.dataWatcher.getWatchableObjectByte(9);
    }

    public final void setArrowCountInEntity(int count) {
        this.dataWatcher.updateObject(9, (byte)count);
    }

    private int getArmSwingAnimationEnd() {
        return this.isPotionActive(Potion.digSpeed)
            ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1
            : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    public void swingItem() {
        Animations instance = Animations.getInstance();
        if (instance != null) {
            instance.handleEntityLivingBase(this);
        }

        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;
            if (this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    @Override
    public void handleHealthUpdate(byte id) {
        if (id == 2) {
            this.limbSwingAmount = 1.5F;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.hurtTime = this.maxHurtTime = 10;
            this.ticksSinceLastDamage = 0;
            this.attackedAtYaw = 0.0F;
            String hurtSound = this.getHurtSound();
            if (hurtSound != null) {
                this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.attackEntityFrom(DamageSource.generic, 0.0F);
        } else if (id == 3) {
            String deathSound = this.getDeathSound();
            if (deathSound != null) {
                this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.setHealth(0.0F);
            this.onDeath(DamageSource.generic);
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    protected void kill() {
        this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
    }

    protected void updateArmSwingProgress() {
        int armSwingAnimationEnd = this.getArmSwingAnimationEnd();
        if (this.isSwingInProgress) {
            this.swingProgressInt++;
            if (this.swingProgressInt >= armSwingAnimationEnd) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        } else {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float)this.swingProgressInt / armSwingAnimationEnd;
    }

    public IAttributeInstance getEntityAttribute(IAttribute attribute) {
        return this.getAttributeMap().getAttributeInstance(attribute);
    }

    public BaseAttributeMap getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new ServersideAttributeMap();
        }

        return this.attributeMap;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public abstract ItemStack getHeldItem();

    public abstract ItemStack getEquipmentInSlot(int slotIn);

    public abstract ItemStack getCurrentArmor(int slotIn);

    @Override
    public abstract void setCurrentItemOrArmor(int slotIn, ItemStack stack);

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (entityAttribute.getModifier(sprintingSpeedBoostModifierUUID) != null) {
            entityAttribute.removeModifier(sprintingSpeedBoostModifier);
        }

        if (sprinting) {
            entityAttribute.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    @Override
    public abstract ItemStack[] getInventory();

    protected float getSoundVolume() {
        return 1.0F;
    }

    public float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isMovementBlocked() {
        return this.getHealth() <= 0.0F;
    }

    public void dismountEntity(Entity p_110145_1_) {
        double x = p_110145_1_.posX;
        double d = p_110145_1_.getEntityBoundingBox().minY + p_110145_1_.height;
        double z = p_110145_1_.posZ;
        int n = 1;

        for (int i = -n; i <= n; i++) {
            for (int j = -n; j < n; j++) {
                if (i != 0 || j != 0) {
                    int n2 = (int)(this.posX + i);
                    int n3 = (int)(this.posZ + j);
                    AxisAlignedBB box = this.getEntityBoundingBox().offset(i, 1.0, j);
                    if (this.worldObj.func_147461_a(box).isEmpty()) {
                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(n2, (int)this.posY, n3))) {
                            this.setPositionAndUpdate(this.posX + i, this.posY + 1.0, this.posZ + j);
                            return;
                        }

                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(n2, (int)this.posY - 1, n3))
                            || this.worldObj.getBlockState(new BlockPos(n2, (int)this.posY - 1, n3)).getBlock().getMaterial() == Material.water) {
                            x = this.posX + i;
                            d = this.posY + 1.0;
                            z = this.posZ + j;
                        }
                    }
                }
            }
        }

        this.setPositionAndUpdate(x, d, z);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    protected float getJumpUpwardsMotion() {
        return 0.42F;
    }

    public void jump() {
        this.motionY = this.getJumpUpwardsMotion();
        if (this.isPotionActive(Potion.jump)) {
            this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        if (this instanceof EntityPlayerSP) {
            GetYawEvent2 getYawEvent2 = new GetYawEvent2(this.getJumpUpwardsMotion(), this.rotationYaw, this.isSprinting());
            getYawEvent2.process();
        }

        if (this.isSprinting()) {
            boolean flag = false;
            float yaw = this.rotationYaw;
            Float val = null;
            if (this instanceof EntityPlayerSP) {
                val = MovementFix.computeFloatType3();
                if (val != null && !val.isNaN()) {
                    flag = true;
                    this.rotationYaw = val;
                }
            }

            float f = this.rotationYaw * (float) (Math.PI / 180.0);
            this.motionX = this.motionX - MathHelper.sinVanilla(f) * 0.2F;
            this.motionZ = this.motionZ + MathHelper.cosVanilla(f) * 0.2F;
            if (flag) {
                this.rotationYaw = yaw;
            }
        }

        this.isAirBorne = true;
    }

    protected void updateAITick() {
        this.motionY += 0.04F;
    }

    protected void handleJumpLava() {
        this.motionY += 0.04F;
    }

    public void moveEntityWithHeading(float strafe, float forward) {
        if (this.isServerWorld()) {
            if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
                if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
                    float f = 0.91F;
                    if (this.onGround) {
                        f = this.worldObj
                                .getBlockState(
                                    new BlockPos(
                                        MathHelper.floor_double(this.posX),
                                        MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                                        MathHelper.floor_double(this.posZ)
                                    )
                                )
                                .getBlock()
                                .slipperiness
                            * 0.91F;
                    }

                    float f2 = 0.16277136F / (f * f * f);
                    float f3;
                    if (this.onGround) {
                        f3 = this.getAIMoveSpeed() * f2;
                    } else {
                        f3 = this.jumpMovementFactor;
                    }

                    boolean flag = false;
                    float yaw = this.rotationYaw;
                    Float val = null;
                    if (this instanceof EntityPlayerSP) {
                        val = MovementFix.computeFloatType3();
                        if (val != null && !val.isNaN()) {
                            flag = true;
                            this.rotationYaw = val;
                        }
                    }

                    this.moveFlying(strafe, forward, f3);
                    if (flag) {
                        this.rotationYaw = yaw;
                    }

                    float f4 = 0.91F;
                    if (this.onGround) {
                        f4 = this.worldObj
                                .getBlockState(
                                    new BlockPos(
                                        MathHelper.floor_double(this.posX),
                                        MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                                        MathHelper.floor_double(this.posZ)
                                    )
                                )
                                .getBlock()
                                .slipperiness
                            * 0.91F;
                    }

                    if (this.isOnLadder()) {
                        float f5 = 0.15F;
                        this.motionX = MathHelper.clamp_double(this.motionX, -f5, f5);
                        this.motionZ = MathHelper.clamp_double(this.motionZ, -f5, f5);
                        this.fallDistance = 0.0F;
                        if (this.motionY < -0.15) {
                            this.motionY = -0.15;
                        }

                        boolean flag2 = this.isSneaking() && this instanceof EntityPlayer;
                        if (flag2 && this.motionY < 0.0) {
                            this.motionY = 0.0;
                        }
                    }

                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    if (this.isCollidedHorizontally && this.isOnLadder()) {
                        this.motionY = 0.2;
                    }

                    if (this.worldObj.isRemote
                        && (
                            !this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ))
                                || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded()
                        )) {
                        if (this.posY > 0.0) {
                            this.motionY = -0.1;
                        } else {
                            this.motionY = 0.0;
                        }
                    } else {
                        this.motionY -= 0.08;
                    }

                    this.motionY *= 0.98F;
                    this.motionX *= f4;
                    this.motionZ *= f4;
                } else {
                    double y = this.posY;
                    boolean flag3 = false;
                    float yaw2 = this.rotationYaw;
                    Float val2 = null;
                    if (this instanceof EntityPlayerSP) {
                        val2 = MovementFix.computeFloatType3();
                        if (val2 != null && !val2.isNaN()) {
                            flag3 = true;
                            this.rotationYaw = val2;
                        }
                    }

                    this.moveFlying(strafe, forward, 0.02F);
                    if (flag3) {
                        this.rotationYaw = yaw2;
                    }

                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                    this.motionY -= 0.02;
                    if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + y, this.motionZ)) {
                        this.motionY = 0.3F;
                    }
                }
            } else {
                double y2 = this.posY;
                float f6 = 0.8F;
                float f7 = 0.02F;
                float depthStriderModifier = EnchantmentHelper.getDepthStriderModifier(this);
                if (depthStriderModifier > 3.0F) {
                    depthStriderModifier = 3.0F;
                }

                if (!this.onGround) {
                    depthStriderModifier *= 0.5F;
                }

                if (depthStriderModifier > 0.0F) {
                    f6 += (0.54600006F - f6) * depthStriderModifier / 3.0F;
                    f7 += (this.getAIMoveSpeed() * 1.0F - f7) * depthStriderModifier / 3.0F;
                }

                boolean flag4 = false;
                float yaw3 = this.rotationYaw;
                Float val3 = null;
                if (this instanceof EntityPlayerSP) {
                    val3 = MovementFix.computeFloatType3();
                    if (val3 != null && !val3.isNaN()) {
                        flag4 = true;
                        this.rotationYaw = val3;
                    }
                }

                this.moveFlying(strafe, forward, f7);
                if (flag4) {
                    this.rotationYaw = yaw3;
                }

                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= f6;
                this.motionY *= 0.8F;
                this.motionZ *= f6;
                this.motionY -= 0.02;
                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + y2, this.motionZ)) {
                    this.motionY = 0.3F;
                }
            }
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d = this.posX - this.prevPosX;
        double d2 = this.posZ - this.prevPosZ;
        float f8 = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
        if (f8 > 1.0F) {
            f8 = 1.0F;
        }

        this.limbSwingAmount = this.limbSwingAmount + (f8 - this.limbSwingAmount) * 0.4F;
        this.limbSwing = this.limbSwing + this.limbSwingAmount;
    }

    public float getAIMoveSpeed() {
        return this.landMovementFactor;
    }

    public void setAIMoveSpeed(float speedIn) {
        this.landMovementFactor = speedIn;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.setLastAttacker(entityIn);
        return false;
    }

    public boolean isPlayerSleeping() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            int arrowCountInEntity = this.getArrowCountInEntity();
            if (arrowCountInEntity > 0) {
                if (this.arrowHitTimer <= 0) {
                    this.arrowHitTimer = 20 * (30 - arrowCountInEntity);
                }

                this.arrowHitTimer--;
                if (this.arrowHitTimer <= 0) {
                    this.setArrowCountInEntity(arrowCountInEntity - 1);
                }
            }

            for (int i = 0; i < 5; i++) {
                ItemStack stack = this.previousEquipment[i];
                ItemStack equipmentInSlot = this.getEquipmentInSlot(i);
                if (!ItemStack.areItemStacksEqual(equipmentInSlot, stack)) {
                    ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), i, equipmentInSlot));
                    if (stack != null) {
                        this.attributeMap.removeAttributeModifiers(stack.getAttributeModifiers());
                    }

                    if (equipmentInSlot != null) {
                        this.attributeMap.applyAttributeModifiers(equipmentInSlot.getAttributeModifiers());
                    }

                    this.previousEquipment[i] = equipmentInSlot == null ? null : equipmentInSlot.copy();
                }
            }

            if (this.ticksExisted % 20 == 0) {
                this.getCombatTracker().reset();
            }
        }

        this.onLivingUpdate();
        double d = this.posX - this.prevPosX;
        double d2 = this.posZ - this.prevPosZ;
        float f = (float)(d * d + d2 * d2);
        float renderYawOffset = this.renderYawOffset;
        float f2 = 0.0F;
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        float f3 = 0.0F;
        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float)Math.sqrt(f) * 3.0F;
            renderYawOffset = (float)MathHelper.func_181159_b(d2, d) * 180.0F / (float) Math.PI - 90.0F;
        }

        if (this.swingProgress > 0.0F) {
            renderYawOffset = this.rotationYaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.onGroundSpeedFactor = this.onGroundSpeedFactor + (f3 - this.onGroundSpeedFactor) * 0.3F;
        this.worldObj.theProfiler.startSection("headTurn");
        float f4 = this.func_110146_f(renderYawOffset, f2);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
            this.prevRotationYawHead -= 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
            this.prevRotationYawHead += 360.0F;
        }

        this.worldObj.theProfiler.endSection();
        this.movedDistance += f4;
    }

    protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
        float f = MathHelper.wrapAngleTo180_float(p_110146_1_ - this.renderYawOffset);
        this.renderYawOffset += f * 0.3F;
        float f2 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
        boolean flag = f2 < -90.0F || f2 >= 90.0F;
        if (f2 < -75.0F) {
            f2 = -75.0F;
        }

        if (f2 >= 75.0F) {
            f2 = 75.0F;
        }

        this.renderYawOffset = this.rotationYaw - f2;
        if (f2 * f2 > 2500.0F) {
            this.renderYawOffset += f2 * 0.2F;
        }

        if (flag) {
            p_110146_2_ *= -1.0F;
        }

        return p_110146_2_;
    }

    public void onLivingUpdate() {
        if (this.jumpTicks > 0) {
            this.jumpTicks--;
        }

        if (this.newPosRotationIncrements > 0) {
            double d = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
            double d2 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
            double d3 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
            double d4 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d4 / this.newPosRotationIncrements);
            this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
            this.newPosRotationIncrements--;
            this.setPosition(d, d2, d3);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else if (!this.isServerWorld()) {
            this.motionX *= 0.98;
            this.motionY *= 0.98;
            this.motionZ *= 0.98;
        }

        if (Math.abs(this.motionX) < 0.005) {
            this.motionX = 0.0;
        }

        if (Math.abs(this.motionY) < 0.005) {
            this.motionY = 0.0;
        }

        if (Math.abs(this.motionZ) < 0.005) {
            this.motionZ = 0.0;
        }

        this.worldObj.theProfiler.startSection("ai");
        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if (this.isServerWorld()) {
            this.worldObj.theProfiler.startSection("newAi");
            this.updateEntityActionState();
            this.worldObj.theProfiler.endSection();
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");
        if (this.isJumping) {
            if (this.isInWater()) {
                this.updateAITick();
            } else if (this.isInLava()) {
                this.handleJumpLava();
            } else if (this.onGround && this.jumpTicks == 0) {
                this.jump();
                this.jumpTicks = 10;
            }
        } else {
            this.jumpTicks = 0;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");
        if (!this.worldObj.isRemote) {
            this.collideWithNearbyEntities();
        }

        this.worldObj.theProfiler.endSection();
    }

    public void updateEntityActionState() {
    }

    protected void collideWithNearbyEntities() {
        List entitiesInAABBexcluding = this.worldObj
            .getEntitiesInAABBexcluding(
                this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                    public boolean apply(Entity p_apply_1_) {
                        return p_apply_1_.canBePushed();
                    }
                })
            );
        if (!entitiesInAABBexcluding.isEmpty()) {
            for (int i = 0; i < entitiesInAABBexcluding.size(); i++) {
                Entity entity = (Entity)entitiesInAABBexcluding.get(i);
                this.collideWithEntity(entity);
            }
        }
    }

    public void collideWithEntity(Entity p_82167_1_) {
        p_82167_1_.applyEntityCollision(this);
    }

    @Override
    public void mountEntity(Entity entityIn) {
        if (this.ridingEntity != null && entityIn == null) {
            if (!this.worldObj.isRemote) {
                this.dismountEntity(this.ridingEntity);
            }

            if (this.ridingEntity != null) {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        } else {
            super.mountEntity(entityIn);
        }
    }

    @Override
    public void updateRidden() {
        super.updateRidden();
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        this.onGroundSpeedFactor = 0.0F;
        this.fallDistance = 0.0F;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
        this.newRotationYaw = yaw;
        this.newRotationPitch = pitch;
        this.newPosRotationIncrements = posRotationIncrements;
    }

    public void setJumping(boolean p_70637_1_) {
        this.isJumping = p_70637_1_;
    }

    public void onItemPickup(Entity p_71001_1_, int p_71001_2_) {
        if (!p_71001_1_.isDead && !this.worldObj.isRemote) {
            EntityTracker entityTracker = ((WorldServer)this.worldObj).getEntityTracker();
            if (p_71001_1_ instanceof EntityItem) {
                entityTracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }

            if (p_71001_1_ instanceof EntityArrow) {
                entityTracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }

            if (p_71001_1_ instanceof EntityXPOrb) {
                entityTracker.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
        }
    }

    public boolean canEntityBeSeen(Entity entityIn) {
        return this.worldObj
                .rayTraceBlocks(
                    new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ), new Vec3(entityIn.posX, entityIn.posY + entityIn.getEyeHeight(), entityIn.posZ)
                )
            == null;
    }

    @Override
    public Vec3 getLookVec() {
        return this.getLook(1.0F);
    }

    @Override
    public Vec3 getLook(float partialTicks) {
        if (partialTicks == 1.0F) {
            return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
        }
        float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
        float f2 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
        return this.getVectorForRotation(f, f2);
    }

    public float getSwingProgress(float partialTickTime) {
        float f = this.swingProgress - this.prevSwingProgress;
        if (f < 0.0F) {
            f++;
        }

        float f2 = this.prevSwingProgress + f * partialTickTime;
        Animations instance = Animations.getInstance();
        return instance != null && instance.checkState44() && instance.checkState5() ? instance.computeFloat(this, partialTickTime, f2) : f2;
    }

    public boolean isServerWorld() {
        return !this.worldObj.isRemote;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

    @Override
    protected void setBeenAttacked() {
        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    @Override
    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

    @Override
    public void setRotationYawHead(float rotation) {
        this.rotationYawHead = rotation;
    }

    @Override
    public void func_181013_g(float p_181013_1_) {
        this.renderYawOffset = p_181013_1_;
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float amount) {
        if (amount < 0.0F) {
            amount = 0.0F;
        }

        this.absorptionAmount = amount;
    }

    public Team getTeam() {
        return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
    }

    public boolean isOnSameTeam(EntityLivingBase otherEntity) {
        return this.isOnTeam(otherEntity.getTeam());
    }

    public boolean isOnTeam(Team p_142012_1_) {
        return this.getTeam() != null ? this.getTeam().isSameTeam(p_142012_1_) : false;
    }

    public void sendEnterCombat() {
    }

    public void sendEndCombat() {
    }

    protected void markPotionsDirty() {
        this.potionsNeedUpdate = true;
    }

    public Map<Integer, PotionEffect> getActivePotionsMap() {
        return this.activePotionsMap;
    }
}
