/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.particle.ParticleSnowShovel
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.fx;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.ParticleSnowShovel;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ParticleBlizzard
extends ParticleSnowShovel {
    private final EntityPlayerSP focus;

    public ParticleBlizzard(World w, double x, double y, double z, double vx, double vy, double vz, EntityPlayerSP focus) {
        super(w, x, y, z, vx, vy, vz);
        this.focus = focus;
        this.particleScale = 0.8f + this.rand.nextFloat() * 0.6f;
        this.particleGravity = 0.06f;
        this.particleAlpha = 0.95f;
        this.canCollide = true;
        this.particleMaxAge = 200;
        this.motionX = vx;
        this.motionY = vy;
        this.motionZ = vz;
    }

    public void onUpdate() {
        this.motionY -= 0.02;
        super.onUpdate();
        if (this.onGround) {
            this.setExpired();
            return;
        }
        if (this.focus != null && this.focus.getDistanceSq(this.posX, this.posY, this.posZ) > 400.0) {
            this.setExpired();
        }
    }
}

