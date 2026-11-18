/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockSandStone
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IPlantable
 */
package com.dhanantry.scapeandrunparasites.util.convert;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import java.util.Collection;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public final class HarlequinBlockConverter {
    public static Block convert(World world, BlockPos pos, IBlockState state, boolean blotchHere, Random rand, Config cfg) {
        Block b = state.getBlock();
        Material mat = state.getMaterial();
        if (mat == Material.air || mat == Material.water || mat == Material.lava) {
            return null;
        }
        if (b instanceof BlockLeaves) {
            BlockPos below;
            BlockStates.set(world, pos, BlockStates.defaultState(cfg.ALVEOLI), 2);
            if (rand.nextInt(100) < 30 && WorldAccess.isAirBlock(world, below = pos.down())) {
                IBlockState growth = HarlequinBlockConverter.setFacingIfPresent(BlockStates.defaultState(cfg.ALVEOLI_GROWTH), EnumFacing.DOWN);
                BlockStates.set(world, below, growth, 2);
            }
            return cfg.ALVEOLI;
        }
        if (b == Blocks.planks || b == Blocks.cobblestone || b == Blocks.mossy_cobblestone) {
            BlockStates.set(world, pos, BlockStates.defaultState(cfg.HARLESKINN), 2);
            return cfg.HARLESKINN;
        }
        if (b == Blocks.grass) {
            if (blotchHere) {
                BlockStates.set(world, pos, BlockStates.defaultState(cfg.HARLESKINN), 2);
                BlockPos up = pos.up();
                Block above = BlockStates.get(world, up).getBlock();
                if (above == cfg.TRESSES || above == cfg.HIRSUTE) {
                    WorldAccess.setBlockToAir(world, up);
                }
                return cfg.HARLESKINN;
            }
            BlockStates.set(world, pos, BlockStates.defaultState(cfg.HARLEQUINN_GRASS), 2);
            HarlequinBlockConverter.maybePlaceHair(world, pos, rand, cfg);
            return cfg.HARLEQUINN_GRASS;
        }
        if (HarlequinBlockConverter.isTopSunlit(world, pos) && (HarlequinBlockConverter.isAnySand(state) || HarlequinBlockConverter.isAnySandstone(state) || b == Blocks.stone)) {
            if (blotchHere) {
                BlockStates.set(world, pos, BlockStates.defaultState(cfg.HARLESKINN), 2);
                return cfg.HARLESKINN;
            }
            BlockStates.set(world, pos, BlockStates.defaultState(cfg.HARLEQUINN_GRASS), 2);
            HarlequinBlockConverter.maybePlaceHair(world, pos, rand, cfg);
            return cfg.HARLEQUINN_GRASS;
        }
        if (HarlequinBlockConverter.isAnySand(state) || HarlequinBlockConverter.isAnySandstone(state) || b == Blocks.stone) {
            BlockStates.set(world, pos, BlockStates.defaultState(cfg.HARLESKINN), 2);
            return cfg.HARLESKINN;
        }
        return null;
    }

    private static void maybePlaceHair(World world, BlockPos pos, Random rand, Config cfg) {
        BlockPos up = pos.up();
        if (WorldAccess.isAirBlock(world, up) && HarlequinBlockConverter.isTopSunlit(world, pos)) {
            int roll = rand.nextInt(100);
            boolean canTresses = HarlequinBlockConverter.canSustain(world, BlockStates.defaultState(cfg.HARLEQUINN_GRASS), pos, cfg.TRESSES);
            boolean canHirsute = HarlequinBlockConverter.canSustain(world, BlockStates.defaultState(cfg.HARLEQUINN_GRASS), pos, cfg.HIRSUTE);
            if (roll < 60 && canTresses && WorldAccess.isAirBlock(world, up.up())) {
                HarlequinBlockConverter.placeDoublePlant(world, up, cfg.TRESSES);
            } else if (roll < 80 && canHirsute) {
                BlockStates.set(world, up, BlockStates.defaultState(cfg.HIRSUTE), 2);
            }
        }
    }

    private static boolean isTopSunlit(World world, BlockPos pos) {
        return WorldAccess.canSeeSky(world, pos.up());
    }

    private static boolean isAnySand(IBlockState s) {
        return s.getBlock() instanceof BlockSand;
    }

    private static boolean isAnySandstone(IBlockState s) {
        return s.getBlock() instanceof BlockSandStone || s.getBlock() == Blocks.RED_SANDSTONE;
    }

    private static boolean canSustain(World world, IBlockState ground, BlockPos pos, Block plant) {
        if (!(plant instanceof IPlantable)) {
            return true;
        }
        try {
            return ground.getBlock().canSustainPlant(ground, (IBlockAccess)world, pos, EnumFacing.UP, (IPlantable)plant);
        }
        catch (Throwable t) {
            return true;
        }
    }

    private static IBlockState setFacingIfPresent(IBlockState state, EnumFacing face) {
        Collection props = state.getPropertyKeys();
        for (IProperty p : props) {
            IProperty pf;
            if (!p.getName().equalsIgnoreCase("facing") || p.getValueClass() != EnumFacing.class || !(pf = p).getAllowedValues().contains(face)) continue;
            return state.withProperty(pf, (Comparable)face);
        }
        return state;
    }

    private static void placeDoublePlant(World world, BlockPos pos, Block plant) {
        try {
            if (plant instanceof BlockDoublePlant) {
                BlockDoublePlant bdp = (BlockDoublePlant)plant;
                BlockStates.set(world, pos, BlockStates.defaultState(bdp).withProperty((IProperty)BlockDoublePlant.HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.LOWER), 2);
                BlockStates.set(world, pos.up(), BlockStates.defaultState(bdp).withProperty((IProperty)BlockDoublePlant.HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
            } else {
                BlockStates.set(world, pos, BlockStates.defaultState(plant), 2);
            }
        }
        catch (Throwable ignored) {
            BlockStates.set(world, pos, BlockStates.defaultState(plant), 2);
        }
    }

    private HarlequinBlockConverter() {
    }

    public static final class Config {
        public final Block HARLESKINN;
        public final Block HARLEQUINN_GRASS;
        public final Block ALVEOLI;
        public final Block ALVEOLI_GROWTH;
        public final Block LIPOMA;
        public final Block TRESSES;
        public final Block HIRSUTE;

        public Config(Block harleskinn, Block harlequinnGrass, Block alveoli, Block alveoliGrowth, Block lipoma, Block tresses, Block hirsute) {
            this.HARLESKINN = harleskinn;
            this.HARLEQUINN_GRASS = harlequinnGrass;
            this.ALVEOLI = alveoli;
            this.ALVEOLI_GROWTH = alveoliGrowth;
            this.LIPOMA = lipoma;
            this.TRESSES = tresses;
            this.HIRSUTE = hirsute;
        }
    }
}

