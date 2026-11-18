/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEndGateway
 *  net.minecraft.block.BlockEndPortalFrame
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockPortal
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.MultiPartEntityPart
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityMinecart
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemShield
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.PathNavigateFlying
 *  net.minecraft.pathfinding.PathNavigateGround
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.IHitboxedEntity;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIParasiteFollow;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWanderStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolRangeBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndGateway;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityParasiteBase
extends com.dhanantry.scapeandrunparasites.backport.entity.LegacyEntityMob
implements IHitboxedEntity {
    // 1.7's hurt callback has no DamageSource argument; retain the source for original overrides.
    private DamageSource soundDamageSource = DamageSource.generic;
    @Override protected String getHurtSound() { return getHurtSound(soundDamageSource); }
    protected String getHurtSound(DamageSource source) { return super.getHurtSound(); }

    protected int attackSpeedT;
    protected double killcount = 0.0;
    protected boolean canD = true;
    private int waitInt;
    private boolean canWorkTask;
    private EntityParasiteBase owner;
    private EntityParasiteBase notOwner;
    public boolean canChangeVariant;
    protected float scentHPMultiplier = 2.5f;
    protected int foodRootNumber;
    protected double foodRott;
    public boolean disloNumberTwo = false;
    public boolean disloNumberThree = false;
    public boolean disloNumberFour = false;
    public boolean disloNumberSix = false;
    public boolean disloNumberSeven = false;
    public boolean disloNumberEight = false;
    public boolean disloNumberNine = false;
    public int disloNumberEleven = 0;
    public int disloNumberFifteen = 0;
    public static final DataParameter<Boolean> DISLO15 = EntityDataManager.createKey(EntityParasiteBase.class, (DataSerializer)DataSerializers.BOOLEAN);
    public int disloNumberSixteen = 0;
    public float distanceWalkedOnStepModifiedDislo;
    public int disloNumberSeventeen = 0;
    public boolean disloNumberEighteen = false;
    public int disloNumberNineteen = 0;
    public double disloNumberNineteenValue = 0.0;
    public boolean disloNumberTwentyone = false;
    public boolean disloNumberTwentytwo = false;
    protected boolean geneMindam = true;
    protected boolean geneDamcap = true;
    protected boolean geneLookwall = true;
    protected boolean geneSprinting = true;
    protected boolean geneWaterleap = true;
    protected boolean geneSpecialmove = true;
    protected float genePoisonHealing = 2.5f;
    protected float geneMobHealing = 3.0f;
    protected float geneAttackSpeed = 0.5f;
    private static final DataParameter<Byte> SPECIAL = EntityDataManager.createKey(EntityParasiteBase.class, (DataSerializer)DataSerializers.BYTE);
    private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityParasiteBase.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Byte> SKIN = EntityDataManager.createKey(EntityParasiteBase.class, (DataSerializer)DataSerializers.BYTE);
    private static final DataParameter<Boolean> COLD_L = EntityDataManager.createKey(EntityParasiteBase.class, (DataSerializer)DataSerializers.BOOLEAN);
    public int srpTicks;
    protected int lastActiveTime;
    protected int timeSinceIgnited;
    protected int fuseTime = 40;
    protected byte canModRender = 0;
    protected int attackCooldown;
    protected byte madeRng = (byte)-1;
    protected byte paraGeneration = 0;
    public boolean spawnedByColo;
    protected byte type = 0;
    public int damageCap = 1;
    protected float MiniDamage = 0.0f;
    protected float miniCap;
    protected boolean miniCapA = false;
    protected byte phaseCreated;
    protected byte levelCreated;
    protected EntityAIJumping jumpT = new EntityAIJumping((EntityLiving)this);
    protected EntityAIWait wait = new EntityAIWait();
    public EntityAIParasiteFollow folow = new EntityAIParasiteFollow(this, 1.3, 16.0, 6.0, true);
    public EntityAIWanderStatus aiWander = new EntityAIWanderStatus(this, 1.0, 120, 0.001f, true);
    protected int oneMindDeathValue;
    protected float foodSteal;
    private float aniticks;
    private boolean still;
    private int stillTicks;
    private int canBeStored;
    private int dodFatherID = 0;
    public boolean canSpawnSpawn;
    protected int valueEvDeath;
    protected int liquidLeap;
    protected float cothSpread;
    private EntityLivingBase last;
    public int targetNewCool;
    protected float blockH;
    protected int BGheight;
    protected int BGrange;
    protected boolean SkillBGflag;
    private ArrayList<String> inbBlockName = new ArrayList();
    private ArrayList<Integer> inbBlockNumber = new ArrayList();
    protected int attacking;
    protected double targetX;
    protected double targetZ;
    protected float leapMotionY;
    protected double jumpSpeed;
    protected int jumpR;
    protected boolean SkillLeapFlag;
    public EntityHitbox[] hitboxes;
    public float prevHitboxScale;

    public EntityParasiteBase(World worldIn) {
        super(worldIn);
        this.tasks.addTask(0, (EntityAIBase)this.wait);
        this.tasks.addTask(5, (EntityAIBase)this.aiWander);
        this.tasks.addTask(6, (EntityAIBase)this.folow);
        this.tasks.addTask(5, (EntityAIBase)this.jumpT);
        this.canBeStored = 0;
        this.attackCooldown = 0;
        this.canWorkTask = true;
        this.srpTicks = 0;
        this.madeRng = (byte)-1;
        this.foodSteal = 0.001f;
        this.spawnedByColo = false;
        this.SkillBGflag = false;
        this.SkillLeapFlag = false;
        this.canChangeVariant = false;
        this.oneMindDeathValue = 1;
        this.foodRootNumber = 0;
        this.foodRott = 0.0;
        this.attackSpeedT = 20;
        this.canSpawnSpawn = true;
        this.still = false;
        this.miniCap = SRPConfig.miniminiCap;
        this.setScentHPMultiplier(2.5f);
    }

    public void setCreatedPhase(int in, int ud) {
        this.phaseCreated = (byte)in;
        this.levelCreated = (byte)ud;
    }

    public void setScentHPMultiplier(float scentHPMultiplier) {
        this.scentHPMultiplier = scentHPMultiplier;
    }

    public float getScentHPMultiplier() {
        return this.scentHPMultiplier;
    }

    public double scentHostHPCalculation() {
        return this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue() * (double)this.getScentHPMultiplier();
    }

    public abstract int getParasiteIDRegister();

    public int getAttackSpeed() {
        return (int)((float)this.attackSpeedT * this.geneAttackSpeed);
    }

    public void applyBonuses(SRPSaveData sabe, World world) {
        this.setCreatedPhase(sabe.getEvolutionPhase(world.provider.dimensionId), sabe.getDeveLevel());
        int id = world.provider.dimensionId;
        this.applyGene(sabe.getGeneModi(id), sabe.getGeneModi2(id));
        if (!(SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution)) {
            return;
        }
        this.phaseCreated = sabe.getEvolutionPhase(world.provider.dimensionId);
        if (this.phaseCreated >= SRPConfigSystems.evolutionAssimilatedDehiding) {
            this.targetTasks.addTask(5, new EntityAINearestAttackableTargetStatus<EntityVillager>(this, EntityVillager.class, true));
        }
        if (this.phaseCreated >= SRPConfigSystems.evolutionTotalKill) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLivingBase>(this, EntityLivingBase.class, 0, false, false, entity -> !(entity instanceof EntityPlayer) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite), SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
    }

    public void applyGene(boolean[] kool, float[] goon) {
        if (!SRPConfigSystems.generationUse) {
            return;
        }
        this.geneMindam = kool[0];
        this.geneDamcap = kool[1];
        this.geneLookwall = kool[2];
        this.geneSprinting = kool[3];
        this.geneWaterleap = kool[4];
        this.geneSpecialmove = kool[5];
        this.genePoisonHealing = goon[0];
        this.geneMobHealing = goon[1];
        this.geneAttackSpeed = goon[2];
    }

    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(SPECIAL, (Object)((byte)0));
        EntityDataManager.of(this).register(SELFE, (Object)(-1));
        EntityDataManager.of(this).register(SKIN, (Object)((byte)0));
        EntityDataManager.of(this).register(COLD_L, (Object)false);
        EntityDataManager.of(this).register(DISLO15, (Object)false);
    }

    public void cannotDespawn(boolean in) {
        this.canD = in;
    }

    public boolean canDespawn() {
        return this.canD;
    }

    public void setApplyColonyB(boolean in) {
        this.spawnedByColo = in;
    }

    public boolean getApplyColonyB() {
        return this.spawnedByColo;
    }

    public void onLivingUpdate() {
        ++this.srpTicks;
        if (this.isAIDisabled()) {
            return;
        }
        if (this.getWait() > 0) {
            int j1 = this.getWait() - 1;
            if (j1 == 0) {
                this.setParasiteStatus(0);
            }
            this.setWait(j1);
        } else {
            super.onLivingUpdate();
        }
        if (this.hitboxes != null && this.hitboxes.length > 0) {
            this.updateHitboxes();
            this.resetHitboxes(this.getRenderSizeModifier());
        }
        if (this.worldObj.isRemote) {
            if (this.isPotionActive(SRPPotions.PARATE_E)) {
                if (this.worldObj.rand.nextInt(3) != 0) {
                    return;
                }
                for (int i = 0; i <= 1; ++i) {
                    this.spawnParticles(SRPEnumParticle.RHAPPY, 0, 0, 0);
                }
            }
            if (this.srpTicks > 20) {
                this.srpTicks = 0;
            }
        } else {
            EntityLivingBase tgt;
            if (this.targetNewCool >= 0) {
                --this.targetNewCool;
            }
            if ((tgt = this.getAttackTarget()) instanceof EntityPlayer && tgt.isPotionActive(SRPPotions.THE_SIGN_E)) {
                super.setAttackTarget(null);
                this.setRevengeTarget(null);
                this.setLastAttacker(null);
                this.getNavigator().clearPathEntity();
            }
            if (this.disloNumberEleven >= 0) {
                --this.disloNumberEleven;
            }
            if (this.disloNumberFifteen >= 0) {
                --this.disloNumberFifteen;
                if (this.disloNumberFifteen <= 0) {
                    EntityDataManager.of(this).set(DISLO15, (Object)false);
                }
            }
            if (this.disloNumberSixteen >= 0) {
                this.distanceWalkedOnStepModified = -10.0f;
                --this.disloNumberSixteen;
                if (this.disloNumberSixteen <= 0) {
                    this.distanceWalkedOnStepModified = this.distanceWalkedOnStepModifiedDislo + 500.0f;
                }
            }
            if (this.disloNumberSeventeen >= 0) {
                --this.disloNumberSeventeen;
            }
            if (this.disloNumberNineteen >= 0) {
                --this.disloNumberNineteen;
            }
            this.handleParasiteStatus();
            if (this.srpTicks == 1) {
                this.handleWater(true);
                if (this.disloNumberNineteen > 0) {
                    this.killcount += this.disloNumberNineteenValue;
                }
            }
            if (this.srpTicks > 20) {
                this.srpTicks = 0;
                if (!this.inbBlockName.isEmpty() && this.rand.nextInt(50) == 0) {
                    this.spawnCyst();
                }
                if (this.getAttackTarget() instanceof EntityPlayer) {
                    this.fearPlayer(this.getAttackTarget());
                }
                if (SRPConfigSystems.useEvolution && this.killcount >= 0.0) {
                    switch (this.phaseCreated) {
                        case 1: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusOne;
                            break;
                        }
                        case 2: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusTwo;
                            break;
                        }
                        case 3: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusThree;
                            break;
                        }
                        case 4: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusFour;
                            break;
                        }
                        case 5: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusFive;
                            break;
                        }
                        case 6: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusSix;
                            break;
                        }
                        case 7: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusSeven;
                            break;
                        }
                        case 8: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusEight;
                            break;
                        }
                        case 9: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusNine;
                            break;
                        }
                        case 10: {
                            this.killcount += SRPConfigSystems.phaseKillCountPlusTen;
                        }
                    }
                } else if (this.worldObj.getDifficulty() == EnumDifficulty.HARD && this.killcount >= 0.0) {
                    this.killcount += SRPConfig.killcountplus;
                }
                this.placeNidus();
            }
            this.handleWater(false);
        }
        if (this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
            ++this.stillTicks;
        } else {
            this.canBeStored = 55;
            this.stillTicks = 0;
            this.still = false;
        }
        if (this.stillTicks > 25) {
            this.still = true;
        }
        this.aniticks = this.still ? (this.aniticks += 0.0012f) : 0.0f;
        if (this.canBeStored > 0) {
            --this.canBeStored;
        }
    }

    protected void handleWater(boolean check) {
        if (check && BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockLiquid && this.getAttackTarget() != null) {
            ++this.liquidLeap;
            if (this.liquidLeap > 4) {
                this.liquidLeap = 4;
            }
        }
        if (this.liquidLeap >= 1) {
            if (this.geneWaterleap) {
                double h = 0.1;
                double str = 0.5;
                this.getNavigator().clearPathEntity();
                EntityLivingBase entitylivingbase = this.getAttackTarget();
                if (entitylivingbase != null) {
                    Block bl = BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock();
                    if (!bl.isPassable((IBlockAccess)this.worldObj, EntityQueries.position(this))) {
                        h = 0.3;
                        str = 1.0;
                    }
                    --this.liquidLeap;
                    double dd0 = entitylivingbase.posX - this.posX;
                    double dd1 = entitylivingbase.posZ - this.posZ;
                    float f = MathHelper.sqrt_double((double)(dd0 * dd0 + dd1 * dd1));
                    this.motionX += dd0 / (double)f * str * (double)0.8f + this.motionX * (double)0.2f;
                    this.motionZ += dd1 / (double)f * str * (double)0.8f + this.motionZ * (double)0.2f;
                    this.motionY = h;
                    this.lookAt((Entity)entitylivingbase);
                }
            } else {
                --this.liquidLeap;
            }
        }
    }

    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2) {
                if (this.getAttackTarget() != null) {
                    if (!this.getAttackTarget().isEntityAlive()) {
                        this.setAttackTarget(null);
                        this.setParasiteStatus(0);
                    } else if (this.srpTicks == 10 && this.rand.nextInt(10) == 0) {
                        double follo = this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
                        if (this.getDistanceSqToEntity((Entity)this.getAttackTarget()) > follo * follo) {
                            this.setAttackTarget(null);
                            this.setParasiteStatus(0);
                        }
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setAttackTarget(null);
                }
            }
        }
    }

    protected void fearPlayer(EntityLivingBase player) {
    }

    public boolean checkPositionWander(double x, double y, double z) {
        return true;
    }

    protected void placeNidus() {
        if (SRPConfigSystems.evolutionNests < this.getPhaseCreated() && SRPConfigSystems.deveNestsUse < this.getLevelCreated()) {
            return;
        }
        if (SRPConfigSystems.useEvolution && this.phaseCreated <= 0) {
            return;
        }
        if (this.worldObj.rand.nextInt(350) != 0 || this.type < 11) {
            return;
        }
        if (this.killcount < 10.0) {
            return;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), SRPConfig.nexussivFollow);
        List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
        int collec = 0;
        for (EntityParasiteBase mob : moblist) {
            int idP = mob.getParasiteIDRegister();
            if (idP == 73 || idP == 77 || idP == 78 || idP == 79) {
                return;
            }
            collec += (int)mob.getKillC();
        }
        if (collec < SRPConfig.reinforcerNidusStart) {
            return;
        }
        double d0 = (float)this.posX + this.worldObj.rand.nextFloat();
        double d1 = (float)this.posY + this.worldObj.rand.nextFloat();
        double d2 = (float)this.posZ + this.worldObj.rand.nextFloat();
        double d3 = d0 - this.posX;
        double d4 = d1 - this.posY;
        double d5 = d2 - this.posZ;
        double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d4 = d4 * d7 * 2.0;
        EntityGore bomb = new EntityGore(this.worldObj);
        bomb.setType((byte)11);
        bomb.copyLocationAndAnglesFrom((Entity)this);
        bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.1, 0.5);
        this.worldObj.spawnEntityInWorld((Entity)bomb);
    }

    public void setDodFatherID(int in) {
        this.dodFatherID = in;
    }

    public boolean getGeneMod(int id) {
        switch (id) {
            case 0: {
                return this.geneMindam;
            }
            case 1: {
                return this.geneDamcap;
            }
            case 2: {
                return this.geneLookwall;
            }
            case 3: {
                return this.geneSprinting;
            }
            case 4: {
                return this.geneWaterleap;
            }
            case 5: {
                return this.geneSpecialmove;
            }
        }
        return false;
    }

    protected void updateAITasks() {
        super.updateAITasks();
        if (this.getWait() > 0) {
            int j1 = this.getWait() - 1;
            if (j1 == 0) {
                this.setParasiteStatus(0);
            }
            this.setWait(j1);
        }
    }

    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        this.soundDamageSource = source;
        if (this.worldObj.isRemote) {
            return false;
        }
        this.canBeStored = 55;
        if (this.madeRng == -1) {
            this.madeRng = (byte)this.rand.nextInt(2);
            if (this.madeRng == 0) {
                this.worldObj.setEntityState((Entity)this, (byte)40);
            }
        }
        if (source == DamageSource.magic && this.isPotionActive(MobEffects.poison) && amount == 1.0f) {
            this.heal(1.0f * this.genePoisonHealing);
            return false;
        }
        if (this.disloNumberTwentyone && !this.isBurning() && source != DamageSource.outOfWorld) {
            return super.attackEntityFrom(source, 0.0f);
        }
        if (source == DamageSource.outOfWorld) {
            return super.attackEntityFrom(source, amount);
        }
        if (source == DamageSource.inWall) {
            this.skillBreakBlocks();
        }
        if (this.disloNumberSeven && this.rand.nextInt(5) == 0) {
            this.particleStatus((byte)8);
        }
        if (this.disloNumberEight && this.rand.nextInt(5) == 0) {
            this.particleStatus((byte)11);
        }
        if (this.disloNumberNine && this.rand.nextInt(5) == 0) {
            this.particleStatus((byte)13);
        }
        if (this.rand.nextDouble() < 0.1 && this.getHealth() > 0.0f) {
            this.attackEntityFromEffects(1, 1);
        }
        if (source.getSourceOfDamage() != null && source.getSourceOfDamage() instanceof EntityPlayer) {
            EntityPlayer playerIn = (EntityPlayer)source.getSourceOfDamage();
            if (this.disloNumberSix) {
                SRPSaveData data = SRPSaveData.get(this.worldObj, 32);
                int count = data.getCurrentCode(this.worldObj.provider.dimensionId, 6);
                if (count >= 1) {
                    playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).damageItem(count, (EntityLivingBase)playerIn);
                    playerIn.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).damageItem(count, (EntityLivingBase)playerIn);
                } else {
                    this.disloNumberSix = false;
                }
            }
            if (playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() instanceof WeaponToolMeleeBase) {
                return super.attackEntityFrom(source, amount);
            }
        }
        if (source == DamageSource.inFire || source == DamageSource.onFire) {
            if (!this.isImmuneToFire) {
                if (this.rand.nextInt(5) == 0 && !this.isPotionActive(SRPPotions.RAGE_E)) {
                    this.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 200, 1, false, false));
                }
                return super.attackEntityFrom(source, this.getSkin() == 120 ? amount * (SRPConfig.firemultyplier - SRPConfig.firemultyplier / 2.0f) : amount * SRPConfig.firemultyplier);
            }
            return false;
        }
        if (this.notOwner != null) {
            if (this.notOwner.isEntityAlive()) {
                PotionEffect pivot = this.getActivePotionEffect(SRPPotions.PIVOT_E);
                if (pivot != null) {
                    float redirected = (float)(pivot.getAmplifier() + 1) * SRPConfigSystems.pivotDamageRHost;
                    redirected = Math.min(redirected, 0.95f);
                    float remaining = 1.0f - redirected;
                    this.notOwner.attackEntityFrom(source, amount * redirected);
                    return super.attackEntityFrom(source, amount * remaining);
                }
                this.removePotionEffect(SRPPotions.PIVOT_E);
                this.notOwner = null;
            } else {
                this.notOwner = null;
            }
        }
        boolean flagWeak = SRPConfig.parasiteWeakToMobs.length > 0 || SRPConfig.parasiteWeakToItems.length > 0 || SRPConfig.parasiteWeakToElse.length > 0;
        boolean flagCap = this.damageCap > 1 && this.geneDamcap;
        String damageName = "";
        int naniDesu = 0;
        if (flagWeak || flagCap) {
            if (source.getSourceOfDamage() instanceof EntityPlayer) {
                damageName = RegistryNames.get(((EntityPlayer)Objects.requireNonNull(source.getEntity())).getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem()).toString();
                naniDesu = 1;
            } else if (source.getSourceOfDamage() instanceof EntityLivingBase) {
                damageName = EntityList.getKey((Entity)source.getSourceOfDamage()).toString();
                naniDesu = 2;
            } else {
                damageName = source.damageType;
                naniDesu = 3;
            }
        }
        if (flagWeak) {
            switch (naniDesu) {
                case 1: {
                    int i;
                    for (i = 0; i < SRPConfig.parasiteWeakToItems.length; ++i) {
                        String[] here;
                        if (SRPConfig.parasiteWeakToItems[i] == null || !(here = SRPConfig.parasiteWeakToItems[i].split(";"))[0].equals(damageName)) continue;
                        amount *= Float.parseFloat(here[1]);
                    }
                    break;
                }
                case 2: {
                    int i;
                    for (i = 0; i < SRPConfig.parasiteWeakToMobs.length; ++i) {
                        String[] here;
                        if (SRPConfig.parasiteWeakToMobs[i] == null || !(here = SRPConfig.parasiteWeakToMobs[i].split(";"))[0].equals(damageName)) continue;
                        amount *= Float.parseFloat(here[1]);
                    }
                    break;
                }
                case 3: {
                    int i;
                    for (i = 0; i < SRPConfig.parasiteWeakToElse.length; ++i) {
                        String[] here;
                        if (SRPConfig.parasiteWeakToElse[i] == null || !(here = SRPConfig.parasiteWeakToElse[i].split(";"))[0].equals(damageName)) continue;
                        amount *= Float.parseFloat(here[1]);
                    }
                    break;
                }
            }
        }
        if (flagCap) {
            switch (naniDesu) {
                case 1: {
                    if (!ParasiteEventEntity.checkName(damageName, SRPConfig.damageCapBlackListItem, SRPConfig.damageCapBlackListWhite)) break;
                    return super.attackEntityFrom(source, amount);
                }
                case 2: {
                    if (!ParasiteEventEntity.checkName(damageName, SRPConfig.damageCapBlackListMob, SRPConfig.damageCapBlackListWhite)) break;
                    return super.attackEntityFrom(source, amount);
                }
                case 3: {
                    if (!ParasiteEventEntity.checkName(damageName, SRPConfig.damageCapBlackListElse, SRPConfig.damageCapBlackListWhite)) break;
                    return super.attackEntityFrom(source, amount);
                }
            }
            float damage = this.getMaxHealth() / (float)this.damageCap + this.getMaxHealth() % (float)this.damageCap * 0.5f;
            if (amount >= damage) {
                source.setDamageBypassesArmor();
                if (!this.isPotionActive(SRPPotions.RAGE_E)) {
                    this.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 200, 1, false, false));
                }
                if (this.rand.nextDouble() < 0.3 && this.getHealth() > 0.0f) {
                    this.attackEntityFromEffects(1, 1);
                    this.attackEntityFromCap(1);
                }
            }
            return super.attackEntityFrom(source, Math.min(amount, damage));
        }
        return super.attackEntityFrom(source, amount);
    }

    protected void attackEntityFromEffects(int range, int count) {
    }

    protected void attackEntityFromCap(int go) {
    }

    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag;
        if (this.worldObj.isRemote) {
            return false;
        }
        this.canBeStored = 55;
        boolean green = entityIn instanceof EntityLivingBase;
        if (green) {
            this.attackEntityAsMobMinimum((EntityLivingBase)entityIn, this.MiniDamage);
        }
        if ((flag = super.attackEntityAsMob(entityIn)) && green) {
            EntityLivingBase target;
            if (this.getSkin() == 120) {
                SRPPotions.applyStackPotion(MobEffects.moveSlowdown, (EntityLivingBase)entityIn, 100, 1);
            }
            this.setAttackCooldownAni(100);
            if (this.disloNumberSix && entityIn instanceof EntityPlayer) {
                EntityPlayer playerIn = (EntityPlayer)entityIn;
                SRPSaveData data = SRPSaveData.get(this.worldObj, 33);
                int count = data.getCurrentCode(this.worldObj.provider.dimensionId, 6);
                if (count >= 1) {
                    for (ItemStack itemstack : playerIn.inventory.armorInventory) {
                        itemstack.damageItem(count, (EntityLivingBase)playerIn);
                    }
                } else {
                    this.disloNumberSix = false;
                }
            }
            if (this.rand.nextDouble() < (double)this.cothSpread && !(target = (EntityLivingBase)entityIn).isPotionActive(SRPPotions.COTH_E) && !target.isPotionActive(SRPPotions.EPEL_E)) {
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            this.attackEntityAsMobFood(entityIn, true, this.foodRootNumber, this.foodRott);
        }
        return flag;
    }

    public boolean attackEntityAsMobMinimum(EntityLivingBase target, float MinimumDamage) {
        if (MinimumDamage <= 0.0f) {
            return false;
        }
        if (!this.geneMindam) {
            return false;
        }
        float f1 = target.getHealth();
        if (f1 <= 0.0f || f1 <= this.miniCap && this.miniCapA) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            ItemStack itemstack1;
            EntityPlayer entityplayer = (EntityPlayer)target;
            if (entityplayer.capabilities.isCreativeMode) {
                return false;
            }
            entityplayer.addExhaustion(this.foodSteal);
            ItemStack itemStack = itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
            if (itemstack1.getItem() instanceof ItemShield && entityplayer.isActiveItemStackBlocking() && !entityplayer.getCooldownTracker().hasCooldown(itemstack1.getItem())) {
                float f12 = 0.25f + (float)EnchantmentHelper.getEfficiencyModifier((EntityLivingBase)this) * 0.05f;
                if (this.rand.nextFloat() < f12 && this.type >= 31 || this.disloNumberSeventeen > 0) {
                    entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                    this.worldObj.setEntityState((Entity)entityplayer, (byte)30);
                }
                this.damageShield(MinimumDamage);
                return true;
            }
        }
        DamageSource s = DamageSource.causeMobDamage((EntityLivingBase)this);
        float damage = 0.0f;
        if (target.isPotionActive(SRPPotions.VIRA_E)) {
            damage = MinimumDamage * (float)(target.getActivePotionEffect(SRPPotions.VIRA_E).getAmplifier() + 1);
        }
        damage += MinimumDamage;
        try {
            if (target.getCombatTracker() != null && s != null) {
                target.getCombatTracker().trackDamage(s, f1, damage);
            }
        }
        catch (Exception f12) {
            // empty catch block
        }
        if (target.getAbsorptionAmount() > 0.0f) {
            target.setHealth(f1 - damage / 2.0f);
            target.setAbsorptionAmount(target.getAbsorptionAmount() - damage / 2.0f);
        } else {
            target.setHealth(f1 - damage);
        }
        this.worldObj.setEntityState((Entity)target, (byte)2);
        if (target.getHealth() <= 0.0f) {
            ItemStack itemstack = null;
            for (EnumHand enumhand : EnumHand.values()) {
                ItemStack itemstack1 = target.getHeldItem(enumhand);
                if (itemstack1.getItem() != Items.TOTEM_OF_UNDYING) continue;
                itemstack = itemstack1.copy();
                itemstack1.shrink(1);
                break;
            }
            if (itemstack != null) {
                if (target instanceof EntityPlayerMP) {
                    EntityPlayerMP entityplayermp = (EntityPlayerMP)target;
                    entityplayermp.triggerAchievement(StatList.getObjectUseStats((Item)Items.TOTEM_OF_UNDYING));
                    CriteriaTriggers.USED_TOTEM.trigger(entityplayermp, itemstack);
                }
                target.setHealth(1.0f);
                target.clearActivePotions();
                target.addPotionEffect(new PotionEffect(MobEffects.regeneration, 900, 1));
                target.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
                target.worldObj.setEntityState((Entity)target, (byte)35);
            } else {
                target.onDeath(s);
                this.killcount += 1.0;
            }
        }
        return true;
    }

    public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
        if (livingBase instanceof EntityPlayer && livingBase.isPotionActive(SRPPotions.THE_SIGN_E)) {
            super.setRevengeTarget(null);
            return;
        }
        super.setRevengeTarget(livingBase);
    }

    public boolean attackEntityAsMobFood(Entity target, boolean hit, int foodnumber, double foodchance) {
        if (this.disloNumberSeventeen > 0) {
            foodnumber = 1;
            foodchance = 1.0;
        }
        if (this.rand.nextDouble() >= foodchance && hit) {
            return false;
        }
        if (!(target instanceof EntityPlayer)) {
            return false;
        }
        if (foodnumber <= 0) {
            return false;
        }
        EntityPlayer player = (EntityPlayer)target;
        if (player.capabilities.isCreativeMode) {
            return false;
        }
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            if (!(((ItemStack)player.inventory.mainInventory.get(i)).getItem() instanceof ItemFood)) continue;
            int amm = this.rand.nextInt(foodnumber) + 1;
            ((ItemStack)player.inventory.mainInventory.get(i)).shrink(amm);
            ItemStack stack = new ItemStack(SRPItems.infected_drop, amm);
            EntityItem entityitem = new EntityItem(this.worldObj, target.posX, target.posY, target.posZ, stack);
            entityitem.setDefaultPickupDelay();
            this.worldObj.spawnEntityInWorld((Entity)entityitem);
            return true;
        }
        if (player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() instanceof ItemFood) {
            int amm = this.rand.nextInt(foodnumber) + 1;
            player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).shrink(amm);
            ItemStack stack = new ItemStack(SRPItems.infected_drop, amm);
            EntityItem entityitem = new EntityItem(this.worldObj, target.posX, target.posY, target.posZ, stack);
            entityitem.setDefaultPickupDelay();
            this.worldObj.spawnEntityInWorld((Entity)entityitem);
            return true;
        }
        return false;
    }

    public void onKillEntity(EntityLivingBase entityLivingIn) {
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.killcount >= 0.0) {
            if (this.getSkin() != 120) {
                this.killcount += 1.0;
            } else if (this.rand.nextInt(3) == 0) {
                this.killcount += 1.0;
            }
        }
        if (SRPConfigSystems.useEvolution) {
            if (this.isPotionActive(SRPPotions.PIVOT_E)) {
                int amp = this.getActivePotionEffect(SRPPotions.PIVOT_E).getAmplifier();
                SRPSaveData.get(this.worldObj, 35).setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueKill * (SRPConfigSystems.pivotPointMultiplier * (amp + 1)), true, this.worldObj, true, 17);
            } else {
                SRPSaveData.get(this.worldObj, 34).setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueKill, true, this.worldObj, true, 16);
            }
        }
        if (SRPConfigWorld.originActivated) {
            ParasiteEventWorld.setOriginInHealth(this.worldObj, EntityQueries.position(this), (int)((double)entityLivingIn.getMaxHealth() * SRPConfigWorld.originKillMultiplier), true);
            if (SRPConfigWorld.originTriggerKill > this.worldObj.rand.nextDouble()) {
                ParasiteEventWorld.placeOriginInWorld(this.worldObj, new BlockPos(this.posX, this.posY, this.posZ), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
            }
        }
        if (entityLivingIn.isPotionActive(SRPPotions.COTH_E) && this.rand.nextDouble() < (double)SRPConfigSystems.cothConvert) {
            ParasiteEventEntity.convertEntity(entityLivingIn, entityLivingIn.getEntityData(), true, SRPConfigSystems.COTHVictimParasite);
        }
        if (this.isPotionActive(SRPPotions.PARATE_E)) {
            int bonuss = this.getActivePotionEffect(SRPPotions.PARATE_E).getAmplifier() + 1;
            if (entityLivingIn.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null) {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + entityLivingIn.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null) {
                this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() + entityLivingIn.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getEntityAttribute(SharedMonsterAttributes.attackDamage) != null) {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() + entityLivingIn.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
        }
        this.heal(entityLivingIn.getHealth() * this.geneMobHealing);
        this.setWait(10);
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if (this.disloNumberEleven > 0 && potioneffectIn.getPotion().isBadEffect()) {
            return false;
        }
        Potion potion = potioneffectIn.getPotion();
        if (potion == SRPPotions.COTH_E || potion == SRPPotions.VIRA_E || potion == SRPPotions.CORRO_E || potion == SRPPotions.DLER_E) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    protected boolean canBeRidden(Entity entityIn) {
        if (entityIn instanceof EntityBoat || entityIn instanceof EntityMinecart) {
            return false;
        }
        return super.canBeRidden(entityIn);
    }

    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (cls == EntityPlayer.class || cls == EntityPlayerMP.class) {
            return true;
        }
        String name = null;
        try {
            name = EntityList.getKey(cls).toString();
        }
        catch (Exception e) {
            return true;
        }
        if (name == null) {
            return true;
        }
        if (name.contains("srparasites")) {
            return false;
        }
        return !SRPConfig.mobAttackingFull || !ParasiteEventEntity.checkName(name, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
    }

    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        if (forSpawnCount) {
            return false;
        }
        if (this instanceof EntityLiving && this.isNoDespawnRequired()) {
            return false;
        }
        return type.getCreatureClass().isAssignableFrom(this.getClass());
    }

    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (hand != EnumHand.MAIN_HAND || this.worldObj.isRemote) {
            return super.processInteract(player, hand);
        }
        Item wea = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
        if (wea == SRPItems.itemEvolve) {
            this.killcount += 1.0E9;
            this.killcount += 1.0E9;
            this.killcount += 1.0E9;
        } else if (wea == SRPItems.itemDevolve) {
            this.canChangeVariant = true;
            this.onDeath(DamageSource.generic);
            this.setDead();
        } else if (wea == SRPItems.itemVariant) {
            this.cycleManualVariant();
        }
        return super.processInteract(player, hand);
    }

    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
        if (entitylivingbaseIn instanceof EntityPlayer && entitylivingbaseIn.isPotionActive(SRPPotions.THE_SIGN_E)) {
            super.setAttackTarget(null);
            return;
        }
        if (this.targetNewCool > 0 && entitylivingbaseIn != null) {
            return;
        }
        super.setAttackTarget(entitylivingbaseIn);
        if (entitylivingbaseIn != null && entitylivingbaseIn != this.last) {
            this.doLast(entitylivingbaseIn);
            this.last = entitylivingbaseIn;
        }
    }

    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn, int negative) {
        this.setAttackTarget(entitylivingbaseIn);
        this.targetNewCool = negative;
    }

    protected void doLast(EntityLivingBase entitylivingbaseIn) {
        if (entitylivingbaseIn == null) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(this.worldObj);
        if (data == null) {
            return;
        }
        if (data.nearestInfectionPosition(false, EntityQueries.position(this)) == null) {
            return;
        }
        entitylivingbaseIn.addPotionEffect(new PotionEffect(SRPPotions.SPOT_E, 1200, 0, false, false));
        if (SRPConfigSystems.useOneMind) {
            ParasiteEventEntity.alertOthers(this, entitylivingbaseIn, this.worldObj, 7);
        }
    }

    public int canBeStored() {
        return this.canBeStored;
    }

    public int getSelfeState() {
        return (Integer)EntityDataManager.of(this).get(SELFE);
    }

    public void setSelfeState(int state) {
        EntityDataManager.of(this).set(SELFE, (Object)state);
    }

    public float getMiniDamage() {
        return this.MiniDamage;
    }

    public int getLLeap() {
        return this.liquidLeap;
    }

    public void setWorkTask(boolean in) {
        this.canWorkTask = in;
    }

    public boolean shouldWorkTask() {
        return this.canWorkTask;
    }

    public void setParasiteToFollow(@Nullable EntityParasiteBase in) {
        this.owner = in;
    }

    public EntityParasiteBase getParasiteFollowing() {
        if (this.owner != null) {
            if (this.owner.isEntityAlive()) {
                return this.owner;
            }
            this.setParasiteToFollow(null);
        }
        return null;
    }

    public void SetRooter(@Nullable EntityParasiteBase in) {
        this.notOwner = in;
    }

    public byte getParasiteStatus() {
        return (Byte)EntityDataManager.of(this).get(SPECIAL);
    }

    public void setParasiteStatus(int state) {
        EntityDataManager.of(this).set(SPECIAL, (Object)((byte)state));
    }

    public void setWait(int in) {
        this.waitInt = in;
    }

    public int getWait() {
        return this.waitInt;
    }

    public void resetIdleTime() {
        this.entityAge = 0;
    }

    public byte getPhaseCreated() {
        return this.phaseCreated;
    }

    public byte getLevelCreated() {
        return this.levelCreated;
    }

    public int getSkin() {
        return ((Byte)EntityDataManager.of(this).get(SKIN)).intValue();
    }

    public int getMaxManualVariants() {
        return 10;
    }

    public void cycleManualVariant() {
        this.canChangeVariant = true;
        int next = this.getSkin() + 1;
        if (next >= this.getMaxManualVariants()) {
            next = 0;
        }
        this.setSkin(next);
        this.canChangeVariant = false;
    }

    public boolean getColdL() {
        return (Boolean)EntityDataManager.of(this).get(COLD_L);
    }

    public boolean getDislo15() {
        return (Boolean)EntityDataManager.of(this).get(DISLO15);
    }

    public void setDislo15(boolean in) {
        EntityDataManager.of(this).set(DISLO15, (Object)in);
    }

    public void setSkin(int texture) {
        if (this.getSkin() == 120 && !this.canChangeVariant) {
            return;
        }
        EntityDataManager.of(this).set(SKIN, (Object)((byte)texture));
    }

    public int getCCDeathValue() {
        return this.oneMindDeathValue;
    }

    public void setAttackCooldownAni(int i) {
        this.attackCooldown = i;
    }

    public int getAttackCooldownAni() {
        return this.attackCooldown;
    }

    public void setKillC(double i) {
        this.killcount = i;
    }

    public double getKillC() {
        return this.killcount;
    }

    public byte getParasiteType() {
        return this.type;
    }

    public void playLivingSound() {
        if (((Boolean)EntityDataManager.of(this).get(DISLO15)).booleanValue()) {
            return;
        }
        super.playLivingSound();
    }

    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!this.worldObj.isRemote) {
            if (this.onDeathDislo(cause)) {
                return;
            }
            this.spawnCyst();
            ParasiteEventEntity.spawnBeckon(this.worldObj, cause, this);
            ParasiteEventEntity.leaveScent(this.worldObj, cause, this);
            ParasiteEventWorld.setOriginInHealth(this.worldObj, EntityQueries.position(this), (int)(-((double)this.getMaxHealth() * SRPConfigWorld.originParasiteDeath)), true);
            if (SRPConfigSystems.useEvolution && !this.isPotionActive(SRPPotions.DEBAR_E)) {
                SRPSaveData.get(this.worldObj, 39).setTotalKills(this.worldObj.provider.dimensionId, -this.valueEvDeath, true, this.worldObj, false, 18);
            }
            if (cause.getEntity() != null && cause.getEntity() instanceof EntityPlayer) {
                this.checkWeapon((EntityPlayer)cause.getEntity());
            }
        }
    }

    protected boolean onDeathDislo(DamageSource cause) {
        List moblist;
        int count;
        ParasiteEventWorld.setDisloWorldPhase(this.worldObj, SRPAttributes.EVENTPARADEATH, SRPConfigSystems.chanceEventParaDeath, 0, null);
        SRPSaveData data = SRPSaveData.get(this.worldObj, 38);
        if (this.disloNumberTwo && (count = data.getCurrentCode(this.worldObj.provider.dimensionId, 2)) >= 1 && cause.getEntity() instanceof EntityLivingBase) {
            count = (int)((float)count + this.getMaxHealth());
            int furaa = data.getCurrentCodeDuration(this.worldObj.provider.dimensionId, 2);
            SRPPotions.applyStackPotion(SRPPotions.JUGG_E, (EntityLivingBase)cause.getEntity(), furaa * 20 + 50, 1);
            data.setCurrentCode(this.worldObj.provider.dimensionId, 2, count, furaa, null, false, 0);
        }
        if (this.disloNumberSeven && (count = data.getCurrentCode(this.worldObj.provider.dimensionId, 7)) >= 1) {
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 7.0);
            moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
            for (EntityParasiteBase mob : moblist) {
                mob.heal(count);
            }
            this.playSound(SRPSounds.RATHOL_BOOM, 0.2f, 0.7f);
            this.particleStatus((byte)7);
        }
        if (this.disloNumberEight) {
            count = data.getCurrentCode(this.worldObj.provider.dimensionId, 8);
            if (count >= 1) {
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 7.0);
                moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                for (EntityParasiteBase mob : moblist) {
                    if (mob instanceof EntityParasiteBase) continue;
                    mob.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), count);
                }
            }
            this.playSound(SRPSounds.RATHOL_BOOM, 0.2f, 0.7f);
            this.particleStatus((byte)7);
        }
        if (this.disloNumberNine) {
            count = data.getCurrentCode(this.worldObj.provider.dimensionId, 9);
            if (count >= 1) {
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 7.0);
                moblist = EntityQueries.entities(this.worldObj, EntityPlayer.class, axisalignedbb);
                for (EntityParasiteBase mob : moblist) {
                    mob.addExhaustion(count);
                }
            }
            this.playSound(SRPSounds.RATHOL_BOOM, 0.2f, 0.7f);
            this.particleStatus((byte)7);
        }
        if (this.disloNumberEighteen && data.getCurrentCode(this.worldObj.provider.dimensionId, 18) >= 1) {
            this.experienceValue = 0;
        }
        return false;
    }

    private void checkWeapon(EntityPlayer playerIn) {
        if (!SRPConfigSystems.useScent) {
            return;
        }
        if (this.getLevelCreated() < SRPConfigSystems.deveScentUse) {
            return;
        }
        Item ga = playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
        if ((ga instanceof WeaponToolMeleeBase || ga instanceof WeaponToolRangeBase) && this.rand.nextInt(10) == 0) {
            playerIn.addPotionEffect(new PotionEffect(SRPPotions.PREY_E, 600, 0, false, false));
        }
    }

    protected void onDeathUpdate() {
        if (this.canModRender == 1) {
            if (this.madeRng == 0) {
                this.setSelfeState(1);
                this.setParasiteStatus(6);
                this.dyingBurst(true, 1);
            } else {
                this.onDeathUpdateOG();
            }
        } else if (this.canModRender == 2) {
            if (this.madeRng == 0) {
                this.selfExplode();
                this.OnDeathHelper();
            } else {
                this.onDeathUpdateOG();
            }
        } else {
            this.onDeathUpdateOG();
        }
    }

    protected void onDeathUpdateOG() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            if (!this.worldObj.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) && !this.isPotionActive(SRPPotions.DEBAR_E)) {
                int j;
                int i;
                if (SRPConfigSystems.useEvolution) {
                    if (SRPSaveData.get(this.worldObj, 37).getEvolutionPhase(this.worldObj.provider.dimensionId) < SRPConfigSystems.evolutionPArasitesWithoutXP) {
                        i = this.getExperiencePoints(this.attackingPlayer);
                        for (i = ForgeEventFactory.getExperienceDrop((EntityLivingBase)this, (EntityPlayer)this.attackingPlayer, (int)i); i > 0; i -= j) {
                            j = EntityXPOrb.getXPSplit((int)i);
                            this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                        }
                    }
                } else {
                    i = this.getExperiencePoints(this.attackingPlayer);
                    for (i = ForgeEventFactory.getExperienceDrop((EntityLivingBase)this, (EntityPlayer)this.attackingPlayer, (int)i); i > 0; i -= j) {
                        j = EntityXPOrb.getXPSplit((int)i);
                        this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                    }
                }
            }
            this.setDead();
            if (this.worldObj.isRemote) {
                for (int k = 0; k < 20; ++k) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                }
            }
        }
    }

    protected void dyingBurst(boolean fromDeath, int value) {
        int i = this.getSelfeState();
        if (i <= 0 || this.timeSinceIgnited == 0) {
            // empty if block
        }
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.selfExplode();
            if (fromDeath) {
                this.OnDeathHelper();
            }
        }
    }

    public void OnDeathHelper() {
        if (!this.worldObj.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))) {
            int j;
            int i = this.getExperiencePoints(this.attackingPlayer);
            for (i = ForgeEventFactory.getExperienceDrop((EntityLivingBase)this, (EntityPlayer)this.attackingPlayer, (int)i); i > 0; i -= j) {
                j = EntityXPOrb.getXPSplit((int)i);
                this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }
        }
    }

    protected void selfExplode() {
        if (this.worldObj.isRemote) {
            this.spawnEffectsGore();
        }
        if (!this.worldObj.isRemote) {
            this.spawnGore();
            this.playSound(SRPSounds.MOBEXPLOTION, 1.0f, 1.0f);
            this.dead = true;
            this.setDead();
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY, this.posZ);
            entityareaeffectcloud.setRadius(this.width * 1.5f, 0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.poison, 300, 0));
            entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
        }
    }

    protected void spawnGore() {
    }

    protected void collideWithEntity(Entity entityIn) {
        super.collideWithEntity(entityIn);
    }

    public boolean getCanSpawnHere() {
        if (SRPConfigSystems.useEvolution) {
            if (this.phaseCreated >= SRPConfigSystems.evolutionSpawningIgnoreSunlight || this.phaseCreated == -1 && SRPConfigSystems.phaseLightlessMinusOne) {
                IBlockState iblockstate = BlockStates.get(this.worldObj, new BlockPos((Entity)this).down());
                return iblockstate.canEntitySpawn((Entity)this) && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.worldObj.getTotalWorldTime();
            }
            IBlockState iblockstate = BlockStates.get(this.worldObj, new BlockPos((Entity)this).down());
            return iblockstate.canEntitySpawn((Entity)this) && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelOne() && SRPConfig.spawnDays <= (int)this.worldObj.getTotalWorldTime();
        }
        if (SRPConfig.ignoreL) {
            IBlockState iblockstate = BlockStates.get(this.worldObj, new BlockPos((Entity)this).down());
            return iblockstate.canEntitySpawn((Entity)this) && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.worldObj.getTotalWorldTime();
        }
        IBlockState iblockstate = BlockStates.get(this.worldObj, new BlockPos((Entity)this).down());
        return iblockstate.canEntitySpawn((Entity)this) && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevelOne() && SRPConfig.spawnDays <= (int)this.worldObj.getTotalWorldTime();
    }

    protected void despawnEntity() {
        Event.Result result = null;
        if (this.isNoDespawnRequired()) {
            this.entityAge = 0;
        } else if ((this.entityAge & 0x1F) == 31 && (result = ForgeEventFactory.canEntityDespawn((EntityLiving)this)) != Event.Result.DEFAULT) {
            if (result == Event.Result.DENY) {
                this.entityAge = 0;
            } else {
                this.spawnCyst();
                this.storeBefDes();
                this.setDead();
            }
        } else {
            EntityPlayer entity = this.worldObj.getClosestPlayerToEntity((Entity)this, -1.0);
            if (entity != null) {
                double d0 = entity.posX - this.posX;
                double d1 = entity.posY - this.posY;
                double d2 = entity.posZ - this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (this.canDespawn() && d3 > 16384.0) {
                    this.spawnCyst();
                    this.storeBefDes();
                    this.setDead();
                }
                if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0 && this.canDespawn()) {
                    this.spawnCyst();
                    this.storeBefDes();
                    this.setDead();
                } else if (d3 < 1024.0) {
                    this.entityAge = 0;
                }
            }
        }
    }

    public void reduceIdleChance(int chance, int amount) {
        if (this.rand.nextInt(chance) == 0) {
            this.entityAge -= amount;
        }
    }

    protected void storeBefDes() {
        Entity dod;
        if (this.dodFatherID != 0 && (dod = this.worldObj.getEntityByID(this.dodFatherID)) != null && dod.isEntityAlive() && dod instanceof EntityPDispatcher) {
            ((EntityPDispatcher)dod).storeParasite(this);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected boolean isValidLightLevelOne() {
        if (this.worldObj.getBiome(EntityQueries.position(this)) instanceof BiomeParasiteBase) {
            return this.isValidLightLevelTwo();
        }
        BlockPos blockpos = new BlockPos(this.posX, Boxes.entityBox(this).minY, this.posZ);
        if (this.worldObj.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)) {
            return false;
        }
        int i = WorldAccess.getLightFromNeighbors(this.worldObj, blockpos);
        if (this.worldObj.isThundering()) {
            int j = this.worldObj.getSkylightSubtracted();
            this.worldObj.setSkylightSubtracted(10);
            i = WorldAccess.getLightFromNeighbors(this.worldObj, blockpos);
            this.worldObj.setSkylightSubtracted(j);
        }
        if (i > this.rand.nextInt(8)) return false;
        BlockPos blockPos = new BlockPos(this.posX, Boxes.entityBox(this).minY, this.posZ);
        if (!(this.getBlockPathWeight(blockPos) >= 0.0f)) return false;
        return true;
    }

    protected boolean isValidLightLevelTwo() {
        BlockPos blockpos = new BlockPos(this.posX, Boxes.entityBox(this).minY, this.posZ);
        int light = this.worldObj.getLightFor(EnumSkyBlock.BLOCK, blockpos);
        if (light > this.rand.nextInt(1000) || light > 7) {
            return false;
        }
        return this.rand.nextInt(8) == 0;
    }

    public void lookAt(Entity in) {
        this.lookAt(in.posX, in.posY, in.posZ);
    }

    public void lookAt(double x, double y, double z) {
        double dx = x - this.posX;
        double dy = y - (this.posY + (double)this.getEyeHeight());
        double dz = z - this.posZ;
        double yaw = Math.atan2(dz, dx) * 57.29577951308232 - 90.0;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double pitch = -Math.atan2(dy, distance) * 57.29577951308232;
        this.rotationYaw = (float)yaw;
        this.rotationPitch = (float)pitch;
    }

    private boolean isInParasiteBiome() {
        return this.rand.nextInt(8) == 0 && this.worldObj.getBiome(EntityQueries.position(this)) instanceof BiomeParasiteBase;
    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (SRPConfigSystems.useEvolution && !this.worldObj.isRemote) {
            this.phaseCreated = SRPSaveData.get(this.worldObj, 36).getEvolutionPhase(this.worldObj.provider.dimensionId);
            if (this.phaseCreated >= SRPConfigSystems.evolutionParasiteStatIncrease) {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * (double)(1.0f + SRPConfigSystems.evolutionParasiteStatIncreaseValue));
                this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() * (double)(1.0f + SRPConfigSystems.evolutionParasiteStatIncreaseValue));
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * (double)(1.0f + SRPConfigSystems.evolutionParasiteStatIncreaseValue));
            }
        }
        return floo;
    }

    protected boolean applyColdBiome() {
        Biome c = this.worldObj.getBiome(EntityQueries.position(this));
        if (c == null) {
            return false;
        }
        if (this.isBiomeBlacklistedForFrozen(c)) {
            return false;
        }
        if (c.getTemperature(EntityQueries.position(this)) <= (float)SRPConfigWorld.frozenVariantTempThreshold || c.isSnowyBiome() || this.canChangeVariant) {
            this.setSkin(120);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() * 0.6);
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * 1.4);
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.followRange).getBaseValue() * 1.3);
            return true;
        }
        return false;
    }

    private boolean isBiomeBlacklistedForFrozen(Biome biome) {
        if (biome == null || biome.getRegistryName() == null) {
            return false;
        }
        String biomeKey = biome.getRegistryName().toString();
        for (String raw : SRPConfigWorld.frozenVariantBiomeBlacklist) {
            if (raw == null || raw.isEmpty() || !biomeKey.equalsIgnoreCase(raw.trim())) continue;
            return true;
        }
        return false;
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("dsltwo", this.disloNumberTwo);
        compound.setBoolean("dslthree", this.disloNumberThree);
        compound.setBoolean("dslfour", this.disloNumberFour);
        compound.setBoolean("dslsix", this.disloNumberSix);
        compound.setBoolean("dslseven", this.disloNumberSeven);
        compound.setBoolean("dsleight", this.disloNumberEight);
        compound.setBoolean("dslnine", this.disloNumberNine);
        compound.setInteger("dsleleven", this.disloNumberEleven);
        compound.setInteger("dslfifteen", this.disloNumberFifteen);
        compound.setBoolean("dslfifteenb", ((Boolean)EntityDataManager.of(this).get(DISLO15)).booleanValue());
        compound.setInteger("dslsixteen", this.disloNumberSixteen);
        compound.setInteger("dslseventeen", this.disloNumberSeventeen);
        compound.setBoolean("dsleighteen", this.disloNumberEighteen);
        compound.setInteger("dslnineteen", this.disloNumberNineteen);
        compound.setDouble("dslnineteenv", this.disloNumberNineteenValue);
        compound.setBoolean("dsltyone", this.disloNumberTwentyone);
        compound.setBoolean("dsltytwo", this.disloNumberTwentytwo);
        compound.setInteger("parasitetype", this.getSkin());
        compound.setBoolean("parasitedespawn", this.canDespawn());
        compound.setFloat("parasitekills", (float)this.getKillC());
        compound.setBoolean("parasitecolob", this.getApplyColonyB());
        compound.setBoolean("parasitecoldl", ((Boolean)EntityDataManager.of(this).get(COLD_L)).booleanValue());
        compound.setByte("phasecreat", this.phaseCreated);
        compound.setByte("levelcreat", this.levelCreated);
        compound.setByte("paragener", this.paraGeneration);
        NBTTagList allResS = new NBTTagList();
        NBTTagList allResI = new NBTTagList();
        if (this.inbBlockName.size() != this.inbBlockNumber.size()) {
            return;
        }
        for (int i = 0; i < this.inbBlockName.size(); ++i) {
            String res = this.inbBlockName.get(i);
            NBTTagCompound resT = new NBTTagCompound();
            resT.setString("blocks" + i, res);
            allResS.appendTag((NBTBase)resT);
            int resi = this.inbBlockNumber.get(i);
            NBTTagCompound resU = new NBTTagCompound();
            resU.setInteger("blocki" + i, resi);
            allResI.appendTag((NBTBase)resU);
        }
        compound.setTag("srpinvblocksname", (NBTBase)allResS);
        compound.setTag("srpinvblocksnumber", (NBTBase)allResI);
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("dsltwo", 99)) {
            this.disloNumberTwo = compound.getBoolean("dsltwo");
        }
        if (compound.hasKey("dslthree", 99)) {
            this.disloNumberThree = compound.getBoolean("dslthree");
        }
        if (compound.hasKey("dslfour", 99)) {
            this.disloNumberFour = compound.getBoolean("dslfour");
        }
        if (compound.hasKey("dslsix", 99)) {
            this.disloNumberSix = compound.getBoolean("dslsix");
        }
        if (compound.hasKey("dslseven", 99)) {
            this.disloNumberSeven = compound.getBoolean("dslseven");
        }
        if (compound.hasKey("dsleight", 99)) {
            this.disloNumberEight = compound.getBoolean("dsleight");
        }
        if (compound.hasKey("dslnine", 99)) {
            this.disloNumberNine = compound.getBoolean("dslnine");
        }
        if (compound.hasKey("dsleleven", 99)) {
            this.disloNumberEleven = compound.getInteger("dsleleven");
        }
        if (compound.hasKey("dslfifteen", 99)) {
            this.disloNumberFifteen = compound.getInteger("dslfifteen");
        }
        if (compound.hasKey("dslfifteenb", 99)) {
            EntityDataManager.of(this).set(DISLO15, (Object)compound.getBoolean("dslfifteenb"));
        }
        if (compound.hasKey("dslsixteen", 99)) {
            this.disloNumberSixteen = compound.getInteger("dslsixteen");
        }
        if (compound.hasKey("dslseventeen", 99)) {
            this.disloNumberSeventeen = compound.getInteger("dslseventeen");
        }
        if (compound.hasKey("dsleighteen", 99)) {
            this.disloNumberEighteen = compound.getBoolean("dsleighteen");
        }
        if (compound.hasKey("dslnineteen", 99)) {
            this.disloNumberNineteen = compound.getInteger("dslnineteen");
        }
        if (compound.hasKey("dslnineteenv", 99)) {
            this.disloNumberNineteenValue = compound.getDouble("dslnineteenv");
        }
        if (compound.hasKey("dsltyone", 99)) {
            this.disloNumberTwentyone = compound.getBoolean("dsltyone");
        }
        if (compound.hasKey("dsltytwo", 99)) {
            this.disloNumberTwentytwo = compound.getBoolean("dsltytwo");
        }
        if (compound.hasKey("parasitetype", 99)) {
            this.setSkin(compound.getInteger("parasitetype"));
        }
        if (compound.hasKey("parasitedespawn", 99)) {
            this.cannotDespawn(compound.getBoolean("parasitedespawn"));
        }
        if (compound.hasKey("parasitekills", 99)) {
            this.setKillC(compound.getFloat("parasitekills"));
        }
        if (compound.hasKey("parasitecolob", 99)) {
            this.setApplyColonyB(compound.getBoolean("parasitecolob"));
        }
        if (compound.hasKey("parasitecoldl", 99)) {
            EntityDataManager.of(this).set(COLD_L, (Object)compound.getBoolean("parasitecoldl"));
        }
        if (compound.hasKey("phasecreat", 99)) {
            this.phaseCreated = compound.getByte("phasecreat");
        }
        if (compound.hasKey("levelcreat", 99)) {
            this.levelCreated = compound.getByte("levelcreat");
        }
        if (compound.hasKey("paragener", 99)) {
            this.paraGeneration = compound.getByte("paragener");
        }
        if (compound.hasKey("srpinvblocksname")) {
            NBTTagList allResS = compound.getTagList("srpinvblocksname", 10);
            NBTTagList allResI = compound.getTagList("srpinvblocksnumber", 10);
            if (allResS.tagCount() != allResI.tagCount()) {
                return;
            }
            for (int i = 0; i < allResS.tagCount(); ++i) {
                NBTTagCompound resT = allResS.getCompoundTagAt(i);
                String res = resT.getString("blocks" + i);
                this.inbBlockName.add(i, res);
                NBTTagCompound resU = allResI.getCompoundTagAt(i);
                int resi = resU.getInteger("blocki" + i);
                this.inbBlockNumber.add(i, resi);
            }
        }
    }

    public void particleStatus(byte id) {
        this.worldObj.setEntityState((Entity)this, id);
    }

    @SideOnly(value=Side.CLIENT)
    protected void spawnEffectsGore() {
    }

    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        switch (id) {
            case 5: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.RHAPPY, 0, 0, 0);
                }
                break;
            }
            case 6: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                }
                break;
            }
            case 7: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(EnumParticleTypes.EXPLOSION_LARGE);
                }
                break;
            }
            case 8: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
                }
                break;
            }
            case 10: {
                for (int i = 0; i <= 40; ++i) {
                    this.spawnParticles(EnumParticleTypes.CRIT_MAGIC);
                }
                break;
            }
            case 11: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0);
                }
                break;
            }
            case 12: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 0);
                }
                break;
            }
            case 13: {
                for (int i = 0; i <= 10; ++i) {
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 4, -1, -1);
                }
                break;
            }
            case 40: {
                this.madeRng = 0;
                break;
            }
            case 43: {
                int iddd = BlockStates.stateId((IBlockState)BlockStates.get(this.worldObj, new BlockPos(this.posX, this.posY, this.posZ).down()));
                for (int i = 0; i <= 10; ++i) {
                    this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, new int[]{iddd});
                }
                break;
            }
            case 51: {
                for (int i = 0; i <= 60; ++i) {
                    if (i % 4 == 0) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
                    }
                    if (i % 5 != 0) continue;
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
                }
                break;
            }
            case 52: {
                for (int i = 0; i <= 80; ++i) {
                    if (i % 3 == 0) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 200, 200, 0);
                    }
                    if (i % 5 != 0) continue;
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 3, -1, -1);
                }
                break;
            }
            default: {
                super.handleHealthUpdate(id);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(EnumParticleTypes particleType) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        this.worldObj.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, new int[0]);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, r, g, b);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticlesGore(SRPEnumParticle particleType, int r, int g, int b) {
        this.spawnParticlesGore(particleType, r, g, b, 1.0, 4.0);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticlesGore(SRPEnumParticle particleType, int r, int g, int b, double xF, double yF) {
        double d0 = (float)this.posX + this.rand.nextFloat();
        double d1 = (float)this.posY + this.rand.nextFloat();
        double d2 = (float)this.posZ + this.rand.nextFloat();
        double d3 = d0 - this.posX;
        double d4 = d1 - this.posY;
        double d5 = d2 - this.posZ;
        double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d3 = d3 * (d7 *= (double)(this.rand.nextFloat() * this.rand.nextFloat() + 0.3f)) * xF;
        d4 = d4 * d7 * yF;
        d5 = d5 * d7 * xF;
        d3 = Math.min(d3, 0.2) * (Math.random() * 2.0 - 1.0);
        d4 = Math.min(d4, 0.6) * (Math.random() * 2.0 - 1.0);
        d5 = Math.min(d5, 0.2) * (Math.random() * 2.0 - 1.0);
        ParticleSpawner.spawnParticle(particleType, this.posX, this.posY + 0.2 + (double)this.height, this.posZ, d3, d4, d5, r, g, b);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticlesGoreMouth(SRPEnumParticle particleType, int r, int g, int b, double xF, double yF) {
        double d0 = (float)this.posX + this.rand.nextFloat();
        double d1 = (float)this.posY + this.rand.nextFloat();
        double d2 = (float)this.posZ + this.rand.nextFloat();
        double d3 = d0 - this.posX;
        double d4 = d1 - this.posY;
        double d5 = d2 - this.posZ;
        double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d3 = d3 * (d7 *= (double)(this.rand.nextFloat() * this.rand.nextFloat() + 0.3f)) * xF;
        d4 = d4 * d7 * yF;
        d5 = d5 * d7 * xF;
        d3 = Math.min(d3, 0.2) * (Math.random() * 2.0 - 1.0);
        d4 = Math.min(d4, 0.6) * (Math.random() * 2.0 - 1.0);
        d5 = Math.min(d5, 0.2) * (Math.random() * 2.0 - 1.0);
        ParticleSpawner.spawnParticle(particleType, this.posX, this.posY, this.posZ, d3, d4, d5, r, g, b);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticlesGoreBox(SRPEnumParticle particleType, int r, int g, int b, double xF, double yF) {
        double d0 = (float)this.posX + this.rand.nextFloat();
        double d1 = (float)this.posY + this.rand.nextFloat();
        double d2 = (float)this.posZ + this.rand.nextFloat();
        double d3 = d0 - this.posX;
        double d4 = d1 - this.posY;
        double d5 = d2 - this.posZ;
        double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d3 = d3 * (d7 *= (double)(this.rand.nextFloat() * this.rand.nextFloat() + 0.3f)) * xF;
        d4 = d4 * d7 * yF;
        d5 = d5 * d7 * xF;
        d3 = Math.min(d3, 0.2) * (Math.random() * 2.0 - 1.0);
        d4 = Math.min(d4, 0.6) * (Math.random() * 2.0 - 1.0);
        d5 = Math.min(d5, 0.2) * (Math.random() * 2.0 - 1.0);
        ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d3, d4, d5, r, g, b);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(EnumParticleTypes particleType, double d0, double d1, double d2, double d3, double d4, double d5) {
        this.worldObj.spawnParticle(particleType, d0, d1, d2, d3, d4, d5, new int[0]);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b, double d0, double d1, double d2, double d3, double d4, double d5) {
        ParticleSpawner.spawnParticle(particleType, d0, d1, d2, d3, d4, d5, r, g, b);
    }

    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity(float p_70831_1_) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_) / (float)(this.fuseTime - 2);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean isSwingingArms() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public float getscale(float p_70831_1_) {
        return 0.0f;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean getStillAni() {
        return this.still;
    }

    @SideOnly(value=Side.CLIENT)
    public float getAniTick() {
        return this.aniticks;
    }

    public void doSpecialSkill(byte id) {
        switch (id) {
            case 13: {
                this.skillBreakBlocks();
                return;
            }
            case 14: {
                this.skillLeap();
                return;
            }
        }
    }

    public boolean getFinished(byte attID) {
        switch (attID) {
            case 13: {
                return this.SkillBGflag;
            }
            case 14: {
                return this.SkillLeapFlag;
            }
        }
        return false;
    }

    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 13: {
                this.SkillBGflag = in;
            }
            case 14: {
                this.SkillLeapFlag = in;
            }
        }
    }

    public void setSkillBreakBlocksValues(float hardness, int heightIn, int rangeIn) {
        this.blockH = hardness;
        this.BGheight = heightIn;
        this.BGrange = rangeIn;
    }

    public float getBlockH() {
        if (this.getSkin() == 120) {
            return this.blockH * 1.5f;
        }
        if (this.getSkin() == 7) {
            return this.blockH * 2.0f;
        }
        return this.blockH;
    }

    public void skillBreakBlocks() {
        EntityLivingBase target;
        if (this.isDead) {
            return;
        }
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        if (!ForgeEventFactory.getMobGriefingEvent((World)this.worldObj, (Entity)this)) {
            return;
        }
        int i1 = MathHelper.floor_double((double)(this.posY + 0.1));
        double l1 = this.posX;
        double i2 = this.posZ;
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = 0;
        if (this.getAttackTarget() != null && (target = this.getAttackTarget()).getDistanceSq(this.posX, target.posY, this.posZ) < 9.0) {
            if (target.posY - this.posY < -1.0) {
                offsetT -= 2;
                if (!this.onGround) {
                    --offsetT;
                }
            } else if (target.posY - this.posY > 2.0) {
                ++offsetT;
                this.BGrange = 0;
            }
        }
        for (int k2 = -1 * this.BGrange; k2 <= this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= this.BGrange; ++l2) {
                for (int j = 1 + offsetT; j <= this.BGheight + offsetT; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = new BlockPos(i3, k, l);
                    IBlockState iblockstate = BlockStates.get(this.worldObj, blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getBlockHardness(this.worldObj, blockpos);
                    if (!(bHard <= this.getBlockH()) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister || block == SRPBlocks.BiomeHeart || block == SRPBlocks.ColonyHeart || block == SRPBlocks.ParasiteRubbleDense || block == SRPBlocks.ParasiteCanisterActive || block == SRPBlocks.dodN || block instanceof BlockLiquid || block instanceof BlockPortal || block instanceof BlockEndGateway || block instanceof BlockEndPortalFrame || iblockstate.getMaterial() == Material.portal || this.blockException(name = RegistryNames.get(block).toString()) || block == Blocks.air || !block.canEntityDestroy(iblockstate, (IBlockAccess)this.worldObj, blockpos, (Entity)this) || !ForgeEventFactory.onEntityDestroyBlock((EntityLivingBase)this, (BlockPos)blockpos, (IBlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        int meta = BlockStates.meta(block, iblockstate);
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.addToBlockInv(name + ";" + meta);
                        }
                    } else {
                        this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                    }
                    ++blocksbroke;
                }
            }
        }
        this.BGrange = Brangeatm;
        this.SkillBGflag = true;
    }

    protected boolean destroyBlockPos(BlockPos pos, boolean dropBlock) {
        IBlockState iblockstate = BlockStates.get(this.worldObj, pos);
        Block block = iblockstate.getBlock();
        if (block.isAir(iblockstate, (IBlockAccess)this.worldObj, pos)) {
            return false;
        }
        if (this.rand.nextDouble() < SRPConfig.blockParticleChance) {
            this.worldObj.playEvent(2001, pos, BlockStates.stateId((IBlockState)iblockstate));
        }
        if (this.rand.nextDouble() < SRPConfig.blockSoundChance) {
            SoundType soundtype = block.getSoundType(BlockStates.stateById((int)BlockStates.stateId((IBlockState)iblockstate)), this.worldObj, pos, null);
            this.worldObj.playSound((EntityPlayer)null, pos, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
        }
        if (dropBlock && this.rand.nextDouble() < SRPConfig.blockDropChance) {
            block.dropBlockAsItem(this.worldObj, pos, iblockstate, 0);
        }
        return BlockStates.set(this.worldObj, pos, BlockStates.defaultState(Blocks.air), 3);
    }

    protected boolean blockException(String name) {
        return ParasiteEventEntity.checkName(name, SRPConfig.parasiteGriefingBlackList, SRPConfig.parasiteGriefingWhite);
    }

    public void addToBlockInv(String name) {
        boolean flag = true;
        for (int i = 0; i < this.inbBlockName.size(); ++i) {
            if (!this.inbBlockName.get(i).equals(name) || this.inbBlockNumber.get(i) > 64) continue;
            int iiii = this.inbBlockNumber.get(i) + 1;
            this.inbBlockNumber.set(i, iiii);
            flag = false;
            break;
        }
        if (flag) {
            if (this.inbBlockName.size() >= 22) {
                this.spawnCyst();
            }
            this.inbBlockName.add(name);
            this.inbBlockNumber.add(1);
        }
    }

    protected void spawnCyst() {
        if (this.inbBlockName.isEmpty()) {
            return;
        }
        double d0 = (float)this.posX + this.worldObj.rand.nextFloat();
        double d1 = (float)this.posY + this.worldObj.rand.nextFloat();
        double d2 = (float)this.posZ + this.worldObj.rand.nextFloat();
        double d3 = d0 - this.posX;
        double d4 = d1 - this.posY;
        double d5 = d2 - this.posZ;
        double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d4 = d4 * d7 * 2.0;
        EntityGore bomb = new EntityGore(this.worldObj);
        bomb.setType((byte)10);
        bomb.copyLocationAndAnglesFrom((Entity)this);
        bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.25, 0.75);
        this.worldObj.spawnEntityInWorld((Entity)bomb);
        bomb.inbBlockName = new ArrayList<String>(this.inbBlockName);
        bomb.inbBlockNumber = new ArrayList<Integer>(this.inbBlockNumber);
        this.inbBlockName = new ArrayList();
        this.inbBlockNumber = new ArrayList();
    }

    public void setskillLeapValues(float leapY, double leapSpeed, int jumpRad) {
        this.leapMotionY = leapY;
        this.jumpSpeed = leapSpeed;
        this.jumpR = jumpRad;
    }

    protected void skillLeap() {
        if (this.leapMotionY == 0.0f) {
            return;
        }
        if (this.getAttackTarget() != null && this.shouldWorkTask() && !this.isPotionActive(MobEffects.moveSlowdown) && this.getParasiteStatus() <= 2) {
            EntityLivingBase entitylivingbase = this.getAttackTarget();
            if (this.attacking == 0) {
                ++this.attacking;
                this.targetX = entitylivingbase.posX;
                this.targetZ = entitylivingbase.posZ;
            }
        }
        if (this.attacking >= 1) {
            ++this.attacking;
            this.skillBreakBlocks();
            if (this.attacking == 2 && this.onGround) {
                this.setParasiteStatus(10);
                this.getNavigator().clearPathEntity();
                double d0 = this.targetX - this.posX;
                double d1 = this.targetZ - this.posZ;
                double f = MathHelper.sqrt_double((double)(d0 * d0 + d1 * d1));
                this.motionY = this.leapMotionY;
                this.motionX += d0 / f * this.jumpSpeed * 0.9 + this.motionX * 0.3;
                this.motionZ += d1 / f * this.jumpSpeed * 0.9 + this.motionZ * 0.3;
            }
            if (this.attacking > 2 && this.onGround) {
                if (this.jumpR != 0) {
                    float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue();
                    AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), (double)this.jumpR, 2.0, (double)this.jumpR);
                    List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                    for (EntityLivingBase mob : moblist) {
                        if (mob == this || mob instanceof EntityParasiteBase) continue;
                        mob.knockBack((Entity)mob, 2.5f, this.posX - mob.posX, this.posZ - mob.posZ);
                        this.attackEntityAsMob((Entity)mob);
                    }
                }
                this.setParasiteStatus(0);
                this.attacking = 0;
                if (this.type >= 31 && this.height > 2.0f) {
                    this.playSound(SRPSounds.HITGROUND, 15.0f, 1.0f);
                }
                this.SkillLeapFlag = true;
            }
        }
    }

    public boolean attackEntityFromPart(MultiPartEntityPart dragonPart, DamageSource source, float damage) {
        return damage > 0.0f;
    }

    @Override
    public EntityParasiteBase getParent() {
        return this;
    }

    public void updateHitboxes() {
        for (EntityHitbox hb : this.hitboxes) {
            if (hb == null) continue;
            hb.onUpdate();
        }
    }

    public void resetHitboxes(float scale) {
        if (scale > this.prevHitboxScale) {
            this.prevHitboxScale = scale;
            for (EntityHitbox hb : this.hitboxes) {
                if (hb == null) continue;
                hb.resize(scale);
            }
        }
    }

    private void clearHitboxes() {
        for (EntityHitbox hb : this.hitboxes) {
            if (hb == null) continue;
            hb.setDead();
            this.worldObj.removePlayerEntityDangerously((Entity)hb);
        }
    }

    public void setDead() {
        if (this.hitboxes != null) {
            this.clearHitboxes();
        }
        super.setDead();
    }

    @Nonnull
    public World getWorld() {
        return this.worldObj;
    }

    public static class EntityAIJumping
    extends EntityAIBase {
        private final EntityLiving parent;
        private int secs;

        public EntityAIJumping(EntityLiving parentIn) {
            this.parent = parentIn;
            this.setMutexBits(4);
            if (parentIn.getNavigator() instanceof PathNavigateGround) {
                ((PathNavigateGround)parentIn.getNavigator()).setCanSwim(true);
            } else if (parentIn.getNavigator() instanceof PathNavigateFlying) {
                ((PathNavigateFlying)parentIn.getNavigator()).setCanFloat(true);
            }
        }

        public boolean shouldExecute() {
            ++this.secs;
            if (this.secs < 10) {
                return false;
            }
            this.secs = 0;
            EntityLivingBase target = this.parent.getAttackTarget();
            if (target == null) {
                return false;
            }
            if (target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 4.0 && target.posY - (this.parent.posY + (double)this.parent.getEyeHeight()) > 1.0 && this.parent.onGround) {
                this.parent.getNavigator().clearPathEntity();
                double dd0 = target.posX - this.parent.posX;
                double dd1 = target.posZ - this.parent.posZ;
                float f = MathHelper.sqrt_double((double)(dd0 * dd0 + dd1 * dd1));
                this.parent.motionX += dd0 / (double)f * 0.5 * (double)0.8f + this.parent.motionX * (double)0.2f;
                this.parent.motionZ += dd1 / (double)f * 0.5 * (double)0.8f + this.parent.motionZ * (double)0.2f;
                this.parent.motionY = 0.2 + (double)this.parent.height * 0.15;
            }
            return false;
        }

        public void updateTask() {
            if (this.parent.getRNG().nextFloat() < 0.8f) {
                this.parent.getJumpHelper().setJumping();
            }
        }
    }

    class EntityAIWait
    extends EntityAIBase {
        public EntityAIWait() {
            this.setMutexBits(7);
        }

        public boolean shouldExecute() {
            return EntityParasiteBase.this.getWait() > 0;
        }

        public void updateTask() {
        }
    }
}

