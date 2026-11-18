/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

public class EntityAIEvadeDash
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int eCooldown;
    private int cooldown;
    private int eDuration;
    private int tracker;
    private boolean inC;
    private int blockDistance;
    private double dash;
    private int maxDis;

    public EntityAIEvadeDash(EntityParasiteBase in, int cooldown, int duration, int distance) {
        this.parent = in;
        this.tracker = 0;
        this.eCooldown = cooldown;
        this.cooldown = cooldown + 1;
        this.eDuration = duration;
        this.blockDistance = distance * distance;
        this.inC = false;
        this.dash = 3.0;
        this.maxDis = 225;
    }

    public EntityAIEvadeDash(EntityParasiteBase in, int cooldown, int duration, int distance, double dash, int maxD) {
        this(in, cooldown, duration, distance);
        this.dash = dash;
        this.maxDis = maxD * maxD;
    }

    public boolean shouldExecute() {
        return (this.parent.getAttackTarget() != null || this.inC) && this.parent.getParasiteStatus() > 0 && this.parent.getParasiteStatus() < 3 && this.parent.onGround;
    }

    public void resetTask() {
        this.cooldown = 0;
        this.tracker = 0;
        this.inC = false;
        this.parent.moveStrafing = 0.0f;
    }

    public void updateTask() {
        EntityLivingBase target = this.parent.getAttackTarget();
        if (target != null || this.inC) {
            if (this.inC) {
                ++this.tracker;
                if (this.tracker >= this.eDuration) {
                    this.parent.moveStrafing = 0.0f;
                    this.tracker = 0;
                    this.cooldown = 0;
                    this.inC = false;
                }
            } else {
                if (this.parent.getDistanceSqToEntity((Entity)target) > (double)this.blockDistance && this.parent.canEntityBeSeen((Entity)target) && this.parent.getDistanceSqToEntity((Entity)target) < (double)this.maxDis && this.cooldown < this.eCooldown) {
                    ++this.cooldown;
                }
                if (this.cooldown >= this.eCooldown) {
                    this.parent.particleStatus((byte)10);
                    double dd0 = target.posX - this.parent.posX;
                    double dd1 = target.posZ - this.parent.posZ;
                    float f = MathHelper.sqrt_double((double)(dd0 * dd0 + dd1 * dd1));
                    double i = 0.0;
                    double j = 0.0;
                    if (this.parent.worldObj.rand.nextBoolean()) {
                        i = this.dash;
                    } else {
                        j = this.dash;
                    }
                    this.parent.motionX += dd0 / (double)f * this.dash * (double)0.8f + this.parent.motionX * (double)0.2f + i;
                    this.parent.motionZ += dd1 / (double)f * this.dash * (double)0.8f + this.parent.motionZ * (double)0.2f + j;
                    this.parent.getNavigator().clearPathEntity();
                    this.resetTask();
                }
            }
        }
    }
}

