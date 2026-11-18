/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.BossInfo$Color
 *  net.minecraft.world.BossInfo$Overlay
 *  net.minecraft.world.BossInfoServer
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySource
extends Entity {
    private final BossInfoServer bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS).setDarkenSky(false);
    private byte type;
    private float total;
    private float charging;

    public EntitySource(World parent) {
        super(parent);
        this.setSize(0.5f, 0.5f);
        this.total = 100.0f;
    }

    public void onUpdate() {
        super.onUpdate();
        this.bossInfo.setPercent(this.charging / this.total);
        if (this.ticksExisted % 20 == 0 && !this.worldObj.isRemote) {
            this.charging += 1.0f;
            if (this.charging > this.total) {
                this.attack();
            }
        }
    }

    private void attack() {
        if (this.charging > 200.0f) {
            this.setDead();
        }
    }

    public void setMinecartName(String name) {
        super.setMinecartName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public IChatComponent getDisplayName() {
        return new ChatComponentText("The Source");
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    protected void entityInit() {
    }

    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn;
    }

    @SideOnly(value=Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    }
}

