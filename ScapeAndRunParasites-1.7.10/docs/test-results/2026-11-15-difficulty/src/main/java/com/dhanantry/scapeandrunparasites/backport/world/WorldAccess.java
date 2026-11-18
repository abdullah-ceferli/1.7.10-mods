package com.dhanantry.scapeandrunparasites.backport.world;
import com.dhanantry.scapeandrunparasites.backport.math.*;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;
import net.minecraft.block.Block;import net.minecraft.entity.player.EntityPlayer;import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;import net.minecraft.util.Vec3;import net.minecraft.world.*;import net.minecraft.world.chunk.Chunk;
/** Position boundaries with native Forge hooks and explicit modern query semantics. */
public final class WorldAccess {
 private WorldAccess(){}
 public static boolean isAirBlock(IBlockAccess access,BlockPos pos){return access.isAirBlock(pos.getX(),pos.getY(),pos.getZ());}
 public static TileEntity getTileEntity(IBlockAccess access,BlockPos pos){return access.getTileEntity(pos.getX(),pos.getY(),pos.getZ());}
 public static boolean setBlockToAir(World world,BlockPos pos){return world.setBlockToAir(pos.getX(),pos.getY(),pos.getZ());}
 public static void setTileEntity(World world,BlockPos pos,TileEntity tile){if(!outsideBuildHeight(pos))world.setTileEntity(pos.getX(),pos.getY(),pos.getZ(),tile);}
 public static void removeTileEntity(World world,BlockPos pos){if(!outsideBuildHeight(pos))world.removeTileEntity(pos.getX(),pos.getY(),pos.getZ());}
 public static boolean canSeeSky(World world,BlockPos pos){return world.canBlockSeeTheSky(pos.getX(),pos.getY(),pos.getZ());}
 public static boolean canSeeSky(Chunk chunk,BlockPos pos){return chunk.canBlockSeeTheSky(pos.getX()&15,pos.getY(),pos.getZ()&15);}
 public static int getLightFromNeighbors(World world,BlockPos pos){return world.getBlockLightValue(pos.getX(),pos.getY(),pos.getZ());}
 public static boolean isBlockModifiable(World world,EntityPlayer player,BlockPos pos){return world.canMineBlock(player,pos.getX(),pos.getY(),pos.getZ());}
 public static boolean isRainingAt(World world,BlockPos pos){return world.canLightningStrikeAt(pos.getX(),pos.getY(),pos.getZ());}
 public static void markBlockRangeForRenderUpdate(World world,BlockPos a,BlockPos b){world.markBlockRangeForRenderUpdate(a.getX(),a.getY(),a.getZ(),b.getX(),b.getY(),b.getZ());}
 public static BlockPos getPrecipitationHeight(World world,BlockPos pos){return new BlockPos(pos.getX(),world.getPrecipitationHeight(pos.getX(),pos.getZ()),pos.getZ());}
 public static Chunk getChunk(World world,BlockPos pos){return world.getChunkFromBlockCoords(pos.getX(),pos.getZ());}
 public static Chunk getChunk(World world,int x,int z){return world.getChunkFromChunkCoords(x,z);}
 public static boolean canSnowAt(World world,BlockPos pos,boolean checkLight){return world.func_147478_e(pos.getX(),pos.getY(),pos.getZ(),checkLight);}
 public static boolean destroyBlock(World world,BlockPos pos,boolean drop){return world.func_147480_a(pos.getX(),pos.getY(),pos.getZ(),drop);}
 public static void scheduleUpdate(World world,BlockPos pos,Block block,int ticks){world.scheduleBlockUpdate(pos.getX(),pos.getY(),pos.getZ(),block,ticks);}
 public static boolean outsideBuildHeight(BlockPos pos){return pos.getY()<0||pos.getY()>=256;}
 public static boolean isSideSolid(IBlockAccess access,BlockPos pos,EnumFacing side){return isSideSolid(access,pos,side,false);}
 public static boolean isSideSolid(IBlockAccess access,BlockPos pos,EnumFacing side,boolean fallback){
  if(outsideBuildHeight(pos)||pos.getX()<-30000000||pos.getZ()<-30000000||pos.getX()>=30000000||pos.getZ()>=30000000)return fallback;
  if(access instanceof World){Chunk c=((World)access).getChunkFromChunkCoords(pos.getX()>>4,pos.getZ()>>4);if(c==null||c.isEmpty())return fallback;}
  return BlockStates.get(access,pos).isSideSolid(access,pos,side);
 }
 // On the original server allowEmpty is ignored; on the original client it accepts unloaded empty placeholders.
 // chunkExists is a lookup on the native server provider and never provides/loads a chunk.
 public static boolean isChunkLoaded(World world,int x,int z,boolean allowEmpty){
  if(world.isRemote)return allowEmpty||!world.getChunkFromChunkCoords(x,z).isEmpty();
  return world.getChunkProvider().chunkExists(x,z);
 }
 public static boolean isBlockLoaded(World world,BlockPos pos){return isBlockLoaded(world,pos,true);}
 public static boolean isBlockLoaded(World world,BlockPos pos,boolean allowEmpty){return isChunkLoaded(world,pos.getX()>>4,pos.getZ()>>4,allowEmpty);}
 public static boolean isAreaLoaded(World world,BlockPos center,int radius){return isAreaLoaded(world,center,radius,true);}
 public static boolean isAreaLoaded(World world,BlockPos center,int radius,boolean allowEmpty){return isAreaLoaded(world,center.getX()-radius,center.getY()-radius,center.getZ()-radius,center.getX()+radius,center.getY()+radius,center.getZ()+radius,allowEmpty);}
 public static boolean isAreaLoaded(World world,BlockPos a,BlockPos b){return isAreaLoaded(world,a,b,true);}
 public static boolean isAreaLoaded(World world,BlockPos a,BlockPos b,boolean allowEmpty){return isAreaLoaded(world,a.getX(),a.getY(),a.getZ(),b.getX(),b.getY(),b.getZ(),allowEmpty);}
 private static boolean isAreaLoaded(World world,int x1,int y1,int z1,int x2,int y2,int z2,boolean allowEmpty){
  if(y2<0||y1>=256)return false;
  x1>>=4;z1>>=4;x2>>=4;z2>>=4;
  for(int x=x1;x<=x2;x++)for(int z=z1;z<=z2;z++)if(!isChunkLoaded(world,x,z,allowEmpty))return false;
  return true;
 }
 public static int getHeight(Chunk chunk,BlockPos pos){return chunk.getHeightValue(pos.getX()&15,pos.getZ()&15);}
 public static BlockPos getHeight(World world,BlockPos pos){return new BlockPos(pos.getX(),world.getHeightValue(pos.getX(),pos.getZ()),pos.getZ());}
 public static BlockPos getTopSolidOrLiquidBlock(World world,BlockPos pos){
  Chunk chunk=world.getChunkFromBlockCoords(pos.getX(),pos.getZ());int y=chunk.getTopFilledSegment()+16;
  while(y>=0){int below=y-1;Block b=below<0?net.minecraft.init.Blocks.air:chunk.getBlock(pos.getX()&15,below,pos.getZ()&15);
   if(b.getMaterial().blocksMovement()&&!b.isLeaves(world,pos.getX(),below,pos.getZ())&&!b.isFoliage(world,pos.getX(),below,pos.getZ()))break;
   y=below;
  }
  return new BlockPos(pos.getX(),y,pos.getZ());
 }
 public static float getBlockDensity(World world,Vec3d target,AxisAlignedBB box){
  double dx=1/((box.maxX-box.minX)*2+1),dy=1/((box.maxY-box.minY)*2+1),dz=1/((box.maxZ-box.minZ)*2+1);
  double ox=(1-Math.floor(1/dx)*dx)/2,oz=(1-Math.floor(1/dz)*dz)/2;
  if(dx<0||dy<0||dz<0)return 0;
  int clear=0,total=0;Vec3 end=target.toLegacy();
  for(float x=0;x<=1;x=(float)(x+dx))for(float y=0;y<=1;y=(float)(y+dy))for(float z=0;z<=1;z=(float)(z+dz)){
   Vec3 start=Vec3.createVectorHelper(box.minX+(box.maxX-box.minX)*x+ox,box.minY+(box.maxY-box.minY)*y,box.minZ+(box.maxZ-box.minZ)*z+oz);
   if(world.rayTraceBlocks(start,end)==null)clear++;total++;
  }
  return (float)clear/total;
 }
}
