/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.celestial.effects;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.world.celestial.ICelestialEventEffect;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

public class EffectTwentySeven
implements ICelestialEventEffect {
    private static final int DURATION = 12000;

    @Override
    public void onNightStart(World world, int dim, int phase, long nightIndex) {
        for (EntityParasiteBase parasite : world.getEntities(EntityParasiteBase.class, EntitySelectors.selectAnything)) {
            parasite.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 12000, 1, false, false));
        }
    }

    @Override
    public void onParasiteSpawn(EntityParasiteBase parasite, @Nullable EntityLivingBase spawner) {
        parasite.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 12000, 1, false, false));
    }
}

