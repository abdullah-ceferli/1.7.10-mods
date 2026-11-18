/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.container.ContainerInfuserFurnace;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfuserFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiInfuserFurnace
extends GuiContainer {
    private static final ResourceLocation TEX_BG = new ResourceLocation("srparasites", "textures/gui/infuser_furnace.png");
    private final InventoryPlayer playerInv;
    private final TileEntityInfuserFurnace te;
    private static final int VANILLA_ARROW_X = 79;
    private static final int VANILLA_ARROW_Y = 35;
    private static final int ARROW_SHIFT_LEFT = 24;

    public GuiInfuserFurnace(InventoryPlayer playerInv, TileEntityInfuserFurnace te) {
        super((Container)new ContainerInfuserFurnace(playerInv, te, playerInv.player));
        this.playerInv = playerInv;
        this.te = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(I18n.format((String)this.te.getCommandSenderName(), (Object[])new Object[0]), 8, 6, 0x404040);
        this.fontRendererObj.drawString(I18n.format((String)"container.inventory", (Object[])new Object[0]), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int infuse;
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.mc.getTextureManager().bindTexture(TEX_BG);
        this.drawTexturedModalRect(x, y, 0, 0, 176, 166);
        this.mc.getTextureManager().bindTexture(TEX_BG);
        int smelt = Math.min(this.getCookScaled(this.te.getField(2), 24), 24);
        if (smelt > 0) {
            this.drawTexturedModalRect(x + 80, y + 35, 176, 14, smelt + 1, 16);
        }
        if (this.te.isBurning()) {
            int k = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }
        if ((infuse = this.getCookScaled(this.te.getField(3), 24)) > 0) {
            this.drawTexturedModalRect(x + 80, y + 35, 176, 14, infuse + 1, 16);
        }
    }

    private int getCookScaled(int cook, int pixels) {
        if (cook <= 0) {
            return 0;
        }
        return cook * pixels / 200;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    private int getBurnLeftScaled(int pixels) {
        int burn;
        int cur = this.te.getField(1);
        if (cur <= 0) {
            cur = 200;
        }
        if ((burn = this.te.getField(0)) <= 0) {
            return 0;
        }
        return burn * pixels / cur;
    }
}

