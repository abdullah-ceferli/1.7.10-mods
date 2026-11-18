package com.baisylia.culturaldelights.legacy.client;
import com.baisylia.culturaldelights.legacy.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.world.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.block.Block;
import net.minecraft.init.*;
import org.lwjgl.opengl.GL11;
public final class ClientChecks {
    private int stage,wait=30,ticks;private boolean done;private volatile Throwable failure;
    private final List<String> report=new ArrayList<String>();private final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    public static void init(){FMLCommonHandler.instance().bus().register(new ClientChecks());}
    @SubscribeEvent public void server(TickEvent.ServerTickEvent e){if(e.phase!=TickEvent.Phase.END)return;Runnable r;while((r=tasks.poll())!=null)try{r.run();}catch(Throwable t){failure=t;t.printStackTrace();}}
    private static void check(boolean good,String text){if(!good)throw new AssertionError(text);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();try{
        check(++ticks<1200,"Client test timeout at stage "+stage);if(failure!=null)throw new AssertionError(failure);
        if(stage==0){if(!(mc.currentScreen instanceof GuiMainMenu))return;WorldSettings settings=new WorldSettings(171118,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.guiScale=2;mc.gameSettings.showInventoryAchievementHint=false;mc.launchIntegratedServer("garden-checks","Garden checks",settings);stage=1;wait=100;return;}
        if(stage==4){check(mc.currentScreen instanceof GuiMainMenu,"Failed to return to title screen");capture(mc,"foodports-menu.png");report.add("PASS: Native title screen renders after leaving the garden with no OpenGL error");finish(mc,false);return;}
        if(mc.theWorld==null||mc.thePlayer==null)return;
        if(stage==1){mc.displayGuiScreen(null);int items=0;for(Item item:Port.CONTENT.items.values()){check(item.getIconFromDamage(0)!=null,"Missing item icon "+item.getUnlocalizedName());items++;}report.add("PASS: Native texture stitch provides icons for "+items+" Croptopia item identities");int sprites=ClientProxy.renderer.checkSprites();try{Class<?> proxy=Class.forName("com.baisylia.culturaldelights.legacy.client.ClientProxy");Object renderer=proxy.getField("renderer").get(null);sprites+=((Integer)renderer.getClass().getMethod("checkSprites").invoke(renderer)).intValue();}catch(Exception ex){throw new RuntimeException(ex);}report.add("PASS: "+sprites+" block texture sprites have unique atlas regions rather than the missing-texture fallback");
            int nei=0;try{Class<?> handler=Class.forName("vectorwing.farmersdelight.legacy.integration.nei.FoundationHandler");for(vectorwing.farmersdelight.legacy.SourceCrafting recipe:vectorwing.farmersdelight.legacy.SourceCrafting.ALL)if(recipe.id.startsWith("croptopia:")||recipe.id.startsWith("culturaldelights:")){Object view=handler.newInstance();handler.getMethod("loadCraftingRecipes",ItemStack.class).invoke(view,recipe.output);int recipes=((Integer)handler.getMethod("numRecipes").invoke(view)).intValue();if(recipes==0&&Loader.isModLoaded("handson_delight")){Class<?> assembly=Class.forName("com.pockyl.handson_delight.compat.nei.AssemblyHandler");Object moved=assembly.newInstance();assembly.getMethod("loadCraftingRecipes",ItemStack.class).invoke(moved,recipe.output);recipes=((Integer)assembly.getMethod("numRecipes").invoke(moved)).intValue();}check(recipes>0,"NEI cannot find "+recipe.id);nei++;}}catch(Exception ex){throw new RuntimeException(ex);}report.add("PASS: Native NEI resolves all "+nei+" available original crafting outputs from both ports");
            tasks.offer(new Runnable(){public void run(){WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);for(int x=197;x<216;x++)for(int z=195;z<211;z++){w.setBlock(x,79,z,Blocks.grass);for(int y=80;y<85;y++)w.setBlockToAir(x,y,z);}int i=0;for(Plant p:Port.CONTENT.crops.values()){int x=198+i%15,z=198+i/15;w.setBlock(x,79,z,Blocks.farmland,7,3);w.setBlock(x,80,z,p,p.maxAge,3);i++;}
                String[] names={"cucumbers","eggplants","corn","wild_cucumbers","wild_eggplants","wild_corn","avocado_crate","eggplant_parmesan_block","exotic_roll_medley"};for(int k=0;k<names.length;k++){Block b=GameRegistry.findBlock("culturaldelights",names[k]);if(b!=null){w.setBlock(198+k,79,204,Blocks.farmland,7,3);w.setBlock(198+k,80,204,b,k<3?(k==2?3:7):0,3);}}
                for(int k=0;k<4;k++){String leaf=Port.CONTENT.trees.get(k).get("leaves").getAsString();GardenBlock b=Port.CONTENT.blocks.get(leaf);w.setBlock(198+k*3,80,207,Blocks.log);w.setBlock(198+k*3,81,207,b,b.maxAge|8,3);}
                EntityPlayerMP p=(EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);p.capabilities.isCreativeMode=true;p.capabilities.allowFlying=true;p.capabilities.isFlying=true;p.sendPlayerAbilities();p.setPositionAndUpdate(205,84,193);p.inventory.setInventorySlotContents(0,new ItemStack(Port.CONTENT.items.get("orange")));p.inventoryContainer.detectAndSendChanges();}});stage=2;wait=100;return;}
        if(stage==2){mc.thePlayer.rotationYaw=0;mc.thePlayer.rotationPitch=25;mc.gameSettings.hideGUI=false;capture(mc,"foodports-garden.png");report.add("PASS: Original crop, fruit, crate and serving-model families render together in a native integrated world without OpenGL errors");
            try{for(String mod:new String[]{"croptopia","culturaldelights"}){Path reportPath=mc.mcDataDir.toPath().resolve("checks-"+mod+"-initial.txt");check(Files.exists(reportPath),"Integrated server checks did not finish for "+mod);String text=new String(Files.readAllBytes(reportPath),StandardCharsets.UTF_8);check(text.contains("Failed: 0"),"Integrated server checks failed: "+text);}}catch(java.io.IOException ex){throw new RuntimeException(ex);}stage=3;wait=20;return;}
        if(stage==3){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);mc.displayGuiScreen(new GuiMainMenu());stage=4;wait=30;}
    }catch(Throwable t){t.printStackTrace();report.add("FAIL: stage "+stage+" :: "+t);finish(mc,true);}}
    private void capture(Minecraft mc,String name){check(GL11.glGetError()==GL11.GL_NO_ERROR,"OpenGL error before screenshot");net.minecraft.util.ScreenShotHelper.saveScreenshot(mc.mcDataDir,name,mc.displayWidth,mc.displayHeight,mc.getFramebuffer());}
    private void finish(Minecraft mc,boolean failed){done=true;report.add("Failed: "+(failed?1:0));try{Files.write(mc.mcDataDir.toPath().resolve("checks-client.txt"),report,StandardCharsets.UTF_8);}catch(Exception e){throw new RuntimeException(e);}if(mc.theWorld!=null){mc.theWorld.sendQuittingDisconnectingPacket();mc.loadWorld(null);}mc.shutdown();}
}
