/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialPhaseClient;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialEventManager;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandForceCelestial
extends CommandBase {
    public String getCommandName() {
        return "srp_celestial";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/srp_celestial <list|debug|clear|all|dark_days|dark_days_end|id>";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    private static long getNightIndex(World world) {
        return world.getWorldTime() / 24000L;
    }

    private static long getNextTimeOfDay(World world, long targetDayTime) {
        long current = world.getWorldTime();
        long dayStart = current - current % 24000L;
        long target = dayStart + targetDayTime;
        if (target <= current) {
            target += 24000L;
        }
        return target;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean nowForced;
        if (!SRPConfigWorld.enableCelestialObjects) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("SRP celestial objects are disabled in the config (enableCelestialObjects = false)."));
            return;
        }
        if (args.length < 1) {
            throw new WrongUsageException("Usage: /srp_celestial <list|debug|clear|all|dark_days|dark_days_end|id>", new Object[0]);
        }
        String sub = args[0];
        World world = sender.getEntityWorld();
        int dim = world.provider.dimensionId;
        if ("list".equalsIgnoreCase(sub)) {
            List<String> ids = CelestialObjectRegistry.getAllIds();
            if (ids.isEmpty()) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("No celestial events are registered."));
            } else {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Celestial events: " + String.join((CharSequence)", ", ids)));
            }
            sender.addChatMessage((IChatComponent)new ChatComponentText("Special commands: dark_days, dark_days_end"));
            return;
        }
        if ("debug".equalsIgnoreCase(sub)) {
            if (!world.isRemote) {
                SRPSaveData save = SRPSaveData.get(world, dim);
                int phase = save == null ? -1 : (int)save.getEvolutionPhase(dim);
                CelestialNightData nightData = CelestialNightData.get(world);
                CelestialNightData.DimState state = nightData.getOrCreate(dim);
                sender.addChatMessage((IChatComponent)new ChatComponentText("[SERVER] dim=" + dim + " phase=" + phase + " day=" + CommandForceCelestial.getNightIndex(world) + " dayTime=" + world.getWorldTime() % 24000L + " active=" + state.active.size() + " forced=" + state.forced.size() + " darkDaysEndTime=" + state.darkDaysEndTime + " darkDaysEndingSoundPlayed=" + state.darkDaysEndingSoundPlayed));
                sender.addChatMessage((IChatComponent)new ChatComponentText("[SERVER] active=" + String.join((CharSequence)", ", state.active)));
                sender.addChatMessage((IChatComponent)new ChatComponentText("[SERVER] forced=" + String.join((CharSequence)", ", state.forced)));
            } else {
                int phase = CelestialPhaseClient.getPhase(dim);
                long night = CelestialPhaseClient.getNightIndex(dim);
                Set<String> a = CelestialPhaseClient.getActiveIds(dim);
                Set<String> f = CelestialPhaseClient.getForcedIds(dim);
                sender.addChatMessage((IChatComponent)new ChatComponentText("[CLIENT] dim=" + dim + " phase=" + phase + " night=" + night + " active=" + a.size() + " forced=" + f.size()));
                sender.addChatMessage((IChatComponent)new ChatComponentText("[CLIENT] active=" + String.join((CharSequence)", ", a)));
                sender.addChatMessage((IChatComponent)new ChatComponentText("[CLIENT] forced=" + String.join((CharSequence)", ", f)));
            }
            return;
        }
        if (world.isRemote) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("This command must be run server-side."));
            return;
        }
        SRPSaveData save = SRPSaveData.get(world, dim);
        if (save == null) {
            throw new CommandException("SRPSaveData is null for dim " + dim, new Object[0]);
        }
        byte phase = save.getEvolutionPhase(dim);
        CelestialNightData nightData = CelestialNightData.get(world);
        CelestialNightData.DimState state = nightData.getOrCreate(dim);
        long nightIndex = CommandForceCelestial.getNightIndex(world);
        if ("clear".equalsIgnoreCase(sub) || "none".equalsIgnoreCase(sub) || "off".equalsIgnoreCase(sub)) {
            state.forced.clear();
            state.active.remove("dark_days");
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
            nightData.markDirty();
            CelestialEventManager.clearForced(world);
            CelestialEventManager.syncDim(world, dim);
            sender.addChatMessage((IChatComponent)new ChatComponentText("Cleared all forced celestial events in dimension " + dim));
            return;
        }
        if ("dark_days".equalsIgnoreCase(sub)) {
            if (!SRPConfigWorld.darkDaysEnabled) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Dark Days is disabled in the config."));
                return;
            }
            world.setWorldTime(CommandForceCelestial.getNextTimeOfDay(world, 1000L));
            state.active.clear();
            state.forced.clear();
            state.darkDaysStartTime = world.getWorldTime() + 160L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
            nightData.markDirty();
            CelestialEventManager.startDarkDays(world);
            sender.addChatMessage((IChatComponent)new ChatComponentText("Queued Celestial Displacement. CD DD will activate in 8 seconds in dimension " + dim));
            return;
        }
        if ("dark_days_end".equalsIgnoreCase(sub)) {
            boolean wasActive;
            boolean bl = wasActive = state.forced.contains("dark_days") || state.active.contains("dark_days");
            if (!wasActive) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Celestial Displacement is not active in dimension " + dim));
                return;
            }
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = world.getWorldTime() + 200L;
            state.darkDaysEndingSoundPlayed = true;
            nightData.markDirty();
            CelestialEventManager.stopDarkDays(world);
            sender.addChatMessage((IChatComponent)new ChatComponentText("Queued Celestial Realignment. Displacement will end in 10 seconds in dimension " + dim));
            return;
        }
        if ("all".equalsIgnoreCase(sub)) {
            List<String> ids = CelestialObjectRegistry.getAllIds();
            if (ids.isEmpty()) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("No celestial events are registered."));
                return;
            }
            ArrayList<String> toForce = new ArrayList<String>();
            for (String eventId : ids) {
                if ("dark_days".equals(eventId) || SRPConfigWorld.isCelestialEventBlacklisted(eventId)) continue;
                toForce.add(eventId);
            }
            state.active.remove("dark_days");
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
            CelestialEventManager.clearForced(world);
            state.forced.clear();
            for (String eventId : toForce) {
                state.forced.add(eventId);
                CelestialEventManager.force(world, eventId, true);
            }
            nightData.markDirty();
            CelestialEventManager.syncDim(world, dim);
            sender.addChatMessage((IChatComponent)new ChatComponentText("Forced " + toForce.size() + " compatible celestial events in dimension " + dim + ". Celestial Displacement was skipped."));
            return;
        }
        String id = sub;
        if (!CelestialObjectRegistry.getAllIds().contains(id)) {
            throw new CommandException("Unknown celestial id: " + id, new Object[0]);
        }
        if (SRPConfigWorld.isCelestialEventBlacklisted(id) || "dark_days".equals(id) && !SRPConfigWorld.darkDaysEnabled) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("Celestial event '" + id + "' is disabled in the config."));
            return;
        }
        if (state.forced.contains(id)) {
            state.forced.remove(id);
            state.active.remove(id);
            nowForced = false;
            CelestialEventManager.force(world, id, false);
        } else {
            state.forced.add(id);
            nowForced = true;
            if ("dark_days".equals(id)) {
                state.active.clear();
                state.forced.clear();
                state.forced.add("dark_days");
                state.darkDaysEndTime = world.getWorldTime() + 6000L;
                state.darkDaysEndingSoundPlayed = false;
                CelestialEventManager.startDarkDays(world);
            } else {
                CelestialEventManager.force(world, id, true);
            }
        }
        if (!SRPConfigWorld.darkDaysEnabled) {
            state.active.remove("dark_days");
            state.forced.remove("dark_days");
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
        }
        nightData.markDirty();
        CelestialEventManager.syncDim(world, dim);
        sender.addChatMessage((IChatComponent)new ChatComponentText((nowForced ? "Forced" : "Unforced") + " celestial event '" + id + "' in dimension " + dim));
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            ArrayList<String> options = new ArrayList<String>();
            options.add("list");
            options.add("debug");
            options.add("clear");
            options.add("none");
            options.add("off");
            options.add("all");
            options.add("dark_days");
            options.add("dark_days_end");
            options.addAll(CelestialObjectRegistry.getAllIds());
            return CommandForceCelestial.getListOfStringsMatchingLastWord((String[])args, options);
        }
        return Collections.emptyList();
    }
}

