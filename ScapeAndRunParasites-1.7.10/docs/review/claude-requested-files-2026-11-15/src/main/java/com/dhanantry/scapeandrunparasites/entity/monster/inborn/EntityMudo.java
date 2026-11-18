/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAreaEffectCloud
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILeapAtTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAINearestAttackableTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.monster.EntityMob
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
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.pathfinding.PathNavigateClimber
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidEntityStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidOrAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityMudo
extends EntityParasiteBase {
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityMudo.class, (DataSerializer)DataSerializers.BYTE);

    public EntityMudo(World worldIn) {
        super(worldIn);
        this.setSize(0.85f, 1.0f);
        this.type = (byte)5;
        this.MiniDamage = SRPConfigMobs.mudoMinDamage;
        this.experienceValue = SRPAttributes.XP_LiTTLE;
        this.attackSpeedT = 10;
    }

    @Override
    public int getParasiteIDRegister() {
        return 12;
    }

    @Override
    public void applyBonuses(SRPSaveData sabe, World world) {
        super.applyBonuses(sabe, world);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, true, false, null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, true, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        if (SRPConfigSystems.useEvolution) {
            byte phase = sabe.getEvolutionPhase(world.provider.dimensionId);
            if (phase >= SRPConfigSystems.evolutionMudoAttack) {
                if (phase >= SRPConfigSystems.evolutionAssimilatedDehiding) {
                    this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 10, true, false, new Predicate<EntityLiving>(){

                        public boolean apply(@Nullable EntityLiving entity) {
                            return !(entity instanceof EntityMob) && !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                        }
                    }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
                } else {
                    this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 10, true, false, new Predicate<EntityLiving>(){

                        public boolean apply(@Nullable EntityLiving entity) {
                            return !entity.isPotionActive(SRPPotions.COTH_E) && !(entity instanceof EntityMob) && !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                        }
                    }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
                }
            } else {
                this.tasks.addTask(2, (EntityAIBase)new EntityAIMudoInfest(this, 1.0));
                this.tasks.addTask(4, (EntityAIBase)new EntityAIAvoidOrAttack(this, 0.0f, 10, 2));
                this.tasks.addTask(5, new EntityAIAvoidEntityStatus<EntityLiving>(this, EntityLiving.class, new Predicate<EntityLiving>(){

                    public boolean apply(@Nullable EntityLiving entity) {
                        return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityParasiteBase) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager);
                    }
                }, 8.0f, 1.3));
            }
        }
    }

    @Override
    public void applyGene(boolean[] kool, float[] goon) {
    }

    @Override
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, (EntityAIBase)new EntityAISkill(this, 40, 100, 5, true, 14));
        this.setskillLeapValues(0.7f, 2.5, 0);
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAILeapAtTarget((EntityLiving)this, 0.4f));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        if (SRPConfigMobs.mudoAnimalAttacking && !SRPConfigSystems.useEvolution) {
            this.targetTasks.addTask(4, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityAnimal.class, true));
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.srpTicks == 10 && !this.worldObj.isRemote && this.killcount >= (double)SRPConfigMobs.mudoTunnelValue && this.phaseCreated < SRPConfigMobs.mudoTunnelPhase && this.rand.nextInt(30) == 0) {
            if (this.getAttackTarget() != null) {
                return;
            }
            if (BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() == Blocks.air && BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullBlock() && BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullCube()) {
                BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.buglin));
                this.killcount -= (double)SRPConfigMobs.mudoTunnelValue;
            }
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.MUDO_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.MUDO_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.MUDO_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.MUDO_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0);
    }

    @Override
    public int getMaxManualVariants() {
        return 8;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag && entityIn instanceof EntityLivingBase) {
            switch (this.getSkin()) {
                case 5: {
                    SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
                    break;
                }
                case 6: {
                    SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
                }
            }
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.moveSlowdown, 40, 1));
            if (!((EntityLivingBase)entityIn).isPotionActive(SRPPotions.COTH_E)) {
                ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
        }
        return flag;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        if (this.killcount >= 0.0) {
            this.killcount += 1.0;
        }
        if (!this.worldObj.isRemote && SRPConfigSystems.useEvolution) {
            SRPSaveData data = SRPSaveData.get(this.worldObj, 43);
            data.setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueKill, true, this.worldObj, true, 37);
        }
        if (!entityLivingIn.isPotionActive(SRPPotions.COTH_E)) {
            entityLivingIn.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
        }
        ParasiteEventEntity.convertEntity(entityLivingIn, entityLivingIn.getEntityData(), true, SRPConfigSystems.COTHVictimParasite);
        this.addPotionEffect(new PotionEffect(MobEffects.regeneration, 80, 0, false, false));
        this.setWait(10);
        if (!this.worldObj.isRemote && this.killcount > (double)SRPConfigMobs.mudoMangler && !this.isDead && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityNuuh(this.worldObj), true, false);
        }
    }

    public void fall(float distance, float damageMultiplier) {
        if (distance >= 60.0f) {
            super.fall(distance, damageMultiplier);
        }
    }

    public double getMountedYOffset() {
        return this.height * 0.5f;
    }

    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateClimber((EntityLiving)this, worldIn);
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            this.setBesideClimbableBlock(this.isCollidedHorizontally);
            if (this.srpTicks == 10 && this.killcount > (double)SRPConfigMobs.mudoMangler && ParasiteEventEntity.canSpawnNext) {
                ParasiteEventEntity.spawnNext(this, new EntityNuuh(this.worldObj), true, true);
            }
        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        super.collideWithEntity(entityIn);
        if (this.worldObj.isRemote) {
            return;
        }
        if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(CLIMBING, (Object)((byte)0));
    }

    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    public boolean isBesideClimbableBlock() {
        return ((Byte)EntityDataManager.of(this).get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)EntityDataManager.of(this).get(CLIMBING);
        if (this.getAttackTarget() != null) {
            if (!this.canEntityBeSeen((Entity)this.getAttackTarget())) {
                if (this.getDistanceSqToEntity((Entity)this.getAttackTarget()) < 100.0) {
                    b0 = (byte)(b0 & 0xFFFFFFFE);
                    EntityDataManager.of(this).set(CLIMBING, (Object)b0);
                    return;
                }
            } else if (this.getAttackTarget().posY + 1.0 < this.posY) {
                b0 = (byte)(b0 & 0xFFFFFFFE);
                EntityDataManager.of(this).set(CLIMBING, (Object)b0);
                return;
            }
        }
        b0 = climbing ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        EntityDataManager.of(this).set(CLIMBING, (Object)b0);
    }

    public float getEyeHeight() {
        return 0.8f;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 && SRPConfigMobs.lodoEnabled || this.canChangeVariant) {
                    ParasiteEventEntity.spawnNext(this, new EntityLodo(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (!this.worldObj.isRemote) {
            this.rollTextureVariantWeighted();
        }
        return floo;
    }

    private void rollTextureVariantWeighted() {
        float r = this.rand.nextFloat();
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.rand.nextInt(2)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                }
            }
        } else if (r < 2.0E-4f) {
            this.setSkin(2);
        } else if (r < 0.05f) {
            this.setSkin(7);
        } else if (r < 0.15f) {
            this.setSkin(1);
        } else if (r < 0.25f) {
            this.setSkin(4);
        } else if (r < 0.4f) {
            this.setSkin(3);
        }
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.MUDO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MUDO_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.MUDO_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getSoundPitch());
    }

    protected String getStepSound() {
        return SRPSounds.SMALL_STEPS;
    }

    static class EntityAIMudoInfest
    extends EntityAIBase {
        private final EntityMudo parent;
        private int count;

        public EntityAIMudoInfest(EntityMudo animal, double speedIn) {
            this.parent = animal;
            this.count = 0;
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            ++this.count;
            if (this.count >= 20) {
                boolean tar = this.parent.getAttackTarget() == null;
                boolean nav = this.parent.getNavigator().noPath();
                String na = "";
                if (!tar) {
                    na = this.parent.getAttackTarget().getCommandSenderName();
                }
                this.count = 0;
                return this.parent.getAttackTarget() == null && this.parent.getNavigator().noPath();
            }
            return false;
        }

        public void updateTask() {
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0, this.parent.posY + 1.0, this.parent.posZ + 1.0), 12.0, 3.0, 12.0);
            List moblist = EntityQueries.entities(this.parent.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob == this.parent || mob instanceof EntityMob) continue;
                if (!mob.isPotionActive(SRPPotions.COTH_E) && this.parent.canEntityBeSeen((Entity)mob) && this.parent.getNavigator().tryMoveToEntityLiving((Entity)mob, 1.3) && this.parent.getDistanceSqToEntity((Entity)mob) < 9.0) {
                    this.spawnLingeringCloud();
                    this.parent.playSound(SRPSounds.MUDO_CLOUD, 2.0f, 1.0f);
                }
                if (this.parent.getNavigator().noPath()) continue;
                return;
            }
        }

        private void spawnLingeringCloud() {
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.parent.worldObj, this.parent.posX, this.parent.posY, this.parent.posZ);
            entityareaeffectcloud.setRadius(this.parent.width * 4.0f);
            entityareaeffectcloud.setRadiusOnUse(-0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 1, false, false));
            this.parent.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
        }
    }
}

