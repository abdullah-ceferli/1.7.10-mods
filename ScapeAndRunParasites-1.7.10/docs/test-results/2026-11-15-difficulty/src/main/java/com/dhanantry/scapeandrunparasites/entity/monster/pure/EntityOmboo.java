/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityOmboo
extends EntityPPure
implements EntityCanFly {
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityOmboo.class, (DataSerializer)DataSerializers.BYTE);

    public EntityOmboo(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(1.7f, 2.4f);
        this.setNoGravity(true);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.borderOrb = -1;
        if (SRPConfigMobs.ombooMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
        }
        this.adaptationCap = 0.95f;
    }

    @Override
    public int getParasiteIDRegister() {
        return 47;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.tasks.addTask(4, (EntityAIBase)new AIChargeAttack());
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(5, (EntityAIBase)new AIBomb(this));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIFlightLimits(this, 7, false));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIFlightLimits(this, 20, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.OMBOO_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.OMBOO_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.OMBOO_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.pureFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.srpTicks == 10) {
            if (this.onGround) {
                this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
            }
            if ((BlockStates.get(this.worldObj, EntityQueries.position(this).down(1)).getBlock() != Blocks.air || BlockStates.get(this.worldObj, EntityQueries.position(this).down(2)).getBlock() != Blocks.air) && this.getAttackTarget() != null) {
                this.motionY = 0.5;
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
        return 2.4f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.OMBOO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.OMBOO_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.OMBOO_DEATH;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.rand.nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
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

    class AIBomb
    extends EntityAIBase {
        private final EntityParasiteBase parent;
        private int ccc;

        public AIBomb(EntityParasiteBase parentIn) {
            this.parent = parentIn;
            this.ccc = 0;
        }

        public boolean shouldExecute() {
            ++this.ccc;
            if (this.ccc >= 15) {
                this.ccc = 0;
                if (this.parent.getAttackTarget() != null) {
                    EntityLivingBase target = this.parent.getAttackTarget();
                    if (!target.onGround) {
                        this.ccc = 7;
                        return false;
                    }
                    if (target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 25.0) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void updateTask() {
            EntityBomb out = new EntityBomb(this.parent.worldObj, this.parent, SRPConfigMobs.ombooGriefing);
            out.copyLocationAndAnglesFrom((Entity)this.parent);
            out.setFuse(80);
            out.setStren(1.0f);
            out.setDamage((float)SRPAttributes.OMBOO_BOMBDAMAGE, 4);
            this.parent.worldObj.spawnEntityInWorld((Entity)out);
            out.updateSTR();
        }
    }

    class AIChargeAttack
    extends EntityAIBase {
        public AIChargeAttack() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            if (EntityOmboo.this.getAttackTarget() != null && EntityOmboo.this.rand.nextInt(7) == 0) {
                return EntityOmboo.this.getDistanceSqToEntity((Entity)EntityOmboo.this.getAttackTarget()) > 3.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityOmboo.this.getMoveHelper().isUpdating() && EntityOmboo.this.isCharging() && EntityOmboo.this.getAttackTarget() != null && EntityOmboo.this.getAttackTarget().isEntityAlive();
        }

        public void startExecuting() {
            EntityLivingBase entitylivingbase = EntityOmboo.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
            EntityOmboo.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord + 10.0, vec3d.zCoord, 1.0);
            EntityOmboo.this.setCharging(true);
        }

        public void resetTask() {
            EntityOmboo.this.setCharging(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = EntityOmboo.this.getAttackTarget();
            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                if (Boxes.intersects(Boxes.entityBox(EntityOmboo.this), Boxes.entityBox(entitylivingbase))) {
                    EntityOmboo.this.attackEntityAsMob((Entity)entitylivingbase);
                    EntityOmboo.this.setCharging(false);
                } else {
                    double d0 = EntityOmboo.this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityOmboo.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord + 10.0, vec3d.zCoord, 1.0);
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
            return !EntityOmboo.this.getMoveHelper().isUpdating() && EntityOmboo.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityOmboo.this);
            int flag = 1;
            double speed = 0.2;
            if (EntityOmboo.this.getAttackTarget() != null) {
                if (EntityOmboo.this.getDistanceSqToEntity((Entity)EntityOmboo.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityOmboo.this.getAttackTarget());
                    flag = 2;
                    speed += 0.1;
                } else if (EntityOmboo.this.getDistanceSqToEntity((Entity)EntityOmboo.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityOmboo.this.getAttackTarget());
                    flag = 3;
                    speed += 0.1;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityOmboo.this.rand.nextInt(15) - 7, EntityOmboo.this.rand.nextInt(11) - 5, EntityOmboo.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityOmboo.this.rand.nextInt(6) - 2, EntityOmboo.this.rand.nextInt(7) - 2, EntityOmboo.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityOmboo.this.rand.nextInt(4) + 3, EntityOmboo.this.rand.nextInt(5) + 4, EntityOmboo.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityOmboo.this.worldObj, blockpos1)) continue;
                EntityOmboo.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityOmboo.this.getAttackTarget() != null) break;
                EntityOmboo.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityOmboo vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityOmboo.this.posX;
                double d1 = this.posY - EntityOmboo.this.posY;
                double d2 = this.posZ - EntityOmboo.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityOmboo.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityOmboo.this.motionX *= 0.5;
                    EntityOmboo.this.motionY *= 0.5;
                    EntityOmboo.this.motionZ *= 0.5;
                } else {
                    EntityOmboo.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityOmboo.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityOmboo.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityOmboo.this.getAttackTarget() == null) {
                        EntityOmboo.this.renderYawOffset = EntityOmboo.this.rotationYaw = -((float)MathHelper.atan2((double)EntityOmboo.this.motionX, (double)EntityOmboo.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityOmboo.this.getAttackTarget().posX - EntityOmboo.this.posX;
                        double d5 = EntityOmboo.this.getAttackTarget().posZ - EntityOmboo.this.posZ;
                        EntityOmboo.this.renderYawOffset = EntityOmboo.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

