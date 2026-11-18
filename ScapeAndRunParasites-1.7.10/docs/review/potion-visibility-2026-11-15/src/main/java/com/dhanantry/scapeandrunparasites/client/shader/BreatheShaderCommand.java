/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.client.ClientCommandHandler
 *  net.minecraftforge.client.IClientCommand
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.shader;

import com.dhanantry.scapeandrunparasites.client.fog.SRPFogManager;
import com.dhanantry.scapeandrunparasites.client.shader.BreatheShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.IClientCommand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class BreatheShaderCommand
extends CommandBase
implements IClientCommand {
    public String getCommandName() {
        return "srp_breathe";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/srp_breathe <on|off|toggle>";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        boolean enable;
        String mode;
        switch (mode = args.length == 0 ? "toggle" : args[0].toLowerCase()) {
            case "on": {
                enable = true;
                break;
            }
            case "off": {
                enable = false;
                break;
            }
            default: {
                boolean bl = enable = Minecraft.getMinecraft().entityRenderer.getShaderGroup() == null;
            }
        }
        if (enable) {
            BreatheShaderManager.get().enable();
            SRPFogManager.get().enable();
            sender.addChatMessage((IChatComponent)new ChatComponentText("[SRP] Breathe shader: ON"));
        } else {
            BreatheShaderManager.get().disable();
            SRPFogManager.get().disable();
            sender.addChatMessage((IChatComponent)new ChatComponentText("[SRP] Breathe shader: OFF"));
        }
    }

    public static void register() {
        ClientCommandHandler.instance.registerCommand((ICommand)new BreatheShaderCommand());
    }
}

