/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import net.minecraft.entity.EntityLivingBase;

public class PotionFoster
extends SRPEffectBase {
    public PotionFoster(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, IconIndexX, IconIndexY);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.worldObj.isRemote) {
            return;
        }
        if (entity instanceof EntityPMalleable) {
            ((EntityPMalleable)entity).increaseAllResistances();
        }
    }
}

