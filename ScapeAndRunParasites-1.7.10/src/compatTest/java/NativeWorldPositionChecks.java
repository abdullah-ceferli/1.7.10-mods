package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.registry.GameRegistry;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.world.chunk.Chunk;import net.minecraft.world.gen.ChunkProviderServer;import net.minecraft.init.Blocks;import net.minecraft.block.*;import net.minecraft.block.material.Material;import net.minecraft.tileentity.*;import net.minecraft.util.Vec3;
import com.dhanantry.scapeandrunparasites.backport.math.*;import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;
import net.minecraft.server.MinecraftServer;
@Mod(modid="srp_world_position_checks",name="Disposable native world position checks",version="1")
public final class NativeWorldPositionChecks{
 public static class LeafRock extends Block{LeafRock(){super(Material.rock);}public boolean isLeaves(IBlockAccess w,int x,int y,int z){return true;}}
 public static class InvisibleAir extends Block{InvisibleAir(){super(Material.rock);}public boolean isAir(IBlockAccess w,int x,int y,int z){return true;}}
 public static class Ticker extends Block{int count;Ticker(){super(Material.rock);}public void updateTick(World w,int x,int y,int z,Random rand){count++;}}
 static final LeafRock leaf=new LeafRock();static final InvisibleAir air=new InvisibleAir();static final Ticker ticker=new Ticker();
 final List<String> report=new ArrayList<String>();int ticks,failed;boolean done;WorldServer world;
 @Mod.EventHandler public void pre(FMLPreInitializationEvent event){GameRegistry.registerBlock(leaf,"leafrock");GameRegistry.registerBlock(air,"testair");GameRegistry.registerBlock(ticker,"ticker");}
 @Mod.EventHandler public void init(FMLInitializationEvent event){FMLCommonHandler.instance().bus().register(this);}
 interface Check{void run()throws Exception;}void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}void test(String name,Check fn){try{fn.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
 void checks(){
  test("Converted world calls preserve nested evaluation, chunk/world height overloads, state methods, comments and literals",()->WorldPositionFixture.run(world));
  test("Loaded checks never provide a missing server chunk and preserve original vertical/reversed-area behavior",()->{
   ChunkProviderServer provider=(ChunkProviderServer)world.getChunkProvider();int cx=12000,cz=-12001;check(!provider.chunkExists(cx,cz),"Fixture chunk already loaded");int n=provider.getLoadedChunkCount();
   BlockPos far=new BlockPos(cx*16,70,cz*16);check(!WorldAccess.isBlockLoaded(world,far)&&!WorldAccess.isBlockLoaded(world,far,false),"Missing chunk reported loaded");
   check(!WorldAccess.isAreaLoaded(world,far,2)&&!WorldAccess.isAreaLoaded(world,far,far.add(16,1,16),false),"Missing area reported loaded");
   check(provider.getLoadedChunkCount()==n&&!provider.chunkExists(cx,cz),"Query loaded an absent chunk");
   BlockPos here=new BlockPos(8,70,8);check(WorldAccess.isBlockLoaded(world,new BlockPos(0,-500,0))&&WorldAccess.isBlockLoaded(world,new BlockPos(0,900,0)),"Block lookup incorrectly checks height");
   check(!WorldAccess.isAreaLoaded(world,new BlockPos(0,-20,0),new BlockPos(1,-1,1))&&!WorldAccess.isAreaLoaded(world,new BlockPos(0,256,0),new BlockPos(1,300,1)),"Outside vertical interval accepted");
   check(WorldAccess.isAreaLoaded(world,new BlockPos(0,-20,0),new BlockPos(1,300,1))&&WorldAccess.isAreaLoaded(world,here,1),"Partly outside vertical interval rejected");
   check(WorldAccess.isAreaLoaded(world,new BlockPos(cx*16+20,70,cz*16+20),far),"Reversed range was reordered");
  });
  test("Modern top-surface scan includes y=0, skips Forge leaves/foliage and liquids, and returns -1 for empty columns",()->{
   int x=7,z=7;for(int y=0;y<256;y++)world.setBlockToAir(x,y,z);
   BlockPos p=new BlockPos(x,80,z);check(WorldAccess.getTopSolidOrLiquidBlock(world,p).getY()==-1,"Empty top sentinel");
   world.setBlock(x,0,z,Blocks.stone,0,3);check(WorldAccess.getTopSolidOrLiquidBlock(world,p).getY()==1,"Bottom layer skipped");
   world.setBlock(x,70,z,Blocks.stone,0,3);world.setBlock(x,80,z,Blocks.water,0,3);world.setBlock(x,90,z,leaf,0,3);world.setBlock(x,95,z,Blocks.leaves,0,3);
   check(WorldAccess.getTopSolidOrLiquidBlock(world,p).getY()==71,"Leaves/liquid counted as solid surface");
   check(WorldAccess.getHeight(world,p).getY()==world.getHeightValue(x,z)&&WorldAccess.getHeight(WorldAccess.getChunk(world,p),p)==world.getHeightValue(x,z),"Native height map mismatch");
  });
  test("Native Forge air, solidity, light, precipitation and tile-entity hooks retain coordinates",()->{
   BlockPos p=new BlockPos(-17,85,-18);WorldAccess.getChunk(world,p);world.setBlock(-17,85,-18,air,0,3);check(WorldAccess.isAirBlock(world,p),"Forge isAir hook ignored");world.setBlock(-17,85,-18,Blocks.stone,0,3);
   check(WorldAccess.isSideSolid(world,p,EnumFacing.UP)&&WorldAccess.isSideSolid(world,new BlockPos(-17,-1,-18),EnumFacing.UP,true),"Solidity/default boundary");
   check(WorldAccess.getLightFromNeighbors(world,p)==world.getBlockLightValue(-17,85,-18),"Light query mismatch");
   check(WorldAccess.canSeeSky(world,p)==world.canBlockSeeTheSky(-17,85,-18)&&WorldAccess.getPrecipitationHeight(world,p).getY()==world.getPrecipitationHeight(-17,-18)&&WorldAccess.isRainingAt(world,p)==world.canLightningStrikeAt(-17,85,-18),"Sky/weather query mismatch");
   world.setBlock(-17,85,-18,Blocks.chest,0,3);TileEntityChest chest=new TileEntityChest();WorldAccess.setTileEntity(world,p,chest);check(WorldAccess.getTileEntity(world,p)==chest&&chest.xCoord==-17&&chest.yCoord==85&&chest.zCoord==-18,"Tile insertion coordinates");
   WorldAccess.removeTileEntity(world,p);check(chest.isInvalid(),"Removed tile not invalidated");check(WorldAccess.setBlockToAir(world,p),"Block removal failed");
  });
  test("Modern explosion sampling uses centered horizontal samples against real native collision geometry",()->{
   Vec3d target=new Vec3d(25,92,25);check(WorldAccess.getBlockDensity(world,target,Boxes.create(20,90,20,21.3,91.9,21.7))==1,"Open density");
   for(int x=19;x<=28;x++)for(int z=19;z<=28;z++)world.setBlock(x,92,z,Blocks.stone,0,3);
   check(WorldAccess.getBlockDensity(world,new Vec3d(25,94,25),Boxes.create(20,90,20,21.3,91.9,21.7))==0,"Roof did not occlude sampled rays");
   check(WorldAccess.getBlockDensity(world,target,Boxes.create(Double.NaN,0,0,1,1,1))==1,"Original NaN samples must pass the native ray tracer");
  });
  BlockPos p=new BlockPos(40,85,4);world.setBlock(40,85,4,ticker,0,3);WorldAccess.scheduleUpdate(world,p,ticker,2);
 }
 @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END)return;ticks++;try{
  if(ticks==20){world=MinecraftServer.getServer().worldServerForDimension(0);world.getChunkFromChunkCoords(0,0);world.getChunkFromChunkCoords(2,0);checks();}
  if(ticks==26){test("Scheduled position updates reach the native block tick callback",()->check(ticker.count==1,"Expected one scheduled tick, got "+ticker.count));finish();}
 }catch(Throwable e){failed++;report.add("FAIL: setup :: "+e);e.printStackTrace();finish();}}
 void finish(){done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("world-position-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}
}
