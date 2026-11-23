package dev.zahin.zalithrender;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dev.zahin.zalithrender.render.RenderHooks;
import dev.zahin.zalithrender.render.Settings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid="zalithrender",name="ZalithRender",version="0.2.0",acceptedMinecraftVersions="[1.7.10]",acceptableRemoteVersions="*")
public final class ZalithRender {
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        Configuration config=new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        Settings.enabled=config.get("rendering","enabled",true,"Use the chunk vertex-buffer cache when compatible.").getBoolean(true);
        Settings.debugOverlay=config.get("rendering","debugOverlay",true,"Show cache statistics on the F3 screen.").getBoolean(true);
        Settings.cacheBytes=clamp(config.get("rendering","cacheMiB",32,"Maximum additional GPU mesh memory; original display lists are retained for fallback.").getInt(32),4,128)*1024*1024;
        Settings.maxMeshBytes=clamp(config.get("rendering","maxMeshKiB",2048,"Maximum size of one cached chunk pass.").getInt(2048),64,4096)*1024;
        Settings.maxMeshes=clamp(config.get("rendering","maxMeshes",4096,"Maximum number of cached chunk passes.").getInt(4096),64,8192);
        Settings.uploadsPerFrame=clamp(config.get("rendering","uploadsPerFrame",8,"Limit additional GPU uploads during a frame; other chunks use normal rendering.").getInt(8),1,32);
        Settings.uploadBudgetNanos=clamp(config.get("rendering","uploadBudgetMicros",2000,"Stop starting extra cache work after this much CPU time in one frame; one driver call can overrun. 0 disables the time limit.").getInt(2000),0,10000)*1000L;
        Settings.framePacing=config.get("pacing","enabled",true,"Sleep between complete frames to reduce unnecessary rendering and power use; respects VSync and lower native FPS caps.").getBoolean(true);
        Settings.targetFps=clamp(config.get("pacing","targetFps",60,"Requested complete frames per second, not a guaranteed minimum. Set 70 for a 70 FPS cap.").getInt(60),30,120);
        if(config.hasChanged())config.save();
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        if(!FMLCommonHandler.instance().getSide().isClient())return;
        for(ModContainer mod:Loader.instance().getActiveModList()) {
            String id=mod.getModId().toLowerCase(java.util.Locale.ROOT);
            for(String incompatible:new String[]{"angelica","neodymium","beddium","fastcraft","falsetweaks","shadersmod","betterfoliage","rple"}) {
                if(id.equals(incompatible))RenderHooks.block("another rendering mod: "+mod.getModId());
            }
        }
        try { Class.forName("optifine.OptiFineClassTransformer",false,getClass().getClassLoader());RenderHooks.block("OptiFine rendering pipeline"); }
        catch(ClassNotFoundException expected){}
        ClientEvents events=new ClientEvents();
        FMLCommonHandler.instance().bus().register(events);MinecraftForge.EVENT_BUS.register(events);
        System.out.println("[ZalithRender] 0.2.0 loaded. Chunk rendering cache and frame pacing by Zahin.");
    }
    private static int clamp(int value,int min,int max){return Math.max(min,Math.min(max,value));}
}
