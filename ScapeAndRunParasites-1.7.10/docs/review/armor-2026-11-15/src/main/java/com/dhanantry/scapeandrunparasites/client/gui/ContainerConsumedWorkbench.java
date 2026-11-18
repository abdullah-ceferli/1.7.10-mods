/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockWorkbench
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.ContainerWorkbench
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class ContainerConsumedWorkbench
extends ContainerWorkbench {
    private final World world;
    private final BlockPos pos;

    public ContainerConsumedWorkbench(InventoryPlayer playerInv, World world, BlockPos pos) {
        super(playerInv, world, pos);
        this.world = world;
        this.pos = pos;
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return BlockStates.get(this.world, this.pos).getBlock() instanceof BlockWorkbench;
    }
}

