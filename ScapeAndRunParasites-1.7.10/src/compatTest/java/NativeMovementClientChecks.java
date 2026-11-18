package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.client.registry.RenderingRegistry;import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.client.model.ModelBiped;import net.minecraft.client.renderer.entity.RenderLiving;import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.init.Blocks;import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.*;
public final class NativeMovementClientChecks {
    final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    volatile NativeMovementChecks.Actor serverActor;volatile Throwable error;NativeMovementChecks.Actor previous;int stage,wait=30,elapsed,stable;boolean done;
    public static void initialize(){
        RenderingRegistry.registerEntityRenderingHandler(NativeMovementChecks.Actor.class,new RenderLiving(new ModelBiped(),0.5F){@Override protected ResourceLocation getEntityTexture(Entity e){return new ResourceLocation("textures/entity/steve.png");}});
        FMLCommonHandler.instance().bus().register(new NativeMovementClientChecks());
    }
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void pass(String name){report.add("PASS: "+name);System.out.println("MOVEMENT CLIENT PASS: "+name);}
    EntityPlayerMP player(){return (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);}
    NativeMovementChecks.Actor actor(Minecraft mc){if(serverActor==null)return null;Entity e=mc.theWorld.getEntityByID(serverActor.getEntityId());return e instanceof NativeMovementChecks.Actor?(NativeMovementChecks.Actor)e:null;}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent e){if(e.phase!=TickEvent.Phase.END)return;Runnable task;while((task=tasks.poll())!=null)try{task.run();}catch(Throwable t){error=t;t.printStackTrace();}}
    void floor(WorldServer world,int x,int z){for(int a=x-3;a<=x+3;a++)for(int b=z-3;b<=z+3;b++)world.setBlock(a,79,b,Blocks.stone,0,3);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try{
            check(++elapsed<1600,"Movement client timeout stage "+stage);if(error!=null)throw new AssertionError("Server fixture failed",error);
            if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=8;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;WorldSettings settings=new WorldSettings(1710112,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("movement-client","Movement compatibility",settings);stage=1;wait=60;return;}
            if(mc.theWorld==null||mc.thePlayer==null)return;
            if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);floor(w,22,22);floor(w,140,20);player().setPositionAndUpdate(20.5,80,20.5);NativeMovementChecks.Actor a=new NativeMovementChecks.Actor(w);a.setPosition(22,82,22);GravityAccess.set(a,true);check(w.spawnEntityInWorld(a),"Mob spawn");serverActor=a;});stage=2;return;}
            NativeMovementChecks.Actor a=actor(mc);
            if(stage==2){if(a==null||!GravityAccess.get(a)||Math.abs(a.motionY)>1E-8)return;if(++stable<10)return;check(a!=serverActor&&a.worldObj.isRemote,"Read server entity");check(Math.abs(serverActor.posY-82)<1E-8&&Math.abs(a.posY-82)<0.02,"Gravity-free mob drift server="+serverActor.posY+" client="+a.posY);pass("Initial tracking sends gravity to a distinct real client mob and both sides remain suspended");stable=0;tasks.add(()->GravityAccess.set(serverActor,false));stage=3;return;}
            if(stage==3){if(a==null||GravityAccess.get(a)||a.posY>81.5)return;pass("A live server gravity toggle reaches the client and native falling resumes");tasks.add(()->{serverActor.setPositionAndUpdate(22,82,22);serverActor.motionX=serverActor.motionY=serverActor.motionZ=0;GravityAccess.set(serverActor,true);});stage=4;return;}
            if(stage==4){if(a==null||!GravityAccess.get(a)||Math.abs(a.posY-82)>0.02||Math.abs(a.motionY)>1E-8)return;previous=a;tasks.add(()->player().setPositionAndUpdate(140.5,80,20.5));stage=5;return;}
            if(stage==5){if(a!=null)return;pass("Leaving native tracking range removes the client entity and its binding");tasks.add(()->{check(MinecraftServer.getServer().worldServerForDimension(0).getEntityByID(serverActor.getEntityId())==serverActor,"Server entity unloaded");GravityAccess.set(serverActor,false);GravityAccess.set(serverActor,true);player().setPositionAndUpdate(20.5,80,20.5);});stage=6;return;}
            if(stage==6){if(a==null||!GravityAccess.get(a))return;check(a!=previous,"Tracking reused old client entity");pass("Tracking re-entry reconstructs a fresh client entity with the current gravity state");
                GravityNetwork.INBOX.clear();GravityAccess.receive(a,false,0);stage=7;return;}
            if(stage==7){if(a==null||!GravityAccess.get(a)||GravityNetwork.INBOX.boundEntities()==0)return;pass("Client resynchronization repairs a missing snapshot through the real authenticated request channel");tasks.add(()->GravityAccess.set(player(),true));stage=8;return;}
            if(stage==8){if(!GravityAccess.get(mc.thePlayer))return;pass("The owning player receives gravity despite different native server and client player classes");tasks.add(()->GravityAccess.set(player(),false));stage=9;return;}
            if(stage==9){if(GravityAccess.get(mc.thePlayer))return;finish(mc,false);}
        }catch(Throwable t){t.printStackTrace();report.add("FAIL: stage "+stage+" :: "+t);finish(mc,true);}
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"movement-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
