/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class SRPCommand
implements ICommand {
    private final List aliases = new ArrayList();
    protected String fullEntityName;
    protected Entity conjuredEntity;

    public SRPCommand() {
        this.aliases.add("conjure");
        this.aliases.add("conj");
    }

    public int compareTo(ICommand arg0) {
        return 0;
    }

    public String getCommandName() {
        return "conjure";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "conjure <text>";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            if (argString.length == 0) {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Invalid argument"));
                return;
            }
            sender.addChatMessage((IChatComponent)new ChatComponentText("Conjuring: [" + argString[0] + "]"));
            this.fullEntityName = "srparasites:" + argString[0];
            ResourceLocation ccc = new ResourceLocation(this.fullEntityName);
            if (EntityList.isRegistered((ResourceLocation)ccc)) {
                this.conjuredEntity = EntityList.createEntityByIDFromName((ResourceLocation)ccc, (World)world);
                this.conjuredEntity.copyLocationAndAnglesFrom(sender.getCommandSenderEntity());
                world.spawnEntityInWorld(this.conjuredEntity);
            } else {
                sender.addChatMessage((IChatComponent)new ChatComponentText("Entity not found"));
            }
        }
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        ArrayList<String> atm = new ArrayList<String>();
        atm.add("sentry");
        atm.add("carrier_heavy");
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, atm);
        }
        return atm;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}

