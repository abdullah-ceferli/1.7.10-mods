/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockLocs
extends BlockBase {
    public static final PropertyBool SNOWY = PropertyBool.create((String)"snowy");

    public BlockLocs(Material material, String name, float hardness, boolean creative, boolean fullCube) {
        super(material, name, hardness, creative, fullCube);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)SNOWY, (Comparable)Boolean.FALSE));
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{SNOWY});
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this);
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState above = BlockStates.get(worldIn, pos.up());
        boolean snowy = above.getBlock() == Blocks.snow_layer || above.getBlock() == Blocks.snow;
        return state.withProperty((IProperty)SNOWY, (Comparable)Boolean.valueOf(snowy));
    }
}

