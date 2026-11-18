/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockDoor$EnumDoorHalf
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemDoor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class SRPDoor
extends BlockDoor {
    private final String regName;
    private final Item doorItem;

    public SRPDoor(Material material, String regName) {
        super(material);
        this.regName = regName;
        RegistryNames.set(this, "srparasites", regName);
        this.setBlockName("srparasites." + regName);
        this.setHardness(material == Material.iron ? 5.0f : 3.0f);
        this.setStepSound(material == Material.iron ? com.dhanantry.scapeandrunparasites.backport.sound.SoundType.METAL : com.dhanantry.scapeandrunparasites.backport.sound.SoundType.WOOD);
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        ItemDoor door = new ItemDoor((Block)this);
        RegistryNames.set(door, Objects.requireNonNull(RegistryNames.get(this)));
        door.setUnlocalizedName("srparasites." + regName);
        door.setCreativeTab((CreativeTabs)SRPMain.SRP_CREATIVETAB);
        SRPItems.SRP_ITEMS.add((Item)door);
        this.doorItem = door;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue((IProperty)HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : this.doorItem;
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this.doorItem);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this.doorItem);
    }
}

