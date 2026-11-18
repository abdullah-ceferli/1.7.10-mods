/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemLead
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.Mirror;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockParasiteThin
extends Block {
    public static final PropertyBool NORTH = PropertyBool.create((String)"north");
    public static final PropertyBool EAST = PropertyBool.create((String)"east");
    public static final PropertyBool SOUTH = PropertyBool.create((String)"south");
    public static final PropertyBool WEST = PropertyBool.create((String)"west");
    public static final PropertyBool UP = PropertyBool.create((String)"up");
    public static final PropertyBool DOWN = PropertyBool.create((String)"down");
    protected static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[]{Boxes.create(0.375, 0.0, 0.375, 0.625, 1.0, 0.625), Boxes.create(0.375, 0.0, 0.375, 0.625, 1.0, 1.0), Boxes.create(0.0, 0.0, 0.375, 0.625, 1.0, 0.625), Boxes.create(0.0, 0.0, 0.375, 0.625, 1.0, 1.0), Boxes.create(0.375, 0.0, 0.0, 0.625, 1.0, 0.625), Boxes.create(0.375, 0.0, 0.0, 0.625, 1.0, 1.0), Boxes.create(0.0, 0.0, 0.0, 0.625, 1.0, 0.625), Boxes.create(0.0, 0.0, 0.0, 0.625, 1.0, 1.0), Boxes.create(0.375, 0.0, 0.375, 1.0, 1.0, 0.625), Boxes.create(0.375, 0.0, 0.375, 1.0, 1.0, 1.0), Boxes.create(0.0, 0.0, 0.375, 1.0, 1.0, 0.625), Boxes.create(0.0, 0.0, 0.375, 1.0, 1.0, 1.0), Boxes.create(0.375, 0.0, 0.0, 1.0, 1.0, 0.625), Boxes.create(0.375, 0.0, 0.0, 1.0, 1.0, 1.0), Boxes.create(0.0, 0.0, 0.0, 1.0, 1.0, 0.625), Boxes.create(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};
    public static final AxisAlignedBB PILLAR_AABB = Boxes.create(0.375, 0.5, 0.375, 0.625, 1.5, 0.625);
    public static final AxisAlignedBB SOUTH_AABB = Boxes.create(0.375, 0.0, 0.625, 0.625, 1.5, 1.0);
    public static final AxisAlignedBB WEST_AABB = Boxes.create(0.0, 0.0, 0.375, 0.375, 1.5, 0.625);
    public static final AxisAlignedBB NORTH_AABB = Boxes.create(0.375, 0.0, 0.0, 0.625, 1.5, 0.375);
    public static final AxisAlignedBB EAST_AABB = Boxes.create(0.625, 0.0, 0.375, 1.0, 1.5, 0.625);

    public BlockParasiteThin(String name, float hardness) {
        super(Material.wood);
        this.setHarvestLevel("axe", 0);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(hardness);
        this.setStepSound(SoundType.WOOD);
        SRPBlocks.SRP_BLOCKS.add(this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if (!isActualState) {
            state = state.getActualState((IBlockAccess)worldIn, pos);
        }
        BlockParasiteThin.addCollisionBoxToList((BlockPos)pos, (AxisAlignedBB)entityBox, collidingBoxes, (AxisAlignedBB)PILLAR_AABB);
        if (((Boolean)state.getValue((IProperty)NORTH)).booleanValue()) {
            BlockParasiteThin.addCollisionBoxToList((BlockPos)pos, (AxisAlignedBB)entityBox, collidingBoxes, (AxisAlignedBB)NORTH_AABB);
        }
        if (((Boolean)state.getValue((IProperty)EAST)).booleanValue()) {
            BlockParasiteThin.addCollisionBoxToList((BlockPos)pos, (AxisAlignedBB)entityBox, collidingBoxes, (AxisAlignedBB)EAST_AABB);
        }
        if (((Boolean)state.getValue((IProperty)SOUTH)).booleanValue()) {
            BlockParasiteThin.addCollisionBoxToList((BlockPos)pos, (AxisAlignedBB)entityBox, collidingBoxes, (AxisAlignedBB)SOUTH_AABB);
        }
        if (((Boolean)state.getValue((IProperty)WEST)).booleanValue()) {
            BlockParasiteThin.addCollisionBoxToList((BlockPos)pos, (AxisAlignedBB)entityBox, collidingBoxes, (AxisAlignedBB)WEST_AABB);
        }
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = this.getActualState(state, source, pos);
        return BOUNDING_BOXES[BlockParasiteThin.getBoundingBoxIdx(state)];
    }

    private static int getBoundingBoxIdx(IBlockState state) {
        int i = 0;
        if (((Boolean)state.getValue((IProperty)NORTH)).booleanValue()) {
            i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }
        if (((Boolean)state.getValue((IProperty)EAST)).booleanValue()) {
            i |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }
        if (((Boolean)state.getValue((IProperty)SOUTH)).booleanValue()) {
            i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }
        if (((Boolean)state.getValue((IProperty)WEST)).booleanValue()) {
            i |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }
        return i;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
        IBlockState iblockstate = BlockStates.get(worldIn, pos);
        BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, facing);
        Block block = iblockstate.getBlock();
        boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE && iblockstate.getMaterial() == this.blockMaterial;
        boolean flag2 = block == SRPBlocks.ParasiteThin;
        boolean flag3 = blockfaceshape == BlockFaceShape.SOLID && (facing == EnumFacing.UP || iblockstate.isFullCube());
        return !BlockParasiteThin.isExcepBlockForAttachWithPiston(block) && flag3 || flag || flag2;
    }

    protected static boolean isExcepBlockForAttachWithPiston(Block p_194142_0_) {
        return Block.isExceptBlockForAttachWithPiston((Block)p_194142_0_) || p_194142_0_ == Blocks.BARRIER || p_194142_0_ == Blocks.melon_block || p_194142_0_ == Blocks.pumpkin || p_194142_0_ == Blocks.lit_pumpkin;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            return ItemLead.attachToFence((EntityPlayer)playerIn, (World)worldIn, (BlockPos)pos);
        }
        ItemStack itemstack = playerIn.getHeldItem(hand);
        return itemstack.getItem() == Items.lead || itemstack.isEmpty();
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty((IProperty)NORTH, (Comparable)Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.NORTH))).withProperty((IProperty)EAST, (Comparable)Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.EAST))).withProperty((IProperty)SOUTH, (Comparable)Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.SOUTH))).withProperty((IProperty)WEST, (Comparable)Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.WEST))).withProperty((IProperty)UP, (Comparable)Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.UP))).withProperty((IProperty)DOWN, (Comparable)Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.DOWN)));
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return state.withProperty((IProperty)NORTH, state.getValue((IProperty)SOUTH)).withProperty((IProperty)EAST, state.getValue((IProperty)WEST)).withProperty((IProperty)SOUTH, state.getValue((IProperty)NORTH)).withProperty((IProperty)WEST, state.getValue((IProperty)EAST)).withProperty((IProperty)UP, state.getValue((IProperty)UP)).withProperty((IProperty)DOWN, state.getValue((IProperty)DOWN));
            }
            case COUNTERCLOCKWISE_90: {
                return state.withProperty((IProperty)NORTH, state.getValue((IProperty)EAST)).withProperty((IProperty)EAST, state.getValue((IProperty)SOUTH)).withProperty((IProperty)SOUTH, state.getValue((IProperty)WEST)).withProperty((IProperty)WEST, state.getValue((IProperty)NORTH)).withProperty((IProperty)UP, state.getValue((IProperty)UP)).withProperty((IProperty)DOWN, state.getValue((IProperty)DOWN));
            }
            case CLOCKWISE_90: {
                return state.withProperty((IProperty)NORTH, state.getValue((IProperty)WEST)).withProperty((IProperty)EAST, state.getValue((IProperty)NORTH)).withProperty((IProperty)SOUTH, state.getValue((IProperty)EAST)).withProperty((IProperty)WEST, state.getValue((IProperty)SOUTH)).withProperty((IProperty)UP, state.getValue((IProperty)UP)).withProperty((IProperty)DOWN, state.getValue((IProperty)DOWN));
            }
        }
        return state;
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT: {
                return state.withProperty((IProperty)NORTH, state.getValue((IProperty)SOUTH)).withProperty((IProperty)SOUTH, state.getValue((IProperty)NORTH));
            }
            case FRONT_BACK: {
                return state.withProperty((IProperty)EAST, state.getValue((IProperty)WEST)).withProperty((IProperty)WEST, state.getValue((IProperty)EAST));
            }
        }
        return super.withMirror(state, mirrorIn);
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{NORTH, EAST, WEST, SOUTH, UP, DOWN});
    }

    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return this.canConnectTo(world, pos.offset(facing), facing.getOpposite());
    }

    private boolean canFenceConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos other = pos.offset(facing);
        Block block = BlockStates.get(world, other).getBlock();
        return this.canConnectTo(world, other, facing.getOpposite());
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
    }
}

