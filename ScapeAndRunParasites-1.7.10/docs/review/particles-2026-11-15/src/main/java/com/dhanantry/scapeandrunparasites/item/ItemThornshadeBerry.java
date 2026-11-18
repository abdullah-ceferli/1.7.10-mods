/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.ItemBase;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemThornshadeBerry
extends ItemBase {
    public ItemThornshadeBerry() {
        super("thornshade_berry", 64, (byte)30);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos placePos;
        ItemStack stack = player.getHeldItem(hand);
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        IBlockState clickedState = BlockStates.get(worldIn, pos);
        Block clickedBlock = clickedState.getBlock();
        BlockPos blockPos = placePos = clickedBlock.isReplaceable((IBlockAccess)worldIn, pos) ? pos : pos.up();
        if (!player.canPlayerEdit(placePos, facing, stack)) {
            return EnumActionResult.FAIL;
        }
        if (!SRPBlocks.Thornshade.canPlaceBlockAt(worldIn, placePos)) {
            return EnumActionResult.FAIL;
        }
        IBlockState placeState = SRPBlocks.Thornshade.getStateForPlacement(worldIn, placePos, facing, hitX, hitY, hitZ, 0, (EntityLivingBase)player, hand);
        if (!worldIn.isRemote) {
            if (!BlockStates.set(worldIn, placePos, placeState, 11)) {
                return EnumActionResult.FAIL;
            }
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            worldIn.playSound(null, placePos, SRPSounds.BLOCKINFEST_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return EnumActionResult.SUCCESS;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 10;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.canEat(true)) {
            playerIn.setActiveHand(handIn);
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        return new ActionResult(EnumActionResult.FAIL, (Object)stack);
    }

    public ItemStack onEaten(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entityLiving;
            if (!worldIn.isRemote) {
                player.getFoodStats().addStats(2, 0.1f);
                player.attackEntityFrom(DamageSource.magic, 4.0f);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_HURT, player.getSoundCategory(), 1.0f, 1.0f);
            player.playSound(SRPSounds.FLESH_GROW, 10.0f, 1.0f);
        }
        return stack;
    }
}

