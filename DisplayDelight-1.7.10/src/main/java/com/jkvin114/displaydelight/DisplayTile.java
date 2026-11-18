package com.jkvin114.displaydelight;
import net.minecraft.tileentity.TileEntity;import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;import net.minecraft.network.NetworkManager;import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

/** Count/plate flags exceed the four native metadata bits. This tile never ticks. */
public final class DisplayTile extends TileEntity {
    public int count=1;public boolean hidden;
    @Override public boolean canUpdate(){return false;}
    public static DisplayTile at(net.minecraft.world.IBlockAccess w,int x,int y,int z){TileEntity t=w.getTileEntity(x,y,z);return t instanceof DisplayTile?(DisplayTile)t:null;}
    public void set(int value,boolean hide){
        int max=getBlockType() instanceof DisplayBlock?((DisplayBlock)getBlockType()).definition.max:10;
        count=Math.max(1,Math.min(max,value));hidden=hide;markDirty();if(worldObj!=null)worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }
    @Override public void writeToNBT(NBTTagCompound n){super.writeToNBT(n);n.setInteger("FoodCount",count);n.setBoolean("PlateHidden",hidden);}
    @Override public void readFromNBT(NBTTagCompound n){super.readFromNBT(n);count=Math.max(1,Math.min(10,n.getInteger("FoodCount")));hidden=n.getBoolean("PlateHidden");}
    @Override public Packet getDescriptionPacket(){NBTTagCompound n=new NBTTagCompound();writeToNBT(n);return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,1,n);}
    @Override public void onDataPacket(NetworkManager manager,S35PacketUpdateTileEntity packet){readFromNBT(packet.func_148857_g());worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);}
}
