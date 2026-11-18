/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.IStagedBlock;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockWallBase
extends BlockWall {
    public BlockWallBase(String name, boolean creative, Block modelBlock) {
        super(modelBlock);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        ItemBlock itemBlock = new ItemBlock((Block)this);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
            itemBlock.setCreativeTab((CreativeTabs)SRPMain.SRP_CREATIVETAB);
        }
        this.setTickRandomly(true);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        CreativeTabs myTab = this.getCreativeTabToDisplayOn();
        if (tab == CreativeTabs.tabAllSearch || tab == myTab) {
            items.add((Object)new ItemStack((Block)this));
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        if (!worldIn.isRemote) {
            WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 10);
        }
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!worldIn.isRemote) {
            WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 10);
        }
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.updateTick(worldIn, pos, state, rand);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        if (BlockWallBase.touchingAnyInfestation(worldIn, pos)) {
            BeckonBlockInfestation.beckonInfestation(worldIn, pos, rand, 1, false);
            WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 20);
        }
    }

    private static boolean touchingAnyInfestation(World worldIn, BlockPos pos) {
        for (int dir = 0; dir <= 5; ++dir) {
            String path;
            BlockPos helper = BlockParasiteSpreading.directionToSpread(pos, dir);
            IBlockState st = BlockStates.get(worldIn, helper);
            Block b = st.getBlock();
            if (b instanceof IStagedBlock) {
                return true;
            }
            ResourceLocation rl = RegistryNames.get(b);
            if (rl == null || !"srparasites".equals(rl.getResourceDomain()) || !(path = rl.getResourcePath().toLowerCase(Locale.ROOT)).contains("infest")) continue;
            return true;
        }
        return false;
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this);
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}

