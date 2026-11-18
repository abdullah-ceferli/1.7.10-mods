/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.item.ItemCompass;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.entity.Entity;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class CompassColony
extends ItemCompass {
    public static BlockPos orig;

    public CompassColony(String name) {
        super(name, 2);
    }

    @Override
    public BlockPos getOrigin(Entity entityIn, World worldIn) {
        SRPWorldData aaa = SRPWorldData.get(worldIn);
        return aaa.nearestColonyPosition(EntityQueries.position(entityIn), true);
    }

    @Override
    public BlockPos getOri() {
        return orig;
    }
}

