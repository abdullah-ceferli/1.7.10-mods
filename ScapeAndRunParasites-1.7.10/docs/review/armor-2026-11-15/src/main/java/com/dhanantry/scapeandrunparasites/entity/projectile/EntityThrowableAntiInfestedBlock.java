/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.projectile.EntityThrowable
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedRubble;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityThrowableAntiInfestedBlock
extends EntityThrowable {
    public EntityThrowableAntiInfestedBlock(World worldIn) {
        super(worldIn);
    }

    public EntityThrowableAntiInfestedBlock(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityThrowableAntiInfestedBlock(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage((Entity)this, (Entity)this.getThrower()), 0.0f);
        }
        if (!this.worldObj.isRemote) {
            int i1 = MathHelper.floor_double((double)(this.posY + 0.1));
            double l1 = this.posX;
            double i2 = this.posZ;
            boolean flag = false;
            int offsetT = 5;
            int range = 7;
            for (int k2 = -1 * range; k2 <= 1 * range; ++k2) {
                for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                    for (int j = -1 * offsetT; j <= 1 * offsetT; ++j) {
                        double i3 = l1 + (double)k2;
                        double k = i1 + j;
                        double l = i2 + (double)l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        IBlockState iblockstate = BlockStates.get(this.worldObj, blockpos);
                        Block block = iblockstate.getBlock();
                        if (block == SRPBlocks.InfestedStain) {
                            BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.InfestedStain).withProperty((IProperty)BlockInfestedStain.STAGE, (Comparable)Integer.valueOf(5)));
                            this.worldObj.updateBlockTick(blockpos, BlockStates.defaultState(SRPBlocks.InfestedStain).withProperty((IProperty)BlockInfestedStain.STAGE, (Comparable)Integer.valueOf(5)).getBlock(), 40, 5);
                        }
                        if (block != SRPBlocks.InfestedRubble) continue;
                        BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.InfestedRubble).withProperty((IProperty)BlockInfestedRubble.STAGE, (Comparable)Integer.valueOf(5)));
                        this.worldObj.updateBlockTick(blockpos, BlockStates.defaultState(SRPBlocks.InfestedRubble).withProperty((IProperty)BlockInfestedRubble.STAGE, (Comparable)Integer.valueOf(5)).getBlock(), 40, 5);
                    }
                }
            }
            this.setDead();
        } else {
            double d0 = this.rand.nextGaussian() * 0.02;
            double d1 = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, new int[0]);
        }
    }
}

