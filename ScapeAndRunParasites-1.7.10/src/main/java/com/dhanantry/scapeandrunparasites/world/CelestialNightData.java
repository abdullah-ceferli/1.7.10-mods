/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.MapStorage
 *  net.minecraft.world.storage.WorldSavedData
 */
package com.dhanantry.scapeandrunparasites.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class CelestialNightData
extends WorldSavedData {
    public static final String DATA_NAME = "srp_celestial_night";
    public long darkDaysEndTime = -1L;
    public boolean darkDaysEndingSoundPlayed = false;
    private final Map<Integer, DimState> states = new HashMap<Integer, DimState>();

    public CelestialNightData() {
        super(DATA_NAME);
    }

    public CelestialNightData(String name) {
        super(name);
    }

    public static CelestialNightData get(World world) {
        MapStorage storage = world.getMapStorage();
        CelestialNightData data = (CelestialNightData)storage.loadData(CelestialNightData.class, DATA_NAME);
        if (data == null) {
            data = new CelestialNightData(DATA_NAME);
            storage.setData(DATA_NAME, (WorldSavedData)data);
        }
        return data;
    }

    public DimState getOrCreate(int dim) {
        DimState s = this.states.get(dim);
        if (s == null) {
            s = new DimState();
            this.states.put(dim, s);
        }
        return s;
    }

    public DimState getState(int dim) {
        return this.states.get(dim);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.states.clear();
        NBTTagList list = nbt.getTagList("dims", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound d = list.getCompoundTagAt(i);
            int dim = d.getInteger("dim");
            DimState s = new DimState();
            s.nightIndex = d.getLong("night");
            NBTTagList active = d.getTagList("active", 8);
            for (int j = 0; j < active.tagCount(); ++j) {
                s.active.add(active.getStringTagAt(j));
            }
            NBTTagList forced = d.getTagList("forced", 8);
            for (int j = 0; j < forced.tagCount(); ++j) {
                s.forced.add(forced.getStringTagAt(j));
            }
            this.states.put(dim, s);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, DimState> e : this.states.entrySet()) {
            NBTTagCompound d = new NBTTagCompound();
            d.setInteger("dim", e.getKey().intValue());
            DimState s = e.getValue();
            d.setLong("night", s.nightIndex);
            NBTTagList active = new NBTTagList();
            for (String id : s.active) {
                active.appendTag((NBTBase)new NBTTagString(id));
            }
            d.setTag("active", (NBTBase)active);
            NBTTagList forced = new NBTTagList();
            for (String id : s.forced) {
                forced.appendTag((NBTBase)new NBTTagString(id));
            }
            d.setTag("forced", (NBTBase)forced);
            list.appendTag((NBTBase)d);
        }
        nbt.setTag("dims", (NBTBase)list);
        return nbt;
    }

    public static class DimState {
        public long nightIndex = -1L;
        public final Set<String> active = new HashSet<String>();
        public final Set<String> forced = new HashSet<String>();
    }
}

