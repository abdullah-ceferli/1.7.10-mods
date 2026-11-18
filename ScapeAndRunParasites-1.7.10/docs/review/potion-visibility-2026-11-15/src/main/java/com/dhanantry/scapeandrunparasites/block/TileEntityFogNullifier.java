/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 */
package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityFogNullifier
extends TileEntity {
    private int usesRemaining = 0;

    public int getUsesRemaining() {
        return this.usesRemaining;
    }

    public void setUsesRemaining(int uses) {
        this.usesRemaining = Math.max(0, uses);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("UsesRemaining", this.usesRemaining);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.usesRemaining = compound.getInteger("UsesRemaining");
    }
}

