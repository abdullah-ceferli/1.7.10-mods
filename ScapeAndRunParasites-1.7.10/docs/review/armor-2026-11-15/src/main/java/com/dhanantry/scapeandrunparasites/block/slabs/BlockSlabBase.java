/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.material.Material
 */
package com.dhanantry.scapeandrunparasites.block.slabs;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;

public abstract class BlockSlabBase
extends BlockSlab
implements IMetaName {
    public BlockSlabBase(Material materialIn, String name, float hardness, boolean creative, boolean tickRandom, BlockSlab half) {
        super(materialIn);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(hardness);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        this.useNeighborBrightness = !this.isDouble();
    }

    public abstract BlockSlab getHalfBlock();

    public abstract BlockSlab getDoubleBlock();
}

