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
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAlafhaBall;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.Random;
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
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityAlafha
extends EntityPPure
implements EntityBodyParts,
EntityCutomAttack,
EntityCanShoot,
EntityCanFly {
    private EntityBody head;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityAlafha.class, (DataSerializer)DataSerializers.BYTE);

    public EntityAlafha(World worldIn) {
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
        this.head = new EntityBody(this, 1.2f, 1.2f, 1.0f, 3.0f, 0.0f, -1, 1, false, 0.2f);
        this.adaptationCap = 0.95f;
    }

    @Override
    public int getParasiteIDRegister() {
        return 9;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(5, (EntityAIBase)new EntityAIAirVomitSummon(this, SRPConfigMobs.alafhaSummoningCooldown, SRPConfigMobs.alafhaLimit, SRPConfigMobs.alafhaMobList));
        this.tasks.addTask(1, (EntityAIBase)new EntityAIAttackProjectile(this, 20, 10, 4));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackMeleeNotGround(this, 4.5, 16.0, 0.045, true));
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
        if ((BlockStates.get(this.worldObj, EntityQueries.position(this).down(1)).getBlock() != Blocks.air || BlockStates.get(this.worldObj, EntityQueries.position(this).down(2)).getBlock() != Blocks.air) && this.getAttackTarget() != null) {
            this.motionY = 0.5;
        }
        this.head.onUpdate();
    }

    public void onUpdate() {
        super.onUpdate();
        this.setNoGravity(true);
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        return this.attackEntityFrom(source, amount * 3.0f);
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.attackEntityAsMob(entityIn);
    }

    public float getEyeHeight() {
        return 1.6f;
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.head);
        }
        super.setDead();
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

    public EntityFireball getProj(double accelX, double accelY, double accelZ) {
        this.playSound(SRPSounds.ALAFHA_SHOOTING, 2.0f, 1.0f);
        return new EntityProjectileAlafhaBall(this.worldObj, (EntityLivingBase)this, accelX, accelY, accelZ);
    }

    @Override
    public void playProjSound() {
        this.playSound(SRPSounds.ALAFHA_SHOOTINGPOST, 2.0f, 1.0f);
    }

    static class EntityAIAirVomitSummon
    extends EntityAIBase {
        private final EntityParasiteBase parentEntity;
        private int limit = 0;
        private int sCooldown;
        private int sLimit;
        private String[] sMobs;
        private int attackTimer = 0;
        private int attacking = 0;
        private double targetX;
        private double targetY;
        private double targetZ;

        public EntityAIAirVomitSummon(EntityParasiteBase summoner, int cooldown, int limit, String[] mobList) {
            this.parentEntity = summoner;
            this.sCooldown = cooldown;
            this.sLimit = limit;
            this.sMobs = mobList;
        }

        public boolean shouldExecute() {
            if (this.attacking >= 1) {
                return true;
            }
            if (this.parentEntity.getAttackTarget() != null) {
                return this.parentEntity.getAttackTarget().onGround;
            }
            return false;
        }

        public void startExecuting() {
        }

        public void resetTask() {
        }

        public void updateTask() {
            if (this.attacking >= 1) {
                ++this.attacking;
                this.parentEntity.setParasiteStatus(10);
                this.parentEntity.getNavigator().clearPathEntity();
                if (this.attacking == 2) {
                    // empty if block
                }
                if (this.attacking % 20 == 0 && this.attacking >= 40) {
                    EntityCanSummon parent = (EntityCanSummon)((Object)this.parentEntity);
                    parent.checkID();
                    if (parent.getActualParasites() < parent.getTotalParasites() && this.limit < this.sLimit && ParasiteEventEntity.spawnBiomassFromProjectile(this.parentEntity, this.sMobs, this.parentEntity.getAttackTarget())) {
                        ++this.limit;
                        this.parentEntity.particleStatus((byte)8);
                    }
                }
                if (this.attacking >= 80 || this.limit >= this.sLimit) {
                    this.attacking = 0;
                    this.attackTimer = 0;
                    this.limit = 0;
                    this.parentEntity.setParasiteStatus(2);
                }
            } else if (this.parentEntity.getAttackTarget() == null) {
                if (this.attackTimer > 0) {
                    --this.attackTimer;
                }
            } else if (this.parentEntity.getAttackTarget().isDead) {
                if (this.attackTimer > 0) {
                    --this.attackTimer;
                }
            } else {
                EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
                if (this.parentEntity.canEntityBeSeen((Entity)entitylivingbase) && this.parentEntity.getDistanceSqToEntity((Entity)entitylivingbase) < 256.0 && entitylivingbase.onGround) {
                    ++this.attackTimer;
                    if (this.attackTimer >= this.sCooldown && this.attacking == 0) {
                        ++this.attacking;
                        this.targetX = this.parentEntity.posX;
                        this.targetY = this.parentEntity.posY;
                        this.targetZ = this.parentEntity.posZ;
                    }
                } else if (this.attackTimer > 0 && this.attackTimer > 0) {
                    --this.attackTimer;
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
            double d2;
            double d1;
            EntityMoveHelper entitymovehelper = EntityAlafha.this.getMoveHelper();
            if (!entitymovehelper.isUpdating()) {
                return true;
            }
            double d0 = entitymovehelper.getX() - EntityAlafha.this.posX;
            double d3 = d0 * d0 + (d1 = entitymovehelper.getY() - EntityAlafha.this.posY) * d1 + (d2 = entitymovehelper.getZ() - EntityAlafha.this.posZ) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            if (EntityAlafha.this.getAttackTarget() != null) {
                BlockPos blockpos = new BlockPos((Entity)EntityAlafha.this);
                int flag = 1;
                double speed = 0.11;
                if (EntityAlafha.this.getDistanceSqToEntity((Entity)EntityAlafha.this.getAttackTarget()) > 400.0) {
                    blockpos = new BlockPos((Entity)EntityAlafha.this.getAttackTarget());
                    flag = 2;
                    speed += 0.11;
                } else if (EntityAlafha.this.getDistanceSqToEntity((Entity)EntityAlafha.this.getAttackTarget()) < 100.0) {
                    blockpos = new BlockPos((Entity)EntityAlafha.this.getAttackTarget());
                    flag = 3;
                    speed += 0.11;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.add(EntityAlafha.this.rand.nextInt(15) - 7, EntityAlafha.this.rand.nextInt(9) - 5, EntityAlafha.this.rand.nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.add(EntityAlafha.this.rand.nextInt(6) - 2, EntityAlafha.this.rand.nextInt(7) - 2, EntityAlafha.this.rand.nextInt(6) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.add(EntityAlafha.this.rand.nextInt(4) + 3, EntityAlafha.this.rand.nextInt(5) + 4, EntityAlafha.this.rand.nextInt(4) + 3);
                    }
                    if (!WorldAccess.isAirBlock(EntityAlafha.this.worldObj, blockpos1)) continue;
                    EntityAlafha.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntityAlafha.this.getAttackTarget() == null) {
                        EntityAlafha.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                Random random = EntityAlafha.this.getRNG();
                double d0 = EntityAlafha.this.posX + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntityAlafha.this.posY + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntityAlafha.this.posZ + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntityAlafha.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.5);
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityAlafha vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityAlafha.this.posX;
                double d1 = this.posY - EntityAlafha.this.posY;
                double d2 = this.posZ - EntityAlafha.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityAlafha.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityAlafha.this.motionX *= 0.5;
                    EntityAlafha.this.motionY *= 0.5;
                    EntityAlafha.this.motionZ *= 0.5;
                } else {
                    EntityAlafha.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityAlafha.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityAlafha.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityAlafha.this.getAttackTarget() == null) {
                        EntityAlafha.this.renderYawOffset = EntityAlafha.this.rotationYaw = -((float)MathHelper.atan2((double)EntityAlafha.this.motionX, (double)EntityAlafha.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityAlafha.this.getAttackTarget().posX - EntityAlafha.this.posX;
                        double d5 = EntityAlafha.this.getAttackTarget().posZ - EntityAlafha.this.posZ;
                        EntityAlafha.this.renderYawOffset = EntityAlafha.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

