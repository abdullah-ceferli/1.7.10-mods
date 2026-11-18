/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIEvadeTP
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int eCooldown;
    private int cooldown;
    private int blockDistance;
    private int dash;
    private int currentDash;
    private int maxDis;
    private double targetIniDis;
    private double targetIniDisSegment;

    public EntityAIEvadeTP(EntityParasiteBase in, int cooldown, int distance) {
        this.parent = in;
        this.eCooldown = cooldown;
        this.cooldown = cooldown + 1;
        this.blockDistance = distance * distance;
        this.dash = 3;
        this.maxDis = 225;
    }

    public EntityAIEvadeTP(EntityParasiteBase in, int cooldown, int distance, int jumps, int maxD) {
        this(in, cooldown, distance);
        this.dash = jumps;
        this.maxDis = maxD * maxD;
    }

    public boolean shouldExecute() {
        return this.parent.getAttackTarget() != null && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround;
    }

    public void resetTask() {
        this.cooldown = 0;
        this.parent.moveStrafing = 0.0f;
    }

    public void updateTask() {
        EntityLivingBase target = this.parent.getAttackTarget();
        if (target != null) {
            if (this.parent.getDistanceSqToEntity((Entity)target) > (double)this.blockDistance && this.parent.canEntityBeSeen((Entity)target) && this.parent.getDistanceSqToEntity((Entity)target) < (double)this.maxDis && this.cooldown < this.eCooldown) {
                ++this.cooldown;
                if (this.cooldown >= this.eCooldown) {
                    this.targetIniDis = this.parent.getDistanceSqToEntity((Entity)target);
                    this.targetIniDisSegment = this.targetIniDis / (double)(this.dash + 1);
                }
            }
            if (this.cooldown >= this.eCooldown) {
                this.parent.particleStatus((byte)10);
                Random rand = new Random();
                double x = target.posX;
                double y = target.posY;
                double z = target.posZ;
                this.parent.getNavigator().clearPathEntity();
                this.resetTask();
            }
        }
    }

    protected boolean teleportRandomly() {
        double d0 = this.parent.posX + (this.parent.worldObj.rand.nextDouble() - 0.5) * 64.0;
        double d1 = this.parent.posY + (double)(this.parent.worldObj.rand.nextInt(64) - 32);
        double d2 = this.parent.posZ + (this.parent.worldObj.rand.nextDouble() - 0.5) * 64.0;
        if (this.parent.getAttackTarget() != null && this.parent.getAttackTarget().getDistance(d0, d1, d2) < 10.0) {
            return false;
        }
        return this.teleportTo(d0, d1, d2);
    }

    private boolean teleportTo(double x, double y, double z) {
        boolean flag = this.parent.attemptTeleport(x, y, z);
        if (flag) {
            this.parent.worldObj.playSound((EntityPlayer)null, this.parent.prevPosX, this.parent.prevPosY, this.parent.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.parent.getSoundCategory(), 1.0f, 1.0f);
            this.parent.playSound(SRPSounds.INFECTEDENDERMAN_PORTAL, 1.0f, 1.0f);
        }
        return flag;
    }
}

