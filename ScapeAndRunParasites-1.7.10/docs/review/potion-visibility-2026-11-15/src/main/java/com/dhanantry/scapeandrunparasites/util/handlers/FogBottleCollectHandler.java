/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FogBottleCollectHandler {
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        this.tryCollectFog(event.getWorld(), event.getEntityPlayer(), event.getHand(), (PlayerInteractEvent)event);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        this.tryCollectFog(event.getWorld(), event.getEntityPlayer(), event.getHand(), (PlayerInteractEvent)event);
    }

    private void tryCollectFog(World world, EntityPlayer player, EnumHand hand, PlayerInteractEvent event) {
        ItemStack fogBottle;
        if (world == null || player == null || hand == null) {
            return;
        }
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || held.getItem() != Items.glass_bottle) {
            return;
        }
        BlockPos fogPos = this.findFogInLook(world, player);
        if (fogPos == null) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        if (world.isRemote) {
            return;
        }
        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
        }
        if (!player.inventory.addItemStackToInventory(fogBottle = new ItemStack(SRPItems.FOG_BOTTLE))) {
            player.dropPlayerItemWithRandomChoice(fogBottle, false);
        }
        world.playSound(null, fogPos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
        BlockStates.set(world, fogPos, BlockStates.defaultState(Blocks.air), 3);
    }

    private BlockPos findFogInLook(World world, EntityPlayer player) {
        double reach = 5.0;
        if (player instanceof EntityPlayerMP) {
            reach = ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3d eyes = player.getPositionEyes(1.0f);
        Vec3d look = player.getLook(1.0f);
        BlockPos lastPos = null;
        for (double d = 0.0; d <= reach; d += 0.15) {
            Vec3d point = eyes.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
            BlockPos pos = new BlockPos(point.xCoord, point.yCoord, point.zCoord);
            if (pos.equals(lastPos)) continue;
            lastPos = pos;
            IBlockState state = BlockStates.get(world, pos);
            if (state.getBlock() != SRPBlocks.ParasiteFog) continue;
            return pos;
        }
        return null;
    }
}

