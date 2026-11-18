/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityOroncoTen
extends EntityParasiteBase {
    private int ticksGround;
    private int maxMobs;

    public EntityOroncoTen(World worldIn) {
        super(worldIn);
        this.setSize(1.0f, 0.7f);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.ignoreFrustumCheck = true;
        this.canD = SRPConfig.ancientdespawn;
        this.killcount = -10.0;
        this.type = (byte)62;
    }

    @Override
    public int getParasiteIDRegister() {
        return 35;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.ORONCO_HEALTH * 0.25);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ORONCO_ARMOR * 0.25);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(2.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.onGround) {
            ++this.ticksGround;
            if (this.ticksGround > 200 && !this.worldObj.isRemote) {
                if (this.ticksGround % 20 == 0 && this.nearbydangerous() && this.maxMobs < 10) {
                    if (ParasiteSummon.SummonM((EntityLivingBase)this, new String[]{"srparasites:lodo;1;1"}, 2, 3, this.getAttackTarget())) {
                        ++this.maxMobs;
                    } else if (this.maxMobs >= 10) {
                        this.setDead();
                    }
                }
            } else if (this.worldObj.isRemote && this.ticksGround > 200) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 91, 81, 75);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 164, 174, 180);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 91, 81, 75);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 164, 174, 180);
            }
        }
    }

    private boolean nearbydangerous() {
        int k = 0;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 16.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob != this && mob instanceof EntityParasiteBase && mob.isEntityAlive()) {
                --k;
                continue;
            }
            if (!mob.isEntityAlive()) continue;
            ++k;
        }
        return k > 0;
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 0.5f;
    }
}

