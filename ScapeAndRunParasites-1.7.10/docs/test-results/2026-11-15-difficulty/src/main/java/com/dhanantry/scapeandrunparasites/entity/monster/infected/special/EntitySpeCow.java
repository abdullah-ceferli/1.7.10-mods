/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAreaEffectCloud
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.IRangedAttackMob
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.init.Biomes
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected.special;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAssimara;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySpeCow
extends EntityPAssimara
implements IRangedAttackMob {
    int vomit;
    private int lastSkin = -1;

    public EntitySpeCow(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 1.4f);
        this.canModRender = 1;
        this.type = (byte)11;
        this.fuseTime = 40;
    }

    @Override
    public int getIDSpawn() {
        return 13;
    }

    @Override
    public int getParasiteIDRegister() {
        return 322;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infcowCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 1, 16));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIAttackMeleeRangeSwitch(this, 3.0f));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAttackRangedStatus(this, 1.0, 200, 7.0f, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.MARCOW_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.MARCOW_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue((double)0.2f);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.MARCOW_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.MARCOW_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.assimaraFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            int skin = this.getSkin();
            if (skin != this.lastSkin) {
                if (skin == 1) {
                    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.MARCOW_HEALTH * 0.75);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.26000000298023224);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.MARCOW_ATTACK_DAMAGE * 1.25);
                    if (this.getHealth() > this.getMaxHealth()) {
                        this.setHealth(this.getMaxHealth());
                    }
                } else {
                    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.MARCOW_HEALTH);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue((double)0.2f);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.MARCOW_ATTACK_DAMAGE);
                    if (this.isPotionActive(SRPPotions.RAGE_E)) {
                        this.removePotionEffect(SRPPotions.RAGE_E);
                    }
                }
                this.lastSkin = skin;
            }
            if (!(skin != 1 || this.isPotionActive(SRPPotions.RAGE_E) && this.getActivePotionEffect(SRPPotions.RAGE_E).getDuration() >= 40)) {
                this.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 400, 0, false, false));
            }
        }
        if (this.worldObj.isRemote && this.vomit > 0) {
            --this.vomit;
            for (int i = 0; i < 6; ++i) {
                Vec3d vec3d = this.getLook(1.0f);
                double bon = 1.2;
                double offsetX = this.posX + vec3d.xCoord * bon;
                double offsetY = this.posY + (double)this.getEyeHeight() - 0.2;
                double offsetZ = this.posZ + vec3d.zCoord * bon;
                double motionX = (double)(-MathHelper.sin((float)(this.rotationYaw * (float)Math.PI / 180.0f))) * 0.2;
                double motionZ = (double)MathHelper.cos((float)(this.rotationYaw * (float)Math.PI / 180.0f)) * 0.2;
                double motionY = 0.01 + this.rand.nextDouble() * 0.1;
                double spreadFactor = 0.25;
                this.spawnParticles(SRPEnumParticle.GCLOUD, 123, 0, 196, offsetX, offsetY, offsetZ, motionX += (this.rand.nextDouble() - 0.5) * spreadFactor, motionY, motionZ += (this.rand.nextDouble() - 0.5) * spreadFactor);
            }
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!this.worldObj.isRemote) {
            Biome biome = this.worldObj.getBiome(EntityQueries.position(this));
            boolean desert = biome == Biomes.desert || biome == Biomes.desertHills || biome == Biomes.MUTATED_DESERT;
            double chance = desert ? 0.8 : 0.01;
            this.canChangeVariant = true;
            if (this.rand.nextDouble() < chance) {
                this.setSkin(1);
            } else {
                this.setSkin(0);
            }
            this.canChangeVariant = false;
        }
        return livingdata;
    }

    @Override
    public int getMaxManualVariants() {
        return 2;
    }

    @Override
    protected void selfExplode() {
        super.selfExplode();
        ParasiteSummon.spawnM(this, new String[]{SRPConfigMobs.infcowmob}, 0, false, this.getCustomNameTag());
    }

    public float getEyeHeight() {
        return 1.3f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.INFECTEDCOW_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDCOW_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.INFECTEDCOW_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15f, 1.0f);
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        this.worldObj.setEntityState((Entity)this, (byte)100);
        Vec3d vec3d = this.getLook(1.0f);
        double bon = 4.5;
        EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.worldObj, this.posX + vec3d.xCoord * bon, this.posY, this.posZ + vec3d.zCoord * bon);
        entityareaeffectcloud.setRadius(3.0f);
        entityareaeffectcloud.setDuration(100);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.VOMIT_E, 300, 0, false, true));
        entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.VIRA_E, 300, 20, false, true));
        entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.weakness, 300, 20, false, true));
        entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.hunger, 300, 20, false, true));
        entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.CORRO_E, 300, 20, false, true));
        this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
        this.lookAt((Entity)target);
        this.setWait(60);
    }

    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 100) {
            this.vomit = 40;
        } else {
            super.handleHealthUpdate(id);
        }
    }
}

