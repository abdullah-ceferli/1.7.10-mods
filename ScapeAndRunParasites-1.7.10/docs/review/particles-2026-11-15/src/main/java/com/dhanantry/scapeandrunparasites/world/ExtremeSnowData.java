/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.MapStorage
 *  net.minecraft.world.storage.WorldSavedData
 */
package com.dhanantry.scapeandrunparasites.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ExtremeSnowData
extends WorldSavedData {
    private static final String KEY = "srp_extreme_snow";
    private boolean enabled = false;
    private float intensity = 1.0f;
    private boolean forceAnywhere = true;
    private float windDeg = 30.0f;
    private float windSpeed = 0.5f;

    public ExtremeSnowData() {
        super(KEY);
    }

    public ExtremeSnowData(String name) {
        super(name);
    }

    public static ExtremeSnowData get(World world) {
        String dimKey;
        MapStorage storage = world.getPerWorldStorage();
        ExtremeSnowData data = (ExtremeSnowData)storage.loadData(ExtremeSnowData.class, dimKey = "srp_extreme_snow_" + world.provider.dimensionId);
        if (data == null) {
            data = new ExtremeSnowData(dimKey);
            storage.setData(dimKey, (WorldSavedData)data);
        }
        return data;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public void setEnabled(boolean e) {
        this.enabled = e;
    }

    public void setIntensity(float i) {
        this.intensity = i;
    }

    public boolean isForceAnywhere() {
        return this.forceAnywhere;
    }

    public void setForceAnywhere(boolean b) {
        this.forceAnywhere = b;
    }

    public float getWindDeg() {
        return this.windDeg;
    }

    public void setWindDeg(float d) {
        this.windDeg = d;
    }

    public float getWindSpeed() {
        return this.windSpeed;
    }

    public void setWindSpeed(float s) {
        this.windSpeed = s;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.enabled = nbt.getBoolean("enabled");
        this.intensity = nbt.getFloat("intensity");
        this.forceAnywhere = nbt.getBoolean("forceAnywhere");
        this.windDeg = nbt.getFloat("windDeg");
        this.windSpeed = nbt.getFloat("windSpeed");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("enabled", this.enabled);
        nbt.setFloat("intensity", this.intensity);
        nbt.setBoolean("forceAnywhere", this.forceAnywhere);
        nbt.setFloat("windDeg", this.windDeg);
        nbt.setFloat("windSpeed", this.windSpeed);
        return nbt;
    }
}

