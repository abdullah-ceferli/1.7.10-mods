/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShrimp
extends ItemFood {
    public ItemShrimp(String name) {
        super(4, 0.4f, true);
        RegistryNames.set(this, name);
        this.setUnlocalizedName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setMaxStackSize(64);
        this.setAlwaysEdible();
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(EnumChatFormatting.WHITE + I18n.format((String)"tooltip.srparasites.shrimp.desc", (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.GRAY + I18n.format((String)"tooltip.srparasites.shrimp.arrow", (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.DARK_AQUA + I18n.format((String)"tooltip.srparasites.shrimp.ariral", (Object[])new Object[0]));
    }

    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if (!worldIn.isRemote && player != null) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(worldIn, null, player.posX, player.posY, player.posZ, SRPSounds.SHRIMP_EAT, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.PLAYERS, 0.8f, 0.95f + worldIn.rand.nextFloat() * 0.1f);
        }
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!(target instanceof EntityInfEnderman)) {
            return false;
        }
        EntityInfEnderman enderman = (EntityInfEnderman)target;
        if (enderman.isAriral()) {
            return true;
        }
        if (!player.worldObj.isRemote) {
            enderman.setAriral(true);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(player.worldObj, null, enderman.posX, enderman.posY, enderman.posZ, SoundEvents.ENTITY_PLAYER_BURP, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.NEUTRAL, 0.6f, 0.9f + player.worldObj.rand.nextFloat() * 0.2f);
        }
        return true;
    }
}

