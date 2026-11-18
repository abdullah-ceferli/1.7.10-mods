/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IRangedAttackMob
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import com.dhanantry.scapeandrunparasites.backport.navigation.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHostII
extends EntityPMalleable
implements IRangedAttackMob,
EntityCutomAttack {
    private boolean open;
    private float buried;
    private int blockI;
    private int buriedC;
    private static final DataParameter<Boolean> UP = EntityDataManager.createKey(EntityHostII.class, (DataSerializer)DataSerializers.BOOLEAN);
    private float attackTimer;
    private boolean up;
    private int border;
    private boolean skillshockwave;

    public EntityHostII(World worldIn) {
        super(worldIn);
        this.setSize(1.5f, 0.25f);
        this.borderOrb = -1;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.setPathPriority(PathNodeType.WATER, -1.0f);
        this.ignoreFrustumCheck = true;
        this.canModRender = 0;
        this.type = (byte)41;
        this.buried = 8.3f;
        this.stepHeight = 1.0f;
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, SRPConfig.adaptedWalls, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, SRPConfig.adaptedWalls, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
        this.experienceValue = SRPAttributes.XP_ADAPTED;
        this.canD = SRPConfig.adapteddespawn;
        this.damageCap = SRPConfig.adaptedCap;
        this.foodSteal = SRPConfig.adaptedFoodSteal;
        this.pointCap = SRPConfig.adaptedPointCap;
        this.pointReduction = SRPConfig.adaptedPointRed;
        this.chanceLearn = SRPConfig.adaptedChanceLe;
        this.chanceLearnFire = SRPConfig.adaptedChanceLeFire;
        this.DamageTypeCap = SRPConfig.adaptedPointDamCap;
        this.MiniDamage = SRPConfig.adaptedMinDamage;
        this.regen = SRPConfig.adaptedRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.adaptedOneMindDeathV;
        this.regenEff = 2;
        this.adaptationCap = 0.95f;
    }

    @Override
    public int getParasiteIDRegister() {
        return 75;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 0.0, 5.0, true));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIAttackMeleeRangeSwitch(this, 5.0f));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAttackRangedStatus(this, 1.3, 10, 16.0f, false));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 20, (int)SRPConfig.adaptedFollow, 2, false, 1, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(UP, (Object)false);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.HERD_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.HERD_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.12);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.HERD_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.adaptedFollow);
    }

    @Override
    public void onLivingUpdate() {
        if (this.isAIDisabled()) {
            this.setParasiteStatus(3);
            this.setBurrowed(true);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
            this.buriedC = 80;
            if (this.buriedC > 0) {
                --this.buriedC;
            }
            ++this.blockI;
            if (this.up) {
                this.attackTimer = (float)((double)this.attackTimer + 0.2);
                if (this.attackTimer > 1.0f) {
                    this.up = false;
                }
            } else {
                this.attackTimer = (float)((double)this.attackTimer - 0.1);
            }
            this.checkBurrowed();
            return;
        }
        super.onLivingUpdate();
        if (this.buriedC > 0) {
            --this.buriedC;
        }
        ++this.blockI;
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2);
            if (this.attackTimer > 1.0f) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1);
        }
        this.spawnRupters();
        this.checkBurrowed();
        this.checkSpeed();
        this.teleportByGround();
        if (!(this.getBurrowed() || this.posX == this.prevPosX && this.posZ == this.prevPosZ)) {
            this.spawnGroundParticles();
        }
    }

    private void spawnRupters() {
        if (!SRPConfigMobs.nuuhEnabled) {
            return;
        }
        if (!this.worldObj.isRemote && !this.getBurrowed()) {
            AxisAlignedBB axisalignedbb;
            List moblist;
            if (this.getAttackTarget() != null && this.rand.nextInt(150) == 0) {
                AxisAlignedBB axisalignedbb2 = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 16.0);
                List moblist2 = EntityQueries.entities(this.worldObj, EntityNuuh.class, axisalignedbb2);
                if (moblist2.size() <= 4) {
                    EntityNuuh out = new EntityNuuh(this.worldObj);
                    out.copyLocationAndAnglesFrom((Entity)this);
                    this.worldObj.spawnEntityInWorld((Entity)out);
                    out.setAttackTarget(this.getAttackTarget());
                }
            } else if (this.rand.nextInt(400) == 0 && (moblist = EntityQueries.entities(this.worldObj, EntityNuuh.class, axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 16.0))).size() <= 3) {
                EntityNuuh out = new EntityNuuh(this.worldObj);
                out.copyLocationAndAnglesFrom((Entity)this);
                this.worldObj.spawnEntityInWorld((Entity)out);
            }
        }
    }

    private void checkBurrowed() {
        if (this.getBurrowed()) {
            if (this.height < 7.5f) {
                this.setSize(1.5f, this.height + 0.09f);
            }
            if (this.buried >= 0.0f) {
                this.buried -= 0.1f;
                this.spawnGroundParticles();
                this.open = false;
                this.worldObj.setEntityState((Entity)this, (byte)12);
            } else {
                this.open = true;
                this.worldObj.setEntityState((Entity)this, (byte)11);
            }
        } else {
            if (this.height > 0.25f) {
                this.setSize(1.5f, this.height - 0.09f);
            }
            if (this.buried < 8.3f) {
                this.buried += 0.1f;
                this.spawnGroundParticles();
                this.open = false;
                this.worldObj.setEntityState((Entity)this, (byte)12);
            }
        }
    }

    private void checkSpeed() {
        if (!this.worldObj.isRemote) {
            if (this.getAttackTarget() != null) {
                if (this.getDistanceSqToEntity((Entity)this.getAttackTarget()) <= 9.0) {
                    this.setParasiteStatus(3);
                    this.setBurrowed(true);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
                    this.buriedC = 80;
                } else if (this.buriedC <= 0) {
                    this.setParasiteStatus(0);
                    this.setBurrowed(false);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.12);
                }
            } else if (this.getBurrowed() && this.buriedC <= 0) {
                this.setParasiteStatus(0);
                this.setBurrowed(false);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.12);
            }
            if (this.blockI > 20) {
                this.blockI = 0;
                IBlockState block = BlockStates.get(this.worldObj, EntityQueries.position(this).down());
                if (!(block.getBlock() instanceof IMetaName) && block.isFullBlock() && BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() == Blocks.air) {
                    BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.fromMeta(SRPBlocks.InfestRemain, 1));
                }
            }
        }
    }

    private void spawnGroundParticles() {
        IBlockState state = BlockStates.get(this.worldObj, EntityQueries.position(this).down());
        if (state.getBlock() != Blocks.air) {
            int id = BlockStates.stateId((IBlockState)state);
            for (int i = 0; i < 15; ++i) {
                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 3.0f) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 3.0f) - (double)this.width, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, new int[]{id});
            }
        }
    }

    private void teleportByGround() {
        EntityLivingBase target;
        if (this.srpTicks != 10) {
            return;
        }
        if (!this.getBurrowed() && this.rand.nextInt(2) == 0 && this.height <= 0.25f && (target = this.getAttackTarget()) != null && target.getDistanceSqToEntity((Entity)this) > 49.0 && ParasiteEventEntity.teleportDigging(this, 15.0f, EntityQueries.position(target), 5, 1)) {
            this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 30, 30, false, false));
        }
    }

    public void addVelocity(double x, double y, double z) {
        if (this.getBurrowed()) {
            super.addVelocity(x, y, z);
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (this.worldObj.isRemote) {
            return false;
        }
        if (source == DamageSource.inWall) {
            return false;
        }
        if (source == DamageSource.outOfWorld) {
            return super.attackEntityFrom(source, amount);
        }
        if (!this.getBurrowed() && this.height < 1.0f) {
            if (source == DamageSource.drown) {
                return false;
            }
            if (source.getEntity() == null) {
                return super.attackEntityFrom(source, amount);
            }
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (this.isPotionActive(net.minecraft.potion.Potion.moveSlowdown) && !this.getBurrowed()) {
            return;
        }
        if (this.rand.nextDouble() < 0.9 && !this.getBurrowed()) {
            if (this.getNavigator().getPath() == null) {
                this.getNavigator().tryMoveToEntityLiving((Entity)target, 0.5);
            }
            return;
        }
        this.buriedC = 120;
        if (this.getBurrowed() && this.height > 1.0f) {
            EntityProjectileSpineball entitylargefireball;
            if (this.rand.nextInt(4) == 0 && target.getDistanceSqToEntity((Entity)this) < 225.0 && target.getDistanceSqToEntity((Entity)this) > 25.0) {
                double d0 = target.posY + (double)target.getEyeHeight() - (double)1.1f;
                double d1 = target.posX + target.motionX - this.posX;
                double d2 = d0 - this.posY;
                double d3 = target.posZ + target.motionZ - this.posZ;
                float f = MathHelper.sqrt_double((double)(d1 * d1 + d3 * d3));
                EntityBomb bomb = new EntityBomb(this.worldObj, this, false);
                bomb.setFuse(40);
                bomb.setStren(0.0f);
                bomb.setSkin(1);
                bomb.setDamage((float)SRPAttributes.HERD_BOMBDAMAGE, 5);
                bomb.rotationPitch -= -20.0f;
                bomb.shootTwo(d1, d2 + (double)(f * 0.2f), d3, 0.75f, 8.0f);
                this.worldObj.spawnEntityInWorld((Entity)bomb);
                bomb.updateSTR();
            }
            if (this.rand.nextInt(2) == 0) {
                return;
            }
            double d1 = 4.0;
            Vec3d vec3d = this.getLook(1.0f);
            double d2 = target.posX - (this.posX + vec3d.xCoord);
            double d3 = Boxes.entityBox(target).minY + (double)(target.height / 2.0f) - (2.5 + this.posY + (double)(this.height / 1.5f));
            double d4 = target.posZ - (this.posZ + vec3d.zCoord);
            EntityProjectileSpineball ball = entitylargefireball = new EntityProjectileSpineball(this.worldObj, (EntityLivingBase)this, d2, d3, d4, SRPAttributes.EMANA_RANGED_DAMAGE);
            ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration, SRPConfigMobs.emanaPoisonAmplifier);
            ball.setGearDamage(SRPConfigMobs.emanaGearD);
            entitylargefireball.posX = this.posX + vec3d.xCoord;
            entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2;
            entitylargefireball.posZ = this.posZ + vec3d.zCoord;
            this.worldObj.spawnEntityInWorld((Entity)entitylargefireball);
        } else {
            this.setParasiteStatus(3);
            this.setBurrowed(true);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
            this.getNavigator().clearPathEntity();
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if (!this.getBurrowed() || this.height < 1.0f) {
            if (entityIn == null) {
                return false;
            }
            if (this.getDistanceSqToEntity(entityIn) > 4.0) {
                return false;
            }
            this.setParasiteStatus(3);
            this.setBurrowed(true);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
            this.buriedC = 160;
            return false;
        }
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            this.buriedC += 160;
            if (this.rand.nextInt(3) == 0) {
                // empty if block
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.up = true;
        this.attackTimer = 0.0f;
        this.worldObj.setEntityState((Entity)this, (byte)13);
        boolean flag = false;
        if (!this.getBurrowed() || this.height < 1.0f) {
            return false;
        }
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.SWIPE, 1.0f, 1.0f);
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.SWIPE, 1.0f, 1.25f);
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.SWIPE, 2.0f, 1.0f);
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 3.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob == this || mob instanceof EntityParasiteBase || !this.canEntityBeSeen((Entity)mob) || !this.attackEntityAsMob((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    public boolean isPushedByWater() {
        return super.isPushedByWater();
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityHost(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return super.isPotionApplicable(potioneffectIn);
    }

    protected String getLivingSound() {
        return !this.getBurrowed() ? SRPSounds.HOST_UN : SRPSounds.HOST_EM;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HOST_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.HOST_DEATH;
    }

    protected float getSoundPitch() {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.5f;
    }

    public boolean getBurrowed() {
        return (Boolean)EntityDataManager.of(this).get(UP);
    }

    public void setBurrowed(boolean in) {
        EntityDataManager.of(this).set(UP, (Object)in);
    }

    public float getEyeHeight() {
        return this.height;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @SideOnly(value=Side.CLIENT)
    public float getBurrowTimer() {
        return this.buried;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean getOpen() {
        return this.open;
    }

    @SideOnly(value=Side.CLIENT)
    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 11) {
            this.open = true;
        } else if (id == 12) {
            this.open = false;
        } else if (id == 13) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else if (id == 100) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(EnumParticleTypes.FLAME);
            }
        } else {
            super.handleHealthUpdate(id);
        }
    }

    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillshockwave;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillshockwave = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.shockwave();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void shockwave() {
        this.getNavigator().clearPathEntity();
        if (this.border == 0) {
            float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f + 2.0f;
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, this.getHurtSound(DamageSource.generic), 4.0f, v);
            ++this.border;
            return;
        }
        if (this.border <= 2) {
            this.worldObj.setEntityState((Entity)this, (byte)100);
        }
        if (this.ticksExisted % 20 != 0) {
            return;
        }
        ++this.border;
        if (this.getAttackTarget() == null) {
            this.skillshockwave = true;
            this.border = 0;
            return;
        }
        if (this.border == 2) {
            this.spawnShock();
            this.up = true;
            this.attackTimer = 0.0f;
            this.worldObj.setEntityState((Entity)this, (byte)12);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.SWIPE, 2.0f, 1.0f);
        }
        if (this.border > 3) {
            this.skillshockwave = true;
            this.border = 0;
        }
    }

    private void spawnShock() {
        EntityWave wa = new EntityWave(this.worldObj);
        float f19 = MathHelper.sin((float)(this.rotationYaw * ((float)Math.PI / 180) - this.randomYawVelocity * 0.01f));
        float f14 = 0.17453292f;
        float f16 = MathHelper.cos((float)f14);
        float f4 = MathHelper.cos((float)(this.rotationYaw * ((float)Math.PI / 180) - this.randomYawVelocity * 0.01f));
        wa.setPositionAndUpdate(this.posX + -1.0 * (double)(f19 * 3.0f * f16), this.posY, this.posZ - -1.0 * (double)(f4 * 3.0f * f16));
        wa.setDamages(this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() * 0.3, this.MiniDamage, 1, 60);
        this.worldObj.spawnEntityInWorld((Entity)wa);
        wa.setAttackTarget(this.getAttackTarget());
    }
}

