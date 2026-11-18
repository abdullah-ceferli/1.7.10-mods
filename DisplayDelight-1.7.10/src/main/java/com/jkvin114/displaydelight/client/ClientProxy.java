package com.jkvin114.displaydelight.client;

import com.jkvin114.displaydelight.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;

public final class ClientProxy extends CommonProxy {
    @Override public void initialize(){
        DisplayBlock.renderId=RenderingRegistry.getNextAvailableRenderId();
        DisplayRenderer renderer=new DisplayRenderer();
        RenderingRegistry.registerBlockHandler(renderer);MinecraftForge.EVENT_BUS.register(renderer);
    }
}
