/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.world.WorldServer
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import net.minecraft.entity.EntityLivingBase;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.world.WorldServer;

public class EffectDodSmokeTrail
extends SRPEffectBase {
    public EffectDodSmokeTrail() {
        super("dod_smoke_trail", false, 0x404040, 0, 0);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (!(entity.worldObj instanceof WorldServer)) {
            return;
        }
        int remaining = entity.getActivePotionEffect(SRPPotions.DOD_SMOKE_TRAIL_E).getDuration();
        if (entity.onGround && remaining <= 10) {
            entity.removeActivePotionEffect(SRPPotions.DOD_SMOKE_TRAIL_E);
            return;
        }
        WorldServer ws = (WorldServer)entity.worldObj;
        double x = entity.posX;
        double y = entity.posY + (double)entity.getEyeHeight();
        double z = entity.posZ;
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.SMOKE_NORMAL, false, x, y, z, 6, 0.15, 0.15, 0.15, 0.02, new int[0]);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}

