package dev.zahin.mobileperformance.test;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import net.minecraft.server.MinecraftServer;

@Mod(modid="mobileperformancetests",name="Mobile Performance native checks",version="1",dependencies="required-after:mobileperformance")
public final class NativeChecks {
    private int ticks;
    @Mod.EventHandler public void init(FMLInitializationEvent event){if(!Boolean.getBoolean("mobileperformance.test"))return;if(event.getSide()==Side.CLIENT)ClientChecks.install();else FMLCommonHandler.instance().bus().register(this);}
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event)throws Exception{if(event.phase==TickEvent.Phase.END&&++ticks==20){Files.write(Paths.get("checks-server.txt"),Arrays.asList("PASS: Client-only mod loads on a dedicated server without initializing client rendering", "Failed: 0"),StandardCharsets.UTF_8);MinecraftServer.getServer().initiateShutdown();}}
}
