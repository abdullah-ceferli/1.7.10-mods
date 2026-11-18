/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.player.BonemealEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockThornshade;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class ThornshadeBonemealBlocker {
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        BlockPos pos;
        World world = event.getWorld();
        IBlockState state = BlockStates.get(world, pos = event.getPos());
        if (!(state.getBlock() instanceof BlockThornshade)) {
            return;
        }
        ItemStack stack = event.getStack();
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getItem() == Items.dye && stack.getItemDamage() == 15) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
    }
}

