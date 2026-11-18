package com.jkvin114.displaydelight.tests;

import com.jkvin114.displaydelight.*;import com.jkvin114.displaydelight.client.DisplayRenderer;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.lang.reflect.Field;import java.util.*;import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiMainMenu;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayerMP;import net.minecraft.init.Blocks;import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public final class NativeDisplayClientChecks {
    final List<String> report=new ArrayList<String>();final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    volatile int serverStage;volatile Throwable error;int stage,ticks,wait=30;boolean done;
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    public static void initialize(){FMLCommonHandler.instance().bus().register(new NativeDisplayClientChecks());}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent e){if(e.phase!=TickEvent.Phase.END)return;Runnable r;while((r=tasks.poll())!=null)try{r.run();}catch(Throwable t){error=t;t.printStackTrace();}}
    @SubscribeEvent public void client(TickEvent.ClientTickEvent e){
        if(done||e.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();
        try{
            check(++ticks<1800,"Client test timed out at "+stage);if(error!=null)throw new AssertionError("Server fixture failed",error);
            if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;
                DisplayRenderer renderer=new DisplayRenderer();Field icons=DisplayRenderer.class.getDeclaredField("icons");icons.setAccessible(true);
                check(net.minecraft.client.resources.I18n.format("tile.displaydelight.food_plate.name").equals("Food Plate"),"Native translation failed");
                Map<String,?> textures=(Map<String,?>)icons.get(renderer);
                for(String name:textures.keySet()){
                    String[] parts=DisplayRenderer.nativeIcon(name).split(":");
                    mc.getResourceManager().getResource(new net.minecraft.util.ResourceLocation(parts[0],"textures/blocks/"+parts[1]+".png")).getInputStream().close();
                }
                report.add("PASS: All native texture resource paths resolve and the plate name is translated");
                check(renderer.displayTypes()==382,"Client mesh type count");report.add("PASS: Client loads all 382 food mesh types");
                WorldSettings settings=new WorldSettings(171017,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.gameSettings.renderDistanceChunks=6;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.showInventoryAchievementHint=false;
                mc.launchIntegratedServer("food-gallery","Food display gallery",settings);stage=1;wait=60;return;
            }
            if(mc.theWorld==null||mc.thePlayer==null)return;
            if(stage==1){mc.displayGuiScreen(null);tasks.add(()->{
                WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);
                for(int x=-2;x<44;x++)for(int z=-2;z<49;z++)w.setBlock(x,79,z,Blocks.stonebrick,0,2);
                int i=0;for(DisplayBlock b:Catalog.blocks.values()){DisplayBlock.place(w,(i%20)*2,80,(i/20)*2,b,i%4,b.definition.max,false);i++;}
                EntityPlayerMP p=(EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);p.capabilities.allowFlying=true;p.capabilities.isFlying=true;p.sendPlayerAbilities();p.setPositionAndUpdate(19.5,103,51.5);p.rotationYaw=180;p.rotationPitch=52;
                serverStage=1;
            });stage=2;wait=150;return;}
            if(stage==2){if(serverStage<1)return;int i=0;for(DisplayBlock b:Catalog.blocks.values()){
                int x=(i%20)*2,z=(i/20)*2;if(mc.theWorld.getBlock(x,80,z)!=b)return;DisplayTile t=DisplayTile.at(mc.theWorld,x,80,z);if(t==null||t.count!=b.definition.max)return;i++;
            }
                mc.thePlayer.rotationYaw=180;mc.thePlayer.rotationPitch=52;mc.gameSettings.hideGUI=true;
                report.add("PASS: All 382 placed displays and full food counts synchronize to an actual native client");
                tasks.add(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);DisplayBlock.place(w,20,80,42,Catalog.blocks.get("plated_cookie"),0,3,true);serverStage=2;});
                stage=3;wait=60;return;
            }
            if(stage==3){DisplayTile t=DisplayTile.at(mc.theWorld,20,80,42);if(serverStage<2||t==null||t.count!=3||!t.hidden)return;
                report.add("PASS: Food count and hidden-plate state reach native client tile packets");stage=4;wait=120;return;}
            if(stage==4){check(GL11.glGetError()==GL11.GL_NO_ERROR,"Native OpenGL error");
                net.minecraft.util.ScreenShotHelper.saveScreenshot(mc.mcDataDir,"displaydelight-gallery.png",mc.displayWidth,mc.displayHeight,mc.getFramebuffer());
                report.add("PASS: Native renderer draws the gallery without OpenGL errors; screenshot saved");mc.gameSettings.guiScale=2;mc.displayGuiScreen(new InventoryGallery());stage=5;wait=60;return;}
            if(stage==5){check(GL11.glGetError()==GL11.GL_NO_ERROR,"Native inventory OpenGL error");
                net.minecraft.util.ScreenShotHelper.saveScreenshot(mc.mcDataDir,"displaydelight-inventory.png",mc.displayWidth,mc.displayHeight,mc.getFramebuffer());
                report.add("PASS: Native inventory pipeline draws all 382 display items; screenshot saved");finish(mc,false);}
        }catch(Throwable t){t.printStackTrace();report.add("FAIL: stage "+stage+" :: "+t);finish(mc,true);}
    }
    public static final class InventoryGallery extends net.minecraft.client.gui.GuiScreen {
        private final net.minecraft.client.renderer.entity.RenderItem items=new net.minecraft.client.renderer.entity.RenderItem();
        @Override public boolean doesGuiPauseGame(){return false;}
        @Override public void drawScreen(int mouseX,int mouseY,float partial){
            drawDefaultBackground();net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();int i=0;
            for(DisplayBlock b:Catalog.blocks.values()){items.renderItemAndEffectIntoGUI(fontRendererObj,mc.getTextureManager(),new ItemStack(b),10+(i%25)*18,18+(i/25)*18);i++;}
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();super.drawScreen(mouseX,mouseY,partial);
        }
    }
    void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(new File(mc.mcDataDir,"checks-client.txt").toPath(),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}mc.displayGuiScreen(new GuiMainMenu());if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
