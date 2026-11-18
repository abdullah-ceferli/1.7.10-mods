package com.dhanantry.scapeandrunparasites.backport.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/** Modern melee reach, retry timing and attack cadence on native navigation and combat. */
public class EntityAIAttackMelee extends EntityAIBase {
    World world;
    protected EntityCreature attacker;
    protected int attackTick;
    double speedTowardsTarget;
    boolean longMemory;
    PathEntity path;
    private int delayCounter;
    private double targetX, targetY, targetZ;
    protected final int attackInterval = 20;

    public EntityAIAttackMelee(EntityCreature creature, double speed, boolean rememberTarget) {
        attacker = creature;
        world = creature.worldObj;
        speedTowardsTarget = speed;
        longMemory = rememberTarget;
        setMutexBits(3);
    }

    @Override public boolean shouldExecute() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;
        path = attacker.getNavigator().getPathToEntityLiving(target);
        if (path != null) return true;
        return getAttackReachSqr(target) >= attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
    }

    @Override public boolean continueExecuting() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;
        if (!longMemory) return !attacker.getNavigator().noPath();
        if (!attacker.isWithinHomeDistance(MathHelper.floor_double(target.posX), MathHelper.floor_double(target.posY), MathHelper.floor_double(target.posZ))) return false;
        return !excludedPlayer(target);
    }

    @Override public void startExecuting() {
        attacker.getNavigator().setPath(path, speedTowardsTarget);
        delayCounter = 0;
    }

    @Override public void resetTask() {
        if (excludedPlayer(attacker.getAttackTarget())) attacker.setAttackTarget(null);
        attacker.getNavigator().clearPathEntity();
    }

    /** Native 1.7 has creative and damage immunity, but no spectator game mode. */
    private static boolean excludedPlayer(EntityLivingBase target) {
        if (!(target instanceof EntityPlayer)) return false;
        EntityPlayer player = (EntityPlayer)target;
        return player.capabilities.isCreativeMode || player.capabilities.disableDamage;
    }

    @Override public void updateTask() {
        EntityLivingBase target = attacker.getAttackTarget();
        attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double distance = attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
        --delayCounter;
        if ((longMemory || attacker.getEntitySenses().canSee(target)) && delayCounter <= 0
            && (targetX == 0.0 && targetY == 0.0 && targetZ == 0.0
                || target.getDistanceSq(targetX, targetY, targetZ) >= 1.0
                || attacker.getRNG().nextFloat() < 0.05F)) {
            targetX = target.posX;
            targetY = target.boundingBox.minY;
            targetZ = target.posZ;
            delayCounter = 4 + attacker.getRNG().nextInt(7);
            if (distance > 1024.0) delayCounter += 10;
            else if (distance > 256.0) delayCounter += 5;
            if (!attacker.getNavigator().tryMoveToEntityLiving(target, speedTowardsTarget)) delayCounter += 15;
        }
        attackTick = Math.max(attackTick - 1, 0);
        checkAndPerformAttack(target, distance);
    }

    protected void checkAndPerformAttack(EntityLivingBase target, double distance) {
        if (distance <= getAttackReachSqr(target) && attackTick <= 0) {
            attackTick = 20;
            attacker.swingItem();
            attacker.attackEntityAsMob(target);
        }
    }

    protected double getAttackReachSqr(EntityLivingBase target) {
        return attacker.width * 2.0F * (attacker.width * 2.0F) + target.width;
    }
}
