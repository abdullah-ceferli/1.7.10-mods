package com.epherical.croptopia.legacy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
public final class GardenTile extends TileEntity {
    public int remaining=4,facing;
    public void changed(){markDirty();if(worldObj!=null)worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);}
    public boolean canUpdate(){return false;}
    public void writeToNBT(NBTTagCompound tag){super.writeToNBT(tag);tag.setInteger("Servings",remaining);tag.setInteger("Facing",facing);}
    public void readFromNBT(NBTTagCompound tag){super.readFromNBT(tag);remaining=Math.max(0,Math.min(8,tag.getInteger("Servings")));facing=tag.getInteger("Facing")&3;}
    public Packet getDescriptionPacket(){NBTTagCompound n=new NBTTagCompound();writeToNBT(n);return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,1,n);}
    public void onDataPacket(NetworkManager net,S35PacketUpdateTileEntity packet){readFromNBT(packet.func_148857_g());if(worldObj!=null)worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);}
}
