package com.dhanantry.scapeandrunparasites.backport.client;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;import com.dhanantry.scapeandrunparasites.backport.datasync.*;
/** Loaded only by the physical client proxy. Common network handlers never resolve Minecraft client classes. */
public final class EntityDataClient {
    private static boolean initialized;
    public static synchronized void initialize(){if(initialized)return;initialized=true;FMLCommonHandler.instance().bus().register(new EntityDataClient());}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){if(event.phase==TickEvent.Phase.END)NativeEntityDataSync.CLIENT_INBOX.tick(Minecraft.getMinecraft().theWorld,NativeEntityDataSync::request);}
    @SubscribeEvent public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){NativeEntityDataSync.CLIENT_INBOX.clear();}
}
