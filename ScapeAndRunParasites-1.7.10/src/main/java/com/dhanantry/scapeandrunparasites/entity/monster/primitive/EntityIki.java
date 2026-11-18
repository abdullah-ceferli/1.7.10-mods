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
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.entity.projectile.EntityFireball
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityIki
extends EntityPPrimitive
implements EntityCanShoot,
EntityCanFly {
    private EntityHitbox back;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityIki.class, (DataSerializer)DataSerializers.BYTE);
    private int count;

    public EntityIki(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(1.1f, 1.4f);
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, true);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.borderOrb = -1;
        this.ignoreFrustumCheck = true;
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
        this.back = new EntityHitbox((EntityLiving)this, -1.6f, 1.2f, -0.2f, 1.6f, 1.6f, 0.5f);
        this.hitboxes = new EntityHitbox[]{this.back};
    }

    @Override
    public int getParasiteIDRegister() {
        return 92;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.IKI_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.IKI_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.IKI_DEATH;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.primitiveFollow));
        this.tasks.addTask(4, (EntityAIBase)new AIChargeAttack());
        this.tasks.addTask(5, (EntityAIBase)new AIBomb(this));
        this.tasks.addTask(7, (EntityAIBase)new AIMoveRandom());
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.IKI_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.IKI_A_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.IKI_A_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.IKI_A_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.primitiveFollow);
    }

    @Override
    public void onLivingUpdate() {
        block4: {
            block3: {
                super.onLivingUpdate();
                if (this.worldObj.isRemote) break block3;
                if (this.srpTicks != 10) break block4;
                if (this.onGround && !this.worldObj.isRemote) {
                    this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
                }
                if (BlockStates.get(this.worldObj, EntityQueries.position(this).down(1)).getBlock() == Blocks.air && BlockStates.get(this.worldObj, EntityQueries.position(this).down(2)).getBlock() == Blocks.air || this.getAttackTarget() == null) break block4;
                this.motionY = 0.5;
                break block4;
            }
            if (this.rand.nextInt(25) == 0) {
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
    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 0.7f;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
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
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.EMANA_SHOOTING, 2.0f, pit);
        if (this.count >= 4) {
            this.count = 0;
            return new EntityProjectileNade(this.worldObj, (EntityLivingBase)this, accelX, accelY, accelZ, 3, 60);
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
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, this.getHurtSound(DamageSource.generic), 4.0f, v);
            return;
        }
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.EMANA_SHOOTINGPOST, 2.0f, 1.0f);
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
            if (this.ccc >= 20) {
                this.ccc = 0;
                if (this.parent.getAttackTarget() != null) {
                    EntityLivingBase target = this.parent.getAttackTarget();
                    if (!target.onGround) {
                        this.ccc = 10;
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
            List serverList = EntityQueries.loaded(this.parent.worldObj);
            int count = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityAta)) continue;
                ++count;
            }
            if (count < SRPConfig.worldGnatCap) {
                EntityAta out = new EntityAta(this.parent.worldObj);
                if (this.parent.getAttackTarget() != null) {
                    out.copyLocationAndAnglesFrom((Entity)this.parent);
                    this.parent.worldObj.spawnEntityInWorld((Entity)out);
                    SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.parent.posX, this.parent.posY, this.parent.posZ, 0.5f, 0.5f, 10));
                }
            } else {
                EntityBomb bomb = new EntityBomb(this.parent.worldObj, this.parent, false);
                if (this.parent.getAttackTarget() != null) {
                    if (this.parent.getAttackTarget().posY > this.parent.posY) {
                        return;
                    }
                    bomb.copyLocationAndAnglesFrom((Entity)this.parent);
                    bomb.setFuse(60);
                    bomb.setStren(0.0f);
                    bomb.setSkin(1);
                    bomb.setDamage((float)this.parent.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue(), 2);
                    bomb.rotationPitch -= -20.0f;
                    this.parent.worldObj.spawnEntityInWorld((Entity)bomb);
                    bomb.updateSTR();
                    SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.parent.posX, this.parent.posY, this.parent.posZ, 0.5f, 0.5f, 10));
                }
            }
        }
    }

    class AIChargeAttack
    extends EntityAIBase {
        public AIChargeAttack() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            if (EntityIki.this.getAttackTarget() != null && EntityIki.this.rand.nextInt(5) == 0) {
                return EntityIki.this.getDistanceSqToEntity((Entity)EntityIki.this.getAttackTarget()) > 4.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityIki.this.getMoveHelper().isUpdating() && EntityIki.this.isCharging() && EntityIki.this.getAttackTarget() != null && EntityIki.this.getAttackTarget().isEntityAlive();
        }

        public void startExecuting() {
            EntityLivingBase entitylivingbase = EntityIki.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
            EntityIki.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 7.0, vec3d.zCoord, 0.2);
            EntityIki.this.setCharging(true);
        }

        public void resetTask() {
            EntityIki.this.setCharging(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = EntityIki.this.getAttackTarget();
            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                if (Boxes.intersects(Boxes.entityBox(EntityIki.this), Boxes.entityBox(entitylivingbase))) {
                    EntityIki.this.attackEntityAsMob((Entity)entitylivingbase);
                    EntityIki.this.setCharging(false);
                } else {
                    double d0 = EntityIki.this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityIki.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 7.0, vec3d.zCoord, 1.0);
                    } else {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityIki.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 7.0, vec3d.zCoord, 1.1);
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
            return !EntityIki.this.getMoveHelper().isUpdating() && EntityIki.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityIki.this);
            int flag = 1;
            double speed = 0.5;
            if (EntityIki.this.getAttackTarget() != null) {
                if (EntityIki.this.getDistanceSqToEntity((Entity)EntityIki.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityIki.this.getAttackTarget());
                    flag = 2;
                    speed += 0.25;
                } else if (EntityIki.this.getDistanceSqToEntity((Entity)EntityIki.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityIki.this.getAttackTarget());
                    flag = 3;
                    speed += 0.25;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityIki.this.rand.nextInt(15) - 7, EntityIki.this.rand.nextInt(11) - 5, EntityIki.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityIki.this.rand.nextInt(6) - 2, EntityIki.this.rand.nextInt(7) - 2, EntityIki.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityIki.this.rand.nextInt(4) + 3, EntityIki.this.rand.nextInt(5) + 4, EntityIki.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityIki.this.worldObj, blockpos1)) continue;
                EntityIki.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityIki.this.getAttackTarget() != null) break;
                EntityIki.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityIki vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityIki.this.posX;
                double d1 = this.posY - EntityIki.this.posY;
                double d2 = this.posZ - EntityIki.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityIki.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityIki.this.motionX *= 0.5;
                    EntityIki.this.motionY *= 0.5;
                    EntityIki.this.motionZ *= 0.5;
                } else {
                    EntityIki.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityIki.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityIki.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityIki.this.getAttackTarget() == null) {
                        EntityIki.this.renderYawOffset = EntityIki.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityIki.this.motionX, (double)EntityIki.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityIki.this.getAttackTarget().posX - EntityIki.this.posX;
                        double d5 = EntityIki.this.getAttackTarget().posZ - EntityIki.this.posZ;
                        EntityIki.this.renderYawOffset = EntityIki.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

