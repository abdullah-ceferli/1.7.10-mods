/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockFallingBase;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.IStagedBlock;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFallingInfestedStain
extends BlockFallingBase
implements IStagedBlock {
    public static final PropertyInteger STAGE = PropertyInteger.create((String)"stage", (int)0, (int)5);
    public static final PropertyBool SNOWY = PropertyBool.create((String)"snowy");

    @Override
    public PropertyInteger getStageProperty() {
        return STAGE;
    }

    public BlockFallingInfestedStain(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
        super(material, name, hardness, creative, tickRandom);
        this.setHarvestLevel("shovel", 0);
        this.setStepSound(new com.dhanantry.scapeandrunparasites.backport.sound.SoundType(1.0f, 0.5f, SRPSounds.BLOCKINFEST_BREAK, SRPSounds.BLOCKINFEST_STEP, SRPSounds.BLOCKINFEST_PLACE, SRPSounds.BLOCKINFEST_HIT, SoundEvents.BLOCK_SLIME_FALL));
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(0)).withProperty((IProperty)SNOWY, (Comparable)Boolean.FALSE));
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        boolean flag = super.removedByPlayer(state, world, pos, player, willHarvest);
        if (world.isRemote) {
            return flag;
        }
        if (SRPConfigSystems.useEvolution && BlockStates.meta(this, state) > 0) {
            SRPSaveData data = SRPSaveData.get(world, 9);
            data.setTotalKills(world.provider.dimensionId, -SRPConfigSystems.valueLossBlockStain, true, world, false, 33);
        }
        return flag;
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
        if (rand.nextDouble() < 0.5) {
            if (worldIn.getBiome(pos) instanceof BiomeParasiteBase) {
                BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteStain));
            } else {
                int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
                if (heart > 0) {
                    BlockParasiteSpreading.SpreadBiome(worldIn, pos, heart, ParasiteEventWorld.canBiomeStillExistType(worldIn, pos, true));
                }
            }
        } else if (rand.nextDouble() <= 0.05 && !worldIn.isRemote) {
            BlockParasiteSpreading.canInfestBlock(worldIn, pos, rand, BlockStates.meta(this, state), false);
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
        super.updateTick(worldIn, pos, state, rand);
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{STAGE, SNOWY});
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState above = BlockStates.get(worldIn, pos.up());
        boolean snowy = above.getBlock() == Blocks.snow || above.getBlock() == Blocks.snow_layer;
        return state.withProperty((IProperty)SNOWY, (Comparable)Boolean.valueOf(snowy));
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)STAGE);
    }

    public IBlockState getStateFromMeta(int meta) {
        if (meta >= 5) {
            meta = 5;
        }
        return BlockStates.defaultState(this).withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(meta & 7));
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (BlockStates.get(worldIn, pos.up()).getBlock() != Blocks.air && BlockStates.get(worldIn, pos.up()).getBlock() != SRPBlocks.ParasiteBush) {
            return;
        }
        int stage = BlockStates.meta(this, stateIn);
        if (stage == 2) {
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleS) {
                double d0 = (double)pos.getX() + rand.nextDouble();
                double d1 = (double)pos.getY() + 2.5;
                double d2 = (double)pos.getZ() + rand.nextDouble();
                ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
            }
        } else if (stage == 3) {
            double d2;
            double d1;
            double d0;
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleF) {
                d0 = (double)pos.getX() + rand.nextDouble();
                d1 = (double)pos.getY() + 1.5;
                d2 = (double)pos.getZ() + rand.nextDouble();
                int g = 200;
                if (rand.nextInt(3) == 0) {
                    g += 50;
                }
                ParticleSpawner.spawnParticle(SRPEnumParticle.FOG, d0, d1, d2, 0.0, 1.0E-4, 0.0, 0, g, 0);
            }
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleS) {
                d0 = (double)pos.getX() + rand.nextDouble();
                d1 = (double)pos.getY() + 2.5;
                d2 = (double)pos.getZ() + rand.nextDouble();
                ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
            }
        }
    }
}

