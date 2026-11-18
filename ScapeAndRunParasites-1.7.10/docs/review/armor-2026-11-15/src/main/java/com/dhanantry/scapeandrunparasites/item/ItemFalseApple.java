/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.item.ItemBase;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFalseApple
extends ItemBase {
    private static final int HUNGER = 4;
    private static final float SATURATION = 0.3f;
    private static final int BUGLIN_COUNT = 5;
    private static final ResourceLocation BUGLIN_ID = new ResourceLocation("srparasites", "buglin");
    private boolean alwaysEdible = true;

    public ItemFalseApple(String name) {
        super(name, 64, (byte)0);
        this.setMaxStackSize(64);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format((String)"tooltip.srparasites.false_apple", (Object[])new Object[0]));
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (this.alwaysEdible || player.getFoodStats().needFood()) {
            player.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        return new ActionResult(EnumActionResult.FAIL, (Object)stack);
    }

    public ItemStack onEaten(ItemStack stack, World world, EntityLivingBase eater) {
        if (eater instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)eater;
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            player.getFoodStats().addStats(4, 0.3f);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.8f, 1.0f);
            player.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.confusion, 200, 0));
            player.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.hunger, 600, 0));
            if (!world.isRemote) {
                for (int i = 0; i < 5; ++i) {
                    Entity e = EntityList.createEntityByIDFromName((ResourceLocation)BUGLIN_ID, (World)world);
                    if (e == null) continue;
                    double offX = (world.rand.nextDouble() - 0.5) * 0.8;
                    double offZ = (world.rand.nextDouble() - 0.5) * 0.8;
                    e.setLocationAndAngles(player.posX + offX, player.posY, player.posZ + offZ, world.rand.nextFloat() * 360.0f, 0.0f);
                    world.spawnEntityInWorld(e);
                }
            }
        }
        return stack;
    }

    public String getUnlocalizedName() {
        return "item.srparasites." + RegistryNames.get(this).getResourcePath();
    }
}

