/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.EntityRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$FogMode
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.biome.Biome
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.dhanantry.scapeandrunparasites.mixins;

import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardClient;
import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardFogRenderer;
import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardRenderer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRendererBlizzard {
    @Inject(method={"renderRainSnow(F)V"}, at={@At(value="RETURN")})
    private void srp$renderBlizzard(float partialTicks, CallbackInfo ci) {
        float intensity = SRPBlizzardClient.getIntensity(partialTicks);
        if (intensity <= 0.001f) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        SRPBlizzardFogRenderer.render(mc, partialTicks, intensity);
        SRPBlizzardRenderer.render(mc, partialTicks, intensity);
    }

    @Inject(method={"setupFog(IF)V"}, at={@At(value="TAIL")})
    private void srp$setupBlizzardFog(int startCoords, float partialTicks, CallbackInfo ci) {
        float intensity = SRPBlizzardClient.getIntensity(partialTicks);
        if (intensity <= 0.001f) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();
        if (camera == null) {
            return;
        }
        if (camera.isInsideOfMaterial(Material.water) || camera.isInsideOfMaterial(Material.lava)) {
            return;
        }
        float fogEnd = 72.0f - intensity * 56.0f;
        float fogStart = fogEnd * 0.08f;
        GlStateManager.setFog((GlStateManager.FogMode)GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart((float)fogStart);
        GlStateManager.setFogEnd((float)fogEnd);
    }

    @Redirect(method={"renderRainSnow(F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/biome/Biome;canRain()Z"), require=0)
    private boolean srp$disableVanillaRain(Biome biome) {
        if (SRPBlizzardClient.getIntensity(1.0f) > 0.001f) {
            return false;
        }
        return biome.canSpawnLightningBolt();
    }

    @Redirect(method={"renderRainSnow(F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/biome/Biome;getEnableSnow()Z"), require=0)
    private boolean srp$disableVanillaSnow(Biome biome) {
        if (SRPBlizzardClient.getIntensity(1.0f) > 0.001f) {
            return false;
        }
        return biome.getEnableSnow();
    }

    private static float lerp(float from, float to, float amount) {
        return from + (to - from) * amount;
    }
}

