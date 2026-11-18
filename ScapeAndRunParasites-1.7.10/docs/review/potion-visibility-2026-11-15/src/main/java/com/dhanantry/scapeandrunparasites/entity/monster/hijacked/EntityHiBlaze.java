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
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.hijacked;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPHijacked;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHiBlaze
extends EntityPHijacked
implements EntityCanShoot,
EntityCanFly {
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityHiBlaze.class, (DataSerializer)DataSerializers.BYTE);
    private int count;

    public EntityHiBlaze(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(0.6f, 0.95f);
        this.type = (byte)11;
        this.setNoGravity(true);
        this.tasks.removeTask((EntityAIBase)this.folow);
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
    }

    @Override
    public int getParasiteIDRegister() {
        return 302;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infbearCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.primitiveFollow));
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(1, (EntityAIBase)new EntityAIAttackProjectile(this, 80, 20, 1));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.EMANA_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.EMANA_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.EMANA_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.EMANA_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.primitiveFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.srpTicks == 10) {
            if (this.onGround && !this.worldObj.isRemote) {
                this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
            }
            if ((BlockStates.get(this.worldObj, EntityQueries.position(this).down(1)).getBlock() != Blocks.air || BlockStates.get(this.worldObj, EntityQueries.position(this).down(2)).getBlock() != Blocks.air) && this.getAttackTarget() != null) {
                this.motionY = 0.1;
            }
            List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, Boxes.grow(Boxes.create(EntityQueries.position(this)), 7.0, 3.0, 7.0));
            for (EntityParasiteBase mob : moblist) {
                mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.fireResistance, 60, 0, false, true));
            }
        }
    }

    public void onUpdate() {
        super.onUpdate();
        this.setNoGravity(true);
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 0.6f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.HI_BLAZE_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HI_BLAZE_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.HI_BLAZE_DEATH;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
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

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 100) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(EnumParticleTypes.FLAME);
            }
        } else {
            super.handleHealthUpdate(id);
        }
    }

    public EntityFireball getProj(double accelX, double accelY, double accelZ) {
        float pit = 1.0f;
        if (this.count == 4) {
            pit = 2.0f;
        }
        EntityProjectileSpineball ball = new EntityProjectileSpineball(this.worldObj, (EntityLivingBase)this, accelX, accelY, accelZ, SRPAttributes.EMANA_RANGED_DAMAGE);
        ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration, SRPConfigMobs.emanaPoisonAmplifier);
        ball.setGearDamage(SRPConfigMobs.emanaGearD);
        return ball;
    }

    @Override
    public void playProjSound() {
        ++this.count;
        if (this.count == 4) {
            this.worldObj.setEntityState((Entity)this, (byte)100);
            float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f + 2.0f;
            this.playSound(this.getHurtSound(DamageSource.generic), 4.0f, v);
            return;
        }
        this.playSound(SRPSounds.HI_BLAZE_FIRE, 2.0f, 1.0f);
    }

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            return !EntityHiBlaze.this.getMoveHelper().isUpdating() && EntityHiBlaze.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityHiBlaze.this);
            int flag = 1;
            double speed = 0.2;
            if (EntityHiBlaze.this.getAttackTarget() != null) {
                if (EntityHiBlaze.this.getDistanceSqToEntity((Entity)EntityHiBlaze.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityHiBlaze.this.getAttackTarget());
                    flag = 2;
                } else if (EntityHiBlaze.this.getDistanceSqToEntity((Entity)EntityHiBlaze.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityHiBlaze.this.getAttackTarget());
                    flag = 3;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityHiBlaze.this.rand.nextInt(15) - 7, EntityHiBlaze.this.rand.nextInt(11) - 5, EntityHiBlaze.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityHiBlaze.this.rand.nextInt(6) - 2, EntityHiBlaze.this.rand.nextInt(7) - 2, EntityHiBlaze.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityHiBlaze.this.rand.nextInt(4) + 3, EntityHiBlaze.this.rand.nextInt(5) + 4, EntityHiBlaze.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityHiBlaze.this.worldObj, blockpos1)) continue;
                EntityHiBlaze.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityHiBlaze.this.getAttackTarget() != null) break;
                EntityHiBlaze.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityHiBlaze vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityHiBlaze.this.posX;
                double d1 = this.posY - EntityHiBlaze.this.posY;
                double d2 = this.posZ - EntityHiBlaze.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityHiBlaze.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityHiBlaze.this.motionX *= 0.5;
                    EntityHiBlaze.this.motionY *= 0.5;
                    EntityHiBlaze.this.motionZ *= 0.5;
                } else {
                    EntityHiBlaze.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityHiBlaze.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityHiBlaze.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityHiBlaze.this.getAttackTarget() == null) {
                        EntityHiBlaze.this.renderYawOffset = EntityHiBlaze.this.rotationYaw = -((float)MathHelper.atan2((double)EntityHiBlaze.this.motionX, (double)EntityHiBlaze.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityHiBlaze.this.getAttackTarget().posX - EntityHiBlaze.this.posX;
                        double d5 = EntityHiBlaze.this.getAttackTarget().posZ - EntityHiBlaze.this.posZ;
                        EntityHiBlaze.this.renderYawOffset = EntityHiBlaze.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

