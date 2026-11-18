package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.client.registry.RenderingRegistry;import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.client.model.ModelBiped;import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.init.*;import net.minecraft.item.ItemStack;import net.minecraft.util.*;
import com.dhanantry.scapeandrunparasites.backport.entity.*;
public final class NativeArmorClientChecks {
    final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    volatile NativeArmorChecks.Actor serverActor;volatile Throwable error;volatile float expectedHealth;Entity previous;int stage,wait=30,elapsed;boolean done;
    public static void initialize(){
        RenderingRegistry.registerEntityRenderingHandler(NativeArmorChecks.Actor.class,new RenderLiving(new ModelBiped(),0.5F){@Override protected ResourceLocation getEntityTexture(Entity entity){return new ResourceLocation("textures/entity/steve.png");}});
        FMLCommonHandler.instance().bus().register(new NativeArmorClientChecks());
    }
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    static boolean near(double a,double b){return Math.abs(a-b)<0.00003;}
    void pass(String name){report.add("PASS: "+name);System.out.println("ARMOR CLIENT PASS: "+name);}
    EntityPlayerMP player(){return (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent event){if(event.phase!=TickEvent.Phase.END)return;Runnable task;while((task=tasks.poll())!=null)try{task.run();}catch(Throwable problem){error=problem;problem.printStackTrace();}}
    NativeArmorChecks.Actor actor(Minecraft mc){if(serverActor==null||mc.theWorld==null)return null;Entity e=mc.theWorld.getEntityByID(serverActor.getEntityId());return e instanceof NativeArmorChecks.Actor?(NativeArmorChecks.Actor)e:null;}
    void floor(WorldServer w,int x,int z){for(int a=x-3;a<=x+3;a++)for(int b=z-3;b<=z+3;b++)w.setBlock(a,79,b,Blocks.stone,0,3);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(done||event.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try{
            check(++elapsed<1800,"Armor client timeout at stage "+stage);if(error!=null)throw new AssertionError("Server fixture failed",error);
            if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;mc.gameSettings.renderDistanceChunks=8;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.limitFramerate=60;WorldSettings settings=new WorldSettings(1710112,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("disposable-armor-client","Disposable armor QA",settings);stage=1;wait=60;return;}
            if(mc.theWorld==null||mc.thePlayer==null)return;
            if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);floor(w,22,22);floor(w,140,20);player().setPositionAndUpdate(20.5,80,20.5);NativeArmorChecks.Actor a=new NativeArmorChecks.Actor(w);a.setPosition(22,80,22);NativeArmorChecks.armor(a).setBaseValue(7);NativeArmorChecks.tough(a).setBaseValue(6);a.setCurrentItemOrArmor(4,new ItemStack(Items.diamond_helmet));a.getTotalArmorValue();check(w.spawnEntityInWorld(a),"Native mod-entity spawn failed");serverActor=a;});stage=2;return;}
            NativeArmorChecks.Actor a=actor(mc);
            if(stage==2){if(a==null||a.getEquipmentInSlot(4)==null)return;if(!near(NativeArmorChecks.armor(a).getBaseValue(),7)||!near(NativeArmorChecks.armor(a).getAttributeValue(),10)||!near(NativeArmorChecks.tough(a).getAttributeValue(),8))return;
                check(a!=serverActor&&a.worldObj.isRemote,"Read server actor");check(a.getTotalArmorValue()==10,"Equipment attribute counted twice on native client");pass("Native custom-mob spawn, equipment and watched-attribute packets deliver base armor and one equipment contribution to a distinct client entity");
                check(NativeArmorChecks.armor(mc.thePlayer)!=null&&NativeArmorChecks.tough(mc.thePlayer)!=null,"Client player missing modern attribute names");pass("The real owning client player has both registered native attribute names");
                tasks.add(()->{NativeArmorChecks.armor(serverActor).setBaseValue(20);NativeArmorChecks.tough(serverActor).setBaseValue(12);serverActor.setCurrentItemOrArmor(4,null);serverActor.hit(NativeArmorChecks.HIT,40);expectedHealth=100-ArmorMath.afterAbsorb(40,20,12);});stage=3;return;}
            if(stage==3){if(a==null||a.getEquipmentInSlot(4)!=null||expectedHealth==0)return;if(!near(NativeArmorChecks.armor(a).getAttributeValue(),20)||!near(NativeArmorChecks.tough(a).getAttributeValue(),12)||!near(a.getHealth(),expectedHealth))return;
                check(a.getTotalArmorValue()==20,"Removed equipment was retained on client");pass("Runtime armor/toughness changes, equipment removal and reduced server damage synchronize through actual native packets");previous=a;tasks.add(()->player().setPositionAndUpdate(140.5,80,20.5));stage=4;return;}
            if(stage==4){if(a!=null)return;pass("Leaving native tracking range removes the armor fixture from the client world");tasks.add(()->{check(MinecraftServer.getServer().worldServerForDimension(0).getEntityByID(serverActor.getEntityId())==serverActor,"Server actor unloaded");player().setPositionAndUpdate(20.5,80,20.5);});stage=5;return;}
            if(stage==5){if(a==null||!near(NativeArmorChecks.armor(a).getAttributeValue(),20)||!near(NativeArmorChecks.tough(a).getAttributeValue(),12))return;check(a!=previous&&a.getTotalArmorValue()==20&&near(a.getHealth(),expectedHealth),"Fresh tracked entity lost armor state");pass("Tracking re-entry reconstructs a fresh client entity with current armor, toughness and health");finish(mc,false);}
        }catch(Throwable problem){problem.printStackTrace();report.add("FAIL: stage "+stage+" :: "+problem);finish(mc,true);}
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"armor-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException problem){throw new RuntimeException(problem);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
