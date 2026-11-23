package dev.zahin.mobileperformance;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import dev.zahin.mobileperformance.client.ClientRuntime;

@Mod(modid="mobileperformance",name="Mobile Performance",version="1.0.0",acceptableRemoteVersions="*",acceptedMinecraftVersions="[1.7.10]")
public final class MobilePerformance {
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        if(event.getSide()==Side.CLIENT) ClientRuntime.install(event.getModConfigurationDirectory(),event.getModLog());
    }
}
