/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockWebBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

import net.minecraft.potion.PotionEffect;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityProjectileWebball
extends EntitySRPProjectile {
    private static final float WEB_BLIND_CHANCE = 0.3f;
    private static final int WEB_BLIND_TICKS = 60;
    private static final int WEB_BLIND_AMPLIFIER = 0;
    private byte type;

    public EntityProjectileWebball(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
        this.type = 1;
    }

    public EntityProjectileWebball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
        this.type = 1;
    }

    public EntityProjectileWebball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, byte t) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
        this.type = t;
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    protected void onImpact(RayTraceResult result) {
        if (this.worldObj.isRemote) {
            return;
        }
        boolean griefing = ForgeEventFactory.getMobGriefingEvent((World)this.worldObj, (Entity)this);
        Consumer<BlockPos> tryPlaceWeb = pos -> {
            if (griefing && WorldAccess.isAirBlock(this.worldObj, pos)) {
                BlockStates.set(this.worldObj, pos, BlockStates.defaultState(Blocks.web), 3);
            }
        };
        if (result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)result.entityHit;
            if (target instanceof EntityPlayer && !target.isPotionActive(net.minecraft.potion.Potion.blindness) && this.rand.nextFloat() < 0.3f) {
                target.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.blindness, 60, 0, false, true));
            }
            BlockPos feet = new BlockPos(target.posX, Math.floor(Boxes.entityBox(target).minY), target.posZ);
            tryPlaceWeb.accept(feet);
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos placePos = result.getBlockPos().offset(result.sideHit);
            tryPlaceWeb.accept(placePos);
        }
        this.worldObj.setEntityState((Entity)this, (byte)3);
        this.setDead();
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 60) {
            this.setWebsAround();
            this.setDead();
        }
    }

    private void setWebsAround() {
        int totalWebs = this.rand.nextInt(3) + 1;
        int[] positionss = new int[]{-1, 0, 1};
        block5: for (int i = 1; i <= totalWebs; ++i) {
            int poz;
            int poy;
            int pox = positionss[this.rand.nextInt(3)];
            if (BlockStates.get(this.worldObj, new BlockPos(this.posX + (double)pox, this.posY + (double)(poy = positionss[this.rand.nextInt(3)]), this.posZ + (double)(poz = positionss[this.rand.nextInt(3)]))).getBlock() != Blocks.air) continue;
            switch (this.type) {
                case 1: {
                    BlockStates.set(this.worldObj, new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), BlockStates.defaultState(SRPBlocks.SRPWeb));
                    continue block5;
                }
                case 2: {
                    BlockStates.set(this.worldObj, new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), BlockStates.defaultState(SRPBlocks.SRPWeb).withProperty(BlockWebBase.VARIANT, (Comparable)((Object)BlockWebBase.EnumType.TWO)));
                    continue block5;
                }
                case 3: {
                    BlockStates.set(this.worldObj, new BlockPos(this.posX + (double)pox, this.posY + (double)poy, this.posZ + (double)poz), BlockStates.defaultState(SRPBlocks.SRPWeb).withProperty(BlockWebBase.VARIANT, (Comparable)((Object)BlockWebBase.EnumType.THREE)));
                }
            }
        }
    }
}

