package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.client.registry.RenderingRegistry;import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.client.model.ModelBiped;import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.init.Blocks;import net.minecraft.util.ResourceLocation;

public final class NativeMeleeClientChecks {
    final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    volatile NativeMeleeChecks.Combatant serverActor;volatile NativeMeleeChecks.Victim serverVictim;volatile Throwable error;
    int stage,wait=30,elapsed;boolean done,seenSwing;
    public static void initialize(){
        RenderLiving renderer=new RenderLiving(new ModelBiped(),0.5F){@Override protected ResourceLocation getEntityTexture(Entity e){return new ResourceLocation("textures/entity/steve.png");}};
        RenderingRegistry.registerEntityRenderingHandler(NativeMeleeChecks.Combatant.class,renderer);RenderingRegistry.registerEntityRenderingHandler(NativeMeleeChecks.Victim.class,renderer);
        FMLCommonHandler.instance().bus().register(new NativeMeleeClientChecks());
    }
    static void check(boolean value,String why){if(!value)throw new AssertionError(why);}
    void pass(String message){report.add("PASS: "+message);System.out.println("MELEE CLIENT PASS: "+message);}
    EntityPlayerMP player(){return(EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent event){if(event.phase!=TickEvent.Phase.END)return;Runnable task;while((task=tasks.poll())!=null)try{task.run();}catch(Throwable t){error=t;t.printStackTrace();}}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(done||event.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try{
            check(++elapsed<1600,"Melee client timeout stage "+stage);if(error!=null)throw new AssertionError("Server fixture failed",error);
            if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=6;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;WorldSettings settings=new WorldSettings(171012,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("melee-client","Melee compatibility",settings);stage=1;wait=60;return;}
            if(mc.theWorld==null||mc.thePlayer==null)return;
            if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{
                WorldServer world=MinecraftServer.getServer().worldServerForDimension(0);for(int x=17;x<=35;x++)for(int z=17;z<=25;z++)world.setBlock(x,79,z,Blocks.stone,0,3);player().setPositionAndUpdate(20.5,80,20.5);
                NativeMeleeChecks.Combatant a=new NativeMeleeChecks.Combatant(world);NativeMeleeChecks.Victim v=new NativeMeleeChecks.Victim(world);a.setPosition(22.5,80,22.5);v.setPosition(30.5,80,22.5);a.setAttackTarget(v);check(world.spawnEntityInWorld(v)&&world.spawnEntityInWorld(a),"Spawn failed");serverVictim=v;serverActor=a;
            });stage=2;return;}
            if(serverActor==null||serverVictim==null)return;
            Entity raw=mc.theWorld.getEntityByID(serverActor.getEntityId()),rawVictim=mc.theWorld.getEntityByID(serverVictim.getEntityId());
            if(!(raw instanceof NativeMeleeChecks.Combatant)||!(rawVictim instanceof NativeMeleeChecks.Victim))return;
            NativeMeleeChecks.Combatant a=(NativeMeleeChecks.Combatant)raw;NativeMeleeChecks.Victim v=(NativeMeleeChecks.Victim)rawVictim;seenSwing|=a.isSwingInProgress;
            if(stage==2){if(serverActor.hits<2||v.getHealth()>=v.getMaxHealth())return;check(a!=serverActor&&v!=serverVictim&&a.worldObj.isRemote,"Not distinct client entities");check(serverActor.callbacks==1&&a.callbacks==0&&a.hits==0,"Client ran server goal");check(serverActor.posX>27,"Native path did not chase target");pass("A server-only constructor goal navigates to its target and native damage reaches a distinct real client victim");stage=3;}
            if(stage==3&&seenSwing){check(serverActor.swings==serverActor.hits,"Swing/attack dispatch mismatch");pass("Unarmed server melee swings arrive through native tracking and animate the real client attacker");finish(mc,false);}
        }catch(Throwable t){t.printStackTrace();report.add("FAIL: stage "+stage+" :: "+t);finish(mc,true);}
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"melee-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
