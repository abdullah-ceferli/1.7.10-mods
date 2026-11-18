/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound$AttenuationType
 *  net.minecraft.client.audio.MovingSound
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.util.SoundCategory
 */
package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.client.celestial.BlackSkyClient;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.entity.EntityPlayerSP;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;

public class DarkDaysRumbleSound
extends MovingSound {
    public DarkDaysRumbleSound() {
        super(SRPSounds.DARK_DAYS_RUMBLE, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.AMBIENT);
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.75f;
        this.pitch = 1.0f;
        this.attenuationType = ISound.AttenuationType.NONE;
    }

    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null || !BlackSkyClient.isDarkDaysActive()) {
            this.donePlaying = true;
            return;
        }
        this.xPosF = (float)player.posX;
        this.yPosF = (float)player.posY;
        this.zPosF = (float)player.posZ;
    }
}

