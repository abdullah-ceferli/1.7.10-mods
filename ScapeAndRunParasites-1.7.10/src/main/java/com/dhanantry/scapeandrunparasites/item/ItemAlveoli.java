/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAlveoli
extends ItemBase {
    private static final int DURATION_TICKS = 600;
    private final boolean alwaysEdible = true;

    public ItemAlveoli(String name) {
        super(name, 64, (byte)0);
        this.setMaxStackSize(64);
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    public ItemStack onEaten(ItemStack stack, World world, EntityLivingBase eater) {
        if (eater instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer)eater;
            if (!p.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(world, null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_PLAYER_BURP, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.PLAYERS, 0.8f, 1.0f);
        }
        return stack;
    }

    public String getUnlocalizedName() {
        return "item.srparasites." + RegistryNames.get(this).getResourcePath();
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        ResourceLocation rl = RegistryNames.get(this);
        String key = rl != null ? "tooltip.srparasites." + rl.getResourcePath() : "tooltip.srparasites.alveoli";
        tooltip.add(I18n.format((String)key, (Object[])new Object[0]));
    }
}

