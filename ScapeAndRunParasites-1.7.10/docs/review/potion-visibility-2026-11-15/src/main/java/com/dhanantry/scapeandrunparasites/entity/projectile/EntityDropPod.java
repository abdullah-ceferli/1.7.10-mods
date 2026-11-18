/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAreaEffectCloud
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityDropPod
extends EntityParasiteBase {
    private byte owner;

    public EntityDropPod(World worldIn) {
        super(worldIn);
        this.setSize(1.0f, 2.0f);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.fuseTime = 80;
        this.type = (byte)61;
        this.setParasiteStatus(1);
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 34;
    }

    public EntityDropPod(World worldIn, int points) {
        this(worldIn);
    }

    public void applyEntityCollision(Entity entityIn) {
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((SRPAttributes.ORONCO_HEALTH + SRPAttributes.TERLA_HEALTH) * 0.1);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(2.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.onGround) {
            this.setParasiteStatus(0);
            this.setSelfeState(1);
            this.dyingBurst(false, 2);
        } else {
            this.particleStatus((byte)12);
        }
    }

    @Override
    protected void selfExplode() {
        boolean flag = ForgeEventFactory.getMobGriefingEvent((World)this.worldObj, (Entity)this) && SRPConfigMobs.ratholGriefing;
        ParasiteEventEntity.createExplosion(this.worldObj, (Entity)this, this.posX, this.posY, this.posZ, 4.0f, flag);
        if (!this.worldObj.isRemote) {
            this.playSound(SRPSounds.RATHOL_BOOM, 1.0f, 1.0f);
            this.dead = true;
            String[] here = new String[3];
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 7.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (String i : SRPConfigMobs.pod1Effects) {
                here = i.split(";");
                Potion potionE = Potion.getPotionFromResourceLocation((String)here[2]);
                if (potionE == null) continue;
                int duration = Integer.parseInt(here[0]) * 20;
                int amp = Integer.parseInt(here[1]);
                for (EntityLivingBase mob : moblist) {
                    if (mob == this || mob instanceof EntityParasiteBase) continue;
                    mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(potionE, duration, amp, false, false));
                }
            }
            this.setDead();
            this.spawnLingeringCloud();
            switch (this.owner) {
                case 62: {
                    int limit = 0;
                    int cap = 0;
                    while (limit < SRPConfigMobs.oroncoMaxMobPod && cap < 5) {
                        if (ParasiteSummon.SummonM(this, SRPConfigMobs.oroncoMobList, 1, this.posX, this.posY, this.posZ, this.getAttackTarget(), false)) {
                            ++limit;
                            continue;
                        }
                        ++cap;
                    }
                    break;
                }
                case 63: {
                    ParasiteSummon.spawnM(this, SRPConfigMobs.oroncoMobList, 0, false, this.getCustomNameTag());
                    break;
                }
            }
        }
    }

    private void spawnLingeringCloud() {
        EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.worldObj, this.posX, this.posY, this.posZ);
        entityareaeffectcloud.setRadius(this.width * 2.0f);
        entityareaeffectcloud.setWaitTime(5);
        entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration());
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.poison, 300, 0));
        entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
        this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
    }

    public void setOwner(byte in) {
        this.owner = in;
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 2.1f;
    }
}

