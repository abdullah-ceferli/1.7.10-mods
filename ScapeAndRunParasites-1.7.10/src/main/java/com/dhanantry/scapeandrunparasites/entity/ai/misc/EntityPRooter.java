/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityLeemB;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNexusProtection3;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public abstract class EntityPRooter
extends EntityPStationaryArchitect {
    protected int leemRange;
    protected int leemRangeEffect;
    protected int leemBalls;
    protected int leemCooldown;
    protected int leemCooldownReset;
    private int blockR;
    private static final DataParameter<Boolean> RTTS = EntityDataManager.createKey(EntityPRooter.class, (DataSerializer)DataSerializers.BOOLEAN);
    protected int rangeB = 0;

    public EntityPRooter(World worldIn) {
        super(worldIn);
        this.setScentHPMultiplier(1.0f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(RTTS, (Object)false);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.srpTicks == 5) {
                --this.leemCooldown;
                ++this.blockR;
                if (this.blockR >= 7) {
                    this.checkRTTS();
                    this.blockR = 0;
                }
            }
            if (this.srpTicks == 10 && this.leemCooldown <= 0) {
                this.leemCooldown = this.leemCooldownReset;
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), (double)this.leemRangeEffect, (double)this.leemRangeEffect, (double)this.leemRangeEffect);
                List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this || !mob.isEntityAlive() || mob instanceof EntityPRooter || mob.getParasiteIDRegister() == 314) continue;
                    mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.PIVOT_E, 300, this.stage - 1, false, false));
                    mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.PARATE_E, 300, this.stage - 1, false, false));
                    mob.SetRooter(this);
                    mob.applyGene(new boolean[]{true, true, true, true, true, true, true, true, true, true}, new float[]{2.5f, 3.0f, 0.5f});
                }
            }
        }
    }

    @Override
    public void addPotionEffect(PotionEffect potioneffectIn) {
        if (potioneffectIn.getPotion() == SRPPotions.PIVOT_E) {
            return;
        }
        super.addPotionEffect(potioneffectIn);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (this.worldObj.isRemote) {
            return false;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), (double)(this.leemRange + 1), (double)this.leemRange, (double)(this.leemRange + 1));
        List moblist = EntityQueries.entities(this.worldObj, EntityLeemB.class, axisalignedbb);
        if (moblist.size() > 0) {
            float part = amount / (float)moblist.size();
            for (EntityLeemB mob : moblist) {
                mob.attackEntityFrom(source, part);
            }
            return super.attackEntityFrom(source, 0.0f);
        }
        this.spawnLeemB(this.leemBalls);
        return super.attackEntityFrom(source, amount);
    }

    protected void spawnLeemB(int in) {
        int cap = this.rand.nextInt(this.leemBalls) + 1;
        for (int i = 0; i < cap; ++i) {
            ParasiteSummon.SummonM((EntityLivingBase)this, new String[]{"srparasites:rooterball;1;1"}, 2, 5, null);
        }
    }

    public void checkRTTS() {
        if (this.rangeB == 0) {
            return;
        }
        double l1 = this.posX;
        double i2 = this.posZ;
        for (int k2 = -1 * this.rangeB; k2 <= 1 * this.rangeB; ++k2) {
            for (int l2 = -1 * this.rangeB; l2 <= 1 * this.rangeB; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                IBlockState iblockstate = BlockStates.get(this.worldObj, new BlockPos(i3, this.posY - 0.5, l));
                Block block = iblockstate.getBlock();
                if (block != Blocks.air) continue;
                this.setRTTS(false);
                return;
            }
        }
        this.setRTTS(true);
    }

    @Override
    public void generateStructure() {
        if (SRPConfig.nexusStructures) {
            WorldGenParasiteNexusProtection3 p1 = new WorldGenParasiteNexusProtection3(false, 1);
            p1.generate(this.worldObj, new Random(), EntityQueries.position(this));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    public boolean getRTTS() {
        return (Boolean)EntityDataManager.of(this).get(RTTS);
    }

    public void setRTTS(boolean in) {
        EntityDataManager.of(this).set(RTTS, (Object)in);
    }
}

