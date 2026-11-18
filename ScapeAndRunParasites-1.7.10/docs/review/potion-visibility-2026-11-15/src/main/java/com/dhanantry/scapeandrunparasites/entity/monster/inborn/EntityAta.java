/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
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
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.pathfinding.PathNavigateClimber
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class EntityAta
extends EntityParasiteBase {
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityAta.class, (DataSerializer)DataSerializers.BYTE);
    private int lifespan = 0;

    public EntityAta(World worldIn) {
        super(worldIn);
        this.setSize(0.85f, 1.0f);
        this.type = (byte)5;
        this.lifespan = 0;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, true, false, null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, true, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        }
        this.attackSpeedT = 6;
    }

    @Override
    public int getParasiteIDRegister() {
        return 91;
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, (EntityAIBase)new EntityAISkill(this, 20, 100, 5, true, 14));
        this.setskillLeapValues(0.4f, 1.0, 0);
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.12));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAILeapAtTarget((EntityLiving)this, 0.4f));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        ++this.lifespan;
        if (this.lifespan > 1200) {
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 11));
            this.setDead();
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.ATA_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ATA_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.34559);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.ATA_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.ATA_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        super.collideWithEntity(entityIn);
        if (entityIn == this.getAttackTarget()) {
            EntityLivingBase target = (EntityLivingBase)entityIn;
            if (target.getHealth() <= target.getMaxHealth() * SRPConfigSystems.hijackHealth) {
                if (ParasiteEventEntity.convertEntityFeral((EntityLivingBase)entityIn, entityIn.getEntityData(), true, SRPConfigSystems.COTHVictimParasite) || ParasiteEventEntity.hijackEntity((EntityLivingBase)entityIn, SRPConfigSystems.HIJACKVictimParasite)) {
                    SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 11));
                    SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 11));
                } else {
                    entityIn.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                    SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 10));
                }
            } else {
                entityIn.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 10));
            }
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 10));
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 10));
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 10));
            this.playSound(SRPSounds.BUTHOL_BOOM, 0.3f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 120, 2);
            this.setDead();
        }
    }

    @Override
    protected void onDeathUpdate() {
        SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, 0.5f, 0.5f, 10));
        this.setDead();
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
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
        super.onDeath(cause);
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        return false;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.MOBSILENCE;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MOBSILENCE;
    }

    protected String getDeathSound() {
        return SRPSounds.MOBSILENCE;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getSoundPitch());
    }

    protected String getStepSound() {
        return SRPSounds.SMALL_STEPS;
    }
}

