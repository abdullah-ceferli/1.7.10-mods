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
 *  net.minecraft.world.biome.Biome$TempCategory
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
import net.minecraft.world.biome.Biome;

public class SRPCommandNode
implements ICommand {
    private final List aliases = new ArrayList();

    public SRPCommandNode() {
        this.aliases.add("srpnodes");
    }

    public int compareTo(ICommand arg0) {
        return 0;
    }

    public String getCommandName() {
        return "srpnodes";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "srpnodes <text>";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            int t;
            int z;
            int y;
            int x;
            if (!SRPConfigWorld.nodesActivated) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Nodes are not activated"));
                return;
            }
            if (!SRPConfigWorld.biomeRegster) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Biome is not activated"));
                return;
            }
            if (argString.length == 0) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            if (argString[0].equals("viewall")) {
                ParasiteEventWorld.checkNodeStatus(world);
                SRPWorldData data = SRPWorldData.get(world);
                ArrayList<Integer> nodesX = data.getNodes("x");
                ArrayList<Integer> nodesY = data.getNodes("y");
                ArrayList<Integer> nodesZ = data.getNodes("z");
                ArrayList<Integer> nodesA = data.getNodes("a");
                ArrayList<Integer> nodesT = data.getNodes("t");
                String out = "Current nodes in the world (x, y, z, age, type): \n";
                for (int i = 0; i < nodesX.size(); ++i) {
                    out = out + "-> [" + nodesX.get(i) + ", " + nodesY.get(i) + ", " + nodesZ.get(i) + ", " + nodesA.get(i) + ", " + nodesT.get(i) + "] \n";
                }
                sender.addChatMessage((IChatComponent)new ChatComponentText(out));
                return;
            }
            if (argString[0].equals("clearworld")) {
                SRPWorldData data = SRPWorldData.get(world);
                data.clearNodeList();
                sender.addChatMessage((IChatComponent)new ChatComponentText("There are no longer nodes in this world"));
                return;
            }
            if (argString.length != 5) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            try {
                x = Integer.parseInt(argString[1]);
                y = Integer.parseInt(argString[2]);
                z = Integer.parseInt(argString[3]);
                t = Integer.parseInt(argString[4]);
            }
            catch (NumberFormatException nfe) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            if (argString[0].equals("setnode")) {
                int typeB = 1;
                BlockPos posss = new BlockPos(x, y, z);
                if (world.getBiome(posss).isHighHumidity()) {
                    typeB = world.getBiome(posss).getTempCategory() == Biome.TempCategory.WARM ? 1 : 3;
                } else if (world.getBiome(posss).getTempCategory() == Biome.TempCategory.COLD) {
                    typeB = 4;
                } else if (world.getBiome(posss).getTempCategory() == Biome.TempCategory.WARM && !world.getBiome(posss).canSpawnLightningBolt()) {
                    typeB = 2;
                }
                int key = ParasiteEventWorld.placeHeartInWorld(world, posss, t == -1 || t > 4 ? typeB : t);
                switch (key) {
                    case 1: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Node placed at " + x + " " + y + " " + z));
                        break;
                    }
                    case 2: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Nodes cannot be placed in this dimension"));
                        break;
                    }
                    case 3: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Nodes are not activated"));
                        break;
                    }
                    case 4: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Biome is not activated"));
                        break;
                    }
                    case 5: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Node is too close to spawn point"));
                        break;
                    }
                    case 6: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Evolution Phase or UD level is not high enough to place a Node "));
                        break;
                    }
                    case 7: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Unable to find a place for the Node"));
                        break;
                    }
                    case 8: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Node is too close to another Node"));
                        break;
                    }
                    case 9: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Maximum number of Nodes reached"));
                        break;
                    }
                    default: {
                        sender.addChatMessage((IChatComponent)new ChatComponentText("Unknown - Nodes " + key));
                    }
                }
                return;
            }
            if (argString[0].equals("removenode")) {
                if (ParasiteEventWorld.removeHeartInWorld(world, new BlockPos(x, y, z))) {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Node removed at " + x + " " + y + " " + z));
                } else {
                    sender.addChatMessage((IChatComponent)new ChatComponentText("Node cannot be removed at " + x + " " + y + " " + z));
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
            atm.add("setnode");
            atm.add("removenode");
        }
        if ((args[0].equals("setnode") || args[0].equals("removenode")) && args.length == 2) {
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

