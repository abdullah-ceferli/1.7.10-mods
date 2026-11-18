/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.BossInfo$Color
 *  net.minecraft.world.BossInfo$Overlay
 *  net.minecraft.world.BossInfoServer
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMovingSound;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class EntityParasiticScent
extends Entity {
    private final BossInfoServer bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS).setDarkenSky(false);
    private boolean phaseI = true;
    private byte scentState;
    private int lifeTicks = 600;
    private int currentL;
    private int dangerToUs = 100;
    private byte active;
    private int delay;
    private int timerTick;
    private byte scentLevel;
    private byte scentReaction;
    private int minwave;
    private int maxwave;
    private int minmob;
    private int maxmob;
    private boolean followTargetScent = false;
    private boolean dieAfterKilling;
    private boolean hasCheckedForOthers;
    private byte loopLife = (byte)103;
    private byte failing;
    private static final String SCENT_HOST_TAG = "SRPScentBuffed";
    private double originalHostMaxHealth = -1.0;
    private boolean hostBuffApplied = false;
    private EntityLivingBase targetScent;
    private EntityLivingBase hostLiving;

    public void setMinecartName(String name) {
        super.setMinecartName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
        List playerEntityList = EntityQueries.players(this.worldObj);
        for (EntityPlayer entityPlayer : playerEntityList) {
            SRPMain.network.sendToDimension((IMessage)new SRPPacketMovingSound(102), this.worldObj.provider.dimensionId);
        }
    }

    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
        List playerEntityList = EntityQueries.players(this.worldObj);
        for (EntityPlayer entityPlayer : playerEntityList) {
            SRPMain.network.sendToDimension((IMessage)new SRPPacketMovingSound(103), this.worldObj.provider.dimensionId);
        }
    }

    public EntityParasiticScent(World worldIn) {
        super(worldIn);
    }

    public EntityParasiticScent(World worldIn, int status) {
        this(worldIn);
        this.scentState = (byte)status;
    }

    public EntityParasiticScent(World worldIn, int status, EntityLivingBase tar) {
        this(worldIn, status);
        this.setTargetToKill(tar, false);
    }

    protected void entityInit() {
    }

    public void onUpdate() {
        EntityPlayer pa;
        super.onUpdate();
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.phaseI) {
            if (this.currentL != 600) {
                ++this.currentL;
            }
            if (this.hostLiving == null) {
                AxisAlignedBB axisalignedbb;
                List moblist;
                if (this.currentL % 20 == 0 && this.currentL >= 300 && !(moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 80.0))).isEmpty()) {
                    EntityParasiteBase pickedHost = (EntityParasiteBase)moblist.get(this.worldObj.rand.nextInt(moblist.size()));
                    if (pickedHost.getEntityData().getBoolean(SCENT_HOST_TAG)) {
                        return;
                    }
                    this.hostLiving = pickedHost;
                    this.hostLiving.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(MobEffects.GLOWING, this.currentL, 3, false, true));
                    if (this.hostLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null) {
                        this.originalHostMaxHealth = this.hostLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
                        double newMax = this.originalHostMaxHealth * 10.0;
                        this.hostLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(newMax);
                        this.hostLiving.setHealth((float)newMax);
                        this.hostLiving.getEntityData().setBoolean(SCENT_HOST_TAG, true);
                        this.hostBuffApplied = true;
                    }
                }
            } else if (!this.hostLiving.isEntityAlive()) {
                this.setDead();
                return;
            }
            this.bossInfo.setPercent(((float)this.currentL + 1.0f) / (float)this.lifeTicks);
            if (this.currentL == 600) {
                --this.dangerToUs;
                if (this.dangerToUs == 0) {
                    this.phaseI = false;
                }
            }
            return;
        }
        float hea = ((float)this.currentL + 1.0f) / (float)this.lifeTicks;
        this.bossInfo.setPercent(hea);
        if (this.scentState >= 5) {
            this.currentL -= 20;
        }
        if (this.currentL <= 0 || DifficultyAccess.difficulty(this.worldObj) == EnumDifficulty.PEACEFUL || this.loopLife < 0 || this.hostLiving == null) {
            this.setDead();
            return;
        }
        if (!this.hostLiving.isEntityAlive()) {
            this.setDead();
            return;
        }
        if (this.getTargetToKill() == null) {
            if (this.scentState > 1) {
                this.scentState = 1;
                return;
            }
        } else if (this.getTargetToKill().getDistanceSqToEntity((Entity)this) > 4096.0) {
            if (this.dieAfterKilling) {
                this.setDead();
                return;
            }
            this.setTargetToKill(null, false);
            this.scentState = 1;
            return;
        }
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        this.scentFollower();
        if (this.getTargetToKill() instanceof EntityPlayer && ((pa = (EntityPlayer)this.getTargetToKill()).isSpectator() || pa.isCreative())) {
            this.setTargetToKill(null, false);
            return;
        }
        switch (this.scentState) {
            case 0: {
                if (this.rand.nextInt(3) == 0) {
                    this.scentObserver();
                }
                this.scentListener();
                break;
            }
            case 1: {
                this.scentObserver();
                this.scentListener();
                break;
            }
            case 4: {
                this.scentTactical();
                break;
            }
            case 5: {
                this.scentAttacker();
                break;
            }
            case 6: {
                this.scentBuilder();
            }
        }
    }

    private void scentListener() {
        if (this.active >= this.scentReaction && this.getTargetToKill() != null) {
            this.scentState = (byte)4;
            this.active = (byte)2;
            this.warnPlayers(I18n.translateToLocal((String)"srp.msg.scent.active"));
        }
    }

    private void scentObserver() {
        if (this.ticksExisted % 20 != 0) {
            return;
        }
        if (this.followTargetScent) {
            return;
        }
        if (this.getTargetToKill() != null) {
            this.active = (byte)(this.active + 1);
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 80.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
            for (EntityParasiteBase mob : moblist) {
                double ra;
                if (!mob.isEntityAlive()) continue;
                if (mob.getAttackTarget() == null) {
                    ra = mob.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
                    if (!(mob.getDistanceSqToEntity((Entity)this.getTargetToKill()) < ra * ra)) continue;
                    mob.setAttackTarget(this.getTargetToKill());
                    continue;
                }
                if (mob.getAttackTarget().isEntityAlive()) continue;
                ra = mob.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
                if (!(mob.getDistanceSqToEntity((Entity)this.getTargetToKill()) < ra * ra)) continue;
                mob.setAttackTarget(this.getTargetToKill());
            }
            return;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 80.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
        int parasites = moblist.size();
        if (parasites < (moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb)).size()) {
            EntityLivingBase target = null;
            double dis = 40000.0;
            for (EntityLivingBase mob : moblist) {
                EntityPlayer qqq;
                double atm;
                if (mob instanceof EntityParasiteBase || mob instanceof EntityWaterMob || mob instanceof EntityCreeper || (atm = mob.getDistanceSqToEntity((Entity)this)) > 4096.0 || !this.checkAttri(mob) || mob instanceof EntityPlayer && ((qqq = (EntityPlayer)mob).isCreative() || qqq.isSpectator()) || !(atm < dis)) continue;
                dis = atm;
                target = mob;
            }
            this.setTargetToKill(target, false);
        }
    }

    private void scentTactical() {
        if (this.active >= SRPConfigSystems.scentSpawnWaves) {
            this.scentState = (byte)5;
            this.active = (byte)(this.active - 5);
            return;
        }
        if (this.getTargetToKill() != null) {
            this.active = (byte)(this.active + 1);
        } else if (this.ticksExisted % 80 == 0) {
            this.active = (byte)(this.active - 1);
            if (this.active <= 0) {
                this.scentState = 1;
                return;
            }
        }
    }

    private void scentAttacker() {
        if (this.checkNearby() <= 6) {
            this.scentState = (byte)6;
        } else {
            this.delay = 100;
            this.scentState = (byte)4;
        }
    }

    private void scentBuilder() {
        this.worldObj.playSound(null, this.posX, this.posY, this.posZ, SRPSounds.SCENTWAVE, this.getSoundCategory(), 10.0f, 1.0f);
        this.worldObj.playSound(null, this.getTargetToKill().posX, this.getTargetToKill().posY, this.getTargetToKill().posZ, SRPSounds.SCENTWAVE, this.getTargetToKill().getSoundCategory(), 10.0f, 1.0f);
        this.loopLife = (byte)(this.loopLife - 1);
        int a = 0;
        int horde = this.maxMinInt(this.minwave, this.maxwave);
        for (int limit = 0; limit < 10; ++limit) {
            if ((a += this.placeWaves(SRPConfigSystems.scentMiniDis, SRPConfigSystems.scentMaxDis)) < horde) continue;
            this.delay = 100 + a * 20;
            this.scentState = (byte)4;
            return;
        }
        this.delay = 200;
        if (a <= 0) {
            this.delay = 100;
        }
    }

    private void scentFollower() {
        if (this.followTargetScent) {
            if (this.getTargetToKill() != null) {
                if (this.getTargetToKill().getDistanceSqToEntity((Entity)this) > 144.0 && this.getTargetToKill().isPotionActive(SRPPotions.PREY_E)) {
                    this.copyLocationAndAnglesFrom((Entity)this.getTargetToKill());
                }
            } else {
                this.setTargetToKill(null, false);
                this.followTargetScent = false;
                return;
            }
        }
    }

    public boolean getCanFollow() {
        return this.followTargetScent;
    }

    public void setCanFollow(boolean in) {
        this.followTargetScent = in;
    }

    public boolean getDieToE() {
        return this.dieAfterKilling;
    }

    public void setDieToE(boolean in) {
        this.dieAfterKilling = in;
    }

    public void setScentState(int in) {
        this.scentState = (byte)in;
    }

    public byte getScentState() {
        return this.scentState;
    }

    public void setScentLife(int in) {
        this.lifeTicks = in;
    }

    public int getScentLife() {
        return this.lifeTicks;
    }

    public boolean setTargetToKill(EntityLivingBase in, boolean checkATT) {
        if (this.followTargetScent) {
            return false;
        }
        if (in == null) {
            return false;
        }
        if (!in.isEntityAlive()) {
            return false;
        }
        if (checkATT) {
            if (this.checkAttri(in)) {
                this.targetScent = in;
                return true;
            }
            return false;
        }
        this.targetScent = in;
        return true;
    }

    private boolean checkAttri(EntityLivingBase in) {
        if (in == null) {
            return false;
        }
        int cond = 0;
        if (in.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null && in.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue() >= SRPConfigSystems.minAttriHealth) {
            ++cond;
        }
        if (in.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null && in.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue() >= SRPConfigSystems.minAttriArmor) {
            ++cond;
        }
        if (in.getEntityAttribute(SharedMonsterAttributes.attackDamage) != null && in.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() >= SRPConfigSystems.minAttriDamage) {
            ++cond;
        }
        return cond >= SRPConfigSystems.minAttriFailCount || cond == 0;
    }

    public EntityLivingBase getTargetToKill() {
        if (this.targetScent == null) {
            return null;
        }
        if (!this.targetScent.isEntityAlive()) {
            this.targetScent = null;
            return null;
        }
        return this.targetScent;
    }

    public void increaseDanger(int in, boolean plus) {
        this.dangerToUs = plus ? (this.dangerToUs += in) : in;
        this.updateScentOLevel();
    }

    private void updateScentOLevel() {
        if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsEight) {
            this.scentLevel = (byte)8;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveEight;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveEight;
            this.maxwave = SRPConfigSystems.scentWaveMaximumEight;
            this.minwave = SRPConfigSystems.scentWaveMinimumEight;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsSeven) {
            this.scentLevel = (byte)7;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveSeven;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveSeven;
            this.maxwave = SRPConfigSystems.scentWaveMaximumSeven;
            this.minwave = SRPConfigSystems.scentWaveMinimumSeven;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsSix) {
            this.scentLevel = (byte)6;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveSix;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveSix;
            this.maxwave = SRPConfigSystems.scentWaveMaximumSix;
            this.minwave = SRPConfigSystems.scentWaveMinimumSix;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsFive) {
            this.scentLevel = (byte)5;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveFive;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveFive;
            this.maxwave = SRPConfigSystems.scentWaveMaximumFive;
            this.minwave = SRPConfigSystems.scentWaveMinimumFive;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsFour) {
            this.scentLevel = (byte)4;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveFour;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveFour;
            this.maxwave = SRPConfigSystems.scentWaveMaximumFour;
            this.minwave = SRPConfigSystems.scentWaveMinimumFour;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsThree) {
            this.scentLevel = (byte)3;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveThree;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveThree;
            this.maxwave = SRPConfigSystems.scentWaveMaximumThree;
            this.minwave = SRPConfigSystems.scentWaveMinimumThree;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsTwo) {
            this.scentLevel = (byte)2;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveTwo;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveTwo;
            this.maxwave = SRPConfigSystems.scentWaveMaximumTwo;
            this.minwave = SRPConfigSystems.scentWaveMinimumTwo;
        } else if (this.dangerToUs >= SRPConfigSystems.scentLevelPointsOne) {
            this.scentLevel = 1;
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveOne;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveOne;
            this.maxwave = SRPConfigSystems.scentWaveMaximumOne;
            this.minwave = SRPConfigSystems.scentWaveMinimumOne;
        } else {
            this.maxmob = SRPConfigSystems.scentWaveMaxMobWaveZero;
            this.minmob = SRPConfigSystems.scentWaveMinMobWaveZero;
            this.maxwave = SRPConfigSystems.scentWaveMaximumZero;
            this.minwave = SRPConfigSystems.scentWaveMinimumZero;
        }
    }

    public int getDanger() {
        return this.dangerToUs;
    }

    public void increaseActivity(int in, boolean plus) {
        if (in > 100) {
            return;
        }
        this.active = plus ? (byte)(this.active + (byte)in) : (byte)in;
    }

    private int checkNearby() {
        int i = 0;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 80.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (mob.motionX == 0.0 || mob.motionY == 0.0 || mob.motionZ == 0.0 || mob.getAttackTarget() != null || !mob.isEntityAlive() || this.worldObj.getLightFor(EnumSkyBlock.BLOCK, EntityQueries.position(this)) >= 5 || mob instanceof EntityPStationary || mob instanceof EntityBiomass || mob.hurtTime > 0 || !this.moveMobToLoc(mob)) continue;
            mob.setAttackTarget(this.getTargetToKill());
            ++i;
        }
        List serverList = EntityQueries.loaded(this.worldObj);
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        int players = EntityQueries.players(this.worldObj).size();
        if (count > SRPConfig.worldMobCap + (players *= SRPConfig.worldMobCapPlusPlayer)) {
            return 20;
        }
        return i;
    }

    private boolean moveMobToLoc(EntityParasiteBase in) {
        int minDist = SRPConfigSystems.scentMiniDis;
        int maxDist = SRPConfigSystems.scentMaxDis;
        int loop = 0;
        if (loop < 7) {
            ++loop;
            int range = maxDist * 2;
            int tryX = (int)Math.floor(this.getTargetToKill().posX - (double)range / 2.0 + (double)this.rand.nextInt(range));
            int tryY = (int)this.getTargetToKill().posY;
            int tryZ = (int)Math.floor(this.getTargetToKill().posZ - (double)range / 2.0 + (double)this.rand.nextInt(range));
            BlockPos poss = new BlockPos(tryX, tryY, tryZ);
            if ((poss = ParasiteEventEntity.getFloor(this.worldObj, poss, 10)) == null) {
                return false;
            }
            if (this.worldObj.getLightFor(EnumSkyBlock.BLOCK, poss) > 4) {
                return false;
            }
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)tryX, (double)tryY, (double)tryZ, (double)(tryX + 1), (double)(tryY + 1), (double)(tryZ + 1)), (double)minDist, 5.0, (double)minDist);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                return false;
            }
            if (this.getDistance(poss.getX(), poss.getY(), poss.getZ()) < (double)minDist || this.getDistance(poss.getX(), poss.getY(), poss.getZ()) > (double)maxDist) {
                return false;
            }
            in.setPosition((double)poss.getX() + 0.5, poss.getY(), (double)poss.getZ() + 0.5);
            return true;
        }
        return false;
    }

    public int placeWaves(int minDist, int maxDist) {
        if (this.getTargetToKill() == null) {
            return 0;
        }
        if (!SRPConfigSystems.useScent) {
            this.setDead();
            return 0;
        }
        int range = maxDist * 2;
        int tryX = (int)Math.floor(this.getTargetToKill().posX - (double)range / 2.0 + (double)this.rand.nextInt(range));
        int tryY = (int)this.getTargetToKill().posY;
        int tryZ = (int)Math.floor(this.getTargetToKill().posZ - (double)range / 2.0 + (double)this.rand.nextInt(range));
        BlockPos poss = new BlockPos(tryX, tryY, tryZ);
        if ((poss = ParasiteEventEntity.getFloor(this.worldObj, poss, 10)) == null) {
            return 0;
        }
        if (this.getDistance(poss.getX(), poss.getY(), poss.getZ()) < (double)minDist || this.getDistance(poss.getX(), poss.getY(), poss.getZ()) > (double)SRPConfigSystems.oneMinRangeCap) {
            return 0;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)poss.getX(), (double)poss.getY(), (double)poss.getZ(), (double)(poss.getX() + 1), (double)(poss.getY() + 1), (double)(poss.getZ() + 1)), (double)maxDist, 16.0, (double)maxDist);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        int living = moblist.size();
        moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
        List serverList = EntityQueries.loaded(this.worldObj);
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        int players = EntityQueries.players(this.worldObj).size();
        if (count > SRPConfig.worldMobCap + (players *= SRPConfig.worldMobCapPlusPlayer)) {
            return 0;
        }
        if (living == moblist.size()) {
            return 0;
        }
        axisalignedbb = Boxes.grow(Boxes.create((double)poss.getX(), (double)poss.getY(), (double)poss.getZ(), (double)(poss.getX() + 1), (double)(poss.getY() + 1), (double)(poss.getZ() + 1)), (double)minDist, 5.0, (double)minDist);
        moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        this.updateScentOLevel();
        ParasiteEventEntity.spawnUnitFromRof(this.worldObj, this.getTargetToKill(), poss, this.getMob(), this.minmob, this.maxmob);
        return 1;
    }

    private String[] getMob() {
        String[] mob = new String[]{"minecraft:zombie"};
        switch (this.getareaValue()) {
            case 0: {
                mob = SRPConfigSystems.scentLevelZero;
                break;
            }
            case 1: {
                mob = SRPConfigSystems.scentLevelOne;
                break;
            }
            case 2: {
                mob = SRPConfigSystems.scentLevelTwo;
                break;
            }
            case 3: {
                mob = SRPConfigSystems.scentLevelThree;
                break;
            }
            case 4: {
                mob = SRPConfigSystems.scentLevelFour;
                break;
            }
            case 5: {
                mob = SRPConfigSystems.scentLevelFive;
                break;
            }
            case 6: {
                mob = SRPConfigSystems.scentLevelSix;
                break;
            }
            case 7: {
                mob = SRPConfigSystems.scentLevelSeven;
                break;
            }
            case 8: {
                mob = SRPConfigSystems.scentLevelEight;
            }
        }
        return mob;
    }

    private byte getareaValue() {
        byte i = this.scentLevel;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 80.0);
        List moblist1 = EntityQueries.entities(this.worldObj, EntityParasiticScent.class, axisalignedbb);
        for (EntityParasiticScent mob : moblist1) {
            if (mob.getScentLevel() <= i) continue;
            i = mob.getScentLevel();
        }
        return i;
    }

    public void setScentReaction(byte in, boolean override) {
        if (override) {
            this.scentReaction = in;
        } else if (in > this.scentReaction) {
            this.scentReaction = in;
        }
    }

    public byte getScentLevel() {
        return this.scentLevel;
    }

    public void setScentLevel(int in) {
        this.scentLevel = (byte)in;
    }

    public void warnPlayers(String in) {
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 80.0);
        List moblist1 = EntityQueries.entities(this.worldObj, EntityPlayer.class, axisalignedbb);
        for (EntityPlayer mob : moblist1) {
            mob.addChatComponentMessage((IChatComponent)new ChatComponentText(in), true);
        }
    }

    private int maxMinInt(int min, int max) {
        int atm = max - min + 1;
        return this.rand.nextInt(atm) + min;
    }

    public boolean checkIfScentAlone(EntityParasiticScent scent) {
        ArrayList<BlockPos> scentsLoadedPositions = new ArrayList<BlockPos>();
        List serverList = EntityQueries.loaded(this.worldObj);
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiticScent) || entity == scent) continue;
            scentsLoadedPositions.add(EntityQueries.position(entity));
        }
        for (BlockPos s : scentsLoadedPositions) {
            if (!(scent.getDistance(s.getX(), s.getY(), s.getZ()) <= (double)SRPConfigSystems.scentSpacing)) continue;
            return false;
        }
        return true;
    }

    public void applyEntityCollision(Entity entityIn) {
        super.applyEntityCollision(entityIn);
    }

    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (this.ticksExisted % 20 == 0 && !entityIn.worldObj.isRemote) {
            System.out.println("\n scent state " + this.scentState + "\n active " + this.active + "\n reaction " + this.scentReaction + "\n danger " + this.dangerToUs + "\n delay " + this.delay + "\n total life " + this.lifeTicks + "\n current life " + this.currentL + "\n targetScent is null " + (this.getTargetToKill() == null) + "\n level " + this.scentLevel + "\n targetScent name " + (this.getTargetToKill() != null ? this.getTargetToKill().getClass().toString() : " b ") + "\n min max mob " + this.minmob + " " + this.maxmob + "\n loop " + this.loopLife + "\n fail " + this.failing);
        }
        super.onCollideWithPlayer(entityIn);
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("parasitehostbuffapplied", 1)) {
            this.hostBuffApplied = compound.getBoolean("parasitehostbuffapplied");
        }
        if (compound.hasKey("parasitehostoriginalmax", 6)) {
            this.originalHostMaxHealth = compound.getDouble("parasitehostoriginalmax");
        }
        if (compound.hasKey("parasitescenttype", 99)) {
            this.scentState = compound.getByte("parasitescenttype");
        }
        if (compound.hasKey("parasitescentactive", 99)) {
            this.active = compound.getByte("parasitescentactive");
        }
        if (compound.hasKey("parasitescentlevel", 99)) {
            this.scentLevel = compound.getByte("parasitescentlevel");
        }
        if (compound.hasKey("parasitelifespan", 99)) {
            this.lifeTicks = compound.getInteger("parasitelifespan");
        }
        if (compound.hasKey("parasitelifecurrent", 99)) {
            this.currentL = compound.getInteger("parasitelifecurrent");
        }
        if (compound.hasKey("parasitedangerous", 99)) {
            this.dangerToUs = compound.getInteger("parasitedangerous");
        }
        if (compound.hasKey("parasitedelay", 99)) {
            this.delay = compound.getInteger("parasitedelay");
        }
        if (compound.hasKey("parasitescentreaction", 99)) {
            this.scentReaction = compound.getByte("parasitescentreaction");
        }
        if (compound.hasKey("parasitescentloopf", 99)) {
            this.loopLife = compound.getByte("parasitescentloopf");
        }
        if (compound.hasKey("parasitescentfailing", 99)) {
            this.failing = compound.getByte("parasitescentfailing");
        }
        if (compound.hasKey("parasitescentdyeing", 99)) {
            this.dieAfterKilling = compound.getBoolean("parasitescentdyeing");
        }
        if (compound.hasKey("parasitescentfollowing", 99)) {
            this.followTargetScent = compound.getBoolean("parasitescentfollowing");
        }
        if (compound.hasKey("parasitescentphase", 99)) {
            this.phaseI = compound.getBoolean("parasitescentphase");
        }
        if (compound.hasKey("parasitehost", 99)) {
            this.hostLiving = (EntityLivingBase)this.worldObj.getEntityByID(compound.getInteger("parasitehost"));
        }
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setBoolean("parasitehostbuffapplied", this.hostBuffApplied);
        compound.setDouble("parasitehostoriginalmax", this.originalHostMaxHealth);
        compound.setByte("parasitescenttype", this.scentState);
        compound.setByte("parasitescentactive", this.active);
        compound.setByte("parasitescentlevel", this.scentLevel);
        compound.setInteger("parasitelifespan", this.lifeTicks);
        compound.setInteger("parasitelifecurrent", this.currentL);
        compound.setInteger("parasitedangerous", this.dangerToUs);
        compound.setInteger("parasitedelay", this.delay);
        compound.setByte("parasitescentreaction", this.scentReaction);
        compound.setByte("parasitescentloopf", this.loopLife);
        compound.setByte("parasitescentfailing", this.failing);
        compound.setBoolean("parasitescentdyeing", this.dieAfterKilling);
        compound.setBoolean("parasitescentfollowing", this.followTargetScent);
        compound.setBoolean("parasitescentphase", this.phaseI);
        if (this.hostLiving != null) {
            compound.setInteger("parasitehost", this.hostLiving.getEntityId());
        }
    }

    private void cleanupHostBuff() {
        if (!this.hostBuffApplied || this.hostLiving == null) {
            return;
        }
        if (this.hostLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null && this.hostLiving.isEntityAlive()) {
            double restore = this.originalHostMaxHealth > 0.0 ? this.originalHostMaxHealth : this.hostLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() / 10.0;
            this.hostLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(restore);
            if ((double)this.hostLiving.getHealth() > restore) {
                this.hostLiving.setHealth((float)restore);
            }
        }
        this.hostLiving.getEntityData().removeTag(SCENT_HOST_TAG);
        this.hostBuffApplied = false;
        this.originalHostMaxHealth = -1.0;
    }

    public void setDead() {
        if (!this.worldObj.isRemote) {
            this.cleanupHostBuff();
        }
        super.setDead();
    }
}

