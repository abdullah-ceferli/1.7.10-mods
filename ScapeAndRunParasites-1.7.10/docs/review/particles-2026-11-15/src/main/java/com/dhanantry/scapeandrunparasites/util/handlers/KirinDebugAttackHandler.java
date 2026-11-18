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

import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
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
public class KirinDebugAttackHandler {
    private static final ResourceLocation DEBUG_ITEM_ID = new ResourceLocation("srparasites", "itemmobspawner_kirin");
    public static boolean DEBUG_FORCE_ENABLE_KIRIN_ITEM = false;

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        KirinDebugAttackHandler.tryTrigger(event.getEntityPlayer(), event.getHand(), (PlayerInteractEvent)event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        KirinDebugAttackHandler.tryTrigger(event.getEntityPlayer(), event.getHand(), (PlayerInteractEvent)event);
    }

    private static void tryTrigger(EntityPlayer player, EnumHand hand, PlayerInteractEvent event) {
        if (!SRPConfigMobs.kirinDebugSpecialAttack && !DEBUG_FORCE_ENABLE_KIRIN_ITEM) {
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
        EntityKirin closestKirin = KirinDebugAttackHandler.findClosestKirin(player);
        if (closestKirin == null) {
            return;
        }
        EntityLivingBase target = closestKirin.getAttackTarget();
        if (!KirinDebugAttackHandler.isValidTargetForDebug(closestKirin, target)) {
            target = KirinDebugAttackHandler.findClosestValidTarget(closestKirin, player);
        }
        if (target == null && SRPConfigMobs.kirinDebugTargetPlayerIfNoTarget) {
            target = player;
        }
        if (target == null) {
            return;
        }
        closestKirin.setAttackTarget(target);
        closestKirin.spawnJudgementCuts(target);
        closestKirin.resetIdleTime();
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static boolean isValidTargetForDebug(EntityKirin kirin, EntityLivingBase living) {
        if (living == null || living.isDead) {
            return false;
        }
        if (living == kirin) {
            return false;
        }
        if (KirinDebugAttackHandler.isSRParasitesMob(living)) {
            return false;
        }
        if (living instanceof EntityPlayer) {
            EntityPlayer targetPlayer = (EntityPlayer)living;
            if (targetPlayer.isSpectator()) {
                return false;
            }
            if (targetPlayer.isCreative() && !SRPConfigMobs.kirinDebugTargetCreativePlayers) {
                return false;
            }
        }
        return kirin.canEntityBeSeen((Entity)living);
    }

    private static EntityKirin findClosestKirin(EntityPlayer player) {
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(player), 48.0, 32.0, 48.0);
        List list = EntityQueries.entities(player.worldObj, EntityKirin.class, box);
        EntityKirin closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityKirin kirin : list) {
            double dist;
            if (kirin == null || kirin.isDead || !((dist = kirin.getDistanceSqToEntity((Entity)player)) < closestDist)) continue;
            closestDist = dist;
            closest = kirin;
        }
        return closest;
    }

    private static EntityLivingBase findClosestValidTarget(EntityKirin kirin, EntityPlayer playerUsingItem) {
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(kirin), 48.0, 32.0, 48.0);
        List list = EntityQueries.entities(kirin.worldObj, EntityLivingBase.class, box);
        EntityLivingBase closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityLivingBase living : list) {
            double dist;
            EntityPlayer targetPlayer;
            if (living == null || living.isDead || living == kirin || KirinDebugAttackHandler.isSRParasitesMob(living) || living instanceof EntityPlayer && ((targetPlayer = (EntityPlayer)living).isSpectator() || targetPlayer.isCreative() && !SRPConfigMobs.kirinDebugTargetCreativePlayers) || !kirin.canEntityBeSeen((Entity)living) || !((dist = living.getDistanceSqToEntity((Entity)kirin)) < closestDist)) continue;
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

