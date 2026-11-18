/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.shader.BreatheShaderManager;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAlveolarFluid
extends Item {
    private static final int DURATION = 600;

    private void init(String name) {
        RegistryNames.set(this, name);
        this.setUnlocalizedName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setMaxStackSize(1);
        SRPItems.SRP_ITEMS.add(this);
    }

    public ItemAlveolarFluid(String name) {
        this.init(name);
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand hand) {
        p.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, (Object)p.getHeldItem(hand));
    }

    public ItemStack onEaten(ItemStack stack, World w, EntityLivingBase entity) {
        EntityPlayer player;
        if (!w.isRemote) {
            entity.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.digSpeed, 600, 0));
            entity.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSpeed, 600, 0));
            entity.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.VIRA_E, 600, 2));
        }
        if (w.isRemote && entity instanceof EntityPlayer) {
            BreatheShaderManager.get().enableFor(600);
        }
        EntityPlayer entityPlayer = player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
        if (player == null || !player.capabilities.isCreativeMode) {
            stack.shrink(1);
            if (player != null) {
                ItemStack bottle = new ItemStack(Items.glass_bottle);
                if (stack.isEmpty()) {
                    return bottle;
                }
                if (!player.inventory.addItemStackToInventory(bottle)) {
                    player.dropPlayerItemWithRandomChoice(bottle, false);
                }
            }
        }
        return stack;
    }
}

