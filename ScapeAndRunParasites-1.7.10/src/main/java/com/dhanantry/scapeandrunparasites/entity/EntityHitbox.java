/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.IEntityMultiPart
 *  net.minecraft.entity.MultiPartEntityPart
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.network.HitboxHit;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.EnumDifficulty;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHitbox
extends MultiPartEntityPart {
    private float radius;
    private float angle;
    private float yPos;
    private float dmgVuln;
    private EntityLiving parentEntity;

    public EntityHitbox(EntityLiving parent, float angle, float radius, float yOffset, float width, float height, float damageVulnerability) {
        super((IEntityMultiPart)parent, "part", width, height);
        this.parentEntity = parent;
        this.yPos = yOffset;
        this.radius = radius;
        this.angle = angle;
        this.dmgVuln = damageVulnerability;
        this.ignoreFrustumCheck = true;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getRadius() {
        return this.radius;
    }

    public boolean canBeCollidedWith() {
        return this.getParent().isEntityAlive();
    }

    public void resize(float scale) {
        this.setSize(this.width * scale, this.height * scale);
    }

    public EntityLiving getParent() {
        return this.parentEntity;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.worldObj.isRemote && (source.getSourceOfDamage() instanceof EntityPlayer || source.getEntity() instanceof EntityPlayer)) {
            SRPNetwork.CHANNEL.sendToServer((IMessage)new HitboxHit(this.getParent(), amount * this.dmgVuln, true));
        }
        return this.getParent().attackEntityFrom(source, amount * this.dmgVuln);
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (this.worldObj.isRemote) {
            SRPNetwork.CHANNEL.sendToServer((IMessage)new HitboxHit(this.getParent(), 0.0f, false));
        }
        return this.getParent().processInitialInteract(player, hand);
    }

    public boolean isEntityInvulnerable(DamageSource source) {
        return this.getIsInvulnerable() && source != DamageSource.outOfWorld;
    }

    public void onUpdate() {
        this.setPositionAndUpdate(this.getParent().posX + (double)this.radius * Math.cos((double)this.getParent().renderYawOffset * (Math.PI / 180) + (double)this.angle), this.getParent().posY + (double)this.yPos, this.getParent().posZ + (double)this.radius * Math.sin((double)this.getParent().renderYawOffset * (Math.PI / 180) + (double)this.angle));
        if (!this.worldObj.isRemote && this.width >= this.getParent().width) {
            this.collideWithNearbyEntities();
        }
        if (!this.getParent().isEntityAlive() || this.getParent() == null || DifficultyAccess.difficulty(this.getEntityWorld()) == EnumDifficulty.PEACEFUL) {
            this.getEntityWorld().removePlayerEntityDangerously((Entity)this);
        }
        super.onUpdate();
    }

    public void collideWithNearbyEntities() {
        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, Boxes.expand(Boxes.entityBox(this), (double)0.2f, 0.0, (double)0.2f));
        entities.stream().filter(entity -> entity != this.entityDragonObj && !(entity instanceof EntityHitbox) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision((Entity)this.getParent()));
    }

    @SideOnly(value=Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    }
}

