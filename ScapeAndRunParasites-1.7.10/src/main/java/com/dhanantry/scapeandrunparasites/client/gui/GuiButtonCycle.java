/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.resources.I18n
 *  org.lwjgl.opengl.GL11
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

public class GuiButtonCycle
extends GuiButton {
    private final String labelKey;
    private final String[] optionKeys;
    private int index;

    public GuiButtonCycle(int buttonId, int x, int y, int width, int height, String labelKey, String[] optionKeys, int defaultIndex) {
        super(buttonId, x, y, width, height, "");
        this.labelKey = labelKey;
        this.optionKeys = optionKeys;
        this.index = this.clampIndex(defaultIndex);
        this.updateDisplayString();
    }

    private int clampIndex(int i) {
        if (this.optionKeys == null || this.optionKeys.length == 0) {
            return 0;
        }
        if (i < 0) {
            return 0;
        }
        if (i >= this.optionKeys.length) {
            return this.optionKeys.length - 1;
        }
        return i;
    }

    public void cycle() {
        if (this.optionKeys == null || this.optionKeys.length == 0) {
            return;
        }
        this.index = (this.index + 1) % this.optionKeys.length;
        this.updateDisplayString();
    }

    public int getIndex() {
        return this.index;
    }

    public String getCurrentOptionKey() {
        if (this.optionKeys == null || this.optionKeys.length == 0) {
            return "";
        }
        return this.optionKeys[this.index];
    }

    public void updateDisplayString() {
        String label = I18n.format((String)this.labelKey, (Object[])new Object[0]);
        String option = this.optionKeys != null && this.optionKeys.length > 0 ? I18n.format((String)this.optionKeys[this.index], (Object[])new Object[0]) : "";
        this.displayString = label + ": " + option;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        this.updateDisplayString();
        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    private void drawScaledCenteredString(Minecraft mc, String text, int centerX, int centerY, int maxWidth, int color) {
        int textWidth = mc.fontRenderer.getStringWidth(text);
        if (textWidth <= maxWidth) {
            mc.fontRenderer.drawStringWithShadow(text, (float)centerX - (float)textWidth / 2.0f, (float)centerY, color);
            return;
        }
        float scale = (float)maxWidth / (float)textWidth;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)centerX, (float)centerY, (float)0.0f);
        GL11.glScalef((float)scale, (float)scale, (float)1.0f);
        mc.fontRenderer.drawStringWithShadow(text, (float)(-textWidth) / 2.0f, 0.0f, color);
        GL11.glPopMatrix();
    }
}

