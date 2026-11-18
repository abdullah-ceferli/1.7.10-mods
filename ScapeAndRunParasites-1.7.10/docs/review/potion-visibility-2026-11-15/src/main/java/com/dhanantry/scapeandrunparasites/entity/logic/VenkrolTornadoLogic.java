/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.logic;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class VenkrolTornadoLogic {
    public static void tickTornadoEffects(EntityLivingBase venkrol) {
        if (!SRPConfigWorld.venkrolTornadoEnabled) {
            return;
        }
        if (venkrol == null) {
            return;
        }
        World world = venkrol.worldObj;
        if (world == null || world.isRemote) {
            return;
        }
        if (!world.isRaining() || !world.isThundering()) {
            return;
        }
        double x = venkrol.posX;
        double maxRadius = 120.0;
        double y = venkrol.posY;
        double z = venkrol.posZ;
        double height = 50.0;
        AxisAlignedBB box = Boxes.create(x - maxRadius, y, z - maxRadius, x + maxRadius, y + height, z + maxRadius);
        List targets = EntityQueries.entities(world, EntityLivingBase.class, box);
        if (targets.isEmpty()) {
            return;
        }
        for (EntityLivingBase e : targets) {
            if (e == venkrol || !e.isEntityAlive() || VenkrolTornadoLogic.isSRPParasite(e) || e.posY < venkrol.posY) continue;
            VenkrolTornadoLogic.applyTornadoForces(venkrol, e, maxRadius);
        }
    }

    private static boolean isSRPParasite(EntityLivingBase e) {
        ResourceLocation rl = EntityList.getKey((Entity)e);
        if (rl == null) {
            return false;
        }
        String modid = rl.getResourceDomain();
        return "srparasites".equals(modid);
    }

    private static void applyTornadoForces(EntityLivingBase venkrol, EntityLivingBase target, double maxRadius) {
        double heightAboveVenkrol;
        boolean inFlingZone;
        double horizDist;
        World world;
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)target;
            ItemStack boots = (ItemStack)player.inventory.armorInventory.get(0);
            if (!boots.isEmpty() && boots.getItem() == SRPItems.VENKROL_BOOTS) {
                return;
            }
            if (player.isSpectator()) {
                return;
            }
            if (player.capabilities.isCreativeMode && player.capabilities.isFlying) {
                return;
            }
        }
        if ((world = venkrol.worldObj) == null) {
            return;
        }
        if (target.isRiding() || target.isDead) {
            return;
        }
        double dx = venkrol.posX - target.posX;
        double dz = venkrol.posZ - target.posZ;
        double distSq = dx * dx + dz * dz;
        if (distSq < 1.0E-4) {
            distSq = 1.0E-4;
        }
        if ((horizDist = Math.sqrt(distSq)) > maxRadius) {
            return;
        }
        double normX = dx / horizDist;
        double normZ = dz / horizDist;
        double pullTierFactor = horizDist >= 50.0 ? 0.05 : (horizDist >= 25.0 ? 0.1 : (horizDist >= 15.0 ? 0.2 : (horizDist >= 10.0 ? 0.35 : (horizDist >= 5.0 ? 0.55 : 1.0))));
        double basePullStrength = 0.08;
        double baseSwirlStrength = 0.07;
        double pullStrength = basePullStrength * pullTierFactor;
        double swirlStrength = baseSwirlStrength * pullTierFactor;
        double swirlX = -normZ;
        double swirlZ = normX;
        double innerLiftRadius = 15.0;
        double liftAccel = 0.0;
        if (horizDist <= innerLiftRadius) {
            double liftFactor = 1.0 - horizDist / innerLiftRadius;
            if (liftFactor < 0.0) {
                liftFactor = 0.0;
            }
            if (liftFactor > 1.0) {
                liftFactor = 1.0;
            }
            double baseLiftAccel = 0.25;
            liftAccel = baseLiftAccel * liftFactor;
        }
        boolean bl = inFlingZone = (heightAboveVenkrol = target.posY - venkrol.posY) > 16.0 && horizDist < 12.0;
        if (heightAboveVenkrol > 18.0 && horizDist > 18.0) {
            return;
        }
        double radialDirX = normX;
        double radialDirZ = normZ;
        if (inFlingZone) {
            radialDirX = -normX;
            radialDirZ = -normZ;
            double flingFactor = 1.0 - Math.min(horizDist / 12.0, 1.0);
            double baseFlingMult = 1.4;
            double maxFlingMult = 6.0;
            double flingMult = baseFlingMult + (maxFlingMult - baseFlingMult) * flingFactor;
            pullStrength *= flingMult;
            double baseSwirlMult = 1.0;
            double maxSwirlMult = 2.3;
            double swirlMult = baseSwirlMult + (maxSwirlMult - baseSwirlMult) * flingFactor;
            swirlStrength *= swirlMult;
            double outwardBurst = 1.1 * flingFactor;
            target.motionX += radialDirX * outwardBurst;
            target.motionZ += radialDirZ * outwardBurst;
            double downwardBoost = -0.16 * flingFactor;
            target.motionY += downwardBoost;
            if (target.motionY < -1.6) {
                target.motionY = -1.6;
            }
        } else if (liftAccel > 0.0 && heightAboveVenkrol < 25.0) {
            target.motionY += liftAccel;
            if (target.motionY > 1.2) {
                target.motionY = 1.2;
            }
            target.fallDistance = 0.0f;
        }
        double awayX = -normX;
        double awayZ = -normZ;
        double dotAway = target.motionX * awayX + target.motionZ * awayZ;
        double forceScale = dotAway > 0.0 ? 1.0 : 0.25;
        target.motionX += radialDirX * (pullStrength *= forceScale) + swirlX * swirlStrength;
        target.motionZ += radialDirZ * pullStrength + swirlZ * swirlStrength;
        target.velocityChanged = true;
    }
}

