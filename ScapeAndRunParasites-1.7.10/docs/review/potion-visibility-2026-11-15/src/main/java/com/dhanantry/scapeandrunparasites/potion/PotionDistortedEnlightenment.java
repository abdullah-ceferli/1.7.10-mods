/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.attributes.AbstractAttributeMap
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class PotionDistortedEnlightenment
extends SRPEffectBase {
    public PotionDistortedEnlightenment(String name, boolean isBadEffectIn, int liquidColorIn, int iconIndexX, int iconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, iconIndexX, iconIndexY);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        super.performEffect(entity, amplifier);
        if (!entity.worldObj.isRemote) {
            entity.setGlowing(true);
        }
    }

    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
        if (!entity.worldObj.isRemote && !entity.isPotionActive(SRPPotions.DISTORTED_ENLIGHTENMENT_E)) {
            entity.setGlowing(false);
        }
    }
}

