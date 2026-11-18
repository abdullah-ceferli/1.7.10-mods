/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBookshelf
 *  net.minecraft.block.SoundType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.IModelSRP;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBookshelf;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockSRPBookshelf
extends BlockBookshelf
implements IModelSRP {
    public BlockSRPBookshelf(String name) {
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(1.5f);
        this.setResistance(7.5f);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.WOOD);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    @Override
    public void registerModels() {
        SRPMain.proxy.modelReg(Item.getItemFromBlock((Block)this), 0, "inventory");
    }

    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return 1.0f;
    }
}

