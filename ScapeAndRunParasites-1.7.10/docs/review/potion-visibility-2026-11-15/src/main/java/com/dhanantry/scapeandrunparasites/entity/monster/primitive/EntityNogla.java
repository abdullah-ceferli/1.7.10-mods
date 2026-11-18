/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.monster.EntitySlime
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIDiveBomb;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumChatFormatting;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityNogla
extends EntityPPrimitive {
    private EntityHitbox head;
    private static final byte STATUS_RICARDO_BURST = 77;
    private boolean ricardoBaseCaptured = false;
    private double ricardoBaseMaxHealth;
    private double ricardoBaseArmor;
    private double ricardoBaseToughness;
    private static final DataParameter<Boolean> RICARDO_BALD = EntityDataManager.createKey(EntityNogla.class, (DataSerializer)DataSerializers.BOOLEAN);
    private int attacking;
    private double targetX;
    private double targetY;
    private double targetZ;
    private boolean skillCharge;

    public EntityNogla(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 2.6f);
        this.stepHeight = 1.0f;
        this.skillCharge = false;
        this.head = new EntityHitbox((EntityLiving)this, 1.6f, 0.9f, 2.2f, 0.7f, 0.8f, 1.25f);
        this.hitboxes = new EntityHitbox[]{this.head};
    }

    @Override
    public int getParasiteIDRegister() {
        return 10;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.095));
        this.tasks.addTask(1, (EntityAIBase)new EntityAIDiveBomb((EntityLiving)this, 1200, 60, 3.8, 3.0f));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 40, 32, 8, true, 1));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 2, 16));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIEvade(this, 55, 10, 4.0));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(RICARDO_BALD, (Object)false);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.NOGLA_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.NOGLA_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.31234);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.NOGLA_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.NOGLA_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.primitiveFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.ticksExisted % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityNoglaAdapted(this.worldObj), true, true);
        }
        if (!this.worldObj.isRemote && this.isRicardoBald() && this.ticksExisted % 20 == 0) {
            this.applyPermanentRicardoRage();
        }
        if (this.worldObj.isRemote && this.isRicardoVariant() && (this.ticksExisted & 3) == 0) {
            for (int i = 0; i < 3; ++i) {
                double x = this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width * 1.6;
                double y = this.posY + this.rand.nextDouble() * ((double)this.height * 0.9);
                double z = this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width * 1.6;
                float r = 1.0f;
                float g = 0.25f;
                float b = 0.75f;
                this.worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, x, y, z, (double)r, (double)g, (double)b, new int[0]);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("RicardoBald", ((Boolean)EntityDataManager.of(this).get(RICARDO_BALD)).booleanValue());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        EntityDataManager.of(this).set(RICARDO_BALD, (Object)compound.getBoolean("RicardoBald"));
        if (!this.worldObj.isRemote && this.isRicardoBald()) {
            this.applyPermanentRicardoRage();
        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        super.collideWithEntity(entityIn);
        if (this.worldObj.isRemote) {
            return;
        }
        if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
        }
    }

    public float getEyeHeight() {
        return 2.4f;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (this.isRicardoVariant()) {
                this.entityDropItem(new ItemStack(SRPItems.bookofvengeance, 1), 0.0f);
            }
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            if (entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase)entityIn).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.poison, 100));
                switch (this.getSkin()) {
                    case 5: {
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
                        break;
                    }
                    case 6: {
                        SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 40, 0);
                    }
                }
            }
            if (!this.worldObj.isRemote && entityIn instanceof EntitySlime && this.getRNG().nextFloat() < 0.1f) {
                double dx = entityIn.posX - this.posX;
                double dz = entityIn.posZ - this.posZ;
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len < 1.0E-4) {
                    dx = this.getRNG().nextDouble() - 0.5;
                    dz = this.getRNG().nextDouble() - 0.5;
                    len = Math.sqrt(dx * dx + dz * dz);
                }
                double horizontal = 4.75;
                double yBoost = 1.2;
                entityIn.addVelocity((dx /= len) * horizontal, yBoost, (dz /= len) * horizontal);
                entityIn.velocityChanged = true;
            }
            if (this.isRicardoVariant() && !this.worldObj.isRemote) {
                this.worldObj.newExplosion((Entity)this, entityIn.posX, entityIn.posY, entityIn.posZ, 1.8f, false, false);
            }
        }
        return flag;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.worldObj.isRemote && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityNoglaAdapted(this.worldObj), true, true);
        }
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.NOGLA_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.NOGLA_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.NOGLA_DEATH;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.noglaOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP, 0.15f, 1.0f);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.rand.nextInt(4)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                    break;
                }
                case 2: {
                    this.setSkin(1);
                    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.NOGLA_HEALTH * 0.5);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.NOGLA_ATTACK_DAMAGE * 1.5);
                    this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue());
                    break;
                }
                case 3: {
                    this.setSkin(7);
                }
            }
        }
        if (!this.worldObj.isRemote) {
            this.updateRicardoAttributes();
        }
        return floo;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        switch (id) {
            case 100: {
                for (int i = 0; i <= 1; ++i) {
                    this.spawnParticles(EnumParticleTypes.FLAME);
                }
                break;
            }
            default: {
                super.handleHealthUpdate(id);
            }
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillCharge;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillCharge = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    public boolean isRicardoVariant() {
        if (!SRPConfigMobs.noglaRicardoVariantEnabled) {
            return false;
        }
        if (!this.hasCustomInventoryName()) {
            return false;
        }
        String raw = EnumChatFormatting.getTextWithoutFormattingCodes((String)this.getCustomNameTag());
        if (raw == null) {
            return false;
        }
        return raw.trim().toLowerCase(Locale.ROOT).equals("ricardo");
    }

    public boolean isRicardoBald() {
        return this.isRicardoVariant() && (Boolean)EntityDataManager.of(this).get(RICARDO_BALD) != false;
    }

    private void setRicardoBald(boolean bald) {
        EntityDataManager.of(this).set(RICARDO_BALD, (Object)bald);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!this.worldObj.isRemote && this.isRicardoVariant() && !this.isRicardoBald() && !stack.isEmpty() && stack.getItem() == Items.shears) {
            this.setRicardoBald(true);
            this.applyPermanentRicardoRage();
            this.grantRicardoShearAdvancement(player);
            stack.damageItem(1, (EntityLivingBase)player);
            this.worldObj.playSound(null, this.posX, this.posY, this.posZ, SRPSounds.NOGLA_HURT, SoundCategory.HOSTILE, 2.0f, 0.65f);
            this.worldObj.playSound(null, this.posX, this.posY, this.posZ, SRPSounds.NOGLA_GROWL, SoundCategory.HOSTILE, 2.5f, 0.75f);
            ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX, this.posY + 1.2, this.posZ, 40, 0.6, 0.8, 0.6, 0.05, new int[0]);
            this.worldObj.setEntityState((Entity)this, (byte)77);
            return true;
        }
        return super.processInteract(player, hand);
    }

    private void grantRicardoShearAdvancement(EntityPlayer player) {
        if (this.worldObj.isRemote || !(player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP playerMP = (EntityPlayerMP)player;
        Advancement advancement = playerMP.getServerForPlayer().getAdvancementManager().getAdvancement(new ResourceLocation("srparasites", "tricked_me_did_you"));
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = playerMP.getAdvancements().getProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemaningCriteria()) {
                playerMP.getAdvancements().grantCriterion(advancement, criterion);
            }
        }
    }

    private void applyPermanentRicardoRage() {
        if (!this.isRicardoBald()) {
            return;
        }
        this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 1200, 0, false, true));
    }

    private void captureRicardoBaselines() {
        if (this.ricardoBaseCaptured) {
            return;
        }
        this.ricardoBaseMaxHealth = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
        this.ricardoBaseArmor = this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue();
        this.ricardoBaseToughness = this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getBaseValue();
        this.ricardoBaseCaptured = true;
    }

    private void updateRicardoAttributes() {
        this.captureRicardoBaselines();
        double baseSpeed = this.isRicardoVariant() ? 0.45 : 0.3;
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(baseSpeed);
        if (this.isRicardoVariant()) {
            double MAX_HP = 3763.0;
            double ARMOR_NORMAL = 32.0;
            double ARMOR_ENRAGE = 40.0;
            double SPEED_ENRAGE = 0.58;
            double ENRAGE_PCT = 0.25;
            double BERSERK_PCT = 0.1;
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(3763.0);
            double cur = this.getHealth();
            double max = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
            if (max <= 0.0) {
                max = 3763.0;
            }
            double pct = max > 0.0 ? cur / max : 1.0;
            boolean enrage = pct <= 0.25;
            boolean berserk = pct <= 0.1;
            double armorNow = 32.0;
            double speedNow = baseSpeed;
            if (enrage) {
                armorNow = 40.0;
                speedNow = 0.58;
                this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.regeneration, 60, 1, false, true));
                this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.damageBoost, 60, 0, false, true));
                if (!this.worldObj.isRemote && this.ticksExisted % 20 == 0) {
                    ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY + 1.2, this.posZ, 12, 0.35, 0.6, 0.35, 0.02, new int[0]);
                }
            }
            if (berserk) {
                armorNow = Math.max(armorNow, 44.0);
                speedNow = Math.max(speedNow, 0.62);
                this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.damageBoost, 60, 1, false, true));
                this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.regeneration, 60, 2, false, true));
            }
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(armorNow);
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(this.ricardoBaseToughness);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(speedNow);
            if (this.getHealth() > (float)max) {
                this.setHealth((float)max);
            } else if (this.getHealth() <= 0.0f) {
                this.setHealth((float)max);
            }
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.ricardoBaseMaxHealth);
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(this.ricardoBaseArmor);
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(this.ricardoBaseToughness);
            if (this.getHealth() > (float)this.ricardoBaseMaxHealth) {
                this.setHealth((float)this.ricardoBaseMaxHealth);
            }
        }
    }

    public void setMinecartName(String name) {
        boolean wasRicardo = this.isRicardoVariant();
        super.setMinecartName(name);
        System.out.println("[SRP] noglaRicardoVariantEnabled=" + SRPConfigMobs.noglaRicardoVariantEnabled + " name=" + this.getCustomNameTag());
        if (!this.worldObj.isRemote) {
            if (!SRPConfigMobs.noglaRicardoVariantEnabled) {
                this.updateRicardoAttributes();
                return;
            }
            boolean isNowRicardo = this.isRicardoVariant();
            this.updateRicardoAttributes();
            if (isNowRicardo && !wasRicardo) {
                this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue());
                this.worldObj.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.HOSTILE, 8.0f, 1.0f);
                this.worldObj.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.HOSTILE, 4.0f, 1.0f);
                ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY + 1.2, this.posZ, 40, 0.6, 0.8, 0.6, 0.1, new int[0]);
                this.worldObj.setEntityState((Entity)this, (byte)77);
            }
        }
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.charge();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void charge() {
        ++this.attacking;
        this.miniCapA = true;
        if (this.attacking < 20) {
            EntityLivingBase entitylivingbase;
            this.worldObj.setEntityState((Entity)this, (byte)100);
            if (this.attacking == 2) {
                float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f + 2.0f;
                this.playSound(this.getHurtSound(DamageSource.generic), 4.0f, v);
            }
            if ((entitylivingbase = this.getAttackTarget()) == null || !this.onGround || this.isInWater() || entitylivingbase.posY > this.posY && entitylivingbase.onGround) {
                this.skillCharge = true;
                this.attacking = 0;
                this.miniCapA = false;
                this.setParasiteStatus(0);
                return;
            }
            if (!entitylivingbase.isEntityAlive()) {
                this.skillCharge = true;
                this.attacking = 0;
                this.miniCapA = false;
                this.setParasiteStatus(0);
                return;
            }
            if (this.attacking <= 19) {
                double dis = this.getDistanceToEntity((Entity)entitylivingbase);
                this.setParasiteStatus(3);
                this.getNavigator().clearPathEntity();
                this.targetX = this.posX + 15.0 * (entitylivingbase.posX - this.posX) / dis;
                this.targetY = this.posY + 15.0 * (entitylivingbase.posY - this.posY) / dis;
                this.targetZ = this.posZ + 15.0 * (entitylivingbase.posZ - this.posZ) / dis;
            }
        }
        if (this.attacking == 20) {
            this.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, 2.5);
        }
        if (this.attacking >= 20) {
            for (EntityLivingBase mob : EntityQueries.entities(this.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(this), 2.0, 0.0, 2.0))) {
                if (mob == this || mob instanceof EntityParasiteBase) continue;
                float f = (float)MathHelper.atan2((double)(mob.posZ - this.posZ), (double)(mob.posX - this.posX));
                EntityDamage damage = new EntityDamage(this.worldObj, mob.posX, mob.posY, mob.posZ, f, (EntityLivingBase)this, 1.0f, false, 0.5f);
                this.worldObj.spawnEntityInWorld((Entity)damage);
            }
        }
        this.skillBreakBlocks();
        if (!this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
        if (this.attacking >= 60 && this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
            this.attacking = 0;
            this.miniCapA = false;
            this.skillCharge = true;
            this.setParasiteStatus(2);
        }
    }
}

