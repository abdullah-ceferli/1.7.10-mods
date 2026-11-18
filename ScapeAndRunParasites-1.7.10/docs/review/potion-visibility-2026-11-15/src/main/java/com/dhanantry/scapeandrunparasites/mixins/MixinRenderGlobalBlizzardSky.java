/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderGlobal
 *  net.minecraft.world.WorldProvider
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.dhanantry.scapeandrunparasites.mixins;

import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderGlobal.class})
public abstract class MixinRenderGlobalBlizzardSky {
    @Redirect(method={"renderSky(FI)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/WorldProvider;calcSunriseSunsetColors(FF)[F"))
    private float[] srp$hideSunriseSunset(WorldProvider provider, float celestialAngle, float partialTicks) {
        if (SRPBlizzardClient.getIntensity(partialTicks) > 0.001f) {
            return null;
        }
        return provider.calcSunriseSunsetColors(celestialAngle, partialTicks);
    }
}

