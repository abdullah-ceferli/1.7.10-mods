/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.InventoryHelper
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfestedFurnace;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInfestedFurnace
extends BlockContainer {
    public static final PropertyBool LIT = PropertyBool.create((String)"lit");
    public static boolean DEBUG = true;
    public static boolean keepInventory = false;

    public BlockInfestedFurnace() {
        super(Material.rock);
        RegistryNames.set(this, new ResourceLocation("srparasites", "infested_furnace"));
        this.setBlockName("srparasites.infested_furnace");
        this.setHardness(3.5f);
        this.setStepSound(SoundType.STONE);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)BlockHorizontal.FACING, (Comparable)EnumFacing.NORTH).withProperty((IProperty)LIT, (Comparable)Boolean.FALSE));
    }

    public static void setLitState(boolean active, World world, BlockPos pos) {
        IBlockState state = BlockStates.get(world, pos);
        if (!(state.getBlock() instanceof BlockInfestedFurnace)) {
            return;
        }
        boolean currentlyLit = (Boolean)state.getValue((IProperty)LIT);
        if (currentlyLit == active) {
            return;
        }
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        keepInventory = true;
        if (DEBUG) {
            System.out.println("[InfestedFurnace] setLitState pos=" + pos + " " + currentlyLit + " -> " + active + " te=" + (te == null ? "null" : te.getClass().getName()));
        }
        BlockStates.set(world, pos, state.withProperty((IProperty)LIT, (Comparable)Boolean.valueOf(active)), 3);
        if (te != null) {
            te.validate();
            WorldAccess.setTileEntity(world, pos, te);
        }
        keepInventory = false;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityInfestedFurnace();
    }

    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return (Boolean)state.getValue((IProperty)LIT) != false ? 14 : 0;
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (DEBUG) {
            System.out.println("[InfestedFurnace] onBlockActivated side=" + (worldIn.isRemote ? "CLIENT" : "SERVER") + " pos=" + pos + " hand=" + hand + " sneaking=" + playerIn.isSneaking() + " held=" + playerIn.getHeldItem(hand));
        }
        if (worldIn.isRemote) {
            return true;
        }
        TileEntity te = WorldAccess.getTileEntity(worldIn, pos);
        if (DEBUG) {
            System.out.println("[InfestedFurnace] TE=" + (te == null ? "null" : te.getClass().getName()));
        }
        if (!(te instanceof TileEntityInfestedFurnace)) {
            if (DEBUG) {
                System.out.println("[InfestedFurnace] Not our TE, aborting open.");
            }
            return true;
        }
        if (DEBUG) {
            System.out.println("[InfestedFurnace] Opening GUI via displayGUIChest");
        }
        playerIn.displayGUIChest((IInventory)te);
        return true;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te;
        if (!keepInventory && (te = WorldAccess.getTileEntity(worldIn, pos)) instanceof IInventory) {
            InventoryHelper.dropInventoryItems((World)worldIn, (BlockPos)pos, (IInventory)((IInventory)te));
            worldIn.updateComparatorOutputLevel(pos, (Block)this);
        }
        WorldAccess.removeTileEntity(worldIn, pos);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock((Block)this);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack((Block)this);
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack((Block)this);
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return BlockStates.defaultState(this).withProperty((IProperty)BlockHorizontal.FACING, (Comparable)placer.getHorizontalFacing().getOpposite()).withProperty((IProperty)LIT, (Comparable)Boolean.FALSE);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te;
        BlockStates.set(worldIn, pos, state.withProperty((IProperty)BlockHorizontal.FACING, (Comparable)placer.getHorizontalFacing().getOpposite()), 2);
        if (stack.hasDisplayName() && (te = WorldAccess.getTileEntity(worldIn, pos)) instanceof TileEntityInfestedFurnace) {
            ((TileEntityInfestedFurnace)te).setCustomInventoryName(stack.getDisplayName());
        }
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{BlockHorizontal.FACING, LIT});
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront((int)(meta & 7));
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        boolean lit = (meta & 8) != 0;
        return BlockStates.defaultState(this).withProperty((IProperty)BlockHorizontal.FACING, (Comparable)enumfacing).withProperty((IProperty)LIT, (Comparable)Boolean.valueOf(lit));
    }

    public int getMetaFromState(IBlockState state) {
        int meta = ((EnumFacing)state.getValue((IProperty)BlockHorizontal.FACING)).getIndex();
        if (((Boolean)state.getValue((IProperty)LIT)).booleanValue()) {
            meta |= 8;
        }
        return meta;
    }
}

