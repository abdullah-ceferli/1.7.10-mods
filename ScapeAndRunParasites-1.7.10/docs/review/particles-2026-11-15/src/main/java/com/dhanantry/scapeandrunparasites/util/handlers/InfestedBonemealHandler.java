/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.player.BonemealEvent
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class InfestedBonemealHandler {
    @SubscribeEvent
    public void onBonemeal(BonemealEvent e) {
        boolean growable;
        if (e.getStack() == null || e.getStack().getItem() != SRPItems.infestedbonemeal) {
            return;
        }
        World world = e.getWorld();
        BlockPos pos = e.getPos();
        Random rand = world.rand;
        IBlockState state = e.getBlock();
        Block block = state.getBlock();
        if (block != SRPBlocks.ParasiteSapling) {
            return;
        }
        if (!(block instanceof BlockParasiteSapling)) {
            return;
        }
        BlockParasiteSapling sap = (BlockParasiteSapling)block;
        BlockParasiteSapling.EnumType type = (BlockParasiteSapling.EnumType)((Object)state.getValue(BlockParasiteSapling.VARIANT));
        boolean bl = growable = type == BlockParasiteSapling.EnumType.CONSUMED || type == BlockParasiteSapling.EnumType.DEADHEAD || type == BlockParasiteSapling.EnumType.INFESTED;
        if (!growable) {
            e.setResult(Event.Result.DENY);
            return;
        }
        boolean grew = false;
        if ((Integer)state.getValue((IProperty)BlockParasiteSapling.STAGE) == 0) {
            grew = BlockStates.set(world, pos, state.withProperty((IProperty)BlockParasiteSapling.STAGE, (Comparable)Integer.valueOf(1)), 4);
        } else {
            sap.generateTree(world, pos, state, rand);
            grew = true;
        }
        e.setResult(grew ? Event.Result.ALLOW : Event.Result.DENY);
    }
}

