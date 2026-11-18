/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fml.common.network.ByteBufUtils
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.CurrentProgressClientCache;
import com.dhanantry.scapeandrunparasites.network.SRPProgressSnapshot;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgSyncProgressSnapshot
implements IMessage {
    private NBTTagCompound tag;

    public MsgSyncProgressSnapshot() {
        this.tag = new NBTTagCompound();
    }

    public MsgSyncProgressSnapshot(SRPProgressSnapshot snapshot) {
        this.tag = snapshot == null ? new NBTTagCompound() : snapshot.toNBT();
    }

    public void fromBytes(ByteBuf buf) {
        NBTTagCompound read = ByteBufUtils.readTag((ByteBuf)buf);
        this.tag = read == null ? new NBTTagCompound() : read;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag((ByteBuf)buf, (NBTTagCompound)(this.tag == null ? new NBTTagCompound() : this.tag));
    }

    public static class Handler
    implements IMessageHandler<MsgSyncProgressSnapshot, IMessage> {
        public IMessage onMessage(final MsgSyncProgressSnapshot message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    CurrentProgressClientCache.read(message.tag);
                }
            });
            return null;
        }
    }
}

