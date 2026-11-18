/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIKirinBlink
extends EntityAIBase {
    private final EntityKirin kirin;
    private final World world;
    private BlockPos targetPos = null;
    private int chargeTicks = 0;
    private static final int CHARGE_TIME = 60;
    private static final int COOLDOWN_TICKS = 200;
    private int nextAllowedTick = 0;
    private static final double MIN_FAR_DIST_SQ = 256.0;
    private static final int RADIUS_MAX = 24;
    private static final int MAX_TRIES = 64;

    public EntityAIKirinBlink(EntityKirin kirin) {
        this.kirin = kirin;
        this.world = kirin.worldObj;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        if (this.kirin.ticksExisted < this.nextAllowedTick || this.kirin.getParasiteStatus() >= 3) {
            return false;
        }
        EntityLivingBase target = this.kirin.getAttackTarget();
        if (target == null) {
            return false;
        }
        if (!this.kirin.getEntitySenses().canSee((Entity)target)) {
            return false;
        }
        if (this.isIndoors(target)) {
            return false;
        }
        if (this.kirin.getDistanceSqToEntity((Entity)target) <= 256.0) {
            return false;
        }
        BlockPos pos = this.findBlinkSpotNear(target);
        if (pos == null) {
            return false;
        }
        this.targetPos = pos;
        return true;
    }

    public boolean continueExecuting() {
        return this.chargeTicks > 0;
    }

    public boolean isInterruptible() {
        return false;
    }

    public void startExecuting() {
        this.chargeTicks = 60;
        this.kirin.getNavigator().clearPathEntity();
        this.kirin.motionX = 0.0;
        this.kirin.motionY = 0.0;
        this.kirin.motionZ = 0.0;
        this.kirin.setBlinkCharge(this.targetPos, this.chargeTicks);
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.kirin, SRPSounds.OMBOO_DEATH, 1.0f, 0.9f);
    }

    public void updateTask() {
        if (this.targetPos == null) {
            this.chargeTicks = 0;
            return;
        }
        this.kirin.getNavigator().clearPathEntity();
        this.kirin.motionZ = 0.0;
        this.kirin.motionY = 0.0;
        this.kirin.motionX = 0.0;
        if (!this.world.isRemote && this.chargeTicks % 10 == 0) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.kirin, SoundEvents.BLOCK_NOTE_HAT, 0.9f, 1.25f);
        }
        --this.chargeTicks;
        this.kirin.setBlinkCharge(this.targetPos, Math.max(this.chargeTicks, 0));
        if (!this.world.isRemote && this.chargeTicks <= 0) {
            this.kirin.attemptTeleport((double)this.targetPos.getX() + 0.5, this.targetPos.getY(), (double)this.targetPos.getZ() + 0.5);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.kirin, SRPSounds.INFECTEDENDERMAN_PORTAL, 1.0f, 1.0f);
            this.doBlinkLifeSteal();
            this.nextAllowedTick = this.kirin.ticksExisted + 200;
            this.kirin.clearBlinkCharge();
            this.resetTask();
        }
    }

    public void resetTask() {
        this.targetPos = null;
        this.chargeTicks = 0;
    }

    private boolean isIndoors(EntityLivingBase target) {
        BlockPos head = new BlockPos(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ);
        for (int i = 0; i < 3; ++i) {
            if (!WorldAccess.canSeeSky(this.world, head.up(i))) continue;
            return false;
        }
        return true;
    }

    private BlockPos findBlinkSpotNear(EntityLivingBase target) {
        BlockPos tpos = EntityQueries.position(target);
        for (int i = 0; i < 64; ++i) {
            int[] ys;
            double r = 1.5 + this.kirin.getRNG().nextDouble() * 22.5;
            double a = this.kirin.getRNG().nextDouble() * Math.PI * 2.0;
            int x = MathHelper.floor_double((double)((double)tpos.getX() + 0.5 + r * Math.cos(a)));
            int z = MathHelper.floor_double((double)((double)tpos.getZ() + 0.5 + r * Math.sin(a)));
            for (int y : ys = new int[]{tpos.getY(), tpos.getY() + 1, tpos.getY() - 1, tpos.getY() + 2, tpos.getY() - 2, tpos.getY() + 3, tpos.getY() - 3, tpos.getY() + 4, tpos.getY() - 4, tpos.getY() + 6, tpos.getY() - 6, tpos.getY() + 8, tpos.getY() - 8}) {
                BlockPos p = new BlockPos(x, y, z);
                if (!this.isSpotValid(p) || !WorldAccess.canSeeSky(this.world, p.up()) || !this.hasLineOfSight(target, p)) continue;
                return p;
            }
        }
        return null;
    }

    private void doBlinkLifeSteal() {
        if (this.world.isRemote) {
            return;
        }
        double radius = 5.0;
        AxisAlignedBB box = Boxes.create(this.kirin.posX - radius, this.kirin.posY - radius, this.kirin.posZ - radius, this.kirin.posX + radius, this.kirin.posY + radius, this.kirin.posZ + radius);
        List nearby = EntityQueries.entities(this.world, EntityLivingBase.class, box, e -> e != null && e != this.kirin && e.isEntityAlive() && !this.isSRPEntity((EntityLivingBase)e));
        if (nearby.isEmpty()) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.world, null, this.kirin.posX, this.kirin.posY, this.kirin.posZ, SRPSounds.ALAFHA_HURT, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 0.7f, 0.9f + this.kirin.getRNG().nextFloat() * 0.2f);
            return;
        }
        EntityLivingBase target = (EntityLivingBase)nearby.get(0);
        float currentHealth = target.getHealth();
        if (currentHealth <= 0.0f) {
            return;
        }
        float stolen = currentHealth * 0.5f;
        float newHealth = currentHealth - stolen;
        if (newHealth < 0.0f) {
            newHealth = 0.0f;
        }
        target.setHealth(newHealth);
        this.forceHurtAnim(target);
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.world, null, target.posX, target.posY, target.posZ, SRPSounds.CRUX_HURT, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 1.0f, 0.8f + this.kirin.getRNG().nextFloat() * 0.4f);
        if (stolen > 0.0f) {
            this.kirin.heal(stolen);
        }
    }

    public void forceHurtAnim(EntityLivingBase target) {
        target.hurtTime = target.maxHurtTime = 10;
        target.attackedAtYaw = 0.0f;
    }

    private boolean isSRPEntity(EntityLivingBase entity) {
        String name = entity.getClass().getName();
        return name.startsWith("com.dhanantry.scapeandrunparasites.entity");
    }

    private boolean isSpotValid(BlockPos p) {
        if (!WorldAccess.isBlockLoaded(this.world, p)) {
            return false;
        }
        AxisAlignedBB aabb = Boxes.shrink(Boxes.create(p), 0.05);
        if (!this.world.getCollisionBoxes((Entity)this.kirin, aabb).isEmpty()) {
            return false;
        }
        BlockPos below = p.down();
        return BlockStates.get(this.world, below).getMaterial().isSolid();
    }

    private boolean hasLineOfSight(EntityLivingBase target, BlockPos to) {
        Vec3d dest;
        Vec3d from = new Vec3d(this.kirin.posX, this.kirin.posY + (double)this.kirin.getEyeHeight(), this.kirin.posZ);
        RayTraceResult r = this.world.rayTraceBlocks(from, dest = new Vec3d((double)to.getX() + 0.5, (double)to.getY() + 0.5, (double)to.getZ() + 0.5), false, true, false);
        if (r != null && r.typeOfHit == RayTraceResult.Type.BLOCK) {
            return false;
        }
        return this.kirin.getEntitySenses().canSee((Entity)target);
    }

    public static boolean tryBlinkToNearbyLand(EntityKirin kirin, int horizontalRange, int verticalRange) {
        World world = kirin.worldObj;
        BlockPos origin = EntityQueries.position(kirin);
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (int dx = -horizontalRange; dx <= horizontalRange; ++dx) {
            block1: for (int dz = -horizontalRange; dz <= horizontalRange; ++dz) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                for (int dy = verticalRange; dy >= -verticalRange; --dy) {
                    int y = origin.getY() + dy;
                    BlockPos p = new BlockPos(x, y, z);
                    if (!EntityAIKirinBlink.isRecoverySpotValid(world, p)) continue;
                    double d = kirin.getDistanceSqToCenter(p);
                    if (!(d < bestDistSq)) continue block1;
                    bestDistSq = d;
                    best = p;
                    continue block1;
                }
            }
        }
        if (best != null) {
            return kirin.attemptTeleport((double)best.getX() + 0.5, best.getY(), (double)best.getZ() + 0.5);
        }
        return false;
    }

    private static boolean isRecoverySpotValid(World world, BlockPos p) {
        return BlockStates.get(world, p.down()).isFullCube() && WorldAccess.isAirBlock(world, p) && WorldAccess.isAirBlock(world, p.up()) && WorldAccess.canSeeSky(world, p.up());
    }
}

