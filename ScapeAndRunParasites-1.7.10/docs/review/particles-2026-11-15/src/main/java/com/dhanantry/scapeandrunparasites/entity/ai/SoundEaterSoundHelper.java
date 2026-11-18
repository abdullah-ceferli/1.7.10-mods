/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import java.util.List;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class SoundEaterSoundHelper {
    public static void broadcastSound(World world, BlockPos pos, double radius, int lifeTicks) {
        if (world == null || world.isRemote) {
            return;
        }
        AxisAlignedBB box = Boxes.grow(Boxes.create(pos), radius, radius, radius);
        List list = EntityQueries.entities(world, EntityInfHuman.class, box);
        for (EntityInfHuman human : list) {
            if (human.getSkin() != 111) continue;
            human.notifyHeardSound(pos, lifeTicks);
        }
    }
}

