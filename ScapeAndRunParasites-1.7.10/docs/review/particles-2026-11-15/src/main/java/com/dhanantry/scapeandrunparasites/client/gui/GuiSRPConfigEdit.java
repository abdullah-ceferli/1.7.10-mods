/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiTextField
 *  net.minecraft.client.resources.I18n
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.gui.GuiSRPConfigView;
import com.dhanantry.scapeandrunparasites.client.gui.SRPConfigList;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiSRPConfigEdit
extends GuiScreen {
    private final GuiSRPConfigView parent;
    private final SRPConfigList.Entry entry;
    private GuiTextField field;
    private static final int BTN_CANCEL = 0;
    private static final int BTN_APPLY = 1;

    public GuiSRPConfigEdit(GuiSRPConfigView parent, SRPConfigList.Entry entry) {
        this.parent = parent;
        this.entry = entry;
    }

    public void initGui() {
        this.buttonList.clear();
        int cx = this.width / 2;
        this.field = new GuiTextField(7001, this.fontRendererObj, cx - 140, 70, 280, 20);
        this.field.setMaxStringLength(256);
        this.field.setText(this.entry.value == null ? "" : this.entry.value);
        this.field.setFocused(true);
        this.buttonList.add(new GuiButton(0, cx - 140, this.height - 28, 138, 20, I18n.format((String)"gui.cancel", (Object[])new Object[0])));
        this.buttonList.add(new GuiButton(1, cx + 2, this.height - 28, 138, 20, I18n.format((String)"gui.srparasites.config.apply", (Object[])new Object[0])));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen((GuiScreen)this.parent);
            return;
        }
        if (button.id == 1) {
            String newVal = this.field.getText();
            this.parent.applyEditedValue(this.entry, newVal);
            this.mc.displayGuiScreen((GuiScreen)this.parent);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.field != null && this.field.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.field != null) {
            this.field.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format((String)"gui.srparasites.config.edit.title", (Object[])new Object[0]), this.width / 2, 20, 0xFFFFFF);
        this.drawString(this.fontRendererObj, I18n.format((String)"gui.srparasites.config.edit.key", (Object[])new Object[]{this.entry.category, this.entry.key}), this.width / 2 - 140, 44, 0xAAAAAA);
        if (this.field != null) {
            this.field.drawTextBox();
            this.field.updateCursorCounter();
        }
        this.drawString(this.fontRendererObj, I18n.format((String)"gui.srparasites.config.edit.todo", (Object[])new Object[0]), this.width / 2 - 140, 98, 0x888888);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}

