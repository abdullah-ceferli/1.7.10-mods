/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 */
package com.dhanantry.scapeandrunparasites.item.hijacked;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public final class HijackedHitEffects {
    public static final int BLEED_TICKS = 100;
    public static final int BLEED_AMP = 0;
    public static final int RAGE_TICKS = 60;
    public static final int RAGE_AMP = 0;
    public static final float PARASITE_BONUS_DAMAGE = 3.0f;

    private HijackedHitEffects() {
    }

    public static void apply(EntityLivingBase attacker, EntityLivingBase target) {
        float max;
        if (target == null || target.isDead) {
            return;
        }
        target.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.BLEED_E, 100, 0, false, true));
        if (target instanceof EntityParasiteBase) {
            DamageSource src = attacker instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer)((EntityPlayer)attacker)) : DamageSource.causeMobDamage((EntityLivingBase)attacker);
            target.attackEntityFrom(src, 3.0f);
        }
        if ((max = target.getMaxHealth()) > 0.0f && target.getHealth() <= max * 0.1f) {
            target.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 60, 0, false, true));
        }
    }
}

