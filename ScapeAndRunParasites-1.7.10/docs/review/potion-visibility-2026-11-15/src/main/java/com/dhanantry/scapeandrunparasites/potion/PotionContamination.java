/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;

public class PotionContamination
extends SRPEffectBase {
    public PotionContamination(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, IconIndexX, IconIndexY);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.worldObj.isRemote) {
            return;
        }
        if (entity.ticksExisted % 40 == 0) {
            if (entity.getHealth() > 1.0f) {
                entity.attackEntityFrom(DamageSource.magic, 1.0f);
            }
            int dur = entity.getActivePotionEffect(SRPPotions.CONTA_E).getDuration();
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0, entity.posY + 1.0, entity.posZ + 1.0), 4.0, 3.0, 4.0);
            List moblist = EntityQueries.entities(entity.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                SRPPotions.applyStackPotion(SRPPotions.CONTA_E, mob, dur, amplifier);
            }
        }
    }
}

