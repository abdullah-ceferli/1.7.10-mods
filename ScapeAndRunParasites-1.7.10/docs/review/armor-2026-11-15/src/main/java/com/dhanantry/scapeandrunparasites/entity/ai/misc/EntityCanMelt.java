/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface EntityCanMelt {
    public void melt();

    public boolean isMelting();

    public void melting();

    public float getTHeigh();

    public void setTHeigh(float var1);

    public float getaSize();

    public void setaSize(float var1);

    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity2();
}

