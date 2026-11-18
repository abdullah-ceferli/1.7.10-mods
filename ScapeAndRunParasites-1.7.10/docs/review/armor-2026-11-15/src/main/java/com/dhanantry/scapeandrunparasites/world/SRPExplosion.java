/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.EnchantmentProtection
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SRPExplosion
extends Explosion {
    private boolean causesFire;
    private boolean damagesTerrain;
    private Random random = new Random();
    private World world;
    private double x;
    private double y;
    private double z;
    private Entity exploder;
    private float size;
    private List<BlockPos> affectedBlockPositions = Lists.newArrayList();
    private Vec3d position;
    EntityParasiteBase shooterTwo;

    @SideOnly(value=Side.CLIENT)
    public SRPExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
        this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
    }

    @SideOnly(value=Side.CLIENT)
    public SRPExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean causesFire, boolean damagesTerrain, List<BlockPos> affectedPositions) {
        this(worldIn, entityIn, x, y, z, size, causesFire, damagesTerrain);
        this.affectedBlockPositions.addAll(affectedPositions);
    }

    public SRPExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain) {
        super(worldIn, entityIn, x, y, z, size, flaming, true);
        this.world = worldIn;
        this.exploder = entityIn;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.causesFire = flaming;
        this.damagesTerrain = damagesTerrain;
        this.position = new Vec3d(this.x, this.y, this.z);
        if (entityIn instanceof EntityParasiteBase) {
            this.shooterTwo = (EntityParasiteBase)entityIn;
        }
    }

    public void doExplosionA() {
        if (this.world.isRemote) {
            return;
        }
        HashSet set = Sets.newHashSet();
        int i = 16;
        for (int j = 0; j < i; ++j) {
            for (int k = 0; k < i; ++k) {
                for (int l = 0; l < i; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    double d0 = (float)j / 15.0f * 2.0f - 1.0f;
                    double d1 = (float)k / 15.0f * 2.0f - 1.0f;
                    double d2 = (float)l / 15.0f * 2.0f - 1.0f;
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 /= d3;
                    d1 /= d3;
                    d2 /= d3;
                    double d4 = this.x;
                    double d6 = this.y;
                    double d8 = this.z;
                    float f1 = 0.3f;
                    for (float f = this.size * (0.7f + this.world.rand.nextFloat() * 0.6f); f > 0.0f; f -= 0.22500001f) {
                        BlockPos blockpos = new BlockPos(d4, d6, d8);
                        IBlockState iblockstate = BlockStates.get(this.world, blockpos);
                        if (iblockstate.getMaterial() != Material.air) {
                            float f2 = this.exploder != null ? this.exploder.getExplosionResistance((Explosion)this, this.world, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(this.world, blockpos, null, (Explosion)this);
                            f -= (f2 + 0.3f) * 0.3f;
                        }
                        if (f > 0.0f && (this.exploder == null || this.exploder.canExplosionDestroyBlock((Explosion)this, this.world, blockpos, iblockstate, f))) {
                            set.add(blockpos);
                        }
                        d4 += d0 * (double)0.3f;
                        d6 += d1 * (double)0.3f;
                        d8 += d2 * (double)0.3f;
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(set);
        float f3 = this.size * 2.0f;
        double df1 = (double)f3 - 1.0;
        double df2 = (double)f3 + 1.0;
        int k1 = MathHelper.floor_double((double)(this.x - df1));
        int l1 = MathHelper.floor_double((double)(this.x + df2));
        int i2 = MathHelper.floor_double((double)(this.y - df1));
        int i1 = MathHelper.floor_double((double)(this.y + df2));
        int j2 = MathHelper.floor_double((double)(this.z - df1));
        int j1 = MathHelper.floor_double((double)(this.z + df2));
        List list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, Boxes.create((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        ForgeEventFactory.onExplosionDetonate((World)this.world, (Explosion)this, (List)list, (double)f3);
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
        for (Entity entity : list) {
            EntityPlayer entityplayer;
            double d9;
            double d7;
            double d5;
            double d13;
            double d12;
            if (entity instanceof EntityParasiteBase && SRPConfig.explotionDamPara || entity.isImmuneToExplosions() || !((d12 = entity.getDistance(this.x, this.y, this.z) / (double)f3) <= 1.0) || (d13 = (double)MathHelper.sqrt_double((double)((d5 = entity.posX - this.x) * d5 + (d7 = entity.posY + (double)entity.getEyeHeight() - this.y) * d7 + (d9 = entity.posZ - this.z) * d9))) == 0.0) continue;
            d5 /= d13;
            d7 /= d13;
            d9 /= d13;
            double d14 = WorldAccess.getBlockDensity(this.world, vec3d, Boxes.entityBox(entity));
            double d10 = (1.0 - d12) * d14;
            entity.attackEntityFrom(DamageSource.setExplosionSource((Explosion)this), (float)((int)((d10 * d10 + d10) / 2.0 * 7.0 * (double)f3 + 1.0)));
            double d11 = d10;
            if (entity instanceof EntityLivingBase) {
                d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)((EntityLivingBase)entity), (double)d10);
            }
            entity.motionX += d5 * d11;
            entity.motionY += d7 * d11;
            entity.motionZ += d9 * d11;
            if (!(entity instanceof EntityPlayer) || (entityplayer = (EntityPlayer)entity).isSpectator() || entityplayer.isCreative() && entityplayer.capabilities.isFlying) continue;
            this.getPlayerKnockbackMap().put(entityplayer, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
        }
    }

    public void doExplosionB(boolean spawnParticles) {
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2f) * 0.7f);
        if (this.size >= 2.0f && this.damagesTerrain) {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.x, this.y, this.z, 1.0, 0.0, 0.0, new int[0]);
        } else {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.x, this.y, this.z, 1.0, 0.0, 0.0, new int[0]);
        }
        if (this.damagesTerrain) {
            for (BlockPos blockpos : this.affectedBlockPositions) {
                IBlockState iblockstate = BlockStates.get(this.world, blockpos);
                Block block = iblockstate.getBlock();
                if (spawnParticles) {
                    double d0 = (float)blockpos.getX() + this.world.rand.nextFloat();
                    double d1 = (float)blockpos.getY() + this.world.rand.nextFloat();
                    double d2 = (float)blockpos.getZ() + this.world.rand.nextFloat();
                    double d3 = d0 - this.x;
                    double d4 = d1 - this.y;
                    double d5 = d2 - this.z;
                    double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5 / (d6 / (double)this.size + 0.1);
                    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.x) / 2.0, (d1 + this.y) / 2.0, (d2 + this.z) / 2.0, d3 *= (d7 *= (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3f)), d4 *= d7, d5 *= d7, new int[0]);
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
                }
                if (iblockstate.getMaterial() == Material.air) continue;
                if (block.canDropFromExplosion((Explosion)this) && this.shooterTwo != null) {
                    this.shooterTwo.addToBlockInv(RegistryNames.get(block).toString() + ";" + BlockStates.meta(block, iblockstate));
                }
                block.onBlockExploded(this.world, blockpos, (Explosion)this);
            }
        }
        if (this.causesFire) {
            for (BlockPos blockpos1 : this.affectedBlockPositions) {
                if (BlockStates.get(this.world, blockpos1).getMaterial() != Material.air || !BlockStates.get(this.world, blockpos1.down()).isFullBlock() || this.random.nextInt(3) != 0) continue;
                BlockStates.set(this.world, blockpos1, BlockStates.defaultState(Blocks.fire));
            }
        }
    }
}

