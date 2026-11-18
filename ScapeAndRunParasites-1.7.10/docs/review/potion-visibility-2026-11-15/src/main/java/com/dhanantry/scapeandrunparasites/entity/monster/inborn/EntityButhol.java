/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 */
package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackSwell;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;

import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityButhol
extends EntityParasiteBase
implements EntityCanFly {
    private EntityAIFlightLimits flightLimit;
    private int maxY;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityButhol.class, (DataSerializer)DataSerializers.BYTE);

    public EntityButhol(World worldIn) {
        super(worldIn);
        this.flightLimit = new EntityAIFlightLimits(this, this.maxY, true);
        this.maxY = SRPAttributes.BUTHOL_MAXY;
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, true, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, true, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
        this.moveHelper = new AIMoveControl(this);
        this.setSize(1.4f, 2.4f);
        this.setNoGravity(true);
        this.experienceValue = SRPAttributes.XP_INFECTED * 2;
        this.tasks.removeTask((EntityAIBase)this.folow);
        if (this.maxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)this.flightLimit);
        }
        this.fuseTime = 30;
        this.killcount = -10.0;
        this.type = (byte)31;
    }

    @Override
    public int getParasiteIDRegister() {
        return 11;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackSwell(this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, 32.0));
        this.tasks.addTask(4, (EntityAIBase)new AIChargeAttack());
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.BUTHOL_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.BUTHOL_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.BUTHOL_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.BUTHOL_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.onGround && !this.worldObj.isRemote) {
            this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
        }
    }

    @Override
    protected void onDeathUpdate() {
        if (this.isBurning()) {
            super.onDeathUpdate();
        } else {
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
        }
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 2.4f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.CARRIER_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.CARRIER_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.CARRIER_DEATH;
    }

    public void moveEntity(MoverType type, double x, double y, double z) {
        super.moveEntity(type, x, y, z);
        this.doBlockCollisions();
    }

    public void onUpdate() {
        if (this.isEntityAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            this.dyingBurst(false, 1);
        }
        this.setNoGravity(true);
        super.onUpdate();
    }

    @Override
    protected void selfExplode() {
        switch (this.getSkin()) {
            case 1: {
                boolean flag = ForgeEventFactory.getMobGriefingEvent((World)this.worldObj, (Entity)this) && SRPConfigMobs.ButholGriefing;
                ParasiteEventEntity.createExplosion(this.worldObj, (Entity)this, this.posX, this.posY, this.posZ, 4.0f, flag);
                if (!this.worldObj.isRemote) {
                    AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 4.0);
                    List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                    for (EntityLivingBase mob : moblist) {
                        if (mob instanceof EntityParasiteBase) continue;
                        mob.attackEntityFrom(DamageSource.wither, (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 1);
                        mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.VOMIT_E, 400, 0, false, true));
                    }
                    this.playSound(SRPSounds.BUTHOL_BOOM, 1.0f, 1.0f);
                    this.dead = true;
                    this.setDead();
                    this.spawnLingeringCloud();
                }
                return;
            }
        }
        boolean flag = ForgeEventFactory.getMobGriefingEvent((World)this.worldObj, (Entity)this) && SRPConfigMobs.ButholGriefing;
        ParasiteEventEntity.createExplosion(this.worldObj, (Entity)this, this.posX, this.posY, this.posZ, 4.0f, flag);
        if (!this.worldObj.isRemote) {
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 4.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 1);
                mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.VOMIT_E, 400, 0, false, true));
            }
            this.playSound(SRPSounds.BUTHOL_BOOM, 1.0f, 1.0f);
            this.dead = true;
            this.setDead();
            this.spawnLingeringCloud();
            ParasiteSummon.spawnM(this, SRPConfigMobs.butholMobs, 0, false, this.getCustomNameTag());
        }
    }

    private void spawnLingeringCloud() {
        switch (this.getSkin()) {
            case 1: {
                EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY, this.posZ);
                entityareaeffectcloud.setRadius(this.width * 3.5f, 0.5f);
                entityareaeffectcloud.setWaitTime(10);
                entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
                entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
                entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.poison, 300, 2));
                entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
                entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.VIRA_E, 3600, 2, false, false));
                this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
                return;
            }
        }
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY, this.posZ);
        entityareaeffectcloud.setRadius(this.width * 3.5f, 0.5f);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.poison, 300, 0));
        entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
        entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.VIRA_E, 3600, 0, false, false));
        this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (this.rand.nextInt(1)) {
                case 0: {
                    this.setSkin(1);
                }
            }
        }
        return floo;
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
            if (EntityButhol.this.getAttackTarget() != null && !EntityButhol.this.getMoveHelper().isUpdating() && EntityButhol.this.rand.nextInt(7) == 0) {
                return EntityButhol.this.getDistanceSqToEntity((Entity)EntityButhol.this.getAttackTarget()) > 4.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityButhol.this.getMoveHelper().isUpdating() && EntityButhol.this.isCharging() && EntityButhol.this.getAttackTarget() != null && EntityButhol.this.getAttackTarget().isEntityAlive();
        }

        public void startExecuting() {
            EntityLivingBase entitylivingbase = EntityButhol.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
            EntityButhol.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 1.0);
            EntityButhol.this.setCharging(true);
        }

        public void resetTask() {
            EntityButhol.this.setCharging(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = EntityButhol.this.getAttackTarget();
            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                if (Boxes.intersects(Boxes.entityBox(EntityButhol.this), Boxes.entityBox(entitylivingbase))) {
                    EntityButhol.this.attackEntityAsMob((Entity)entitylivingbase);
                    EntityButhol.this.setCharging(false);
                } else {
                    double d0 = EntityButhol.this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityButhol.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 1.0);
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
            return !EntityButhol.this.getMoveHelper().isUpdating() && EntityButhol.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityButhol.this);
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityButhol.this.rand.nextInt(15) - 7, EntityButhol.this.rand.nextInt(11) - 5, EntityButhol.this.rand.nextInt(15) - 7);
                if (!WorldAccess.isAirBlock(EntityButhol.this.worldObj, blockpos1)) continue;
                EntityButhol.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                if (EntityButhol.this.getAttackTarget() != null) break;
                EntityButhol.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityButhol vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityButhol.this.posX;
                double d1 = this.posY - EntityButhol.this.posY;
                double d2 = this.posZ - EntityButhol.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityButhol.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityButhol.this.motionX *= 0.5;
                    EntityButhol.this.motionY *= 0.5;
                    EntityButhol.this.motionZ *= 0.5;
                } else {
                    EntityButhol.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityButhol.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityButhol.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityButhol.this.getAttackTarget() == null) {
                        EntityButhol.this.renderYawOffset = EntityButhol.this.rotationYaw = -((float)MathHelper.atan2((double)EntityButhol.this.motionX, (double)EntityButhol.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityButhol.this.getAttackTarget().posX - EntityButhol.this.posX;
                        double d5 = EntityButhol.this.getAttackTarget().posZ - EntityButhol.this.posZ;
                        EntityButhol.this.renderYawOffset = EntityButhol.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

