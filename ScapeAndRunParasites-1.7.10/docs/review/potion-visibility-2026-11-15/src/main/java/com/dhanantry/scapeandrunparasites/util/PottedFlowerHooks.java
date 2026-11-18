/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityFlowerPot
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockPottedSRPFlower;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumActionResult;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class PottedFlowerHooks {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getFace() == EnumFacing.UP) {
            return;
        }
        if (BlockStates.get(e.getWorld(), e.getPos()).getBlock() != Blocks.flower_pot) {
            return;
        }
        TileEntity te = WorldAccess.getTileEntity(e.getWorld(), e.getPos());
        if (!(te instanceof TileEntityFlowerPot)) {
            return;
        }
        if (!((TileEntityFlowerPot)te).getFlowerItemStack().isEmpty()) {
            return;
        }
        ItemStack held = e.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        Item item = held.getItem();
        BlockPottedSRPFlower potted = null;
        if (item == Item.getItemFromBlock((Block)SRPBlocks.ASSIMILATED_BLOSSOM)) {
            potted = SRPBlocks.POTTED_ASSIMILATED_BLOSSOM;
        }
        if (potted == null) {
            return;
        }
        if (!e.getWorld().isRemote) {
            BlockStates.set(e.getWorld(), e.getPos(), BlockStates.defaultState(potted), 3);
            if (!e.getEntityPlayer().capabilities.isCreativeMode) {
                held.shrink(1);
            }
        }
        e.setCanceled(true);
        e.setCancellationResult(EnumActionResult.SUCCESS);
    }
}

