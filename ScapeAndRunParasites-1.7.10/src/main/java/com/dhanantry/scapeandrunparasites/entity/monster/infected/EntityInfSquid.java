/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
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
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.pathfinding.PathNavigateSwimmer
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSwim;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import com.dhanantry.scapeandrunparasites.backport.navigation.PathNavigateSwimmer;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityInfSquid
extends EntityPInfected
implements EntityCanSwim,
EntityCutomAttack {
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityInfSquid.class, (DataSerializer)DataSerializers.BYTE);

    public EntityInfSquid(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 0.9f);
        this.moveHelper = new AIMoveControl(this);
        this.canModRender = 0;
        this.type = (byte)15;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
    }

    @Override
    public int getParasiteIDRegister() {
        return 307;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infsquidCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackMeleeNotGround(this, 3.0, SRPConfig.primitiveFollow, 0.05, false, 0, 1));
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFSQUID_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.INFSQUID_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.INFSQUID_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFSQUID_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.infectedFollow);
    }

    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateSwimmer((EntityLiving)this, worldIn);
    }

    @Override
    public void onLivingUpdate() {
        if (this.isAIDisabled()) {
            return;
        }
        this.liquidLeap = 0;
        super.onLivingUpdate();
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, this.inWater);
        this.liquidLeap = 0;
    }

    public float getEyeHeight() {
        return this.height * 0.8f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.INFECTEDSQUID_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDSQUID_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.INFECTEDSQUID_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SoundEvents.ENTITY_WOLF_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.attackEntityAsMob(entityIn);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        return DifficultyAccess.difficulty(this.worldObj) != EnumDifficulty.PEACEFUL && SRPConfig.spawnDays <= (int)this.worldObj.getTotalWorldTime();
    }

    public boolean handleLavaMovement() {
        return this.worldObj.checkNoEntityCollision(Boxes.entityBox(this), (Entity)this);
    }

    public int getTalkInterval() {
        return 0;
    }

    @Override
    public boolean canDespawn() {
        return true;
    }

    protected int getExperiencePoints(EntityPlayer player) {
        return 1 + this.worldObj.rand.nextInt(3);
    }

    public void onEntityUpdate() {
        int i = this.getAir();
        super.onEntityUpdate();
        if (this.isAIDisabled()) {
            return;
        }
        if (this.isEntityAlive() && !this.isInWater()) {
            this.setAir(--i);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.attackEntityFrom(DamageSource.drown, 2.0f);
            }
        } else {
            this.setAir(300);
        }
    }

    public boolean isPushedByWater() {
        return false;
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
            return EntityInfSquid.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityInfSquid.this);
            int flag = 1;
            double speed = 0.13;
            if (EntityInfSquid.this.getAttackTarget() != null) {
                if (EntityInfSquid.this.getDistanceSqToEntity((Entity)EntityInfSquid.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityInfSquid.this.getAttackTarget());
                    flag = 2;
                } else if (EntityInfSquid.this.getDistanceSqToEntity((Entity)EntityInfSquid.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityInfSquid.this.getAttackTarget());
                    flag = 3;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityInfSquid.this.rand.nextInt(15) - 7, EntityInfSquid.this.rand.nextInt(11) - 5, EntityInfSquid.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityInfSquid.this.rand.nextInt(6) - 2, EntityInfSquid.this.rand.nextInt(7) - 2, EntityInfSquid.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityInfSquid.this.rand.nextInt(4) + 3, EntityInfSquid.this.rand.nextInt(5) + 4, EntityInfSquid.this.rand.nextInt(4) + 3);
                }
                if (BlockStates.get(EntityInfSquid.this.worldObj, blockpos1).getMaterial() != Material.water) continue;
                EntityInfSquid.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityInfSquid.this.getAttackTarget() != null) break;
                EntityInfSquid.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityInfSquid vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityInfSquid.this.posX;
                double d1 = this.posY - EntityInfSquid.this.posY;
                double d2 = this.posZ - EntityInfSquid.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityInfSquid.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityInfSquid.this.motionX *= 0.5;
                    EntityInfSquid.this.motionY *= 0.5;
                    EntityInfSquid.this.motionZ *= 0.5;
                } else {
                    EntityInfSquid.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityInfSquid.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityInfSquid.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityInfSquid.this.getAttackTarget() == null) {
                        EntityInfSquid.this.renderYawOffset = EntityInfSquid.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityInfSquid.this.motionX, (double)EntityInfSquid.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityInfSquid.this.getAttackTarget().posX - EntityInfSquid.this.posX;
                        double d5 = EntityInfSquid.this.getAttackTarget().posZ - EntityInfSquid.this.posZ;
                        EntityInfSquid.this.renderYawOffset = EntityInfSquid.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

