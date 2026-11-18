/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class HebluDebugAttackHandler {
    private static final ResourceLocation DEBUG_ITEM_ID = new ResourceLocation("srparasites", "itemmobspawner_heblu");
    public static boolean DEBUG_FORCE_ENABLE_HEBLU_ITEM = false;
    public static boolean DEBUG_ALLOW_CREATIVE_PLAYER_TARGET = false;
    public static boolean DEBUG_TARGET_PLAYER_IF_NO_TARGET = false;

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        HebluDebugAttackHandler.tryTrigger(event.getEntityPlayer(), event.getHand(), (PlayerInteractEvent)event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        HebluDebugAttackHandler.tryTrigger(event.getEntityPlayer(), event.getHand(), (PlayerInteractEvent)event);
    }

    private static void tryTrigger(EntityPlayer player, EnumHand hand, PlayerInteractEvent event) {
        if (!SRPConfigMobs.hebluDebugSpecialAttack && !DEBUG_FORCE_ENABLE_HEBLU_ITEM) {
            return;
        }
        if (player == null || player.worldObj.isRemote) {
            return;
        }
        if (hand != EnumHand.MAIN_HAND) {
            return;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation heldId = RegistryNames.get(stack.getItem());
        if (heldId == null || !DEBUG_ITEM_ID.equals((Object)heldId)) {
            return;
        }
        EntityHeblu closestHeblu = HebluDebugAttackHandler.findClosestHeblu(player);
        if (closestHeblu == null) {
            return;
        }
        EntityLivingBase target = HebluDebugAttackHandler.findClosestValidTarget(closestHeblu, player);
        if (target == null && DEBUG_TARGET_PLAYER_IF_NO_TARGET) {
            target = player;
        }
        if (!closestHeblu.getFlyingState()) {
            closestHeblu.changeStateTo(true);
        }
        if (target != null) {
            closestHeblu.setAttackTarget(target);
        }
        closestHeblu.spawnLightBarrage(target, true);
        closestHeblu.resetIdleTime();
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static EntityHeblu findClosestHeblu(EntityPlayer player) {
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(player), 48.0, 32.0, 48.0);
        List list = EntityQueries.entities(player.worldObj, EntityHeblu.class, box);
        EntityHeblu closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityHeblu heblu : list) {
            double dist;
            if (heblu == null || heblu.isDead || !((dist = heblu.getDistanceSqToEntity((Entity)player)) < closestDist)) continue;
            closestDist = dist;
            closest = heblu;
        }
        return closest;
    }

    private static EntityLivingBase findClosestValidTarget(EntityHeblu heblu, EntityPlayer playerUsingItem) {
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(heblu), 48.0, 32.0, 48.0);
        List list = EntityQueries.entities(heblu.worldObj, EntityLivingBase.class, box);
        EntityLivingBase closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityLivingBase living : list) {
            double dist;
            EntityPlayer targetPlayer;
            if (living == null || living.isDead || living == heblu || HebluDebugAttackHandler.isSRParasitesMob(living) || living instanceof EntityPlayer && ((targetPlayer = (EntityPlayer)living).isSpectator() || targetPlayer.isCreative() && !DEBUG_ALLOW_CREATIVE_PLAYER_TARGET) || !heblu.canEntityBeSeen((Entity)living) || !((dist = living.getDistanceSqToEntity((Entity)heblu)) < closestDist)) continue;
            closestDist = dist;
            closest = living;
        }
        return closest;
    }

    private static boolean isSRParasitesMob(EntityLivingBase living) {
        String className = living.getClass().getName();
        return className.startsWith("com.dhanantry.scapeandrunparasites.entity.monster.");
    }
}

