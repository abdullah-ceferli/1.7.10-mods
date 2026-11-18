/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.BlockStairs$EnumHalf
 *  net.minecraft.block.BlockStairs$EnumShape
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.util.EnumFacing
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.IModelSRP;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;

public class BlockStairBase
extends BlockStairs
implements IModelSRP {
    public BlockStairBase(String name, boolean creative, IBlockState modelState) {
        super(modelState);
        RegistryNames.set(this, name + "stairs");
        this.setBlockName("srparasites." + name + "stairs");
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH).withProperty((IProperty)HALF, (Comparable)BlockStairs.EnumHalf.BOTTOM).withProperty((IProperty)SHAPE, (Comparable)BlockStairs.EnumShape.STRAIGHT));
        this.setLightOpacity(255);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    public Block setToolStats(String effectiveTool, int toolLevel) {
        this.setHarvestLevel(effectiveTool, toolLevel);
        return this;
    }

    @Override
    public void registerModels() {
        SRPMain.proxy.modelReg(Item.getItemFromBlock((Block)this), 0, "inventory");
    }
}

