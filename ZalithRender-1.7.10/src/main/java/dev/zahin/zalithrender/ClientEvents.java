package dev.zahin.zalithrender;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.zahin.zalithrender.render.RenderHooks;
import dev.zahin.zalithrender.render.FrameHooks;
import dev.zahin.zalithrender.render.Settings;
import java.lang.reflect.Field;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public final class ClientEvents {
    private final int forgeBusId;
    private Field gameSettings, debug;
    public ClientEvents() {
        int id=-1;
        try {Field field=EventBus.class.getDeclaredField("busID");field.setAccessible(true);id=field.getInt(MinecraftForge.EVENT_BUS);}
        catch(ReflectiveOperationException ex){RenderHooks.block("world-hook compatibility check unavailable");}
        forgeBusId=id;
    }
    @SubscribeEvent public void frame(TickEvent.RenderTickEvent event) {
        if(event.phase!=TickEvent.Phase.START)return;
        if(forgeBusId>=0) {
            boolean custom=new RenderWorldEvent.Pre(null,null,null,0).getListenerList().getListeners(forgeBusId).length!=0
                ||new RenderWorldEvent.Post(null,null,null,0).getListenerList().getListeners(forgeBusId).length!=0;
            RenderHooks.worldHooks(custom);
        }
        RenderHooks.frameStart();
    }
    @SubscribeEvent public void unload(WorldEvent.Unload event) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())RenderHooks.resetLater();
    }
    @SubscribeEvent public void text(RenderGameOverlayEvent.Text event) {
        if(!Settings.debugOverlay)return;
        try {
            Object client=FMLClientHandler.instance().getClient();
            if(gameSettings==null)gameSettings=find(client.getClass(),"gameSettings","field_71474_y");
            Object settings=gameSettings.get(client);
            if(debug==null)debug=find(settings.getClass(),"showDebugInfo","field_74330_P");
            if(debug.getBoolean(settings)) { event.right.add(RenderHooks.status()); event.right.add(FrameHooks.status()); }
        }catch(ReflectiveOperationException ignored){}
    }
    private static Field find(Class<?> owner,String mcp,String srg)throws NoSuchFieldException {
        Field field;
        try{field=owner.getDeclaredField(mcp);}catch(NoSuchFieldException ex){field=owner.getDeclaredField(srg);}
        field.setAccessible(true);return field;
    }
}
