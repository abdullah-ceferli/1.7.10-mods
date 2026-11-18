/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.client.model;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.util.MathHelper;

public abstract class ModelEffect
extends ModelSRP {
    public void renderTwo(ModelRenderer in, float ageInTicks, float f5, float width, float height, double n1, float n2, double n3, double n4) {
        double f1 = n1 + (double)MathHelper.sin((float)(ageInTicks * n2)) * n3 + n4;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)in.offsetX, (float)in.offsetY, (float)in.offsetZ);
        GlStateManager.translate((float)(in.rotationPointX * f5), (float)(in.rotationPointY * f5), (float)(in.rotationPointZ * f5));
        GlStateManager.scale((double)(f1 + (double)width), (double)(f1 + (double)height), (double)(f1 + (double)width));
        GlStateManager.translate((float)(-in.offsetX), (float)(-in.offsetY), (float)(-in.offsetZ));
        GlStateManager.translate((float)(-in.rotationPointX * f5), (float)(-in.rotationPointY * f5), (float)(-in.rotationPointZ * f5));
        in.render(f5);
        GlStateManager.popMatrix();
    }
}

