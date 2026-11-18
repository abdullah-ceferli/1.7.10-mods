package dev.srpbackport.tests;

import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.client.registry.RenderingRegistry;import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.client.model.ModelBiped;import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.init.Blocks;import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.navigation.*;

public final class NativeNavigationClientChecks {
    final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    volatile NativeNavigationChecks.GroundActor[] serverActors;volatile Throwable error;int stage,wait=30,elapsed;boolean done;
    public static void initialize(){
        Class<?>[] types={NativeNavigationChecks.GroundActor.class,NativeNavigationChecks.FlyingActor.class,NativeNavigationChecks.SwimmingActor.class,NativeNavigationChecks.ClimbingActor.class};
        for(Class<?> type:types)RenderingRegistry.registerEntityRenderingHandler((Class)type,new RenderLiving(new ModelBiped(),0.3F){@Override protected ResourceLocation getEntityTexture(Entity e){return new ResourceLocation("textures/entity/steve.png");}});
        FMLCommonHandler.instance().bus().register(new NativeNavigationClientChecks());
    }
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void pass(String msg){report.add("PASS: "+msg);}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent event){if(event.phase!=TickEvent.Phase.END)return;Runnable r;while((r=tasks.poll())!=null)try{r.run();}catch(Throwable t){error=t;t.printStackTrace();}}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(done||event.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try{
            check(++elapsed<1500,"Client navigation timeout at "+stage+" "+state());if(error!=null)throw new AssertionError("Server fixture",error);
            if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=4;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;
                WorldSettings settings=new WorldSettings(171012,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("navigation-client","Navigation compatibility",settings);stage=1;wait=60;return;}
            if(mc.theWorld==null||mc.thePlayer==null)return;
            if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{
                WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);NativeNavigationChecks.fill(w,15,79,15,40,79,30,Blocks.stone);
                NativeNavigationChecks.fill(w,20,80,23,30,86,28,Blocks.water);NativeNavigationChecks.fill(w,26,80,16,26,85,21,Blocks.stone);
                ((EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0)).setPositionAndUpdate(20.5,80,17.5);
                NativeNavigationChecks.GroundActor[] actors={new NativeNavigationChecks.GroundActor(w),new NativeNavigationChecks.FlyingActor(w),new NativeNavigationChecks.SwimmingActor(w),new NativeNavigationChecks.ClimbingActor(w)};
                double[][] pos={{22.5,80,16.5},{22.5,82,19.5},{22.5,82,25.5},{23.5,80,16.5}};
                for(int i=0;i<4;i++){actors[i].setPosition(pos[i][0],pos[i][1],pos[i][2]);check(w.spawnEntityInWorld(actors[i]),"Spawn "+i);}serverActors=actors;
            });stage=2;return;}
            if(serverActors==null)return;
            NativeNavigationChecks.GroundActor[] clients=new NativeNavigationChecks.GroundActor[4];
            for(int i=0;i<4;i++){Entity e=mc.theWorld.getEntityByID(serverActors[i].getEntityId());if(!(e instanceof NativeNavigationChecks.GroundActor))return;clients[i]=(NativeNavigationChecks.GroundActor)e;}
            if(stage==2){Class<?>[] types={PathNavigateGround.class,PathNavigateFlying.class,PathNavigateSwimmer.class,PathNavigateClimber.class};
                for(int i=0;i<4;i++)check(clients[i]!=serverActors[i]&&clients[i].factories==1&&clients[i].callbacks==0&&clients[i].getNavigator().getClass()==types[i]&&serverActors[i].callbacks==1&&serverActors[i].ready,"Factory side/order "+i);
                pass("All four navigator types construct on tracked native clients while only server entities initialize goals");
                tasks.add(()->check(serverActors[1].getNavigator().tryMoveToXYZ(32,87,19,1),"Client scene route absent"));stage=3;return;
            }
            if(stage==3){if(serverActors[1].getDistanceSq(32.5,87,19.5)>=2||clients[1].getDistanceSq(32.5,87,19.5)>=3)return;
                check(serverActors[1].getDistanceSqToEntity(clients[1])<2,"Tracker divergence");pass("Server pathfinding drives actual flying motion around a wall and native tracking delivers the arrival to the client");
                tasks.add(()->{
                    NativeNavigationChecks.FlyingActor actor=(NativeNavigationChecks.FlyingActor)serverActors[1];WorldServer w=(WorldServer)actor.worldObj;
                    check(actor.getNavigator().tryMoveToXYZ(22,82,19,1),"Return route absent");
                    NativeNavigationChecks.fill(w,29,80,16,29,90,22,Blocks.stone);w.markBlockForUpdate(29,87,19);
                    check(((com.dhanantry.scapeandrunparasites.backport.navigation.PathNavigate)actor.getNavigator()).canUpdatePathOnTimeout(),"Server world update did not queue");
                });stage=4;return;}
            if(stage==4){if(elapsed%100==0)tasks.add(()->System.out.println("REROUTE "+state()));if(serverActors[1].getDistanceSq(22.5,82,19.5)>=2||clients[1].getDistanceSq(22.5,82,19.5)>=3)return;
                check(serverActors[1].getDistanceSqToEntity(clients[1])<2,"Reroute tracker divergence");
                pass("A wall built after route creation triggers server rerouting and the tracked client receives the new arrival");finish(mc,false);}
        }catch(Throwable t){t.printStackTrace();report.add("FAIL: stage "+stage+" :: "+t);finish(mc,true);}
    }
    String state(){
        if(serverActors==null)return "no actors";
        EntityLiving a=serverActors[1];net.minecraft.pathfinding.PathEntity p=a.getNavigator().getPath();
        return "server="+a.posX+","+a.posY+","+a.posZ+" motion="+a.motionX+","+a.motionY+","+a.motionZ+" path="+(p==null?"null":p.getCurrentPathIndex()+"/"+p.getCurrentPathLength()+" end="+p.getFinalPathPoint())+" pending="+((com.dhanantry.scapeandrunparasites.backport.navigation.PathNavigate)a.getNavigator()).canUpdatePathOnTimeout();
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"navigation-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
