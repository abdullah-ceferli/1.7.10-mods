/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.hijacked;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPHijacked;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.MsgQlipShake;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHiGolem
extends EntityPHijacked
implements EntityBodyParts {
    private EntityBody leftTendril;
    private int attacking;
    private float lockedHeadYaw = 0.0f;
    private boolean skillCharge;
    private BlockPos positi;
    private int positicool;
    public boolean chargeFlag;
    private int timeGrab;

    public EntityHiGolem(World worldIn) {
        super(worldIn);
        this.setSize(1.1f, 2.7f);
        this.type = (byte)11;
        this.stepHeight = 1.0f;
        this.chargeFlag = false;
        this.leftTendril = new EntityBody(this, 0.1f, 0.1f, 1.0f, 0.0f, 0.0f, 1, 1, true);
    }

    @Override
    public int getParasiteIDRegister() {
        return 301;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.higolemCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 1, 16));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 100, 32, 3, true, 1, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.HIGOLEM_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.HIGOLEM_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2725);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.HIGOLEM_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.HIGOLEM_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.hijackedFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.leftTendril.onUpdate();
        if (this.srpTicks == 10 && !this.worldObj.isRemote && this.getPassengers().size() == 0 && this.chargeFlag && this.positi == null) {
            this.leftTendril.setBodySize(0.1f, 0.1f);
            this.chargeFlag = false;
            this.worldObj.setEntityState((Entity)this, (byte)22);
        }
    }

    @Override
    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2 || k == 3) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2 || k == 3) {
                if (this.getAttackTarget() != null) {
                    if (!this.getAttackTarget().isEntityAlive()) {
                        this.setAttackTarget(null);
                        this.setParasiteStatus(0);
                    } else if (this.positi == null) {
                        this.setParasiteStatus(Math.min(k, 1));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setAttackTarget(null);
                }
            }
        }
    }

    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            this.rotationYawHead = this.lockedHeadYaw;
            if (this.chargeFlag) {
                this.leftTendril.setBodySize(1.2f, 2.8f);
            }
            if (!this.worldObj.isRemote) {
                this.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.moveSlowdown, 20, 200, false, false));
                if (this.srpTicks == 10 && this.positi == null) {
                    if (passenger instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)passenger;
                        SRPNetwork.CHANNEL.sendTo((IMessage)new MsgQlipShake(250, 0, true, false, 4.0f), (EntityPlayerMP)player);
                        if (player.capabilities.disableDamage) {
                            return;
                        }
                    }
                    this.attackEntityAsMob(passenger);
                    this.attackEntityAsMobMinimum((EntityLivingBase)passenger, this.getMiniDamage() * 5.0f);
                }
            }
            Vec3d vec3d = this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
            passenger.motionX = 0.0;
            passenger.motionZ = 0.0;
            passenger.setPosition(this.posX + vec3d.xCoord * 1.5, this.posY + (double)this.getEyeHeight(), this.posZ + vec3d.zCoord * 1.5);
        }
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        return this.attackEntityFrom(source, amount);
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        }
        super.setDead();
    }

    public float getEyeHeight() {
        return 1.73f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.HIGOLEM_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HIGOLEM_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.HIGOLEM_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 0.15f, 1.0f);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        return floo;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 11) {
            this.chargeFlag = true;
        } else if (id == 22) {
            this.chargeFlag = false;
            this.leftTendril.setBodySize(0.1f, 0.1f);
        } else if (id == 100) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(EnumParticleTypes.FLAME);
            }
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillCharge;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillCharge = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.charge();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void charge() {
        ++this.attacking;
        this.miniCapA = true;
        if (this.positi != null) {
            this.chargeMoving();
            for (EntityLivingBase mob : EntityQueries.entities(this.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(this), 2.0, 0.0, 2.0))) {
                if (mob == this || mob instanceof EntityParasiteBase || mob == this.getAttackTarget()) continue;
                float f = (float)MathHelper.atan2((double)(mob.posZ - this.posZ), (double)(mob.posX - this.posX));
                EntityDamage damage = new EntityDamage(this.worldObj, mob.posX, mob.posY, mob.posZ, f, (EntityLivingBase)this, 1.0f, false, 0.5f);
                this.worldObj.spawnEntityInWorld((Entity)damage);
            }
            this.skillBreakBlocks();
            if (!this.onGround) {
                this.motionX *= 0.7;
                this.motionZ *= 0.7;
            }
        } else {
            if (this.attacking < 20) {
                EntityLivingBase entitylivingbase;
                this.worldObj.setEntityState((Entity)this, (byte)100);
                if (this.attacking == 2) {
                    float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f + 2.0f;
                    this.playSound(this.getHurtSound(DamageSource.generic), 4.0f, v);
                }
                if ((entitylivingbase = this.getAttackTarget()) == null || !this.onGround || this.isInWater() || entitylivingbase.posY > this.posY + 3.0) {
                    this.skillCharge = true;
                    this.attacking = 0;
                    this.miniCapA = false;
                    this.setParasiteStatus(0);
                    return;
                }
                if (!entitylivingbase.isEntityAlive() || !this.getPassengers().isEmpty()) {
                    this.skillCharge = true;
                    this.attacking = 0;
                    this.miniCapA = false;
                    this.setParasiteStatus(0);
                    return;
                }
                if (this.attacking <= 19) {
                    double dis = this.getDistanceToEntity((Entity)entitylivingbase);
                    this.setParasiteStatus(3);
                    this.getNavigator().clearPathEntity();
                }
            }
            if (this.attacking == 20) {
                this.positi = this.getChargeDestination((Entity)this.getAttackTarget(), 0.5);
                this.chargeFlag = true;
                this.worldObj.setEntityState((Entity)this, (byte)11);
            }
            if (this.attacking >= 60 && this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
                this.attacking = 0;
                this.miniCapA = false;
                this.skillCharge = true;
                this.setParasiteStatus(1);
            }
        }
    }

    private BlockPos getChargeDestination(Entity target, double distance) {
        double d0 = this.getDistanceSqToEntity(target);
        if (target != this) {
            this.timeGrab = (int)this.getDistanceToEntity(target);
        }
        if ((d0 < 9.0 || d0 >= 256.0) && target != this || !this.canEntityBeSeen(target) && target != this) {
            return null;
        }
        if (target.posY > this.posY + 2.0) {
            return null;
        }
        Vec3d lookVec = target.getLook(1.0f);
        double startX = target.posX;
        double startY = target.posY + (double)target.getEyeHeight();
        double startZ = target.posZ;
        Vec3d startVec = new Vec3d(startX, startY, startZ);
        Vec3d destVec = startVec.add(lookVec.scale(distance));
        boolean bon = true;
        this.lockedHeadYaw = this.rotationYawHead;
        return new BlockPos(destVec.xCoord * (double)bon, destVec.yCoord, destVec.zCoord * (double)bon);
    }

    private void chargeMoving() {
        this.rotationYawHead = this.lockedHeadYaw;
        this.setParasiteStatus(3);
        ++this.positicool;
        double str = 0.35;
        double deltaX = (double)this.positi.getX() - this.posX;
        double deltaY = (double)this.positi.getY() - this.posY;
        double deltaZ = (double)this.positi.getZ() - this.posZ;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        if (distance == 0.0) {
            return;
        }
        deltaY /= distance;
        this.motionX += (deltaX /= distance) * str;
        this.motionZ += (deltaZ /= distance) * str;
        this.lookAt(this.positi.getX(), this.positi.getY(), this.positi.getZ());
        if (this.getAttackTarget() != null && this.getDistanceSqToEntity((Entity)this.getAttackTarget()) <= 9.0 && this.getPassengers().isEmpty()) {
            this.getAttackTarget().startRiding((Entity)this, true);
            if (this.getAttackTarget() instanceof EntityPlayerMP) {
                EntityPlayer player = (EntityPlayer)this.getAttackTarget();
                SRPNetwork.CHANNEL.sendTo((IMessage)new MsgQlipShake(2500, 0, true, false, 4.0f), (EntityPlayerMP)player);
            }
        }
        if (this.positicool == 2) {
            this.positi = this.getChargeDestination((Entity)this, 1000.0);
        }
        if (this.positicool > this.timeGrab + 50) {
            this.positi = null;
            this.positicool = 0;
        }
    }
}

