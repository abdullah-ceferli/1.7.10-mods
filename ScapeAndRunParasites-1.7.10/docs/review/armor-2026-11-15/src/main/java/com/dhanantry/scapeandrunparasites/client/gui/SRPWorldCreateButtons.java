/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiCreateWorld
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraftforge.client.event.GuiScreenEvent$ActionPerformedEvent$Post
 *  net.minecraftforge.client.event.GuiScreenEvent$ActionPerformedEvent$Pre
 *  net.minecraftforge.client.event.GuiScreenEvent$InitGuiEvent$Post
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.gui.GuiSRPWorldSettings;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class SRPWorldCreateButtons {
    private static final int BTN_SRP_WORLD_SETTINGS = 50001;
    private static final boolean ENABLE_SRP_WORLD_SETTINGS_BUTTON = true;

    private SRPWorldCreateButtons() {
    }

    @SubscribeEvent
    public static void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post e) {
        if (!SRPConfig.worldGIU) {
            return;
        }
        if (!(e.getGui() instanceof GuiCreateWorld)) {
            return;
        }
        SRPWorldCreateButtons.layoutSrpButton(e.getButtonList());
    }

    private static GuiButton findMoreWorldOptionsButton(List<GuiButton> buttons) {
        String moreOptions = I18n.format((String)"selectWorld.moreWorldOptions", (Object[])new Object[0]);
        for (GuiButton b : buttons) {
            if (b == null || b.displayString == null || !b.displayString.startsWith(moreOptions)) continue;
            return b;
        }
        return null;
    }

    @SubscribeEvent
    public static void onActionPerformedPost(GuiScreenEvent.ActionPerformedEvent.Post e) {
        if (!SRPConfig.worldGIU) {
            return;
        }
        if (!(e.getGui() instanceof GuiCreateWorld)) {
            return;
        }
        SRPWorldCreateButtons.layoutSrpButton(e.getButtonList());
        if (e.getButton() == null) {
            return;
        }
        if (!SRPWorldCreateButtons.isOnMainCreateWorldPage(e.getButtonList())) {
            return;
        }
        if (e.getButton().id == 50001) {
            GuiCreateWorld parent = (GuiCreateWorld)e.getGui();
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiSRPWorldSettings((GuiScreen)parent));
        }
    }

    private static void layoutSrpButton(List<GuiButton> buttons) {
        GuiButton srp = SRPWorldCreateButtons.findButton(buttons, 50001);
        if (srp == null) {
            srp = new GuiButton(50001, 0, 0, 150, 20, I18n.format((String)"gui.srparasites.worldsettings.open", (Object[])new Object[0]));
            buttons.add(srp);
        }
        srp.displayString = I18n.format((String)"gui.srparasites.worldsettings.open", (Object[])new Object[0]);
        GuiButton more = SRPWorldCreateButtons.findMoreWorldOptionsButton(buttons);
        if (more != null) {
            srp.xPosition = more.xPosition;
            srp.yPosition = more.yPosition - 24;
            srp.width = more.width;
            srp.height = more.height;
            srp.visible = true;
            srp.enabled = true;
        } else {
            srp.visible = false;
            srp.enabled = false;
        }
    }

    private static GuiButton findButton(List<GuiButton> buttons, int id) {
        for (GuiButton b : buttons) {
            if (b == null || b.id != id) continue;
            return b;
        }
        return null;
    }

    private static boolean isOnMainCreateWorldPage(List<GuiButton> buttons) {
        String moreOptions = I18n.format((String)"selectWorld.moreWorldOptions", (Object[])new Object[0]);
        for (GuiButton b : buttons) {
            if (b == null || b.displayString == null || !b.displayString.startsWith(moreOptions)) continue;
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onActionPerformedPre(GuiScreenEvent.ActionPerformedEvent.Pre e) {
        String clicked;
        if (!(e.getGui() instanceof GuiCreateWorld)) {
            return;
        }
        if (e.getButton() == null) {
            return;
        }
        List buttons = e.getButtonList();
        GuiButton srp = SRPWorldCreateButtons.findButton(buttons, 50001);
        if (srp == null) {
            return;
        }
        String moreOptions = I18n.format((String)"selectWorld.moreWorldOptions", (Object[])new Object[0]);
        String done = I18n.format((String)"gui.done", (Object[])new Object[0]);
        String string = clicked = e.getButton().displayString == null ? "" : e.getButton().displayString;
        if (clicked.startsWith(moreOptions)) {
            srp.visible = false;
            srp.enabled = false;
            return;
        }
    }
}

