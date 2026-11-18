/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.layers.LayerArmorBase
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.ForgeHooksClient
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.model.entity.SRPModelBiped;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class SRPLayerBipedArmor
extends LayerArmorBase<ModelBiped> {
    private final RenderLivingBase<?> renderer;
    private float alpha = 1.0f;
    private float colorR = 1.0f;
    private float colorG = 1.0f;
    private float colorB = 1.0f;
    private boolean skipRenderGlint;
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    ModelBiped cachedArmorModelHook = null;

    public SRPLayerBipedArmor(RenderLivingBase<?> rendererIn) {
        super(rendererIn);
        this.renderer = rendererIn;
    }

    protected void initArmor() {
        this.modelLeggings = new SRPModelBiped(0.5f);
        this.modelArmor = new SRPModelBiped(1.0f);
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        byte type = 0;
        if (entitylivingbaseIn instanceof EntityInfPlayer) {
            type = 1;
        }
        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS, type);
        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET, type);
        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD, type);
    }

    private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, byte in) {
        ItemArmor itemarmor;
        ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);
        if (itemstack.getItem() instanceof ItemArmor && (itemarmor = (ItemArmor)itemstack.getItem()).getEquipmentSlot() == slotIn) {
            ModelBase original = this.getModelFromSlot(slotIn);
            if (!(original instanceof SRPModelBiped)) {
                return;
            }
            SRPModelBiped t = (SRPModelBiped)this.getModelFromSlot(slotIn);
            ModelBiped hooked = this.getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, (ModelBiped)original);
            if (!(hooked instanceof SRPModelBiped)) {
                return;
            }
            t = (SRPModelBiped)hooked;
            t.setParent(in);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.setModelSlotVisible2(t, slotIn);
            this.renderer.bindTexture(this.getArmorResource((Entity)entityLivingBaseIn, itemstack, slotIn, null));
            if (itemarmor.hasOverlay(itemstack)) {
                int i = itemarmor.getColor(itemstack);
                float f = (float)(i >> 16 & 0xFF) / 255.0f;
                float f1 = (float)(i >> 8 & 0xFF) / 255.0f;
                float f2 = (float)(i & 0xFF) / 255.0f;
                GlStateManager.color((float)(this.colorR * f), (float)(this.colorG * f1), (float)(this.colorB * f2), (float)this.alpha);
                t.render((Entity)entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                this.renderer.bindTexture(this.getArmorResource((Entity)entityLivingBaseIn, itemstack, slotIn, "overlay"));
            }
            GlStateManager.color((float)this.colorR, (float)this.colorG, (float)this.colorB, (float)this.alpha);
            t.render((Entity)entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (!this.skipRenderGlint && itemstack.hasEffect()) {
                SRPLayerBipedArmor.renderEnchantedGlint(this.renderer, (EntityLivingBase)entityLivingBaseIn, (ModelBase)t, (float)limbSwing, (float)limbSwingAmount, (float)partialTicks, (float)ageInTicks, (float)netHeadYaw, (float)headPitch, (float)scale);
            }
        }
    }

    protected void setModelSlotVisible2(SRPModelBiped model, EntityEquipmentSlot slotIn) {
        this.setModelVisible(model);
        switch (slotIn) {
            case HEAD: {
                model.jointH.showModel = true;
                model.jointHW.showModel = true;
                break;
            }
            case CHEST: {
                model.body.showModel = true;
                model.rightA.showModel = true;
                model.leftA.showModel = true;
                break;
            }
            case LEGS: {
                model.body.showModel = true;
                model.rightL.showModel = true;
                model.leftL.showModel = true;
                break;
            }
            case FEET: {
                model.rightL.showModel = true;
                model.leftL.showModel = true;
            }
        }
    }

    protected void setModelVisible(ModelBiped model) {
        model.setVisible(false);
    }

    protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
        if (this.cachedArmorModelHook == null || this.cachedArmorModelHook.getClass() != model.getClass()) {
            this.cachedArmorModelHook = ForgeHooksClient.getArmorModel((EntityLivingBase)entity, (ItemStack)itemStack, (EntityEquipmentSlot)slot, (ModelBiped)model);
        }
        return ForgeHooksClient.getArmorModel((EntityLivingBase)entity, (ItemStack)itemStack, (EntityEquipmentSlot)slot, (ModelBiped)model);
    }

    protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn) {
        if (model instanceof SRPModelBiped) {
            this.setModelSlotVisible2((SRPModelBiped)model, slotIn);
        }
    }
}

