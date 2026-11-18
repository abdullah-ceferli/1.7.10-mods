/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.shader;

import com.dhanantry.scapeandrunparasites.client.shader.BreatheShaderManager;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class DistortedEnlightenmentShaderEvents {
    private static boolean wasActive = false;

    private DistortedEnlightenmentShaderEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            if (wasActive) {
                BreatheShaderManager.get().disable();
                wasActive = false;
            }
            return;
        }
        boolean active = mc.thePlayer.isPotionActive(SRPPotions.DISTORTED_ENLIGHTENMENT_E);
        if (active) {
            BreatheShaderManager.get().enableFor(8);
            wasActive = true;
        } else if (wasActive) {
            BreatheShaderManager.get().disable();
            wasActive = false;
        }
    }
}

