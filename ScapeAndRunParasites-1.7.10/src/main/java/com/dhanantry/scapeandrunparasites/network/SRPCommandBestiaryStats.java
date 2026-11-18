/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

public class SRPCommandBestiaryStats
implements ICommand {
    private final List<String> aliases = new ArrayList<String>();

    public SRPCommandBestiaryStats() {
        this.aliases.add("srpstats");
        this.aliases.add("srpbestiarystats");
    }

    public int compareTo(ICommand other) {
        return this.getCommandName().compareTo(other.getCommandName());
    }

    public String getCommandName() {
        return "srpbestiarystats";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/srpbestiarystats clear [player]";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        IBestiaryProgress prog;
        EntityPlayerMP target;
        if (args.length < 1 || !"clear".equalsIgnoreCase(args[0])) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.bestiary_stats.usage", new Object[]{this.getCommandUsage(sender)}));
            return;
        }
        if (args.length >= 2) {
            target = server.getPlayerList().getPlayerByUsername(args[1]);
            if (target == null) {
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.bestiary_stats.player_not_found", new Object[]{args[1]}));
                return;
            }
        } else {
            if (!(sender.getCommandSenderEntity() instanceof EntityPlayerMP)) {
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.bestiary_stats.console_requires_player", new Object[0]));
                return;
            }
            target = (EntityPlayerMP)sender.getCommandSenderEntity();
        }
        if ((prog = (IBestiaryProgress)target.getCapability(BestiaryCapability.CAP, null)) == null) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.bestiary_stats.cap_missing", new Object[]{target.getCommandSenderName()}));
            return;
        }
        prog.clearStatsPageData();
        sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.bestiary_stats.cleared_other", new Object[]{target.getCommandSenderName()}));
        if (sender.getCommandSenderEntity() != target) {
            target.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.bestiary_stats.cleared_self", new Object[0]));
        }
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        ArrayList<String> out = new ArrayList<String>();
        if (args.length == 1) {
            out.add("clear");
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, out);
        }
        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, (String[])server.getAllUsernames());
        }
        return out;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}

