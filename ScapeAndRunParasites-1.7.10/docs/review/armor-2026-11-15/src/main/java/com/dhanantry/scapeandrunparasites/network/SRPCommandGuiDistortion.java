/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.GameType
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.network.SRPPacketGuiDistortionState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.GameType;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class SRPCommandGuiDistortion
implements ICommand {
    private final List<String> aliases = new ArrayList<String>();

    public SRPCommandGuiDistortion() {
        this.aliases.add("srpguidist");
        this.aliases.add("srpdistortion");
    }

    public int compareTo(ICommand other) {
        return this.getCommandName().compareTo(other.getCommandName());
    }

    public String getCommandName() {
        return "srpguidistortion";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.srparasites.guidistortion.usage";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.usage.text", new Object[]{"/" + this.getCommandName() + " <on|off|toggle|status> [player|all]"}));
            return;
        }
        String mode = args[0].toLowerCase();
        if (!("on".equals(mode) || "off".equals(mode) || "toggle".equals(mode) || "status".equals(mode))) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.invalid_argument", new Object[0]));
            return;
        }
        List<EntityPlayerMP> targets = this.getTargets(server, sender, args);
        if (targets.isEmpty()) {
            return;
        }
        for (EntityPlayerMP target : targets) {
            this.handleTarget(sender, target, mode, targets.size() > 1);
        }
        if (targets.size() > 1 && !"status".equals(mode)) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.set_all", new Object[]{targets.size()}));
        }
    }

    private List<EntityPlayerMP> getTargets(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            String targetName = args[1];
            if ("all".equalsIgnoreCase(targetName) || "@a".equalsIgnoreCase(targetName)) {
                return new ArrayList<EntityPlayerMP>(server.getPlayerList().getPlayers());
            }
            EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(targetName);
            if (target == null) {
                sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.player_not_found", new Object[]{targetName}));
                return Collections.emptyList();
            }
            ArrayList<EntityPlayerMP> one = new ArrayList<EntityPlayerMP>();
            one.add(target);
            return one;
        }
        if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
            ArrayList<EntityPlayerMP> one = new ArrayList<EntityPlayerMP>();
            one.add((EntityPlayerMP)sender.getCommandSenderEntity());
            return one;
        }
        sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.console_requires_player", new Object[0]));
        return Collections.emptyList();
    }

    private void handleTarget(ICommandSender sender, EntityPlayerMP target, String mode, boolean multiTarget) {
        boolean newDisabled;
        boolean effectivelyDisabled;
        NBTTagCompound persisted = SRPCommandGuiDistortion.getPersistedData(target);
        boolean currentlyDisabled = persisted.getBoolean("SRPGuiDistortionDisabled");
        boolean creativeOverride = persisted.getBoolean("SRPGuiDistortionCreativeOverride");
        boolean creativeOrSpectator = SRPCommandGuiDistortion.isCreativeOrSpectator(target);
        boolean bl = effectivelyDisabled = currentlyDisabled || creativeOrSpectator && !creativeOverride;
        if ("status".equals(mode)) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.status.detailed", new Object[]{target.getCommandSenderName(), currentlyDisabled ? new ChatComponentTranslation("commands.srparasites.guidistortion.state.disabled", new Object[0]) : new ChatComponentTranslation("commands.srparasites.guidistortion.state.enabled", new Object[0]), creativeOverride ? new ChatComponentTranslation("commands.srparasites.guidistortion.creative_override.enabled", new Object[0]) : new ChatComponentTranslation("commands.srparasites.guidistortion.creative_override.disabled", new Object[0]), creativeOrSpectator ? new ChatComponentTranslation("commands.srparasites.guidistortion.gamemode.creative_or_spectator", new Object[0]) : new ChatComponentTranslation("commands.srparasites.guidistortion.gamemode.normal", new Object[0])}));
            return;
        }
        boolean newCreativeOverride = creativeOverride;
        if ("on".equals(mode)) {
            newDisabled = false;
            if (creativeOrSpectator) {
                newCreativeOverride = true;
            }
        } else if ("off".equals(mode)) {
            newDisabled = true;
            newCreativeOverride = false;
        } else if (effectivelyDisabled) {
            newDisabled = false;
            if (creativeOrSpectator) {
                newCreativeOverride = true;
            }
        } else {
            newDisabled = true;
            newCreativeOverride = false;
        }
        persisted.setBoolean("SRPGuiDistortionDisabled", newDisabled);
        persisted.setBoolean("SRPGuiDistortionCreativeOverride", newCreativeOverride);
        SRPMain.network.sendTo((IMessage)new SRPPacketGuiDistortionState(newDisabled, newCreativeOverride), target);
        if (!multiTarget) {
            sender.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.set_other", new Object[]{target.getCommandSenderName(), newDisabled ? new ChatComponentTranslation("commands.srparasites.guidistortion.state.disabled", new Object[0]) : new ChatComponentTranslation("commands.srparasites.guidistortion.state.enabled", new Object[0])}));
        }
        if (target != sender) {
            target.addChatMessage((IChatComponent)new ChatComponentTranslation("commands.srparasites.guidistortion.set_self", new Object[]{newDisabled ? new ChatComponentTranslation("commands.srparasites.guidistortion.state.disabled", new Object[0]) : new ChatComponentTranslation("commands.srparasites.guidistortion.state.enabled", new Object[0])}));
        }
    }

    private static NBTTagCompound getPersistedData(EntityPlayerMP player) {
        NBTTagCompound persisted;
        NBTTagCompound entityData = player.getEntityData();
        if (entityData.hasKey("PlayerPersisted", 10)) {
            persisted = entityData.getCompoundTag("PlayerPersisted");
        } else {
            persisted = new NBTTagCompound();
            entityData.setTag("PlayerPersisted", (NBTBase)persisted);
        }
        return persisted;
    }

    private static boolean isCreativeOrSpectator(EntityPlayerMP player) {
        if (player == null) {
            return false;
        }
        if (player.isSpectator()) {
            return true;
        }
        if (player.theItemInWorldManager != null) {
            GameType type = player.theItemInWorldManager.getGameType();
            return type == GameType.CREATIVE || type == GameType.SPECTATOR;
        }
        return false;
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        ArrayList<String> out = new ArrayList<String>();
        if (args.length == 1) {
            out.add("on");
            out.add("off");
            out.add("toggle");
            out.add("status");
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, out);
        }
        if (args.length == 2) {
            String[] names;
            out.add("all");
            out.add("@a");
            for (String name : names = server.getAllUsernames()) {
                out.add(name);
            }
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, out);
        }
        return out;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}

