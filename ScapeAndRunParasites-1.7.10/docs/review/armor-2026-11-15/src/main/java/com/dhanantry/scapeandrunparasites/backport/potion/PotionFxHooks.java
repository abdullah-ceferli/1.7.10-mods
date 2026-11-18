package com.dhanantry.scapeandrunparasites.backport.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

/**
 * Static hooks the runtime transformer must call. Both run on the server only.
 *  applied(...)  : in EntityLivingBase.addPotionEffect, after the isPotionApplicable check passed and the map changed in either
 *                  branch, before the virtual onChanged/onNew callback (preserving nested application order).
 *  finished(...) : at the start of EntityLivingBase.onFinishedPotionEffect, which the engine calls for expiry,
 *                  removePotionEffect and clearActivePotions.
 */
public final class PotionFxHooks {
    private PotionFxHooks() {}

    public static void applied(EntityLivingBase entity, PotionEffect incoming) {
        if (entity.worldObj == null || entity.worldObj.isRemote) return;
        PotionVisibility v = PotionVisibility.get(entity);
        if (v == null) return;
        boolean show = !(incoming instanceof SRPPotionEffect) || ((SRPPotionEffect) incoming).doesShowParticles();
        v.set(incoming.getPotionID(), !show);
    }

    public static void finished(EntityLivingBase entity, PotionEffect ended) {
        if (entity.worldObj == null || entity.worldObj.isRemote) return;
        PotionVisibility v = PotionVisibility.get(entity);
        if (v != null) v.set(ended.getPotionID(), false);
    }
}
