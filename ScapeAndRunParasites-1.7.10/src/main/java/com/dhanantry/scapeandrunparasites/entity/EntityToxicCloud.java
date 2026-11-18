/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.minecraft.block.material.EnumPushReaction
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.PotionTypes
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.potion.PotionType
 *  net.minecraft.potion.PotionUtils
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.PotionTypes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityToxicCloud
extends com.dhanantry.scapeandrunparasites.backport.entity.LegacyEntity {
    private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(EntityToxicCloud.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Float> HEIG = EntityDataManager.createKey(EntityToxicCloud.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Byte> PART = EntityDataManager.createKey(EntityToxicCloud.class, (DataSerializer)DataSerializers.BYTE);
    private static final DataParameter<Boolean> IGNORE_RADIUS = EntityDataManager.createKey(EntityToxicCloud.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> PARTICLE = EntityDataManager.createKey(EntityToxicCloud.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> PARTICLE_PARAM_1 = EntityDataManager.createKey(EntityToxicCloud.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> PARTICLE_PARAM_2 = EntityDataManager.createKey(EntityToxicCloud.class, (DataSerializer)DataSerializers.VARINT);
    private PotionType potion = PotionTypes.EMPTY;
    private final List<PotionEffect> effects = Lists.newArrayList();
    private final Map<Entity, Integer> reapplicationDelayMap = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private float radiusOnUse;
    private float radiusPerTick;
    private EntityParasiteBase owner;
    private UUID ownerUniqueId;
    public int effectParticles;

    public EntityToxicCloud(World worldIn) {
        super(worldIn);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.setRadius(3.0f, 0.5f);
    }

    public EntityToxicCloud(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(PART, (Object)((byte)0));
        EntityDataManager.of(this).register(RADIUS, (Object)Float.valueOf(0.5f));
        EntityDataManager.of(this).register(HEIG, (Object)Float.valueOf(0.5f));
        EntityDataManager.of(this).register(IGNORE_RADIUS, (Object)Boolean.FALSE);
        EntityDataManager.of(this).register(PARTICLE, (Object)EnumParticleTypes.SPELL_MOB.getParticleID());
        EntityDataManager.of(this).register(PARTICLE_PARAM_1, (Object)0);
        EntityDataManager.of(this).register(PARTICLE_PARAM_2, (Object)0);
    }

    public void setRadius(float radiusIn, float heightIn) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        this.setSize(radiusIn * 2.0f, heightIn);
        this.setPosition(d0, d1, d2);
        if (!this.worldObj.isRemote) {
            EntityDataManager.of(this).set(RADIUS, (Object)Float.valueOf(radiusIn));
            EntityDataManager.of(this).set(HEIG, (Object)Float.valueOf(heightIn));
        }
    }

    public float getRadius() {
        return ((Float)EntityDataManager.of(this).get(RADIUS)).floatValue();
    }

    public float getHeight() {
        return ((Float)EntityDataManager.of(this).get(HEIG)).floatValue();
    }

    public void setPotion(PotionType potionIn) {
        this.potion = potionIn;
    }

    public void addEffect(PotionEffect effect) {
        this.effects.add(effect);
    }

    public byte getColor() {
        return (Byte)EntityDataManager.of(this).get(PART);
    }

    public void setColor(int colorIn) {
        EntityDataManager.of(this).set(PART, (Object)((byte)colorIn));
    }

    public EnumParticleTypes getParticle() {
        return EnumParticleTypes.getParticleFromId((int)((Integer)EntityDataManager.of(this).get(PARTICLE)));
    }

    public void setParticle(EnumParticleTypes particleIn) {
        EntityDataManager.of(this).set(PARTICLE, (Object)particleIn.getParticleID());
    }

    public int getParticleParam1() {
        return (Integer)EntityDataManager.of(this).get(PARTICLE_PARAM_1);
    }

    public void setParticleParam1(int particleParam) {
        EntityDataManager.of(this).set(PARTICLE_PARAM_1, (Object)particleParam);
    }

    public int getParticleParam2() {
        return (Integer)EntityDataManager.of(this).get(PARTICLE_PARAM_2);
    }

    public void setParticleParam2(int particleParam) {
        EntityDataManager.of(this).set(PARTICLE_PARAM_2, (Object)particleParam);
    }

    protected void setIgnoreRadius(boolean ignoreRadius) {
        EntityDataManager.of(this).set(IGNORE_RADIUS, (Object)ignoreRadius);
    }

    public boolean shouldIgnoreRadius() {
        return (Boolean)EntityDataManager.of(this).get(IGNORE_RADIUS);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    public void onUpdate() {
        block29: {
            boolean flag1;
            float f;
            boolean flag;
            block27: {
                int[] aint;
                EnumParticleTypes enumparticletypes;
                block28: {
                    super.onUpdate();
                    flag = this.shouldIgnoreRadius();
                    f = this.getRadius();
                    if (!this.worldObj.isRemote) break block27;
                    enumparticletypes = this.getParticle();
                    aint = new int[enumparticletypes.getArgumentCount()];
                    if (aint.length > 0) {
                        aint[0] = this.getParticleParam1();
                    }
                    if (aint.length > 1) {
                        aint[1] = this.getParticleParam2();
                    }
                    if (!flag) break block28;
                    if (this.rand.nextInt(3) == 0) break block29;
                    for (int i = 0; i < 2; ++i) {
                        float f1 = this.rand.nextFloat() * ((float)Math.PI * 2);
                        float f2 = MathHelper.sqrt_float((float)this.rand.nextFloat()) * 0.2f;
                        float f3 = MathHelper.cos((float)f1) * f2;
                        float f4 = MathHelper.sin((float)f1) * f2;
                        if (enumparticletypes == EnumParticleTypes.SPELL_MOB) {
                            int j = this.rand.nextBoolean() ? 0xFFFFFF : (int)this.getColor();
                            int k = j >> 16 & 0xFF;
                            int l = j >> 8 & 0xFF;
                            int i1 = j & 0xFF;
                            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.alwaysVisible(this.worldObj, EnumParticleTypes.SPELL_MOB.getParticleID(), this.posX + (double)f3, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)f4, (double)((float)k / 255.0f), (double)((float)l / 255.0f), (double)((float)i1 / 255.0f), new int[0]);
                            continue;
                        }
                        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.alwaysVisible(this.worldObj, enumparticletypes.getParticleID(), this.posX + (double)f3, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)f4, 0.0, 0.0, 0.0, aint);
                    }
                    break block29;
                }
                float f5 = (float)Math.PI * f * f;
                int k1 = 0;
                while ((float)k1 < f5) {
                    if (this.rand.nextInt(3) == 0) {
                        float f6 = this.rand.nextFloat() * ((float)Math.PI * 2);
                        float f7 = MathHelper.sqrt_float((float)this.rand.nextFloat()) * f;
                        float f8 = MathHelper.cos((float)f6) * f7;
                        float f9 = MathHelper.sin((float)f6) * f7;
                        if (enumparticletypes == EnumParticleTypes.SPELL_MOB) {
                            byte l1 = this.getColor();
                            int i2 = l1 >> 16 & 0xFF;
                            int j2 = l1 >> 8 & 0xFF;
                            int j1 = l1 & 0xFF;
                            double d11 = this.posX + (double)f8 * 1.0;
                            double d22 = this.posY + (double)(this.rand.nextFloat() * this.height);
                            double d33 = this.posZ + (double)f9 * 1.0;
                            double d44 = (float)i2 / 255.0f;
                            double d55 = (float)j2 / 255.0f;
                            double d66 = (float)j1 / 255.0f;
                            if (this.effectParticles == 1) {
                                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.alwaysVisible(this.worldObj, EnumParticleTypes.FLAME.getParticleID(), d11, d22, d33, d44, d55, d66, new int[0]);
                                this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0, d11, d22 + 0.5, d33, d44, d55, d66);
                            } else {
                                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.alwaysVisible(this.worldObj, EnumParticleTypes.SPELL_MOB.getParticleID(), d11, d22, d33, d44, d55, d66, new int[0]);
                            }
                        } else {
                            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.alwaysVisible(this.worldObj, enumparticletypes.getParticleID(), this.posX + (double)f8, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)f9, (0.5 - this.rand.nextDouble()) * 0.15, (double)0.01f, (0.5 - this.rand.nextDouble()) * 0.15, aint);
                        }
                    }
                    ++k1;
                }
                break block29;
            }
            if (this.ticksExisted >= this.waitTime + this.duration) {
                this.setDead();
                return;
            }
            boolean bl = flag1 = this.ticksExisted < this.waitTime;
            if (flag != flag1) {
                this.setIgnoreRadius(flag1);
            }
            if (flag1) {
                return;
            }
            if (this.radiusPerTick != 0.0f) {
                if ((f += this.radiusPerTick) < 0.5f) {
                    this.setDead();
                    return;
                }
                this.setRadius(f, this.getHeight());
            }
            if (this.ticksExisted % 5 == 0) {
                this.reapplicationDelayMap.entrySet().removeIf(entry -> this.ticksExisted >= (Integer)entry.getValue());
                ArrayList potions = Lists.newArrayList();
                for (PotionEffect potioneffect1 : this.potion.getEffects()) {
                    potions.add(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(potioneffect1.getPotion(), potioneffect1.getDuration() / 4, potioneffect1.getAmplifier(), potioneffect1.getIsAmbient(), potioneffect1.doesShowParticles()));
                }
                potions.addAll(this.effects);
                if (potions.isEmpty()) {
                    this.reapplicationDelayMap.clear();
                } else {
                    List list = EntityQueries.entities(this.worldObj, EntityLivingBase.class, Boxes.entityBox(this));
                    if (!list.isEmpty()) {
                        for (EntityLivingBase entitylivingbase : list) {
                            double d1;
                            double d0;
                            double d2;
                            boolean pb = entitylivingbase instanceof EntityParasiteBase;
                            if (this.owner != null) {
                                if (!pb) {
                                    this.owner.attackEntityAsMobMinimum(entitylivingbase, this.owner.getMiniDamage() / 2.0f);
                                }
                                if (!(entitylivingbase instanceof EntityPCosmical)) {
                                    entitylivingbase.attackEntityFrom(DamageSource.DRAGON_BREATH, 10.0f);
                                }
                            }
                            if (pb || this.reapplicationDelayMap.containsKey(entitylivingbase) || !entitylivingbase.canBeHitWithPotion() || !((d2 = (d0 = entitylivingbase.posX - this.posX) * d0 + (d1 = entitylivingbase.posZ - this.posZ) * d1) <= (double)(f * f))) continue;
                            this.reapplicationDelayMap.put((Entity)entitylivingbase, this.ticksExisted + this.reapplicationDelay);
                            for (PotionEffect potioneffect : potions) {
                                if (potioneffect.getPotion().isInstant()) {
                                    potioneffect.getPotion().affectEntity((Entity)this, (Entity)this.getOwner(), entitylivingbase, potioneffect.getAmplifier(), 0.5);
                                    continue;
                                }
                                SRPPotions.applyStackPotion(potioneffect.getPotion(), entitylivingbase, potioneffect.getDuration(), potioneffect.getAmplifier());
                            }
                            if (this.radiusOnUse == 0.0f) continue;
                            if ((f += this.radiusOnUse) < 0.5f) {
                                this.setDead();
                                return;
                            }
                            this.setRadius(f, this.getHeight());
                        }
                    }
                }
            }
        }
    }

    public void setRadiusOnUse(float radiusOnUseIn) {
        this.radiusOnUse = radiusOnUseIn;
    }

    public void setRadiusPerTick(float radiusPerTickIn) {
        this.radiusPerTick = radiusPerTickIn;
    }

    public void setWaitTime(int waitTimeIn) {
        this.waitTime = waitTimeIn;
    }

    public void setOwner(@Nullable EntityParasiteBase ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUniqueID();
    }

    @Nullable
    public EntityParasiteBase getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUniqueId != null && this.worldObj instanceof WorldServer && (entity = ((WorldServer)this.worldObj).getEntityFromUuid(this.ownerUniqueId)) instanceof EntityParasiteBase) {
            this.owner = (EntityParasiteBase)entity;
        }
        return this.owner;
    }

    public void handleHealthUpdate(byte id) {
        if (id == 77) {
            this.effectParticles = 1;
        } else {
            super.handleHealthUpdate(id);
        }
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        EnumParticleTypes enumparticletypes;
        this.ticksExisted = compound.getInteger("Age");
        this.duration = compound.getInteger("Duration");
        this.waitTime = compound.getInteger("WaitTime");
        this.reapplicationDelay = compound.getInteger("ReapplicationDelay");
        this.radiusOnUse = compound.getFloat("RadiusOnUse");
        this.radiusPerTick = compound.getFloat("RadiusPerTick");
        this.setRadius(compound.getFloat("Radius"), compound.getFloat("heig"));
        this.ownerUniqueId = compound.getUniqueId("OwnerUUID");
        if (compound.hasKey("Particle", 8) && (enumparticletypes = EnumParticleTypes.getByName((String)compound.getString("Particle"))) != null) {
            this.setParticle(enumparticletypes);
            this.setParticleParam1(compound.getInteger("ParticleParam1"));
            this.setParticleParam2(compound.getInteger("ParticleParam2"));
        }
        if (compound.hasKey("Color", 99)) {
            this.setColor(compound.getInteger("Color"));
        }
        if (compound.hasKey("Potion", 8)) {
            this.setPotion(PotionUtils.getPotionTypeFromNBT((NBTTagCompound)compound));
        }
        if (compound.hasKey("Effects", 9)) {
            NBTTagList nbttaglist = compound.getTagList("Effects", 10);
            this.effects.clear();
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT((NBTTagCompound)nbttaglist.getCompoundTagAt(i));
                if (potioneffect == null) continue;
                this.addEffect(potioneffect);
            }
        }
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("Age", this.ticksExisted);
        compound.setInteger("Duration", this.duration);
        compound.setInteger("WaitTime", this.waitTime);
        compound.setInteger("ReapplicationDelay", this.reapplicationDelay);
        compound.setFloat("RadiusOnUse", this.radiusOnUse);
        compound.setFloat("RadiusPerTick", this.radiusPerTick);
        compound.setFloat("Radius", this.getRadius());
        compound.setFloat("heig", this.getHeight());
        compound.setString("Particle", this.getParticle().getParticleName());
        compound.setInteger("ParticleParam1", this.getParticleParam1());
        compound.setInteger("ParticleParam2", this.getParticleParam2());
        if (this.ownerUniqueId != null) {
            compound.setUniqueId("OwnerUUID", this.ownerUniqueId);
        }
        if (this.potion != PotionTypes.EMPTY && this.potion != null) {
            compound.setString("Potion", ((ResourceLocation)PotionType.REGISTRY.getNameForObject((Object)this.potion)).toString());
        }
        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            for (PotionEffect potioneffect : this.effects) {
                nbttaglist.appendTag((NBTBase)potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }
            compound.setTag("Effects", (NBTBase)nbttaglist);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b, double d0, double d1, double d2, double d3, double d4, double d5) {
        ParticleSpawner.spawnParticle(particleType, d0, d1, d2, d3, d4, d5, r, g, b);
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        if (RADIUS.equals(key)) {
            this.setRadius(this.getRadius(), this.getHeight());
        }
        super.notifyDataManagerChange(key);
    }

    public EnumPushReaction getMobilityFlag() {
        return EnumPushReaction.IGNORE;
    }
}

