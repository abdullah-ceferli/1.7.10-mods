package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;import net.minecraft.block.Block;import net.minecraft.block.material.Material;import net.minecraft.entity.player.EntityPlayer;import net.minecraft.init.Blocks;import net.minecraft.server.MinecraftServer;import net.minecraft.tileentity.TileEntity;import net.minecraft.nbt.NBTTagCompound;import net.minecraft.util.AxisAlignedBB;import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.state.*;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;

/** Test-only Forge mod. Its disposable worlds and JAR are never installed in the user's game. */
@Mod(modid="srp_state_checks",name="Disposable SRP block-state checks",version="1")
public final class NativeBlockStateChecks {
    static Probe block;static TileProbe tileBlock;private int ticks,failed,assertions;private boolean done;private WorldServer world;private final List<String> report=new ArrayList<String>();
    public static class Probe extends StatefulBlock {
        static final PropertyBool ACTIVE=PropertyBool.create("active");static final PropertyInteger AGE=PropertyInteger.create("age",0,7);
        int added,ticked,broken,removed,lastMeta=-1;BlockPos lastPos;IBlockState lastBreak;
        Probe(){super(Material.rock);setBlockName("srp_state_probe");setHardness(2);setDefaultState(blockState.getBaseState().withProperty(ACTIVE,false).withProperty(AGE,0));}
        @Override protected BlockStateContainer createBlockState(){return new BlockStateContainer(this,AGE,ACTIVE);}
        @Override public IBlockState getStateFromMeta(int meta){return getDefaultState().withProperty(AGE,meta&7).withProperty(ACTIVE,(meta&8)!=0);}
        @Override public int getMetaFromState(IBlockState s){return s.getValue(AGE)|(s.getValue(ACTIVE)?8:0);}
        @Override public int getLightValue(IBlockState s){return s.getValue(ACTIVE)?12:0;}
        @Override public boolean isOpaqueCube(IBlockState s){return false;}
        @Override public boolean isFullCube(IBlockState s){return false;}
        @Override public BlockFaceShape getBlockFaceShape(IBlockAccess access,IBlockState s,BlockPos p,EnumFacing f){return f==EnumFacing.UP&&s.getValue(ACTIVE)?BlockFaceShape.SOLID:BlockFaceShape.UNDEFINED;}
        @Override public AxisAlignedBB getBoundingBox(IBlockState s,IBlockAccess access,BlockPos p){return AxisAlignedBB.getBoundingBox(0.125,0,0.25,0.875,(s.getValue(AGE)+1)/8.0,0.75);}
        int walked,collided,harvested;Entity lastEntity;
        @Override public int damageDropped(IBlockState s){return getMetaFromState(s);}
        @Override public net.minecraft.item.Item getItemDropped(IBlockState s,Random rng,int fortune){return net.minecraft.item.Item.getItemFromBlock(this);}
        @Override public void onEntityWalk(World w,BlockPos p,Entity e){walked++;lastEntity=e;lastPos=p;super.onEntityWalk(w,p,e);}
        @Override public void onEntityCollision(World w,BlockPos p,IBlockState s,Entity e){collided++;lastMeta=getMetaFromState(s);lastEntity=e;lastPos=p;super.onEntityCollision(w,p,s,e);}
        @Override public void onBlockHarvested(World w,BlockPos p,IBlockState s,EntityPlayer e){harvested++;lastMeta=getMetaFromState(s);lastPos=p;super.onBlockHarvested(w,p,s,e);}
        @Override public void onBlockAdded(World w,BlockPos p,IBlockState s){added++;lastMeta=getMetaFromState(s);lastPos=p;super.onBlockAdded(w,p,s);}
        @Override public void updateTick(World w,BlockPos p,IBlockState s,Random rng){ticked++;lastMeta=getMetaFromState(s);lastPos=p;super.updateTick(w,p,s,rng);}
        @Override public void breakBlock(World w,BlockPos p,IBlockState s){broken++;lastBreak=s;lastPos=p;super.breakBlock(w,p,s);}
        @Override public boolean removedByPlayer(IBlockState s,World w,BlockPos p,EntityPlayer player,boolean harvest){removed++;lastMeta=getMetaFromState(s);return super.removedByPlayer(s,w,p,player,harvest);}
    }
    public static final class TileProbe extends Probe {
        @Override public boolean hasTileEntity(IBlockState s){return s.getValue(ACTIVE);}
        @Override public TileEntity createTileEntity(World w,IBlockState s){if(!s.getValue(ACTIVE))return null;SavedTile tile=new SavedTile();tile.code=getMetaFromState(s);return tile;}
    }
    public static final class SavedTile extends TileEntity {
        int code;
        @Override public void writeToNBT(NBTTagCompound n){super.writeToNBT(n);n.setInteger("probeCode",code);}
        @Override public void readFromNBT(NBTTagCompound n){super.readFromNBT(n);code=n.getInteger("probeCode");}
    }
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event){block=new Probe();tileBlock=new TileProbe();GameRegistry.registerBlock(block,"state_probe");GameRegistry.registerBlock(tileBlock,"tile_state_probe");GameRegistry.registerTileEntity(SavedTile.class,"srp_state_checks:tile");}
    @Mod.EventHandler public void init(FMLInitializationEvent event){FMLCommonHandler.instance().bus().register(this);}
    interface Check{void run()throws Exception;}
    void check(boolean value,String message){assertions++;if(!value)throw new AssertionError(message);}
    void test(String name,Check c){try{c.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
    IBlockState state(int meta){return block.getStateFromMeta(meta);}
    void initial(){
        test("All 16 state variants round-trip through native chunk metadata and invoke the added callback",()->{int before=block.added;for(int meta=0;meta<16;meta++){BlockPos p=new BlockPos(meta,80,0);check(BlockStates.meta(state(meta))==meta,"Codec "+meta);check(BlockStates.set(world,p,state(meta),3),"Place "+meta);check(world.getBlockMetadata(meta,80,0)==meta&&BlockStates.get(world,p)==state(meta),"Chunk metadata "+meta);check(block.lastMeta==meta&&p.equals(block.lastPos),"Added callback state "+meta);}check(block.added==before+16,"Missing added callbacks");});
        test("Metadata-dependent light and side solidity use actual world state",()->{for(int meta=0;meta<16;meta++){int expected=meta<8?0:12;check(block.getLightValue(world,meta,80,0)==expected,"Light "+meta);check(block.isSideSolid(world,meta,80,0,ForgeDirection.UP)==(meta>=8),"UP solid "+meta);check(!block.isSideSolid(world,meta,80,0,ForgeDirection.DOWN),"DOWN solid");check(!block.isNormalCube(world,meta,80,0),"Cube shape");}});
        test("Native collision boxes offset local state-dependent bounds exactly once",()->{for(int meta=0;meta<16;meta++){AxisAlignedBB box=block.getCollisionBoundingBoxFromPool(world,meta,80,0);check(box.minX==meta+0.125&&box.maxX==meta+0.875&&box.minY==80&&box.maxY==80+((meta&7)+1)/8.0&&box.minZ==0.25&&box.maxZ==0.75,"World collision "+meta);AxisAlignedBB local=BlockStates.get(world,new BlockPos(meta,80,0)).getCollisionBoundingBox(world,new BlockPos(meta,80,0));check(local.minX==0.125&&local.minY==0,"Local collision");}});
        test("Scheduled native ticks deliver decoded metadata and immutable coordinates",()->{BlockPos p=new BlockPos(12,80,0);int before=block.ticked;block.updateTick(world,12,80,0,new Random(3));check(block.ticked==before+1&&block.lastMeta==12&&block.lastPos.equals(p),"Tick bridge");});
        test("Breaking a block receives its old state after the world block has changed",()->{BlockPos p=new BlockPos(30,80,0);BlockStates.set(world,p,state(13),3);int before=block.broken;world.setBlock(30,80,0,Blocks.stone,0,3);check(block.broken==before+1&&block.lastBreak==state(13)&&p.equals(block.lastPos),"Old-state break dispatch");});
        test("Native player-removal callback dispatches once and removes the block",()->{BlockPos p=new BlockPos(31,80,0);BlockStates.set(world,p,state(11),3);int before=block.removed;check(block.removedByPlayer(world,null,31,80,0,false),"Removal result");check(world.isAirBlock(31,80,0)&&block.removed==before+1&&block.lastMeta==11,"Removal bridge");});
        test("Vanilla raw states preserve all metadata and canonical identity",()->{for(int meta=0;meta<16;meta++){IBlockState s=BlockStates.fromMeta(Blocks.wool,meta);check(BlockStates.meta(s)==meta&&s==BlockStates.fromMeta(Blocks.wool,meta)&&s.getProperties().isEmpty(),"Raw legacy state "+meta);}});
        test("Out-of-range metadata and unimplemented vanilla codecs fail before world storage",()->{BlockPos p=new BlockPos(40,80,0);for(int bad:new int[]{-1,16,2048}){boolean rejected=false;try{BlockStates.fromMeta(block,bad);}catch(IllegalArgumentException expected){rejected=true;}check(rejected,"Invalid metadata accepted "+bad);}boolean unsupported=false;try{BlockStates.meta(new BlockStateContainer(Blocks.wool).getBaseState());}catch(IllegalArgumentException expected){unsupported=true;}check(unsupported,"Unmapped property graph silently became metadata zero");
            unsupported=false;try{BlockStates.fromMeta(Blocks.oak_stairs,0).withRotation(com.dhanantry.scapeandrunparasites.backport.util.Rotation.CLOCKWISE_90);}catch(UnsupportedOperationException expected){unsupported=true;}check(unsupported,"Unimplemented vanilla rotation silently ignored");check(world.isAirBlock(40,80,0),"Unexpected write");});
        test("Native drops and middle-click preserve all 16 item metadata variants",()->{for(int meta=0;meta<16;meta++){java.util.ArrayList<net.minecraft.item.ItemStack> drops=block.getDrops(world,meta,80,0,meta,0);check(drops.size()==1&&drops.get(0).getItemDamage()==meta&&drops.get(0).getItem()==net.minecraft.item.Item.getItemFromBlock(block),"Drop variant "+meta);net.minecraft.util.MovingObjectPosition hit=new net.minecraft.util.MovingObjectPosition(meta,80,0,1,net.minecraft.util.Vec3.createVectorHelper(meta+0.5,81,0.5));net.minecraft.item.ItemStack pick=block.getPickBlock(hit,world,meta,80,0,null);check(pick.getItemDamage()==meta&&pick.getItem()==drops.get(0).getItem(),"Pick variant "+meta);}});
        test("Native walking, contact and harvesting invoke state-aware overrides exactly once",()->{int a=block.walked,b=block.collided,c=block.harvested;Entity e=new net.minecraft.entity.passive.EntityPig(world);BlockPos p=new BlockPos(10,80,0);block.onEntityWalking(world,10,80,0,e);block.onEntityCollidedWithBlock(world,10,80,0,e);block.onBlockHarvested(world,10,80,0,10,null);check(block.walked==a+1&&block.collided==b+1&&block.harvested==c+1&&block.lastMeta==10&&p.equals(block.lastPos)&&block.lastEntity==e,"Entity/harvest callbacks");});
        test("State IDs preserve the 12-bit block ID and upper metadata layout",()->{for(int meta=0;meta<16;meta++){int id=BlockStates.stateId(state(meta));check(id==Block.getIdFromBlock(block)+(meta<<12)&&BlockStates.stateById(id)==state(meta),"Packed state ID "+meta);}});
        test("Default world-write flags and explicit receiver codecs retain stored state",()->{BlockPos p=new BlockPos(33,80,0);check(BlockStates.set(world,p,state(9))&&BlockStates.meta(block,BlockStates.get(world,p))==9,"Default write flags");boolean rejected=false;try{BlockStates.meta(Blocks.stone,state(9));}catch(IllegalArgumentException expected){rejected=true;}check(rejected,"Mismatched codec owner");});
        test("Converted Java calls preserve nested expressions, receiver evaluation, flags and literals in a real world",()->StateCallFixture.run(world,block));
        test("Tile creation dispatches with metadata and persists its original state",()->{for(int meta:new int[]{3,14}){BlockPos p=new BlockPos(meta,80,2);BlockStates.set(world,p,tileBlock.getStateFromMeta(meta),3);TileEntity tile=world.getTileEntity(meta,80,2);check((meta==14)==(tile instanceof SavedTile),"Metadata tile presence "+meta);if(tile!=null)check(((SavedTile)tile).code==meta,"Tile received wrong metadata");}});
        test("Native chunk save completes for all 16 variants and a stateful tile",()->{world.saveAllChunks(true,null);Files.write(Paths.get("states-saved.done"),Arrays.asList("16 variants and tile saved"),StandardCharsets.UTF_8);check(Files.isRegularFile(Paths.get("states-saved.done")),"Save marker");});
    }
    void restart(){
        test("All 16 property states survive a genuine dedicated-server JVM restart",()->{for(int meta=0;meta<16;meta++){BlockPos p=new BlockPos(meta,80,0);check(world.getBlock(meta,80,0)==block&&world.getBlockMetadata(meta,80,0)==meta&&BlockStates.get(world,p)==state(meta),"Restart state "+meta);}});
        test("State-dependent tile presence and its NBT survive a genuine restart",()->{check(world.getTileEntity(3,80,2)==null,"Unexpected inactive tile");TileEntity tile=world.getTileEntity(14,80,2);check(tile instanceof SavedTile&&((SavedTile)tile).code==14&&BlockStates.meta(BlockStates.get(world,new BlockPos(14,80,2)))==14,"Tile restart state");});
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;String phase=Files.exists(Paths.get("states-saved.done"))?"restart":"initial";try{world=MinecraftServer.getServer().worldServerForDimension(0);for(int x=0;x<3;x++)world.getChunkFromChunkCoords(x,0);if(phase.equals("initial"))initial();else restart();}catch(Throwable e){failed++;report.add("FAIL: setup :: "+e);e.printStackTrace();}finally{done=true;report.add("Assertions: "+assertions);report.add("Failed: "+failed);try{Files.write(Paths.get("states-"+phase+".txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}}
}
