package com.baisylia.culturaldelights.legacy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid="culturaldelights",name="Cultural Delights",version="0.16.7-backport.1",acceptedMinecraftVersions="[1.7.10]",dependencies="required-after:farmersdelight@[1.1.1,)")
public final class Port {
    @SidedProxy(clientSide="com.baisylia.culturaldelights.legacy.client.ClientProxy",serverSide="com.baisylia.culturaldelights.legacy.Proxy") public static Proxy proxy;
    public static final Catalog CONTENT=new Catalog();
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        CONTENT.register(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new HarvestEvents());
        GameRegistry.registerWorldGenerator(new Gardens(), 30);
        proxy.prepare();
        if(Boolean.getBoolean("foodports.qa"))try {cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(Class.forName("com.baisylia.culturaldelights.legacy.Checks").newInstance());}catch(Exception e){throw new IllegalStateException(e);}
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) { CONTENT.recipes(); proxy.init(); }
    @Mod.EventHandler public void postInit(FMLPostInitializationEvent event) { CONTENT.report(); }
}
