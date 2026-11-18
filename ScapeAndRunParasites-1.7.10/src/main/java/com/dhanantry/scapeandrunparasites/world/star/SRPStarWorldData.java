/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.MapStorage
 *  net.minecraft.world.storage.WorldSavedData
 */
package com.dhanantry.scapeandrunparasites.world.star;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class SRPStarWorldData
extends WorldSavedData {
    private static final String DATA_NAME = "srp_star_world_data";
    private int starType = 0;
    private boolean mushroomTreesEnabled = false;
    private boolean fracturedTerrainEnabled = false;

    public SRPStarWorldData() {
        super(DATA_NAME);
    }

    public SRPStarWorldData(String name) {
        super(name);
    }

    public static SRPStarWorldData get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        SRPStarWorldData data = (SRPStarWorldData)storage.loadData(SRPStarWorldData.class, DATA_NAME);
        if (data == null) {
            data = new SRPStarWorldData();
            storage.setData(DATA_NAME, (WorldSavedData)data);
            data.markDirty();
        }
        return data;
    }

    public int getStarType() {
        return this.starType;
    }

    public void setStarType(int starType) {
        if (starType < 0 || starType > 2) {
            starType = 0;
        }
        boolean changed = this.starType != starType;
        this.starType = starType;
        if (this.starType != 1 && this.fracturedTerrainEnabled) {
            this.fracturedTerrainEnabled = false;
            changed = true;
        }
        if (changed) {
            this.markDirty();
        }
    }

    public boolean isMushroomTreesEnabled() {
        return this.mushroomTreesEnabled;
    }

    public void setMushroomTreesEnabled(boolean enabled) {
        if (this.mushroomTreesEnabled != enabled) {
            this.mushroomTreesEnabled = enabled;
            this.markDirty();
        }
    }

    public boolean isFracturedTerrainEnabled() {
        return this.starType == 1 && this.fracturedTerrainEnabled;
    }

    public void setFracturedTerrainEnabled(boolean enabled) {
        boolean allowed;
        boolean bl = allowed = this.starType == 1 && enabled;
        if (this.fracturedTerrainEnabled != allowed) {
            this.fracturedTerrainEnabled = allowed;
            this.markDirty();
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.starType = nbt.getInteger("StarType");
        if (this.starType < 0 || this.starType > 2) {
            this.starType = 0;
        }
        this.mushroomTreesEnabled = nbt.hasKey("MushroomTrees") && nbt.getBoolean("MushroomTrees");
        this.fracturedTerrainEnabled = this.starType == 1 && nbt.hasKey("FracturedTerrain") && nbt.getBoolean("FracturedTerrain");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("StarType", this.starType);
        compound.setBoolean("MushroomTrees", this.mushroomTreesEnabled);
        compound.setBoolean("FracturedTerrain", this.fracturedTerrainEnabled);
        return compound;
    }
}

