/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  org.apache.logging.log4j.Level
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.Objects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import org.apache.logging.log4j.Level;

public class PotionNeedler
extends SRPEffectBase {
    public PotionNeedler(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, IconIndexX, IconIndexY);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.worldObj.isRemote) {
            return;
        }
        this.effectNeedler(entity, amplifier);
    }

    private void effectNeedler(EntityLivingBase entity, int amplifier) {
        if (amplifier >= SRPConfigSystems.needlerTerminal) {
            String mobName;
            amplifier -= SRPConfigSystems.needlerTerminal;
            entity.removeActivePotionEffect(SRPPotions.DLER_E);
            try {
                mobName = EntityList.getKey((Entity)entity).toString();
            }
            catch (Exception e) {
                SRPMain.logger.log(Level.ERROR, "Problem with needler and an entity", (Throwable)e);
                return;
            }
            if (ParasiteEventEntity.checkName(mobName, SRPConfigSystems.needlerImmuneList, SRPConfigSystems.needlerImmuneListWhite)) {
                return;
            }
            entity.addPotionEffect(new PotionEffect(SRPPotions.DLER_E, 400, amplifier, false, false));
            float f1 = entity.getHealth();
            if (f1 <= 0.0f) {
                return;
            }
            float damageE = entity.getMaxHealth() * SRPConfigSystems.needlerDamage;
            damageE = entity instanceof EntityPlayer ? Math.min(damageE, SRPConfigSystems.needlerMaxDamPlayer) : Math.min(damageE, SRPConfigSystems.needlerMaxDamMonster);
            entity.setHealth(f1 - damageE);
            entity.worldObj.setEntityState((Entity)entity, (byte)2);
            entity.worldObj.createExplosion((Entity)entity, entity.posX, entity.posY, entity.posZ, 0.0f, false);
            if (entity.getHealth() <= 0.0f) {
                ItemStack itemstack = null;
                for (EnumHand enumhand : EnumHand.values()) {
                    ItemStack itemstack1 = entity.getHeldItem(enumhand);
                    if (itemstack1.getItem() != Items.TOTEM_OF_UNDYING) continue;
                    itemstack = itemstack1.copy();
                    itemstack1.shrink(1);
                    break;
                }
                if (itemstack != null) {
                    if (entity instanceof EntityPlayerMP) {
                        EntityPlayerMP entityplayermp = (EntityPlayerMP)entity;
                        entityplayermp.triggerAchievement(Objects.requireNonNull(StatList.getObjectUseStats((Item)Items.TOTEM_OF_UNDYING)));
                        CriteriaTriggers.USED_TOTEM.trigger(entityplayermp, itemstack);
                    }
                    entity.setHealth(1.0f);
                    entity.clearActivePotions();
                    entity.addPotionEffect(new PotionEffect(MobEffects.regeneration, 900, 1));
                    entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
                    entity.worldObj.setEntityState((Entity)entity, (byte)35);
                } else {
                    entity.onDeath(DamageSource.magic);
                }
            }
        }
    }
}

