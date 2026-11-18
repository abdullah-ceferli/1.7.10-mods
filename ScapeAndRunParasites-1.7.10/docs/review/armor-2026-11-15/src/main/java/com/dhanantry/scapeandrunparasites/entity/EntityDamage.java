/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.event.ForgeEventFactory
 */
package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityDamage
extends Entity {
    private int lifeTicks = 10;
    private EntityLivingBase caster;
    private UUID casterUuid;
    private float mobDamage;
    private boolean pulling;
    private float str;
    private boolean follow;
    private Entity ffollow;

    public EntityDamage(World worldIn) {
        super(worldIn);
        this.setSize(1.2f, 0.9f);
    }

    public EntityDamage(World worldIn, double x, double y, double z, float rotation, EntityLivingBase casterIn, float damage, boolean pull, float strenght) {
        this(worldIn);
        this.setCaster(casterIn);
        this.rotationYaw = rotation * 57.295776f;
        this.setPosition(x, y, z);
        this.mobDamage = damage;
        this.pulling = pull;
        this.str = strenght;
    }

    public EntityDamage(World worldIn, double x, double y, double z, float rotation, EntityLivingBase casterIn, float damage, boolean pull, float strenght, float w, float h) {
        this(worldIn, x, y, z, rotation, casterIn, damage, pull, strenght);
        this.setSize(w, h);
    }

    protected void entityInit() {
    }

    public void setCaster(@Nullable EntityLivingBase in) {
        this.caster = in;
        this.casterUuid = in == null ? null : in.getUniqueID();
    }

    public void setFollower(Entity in) {
        this.follow = true;
        this.ffollow = in;
    }

    @Nullable
    public EntityLivingBase getCaster() {
        Entity entity;
        if (this.caster == null && this.casterUuid != null && this.worldObj instanceof WorldServer && (entity = ((WorldServer)this.worldObj).getEntityFromUuid(this.casterUuid)) instanceof EntityLivingBase) {
            this.caster = (EntityLivingBase)entity;
        }
        return this.caster;
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.casterUuid = compound.getUniqueId("OwnerUUID");
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (this.casterUuid != null) {
            compound.setUniqueId("OwnerUUID", this.casterUuid);
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            if (this.follow) {
                if (this.ffollow == null) {
                    if (--this.lifeTicks < 0) {
                        this.setDead();
                    }
                } else {
                    AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 1.5);
                    List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                    for (EntityLivingBase entityIn : moblist) {
                        if (this.worldObj.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), false, true, false) != null) continue;
                        this.damage(entityIn);
                    }
                    int i1 = MathHelper.floor_double((double)(this.posY + 0.1));
                    double l1 = this.posX;
                    double i2 = this.posZ;
                    boolean flag = false;
                    int offsetT = 0;
                    int BGrange = 2;
                    int BGheight = 2;
                    for (int k2 = -1 * BGrange; k2 <= BGrange; ++k2) {
                        for (int l2 = -1 * BGrange; l2 <= BGrange; ++l2) {
                            for (int j = 1 + offsetT; j <= BGheight + offsetT; ++j) {
                                double i3 = l1 + (double)k2;
                                double k = i1 + j;
                                double l = i2 + (double)l2;
                                BlockPos blockpos = new BlockPos(i3, k, l);
                                IBlockState iblockstate = BlockStates.get(this.worldObj, blockpos);
                                Block block = iblockstate.getBlock();
                                float bHard = iblockstate.getBlockHardness(this.worldObj, blockpos);
                                if (!((double)bHard <= 0.3 && bHard >= 0.0f) && !(block instanceof IMetaName) || block == SRPBlocks.BiomeHeart || block == SRPBlocks.ColonyHeart || block == SRPBlocks.ParasiteRubbleDense || block == SRPBlocks.ParasiteCanisterActive || block == Blocks.air || !block.canEntityDestroy(iblockstate, (IBlockAccess)this.worldObj, blockpos, (Entity)this) || !ForgeEventFactory.onEntityDestroyBlock((EntityLivingBase)this.getCaster(), (BlockPos)blockpos, (IBlockState)iblockstate)) continue;
                                flag = WorldAccess.destroyBlock(this.worldObj, blockpos, true) || flag;
                            }
                        }
                    }
                    this.posX = this.ffollow.posX;
                    this.posY = this.ffollow.posY;
                    this.posZ = this.ffollow.posZ;
                    if (!this.ffollow.isEntityAlive()) {
                        this.ffollow = null;
                    }
                }
            } else {
                for (EntityLivingBase entitylivingbase : EntityQueries.entities(this.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(this), 0.3, 0.0, 0.2))) {
                    this.damage(entitylivingbase);
                }
                if (--this.lifeTicks < 0) {
                    this.setDead();
                }
            }
        }
    }

    private void damage(EntityLivingBase in) {
        if (in instanceof EntityParasiteBase) {
            return;
        }
        EntityLivingBase entitylivingbase = this.getCaster();
        if (in.isEntityAlive() && !in.getIsInvulnerable() && in != entitylivingbase) {
            if (entitylivingbase == null) {
                in.attackEntityFrom(DamageSource.generic.setDamageBypassesArmor(), this.mobDamage);
            } else {
                if (entitylivingbase.isOnSameTeam((Entity)in)) {
                    return;
                }
                if (this.pulling) {
                    if (in instanceof EntityPlayer) {
                        this.knockBack2((Entity)in, this.str, in.posX - this.caster.posX, in.posZ - this.caster.posZ);
                    } else {
                        this.knockBack2((Entity)in, this.str / 3.0f, in.posX - this.caster.posX, in.posZ - this.caster.posZ);
                    }
                } else if (in instanceof EntityPlayer) {
                    this.knockBack2((Entity)in, this.str, this.caster.posX - in.posX, this.caster.posZ - in.posZ);
                } else {
                    this.knockBack2((Entity)in, this.str, this.caster.posX - in.posX, this.caster.posZ - in.posZ);
                }
                entitylivingbase.attackEntityAsMob((Entity)in);
            }
        }
    }

    private void knockBack2(Entity entityIn, float strength, double xRatio, double zRatio) {
        float f = MathHelper.sqrt_double((double)(xRatio * xRatio + zRatio * zRatio));
        entityIn.motionX /= 2.0;
        entityIn.motionZ /= 2.0;
        entityIn.motionX -= xRatio / (double)f * (double)strength;
        entityIn.motionZ -= zRatio / (double)f * (double)strength;
        if (entityIn.onGround) {
            entityIn.motionY /= 2.0;
            entityIn.motionY += (double)strength;
            if (entityIn.motionY > (double)0.4f) {
                entityIn.motionY = 0.4f;
            }
        }
    }
}

