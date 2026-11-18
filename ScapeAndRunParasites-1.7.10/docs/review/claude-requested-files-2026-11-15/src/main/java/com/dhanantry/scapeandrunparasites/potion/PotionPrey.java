/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class PotionPrey
extends SRPEffectBase {
    public PotionPrey(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, IconIndexX, IconIndexY);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        EntityPlayer pa;
        if (entity.worldObj.isRemote) {
            return;
        }
        if (entity instanceof EntityPlayer && ((pa = (EntityPlayer)entity).isSpectator() || pa.isCreative())) {
            return;
        }
        if (entity.ticksExisted % 80 == 0) {
            List serverList = EntityQueries.loaded(entity.worldObj);
            int count = 0;
            for (Entity listedEntity : serverList) {
                if (!(listedEntity instanceof EntityParasiticScent)) continue;
                ++count;
            }
            if (count > SRPConfigSystems.scentCap) {
                return;
            }
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0, entity.posY + 1.0, entity.posZ + 1.0), 64.0);
            List moblist = EntityQueries.entities(entity.worldObj, EntityParasiticScent.class, axisalignedbb);
            for (EntityParasiticScent mob : moblist) {
                if (mob.getTargetToKill() != entity || !mob.getCanFollow()) continue;
                return;
            }
            byte phase = SRPSaveData.get(entity.worldObj, 109).getEvolutionPhase(entity.worldObj.provider.dimensionId);
            EntityParasiticScent nut = new EntityParasiticScent(entity.worldObj, 1, entity);
            nut.copyLocationAndAnglesFrom((Entity)entity);
            nut.setScentLife(SRPConfigSystems.scentLifeObserver * 20);
            nut.increaseDanger(ParasiteEventEntity.getScentBonus(phase), true);
            nut.setScentReaction(ParasiteEventEntity.getScentReactionBonus(phase), false);
            nut.setCanFollow(true);
            entity.worldObj.spawnEntityInWorld((Entity)nut);
        }
    }
}

