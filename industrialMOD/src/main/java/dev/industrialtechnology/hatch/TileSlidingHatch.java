package dev.industrialtechnology.hatch;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

/** Non-ticking parts each retain enough state for collision, drawing and safe chunk reload. */
public final class TileSlidingHatch extends TileEntity {
    public int baseX,baseY,baseZ,variant;public boolean offsetX,offsetZ,upper,open,removing;
    @Override public boolean canUpdate(){return false;}
    @Override public void writeToNBT(NBTTagCompound n){super.writeToNBT(n);n.setInteger("BaseX",baseX);n.setInteger("BaseY",baseY);n.setInteger("BaseZ",baseZ);n.setInteger("Variant",variant);n.setBoolean("OffsetX",offsetX);n.setBoolean("OffsetZ",offsetZ);n.setBoolean("Upper",upper);n.setBoolean("Open",open);}
    @Override public void readFromNBT(NBTTagCompound n){super.readFromNBT(n);baseX=n.getInteger("BaseX");baseY=n.getInteger("BaseY");baseZ=n.getInteger("BaseZ");variant=Math.max(0,Math.min(2,n.getInteger("Variant")));offsetX=n.getBoolean("OffsetX");offsetZ=n.getBoolean("OffsetZ");upper=n.getBoolean("Upper");open=n.getBoolean("Open");}
    @Override public Packet getDescriptionPacket(){NBTTagCompound n=new NBTTagCompound();writeToNBT(n);return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,n);}
    @Override public void onDataPacket(NetworkManager manager,S35PacketUpdateTileEntity packet){readFromNBT(packet.func_148857_g());worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);}
    public void changed(){if(worldObj!=null){worldObj.getChunkFromBlockCoords(xCoord,zCoord).setChunkModified();worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);}}
    public boolean same(TileSlidingHatch t){return t!=null&&t.baseX==baseX&&t.baseY==baseY&&t.baseZ==baseZ&&t.offsetX==offsetX&&t.offsetZ==offsetZ&&t.variant==variant&&t.upper==upper;}
}
