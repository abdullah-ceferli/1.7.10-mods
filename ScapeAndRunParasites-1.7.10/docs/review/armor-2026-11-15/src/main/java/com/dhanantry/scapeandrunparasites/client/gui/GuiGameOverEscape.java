/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiGameOver
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraftforge.client.event.GuiOpenEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.EscapeClientState;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.network.msg.C2SRequestEscape;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiGameOverEscape
extends GuiGameOver {
    private static final int BTN_ESCAPE = 911;
    private GuiButton escapeBtn;

    public GuiGameOverEscape() {
        super(null);
    }

    public void initGui() {
        super.initGui();
        if (EscapeClientState.OFFER) {
            int min = Math.max(0, SRPConfigWorld.escapeMinDistance);
            int max = Math.max(min, SRPConfigWorld.escapeMaxDistance);
            String label = I18n.translateToLocal((String)"gui.srparasites.escape_button") + min + "-" + max + " " + I18n.translateToLocal((String)"gui.srparasites.escape_button_blocks");
            int x = this.width / 2 - 100;
            int bottom = 0;
            for (GuiButton b : this.buttonList) {
                bottom = Math.max(bottom, b.yPosition + b.height);
            }
            int y = bottom + 6;
            this.escapeBtn = new GuiButton(911, x, y, 200, 20, label);
            this.buttonList.add(this.escapeBtn);
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 911) {
            SRPNetwork.CHANNEL.sendToServer((IMessage)new C2SRequestEscape());
            Minecraft.getMinecraft().thePlayer.respawnPlayer();
        } else {
            super.actionPerformed(button);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public static class OpenHook {
        @SubscribeEvent
        public void onOpen(GuiOpenEvent e) {
            if (e.getGui() instanceof GuiGameOver && EscapeClientState.OFFER && !(e.getGui() instanceof GuiGameOverEscape)) {
                e.setGui((GuiScreen)new GuiGameOverEscape());
            }
        }
    }
}

