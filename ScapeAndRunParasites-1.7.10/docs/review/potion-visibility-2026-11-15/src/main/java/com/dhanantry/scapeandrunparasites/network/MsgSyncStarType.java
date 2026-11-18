/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.client.world.SRPClientStarWorldState;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgSyncStarType
implements IMessage {
    private int starType;

    public MsgSyncStarType() {
    }

    public MsgSyncStarType(int starType) {
        this.starType = starType;
    }

    public void fromBytes(ByteBuf buf) {
        this.starType = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.starType);
    }

    public static class Handler
    implements IMessageHandler<MsgSyncStarType, IMessage> {
        public IMessage onMessage(final MsgSyncStarType message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    SRPClientStarWorldState.setStarType(message.starType);
                }
            });
            return null;
        }
    }
}

