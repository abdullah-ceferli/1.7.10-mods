/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAlveoliGrowth
extends BlockBase {
    private static final AxisAlignedBB SHAPE = Boxes.create(0.2, 0.8, 0.2, 0.8, 1.0, 0.8);

    public BlockAlveoliGrowth() {
        super(Material.plants, "alveoli_growth", 0.0f, true, true);
        this.setLightOpacity(0);
        this.setStepSound(SRPSoundTypes.FLESH);
    }

    private boolean isSRPBlock(World w, BlockPos p) {
        Block b = BlockStates.get(w, p).getBlock();
        ResourceLocation rl = RegistryNames.get(b);
        return rl != null && "srparasites".equals(rl.getResourceDomain());
    }

    public boolean canPlaceBlockAt(World w, BlockPos pos) {
        return this.isSRPBlock(w, pos.up());
    }

    public boolean canPlaceBlockOnSide(World w, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN && this.isSRPBlock(w, pos.up());
    }

    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.isSRPBlock(w, pos.up())) {
            WorldAccess.destroyBlock(w, pos, true);
        }
    }

    public boolean isOpaqueCube(IBlockState s) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState s) {
        return false;
    }

    public boolean isFullBlock(IBlockState s) {
        return false;
    }

    public boolean isNormalCube(IBlockState s, IBlockAccess w, BlockPos p) {
        return false;
    }

    public boolean isTopSolid(IBlockState s) {
        return false;
    }

    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return true;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return SHAPE;
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(value=Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return Boxes.offset(SHAPE, pos);
    }

    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
}

