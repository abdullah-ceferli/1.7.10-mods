/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.util.text.TextFormatting
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.gui.GuiButtonCycle;
import com.dhanantry.scapeandrunparasites.client.gui.GuiSRPConfigView;
import com.dhanantry.scapeandrunparasites.client.gui.GuiSRPWorldPreview;
import com.dhanantry.scapeandrunparasites.client.gui.SRPConfigFile;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldEvents;
import java.io.IOException;
import java.util.Arrays;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public class GuiSRPWorldSettings
extends GuiScreen {
    private final GuiScreen parent;
    private static final int BTN_DONE = 0;
    private static final int BTN_DIFFICULTY = 10;
    private static final int BTN_CHALLENGE = 11;
    private static final int BTN_METEOR = 12;
    private static final int BTN_VECTORS = 13;
    private static final int BTN_STAR_TYPE = 14;
    private static final int BTN_MUSHROOM_TREES = 15;
    private static final int BTN_FRACTURED_TERRAIN = 16;
    private static final int BUTTON_W = 176;
    private static final int BUTTON_H = 20;
    private int previewX;
    private int previewY;
    private int previewW;
    private int previewH;
    private GuiButtonCycle difficultyBtn;
    private GuiButtonCycle challengeBtn;
    private static boolean pendingVectorsEnabled = false;
    private GuiButtonCycle meteorBtn;
    private GuiButtonCycle vectorsBtn;
    private GuiButtonCycle starTypeBtn;
    private GuiButtonCycle mushroomTreesBtn;
    private GuiButtonCycle fracturedTerrainBtn;
    private static int pendingStarType = 0;
    private boolean pendingMushroomTreesEnabled = false;
    private boolean pendingFracturedTerrainEnabled = false;
    private static final int BTN_CFG_GENERAL = 20;
    private static final int BTN_CFG_WORLD = 21;
    private static final int BTN_CFG_SYSTEMS = 22;
    private static final int BTN_CFG_MOBS = 23;

    public GuiSRPWorldSettings(GuiScreen parent) {
        this.parent = parent;
    }

    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int topY = 60;
        int gap = 12;
        int previewDefaultW = 160;
        int totalW = 176 + gap + previewDefaultW;
        int leftX = centerX - totalW / 2;
        this.previewX = leftX + 176 + gap;
        this.previewY = topY - 4;
        this.previewW = Math.min(previewDefaultW, this.width - this.previewX - 12);
        this.previewH = 122;
        if (this.previewW < 110) {
            this.previewW = 110;
        }
        int cfgTopY = topY;
        this.difficultyBtn = new GuiButtonCycle(10, leftX, topY, 176, 20, "gui.srparasites.worldsettings.difficulty", new String[]{"gui.srparasites.worldsettings.difficulty.easy", "gui.srparasites.worldsettings.difficulty.normal", "gui.srparasites.worldsettings.difficulty.hard", "gui.srparasites.worldsettings.difficulty.impossible"}, 1);
        this.meteorBtn = new GuiButtonCycle(12, leftX, topY + 48, 176, 20, "gui.srparasites.worldsettings.meteor", new String[]{"gui.srparasites.worldsettings.meteor.on", "gui.srparasites.worldsettings.meteor.off"}, 1);
        pendingStarType = SRPWorldEntitySpawner.starType;
        this.starTypeBtn = new GuiButtonCycle(14, leftX, topY + 72, 176, 20, "gui.srparasites.worldsettings.star", new String[]{"gui.srparasites.worldsettings.star.normal", "gui.srparasites.worldsettings.star.cold"}, pendingStarType);
        this.mushroomTreesBtn = new GuiButtonCycle(15, leftX, topY + 96, 176, 20, "gui.srparasites.worldsettings.mushroom_trees", new String[]{"gui.srparasites.worldsettings.mushroom_trees.on", "gui.srparasites.worldsettings.mushroom_trees.off"}, this.pendingMushroomTreesEnabled ? 0 : 1);
        this.mushroomTreesBtn.visible = pendingStarType == 1;
        this.fracturedTerrainBtn = new GuiButtonCycle(16, leftX, topY + 120, 176, 20, "gui.srparasites.worldsettings.fractured", new String[]{"gui.srparasites.worldsettings.fractured.on", "gui.srparasites.worldsettings.fractured.off"}, this.pendingFracturedTerrainEnabled ? 0 : 1);
        this.fracturedTerrainBtn.visible = pendingStarType == 1;
        this.buttonList.add(this.difficultyBtn);
        this.buttonList.add(this.meteorBtn);
        this.buttonList.add(this.starTypeBtn);
        this.buttonList.add(this.mushroomTreesBtn);
        this.buttonList.add(this.fracturedTerrainBtn);
        this.buttonList.add(new GuiButton(0, centerX - 100, this.height - 28, 200, 20, I18n.format((String)"gui.srparasites.worldsettings.done", (Object[])new Object[0])));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            boolean coldStar = pendingStarType == 1;
            boolean mushroomTrees = coldStar && this.pendingMushroomTreesEnabled;
            boolean fracturedTerrain = coldStar && this.pendingFracturedTerrainEnabled;
            SRPStarWorldEvents.markCreatingWorld(pendingStarType, mushroomTrees, fracturedTerrain);
            SRPWorldEntitySpawner.starType = pendingStarType;
            this.mc.displayGuiScreen(this.parent);
            return;
        }
        if (button instanceof GuiButtonCycle) {
            GuiButtonCycle cyc = (GuiButtonCycle)button;
            cyc.cycle();
        }
        if (button.id == 12) {
            SRPWorldEntitySpawner.triggerSPAWNING = SRPConfigWorld.meteorActive = !SRPConfigWorld.meteorActive;
        }
        if (button.id == 14) {
            boolean coldStar;
            SRPWorldEntitySpawner.starType = pendingStarType = (pendingStarType + 1) % 2;
            boolean bl = coldStar = pendingStarType == 1;
            if (this.mushroomTreesBtn != null) {
                this.mushroomTreesBtn.visible = coldStar;
            }
            if (!coldStar) {
                this.pendingFracturedTerrainEnabled = false;
            }
            if (this.fracturedTerrainBtn != null) {
                this.fracturedTerrainBtn.visible = coldStar;
            }
        }
        if (button.id == 15) {
            boolean bl = this.pendingMushroomTreesEnabled = !this.pendingMushroomTreesEnabled;
        }
        if (button.id == 16) {
            this.pendingFracturedTerrainEnabled = pendingStarType == 1 ? !this.pendingFracturedTerrainEnabled : false;
        }
        if (button.id == 10) {
            SRPWorldEntitySpawner.choiceNUMBER = (SRPWorldEntitySpawner.choiceNUMBER + 1) % 4;
        }
        if (button.id == 20) {
            this.mc.displayGuiScreen((GuiScreen)new GuiSRPConfigView(this, SRPConfigFile.GENERAL));
            return;
        }
        if (button.id == 21) {
            this.mc.displayGuiScreen((GuiScreen)new GuiSRPConfigView(this, SRPConfigFile.WORLD));
            return;
        }
        if (button.id == 22) {
            this.mc.displayGuiScreen((GuiScreen)new GuiSRPConfigView(this, SRPConfigFile.SYSTEMS));
            return;
        }
        if (button.id == 23) {
            this.mc.displayGuiScreen((GuiScreen)new GuiSRPConfigView(this, SRPConfigFile.MOBS));
            return;
        }
    }

    private void syncButtonToOption(GuiButtonCycle button, String targetKey) {
        if (button == null) {
            return;
        }
        for (int i = 0; i < 8; ++i) {
            if (targetKey.equals(button.getCurrentOptionKey())) {
                return;
            }
            button.cycle();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String starTypeKey;
        String difficultyKey;
        this.syncButtonToOption(this.meteorBtn, SRPConfigWorld.meteorActive ? "gui.srparasites.worldsettings.meteor.on" : "gui.srparasites.worldsettings.meteor.off");
        switch (SRPWorldEntitySpawner.choiceNUMBER) {
            case 0: {
                difficultyKey = "gui.srparasites.worldsettings.difficulty.easy";
                break;
            }
            case 2: {
                difficultyKey = "gui.srparasites.worldsettings.difficulty.hard";
                break;
            }
            case 3: {
                difficultyKey = "gui.srparasites.worldsettings.difficulty.impossible";
                break;
            }
            default: {
                difficultyKey = "gui.srparasites.worldsettings.difficulty.normal";
            }
        }
        this.syncButtonToOption(this.difficultyBtn, difficultyKey);
        switch (pendingStarType) {
            case 1: {
                starTypeKey = "gui.srparasites.worldsettings.star.cold";
                break;
            }
            default: {
                starTypeKey = "gui.srparasites.worldsettings.star.normal";
            }
        }
        this.syncButtonToOption(this.starTypeBtn, starTypeKey);
        if (this.mushroomTreesBtn != null) {
            this.mushroomTreesBtn.visible = pendingStarType == 1;
            this.syncButtonToOption(this.mushroomTreesBtn, this.pendingMushroomTreesEnabled ? "gui.srparasites.worldsettings.mushroom_trees.on" : "gui.srparasites.worldsettings.mushroom_trees.off");
        }
        if (this.fracturedTerrainBtn != null) {
            boolean coldStar;
            this.fracturedTerrainBtn.visible = coldStar = pendingStarType == 1;
            if (!coldStar) {
                this.pendingFracturedTerrainEnabled = false;
            }
            this.syncButtonToOption(this.fracturedTerrainBtn, this.pendingFracturedTerrainEnabled ? "gui.srparasites.worldsettings.fractured.on" : "gui.srparasites.worldsettings.fractured.off");
        }
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format((String)"gui.srparasites.worldsettings.title", (Object[])new Object[0]), this.width / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, I18n.format((String)"gui.srparasites.worldsettings.subtitle", (Object[])new Object[0]), this.width / 2, 34, 0xAAAAAA);
        GuiSRPWorldPreview.draw(this.mc, this.previewX, this.previewY, this.previewW, this.previewH, SRPWorldEntitySpawner.choiceNUMBER, SRPConfigWorld.meteorActive, pendingStarType, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawTooltips(mouseX, mouseY);
    }

    private void drawTooltips(int mouseX, int mouseY) {
        if (this.difficultyBtn != null && this.difficultyBtn.isMouseOver()) {
            String opt = this.difficultyBtn.getCurrentOptionKey();
            if ("gui.srparasites.worldsettings.challenge.off".equals(opt)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.challenge.off", (Object[])new Object[0])), mouseX, mouseY);
            }
            if ("gui.srparasites.worldsettings.difficulty.easy".equals(opt)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.difficulty.easy", (Object[])new Object[0])), mouseX, mouseY);
            } else if ("gui.srparasites.worldsettings.difficulty.normal".equals(opt)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.difficulty.normal", (Object[])new Object[0])), mouseX, mouseY);
            } else if ("gui.srparasites.worldsettings.difficulty.hard".equals(opt)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.difficulty.hard", (Object[])new Object[0])), mouseX, mouseY);
            } else if ("gui.srparasites.worldsettings.difficulty.impossible".equals(opt)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.difficulty.impossible", (Object[])new Object[0])), mouseX, mouseY);
            } else if (this.starTypeBtn != null && this.starTypeBtn.isMouseOver()) {
                String optKey = this.starTypeBtn.getCurrentOptionKey();
                if ("gui.srparasites.worldsettings.star.cold".equals(optKey)) {
                    this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.cold", (Object[])new Object[0])), mouseX, mouseY);
                } else {
                    this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.normal", (Object[])new Object[0])), mouseX, mouseY);
                }
                return;
            }
            return;
        }
        if (this.challengeBtn != null && this.challengeBtn.isMouseOver()) {
            String optKey = this.challengeBtn.getCurrentOptionKey();
            if ("gui.srparasites.worldsettings.challenge.c1".equals(optKey)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.challenge.c1", (Object[])new Object[0])), mouseX, mouseY);
            } else if ("gui.srparasites.worldsettings.challenge.c2".equals(optKey)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.challenge.c2", (Object[])new Object[0])), mouseX, mouseY);
            } else if ("gui.srparasites.worldsettings.challenge.c3".equals(optKey)) {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.challenge.c3", (Object[])new Object[0])), mouseX, mouseY);
            } else {
                this.drawHoveringText(Arrays.asList(I18n.format((String)"gui.srparasites.worldsettings.tooltip.challenge", (Object[])new Object[0])), mouseX, mouseY);
            }
            return;
        }
        if (this.meteorBtn != null && this.meteorBtn.isMouseOver()) {
            int dayTicks = SRPConfig.dayTickValue > 0 ? SRPConfig.dayTickValue : 24000;
            int gateTicks = SRPConfig.spawnDays;
            int checkTicks = SRPConfigWorld.meteorTick;
            double gateDays = (double)gateTicks / (double)dayTicks;
            double checkDays = (double)checkTicks / (double)dayTicks;
            double chancePct = SRPConfigWorld.meteorChance * 100.0;
            this.drawHoveringText(Arrays.asList(EnumChatFormatting.RED + I18n.format((String)"gui.srparasites.worldsettings.meteor", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.meteor", (Object[])new Object[]{String.format("%.2f", gateDays), String.format("%.3f", checkDays), String.format("%,d", checkTicks), String.format("%.2f", chancePct)}), SRPConfigWorld.meteorActive ? EnumChatFormatting.DARK_RED + I18n.format((String)"gui.srparasites.worldsettings.tooltip.meteor.active", (Object[])new Object[0]) : EnumChatFormatting.DARK_GREEN + I18n.format((String)"gui.srparasites.worldsettings.tooltip.meteor.inactive", (Object[])new Object[0])), mouseX, mouseY);
            return;
        }
        if (this.starTypeBtn != null && this.starTypeBtn.isMouseOver()) {
            String optKey = this.starTypeBtn.getCurrentOptionKey();
            if ("gui.srparasites.worldsettings.star.cold".equals(optKey)) {
                this.drawHoveringText(Arrays.asList(EnumChatFormatting.AQUA + I18n.format((String)"gui.srparasites.worldsettings.star.cold", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.cold.1", (Object[])new Object[0]), EnumChatFormatting.BLUE + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.cold.2", (Object[])new Object[0]), EnumChatFormatting.WHITE + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.cold.3", (Object[])new Object[0])), mouseX, mouseY);
            } else if ("gui.srparasites.worldsettings.star.warm".equals(optKey)) {
                this.drawHoveringText(Arrays.asList(EnumChatFormatting.GOLD + I18n.format((String)"gui.srparasites.worldsettings.star.warm", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.warm.1", (Object[])new Object[0]), EnumChatFormatting.YELLOW + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.warm.2", (Object[])new Object[0]), EnumChatFormatting.RED + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.warm.3", (Object[])new Object[0])), mouseX, mouseY);
            } else {
                this.drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + I18n.format((String)"gui.srparasites.worldsettings.star.normal", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.normal.1", (Object[])new Object[0]), EnumChatFormatting.WHITE + I18n.format((String)"gui.srparasites.worldsettings.tooltip.star.normal.2", (Object[])new Object[0])), mouseX, mouseY);
            }
            return;
        }
        if (this.mushroomTreesBtn != null && this.mushroomTreesBtn.visible && this.mushroomTreesBtn.isMouseOver()) {
            this.drawHoveringText(Arrays.asList(EnumChatFormatting.DARK_GREEN + I18n.format((String)"gui.srparasites.worldsettings.mushroom_trees", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.mushroom_trees.1", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.mushroom_trees.2", (Object[])new Object[0]), this.pendingMushroomTreesEnabled ? EnumChatFormatting.RED + I18n.format((String)"gui.srparasites.worldsettings.tooltip.mushroom_trees.warning", (Object[])new Object[0]) : EnumChatFormatting.GREEN + I18n.format((String)"gui.srparasites.worldsettings.tooltip.mushroom_trees.off", (Object[])new Object[0])), mouseX, mouseY);
            return;
        }
        if (this.fracturedTerrainBtn != null && this.fracturedTerrainBtn.visible && this.fracturedTerrainBtn.isMouseOver()) {
            this.drawHoveringText(Arrays.asList(EnumChatFormatting.DARK_RED + I18n.format((String)"gui.srparasites.worldsettings.fractured", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.fractured.1", (Object[])new Object[0]), EnumChatFormatting.GRAY + I18n.format((String)"gui.srparasites.worldsettings.tooltip.fractured.2", (Object[])new Object[0]), this.pendingFracturedTerrainEnabled ? EnumChatFormatting.RED + I18n.format((String)"gui.srparasites.worldsettings.tooltip.fractured.warning", (Object[])new Object[0]) : EnumChatFormatting.GREEN + I18n.format((String)"gui.srparasites.worldsettings.tooltip.fractured.off", (Object[])new Object[0])), mouseX, mouseY);
            return;
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}

