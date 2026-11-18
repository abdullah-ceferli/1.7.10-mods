/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;

public class EntityJinjo
extends EntityPPreeminent
implements EntityCanFly {
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityJinjo.class, (DataSerializer)DataSerializers.BYTE);

    public EntityJinjo(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(3.7f, 4.4f);
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, true);
        if (SRPConfigMobs.ombooMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
        }
        this.canTeleportToo = true;
        this.adaptationCap = 0.95f;
        this.ignoreFrustumCheck = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 65;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.tasks.addTask(4, (EntityAIBase)new AIChargeAttack());
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(5, (EntityAIBase)new AIBomb(this));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIFlightLimits(this, 10, false));
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.JINJO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.JINJO_HURT;
    }

    protected float getSoundVolume() {
        return 6.0f;
    }

    protected String getDeathSound() {
        return SRPSounds.JINJO_DEATH;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.JINJO_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.JINJO_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.JINJO_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.JINJO_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.preeminentFollow);
    }

    @Override
    public void onLivingUpdate() {
        block7: {
            block6: {
                super.onLivingUpdate();
                if (this.worldObj.isRemote) break block6;
                if (this.onGround) {
                    this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
                }
                if (this.srpTicks == 10 && (BlockStates.get(this.worldObj, EntityQueries.position(this).down(1)).getBlock() != Blocks.air || BlockStates.get(this.worldObj, EntityQueries.position(this).down(2)).getBlock() != Blocks.air) && this.getAttackTarget() != null) {
                    this.motionY = 0.5;
                }
                if (this.srpTicks != 10 || this.rand.nextInt(7) != 0) break block7;
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
                if (origin == null) break block7;
                EntityKol aaa = new EntityKol(this.worldObj);
                aaa.copyLocationAndAnglesFrom((Entity)this);
                aaa.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
                this.worldObj.spawnEntityInWorld((Entity)aaa);
                break block7;
            }
            if (this.rand.nextInt(30) == 0) {
                for (int i = 0; i <= 20; ++i) {
                    if (i % 5 != 0) continue;
                    this.spawnParticlesGoreMouth(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.2, 0.0);
                }
            }
        }
    }

    public void onUpdate() {
        super.onUpdate();
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, true);
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.jinjoOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 2.4f;
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
            if (this.ccc >= 100) {
                this.ccc = 0;
                if (this.parent.getAttackTarget() != null) {
                    EntityLivingBase target = this.parent.getAttackTarget();
                    if (!target.onGround) {
                        this.ccc = 70;
                        return false;
                    }
                    if (target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 256.0) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void updateTask() {
            EntityBomb out = new EntityBomb(this.parent.worldObj, this.parent, SRPConfigMobs.jinjoGriefing);
            if (this.parent.getAttackTarget() != null) {
                boolean flag = this.parent.canEntityBeSeen((Entity)this.parent.getAttackTarget()) || this.parent.worldObj.rand.nextInt(3) == 0;
                out.copyLocationAndAnglesFrom((Entity)this.parent);
                out.setFuse(80);
                out.setStren(flag ? 4.0f : 8.0f);
                out.setSkin(flag ? 2 : 3);
                out.setDamage((float)this.parent.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() * SRPConfigMobs.jinjoExplotionMult, 7);
                this.parent.worldObj.spawnEntityInWorld((Entity)out);
                out.updateSTR();
            }
        }
    }

    class AIChargeAttack
    extends EntityAIBase {
        public AIChargeAttack() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            if (EntityJinjo.this.getAttackTarget() != null && EntityJinjo.this.rand.nextInt(5) == 0) {
                return EntityJinjo.this.getDistanceSqToEntity((Entity)EntityJinjo.this.getAttackTarget()) > 4.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityJinjo.this.getMoveHelper().isUpdating() && EntityJinjo.this.isCharging() && EntityJinjo.this.getAttackTarget() != null && EntityJinjo.this.getAttackTarget().isEntityAlive();
        }

        public void startExecuting() {
            EntityLivingBase entitylivingbase = EntityJinjo.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
            EntityJinjo.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 20.0, vec3d.zCoord, 0.2);
            EntityJinjo.this.setCharging(true);
        }

        public void resetTask() {
            EntityJinjo.this.setCharging(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = EntityJinjo.this.getAttackTarget();
            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                if (Boxes.intersects(Boxes.entityBox(EntityJinjo.this), Boxes.entityBox(entitylivingbase))) {
                    EntityJinjo.this.attackEntityAsMob((Entity)entitylivingbase);
                    EntityJinjo.this.setCharging(false);
                } else {
                    double d0 = EntityJinjo.this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityJinjo.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 20.0, vec3d.zCoord, 1.0);
                    } else {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityJinjo.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 20.0, vec3d.zCoord, 1.1);
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
            return !EntityJinjo.this.getMoveHelper().isUpdating() && EntityJinjo.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityJinjo.this);
            int flag = 1;
            double speed = 0.1;
            if (EntityJinjo.this.getAttackTarget() != null) {
                if (EntityJinjo.this.getDistanceSqToEntity((Entity)EntityJinjo.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityJinjo.this.getAttackTarget());
                    flag = 2;
                    speed += 0.05;
                } else if (EntityJinjo.this.getDistanceSqToEntity((Entity)EntityJinjo.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityJinjo.this.getAttackTarget());
                    flag = 3;
                    speed += 0.05;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityJinjo.this.rand.nextInt(15) - 7, EntityJinjo.this.rand.nextInt(11) - 5, EntityJinjo.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityJinjo.this.rand.nextInt(6) - 2, EntityJinjo.this.rand.nextInt(7) - 2, EntityJinjo.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityJinjo.this.rand.nextInt(4) + 3, EntityJinjo.this.rand.nextInt(5) + 4, EntityJinjo.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityJinjo.this.worldObj, blockpos1)) continue;
                EntityJinjo.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityJinjo.this.getAttackTarget() != null) break;
                EntityJinjo.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityJinjo vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityJinjo.this.posX;
                double d1 = this.posY - EntityJinjo.this.posY;
                double d2 = this.posZ - EntityJinjo.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityJinjo.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityJinjo.this.motionX *= 0.5;
                    EntityJinjo.this.motionY *= 0.5;
                    EntityJinjo.this.motionZ *= 0.5;
                } else {
                    EntityJinjo.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityJinjo.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityJinjo.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityJinjo.this.getAttackTarget() == null) {
                        EntityJinjo.this.renderYawOffset = EntityJinjo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityJinjo.this.motionX, (double)EntityJinjo.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityJinjo.this.getAttackTarget().posX - EntityJinjo.this.posX;
                        double d5 = EntityJinjo.this.getAttackTarget().posZ - EntityJinjo.this.posZ;
                        EntityJinjo.this.renderYawOffset = EntityJinjo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

