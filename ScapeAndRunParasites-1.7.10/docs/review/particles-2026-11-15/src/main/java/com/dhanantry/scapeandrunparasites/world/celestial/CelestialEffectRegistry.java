/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData;
import com.dhanantry.scapeandrunparasites.world.celestial.ICelestialEventEffect;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.World;

public final class CelestialEffectRegistry {
    private static final Map<String, ICelestialEventEffect> EFFECTS = new HashMap<String, ICelestialEventEffect>();

    private CelestialEffectRegistry() {
    }

    public static void register(String id, ICelestialEventEffect effect) {
        if (id == null || effect == null) {
            return;
        }
        EFFECTS.put(id, effect);
    }

    public static ICelestialEventEffect get(String id) {
        return EFFECTS.get(id);
    }

    public static Set<String> getActiveIds(World world) {
        if (world == null || world.isRemote) {
            return Collections.emptySet();
        }
        int dim = world.provider.dimensionId;
        CelestialNightData night = CelestialNightData.get(world);
        CelestialNightData.DimState s = night.getState(dim);
        if (s == null) {
            return Collections.emptySet();
        }
        boolean blackSky = s.active.contains("dark_days") || s.forced.contains("dark_days");
        HashSet<String> out = new HashSet<String>();
        if (blackSky) {
            out.add("dark_days");
            return out;
        }
        out.addAll(s.active);
        out.addAll(s.forced);
        return out;
    }
}

