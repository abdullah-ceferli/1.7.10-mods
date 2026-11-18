/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.bestiary.blocks;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.bestiary.blocks.BlockBestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.SRPBlockCompendiumRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.net.BestiaryNetwork;
import com.dhanantry.scapeandrunparasites.bestiary.net.PacketBestiarySync;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

@Mod.EventBusSubscriber(modid="srparasites")
public class BlockDiscoveryHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = e.player;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP p = (EntityPlayerMP)player;
        IBestiaryProgress prog = (IBestiaryProgress)p.getCapability(BestiaryCapability.CAP, null);
        if (prog == null) {
            return;
        }
        boolean changed = false;
        if (p.inventory != null) {
            changed |= BlockDiscoveryHandler.scanInventory((IInventory)p.inventory, prog);
        }
        if (changed) {
            BestiaryNetwork.CH.sendTo((IMessage)new PacketBestiarySync(prog), p);
        }
    }

    private static boolean scanInventory(IInventory inv, IBestiaryProgress prog) {
        boolean changed = false;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            BlockBestiaryEntry entry;
            ItemBlock itemBlock;
            ResourceLocation id;
            ItemStack st = inv.getStackInSlot(i);
            if (st.isEmpty() || !(st.getItem() instanceof ItemBlock) || (id = RegistryNames.get((itemBlock = (ItemBlock)st.getItem()).getBlock())) == null || (entry = SRPBlockCompendiumRegistry.get(id)) == null || prog.hasSeenBlock(id)) continue;
            prog.markBlockSeen(id);
            changed = true;
        }
        return changed;
    }
}

