/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.MobEffects
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import net.minecraft.util.MathHelper;

public class EntityAIEvade
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int eCooldown;
    private int cooldown;
    private int eDuration;
    private int tracker;
    private boolean inC;
    private double blockDistance;
    private boolean useDis;
    private int disValue;
    private boolean ignoreGene;

    public EntityAIEvade(EntityParasiteBase in, int cooldown, int duration, double distance) {
        this.parent = in;
        this.tracker = 0;
        this.eCooldown = cooldown;
        this.cooldown = cooldown + 1;
        this.eDuration = duration;
        this.blockDistance = distance * distance;
        this.inC = false;
    }

    public EntityAIEvade(EntityParasiteBase in, int cooldown, int duration, double distance, boolean use, int diss, boolean ignoreGen) {
        this(in, cooldown, duration, distance);
        this.useDis = use;
        this.disValue = diss;
        this.ignoreGene = ignoreGen;
    }

    public boolean shouldExecute() {
        if (this.ignoreGene) {
            return (this.parent.getAttackTarget() != null || this.inC) && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround || this.inC;
        }
        return this.parent.getAttackTarget() != null && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround && this.parent.getGeneMod(5) || this.inC;
    }

    public void resetTask() {
        this.cooldown = 0;
        this.tracker = 0;
        this.inC = false;
        this.parent.moveStrafing = 0.0f;
    }

    public void updateTask() {
        EntityLivingBase target = this.parent.getAttackTarget();
        if ((target != null || this.inC) && !this.parent.isPotionActive(net.minecraft.potion.Potion.moveSlowdown)) {
            if (this.inC && !this.useDis) {
                ++this.tracker;
                if (this.tracker >= this.eDuration) {
                    this.parent.moveStrafing = 0.0f;
                    this.tracker = 0;
                    this.cooldown = 0;
                    this.inC = false;
                }
            } else {
                boolean flag;
                boolean bl = this.useDis ? this.parent.getDistanceSqToEntity((Entity)target) > (double)(this.disValue * this.disValue) : (flag = this.parent.getDistanceSqToEntity((Entity)target) < 225.0);
                if (this.parent.getDistanceSqToEntity((Entity)target) > this.blockDistance && this.parent.canEntityBeSeen((Entity)target) && flag) {
                    ++this.cooldown;
                }
                if (this.cooldown >= this.eCooldown) {
                    this.parent.lookAt((Entity)target);
                    if (this.useDis) {
                        this.jumpTowardsTarget((Entity)target);
                        this.parent.getNavigator().clearPathEntity();
                        return;
                    }
                    if (this.parent.moveStrafing == 0.0f) {
                        int i = this.parent.getRNG().nextInt(2) == 0 ? 1 : -1;
                        this.parent.moveStrafing = i;
                        this.inC = true;
                        double dd0 = target.posX - this.parent.posX;
                        double dd1 = target.posZ - this.parent.posZ;
                        float f = MathHelper.sqrt_double((double)(dd0 * dd0 + dd1 * dd1));
                        this.parent.motionX += dd0 / (double)f * 1.77 * (double)0.8f + this.parent.motionX * (double)0.2f;
                        this.parent.motionZ += dd1 / (double)f * 1.77 * (double)0.8f + this.parent.motionZ * (double)0.2f;
                        this.parent.motionY = 0.2 + (double)this.parent.height * 0.1;
                        this.parent.getNavigator().clearPathEntity();
                    }
                }
            }
        }
    }

    private void jumpTowardsTarget(Entity target) {
        double dx = target.posX - this.parent.posX;
        double dz = target.posZ - this.parent.posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < (double)this.eDuration) {
            return;
        }
        double angle = Math.toRadians(45.0);
        double gravity = 0.8;
        double velocity = Math.sqrt(distance * gravity / Math.sin(2.0 * angle));
        velocity = Math.min(velocity, this.blockDistance);
        double horizontalVelocity = velocity * 1.9 * Math.cos(angle);
        double verticalVelocity = velocity * 0.6 * Math.sin(angle);
        double norm = distance;
        this.parent.motionX = horizontalVelocity * (dx / norm);
        this.parent.motionZ = horizontalVelocity * (dz / norm);
        this.parent.motionY = verticalVelocity;
    }
}

