/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
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
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.List;
import java.util.Random;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityTenn
extends EntityPPreeminent
implements EntityCanFly {
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityTenn.class, (DataSerializer)DataSerializers.BYTE);

    public EntityTenn(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(1.9f, 2.6f);
        this.setNoGravity(true);
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
    }

    @Override
    public int getParasiteIDRegister() {
        return 90;
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
        if (!this.worldObj.isRemote) {
            if (this.onGround) {
                this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
            }
            if (this.srpTicks == 10 && this.rand.nextInt(10) == 0) {
                if (!SRPConfigWorld.coloniesActivated) {
                    return;
                }
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 16.0);
                List moblist = EntityQueries.entities(this.worldObj, EntityKol.class, axisalignedbb);
                if (moblist.size() > 3) {
                    return;
                }
                SRPWorldData data = SRPWorldData.get(this.worldObj);
                BlockPos origin = data.nearestColonyPosition(EntityQueries.position(this), false);
                if (origin != null) {
                    EntityKol aaa = new EntityKol(this.worldObj);
                    aaa.copyLocationAndAnglesFrom((Entity)this);
                    aaa.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
                    this.worldObj.spawnEntityInWorld((Entity)aaa);
                }
            }
        }
    }

    public void onUpdate() {
        super.onUpdate();
        this.setNoGravity(true);
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
    public boolean getCanSpawnHere() {
        IBlockState iblockstate = BlockStates.get(this.worldObj, new BlockPos((Entity)this).down());
        return iblockstate.canEntitySpawn((Entity)this) && DifficultyAccess.difficulty(this.worldObj) != EnumDifficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.worldObj.getTotalWorldTime();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public boolean onlySpawnInside() {
        return true;
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

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            double d2;
            double d1;
            EntityMoveHelper entitymovehelper = EntityTenn.this.getMoveHelper();
            if (!entitymovehelper.isUpdating()) {
                return true;
            }
            double d0 = entitymovehelper.getX() - EntityTenn.this.posX;
            double d3 = d0 * d0 + (d1 = entitymovehelper.getY() - EntityTenn.this.posY) * d1 + (d2 = entitymovehelper.getZ() - EntityTenn.this.posZ) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            if (EntityTenn.this.getAttackTarget() != null) {
                BlockPos blockpos = new BlockPos((Entity)EntityTenn.this);
                int flag = 1;
                double speed = 0.11;
                if (EntityTenn.this.getDistanceSqToEntity((Entity)EntityTenn.this.getAttackTarget()) > 400.0) {
                    blockpos = new BlockPos((Entity)EntityTenn.this.getAttackTarget());
                    flag = 2;
                    speed += 0.11;
                } else if (EntityTenn.this.getDistanceSqToEntity((Entity)EntityTenn.this.getAttackTarget()) < 100.0) {
                    blockpos = new BlockPos((Entity)EntityTenn.this.getAttackTarget());
                    flag = 3;
                    speed += 0.11;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.add(EntityTenn.this.rand.nextInt(15) - 7, EntityTenn.this.rand.nextInt(9) - 5, EntityTenn.this.rand.nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.add(EntityTenn.this.rand.nextInt(6) - 2, EntityTenn.this.rand.nextInt(7) - 2, EntityTenn.this.rand.nextInt(6) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.add(EntityTenn.this.rand.nextInt(4) + 3, EntityTenn.this.rand.nextInt(5) + 4, EntityTenn.this.rand.nextInt(4) + 3);
                    }
                    if (!WorldAccess.isAirBlock(EntityTenn.this.worldObj, blockpos1)) continue;
                    EntityTenn.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntityTenn.this.getAttackTarget() == null) {
                        EntityTenn.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                Random random = EntityTenn.this.getRNG();
                double d0 = EntityTenn.this.posX + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntityTenn.this.posY + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntityTenn.this.posZ + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntityTenn.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.5);
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityTenn vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityTenn.this.posX;
                double d1 = this.posY - EntityTenn.this.posY;
                double d2 = this.posZ - EntityTenn.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityTenn.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityTenn.this.motionX *= 0.5;
                    EntityTenn.this.motionY *= 0.5;
                    EntityTenn.this.motionZ *= 0.5;
                } else {
                    EntityTenn.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityTenn.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityTenn.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityTenn.this.getAttackTarget() == null) {
                        EntityTenn.this.renderYawOffset = EntityTenn.this.rotationYaw = -((float)MathHelper.atan2((double)EntityTenn.this.motionX, (double)EntityTenn.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityTenn.this.getAttackTarget().posX - EntityTenn.this.posX;
                        double d5 = EntityTenn.this.getAttackTarget().posZ - EntityTenn.this.posZ;
                        EntityTenn.this.renderYawOffset = EntityTenn.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

