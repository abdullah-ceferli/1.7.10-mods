/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.Slot
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.gui;

import com.dhanantry.scapeandrunparasites.container.ScannerContainer;
import com.dhanantry.scapeandrunparasites.network.MsgRequestScan;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ScannerGui
extends GuiContainer {
    private static final ResourceLocation BG = new ResourceLocation("srparasites", "textures/gui/scanner_gui.png");
    private static final int MODULE_SLOT_INDEX = 0;
    private final TileEntityRelayController te;
    private GuiButton scanBtn;
    private int cdTotalTicks = 0;
    private long cdEndMs = 0L;
    private float cdSlide = 0.0f;

    public ScannerGui(InventoryPlayer playerInv, TileEntityRelayController te) {
        super((Container)new ScannerContainer(playerInv, te));
        this.te = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    public void initGui() {
        super.initGui();
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.scanBtn = new GuiButton(0, x + 120, y + 34, 48, 20, I18n.format((String)"chat.srparasites.relay.scan_text", (Object[])new Object[0]));
        this.buttonList.add(this.scanBtn);
        try {
            int remaining = this.te.getCooldownRemainingTicks();
            int total = this.te.getCooldownTotalTicks();
            if (remaining > 0 && total > 0) {
                this.cdTotalTicks = total;
                this.cdEndMs = System.currentTimeMillis() + (long)remaining * 50L;
                this.cdSlide = 1.0f;
            } else {
                this.cdTotalTicks = total;
                this.cdEndMs = 0L;
                this.cdSlide = 0.0f;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.updateScanButtonEnabled();
    }

    public void updateScreen() {
        super.updateScreen();
        this.animateCooldownBar();
        this.updateScanButtonEnabled();
    }

    private boolean isCooldownVisible() {
        if (this.cdTotalTicks <= 0) {
            return false;
        }
        long now = System.currentTimeMillis();
        return now < this.cdEndMs;
    }

    private void updateScanButtonEnabled() {
        if (this.scanBtn == null) {
            return;
        }
        boolean hasModule = false;
        if (!this.inventorySlots.inventorySlots.isEmpty()) {
            Slot moduleSlot = this.inventorySlots.getSlot(0);
            hasModule = moduleSlot != null && moduleSlot.getHasStack();
        }
        boolean formed = this.te != null && this.te.formed;
        this.scanBtn.enabled = hasModule && formed && !this.isCooldownVisible();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == this.scanBtn) {
            SRPNetwork.CHANNEL.sendToServer((IMessage)new MsgRequestScan(this.te.getPos()));
        }
        super.actionPerformed(button);
    }

    public void onCooldownUpdate(int totalTicks, int remainingTicks, boolean started) {
        this.cdTotalTicks = Math.max(0, totalTicks);
        long now = System.currentTimeMillis();
        this.cdEndMs = now + (long)Math.max(0, remainingTicks) * 50L;
        if (this.isCooldownVisible() && this.cdSlide < 0.1f) {
            this.cdSlide = 0.1f;
        }
        this.updateScanButtonEnabled();
    }

    private void animateCooldownBar() {
        boolean visible = this.isCooldownVisible();
        float target = visible ? 1.0f : 0.0f;
        this.cdSlide += (target - this.cdSlide) * 0.25f;
        if (!visible && this.cdSlide < 0.01f) {
            this.cdSlide = 0.0f;
        }
    }

    private void drawCooldownBar(int mouseX, int mouseY, float partialTicks) {
        float progress;
        if (this.cdSlide <= 0.0f || this.cdTotalTicks <= 0) {
            return;
        }
        int barH = 20;
        int x = this.guiLeft;
        int w = this.xSize;
        int yHidden = this.guiTop + this.ySize + barH;
        int yShown = this.guiTop + this.ySize;
        int y = (int)((float)yHidden + (float)(yShown - yHidden) * this.cdSlide);
        ScannerGui.drawRect((int)x, (int)y, (int)(x + w), (int)(y + barH), (int)-1442840576);
        long now = System.currentTimeMillis();
        float remainingMs = Math.max(0L, this.cdEndMs - now);
        float totalMs = (float)this.cdTotalTicks * 50.0f;
        float f = progress = totalMs > 0.0f ? 1.0f - remainingMs / totalMs : 0.0f;
        if (progress < 0.0f) {
            progress = 0.0f;
        }
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        int pad = 2;
        int filled = (int)((float)(w - pad * 2) * progress);
        ScannerGui.drawRect((int)(x + pad), (int)(y + pad), (int)(x + pad + filled), (int)(y + barH - pad), (int)-11141291);
        int secs = (int)Math.ceil((double)remainingMs / 1000.0);
        String label = I18n.format((String)"gui.srparasites.scanner.cooldown", (Object[])new Object[]{secs});
        this.fontRendererObj.drawString(label, x + 6, y + 6, 0xFFFFFF);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(BG);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCooldownBar(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}

