/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeChunkManager
 *  net.minecraftforge.common.ForgeChunkManager$LoadingCallback
 *  net.minecraftforge.common.ForgeChunkManager$Ticket
 */
package com.dhanantry.scapeandrunparasites.util;

import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class ChunkLoadingHandler
implements ForgeChunkManager.LoadingCallback {
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            NBTTagCompound tag = ticket.getModData();
            if (!tag.hasKey("x") || !tag.hasKey("z") || !tag.hasKey("e")) continue;
            int x = tag.getInteger("x");
            int z = tag.getInteger("z");
            ticket.bindEntity(world.getEntityByID(tag.getInteger("e")));
            BlockPos pos = new BlockPos(x, 0, z);
            ChunkPos chunkPos = new ChunkPos(pos);
            ForgeChunkManager.forceChunk((ForgeChunkManager.Ticket)ticket, (ChunkPos)chunkPos);
        }
    }
}

