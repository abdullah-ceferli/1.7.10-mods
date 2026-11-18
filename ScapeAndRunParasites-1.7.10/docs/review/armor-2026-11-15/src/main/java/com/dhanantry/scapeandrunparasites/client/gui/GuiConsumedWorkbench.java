/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiButtonImage
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.gui.recipebook.GuiRecipeBook
 *  net.minecraft.client.gui.recipebook.IRecipeShownListener
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.Slot
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.gui.ContainerConsumedWorkbench;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class GuiConsumedWorkbench
extends GuiContainer
implements IRecipeShownListener {
    private static final ResourceLocation CRAFTING_TEX = new ResourceLocation("textures/gui/container/crafting_table.png");
    private final GuiRecipeBook recipeBookGui = new GuiRecipeBook();
    private boolean widthTooNarrow;
    private final InventoryPlayer playerInv;
    private GuiButtonImage recipeButton;

    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    public GuiConsumedWorkbench(InventoryPlayer playerInv, World world, BlockPos pos) {
        super((Container)new ContainerConsumedWorkbench(playerInv, world, pos));
        this.playerInv = playerInv;
    }

    private ContainerConsumedWorkbench getCW() {
        return (ContainerConsumedWorkbench)this.inventorySlots;
    }

    public void initGui() {
        this.widthTooNarrow = this.width < 379;
        super.initGui();
        this.recipeBookGui.func_194303_a(this.width, this.height, this.mc, this.widthTooNarrow, this.getCW().craftMatrix);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        int btnX = this.guiLeft + 5;
        int btnY = this.guiTop + 34;
        this.recipeButton = new GuiButtonImage(10, btnX, btnY, 20, 18, 0, 0, 0, CRAFTING_TEX){

            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                if (!this.visible) {
                    return;
                }
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                mc.getTextureManager().bindTexture(CRAFTING_TEX);
                boolean u = false;
                int v = this.hovered ? 187 : 168;
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, v, this.width, this.height);
            }
        };
        this.buttonList.add(this.recipeButton);
    }

    public void updateScreen() {
        super.updateScreen();
        this.recipeBookGui.tick();
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.recipeBookGui.removed();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 10) {
            this.recipeBookGui.initVisuals(this.widthTooNarrow, this.getCW().craftMatrix);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            this.recipeButton.setPosition(this.guiLeft + 5, this.guiTop + 34);
            return;
        }
        super.actionPerformed(button);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.recipeBookGui.keyPressed(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            super.drawScreen(mouseX, mouseY, partialTicks);
        } else {
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
        }
        this.renderHoveredToolTip(mouseX, mouseY);
        this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(I18n.format((String)"container.crafting", (Object[])new Object[0]), 28, 6, 0x404040);
        this.fontRendererObj.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(CRAFTING_TEX);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    public GuiRecipeBook func_194310_f() {
        return this.recipeBookGui;
    }
}

