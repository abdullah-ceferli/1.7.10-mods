/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SRPEffectBase
extends com.dhanantry.scapeandrunparasites.backport.potion.NativePotionBase {
    private ResourceLocation icon;
    private static final boolean SHOW_RAGE_PARTICLES_SELF = true;
    private static final boolean SHOW_BLOOD_PARTICLES_SELF = true;

    public SRPEffectBase(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn);
        this.setPotionName("mob_effect." + this.getRegistryName());
        this.setIconIndex(IconIndexX, IconIndexY);
        this.icon = new ResourceLocation("srparasites:textures/gui/potion_" + name + ".png");
    }

    public boolean hasStatusIcon() {
        return false;
    }

    public List<ItemStack> getCurativeItems() {
        return new ArrayList<ItemStack>();
    }

    public boolean isReady(int duration, int amplifier) {
        if (this == SRPPotions.BLEED_E || this == SRPPotions.EPEL_E || this == SRPPotions.DLER_E || this == SRPPotions.CORRO_E || this == SRPPotions.FOSTER_E || this == SRPPotions.CONTA_E) {
            int j = 25 >> amplifier;
            if (j > 0) {
                return duration % j == 0;
            }
            return true;
        }
        return true;
    }

    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (!entity.worldObj.isRemote) {
            int i;
            if (this == SRPPotions.RAGE_E) {
                for (i = 0; i <= 3; ++i) {
                    if (!(entity.worldObj.rand.nextFloat() < 0.1f)) continue;
                    this.sendParticle(entity, (byte)12, true);
                }
                if (entity.worldObj.rand.nextFloat() < 0.8f) {
                    this.sendParticle(entity, (byte)2, true);
                }
            }
            if (this == SRPPotions.BLEED_E) {
                for (i = 0; i <= 3; ++i) {
                    if (!(entity.worldObj.rand.nextFloat() < 0.8f)) continue;
                    this.sendParticle(entity, (byte)13, true);
                }
            }
            if (this == SRPPotions.INDEAF_E) {
                entity.motionX = 0.0;
                entity.motionZ = 0.0;
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)entity;
                    player.moveForward = 0.0f;
                    player.moveStrafing = 0.0f;
                }
            } else if (this == SRPPotions.EFFECTPOS_E) {
                this.effectEffectPos(entity, amplifier);
            } else if (this == SRPPotions.EFFECTNEG_E) {
                this.effectEffectNeg(entity, amplifier);
            }
        }
    }

    private void sendParticle(EntityLivingBase entity, byte type, boolean showForSelf) {
        SRPPacketParticle packet = new SRPPacketParticle(entity.posX, entity.posY, entity.posZ, entity.width, entity.height, type);
        SRPMain.network.sendToAllTracking((IMessage)packet, (Entity)entity);
        if (showForSelf && entity instanceof EntityPlayerMP) {
            SRPMain.network.sendTo((IMessage)packet, (EntityPlayerMP)entity);
        }
    }

    protected void InfectNearby(EntityLivingBase entity, int range) {
        if (SRPConfigSystems.cothAura <= 0) {
            return;
        }
        if (entity.isPotionActive(SRPPotions.EPEL_E)) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.capabilities.isCreativeMode) {
                return;
            }
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0, entity.posY + 1.0, entity.posZ + 1.0), (double)range);
        List moblist = EntityQueries.entities(entity.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (!entity.canEntityBeSeen((Entity)mob) || mob == entity || mob.isPotionActive(SRPPotions.COTH_E) || mob.isPotionActive(SRPPotions.EPEL_E)) continue;
            mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 4800, 0, false, false));
        }
    }

    protected BlockPos getRandomPosInCircle(Random rand, BlockPos center, double radius) {
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        double r = Math.sqrt(rand.nextDouble()) * radius;
        int dx = (int)Math.floor(Math.cos(theta) * r);
        int dz = (int)Math.floor(Math.sin(theta) * r);
        return center.add(dx, 0, dz);
    }

    protected void effectEffectPos(EntityLivingBase entity, int amplifier) {
        if (entity.ticksExisted % 20 != 0) {
            return;
        }
        for (PotionEffect pe : entity.getActivePotionEffects()) {
            Potion p = pe.getPotion();
            if (p.isBadEffect()) continue;
            entity.attackEntityFrom(DamageSource.wither, 0.5f * (float)(pe.getAmplifier() + 1));
        }
    }

    protected void effectEffectNeg(EntityLivingBase entity, int amplifier) {
        if (entity.ticksExisted % 20 != 0) {
            return;
        }
        for (PotionEffect pe : entity.getActivePotionEffects()) {
            Potion p = pe.getPotion();
            if (!p.isBadEffect()) continue;
            SRPPotions.applyStackPotion(p, entity, 20, amplifier);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        if (mc.currentScreen != null) {
            mc.getTextureManager().bindTexture(this.icon);
            Gui.drawModalRectWithCustomSizedTexture((int)(x + 6), (int)(y + 7), (float)0.0f, (float)0.0f, (int)18, (int)18, (float)18.0f, (float)18.0f);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        mc.getTextureManager().bindTexture(this.icon);
        Gui.drawModalRectWithCustomSizedTexture((int)(x + 3), (int)(y + 3), (float)0.0f, (float)0.0f, (int)18, (int)18, (float)18.0f, (float)18.0f);
    }
}

