/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockTrapDoor
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class SRPTrapDoor
extends BlockTrapDoor {
    public SRPTrapDoor(Material material, String regName, String modid) {
        super(material);
        RegistryNames.set(this, regName);
        this.setBlockName(modid + "." + regName);
        this.setHardness(3.0f);
        this.setStepSound(material == Material.wood ? com.dhanantry.scapeandrunparasites.backport.sound.SoundType.WOOD : com.dhanantry.scapeandrunparasites.backport.sound.SoundType.METAL);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        Item item = ((Item)RegistryNames.set(new ItemBlock((Block)this), RegistryNames.get(this))).setUnlocalizedName(modid + "." + regName).setCreativeTab((CreativeTabs)SRPMain.SRP_CREATIVETAB);
        SRPItems.SRP_ITEMS.add(item);
    }

    public SRPTrapDoor(Material material, String regName) {
        this(material, regName, "srparasites");
    }

    public SRPTrapDoor(String regName) {
        this(Material.wood, regName, "srparasites");
    }
}

