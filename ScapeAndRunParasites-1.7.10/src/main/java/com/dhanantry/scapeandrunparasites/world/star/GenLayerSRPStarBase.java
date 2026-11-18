/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.gen.layer.GenLayer
 */
package com.dhanantry.scapeandrunparasites.world.star;

import net.minecraft.world.gen.layer.GenLayer;

public abstract class GenLayerSRPStarBase
extends GenLayer {
    public GenLayerSRPStarBase(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }
}

