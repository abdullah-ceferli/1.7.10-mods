/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.client.celestial.CelestialPhaseClient;
import net.minecraft.client.Minecraft;

public final class BlackSkyClient {
    private BlackSkyClient() {
    }

    public static boolean isDarkDaysActive() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) {
            return false;
        }
        if (!mc.theWorld.provider.isSurfaceWorld()) {
            return false;
        }
        int dim = mc.theWorld.provider.dimensionId;
        return CelestialPhaseClient.getActiveIds(dim).contains("dark_days") || CelestialPhaseClient.getForcedIds(dim).contains("dark_days");
    }

    public static boolean isBlackSkyActive() {
        return BlackSkyClient.isDarkDaysActive();
    }
}

