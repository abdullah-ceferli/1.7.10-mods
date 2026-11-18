/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class SRPCommandGeneration
implements ICommand {
    private final List aliases = new ArrayList();

    public SRPCommandGeneration() {
        this.aliases.add("srpgeneration");
    }

    public int compareTo(ICommand arg0) {
        return 0;
    }

    public String getCommandName() {
        return "srpgeneration";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "srpgeneration <text>";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            if (!SRPConfigSystems.generationUse) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Generation is not active"));
                return;
            }
            if (argString.length == 0) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            if (argString[0].equals("setgeneration")) {
                String nani;
                try {
                    nani = argString[1];
                    int n = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid arg"));
                    return;
                }
                nani = argString[1];
                int option = Integer.parseInt(nani);
                SRPSaveData data = SRPSaveData.get(world, 66);
                if (option >= 6) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument: generation too high"));
                    return;
                }
                if (option <= -1) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument: generation too low"));
                    return;
                }
                data.setGeneration((byte)option, world.provider.dimensionId);
                data.setGenerationTime(0, world.provider.dimensionId);
                sender.addChatMessage((IChatComponent)new ChatComponentText("Changed Generation of Parasites to " + argString[1]));
                return;
            }
            if (argString[0].equals("getgeneration")) {
                SRPSaveData data = SRPSaveData.get(world, 67);
                sender.addChatMessage((IChatComponent)new ChatComponentText("Current Generation of Parasites: " + data.getGeneration(world.provider.dimensionId)));
                return;
            }
            if (argString[0].equals("addticks")) {
                int option;
                String nani;
                try {
                    nani = argString[1];
                    option = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid arg"));
                    return;
                }
                nani = argString[1];
                option = Integer.parseInt(nani);
                SRPSaveData data = SRPSaveData.get(world, 6777);
                int id = world.provider.dimensionId;
                int value = data.getGenerationTime(id) + option;
                data.setGenerationTime(value, id);
                sender.addChatMessage((IChatComponent)new ChatComponentText("Added: " + option + " to the current Time"));
                return;
            }
            sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid command"));
        }
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        for (int i = 0; i < args.length; ++i) {
        }
        ArrayList<String> atm = new ArrayList<String>();
        atm.add("setgeneration");
        atm.add("getgeneration");
        atm.add("addticks");
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, atm);
        }
        return atm;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}

