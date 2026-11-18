/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanPullMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLeer;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectilePullball
extends EntitySRPProjectile {
    private EntityCanPullMobs spider;

    public EntityProjectilePullball(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
    }

    public EntityProjectilePullball(World worldIn, EntityParasiteBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, (EntityLivingBase)shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
        this.spider = (EntityCanPullMobs)((Object)shooter);
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote) {
            if (result.entityHit != null) {
                if (result.entityHit instanceof EntityLivingBase) {
                    // empty if block
                }
            } else {
                this.setWebsAround();
            }
            this.setDead();
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            if (this.ticksExisted == 5) {
                this.accelerationX *= (double)this.spider.getAcceleration();
                this.accelerationZ *= (double)this.spider.getAcceleration();
            }
            if (this.shootingEntity == null) {
                this.setDead();
                return;
            }
            if (this.spider.hasTargetedEntity() && !(this.shootingEntity instanceof EntityLeer)) {
                this.setDead();
                return;
            }
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 2.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob instanceof EntityParasiteBase || !this.shootingEntity.canEntityBeSeen((Entity)mob) || !this.spider.checkAttackTarget(mob) || !mob.isEntityAlive()) continue;
                this.spider.setPStatus(3);
                this.spider.setPullingMobEffects(mob);
                this.spider.setTargetedEntity(mob.getEntityId());
                this.spider.resetPullSkill();
                this.setDead();
            }
        }
    }

    private void setWebsAround() {
        int totalWebs = this.rand.nextInt(3) + 1;
        int[] positionss = new int[]{-1, 0, 1};
        for (int i = 1; i <= totalWebs; ++i) {
            int poz;
            int poy;
            int pox = positionss[this.rand.nextInt(3)];
            if (BlockStates.get(this.worldObj, new BlockPos(this.posX + (double)pox, this.posY + (double)(poy = positionss[this.rand.nextInt(3)]), this.posZ + (double)(poz = positionss[this.rand.nextInt(3)]))).getBlock() != Blocks.air) continue;
            BlockStates.set(this.worldObj, new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), BlockStates.defaultState(SRPBlocks.SRPWeb));
        }
    }
}

