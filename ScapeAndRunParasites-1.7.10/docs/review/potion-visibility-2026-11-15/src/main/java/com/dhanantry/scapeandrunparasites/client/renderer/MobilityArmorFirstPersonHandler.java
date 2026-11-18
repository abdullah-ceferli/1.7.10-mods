/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderSpecificHandEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.model.ModelMobilityArmor;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public final class MobilityArmorFirstPersonHandler {
    private static final ModelMobilityArmor MODEL = new ModelMobilityArmor();

    @SubscribeEvent
    public void onRenderSpecificHand(RenderSpecificHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            return;
        }
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        if (player.isInvisible()) {
            return;
        }
        if (event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty() || chest.getItem() != SRPItems.mobility_armor_chestpiece) {
            return;
        }
        ItemStack held = player.getHeldItem(event.getHand());
        if (!held.isEmpty()) {
            return;
        }
        event.setCanceled(true);
        EnumHandSide side = MobilityArmorFirstPersonHandler.getHandSide(player, event.getHand());
        GlStateManager.pushMatrix();
        MobilityArmorFirstPersonHandler.applyVanillaFirstPersonArmTransform(side, event.getEquipProgress(), event.getSwingProgress());
        GlStateManager.translate((float)0.0f, (float)0.03f, (float)0.0f);
        MODEL.renderFirstPersonArm((Entity)player, side, 0.0625f);
        GlStateManager.popMatrix();
    }

    private static EnumHandSide getHandSide(EntityPlayerSP player, EnumHand hand) {
        EnumHandSide main = player.getPrimaryHand();
        if (hand == EnumHand.MAIN_HAND) {
            return main;
        }
        return main == EnumHandSide.RIGHT ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
    }

    private static void applyVanillaFirstPersonArmTransform(EnumHandSide side, float equipProgress, float swingProgress) {
        boolean right = side == EnumHandSide.RIGHT;
        float sideSign = right ? 1.0f : -1.0f;
        float swingRoot = MathHelper.sqrt_float((float)swingProgress);
        float xSwing = -0.3f * MathHelper.sin((float)(swingRoot * (float)Math.PI));
        float ySwing = 0.4f * MathHelper.sin((float)(swingRoot * ((float)Math.PI * 2)));
        float zSwing = -0.4f * MathHelper.sin((float)(swingProgress * (float)Math.PI));
        GlStateManager.translate((float)(sideSign * (xSwing + 0.64000005f)), (float)(ySwing - 0.35f + equipProgress * -0.6f), (float)(zSwing - 0.71999997f));
        GlStateManager.rotate((float)(sideSign * 45.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        float swingCurveA = MathHelper.sin((float)(swingProgress * swingProgress * (float)Math.PI));
        float swingCurveB = MathHelper.sin((float)(swingRoot * (float)Math.PI));
        GlStateManager.rotate((float)(sideSign * swingCurveB * 70.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(sideSign * swingCurveA * -20.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((float)(sideSign * -1.0f), (float)3.6f, (float)3.5f);
        GlStateManager.rotate((float)(sideSign * 120.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.rotate((float)200.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.rotate((float)(sideSign * -135.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((float)(sideSign * 5.6f), (float)0.0f, (float)0.0f);
    }
}

