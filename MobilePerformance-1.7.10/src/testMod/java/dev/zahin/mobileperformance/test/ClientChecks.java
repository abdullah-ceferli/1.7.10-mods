package dev.zahin.mobileperformance.test;

import com.google.gson.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.zahin.mobileperformance.client.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.*;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.world.*;
import net.minecraftforge.client.*;
import org.lwjgl.opengl.GL11;

public final class ClientChecks {
    private final List<String> report=new ArrayList<String>();private final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    private int phase,wait=25,failed,tries;private boolean done;private volatile boolean ready;private volatile Throwable serverError;
    private JsonObject benchmark=new JsonObject();private long entityStart,tileStart;private int fpsBefore,distanceBefore,particlesBefore,mipmapsBefore,aoBefore;private boolean fancyBefore,vsyncBefore,cloudsBefore;
    public static void install(){FMLCommonHandler.instance().bus().register(new ClientChecks());}
    private void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    private void test(String text,Runnable action){try{action.run();report.add("PASS: "+text);}catch(Throwable error){failed++;report.add("FAIL: "+text+" :: "+error);error.printStackTrace();}}
    private void server(Runnable action){ready=false;tasks.add(()->{try{action.run();}catch(Throwable e){serverError=e;}finally{ready=true;}});}
    @SubscribeEvent public void serverTick(TickEvent.ServerTickEvent e){if(e.phase==TickEvent.Phase.END){Runnable task;while((task=tasks.poll())!=null)task.run();}}
    private void setup(){WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);w.setWorldTime(6000);w.getGameRules().setOrCreateGameRule("doDaylightCycle","false");w.getGameRules().setOrCreateGameRule("doMobSpawning","false");
        for(int x=-28;x<=28;x++)for(int z=-12;z<=66;z++)w.setBlock(x,64,z,Blocks.stone,0,2);
        for(int x=-24;x<=24;x+=3)for(int z=10;z<=58;z+=3)w.setBlock(x,65,z,Blocks.chest,2,2);
        for(int i=0;i<100;i++){EntityPig pig=new EntityPig(w);pig.setPosition(-20+(i%10)*4,65,34+(i/10)*2);pig.tasks.taskEntries.clear();pig.targetTasks.taskEntries.clear();w.spawnEntityInWorld(pig);}
        for(int i=0;i<80;i++){EntityItem item=new EntityItem(w,-18+(i%10)*4,66,22+(i/10)*4,new ItemStack(Items.apple));item.motionX=item.motionY=item.motionZ=0;w.spawnEntityInWorld(item);}
        EntityPlayerMP p=(EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);p.setPositionAndUpdate(.5,65,-6.5);p.rotationYaw=0;p.rotationPitch=5;
    }
    private void shot(Minecraft mc,String name){check(GL11.glGetError()==0,"OpenGL error before screenshot");ScreenShotHelper.saveScreenshot(mc.mcDataDir,name,mc.displayWidth,mc.displayHeight,mc.getFramebuffer());}
    private int atlasLevel(Minecraft mc){return ((Integer)cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue(net.minecraft.client.renderer.texture.TextureMap.class,mc.getTextureMapBlocks(),"mipmapLevels","field_147636_j")).intValue();}
    private void contracts(Minecraft mc){final ClientRuntime r=Hooks.runtime;check(r!=null,"Client runtime missing");r.config.enabled=true;mc.displayGuiScreen(null);r.render(new TickEvent.RenderTickEvent(TickEvent.Phase.START,1));
        test("Installed core hooks execute in real vanilla renderer, particle and animation methods",()->{int pass=MinecraftForgeClient.getRenderPass();ForgeHooksClient.setRenderPass(0);try{
            EntityItem item=new EntityItem(mc.theWorld,mc.thePlayer.posX+50,mc.thePlayer.posY,mc.thePlayer.posZ,new ItemStack(Items.apple));long calls=Hooks.entityCalls,skips=r.entitySkipped;int bound=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);check(RenderManager.instance.func_147939_a(item,50,0,0,0,0,false),"Skipped render returned failure");check(Hooks.entityCalls==calls+1&&r.entitySkipped==skips+1,"Entity core hook missing");check(GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)==bound,"Skipped entity changed GL binding");
            TileEntityChest tile=new TileEntityChest();tile.setWorldObj(mc.theWorld);tile.xCoord=(int)mc.thePlayer.posX+50;tile.yCoord=65;tile.zCoord=(int)mc.thePlayer.posZ;calls=Hooks.tileCalls;skips=r.tileSkipped;TileEntityRendererDispatcher.instance.renderTileEntity(tile,0);check(Hooks.tileCalls==calls+1&&r.tileSkipped==skips+1,"Tile core hook missing");
            calls=Hooks.particleCalls;skips=r.particlesDropped;mc.effectRenderer.addEffect(new EntitySmokeFX(mc.theWorld,mc.thePlayer.posX+40,mc.thePlayer.posY,mc.thePlayer.posZ,0,0,0));check(Hooks.particleCalls==calls+1&&r.particlesDropped==skips+1,"Particle core hook missing");
            r.config.animationInterval=2;r.tick=1;calls=Hooks.animationCalls;skips=r.animationsSkipped;mc.getTextureMapBlocks().updateAnimations();check(Hooks.animationCalls==calls+1&&r.animationsSkipped==skips+1,"Animation core hook missing");r.config.animationInterval=1;
        }finally{ForgeHooksClient.setRenderPass(pass);}});
        test("Nearby objects, players, GUI previews and unknown mod tiles retain native rendering",()->{int pass=MinecraftForgeClient.getRenderPass();ForgeHooksClient.setRenderPass(0);try{EntityPig near=new EntityPig(mc.theWorld);near.setPosition(mc.thePlayer.posX+4,65,mc.thePlayer.posZ);check(Hooks.entity(near)&&Hooks.entity(mc.thePlayer),"Near mob/player hidden");TileEntity custom=new TileEntity();custom.setWorldObj(mc.theWorld);custom.xCoord=5000;check(Hooks.tile(custom),"Unknown custom tile hidden");ForgeHooksClient.setRenderPass(-1);EntityItem far=new EntityItem(mc.theWorld,5000,65,5000,new ItemStack(Items.apple));check(Hooks.entity(far),"GUI render was culled");}finally{ForgeHooksClient.setRenderPass(pass);}});
        test("Cosmetic spawn budget is bounded while close particles and combat feedback are retained",()->{r.clientTick(new TickEvent.ClientTickEvent(TickEvent.Phase.START));int accepted=0;for(int i=0;i<200;i++)if(Hooks.particle(new EntitySmokeFX(mc.theWorld,mc.thePlayer.posX+12,mc.thePlayer.posY,mc.thePlayer.posZ,0,0,0)))accepted++;check(accepted==r.config.particlesPerTick,"Cosmetic budget mismatch: "+accepted);check(Hooks.particle(new EntitySmokeFX(mc.theWorld,mc.thePlayer.posX+2,mc.thePlayer.posY,mc.thePlayer.posZ,0,0,0)),"Close smoke hidden");check(Hooks.particle(new EntityFlameFX(mc.theWorld,mc.thePlayer.posX+50,mc.thePlayer.posY,mc.thePlayer.posZ,0,0,0)),"Fire feedback hidden");});
        test("Preset backs up once, restores actual graphics values and retains malformed backups",()->{try{
            mc.gameSettings.mipmapLevels=1;mc.getTextureMapBlocks().setMipmapLevels(1);distanceBefore=mc.gameSettings.renderDistanceChunks;fpsBefore=mc.gameSettings.limitFramerate;particlesBefore=mc.gameSettings.particleSetting;mipmapsBefore=mc.gameSettings.mipmapLevels;fancyBefore=mc.gameSettings.fancyGraphics;vsyncBefore=mc.gameSettings.enableVsync;aoBefore=mc.gameSettings.ambientOcclusion;cloudsBefore=mc.gameSettings.clouds;int jump=mc.gameSettings.keyBindJump.getKeyCode();
            Path backup=mc.mcDataDir.toPath().resolve("config/mobileperformance-before-preset.json");r.preset.apply(r.config);byte[] original=Files.readAllBytes(backup);r.preset.apply(r.config);check(Arrays.equals(original,Files.readAllBytes(backup)),"Repeated preset overwrote original settings");check(mc.gameSettings.renderDistanceChunks==4&&mc.gameSettings.mipmapLevels==0&&r.config.animationInterval==2&&atlasLevel(mc)==0,"Preset/live atlas values missing");r.preset.restore(r.config);check(!Files.exists(backup),"Restored backup stayed active");check(mc.gameSettings.renderDistanceChunks==distanceBefore&&mc.gameSettings.limitFramerate==fpsBefore&&mc.gameSettings.particleSetting==particlesBefore&&mc.gameSettings.mipmapLevels==mipmapsBefore&&mc.gameSettings.fancyGraphics==fancyBefore&&mc.gameSettings.enableVsync==vsyncBefore&&mc.gameSettings.keyBindJump.getKeyCode()==jump&&atlasLevel(mc)==1&&mc.gameSettings.ambientOcclusion==aoBefore&&mc.gameSettings.clouds==cloudsBefore,"Preset failed to restore or changed controls");
            Files.write(backup,"{broken".getBytes(StandardCharsets.UTF_8));try{r.preset.apply(r.config);throw new AssertionError("Malformed backup accepted");}catch(java.io.IOException expected){check(mc.gameSettings.renderDistanceChunks==distanceBefore,"Malformed backup changed graphics");check(new String(Files.readAllBytes(backup),StandardCharsets.UTF_8).equals("{broken"),"Malformed backup destroyed");ClientCommandHandler.instance.executeCommand(mc.thePlayer,"/mobileperf off");check(!r.config.enabled&&Files.exists(backup),"Malformed backup prevented disabling or was removed");r.config.enabled=true;r.config.save();}finally{Files.deleteIfExists(backup);}
        }catch(java.io.IOException e){throw new RuntimeException(e);}});
        test("Client command works without server permissions and disabling bypasses render budgets",()->{int pass=MinecraftForgeClient.getRenderPass();try{ForgeHooksClient.setRenderPass(0);check(ClientCommandHandler.instance.executeCommand(mc.thePlayer,"/mobileperf off")==1&&!r.config.enabled,"Off command failed");EntityItem far=new EntityItem(mc.theWorld,5000,65,5000,new ItemStack(Items.apple));check(Hooks.entity(far),"Disabled mod kept culling");check(ClientCommandHandler.instance.executeCommand(mc.thePlayer,"/mobileperf on")==1&&r.config.enabled,"On command failed");}finally{ForgeHooksClient.setRenderPass(pass);}});
    }
    private void beginSample(ClientRuntime r){r.resetCounters();entityStart=Hooks.entityCalls;tileStart=Hooks.tileCalls;}
    private void record(String key,ClientRuntime r){JsonObject n=new JsonObject();n.addProperty("frames",r.frames.samples());n.addProperty("fps",r.frames.fps());n.addProperty("averageFrameMs",r.frames.averageMs());n.addProperty("p95FrameMs",r.frames.p95Ms());n.addProperty("entityCalls",Hooks.entityCalls-entityStart);n.addProperty("tileCalls",Hooks.tileCalls-tileStart);n.addProperty("entityRendersSkipped",r.entitySkipped);n.addProperty("tileRendersSkipped",r.tileSkipped);benchmark.add(key,n);}
    private void finish(Minecraft mc){done=true;report.add("Failed: "+failed);try{Files.write(mc.mcDataDir.toPath().resolve("checks-client.txt"),report,StandardCharsets.UTF_8);benchmark.addProperty("device","Windows desktop test client; not an Android FPS prediction");Files.write(mc.mcDataDir.toPath().resolve("benchmark.json"),new GsonBuilder().setPrettyPrinting().create().toJson(benchmark).getBytes(StandardCharsets.UTF_8));}catch(Exception e){throw new RuntimeException(e);}mc.shutdown();}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();try{
        if(serverError!=null)throw new AssertionError("Fixture setup failed",serverError);
        if(phase==0){if(!(mc.currentScreen instanceof GuiMainMenu)){wait=10;return;}mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.renderDistanceChunks=5;mc.gameSettings.limitFramerate=260;mc.gameSettings.enableVsync=false;mc.gameSettings.mipmapLevels=0;mc.gameSettings.showInventoryAchievementHint=false;mc.gameSettings.guiScale=2;WorldSettings settings=new WorldSettings(912,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("mobile-performance-checks","Disposable performance checks",settings);phase=1;wait=40;return;}
        if(mc.theWorld==null||mc.thePlayer==null){check(++tries<100,"World join timeout");wait=10;return;}
        if(phase==1){mc.displayGuiScreen(null);server(()->setup());phase=2;wait=40;return;}
        if(phase==2){if(!ready){wait=10;return;}mc.thePlayer.rotationYaw=0;mc.thePlayer.rotationPitch=5;contracts(mc);mc.thePlayer.rotationYaw=0;mc.thePlayer.rotationPitch=5;Hooks.runtime.config.enabled=false;phase=3;wait=40;return;}
        ClientRuntime r=Hooks.runtime;
        if(phase==3){beginSample(r);phase=4;wait=80;return;}
        if(phase==4){record("disabled",r);shot(mc,"performance-disabled.png");r.config.enabled=true;phase=5;wait=40;return;}
        if(phase==5){beginSample(r);phase=6;wait=80;return;}
        if(phase==6){record("enabled",r);test("Actual scene rendering skips distant objects while native frame sampling remains active",()->{check(r.entitySkipped>0&&r.tileSkipped>0,"Scene did not exercise both render budgets: "+r.entitySkipped+"/"+r.tileSkipped);check(r.frames.samples()>20&&r.frames.p95Ms()>0,"Frame sampling failed");});shot(mc,"performance-enabled.png");mc.displayGuiScreen(new GuiInventory(mc.thePlayer));phase=7;wait=20;return;}
        if(phase==7){test("Texture updates keep full native behavior inside menus",()->{r.config.animationInterval=2;r.tick=1;long before=r.animationsSkipped;mc.getTextureMapBlocks().updateAnimations();check(r.animationsSkipped==before,"GUI texture updates throttled");check(GL11.glGetError()==0,"GUI OpenGL error");r.config.animationInterval=1;});shot(mc,"performance-inventory.png");mc.displayGuiScreen(null);phase=8;wait=25;return;}
        if(phase==8){test("Closing the inventory returns to world rendering without a GL error",()->check(GL11.glGetError()==0,"Post-menu GL error"));shot(mc,"performance-after-menu.png");r.config.enabled=true;r.config.animationInterval=1;r.config.save();finish(mc);}
    }catch(Throwable error){failed++;report.add("FAIL: native fixture :: "+error);error.printStackTrace();finish(mc);}}
}
