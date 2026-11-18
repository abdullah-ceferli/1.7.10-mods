package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;import net.minecraft.entity.passive.EntityPig;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.init.Blocks;import net.minecraft.potion.*;
import com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect;

/** Real native spawning, tracker packets and client worlds; no custom visibility packet or HUD. */
public final class NativePotionFxClientChecks {
    final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    volatile EntityPig serverActor;volatile Throwable error;volatile int shownColor;Entity previous;
    int stage,wait=30,elapsed,stable;boolean done;
    public static void initialize(){FMLCommonHandler.instance().bus().register(new NativePotionFxClientChecks());}
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void pass(String name){report.add("PASS: "+name);System.out.println("POTION FX CLIENT PASS: "+name);}
    EntityPlayerMP player(){return (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent e){if(e.phase!=TickEvent.Phase.END)return;Runnable task;while((task=tasks.poll())!=null)try{task.run();}catch(Throwable problem){error=problem;problem.printStackTrace();}}
    EntityPig actor(Minecraft mc){if(serverActor==null||mc.theWorld==null)return null;Entity entity=mc.theWorld.getEntityByID(serverActor.getEntityId());return entity instanceof EntityPig?(EntityPig)entity:null;}
    int color(EntityPig actor){return actor.getDataWatcher().getWatchableObjectInt(7);}
    void floor(WorldServer w,int x,int z){for(int a=x-3;a<=x+3;a++)for(int b=z-3;b<=z+3;b++)w.setBlock(a,79,b,Blocks.stone,0,3);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(done||event.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try {
            check(++elapsed<2400,"Visibility client timed out at stage "+stage);if(error!=null)throw new AssertionError("Integrated server failed",error);
            if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=8;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;
                WorldSettings settings=new WorldSettings(1710112,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("disposable-potion-fx-client","Disposable potion visibility QA",settings);stage=1;wait=60;return;}
            if(mc.theWorld==null||mc.thePlayer==null)return;
            if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);floor(w,22,22);floor(w,140,20);player().setPositionAndUpdate(20.5,80,20.5);
                    EntityPig pig=new EntityPig(w);pig.setPosition(22,80,22);pig.addPotionEffect(new SRPPotionEffect(NativePotionFxChecks.red,12000,3,false,false));pig.addPotionEffect(new PotionEffect(NativePotionFxChecks.blue.id,12000,0));check(w.spawnEntityInWorld(pig),"Native pig spawn");serverActor=pig;});stage=2;return;}
            EntityPig pig=actor(mc);
            if(stage==2){if(pig==null)return;check(color(pig)==0||color(pig)==0x0000FF,"Unfiltered spawn color reached the native client");if(color(pig)!=0x0000FF||!pig.isPotionActive(NativePotionFxChecks.red)||!pig.isPotionActive(NativePotionFxChecks.blue))return;
                check(pig!=serverActor&&pig.worldObj.isRemote,"Read server object instead of real client entity");pass("Actual native spawn/tracker packets deliver filtered color while both gameplay effects exist on a distinct client entity");
                tasks.add(()->{serverActor.addPotionEffect(new PotionEffect(NativePotionFxChecks.red.id,20,0));shownColor=PotionHelper.calcPotionLiquidColor(serverActor.getActivePotionEffects());});stage=3;return;}
            if(stage==3){if(pig==null||shownColor==0||color(pig)!=shownColor)return;check(shownColor!=0x0000FF,"Visible blend fixture failed");pass("A plain native weaker application resets visibility and synchronizes the real mixed color");
                tasks.add(()->serverActor.addPotionEffect(new SRPPotionEffect(NativePotionFxChecks.red,20,0,false,false)));stage=4;return;}
            if(stage==4){if(pig==null||color(pig)!=0x0000FF)return;pass("A hidden reapplication synchronizes the filtered color again without removing either effect");tasks.add(()->serverActor.removePotionEffect(NativePotionFxChecks.blue.id));stage=5;return;}
            if(stage==5){if(pig==null)return;check(color(pig)==0x0000FF||color(pig)==0,"Hidden-only transition leaked the native unfiltered red color");if(color(pig)!=0||pig.isPotionActive(NativePotionFxChecks.blue))return;
                check(pig.isPotionActive(NativePotionFxChecks.red),"Hiding removed gameplay effect");if(++stable<30)return;pass("Removing the visible effect leaves zero synced color for the still-active hidden effect, with no unfiltered-color tick");previous=pig;
                tasks.add(()->player().setPositionAndUpdate(140.5,80,20.5));stage=6;return;}
            if(stage==6){if(pig!=null)return;pass("Leaving native tracking range removes the client entity");tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);check(w.getEntityByID(serverActor.getEntityId())==serverActor,"Native actor unloaded during tracking fixture");player().setPositionAndUpdate(20.5,80,20.5);});stage=7;return;}
            if(stage==7){if(pig==null||!pig.isPotionActive(NativePotionFxChecks.red))return;check(pig!=previous&&color(pig)==0,"Tracker restart lost filtered metadata");pass("Tracker re-entry creates a fresh native client entity with the hidden effect and zero particle color");finish(mc,false);}
        }catch(Throwable problem){problem.printStackTrace();report.add("FAIL: stage "+stage+" :: "+problem);finish(mc,true);}
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"potion-fx-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException problem){throw new RuntimeException(problem);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
