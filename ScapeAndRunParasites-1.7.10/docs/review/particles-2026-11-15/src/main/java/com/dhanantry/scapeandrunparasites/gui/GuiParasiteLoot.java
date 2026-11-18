/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.util.ResourceLocation
 */
package com.dhanantry.scapeandrunparasites.gui;

import com.dhanantry.scapeandrunparasites.container.ContainerParasiteLoot;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiParasiteLoot
extends GuiContainer {
    private static final ResourceLocation BG = new ResourceLocation("srparasites", "textures/gui/parasite_loot.png");
    private static final ResourceLocation BUBBLE_TEX = new ResourceLocation("srparasites", "textures/gui/blood_bubble.png");
    private final List<Bubble> bubbles = new ArrayList<Bubble>();
    private final Random fxRand = new Random();
    private int guiTick = 0;
    private static final int MAX_BUBBLES = 28;
    private static final float MIN_SIZE = 8.0f;
    private static final float MAX_SIZE = 24.0f;
    private static final float MIN_SPEED = 0.25f;
    private static final float MAX_SPEED = 0.9f;
    private static final float MAX_ROTSPD = 0.6f;
    private int boostStartTick = -1;
    private int boostActiveUntil = -1;
    private int boostCooldownUntil = 0;
    private static final int BOOST_DURATION_TICKS = 10;
    private static final int BOOST_COOLDOWN_TICKS = 10;
    private static final float BOOST_STRENGTH = 1.25f;
    private int prevFullnessScaled = -1;
    private final TileEntityParasiteLoot te;
    private int fullnessScaled = 0;

    public GuiParasiteLoot(InventoryPlayer inv, TileEntityParasiteLoot te) {
        super((Container)new ContainerParasiteLoot(inv, te, inv.player));
        this.te = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(BUBBLE_TEX);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.disableLighting();
        float oldZ = this.zLevel;
        this.zLevel = -100.0f;
        for (Bubble b : this.bubbles) {
            GlStateManager.pushMatrix();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)b.alpha);
            GlStateManager.translate((float)(b.x + b.size / 2.0f), (float)(b.y + b.size / 2.0f), (float)0.0f);
            GlStateManager.rotate((float)b.rot, (float)0.0f, (float)0.0f, (float)1.0f);
            GlStateManager.translate((float)(-b.size / 2.0f), (float)(-b.size / 2.0f), (float)0.0f);
            GuiParasiteLoot.drawModalRectWithCustomSizedTexture((int)0, (int)0, (float)0.0f, (float)0.0f, (int)((int)b.size), (int)((int)b.size), (float)((int)b.size), (float)((int)b.size));
            GlStateManager.popMatrix();
        }
        this.zLevel = oldZ;
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.mc.getTextureManager().bindTexture(BG);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        int barMax = 160;
        int barH = 6;
        float f = (float)this.fullnessScaled / 1000.0f;
        int barFilled = Math.round((float)barMax * f);
        int barX = x + 8;
        int barY = y + 16;
        GuiParasiteLoot.drawRect((int)barX, (int)barY, (int)(barX + barMax), (int)(barY + barH), (int)-14671840);
        int fillColor = GuiParasiteLoot.colorRedYellowGreen(f);
        GuiParasiteLoot.drawRect((int)barX, (int)barY, (int)(barX + barFilled), (int)(barY + barH), (int)fillColor);
    }

    private void triggerBubbleBoost() {
        if (this.guiTick >= this.boostCooldownUntil) {
            this.boostStartTick = this.guiTick;
            this.boostActiveUntil = this.guiTick + 10;
            this.boostCooldownUntil = this.guiTick + 10;
        }
    }

    private float currentBoostMultiplier() {
        if (this.guiTick >= this.boostActiveUntil || this.boostStartTick < 0) {
            return 1.0f;
        }
        float t = (float)(this.guiTick - this.boostStartTick) / 10.0f;
        float pulse = (float)Math.sin(Math.PI * (double)t);
        return 1.0f + 1.25f * pulse;
    }

    private void spawnBubble() {
        Bubble b = new Bubble();
        b.size = 8.0f + this.fxRand.nextFloat() * 16.0f;
        b.x = this.fxRand.nextFloat() * ((float)this.width - b.size);
        b.y = (float)this.height + b.size;
        b.rot = this.fxRand.nextFloat() * 360.0f;
        b.rotSpeed = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.6f;
        b.speed = 0.25f + this.fxRand.nextFloat() * 0.65f;
        b.alpha = 0.35f + this.fxRand.nextFloat() * 0.35f;
        b.vx = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.15f;
        b.splitTick = this.fxRand.nextFloat() < 0.35f ? this.guiTick + (100 + this.fxRand.nextInt(101)) : -1;
        this.bubbles.add(b);
    }

    private List<Bubble> makeChildBubbles(Bubble parent) {
        int room = Math.max(0, 28 - this.bubbles.size());
        if (room <= 0) {
            return Collections.emptyList();
        }
        if (parent.size < 12.8f) {
            return Collections.emptyList();
        }
        int count = Math.min(2, room);
        ArrayList<Bubble> out = new ArrayList<Bubble>(count);
        for (int i = 0; i < count; ++i) {
            Bubble c = new Bubble();
            float scale = 0.5f + this.fxRand.nextFloat() * 0.2f;
            c.size = Math.max(8.0f, parent.size * scale);
            float offset = i == 0 ? -c.size * 0.4f : c.size * 0.4f;
            c.x = Math.max(0.0f, Math.min((float)this.width - c.size, parent.x + offset));
            c.y = parent.y + this.fxRand.nextFloat() * 2.0f;
            c.rot = this.fxRand.nextFloat() * 360.0f;
            c.rotSpeed = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.6f;
            c.speed = Math.min(1.125f, parent.speed * (1.05f + this.fxRand.nextFloat() * 0.15f));
            c.alpha = Math.min(1.0f, parent.alpha * (0.9f + this.fxRand.nextFloat() * 0.2f));
            c.vx = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.2f;
            c.splitTick = c.size > 14.4f && this.fxRand.nextFloat() < 0.15f ? this.guiTick + (80 + this.fxRand.nextInt(81)) : -1;
            out.add(c);
        }
        return out;
    }

    private static int colorRedYellowGreen(float f) {
        int g;
        int r;
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f < 0.5f) {
            r = 255;
            g = (int)(f / 0.5f * 255.0f);
        } else {
            r = (int)((1.0f - f) / 0.5f * 255.0f);
            g = 255;
        }
        int a = 255;
        int b = 0;
        return a << 24 | r << 16 | g << 8 | b;
    }

    public void updateScreen() {
        int allowed;
        super.updateScreen();
        ++this.guiTick;
        int newFullnessScaled = this.te.getField(0);
        if (this.prevFullnessScaled == -1) {
            this.prevFullnessScaled = newFullnessScaled;
        } else if (newFullnessScaled != this.prevFullnessScaled) {
            this.triggerBubbleBoost();
            this.prevFullnessScaled = newFullnessScaled;
        }
        this.fullnessScaled = newFullnessScaled;
        float boost = this.currentBoostMultiplier();
        List<Bubble> toAdd = null;
        Iterator<Bubble> it = this.bubbles.iterator();
        while (it.hasNext()) {
            Bubble b = it.next();
            b.y -= b.speed * boost;
            b.x += b.vx;
            b.rot += b.rotSpeed * (0.5f + 0.5f * boost);
            if (b.x < -b.size) {
                b.x = -b.size;
            }
            if (b.x > (float)this.width) {
                b.x = this.width;
            }
            if (b.splitTick > 0 && this.guiTick >= b.splitTick) {
                List<Bubble> children = this.makeChildBubbles(b);
                if (!children.isEmpty()) {
                    if (toAdd == null) {
                        toAdd = new ArrayList(children.size());
                    }
                    toAdd.addAll(children);
                }
                it.remove();
                continue;
            }
            if (!(b.y + b.size < 0.0f)) continue;
            it.remove();
        }
        if (toAdd != null && !toAdd.isEmpty() && (allowed = Math.max(0, 28 - this.bubbles.size())) > 0) {
            if (toAdd.size() > allowed) {
                toAdd = toAdd.subList(0, allowed);
            }
            this.bubbles.addAll((Collection<Bubble>)toAdd);
        }
        if (this.bubbles.size() < 28) {
            float f = (float)this.fullnessScaled / 1000.0f;
            float spawnChance = 0.18f + (1.0f - f) * 0.22f;
            if (this.fxRand.nextFloat() < spawnChance) {
                this.spawnBubble();
            }
        }
    }

    private static class Bubble {
        float x;
        float y;
        float size;
        float rot;
        float rotSpeed;
        float speed;
        float alpha;
        float vx;
        int splitTick;

        private Bubble() {
        }
    }
}

