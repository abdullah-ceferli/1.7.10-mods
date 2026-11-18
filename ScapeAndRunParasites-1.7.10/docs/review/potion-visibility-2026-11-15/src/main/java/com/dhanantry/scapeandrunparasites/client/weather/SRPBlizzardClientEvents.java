/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Pre
 *  net.minecraftforge.event.world.WorldEvent$Load
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client.weather;

import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardClient;
import com.dhanantry.scapeandrunparasites.client.world.SRPClientStarWorldState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class SRPBlizzardClientEvents {
    private SRPBlizzardClientEvents() {
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            return;
        }
        SRPClientStarWorldState.reset();
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            return;
        }
        SRPClientStarWorldState.reset();
    }

    @SubscribeEvent
    public static void onBlizzardOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        float intensity = SRPBlizzardClient.getIntensity(event.getPartialTicks());
        if (intensity <= 0.001f) {
            return;
        }
        float alpha = MathHelper.clamp_float((float)(intensity * 0.24f), (float)0.0f, (float)0.24f);
        int alphaByte = MathHelper.clamp_int((int)((int)(alpha * 255.0f)), (int)0, (int)255);
        int red = 205;
        int green = 215;
        int blue = 224;
        int color = alphaByte << 24 | red << 16 | green << 8 | blue;
        Gui.drawRect((int)0, (int)0, (int)event.getResolution().getScaledWidth(), (int)event.getResolution().getScaledHeight(), (int)color);
    }
}

