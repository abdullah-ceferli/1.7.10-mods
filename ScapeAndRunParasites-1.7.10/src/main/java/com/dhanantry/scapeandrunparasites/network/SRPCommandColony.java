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

import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
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

public class SRPCommandColony
implements ICommand {
    private final List aliases = new ArrayList();

    public SRPCommandColony() {
        this.aliases.add("srpcolonies");
    }

    public int compareTo(ICommand arg0) {
        return 0;
    }

    public String getCommandName() {
        return "srpcolonies";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "srpcolonies <text>";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            int z;
            int y;
            int x;
            if (!SRPConfigWorld.coloniesActivated) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Colonies are not activated"));
                return;
            }
            if (argString.length == 0) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            if (argString[0].equals("viewall")) {
                ParasiteEventWorld.checkColonyStatus(world);
                SRPWorldData data = SRPWorldData.get(world);
                ArrayList<Integer> coloniessX = data.getColonies("x");
                ArrayList<Integer> coloniessY = data.getColonies("y");
                ArrayList<Integer> coloniessZ = data.getColonies("z");
                ArrayList<Integer> coloniessA = data.getColonies("a");
                String out = "Current colonies in the world (x, y, z, points): \n";
                for (int i = 0; i < coloniessX.size(); ++i) {
                    out = out + "-> [" + coloniessX.get(i) + ", " + coloniessY.get(i) + ", " + coloniessZ.get(i) + ", " + coloniessA.get(i) + "] \n";
                }
                sender.addChatMessage((IChatComponent)new ChatComponentText(out));
                return;
            }
            if (argString[0].equals("resetglobaladaptation")) {
                SRPWorldData data = SRPWorldData.get(world);
                data.resetGlobalAdaptation();
                sender.addChatMessage((IChatComponent)new ChatComponentText("Global adaptation has been reset"));
                return;
            }
            if (argString[0].equals("viewallglobaladaptation")) {
                SRPWorldData data = SRPWorldData.get(world);
                ArrayList<Integer> a = data.getAdaptationI();
                ArrayList<String> b = data.getAdaptationS();
                String out = "Current global adaptation (Damage type, points): \n ";
                for (int i = 0; i < a.size(); ++i) {
                    out = out + "-> [" + b.get(i) + ", " + a.get(i) + "] \n";
                }
                sender.addChatMessage((IChatComponent)new ChatComponentText(out));
                return;
            }
            if (argString[0].equals("clearworld")) {
                SRPWorldData data = SRPWorldData.get(world);
                data.clearColonyList();
                sender.addChatMessage((IChatComponent)new ChatComponentText("There are no longer colonies in this world"));
                return;
            }
            if (argString.length != 4) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            try {
                x = Integer.parseInt(argString[1]);
                y = Integer.parseInt(argString[2]);
                z = Integer.parseInt(argString[3]);
            }
            catch (NumberFormatException nfe) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            if (argString[0].equals("setcolony")) {
                switch (ParasiteEventWorld.placeColonyInWorld(world, new BlockPos(x, y, z))) {
                    case 1: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Colony placed at " + x + " " + y + " " + z));
                        break;
                    }
                    case 2: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Colonies cannot be placed in this dimension"));
                        break;
                    }
                    case 3: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Colonies are not activated"));
                        break;
                    }
                    case 4: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Evolution Phase or UD level is not high enough to place a Colony"));
                        break;
                    }
                    case 5: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Unable to find a place for the Colony "));
                        break;
                    }
                    case 6: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Colony too close to another Colony "));
                        break;
                    }
                    case 7: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Maximum number of Colonies reached "));
                        break;
                    }
                    default: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Unknown - Colonies "));
                    }
                }
                return;
            }
            if (argString[0].equals("removecolony")) {
                if (ParasiteEventWorld.removeColonyInWorld(world, new BlockPos(x, y, z))) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Colony removed at " + x + " " + y + " " + z));
                } else {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Colony cannot be removed at " + x + " " + y + " " + z));
                }
                return;
            }
        }
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        ArrayList<String> atm = new ArrayList<String>();
        if (args.length == 1) {
            atm.add("viewall");
            atm.add("clearworld");
            atm.add("setcolony");
            atm.add("removecolony");
            atm.add("resetglobaladaptation");
            atm.add("viewallglobaladaptation");
        }
        if ((args[0].equals("setcolony") || args[0].equals("removecolony")) && args.length == 2) {
            atm.add("" + sender.getPosition().getX());
        }
        if (args.length == 3) {
            atm.add("" + sender.getPosition().getY());
        }
        if (args.length == 4) {
            atm.add("" + sender.getPosition().getZ());
        }
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, atm);
        }
        return atm;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}

