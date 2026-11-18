/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.network.msg;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractPacket<T extends AbstractPacket<T>>
implements IMessage,
IMessageHandler<T, IMessage> {
    public IMessage onMessage(T message, MessageContext messageContext) {
        SRPMain.proxy.networkMessage(message, messageContext);
        return null;
    }

    @SideOnly(value=Side.CLIENT)
    public abstract void clientSide(Minecraft var1, T var2, EntityPlayer var3, MessageContext var4);

    public abstract void serverSide(MinecraftServer var1, T var2, EntityPlayerMP var3, MessageContext var4);
}

