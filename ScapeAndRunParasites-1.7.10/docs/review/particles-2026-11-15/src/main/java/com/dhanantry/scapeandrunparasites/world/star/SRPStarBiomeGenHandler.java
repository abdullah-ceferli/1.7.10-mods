/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.gen.layer.GenLayer
 *  net.minecraftforge.event.terraingen.WorldTypeEvent$InitBiomeGens
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.star.GenLayerSRPDynamicStar;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.event.terraingen.WorldTypeEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SRPStarBiomeGenHandler {
    @SubscribeEvent
    public void onInitBiomeGens(WorldTypeEvent.InitBiomeGens event) {
        GenLayer[] original = event.getOriginalBiomeGens();
        GenLayer[] replaced = new GenLayer[original.length];
        for (int i = 0; i < original.length; ++i) {
            if (original[i] == null) continue;
            replaced[i] = new GenLayerSRPDynamicStar(7231L + (long)i, original[i]);
            replaced[i].initWorldGenSeed(event.getSeed());
        }
        event.setNewBiomeGens(replaced);
    }
}

