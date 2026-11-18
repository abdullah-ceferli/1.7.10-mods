/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityRemain
extends Entity {
    private int plus;
    private int count;
    private int goal;
    private boolean active;
    private String parasite;
    private float health;
    private byte skin;

    public EntityRemain(World in) {
        super(in);
        this.setSize(0.5f, 0.5f);
        this.active = false;
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            if (this.ticksExisted % 20 == 0) {
                if (!(BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockGore) || this.parasite == null) {
                    this.setDead();
                }
                if (!this.getActive() && SRPConfigSystems.disloGiveBodies && SRPSaveData.get(this.worldObj, 50).getCurrentCode(this.worldObj.provider.dimensionId, 20) >= 1) {
                    this.setPlus(SRPConfigMobs.canraadaptedremainplus);
                    this.setHealth(SRPConfigMobs.canraadaptedremainhealth);
                }
            }
            if (this.active) {
                this.count += this.plus;
                if (this.count > this.goal) {
                    List serverList = EntityQueries.loaded(this.worldObj);
                    int count = 0;
                    for (int x = 0; x < serverList.size(); ++x) {
                        if (serverList.get(x) instanceof EntityParasiteBase) {
                            ++count;
                        }
                        if (count <= SRPConfig.worldMobCap) continue;
                        this.count = 0;
                        return;
                    }
                    EntityParasiteBase out = (EntityParasiteBase)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(this.parasite), (World)this.worldObj);
                    if (out == null) {
                        return;
                    }
                    if (!out.worldObj.getCollisionBoxes((Entity)out, Boxes.entityBox(out)).isEmpty()) {
                        out.setDead();
                        return;
                    }
                    out.copyLocationAndAnglesFrom(this);
                    out.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(out)), null);
                    out.setSkin(this.skin);
                    out.setHealth(out.getMaxHealth() * this.health);
                    this.worldObj.playSound(null, EntityQueries.position(this), SRPSounds.RESURRECT, SoundCategory.HOSTILE, 1.0f, 1.0f);
                    this.worldObj.spawnEntityInWorld((Entity)out);
                    out.particleStatus((byte)7);
                    out.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.DEBAR_E, 400, 0, false, false));
                    BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(Blocks.air));
                    this.setDead();
                }
                if (this.count % 10 == 0) {
                    this.worldObj.setEntityState((Entity)this, (byte)18);
                }
            }
        }
    }

    public float getEyeHeight() {
        return this.height;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public void setParasite(String in) {
        this.parasite = in;
    }

    public void setGoal(int g) {
        this.goal = g;
    }

    public void setPlus(int p) {
        if (this.plus >= p) {
            return;
        }
        this.plus = p;
        this.active = true;
    }

    public void setSkin(byte in) {
        this.skin = in;
    }

    public void setHealth(float in) {
        if (this.health >= in) {
            return;
        }
        this.health = in;
    }

    public boolean getActive() {
        return this.active;
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("parasiteparasite", 8)) {
            this.parasite = compound.getString("parasiteparasite");
        }
        if (compound.hasKey("parasiteactive", 99)) {
            this.active = compound.getBoolean("parasiteactive");
        }
        if (compound.hasKey("parasitepoint", 99)) {
            this.count = compound.getInteger("parasitepoint");
        }
        if (compound.hasKey("parasiteplus", 99)) {
            this.plus = compound.getInteger("parasiteplus");
        }
        if (compound.hasKey("parasitegoal", 99)) {
            this.goal = compound.getInteger("parasitegoal");
        }
        if (compound.hasKey("parasiteskin", 99)) {
            this.skin = compound.getByte("parasiteskin");
        }
        if (compound.hasKey("parasitehealth", 99)) {
            this.health = compound.getFloat("parasitehealth");
        }
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (this.parasite != null) {
            compound.setString("parasiteparasite", this.parasite);
        }
        compound.setBoolean("parasiteactive", this.active);
        compound.setInteger("parasitepoint", this.count);
        compound.setInteger("parasiteplus", this.plus);
        compound.setInteger("parasitegoal", this.goal);
        compound.setByte("parasiteskin", this.skin);
        compound.setFloat("parasitehealth", this.health);
    }

    public void handleHealthUpdate(byte id) {
        if (id == 18) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
            }
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)this.rand.nextFloat() * ((double)this.height + 0.5), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, r, g, b);
    }

    protected void entityInit() {
    }
}

