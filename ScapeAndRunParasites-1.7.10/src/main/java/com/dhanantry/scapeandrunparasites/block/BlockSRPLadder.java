/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLadder
 *  net.minecraft.block.SoundType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.IModelSRP;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockSRPLadder
extends BlockLadder
implements IModelSRP {
    public BlockSRPLadder(String name) {
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(0.4f);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.LADDER);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    @Override
    public void registerModels() {
        SRPMain.proxy.modelReg(Item.getItemFromBlock((Block)this), 0, "inventory");
    }
}

