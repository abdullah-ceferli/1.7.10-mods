/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fml.common.Loader
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.SidedProxy
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLServerStartingEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
 *  org.apache.logging.log4j.Logger
 */
package com.dhanantry.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.compatibility.ModCompatibility;
import com.dhanantry.scapeandrunparasites.proxy.CommonProxy;
import com.dhanantry.scapeandrunparasites.util.SRPCreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

@Mod(modid="srparasites", name="Scape and Run Parasites", version="1.10.9-backport-dev", acceptedMinecraftVersions="[1.7.10]", dependencies="before:jeid@[2.2.5,)")
public class SRPMain {
    @Mod.Instance
    public static SRPMain instance;
    @SidedProxy(clientSide="com.dhanantry.scapeandrunparasites.proxy.ClientProxy", serverSide="com.dhanantry.scapeandrunparasites.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static final SRPCreativeTabs SRP_CREATIVETAB;
    public static Logger logger;
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        com.dhanantry.scapeandrunparasites.backport.potion.PotionFxEvents.register();
        com.dhanantry.scapeandrunparasites.backport.potion.NativePotionRegistry.initialize(new java.io.File(event.getModConfigurationDirectory(), "srparasites-potion-ids.cfg"));
        logger = event.getModLog();
        proxy.preInit(event);
        ModCompatibility.FLUIDLOGGED_API = Loader.isModLoaded((String)"fluidlogged_api");
    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void Postinit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void Serverinit(FMLServerStartingEvent event) {
        proxy.serverInit(event);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation("srparasites", path);
    }

    static {
        SRP_CREATIVETAB = new SRPCreativeTabs("SRParasites");
        FluidRegistry.enableUniversalBucket();
    }
}

