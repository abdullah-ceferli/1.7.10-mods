/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network.msg;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class C2SRequestEscape
implements IMessage,
IMessageHandler<C2SRequestEscape, IMessage> {
    public void toBytes(ByteBuf buf) {
    }

    public void fromBytes(ByteBuf buf) {
    }

    public IMessage onMessage(C2SRequestEscape msg, MessageContext ctx) {
        ctx.getServerHandler().playerEntity.getEntityData().getCompoundTag("PlayerPersisted").setBoolean("srp_escape_pending", true);
        return null;
    }
}

