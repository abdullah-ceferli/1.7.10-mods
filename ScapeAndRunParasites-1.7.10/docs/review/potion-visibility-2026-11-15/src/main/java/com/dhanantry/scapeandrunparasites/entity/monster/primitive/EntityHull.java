/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;

import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityHull
extends EntityPPrimitive {
    private int timer = 0;
    private static final DataParameter<Boolean> CAM = EntityDataManager.createKey(EntityHull.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityHull.class, (DataSerializer)DataSerializers.VARINT);
    private EntityLivingBase targetedEntity;
    private int pulling;
    private boolean canPull;

    public EntityHull(World worldIn) {
        super(worldIn);
        this.setSize(1.3f, 1.7f);
        this.attackSpeedT = 6;
    }

    @Override
    public int getParasiteIDRegister() {
        return 7;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(CAM, (Object)false);
        EntityDataManager.of(this).register(TARGET_ENTITY, (Object)0);
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.095));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIEvade(this, 40, 5, 8.0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.HULL_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.HULL_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.HULL_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.HULL_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.primitiveFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.srpTicks == 10) {
                float currentH = this.getHealth() / this.getMaxHealth();
                if (this.getSSS()) {
                    this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.invisibility, 25, 0, false, false));
                    this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 25, 2, false, false));
                    if (this.ticksExisted % 2 == 0) {
                        this.playSound(SRPSounds.HULL_C, 0.2f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                    }
                    if ((double)currentH < SRPConfigMobs.hullNeededHealth) {
                        this.setSSS(false);
                    }
                } else if ((double)currentH >= SRPConfigMobs.hullNeededHealth) {
                    ++this.timer;
                    if ((float)this.timer > SRPConfigMobs.hullNeededTime) {
                        this.setSSS(true);
                        this.particleStatus((byte)6);
                        this.timer = 0;
                    }
                }
                if (this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
                    ParasiteEventEntity.spawnNext(this, new EntityHullAdapted(this.worldObj), true, true);
                }
            }
            if (!this.canPull) {
                --this.pulling;
                if (this.pulling == 0) {
                    this.canPull = true;
                }
            }
            if (this.getAttackTarget() != null) {
                if (!this.getAttackTarget().isEntityAlive()) {
                    this.setAttackTarget(null);
                    this.setTargetedEntity(0);
                } else if (this.canEntityBeSeen((Entity)this.getAttackTarget()) && this.getDistanceSqToEntity((Entity)this.getAttackTarget()) > 0.0 && this.canPull && this.getTargetedEntity() != null) {
                    this.getAttackTarget().addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 20, 1, false, false));
                    this.getAttackTarget().addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.digSlowdown, 20, 1, false, false));
                    this.lookAt((Entity)this.getTargetedEntity());
                    this.attackEntityAsMobMinimum(this.getAttackTarget(), 0.02f);
                    this.setParasiteStatus(3);
                    ++this.pulling;
                    if (this.pulling > 200 || this.getDistanceSqToEntity((Entity)this.getAttackTarget()) > 9.0) {
                        this.setTargetedEntity(0);
                        this.canPull = false;
                    }
                } else {
                    this.setTargetedEntity(0);
                }
            } else {
                this.setTargetedEntity(0);
            }
        }
        if (this.getTargetedEntity() != null && this.getDistanceSqToEntity((Entity)this.getTargetedEntity()) > 0.0) {
            EntityLivingBase target = this.getTargetedEntity();
            target.dismountRidingEntity();
            double str = 0.3;
            double deltaX = this.posX - target.posX;
            double deltaY = this.posY - target.posY;
            double deltaZ = this.posZ - target.posZ;
            str = 0.13;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            target.motionX += (deltaX /= distance) * str;
            target.motionY += (deltaY /= distance) * str;
            target.motionZ += (deltaZ /= distance) * str;
        }
    }

    @Override
    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2 || k == 3) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2 || k == 3) {
                if (this.getAttackTarget() != null) {
                    if (!this.getAttackTarget().isEntityAlive()) {
                        this.setAttackTarget(null);
                        this.setParasiteStatus(0);
                    } else if (!this.canPull) {
                        this.setParasiteStatus(Math.min(k, 2));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setAttackTarget(null);
                }
            }
        }
    }

    public void setTargetedEntity(int entityId) {
        if (!this.canPull && entityId != 0) {
            return;
        }
        this.pulling = 0;
        this.canPull = true;
        EntityDataManager.of(this).set(TARGET_ENTITY, (Object)entityId);
    }

    public boolean hasTargetedEntity() {
        if (!this.canPull) {
            return false;
        }
        return (Integer)EntityDataManager.of(this).get(TARGET_ENTITY) != 0;
    }

    public EntityLivingBase getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.worldObj.isRemote) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(TARGET_ENTITY)).intValue());
            if (entity instanceof EntityLivingBase) {
                this.targetedEntity = (EntityLivingBase)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getAttackTarget();
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        this.setSSS(false);
        this.timer = 0;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            if (this.getSSS()) {
                float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() * SRPConfigMobs.hullStealthDamageMultiplier;
                if (entityIn instanceof EntityLivingBase) {
                    f += EnchantmentHelper.getModifierForCreature((ItemStack)this.getHeldItemMainhand(), (EnumCreatureAttribute)((EntityLivingBase)entityIn).getCreatureAttribute());
                }
                entityIn.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), f);
                this.setSSS(false);
                this.timer = 0;
            }
            if (!this.hasTargetedEntity()) {
                this.setTargetedEntity(entityIn.getEntityId());
                ((EntityLivingBase)entityIn).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.weakness, 60, 3, false, false));
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobMinimum(EntityLivingBase entityIn, float damage) {
        boolean flag = super.attackEntityAsMobMinimum(entityIn, damage);
        if (flag && this.getSSS()) {
            this.setSSS(false);
            this.timer = 0;
        }
        return flag;
    }

    public float getEyeHeight() {
        return 1.0f;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    public boolean getSSS() {
        return (Boolean)EntityDataManager.of(this).get(CAM);
    }

    public void setSSS(boolean in) {
        EntityDataManager.of(this).set(CAM, (Object)in);
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.worldObj.isRemote && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityHullAdapted(this.worldObj), true, true);
        }
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0 || this.isPotionActive(net.minecraft.potion.Potion.invisibility)) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.HULL_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.HULL_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.HULL_DEATH;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.hullOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP, 0.15f, 1.0f);
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    public void applyEntityCollision(Entity entityIn) {
        if (this.getTargetedEntity() != null && this.getTargetedEntity() == entityIn) {
            return;
        }
        super.applyEntityCollision(entityIn);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.rand.nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }
}

