/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRotatedPillar
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
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
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyEnum;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockParasiteTrunk
extends BlockRotatedPillar
implements IMetaName {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create((String)"variant", EnumType.class);

    public BlockParasiteTrunk(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
        super(material);
        this.setHarvestLevel("axe", 0);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(hardness);
        this.setTickRandomly(tickRandom);
        this.setStepSound(SoundType.WOOD);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, (Comparable)((Object)EnumType.TREE)).withProperty((IProperty)AXIS, (Comparable)EnumFacing.Axis.Y));
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        ItemBlock itemBlock = this.getItemBlock();
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public int damageDropped(IBlockState state) {
        return ((EnumType)((Object)state.getValue(VARIANT))).ordinal();
    }

    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType variant : EnumType.values()) {
            items.add((Object)new ItemStack((Block)this, 1, variant.ordinal()));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis axis;
        EnumType variant;
        EnumType[] all = EnumType.values();
        if (meta >= 12) {
            int ax = meta - 12;
            variant = all[Math.min(4, all.length - 1)];
            axis = ax == 1 ? EnumFacing.Axis.X : (ax == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.Y);
        } else {
            variant = all[(meta & 3) % all.length];
            int bits = meta & 0xC;
            axis = bits == 4 ? EnumFacing.Axis.X : (bits == 8 ? EnumFacing.Axis.Z : EnumFacing.Axis.Y);
        }
        return BlockStates.defaultState(this).withProperty(VARIANT, (Comparable)((Object)variant)).withProperty((IProperty)AXIS, (Comparable)axis);
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumType[] all = EnumType.values();
        int idx = Math.min(meta, all.length - 1);
        return BlockStates.defaultState(this).withProperty(VARIANT, (Comparable)((Object)all[idx])).withProperty((IProperty)AXIS, (Comparable)facing.getAxis());
    }

    public int getMetaFromState(IBlockState state) {
        EnumFacing.Axis ax = (EnumFacing.Axis)state.getValue((IProperty)AXIS);
        int ord = ((EnumType)((Object)state.getValue(VARIANT))).ordinal();
        if (ord == 4) {
            int axIdx = ax == EnumFacing.Axis.X ? 1 : (ax == EnumFacing.Axis.Z ? 2 : 0);
            return 12 + axIdx;
        }
        int axisBits = ax == EnumFacing.Axis.X ? 4 : (ax == EnumFacing.Axis.Z ? 8 : 0);
        return ord & 3 | axisBits;
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock((Block)this), 1, ((EnumType)((Object)state.getValue(VARIANT))).ordinal());
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{VARIANT, AXIS});
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return super.getSilkTouchDrop(state);
    }

    @Override
    public Enum[] getVariants() {
        return EnumType.values();
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
        if (worldIn.getBiome(pos) instanceof BiomeParasiteBase) {
            BlockParasiteSpreading.spreadBiomeBlockTrunk(worldIn, pos, rand);
        }
    }

    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(VARIANT) == EnumType.DEADHEAD;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        int range;
        int loadedRange;
        if (state.getValue(VARIANT) == EnumType.DEADHEAD && WorldAccess.isAreaLoaded(worldIn, pos.add(-(loadedRange = (range = 4) + 1), -loadedRange, -loadedRange), pos.add(loadedRange, loadedRange, loadedRange))) {
            for (int x = -range; x <= range; ++x) {
                for (int y = -range; y <= range; ++y) {
                    for (int z = -range; z <= range; ++z) {
                        BlockPos checkPos = pos.add(x, y, z);
                        IBlockState checkState = BlockStates.get(worldIn, checkPos);
                        if (!checkState.getBlock().isLeaves(checkState, (IBlockAccess)worldIn, checkPos)) continue;
                        checkState.getBlock().beginLeavesDecay(checkState, worldIn, checkPos);
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockVariant((Block)this);
    }

    public static enum EnumType implements IStringSerializable
    {
        BALL,
        TREE,
        PLANT,
        CIRCLE,
        DEADHEAD;


        public String getName() {
            return this.name().toLowerCase();
        }

        public String toString() {
            return this.getName();
        }
    }
}

