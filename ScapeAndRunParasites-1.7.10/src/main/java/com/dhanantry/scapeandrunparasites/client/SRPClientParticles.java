/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.TextureStitchEvent$Pre
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleKirinWarning;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(value={Side.CLIENT})
public final class SRPClientParticles {
    public static final TextureAtlasSprite[] INFESTED_LEAF = new TextureAtlasSprite[4];
    public static TextureAtlasSprite KIRIN_WARNING;

    private SRPClientParticles() {
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre e) {
        TextureMap map = e.getMap();
        SRPClientParticles.INFESTED_LEAF[0] = map.registerSprite(new ResourceLocation("srparasites:particle/infested_leaves"));
        SRPClientParticles.INFESTED_LEAF[1] = map.registerSprite(new ResourceLocation("srparasites:particle/infested_leaves2"));
        SRPClientParticles.INFESTED_LEAF[2] = map.registerSprite(new ResourceLocation("srparasites:particle/infested_leaves3"));
        SRPClientParticles.INFESTED_LEAF[3] = map.registerSprite(new ResourceLocation("srparasites:particle/infested_leaves4"));
        KIRIN_WARNING = map.registerSprite(new ResourceLocation("srparasites:particle/kirin_warning"));
    }

    public static TextureAtlasSprite randomInfestedLeaf(Random rand) {
        return INFESTED_LEAF[rand.nextInt(INFESTED_LEAF.length)];
    }

    public static void spawnKirinWarning(World world, double x, double y, double z, float sizeBlocks, float yawRad, int maxAgeTicks) {
        if (!world.isRemote) {
            return;
        }
        ParticleKirinWarning p = new ParticleKirinWarning(world, x, y, z, sizeBlocks, yawRad, maxAgeTicks);
        Minecraft.getMinecraft().effectRenderer.addEffect((Particle)p);
    }
}

