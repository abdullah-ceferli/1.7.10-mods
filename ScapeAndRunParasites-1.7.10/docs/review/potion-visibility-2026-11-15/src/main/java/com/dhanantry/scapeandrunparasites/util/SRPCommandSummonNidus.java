/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNexusProtection1;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class SRPCommandSummonNidus
extends CommandBase {
    public String getCommandName() {
        return "srp_summon_nidus";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/srp_summon_nidus [x y z] [stage]";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        BlockPos pos;
        World world = sender.getEntityWorld();
        if (world.isRemote) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("This command must be run server-side."));
            return;
        }
        int stage = 1;
        if (args.length == 0) {
            pos = sender.getPosition();
        } else if (args.length == 3 || args.length == 4) {
            pos = SRPCommandSummonNidus.parseBlockPos((ICommandSender)sender, (String[])args, (int)0, (boolean)false);
            if (args.length == 4) {
                stage = SRPCommandSummonNidus.parseInt((String)args[3], (int)1);
            }
        } else {
            throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
        }
        boolean generated = new WorldGenParasiteNexusProtection1(false, stage).generate(world, new Random(), pos.down());
        sender.addChatMessage((IChatComponent)new ChatComponentText(generated ? "Generated Nidus/Nexus protection structure at " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " with stage " + stage : "Nidus/Nexus protection structure generation returned false."));
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length >= 1 && args.length <= 3) {
            return SRPCommandSummonNidus.getTabCompletionCoordinate((String[])args, (int)0, (BlockPos)targetPos);
        }
        if (args.length == 4) {
            return SRPCommandSummonNidus.getListOfStringsMatchingLastWord((String[])args, (String[])new String[]{"1", "2", "3", "4"});
        }
        return Collections.emptyList();
    }
}

