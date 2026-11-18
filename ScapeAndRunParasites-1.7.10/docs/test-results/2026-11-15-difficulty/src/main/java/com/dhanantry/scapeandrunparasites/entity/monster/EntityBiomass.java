/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBiomass
extends EntityParasiteBase {
    private int fuse;
    private EntityParasiteBase entityin;
    private EntityLivingBase target;
    private String parasite;
    private int point;
    private static final DataParameter<Float> STAGE = EntityDataManager.createKey(EntityBiomass.class, (DataSerializer)DataSerializers.FLOAT);
    private boolean payfather;
    private float currentWidth;
    private float currentHeight;
    private float capWidth;
    private float capHeight;
    private float growWidth;
    private float growHeight;

    public EntityBiomass(World worldIn) {
        super(worldIn);
        this.setSize(0.98f, 0.98f);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
        this.fuse = 777;
        this.preventEntitySpawning = true;
        this.type = (byte)100;
        this.currentWidth = 0.98f;
        this.currentHeight = 0.98f;
        this.capWidth = 0.98f;
        this.capHeight = 0.98f;
    }

    public EntityBiomass(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.setPosition(x, y, z);
        float f = (float)(Math.random() * (Math.PI * 2));
        this.motionX = -((float)Math.sin(f)) * 0.02f;
        this.motionY = 0.2f;
        this.motionZ = -((float)Math.cos(f)) * 0.02f;
        this.setFuse(80);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    public EntityBiomass(World worldIn, EntityParasiteBase fatherEntity, float stage, EntityLivingBase target, boolean payfather) {
        this(worldIn);
        this.entityin = fatherEntity;
        this.setStage(stage);
        this.target = target;
        this.payfather = payfather;
    }

    public EntityBiomass(World worldIn, EntityParasiteBase entityin, EntityLivingBase target) {
        this(worldIn);
        this.entityin = entityin;
        this.target = target;
        this.setStage(1.0f);
        this.payfather = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 205;
    }

    public void fall(float distance, float damageMultiplier) {
        if (distance >= 18.0f) {
            super.fall(distance, damageMultiplier);
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(0.0);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(STAGE, (Object)Float.valueOf(0.0f));
    }

    @Override
    public void onLivingUpdate() {
        block34: {
            block33: {
                super.onLivingUpdate();
                if (!this.worldObj.isRemote) {
                    if (!this.isDead && this.getHealth() > 0.0f) {
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.setHealth(this.getHealth() + 0.1f);
                        }
                    } else {
                        return;
                    }
                }
                if (!this.worldObj.isRemote) break block33;
                switch ((int)this.getStage()) {
                    case 1: {
                        if (this.ticksExisted % 10 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 2: {
                        if (this.ticksExisted % 5 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 3: {
                        if (this.ticksExisted % 3 != 0) break;
                        for (int i = 0; i <= 2; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 4: {
                        if (this.ticksExisted % 10 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 5: {
                        if (this.ticksExisted % 10 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break block34;
                    }
                    case 6: {
                        if (this.ticksExisted % 5 != 0) break;
                        for (int i = 0; i <= 1; ++i) {
                            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                        }
                        break;
                    }
                }
                break block34;
            }
            if (this.ticksExisted % 10 == 0) {
                this.applyCOTH(2);
            }
        }
        if (this.onGround) {
            --this.fuse;
        }
        if (this.ticksExisted >= 200) {
            --this.fuse;
        }
        if (this.fuse <= 0) {
            this.particleStatus((byte)7);
            this.setDead();
            if (!this.worldObj.isRemote) {
                this.explode();
                this.worldObj.setEntityState((Entity)this, (byte)18);
            }
        } else if (this.onGround) {
            switch ((int)this.getStage()) {
                case 1: {
                    this.growWidth += 0.005f;
                    this.growHeight += 0.006f;
                    this.worldObj.setEntityState((Entity)this, (byte)11);
                    break;
                }
                case 2: {
                    this.growWidth += 0.007f;
                    this.growHeight += 0.009f;
                    this.worldObj.setEntityState((Entity)this, (byte)12);
                    break;
                }
                case 3: {
                    this.growWidth += 0.015f;
                    this.growHeight += 0.02f;
                    this.worldObj.setEntityState((Entity)this, (byte)13);
                    break;
                }
                case 4: {
                    this.growWidth += 0.007f;
                    this.growHeight += 0.009f;
                    this.worldObj.setEntityState((Entity)this, (byte)15);
                    break;
                }
                case 5: {
                    this.growWidth += 0.005f;
                    this.growHeight += 0.006f;
                    this.worldObj.setEntityState((Entity)this, (byte)16);
                    break;
                }
                case 6: {
                    this.growWidth += 0.007f;
                    this.growHeight += 0.009f;
                    this.worldObj.setEntityState((Entity)this, (byte)17);
                }
            }
        }
    }

    public void applyCOTH(int r) {
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), (double)r);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob == this || mob instanceof EntityParasiteBase) continue;
            mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 200, 1, false, false));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public float getGrowW() {
        return this.growWidth;
    }

    @SideOnly(value=Side.CLIENT)
    public float getGrowHeight() {
        return this.growHeight;
    }

    private boolean explode() {
        this.applyCOTH(2);
        this.playSound(SRPSounds.FLESH_PRIMITIVE, 1.0f, 1.0f);
        if (this.fuse == 777) {
            return false;
        }
        if (this.entityin == null) {
            return this.explode2();
        }
        EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(this.parasite), (World)this.worldObj);
        if (entityout == null) {
            return false;
        }
        entityout.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue((double)(16.0f + (this.getStage() - 1.0f) * 8.0f));
        entityout.setLocationAndAngles(this.posX, this.posY, this.posZ, this.entityin.rotationYaw, this.entityin.rotationPitch);
        entityout.onInitialSpawn(DifficultyAccess.at(this.entityin.worldObj, EntityQueries.position(entityout)), (IEntityLivingData)null);
        if (SRPConfigSystems.rageEnable) {
            entityout.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1200, 1, false, false));
        }
        entityout.addPotionEffect(new PotionEffect(SRPPotions.DEBAR_E, 120000, 1, false, false));
        if (this.isBurning()) {
            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
            entityout.setFire(8);
        }
        this.entityin.worldObj.spawnEntityInWorld((Entity)entityout);
        if (this.target != null) {
            entityout.setAttackTarget(this.target);
        }
        if (this.payfather && this.entityin instanceof EntityCanSummon) {
            EntityCanSummon father = (EntityCanSummon)((Object)this.entityin);
            father.setActualParasites(this.point);
            father.addID(entityout.getEntityId(), this.point);
        }
        return true;
    }

    private boolean explode2() {
        if (this.fuse == 777 || this.parasite == null) {
            return false;
        }
        EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(this.parasite), (World)this.worldObj);
        if (entityout == null) {
            return false;
        }
        entityout.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue((double)(16.0f + (this.getStage() - 1.0f) * 8.0f));
        entityout.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        entityout.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(entityout)), (IEntityLivingData)null);
        if (SRPConfigSystems.rageEnable) {
            entityout.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1200, 1, false, false));
        }
        entityout.addPotionEffect(new PotionEffect(SRPPotions.DEBAR_E, 120000, 1, false, false));
        if (this.isBurning()) {
            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
            entityout.setFire(8);
        }
        this.worldObj.spawnEntityInWorld((Entity)entityout);
        if (this.target != null) {
            entityout.setAttackTarget(this.target);
        }
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    private void explotionParticles() {
        double b = 0.0;
        switch ((int)this.getStage()) {
            case 2: {
                b = 0.3;
                break;
            }
            case 3: {
                b = 0.6;
                break;
            }
            case 4: {
                b = 0.3;
                break;
            }
            case 6: {
                b = 0.3;
            }
        }
        for (int i = 0; i <= 10; ++i) {
            double d0 = (float)this.posX + this.rand.nextFloat();
            double d1 = (double)((float)this.posY + this.rand.nextFloat()) + 1.0;
            double d2 = (float)this.posZ + this.rand.nextFloat();
            double d3 = d0 - this.posX;
            double d4 = d1 - this.posY;
            double d5 = d2 - this.posZ;
            double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5 / (d6 / 1.0 + 0.1);
            d3 = d3 * (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.7f)) * b;
            d4 = d4 * d7 * (b * 0.5);
            d5 = d5 * d7 * b;
            this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0, d0, d1, d2, d3, d4, d5);
        }
    }

    public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        this.motionX = xSpeedIn * (Math.random() * 2.0 - 1.0);
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn * (Math.random() * 2.0 - 1.0);
    }

    public void setSizeGrow(float width, float height, float capWidht, float capHeight) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.capWidth = capWidht;
        this.capHeight = capHeight;
    }

    public void setFuse(int fuseIn) {
        this.fuse = fuseIn;
    }

    public void setParasite(String in, int points) {
        this.parasite = in;
        this.point = points;
    }

    public int getFuse() {
        return this.fuse;
    }

    @Override
    public int getSkin() {
        return super.getSkin();
    }

    @Override
    public void setSkin(int texture) {
        super.setSkin(texture);
    }

    public float getStage() {
        return ((Float)EntityDataManager.of(this).get(STAGE)).floatValue();
    }

    public void setStage(float in) {
        EntityDataManager.of(this).set(STAGE, (Object)Float.valueOf(in));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setShort("Fuse", (short)this.getFuse());
        if (this.entityin != null) {
            compound.setInteger("parasitefather", this.entityin.getEntityId());
        }
        if (this.target != null) {
            compound.setInteger("parasitetarget", this.target.getEntityId());
        }
        if (this.parasite != null) {
            compound.setString("parasiteparasite", this.parasite);
        }
        compound.setInteger("parasitepoint", this.point);
        compound.setBoolean("parasitepayfather", this.payfather);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFuse(compound.getShort("Fuse"));
        if (compound.hasKey("parasitefather", 99) && this.worldObj.getEntityByID(compound.getInteger("parasitefather")) instanceof EntityParasiteBase) {
            this.entityin = (EntityParasiteBase)this.worldObj.getEntityByID(compound.getInteger("parasitefather"));
        }
        if (compound.hasKey("parasitetarget", 99)) {
            this.target = (EntityLivingBase)this.worldObj.getEntityByID(compound.getInteger("parasitetarget"));
        }
        if (compound.hasKey("parasiteparasite", 8)) {
            this.parasite = compound.getString("parasiteparasite").toString();
        }
        if (compound.hasKey("parasitepoint", 99)) {
            this.point = compound.getInteger("parasitepoint");
        }
        if (compound.hasKey("parasitepayfather", 99)) {
            this.payfather = compound.getBoolean("parasitepayfather");
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 11) {
            this.growWidth += 0.005f;
            this.growHeight += 0.006f;
        } else if (id == 12) {
            this.growWidth += 0.01f;
            this.growHeight += 0.012f;
        } else if (id == 13) {
            this.growWidth += 0.001f;
            this.growHeight += 0.001f;
        } else if (id == 15) {
            this.growWidth += 0.01f;
            this.growHeight += 0.012f;
        } else if (id == 16) {
            this.growWidth += 0.005f;
            this.growHeight += 0.006f;
        } else if (id == 17) {
            this.growWidth += 0.01f;
            this.growHeight += 0.012f;
        } else if (id == 18) {
            this.explotionParticles();
        } else {
            super.handleHealthUpdate(id);
        }
    }
}

