package com.dhanantry.scapeandrunparasites.backport.math;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import net.minecraft.entity.Entity;
import java.util.*;
/** Block coordinates and original 26/12/26-bit persistence format. */
public class BlockPos extends Vec3i {
    public static final BlockPos ORIGIN=new BlockPos(0,0,0);
    public BlockPos(int x,int y,int z){super(x,y,z);}public BlockPos(double x,double y,double z){super(x,y,z);}
    public BlockPos(Entity e){this(e.posX,e.posY,e.posZ);}public BlockPos(Vec3i p){this(p.getX(),p.getY(),p.getZ());}public BlockPos(Vec3d v){this(v.xCoord,v.yCoord,v.zCoord);}
    public BlockPos add(int x,int y,int z){return x==0&&y==0&&z==0?this:new BlockPos(getX()+x,getY()+y,getZ()+z);}
    public BlockPos add(double x,double y,double z){return x==0&&y==0&&z==0?this:new BlockPos(getX()+x,getY()+y,getZ()+z);}
    public BlockPos add(Vec3i v){return add(v.getX(),v.getY(),v.getZ());}public BlockPos subtract(Vec3i v){return add(-v.getX(),-v.getY(),-v.getZ());}
    public BlockPos offset(EnumFacing facing){return offset(facing,1);}public BlockPos offset(EnumFacing facing,int n){return add(facing.getFrontOffsetX()*n,facing.getFrontOffsetY()*n,facing.getFrontOffsetZ()*n);}
    public BlockPos up(){return up(1);}public BlockPos up(int n){return offset(EnumFacing.UP,n);}public BlockPos down(){return down(1);}public BlockPos down(int n){return offset(EnumFacing.DOWN,n);}
    public BlockPos north(){return north(1);}public BlockPos north(int n){return offset(EnumFacing.NORTH,n);}public BlockPos south(){return south(1);}public BlockPos south(int n){return offset(EnumFacing.SOUTH,n);}
    public BlockPos east(){return east(1);}public BlockPos east(int n){return offset(EnumFacing.EAST,n);}public BlockPos west(){return west(1);}public BlockPos west(int n){return offset(EnumFacing.WEST,n);}
    public BlockPos toImmutable(){return this;}
    public long toLong(){return ((long)getX()&0x3ffffffL)<<38|((long)getY()&0xfffL)<<26|((long)getZ()&0x3ffffffL);}
    public static BlockPos fromLong(long value){return new BlockPos((int)(value>>38),(int)(value<<26>>52),(int)(value<<38>>38));}
    @Override public BlockPos crossProduct(Vec3i p){return new BlockPos(super.crossProduct(p));}
    public BlockPos rotate(Rotation rotation){switch(rotation){case CLOCKWISE_90:return new BlockPos(-getZ(),getY(),getX());case CLOCKWISE_180:return new BlockPos(-getX(),getY(),-getZ());case COUNTERCLOCKWISE_90:return new BlockPos(getZ(),getY(),-getX());default:return this;}}
    public static Iterable<BlockPos> getAllInBox(BlockPos a,BlockPos b){return getAllInBox(Math.min(a.getX(),b.getX()),Math.min(a.getY(),b.getY()),Math.min(a.getZ(),b.getZ()),Math.max(a.getX(),b.getX()),Math.max(a.getY(),b.getY()),Math.max(a.getZ(),b.getZ()));}
    public static Iterable<BlockPos> getAllInBox(final int minX,final int minY,final int minZ,final int maxX,final int maxY,final int maxZ){return new Iterable<BlockPos>(){public Iterator<BlockPos> iterator(){return new Iterator<BlockPos>(){int x=minX,y=minY,z=minZ;boolean more=minX<=maxX&&minY<=maxY&&minZ<=maxZ;public boolean hasNext(){return more;}public BlockPos next(){if(!more)throw new NoSuchElementException();BlockPos value=new BlockPos(x,y,z);if(x<maxX)x++;else if(y<maxY){x=minX;y++;}else if(z<maxZ){x=minX;y=minY;z++;}else more=false;return value;}public void remove(){throw new UnsupportedOperationException();}};}};}
    public static class MutableBlockPos extends BlockPos {
        public MutableBlockPos(){this(0,0,0);}public MutableBlockPos(int x,int y,int z){super(x,y,z);}public MutableBlockPos(BlockPos p){this((Vec3i)p);}public MutableBlockPos(Vec3i p){this(p.getX(),p.getY(),p.getZ());}
        public MutableBlockPos setPos(int x,int y,int z){this.x=x;this.y=y;this.z=z;return this;}
        public MutableBlockPos setPos(Vec3i p){return setPos(p.getX(),p.getY(),p.getZ());}
        public MutableBlockPos setPos(double x,double y,double z){BlockPos p=new BlockPos(x,y,z);return setPos(p);}
        public MutableBlockPos move(EnumFacing facing){return move(facing,1);}public MutableBlockPos move(EnumFacing facing,int n){return setPos(x+facing.getFrontOffsetX()*n,y+facing.getFrontOffsetY()*n,z+facing.getFrontOffsetZ()*n);}
        @Override public BlockPos toImmutable(){return new BlockPos(getX(),getY(),getZ());}
        @Override public BlockPos add(int x,int y,int z){return super.add(x,y,z).toImmutable();}
        @Override public BlockPos add(double x,double y,double z){return super.add(x,y,z).toImmutable();}
        @Override public BlockPos rotate(Rotation rotation){return super.rotate(rotation).toImmutable();}
    }
}
