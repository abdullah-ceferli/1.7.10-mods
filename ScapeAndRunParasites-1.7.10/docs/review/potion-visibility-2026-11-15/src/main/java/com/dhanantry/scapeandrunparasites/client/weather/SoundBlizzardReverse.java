/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.ISound$AttenuationType
 *  net.minecraft.client.audio.MovingSound
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 */
package com.dhanantry.scapeandrunparasites.client.weather;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
public class SoundBlizzardReverse
extends MovingSound {
    private final EntityPlayer player;

    public SoundBlizzardReverse(String sound, EntityPlayer player) {
        super(sound, SoundCategory.WEATHER);
        this.player = player;
        this.repeat = false;
        this.repeatDelay = 0;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.updatePosition();
    }

    public void update() {
        if (this.player == null || this.player.isDead || this.player.worldObj == null) {
            this.donePlaying = true;
            return;
        }
        this.updatePosition();
    }

    private void updatePosition() {
        this.xPosF = (float)this.player.posX;
        this.yPosF = (float)(this.player.posY + (double)this.player.getEyeHeight());
        this.zPosF = (float)this.player.posZ;
    }
}

