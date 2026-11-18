/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectDefinition;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData;
import com.dhanantry.scapeandrunparasites.world.celestial.PacketCelestialNightState;
import java.util.Random;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

@Mod.EventBusSubscriber(modid="srparasites")
public class CelestialEventManager {
    private static final Random RAND = new Random();
    public static final int DARK_DAYS_DURATION_TICKS = 6000;
    public static final int DARK_DAYS_END_WARNING_TICKS = 200;
    public static final int DARK_DAYS_INTRO_DELAY_TICKS = 160;
    public static final int DARK_DAYS_OUTRO_DELAY_TICKS = 200;
    public static final long DARK_DAYS_ROLL_TIME = 800L;
    public static final long DARK_DAYS_ACTIVATION_TIME = 1000L;

    public static boolean isActive(World world, String id) {
        if (world == null || world.isRemote) {
            return false;
        }
        int dim = world.provider.dimensionId;
        CelestialNightData.DimState s = CelestialNightData.get(world).getState(dim);
        if (s == null) {
            return false;
        }
        return s.forced.contains(id) || s.active.contains(id);
    }

    public static void force(World world, String id, boolean enabled) {
        if (world == null || world.isRemote) {
            return;
        }
        int dim = world.provider.dimensionId;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        if (enabled) {
            s.forced.add(id);
        } else {
            s.forced.remove(id);
            s.active.remove(id);
            if ("dark_days".equals(id)) {
                s.darkDaysStartTime = -1L;
                s.darkDaysEndTime = -1L;
                s.darkDaysEndingSoundPlayed = false;
            }
        }
        if (s.forced.contains("dark_days")) {
            s.active.clear();
            s.forced.clear();
            s.forced.add("dark_days");
        }
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    private static void clearWeatherForDarkDays(World world) {
        if (world == null) {
            return;
        }
        world.getWorldInfo().setRaining(false);
        world.getWorldInfo().setThundering(false);
        world.getWorldInfo().setRainTime(0);
        world.getWorldInfo().setThunderTime(0);
        world.getWorldInfo().setCleanWeatherTime(6400);
    }

    public static void clearForced(World world) {
        if (world == null || world.isRemote) {
            return;
        }
        int dim = world.provider.dimensionId;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        s.forced.clear();
        s.active.remove("dark_days");
        s.darkDaysStartTime = -1L;
        s.darkDaysEndTime = -1L;
        s.darkDaysEndingSoundPlayed = false;
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    public static void startDarkDays(World world) {
        if (world == null || world.isRemote) {
            return;
        }
        if (!world.provider.isSurfaceWorld()) {
            return;
        }
        CelestialEventManager.clearWeatherForDarkDays(world);
        int dim = world.provider.dimensionId;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        s.active.clear();
        s.forced.clear();
        s.darkDaysStartTime = world.getWorldTime() + 160L;
        s.darkDaysEndTime = -1L;
        s.darkDaysEndingSoundPlayed = false;
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
        CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_START, 1.0f);
    }

    public static void stopDarkDays(World world) {
        boolean active;
        if (world == null || world.isRemote) {
            return;
        }
        if (!world.provider.isSurfaceWorld()) {
            return;
        }
        int dim = world.provider.dimensionId;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        boolean bl = active = s.forced.contains("dark_days") || s.active.contains("dark_days");
        if (!active) {
            s.darkDaysStartTime = -1L;
            s.darkDaysEndTime = -1L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
            return;
        }
        s.darkDaysStartTime = -1L;
        s.darkDaysEndTime = world.getWorldTime() + 200L;
        s.darkDaysEndingSoundPlayed = true;
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
        CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_ENDING, 1.0f);
    }

    public static void syncDim(World world, int dim) {
        if (world == null || world.isRemote) {
            return;
        }
        CelestialNightData.DimState s = CelestialNightData.get(world).getState(dim);
        if (s == null) {
            return;
        }
        PacketCelestialNightState pkt = new PacketCelestialNightState(dim, s.phase, s.nightIndex, s.active, s.forced);
        for (EntityPlayerMP p : world.getMinecraftServer().getPlayerList().getPlayers()) {
            if (p.worldObj.provider.dimensionId != dim) continue;
            SRPNetwork.CHANNEL.sendTo((IMessage)pkt, p);
        }
    }

