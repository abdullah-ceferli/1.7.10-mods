/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIAttackMelee
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGiveEffectsArea;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityBano
extends EntityPPrimitive {
    private float body = 0.0f;

    public EntityBano(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 2.9f);
    }

    @Override
    protected boolean canRandomBlock() {
        return false;
    }

    @Override
    public int getParasiteIDRegister() {
        return 17;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackMelee((EntityCreature)this, 1.0, false));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIGiveEffectsArea(this, SRPAttributes.ZETMO_CD, SRPAttributes.ZETMO_RANGE, SRPConfigMobs.zetmoEffects));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 2, 16));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.ZETMO_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ZETMO_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.19);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.ZETMO_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.ZETMO_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.primitiveFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.getParasiteStatus() == 15) {
            this.setBODY(0.07f);
        } else {
            this.setBODY(-0.07f);
        }
        if (!this.worldObj.isRemote && this.ticksExisted % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityBanoAdapted(this.worldObj), true, true);
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag && entityIn instanceof EntityLivingBase) {
            switch (this.getSkin()) {
                case 5: {
                    SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
                }
            }
        }
        return flag;
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        super.collideWithEntity(entityIn);
        if (this.worldObj.isRemote) {
            return;
        }
        if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
        }
    }

    public float getEyeHeight() {
        return 2.7f;
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

    public float getBODY() {
        return this.body;
    }

    public void setBODY(float in) {
        if ((in += this.getBODY()) > 1.9f) {
            in = 1.9f;
        }
        if (in < 0.0f) {
            in = 0.0f;
        }
        this.body = in;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.worldObj.isRemote && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityBanoAdapted(this.worldObj), true, true);
        }
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.ZETMO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.ZETMO_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.ZETMO_DEATH;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.zetmoOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.rand.nextInt(2)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    static class AITentaclePull
    extends EntityAIBase {
        private final EntityBano parentEntity;

        public AITentaclePull(EntityBano ghast) {
            this.parentEntity = ghast;
        }

        public boolean shouldExecute() {
            return this.parentEntity.getAttackTarget() != null || this.parentEntity.getBODY() != 0.0f;
        }

        public void startExecuting() {
        }

        public void resetTask() {
            this.parentEntity.setParasiteStatus(0);
        }

        public void updateTask() {
            if (this.parentEntity.getAttackTarget() == null) {
                this.parentEntity.setParasiteStatus(0);
            } else if (this.parentEntity.getAttackTarget().isDead) {
                this.parentEntity.setParasiteStatus(0);
            } else {
                EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
                if (entitylivingbase.getDistanceSqToEntity((Entity)this.parentEntity) < 169.0 && this.parentEntity.canEntityBeSeen((Entity)entitylivingbase)) {
                    this.parentEntity.setParasiteStatus(15);
                    if (entitylivingbase.getDistanceSqToEntity((Entity)this.parentEntity) < 64.0 && entitylivingbase.getDistanceSqToEntity((Entity)this.parentEntity) > 4.0 && this.parentEntity.ticksExisted % 20 == 0) {
                        Vec3d vec3d = this.parentEntity.getLook(1.0f);
                        float f = (float)MathHelper.atan2((double)(entitylivingbase.posZ - this.parentEntity.posZ), (double)(entitylivingbase.posX - this.parentEntity.posX));
                        EntityDamage entityevokerfangs = new EntityDamage(this.parentEntity.worldObj, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, f, (EntityLivingBase)this.parentEntity, 1.0f, true, 1.0f);
                        this.parentEntity.worldObj.spawnEntityInWorld((Entity)entityevokerfangs);
                    }
                } else {
                    this.parentEntity.setParasiteStatus(0);
                }
            }
        }
    }
}

