/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.BlockTallGrass$EnumType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.event.terraingen.DecorateBiomeEvent$Post
 *  net.minecraftforge.event.terraingen.PopulateChunkEvent$Post
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SRPStarWorldTweaks {
    private static final int VANILLA_SEA_LEVEL = 63;
    private static final int WARM_STAR_SEA_LEVEL = 48;

    @SubscribeEvent
    public void onPopulatePost(PopulateChunkEvent.Post event) {
        World world = event.getWorld();
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        int starType = SRPStarWorldData.get(world).getStarType();
        if (starType == 2) {
            this.dryWarmStarChunk(world, event.getChunkX(), event.getChunkZ());
        }
    }

    @SubscribeEvent
    public void onDecoratePost(DecorateBiomeEvent.Post event) {
        World world = event.getWorld();
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        int starType = SRPStarWorldData.get(world).getStarType();
        if (starType != 1) {
            return;
        }
        int chunkX = event.getChunkPos().chunkXPos;
        int chunkZ = event.getChunkPos().chunkZPos;
        this.snowColdStarGrass(world, chunkX, chunkZ);
    }

    private void dryWarmStarChunk(World world, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 63; y > 48; --y) {
                    pos.setPos(startX + x, y, startZ + z);
                    IBlockState state = BlockStates.get(world, (BlockPos)pos);
                    if (state.getMaterial() != Material.water && state.getBlock() != Blocks.ice) continue;
                    BlockStates.set(world, (BlockPos)pos, BlockStates.defaultState(Blocks.air), 2);
                }
            }
        }
    }

    private void snowColdStarGrass(World world, int chunkX, int chunkZ) {
        int startX = (chunkX << 4) + 8;
        int startZ = (chunkZ << 4) + 8;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int worldX = startX + x;
                int worldZ = startZ + z;
                this.findAndConvertExposedGrass(world, worldX, worldZ);
            }
        }
    }

    private void findAndConvertExposedGrass(World world, int x, int z) {
        for (int y = world.getActualHeight() - 1; y > 0; --y) {
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = BlockStates.get(world, pos);
            if (state.getBlock() == Blocks.air) continue;
            if (this.isShortVegetation(state)) {
                this.convertShortGrass(world, pos);
                return;
            }
            if (this.isTallGrassUpper(state)) {
                this.convertTallGrassFromUpper(world, pos);
                return;
            }
            if (this.isTallGrassLower(state)) {
                this.convertTallGrassFromLower(world, pos);
                return;
            }
            return;
        }
    }

    private void convertShortGrass(World world, BlockPos pos) {
        if (!this.canBecomeSnowyGrass(world, pos, false)) {
            return;
        }
        IBlockState below = BlockStates.get(world, pos.down());
        if (below.getBlock() != Blocks.grass && below.getBlock() != Blocks.dirt && below.getBlock() != SRPBlocks.SnowCoveredGrass) {
            return;
        }
        BlockStates.set(world, pos, BlockStates.defaultState(SRPBlocks.SnowShortGrass), 2);
        this.ensureSnowCoveredGround(world, pos.down());
    }

    private void convertTallGrassFromUpper(World world, BlockPos upperPos) {
        BlockPos lowerPos = upperPos.down();
        IBlockState lowerState = BlockStates.get(world, lowerPos);
        if (!this.isTallGrassLower(lowerState)) {
            return;
        }
        this.convertTallGrass(world, lowerPos, upperPos);
    }

    private void convertTallGrassFromLower(World world, BlockPos lowerPos) {
        BlockPos upperPos = lowerPos.up();
        IBlockState upperState = BlockStates.get(world, upperPos);
        if (upperState.getBlock() != Blocks.double_plant) {
            return;
        }
        if (upperState.getValue((IProperty)BlockDoublePlant.HALF) != BlockDoublePlant.EnumBlockHalf.UPPER) {
            return;
        }
        this.convertTallGrass(world, lowerPos, upperPos);
    }

    private void convertTallGrass(World world, BlockPos lowerPos, BlockPos upperPos) {
        if (!this.canBecomeSnowyGrass(world, lowerPos, true)) {
            return;
        }
        IBlockState ground = BlockStates.get(world, lowerPos.down());
        if (ground.getBlock() != Blocks.grass && ground.getBlock() != Blocks.dirt && ground.getBlock() != SRPBlocks.SnowCoveredGrass) {
            return;
        }
        BlockStates.set(world, upperPos, BlockStates.defaultState(Blocks.air), 2);
        BlockStates.set(world, lowerPos, BlockStates.defaultState(SRPBlocks.SnowTallGrass), 2);
        this.ensureSnowCoveredGround(world, lowerPos.down());
    }

    private void ensureSnowCoveredGround(World world, BlockPos groundPos) {
        IBlockState ground = BlockStates.get(world, groundPos);
        if (ground.getBlock() == Blocks.grass) {
            BlockStates.set(world, groundPos, BlockStates.defaultState(SRPBlocks.SnowCoveredGrass), 2);
        }
    }

    private boolean canBecomeSnowyGrass(World world, BlockPos pos, boolean tall) {
        BlockPos skyPos;
        BlockPos blockPos = skyPos = tall ? pos.up(2) : pos.up();
        if (!WorldAccess.canSeeSky(world, skyPos)) {
            return false;
        }
        return world.getBiome(pos).getTemperature(pos) <= 0.15f;
    }

    private boolean isShortVegetation(IBlockState state) {
        if (state.getBlock() == Blocks.yellow_flower || state.getBlock() == Blocks.red_flower) {
            return true;
        }
        if (state.getBlock() != Blocks.tallgrass) {
            return false;
        }
        BlockTallGrass.EnumType type = (BlockTallGrass.EnumType)state.getValue((IProperty)BlockTallGrass.TYPE);
        return type == BlockTallGrass.EnumType.GRASS || type == BlockTallGrass.EnumType.FERN;
    }

    private boolean isTallGrassUpper(IBlockState state) {
        return state.getBlock() == Blocks.double_plant && state.getValue((IProperty)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
    }

    private boolean isTallGrassLower(IBlockState state) {
        if (state.getBlock() != Blocks.double_plant) {
            return false;
        }
        return state.getValue((IProperty)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER;
    }
}