    private static void rollIfNeeded(World world) {
        if (world == null || world.isRemote) {
            return;
        }
        if (!world.provider.isSurfaceWorld()) {
            return;
        }
        int dim = world.provider.dimensionId;
        long nightIndex = world.getWorldTime() / 24000L;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        if (s.forced.contains("dark_days")) {
            return;
        }
        if (s.nightIndex == nightIndex) {
            return;
        }
        s.nightIndex = nightIndex;
        s.active.clear();
        long baseSeed = world.getSeed() ^ nightIndex * 918273L;
        for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
            if ("dark_days".equals(def.id) || SRPConfigWorld.isCelestialEventBlacklisted(def.id) || !def.isPhaseAllowed(s.phase)) continue;
            long seed = baseSeed + (long)def.id.hashCode() * 31L;
            RAND.setSeed(seed);
            if (!(RAND.nextFloat() <= def.chancePerNight)) continue;
            s.active.add(def.id);
        }
        if (s.active.contains("dark_days")) {
            s.active.clear();
            s.active.add("dark_days");
        }
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    private static void tickDarkDays(World world) {
        boolean active;
        if (world == null || world.isRemote) {
            return;
        }
        if (!world.provider.isSurfaceWorld()) {
            return;
        }
        int dim = world.provider.dimensionId;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        long now = world.getWorldTime();
        if (s.darkDaysStartTime > 0L && now >= s.darkDaysStartTime) {
            s.darkDaysStartTime = -1L;
            s.active.clear();
            s.forced.clear();
            s.forced.add("dark_days");
            s.darkDaysEndTime = now + 6000L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
        }
        boolean bl = active = s.forced.contains("dark_days") || s.active.contains("dark_days");
        if (!active) {
            return;
        }
        if (s.darkDaysEndTime <= 0L) {
            s.darkDaysEndTime = now + 6000L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
            return;
        }
        if (!s.darkDaysEndingSoundPlayed && now >= s.darkDaysEndTime - 200L) {
            s.darkDaysEndingSoundPlayed = true;
            data.markDirty();
            CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_ENDING, 1.0f);
        }
        if (now >= s.darkDaysEndTime) {
            CelestialEventManager.grantDarkDaysAdvancement(world);
            s.forced.remove("dark_days");
            s.active.remove("dark_days");
            s.darkDaysStartTime = -1L;
            s.darkDaysEndTime = -1L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
        }
    }

    private static void playToDimension(World world, String sound, float pitch) {
        if (world == null || sound == null || world.getMinecraftServer() == null) {
            return;
        }
        int dim = world.provider.dimensionId;
        for (EntityPlayerMP p : world.getMinecraftServer().getPlayerList().getPlayers()) {
            if (p.worldObj.provider.dimensionId != dim) continue;
            p.worldObj.playSound(null, p.posX, p.posY, p.posZ, sound, SoundCategory.AMBIENT, 10000.0f, pitch);
        }
    }

    private static void rollDarkDaysIfNeeded(World world) {
        boolean activeOrQueued;
        if (world == null || world.isRemote) {
            return;
        }
        if (!world.provider.isSurfaceWorld()) {
            return;
        }
        int dim = world.provider.dimensionId;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        boolean bl = activeOrQueued = s.forced.contains("dark_days") || s.active.contains("dark_days") || s.darkDaysStartTime > 0L || s.darkDaysEndTime > 0L;
        if (activeOrQueued) {
            return;
        }
        long worldTime = world.getWorldTime();
        long dayStart = worldTime - worldTime % 24000L;
        long day = dayStart / 24000L;
        long dayTime = worldTime % 24000L;
        if (s.darkDaysLastRollDay == day) {
            return;
        }
        if (dayTime < 800L || dayTime >= 1000L) {
            return;
        }
        s.darkDaysLastRollDay = day;
        CelestialObjectDefinition def = CelestialObjectRegistry.getById("dark_days");
        if (def == null) {
            data.markDirty();
            return;
        }
        long seed = world.getSeed() ^ day * 918273L ^ (long)"dark_days".hashCode() * 31L;
        RAND.setSeed(seed);
        if (RAND.nextFloat() <= def.chancePerNight) {
            s.active.clear();
            s.forced.clear();
            CelestialEventManager.clearWeatherForDarkDays(world);
            s.darkDaysStartTime = dayStart + 1000L;
            s.darkDaysEndTime = -1L;
            s.darkDaysEndingSoundPlayed = false;
            CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_START, 1.0f);
        }
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        World w = e.world;
        if (w == null || w.isRemote) {
            return;
        }
        CelestialEventManager.rollIfNeeded(w);
        CelestialEventManager.rollDarkDaysIfNeeded(w);
        CelestialEventManager.tickDarkDays(w);
    }

    private static void grantDarkDaysAdvancement(World world) {
        if (world == null || world.getMinecraftServer() == null) {
            return;
        }
        Advancement adv = world.getMinecraftServer().getAdvancementManager().getAdvancement(new ResourceLocation("srparasites", "dark_days"));
        if (adv == null) {
            return;
        }
        int dim = world.provider.dimensionId;
        for (EntityPlayerMP p : world.getMinecraftServer().getPlayerList().getPlayers()) {
            AdvancementProgress progress;
            if (p.worldObj.provider.dimensionId != dim || (progress = p.getAdvancements().getProgress(adv)).isDone()) continue;
            for (String criterion : progress.getRemaningCriteria()) {
                p.getAdvancements().grantCriterion(adv, criterion);
            }
        }
    }
}

