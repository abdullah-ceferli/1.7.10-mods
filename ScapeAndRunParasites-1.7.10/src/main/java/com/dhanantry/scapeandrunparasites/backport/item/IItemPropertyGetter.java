package com.dhanantry.scapeandrunparasites.backport.item;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Property evaluation retains the original stack and optional world/living context. */
@FunctionalInterface
public interface IItemPropertyGetter {
    float apply(ItemStack stack, World world, EntityLivingBase entity);
}
