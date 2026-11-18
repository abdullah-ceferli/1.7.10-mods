/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNexusProtection2;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityPBeckon
extends EntityPStationaryArchitect {
    int lifeLeftBeckon = -1000;

    public EntityPBeckon(World worldIn) {
        super(worldIn);
        this.setScentHPMultiplier(1.0f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.lifeLeftBeckon > 0) {
            --this.lifeLeftBeckon;
            if (this.lifeLeftBeckon <= 0 && this.lifeLeftBeckon > -500) {
                this.attackEntityFrom(DamageSource.outOfWorld, 5000000.0f);
            }
        }
    }

    public void setLifeB(int in) {
        this.lifeLeftBeckon = in;
    }

    public int getLifeB() {
        return this.lifeLeftBeckon;
    }

    @Override
    public void generateStructure() {
        if (SRPConfig.nexusStructures) {
            WorldGenParasiteNexusProtection2 p1 = new WorldGenParasiteNexusProtection2(false, 1);
            p1.generate(this.worldObj, new Random(), EntityQueries.position(this));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("beckonlifeleft", this.lifeLeftBeckon);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("beckonlifeleft", 99)) {
            this.lifeLeftBeckon = compound.getInteger("beckonlifeleft");
        }
    }
}

