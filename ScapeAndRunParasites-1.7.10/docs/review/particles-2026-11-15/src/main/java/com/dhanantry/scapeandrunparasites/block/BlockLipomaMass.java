/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockLipomaMass
extends BlockBush {
    public BlockLipomaMass(String name) {
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setStepSound(SRPSoundTypes.FLESH);
        this.setHardness(0.0f);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    private boolean isSRPBlock(Block b) {
        ResourceLocation rl = RegistryNames.get(b);
        return rl != null && rl.getResourceDomain().equals("srparasites");
    }

    public boolean canPlaceBlockAt(World w, BlockPos pos) {
        BlockPos up = pos.up();
        IBlockState above = BlockStates.get(w, up);
        return this.isSRPBlock(above.getBlock()) && WorldAccess.isSideSolid(w, up, EnumFacing.DOWN, true);
    }

    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(w, pos, state)) {
            WorldAccess.destroyBlock(w, pos, true);
        }
    }

    public boolean canBlockStay(World w, BlockPos pos, IBlockState state) {
        BlockPos up = pos.up();
        IBlockState above = BlockStates.get(w, up);
        return this.isSRPBlock(above.getBlock()) && WorldAccess.isSideSolid(w, up, EnumFacing.DOWN, true);
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
}

