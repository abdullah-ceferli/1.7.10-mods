/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockDeadheadLeaves;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.client.IModelSRP;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyEnum;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDeadheadGrassTall
extends BlockBush
implements IModelSRP,
IShearable {
    public static final PropertyEnum<EnumPart> PART = PropertyEnum.create((String)"part", EnumPart.class);
    private static final AxisAlignedBB TALL_TOP_AABB = Boxes.create(0.0, -1.0, 0.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB TALL_BOTTOM_AABB = Boxes.create(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);

    public BlockDeadheadGrassTall(String name) {
        super(Material.vine);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(0.0f);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, (Comparable)((Object)EnumPart.TOP)));
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(PART) == EnumPart.TOP ? TALL_TOP_AABB : TALL_BOTTOM_AABB;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.isValidDeadheadSupport((IBlockAccess)worldIn, pos.up()) && WorldAccess.isAirBlock(worldIn, pos.down());
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        EnumPart part = (EnumPart)((Object)state.getValue(PART));
        if (part == EnumPart.TOP) {
            IBlockState below = BlockStates.get(worldIn, pos.down());
            return this.isValidDeadheadSupport((IBlockAccess)worldIn, pos.up()) && below.getBlock() == this && below.getValue(PART) == EnumPart.BOTTOM;
        }
        IBlockState above = BlockStates.get(worldIn, pos.up());
        return above.getBlock() == this && above.getValue(PART) == EnumPart.TOP;
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return BlockStates.defaultState(this).withProperty(PART, (Comparable)((Object)EnumPart.TOP));
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!worldIn.isRemote && WorldAccess.isAirBlock(worldIn, pos.down())) {
            BlockStates.set(worldIn, pos.down(), BlockStates.defaultState(this).withProperty(PART, (Comparable)((Object)EnumPart.BOTTOM)), 3);
        }
    }

    private boolean isValidDeadheadSupport(IBlockAccess world, BlockPos supportPos) {
        IBlockState supportState = BlockStates.get(world, supportPos);
        Block supportBlock = supportState.getBlock();
        if (supportBlock == SRPBlocks.ParasiteTrunk) {
            return supportState.getValue(BlockParasiteTrunk.VARIANT) == BlockParasiteTrunk.EnumType.DEADHEAD;
        }
        if (supportBlock instanceof BlockDeadheadLeaves) {
            return true;
        }
        ResourceLocation id = RegistryNames.get(supportBlock);
        if (id == null || !supportState.isFullCube()) {
            return false;
        }
        String path = id.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("deadhead") || path.contains("dead_head");
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty(PART, (Comparable)((Object)((meta & 1) == 1 ? EnumPart.BOTTOM : EnumPart.TOP)));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART) == EnumPart.BOTTOM ? 1 : 0;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{PART});
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public boolean canSilkHarvest() {
        return true;
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock((Block)this), 1, 0);
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(Item.getItemFromBlock((Block)this), 1, 0));
    }

    @Override
    public void registerModels() {
        SRPMain.proxy.modelReg(Item.getItemFromBlock((Block)this), 0, "inventory");
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public static enum EnumPart implements IStringSerializable
    {
        TOP,
        BOTTOM;


        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public String toString() {
            return this.getName();
        }
    }
}

