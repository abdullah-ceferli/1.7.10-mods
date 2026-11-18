/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.NonNullList
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;

public class BlockTressesHair
extends BlockDoublePlant {
    public BlockTressesHair(String name) {
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        this.setHardness(0.0f);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    public boolean isOpaqueCube(IBlockState s) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState s) {
        return false;
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    protected boolean canSustainBush(IBlockState stateBelow) {
        return this.isSRPBlock(stateBelow);
    }

    private boolean isSRPBlock(IBlockState s) {
        if (s == null) {
            return false;
        }
        Block b = s.getBlock();
        return RegistryNames.get(b) != null && "srparasites".equals(RegistryNames.get(b).getResourceDomain());
    }

    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        CreativeTabs selfTab = this.getCreativeTabToDisplayOn();
        if (selfTab != null && (tab == selfTab || tab == CreativeTabs.tabAllSearch)) {
            items.add((Object)new ItemStack((Block)this, 1, 0));
        }
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }
}

