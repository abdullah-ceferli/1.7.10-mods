/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.opengl.GL11
 */
package com.dhanantry.scapeandrunparasites.client.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class ModelMobilityArmor
extends ModelBiped {
    private static final ResourceLocation TEXTURE = new ResourceLocation("srparasites", "textures/models/armor/mobility_armor.png");
    private boolean renderingSneakingEntity;
    public final FloatModelPart head;
    public final FloatModelPart body;
    public final FloatModelPart rightArm;
    public final FloatModelPart leftArm;
    public final FloatModelPart rightLeg;
    public final FloatModelPart leftLeg;
    private float renderAgeInTicks;
    private final FloatModelPart H4_r1;
    private final FloatModelPart H3_r1;
    private final FloatModelPart H5_r1;
    private final FloatModelPart H3_r2;
    private final FloatModelPart TENDRIL1;
    private final FloatModelPart TDR1;
    private final FloatModelPart TDR2;
    private final FloatModelPart TENDRIL2;
    private final FloatModelPart TDR3;
    private final FloatModelPart TDR4;
    private final FloatModelPart Teeth;
    private final FloatModelPart H4_r2;
    private final FloatModelPart H5_r2;
    private final FloatModelPart H6_r1;
    private final FloatModelPart B5_r1;
    private final FloatModelPart B5_r2;
    private final FloatModelPart TENDRIL3;
    private final FloatModelPart TDR5;
    private final FloatModelPart TDR6;
    private final FloatModelPart TENDRIL4;
    private final FloatModelPart TDR7;
    private final FloatModelPart TDR8;
    private final FloatModelPart B6_r1;
    private final FloatModelPart B5_r3;
    private final FloatModelPart B7_r1;
    private final FloatModelPart B6_r2;
    private static boolean lastCullState;
    private static boolean lastAlphaState;

    public ModelMobilityArmor() {
        this.textureWidth = 124;
        this.textureHeight = 55;
        this.head = new FloatModelPart(this.textureWidth, this.textureHeight, "head");
        this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.setLocalOffset(0.0f, -6.0f, 0.0f);
        this.head.addBox(0, 0, -4.2f, -2.2f, -4.2f, 8.4f, 8.4f, 8.4f, 0.0f, false);
        this.head.addBox(24, 0, -4.0f, -1.5f, -6.1f, 8.0f, 2.0f, 2.0f, 0.0f, false);
        this.H4_r1 = new FloatModelPart(this.textureWidth, this.textureHeight, "H4_r1");
        this.H4_r1.setRotationPoint(-4.0f, 0.5f, 0.5f);
        this.H4_r1.setRotationAngle(0.0f, 0.3491f, 0.0f);
        this.H4_r1.addBox(44, 0, -1.2f, -1.7f, -1.7f, 2.4f, 3.4f, 3.4f, 0.0f, true);
        this.H3_r1 = new FloatModelPart(this.textureWidth, this.textureHeight, "H3_r1");
        this.H3_r1.setRotationPoint(4.0f, 0.5f, 0.5f);
        this.H3_r1.setRotationAngle(0.0f, -0.3491f, 0.0f);
        this.H3_r1.addBox(32, 8, -1.2f, -1.7f, -1.7f, 2.4f, 3.4f, 3.4f, 0.0f, false);
        this.H5_r1 = new FloatModelPart(this.textureWidth, this.textureHeight, "H5_r1");
        this.H5_r1.setRotationPoint(-0.1f, 4.9183f, -4.7977f);
        this.H5_r1.setRotationAngle(-0.3491f, 0.0f, 0.0f);
        this.H5_r1.addBox(0, 0, 2.4f, -1.0f, 0.0f, 2.2f, 2.0f, 2.0f, 0.0f, true);
        this.H5_r1.addBox(0, 4, -4.4f, -1.0f, 0.0f, 2.2f, 2.0f, 2.0f, 0.0f, false);
        this.H3_r2 = new FloatModelPart(this.textureWidth, this.textureHeight, "H3_r2");
        this.H3_r2.setRotationPoint(0.0f, -0.9183f, -4.7977f);
        this.H3_r2.setRotationAngle(0.3491f, 0.0f, 0.0f);
        this.H3_r2.addBox(24, 4, -4.1f, -1.0f, -1.0f, 8.2f, 2.0f, 2.0f, 0.0f, false);
        this.TENDRIL1 = new FloatModelPart(this.textureWidth, this.textureHeight, "TENDRIL1");
        this.TENDRIL1.setRotationPoint(5.25f, 0.75f, 1.5f);
        this.TENDRIL1.setRotationAngle(-0.3927f, 0.1745f, 0.0f);
        this.TENDRIL1.addBox(76, 0, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 6.4f, 0.0f, false);
        this.TENDRIL1.addBox(86, 0, -0.95f, -1.05f, -1.8f, 2.2f, 2.2f, 2.1f, 0.0f, false);
        this.TDR1 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR1");
        this.TDR1.setRotationPoint(0.05f, 0.05f, 6.5f);
        this.TDR1.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR1.addBox(92, 0, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 6.4f, 0.0f, false);
        this.TDR2 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR2");
        this.TDR2.setRotationPoint(0.0f, 0.0f, 6.3f);
        this.TDR2.setRotationAngle(0.3927f, 0.0f, 0.0f);
        this.TDR2.addBox(108, 0, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 7.4f, 0.0f, false);
        this.TENDRIL2 = new FloatModelPart(this.textureWidth, this.textureHeight, "TENDRIL2");
        this.TENDRIL2.setRotationPoint(-5.25f, 0.75f, 1.5f);
        this.TENDRIL2.setRotationAngle(-0.3927f, -0.2618f, 0.0f);
        this.TENDRIL2.addBox(76, 8, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 6.4f, 0.0f, true);
        this.TENDRIL2.addBox(86, 8, -1.25f, -1.05f, -1.8f, 2.2f, 2.2f, 2.1f, 0.0f, true);
        this.TDR3 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR3");
        this.TDR3.setRotationPoint(-0.05f, 0.05f, 6.5f);
        this.TDR3.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR3.addBox(92, 8, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 6.4f, 0.0f, true);
        this.TDR4 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR4");
        this.TDR4.setRotationPoint(0.0f, 0.0f, 6.3f);
        this.TDR4.setRotationAngle(0.3927f, 0.0f, 0.0f);
        this.TDR4.addBox(108, 8, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 7.4f, 0.0f, true);
        this.Teeth = new FloatModelPart(this.textureWidth, this.textureHeight, "Teeth");
        this.Teeth.setRotationPoint(0.0f, 1.4f, 0.3f);
        this.H4_r2 = new FloatModelPart(this.textureWidth, this.textureHeight, "H4_r2");
        this.H4_r2.setRotationPoint(0.0f, 1.1f, -5.8f);
        this.H4_r2.setRotationAngle(-0.0873f, 0.0f, 0.0f);
        this.H4_r2.addBox(76, 0, 1.8f, -2.1f, -0.6f, 2.0f, 4.0f, 1.0f, 0.0f, true);
        this.H4_r2.addBox(54, 0, -3.8f, -2.1f, -0.6f, 2.0f, 4.0f, 1.0f, 0.0f, false);
        this.H5_r2 = new FloatModelPart(this.textureWidth, this.textureHeight, "H5_r2");
        this.H5_r2.setRotationPoint(0.0f, 0.6f, -5.8f);
        this.H5_r2.setRotationAngle(0.0873f, 0.0f, 0.0f);
        this.H5_r2.addBox(72, 0, 0.3f, -1.6f, -0.4f, 1.0f, 3.0f, 1.0f, 0.0f, true);
        this.H5_r2.addBox(60, 0, -1.3f, -1.6f, -0.4f, 1.0f, 3.0f, 1.0f, 0.0f, false);
        this.H6_r1 = new FloatModelPart(this.textureWidth, this.textureHeight, "H6_r1");
        this.H6_r1.setRotationPoint(0.0f, 2.6f, -5.5f);
        this.H6_r1.setRotationAngle(-0.0873f, 0.0f, 0.0f);
        this.H6_r1.addBox(68, 0, 3.3f, -1.5f, -0.5f, 1.0f, 3.0f, 1.0f, 0.0f, true);
        this.H6_r1.addBox(64, 0, -4.3f, -1.5f, -0.5f, 1.0f, 3.0f, 1.0f, 0.0f, false);
        this.leftArm = new FloatModelPart(this.textureWidth, this.textureHeight, "leftArm");
        this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
        this.leftArm.addBox(108, 16, -1.2f, -2.1f, -2.2f, 4.4f, 12.2f, 4.4f, 0.0f, false);
        this.leftArm.addBox(0, 29, 2.6f, 3.1f, -2.5f, 1.5f, 8.0f, 5.0f, 0.0f, false);
        this.leftArm.addBox(87, 19, -0.9f, -2.3f, -2.5f, 5.0f, 5.0f, 5.0f, 0.0f, false);
        this.leftArm.addBox(24, 16, -1.4f, 8.1f, -2.3f, 2.0f, 2.2f, 2.0f, 0.0f, false);
        this.leftArm.addBox(0, 19, 0.9f, 8.1f, -2.3f, 2.5f, 2.2f, 1.2f, 0.0f, false);
        this.leftArm.addBox(0, 16, 0.9f, 8.1f, -0.8f, 2.5f, 2.2f, 1.2f, 0.0f, false);
        this.leftArm.addBox(54, 23, 0.9f, 8.1f, 0.7f, 2.5f, 2.2f, 1.2f, 0.0f, false);
        this.rightArm = new FloatModelPart(this.textureWidth, this.textureHeight, "rightArm");
        this.rightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        this.rightArm.addBox(28, 28, -3.2f, -2.1f, -2.2f, 4.4f, 12.2f, 4.4f, 0.0f, true);
        this.rightArm.addBox(44, 28, -4.1f, 3.1f, -2.5f, 1.5f, 8.0f, 5.0f, 0.0f, true);
        this.rightArm.addBox(56, 27, -4.1f, -2.3f, -2.5f, 5.0f, 5.0f, 5.0f, 0.0f, true);
        this.rightArm.addBox(12, 16, -0.6f, 8.1f, -2.3f, 2.0f, 2.2f, 2.0f, 0.0f, true);
        this.rightArm.addBox(44, 6, -3.4f, 8.1f, -2.3f, 2.5f, 2.2f, 1.2f, 0.0f, true);
        this.rightArm.addBox(76, 8, -3.4f, 8.1f, -0.8f, 2.5f, 2.2f, 1.2f, 0.0f, true);
        this.rightArm.addBox(76, 11, -3.4f, 8.1f, 0.7f, 2.5f, 2.2f, 1.2f, 0.0f, true);
        this.leftArm.setRenderScale(1.1f, 1.02f, 1.1f);
        this.rightArm.setRenderScale(1.1f, 1.02f, 1.1f);
        this.body = new FloatModelPart(this.textureWidth, this.textureHeight, "body");
        this.body.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.body.setLocalOffset(0.0f, 24.0f, 0.0f);
        this.body.addBox(0, 16, 4.0f, -25.0f, -3.0f, 2.0f, 7.0f, 6.0f, 0.0f, false);
        this.body.addBox(12, 26, -6.0f, -25.0f, -3.0f, 2.0f, 7.0f, 6.0f, 0.0f, false);
        this.body.addBox(32, 14, -4.0f, -24.0f, -3.0f, 8.0f, 8.0f, 1.0f, 0.0f, false);
        this.body.addBox(16, 16, 3.5f, -18.0f, -2.0f, 1.0f, 6.0f, 4.0f, 0.0f, false);
        this.body.addBox(22, 22, -4.5f, -18.0f, -2.0f, 1.0f, 6.0f, 4.0f, 0.0f, false);
        this.body.addBox(58, 5, -4.0f, -24.0f, 2.0f, 8.0f, 10.0f, 1.0f, 0.0f, false);
        this.body.addBox(50, 16, 0.5f, -24.0f, 3.0f, 4.0f, 4.0f, 2.0f, 0.0f, false);
        this.body.addBox(62, 16, -4.5f, -24.0f, 3.0f, 4.0f, 4.0f, 2.0f, 0.0f, false);
        this.body.addBox(32, 23, -4.0f, -24.5f, -2.0f, 8.0f, 1.0f, 4.0f, 0.0f, false);
        this.body.addBox(56, 22, -4.0f, -12.9f, -2.0f, 8.0f, 1.0f, 4.0f, 0.0f, false);
        this.body.addBox(74, 19, -4.0f, -16.0f, -2.7f, 8.0f, 2.0f, 1.0f, 0.0f, false);
        this.body.addBox(92, 16, -4.0f, -14.0f, -2.3f, 8.0f, 2.0f, 1.0f, 0.0f, false);
        this.body.addBox(74, 16, -4.0f, -14.0f, 1.7f, 8.0f, 2.0f, 1.0f, 0.0f, false);
        this.body.addBox(42, 9, -1.7f, -22.0f, -3.9f, 3.5f, 4.0f, 1.0f, 0.0f, false);
        this.B5_r1 = new FloatModelPart(this.textureWidth, this.textureHeight, "B5_r1");
        this.B5_r1.setRotationPoint(2.5f, -22.5f, -3.5f);
        this.B5_r1.setRotationAngle(0.0f, 0.3927f, 0.0f);
        this.B5_r1.addBox(50, 8, -1.5f, 1.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, true);
        this.B5_r1.addBox(50, 10, -1.5f, 3.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, true);
        this.B5_r2 = new FloatModelPart(this.textureWidth, this.textureHeight, "B5_r2");
        this.B5_r2.setRotationPoint(-2.5f, -22.5f, -3.5f);
        this.B5_r2.setRotationAngle(0.0f, -0.3927f, 0.0f);
        this.B5_r2.addBox(50, 12, -1.5f, 3.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, false);
        this.B5_r2.addBox(50, 14, -1.5f, 1.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, false);
        this.TENDRIL3 = new FloatModelPart(this.textureWidth, this.textureHeight, "TENDRIL3");
        this.TENDRIL3.setRotationPoint(-2.25f, -22.25f, 3.5f);
        this.TENDRIL3.setRotationAngle(-0.6981f, -0.5236f, 0.0f);
        this.TENDRIL3.addBox(87, 29, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 7.4f, 0.0f, true);
        this.TDR5 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR5");
        this.TDR5.setRotationPoint(-0.05f, 0.05f, 7.5f);
        this.TDR5.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR5.addBox(70, 36, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 7.4f, 0.0f, true);
        this.TDR6 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR6");
        this.TDR6.setRotationPoint(0.0f, 0.0f, 7.3f);
        this.TDR6.setRotationAngle(-0.2182f, 0.0f, 0.0f);
        this.TDR6.addBox(96, 29, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 9.4f, 0.0f, true);
        this.TENDRIL4 = new FloatModelPart(this.textureWidth, this.textureHeight, "TENDRIL4");
        this.TENDRIL4.setRotationPoint(2.25f, -22.25f, 3.5f);
        this.TENDRIL4.setRotationAngle(-0.6981f, 0.5236f, 0.0f);
        this.TENDRIL4.addBox(81, 44, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 7.4f, 0.0f, false);
        this.TDR7 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR7");
        this.TDR7.setRotationPoint(0.05f, 0.05f, 7.5f);
        this.TDR7.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR7.addBox(59, 38, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 7.4f, 0.0f, false);
        this.TDR8 = new FloatModelPart(this.textureWidth, this.textureHeight, "TDR8");
        this.TDR8.setRotationPoint(0.0f, 0.0f, 7.3f);
        this.TDR8.setRotationAngle(-0.2182f, 0.0f, 0.0f);
        this.TDR8.addBox(48, 42, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 9.4f, 0.0f, false);
        this.leftLeg = new FloatModelPart(this.textureWidth, this.textureHeight, "leftLeg");
        this.leftLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
        this.leftLeg.addBox(16, 40, -2.1f, -0.1f, -2.1f, 4.2f, 11.1f, 4.2f, 0.0f, false);
        this.leftLeg.addBox(76, 29, -2.2f, 10.0785f, -3.0892f, 4.4f, 2.0f, 5.3f, 0.0f, false);
        this.leftLeg.addBox(14, 29, 0.2f, 10.3785f, -4.2892f, 1.7f, 2.0f, 1.2f, 0.0f, false);
        this.leftLeg.addBox(28, 20, -1.9f, 10.1785f, -4.4892f, 1.7f, 2.2f, 1.4f, 0.0f, false);
        this.leftLeg.addBox(28, 23, -2.9f, 10.1785f, 0.2108f, 1.7f, 2.2f, 1.4f, 0.0f, false);
        this.B6_r1 = new FloatModelPart(this.textureWidth, this.textureHeight, "B6_r1");
        this.B6_r1.setRotationPoint(0.0f, 5.0419f, 4.9088f);
        this.B6_r1.setRotationAngle(-0.2618f, 0.0f, 0.0f);
        this.B6_r1.addBox(55, 27, -1.2f, 2.7f, -2.4f, 2.4f, 4.0f, 1.4f, 0.0f, false);
        this.B5_r3 = new FloatModelPart(this.textureWidth, this.textureHeight, "B5_r3");
        this.B5_r3.setRotationPoint(0.0f, 4.0f, -1.0f);
        this.B5_r3.setRotationAngle(-0.1745f, 0.0f, 0.0f);
        this.B5_r3.addBox(0, 42, -2.2f, -4.0f, -2.0f, 4.4f, 8.0f, 4.0f, 0.0f, false);
        this.rightLeg = new FloatModelPart(this.textureWidth, this.textureHeight, "rightLeg");
        this.rightLeg.setRotationPoint(-2.0f, 12.0f, 0.0f);
        this.rightLeg.addBox(108, 39, -2.1f, -0.1f, -2.1f, 4.2f, 11.1f, 4.2f, 0.0f, true);
        this.rightLeg.addBox(39, 41, -2.2f, 10.0785f, -3.0892f, 4.4f, 2.0f, 5.3f, 0.0f, true);
        this.rightLeg.addBox(62, 42, -1.9f, 10.3785f, -4.2892f, 1.7f, 2.0f, 1.2f, 0.0f, true);
        this.rightLeg.addBox(62, 39, 0.2f, 10.1785f, -4.4892f, 1.7f, 2.2f, 1.4f, 0.0f, true);
        this.rightLeg.addBox(56, 37, 1.2f, 10.1785f, 0.2108f, 1.7f, 2.2f, 1.4f, 0.0f, true);
        this.B7_r1 = new FloatModelPart(this.textureWidth, this.textureHeight, "B7_r1");
        this.B7_r1.setRotationPoint(0.0f, 5.0419f, 4.9088f);
        this.B7_r1.setRotationAngle(-0.2618f, 0.0f, 0.0f);
        this.B7_r1.addBox(71, 38, -1.2f, 2.7f, -2.4f, 2.4f, 4.0f, 1.4f, 0.0f, true);
        this.B6_r2 = new FloatModelPart(this.textureWidth, this.textureHeight, "B6_r2");
        this.B6_r2.setRotationPoint(0.0f, 4.0f, -1.0f);
        this.B6_r2.setRotationAngle(-0.1745f, 0.0f, 0.0f);
        this.B6_r2.addBox(92, 39, -2.2f, -4.0f, -2.0f, 4.4f, 8.0f, 4.0f, 0.0f, true);
        this.head.addChild(this.H4_r1);
        this.head.addChild(this.H3_r1);
        this.head.addChild(this.H5_r1);
        this.head.addChild(this.H3_r2);
        this.head.addChild(this.TENDRIL1);
        this.TENDRIL1.addChild(this.TDR1);
        this.TDR1.addChild(this.TDR2);
        this.head.addChild(this.TENDRIL2);
        this.TENDRIL2.addChild(this.TDR3);
        this.TDR3.addChild(this.TDR4);
        this.head.addChild(this.Teeth);
        this.Teeth.addChild(this.H4_r2);
        this.Teeth.addChild(this.H5_r2);
        this.Teeth.addChild(this.H6_r1);
        this.body.addChild(this.B5_r1);
        this.body.addChild(this.B5_r2);
        this.body.addChild(this.TENDRIL3);
        this.TENDRIL3.addChild(this.TDR5);
        this.TDR5.addChild(this.TDR6);
        this.body.addChild(this.TENDRIL4);
        this.TENDRIL4.addChild(this.TDR7);
        this.TDR7.addChild(this.TDR8);
        this.leftLeg.addChild(this.B6_r1);
        this.leftLeg.addChild(this.B5_r3);
        this.rightLeg.addChild(this.B7_r1);
        this.rightLeg.addChild(this.B6_r2);
        this.setVisibleForSlot(null);
    }

    public ModelMobilityArmor setVisibleForSlot(EntityEquipmentSlot slot) {
        this.head.showModel = slot == null || slot == EntityEquipmentSlot.HEAD;
        this.body.showModel = slot == null || slot == EntityEquipmentSlot.CHEST || slot == EntityEquipmentSlot.LEGS;
        this.rightArm.showModel = slot == null || slot == EntityEquipmentSlot.CHEST;
        this.leftArm.showModel = slot == null || slot == EntityEquipmentSlot.CHEST;
        this.rightLeg.showModel = slot == null || slot == EntityEquipmentSlot.LEGS || slot == EntityEquipmentSlot.FEET;
        this.leftLeg.showModel = slot == null || slot == EntityEquipmentSlot.LEGS || slot == EntityEquipmentSlot.FEET;
        return this;
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entityIn.isInvisible()) {
            return;
        }
        this.renderingSneakingEntity = entityIn.isSneaking();
        this.renderAgeInTicks = ageInTicks;
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.syncBipedPose();
        this.beginMobilityRenderState();
        GL11.glPushMatrix();
        if (this.isSneak) {
            GL11.glTranslatef((float)0.0f, (float)0.2f, (float)0.0f);
        }
        this.head.render(scale);
        this.body.render(scale);
        this.rightArm.render(scale);
        this.leftArm.render(scale);
        this.rightLeg.render(scale);
        this.leftLeg.render(scale);
        GL11.glPopMatrix();
        this.endMobilityRenderState();
    }

    public void renderFirstPersonArm(Entity entityIn, EnumHandSide side, float scale) {
        if (entityIn.isInvisible()) {
            return;
        }
        this.beginMobilityRenderState();
        FloatModelPart arm = side == EnumHandSide.RIGHT ? this.rightArm : this.leftArm;
        boolean oldShow = arm.showModel;
        float oldRotationPointX = arm.rotationPointX;
        float oldRotationPointY = arm.rotationPointY;
        float oldRotationPointZ = arm.rotationPointZ;
        float oldRotationX = arm.rotationX;
        float oldRotationY = arm.rotationY;
        float oldRotationZ = arm.rotationZ;
        float oldOffsetX = arm.offsetX;
        float oldOffsetY = arm.offsetY;
        float oldOffsetZ = arm.offsetZ;
        arm.showModel = true;
        arm.rotationPointX = side == EnumHandSide.RIGHT ? -5.0f : 5.0f;
        arm.rotationPointY = 2.0f;
        arm.rotationPointZ = 0.0f;
        arm.rotationX = 0.0f;
        arm.rotationY = 0.0f;
        arm.rotationZ = 0.0f;
        arm.offsetX = 0.0f;
        arm.offsetY = 0.0f;
        arm.offsetZ = 0.0f;
        arm.render(scale);
        arm.showModel = oldShow;
        arm.rotationPointX = oldRotationPointX;
        arm.rotationPointY = oldRotationPointY;
        arm.rotationPointZ = oldRotationPointZ;
        arm.rotationX = oldRotationX;
        arm.rotationY = oldRotationY;
        arm.rotationZ = oldRotationZ;
        arm.offsetX = oldOffsetX;
        arm.offsetY = oldOffsetY;
        arm.offsetZ = oldOffsetZ;
        this.endMobilityRenderState();
    }

    private void beginMobilityRenderState() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        lastCullState = GL11.glIsEnabled((int)2884);
        lastAlphaState = GL11.glIsEnabled((int)3008);
        GL11.glDisable((int)2884);
        GL11.glEnable((int)3008);
        GL11.glAlphaFunc((int)516, (float)0.001f);
    }

    private void endMobilityRenderState() {
        if (lastCullState) {
            GL11.glEnable((int)2884);
        } else {
            GL11.glDisable((int)2884);
        }
        if (lastAlphaState) {
            GL11.glEnable((int)3008);
        } else {
            GL11.glDisable((int)3008);
        }
        GL11.glAlphaFunc((int)516, (float)0.1f);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.animateTentacles(ageInTicks, limbSwing, limbSwingAmount);
    }

    private void animateTentacles(float ageInTicks, float limbSwing, float limbSwingAmount) {
        float move = MathHelper.clamp_float((float)limbSwingAmount, (float)0.0f, (float)1.0f);
        float idleSpeed = ageInTicks * 0.095f;
        float walkSpeed = limbSwing * 0.38f;
        float strength = 0.045f + move * 0.09f;
        float walkStrength = move * 0.075f;
        this.swayChain(this.TENDRIL1, this.TDR1, this.TDR2, idleSpeed, walkSpeed, strength, walkStrength, 0.0f, 1.0f);
        this.swayChain(this.TENDRIL2, this.TDR3, this.TDR4, idleSpeed, walkSpeed, strength, walkStrength, 1.6f, -1.0f);
        this.swayChain(this.TENDRIL3, this.TDR5, this.TDR6, idleSpeed, walkSpeed, strength * 1.25f, walkStrength * 1.15f, 3.1f, -1.0f);
        this.swayChain(this.TENDRIL4, this.TDR7, this.TDR8, idleSpeed, walkSpeed, strength * 1.25f, walkStrength * 1.15f, 4.7f, 1.0f);
    }

    private void swayChain(FloatModelPart base, FloatModelPart mid, FloatModelPart tip, float idleSpeed, float walkSpeed, float strength, float walkStrength, float phase, float side) {
        float idleA = MathHelper.sin((float)(idleSpeed + phase));
        float idleB = MathHelper.sin((float)(idleSpeed * 1.23f + phase + 0.85f));
        float idleC = MathHelper.sin((float)(idleSpeed * 1.47f + phase + 1.7f));
        float walkA = MathHelper.sin((float)(walkSpeed + phase)) * walkStrength;
        base.rotationX = base.baseRotationX + idleB * strength * 0.55f + walkA * 0.45f;
        base.rotationY = base.baseRotationY + side * (idleA * strength * 0.95f + walkA * 0.65f);
        base.rotationZ = base.baseRotationZ + side * idleC * strength * 0.35f;
        mid.rotationX = mid.baseRotationX + idleB * strength * 1.05f + walkA * 0.75f;
        mid.rotationY = mid.baseRotationY + side * (idleC * strength * 1.15f + walkA * 0.85f);
        mid.rotationZ = mid.baseRotationZ + side * idleA * strength * 0.55f;
        tip.rotationX = tip.baseRotationX + idleC * strength * 1.65f + walkA;
        tip.rotationY = tip.baseRotationY + side * (idleB * strength * 1.45f + walkA);
        tip.rotationZ = tip.baseRotationZ + side * idleA * strength * 0.75f;
    }

    private void syncBipedPose() {
        this.copyPose(this.bipedHead, this.head);
        this.copyPose(this.bipedBody, this.body);
        this.copyPose(this.bipedRightArm, this.rightArm);
        this.copyPose(this.bipedLeftArm, this.leftArm);
        this.copyPose(this.bipedRightLeg, this.rightLeg);
        this.copyPose(this.bipedLeftLeg, this.leftLeg);
        this.head.showModel = this.bipedHead.showModel && this.head.showModel;
        this.body.showModel = this.bipedBody.showModel && this.body.showModel;
        this.rightArm.showModel = this.bipedRightArm.showModel && this.rightArm.showModel;
        this.leftArm.showModel = this.bipedLeftArm.showModel && this.leftArm.showModel;
        this.rightLeg.showModel = this.bipedRightLeg.showModel && this.rightLeg.showModel;
        this.leftLeg.showModel = this.bipedLeftLeg.showModel && this.leftLeg.showModel;
    }

    private void copyPose(ModelRenderer vanilla, FloatModelPart custom) {
        custom.rotationPointX = vanilla.rotationPointX;
        custom.rotationPointY = vanilla.rotationPointY;
        custom.rotationPointZ = vanilla.rotationPointZ;
        custom.rotationX = custom.baseRotationX + vanilla.rotateAngleX;
        custom.rotationY = custom.baseRotationY + vanilla.rotateAngleY;
        custom.rotationZ = custom.baseRotationZ + vanilla.rotateAngleZ;
        custom.offsetX = vanilla.offsetX;
        custom.offsetY = vanilla.offsetY;
        custom.offsetZ = vanilla.offsetZ;
    }

    private static final class FloatBox {
        private final int texU;
        private final int texV;
        private final float x1;
        private final float y1;
        private final float z1;
        private final float x2;
        private final float y2;
        private final float z2;
        private final float uvW;
        private final float uvH;
        private final float uvD;
        private final float textureWidth;
        private final float textureHeight;
        private final boolean mirror;

        private FloatBox(int texU, int texV, float x, float y, float z, float width, float height, float depth, float inflate, boolean mirror, int textureWidth, int textureHeight) {
            this.texU = texU;
            this.texV = texV;
            this.uvW = width + inflate * 2.0f;
            this.uvH = height + inflate * 2.0f;
            this.uvD = depth + inflate * 2.0f;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.mirror = mirror;
            float minX = x - inflate;
            float minY = y - inflate;
            float minZ = z - inflate;
            float maxX = x + width + inflate;
            float maxY = y + height + inflate;
            float maxZ = z + depth + inflate;
            if (mirror) {
                float oldMinX = minX;
                minX = maxX;
                maxX = oldMinX;
            }
            this.x1 = minX;
            this.y1 = minY;
            this.z1 = minZ;
            this.x2 = maxX;
            this.y2 = maxY;
            this.z2 = maxZ;
        }

        private void render(float scale) {
            float x1 = this.x1 * scale;
            float y1 = this.y1 * scale;
            float z1 = this.z1 * scale;
            float x2 = this.x2 * scale;
            float y2 = this.y2 * scale;
            float z2 = this.z2 * scale;
            GL11.glBegin((int)7);
            this.drawNorth(x1, y1, z1, x2, y2, z2);
            this.drawSouth(x1, y1, z1, x2, y2, z2);
            this.drawWest(x1, y1, z1, x2, y2, z2);
            this.drawEast(x1, y1, z1, x2, y2, z2);
            this.drawUp(x1, y1, z1, x2, y2, z2);
            this.drawDown(x1, y1, z1, x2, y2, z2);
            GL11.glEnd();
        }

        private void vertex(float x, float y, float z, float u, float v) {
            GL11.glTexCoord2f((float)(u / this.textureWidth), (float)(v / this.textureHeight));
            GL11.glVertex3f((float)x, (float)y, (float)z);
        }

        private void drawNorth(float x1, float y1, float z1, float x2, float y2, float z2) {
            float u1 = (float)this.texU + this.uvD;
            float v1 = (float)this.texV + this.uvD;
            float u2 = u1 + this.uvW;
            float v2 = v1 + this.uvH;
            this.normal(0.0f, 0.0f, -1.0f);
            this.vertex(x2, y1, z1, u1, v1);
            this.vertex(x1, y1, z1, u2, v1);
            this.vertex(x1, y2, z1, u2, v2);
            this.vertex(x2, y2, z1, u1, v2);
        }

        private void drawSouth(float x1, float y1, float z1, float x2, float y2, float z2) {
            float u1 = (float)this.texU + this.uvD + this.uvW + this.uvD;
            float v1 = (float)this.texV + this.uvD;
            float u2 = u1 + this.uvW;
            float v2 = v1 + this.uvH;
            this.normal(0.0f, 0.0f, 1.0f);
            this.vertex(x1, y1, z2, u1, v1);
            this.vertex(x2, y1, z2, u2, v1);
            this.vertex(x2, y2, z2, u2, v2);
            this.vertex(x1, y2, z2, u1, v2);
        }

        private void drawWest(float x1, float y1, float z1, float x2, float y2, float z2) {
            float u1 = this.texU;
            float v1 = (float)this.texV + this.uvD;
            float u2 = u1 + this.uvD;
            float v2 = v1 + this.uvH;
            this.normal(-1.0f, 0.0f, 0.0f);
            this.vertex(x1, y1, z1, u1, v1);
            this.vertex(x1, y1, z2, u2, v1);
            this.vertex(x1, y2, z2, u2, v2);
            this.vertex(x1, y2, z1, u1, v2);
        }

        private void drawEast(float x1, float y1, float z1, float x2, float y2, float z2) {
            float u1 = (float)this.texU + this.uvD + this.uvW;
            float v1 = (float)this.texV + this.uvD;
            float u2 = u1 + this.uvD;
            float v2 = v1 + this.uvH;
            this.normal(1.0f, 0.0f, 0.0f);
            this.vertex(x2, y1, z2, u1, v1);
            this.vertex(x2, y1, z1, u2, v1);
            this.vertex(x2, y2, z1, u2, v2);
            this.vertex(x2, y2, z2, u1, v2);
        }

        private void drawUp(float x1, float y1, float z1, float x2, float y2, float z2) {
            float u1 = (float)this.texU + this.uvD;
            float v1 = this.texV;
            float u2 = u1 + this.uvW;
            float v2 = v1 + this.uvD;
            this.normal(0.0f, -1.0f, 0.0f);
            this.vertex(x1, y1, z2, u1, v1);
            this.vertex(x2, y1, z2, u2, v1);
            this.vertex(x2, y1, z1, u2, v2);
            this.vertex(x1, y1, z1, u1, v2);
        }

        private void drawDown(float x1, float y1, float z1, float x2, float y2, float z2) {
            float u1 = (float)this.texU + this.uvD + this.uvW;
            float v1 = this.texV;
            float u2 = u1 + this.uvW;
            float v2 = v1 + this.uvD;
            this.normal(0.0f, 1.0f, 0.0f);
            this.vertex(x1, y2, z1, u1, v2);
            this.vertex(x2, y2, z1, u2, v2);
            this.vertex(x2, y2, z2, u2, v1);
            this.vertex(x1, y2, z2, u1, v1);
        }

        private void normal(float x, float y, float z) {
            if (this.mirror) {
                GL11.glNormal3f((float)(-x), (float)y, (float)z);
            } else {
                GL11.glNormal3f((float)x, (float)y, (float)z);
            }
        }
    }

    private static final class FloatModelPart {
        private final int textureWidth;
        private final int textureHeight;
        private final String name;
        private final List<FloatBox> boxes = new ArrayList<FloatBox>();
        private final List<FloatModelPart> children = new ArrayList<FloatModelPart>();
        private float rotationPointX;
        private float rotationPointY;
        private float rotationPointZ;
        private float renderScaleX = 1.0f;
        private float renderScaleY = 1.0f;
        private float renderScaleZ = 1.0f;
        private float localOffsetX;
        private float localOffsetY;
        private float localOffsetZ;
        private float baseRotationX;
        private float baseRotationY;
        private float baseRotationZ;
        private float rotationX;
        private float rotationY;
        private float rotationZ;
        private float offsetX;
        private float offsetY;
        private float offsetZ;
        private boolean showModel = true;

        private void setRenderScale(float x, float y, float z) {
            this.renderScaleX = x;
            this.renderScaleY = y;
            this.renderScaleZ = z;
        }

        private FloatModelPart(int textureWidth, int textureHeight, String name) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.name = name;
        }

        private void setRotationPoint(float x, float y, float z) {
            this.rotationPointX = x;
            this.rotationPointY = y;
            this.rotationPointZ = z;
        }

        private void setLocalOffset(float x, float y, float z) {
            this.localOffsetX = x;
            this.localOffsetY = y;
            this.localOffsetZ = z;
        }

        private void setRotationAngle(float x, float y, float z) {
            this.baseRotationX = x;
            this.baseRotationY = y;
            this.baseRotationZ = z;
            this.rotationX = x;
            this.rotationY = y;
            this.rotationZ = z;
        }

        private void addChild(FloatModelPart child) {
            this.children.add(child);
        }

        private void addBox(int texU, int texV, float x, float y, float z, float width, float height, float depth, float inflate, boolean mirror) {
            this.boxes.add(new FloatBox(texU, texV, x, y, z, width, height, depth, inflate, mirror, this.textureWidth, this.textureHeight));
        }

        private void render(float scale) {
            int i;
            if (!this.showModel) {
                return;
            }
            GL11.glPushMatrix();
            if (this.offsetX != 0.0f || this.offsetY != 0.0f || this.offsetZ != 0.0f) {
                GL11.glTranslatef((float)this.offsetX, (float)this.offsetY, (float)this.offsetZ);
            }
            GL11.glTranslatef((float)(this.rotationPointX * scale), (float)(this.rotationPointY * scale), (float)(this.rotationPointZ * scale));
            if (this.rotationZ != 0.0f) {
                GL11.glRotatef((float)(this.rotationZ * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            if (this.rotationY != 0.0f) {
                GL11.glRotatef((float)(this.rotationY * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (this.rotationX != 0.0f) {
                GL11.glRotatef((float)(this.rotationX * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (this.renderScaleX != 1.0f || this.renderScaleY != 1.0f || this.renderScaleZ != 1.0f) {
                GL11.glScalef((float)this.renderScaleX, (float)this.renderScaleY, (float)this.renderScaleZ);
            }
            if (this.localOffsetX != 0.0f || this.localOffsetY != 0.0f || this.localOffsetZ != 0.0f) {
                GL11.glTranslatef((float)(this.localOffsetX * scale), (float)(this.localOffsetY * scale), (float)(this.localOffsetZ * scale));
            }
            for (i = 0; i < this.boxes.size(); ++i) {
                this.boxes.get(i).render(scale);
            }
            for (i = 0; i < this.children.size(); ++i) {
                this.children.get(i).render(scale);
            }
            GL11.glPopMatrix();
        }
    }
}

