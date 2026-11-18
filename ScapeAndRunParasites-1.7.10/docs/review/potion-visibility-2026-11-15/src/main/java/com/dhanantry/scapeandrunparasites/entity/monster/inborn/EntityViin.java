/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class EntityViin
extends EntityParasiteBase
implements EntityCanFly {
    private int lifespan = 0;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityViin.class, (DataSerializer)DataSerializers.BYTE);

    public EntityViin(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
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
        return 334;
    }

    protected void initEntityAI() {
        this.tasks.addTask(4, (EntityAIBase)new AIChargeAttack());
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.adaptedFollow));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
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
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.VIIN_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VIIN_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.34559);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.VIIN_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.VIIN_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.adaptedFollow);
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
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 120, 2);
            this.playSound(SRPSounds.BUTHOL_BOOM, 0.4f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
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
    }

    public double getMountedYOffset() {
        return this.height * 0.5f;
    }

    public float getEyeHeight() {
        return 0.8f;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        return false;
    }

    public void onUpdate() {
        super.onUpdate();
        this.setNoGravity(true);
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

    public void moveEntity(MoverType type, double x, double y, double z) {
        super.moveEntity(type, x, y, z);
        this.doBlockCollisions();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(VEX_FLAGS, (Object)((byte)0));
    }

    private boolean getVexFlag(int mask) {
        byte i = (Byte)EntityDataManager.of(this).get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = ((Byte)EntityDataManager.of(this).get(VEX_FLAGS)).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        EntityDataManager.of(this).set(VEX_FLAGS, (Object)((byte)(i & 0xFF)));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    class AIChargeAttack
    extends EntityAIBase {
        public AIChargeAttack() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            if (EntityViin.this.getAttackTarget() != null && !EntityViin.this.getMoveHelper().isUpdating() && EntityViin.this.rand.nextInt(7) == 0) {
                return EntityViin.this.getDistanceSqToEntity((Entity)EntityViin.this.getAttackTarget()) > 1.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityViin.this.getMoveHelper().isUpdating() && EntityViin.this.isCharging() && EntityViin.this.getAttackTarget() != null && EntityViin.this.getAttackTarget().isEntityAlive();
        }

        public void startExecuting() {
            EntityLivingBase entitylivingbase = EntityViin.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
            EntityViin.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 2.0);
            EntityViin.this.setCharging(true);
        }

        public void resetTask() {
            EntityViin.this.setCharging(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = EntityViin.this.getAttackTarget();
            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                if (Boxes.intersects(Boxes.entityBox(EntityViin.this), Boxes.entityBox(entitylivingbase))) {
                    EntityViin.this.attackEntityAsMob((Entity)entitylivingbase);
                    EntityViin.this.setCharging(false);
                } else {
                    double d0 = EntityViin.this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityViin.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 2.0);
                    }
                }
            }
        }
    }

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            return !EntityViin.this.getMoveHelper().isUpdating() && EntityViin.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityViin.this);
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityViin.this.rand.nextInt(15) - 7, EntityViin.this.rand.nextInt(11) - 5, EntityViin.this.rand.nextInt(15) - 7);
                if (!WorldAccess.isAirBlock(EntityViin.this.worldObj, blockpos1)) continue;
                EntityViin.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                if (EntityViin.this.getAttackTarget() != null) break;
                EntityViin.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityViin vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityViin.this.posX;
                double d1 = this.posY - EntityViin.this.posY;
                double d2 = this.posZ - EntityViin.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityViin.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityViin.this.motionX *= 0.5;
                    EntityViin.this.motionY *= 0.5;
                    EntityViin.this.motionZ *= 0.5;
                } else {
                    EntityViin.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityViin.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityViin.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityViin.this.getAttackTarget() == null) {
                        EntityViin.this.renderYawOffset = EntityViin.this.rotationYaw = -((float)MathHelper.atan2((double)EntityViin.this.motionX, (double)EntityViin.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityViin.this.getAttackTarget().posX - EntityViin.this.posX;
                        double d5 = EntityViin.this.getAttackTarget().posZ - EntityViin.this.posZ;
                        EntityViin.this.renderYawOffset = EntityViin.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

