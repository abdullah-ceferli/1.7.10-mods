/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.minecraftforge.fluids.BlockFluidBase
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DeadBloodBottleHandler {
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (e.getHand() != EnumHand.MAIN_HAND) {
            return;
        }
        EntityPlayer player = e.getEntityPlayer();
        World world = e.getWorld();
        ItemStack held = e.getItemStack();
        if (held.isEmpty() || held.getItem() != Items.glass_bottle) {
            return;
        }
        RayTraceResult rt = DeadBloodBottleHandler.rayTrace(world, player, true);
        if (rt == null || rt.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = rt.getBlockPos();
        if (((BlockFluidBase)SRPFluids.DEADBLOOD_FLUID.getBlock()).getQuantaValue((IBlockAccess)world, pos) <= 0) {
            return;
        }
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            ItemStack filled = new ItemStack(SRPItems.DEADBLOOD_FLUID);
            if (held.isEmpty()) {
                player.setHeldItem(EnumHand.MAIN_HAND, filled);
            } else if (!player.inventory.addItemStackToInventory(filled)) {
                player.dropPlayerItemWithRandomChoice(filled, false);
            }
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.9f, 1.0f);
        }
        e.setCanceled(true);
        e.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static RayTraceResult rayTrace(World world, EntityPlayer player, boolean useLiquids) {
        float pitch = player.rotationPitch;
        float yaw = player.rotationYaw;
        Vec3d eye = player.getPositionEyes(1.0f);
        float f2 = MathHelper.cos((float)(-yaw * ((float)Math.PI / 180) - (float)Math.PI));
        float f3 = MathHelper.sin((float)(-yaw * ((float)Math.PI / 180) - (float)Math.PI));
        float f4 = -MathHelper.cos((float)(-pitch * ((float)Math.PI / 180)));
        float f5 = MathHelper.sin((float)(-pitch * ((float)Math.PI / 180)));
        float x = f3 * f4;
        float y = f5;
        float z = f2 * f4;
        double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d look = eye.addVector((double)x * reach, (double)y * reach, (double)z * reach);
        return world.rayTraceBlocks(eye, look, useLiquids, !useLiquids, false);
    }
}

