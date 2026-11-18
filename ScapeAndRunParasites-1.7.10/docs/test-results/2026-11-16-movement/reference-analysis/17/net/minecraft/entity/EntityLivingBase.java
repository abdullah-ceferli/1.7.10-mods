/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class EntityLivingBase
extends Entity {
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.3f, 2).setSaved(false);
    private BaseAttributeMap attributeMap;
    private final CombatTracker _combatTracker = new CombatTracker(this);
    private final HashMap activePotionsMap = new HashMap();
    private final ItemStack[] previousEquipment = new ItemStack[5];
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;
    public float prevHealth;
    public int hurtTime;
    public int maxHurtTime;
    public float attackedAtYaw;
    public int deathTime;
    public int attackTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float field_70769_ao;
    public float field_70770_ap;
    public float renderYawOffset;
    public float prevRenderYawOffset;
    public float rotationYawHead;
    public float prevRotationYawHead;
    public float jumpMovementFactor = 0.02f;
    protected EntityPlayer attackingPlayer;
    protected int recentlyHit;
    protected boolean dead;
    protected int entityAge;
    protected float field_70768_au;
    protected float field_110154_aX;
    protected float field_70764_aw;
    protected float field_70763_ax;
    protected float field_70741_aB;
    protected int scoreValue;
    protected float lastDamage;
    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;
    protected int newPosRotationIncrements;
    protected double newPosX;
    protected double newPosY;
    protected double newPosZ;
    protected double newRotationYaw;
    protected double newRotationPitch;
    private boolean potionsNeedUpdate = true;
    private EntityLivingBase entityLivingToAttack;
    private int revengeTimer;
    private EntityLivingBase lastAttacker;
    private int lastAttackerTime;
    private float landMovementFactor;
    private int jumpTicks;
    private float field_110151_bq;
    private static final String __OBFID = "CL_00001549";

    public EntityLivingBase(World p_i1594_1_) {
        super(p_i1594_1_);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.field_70770_ap = (float)(Math.random() + 1.0) * 0.01f;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.field_70769_ao = (float)Math.random() * 12398.0f;
        this.rotationYawHead = this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
        this.stepHeight = 0.5f;
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(7, 0);
        this.dataWatcher.addObject(8, (byte)0);
        this.dataWatcher.addObject(9, (byte)0);
        this.dataWatcher.addObject(6, Float.valueOf(1.0f));
    }

    protected void applyEntityAttributes() {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
        if (!this.isAIEnabled()) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1f);
        }
    }

    @Override
    protected void updateFallState(double p_70064_1_, boolean p_70064_3_) {
        if (!this.isInWater()) {
            this.handleWaterMovement();
        }
        if (p_70064_3_ && this.fallDistance > 0.0f) {
            int k;
            int j;
            int i = MathHelper.floor_double(this.posX);
            Block block = this.worldObj.getBlock(i, j = MathHelper.floor_double(this.posY - (double)0.2f - (double)this.yOffset), k = MathHelper.floor_double(this.posZ));
            if (block.getMaterial() == Material.air) {
                int l = this.worldObj.getBlock(i, j - 1, k).getRenderType();
                if (l == 11 || l == 32 || l == 21) {
                    block = this.worldObj.getBlock(i, j - 1, k);
                }
            } else if (!this.worldObj.isRemote && this.fallDistance > 3.0f) {
                this.worldObj.playAuxSFX(2006, i, j, k, MathHelper.ceiling_float_int(this.fallDistance - 3.0f));
            }
            block.onFallenUpon(this.worldObj, i, j, k, this, this.fallDistance);
        }
        super.updateFallState(p_70064_1_, p_70064_3_);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    public void onEntityUpdate() {
        boolean flag;
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("livingEntityBaseTick");
        if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.inWall, 1.0f);
        }
        if (this.isImmuneToFire() || this.worldObj.isRemote) {
            this.extinguish();
        }
        boolean bl = flag = this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.disableDamage;
        if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water)) {
            if (!(this.canBreatheUnderwater() || this.isPotionActive(Potion.waterBreathing.id) || flag)) {
                this.setAir(this.decreaseAirSupply(this.getAir()));
                if (this.getAir() == -20) {
                    this.setAir(0);
                    for (int i = 0; i < 8; ++i) {
                        float f = this.rand.nextFloat() - this.rand.nextFloat();
                        float f1 = this.rand.nextFloat() - this.rand.nextFloat();
                        float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                        this.worldObj.spawnParticle("bubble", this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ);
                    }
                    this.attackEntityFrom(DamageSource.drown, 2.0f);
                }
            }
            if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity != null && this.ridingEntity.shouldDismountInWater(this)) {
                this.mountEntity(null);
            }
        } else {
            this.setAir(300);
        }
        if (this.isEntityAlive() && this.isWet()) {
            this.extinguish();
        }
        this.prevCameraPitch = this.cameraPitch;
        if (this.attackTime > 0) {
            --this.attackTime;
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
            --this.hurtResistantTime;
        }
        if (this.getHealth() <= 0.0f) {
            this.onDeathUpdate();
        }
        if (this.recentlyHit > 0) {
            --this.recentlyHit;
        } else {
            this.attackingPlayer = null;
        }
        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
            this.lastAttacker = null;
        }
        if (this.entityLivingToAttack != null) {
            if (!this.entityLivingToAttack.isEntityAlive()) {
                this.setRevengeTarget(null);
            } else if (this.ticksExisted - this.revengeTimer > 100) {
                this.setRevengeTarget(null);
            }
        }
        this.updatePotionEffects();
        this.field_70763_ax = this.field_70764_aw;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
    }

    public boolean isChild() {
        return false;
    }

    protected void onDeathUpdate() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            int i;
            if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                int j;
                for (i = this.getExperiencePoints(this.attackingPlayer); i > 0; i -= j) {
                    j = EntityXPOrb.getXPSplit(i);
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }
            this.setDead();
            for (i = 0; i < 20; ++i) {
                double d2 = this.rand.nextGaussian() * 0.02;
                double d0 = this.rand.nextGaussian() * 0.02;
                double d1 = this.rand.nextGaussian() * 0.02;
                this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d2, d0, d1);
            }
        }
    }

    protected boolean func_146066_aG() {
        return !this.isChild();
    }

    protected int decreaseAirSupply(int p_70682_1_) {
        int j = EnchantmentHelper.getRespiration(this);
        return j > 0 && this.rand.nextInt(j + 1) > 0 ? p_70682_1_ : p_70682_1_ - 1;
    }

    protected int getExperiencePoints(EntityPlayer p_70693_1_) {
        return 0;
    }

    protected boolean isPlayer() {
        return false;
    }

    public Random getRNG() {
        return this.rand;
    }

    public EntityLivingBase getAITarget() {
        return this.entityLivingToAttack;
    }

    public int func_142015_aE() {
        return this.revengeTimer;
    }

    public void setRevengeTarget(EntityLivingBase p_70604_1_) {
        this.entityLivingToAttack = p_70604_1_;
        this.revengeTimer = this.ticksExisted;
        ForgeHooks.onLivingSetAttackTarget(this, p_70604_1_);
    }

    public EntityLivingBase getLastAttacker() {
        return this.lastAttacker;
    }

    public int getLastAttackerTime() {
        return this.lastAttackerTime;
    }

    public void setLastAttacker(Entity p_130011_1_) {
        this.lastAttacker = p_130011_1_ instanceof EntityLivingBase ? (EntityLivingBase)p_130011_1_ : null;
        this.lastAttackerTime = this.ticksExisted;
    }

    public int getAge() {
        return this.entityAge;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setFloat("HealF", this.getHealth());
        p_70014_1_.setShort("Health", (short)Math.ceil(this.getHealth()));
        p_70014_1_.setShort("HurtTime", (short)this.hurtTime);
        p_70014_1_.setShort("DeathTime", (short)this.deathTime);
        p_70014_1_.setShort("AttackTime", (short)this.attackTime);
        p_70014_1_.setFloat("AbsorptionAmount", this.getAbsorptionAmount());
        for (ItemStack itemstack : this.getLastActiveItems()) {
            if (itemstack == null) continue;
            this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
        }
        p_70014_1_.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));
        for (ItemStack itemstack : this.getLastActiveItems()) {
            if (itemstack == null) continue;
            this.attributeMap.applyAttributeModifiers(itemstack.getAttributeModifiers());
        }
        if (!this.activePotionsMap.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            for (PotionEffect potioneffect : this.activePotionsMap.values()) {
                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }
            p_70014_1_.setTag("ActiveEffects", nbttaglist);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        this.setAbsorptionAmount(p_70037_1_.getFloat("AbsorptionAmount"));
        if (p_70037_1_.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
            SharedMonsterAttributes.func_151475_a(this.getAttributeMap(), p_70037_1_.getTagList("Attributes", 10));
        }
        if (p_70037_1_.hasKey("ActiveEffects", 9)) {
            NBTTagList nbttaglist = p_70037_1_.getTagList("ActiveEffects", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound1);
                if (potioneffect == null) continue;
                this.activePotionsMap.put(potioneffect.getPotionID(), potioneffect);
            }
        }
        if (p_70037_1_.hasKey("HealF", 99)) {
            this.setHealth(p_70037_1_.getFloat("HealF"));
        } else {
            NBTBase nbtbase = p_70037_1_.getTag("Health");
            if (nbtbase == null) {
                this.setHealth(this.getMaxHealth());
            } else if (nbtbase.getId() == 5) {
                this.setHealth(((NBTTagFloat)nbtbase).func_150288_h());
            } else if (nbtbase.getId() == 2) {
                this.setHealth(((NBTTagShort)nbtbase).func_150289_e());
            }
        }
        this.hurtTime = p_70037_1_.getShort("HurtTime");
        this.deathTime = p_70037_1_.getShort("DeathTime");
        this.attackTime = p_70037_1_.getShort("AttackTime");
    }

    protected void updatePotionEffects() {
        boolean flag1;
        Iterator iterator = this.activePotionsMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer integer = (Integer)iterator.next();
            PotionEffect potioneffect = (PotionEffect)this.activePotionsMap.get(integer);
            if (!potioneffect.onUpdate(this)) {
                if (this.worldObj.isRemote) continue;
                iterator.remove();
                this.onFinishedPotionEffect(potioneffect);
                continue;
            }
            if (potioneffect.getDuration() % 600 != 0) continue;
            this.onChangedPotionEffect(potioneffect, false);
        }
        if (this.potionsNeedUpdate) {
            if (!this.worldObj.isRemote) {
                if (this.activePotionsMap.isEmpty()) {
                    this.dataWatcher.updateObject(8, (byte)0);
                    this.dataWatcher.updateObject(7, 0);
                    this.setInvisible(false);
                } else {
                    int i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
                    this.dataWatcher.updateObject(8, (byte)(PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0));
                    this.dataWatcher.updateObject(7, i);
                    this.setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }
            this.potionsNeedUpdate = false;
        }
        int i = this.dataWatcher.getWatchableObjectInt(7);
        boolean bl = flag1 = this.dataWatcher.getWatchableObjectByte(8) > 0;
        if (i > 0) {
            boolean flag = false;
            if (!this.isInvisible()) {
                flag = this.rand.nextBoolean();
            } else {
                boolean bl2 = flag = this.rand.nextInt(15) == 0;
            }
            if (flag1) {
                flag &= this.rand.nextInt(5) == 0;
            }
            if (flag && i > 0) {
                double d0 = (double)(i >> 16 & 0xFF) / 255.0;
                double d1 = (double)(i >> 8 & 0xFF) / 255.0;
                double d2 = (double)(i >> 0 & 0xFF) / 255.0;
                this.worldObj.spawnParticle(flag1 ? "mobSpellAmbient" : "mobSpell", this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - (double)this.yOffset, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, d0, d1, d2);
            }
        }
    }

    public void clearActivePotions() {
        Iterator iterator = this.activePotionsMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer integer = (Integer)iterator.next();
            PotionEffect potioneffect = (PotionEffect)this.activePotionsMap.get(integer);
            if (this.worldObj.isRemote) continue;
            iterator.remove();
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    public Collection getActivePotionEffects() {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(int p_82165_1_) {
        return this.activePotionsMap.containsKey(p_82165_1_);
    }

    public boolean isPotionActive(Potion p_70644_1_) {
        return this.activePotionsMap.containsKey(p_70644_1_.id);
    }

    public PotionEffect getActivePotionEffect(Potion p_70660_1_) {
        return (PotionEffect)this.activePotionsMap.get(p_70660_1_.id);
    }

    public void addPotionEffect(PotionEffect p_70690_1_) {
        if (this.isPotionApplicable(p_70690_1_)) {
            if (this.activePotionsMap.containsKey(p_70690_1_.getPotionID())) {
                ((PotionEffect)this.activePotionsMap.get(p_70690_1_.getPotionID())).combine(p_70690_1_);
                this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(p_70690_1_.getPotionID()), true);
            } else {
                this.activePotionsMap.put(p_70690_1_.getPotionID(), p_70690_1_);
                this.onNewPotionEffect(p_70690_1_);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect p_70687_1_) {
        int i;
        return this.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD || (i = p_70687_1_.getPotionID()) != Potion.regeneration.id && i != Potion.poison.id;
    }

    public boolean isEntityUndead() {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    public void removePotionEffectClient(int p_70618_1_) {
        this.activePotionsMap.remove(p_70618_1_);
    }

    public void removePotionEffect(int p_82170_1_) {
        PotionEffect potioneffect = (PotionEffect)this.activePotionsMap.remove(p_82170_1_);
        if (potioneffect != null) {
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    protected void onNewPotionEffect(PotionEffect p_70670_1_) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[p_70670_1_.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), p_70670_1_.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_) {
        this.potionsNeedUpdate = true;
        if (p_70695_2_ && !this.worldObj.isRemote) {
            Potion.potionTypes[p_70695_1_.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), p_70695_1_.getAmplifier());
            Potion.potionTypes[p_70695_1_.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), p_70695_1_.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[p_70688_1_.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), p_70688_1_.getAmplifier());
        }
    }

    public void heal(float p_70691_1_) {
        if ((p_70691_1_ = ForgeEventFactory.onLivingHeal(this, p_70691_1_)) <= 0.0f) {
            return;
        }
        float f1 = this.getHealth();
        if (f1 > 0.0f) {
            this.setHealth(f1 + p_70691_1_);
        }
    }

    public final float getHealth() {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float p_70606_1_) {
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(p_70606_1_, 0.0f, this.getMaxHealth())));
    }

    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        String s;
        if (ForgeHooks.onLivingAttack(this, p_70097_1_, p_70097_2_)) {
            return false;
        }
        if (this.isEntityInvulnerable()) {
            return false;
        }
        if (this.worldObj.isRemote) {
            return false;
        }
        this.entityAge = 0;
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        if (p_70097_1_.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
            return false;
        }
        if ((p_70097_1_ == DamageSource.anvil || p_70097_1_ == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
            this.getEquipmentInSlot(4).damageItem((int)(p_70097_2_ * 4.0f + this.rand.nextFloat() * p_70097_2_ * 2.0f), this);
            p_70097_2_ *= 0.75f;
        }
        this.limbSwingAmount = 1.5f;
        boolean flag = true;
        if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0f) {
            if (p_70097_2_ <= this.lastDamage) {
                return false;
            }
            this.damageEntity(p_70097_1_, p_70097_2_ - this.lastDamage);
            this.lastDamage = p_70097_2_;
            flag = false;
        } else {
            this.lastDamage = p_70097_2_;
            this.prevHealth = this.getHealth();
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(p_70097_1_, p_70097_2_);
            this.maxHurtTime = 10;
            this.hurtTime = 10;
        }
        this.attackedAtYaw = 0.0f;
        Entity entity = p_70097_1_.getEntity();
        if (entity != null) {
            EntityTameable entitywolf;
            if (entity instanceof EntityLivingBase) {
                this.setRevengeTarget((EntityLivingBase)entity);
            }
            if (entity instanceof EntityPlayer) {
                this.recentlyHit = 100;
                this.attackingPlayer = (EntityPlayer)entity;
            } else if (entity instanceof EntityTameable && (entitywolf = (EntityTameable)entity).isTamed()) {
                this.recentlyHit = 100;
                this.attackingPlayer = null;
            }
        }
        if (flag) {
            this.worldObj.setEntityState(this, (byte)2);
            if (p_70097_1_ != DamageSource.drown) {
                this.setBeenAttacked();
            }
            if (entity != null) {
                double d1 = entity.posX - this.posX;
                double d0 = entity.posZ - this.posZ;
                while (d1 * d1 + d0 * d0 < 1.0E-4) {
                    d1 = (Math.random() - Math.random()) * 0.01;
                    d0 = (Math.random() - Math.random()) * 0.01;
                }
                this.attackedAtYaw = (float)(Math.atan2(d0, d1) * 180.0 / Math.PI) - this.rotationYaw;
                this.knockBack(entity, p_70097_2_, d1, d0);
            } else {
                this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.getHealth() <= 0.0f) {
            s = this.getDeathSound();
            if (flag && s != null) {
                this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
            }
            this.onDeath(p_70097_1_);
        } else {
            s = this.getHurtSound();
            if (flag && s != null) {
                this.playSound(s, this.getSoundVolume(), this.getSoundPitch());
            }
        }
        return true;
    }

    public void renderBrokenItemStack(ItemStack p_70669_1_) {
        this.playSound("random.break", 0.8f, 0.8f + this.worldObj.rand.nextFloat() * 0.4f);
        for (int i = 0; i < 5; ++i) {
            Vec3 vec3 = Vec3.createVectorHelper(((double)this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0f);
            vec3.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0f);
            Vec3 vec31 = Vec3.createVectorHelper(((double)this.rand.nextFloat() - 0.5) * 0.3, (double)(-this.rand.nextFloat()) * 0.6 - 0.3, 0.6);
            vec31.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0f);
            vec31.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0f);
            vec31 = vec31.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
            this.worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(p_70669_1_.getItem()), vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05, vec3.zCoord);
        }
    }

    public void onDeath(DamageSource p_70645_1_) {
        if (ForgeHooks.onLivingDeath(this, p_70645_1_)) {
            return;
        }
        Entity entity = p_70645_1_.getEntity();
        EntityLivingBase entitylivingbase = this.func_94060_bK();
        if (this.scoreValue >= 0 && entitylivingbase != null) {
            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }
        if (entity != null) {
            entity.onKillEntity(this);
        }
        this.dead = true;
        this.func_110142_aN().func_94549_h();
        if (!this.worldObj.isRemote) {
            int i = 0;
            if (entity instanceof EntityPlayer) {
                i = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
            }
            this.captureDrops = true;
            this.capturedDrops.clear();
            int j = 0;
            if (this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                this.dropFewItems(this.recentlyHit > 0, i);
                this.dropEquipment(this.recentlyHit > 0, i);
                if (this.recentlyHit > 0 && (j = this.rand.nextInt(200) - i) < 5) {
                    this.dropRareDrop(j <= 0 ? 1 : 0);
                }
            }
            this.captureDrops = false;
            if (!ForgeHooks.onLivingDrops(this, p_70645_1_, this.capturedDrops, i, this.recentlyHit > 0, j)) {
                for (EntityItem item : this.capturedDrops) {
                    this.worldObj.spawnEntityInWorld(item);
                }
            }
        }
        this.worldObj.setEntityState(this, (byte)3);
    }

    protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {
    }

    public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
            this.isAirBorne = true;
            float f1 = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
            float f2 = 0.4f;
            this.motionX /= 2.0;
            this.motionY /= 2.0;
            this.motionZ /= 2.0;
            this.motionX -= p_70653_3_ / (double)f1 * (double)f2;
            this.motionY += (double)f2;
            this.motionZ -= p_70653_5_ / (double)f1 * (double)f2;
            if (this.motionY > (double)0.4f) {
                this.motionY = 0.4f;
            }
        }
    }

    protected String getHurtSound() {
        return "game.neutral.hurt";
    }

    protected String getDeathSound() {
        return "game.neutral.die";
    }

    protected void dropRareDrop(int p_70600_1_) {
    }

    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
    }

    public boolean isOnLadder() {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlock(i, j, k);
        return ForgeHooks.isLivingOnLadder(block, this.worldObj, i, j, k, this);
    }

    @Override
    public boolean isEntityAlive() {
        return !this.isDead && this.getHealth() > 0.0f;
    }

    @Override
    protected void fall(float p_70069_1_) {
        if ((p_70069_1_ = ForgeHooks.onLivingFall(this, p_70069_1_)) <= 0.0f) {
            return;
        }
        super.fall(p_70069_1_);
        PotionEffect potioneffect = this.getActivePotionEffect(Potion.jump);
        float f1 = potioneffect != null ? (float)(potioneffect.getAmplifier() + 1) : 0.0f;
        int i = MathHelper.ceiling_float_int(p_70069_1_ - 3.0f - f1);
        if (i > 0) {
            this.playSound(this.func_146067_o(i), 1.0f, 1.0f);
            this.attackEntityFrom(DamageSource.fall, i);
            int j = MathHelper.floor_double(this.posX);
            int k = MathHelper.floor_double(this.posY - (double)0.2f - (double)this.yOffset);
            int l = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlock(j, k, l);
            if (block.getMaterial() != Material.air) {
                Block.SoundType soundtype = block.stepSound;
                this.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.5f, soundtype.getPitch() * 0.75f);
            }
        }
    }

    protected String func_146067_o(int p_146067_1_) {
        return p_146067_1_ > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void performHurtAnimation() {
        this.maxHurtTime = 10;
        this.hurtTime = 10;
        this.attackedAtYaw = 0.0f;
    }

    public int getTotalArmorValue() {
        int i = 0;
        for (ItemStack itemstack : this.getLastActiveItems()) {
            if (itemstack == null || !(itemstack.getItem() instanceof ItemArmor)) continue;
            int l = ((ItemArmor)itemstack.getItem()).damageReduceAmount;
            i += l;
        }
        return i;
    }

    protected void damageArmor(float p_70675_1_) {
    }

    protected float applyArmorCalculations(DamageSource p_70655_1_, float p_70655_2_) {
        if (!p_70655_1_.isUnblockable()) {
            int i = 25 - this.getTotalArmorValue();
            float f1 = p_70655_2_ * (float)i;
            this.damageArmor(p_70655_2_);
            p_70655_2_ = f1 / 25.0f;
        }
        return p_70655_2_;
    }

    protected float applyPotionDamageCalculations(DamageSource p_70672_1_, float p_70672_2_) {
        float f1;
        int j;
        int i;
        if (p_70672_1_.isDamageAbsolute()) {
            return p_70672_2_;
        }
        if (this instanceof EntityZombie) {
            // empty if block
        }
        if (this.isPotionActive(Potion.resistance) && p_70672_1_ != DamageSource.outOfWorld) {
            i = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
            j = 25 - i;
            f1 = p_70672_2_ * (float)j;
            p_70672_2_ = f1 / 25.0f;
        }
        if (p_70672_2_ <= 0.0f) {
            return 0.0f;
        }
        i = EnchantmentHelper.getEnchantmentModifierDamage(this.getLastActiveItems(), p_70672_1_);
        if (i > 20) {
            i = 20;
        }
        if (i > 0 && i <= 20) {
            j = 25 - i;
            f1 = p_70672_2_ * (float)j;
            p_70672_2_ = f1 / 25.0f;
        }
        return p_70672_2_;
    }

    protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_) {
        if (!this.isEntityInvulnerable()) {
            if ((p_70665_2_ = ForgeHooks.onLivingHurt(this, p_70665_1_, p_70665_2_)) <= 0.0f) {
                return;
            }
            p_70665_2_ = this.applyArmorCalculations(p_70665_1_, p_70665_2_);
            float f1 = p_70665_2_ = this.applyPotionDamageCalculations(p_70665_1_, p_70665_2_);
            p_70665_2_ = Math.max(p_70665_2_ - this.getAbsorptionAmount(), 0.0f);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - p_70665_2_));
            if (p_70665_2_ != 0.0f) {
                float f2 = this.getHealth();
                this.setHealth(f2 - p_70665_2_);
                this.func_110142_aN().func_94547_a(p_70665_1_, f2, p_70665_2_);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - p_70665_2_);
            }
        }
    }

    public CombatTracker func_110142_aN() {
        return this._combatTracker;
    }

    public EntityLivingBase func_94060_bK() {
        return this._combatTracker.func_94550_c() != null ? this._combatTracker.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null));
    }

    public final float getMaxHealth() {
        return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    public final int getArrowCountInEntity() {
        return this.dataWatcher.getWatchableObjectByte(9);
    }

    public final void setArrowCountInEntity(int p_85034_1_) {
        this.dataWatcher.updateObject(9, (byte)p_85034_1_);
    }

    private int getArmSwingAnimationEnd() {
        return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    public void swingItem() {
        Item item;
        ItemStack stack = this.getHeldItem();
        if (stack != null && stack.getItem() != null && (item = stack.getItem()).onEntitySwing(this, stack)) {
            return;
        }
        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;
            if (this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 2) {
            this.limbSwingAmount = 1.5f;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.maxHurtTime = 10;
            this.hurtTime = 10;
            this.attackedAtYaw = 0.0f;
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            this.attackEntityFrom(DamageSource.generic, 0.0f);
        } else if (p_70103_1_ == 3) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            this.setHealth(0.0f);
            this.onDeath(DamageSource.generic);
        } else {
            super.handleHealthUpdate(p_70103_1_);
        }
    }

    @Override
    protected void kill() {
        this.attackEntityFrom(DamageSource.outOfWorld, 4.0f);
    }

    protected void updateArmSwingProgress() {
        int i = this.getArmSwingAnimationEnd();
        if (this.isSwingInProgress) {
            ++this.swingProgressInt;
            if (this.swingProgressInt >= i) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        } else {
            this.swingProgressInt = 0;
        }
        this.swingProgress = (float)this.swingProgressInt / (float)i;
    }

    public IAttributeInstance getEntityAttribute(IAttribute p_110148_1_) {
        return this.getAttributeMap().getAttributeInstance(p_110148_1_);
    }

    public BaseAttributeMap getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new ServersideAttributeMap();
        }
        return this.attributeMap;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public abstract ItemStack getHeldItem();

    public abstract ItemStack getEquipmentInSlot(int var1);

    @Override
    public abstract void setCurrentItemOrArmor(int var1, ItemStack var2);

    @Override
    public void setSprinting(boolean p_70031_1_) {
        super.setSprinting(p_70031_1_);
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (iattributeinstance.getModifier(sprintingSpeedBoostModifierUUID) != null) {
            iattributeinstance.removeModifier(sprintingSpeedBoostModifier);
        }
        if (p_70031_1_) {
            iattributeinstance.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    @Override
    public abstract ItemStack[] getLastActiveItems();

    protected float getSoundVolume() {
        return 1.0f;
    }

    protected float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.5f : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean isMovementBlocked() {
        return this.getHealth() <= 0.0f;
    }

    public void setPositionAndUpdate(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
        this.setLocationAndAngles(p_70634_1_, p_70634_3_, p_70634_5_, this.rotationYaw, this.rotationPitch);
    }

    public void dismountEntity(Entity p_110145_1_) {
        double d0 = p_110145_1_.posX;
        double d1 = p_110145_1_.boundingBox.minY + (double)p_110145_1_.height;
        double d2 = p_110145_1_.posZ;
        int b0 = 1;
        for (int i = -b0; i <= b0; ++i) {
            for (int j = -b0; j < b0; ++j) {
                if (i == 0 && j == 0) continue;
                int k = (int)(this.posX + (double)i);
                int l = (int)(this.posZ + (double)j);
                AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(i, 1.0, j);
                if (!this.worldObj.func_147461_a(axisalignedbb).isEmpty()) continue;
                if (World.doesBlockHaveSolidTopSurface(this.worldObj, k, (int)this.posY, l)) {
                    this.setPositionAndUpdate(this.posX + (double)i, this.posY + 1.0, this.posZ + (double)j);
                    return;
                }
                if (!World.doesBlockHaveSolidTopSurface(this.worldObj, k, (int)this.posY - 1, l) && this.worldObj.getBlock(k, (int)this.posY - 1, l).getMaterial() != Material.water) continue;
                d0 = this.posX + (double)i;
                d1 = this.posY + 1.0;
                d2 = this.posZ + (double)j;
            }
        }
        this.setPositionAndUpdate(d0, d1, d2);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getItemIcon(ItemStack p_70620_1_, int p_70620_2_) {
        return p_70620_1_.getItem().requiresMultipleRenderPasses() ? p_70620_1_.getItem().getIconFromDamageForRenderPass(p_70620_1_.getItemDamage(), p_70620_2_) : p_70620_1_.getIconIndex();
    }

    protected void jump() {
        this.motionY = 0.42f;
        if (this.isPotionActive(Potion.jump)) {
            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
        }
        if (this.isSprinting()) {
            float f = this.rotationYaw * ((float)Math.PI / 180);
            this.motionX -= (double)(MathHelper.sin(f) * 0.2f);
            this.motionZ += (double)(MathHelper.cos(f) * 0.2f);
        }
        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
        double d0;
        if (!(!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying)) {
            d0 = this.posY;
            this.moveFlying(p_70612_1_, p_70612_2_, this.isAIEnabled() ? 0.04f : 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.8f;
            this.motionY *= (double)0.8f;
            this.motionZ *= (double)0.8f;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6f - this.posY + d0, this.motionZ)) {
                this.motionY = 0.3f;
            }
        } else if (!(!this.handleLavaMovement() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying)) {
            d0 = this.posY;
            this.moveFlying(p_70612_1_, p_70612_2_, 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6f - this.posY + d0, this.motionZ)) {
                this.motionY = 0.3f;
            }
        } else {
            float f2 = 0.91f;
            if (this.onGround) {
                f2 = this.worldObj.getBlock((int)MathHelper.floor_double((double)this.posX), (int)(MathHelper.floor_double((double)this.boundingBox.minY) - 1), (int)MathHelper.floor_double((double)this.posZ)).slipperiness * 0.91f;
            }
            float f3 = 0.16277136f / (f2 * f2 * f2);
            float f4 = this.onGround ? this.getAIMoveSpeed() * f3 : this.jumpMovementFactor;
            this.moveFlying(p_70612_1_, p_70612_2_, f4);
            f2 = 0.91f;
            if (this.onGround) {
                f2 = this.worldObj.getBlock((int)MathHelper.floor_double((double)this.posX), (int)(MathHelper.floor_double((double)this.boundingBox.minY) - 1), (int)MathHelper.floor_double((double)this.posZ)).slipperiness * 0.91f;
            }
            if (this.isOnLadder()) {
                boolean flag;
                float f5 = 0.15f;
                if (this.motionX < (double)(-f5)) {
                    this.motionX = -f5;
                }
                if (this.motionX > (double)f5) {
                    this.motionX = f5;
                }
                if (this.motionZ < (double)(-f5)) {
                    this.motionZ = -f5;
                }
                if (this.motionZ > (double)f5) {
                    this.motionZ = f5;
                }
                this.fallDistance = 0.0f;
                if (this.motionY < -0.15) {
                    this.motionY = -0.15;
                }
                boolean bl = flag = this.isSneaking() && this instanceof EntityPlayer;
                if (flag && this.motionY < 0.0) {
                    this.motionY = 0.0;
                }
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            if (this.isCollidedHorizontally && this.isOnLadder()) {
                this.motionY = 0.2;
            }
            this.motionY = !(!this.worldObj.isRemote || this.worldObj.blockExists((int)this.posX, 0, (int)this.posZ) && this.worldObj.getChunkFromBlockCoords((int)((int)this.posX), (int)((int)this.posZ)).isChunkLoaded) ? (this.posY > 0.0 ? -0.1 : 0.0) : (this.motionY -= 0.08);
            this.motionY *= (double)0.98f;
            this.motionX *= (double)f2;
            this.motionZ *= (double)f2;
        }
        this.prevLimbSwingAmount = this.limbSwingAmount;
        d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f6 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0f;
        if (f6 > 1.0f) {
            f6 = 1.0f;
        }
        this.limbSwingAmount += (f6 - this.limbSwingAmount) * 0.4f;
        this.limbSwing += this.limbSwingAmount;
    }

    protected boolean isAIEnabled() {
        return false;
    }

    public float getAIMoveSpeed() {
        return this.isAIEnabled() ? this.landMovementFactor : 0.1f;
    }

    public void setAIMoveSpeed(float p_70659_1_) {
        this.landMovementFactor = p_70659_1_;
    }

    public boolean attackEntityAsMob(Entity p_70652_1_) {
        this.setLastAttacker(p_70652_1_);
        return false;
    }

    public boolean isPlayerSleeping() {
        return false;
    }

    @Override
    public void onUpdate() {
        if (ForgeHooks.onLivingUpdate(this)) {
            return;
        }
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            int i = this.getArrowCountInEntity();
            if (i > 0) {
                if (this.arrowHitTimer <= 0) {
                    this.arrowHitTimer = 20 * (30 - i);
                }
                --this.arrowHitTimer;
                if (this.arrowHitTimer <= 0) {
                    this.setArrowCountInEntity(i - 1);
                }
            }
            for (int j = 0; j < 5; ++j) {
                ItemStack itemstack = this.previousEquipment[j];
                ItemStack itemstack1 = this.getEquipmentInSlot(j);
                if (ItemStack.areItemStacksEqual(itemstack1, itemstack)) continue;
                ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S04PacketEntityEquipment(this.getEntityId(), j, itemstack1));
                if (itemstack != null) {
                    this.attributeMap.removeAttributeModifiers(itemstack.getAttributeModifiers());
                }
                if (itemstack1 != null) {
                    this.attributeMap.applyAttributeModifiers(itemstack1.getAttributeModifiers());
                }
                this.previousEquipment[j] = itemstack1 == null ? null : itemstack1.copy();
            }
            if (this.ticksExisted % 20 == 0) {
                this.func_110142_aN().func_94549_h();
            }
        }
        this.onLivingUpdate();
        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = (float)(d0 * d0 + d1 * d1);
        float f1 = this.renderYawOffset;
        float f2 = 0.0f;
        this.field_70768_au = this.field_110154_aX;
        float f3 = 0.0f;
        if (f > 0.0025000002f) {
            f3 = 1.0f;
            f2 = (float)Math.sqrt(f) * 3.0f;
            f1 = (float)Math.atan2(d1, d0) * 180.0f / (float)Math.PI - 90.0f;
        }
        if (this.swingProgress > 0.0f) {
            f1 = this.rotationYaw;
        }
        if (!this.onGround) {
            f3 = 0.0f;
        }
        this.field_110154_aX += (f3 - this.field_110154_aX) * 0.3f;
        this.worldObj.theProfiler.startSection("headTurn");
        f2 = this.func_110146_f(f1, f2);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0f) {
            this.prevRenderYawOffset -= 360.0f;
        }
        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0f) {
            this.prevRenderYawOffset += 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYawHead - this.prevRotationYawHead < -180.0f) {
            this.prevRotationYawHead -= 360.0f;
        }
        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0f) {
            this.prevRotationYawHead += 360.0f;
        }
        this.worldObj.theProfiler.endSection();
        this.field_70764_aw += f2;
    }

    protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
        boolean flag;
        float f2 = MathHelper.wrapAngleTo180_float(p_110146_1_ - this.renderYawOffset);
        this.renderYawOffset += f2 * 0.3f;
        float f3 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
        boolean bl = flag = f3 < -90.0f || f3 >= 90.0f;
        if (f3 < -75.0f) {
            f3 = -75.0f;
        }
        if (f3 >= 75.0f) {
            f3 = 75.0f;
        }
        this.renderYawOffset = this.rotationYaw - f3;
        if (f3 * f3 > 2500.0f) {
            this.renderYawOffset += f3 * 0.2f;
        }
        if (flag) {
            p_110146_2_ *= -1.0f;
        }
        return p_110146_2_;
    }

    public void onLivingUpdate() {
        if (this.jumpTicks > 0) {
            --this.jumpTicks;
        }
        if (this.newPosRotationIncrements > 0) {
            double d0 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double d1 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double d2 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else if (!this.isClientWorld()) {
            this.motionX *= 0.98;
            this.motionY *= 0.98;
            this.motionZ *= 0.98;
        }
        if (Math.abs(this.motionX) < 0.005) {
            this.motionX = 0.0;
        }
        if (Math.abs(this.motionY) < 0.005) {
            this.motionY = 0.0;
        }
        if (Math.abs(this.motionZ) < 0.005) {
            this.motionZ = 0.0;
        }
        this.worldObj.theProfiler.startSection("ai");
        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0f;
            this.moveForward = 0.0f;
            this.randomYawVelocity = 0.0f;
        } else if (this.isClientWorld()) {
            if (this.isAIEnabled()) {
                this.worldObj.theProfiler.startSection("newAi");
                this.updateAITasks();
                this.worldObj.theProfiler.endSection();
            } else {
                this.worldObj.theProfiler.startSection("oldAi");
                this.updateEntityActionState();
                this.worldObj.theProfiler.endSection();
                this.rotationYawHead = this.rotationYaw;
            }
        }
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");
        if (this.isJumping) {
            if (!this.isInWater() && !this.handleLavaMovement()) {
                if (this.onGround && this.jumpTicks == 0) {
                    this.jump();
                    this.jumpTicks = 10;
                }
            } else {
                this.motionY += (double)0.04f;
            }
        } else {
            this.jumpTicks = 0;
        }
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98f;
        this.moveForward *= 0.98f;
        this.randomYawVelocity *= 0.9f;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");
        if (!this.worldObj.isRemote) {
            this.collideWithNearbyEntities();
        }
        this.worldObj.theProfiler.endSection();
    }

    protected void updateAITasks() {
    }

    protected void collideWithNearbyEntities() {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2f, 0.0, 0.2f));
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity)list.get(i);
                if (!entity.canBePushed()) continue;
                this.collideWithEntity(entity);
            }
        }
    }

    protected void collideWithEntity(Entity p_82167_1_) {
        p_82167_1_.applyEntityCollision(this);
    }

    @Override
    public void updateRidden() {
        super.updateRidden();
        this.field_70768_au = this.field_110154_aX;
        this.field_110154_aX = 0.0f;
        this.fallDistance = 0.0f;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_) {
        this.yOffset = 0.0f;
        this.newPosX = p_70056_1_;
        this.newPosY = p_70056_3_;
        this.newPosZ = p_70056_5_;
        this.newRotationYaw = p_70056_7_;
        this.newRotationPitch = p_70056_8_;
        this.newPosRotationIncrements = p_70056_9_;
    }

    protected void updateAITick() {
    }

    protected void updateEntityActionState() {
        ++this.entityAge;
    }

    public void setJumping(boolean p_70637_1_) {
        this.isJumping = p_70637_1_;
    }

    public void onItemPickup(Entity p_71001_1_, int p_71001_2_) {
        if (!p_71001_1_.isDead && !this.worldObj.isRemote) {
            EntityTracker entitytracker = ((WorldServer)this.worldObj).getEntityTracker();
            if (p_71001_1_ instanceof EntityItem) {
                entitytracker.func_151247_a(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
            if (p_71001_1_ instanceof EntityArrow) {
                entitytracker.func_151247_a(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
            if (p_71001_1_ instanceof EntityXPOrb) {
                entitytracker.func_151247_a(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
        }
    }

    public boolean canEntityBeSeen(Entity p_70685_1_) {
        return this.worldObj.rayTraceBlocks(Vec3.createVectorHelper(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), Vec3.createVectorHelper(p_70685_1_.posX, p_70685_1_.posY + (double)p_70685_1_.getEyeHeight(), p_70685_1_.posZ)) == null;
    }

    @Override
    public Vec3 getLookVec() {
        return this.getLook(1.0f);
    }

    public Vec3 getLook(float p_70676_1_) {
        if (p_70676_1_ == 1.0f) {
            float f1 = MathHelper.cos(-this.rotationYaw * ((float)Math.PI / 180) - (float)Math.PI);
            float f2 = MathHelper.sin(-this.rotationYaw * ((float)Math.PI / 180) - (float)Math.PI);
            float f3 = -MathHelper.cos(-this.rotationPitch * ((float)Math.PI / 180));
            float f4 = MathHelper.sin(-this.rotationPitch * ((float)Math.PI / 180));
            return Vec3.createVectorHelper(f2 * f3, f4, f1 * f3);
        }
        float f1 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * p_70676_1_;
        float f2 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * p_70676_1_;
        float f3 = MathHelper.cos(-f2 * ((float)Math.PI / 180) - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * ((float)Math.PI / 180) - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * ((float)Math.PI / 180));
        float f6 = MathHelper.sin(-f1 * ((float)Math.PI / 180));
        return Vec3.createVectorHelper(f4 * f5, f6, f3 * f5);
    }

    @SideOnly(value=Side.CLIENT)
    public float getSwingProgress(float p_70678_1_) {
        float f1 = this.swingProgress - this.prevSwingProgress;
        if (f1 < 0.0f) {
            f1 += 1.0f;
        }
        return this.prevSwingProgress + f1 * p_70678_1_;
    }

    @SideOnly(value=Side.CLIENT)
    public Vec3 getPosition(float p_70666_1_) {
        if (p_70666_1_ == 1.0f) {
            return Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        }
        double d0 = this.prevPosX + (this.posX - this.prevPosX) * (double)p_70666_1_;
        double d1 = this.prevPosY + (this.posY - this.prevPosY) * (double)p_70666_1_;
        double d2 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_70666_1_;
        return Vec3.createVectorHelper(d0, d1, d2);
    }

    @SideOnly(value=Side.CLIENT)
    public MovingObjectPosition rayTrace(double p_70614_1_, float p_70614_3_) {
        Vec3 vec3 = this.getPosition(p_70614_3_);
        Vec3 vec31 = this.getLook(p_70614_3_);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * p_70614_1_, vec31.yCoord * p_70614_1_, vec31.zCoord * p_70614_1_);
        return this.worldObj.func_147447_a(vec3, vec32, false, false, true);
    }

    public boolean isClientWorld() {
        return !this.worldObj.isRemote;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.85f;
    }

    @Override
    protected void setBeenAttacked() {
        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    @Override
    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void setRotationYawHead(float p_70034_1_) {
        this.rotationYawHead = p_70034_1_;
    }

    public float getAbsorptionAmount() {
        return this.field_110151_bq;
    }

    public void setAbsorptionAmount(float p_110149_1_) {
        if (p_110149_1_ < 0.0f) {
            p_110149_1_ = 0.0f;
        }
        this.field_110151_bq = p_110149_1_;
    }

    public Team getTeam() {
        return null;
    }

    public boolean isOnSameTeam(EntityLivingBase p_142014_1_) {
        return this.isOnTeam(p_142014_1_.getTeam());
    }

    public boolean isOnTeam(Team p_142012_1_) {
        return this.getTeam() != null ? this.getTeam().isSameTeam(p_142012_1_) : false;
    }

    public void curePotionEffects(ItemStack curativeItem) {
        Iterator potionKey = this.activePotionsMap.keySet().iterator();
        if (this.worldObj.isRemote) {
            return;
        }
        while (potionKey.hasNext()) {
            Integer key = (Integer)potionKey.next();
            PotionEffect effect = (PotionEffect)this.activePotionsMap.get(key);
            if (!effect.isCurativeItem(curativeItem)) continue;
            potionKey.remove();
            this.onFinishedPotionEffect(effect);
        }
    }

    public boolean shouldRiderFaceForward(EntityPlayer player) {
        return this instanceof EntityPig;
    }

    public void func_152111_bt() {
    }

    public void func_152112_bu() {
    }
}

