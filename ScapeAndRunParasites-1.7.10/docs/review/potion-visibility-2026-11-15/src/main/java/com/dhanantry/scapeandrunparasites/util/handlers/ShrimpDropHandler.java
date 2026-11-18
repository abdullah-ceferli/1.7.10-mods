/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.world.CelestialNightData;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ShrimpDropHandler {
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLivingBase)) {
            return;
        }
        if (event.getEntity().worldObj == null || event.getEntity().worldObj.isRemote) {
            return;
        }
        EntityLivingBase entity = event.getEntityLiving();
        if (!this.isSRPMob(entity)) {
            return;
        }
        if (!this.isArrowCelestialActive(entity)) {
            return;
        }
        Random rand = entity.getRNG();
        if (rand.nextFloat() >= 0.25f) {
            return;
        }
        int count = 1 + rand.nextInt(5);
        ItemStack stack = new ItemStack(SRPItems.shrimp, count);
        BlockPos pos = EntityQueries.position(entity);
        EntityItem drop = new EntityItem(entity.worldObj, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, stack);
        event.getDrops().add(drop);
    }

    private boolean isSRPMob(EntityLivingBase entity) {
        return entity instanceof EntityParasiteBase;
    }

    private boolean isArrowCelestialActive(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) {
            return false;
        }
        if (!entity.worldObj.provider.isSurfaceWorld()) {
            return false;
        }
        int dim = entity.worldObj.provider.dimensionId;
        CelestialNightData nightData = CelestialNightData.get(entity.worldObj);
        if (nightData == null) {
            return false;
        }
        CelestialNightData.DimState state = nightData.getOrCreate(dim);
        return state.active.contains("arrow") || state.forced.contains("arrow");
    }
}

