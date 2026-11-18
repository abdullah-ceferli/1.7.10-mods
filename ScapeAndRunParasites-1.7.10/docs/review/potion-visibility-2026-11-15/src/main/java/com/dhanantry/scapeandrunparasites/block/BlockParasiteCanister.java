/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyEnum;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockParasiteCanister
extends BlockBase
implements IMetaName {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create((String)"variant", EnumType.class);
    protected static final AxisAlignedBB TALL_GRASS_AABB = Boxes.create(0.1, 0.0, 0.1, 0.9, 0.55, 0.9);

    public BlockParasiteCanister(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
        super(material, name, hardness, creative, tickRandom);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, (Comparable)((Object)EnumType.SAC)));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(VARIANT) == EnumType.CYST) {
            return TALL_GRASS_AABB;
        }
        return super.getBoundingBox(state, source, pos);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public int damageDropped(IBlockState state) {
        return BlockStates.meta(this, state);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
        if (!worldIn.isRemote && state.getValue(VARIANT) == EnumType.CYST && BlockStates.get(worldIn, pos.down()).getBlock() == Blocks.air) {
            BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.air));
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch ((EnumType)((Object)state.getValue(VARIANT))) {
            case SAC: {
                return SRPItems.itemlurecomponent6;
            }
        }
        return super.getItemDropped(state, rand, fortune);
    }

    public int quantityDropped(IBlockState state, int fortune, Random random) {
        if (state.getValue(VARIANT) == EnumType.SAC) {
            if (random.nextDouble() < 0.05) {
                return 1;
            }
            return 0;
        }
        return super.quantityDropped(state, fortune, random);
    }

    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType variant : EnumType.values()) {
            items.add((Object)new ItemStack((Block)this, 1, variant.ordinal()));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty(VARIANT, (Comparable)((Object)EnumType.values()[meta]));
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumType)((Object)state.getValue(VARIANT))).ordinal();
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock((Block)this), 1, BlockStates.meta(this, BlockStates.get(world, pos)));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{VARIANT});
    }

    @Override
    public Enum[] getVariants() {
        return EnumType.values();
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockVariant(this);
    }

    public static enum EnumType implements IStringSerializable
    {
        SAC,
        CYST,
        LUMP,
        BAG;


        public String getName() {
            return this.name().toLowerCase();
        }

        public String toString() {
            return this.getName();
        }
    }
}

