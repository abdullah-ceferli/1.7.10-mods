/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyHit;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBody
extends Entity {
    protected EntityParasiteBase parent;
    protected float damageMultiplier;
    private float eye;
    private float offx;
    private float offy;
    private float offz;
    private int inverted;
    private int partId;
    private boolean logicSide;

    public EntityBody(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side) {
        super(parent.worldObj);
        this.setSize(width, height);
        this.parent = parent;
        this.damageMultiplier = damageMultiplier;
        this.eye = height * 0.8f;
        this.offx = offset;
        this.offy = offheight;
        this.offz = offset;
        this.inverted = inv;
        this.partId = id;
        this.logicSide = side;
    }

    public EntityBody(EntityParasiteBase parent, float width, float height, float damageMultiplier, float offset, float offheight, int inv, int id, boolean side, float eyes) {
        this(parent, width, height, damageMultiplier, offset, offheight, inv, id, side);
        this.eye = eyes;
    }

    public void onUpdate() {
        if (this.parent == null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this);
            return;
        }
        if (this.parent.isDead) {
            this.worldObj.removePlayerEntityDangerously((Entity)this);
            return;
        }
        if (this.logicSide) {
            this.updatePositionWithParentSides();
        } else {
            this.updatePositionWithParentFront();
        }
        super.onUpdate();
    }

    private void updatePositionWithParentSides() {
        float f17 = this.parent.rotationYaw * ((float)Math.PI / 180);
        float f3 = MathHelper.sin((float)f17);
        float f18 = MathHelper.cos((float)f17);
        this.rotationYaw = this.parent.rotationYaw;
        this.setPositionAndUpdate(this.parent.posX + (double)this.inverted * (double)(f18 * this.offx), this.parent.posY + (double)this.offy, this.parent.posZ + (double)this.inverted * (double)(f3 * this.offz));
    }

    private void updatePositionWithParentFront() {
        float f19 = MathHelper.sin((float)(this.parent.rotationYaw * ((float)Math.PI / 180) - this.parent.randomYawVelocity * 0.01f));
        float f14 = 0.17453292f;
        float f16 = MathHelper.cos((float)f14);
        float f4 = MathHelper.cos((float)(this.parent.rotationYaw * ((float)Math.PI / 180) - this.parent.randomYawVelocity * 0.01f));
        this.rotationYaw = this.parent.rotationYaw;
        this.setPositionAndUpdate(this.parent.posX + (double)this.inverted * (double)(f19 * this.offx * f16), this.parent.posY + (double)this.offy, this.parent.posZ - (double)this.inverted * (double)(f4 * this.offx * f16));
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.worldObj.isRemote && source.getSourceOfDamage() instanceof EntityPlayer) {
            SRPMain.network.sendToServer((IMessage)new SRPPacketEntityBodyHit(this.parent.getEntityId(), this.partId));
            return false;
        }
        if (this.parent instanceof EntityBodyParts) {
            EntityBodyParts rTarget = (EntityBodyParts)((Object)this.parent);
            return rTarget.attackEntityBodyFrom(source, amount, this.partId, false);
        }
        if (this.parent != null) {
            return this.parent.attackEntityFrom(source, amount);
        }
        return super.attackEntityFrom(source, amount);
    }

    public void collideWithNearbyEntities() {
        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, Boxes.offset(Boxes.grow(Boxes.entityBox(this), 1.0, 1.0, 1.0), 0.0, 0.0, 0.0));
        double d0 = (Boxes.entityBox(this).minX + Boxes.entityBox(this).maxX) / 2.0;
        double d1 = (Boxes.entityBox(this).minZ + Boxes.entityBox(this).maxZ) / 2.0;
        for (Entity entity : entities) {
            if (!(entity instanceof EntityLivingBase) || entity == this.parent || entity instanceof EntityParasiteBase) continue;
            double d2 = entity.posX - d0;
            double d3 = entity.posZ - d1;
            double d4 = d2 * d2 + d3 * d3;
            entity.addVelocity(d2 / d4, (double)0.2f, d3 / d4);
            entity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this.parent), 5.0f);
        }
    }

    public int getId() {
        return this.partId;
    }

    public EntityParasiteBase getFather() {
        return this.parent;
    }

    public float getEyeHeight() {
        return this.eye;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public AxisAlignedBB getBoundingBox() {
        return super.getBoundingBox();
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    protected void entityInit() {
    }

    public void setBodySize(float w, float h) {
        this.setSize(w, h);
    }

    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn || this.parent == entityIn;
    }

    @SideOnly(value=Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    }
}

