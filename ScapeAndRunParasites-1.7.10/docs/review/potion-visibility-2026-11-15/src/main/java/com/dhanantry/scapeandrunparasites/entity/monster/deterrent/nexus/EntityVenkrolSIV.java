/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Biomes
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BossInfo$Color
 *  net.minecraft.world.BossInfo$Overlay
 *  net.minecraft.world.BossInfoServer
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 */
package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIVenkrolSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.logic.VenkrolTornadoLogic;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class EntityVenkrolSIV
extends EntityPBeckon
implements EntityBodyParts {
    public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrolsivlimit, 20 * SRPConfigMobs.venkrolsivCooldown, 4, SRPConfigMobs.venkrolsivCAMinimumV, SRPConfigMobs.venkrolsivCAExtraM);
    private int count;
    private int tCount;
    private int ticksss;
    private EntityBody head;
    private final BossInfoServer bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS).setDarkenSky(false);

    public EntityVenkrolSIV(World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 6.9f);
        this.ignoreFrustumCheck = true;
        this.head = new EntityBody(this, 1.9f, 1.9f, 1.0f, 0.0f, 7.0f, -1, 1, false, 0.2f);
        this.totalP = SRPConfigMobs.venkrolsivTotalActiveMobs;
        this.mobID = new int[this.totalP + SRPConfigMobs.venkrolsivlimit];
        this.mobPT = new int[this.totalP + SRPConfigMobs.venkrolsivlimit];
        this.stage = (byte)4;
        this.experienceValue = SRPAttributes.XP_ADAPTED * 4;
        Arrays.fill(this.mobID, -777);
        this.setBODY(1.0f);
        this.tasks.addTask(2, (EntityAIBase)this.summonV);
        this.damageCap = SRPConfig.nexussivCap;
        this.count = 200;
        this.tCount = 300;
        this.ticksss = 0;
        this.pointCap = SRPConfig.nexussivPointCap;
        this.pointReduction = SRPConfig.nexussivPointRed;
        this.chanceLearn = SRPConfig.nexussivChanceLe;
        this.chanceLearnFire = SRPConfig.nexussivChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussivPointDamCap;
        this.valueEvDeath = SRPConfig.nexussivLoosingEPValue;
    }

    @Override
    public void setSkillBreakBlocksValues(float hardness, int heightIn, int rangeIn) {
        this.blockH = hardness;
        this.BGheight = heightIn + 2;
        this.BGrange = rangeIn;
    }

    @Override
    public int getParasiteIDRegister() {
        return 41;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.VENKROLSIV_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VENKROLSIV_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.VENKROLSIV_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.nexussivFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.head.onUpdate();
        --this.count;
        --this.tCount;
        if (this.getParasiteStatus() == 0) {
            this.setBODY(0.04f);
        } else {
            this.setBODY(-0.04f);
        }
        if (!this.worldObj.isRemote) {
            BlockPos pos = EntityQueries.position(this);
            if (SRPConfigWorld.venkrolTornadoEnabled && this.worldObj.isRaining() && this.worldObj.isThundering() && WorldAccess.canSeeSky(this.worldObj, pos.up())) {
                VenkrolTornadoLogic.tickTornadoEffects((EntityLivingBase)this);
            }
        }
        this.spawnThunder();
        this.placeBiome();
    }

    private void spawnThunder() {
        if (!this.worldObj.isRemote && this.rand.nextDouble() < 0.015) {
            BlockPos ray;
            if (this.tCount > 0) {
                return;
            }
            if (this.count < 0 && this.upgradeParasites()) {
                this.tCount = 300;
                this.count = 300;
                return;
            }
            int range = 64;
            double randomx = this.rand.nextInt(range);
            double randomz = this.rand.nextInt(range);
            double negative = this.rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)this.rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            if ((ray = ParasiteEventEntity.getFloor(this.worldObj, new BlockPos(randomx + this.posX, this.posY, randomz + this.posZ), 5)) != null) {
                if (SRPConfig.thunderEnable) {
                    this.worldObj.addWeatherEffect((Entity)new EntityLightningBolt(this.worldObj, (double)ray.getX(), (double)ray.getY(), (double)ray.getZ(), true));
                }
                this.tCount = 200;
            }
        }
    }

    private boolean upgradeParasites() {
        int count = 3;
        int current = 0;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 48.0, 34.0, 48.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityPInfected.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityPStationaryArchitect) || mob.getParasiteIDRegister() != 16 && mob.getParasiteIDRegister() != 18 && mob.getParasiteIDRegister() != 19) continue;
            ((EntityPStationaryArchitect)mob).setActualT(0);
        }
        if (moblist.size() > SRPConfig.nexussivCapUpgrade) {
            return true;
        }
        for (EntityParasiteBase mob : moblist) {
            if (mob.getParasiteIDRegister() == 64) continue;
            this.thunderParasite(mob, 1);
            if (++current < count) continue;
            return true;
        }
        if (current >= count) {
            return true;
        }
        moblist = EntityQueries.entities(this.worldObj, EntityPPrimitive.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            this.thunderParasite(mob, 2);
            if (++current < count) continue;
            return true;
        }
        return false;
    }

    private void thunderParasite(EntityParasiteBase in, int type) {
        if (!ParasiteEventEntity.canSpawnNext) {
            return;
        }
        switch (type) {
            case 1: {
                EntityParasiteBase out = ParasiteEventEntity.getRandomPrimitive(in.worldObj);
                if (SRPConfig.thunderEnable) {
                    in.worldObj.addWeatherEffect((Entity)new EntityLightningBolt(in.worldObj, in.posX, in.posY, in.posZ, true));
                }
                ParasiteEventEntity.spawnNext(in, out, true, true);
                break;
            }
            case 2: {
                if (SRPConfig.thunderEnable) {
                    in.worldObj.addWeatherEffect((Entity)new EntityLightningBolt(in.worldObj, in.posX, in.posY, in.posZ, true));
                }
                in.setKillC(in.getKillC() + 1000.0);
            }
        }
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    public void setMinecartName(String name) {
        super.setMinecartName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(EntityPlayerMP player) {
    }

    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag && source.getEntity() != null && source.getEntity() instanceof EntityPlayerMP) {
            this.bossInfo.addPlayer((EntityPlayerMP)source.getEntity());
        }
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.rand.nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)this, 80, 0);
        }
        return this.attackEntityFrom(source, amount * 3.0f);
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.head);
        }
        super.setDead();
    }

    public float getEyeHeight() {
        return 8.1f;
    }

    public void setBODY(float in) {
        if ((in += this.getBODY()) > 0.5f) {
            in = 0.5f;
        }
        if (in < 0.0f) {
            in = 0.0f;
        }
        this.body = in;
    }

    private void placeBiome() {
        if (this.ticksExisted < 1200) {
            return;
        }
        if (this.worldObj.isRemote) {
            return;
        }
        ++this.ticksss;
        if (this.ticksss < 20) {
            return;
        }
        this.ticksss = 0;
        if (ParasiteEventWorld.canBiomeStillExist(this.worldObj, EntityQueries.position(this), true) >= 1) {
            this.ticksss = -1000;
            return;
        }
        int range = 7;
        int attemp = 3;
        int mini = 5;
        while (attemp > 0) {
            --attemp;
            double randomx = this.rand.nextInt(range);
            double randomz = this.rand.nextInt(range);
            double negative = this.rand.nextInt(2);
            randomx = negative == 0.0 ? randomx * -1.0 - (double)mini : (randomx += (double)mini);
            negative = this.rand.nextInt(2);
            randomz = negative == 0.0 ? randomz * -1.0 - (double)mini : (randomz += (double)mini);
            BlockPos pos = new BlockPos(this.posX + randomx, this.posY, this.posZ + randomz);
            if ((pos = ParasiteEventEntity.getFloor(this.worldObj, pos, 5)) == null) continue;
            int typeB = 1;
            Biome biome = this.worldObj.getBiome(pos);
            if (biome == Biomes.extremeHills || biome == Biomes.hell || biome == Biomes.icePlains || biome == Biomes.iceMountains || biome == Biomes.stoneBeach || biome == Biomes.VOID) {
                typeB = 3;
            }
            if (ParasiteEventWorld.placeHeartInWorld(this.worldObj, pos, typeB) != 1) continue;
            this.ticksss = -1000;
            return;
        }
        this.ticksss = -100;
    }

    @Override
    public boolean canDespawn() {
        return SRPConfig.rsDespawn;
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.VENKROLSIV_ATTACK_DAMAGE;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityVenkrolSIII(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        ParasiteEventWorld.setDisloWorldPhase(this.worldObj, SRPAttributes.EVENTPARANEXUSIVD, SRPConfigSystems.chanceEventParaNexusIVD, 0, null);
        return super.onDeathDislo(cause);
    }
}

