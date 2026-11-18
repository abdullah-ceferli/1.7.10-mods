/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAITarget
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

public class EntityAIDodAttack
extends EntityAIBase {
    private final EntityPDispatcher parent;
    private byte stage;
    private double tickss;
    private byte maxID;
    private int maxDodT;
    private ArrayList<Integer> resistanceI = new ArrayList();
    float maxHardness;
    private int cooldownNak;

    public EntityAIDodAttack(EntityPDispatcher dod, int CURRENTstage, byte limit, float hardness) {
        this.parent = dod;
        this.tickss = 20.0;
        this.stage = (byte)CURRENTstage;
        this.maxID = limit;
        this.maxDodT = CURRENTstage * 3;
        this.maxHardness = hardness;
        this.cooldownNak = 0;
    }

    public boolean shouldExecute() {
        this.tickss -= 1.0;
        --this.cooldownNak;
        if (this.tickss < 0.0) {
            this.tickss = 80.0;
            if (this.parent.getAttackTarget() != null) {
                this.tickss = 10.0;
            }
            return true;
        }
        return false;
    }

    public void resetTask() {
    }

    public void updateTask() {
        double follow = 0.0;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0, this.parent.posY + 1.0, this.parent.posZ + 1.0), this.parent.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue());
        EntityLivingBase target = this.parent.getAttackTarget();
        this.unhide(axisalignedbb);
        this.storePara(axisalignedbb);
        if (target == null) {
            this.findT(follow, axisalignedbb);
        } else {
            if (!this.parent.getMobList().isEmpty() && this.parent.worldObj.rand.nextInt(3) != 0) {
                int sel = this.parent.worldObj.rand.nextInt(this.stage * 2);
                if (sel < this.parent.getMobList().size()) {
                    for (int i = 0; i <= sel; ++i) {
                        this.summonTentacles(target, follow, axisalignedbb, true);
                    }
                }
            } else {
                switch (this.stage) {
                    case 1: {
                        this.parent.checkID();
                        if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(this.parent.worldObj), 3, 1, true)) {
                            // empty if block
                        }
                        break;
                    }
                    case 2: {
                        this.parent.checkID();
                        if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(this.parent.worldObj), 3, 1, true)) {
                            // empty if block
                        }
                        break;
                    }
                    case 3: {
                        if (this.parent.worldObj.rand.nextBoolean()) {
                            this.parent.checkID();
                            if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.spawnDet(target, new EntityUnvo(this.parent.worldObj), 6, 4, true)) {
                                // empty if block
                            }
                        } else {
                            this.parent.checkID();
                            if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(this.parent.worldObj), 3, 1, true)) {
                                // empty if block
                            }
                        }
                        break;
                    }
                    case 4: {
                        if (this.parent.worldObj.rand.nextBoolean()) {
                            this.parent.checkID();
                            if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.spawnDet(target, new EntityUnvo(this.parent.worldObj), 6, 4, true)) {
                                // empty if block
                            }
                            break;
                        }
                        this.parent.checkID();
                        if (this.parent.getActualParasites() >= this.parent.getTotalParasites() || !this.checkNak() || this.spawnDet(target, new EntityNak(this.parent.worldObj), 3, 1, true)) {
                            // empty if block
                        } else {
                            break;
                        }
                    }
                }
            }
            if (!target.isEntityAlive()) {
                this.parent.setAttackTarget(null);
            }
        }
    }

    private void findT(double follow, AxisAlignedBB axisalignedbb) {
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob == null) continue;
            if (mob instanceof EntityPlayer) {
                if (!this.canAttackPlayer((EntityPlayer)mob)) continue;
                this.parent.setAttackTarget(mob);
                return;
            }
            if (!(mob instanceof EntityLiving) || ParasiteEventEntity.checkEntity((EntityLivingBase)((EntityLiving)mob), SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite) || mob == this.parent || !this.canTargetEntity(mob) || !(mob.getDistanceSqToEntity((Entity)this.parent) < 1024.0) || !mob.isEntityAlive() || !this.parent.canAttackClass(mob.getClass())) continue;
            this.parent.setAttackTarget(mob);
            return;
        }
    }

    private boolean canTargetEntity(EntityLivingBase in) {
        return !(in instanceof EntityParasiteBase) && !(in instanceof EntityAnimal) && !(in instanceof EntityCreeper) && !(in instanceof EntityWaterMob);
    }

    private boolean canAttackPlayer(EntityPlayer in) {
        if (in.capabilities.disableDamage) {
            return false;
        }
        return EntityAITarget.isSuitableTarget((EntityLiving)this.parent, (EntityLivingBase)in, (boolean)false, (boolean)true);
    }

    private void summonTentacles(EntityLivingBase in, double follow, AxisAlignedBB axisalignedbb, boolean dod) {
        if (this.parent.worldObj.rand.nextInt(2) != 0) {
            return;
        }
        this.parent.checkID();
        this.checkdodts();
        if (this.resistanceI.size() >= this.maxDodT && dod) {
            return;
        }
        if (this.parent.getActualParasites() >= this.parent.getTotalParasites() && this.parent.getMobList().size() <= 0 || this.spawnDet(in, new EntityDodT(this.parent.worldObj), 5, 3, dod)) {
            // empty if block
        }
    }

    private void checkdodts() {
        for (int i = 0; i < this.resistanceI.size(); ++i) {
            Entity flag = this.parent.worldObj.getEntityByID(this.resistanceI.get(i).intValue());
            if (flag == null) {
                this.resistanceI.remove(i);
                i = 0;
                continue;
            }
            if (flag instanceof EntityDodT) {
                EntityDodT flagg = (EntityDodT)flag;
                if (flagg.isEntityAlive()) continue;
                this.resistanceI.remove(i);
                i = 0;
                continue;
            }
            this.resistanceI.remove(i);
            i = 0;
        }
    }

    private boolean spawnDet(EntityLivingBase in, EntityPStationary entityout, int max, int mini, boolean addId) {
        boolean tele;
        int x;
        int count;
        List serverList;
        boolean flagN;
        boolean bl = flagN = entityout.getParasiteIDRegister() == 72;
        if (flagN) {
            serverList = EntityQueries.loaded(this.parent.worldObj);
            count = 0;
            for (x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityNak)) continue;
                ++count;
            }
            if (count >= 7) {
                entityout.setDead();
                return false;
            }
            if (this.cooldownNak > 0) {
                entityout.setDead();
                return false;
            }
        }
        if (entityout.getParasiteIDRegister() == 30) {
            serverList = EntityQueries.loaded(this.parent.worldObj);
            count = 0;
            for (x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityUnvo)) continue;
                ++count;
            }
            if (count >= 7) {
                return false;
            }
        }
        boolean bl2 = tele = entityout.getParasiteIDRegister() == 74;
        if (tele) {
            List serverList2 = EntityQueries.loaded(this.parent.worldObj);
            int count2 = 0;
            for (int x2 = 0; x2 < serverList2.size(); ++x2) {
                if (!(serverList2.get(x2) instanceof EntityParasiteBase)) continue;
                ++count2;
            }
            if (count2 >= SRPConfig.worldMobCap) {
                return false;
            }
        }
        if (this.parent.getMobList().size() <= 0 && tele) {
            entityout.setDead();
            return false;
        }
        Random rand = new Random();
        double x3 = in.posX;
        double y = in.posY;
        double z = in.posZ;
        double randomx = rand.nextInt(max) + mini;
        double randomz = rand.nextInt(max) + mini;
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int limit = 0;
        boolean flag = true;
        while (flag) {
            if (limit >= 5) {
                entityout.setDead();
                return false;
            }
            BlockPos pos = new BlockPos(x3 + randomx, y, z + randomz);
            if ((pos = ParasiteEventEntity.getFloor(this.parent.worldObj, pos, 5)) != null) {
                IBlockState state = BlockStates.get(this.parent.worldObj, pos);
                if (BlockStates.get(this.parent.worldObj, pos.down()).isFullCube()) {
                    float bHard = 0.0f;
                    IBlockState state2 = BlockStates.get(this.parent.worldObj, pos.down());
                    float atm = state2.getBlockHardness(this.parent.worldObj, pos.down());
                    if (atm <= 0.0f) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    bHard += atm;
                    state2 = BlockStates.get(this.parent.worldObj, pos.down(2));
                    atm = state2.getBlockHardness(this.parent.worldObj, pos.down(2));
                    if (atm <= 0.0f) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    bHard += atm;
                    state2 = BlockStates.get(this.parent.worldObj, pos.down(3));
                    atm = state2.getBlockHardness(this.parent.worldObj, pos.down(3));
                    if (atm <= 0.0f) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    if ((bHard += atm) >= this.maxHardness) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), 2.0);
                    List moblist = EntityQueries.entities(this.parent.worldObj, EntityLivingBase.class, axisalignedbb);
                    if (moblist.isEmpty()) {
                        entityout.setLocationAndAngles(x3 + randomx, y, z + randomz, this.parent.rotationYaw, this.parent.rotationPitch);
                        if (!this.parent.worldObj.getCollisionBoxes((Entity)entityout, Boxes.entityBox(entityout)).isEmpty()) {
                            randomx = rand.nextInt(max) + mini;
                            randomz = rand.nextInt(max) + mini;
                            negative = rand.nextInt(2);
                            if (negative == 0.0) {
                                randomx *= -1.0;
                            }
                            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                                randomz *= -1.0;
                            }
                            ++limit;
                            continue;
                        }
                        entityout.onInitialSpawn(DifficultyAccess.at(this.parent.worldObj, EntityQueries.position(entityout)), null);
                        entityout.setAttackTarget(in);
                        if (tele) {
                            this.resistanceI.add(entityout.getEntityId());
                            String name = this.parent.getParasiteStored();
                            if (name == null) {
                                entityout.setDead();
                                return false;
                            }
                            ((EntityDodT)entityout).setTele(name, this.stage);
                        } else {
                            this.parent.setActualParasites(1);
                            this.parent.addID(entityout.getEntityId(), 1);
                        }
                        entityout.setPeek(true);
                        entityout.setBuried();
                        if (this.parent.isBurning()) {
                            entityout.setFire(8);
                        }
                        this.parent.worldObj.spawnEntityInWorld((Entity)entityout);
                        this.tickss = 10.0;
                        this.parent.worldObj.setEntityState((Entity)entityout, (byte)50);
                        if (entityout.getParasiteIDRegister() == 74) {
                            ((EntityDodT)entityout).setFather(this.parent);
                        } else if (flagN) {
                            ((EntityNak)entityout).setFather(this.parent);
                            this.cooldownNak = 40;
                        }
                        flag = false;
                        return true;
                    }
                }
            }
            randomx = rand.nextInt(max) + mini;
            randomz = rand.nextInt(max) + mini;
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        entityout.setDead();
        return false;
    }

    private void unhide(AxisAlignedBB axisalignedbb) {
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            int key;
            if (mob == this.parent || mob instanceof EntityParasiteBase || !mob.onGround || mob.isInWater() || mob instanceof EntityPlayer) continue;
            if (!(mob instanceof EntityWaterMob) && this.parent.worldObj.rand.nextInt(1) == 0 && moblist.size() < this.parent.getMobList().size()) {
                this.summonTentacles(mob, 0.0, axisalignedbb, true);
            }
            if (!mob.isPotionActive(SRPPotions.COTH_E)) continue;
            NBTTagCompound tags = mob.getEntityData();
            mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 600, 5));
            if (!tags.hasKey("srpcothimmunity") || (key = tags.getInteger("srpcothimmunity")) < 1) continue;
            tags.setInteger("srpcothimmunity", key += 10);
        }
    }

    private void storePara(AxisAlignedBB axisalignedbb) {
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (!mob.isEntityAlive() || mob instanceof EntityPStationary || mob instanceof EntityPPreeminent || mob instanceof EntityPCosmical || mob instanceof EntityCanHaveBodies || mob.getAttackTarget() != null || !mob.onGround || mob.ticksExisted <= 100 || mob.canBeStored() != 0) continue;
            this.parent.storeParasite(mob);
        }
    }

    private boolean checkNak() {
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0, this.parent.posY + 1.0, this.parent.posZ + 1.0), 7.0, 5.0, 7.0);
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityNak.class, axisalignedbb);
        return moblist.isEmpty();
    }
}

