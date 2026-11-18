/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockGlass
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.IStagedBlock;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAshenGlass
extends BlockGlass
implements IStagedBlock {
    public static final PropertyInteger STAGE = PropertyInteger.create((String)"stage", (int)0, (int)5);

    public BlockAshenGlass(String name) {
        this(name, true);
    }

    public BlockAshenGlass(String name, boolean creative) {
        super(Material.glass, true);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(0.3f);
        this.setStepSound(SoundType.GLASS);
        this.setLightOpacity(0);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(0)));
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        ItemBlock itemBlock = new ItemBlock((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    @Override
    public PropertyInteger getStageProperty() {
        return STAGE;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{STAGE});
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)STAGE);
    }

    public IBlockState getStateFromMeta(int meta) {
        if (meta < 0) {
            meta = 0;
        }
        if (meta > 5) {
            meta = 5;
        }
        return BlockStates.defaultState(this).withProperty((IProperty)STAGE, (Comparable)Integer.valueOf(meta));
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    protected boolean canSilkHarvest() {
        return true;
    }
}

