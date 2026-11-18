/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTUtil
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityProjectileHomming
extends EntityLiving {
    private float damage;
    private EntityLivingBase owner;
    private Entity target;
    @Nullable
    private UUID ownerUniqueId;
    private BlockPos ownerBlockPos;
    @Nullable
    private UUID targetUniqueId;
    private BlockPos targetBlockPos;

    public EntityProjectileHomming(World worldIn) {
        super(worldIn);
        this.setSize(0.3125f, 0.3125f);
        this.noClip = true;
        this.moveHelper = new AIMoveControl(this);
        this.setNoGravity(true);
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @SideOnly(value=Side.CLIENT)
    public EntityProjectileHomming(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
        this(worldIn);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.motionX = motionXIn;
        this.motionY = motionYIn;
        this.motionZ = motionZIn;
    }

    public EntityProjectileHomming(World worldIn, EntityLivingBase ownerIn, Entity targetIn, float damage) {
        this(worldIn);
        this.owner = ownerIn;
        BlockPos blockpos = new BlockPos((Entity)ownerIn);
        double d0 = (double)blockpos.getX() + 0.5;
        double d1 = (double)blockpos.getY() + 0.5;
        double d2 = (double)blockpos.getZ() + 0.5;
        this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
        this.target = targetIn;
        this.damage = damage;
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.owner != null) {
            BlockPos blockpos = new BlockPos((Entity)this.owner);
            NBTTagCompound nbttagcompound = NBTUtil.createUUIDTag((UUID)this.owner.getUniqueID());
            nbttagcompound.setInteger("X", blockpos.getX());
            nbttagcompound.setInteger("Y", blockpos.getY());
            nbttagcompound.setInteger("Z", blockpos.getZ());
            compound.setTag("Owner", (NBTBase)nbttagcompound);
        }
        if (this.target != null) {
            BlockPos blockpos1 = new BlockPos(this.target);
            NBTTagCompound nbttagcompound1 = NBTUtil.createUUIDTag((UUID)this.target.getUniqueID());
            nbttagcompound1.setInteger("X", blockpos1.getX());
            nbttagcompound1.setInteger("Y", blockpos1.getY());
            nbttagcompound1.setInteger("Z", blockpos1.getZ());
            compound.setTag("Target", (NBTBase)nbttagcompound1);
        }
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Owner", 10)) {
            NBTTagCompound nbttagcompound = compound.getCompoundTag("Owner");
            this.ownerUniqueId = NBTUtil.getUUIDFromTag((NBTTagCompound)nbttagcompound);
            this.ownerBlockPos = new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z"));
        }
        if (compound.hasKey("Target", 10)) {
            NBTTagCompound nbttagcompound1 = compound.getCompoundTag("Target");
            this.targetUniqueId = NBTUtil.getUUIDFromTag((NBTTagCompound)nbttagcompound1);
            this.targetBlockPos = new BlockPos(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"));
        }
    }

    public void onUpdate() {
        if (!this.worldObj.isRemote && DifficultyAccess.difficulty(this.worldObj) == EnumDifficulty.PEACEFUL) {
            this.setDead();
        } else {
            super.onUpdate();
            if (!this.worldObj.isRemote) {
                if (this.target == null && this.targetUniqueId != null) {
                    this.setDead();
                }
                if (this.owner == null && this.ownerUniqueId != null) {
                    this.setDead();
                }
                if (this.target != null && !this.target.isDead) {
                    this.moveHelper.setMoveTo(this.target.posX, this.target.posY - (double)this.target.height * 1.5, this.target.posZ, 1.5);
                }
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 2.0);
                List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                boolean flag = false;
                for (EntityLivingBase mob : moblist) {
                    if (mob == null || mob instanceof EntityParasiteBase || mob instanceof EntityProjectileHomming) continue;
                    DamageSource damagesource = this.owner == null ? DamageSource.causeThrownDamage((Entity)this, (Entity)this) : DamageSource.causeThrownDamage((Entity)this, (Entity)this.owner);
                    if (flag) continue;
                    flag = mob.attackEntityFrom(damagesource, this.damage);
                }
                if (flag) {
                    this.setDead();
                }
                if (this.ticksExisted > 200) {
                    this.setDead();
                }
            }
        }
    }

    public boolean isBurning() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 16384.0;
    }

    protected void bulletHit(RayTraceResult result) {
        if (result.entityHit == null) {
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)this.worldObj), EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 7, 0.2, 0.2, 0.2, 0.0, new int[0]);
        } else {
            DamageSource damagesource;
            boolean flag;
            if (result.entityHit instanceof EntityParasiteBase || !(flag = result.entityHit.attackEntityFrom(damagesource = this.owner == null ? DamageSource.causeThrownDamage((Entity)this, (Entity)this) : DamageSource.causeThrownDamage((Entity)this, (Entity)this.owner), this.damage)) || result.entityHit instanceof EntityLivingBase) {
                // empty if block
            }
            this.setDead();
        }
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.worldObj.isRemote) {
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0f, 1.0f);
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)this.worldObj), EnumParticleTypes.CRIT, this.posX, this.posY, this.posZ, 15, 0.2, 0.2, 0.2, 0.0, new int[0]);
            this.setDead();
        }
        return true;
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityProjectileHomming vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityProjectileHomming.this.posX;
                double d1 = this.posY - EntityProjectileHomming.this.posY;
                double d2 = this.posZ - EntityProjectileHomming.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityProjectileHomming.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityProjectileHomming.this.motionX *= 0.5;
                    EntityProjectileHomming.this.motionY *= 0.5;
                    EntityProjectileHomming.this.motionZ *= 0.5;
                } else {
                    EntityProjectileHomming.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityProjectileHomming.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityProjectileHomming.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityProjectileHomming.this.target == null) {
                        EntityProjectileHomming.this.rotationYaw = -((float)MathHelper.atan2((double)EntityProjectileHomming.this.motionX, (double)EntityProjectileHomming.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = ((EntityProjectileHomming)EntityProjectileHomming.this).target.posX - EntityProjectileHomming.this.posX;
                        double d5 = ((EntityProjectileHomming)EntityProjectileHomming.this).target.posZ - EntityProjectileHomming.this.posZ;
                        EntityProjectileHomming.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

