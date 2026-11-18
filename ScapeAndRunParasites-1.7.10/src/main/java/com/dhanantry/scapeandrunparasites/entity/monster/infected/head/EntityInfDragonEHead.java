/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILeapAtTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAINearestAttackableTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected.head;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;

public class EntityInfDragonEHead
extends EntityPInfected {
    public EntityInfDragonEHead(World worldIn) {
        super(worldIn);
        this.setSize(1.75f, 1.95f);
        this.killcount = -10.0;
        this.attackSpeedT = 15;
    }

    @Override
    public int getParasiteIDRegister() {
        return 70;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infdragoneCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.tasks.addTask(6, (EntityAIBase)new AIFireballAttack(this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAILeapAtTarget((EntityLiving)this, 0.4f));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.targetTasks.addTask(5, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityPlayer.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFDRAGONE_HEADHEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFDRAGONE_HEADDAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && SRPConfigSystems.disloGiveBodies && this.isEntityAlive() && this.srpTicks == 10 && SRPSaveData.get(this.worldObj, 44).getCurrentCode(this.worldObj.provider.dimensionId, 20) >= 1) {
            ParasiteEventEntity.spawnNext(this, new EntityInfDragonE(this.worldObj), true, false);
            return;
        }
    }

    public void setInWeb() {
    }

    public float getEyeHeight() {
        return 0.8f;
    }

    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier * 0.3f);
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, this.getStepSound(), this.getSoundVolume(), this.getSoundPitch());
    }

    protected String getStepSound() {
        return SRPSounds.SMALL_STEPS;
    }

    static class AIFireballAttack
    extends EntityAIBase {
        private final EntityInfDragonEHead parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityInfDragonEHead ghast) {
            this.parentEntity = ghast;
        }

        public boolean shouldExecute() {
            return this.parentEntity.getAttackTarget() != null;
        }

        public void startExecuting() {
            this.attackTimer = 0;
        }

        public void resetTask() {
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            double d0 = 64.0;
            if (entitylivingbase == null) {
                return;
            }
            if (entitylivingbase.getDistanceSqToEntity((Entity)this.parentEntity) < 4096.0 && this.parentEntity.canEntityBeSeen((Entity)entitylivingbase)) {
                World world = this.parentEntity.worldObj;
                ++this.attackTimer;
                if (this.parentEntity.isPotionActive(SRPPotions.RAGE_E)) {
                    ++this.attackTimer;
                }
                if (this.attackTimer == 15) {
                    double d1 = 4.0;
                    Vec3d vec3d = this.parentEntity.getLook(1.0f);
                    double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.xCoord * 4.0);
                    double d3 = Boxes.entityBox(entitylivingbase).minY + (double)(entitylivingbase.height / 2.0f) - (0.5 + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f));
                    double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.zCoord * 4.0);
                    world.playEvent((EntityPlayer)null, 1016, new BlockPos((Entity)this.parentEntity), 0);
                    EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(world, (EntityLivingBase)this.parentEntity, d2, d3, d4);
                    entitylargefireball.posX = this.parentEntity.posX + vec3d.xCoord;
                    entitylargefireball.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f) + 0.5;
                    entitylargefireball.posZ = this.parentEntity.posZ + vec3d.zCoord;
                    world.spawnEntityInWorld((Entity)entitylargefireball);
                    this.attackTimer = 0;
                    for (int i = 0; i <= 2; ++i) {
                        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.parentEntity.worldObj, EnumParticleTypes.FLAME, this.parentEntity.posX + vec3d.xCoord * 4.0, this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f) + 0.5, this.parentEntity.posZ + vec3d.zCoord * 4.0, 0.0, -1.0, 0.0, new int[0]);
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        }
    }
}

