/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSnowCoveredGrass
extends BlockGrass {
    public BlockSnowCoveredGrass(String name) {
        RegistryNames.set(this, "srparasites", name);
        this.setBlockName("srparasites." + name);
        this.setHardness(0.6f);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty((IProperty)SNOWY, (Comparable)Boolean.valueOf(true));
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack((Block)Blocks.grass);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock((Block)Blocks.dirt);
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack((Block)Blocks.grass);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote && !this.hasSnowGrassAbove((IBlockAccess)world, pos)) {
            BlockStates.set(world, pos, BlockStates.defaultState(Blocks.grass), 3);
        }
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!this.hasSnowGrassAbove((IBlockAccess)world, pos)) {
            BlockStates.set(world, pos, BlockStates.defaultState(Blocks.grass), 3);
            return;
        }
        super.updateTick(world, pos, state, random);
    }

    private boolean hasSnowGrassAbove(IBlockAccess world, BlockPos pos) {
        Block above = BlockStates.get(world, pos.up()).getBlock();
        return above == SRPBlocks.SnowTallGrass || above == SRPBlocks.SnowShortGrass;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.SOLID;
    }
}

