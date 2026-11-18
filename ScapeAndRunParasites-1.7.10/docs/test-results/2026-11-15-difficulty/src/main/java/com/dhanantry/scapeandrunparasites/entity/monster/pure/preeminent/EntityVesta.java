/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGiveEffectsArea;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityVesta
extends EntityPPreeminent
implements EntityBodyParts {
    private EntityBody head;

    public EntityVesta(World worldIn) {
        super(worldIn);
        this.setSize(1.75f, 3.6f);
        this.stepHeight = 1.0f;
        this.borderOrb = -1;
        this.canTeleportToo = true;
        this.ignoreFrustumCheck = true;
        this.type = (byte)31;
        this.attackSpeedT = 10;
        this.head = new EntityBody(this, 3.8f, 3.8f, 1.0f, 3.1f, 1.6f, 1, 1, false, 0.2f);
    }

    @Override
    public int getParasiteIDRegister() {
        return 88;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.15));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 2, 16));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIGiveEffectsArea(this, SRPAttributes.VESTA_CD, SRPAttributes.VESTA_RANGE, SRPConfigMobs.vestaeffects));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.VESTA_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VESTA_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.242);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(2.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.VESTA_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.preeminentFollow);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.head.onUpdate();
        if (this.srpTicks == 25) {
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(EntityQueries.position(this)), 32.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
            for (EntityParasiteBase mob : moblist) {
                SRPPotions.applyStackPotion(SRPPotions.LINK_E, (EntityLivingBase)mob, 6666, 0);
                SRPPotions.applyStackPotion(SRPPotions.FOSTER_E, (EntityLivingBase)mob, 6666, 0);
            }
        }
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.rand.nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)this, 80, 0);
        }
        return this.attackEntityFrom(source, amount * 3.0f);
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.head);
        }
        super.setDead();
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.vestaOrbEffects, mobs);
        }
        return flag;
    }

    public float getEyeHeight() {
        return 1.5f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.VESTA_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.VESTA_HURT;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SRPSounds.HEAVY_STEPS_MULTIPLE, 0.15f, 1.0f);
    }

    protected String getDeathSound() {
        return SRPSounds.VESTA_DEATH;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (2) {
                case 2: {
                    this.setSkin(1);
                    this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VESTA_ARMOR * 1.5);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1694);
                }
            }
        }
        return floo;
    }
}

