/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiBestiary;
import com.dhanantry.scapeandrunparasites.bestiary.net.BestiaryNetwork;
import com.dhanantry.scapeandrunparasites.bestiary.net.PacketBestiaryRequest;
import com.dhanantry.scapeandrunparasites.client.FieldGuideClientSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ItemSRPFieldGuide
extends Item {
    public ItemSRPFieldGuide() {
        RegistryNames.set(this, "srp_field_guide");
        this.setUnlocalizedName("srparasites.srp_field_guide");
        this.setMaxStackSize(1);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            return ClientHandler.handleClient(player, hand);
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)player.getHeldItem(hand));
    }

    private static class ClientHandler {
        private ClientHandler() {
        }

        public static ActionResult<ItemStack> handleClient(EntityPlayer player, EnumHand hand) {
            if (player.isSneaking()) {
                FieldGuideClientSettings.bestiarySounds = !FieldGuideClientSettings.bestiarySounds;
                player.addChatMessage((IChatComponent)new ChatComponentTranslation(FieldGuideClientSettings.bestiarySounds ? "srpguide.sounds.on" : "srpguide.sounds.off", new Object[0]));
                return new ActionResult(EnumActionResult.SUCCESS, (Object)player.getHeldItem(hand));
            }
            BestiaryNetwork.CH.sendToServer((IMessage)new PacketBestiaryRequest());
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiBestiary(player));
            return new ActionResult(EnumActionResult.SUCCESS, (Object)player.getHeldItem(hand));
        }
    }
}

