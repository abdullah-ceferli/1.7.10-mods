/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockWithTooltip
extends ItemBlock {
    public ItemBlockWithTooltip(Block block) {
        super(block);
        RegistryNames.set(this, RegistryNames.get(block));
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (this.block == null || RegistryNames.get(this.block) == null) {
            return;
        }
        String key = "tooltip." + RegistryNames.get(this.block).getResourcePath();
        String translated = I18n.format((String)key, (Object[])new Object[0]);
        if (!translated.equals(key) && !translated.isEmpty()) {
            if (translated.contains("\\n")) {
                Arrays.stream(translated.split("\\\\n")).forEach(tooltip::add);
            } else {
                tooltip.add(translated);
            }
        }
    }
}

