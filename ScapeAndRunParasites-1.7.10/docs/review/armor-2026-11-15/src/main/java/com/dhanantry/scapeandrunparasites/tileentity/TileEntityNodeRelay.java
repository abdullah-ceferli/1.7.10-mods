/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTUtil
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.play.server.SPacketUpdateTileEntity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 */
package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

public class TileEntityNodeRelay
extends TileEntity {
    @Nullable
    private BlockPos controllerPos;

    public void setControllerPos(@Nullable BlockPos pos) {
        this.controllerPos = pos;
        this.markDirty();
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.notifyBlockUpdate(pos == null ? this.getPos() : this.getPos(), BlockStates.get(this.worldObj, this.getPos()), BlockStates.get(this.worldObj, this.getPos()), 3);
        }
    }

    @Nullable
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.controllerPos != null) {
            nbt.setTag("ControllerPos", (NBTBase)NBTUtil.createPosTag((BlockPos)this.controllerPos));
        }
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.controllerPos = nbt.hasKey("ControllerPos", 10) ? NBTUtil.getPosFromTag((NBTTagCompound)nbt.getCompoundTag("ControllerPos")) : null;
    }

    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }
}

