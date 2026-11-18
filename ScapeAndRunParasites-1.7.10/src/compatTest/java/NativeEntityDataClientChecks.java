package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.datasync.*;import com.dhanantry.scapeandrunparasites.backport.client.EntityDataClient;import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import dev.srpbackport.tests.NativeEntityDataChecks.Probe;
public final class NativeEntityDataClientChecks {
 private final List<String> report=new ArrayList<String>();private final ConcurrentLinkedQueue<Runnable> serverTasks=new ConcurrentLinkedQueue<Runnable>();
 private volatile Throwable failure;private volatile Probe serverActor;private Probe previousActor;private int stage,wait=30,elapsed,callbacksBefore;private boolean done;
 private final List<String> worldReport=new ArrayList<String>();private volatile boolean serverWorldQueriesPassed;
 void worldQueries(Minecraft mc){
  if(!Boolean.getBoolean("srp.worldPositionClientTest"))return;
  BlockPos here=new BlockPos(22,80,22),far=new BlockPos(800000,70,-800000);
  int before=mc.theWorld.getChunkProvider().getLoadedChunkCount();
  check(com.dhanantry.scapeandrunparasites.backport.world.WorldAccess.isBlockLoaded(mc.theWorld,here,false),"Actual loaded client chunk rejected");
  check(com.dhanantry.scapeandrunparasites.backport.world.WorldAccess.isBlockLoaded(mc.theWorld,far),"Original client allow-empty behavior lost");
  check(!com.dhanantry.scapeandrunparasites.backport.world.WorldAccess.isBlockLoaded(mc.theWorld,far,false),"Missing client chunk accepted without allow-empty");
  check(com.dhanantry.scapeandrunparasites.backport.world.WorldAccess.isAreaLoaded(mc.theWorld,far,2),"Client placeholder area rejected");
  check(!com.dhanantry.scapeandrunparasites.backport.world.WorldAccess.isAreaLoaded(mc.theWorld,far,2,false),"Client placeholder area accepted without allow-empty");
  check(mc.theWorld.getChunkProvider().getLoadedChunkCount()==before,"Client lookup loaded an absent chunk");
  worldReport.add("PASS: Real client chunks preserve original allow-empty placeholders, strict area checks and unchanged loaded-chunk count");
  onServer(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);int n=w.getChunkProvider().getLoadedChunkCount();
   check(!com.dhanantry.scapeandrunparasites.backport.world.WorldAccess.isBlockLoaded(w,far),"Server accepted the same client's empty placeholder");
   check(!com.dhanantry.scapeandrunparasites.backport.world.WorldAccess.isAreaLoaded(w,far,2),"Server accepted missing area");
   check(w.getChunkProvider().getLoadedChunkCount()==n,"Server lookup loaded a missing chunk");
   serverWorldQueriesPassed=true;});
 }
 public static void initialize(){EntityDataClient.initialize();FMLCommonHandler.instance().bus().register(new NativeEntityDataClientChecks());}
 static void check(boolean ok,String reason){if(!ok)throw new AssertionError(reason);}void pass(String name){report.add("PASS: "+name);System.out.println("ENTITY DATA CLIENT PASS: "+name);}
 void onServer(Runnable task){serverTasks.add(task);}
 @SubscribeEvent public void server(TickEvent.ServerTickEvent event){if(event.phase!=TickEvent.Phase.END)return;Runnable task;while((task=serverTasks.poll())!=null)try{task.run();}catch(Throwable e){failure=e;e.printStackTrace();}}
 EntityPlayerMP player(){return (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);}
 void floor(WorldServer w,int centerX,int centerZ){for(int x=centerX-2;x<=centerX+2;x++)for(int z=centerZ-2;z<=centerZ+2;z++)w.setBlock(x,79,z,Blocks.stone,0,3);}
 Probe actor(Minecraft mc){if(serverActor==null||mc.theWorld==null)return null;Object value=mc.theWorld.getEntityByID(serverActor.getEntityId());return value instanceof Probe?(Probe)value:null;}
 boolean values(Probe p,int animation,int skin,float size,boolean active,BlockPos target){if(p==null)return false;EntityDataManager d=EntityDataManager.of(p);return d.get(Probe.ANIMATION)==animation&&d.get(Probe.SKIN)==(byte)skin&&d.get(Probe.SIZE)==size&&d.get(Probe.ACTIVE)==active&&d.get(Probe.TARGET).equals(target)&&p.animationMirror==animation;}
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){if(done||event.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();try{
  check(++elapsed<3600,"Client tracked-data test timed out at stage "+stage);if(failure!=null)throw new AssertionError("Server test task failed",failure);
  if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=8;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;WorldSettings settings=new WorldSettings(1710112,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("disposable-datasync-client","Disposable entity sync QA",settings);stage=1;wait=60;return;}
  if(mc.theWorld==null||mc.thePlayer==null)return;
  if(stage==1){mc.displayGuiScreen(null);onServer(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);floor(w,20,20);player().setPositionAndUpdate(20.5,80,20.5);Probe p=new Probe(w);p.setPosition(22,80,22);EntityDataManager d=EntityDataManager.of(p);d.set(Probe.SKIN,(byte)7);d.set(Probe.ANIMATION,321);d.set(Probe.SIZE,2.75f);d.set(Probe.ACTIVE,true);d.set(Probe.TARGET,new BlockPos(-42,70,1337));check(w.spawnEntityInWorld(p),"Native actor spawn failed");serverActor=p;});stage=2;return;}
  if(stage==2){Probe p=actor(mc);if(!values(p,321,7,2.75f,true,new BlockPos(-42,70,1337)))return;check(p!=serverActor&&p.worldObj.isRemote,"Client read the server object");check(p.callbackThread==Thread.currentThread(),"Initial sync callback did not run on the client main thread");worldQueries(mc);pass("Actual native entity spawning synchronizes all five tracked types to a distinct client-side entity");onServer(()->{EntityDataManager d=EntityDataManager.of(serverActor);d.set(Probe.SKIN,(byte)12);d.set(Probe.ANIMATION,999);d.set(Probe.SIZE,4.5f);d.set(Probe.ACTIVE,false);d.set(Probe.TARGET,new BlockPos(-1234567,-10,1456789));});stage=3;return;}
  if(stage==3){Probe p=actor(mc);if(!values(p,999,12,4.5f,false,new BlockPos(-1234567,-10,1456789)))return;check(p.callbackThread==Thread.currentThread(),"Delta callback did not run on the client main thread");pass("Real Forge server-to-client deltas update animation, skin, float, boolean and signed target coordinates");callbacksBefore=p.callbacks;NativeEntityDataSync.CLIENT_INBOX.clear();stage=4;return;}
  if(stage==4){Probe p=actor(mc);if(p==null||p.callbacks<callbacksBefore+5||NativeEntityDataSync.CLIENT_INBOX.boundEntities()==0)return;check(values(p,999,12,4.5f,false,new BlockPos(-1234567,-10,1456789)),"Resync state mismatch");pass("A real client resync request passes server tracking validation and restores the complete snapshot");previousActor=p;onServer(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);floor(w,120,20);player().setPositionAndUpdate(120.5,80,20.5);});stage=5;return;}
  if(stage==5){if(actor(mc)!=null)return;pass("Leaving native entity tracking range removes the client entity and releases its sync binding");onServer(()->{check(MinecraftServer.getServer().worldServerForDimension(0).getEntityByID(serverActor.getEntityId())==serverActor,"Test actor chunk unloaded");check(!MinecraftServer.getServer().worldServerForDimension(0).getEntityTracker().getTrackingPlayers(serverActor).contains(player()),"Server still tracks distant actor");EntityDataManager d=EntityDataManager.of(serverActor);d.set(Probe.ANIMATION,7777);d.set(Probe.SKIN,(byte)9);player().setPositionAndUpdate(20.5,80,20.5);});stage=6;return;}
  if(stage==6){Probe p=actor(mc);if(!values(p,7777,9,4.5f,false,new BlockPos(-1234567,-10,1456789)))return;check(p!=previousActor,"Tracking restart reused a removed client entity");pass("Re-entering tracking range spawns a fresh client entity with changes made while it was untracked");finish(mc,false);}
 }catch(Throwable e){e.printStackTrace();report.add("FAIL: stage "+stage+" :: "+e);finish(mc,true);}}
 void finish(Minecraft mc,boolean failed){done=true;try{if(Boolean.getBoolean("srp.worldPositionClientTest")){check(serverWorldQueriesPassed,"Integrated server world-query check did not finish");worldReport.add("PASS: Actual integrated server rejects the same unloaded client placeholder and never forces its chunk");worldReport.add("Failed: "+(failed?1:0));Files.write(new File(mc.mcDataDir,"world-position-client.txt").toPath(),worldReport,StandardCharsets.UTF_8);}report.add("Failed: "+(failed?1:0));Files.write(new File(mc.mcDataDir,"datasync-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
