/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.RandomPositionGenerator
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.Vec3d
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;

public class EntityAIAvoidEntityStatus<T extends Entity>
extends EntityAIBase {
    private final Predicate<Entity> canBeSeenSelector = new Predicate<Entity>(){

        public boolean apply(@Nullable Entity p_apply_1_) {
            return p_apply_1_.isEntityAlive() && EntityAIAvoidEntityStatus.this.entity.getEntitySenses().canSee(p_apply_1_) && !EntityAIAvoidEntityStatus.this.entity.isOnSameTeam(p_apply_1_);
        }
    };
    protected EntityParasiteBase entity;
    private final double farSpeed;
    protected T closestLivingEntity;
    private final float avoidDistance;
    private Path path;
    private final PathNavigate navigation;
    private final Class<T> classToAvoid;
    private final Predicate<? super T> avoidTargetSelector;

    public EntityAIAvoidEntityStatus(EntityParasiteBase entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn) {
        this(entityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn);
    }

    public EntityAIAvoidEntityStatus(EntityParasiteBase entityIn, Class<T> classToAvoidIn, Predicate<? super T> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn) {
        this.entity = entityIn;
        this.classToAvoid = classToAvoidIn;
        this.avoidTargetSelector = avoidTargetSelectorIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.navigation = entityIn.getNavigator();
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
        List list = EntityQueries.entities(this.entity.worldObj, this.classToAvoid, Boxes.grow(Boxes.entityBox(this.entity), (double)this.avoidDistance, 3.0, (double)this.avoidDistance), Predicates.and((Predicate[])new Predicate[]{EntitySelectors.CAN_AI_TARGET, this.canBeSeenSelector, this.avoidTargetSelector}));
        if (list.isEmpty() || this.entity.shouldWorkTask()) {
            return false;
        }
        this.closestLivingEntity = (Entity)list.get(0);
        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom((EntityCreature)this.entity, (int)16, (int)7, (Vec3d)new Vec3d(((Entity)this.closestLivingEntity).posX, ((Entity)this.closestLivingEntity).posY, ((Entity)this.closestLivingEntity).posZ));
        if (vec3d == null) {
            return false;
        }
        if (this.closestLivingEntity.getDistanceSq(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord) < this.closestLivingEntity.getDistanceSqToEntity((Entity)this.entity)) {
            return false;
        }
        this.path = this.navigation.getPathToXYZ(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
        return this.path != null;
    }

    public boolean continueExecuting() {
        return !this.navigation.noPath();
    }

    public void startExecuting() {
        this.navigation.setPath(this.path, this.farSpeed);
    }

    public void resetTask() {
        this.closestLivingEntity = null;
    }

    public void updateTask() {
        this.entity.getNavigator().setSpeed(this.farSpeed);
    }
}

