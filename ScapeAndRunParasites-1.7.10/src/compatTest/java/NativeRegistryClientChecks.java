package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.init.Blocks;import net.minecraft.item.*;import net.minecraft.block.Block;import net.minecraft.util.*;
import com.dhanantry.scapeandrunparasites.backport.registry.*;
public final class NativeRegistryClientChecks{
 final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
 volatile Throwable error;volatile boolean serverReady,serverPlaced;int stage,wait=30,elapsed;boolean done;
 public static void initialize(){FMLCommonHandler.instance().bus().register(new NativeRegistryClientChecks());}
 static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
 void pass(String name){report.add("PASS: "+name);System.out.println("REGISTRY CLIENT PASS: "+name);}
 EntityPlayerMP player(){return (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);}
 @SubscribeEvent public void server(TickEvent.ServerTickEvent e){if(e.phase!=TickEvent.Phase.END)return;Runnable task;while((task=tasks.poll())!=null)try{task.run();}catch(Throwable problem){error=problem;problem.printStackTrace();}
  if(serverReady){WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);if(w.getBlock(10,80,10)==NativeRegistryChecks.variant){check(w.getBlockMetadata(10,80,10)==11,"Server placement metadata");serverPlaced=true;}}
 }
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();try{
  check(++elapsed<1800,"Registry client timed out at stage "+stage);if(error!=null)throw new AssertionError("Integrated server check failed",error);
  if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=3;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;WorldSettings settings=new WorldSettings(1710112,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("disposable-registry-client","Disposable registry QA",settings);stage=1;wait=60;return;}
  if(mc.theWorld==null||mc.thePlayer==null)return;
  if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);for(int x=8;x<13;x++)for(int z=8;z<13;z++)w.setBlock(x,79,z,Blocks.stone,0,3);player().setPositionAndUpdate(10.5,80,12.0);player().inventory.currentItem=0;player().inventory.setInventorySlotContents(0,new ItemStack(NativeRegistryChecks.form,1,11));player().inventoryContainer.detectAndSendChanges();serverReady=true;});stage=2;return;}
  if(stage==2){ItemStack held=mc.thePlayer.getHeldItem();if(!serverReady||held==null||held.getItem()!=NativeRegistryChecks.form||held.getItemDamage()!=11||mc.theWorld.getBlock(10,79,10)!=Blocks.stone)return;
   check(mc.theWorld.isRemote,"Client is not distinct from server world");check(GameRegistry.findBlock("srp_registry_checks","variant")==NativeRegistryChecks.variant&&GameRegistry.findItem("srp_registry_checks","variant")==held.getItem(),"Client native name map");
   check(Item.getIdFromItem(held.getItem())==Block.getIdFromBlock(NativeRegistryChecks.variant)&&held.getItem() instanceof RegistryFixture.VariantItem,"Client custom ItemBlock identity/ID");
   check(GameRegistry.findBlock("explicit_other","foreign")==NativeRegistryChecks.foreign&&GameRegistry.findItem("explicit_other","foreign_item")==NativeRegistryChecks.foreignItem,"Foreign namespace handshake");pass("Actual native client/server registry handshake preserves names, custom ItemBlock classes, explicit namespaces and inventory metadata");
   check(mc.playerController.onPlayerRightClick(mc.thePlayer,mc.theWorld,held,10,79,10,1,Vec3.createVectorHelper(10.5,80,10.5)),"Native client rejected block placement");stage=3;return;
  }
  if(stage==3){if(!serverPlaced||mc.theWorld.getBlock(10,80,10)!=NativeRegistryChecks.variant||mc.theWorld.getBlockMetadata(10,80,10)!=11)return;pass("A real client right-click packet places the custom metadata ItemBlock on the server and synchronizes the result");finish(mc,false);}
 }catch(Throwable problem){problem.printStackTrace();report.add("FAIL: stage "+stage+" :: "+problem);finish(mc,true);}}
 void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"registry-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
