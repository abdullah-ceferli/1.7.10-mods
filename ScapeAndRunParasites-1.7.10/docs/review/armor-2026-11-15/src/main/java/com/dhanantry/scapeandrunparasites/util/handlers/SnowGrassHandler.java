/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.BlockDoublePlant$EnumPlantType
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.BlockTallGrass$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.BlockEvent$PlaceEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class SnowGrassHandler {
    private static final int NATURAL_CHECK_RADIUS = 32;
    private static final int NATURAL_CHECKS_PER_PLAYER = 96;

    @SubscribeEvent
    public static void onSnowPlaced(BlockEvent.PlaceEvent event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        if (event.getPlacedBlock().getBlock() != Blocks.snow_layer) {
            return;
        }
        BlockPos pos = event.getPos();
        IBlockState replaced = event.getBlockSnapshot().getReplacedBlock();
        if (SnowGrassHandler.isShortGrass(replaced)) {
            BlockStates.set(world, pos, BlockStates.defaultState(SRPBlocks.SnowShortGrass), 3);
            return;
        }
        if (SnowGrassHandler.isTallGrass(replaced)) {
            SnowGrassHandler.convertTallGrass(world, pos, replaced);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        World world = event.world;
        if (world.isRemote) {
            return;
        }
        if (!world.isRaining()) {
            return;
        }
        if (world.getTotalWorldTime() % 10L != 0L) {
            return;
        }
        for (EntityPlayer player : EntityQueries.players(world)) {
            SnowGrassHandler.checkNaturalSnowAroundPlayer(world, player);
        }
    }

    private static void checkNaturalSnowAroundPlayer(World world, EntityPlayer player) {
        int centerX = (int)Math.floor(player.posX);
        int centerZ = (int)Math.floor(player.posZ);
        for (int i = 0; i < 96; ++i) {
            IBlockState state;
            BlockPos pos;
            int z;
            int x = centerX + world.rand.nextInt(65) - 32;
            BlockPos column = new BlockPos(x, 0, z = centerZ + world.rand.nextInt(65) - 32);
            if (!WorldAccess.isBlockLoaded(world, column) || !WorldAccess.isBlockLoaded(world, pos = WorldAccess.getTopSolidOrLiquidBlock(world, column)) || !SnowGrassHandler.isShortGrass(state = BlockStates.get(world, pos)) && !SnowGrassHandler.isTallGrass(state) || !SnowGrassHandler.canSnowHere(world, pos)) continue;
            if (SnowGrassHandler.isShortGrass(state)) {
                BlockStates.set(world, pos, BlockStates.defaultState(SRPBlocks.SnowShortGrass), 3);
                continue;
            }
            SnowGrassHandler.convertTallGrass(world, pos, state);
        }
    }

    private static boolean canSnowHere(World world, BlockPos pos) {
        if (!WorldAccess.canSeeSky(world, pos.up())) {
            return false;
        }
        if (world.getBiome(pos).getTemperature(pos) > 0.15f) {
            return false;
        }
        if (world.getLightFor(EnumSkyBlock.BLOCK, pos) >= 10) {
            return false;
        }
        return world.provider.canDoRainSnowIce(WorldAccess.getChunk(world, pos));
    }

    private static boolean isShortGrass(IBlockState state) {
        return state.getBlock() == Blocks.tallgrass && state.getValue((IProperty)BlockTallGrass.TYPE) == BlockTallGrass.EnumType.GRASS;
    }

    private static boolean isTallGrass(IBlockState state) {
        if (state.getBlock() != Blocks.double_plant) {
            return false;
        }
        if (state.getValue((IProperty)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            return true;
        }
        return state.getValue((IProperty)BlockDoublePlant.VARIANT) == BlockDoublePlant.EnumPlantType.GRASS;
    }

    private static void convertTallGrass(World world, BlockPos pos, IBlockState state) {
        BlockPos lowerPos = pos;
        IBlockState lowerState = state;
        if (state.getValue((IProperty)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            lowerPos = pos.down();
            lowerState = BlockStates.get(world, lowerPos);
        }
        if (lowerState.getBlock() != Blocks.double_plant) {
            return;
        }
        if (lowerState.getValue((IProperty)BlockDoublePlant.HALF) != BlockDoublePlant.EnumBlockHalf.LOWER) {
            return;
        }
        if (lowerState.getValue((IProperty)BlockDoublePlant.VARIANT) != BlockDoublePlant.EnumPlantType.GRASS) {
            return;
        }
        WorldAccess.setBlockToAir(world, lowerPos.up());
        BlockStates.set(world, lowerPos, BlockStates.defaultState(SRPBlocks.SnowTallGrass), 3);
    }
}

