/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.client.weather;

import com.dhanantry.scapeandrunparasites.client.world.SRPClientStarWorldState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public final class SRPBlizzardClient {
    private SRPBlizzardClient() {
    }

    public static boolean isColdWorld() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.theWorld.provider == null || mc.theWorld.provider.dimensionId != 0) {
            return false;
        }
        return SRPClientStarWorldState.isCold();
    }

    public static float getIntensity(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!SRPBlizzardClient.isColdWorld()) {
            return 0.0f;
        }
        return MathHelper.clamp_float((float)mc.theWorld.getWeightedThunderStrength(partialTicks), (float)0.0f, (float)1.0f);
    }
}

