/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.gen.feature.WorldGenAbstractTree
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public abstract class WorldGenParasiteTreeAbstract
extends WorldGenAbstractTree {
    public WorldGenParasiteTreeAbstract(boolean notify) {
        super(notify);
    }

    protected boolean canGrowInto(Block blockType) {
        Material material = BlockStates.defaultState(blockType).getMaterial();
        return material == Material.air || material == Material.leaves || blockType == Blocks.grass || blockType == Blocks.dirt || blockType == Blocks.log || blockType == Blocks.log2 || blockType == Blocks.sapling || blockType == Blocks.vine || blockType == SRPBlocks.ParasiteBush;
    }

    protected void setDirtAt(World worldIn, BlockPos pos) {
        if (BlockStates.get(worldIn, pos).getBlock() != Blocks.dirt) {
            this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteStain));
        }
    }

    public boolean isReplaceable(World world, BlockPos pos) {
        IBlockState state = BlockStates.get(world, pos);
        return state.getBlock().isAir(state, (IBlockAccess)world, pos) || state.getBlock().isLeaves(state, (IBlockAccess)world, pos) || state.getBlock().isWood((IBlockAccess)world, pos) || this.canGrowInto(state.getBlock()) || state.getBlock() == SRPBlocks.ParasiteBush;
    }
}

