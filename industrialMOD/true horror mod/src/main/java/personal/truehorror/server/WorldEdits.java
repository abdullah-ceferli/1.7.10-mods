package personal.truehorror.server;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import personal.truehorror.HorrorConfig;

/** Persisted conditional undo: never loads chunks and never replaces player-edited results. */
public final class WorldEdits extends WorldSavedData {
    private final List<NBTTagCompound> edits=new ArrayList<NBTTagCompound>();
    public WorldEdits(){super("truehorror_edits");}
    public WorldEdits(String name){super(name);}
    public static WorldEdits get(World world){
        WorldEdits data=(WorldEdits)world.perWorldStorage.loadData(WorldEdits.class,"truehorror_edits");
        if(data==null){data=new WorldEdits();world.perWorldStorage.setData("truehorror_edits",data);}return data;
    }
    public boolean apply(World world,int x,int y,int z,Block block,int meta,int seconds){
        if(!HorrorConfig.worldChanges||edits.size()>=64||!SafeSpawns.loadedRectangle(world,x-2,z-2,x+2,z+2)||world.getTileEntity(x,y,z)!=null)return false;
        NBTTagCompound edit=new NBTTagCompound();edit.setInteger("x",x);edit.setInteger("y",y);edit.setInteger("z",z);
        edit.setString("old",Block.blockRegistry.getNameForObject(world.getBlock(x,y,z)));
        edit.setInteger("oldMeta",world.getBlockMetadata(x,y,z));
        edit.setString("result",Block.blockRegistry.getNameForObject(block));edit.setInteger("resultMeta",meta);
        edit.setLong("expires",world.getTotalWorldTime()+Math.max(1,seconds)*20L);
        if(!world.setBlock(x,y,z,block,meta,3))return false;
        edit.setString("result",Block.blockRegistry.getNameForObject(world.getBlock(x,y,z)));
        edit.setInteger("resultMeta",world.getBlockMetadata(x,y,z));
        edits.add(edit);refreshExpected(world,x,y,z);markDirty();return true;
    }
    public void refreshExpected(World world,int x,int y,int z){
        for(NBTTagCompound edit:edits)if(edit.getInteger("x")==x&&edit.getInteger("y")==y&&edit.getInteger("z")==z){
            TileEntity tile=world.getTileEntity(x,y,z);
            if(tile!=null){NBTTagCompound nbt=new NBTTagCompound();tile.writeToNBT(nbt);edit.setTag("expectedTile",nbt);}
        }
        markDirty();
    }
    public void restoreExpired(World world){
        int examined=0;
        for(Iterator<NBTTagCompound> it=edits.iterator();it.hasNext()&&examined++<64;){
            NBTTagCompound e=it.next();if(e.getLong("expires")>world.getTotalWorldTime())continue;
            int x=e.getInteger("x"),y=e.getInteger("y"),z=e.getInteger("z");
            if(!SafeSpawns.loadedRectangle(world,x-2,z-2,x+2,z+2))continue;
            boolean unchanged=world.getBlock(x,y,z)==Block.getBlockFromName(e.getString("result"))&&world.getBlockMetadata(x,y,z)==e.getInteger("resultMeta");
            TileEntity tile=world.getTileEntity(x,y,z);
            if(e.hasKey("expectedTile")){
                NBTTagCompound current=new NBTTagCompound();if(tile!=null)tile.writeToNBT(current);
                unchanged&=tile!=null&&current.equals(e.getCompoundTag("expectedTile"));
            }else unchanged&=tile==null;
            Block old=Block.getBlockFromName(e.getString("old"));
            if(unchanged&&old!=null)world.setBlock(x,y,z,old,e.getInteger("oldMeta"),3);
            it.remove();markDirty();
        }
    }
    public int pending(){return edits.size();}
    @Override public void readFromNBT(NBTTagCompound nbt){
        edits.clear();NBTTagList list=nbt.getTagList("edits",10);
        for(int i=0;i<Math.min(64,list.tagCount());i++)edits.add(list.getCompoundTagAt(i));
    }
    @Override public void writeToNBT(NBTTagCompound nbt){NBTTagList list=new NBTTagList();for(NBTTagCompound edit:edits)list.appendTag(edit);nbt.setTag("edits",list);}
}
