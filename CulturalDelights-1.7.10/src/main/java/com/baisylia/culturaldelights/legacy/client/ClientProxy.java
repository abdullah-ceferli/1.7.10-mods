package com.baisylia.culturaldelights.legacy.client;
import com.baisylia.culturaldelights.legacy.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;
public final class ClientProxy extends Proxy {
    public static NativeRender renderer;
    public void prepare(){renderer=new NativeRender();GardenBlock.renderId=renderer.id;MinecraftForge.EVENT_BUS.register(renderer);RenderingRegistry.registerBlockHandler(renderer);}
}
