/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityLeemB
extends EntityPStationary {
    public EntityLeemB(World worldIn) {
        super(worldIn);
        this.setSize(1.4f, 1.4f);
        this.experienceValue = 0;
        this.type = (byte)40;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.valueEvDeath = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 314;
    }

    protected void initEntityAI() {
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.srpTicks == 5) {
            this.removeActivePotionEffect(SRPPotions.PIVOT_E);
        }
    }

    @Override
    public int hasResistance(String damage, byte type) {
        return 0;
    }

    public float getEyeHeight() {
        return 1.8f;
    }
}

