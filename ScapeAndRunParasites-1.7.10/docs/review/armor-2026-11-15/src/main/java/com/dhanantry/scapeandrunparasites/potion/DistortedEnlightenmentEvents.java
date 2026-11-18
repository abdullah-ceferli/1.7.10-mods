/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class DistortedEnlightenmentEvents {
    private DistortedEnlightenmentEvents() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (victim == null || victim.worldObj.isRemote) {
            return;
        }
        Entity trueSource = event.getSource().getEntity();
        if (!(trueSource instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase)trueSource;
        boolean victimHasEffect = victim.isPotionActive(SRPPotions.DISTORTED_ENLIGHTENMENT_E);
        boolean attackerHasEffect = attacker.isPotionActive(SRPPotions.DISTORTED_ENLIGHTENMENT_E);
        float amount = event.getAmount();
        if (victimHasEffect && DistortedEnlightenmentEvents.isSRPParasite((Entity)attacker)) {
            amount *= DistortedEnlightenmentEvents.isDraconite((Entity)attacker) ? 5.0f : 0.8f;
        }
        if (attackerHasEffect && DistortedEnlightenmentEvents.isSRPParasite((Entity)victim)) {
            amount *= 0.8f;
        }
        event.setAmount(amount);
    }

    private static boolean isSRPParasite(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity instanceof EntityParasiteBase;
    }

    private static boolean isDraconite(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity instanceof EntityHeblu;
    }
}

