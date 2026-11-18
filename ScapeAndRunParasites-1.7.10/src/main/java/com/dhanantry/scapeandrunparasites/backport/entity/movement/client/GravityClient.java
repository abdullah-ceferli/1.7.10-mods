package com.dhanantry.scapeandrunparasites.backport.entity.movement.client;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.relauncher.*;import net.minecraft.client.Minecraft;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityNetwork;
@SideOnly(Side.CLIENT)
public final class GravityClient {
    private static boolean initialized;public static synchronized void initialize(){if(initialized)return;initialized=true;FMLCommonHandler.instance().bus().register(new GravityClient());}
    @SubscribeEvent public void disconnect(cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent event){GravityNetwork.INBOX.clear();}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(e.phase==TickEvent.Phase.END)GravityNetwork.INBOX.tick(Minecraft.getMinecraft().theWorld,GravityNetwork::request);}
}
