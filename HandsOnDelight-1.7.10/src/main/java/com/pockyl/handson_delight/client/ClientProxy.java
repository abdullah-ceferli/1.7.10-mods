package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import vectorwing.farmersdelight.legacy.*;

public final class ClientProxy extends com.pockyl.handson_delight.CommonProxy {
    private final ConcurrentLinkedQueue<Packets.Open> opening=new ConcurrentLinkedQueue<Packets.Open>();
    private final java.util.Map<Long,Integer> openingAge=new java.util.HashMap<Long,Integer>();
    private final ConcurrentLinkedQueue<Runnable> tasks=new ConcurrentLinkedQueue<Runnable>();
    public static net.minecraft.nbt.NBTTagCompound practice=new net.minecraft.nbt.NBTTagCompound();
    public void practice(final Packets.Practice p){tasks.offer(new Runnable(){public void run(){practice=p.data;}});}
    public void filled(final Packets.Filled p){tasks.offer(new Runnable(){public void run(){Minecraft mc=Minecraft.getMinecraft();StationScreen s=mc.currentScreen instanceof StationScreen?(StationScreen)mc.currentScreen:StationScreen.visiting;if(s!=null&&s.token==p.token)s.book.confirm(p.recipe,p.success);}});}
    public void init(){FMLCommonHandler.instance().bus().register(this);net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);if(cpw.mods.fml.common.Loader.isModLoaded("NotEnoughItems"))com.pockyl.handson_delight.compat.nei.NEIHandsOnDelightConfig.preInit();}
    @SubscribeEvent public void hand(net.minecraftforge.client.event.RenderHandEvent e){if(KitchenCamera.active())e.setCanceled(true);}
    @SubscribeEvent public void hud(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre e){if(KitchenCamera.active()&&e.type!=net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL)e.setCanceled(true);}
    @SubscribeEvent public void render(TickEvent.RenderTickEvent e){if(e.phase==TickEvent.Phase.START)KitchenCamera.frame();}
    @SubscribeEvent public void unload(net.minecraftforge.event.world.WorldEvent.Unload e){if(e.world.isRemote){KitchenCamera.restore();opening.clear();openingAge.clear();StationScreen.visiting=null;}}
    public void ready(){
        ClientRegistry.bindTileEntitySpecialRenderer(GrillingTile.class,new StationRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CuttingBoardTile.class,new StationRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CookingPotTile.class,new StationRenderer());
    }
    public void open(Packets.Open p){opening.offer(p);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){
        if(e.phase!=TickEvent.Phase.END)return;Packets.Open p;Minecraft mc=Minecraft.getMinecraft();
        Runnable task;while((task=tasks.poll())!=null)task.run();
        if(StationScreen.visiting!=null&&mc.currentScreen!=StationScreen.visiting&&!com.pockyl.handson_delight.compat.nei.NEIHooks.related(mc.currentScreen,StationScreen.visiting))StationScreen.visiting.leaveRecipeViewer();
        for(int attempts=opening.size();attempts>0&&(p=opening.poll())!=null;attempts--){
            if(mc.theWorld==null||mc.thePlayer==null||mc.thePlayer.dimension!=p.dimension)continue;
            TileEntity t=mc.theWorld.getTileEntity(p.x,p.y,p.z);
            if(StationHooks.supported(t)){openingAge.remove(p.token);mc.displayGuiScreen(new StationScreen((InventoryTile)t,p.token));}
            else {int age=openingAge.containsKey(p.token)?openingAge.get(p.token):0;if(age<40){openingAge.put(p.token,age+1);opening.offer(p);}else openingAge.remove(p.token);}
        }
    }
}
