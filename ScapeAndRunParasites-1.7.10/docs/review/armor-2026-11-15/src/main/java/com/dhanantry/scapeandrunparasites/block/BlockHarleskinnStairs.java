/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.state.IBlockState
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;

public class BlockHarleskinnStairs
extends BlockStairs {
    public BlockHarleskinnStairs(IBlockState modelState, String registryName) {
        super(modelState);
        RegistryNames.set(this, registryName);
        this.setBlockName("srparasites." + registryName);
        this.setHardness(1.5f);
        this.setResistance(10.0f);
        this.setStepSound(SRPSoundTypes.FLESH);
        this.useNeighborBrightness = true;
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
    }
}

