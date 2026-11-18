/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block.slabs;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.slabs.BlockSlabBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyEnum;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class BlockSlabRubble
extends BlockSlabBase {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create((String)"variant", EnumType.class);

    public BlockSlabRubble(Material materialIn, String name, float hardness, boolean creative, boolean tickRandom, BlockSlab half) {
        super(materialIn, name, hardness, tickRandom, tickRandom, half);
        this.setHarvestLevel("pickaxe", 1);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        this.useNeighborBrightness = !this.isDouble();
        IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, (Comparable)((Object)EnumType.BONE));
        if (!this.isDouble()) {
            state = state.withProperty((IProperty)HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
        }
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack((Block)this, 1, ((EnumType)((Object)state.getValue(VARIANT))).ordinal());
    }

    public String getTranslationKey(int meta) {
        return super.getUnlocalizedName();
    }

    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    public Comparable<?> getTypeForItem(ItemStack stack) {
        return EnumType.values()[stack.getItemDamage() & 7];
    }

    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType variant : EnumType.values()) {
            items.add((Object)new ItemStack((Block)this, 1, variant.ordinal()));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = BlockStates.defaultState(this).withProperty(VARIANT, (Comparable)((Object)EnumType.values()[meta & 7]));
        if (!this.isDouble()) {
            state = state.withProperty((IProperty)HALF, (Comparable)((meta & 8) != 0 ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM));
        }
        return state;
    }

    public int getMetaFromState(IBlockState state) {
        int meta = ((EnumType)((Object)state.getValue(VARIANT))).ordinal();
        if (!this.isDouble() && state.getValue((IProperty)HALF) == BlockSlab.EnumBlockHalf.TOP) {
            meta |= 8;
        }
        return meta;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{HALF, VARIANT});
    }

    public int damageDropped(IBlockState state) {
        return BlockStates.meta(this, state);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockVariant((Block)this);
    }

    @Override
    public Enum[] getVariants() {
        return EnumType.values();
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock((Block)this.getHalfBlock()), 1, BlockStates.meta(state.getBlock(), state.withProperty((IProperty)HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM)));
    }

    @Override
    public BlockSlab getHalfBlock() {
        return SRPBlocks.ParasiteRubbleSlabHalf;
    }

    @Override
    public BlockSlab getDoubleBlock() {
        return SRPBlocks.ParasiteRubbleSlabDouble;
    }

    public static class BlockSlabRubbleDouble
    extends BlockSlabRubble {
        public BlockSlabRubbleDouble(Material materialIn, String name, float hardness, boolean creative, boolean tickRandom, BlockSlab half) {
            super(materialIn, name + "slabdouble", hardness, creative, tickRandom, half);
            SRPBlocks.SRP_BLOCKS.add((Block)this);
            SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
        }

        public boolean isDouble() {
            return true;
        }
    }

    public static class BlockSlabRubbleHalf
    extends BlockSlabRubble {
        public BlockSlabRubbleHalf(Material materialIn, String name, float hardness, boolean creative, boolean tickRandom, BlockSlab half, BlockSlab doubleSlab) {
            super(materialIn, name + "slabhalf", hardness, creative, tickRandom, half);
            SRPBlocks.SRP_BLOCKS.add((Block)this);
        }

        public boolean isDouble() {
            return false;
        }
    }

    public static enum EnumType implements IStringSerializable
    {
        FLESH,
        BONE,
        STONE,
        STONEDEBRIS,
        WOOD,
        BRICKS,
        METAL,
        OBSIDIAN;


        public String getName() {
            return this.name().toLowerCase();
        }

        public String toString() {
            return this.getName();
        }
    }
}

