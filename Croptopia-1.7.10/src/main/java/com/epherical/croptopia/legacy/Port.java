package com.epherical.croptopia.legacy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid="croptopia",name="Croptopia",version="4.0.1-backport.2",acceptedMinecraftVersions="[1.7.10]",dependencies="required-after:farmersdelight@[1.1.1,)")
public final class Port {
    @SidedProxy(clientSide="com.epherical.croptopia.legacy.client.ClientProxy",serverSide="com.epherical.croptopia.legacy.Proxy") public static Proxy proxy;
    public static final Catalog CONTENT=new Catalog();
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        CONTENT.register(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new HarvestEvents());
        GameRegistry.registerWorldGenerator(new Gardens(), 30);
        proxy.prepare();
        if(Boolean.getBoolean("foodports.qa"))try {cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(Class.forName("com.epherical.croptopia.legacy.Checks").newInstance());}catch(Exception e){throw new IllegalStateException(e);}
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) { CONTENT.recipes(); proxy.init(); }
    @Mod.EventHandler public void postInit(FMLPostInitializationEvent event) { CONTENT.report(); }
}
