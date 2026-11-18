/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.SPacketChunkData
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.management.PlayerChunkMap
 *  net.minecraft.server.management.PlayerChunkMapEntry
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.chunk.Chunk
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import java.lang.reflect.Method;
import java.util.Arrays;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class CommandHarlequinHere
extends CommandBase {
    public String getCommandName() {
        return "harlequin_here";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/harlequin_here [radiusChunks=1]";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP p = CommandHarlequinHere.getCommandSenderAsPlayer((ICommandSender)sender);
        WorldServer world = p.getServerForPlayer();
        if (SRPBiomes.biomeHarlequin == null) {
            throw new CommandException("Harlequin biome is not registered (SRPBiomes.biomeHarlequin is null).", new Object[0]);
        }
        int r = args.length >= 1 ? Math.max(0, CommandHarlequinHere.parseInt((String)args[0])) : 1;
        int baseChunkX = EntityQueries.position(p).getX() >> 4;
        int baseChunkZ = EntityQueries.position(p).getZ() >> 4;
        byte id = (byte)Biome.getIdForBiome((Biome)SRPBiomes.biomeHarlequin);
        int changed = 0;
        for (int dx = -r; dx <= r; ++dx) {
            for (int dz = -r; dz <= r; ++dz) {
                Chunk c = world.getChunkFromChunkCoords(baseChunkX + dx, baseChunkZ + dz);
                Arrays.fill(c.getBiomeArray(), id);
                c.setChunkModified();
                CommandHarlequinHere.sendChunkBiomeUpdate(world, c);
                c.resetRelightChecks();
                ++changed;
            }
        }
        sender.addChatMessage((IChatComponent)new ChatComponentText("Applied Harlequin to " + changed + " chunk(s) in a " + (2 * r + 1) + "x" + (2 * r + 1) + " square."));
        Biome bNow = world.getBiome(EntityQueries.position(p));
        String bName = bNow.getRegistryName() != null ? bNow.getRegistryName().toString() : bNow.getBiomeName();
        sender.addChatMessage((IChatComponent)new ChatComponentText("Server biome at your position now: " + bName));
    }

    private static void sendChunkBiomeUpdate(WorldServer world, Chunk c) {
        PlayerChunkMap pcm = world.getPlayerChunkMap();
        try {
            Method m = pcm.getClass().getMethod("markBlockRangeForSend", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            m.invoke(pcm, c.xPosition << 4, 0, c.zPosition << 4, (c.xPosition << 4) + 15, 255, (c.zPosition << 4) + 15);
            return;
        }
        catch (NoSuchMethodException m) {
        }
        catch (Throwable m) {
            // empty catch block
        }
        PlayerChunkMapEntry entry = pcm.getEntry(c.xPosition, c.zPosition);
        if (entry != null) {
            SPacketChunkData pkt = new SPacketChunkData(c, 65535);
            entry.sendPacket((Packet)pkt);
        }
    }
}

