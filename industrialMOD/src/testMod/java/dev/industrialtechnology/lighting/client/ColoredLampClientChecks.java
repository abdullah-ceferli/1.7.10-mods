package dev.industrialtechnology.lighting.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.industrialtechnology.lighting.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.lwjgl.opengl.GL11;

public final class ColoredLampClientChecks {
    private int phase,wait=25,age,failed;private boolean done;private volatile boolean ready;private volatile Throwable problem;
    private final ConcurrentLinkedQueue<Runnable> work=new ConcurrentLinkedQueue<Runnable>();private final List<String> report=new ArrayList<String>();
    public static void install(){FMLCommonHandler.instance().bus().register(new ColoredLampClientChecks());}
    private static void check(boolean value,String why){if(!value)throw new AssertionError(why);}
    private void server(Runnable r){ready=false;work.add(()->{try{r.run();}catch(Throwable t){problem=t;}finally{ready=true;}});}
    @SubscribeEvent public void serverTick(TickEvent.ServerTickEvent e){if(e.phase==TickEvent.Phase.END){Runnable r;while((r=work.poll())!=null)r.run();}}
    private void shot(Minecraft mc,String name){check(GL11.glGetError()==0,"Lamp rendering produced OpenGL error");ScreenShotHelper.saveScreenshot(mc.mcDataDir,name,mc.displayWidth,mc.displayHeight,mc.getFramebuffer());}
    private void look(Minecraft mc,double x,double y,double z){double dx=x-mc.thePlayer.posX,dz=z-mc.thePlayer.posZ,dy=y-mc.thePlayer.posY-mc.thePlayer.getEyeHeight();mc.thePlayer.rotationYaw=(float)Math.toDegrees(Math.atan2(-dx,dz));mc.thePlayer.rotationPitch=(float)-Math.toDegrees(Math.atan2(dy,Math.sqrt(dx*dx+dz*dz)));}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||--wait>0)return;Minecraft mc=Minecraft.getMinecraft();try{
        if(problem!=null)throw new AssertionError("Server fixture",problem);
        if(phase==0){if(!(mc.currentScreen instanceof GuiMainMenu)){wait=10;return;}mc.gameSettings.guiScale=2;mc.gameSettings.renderDistanceChunks=4;mc.gameSettings.pauseOnLostFocus=false;mc.gameSettings.limitFramerate=60;mc.gameSettings.showInventoryAchievementHint=false;WorldSettings settings=new WorldSettings(214,WorldSettings.GameType.CREATIVE,false,false,WorldType.FLAT);settings.func_82750_a("2;7;1;");mc.launchIntegratedServer("colored-lamps","Disposable colored lamp checks",settings);phase=1;wait=45;return;}
        if(mc.thePlayer==null||mc.theWorld==null){check(++age<100,"Client world join timed out");wait=10;return;}
        if(phase==1){mc.displayGuiScreen(null);server(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);EntityPlayerMP p=(EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(0);for(int x=27;x<60;x++)for(int z=27;z<43;z++)w.setBlock(x,69,z,Blocks.stone,0,2);for(int c=0;c<16;c++){int x=30+c%4*3,z=30+c/4*3;w.setBlock(x,70,z,ColoredLampRegistry.always,c,3);w.setBlock(x+16,70,z,ColoredLampRegistry.redstone,c,3);}w.setWorldTime(18000);w.getGameRules().setOrCreateGameRule("doDaylightCycle","false");p.capabilities.isFlying=true;p.sendPlayerAbilities();p.setPositionAndUpdate(43,77,22);p.inventory.mainInventory[0]=new ItemStack(ColoredLampRegistry.always,64,0);p.inventory.mainInventory[1]=new ItemStack(ColoredLampRegistry.always,64,11);p.inventory.mainInventory[2]=new ItemStack(ColoredLampRegistry.redstone,64,13);p.inventory.currentItem=0;p.sendContainerToPlayer(p.inventoryContainer);});phase=2;wait=50;return;}
        if(phase==2){if(!ready){wait=10;return;}mc.thePlayer.capabilities.isFlying=true;look(mc,43,70.5,35);Set<Integer> colors=new HashSet<Integer>();for(BlockColoredLamp b:new BlockColoredLamp[]{ColoredLampRegistry.always,ColoredLampRegistry.redstone}){List<ItemStack> stacks=new ArrayList<ItemStack>();b.getSubBlocks(Item.getItemFromBlock(b),b.getCreativeTabToDisplayOn(),stacks);check(stacks.size()==16,"Creative tab lost color variants");for(ItemStack s:stacks){check(!s.getDisplayName().startsWith("tile."),"Missing translated lamp name");check(s.getIconIndex()!=null,"Missing native lamp icon");colors.add(b.getRenderColor(s.getItemDamage()));}}check(colors.size()==32,"On/off palette does not distinguish the 32 appearances");report.add("PASS: All 32 native lamp inventory variants have names, icons and distinct on/off color appearances");phase=3;wait=15;return;}
        if(phase==3){for(int c=0;c<16;c++){int x=30+c%4*3,z=30+c/4*3;check(mc.theWorld.getBlock(x,70,z)==ColoredLampRegistry.always&&mc.theWorld.getBlockMetadata(x,70,z)==c,"Always-on client metadata "+c);check(mc.theWorld.getBlock(x+16,70,z)==ColoredLampRegistry.redstone&&mc.theWorld.getBlockMetadata(x+16,70,z)==c,"Off client metadata "+c);}shot(mc,"colored-lamps-off.png");report.add("PASS: Native chunk rendering displays every always-on/off color in a real night-time world without GL errors");server(()->{WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);for(int c=0;c<16;c++)w.setBlock(46+c%4*3,69,30+c/4*3,Blocks.redstone_block,0,3);});phase=4;wait=40;return;}
        if(phase==4){if(!ready){wait=5;return;}for(int c=0;c<16;c++){int x=46+c%4*3,z=30+c/4*3;check(mc.theWorld.getBlock(x,70,z)==ColoredLampRegistry.powered&&mc.theWorld.getBlockMetadata(x,70,z)==c,"Powered state or color did not sync "+c);check(mc.theWorld.getSavedLightValue(EnumSkyBlock.Block,x,70,z)==15,"Native light did not sync "+c);}shot(mc,"colored-lamps-on.png");report.add("PASS: All 16 redstone colors switch and synchronize native block light to the client");
            // Query native NEI handlers when present; no custom handler or layout is needed.
            try{Class<?> shaped=Class.forName("codechicken.nei.recipe.ShapedRecipeHandler"),shapeless=Class.forName("codechicken.nei.recipe.ShapelessRecipeHandler");for(int c=0;c<16;c++)for(int type=0;type<2;type++){Object handler=(type==0?shaped:shapeless).newInstance();handler.getClass().getMethod("loadCraftingRecipes",ItemStack.class).invoke(handler,new ItemStack(type==0?ColoredLampRegistry.always:ColoredLampRegistry.redstone,1,c));check(!((List<?>)handler.getClass().getField("arecipes").get(handler)).isEmpty(),"NEI recipe page missing "+c+" / "+type);}report.add("PASS: Native NEI handlers expose crafting recipes for all 32 lamp variants");}catch(ClassNotFoundException absent){throw new AssertionError("NEI was not installed in the client fixture",absent);}
            mc.displayGuiScreen(new net.minecraft.client.gui.inventory.GuiInventory(mc.thePlayer));phase=5;wait=15;return;}
        if(phase==5){shot(mc,"colored-lamps-inventory.png");mc.displayGuiScreen(null);report.add("PASS: Lamp hotbar/inventory rendering preserves the normal game UI and GL state");finish(mc);}
    }catch(Throwable t){failed++;report.add("FAIL: client fixture "+t);t.printStackTrace();finish(mc);}}
    private void finish(Minecraft mc){done=true;report.add("Failed: "+failed);try{Files.write(mc.mcDataDir.toPath().resolve("colored-lamps-client.txt"),report,StandardCharsets.UTF_8);}catch(Exception e){throw new RuntimeException(e);}mc.shutdown();}
}
