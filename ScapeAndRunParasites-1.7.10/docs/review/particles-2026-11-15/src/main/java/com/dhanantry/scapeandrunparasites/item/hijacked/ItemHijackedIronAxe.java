/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item.hijacked;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.item.hijacked.HijackedHitEffects;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHijackedIronAxe
extends ItemAxe {
    private final String name;
    private static final float ATTACK_DAMAGE = 7.5f;
    private static final float ATTACK_SPEED = -3.05f;

    public ItemHijackedIronAxe(String name, Item.ToolMaterial mat) {
        super(mat, 7.5f, -3.05f);
        this.name = name;
        RegistryNames.set(this, name);
        this.setUnlocalizedName("srparasites." + name);
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        HijackedHitEffects.apply(attacker, target);
        return super.hitEntity(stack, target, attacker);
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String key = "item.srparasites." + this.name + ".desc";
        String s = I18n.format((String)key, (Object[])new Object[0]);
        if (!s.equals(key)) {
            tooltip.add(s);
        }
    }
}

