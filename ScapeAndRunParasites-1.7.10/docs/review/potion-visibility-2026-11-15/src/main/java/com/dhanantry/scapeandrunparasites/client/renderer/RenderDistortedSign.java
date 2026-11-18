/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiUtilRenderComponents
 *  net.minecraft.client.model.ModelSign
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntitySign
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.ITextComponent
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IChatComponent;

public class RenderDistortedSign
extends TileEntitySpecialRenderer<TileEntitySign> {
    private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
    private final ModelSign model = new ModelSign();

    public void render(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Block block = te.getBlockType();
        GlStateManager.pushMatrix();
        if (block == Blocks.standing_sign) {
            GlStateManager.translate((float)((float)x + 0.5f), (float)((float)y + 0.5f), (float)((float)z + 0.5f));
            float rot = (float)(te.getBlockMetadata() * 360) / 16.0f;
            GlStateManager.rotate((float)(-rot), (float)0.0f, (float)1.0f, (float)0.0f);
            this.model.signStick.showModel = true;
        } else {
            int meta = te.getBlockMetadata();
            float rot = 0.0f;
            if (meta == 2) {
                rot = 180.0f;
            }
            if (meta == 4) {
                rot = 90.0f;
            }
            if (meta == 5) {
                rot = -90.0f;
            }
            GlStateManager.translate((float)((float)x + 0.5f), (float)((float)y + 0.5f), (float)((float)z + 0.5f));
            GlStateManager.rotate((float)(-rot), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.translate((float)0.0f, (float)-0.3125f, (float)-0.4375f);
            this.model.signStick.showModel = false;
        }
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode((int)5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale((float)4.0f, (float)2.0f, (float)1.0f);
            GlStateManager.translate((float)0.0625f, (float)0.0625f, (float)0.0625f);
            GlStateManager.matrixMode((int)5888);
        } else {
            this.bindTexture(SIGN_TEXTURE);
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scale((float)0.6666667f, (float)-0.6666667f, (float)-0.6666667f);
        this.model.renderSign();
        GlStateManager.popMatrix();
        FontRenderer font = this.getFontRenderer();
        GlStateManager.translate((float)0.0f, (float)0.33333334f, (float)0.046666667f);
        GlStateManager.scale((float)0.010416667f, (float)-0.010416667f, (float)0.010416667f);
        GlStateManager.glNormal3f((float)0.0f, (float)0.0f, (float)-0.010416667f);
        GlStateManager.depthMask((boolean)false);
        if (destroyStage < 0) {
            boolean distortSigns = GuiDistortionHelper.shouldDistortSigns(Minecraft.getMinecraft());
            for (int j = 0; j < te.signText.length; ++j) {
                String s;
                if (te.signText[j] == null) continue;
                IChatComponent itextcomponent = te.signText[j];
                List list = GuiUtilRenderComponents.splitText((IChatComponent)itextcomponent, (int)90, (FontRenderer)font, (boolean)false, (boolean)true);
                String string = s = list != null && !list.isEmpty() ? ((IChatComponent)list.get(0)).getFormattedText() : "";
                if (j == te.lineBeingEdited) {
                    s = "> " + s + " <";
                }
                String shown = distortSigns ? GuiDistortionHelper.jamText(s) : s;
                font.drawString(shown, -font.getStringWidth(shown) / 2, j * 10 - te.signText.length * 5, 0);
            }
        }
        GlStateManager.depthMask((boolean)true);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.popMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode((int)5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode((int)5888);
        }
    }
}

