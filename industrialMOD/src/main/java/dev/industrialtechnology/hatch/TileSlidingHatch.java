package dev.industrialtechnology.hatch;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import cpw.mods.fml.relauncher.*;

/** No idle tick: both collision and client animation use the synchronized world clock. */
public final class TileSlidingHatch extends TileEntity {
    public int baseX,baseY,baseZ,variant; public boolean offsetX,offsetZ,upper,open,removing;
    public Block backing=Blocks.air; public int backingMeta;
    public float motionFrom; public long motionStart;
    @Override public boolean canUpdate(){return false;}
    public float progress(float partial){return worldObj==null?(open?1:0):HatchGeometry.progress(motionFrom,open,worldObj.getTotalWorldTime()+partial-motionStart);}
    public boolean moving(){return worldObj!=null&&worldObj.getTotalWorldTime()-motionStart<HatchGeometry.MOTION_TICKS&&Math.abs(motionFrom-(open?1:0))>.0001;}
    public void move(boolean target,float from,long start){open=target;motionFrom=from;motionStart=start;changed();}
    @Override public void writeToNBT(NBTTagCompound n){super.writeToNBT(n);n.setInteger("BaseX",baseX);n.setInteger("BaseY",baseY);n.setInteger("BaseZ",baseZ);n.setInteger("Variant",variant);n.setBoolean("OffsetX",offsetX);n.setBoolean("OffsetZ",offsetZ);n.setBoolean("Upper",upper);n.setBoolean("Open",open);n.setFloat("MotionFrom",motionFrom);n.setLong("MotionStart",motionStart);n.setString("FloorBlock",Block.blockRegistry.getNameForObject(backing));n.setInteger("FloorMeta",backingMeta);}
    @Override public void readFromNBT(NBTTagCompound n){super.readFromNBT(n);baseX=n.getInteger("BaseX");baseY=n.getInteger("BaseY");baseZ=n.getInteger("BaseZ");variant=Math.max(0,Math.min(2,n.getInteger("Variant")));offsetX=n.getBoolean("OffsetX");offsetZ=n.getBoolean("OffsetZ");upper=n.getBoolean("Upper");open=n.getBoolean("Open");motionFrom=n.hasKey("MotionFrom")?n.getFloat("MotionFrom"):(open?1:0);motionFrom=Math.max(0,Math.min(1,motionFrom));motionStart=n.getLong("MotionStart");backing=Block.getBlockFromName(n.getString("FloorBlock"));if(backing==null||backing==HatchRegistry.block)backing=Blocks.air;backingMeta=n.getInteger("FloorMeta")&15;}
    @Override public Packet getDescriptionPacket(){NBTTagCompound n=new NBTTagCompound();writeToNBT(n);return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,n);}
    @Override public void onDataPacket(NetworkManager manager,S35PacketUpdateTileEntity packet){readFromNBT(packet.func_148857_g());worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);}
    public void changed(){if(worldObj!=null){markDirty();worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);}}
    public boolean same(TileSlidingHatch t){return t!=null&&t.baseX==baseX&&t.baseY==baseY&&t.baseZ==baseZ&&t.offsetX==offsetX&&t.offsetZ==offsetZ&&t.variant==variant&&t.upper==upper;}
    @Override @SideOnly(Side.CLIENT) public net.minecraft.util.AxisAlignedBB getRenderBoundingBox(){double x=baseX+(offsetX?.5:0),z=baseZ+(offsetZ?.5:0);return net.minecraft.util.AxisAlignedBB.getBoundingBox(x,baseY,z,x+1,baseY+1.01,z+1);}
    @Override @SideOnly(Side.CLIENT) public boolean shouldRenderInPass(int pass){if(pass!=0)return false;if(xCoord==baseX&&zCoord==baseZ)return true;return !BlockSlidingHatch.loaded(worldObj,baseX,baseY,baseZ);}
}
