/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.world.WorldServer
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.world.WorldServer;

public class PotionBleed
extends SRPEffectBase {
    public PotionBleed(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, IconIndexX, IconIndexY);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.worldObj.isRemote) {
            return;
        }
        WorldServer ws = (WorldServer)entity.worldObj;
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.DAMAGE_INDICATOR, false, entity.posX, entity.posY, entity.posZ, 1, 0.15, 0.3, 0.15, 0.0, new int[0]);
        float damage = entity.getMaxHealth() * SRPConfigSystems.bleedingDamage;
        if (entity.posX != entity.prevPosX || entity.posZ != entity.prevPosZ) {
            damage *= (float)(amplifier + 1);
        }
        damage = Math.min(damage, SRPConfigSystems.bleedingDamageCap);
        entity.attackEntityFrom(DamageSource.magic, damage);
    }
}

