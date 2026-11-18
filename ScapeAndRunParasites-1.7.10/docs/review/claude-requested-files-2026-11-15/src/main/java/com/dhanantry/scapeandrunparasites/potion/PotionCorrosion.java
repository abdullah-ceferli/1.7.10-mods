/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class PotionCorrosion
extends SRPEffectBase {
    public PotionCorrosion(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, IconIndexX, IconIndexY);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.worldObj.isRemote) {
            return;
        }
        this.effectCorrosive(entity, amplifier);
    }

    private void effectCorrosive(EntityLivingBase entity, int amplifier) {
        ArrayList<ItemStack> off = new ArrayList<ItemStack>();
        Iterable gear = entity.getArmorInventoryList();
        if (!gear.equals(Collections.emptyList())) {
            for (ItemStack part : gear) {
                if (part.isEmpty() || !part.isItemStackDamageable()) continue;
                off.add(part);
            }
            if (!off.isEmpty()) {
                for (ItemStack part : off) {
                    if (!((double)part.getMaxDamage() * SRPConfigSystems.corrNot < (double)(part.getMaxDamage() - part.getItemDamageForDisplay()))) continue;
                    part.damageItem(SRPConfigSystems.corroValue, entity);
                }
            }
        }
    }
}

