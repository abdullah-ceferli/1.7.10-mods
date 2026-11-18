/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.Packet
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.network.Packet;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHullAdapted
extends EntityPAdapted
implements EntityBodyParts {
    private static final double HULL_SEARCH_RADIUS = 48.0;
    private static final int HULL_FAKE_COOLDOWN_MIN_T = 400;
    private static final int HULL_FAKE_COOLDOWN_MAX_T = 600;
    private static final boolean HULL_FAKE_DEBUG = false;
    private static final boolean HULL_FAKE_FORCE_ENABLE = true;
    private static final double HULL_FAKE_MIN_DIST = 22.0;
    private static final double HULL_FAKE_MAX_DIST = 46.0;
    private static final float HULL_FAKE_VOLUME = 1.0f;
    private static final float HULL_FAKE_PITCH_JIT = 0.1f;
    private static final String[] HULL_FAKE_SOUNDS = new String[]{SoundEvents.ENTITY_ENDERMEN_SCREAM, SoundEvents.BLOCK_ANVIL_LAND, SoundEvents.ENTITY_SPIDER_AMBIENT, SoundEvents.BLOCK_GRASS_STEP};
    private int hullFakeCooldown = 0;
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private int timer = 0;
    private static final DataParameter<Boolean> CAM = EntityDataManager.createKey(EntityHullAdapted.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityHullAdapted.class, (DataSerializer)DataSerializers.VARINT);
    private EntityLivingBase targetedEntity;
    private int pulling;
    private boolean canPull;
    private static final int CLOAK_WARMUP_TICKS = 40;
    private static final int DECLOAK_WARMUP_TICKS = 40;
    private static final float VIB_FREQ_MIN_HZ = 12.0f;
    private static final float VIB_FREQ_MAX_HZ = 108.0f;
    private static final float VIB_MAX_AMP_BLOCKS = 0.09f;
    private static final float VIB_JITTER_FRAC = 0.35f;
    private static final float VIB_MAX_RADIUS = 0.12f;
    private static final float VIB_AXIS_DRIFT_DEG = 3000.0f;
    private static final float VIB_PHASE_JUMP_PROB = 3000.12f;
    private static final float VIB_BURST_PROB = 3000.12f;
    private static final float VIB_STUTTER_PROB = 3000.1f;
    private CloakPhase cloakPhase = CloakPhase.IDLE;
    private int cloakPhaseTicks = 0;
    private double vibAxisX = 0.0;
    private double vibAxisZ = 0.0;
    private double vibLastX = 0.0;
    private double vibLastZ = 0.0;
    private double vibPhase = 0.0;
    private int vibStutterTicks = 0;
    private double vibHoldX = 0.0;
    private double vibHoldZ = 0.0;
    private static final boolean HULL_SOUND_DEBUG = false;
    private boolean hullDumpedSounds = false;

    public EntityHullAdapted(World worldIn) {
        super(worldIn);
        this.setSize(1.4f, 2.7f);
        this.stepHeight = 1.0f;
        this.adaptationCap = 0.95f;
        this.attackSpeedT = 4;
        this.leftTendril = new EntityBody(this, 0.6f, 1.1f, 1.0f, 0.9f, 2.1f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 0.6f, 1.1f, 1.0f, 0.9f, 2.1f, -1, 2, true);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
    }

    @Override
    public int getParasiteIDRegister() {
        return 52;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(CAM, (Object)false);
        EntityDataManager.of(this).register(TARGET_ENTITY, (Object)0);
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.11));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIEvade(this, 25, 10, 4.0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        if (SRPConfig.parasiteGenResidue) {
            this.tasks.addTask(9, (EntityAIBase)new EntityAIBlockResidue(this, 2));
        }
    }

    private void hullBroadcast(String msg) {
    }

    public void setInvisible(boolean invisible) {
        if (!this.worldObj.isRemote) {
            boolean currentlyInvisible = super.isInvisible();
            if (invisible && !currentlyInvisible) {
                if (this.cloakPhase != CloakPhase.PRE_CLOAK && this.cloakPhase != CloakPhase.CLOAKED) {
                    this.startPreCloak();
                    return;
                }
            } else if (!invisible && currentlyInvisible && this.cloakPhase != CloakPhase.PRE_DECLOAK && this.cloakPhase != CloakPhase.IDLE) {
                this.startPreDecloak();
                return;
            }
        }
        super.setInvisible(invisible);
    }

    private void startPreCloak() {
        this.cloakPhase = CloakPhase.PRE_CLOAK;
        this.cloakPhaseTicks = 0;
        this.pickVibeAxis();
        this.vibLastZ = 0.0;
        this.vibLastX = 0.0;
        this.vibPhase = 0.0;
    }

    private void startPreDecloak() {
        this.cloakPhase = CloakPhase.PRE_DECLOAK;
        this.cloakPhaseTicks = 0;
        this.pickVibeAxis();
        this.vibLastZ = 0.0;
        this.vibLastX = 0.0;
        this.vibPhase = 0.0;
    }

    private void pickVibeAxis() {
        double ang = this.rand.nextDouble() * Math.PI * 2.0;
        this.vibAxisX = Math.cos(ang);
        this.vibAxisZ = Math.sin(ang);
    }

    private void tickCloakVibration() {
        double maxR;
        double r2;
        double nz;
        double sin;
        double dAng;
        double cos;
        double nx;
        double nLen;
        if (this.cloakPhase != CloakPhase.PRE_CLOAK && this.cloakPhase != CloakPhase.PRE_DECLOAK) {
            this.cloakPhase = super.isInvisible() ? CloakPhase.CLOAKED : CloakPhase.IDLE;
            return;
        }
        if (this.vibLastX != 0.0 || this.vibLastZ != 0.0) {
            this.setPosition(this.posX - this.vibLastX, this.posY, this.posZ - this.vibLastZ);
            this.vibLastZ = 0.0;
            this.vibLastX = 0.0;
        }
        int duration = this.cloakPhase == CloakPhase.PRE_CLOAK ? 40 : 40;
        float tRaw = (float)this.cloakPhaseTicks / (float)duration;
        float ramp = tRaw * tRaw * (3.0f - 2.0f * tRaw);
        float fRamp = 1.0f - (float)Math.pow(1.0f - ramp, 3.0);
        float freqHz = 12.0f + fRamp * 96.0f;
        double dPhase = Math.PI * 2 * (double)freqHz / 20.0;
        this.vibPhase += dPhase;
        if (this.vibStutterTicks > 0) {
            --this.vibStutterTicks;
            this.vibLastX = this.vibHoldX;
            this.vibLastZ = this.vibHoldZ;
            this.setPosition(this.posX + this.vibLastX, this.posY, this.posZ + this.vibLastZ);
            ++this.cloakPhaseTicks;
            if (this.cloakPhaseTicks >= duration) {
                this.setPosition(this.posX - this.vibLastX, this.posY, this.posZ - this.vibLastZ);
                this.vibLastZ = 0.0;
                this.vibLastX = 0.0;
                if (this.cloakPhase == CloakPhase.PRE_CLOAK) {
                    super.setInvisible(true);
                    this.cloakPhase = CloakPhase.CLOAKED;
                } else {
                    super.setInvisible(false);
                    this.cloakPhase = CloakPhase.IDLE;
                }
            }
            return;
        }
        if (this.rand.nextFloat() < 3000.12f * ramp) {
            double jump = Math.PI * (0.7 + this.rand.nextDouble() * 0.6);
            if (this.rand.nextBoolean()) {
                jump = -jump;
            }
            this.vibPhase += jump;
        }
        if (ramp > 0.0f && (nLen = Math.sqrt((nx = this.vibAxisX * (cos = Math.cos(dAng = Math.toRadians(3000.0) * (double)ramp * (this.rand.nextDouble() * 2.0 - 1.0))) - this.vibAxisZ * (sin = Math.sin(dAng))) * nx + (nz = this.vibAxisX * sin + this.vibAxisZ * cos) * nz)) > 1.0E-6) {
            this.vibAxisX = nx / nLen;
            this.vibAxisZ = nz / nLen;
        }
        double baseMag = (double)(0.09f * ramp) * Math.sin(this.vibPhase);
        double jitterAmp = 0.0315f * ramp;
        double j1 = (this.rand.nextDouble() - 0.5) * 2.0 * jitterAmp;
        double j2 = (this.rand.nextDouble() - 0.5) * 2.0 * jitterAmp;
        double axX = this.vibAxisX;
        double axZ = this.vibAxisZ;
        double pxX = -this.vibAxisZ;
        double pxZ = this.vibAxisX;
        double offX = axX * (baseMag + j1) + pxX * j2;
        double offZ = axZ * (baseMag + j1) + pxZ * j2;
        if (this.rand.nextFloat() < 3000.12f * ramp) {
            double mult = 1.4 + this.rand.nextDouble() * 0.8;
            offX *= mult;
            offZ *= mult;
        }
        if ((r2 = offX * offX + offZ * offZ) > (maxR = (double)(0.12f * ramp)) * maxR && r2 > 0.0) {
            double s = maxR / Math.sqrt(r2);
            offX *= s;
            offZ *= s;
        }
        this.vibLastX = offX;
        this.vibLastZ = offZ;
        this.setPosition(this.posX + this.vibLastX, this.posY, this.posZ + this.vibLastZ);
        if (this.rand.nextFloat() < 3000.1f * ramp) {
            this.vibStutterTicks = 1;
            this.vibHoldX = this.vibLastX;
            this.vibHoldZ = this.vibLastZ;
        }
        ++this.cloakPhaseTicks;
        if (this.cloakPhaseTicks >= duration) {
            if (this.vibLastX != 0.0 || this.vibLastZ != 0.0) {
                this.setPosition(this.posX - this.vibLastX, this.posY, this.posZ - this.vibLastZ);
                this.vibLastZ = 0.0;
                this.vibLastX = 0.0;
            }
            if (this.cloakPhase == CloakPhase.PRE_CLOAK) {
                super.setInvisible(true);
                this.cloakPhase = CloakPhase.CLOAKED;
            } else {
                super.setInvisible(false);
                this.cloakPhase = CloakPhase.IDLE;
            }
        }
    }

    private void hullDebugTo(EntityPlayer player, String msg) {
    }

    private boolean hullPlayFakeSoundNearPlayer(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        double angle = this.rand.nextDouble() * Math.PI * 2.0;
        double dist = 22.0 + this.rand.nextDouble() * 24.0;
        double fx = player.posX + Math.cos(angle) * dist;
        double fz = player.posZ + Math.sin(angle) * dist;
        double fy = player.posY + (double)(this.rand.nextInt(3) - 1);
        BlockPos pos = new BlockPos(fx, fy, fz);
        if (!WorldAccess.isBlockLoaded(this.worldObj, pos) && !WorldAccess.isBlockLoaded(this.worldObj, pos = new BlockPos(fx = (fx + player.posX) * 0.5, fy, fz = (fz + player.posZ) * 0.5))) {
            return false;
        }
        String chosen = HULL_FAKE_SOUNDS[this.rand.nextInt(HULL_FAKE_SOUNDS.length)];
        float pitch = 1.0f + (this.rand.nextFloat() - 0.5f) * 0.2f;
        if (this.worldObj instanceof WorldServer) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(((WorldServer)this.worldObj), null, fx, fy, fz, chosen, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 1.0f, pitch);
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)this.worldObj), EnumParticleTypes.SMOKE_LARGE, fx, fy + 0.2, fz, 10, 0.1, 0.1, 0.1, 0.01, new int[0]);
        }
        if (player instanceof EntityPlayerMP) {
            net.minecraft.network.Packet pkt = com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.packet(chosen, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, fx, fy, fz, 1.0f, pitch);
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacket((Packet)pkt);
        }
        return true;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.HULL_HEALTH + SRPAttributes.HULL_A_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.HULL_ARMOR + SRPAttributes.HULL_A_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.HULL_ATTACK_DAMAGE + SRPAttributes.HULL_A_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.HULL_KD_RESISTANCE + SRPAttributes.HULL_A_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.adaptedFollow);
    }

    private void hullDbg(String msg) {
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.onUpdate();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.onUpdate();
        }
        if (!this.worldObj.isRemote) {
            if (this.srpTicks == 10) {
                float currentH = this.getHealth() / this.getMaxHealth();
                if (this.getSSS()) {
                    this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.invisibility, 25, 1, false, false));
                    this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 25, 2, false, false));
                    if (this.ticksExisted % 2 == 0) {
                        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.HULL_C, 0.2f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                    }
                    if ((double)currentH < SRPConfigMobs.hulladaptedneededhealth) {
                        this.setSSS(false);
                    }
                } else if ((double)currentH >= SRPConfigMobs.hulladaptedneededhealth) {
                    ++this.timer;
                    if ((float)this.timer > SRPConfigMobs.hulladaptedneededtime) {
                        this.setSSS(true);
                        this.particleStatus((byte)6);
                        this.timer = 0;
                    }
                }
            }
            if (!this.canPull) {
                --this.pulling;
                if (this.pulling == 0) {
                    this.canPull = true;
                }
            }
            if (this.getAttackTarget() != null) {
                if (!this.getAttackTarget().isEntityAlive()) {
                    this.setAttackTarget(null);
                    this.setTargetedEntity(0);
                } else if (this.canEntityBeSeen((Entity)this.getAttackTarget()) && this.getDistanceSqToEntity((Entity)this.getAttackTarget()) > 0.0 && this.canPull && this.getTargetedEntity() != null) {
                    this.getAttackTarget().addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 20, 1, false, false));
                    this.getAttackTarget().addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.digSlowdown, 20, 1, false, false));
                    this.lookAt((Entity)this.getTargetedEntity());
                    this.attackEntityAsMobMinimum(this.getAttackTarget(), 0.02f);
                    this.setParasiteStatus(3);
                    ++this.pulling;
                    if (this.pulling > 200 || this.getDistanceSqToEntity((Entity)this.getAttackTarget()) > 9.0) {
                        this.setTargetedEntity(0);
                        this.canPull = false;
                    }
                } else {
                    this.setTargetedEntity(0);
                }
            } else {
                this.setTargetedEntity(0);
            }
        }
        if (this.getTargetedEntity() != null && this.getDistanceSqToEntity((Entity)this.getTargetedEntity()) > 0.0) {
            EntityLivingBase target = this.getTargetedEntity();
            target.dismountRidingEntity();
            double str = 0.3;
            double deltaX = this.posX - target.posX;
            double deltaY = this.posY - target.posY;
            double deltaZ = this.posZ - target.posZ;
            str = 0.13;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            target.motionX += (deltaX /= distance) * str;
            target.motionY += (deltaY /= distance) * str;
            target.motionZ += (deltaZ /= distance) * str;
        }
        if (!this.worldObj.isRemote) {
            this.tickCloakVibration();
        }
        if (!this.worldObj.isRemote) {
            EntityPlayer target = this.worldObj.getClosestPlayerToEntity((Entity)this, 48.0);
            boolean invisibleEnough = true;
            if (this.hullFakeCooldown > 0) {
                --this.hullFakeCooldown;
            } else if (target != null && invisibleEnough) {
                IllusionType type = this.pickIllusionType();
                boolean ok = this.hullPlayTypedIllusion_STRONG(target, type);
                this.hullDbg("Attempt type=" + (Object)((Object)type) + " -> " + (ok ? "OK" : "FAILED"));
                this.hullFakeCooldown = 400 + this.rand.nextInt(201);
            } else {
                this.hullFakeCooldown = 400 + this.rand.nextInt(201);
            }
        }
    }

    private boolean hullPlayTypedIllusion_STRONG(EntityPlayer player, IllusionType type) {
        List<String> pool;
        if (player == null) {
            return false;
        }
        IllusionProfile prof = this.profileFor(type);
        Vec3d pos = this.pickOffset(player, prof.minDist, prof.maxDist);
        BlockPos bp = new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord);
        if (!WorldAccess.isBlockLoaded(this.worldObj, bp)) {
            pos = new Vec3d((pos.xCoord + player.posX) * 0.5, pos.yCoord, (pos.zCoord + player.posZ) * 0.5);
            bp = new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord);
            if (!WorldAccess.isBlockLoaded(this.worldObj, bp)) {
                this.hullDbg("Chunk not loaded even after nudge; abort.");
                return false;
            }
        }
        if ((pool = this.hullBuildSoundPool(type)).isEmpty()) {
            String amb = this.getLivingSound();
            pool = Collections.singletonList(amb != null ? amb : SoundEvents.ENTITY_ENDERMEN_SCREAM);
            this.hullDbg("SRPSounds pool empty for " + (Object)((Object)type) + " \u2014 using fallback.");
        }
        String chosen = pool.get(this.rand.nextInt(pool.size()));
        float pitch = prof.basePitch + (this.rand.nextFloat() - 0.5f) * (prof.pitchJitter * 2.0f);
        IllusionLoudness loud = this.loudnessFor(type);
        float volume = EntityHullAdapted.clampf(loud.base + (this.rand.nextFloat() - 0.5f) * (loud.jitter * 2.0f), 0.01f, 1.0f);
        if (this.worldObj instanceof WorldServer) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, null, pos.xCoord, pos.yCoord, pos.zCoord, chosen, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, volume, pitch);
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)this.worldObj), EnumParticleTypes.SMOKE_LARGE, pos.xCoord, pos.yCoord + 0.2, pos.zCoord, 8, 0.08, 0.08, 0.08, 0.01, new int[0]);
        }
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacket((Packet)com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.packet(chosen, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, pos.xCoord, pos.yCoord, pos.zCoord, volume, pitch));
        }
        return true;
    }

    @Override
    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2 || k == 3) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2 || k == 3) {
                if (this.getAttackTarget() != null) {
                    if (!this.getAttackTarget().isEntityAlive()) {
                        this.setAttackTarget(null);
                        this.setParasiteStatus(0);
                    } else if (!this.canPull) {
                        this.setParasiteStatus(Math.min(k, 2));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setAttackTarget(null);
                }
            }
        }
    }

    public void setTargetedEntity(int entityId) {
        if (!this.canPull && entityId != 0) {
            return;
        }
        this.pulling = 0;
        this.canPull = true;
        EntityDataManager.of(this).set(TARGET_ENTITY, (Object)entityId);
    }

    public boolean hasTargetedEntity() {
        if (!this.canPull) {
            return false;
        }
        return (Integer)EntityDataManager.of(this).get(TARGET_ENTITY) != 0;
    }

    public EntityLivingBase getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.worldObj.isRemote) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(TARGET_ENTITY)).intValue());
            if (entity instanceof EntityLivingBase) {
                this.targetedEntity = (EntityLivingBase)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getAttackTarget();
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        this.setSSS(false);
        this.timer = 0;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.worldObj.isRemote) {
            return false;
        }
        boolean flag = this.attackEntityFrom(source, amount);
        if (!flag) {
            return false;
        }
        if (this.leftTendril.getId() == id) {
            this.leftTendrilHealth -= amount;
            if (this.leftTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(this.worldObj);
                tendril.setSkin(3);
                tendril.copyLocationAndAnglesFrom(this.leftTendril);
                this.worldObj.spawnEntityInWorld((Entity)tendril);
                this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
                this.worldObj.setEntityState((Entity)this, (byte)11);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                SRPMain.network.sendToAll((IMessage)new SRPPacketEntityBodyDead(this.getEntityId(), id));
            }
        } else if (this.rightTendril.getId() == id) {
            this.rightTendrilHealth -= amount;
            if (this.rightTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(this.worldObj);
                tendril.setSkin(3);
                tendril.copyLocationAndAnglesFrom(this.rightTendril);
                this.worldObj.spawnEntityInWorld((Entity)tendril);
                this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
                this.worldObj.setEntityState((Entity)this, (byte)22);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                SRPMain.network.sendToAll((IMessage)new SRPPacketEntityBodyDead(this.getEntityId(), id));
            }
        }
        return flag;
    }

    private IllusionType pickIllusionType() {
        float r = this.rand.nextFloat();
        return r < 0.55f ? IllusionType.FOOTSTEP : (r < 0.9f ? IllusionType.AMBIENT : IllusionType.GROWL);
    }

    private String srpSound(String fieldName) {
        try {
            Field f = SRPSounds.class.getField(fieldName);
            Object v = f.get(null);
            if (v instanceof String) {
                return (String)v;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    private List<String> srpFindMatchingSounds(String[] whoTokens, String[] whatTokens) {
        ArrayList<String> out = new ArrayList<String>();
        try {
            for (Field f : SRPSounds.class.getDeclaredFields()) {
                if (!String.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Object v = f.get(null);
                if (!(v instanceof String)) continue;
                String name = f.getName().toUpperCase();
                boolean whoOk = false;
                boolean whatOk = false;
                for (String w : whoTokens) {
                    if (!name.contains(w)) continue;
                    whoOk = true;
                    break;
                }
                for (String w : whatTokens) {
                    if (!name.contains(w)) continue;
                    whatOk = true;
                    break;
                }
                if (!whoOk || !whatOk) continue;
                out.add((String)v);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (out.isEmpty()) {
            String amb = this.getLivingSound();
            if (amb != null) {
                out.add(amb);
            } else {
                out.add(SoundEvents.ENTITY_ENDERMEN_SCREAM);
            }
        }
        return out;
    }

    private Vec3d pickOffset(EntityPlayer p, double minD, double maxD) {
        double dist = minD + this.rand.nextDouble() * (maxD - minD);
        Vec3d look = p.getLookVec().normalize();
        Vec3d side = look.crossProduct(new Vec3d(0.0, 1.0, 0.0)).normalize();
        Vec3d dir = this.rand.nextBoolean() ? look.scale(-1.0).add(side.scale((this.rand.nextDouble() - 0.5) * 0.6)).normalize() : side.scale(this.rand.nextBoolean() ? 1.0 : -1.0);
        return new Vec3d(p.posX + dir.xCoord * dist, p.posY + (double)(this.rand.nextInt(3) - 1), p.posZ + dir.zCoord * dist);
    }

    private boolean hullPlayTypedIllusion(EntityPlayer player, IllusionType type) {
        List<String> pool;
        if (player == null) {
            return false;
        }
        IllusionProfile prof = this.profileFor(type);
        Vec3d pos = this.pickOffset(player, prof.minDist, prof.maxDist);
        BlockPos bp = new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord);
        if (!WorldAccess.isBlockLoaded(this.worldObj, bp)) {
            pos = new Vec3d((pos.xCoord + player.posX) * 0.5, pos.yCoord, (pos.zCoord + player.posZ) * 0.5);
            bp = new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord);
            if (!WorldAccess.isBlockLoaded(this.worldObj, bp)) {
                return false;
            }
        }
        if ((pool = this.hullBuildSoundPool(type)).isEmpty()) {
            return false;
        }
        String chosen = pool.get(this.rand.nextInt(pool.size()));
        float pitch = prof.basePitch + (this.rand.nextFloat() - 0.5f) * (prof.pitchJitter * 2.0f);
        if (this.worldObj instanceof WorldServer) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(((WorldServer)this.worldObj), null, pos.xCoord, pos.yCoord, pos.zCoord, chosen, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 1.0f, pitch);
        }
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacket((Packet)com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.packet(chosen, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, pos.xCoord, pos.yCoord, pos.zCoord, 1.0f, pitch));
        }
        return true;
    }

    private IllusionProfile profileFor(IllusionType t) {
        switch (t) {
            case FOOTSTEP: {
                return new IllusionProfile(8.0, 14.0, 1.0f, 0.1f);
            }
            case AMBIENT: {
                return new IllusionProfile(12.0, 22.0, 1.0f, 0.12f);
            }
            case GROWL: {
                return new IllusionProfile(16.0, 28.0, 0.98f, 0.14f);
            }
        }
        return new IllusionProfile(12.0, 22.0, 1.0f, 0.12f);
    }

    private IllusionLoudness loudnessFor(IllusionType t) {
        switch (t) {
            case FOOTSTEP: {
                return new IllusionLoudness(0.35f, 0.08f);
            }
            case AMBIENT: {
                return new IllusionLoudness(0.4f, 0.1f);
            }
            case GROWL: {
                return new IllusionLoudness(0.5f, 0.12f);
            }
        }
        return new IllusionLoudness(0.4f, 0.1f);
    }

    private static float clampf(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        } else if (this.rightTendril.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            if (this.getSSS()) {
                float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() * SRPConfigMobs.hulladaptedstealthdamage;
                if (entityIn instanceof EntityLivingBase) {
                    f += EnchantmentHelper.getModifierForCreature((ItemStack)this.getHeldItemMainhand(), (EnumCreatureAttribute)((EntityLivingBase)entityIn).getCreatureAttribute());
                }
                entityIn.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), f);
                this.setSSS(false);
                this.timer = 0;
            }
            if (!this.hasTargetedEntity()) {
                this.setTargetedEntity(entityIn.getEntityId());
                ((EntityLivingBase)entityIn).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.weakness, 60, 3, false, false));
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobMinimum(EntityLivingBase entityIn, float damage) {
        boolean flag = super.attackEntityAsMobMinimum(entityIn, damage);
        if (flag && this.getSSS()) {
            this.setSSS(false);
            this.timer = 0;
        }
        return flag;
    }

    public float getEyeHeight() {
        return 2.0f;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityHull(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        }
        if (this.rightTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
        }
        super.setDead();
    }

    public boolean getSSS() {
        return (Boolean)EntityDataManager.of(this).get(CAM);
    }

    public void setSSS(boolean in) {
        EntityDataManager.of(this).set(CAM, (Object)in);
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0 || this.isPotionActive(net.minecraft.potion.Potion.invisibility)) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.AHULL_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.AHULL_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.AHULL_DEATH;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.hulladaptedOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.HEAVY_STEPS_TWO, 0.15f, 1.0f);
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.rand.nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("parasiteleftTendril", this.leftTendrilHealth);
        compound.setFloat("parasiterightTendril", this.rightTendrilHealth);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("parasiteleftTendril", 99)) {
            this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
            if (this.leftTendrilHealth <= 0.0f) {
                this.worldObj.setEntityState((Entity)this, (byte)11);
            }
        }
        if (compound.hasKey("parasiterightTendril", 99)) {
            this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
            if (this.rightTendrilHealth <= 0.0f) {
                this.worldObj.setEntityState((Entity)this, (byte)22);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public float getLeft() {
        return this.leftTendrilHealth;
    }

    @SideOnly(value=Side.CLIENT)
    public float getRight() {
        return this.rightTendrilHealth;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
        } else {
            super.handleHealthUpdate(id);
        }
    }

    private static boolean anyContains(String hay, String ... needles) {
        for (String n : needles) {
            if (!hay.contains(n)) continue;
            return true;
        }
        return false;
    }

    private List<String> hullBuildSoundPool(IllusionType type) {
        String[] what;
        String[] who = new String[]{"HULL", "MANDUCATOR"};
        switch (type) {
            case FOOTSTEP: {
                what = new String[]{"STEP", "FOOT", "WALK", "MOVE"};
                break;
            }
            case AMBIENT: {
                what = new String[]{"AMBIENT", "IDLE", "BREATH"};
                break;
            }
            case GROWL: {
                what = new String[]{"GROWL", "ROAR", "ALERT", "SNARL", "SCREAM"};
                break;
            }
            default: {
                what = new String[]{"AMBIENT"};
            }
        }
        ArrayList<String> out = new ArrayList<String>();
        try {
            for (Field f : SRPSounds.class.getDeclaredFields()) {
                boolean whatOk;
                if (!String.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Object v = f.get(null);
                if (!(v instanceof String)) continue;
                String s = (String)v;
                String fieldName = f.getName().toUpperCase();
                String regName = String.valueOf(s).toUpperCase();
                if (regName.contains("SILENCE") || fieldName.contains("SILENCE")) continue;
                boolean whoOk = EntityHullAdapted.anyContains(fieldName, who) || EntityHullAdapted.anyContains(regName, who);
                boolean bl = whatOk = EntityHullAdapted.anyContains(fieldName, what) || EntityHullAdapted.anyContains(regName, what);
                if (!whoOk || !whatOk) continue;
                out.add(s);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return out;
    }

    private void dumpSRPSoundsOnce() {
        if (this.hullDumpedSounds || this.worldObj.isRemote) {
            return;
        }
        this.hullDumpedSounds = true;
        StringBuilder sb = new StringBuilder("[Hull] SRPSounds detected:");
        try {
            for (Field f : SRPSounds.class.getDeclaredFields()) {
                if (!String.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Object v = f.get(null);
                if (!(v instanceof String)) continue;
                String s = (String)v;
                sb.append("\n - ").append(f.getName()).append(" -> ").append(s);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.out.println(sb.toString());
    }

    private static class IllusionLoudness {
        final float base;
        final float jitter;

        IllusionLoudness(float base, float jitter) {
            this.base = base;
            this.jitter = jitter;
        }
    }

    private static class IllusionProfile {
        final double minDist;
        final double maxDist;
        final float basePitch;
        final float pitchJitter;

        IllusionProfile(double minDist, double maxDist, float basePitch, float pitchJitter) {
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.basePitch = basePitch;
            this.pitchJitter = pitchJitter;
        }
    }

    private static enum IllusionType {
        FOOTSTEP,
        AMBIENT,
        GROWL;

    }

    private static enum CloakPhase {
        IDLE,
        PRE_CLOAK,
        CLOAKED,
        PRE_DECLOAK;

    }
}

