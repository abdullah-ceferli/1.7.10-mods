/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.InventoryHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Plane
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.TileEntityDermoidCyst;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyDirection;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.Mirror;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockDermoidCyst
extends BlockBase {
    public static final PropertyDirection FACING = PropertyDirection.create((String)"facing", (Predicate)EnumFacing.Plane.HORIZONTAL);

    public BlockDermoidCyst() {
        super(Material.wood, "dermoid_cyst", 2.5f, true, true);
        this.setStepSound(SRPSoundTypes.FLESH);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH));
    }

    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityDermoidCyst();
    }

    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)placer.getHorizontalFacing());
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (te instanceof TileEntityDermoidCyst && stack.hasDisplayName()) {
            ((TileEntityDermoidCyst)te).setCustomName(stack.getDisplayName());
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (te instanceof TileEntityDermoidCyst) {
            player.displayGUIChest((IInventory)((TileEntityDermoidCyst)te));
        }
        return true;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te;
        if (!world.isRemote) {
            world.playSound(null, pos, SRPSounds.FLESH_GROWL, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        if ((te = WorldAccess.getTileEntity(world, pos)) instanceof TileEntityDermoidCyst) {
            InventoryHelper.dropInventoryItems((World)world, (BlockPos)pos, (IInventory)((TileEntityDermoidCyst)te));
            world.updateComparatorOutputLevel(pos, (Block)this);
        }
        super.breakBlock(world, pos, state);
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (te instanceof TileEntityDermoidCyst) {
            return Container.calcRedstoneFromInventory((IInventory)((TileEntityDermoidCyst)te));
        }
        return 0;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty((IProperty)FACING, (Comparable)rot.rotate((EnumFacing)state.getValue((IProperty)FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation((EnumFacing)state.getValue((IProperty)FACING)));
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)FACING)).getHorizontalIndex();
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)EnumFacing.byHorizontalIndex((int)(meta & 3)));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING});
    }

    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        return te != null && te.receiveClientEvent(id, param);
    }
}

