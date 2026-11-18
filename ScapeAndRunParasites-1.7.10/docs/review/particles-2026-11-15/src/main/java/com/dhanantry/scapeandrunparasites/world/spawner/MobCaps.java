/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.spawner;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.World;

public class MobCaps {
    Set<MobCap> mobCaps = new HashSet<MobCap>();

    public int getMobCapForDimension(int dimensionId) {
        for (MobCap mobCap : this.mobCaps) {
            if (mobCap.getDimensionId() != dimensionId) continue;
            return mobCap.getMobCap();
        }
        return SRPConfig.worldMobCap;
    }

    public void initializeMobCaps(Set<Integer> dimensionIds) {
        for (int dimensionId : dimensionIds) {
            this.mobCaps.add(new MobCap(SRPConfig.worldMobCap, dimensionId));
        }
    }

    public void initializeMobCapsForWorlds(Set<World> worlds) {
        for (World world : worlds) {
            this.mobCaps.add(new MobCap(SRPConfig.worldMobCap, world.provider.dimensionId));
        }
    }

    public void updateMobCapForDimension(int dimensionId, World world) {
        for (MobCap mobCap : this.mobCaps) {
            if (mobCap.getDimensionId() != dimensionId) continue;
            mobCap.updateMobCap(world);
            return;
        }
        MobCap newMobCap = new MobCap(SRPConfig.worldMobCap, dimensionId);
        newMobCap.updateMobCap(world);
        this.mobCaps.add(newMobCap);
    }

    public void updateAllMobCaps(Set<World> worlds) {
        for (World world : worlds) {
            this.updateMobCapForDimension(world.provider.dimensionId, world);
        }
    }

    public static class MobCap {
        private int mobCap;
        private int dimensionId;

        public MobCap(int mobCap, int dimensionId) {
            this.mobCap = mobCap;
            this.dimensionId = dimensionId;
        }

        public void updateMobCap(World world) {
            int playerCount = EntityQueries.players(world).size();
            this.setMobCap(SRPConfig.worldMobCap + playerCount * SRPConfig.worldMobCapPlusPlayer);
        }

        public int getDimensionId() {
            return this.dimensionId;
        }

        public void setDimensionId(int dimensionId) {
            this.dimensionId = dimensionId;
        }

        public int getMobCap() {
            return this.mobCap;
        }

        public void setMobCap(int mobCap) {
            this.mobCap = mobCap;
        }
    }
}

