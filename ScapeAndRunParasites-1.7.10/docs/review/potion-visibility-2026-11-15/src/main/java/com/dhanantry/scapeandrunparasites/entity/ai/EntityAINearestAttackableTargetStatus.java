/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityOwnable
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.common.ForgeHooks
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeHooks;

public class EntityAINearestAttackableTargetStatus<T extends EntityLivingBase>
extends EntityAIBase {
    protected Class<T> targetClass;
    protected final EntityParasiteBase taskOwner;
    private int targetChance;
    protected Sorter sorter;
    protected Predicate<? super T> targetEntitySelector;
    protected T targetEntity;
    protected double sneakPPP;
    protected float inviPPP;
    private int targetUnseenTicks;
    private boolean nearbyOnly;
    private int targetSearchStatus;
    private int targetSearchDelay;
    protected int unseenMemoryTicks = 60;
    protected boolean shouldCheckSight;

    public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, boolean checkSight) {
        this(creature, classTarget, checkSight, false);
    }

    public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
        this(creature, classTarget, 10, checkSight, onlyNearby, null, 0.8f, 0.7f);
    }

    public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final @Nullable Predicate<? super T> targetSelector, double sneakP, float inviP) {
        this.taskOwner = creature;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = onlyNearby;
        this.targetClass = classTarget;
        this.targetChance = chance;
        this.sorter = new Sorter((Entity)creature);
        this.setMutexBits(1);
        this.targetEntitySelector = new Predicate<T>(){

            public boolean apply(@Nullable T p_apply_1_) {
                if (p_apply_1_ == null) {
                    return false;
                }
                if (targetSelector != null && !targetSelector.apply(p_apply_1_)) {
                    return false;
                }
                return !EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) ? false : EntityAINearestAttackableTargetStatus.this.isSuitableTarget((EntityLivingBase)p_apply_1_, true);
            }
        };
        this.sneakPPP = sneakP;
        this.inviPPP = inviP;
    }

    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0 || this.taskOwner.getParasiteStatus() != 0) {
            return false;
        }
        if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
            List list = EntityQueries.entities(this.taskOwner.worldObj, this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            }
            Collections.sort(list, this.sorter);
            this.targetEntity = (EntityLivingBase)list.get(0);
            EntityLivingBase entitylivingbase = this.taskOwner.getAttackTarget();
            if (entitylivingbase == null) {
                return true;
            }
            return !entitylivingbase.isEntityAlive();
        }
        this.targetEntity = this.getNearest(this.taskOwner.posX, this.taskOwner.posY + (double)this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), this.targetEntitySelector);
        return this.targetEntity != null;
    }

    public boolean continueExecuting() {
        EntityLivingBase entitylivingbase = this.taskOwner.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive()) {
            return false;
        }
        Team team = this.taskOwner.getTeam();
        Team team1 = entitylivingbase.getTeam();
        if (team != null && team1 == team) {
            return false;
        }
        double d0 = this.getTargetDistance();
        if (this.taskOwner.getDistanceSqToEntity((Entity)entitylivingbase) > d0 * d0) {
            return false;
        }
        if (this.shouldCheckSight || !this.taskOwner.getGeneMod(2)) {
            if (this.taskOwner.getEntitySenses().canSee((Entity)entitylivingbase)) {
                this.targetUnseenTicks = 0;
            } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                return false;
            }
        }
        if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage) {
            return false;
        }
        this.taskOwner.setAttackTarget(entitylivingbase);
        return true;
    }

    private EntityPlayer getNearest(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Predicate<EntityPlayer> predicate) {
        double d0 = -1.0;
        EntityPlayer entityplayer = null;
        for (int j2 = 0; j2 < EntityQueries.players(this.taskOwner.worldObj).size(); ++j2) {
            EntityPlayer entityplayer1 = (EntityPlayer)EntityQueries.players(this.taskOwner.worldObj).get(j2);
            if (entityplayer1.capabilities.disableDamage || !entityplayer1.isEntityAlive() || entityplayer1.isSpectator() || predicate != null && !predicate.apply((Object)entityplayer1)) continue;
            double d1 = entityplayer1.getDistanceSq(posX, entityplayer1.posY, posZ);
            double d2 = maxXZDistance;
            if (entityplayer1.isSneaking()) {
                d2 = maxXZDistance * this.sneakPPP;
            }
            if (entityplayer1.isInvisible()) {
                float f = entityplayer1.getArmorVisibility();
                if (f < 0.1f && this.inviPPP != 1.0f) {
                    f = 0.1f;
                }
                d2 *= (double)(this.inviPPP * f);
            }
            d2 = ForgeHooks.getPlayerVisibilityDistance((EntityPlayer)entityplayer1, (double)d2, (double)maxYDistance);
            if (!(maxYDistance < 0.0) && !(Math.abs(entityplayer1.posY - posY) < maxYDistance * maxYDistance) || !(maxXZDistance < 0.0) && !(d1 < d2 * d2) || d0 != -1.0 && !(d1 < d0)) continue;
            d0 = d1;
            entityplayer = entityplayer1;
        }
        return entityplayer;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return Boxes.grow(Boxes.entityBox(this.taskOwner), targetDistance, 4.0, targetDistance);
    }

    public void startExecuting() {
        double d0 = this.getTargetDistance();
        if (this.taskOwner.getDistanceSqToEntity((Entity)this.targetEntity) > d0 * d0) {
            this.targetEntity = null;
        }
        this.taskOwner.setAttackTarget((EntityLivingBase)this.targetEntity);
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean includeInvincibles) {
        if (!EntityAINearestAttackableTargetStatus.isSuitableTarget((EntityLiving)this.taskOwner, target, includeInvincibles, this.shouldCheckSight || !this.taskOwner.getGeneMod(2))) {
            return false;
        }
        if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos((Entity)target))) {
            return false;
        }
        if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
                this.targetSearchStatus = 0;
            }
            if (this.targetSearchStatus == 0) {
                int n = this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
            }
            if (this.targetSearchStatus == 2) {
                return false;
            }
        }
        return true;
    }

    protected double getTargetDistance() {
        IAttributeInstance iattributeinstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
        return iattributeinstance == null ? 16.0 : iattributeinstance.getAttributeValue();
    }

    public static boolean isSuitableTarget(EntityLiving attacker, @Nullable EntityLivingBase target, boolean includeInvincibles, boolean checkSight) {
        if (target == null) {
            return false;
        }
        if (target == attacker) {
            return false;
        }
        if (!target.isEntityAlive()) {
            return false;
        }
        if (!attacker.canAttackClass(target.getClass())) {
            return false;
        }
        if (attacker.isOnSameTeam((Entity)target)) {
            return false;
        }
        if (attacker instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId() != null) {
            if (target instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(((IEntityOwnable)target).getOwnerId())) {
                return false;
            }
            if (target == ((IEntityOwnable)attacker).getOwner()) {
                return false;
            }
        } else if (target instanceof EntityPlayer && !includeInvincibles && ((EntityPlayer)target).capabilities.disableDamage) {
            return false;
        }
        return !checkSight || attacker.getEntitySenses().canSee((Entity)target);
    }

    private boolean canEasilyReach(EntityLivingBase target) {
        int j;
        this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
        Path path = this.taskOwner.getNavigator().getPathToEntityLiving((Entity)target);
        if (path == null) {
            return false;
        }
        PathPoint pathpoint = path.getFinalPathPoint();
        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.xCoord - MathHelper.floor_double((double)target.posX);
        return (double)(i * i + (j = pathpoint.zCoord - MathHelper.floor_double((double)target.posZ)) * j) <= 2.25;
    }

    public static class Sorter
    implements Comparator<Entity> {
        private final Entity entity;

        public Sorter(Entity entityIn) {
            this.entity = entityIn;
        }

        @Override
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d1;
            double d0 = this.entity.getDistanceSqToEntity(p_compare_1_);
            if (d0 < (d1 = this.entity.getDistanceSqToEntity(p_compare_2_))) {
                return -1;
            }
            return d0 > d1 ? 1 : 0;
        }
    }
}

