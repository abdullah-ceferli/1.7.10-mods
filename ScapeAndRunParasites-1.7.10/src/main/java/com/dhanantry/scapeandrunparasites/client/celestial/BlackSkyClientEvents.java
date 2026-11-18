/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.util.SoundCategory
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.client.celestial.BlackSkyClient;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialSkyRenderer;
import com.dhanantry.scapeandrunparasites.client.celestial.DarkDaysRumbleSound;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class BlackSkyClientEvents {
    private static boolean mutedMusic = false;
    private static float oldMusicVolume = 1.0f;
    private static DarkDaysRumbleSound rumbleSound = null;

    private BlackSkyClientEvents() {
    }

    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors e) {
        if (!SRPConfigWorld.enableCelestialObjects) {
            return;
        }
        if (!BlackSkyClient.isDarkDaysActive()) {
            return;
        }
        if (CelestialSkyRenderer.isSRPDarkDaysShaderPackActive()) {
            return;
        }
        e.setRed(0.0f);
        e.setGreen(0.0f);
        e.setBlue(0.0f);
    }

    @SubscribeEvent
    public static void onFogDensity(EntityViewRenderEvent.FogDensity e) {
        if (!SRPConfigWorld.enableCelestialObjects) {
            return;
        }
        if (!BlackSkyClient.isDarkDaysActive()) {
            return;
        }
        if (CelestialSkyRenderer.isSRPDarkDaysShaderPackActive()) {
            return;
        }
        e.setDensity(0.003f);
        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        boolean active;
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.gameSettings == null) {
            return;
        }
        boolean bl = active = mc.theWorld != null && mc.thePlayer != null && BlackSkyClient.isDarkDaysActive();
        if (active) {
            if (!mutedMusic) {
                oldMusicVolume = mc.gameSettings.getSoundLevel(com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.MUSIC);
                mutedMusic = true;
            }
            mc.gameSettings.setSoundLevel(com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.MUSIC, 0.0f);
            if (rumbleSound == null || !mc.getSoundHandler().isSoundPlaying((ISound)rumbleSound)) {
                rumbleSound = new DarkDaysRumbleSound();
                mc.getSoundHandler().playSound((ISound)rumbleSound);
            }
            return;
        }
        rumbleSound = null;
        if (mutedMusic) {
            mc.gameSettings.setSoundLevel(com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.MUSIC, oldMusicVolume);
            mutedMusic = false;
        }
    }
}

