package dev.industrialtechnology.hatch.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.industrialtechnology.hatch.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.lwjgl.opengl.GL11;

public final class EmbeddedHatchClientChecks {
    private int phase,wait=25,age,failed,step;private boolean done;private volatile boolean ready;private volatile Throwable problem;
    private final boolean[][] intermediate=new boolean[2][3];private final boolean[] shot=new boolean[2];
    private final ConcurrentLinkedQueue<Runnable> work=new ConcurrentLinkedQueue<Runnable>();private final List<String> report=new ArrayList<String>();
    public static void install(){FMLCommonHandler.instance().bus().register(new EmbeddedHatchClientChecks());}
    private static void check(boolean v,String why){if(!v)throw new AssertionError(why);}
    private void server(Runnable r){ready=false;work.add(()->{try{r.run();}catch(Throwable t){problem=t;}finally{ready=true;}});}
    @SubscribeEvent public void serverTick(TickEvent.ServerTickEvent e){if(e.phase==TickEvent.Phase.END){Runnable r;while((r=work.poll())!=null)r.run();}}
    private void shot(Minecraft mc,String name){check(GL11.glGetError()==0,"Hatch rendering produced an OpenGL error");ScreenShotHelper.saveScreenshot(mc.mcDataDir,name,mc.displayWidth,mc.displayHeight,mc.getFramebuffer());}
    private void look(Minecraft mc,double x,double y,double z){double dx=x-mc.thePlayer.posX,dz=z-mc.thePlayer.posZ,dy=y-mc.thePlayer.posY-mc.thePlayer.getEyeHeight();mc.thePlayer.rotationYaw=(float)Math.toDegrees(Math.atan2(-dx,dz));mc.thePlayer.rotationPitch=(float)-Math.toDegrees(Math.atan2(dy,Math.sqrt(dx*dx+dz*dz)));}
    private void click(Minecraft mc,int v){int x=28+v*4;check(mc.playerController.onPlayerRightClick(mc.thePlayer,mc.theWorld,mc.thePlayer.getHeldItem(),x,70,36,1,Vec3.createVectorHelper(x+.9,70.97,36.9)),"Native client click failed "+v);}
    private void sample(Minecraft mc){if((phase==4||phase==6)&&mc.theWorld!=null){int motion=phase==4?0:1;for(int v=0;v<3;v++){TileSlidingHatch t=BlockSlidingHatch.tile(mc.theWorld,28+v*4,70,36);if(t!=null&&t.progress(0)>.05&&t.progress(0)<.95){intermediate[motion][v]=true;if(!shot[motion]&&v==1&&t.progress(0)>.3&&t.progress(0)<.7){shot(mc,"embedded-hatches-"+(motion==0?"opening":"closing")+".png");shot[motion]=true;}}}}}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.getMinecraft();try{
        if(problem!=null)throw new AssertionError("Server fixture",problem);sample(mc);if(--wait>0)return;
        if(phase==0){if(!(mc.currentScreen instanceof GuiMainMenu)){wait=10;return;}mc.gameSettings.guiScale=2;mc.gameSettings.renderDistanceChunks=4;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.limitFramerate=60;mc.gameSettings.showInventoryAchievementHint=false;WorldSettings settings=new WorldSettings(215,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("embedded-hatches","Disposable embedded hatch checks",settings);phase=1;wait=45;return;}
        if(mc.thePlayer==null||mc.theWorld==null){check(++age<100,"World join timed out");wait=10;return;}
        if(phase==1){mc.displayGuiScreen(null);server(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);Block b=Block.getBlockFromName("chisel:technical");check(b!=null,"Real Chisel is required");EntityPlayerMP p=(EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);for(int x=26;x<41;x++)for(int z=34;z<41;z++)w.setBlock(x,70,z,Blocks.stonebrick,0,2);for(int v=0;v<3;v++){int x=28+v*4;for(int dx=0;dx<2;dx++)for(int dz=0;dz<2;dz++)w.setBlock(x+dx,70,36+dz,b,3+dx+dz*2,3);ItemStack s=new ItemStack(HatchRegistry.block,1,v);p.inventory.mainInventory[0]=s;p.setPosition(x-1,74,33);check(s.tryPlaceItemIntoWorld(p,w,x,70,36,1,.98F,1,v==2?.98F:.5F),"Inset fixture placement "+v);}p.inventory.mainInventory[0]=new ItemStack(Items.stick);p.inventory.mainInventory[1]=new ItemStack(HatchRegistry.block,1,0);p.inventory.mainInventory[2]=new ItemStack(HatchRegistry.block,1,1);p.inventory.mainInventory[3]=new ItemStack(HatchRegistry.block,1,2);p.inventory.currentItem=0;p.capabilities.isFlying=true;p.sendPlayerAbilities();p.setPositionAndUpdate(33,73,34);p.sendContainerToPlayer(p.inventoryContainer);w.setWorldTime(6000);w.getGameRules().setOrCreateGameRule("doDaylightCycle","false");});phase=2;wait=50;return;}
        if(phase==2){if(!ready){wait=5;return;}mc.thePlayer.capabilities.isFlying=true;look(mc,33.5,70.7,37);for(int v=0;v<3;v++){TileSlidingHatch t=BlockSlidingHatch.tile(mc.theWorld,28+v*4,70,36);check(t!=null&&t.upper&&t.variant==v&&t.backing==Block.getBlockFromName("chisel:technical")&&!t.open,"Client floor/offset packet "+v);for(TileSlidingHatch a:BlockSlidingHatch.parts(mc.theWorld,t))check(a.backingMeta==3+a.xCoord-t.baseX+(a.zCoord-t.baseZ)*2,"Client lost distinct floor metadata");}report.add("PASS: S35 packets preserve all offsets and exact real Chisel floor variants on every part");phase=3;wait=15;return;}
        if(phase==3){shot(mc,"embedded-hatches-closed.png");for(int v=0;v<3;v++)click(mc,v);phase=4;wait=24;return;}
        if(phase==4){for(int v=0;v<3;v++){TileSlidingHatch t=BlockSlidingHatch.tile(mc.theWorld,28+v*4,70,36);check(intermediate[0][v]&&t.open&&t.progress(0)==1,"Native opening did not animate/sync "+v+" intermediate="+intermediate[0][v]+" target="+t.open+" progress="+t.progress(0));}shot(mc,"embedded-hatches-open.png");report.add("PASS: Real client right-click packets open all three covers with observed intermediate animation and synchronized endpoints");for(int v=0;v<3;v++)click(mc,v);phase=6;wait=24;return;}
        if(phase==6){for(int v=0;v<3;v++){TileSlidingHatch t=BlockSlidingHatch.tile(mc.theWorld,28+v*4,70,36);check(intermediate[1][v]&&!t.open&&t.progress(0)==0,"Native closing did not animate/sync "+v);}shot(mc,"embedded-hatches-reclosed.png");report.add("PASS: All three variants close smoothly over the same multiplayer path without GL errors");mc.displayGuiScreen(new net.minecraft.client.gui.inventory.GuiInventory(mc.thePlayer));phase=7;wait=15;return;}
        if(phase==7){shot(mc,"embedded-hatches-inventory.png");mc.displayGuiScreen(null);report.add("PASS: Original hatch inventory models and normal GUI rendering survive opening and closing");finish(mc);}
    }catch(Throwable t){failed++;report.add("FAIL: client fixture "+t);t.printStackTrace();finish(mc);}}
    private void finish(Minecraft mc){done=true;report.add("Failed: "+failed);try{Files.write(mc.mcDataDir.toPath().resolve("embedded-hatch-client.txt"),report,StandardCharsets.UTF_8);}catch(Exception e){throw new RuntimeException(e);}mc.shutdown();}
}
