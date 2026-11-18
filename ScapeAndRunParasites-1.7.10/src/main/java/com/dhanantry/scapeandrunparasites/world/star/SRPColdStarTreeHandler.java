/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.BlockTallGrass$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.event.terraingen.DecorateBiomeEvent$Decorate
 *  net.minecraftforge.event.terraingen.DecorateBiomeEvent$Decorate$EventType
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenDeadheadTreeStructure;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SRPColdStarTreeHandler {
    private static final float NORMAL_TREE_DENSITY = 0.5f;
    private static final int TREE_SNOW_RADIUS = 7;
    private static final int TREE_SNOW_VERTICAL_SEARCH = 8;
    private final WorldGenDeadheadTreeStructure normalTree = new WorldGenDeadheadTreeStructure(true, false, true);
    private final WorldGenDeadheadTreeStructure mushroomTree = new WorldGenDeadheadTreeStructure(true, true, false);

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onDecorateTree(DecorateBiomeEvent.Decorate event) {
        boolean mushroomTrees;
        if (event.getType() != DecorateBiomeEvent.Decorate.EventType.TREE) {
            return;
        }
        World world = event.getWorld();
        if (world == null || world.isRemote || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        SRPStarWorldData data = SRPStarWorldData.get(world);
        if (data == null || data.getStarType() != 1) {
            return;
        }
        event.setResult(Event.Result.DENY);
        Random rand = event.getRand();
        ChunkPos chunk = event.getChunkPos();
        BlockPos chunkOrigin = new BlockPos(chunk.chunkXPos * 16, 0, chunk.chunkZPos * 16);
        Biome biome = world.getBiome(chunkOrigin.add(8, 0, 8));
        int treeCount = biome.theBiomeDecorator.treesPerChunk;
        if (rand.nextFloat() < biome.theBiomeDecorator.extraTreeChance) {
            ++treeCount;
        }
        if (!(mushroomTrees = data.isMushroomTreesEnabled())) {
            treeCount = this.reduceNormalTreeDensity(treeCount, rand);
        }
        for (int i = 0; i < treeCount; ++i) {
            int sinkDepth;
            BlockPos generationPos;
            BlockPos treePos;
            int x = rand.nextInt(16) + 8;
            int z = rand.nextInt(16) + 8;
            BlockPos column = chunkOrigin.add(x, 0, z);
            if (mushroomTrees) {
                treePos = WorldAccess.getHeight(world, column);
                BlockPos generationPos2 = treePos.down();
                if (!this.mushroomTree.generate(world, rand, generationPos2)) continue;
                this.addSnowUnderTree(world, generationPos2);
                continue;
            }
            treePos = this.findNormalTreePosition(world, column);
            if (treePos == null || !this.normalTree.generate(world, rand, generationPos = treePos.down(sinkDepth = 1 + rand.nextInt(3)))) continue;
            this.addSnowUnderTree(world, treePos);
        }
    }

    private int reduceNormalTreeDensity(int originalCount, Random rand) {
        if (originalCount <= 0) {
            return 0;
        }
        float scaled = (float)originalCount * 0.5f;
        int whole = (int)scaled;
        float remainder = scaled - (float)whole;
        if (rand.nextFloat() < remainder) {
            ++whole;
        }
        return whole;
    }

    private void addSnowUnderTree(World world, BlockPos treeSurfacePos) {
        int centerX = treeSurfacePos.getX();
        int centerZ = treeSurfacePos.getZ();
        int expectedGroundY = treeSurfacePos.getY() - 1;
        for (int dx = -7; dx <= 7; ++dx) {
            for (int dz = -7; dz <= 7; ++dz) {
                BlockPos snowPos;
                BlockPos ground;
                if (dx * dx + dz * dz > 49 || (ground = this.findGroundForSnow(world, centerX + dx, centerZ + dz, expectedGroundY)) == null || this.replaceVegetationWithSnowGrass(world, snowPos = ground.up())) continue;
                IBlockState at = BlockStates.get(world, snowPos);
                if (at.getBlock() == Blocks.snow_layer) {
                    this.repairOrphanedDoublePlant(world, snowPos);
                    continue;
                }
                if (!WorldAccess.isAirBlock(world, snowPos) && !at.getBlock().isReplaceable((IBlockAccess)world, snowPos) || !Blocks.snow_layer.canPlaceBlockAt(world, snowPos)) continue;
                BlockStates.set(world, snowPos, BlockStates.defaultState(Blocks.snow_layer), 2);
            }
        }
    }

    private boolean replaceVegetationWithSnowGrass(World world, BlockPos pos) {
        BlockTallGrass.EnumType type;
        IBlockState state = BlockStates.get(world, pos);
        Block block = state.getBlock();
        if (block == Blocks.double_plant) {
            BlockPos lowerPos;
            BlockPos upperPos;
            if (state.getValue((IProperty)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
                upperPos = pos;
                lowerPos = pos.down();
            } else {
                lowerPos = pos;
                upperPos = pos.up();
            }
            IBlockState lowerState = BlockStates.get(world, lowerPos);
            IBlockState upperState = BlockStates.get(world, upperPos);
            if (lowerState.getBlock() == Blocks.double_plant || upperState.getBlock() == Blocks.double_plant) {
                BlockStates.set(world, upperPos, BlockStates.defaultState(Blocks.air), 2);
                BlockStates.set(world, lowerPos, BlockStates.defaultState(SRPBlocks.SnowTallGrass), 2);
                this.ensureSnowCoveredGround(world, lowerPos.down());
                return true;
            }
        }
        if (block == Blocks.tallgrass && ((type = (BlockTallGrass.EnumType)state.getValue((IProperty)BlockTallGrass.TYPE)) == BlockTallGrass.EnumType.GRASS || type == BlockTallGrass.EnumType.FERN)) {
            BlockStates.set(world, pos, BlockStates.defaultState(SRPBlocks.SnowShortGrass), 2);
            this.ensureSnowCoveredGround(world, pos.down());
            return true;
        }
        if (block == Blocks.yellow_flower || block == Blocks.red_flower) {
            BlockStates.set(world, pos, BlockStates.defaultState(SRPBlocks.SnowShortGrass), 2);
            this.ensureSnowCoveredGround(world, pos.down());
            return true;
        }
        return false;
    }

    private void repairOrphanedDoublePlant(World world, BlockPos snowPos) {
        BlockPos upperPos = snowPos.up();
        IBlockState upperState = BlockStates.get(world, upperPos);
        if (upperState.getBlock() != Blocks.double_plant) {
            return;
        }
        if (upperState.getValue((IProperty)BlockDoublePlant.HALF) != BlockDoublePlant.EnumBlockHalf.UPPER) {
            return;
        }
        BlockStates.set(world, upperPos, BlockStates.defaultState(Blocks.air), 2);
        BlockStates.set(world, snowPos, BlockStates.defaultState(SRPBlocks.SnowTallGrass), 2);
        this.ensureSnowCoveredGround(world, snowPos.down());
    }

    private void ensureSnowCoveredGround(World world, BlockPos groundPos) {
        IBlockState ground = BlockStates.get(world, groundPos);
        if (ground.getBlock() == Blocks.grass) {
            BlockStates.set(world, groundPos, BlockStates.defaultState(SRPBlocks.SnowCoveredGrass), 2);
        }
    }

    private BlockPos findGroundForSnow(World world, int x, int z, int expectedGroundY) {
        int maxY = Math.min(world.getActualHeight() - 2, expectedGroundY + 8);
        int minY = Math.max(1, expectedGroundY - 8);
        for (int y = maxY; y >= minY; --y) {
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = BlockStates.get(world, pos);
            if (WorldAccess.isAirBlock(world, pos) || state.getBlock().isReplaceable((IBlockAccess)world, pos) || this.isTreeOrLeaves(state, world, pos)) continue;
            if (this.isForbiddenSurface(state)) {
                return null;
            }
            if (state.getMaterial().isSolid()) {
                return pos;
            }
            return null;
        }
        return null;
    }

    private BlockPos findNormalTreePosition(World world, BlockPos column) {
        BlockPos top = WorldAccess.getHeight(world, column);
        if (top.getY() <= 1) {
            return null;
        }
        BlockPos cursor = top.down();
        IBlockState state = BlockStates.get(world, cursor);
        if (this.isTreeOrLeaves(state, world, cursor)) {
            return null;
        }
        if (this.isForbiddenSurface(state)) {
            return null;
        }
        for (int guard = 0; guard < 8 && cursor.getY() > 1 && this.isSurfaceDecoration(world, cursor, state); ++guard) {
            cursor = cursor.down();
            state = BlockStates.get(world, cursor);
        }
        if (this.isTreeOrLeaves(state, world, cursor)) {
            return null;
        }
        if (!this.isValidNormalGround(state)) {
            return null;
        }
        BlockPos treePos = cursor.up();
        IBlockState atTreePos = BlockStates.get(world, treePos);
        if (!WorldAccess.isAirBlock(world, treePos) && !atTreePos.getBlock().isReplaceable((IBlockAccess)world, treePos)) {
            return null;
        }
        return treePos;
    }

    private boolean isSurfaceDecoration(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.snow_layer) {
            return true;
        }
        if (state.getMaterial().isLiquid()) {
            return false;
        }
        return block.isReplaceable((IBlockAccess)world, pos);
    }

    private boolean isTreeOrLeaves(IBlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        return block == Blocks.log || block == Blocks.log2 || block == SRPBlocks.ParasiteTrunk || block.isLeaves(state, (IBlockAccess)world, pos);
    }

    private boolean isForbiddenSurface(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.ice || block == Blocks.packed_ice || block == Blocks.FROSTED_ICE || state.getMaterial().isLiquid();
    }

    private boolean isValidNormalGround(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == SRPBlocks.SnowCoveredGrass;
    }
}

