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
 *  net.minecraft.util.text.TextComponentTranslation
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
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class SRPCommandUDevelopment
implements ICommand {
    private final List aliases = new ArrayList();

    private static void msg(ICommandSender sender, String key, Object ... args) {
        sender.addChatMessage((IChatComponent)new ChatComponentTranslation(key, args));
    }

    public SRPCommandUDevelopment() {
        this.aliases.add("srpudevelopment");
    }

    public int compareTo(ICommand arg0) {
        return 0;
    }

    public String getCommandName() {
        return "srpudevelopment";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "srpudevelopment <text>";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            if (!SRPConfigSystems.useEvolution) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Evolution levels are not active"));
                return;
            }
            if (argString.length == 0) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            if (argString[0].equals("getlevel")) {
                SRPSaveData data = SRPSaveData.get(world, 59);
                int id = world.provider.dimensionId;
                sender.addChatMessage((IChatComponent)new ChatComponentText(" ======> \n -> Current Ubiquitous Development Level: " + data.getDeveLevel()));
                return;
            }
            if (argString[0].equals("setlevel")) {
                String nani;
                try {
                    nani = argString[1];
                    int id = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid arg"));
                    return;
                }
                nani = argString[1];
                int option = Integer.parseInt(nani);
                SRPSaveData data = SRPSaveData.get(world, 63);
                if (option >= 5) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument: level too high"));
                    return;
                }
                if (option <= -1) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument: level too low"));
                    return;
                }
                SRPSaveData.falseLevel = option;
                sender.addChatMessage((IChatComponent)new ChatComponentText("Changed Ubiquitous Development Level to " + argString[1]));
                sender.addChatMessage((IChatComponent)new ChatComponentText("Set this value to 0 to remove fake level"));
                return;
            }
            if (argString[0].equals("viewalldims")) {
                SRPSaveData data = SRPSaveData.get(world, -1);
                ArrayList<Integer> phases = data.getColonies("p");
                ArrayList<Integer> dims = data.getColonies("d");
                ArrayList<Integer> kills = data.getColonies("k");
                ArrayList<Integer> gen = data.getColonies("g");
                String out = "Current Parasite progress in dimensions \n (id, phase, points, generation): \n";
                for (int i = 0; i < dims.size(); ++i) {
                    out = out + "-> [" + dims.get(i) + ", " + phases.get(i) + ", " + kills.get(i) + ", " + gen.get(i) + "] \n";
                }
                sender.addChatMessage((IChatComponent)new ChatComponentText(out));
                return;
            }
            if (argString[0].equals("setdimevolution")) {
                String nani2;
                String nani;
                try {
                    nani = argString[1];
                    nani2 = argString[2];
                    int option = Integer.parseInt(nani);
                    int kills = Integer.parseInt(nani2);
                }
                catch (NumberFormatException nfe) {
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.error.invalid_arg", new Object[0]);
                    return;
                }
                nani = argString[1];
                nani2 = argString[2];
                int id = Integer.parseInt(nani);
                int phase = Integer.parseInt(nani2);
                SRPSaveData data = SRPSaveData.get(world, 633);
                if (phase >= 11) {
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.error.phase_too_high", new Object[0]);
                    return;
                }
                if (phase <= -3) {
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.error.phase_too_low", new Object[0]);
                    return;
                }
                if (phase == -1) {
                    data.setEvolutionPhase(id, (byte)phase, true, world);
                    data.setTotalKills(id, 100 * phase, false, world, true, 50);
                } else if (phase == -2) {
                    data.setTotalKills(id, 100 * phase, false, world, true, 51);
                    data.setEvolutionPhase(id, (byte)phase, true, world);
                } else {
                    data.setEvolutionPhase(id, (byte)phase, true, world);
                }
                if (argString.length == 3) {
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.getphase.dimension", argString[1]);
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.changed_phase", argString[2]);
                    return;
                }
                try {
                    nani = argString[3];
                    int out = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.error.invalid_arg_evo", new Object[0]);
                    return;
                }
                nani = argString[3];
                int option = Integer.parseInt(nani);
                if (option >= 6) {
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.changed_phase", argString[3]);
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.error.generation_too_high", new Object[0]);
                    return;
                }
                if (option <= -1) {
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.changed_phase", argString[3]);
                    SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.error.generation_too_low", new Object[0]);
                    return;
                }
                data.setGeneration((byte)option, id);
                data.setGenerationTime(0, id);
                SRPCommandUDevelopment.msg(sender, "command.srpevolution.getphase.dimension", argString[1]);
                SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.changed_phase", argString[2]);
                SRPCommandUDevelopment.msg(sender, "command.srpevolution.setphase.changed_generation", argString[3]);
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
        atm.add("getlevel");
        atm.add("setlevel");
        atm.add("viewalldims");
        atm.add("setdimevolution");
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, atm);
        }
        return atm;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}

