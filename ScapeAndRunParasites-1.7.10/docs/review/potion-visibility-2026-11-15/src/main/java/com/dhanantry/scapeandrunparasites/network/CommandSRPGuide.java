/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.BlockBestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.SRPBlockCompendiumRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.effects.SRPStatusEffectRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.net.BestiaryNetwork;
import com.dhanantry.scapeandrunparasites.bestiary.net.PacketBestiarySync;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectDefinition;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.network.MsgSyncProgressSnapshot;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.network.SRPProgressSnapshot;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class CommandSRPGuide
extends CommandBase {
    private static final ConcurrentHashMap<UUID, NBTTagCompound> SNAPSHOTS = new ConcurrentHashMap();

    public String getCommandName() {
        return "srpguide";
    }

    private static void sync(EntityPlayerMP p, IBestiaryProgress prog) {
        BestiaryNetwork.CH.sendTo((IMessage)new PacketBestiarySync(prog), p);
        SRPNetwork.CHANNEL.sendTo((IMessage)new MsgSyncProgressSnapshot(SRPProgressSnapshot.collect(p.worldObj, (EntityPlayer)p)), p);
    }

    public String getCommandUsage(ICommandSender sender) {
        return this.getUsageKey();
    }

    private String getUsageKey() {
        return "command.srpguide.usage";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String sub;
        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.players_only", new Object[0]));
            return;
        }
        EntityPlayerMP p = (EntityPlayerMP)sender;
        if (args.length == 0) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation(this.getUsageKey(), new Object[0]));
            return;
        }
        switch (sub = args[0].toLowerCase(Locale.ROOT)) {
            case "unlockall": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                SNAPSHOTS.put(p.getUniqueID(), prog.serializeNBT());
                int mobCount = 0;
                int blockCount = 0;
                int celestialCount = 0;
                int effectCount = 0;
                for (ParasiteTier t : ParasiteTier.values()) {
                    prog.markTierSeen(t);
                }
                for (Object e : SRPBestiaryRegistry.all()) {
                    try {
                        Object tierObj;
                        Field fMobId = e.getClass().getField("mobId");
                        Field fTier = e.getClass().getField("tier");
                        Object idObj = fMobId.get(e);
                        if (!(idObj instanceof String)) continue;
                        String mobId = (String)idObj;
                        prog.markMobSeen(mobId);
                        int have = prog.getKills(mobId);
                        if (have < 999) {
                            prog.addKill(mobId, 999 - have);
                        }
                        if ((tierObj = fTier.get(e)) instanceof ParasiteTier) {
                            prog.markTierSeen((ParasiteTier)((Object)tierObj));
                        }
                        ++mobCount;
                    }
                    catch (Throwable fMobId) {}
                }
                for (BlockBestiaryEntry blockBestiaryEntry : SRPBlockCompendiumRegistry.all()) {
                    if (prog.hasSeenBlock(blockBestiaryEntry.id)) continue;
                    prog.markBlockSeen(blockBestiaryEntry.id);
                    ++blockCount;
                }
                for (CelestialObjectDefinition celestialObjectDefinition : CelestialObjectRegistry.getObjects()) {
                    if (prog.hasSeenCelestial(celestialObjectDefinition.id)) continue;
                    prog.markCelestialSeen(celestialObjectDefinition.id);
                    ++celestialCount;
                }
                for (SRPStatusEffectRegistry.Entry entry : SRPStatusEffectRegistry.all()) {
                    if (entry == null || entry.id == null) continue;
                    try {
                        if (prog.hasSeenEffect(entry.id)) continue;
                        prog.markEffectSeen(entry.id);
                        ++effectCount;
                    }
                    catch (Throwable t) {
                        try {
                            Method has = prog.getClass().getMethod("hasSeenEffect", String.class);
                            Method mark = prog.getClass().getMethod("markEffectSeen", String.class);
                            Object ok = has.invoke(prog, entry.id);
                            if (ok instanceof Boolean && ((Boolean)ok).booleanValue()) continue;
                            mark.invoke(prog, entry.id);
                            ++effectCount;
                        }
                        catch (Throwable throwable) {}
                    }
                }
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.unlockall_ok", new Object[]{mobCount}));
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.unlockblocks_ok", new Object[]{blockCount}));
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.unlockcelestial_ok", new Object[]{celestialCount}));
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.unlockeffects_ok", new Object[]{effectCount}));
                CommandSRPGuide.sync(p, prog);
                break;
            }
            case "unlockblocks": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                int blockCount = 0;
                for (BlockBestiaryEntry entry : SRPBlockCompendiumRegistry.all()) {
                    if (prog.hasSeenBlock(entry.id)) continue;
                    prog.markBlockSeen(entry.id);
                    ++blockCount;
                }
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.unlockblocks_ok", new Object[]{blockCount}));
                CommandSRPGuide.sync(p, prog);
                break;
            }
            case "unlockcelestial": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                int count = 0;
                for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
                    if (prog.hasSeenCelestial(def.id)) continue;
                    prog.markCelestialSeen(def.id);
                    ++count;
                }
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.unlockcelestial_ok", new Object[]{count}));
                CommandSRPGuide.sync(p, prog);
                break;
            }
            case "unlockeffects": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                int count = 0;
                for (SRPStatusEffectRegistry.Entry e : SRPStatusEffectRegistry.all()) {
                    if (e == null || e.id == null) continue;
                    try {
                        if (prog.hasSeenEffect(e.id)) continue;
                        prog.markEffectSeen(e.id);
                        ++count;
                    }
                    catch (Throwable t) {
                        try {
                            Method has = prog.getClass().getMethod("hasSeenEffect", String.class);
                            Method method = prog.getClass().getMethod("markEffectSeen", String.class);
                            Object ok = has.invoke(prog, e.id);
                            if (ok instanceof Boolean && ((Boolean)ok).booleanValue()) continue;
                            method.invoke(prog, e.id);
                            ++count;
                        }
                        catch (Throwable throwable) {}
                    }
                }
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.unlockeffects_ok", new Object[]{count}));
                CommandSRPGuide.sync(p, prog);
                break;
            }
            case "clear": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                NBTTagCompound snap = SNAPSHOTS.get(p.getUniqueID());
                if (snap != null) {
                    prog.deserializeNBT(snap.copy());
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.clear_restore_ok", new Object[0]));
                } else {
                    prog.deserializeNBT(new NBTTagCompound());
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.clear_no_snapshot", new Object[0]));
                }
                CommandSRPGuide.sync(p, prog);
                break;
            }
            case "clearall": 
            case "reset": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                prog.deserializeNBT(new NBTTagCompound());
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.clearall_ok", new Object[0]));
                CommandSRPGuide.sync(p, prog);
                break;
            }
            case "clearblocks": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                prog.getSeenBlocks().clear();
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.clearblocks_ok", new Object[0]));
                CommandSRPGuide.sync(p, prog);
                break;
            }
            case "clearcelestial": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                try {
                    Method m = prog.getClass().getMethod("getSeenCelestials", new Class[0]);
                    Object o = m.invoke(prog, new Object[0]);
                    if (o instanceof Set) {
                        ((Set)o).clear();
                        sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.clearcelestial_ok", new Object[0]));
                        CommandSRPGuide.sync(p, prog);
                        break;
                    }
                }
                catch (Throwable m) {
                    // empty catch block
                }
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.clearcelestial_need_getter", new Object[0]));
                break;
            }
            case "cleareffects": {
                IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cap_missing", new Object[0]));
                    return;
                }
                try {
                    Method m = prog.getClass().getMethod("getSeenEffects", new Class[0]);
                    Object o = m.invoke(prog, new Object[0]);
                    if (o instanceof Set) {
                        ((Set)o).clear();
                        sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cleareffects_ok", new Object[0]));
                        CommandSRPGuide.sync(p, prog);
                        break;
                    }
                }
                catch (Throwable m) {
                    // empty catch block
                }
                try {
                    NBTTagCompound tag = prog.serializeNBT();
                    if (tag.hasKey("seenEffects")) {
                        tag.removeTag("seenEffects");
                        prog.deserializeNBT(tag);
                        sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cleareffects_ok", new Object[0]));
                        CommandSRPGuide.sync(p, prog);
                        break;
                    }
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("command.srpguide.cleareffects_need_getter", new Object[0]));
                break;
            }
            default: {
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation(this.getUsageKey(), new Object[0]));
            }
        }
    }
}

