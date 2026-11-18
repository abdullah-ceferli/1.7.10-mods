/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySoo
extends EntityPPure
implements EntityCanFly {
    private int scentCool;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntitySoo.class, (DataSerializer)DataSerializers.BYTE);

    public EntitySoo(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(1.9f, 2.6f);
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, true);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.ignoreFrustumCheck = true;
        this.borderOrb = -1;
        this.totalP = SRPConfigMobs.alafhaTotalActiveMobs;
        this.mobID = new int[this.totalP + SRPConfigMobs.alafhaLimit];
        this.mobPT = new int[this.totalP + SRPConfigMobs.alafhaLimit];
        for (int i = 0; i < this.mobID.length; ++i) {
            this.mobID[i] = -777;
        }
        if (SRPConfigMobs.alafhaMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.alafhaMaxY, true));
        }
        this.adaptationCap = 0.95f;
        this.scentCool = 800;
    }

    @Override
    public int getParasiteIDRegister() {
        return 82;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, 64.0));
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.ALAFHA_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.ALAFHA_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.ALAFHA_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.pureFollow);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.ALAFHA_MELLE);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.onGround && !this.worldObj.isRemote) {
            this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
        }
        --this.scentCool;
        if (this.scentCool < 0 && this.srpTicks == 10 && SRPConfigSystems.useScent && this.getLevelCreated() >= SRPConfigSystems.deveScentUse) {
            List serverList = EntityQueries.loaded(this.worldObj);
            int count = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityParasiticScent)) continue;
                ++count;
            }
            if (count > SRPConfigSystems.scentCap) {
                return;
            }
            if (this.getAttackTarget() != null) {
                EntityParasiticScent sss = new EntityParasiticScent(this.worldObj);
                sss.copyLocationAndAnglesFrom((Entity)this.getAttackTarget());
                sss.setTargetToKill(this.getAttackTarget(), false);
                sss.setDieToE(true);
                sss.setCanFollow(true);
                this.worldObj.spawnEntityInWorld((Entity)sss);
                this.entityAge = 0;
                this.scentCool = 800;
            }
        }
    }

    public void onUpdate() {
        super.onUpdate();
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, true);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 1.6f;
    }

    protected String getLivingSound() {
        return SRPSounds.ALAFHA_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.ALAFHA_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.ALAFHA_DEATH;
    }

    protected float getSoundVolume() {
        return 2.0f;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        return super.onInitialSpawn(difficulty, livingdata);
    }

    public void moveEntity(MoverType type, double x, double y, double z) {
        super.moveEntity(type, x, y, z);
        com.dhanantry.scapeandrunparasites.backport.entity.movement.MovementAccess.blocks(this);
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

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            double d2;
            double d1;
            EntityMoveHelper entitymovehelper = EntitySoo.this.getMoveHelper();
            if (!entitymovehelper.isUpdating()) {
                return true;
            }
            double d0 = entitymovehelper.getX() - EntitySoo.this.posX;
            double d3 = d0 * d0 + (d1 = entitymovehelper.getY() - EntitySoo.this.posY) * d1 + (d2 = entitymovehelper.getZ() - EntitySoo.this.posZ) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            if (EntitySoo.this.getAttackTarget() != null) {
                BlockPos blockpos = new BlockPos((Entity)EntitySoo.this);
                int flag = 1;
                double speed = 0.11;
                if (EntitySoo.this.getDistanceSqToEntity((Entity)EntitySoo.this.getAttackTarget()) > 400.0) {
                    blockpos = new BlockPos((Entity)EntitySoo.this.getAttackTarget());
                    flag = 2;
                    speed += 0.11;
                } else if (EntitySoo.this.getDistanceSqToEntity((Entity)EntitySoo.this.getAttackTarget()) < 100.0) {
                    blockpos = new BlockPos((Entity)EntitySoo.this.getAttackTarget());
                    flag = 3;
                    speed += 0.11;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.add(EntitySoo.this.rand.nextInt(15) - 7, EntitySoo.this.rand.nextInt(9) - 5, EntitySoo.this.rand.nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.add(EntitySoo.this.rand.nextInt(6) - 2, EntitySoo.this.rand.nextInt(7) - 2, EntitySoo.this.rand.nextInt(6) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.add(EntitySoo.this.rand.nextInt(4) + 3, EntitySoo.this.rand.nextInt(5) + 4, EntitySoo.this.rand.nextInt(4) + 3);
                    }
                    if (!WorldAccess.isAirBlock(EntitySoo.this.worldObj, blockpos1)) continue;
                    EntitySoo.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntitySoo.this.getAttackTarget() == null) {
                        EntitySoo.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                Random random = EntitySoo.this.getRNG();
                double d0 = EntitySoo.this.posX + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntitySoo.this.posY + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntitySoo.this.posZ + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntitySoo.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.5);
            }
        }
    }

    class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntitySoo vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntitySoo.this.posX;
                double d1 = this.posY - EntitySoo.this.posY;
                double d2 = this.posZ - EntitySoo.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntitySoo.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntitySoo.this.motionX *= 0.5;
                    EntitySoo.this.motionY *= 0.5;
                    EntitySoo.this.motionZ *= 0.5;
                } else {
                    EntitySoo.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntitySoo.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntitySoo.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntitySoo.this.getAttackTarget() == null) {
                        EntitySoo.this.renderYawOffset = EntitySoo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntitySoo.this.motionX, (double)EntitySoo.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntitySoo.this.getAttackTarget().posX - EntitySoo.this.posX;
                        double d5 = EntitySoo.this.getAttackTarget().posZ - EntitySoo.this.posZ;
                        EntitySoo.this.renderYawOffset = EntitySoo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

