/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.ItemInfestedBonemeal;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PotionCOTH
extends SRPEffectBase {
    public PotionCOTH(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
        super(name, isBadEffectIn, liquidColorIn, IconIndexX, IconIndexY);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        boolean tickFlag;
        if (entity.worldObj.isRemote) {
            return;
        }
        boolean flagPr = entity instanceof EntityPlayer;
        if (!SRPConfigSystems.cothPlayer && flagPr) {
            return;
        }
        NBTTagCompound tags = entity.getEntityData();
        if (tags.hasKey("srpcothimmunity") && tags.getInteger("srpcothimmunity") == 0) {
            entity.removeActivePotionEffect(SRPPotions.COTH_E);
            return;
        }
        boolean flag = !entity.worldObj.isRemote;
        boolean particle = false;
        int dur = entity.getActivePotionEffect(SRPPotions.COTH_E).getDuration();
        if (SRPConfigSystems.disloCOTHIgnoreAmp && amplifier <= 1 && entity.ticksExisted % 20 == 0 && SRPSaveData.get(entity.worldObj, 106).getCurrentCode(entity.worldObj.provider.dimensionId, 0) > 0) {
            entity.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 6666, 10));
            if (tags.hasKey("srpcothimmunity") && tags.getInteger("srpcothimmunity") != 0) {
                ParasiteEventEntity.convertEntity(entity, tags, false, SRPConfigSystems.COTHVictimParasite);
            }
        }
        if (SRPConfigSystems.COTHPopping && amplifier >= 2) {
            EntityPlayer player;
            if (entity instanceof EntityPlayer && !((player = (EntityPlayer)entity).getHealth() > 0.0f)) {
                BlockPos pos = new BlockPos(player.posX, player.posY - 1.0, player.posZ);
                if (WorldAccess.isAirBlock(player.worldObj, pos)) {
                    return;
                }
                ItemInfestedBonemeal.boneMealEffect(player.worldObj, pos, EnumFacing.DOWN);
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(player.worldObj, null, pos, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 1.0f, 1.0f);
                player.removeActivePotionEffect(SRPPotions.COTH_E);
            }
            return;
        }
        boolean bl = tickFlag = entity.ticksExisted % 20 == 0;
        if (amplifier == 0) {
            if (flag) {
                this.effectCOTHextendDuration(dur, 200, entity, amplifier, flagPr, true, true);
            }
        } else if (flag) {
            if (tickFlag) {
                this.InfectNearby(entity, SRPConfigSystems.cothAura);
                this.effectCOTHextendDuration(dur, 200, entity, amplifier, flagPr, true, true);
            }
            particle = this.effectCOTHTransform(entity, dur, tickFlag, flagPr, amplifier == 1);
        }
        if (particle) {
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(entity.posX, entity.posY, entity.posZ, entity.width, entity.height, 1));
        }
    }

    private void effectCOTHextendDuration(int duration, int durationCheck, EntityLivingBase entity, int amplifier, boolean flagPr, boolean origin, boolean evoPoints) {
        if (duration < durationCheck) {
            int cothDur = 3600;
            amplifier = Math.min(amplifier + 1, 2);
            NBTTagCompound tags = entity.getEntityData();
            if (flagPr) {
                entity.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, cothDur, amplifier, false, false));
            } else if (tags.hasKey("srpcothimmunity")) {
                if (tags.getInteger("srpcothimmunity") != 0) {
                    entity.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, cothDur, amplifier, false, false));
                    if (SRPConfigWorld.originActivated && origin) {
                        ParasiteEventWorld.setOriginInHealth(entity.worldObj, EntityQueries.position(entity), (int)((double)entity.getMaxHealth() * SRPConfigWorld.originCOTHMultiplier), true);
                    }
                    if (SRPConfigSystems.useEvolution) {
                        SRPSaveData data = SRPSaveData.get(entity.worldObj, 107);
                        if (evoPoints) {
                            data.setTotalKills(entity.worldObj.provider.dimensionId, SRPConfigSystems.valueCOTH, true, entity.worldObj, true, 52);
                        }
                        if (data.getEvolutionPhase(entity.worldObj.provider.dimensionId) >= SRPConfigSystems.evolutionAssimilatedDehiding) {
                            int key = tags.getInteger("srpcothimmunity");
                            tags.setInteger("srpcothimmunity", ++key);
                        }
                    }
                } else {
                    entity.removeActivePotionEffect(SRPPotions.COTH_E);
                }
            } else {
                entity.removeActivePotionEffect(SRPPotions.COTH_E);
            }
        }
    }

    private boolean effectCOTHTransform(EntityLivingBase entity, int duration, boolean tickFlag, boolean flagPr, boolean insider) {
        if (flagPr) {
            return false;
        }
        boolean flagReturn = false;
        NBTTagCompound tags = entity.getEntityData();
        if (tags.hasKey("srpcothimmunity")) {
            int key = tags.getInteger("srpcothimmunity");
            if (entity.getMaxHealth() * SRPConfigSystems.cothUnhide > entity.getHealth() && entity.getHealth() > 0.0f || key > 1) {
                flagReturn = true;
                if (insider && tickFlag) {
                    ParasiteEventEntity.spawnInsider(entity, entity.worldObj, tags);
                } else if (tickFlag) {
                    ParasiteEventEntity.convertEntity(entity, tags, false, SRPConfigSystems.COTHVictimParasite);
                }
            }
        } else {
            entity.removeActivePotionEffect(SRPPotions.COTH_E);
        }
        return flagReturn;
    }
}

