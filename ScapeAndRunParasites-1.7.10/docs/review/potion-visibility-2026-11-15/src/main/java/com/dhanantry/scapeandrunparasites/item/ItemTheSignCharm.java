/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.item.ItemBase;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTheSignCharm
extends ItemBase {
    public ItemTheSignCharm(String name) {
        super(name, 1, (byte)0);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(EnumChatFormatting.RED + I18n.format((String)"tooltip.srparasites.the_sign_charm.red", (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.WHITE + I18n.format((String)"tooltip.srparasites.the_sign_charm.white", (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC + I18n.format((String)"tooltip.srparasites.the_sign_charm.gray", (Object[])new Object[0]));
    }
}

