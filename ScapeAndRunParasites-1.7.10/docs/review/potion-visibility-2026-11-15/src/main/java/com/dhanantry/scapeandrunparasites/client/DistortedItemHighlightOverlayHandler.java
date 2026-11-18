/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiIngame
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Pre
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DistortedItemHighlightOverlayHandler {
    private Field remainingHighlightTicksField;
    private Field highlightingItemStackField;
    private int savedTicks = -1;
    private ItemStack savedStack = ItemStack.EMPTY;
    private boolean suppressedThisFrame = false;

    public DistortedItemHighlightOverlayHandler() {
        try {
            this.remainingHighlightTicksField = this.findField(GuiIngame.class, "remainingHighlightTicks", "field_92017_k");
            this.highlightingItemStackField = this.findField(GuiIngame.class, "highlightingItemStack", "field_92016_l");
        }
        catch (Exception e) {
            SRPMain.logger.error("Failed to resolve item highlight fields.", (Throwable)e);
            GuiDistortionHelper.itemHighlightHookAvailable = false;
        }
    }

    @SubscribeEvent
    public void onRenderHotbarPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (!GuiDistortionHelper.shouldDistortItemHighlight(Minecraft.getMinecraft())) {
            return;
        }
        if (this.remainingHighlightTicksField == null || this.highlightingItemStackField == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.ingameGUI == null) {
            return;
        }
        try {
            ItemStack stack = (ItemStack)this.highlightingItemStackField.get(mc.ingameGUI);
            int ticks = this.remainingHighlightTicksField.getInt(mc.ingameGUI);
            this.savedStack = stack == null ? ItemStack.EMPTY : stack;
            this.savedTicks = ticks;
            this.suppressedThisFrame = false;
            if (!this.savedStack.isEmpty() && this.savedTicks > 0) {
                this.remainingHighlightTicksField.setInt(mc.ingameGUI, 0);
                this.suppressedThisFrame = true;
            }
        }
        catch (Exception e) {
            GuiDistortionHelper.itemHighlightHookAvailable = false;
            SRPMain.logger.error("Failed to suppress vanilla item highlight text.", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent
    public void onRenderHotbarPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if (!this.suppressedThisFrame) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.ingameGUI == null || mc.fontRenderer == null) {
            this.resetFrameState();
            return;
        }
        try {
            if (this.savedTicks >= 0 && this.remainingHighlightTicksField != null) {
                this.remainingHighlightTicksField.setInt(mc.ingameGUI, this.savedTicks);
            }
            if (this.savedStack.isEmpty() || this.savedTicks <= 0) {
                this.resetFrameState();
                return;
            }
            String s = this.savedStack.getDisplayName();
            if (s == null || s.isEmpty()) {
                this.resetFrameState();
                return;
            }
            s = EnumChatFormatting.getTextWithoutFormattingCodes((String)s);
            s = GuiDistortionHelper.jamText(s);
            ScaledResolution res = new ScaledResolution(mc);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            int color = 0xFFFFFF;
            int alpha = (int)((float)this.savedTicks * 256.0f / 10.0f);
            if (alpha > 255) {
                alpha = 255;
            }
            if (alpha <= 0) {
                this.resetFrameState();
                return;
            }
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            int x = (width - mc.fontRenderer.getStringWidth(s)) / 2;
            int y = height - 59;
            if (!mc.thePlayer.capabilities.isCreativeMode) {
                y += 14;
            }
            int argb = color + (alpha << 24);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            mc.fontRenderer.drawStringWithShadow(s, (float)x, (float)(y += 14), argb);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.enableDepth();
        }
        catch (Exception e) {
            GuiDistortionHelper.itemHighlightHookAvailable = false;
            SRPMain.logger.error("Failed to draw distorted item highlight text.", (Throwable)e);
        }
        finally {
            this.resetFrameState();
        }
    }

    private void resetFrameState() {
        this.savedTicks = -1;
        this.savedStack = ItemStack.EMPTY;
        this.suppressedThisFrame = false;
    }

    private Field findField(Class<?> clazz, String ... names) throws NoSuchFieldException {
        for (String name : names) {
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            }
            catch (NoSuchFieldException noSuchFieldException) {
            }
        }
        throw new NoSuchFieldException("Could not find any of the requested fields.");
    }
}

