/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package com.dhanantry.scapeandrunparasites.network;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;

public class CommandSRPGuideClear
extends CommandBase {
    public String getCommandName() {
        return "srpguideclear";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/srpguideclear";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("Players only."));
            return;
        }
        server.getCommandManager().executeCommand(sender, "srpguide clear");
    }
}

