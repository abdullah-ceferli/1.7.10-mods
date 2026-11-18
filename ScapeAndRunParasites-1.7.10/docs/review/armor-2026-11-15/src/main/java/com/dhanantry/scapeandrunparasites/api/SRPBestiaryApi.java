/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package com.dhanantry.scapeandrunparasites.api;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;

public final class SRPBestiaryApi {
    private SRPBestiaryApi() {
    }

    public static boolean hasSeenCelestial(EntityPlayer player, String celestialId) {
        if (player == null || celestialId == null || celestialId.isEmpty()) {
            return false;
        }
        IBestiaryProgress progress = (IBestiaryProgress)player.getCapability(BestiaryCapability.CAP, null);
        return progress != null && progress.hasSeenCelestial(celestialId);
    }

    public static Set<String> getSeenCelestials(EntityPlayer player) {
        if (player == null) {
            return Collections.emptySet();
        }
        IBestiaryProgress progress = (IBestiaryProgress)player.getCapability(BestiaryCapability.CAP, null);
        if (progress == null || progress.getSeenCelestials() == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<String>(progress.getSeenCelestials()));
    }

    public static int getSeenCelestialCount(EntityPlayer player) {
        return SRPBestiaryApi.getSeenCelestials(player).size();
    }
}

