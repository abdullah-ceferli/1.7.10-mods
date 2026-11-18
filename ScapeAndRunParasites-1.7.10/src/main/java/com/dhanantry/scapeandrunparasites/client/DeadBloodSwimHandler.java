/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.client.DeadBloodFogHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class DeadBloodSwimHandler {
    private static boolean isHeadInDeadBlood(EntityPlayer p) {
        return DeadBloodFogHandler.isEyeInDeadBlood((Entity)p, Minecraft.getMinecraft().getRenderPartialTicks());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        KeyBinding jump;
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        if (e.player == null) {
            return;
        }
        EntityPlayer p = e.player;
        if (p.worldObj.isRemote && DeadBloodSwimHandler.isHeadInDeadBlood(p) && (jump = Minecraft.getMinecraft().gameSettings.keyBindJump).getIsKeyPressed()) {
            p.motionY = Math.min(p.motionY + 0.045, 0.12);
        }
    }
}

