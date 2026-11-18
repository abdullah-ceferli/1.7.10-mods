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
 *  net.minecraft.entity.projectile.EntityFireball
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
package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
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
import net.minecraft.entity.projectile.EntityFireball;
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

public class EntityEmanaAdapted
extends EntityPAdapted
implements EntityCutomAttack,
EntityCanShoot,
EntityCanFly {
    private final EntityHitbox tendril_1;
    private final EntityHitbox tendril_2;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityEmanaAdapted.class, (DataSerializer)DataSerializers.BYTE);
    private int count;

    public EntityEmanaAdapted(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(1.3f, 2.9f);
        this.setNoGravity(true);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.borderOrb = -1;
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
        this.adaptationCap = 0.95f;
        this.tendril_1 = new EntityHitbox((EntityLiving)this, 0.2f, 1.1f, 0.1f, 0.9f, 2.6f, 1.25f);
        this.tendril_2 = new EntityHitbox((EntityLiving)this, 3.0f, 1.1f, 0.1f, 0.9f, 2.6f, 1.25f);
        this.hitboxes = new EntityHitbox[]{this.tendril_1, this.tendril_2};
    }

    @Override
    public int getParasiteIDRegister() {
        return 55;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.adaptedFollow));
        this.tasks.addTask(4, (EntityAIBase)new AIChargeAttack());
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(1, (EntityAIBase)new EntityAIAttackProjectile(this, 60, 20, 2, true));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackMeleeNotGround(this, 2.5, 16.0, 0.04, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.EMANA_HEALTH + SRPAttributes.EMANA_A_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.EMANA_ARMOR + SRPAttributes.EMANA_A_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.EMANA_KD_RESISTANCE + SRPAttributes.EMANA_A_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.adaptedFollow);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.EMANA_A_MELLE);
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
                return;
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

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.attackEntityAsMob(entityIn);
    }

    public float getEyeHeight() {
        return 2.1f;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityEmana(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    protected String getLivingSound() {
        return SRPSounds.AEMANA_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.AEMANA_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.AEMANA_DEATH;
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

    public EntityFireball getProj(double accelX, double accelY, double accelZ) {
        float pit = 1.0f;
        if (this.count == 3) {
            pit = 1.5f;
        }
        this.playSound(SRPSounds.EMANA_SHOOTING, 2.0f, pit);
        if (this.count >= 3) {
            this.count = 0;
            return new EntityProjectileNade(this.worldObj, (EntityLivingBase)this, accelX, accelY, accelZ, 4, 100);
        }
        EntityProjectileSpineball ball = new EntityProjectileSpineball(this.worldObj, (EntityLivingBase)this, accelX, accelY, accelZ, SRPAttributes.EMANA_A_RANGED_DAMAGE);
        ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration * 2, SRPConfigMobs.emanaPoisonAmplifier + 1);
        ball.setGearDamage(SRPConfigMobs.emanaadaptedgeard);
        return ball;
    }

    @Override
    public void playProjSound() {
        ++this.count;
        if (this.count == 3) {
            this.worldObj.setEntityState((Entity)this, (byte)100);
            float v = this.rand.nextFloat() * 0.4f + 1.0f;
            this.playSound(SRPSounds.ATTACKEMANA, 4.0f, v);
            return;
        }
        this.playSound(SRPSounds.AEMANA_SHOOTINGPOST, 2.0f, 1.0f);
    }

    class AIChargeAttack
    extends EntityAIBase {
        public AIChargeAttack() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            if (EntityEmanaAdapted.this.getAttackTarget() != null && !EntityEmanaAdapted.this.getMoveHelper().isUpdating() && EntityEmanaAdapted.this.rand.nextInt(7) == 0) {
                return EntityEmanaAdapted.this.getDistanceSqToEntity((Entity)EntityEmanaAdapted.this.getAttackTarget()) > 4.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityEmanaAdapted.this.getMoveHelper().isUpdating() && EntityEmanaAdapted.this.isCharging() && EntityEmanaAdapted.this.getAttackTarget() != null && EntityEmanaAdapted.this.getAttackTarget().isEntityAlive();
        }

        public void startExecuting() {
            EntityLivingBase entitylivingbase = EntityEmanaAdapted.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
            EntityEmanaAdapted.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 1.5);
            EntityEmanaAdapted.this.setCharging(true);
        }

        public void resetTask() {
            EntityEmanaAdapted.this.setCharging(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = EntityEmanaAdapted.this.getAttackTarget();
            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                if (Boxes.intersects(Boxes.entityBox(EntityEmanaAdapted.this), Boxes.entityBox(entitylivingbase))) {
                    EntityEmanaAdapted.this.attackEntityAsMob((Entity)entitylivingbase);
                    EntityEmanaAdapted.this.setCharging(false);
                } else {
                    double d0 = EntityEmanaAdapted.this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityEmanaAdapted.this.moveHelper.setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, 1.5);
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
            return !EntityEmanaAdapted.this.getMoveHelper().isUpdating() && EntityEmanaAdapted.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityEmanaAdapted.this);
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityEmanaAdapted.this.rand.nextInt(15) - 7, EntityEmanaAdapted.this.rand.nextInt(11) - 5, EntityEmanaAdapted.this.rand.nextInt(15) - 7);
                if (!WorldAccess.isAirBlock(EntityEmanaAdapted.this.worldObj, blockpos1)) continue;
                EntityEmanaAdapted.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                if (EntityEmanaAdapted.this.getAttackTarget() != null) break;
                EntityEmanaAdapted.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityEmanaAdapted vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityEmanaAdapted.this.posX;
                double d1 = this.posY - EntityEmanaAdapted.this.posY;
                double d2 = this.posZ - EntityEmanaAdapted.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityEmanaAdapted.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityEmanaAdapted.this.motionX *= 0.5;
                    EntityEmanaAdapted.this.motionY *= 0.5;
                    EntityEmanaAdapted.this.motionZ *= 0.5;
                } else {
                    EntityEmanaAdapted.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityEmanaAdapted.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityEmanaAdapted.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityEmanaAdapted.this.getAttackTarget() == null) {
                        EntityEmanaAdapted.this.renderYawOffset = EntityEmanaAdapted.this.rotationYaw = -((float)MathHelper.atan2((double)EntityEmanaAdapted.this.motionX, (double)EntityEmanaAdapted.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityEmanaAdapted.this.getAttackTarget().posX - EntityEmanaAdapted.this.posX;
                        double d5 = EntityEmanaAdapted.this.getAttackTarget().posZ - EntityEmanaAdapted.this.posZ;
                        EntityEmanaAdapted.this.renderYawOffset = EntityEmanaAdapted.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

