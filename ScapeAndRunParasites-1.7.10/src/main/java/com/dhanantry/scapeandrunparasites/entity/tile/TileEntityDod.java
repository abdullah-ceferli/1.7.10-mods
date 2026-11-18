/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.math.AxisAlignedBB
 */
package com.dhanantry.scapeandrunparasites.entity.tile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityDod
extends TileEntity
implements ITickable {
    private int ticksSinceSync;
    private int killC;

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("parasitekillc", 3)) {
            this.killC = compound.getInteger("parasitekillc");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("parasitekillc", this.killC);
        return compound;
    }

    public void update() {
        if (!this.worldObj.isRemote) {
            if (BlockStates.get(this.worldObj, this.pos).getBlock() == Blocks.air) {
                this.killSelfTile();
                return;
            }
            ++this.ticksSinceSync;
            if (this.ticksSinceSync % 20 != 0) {
                return;
            }
            double coll = 0.0;
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1)), 16.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
            for (Object mob : moblist) {
                if (!(((EntityParasiteBase)mob).getKillC() > 1.0)) continue;
                coll += ((EntityParasiteBase)mob).getKillC();
                ((EntityParasiteBase)mob).setKillC(0.0);
            }
            if (coll != 0.0) {
                this.killC += (int)coll;
            }
            if (this.killC > SRPConfig.reinforcerNidusValue) {
                assert (Blocks.air != null);
                int ccc = 0;
                axisalignedbb = Boxes.grow(Boxes.create((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1)), SRPConfig.turretFollow);
                moblist = EntityQueries.entities(this.worldObj, EntityPStationaryArchitect.class, axisalignedbb);
                for (EntityParasiteBase mob : moblist) {
                    if (!(mob instanceof EntityDod) && !(mob instanceof EntityDodSII) && !(mob instanceof EntityDodSIII) && !(mob instanceof EntityDodSIV)) continue;
                    ++ccc;
                }
                if (ccc == 0) {
                    List serverList = EntityQueries.loaded(this.worldObj);
                    int count = 0;
                    for (int x = 0; x < serverList.size(); ++x) {
                        if (!(serverList.get(x) instanceof EntityPDispatcher) || ++count <= SRPConfig.nexusDodCap && !(this.getDistanceSq((Entity)serverList.get(x)) < (double)(SRPConfig.nexusDodDis * SRPConfig.nexusDodDis))) continue;
                        if (SRPConfigSystems.useEvolution) {
                            SRPSaveData data = SRPSaveData.get(this.worldObj, 49);
                            data.setTotalKills(this.worldObj.provider.dimensionId, this.killC * SRPConfig.reinforcerNidusMult, true, this.worldObj, true, 45);
                        }
                        return;
                    }
                    if (SRPConfigSystems.useEvolution) {
                        SRPSaveData data = SRPSaveData.get(this.worldObj, 47);
                        if (ParasiteEventEntity.getRSchance(this.worldObj) == 0.0) {
                            data.setTotalKills(this.worldObj.provider.dimensionId, this.killC * SRPConfig.reinforcerNidusMult, true, this.worldObj, true, 46);
                            BlockStates.set(this.worldObj, this.getPos(), BlockStates.defaultState(Blocks.air));
                            this.killSelfTile();
                            return;
                        }
                    }
                    EntityDod dod = new EntityDod(this.worldObj);
                    dod.setPosition((double)this.pos.getX() + 0.5, this.pos.getY() + 1, (double)this.pos.getZ() + 0.5);
                    if (!dod.worldObj.getCollisionBoxes((Entity)dod, Boxes.entityBox(dod)).isEmpty()) {
                        dod.setDead();
                    } else {
                        this.worldObj.spawnEntityInWorld((Entity)dod);
                        dod.particleStatus((byte)7);
                        if (SRPConfigSystems.rsSounds) {
                            if (SRPConfigSystems.disloGrowlNoise) {
                                if (SRPSaveData.get(this.worldObj, 48).getCurrentCode(this.worldObj.provider.dimensionId, 15) == 0) {
                                    com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(dod, SRPSounds.DODSI, 4.0f, 1.0f);
                                }
                            } else {
                                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(dod, SRPSounds.DODSI, 4.0f, 1.0f);
                            }
                        }
                    }
                }
                BlockStates.set(this.worldObj, this.getPos(), BlockStates.defaultState(Blocks.air));
                this.killSelfTile();
                return;
            }
        }
    }

    private void killSelfTile() {
        if (this.worldObj == null) {
            return;
        }
        this.invalidate();
        WorldAccess.removeTileEntity(this.worldObj, this.pos);
    }

    public double getDistanceSq(Entity entityIn) {
        double d0 = (double)this.pos.getX() - entityIn.posX;
        double d1 = (double)this.pos.getY() - entityIn.posY;
        double d2 = (double)this.pos.getZ() - entityIn.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
}

