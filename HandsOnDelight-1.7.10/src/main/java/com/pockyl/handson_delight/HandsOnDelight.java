package com.pockyl.handson_delight;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

@Mod(modid="handson_delight",name="Hands-On Delight",version=HandsOnDelight.VERSION,dependencies="required-after:farmersdelight@[1.1.1]")
public final class HandsOnDelight {
    public static final String VERSION="1.5.1-backport.4";
    public static final SimpleNetworkWrapper NETWORK=NetworkRegistry.INSTANCE.newSimpleChannel("handson_delight");
    @SidedProxy(clientSide="com.pockyl.handson_delight.client.ClientProxy",serverSide="com.pockyl.handson_delight.CommonProxy") public static CommonProxy proxy;
    public static Logger log;
    @Mod.EventHandler public void postInit(FMLPostInitializationEvent e){proxy.ready();}
    @Mod.EventHandler public void stopped(FMLServerStoppedEvent e){Sessions.clearServer();}
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent e){
        log=e.getModLog();NETWORK.registerMessage(Packets.ActionHandler.class,Packets.Action.class,0,Side.SERVER);NETWORK.registerMessage(Packets.OpenHandler.class,Packets.Open.class,1,Side.CLIENT);
        NETWORK.registerMessage(Packets.PracticeHandler.class,Packets.Practice.class,2,Side.CLIENT);NETWORK.registerMessage(Packets.FilledHandler.class,Packets.Filled.class,3,Side.CLIENT);
        Sessions events=new Sessions();MinecraftForge.EVENT_BUS.register(events);FMLCommonHandler.instance().bus().register(events);proxy.init();
    }
}
