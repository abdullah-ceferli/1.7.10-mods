package dev.industrialtechnology.builder.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.industrialtechnology.builder.*;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.lwjgl.opengl.GL11;

public final class CarpenterBuilderClientChecks {
    private int phase,wait=25,age,failed;private boolean done;private volatile boolean ready;private volatile Throwable problem;private NBTTagCompound original;
    private final ConcurrentLinkedQueue<Runnable> work=new ConcurrentLinkedQueue<Runnable>();private final List<String> report=new ArrayList<String>();
    public static void install(){FMLCommonHandler.instance().bus().register(new CarpenterBuilderClientChecks());}
    private static void check(boolean value,String why){if(!value)throw new AssertionError(why);}
    private void server(Runnable r){ready=false;work.add(()->{try{r.run();}catch(Throwable t){problem=t;}finally{ready=true;}});}
    @SubscribeEvent public void serverTick(TickEvent.ServerTickEvent e){if(e.phase==TickEvent.Phase.END){Runnable r;while((r=work.poll())!=null)r.run();}}
    private void click(Minecraft mc,int x,int y,int z){check(mc.playerController.onPlayerRightClick(mc.thePlayer,mc.theWorld,mc.thePlayer.getHeldItem(),x,y,z,1,Vec3.createVectorHelper(x+.5,y+.9,z+.5)),"Native block click failed");}
    private void shot(Minecraft mc,String name){check(GL11.glGetError()==0,"Carpenter rendering caused a GL error");ScreenShotHelper.saveScreenshot(mc.mcDataDir,name,mc.displayWidth,mc.displayHeight,mc.getFramebuffer());}
    private void look(Minecraft mc,double x,double y,double z){double dx=x-mc.thePlayer.posX,dz=z-mc.thePlayer.posZ,dy=y-mc.thePlayer.posY-mc.thePlayer.getEyeHeight();mc.thePlayer.rotationYaw=(float)Math.toDegrees(Math.atan2(-dx,dz));mc.thePlayer.rotationPitch=(float)-Math.toDegrees(Math.atan2(dy,Math.sqrt(dx*dx+dz*dz)));}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();try{
        if(problem!=null)throw new AssertionError("Server fixture",problem);
        if(phase==0){if(!(mc.currentScreen instanceof GuiMainMenu)){wait=10;return;}mc.gameSettings.guiScale=2;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.renderDistanceChunks=4;mc.gameSettings.limitFramerate=60;mc.gameSettings.showInventoryAchievementHint=false;WorldSettings settings=new WorldSettings(216,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("carpenter-builder","Disposable Carpenter building checks",settings);phase=1;wait=45;return;}
        if(mc.thePlayer==null||mc.theWorld==null){check(++age<100,"World join timed out");wait=10;return;}
        if(phase==1){mc.displayGuiScreen(null);server(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);Block slope=Block.getBlockFromName("CarpentersBlocks:blockCarpentersSlope"),cover=Block.getBlockFromName("chisel:technical");check(slope!=null&&cover!=null,"Real Carpenter/Chisel required");for(int x=27;x<41;x++)for(int z=29;z<41;z++)w.setBlock(x,69,z,Blocks.stonebrick,0,2);CarpenterBuilderChecks.configure(w,30,70,32,slope,11,new ItemStack(cover,1,3),false);original=CarpenterBuilderChecks.raw(w.getTileEntity(30,70,32));EntityPlayerMP p=(EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);p.inventory.mainInventory[0]=new ItemStack(BuilderRegistry.stick);p.inventory.mainInventory[1]=new ItemStack(slope,64);p.inventory.mainInventory[2]=new ItemStack(cover,64,3);p.inventory.currentItem=0;p.capabilities.isFlying=true;p.sendPlayerAbilities();p.setPositionAndUpdate(33,73,33.5);p.sendContainerToPlayer(p.inventoryContainer);w.setWorldTime(6000);w.getGameRules().setOrCreateGameRule("doDaylightCycle","false");});phase=2;wait=50;return;}
        if(phase==2){if(!ready){wait=5;return;}mc.thePlayer.capabilities.isFlying=true;mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,1));mc.thePlayer.movementInput.sneak=true;mc.thePlayer.setSneaking(true);net.minecraftforge.event.entity.player.PlayerInteractEvent pick=net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.thePlayer,net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,30,70,32,1,mc.theWorld);check(pick.isCanceled(),"Carpenter activation was not intercepted safely");mc.thePlayer.movementInput.sneak=false;mc.thePlayer.setSneaking(false);mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer,2));phase=3;wait=8;return;}
        if(phase==3){check(BuilderCarpenters.selected(mc.thePlayer.getHeldItem(),BuilderData.material(mc.thePlayer.getHeldItem())),"Server did not synchronize template to stick");check(BuilderCarpenters.label(mc.thePlayer.getHeldItem(),BuilderData.material(mc.thePlayer.getHeldItem())).contains(" / "),"HUD lacks cover material");report.add("PASS: Native sneak/right-click packets select a covered Carpenter slope and synchronize its namespaced appearance to the client stick");click(mc,32,69,36);phase=4;wait=8;return;}
        if(phase==4){check(BuilderData.anchor(mc.thePlayer,mc.thePlayer.getHeldItem())!=null,"First point not synchronized");look(mc,34,70.4,36.5);shot(mc,"carpenter-builder-preview.png");click(mc,36,69,36);phase=5;wait=25;return;}
        if(phase==5){NBTTagCompound expected=BuilderCarpenters.capture(mc.theWorld,30,70,32);for(int x=32;x<=36;x++)check(expected.equals(BuilderCarpenters.capture(mc.theWorld,x,70,36)),"Native client tile description lost cover/orientation "+x);check(BuilderData.anchor(mc.thePlayer,mc.thePlayer.getHeldItem())==null,"Completed shape retained selection");report.add("PASS: Real client endpoints build five slopes with identical Chisel covers and orientations through native S35 tile synchronization");shot(mc,"carpenter-builder-line.png");server(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);check(original.equals(CarpenterBuilderChecks.raw(w.getTileEntity(30,70,32))),"The original block changed");});phase=6;wait=10;return;}
        if(phase==6){if(!ready){wait=5;return;}report.add("PASS: Selecting and building the line leave all original source tile data and cover rotation unchanged");mc.displayGuiScreen(new net.minecraft.client.gui.inventory.GuiInventory(mc.thePlayer));phase=7;wait=12;return;}
        if(phase==7){shot(mc,"carpenter-builder-inventory.png");report.add("PASS: Carpenter/Chisel world rendering, Building Stick HUD and inventory preserve native GL state with NEI installed");mc.displayGuiScreen(null);finish(mc);}
    }catch(Throwable t){failed++;report.add("FAIL: client fixture "+t);t.printStackTrace();finish(mc);}}
    private void finish(Minecraft mc){done=true;report.add("Failed: "+failed);try{Files.write(mc.mcDataDir.toPath().resolve("carpenter-builder-client.txt"),report,StandardCharsets.UTF_8);}catch(Exception e){throw new RuntimeException(e);}mc.shutdown();}
}
