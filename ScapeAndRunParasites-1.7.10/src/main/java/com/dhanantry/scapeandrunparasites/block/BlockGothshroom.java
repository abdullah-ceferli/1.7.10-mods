/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
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

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyDirection;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGothshroom
extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create((String)"facing", Arrays.asList(EnumFacing.values()));
    public static final PropertyBool GROUP = PropertyBool.create((String)"group");
    private static final AxisAlignedBB AABB_FLOOR = Boxes.create(0.25, 0.0, 0.25, 0.75, 0.65, 0.75);
    private static final AxisAlignedBB AABB_CEILING = Boxes.create(0.25, 0.35, 0.25, 0.75, 1.0, 0.75);
    private static final AxisAlignedBB AABB_NORTH = Boxes.create(0.25, 0.25, 0.0, 0.75, 0.75, 0.6);
    private static final AxisAlignedBB AABB_SOUTH = Boxes.create(0.25, 0.25, 0.4, 0.75, 0.75, 1.0);
    private static final AxisAlignedBB AABB_WEST = Boxes.create(0.0, 0.25, 0.25, 0.6, 0.75, 0.75);
    private static final AxisAlignedBB AABB_EAST = Boxes.create(0.4, 0.25, 0.25, 1.0, 0.75, 0.75);

    public BlockGothshroom(String name, boolean creative) {
        super(Material.plants);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        this.setHardness(0.0f);
        this.setResistance(0.0f);
        this.setLightOpacity(0);
        this.setTickRandomly(false);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.UP).withProperty((IProperty)GROUP, (Comparable)Boolean.FALSE));
        RegistryNames.set(this, "srparasites", name);
        this.setBlockName("srparasites." + name);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        SRPBlocks.SRP_BLOCKS.add(this);
        ItemBlock itemBlock = new ItemBlock((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        switch ((EnumFacing)state.getValue((IProperty)FACING)) {
            case DOWN: {
                return AABB_CEILING;
            }
            case NORTH: {
                return AABB_NORTH;
            }
            case SOUTH: {
                return AABB_SOUTH;
            }
            case WEST: {
                return AABB_WEST;
            }
            case EAST: {
                return AABB_EAST;
            }
        }
        return AABB_FLOOR;
    }

    private static boolean isSRP(Block b) {
        ResourceLocation id = RegistryNames.get(b);
        return id != null && "srparasites".equals(id.getResourceDomain());
    }

    private static boolean canAttachTo(World world, BlockPos pos, EnumFacing attachSide) {
        BlockPos supportPos = pos.offset(attachSide.getOpposite());
        IBlockState support = BlockStates.get(world, supportPos);
        return BlockGothshroom.isSRP(support.getBlock()) && support.isSideSolid((IBlockAccess)world, supportPos, attachSide);
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return BlockGothshroom.canAttachTo(worldIn, pos, side);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing f : EnumFacing.values()) {
            if (!BlockGothshroom.canAttachTo(worldIn, pos, f)) continue;
            return true;
        }
        return false;
    }

    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (BlockGothshroom.canAttachTo(world, pos, facing)) {
            EnumFacing outward = BlockGothshroom.outwardFromAttach(facing);
            return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)outward);
        }
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)EnumFacing.UP);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.checkAndDrop(worldIn, pos, state);
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDrop(worldIn, pos, state);
    }

    private void checkAndDrop(World world, BlockPos pos, IBlockState state) {
        EnumFacing attachSide = BlockGothshroom.attachFromOutward((EnumFacing)state.getValue((IProperty)FACING));
        if (!BlockGothshroom.canAttachTo(world, pos, attachSide)) {
            WorldAccess.destroyBlock(world, pos, true);
        }
    }

    private static EnumFacing attachFromOutward(EnumFacing outward) {
        return outward.getAxis().isVertical() ? outward : outward.getOpposite();
    }

    private static EnumFacing outwardFromAttach(EnumFacing attach) {
        return attach.getAxis().isVertical() ? attach : attach.getOpposite();
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) {
            return false;
        }
        if (held.getItem() == Item.getItemFromBlock((Block)this) && !((Boolean)state.getValue((IProperty)GROUP)).booleanValue()) {
            if (!world.isRemote) {
                BlockStates.set(world, pos, state.withProperty((IProperty)GROUP, (Comparable)Boolean.TRUE), 2);
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(world, null, pos, SoundEvents.BLOCK_GRASS_PLACE, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 0.6f, 1.0f);
            }
            return true;
        }
        return false;
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int count = (Boolean)state.getValue((IProperty)GROUP) != false ? 2 : 1;
        for (int i = 0; i < count; ++i) {
            drops.add((Object)new ItemStack(Item.getItemFromBlock((Block)this)));
        }
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING, GROUP});
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)FACING)).getIndex() & 7 | ((Boolean)state.getValue((IProperty)GROUP) != false ? 8 : 0);
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing f = EnumFacing.getFront((int)(meta & 7));
        if (f == null) {
            f = EnumFacing.UP;
        }
        boolean grouped = (meta & 8) != 0;
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)f).withProperty((IProperty)GROUP, (Comparable)Boolean.valueOf(grouped));
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock((Block)this);
    }
}

