/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbScary;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Blocks;

import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityFlam
extends EntityPPreeminent {
    private EntityPPreeminent father;
    private BlockPos targetP;
    private int activationF;
    private boolean moveM;
    private byte typeDead;
    private byte steadyD;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityFlam.class, (DataSerializer)DataSerializers.BYTE);

    public EntityFlam(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(1.2f, 1.2f);
        this.setNoGravity(true);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.borderOrb = -1;
        this.canModRender = 1;
        this.experienceValue = SRPAttributes.XP_LiTTLE;
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
    }

    @Override
    public int getParasiteIDRegister() {
        return 89;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(4, (EntityAIBase)new AIChargeAttack());
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.FLAM_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.FLAM_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.FLAM_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.preeminentFollow);
    }

    public void setDamageATT(EntityParasiteBase in) {
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(in.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() * 2.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.isAIDisabled()) {
            return;
        }
        if (!this.worldObj.isRemote) {
            if (this.moveM) {
                ++this.activationF;
            }
            if (this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
                this.steadyD = (byte)(this.steadyD + 1);
            }
            if (this.steadyD > 20) {
                this.DeadDead();
                return;
            }
            if (this.targetP != null && this.father != null) {
                if (this.ticksExisted % 20 == 0 && this.getBlockH() != 0.0f) {
                    this.skillBreakBlocks();
                }
                if (this.getDistanceSq(this.targetP) < 4.0 || this.moveM) {
                    this.DeadDead();
                    return;
                }
            } else {
                this.activationF += 5000;
                this.DeadDead();
                return;
            }
            if (this.ticksExisted > 400) {
                this.activationF += 5000;
                this.DeadDead();
                return;
            }
        }
    }

    @Override
    protected void selfExplode() {
        if (this.worldObj.isRemote) {
            this.spawnEffectsGore();
        }
        if (!this.worldObj.isRemote) {
            this.spawnGore();
            this.playSound(SRPSounds.MOBEXPLOTION, 1.0f, 1.0f);
            this.dead = true;
            this.setDead();
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY - 2.0, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 4.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                this.attackEntityAsMob((Entity)mob);
            }
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY - 2.0, this.posZ);
            entityareaeffectcloud.setRadius(this.width * 3.5f, 0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new PotionEffect(net.minecraft.potion.Potion.poison, 300, 2));
            entityareaeffectcloud.addEffect(new PotionEffect(net.minecraft.potion.Potion.wither, 300, 2));
            entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
            this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
        }
    }

    public void DeadDead() {
        ++this.activationF;
        this.moveHelper.setMoveTo(this.posX, this.posY, this.posZ, 0.0);
        this.moveM = true;
        if (this.activationF < 20) {
            return;
        }
        block0 : switch (this.typeDead) {
            case 1: {
                this.selfExplode();
                break;
            }
            case 2: {
                EntityOrbScary ttt = new EntityOrbScary(this.worldObj, this.father, this.fuseOrb, this.orbStartTimer, false);
                ttt.copyLocationAndAnglesFrom((Entity)this);
                ttt.posY -= 3.0;
                this.worldObj.spawnEntityInWorld((Entity)ttt);
                this.playSound(SRPSounds.ORB_S, 1.0f, 1.0f);
                break;
            }
            case 3: {
                if (this.targetP != null) {
                    if (this.getDistanceSq(this.targetP) < 16.0) {
                        this.father.copyLocationAndAnglesFrom((Entity)this);
                        this.father.setTeleFlam(false);
                        break;
                    }
                    switch (this.rand.nextInt(2)) {
                        case 0: {
                            this.selfExplode();
                            break;
                        }
                        case 1: {
                            EntityOrbScary ttt1 = new EntityOrbScary(this.worldObj, this.father, this.fuseOrb, this.orbStartTimer, false);
                            ttt1.copyLocationAndAnglesFrom((Entity)this);
                            ttt1.posY -= 3.0;
                            this.worldObj.spawnEntityInWorld((Entity)ttt1);
                            this.playSound(SRPSounds.ORB_S, 1.0f, 1.0f);
                        }
                    }
                    break;
                }
                switch (this.rand.nextInt(2)) {
                    case 0: {
                        this.selfExplode();
                        break block0;
                    }
                    case 1: {
                        EntityOrbScary ttt1 = new EntityOrbScary(this.worldObj, this.father, this.fuseOrb, this.orbStartTimer, false);
                        ttt1.copyLocationAndAnglesFrom((Entity)this);
                        ttt1.posY -= 2.0;
                        this.worldObj.spawnEntityInWorld((Entity)ttt1);
                        this.playSound(SRPSounds.ORB_S, 1.0f, 1.0f);
                    }
                }
            }
        }
        this.spawnCyst();
        this.setDead();
    }

    public void setFatherTo(EntityPPreeminent in, int typeIn) {
        this.father = in;
        this.typeDead = (byte)typeIn;
    }

    public void onUpdate() {
        super.onUpdate();
        this.setNoGravity(true);
    }

    @Override
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
        if (entitylivingbaseIn != null) {
            this.targetP = EntityQueries.position(entitylivingbaseIn);
        }
    }

    @Override
    protected boolean summonFlam(EntityLivingBase in) {
        return false;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 0.5f;
    }

    public void moveEntity(MoverType type, double x, double y, double z) {
        if (!this.moveM) {
            super.moveEntity(type, x, y, z);
        }
        this.doBlockCollisions();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(VEX_FLAGS, (Object)((byte)0));
    }

    @Override
    protected void spawnGore() {
        double i1 = MathHelper.floor_double((double)(this.posY + 0.1));
        double l1 = this.posX;
        double i2 = this.posZ;
        for (int i = 0; i < 10 && SRPConfig.paraGore; ++i) {
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
            bomb.setType((byte)12);
            bomb.copyLocationAndAnglesFrom((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.25, 0.65);
            this.worldObj.spawnEntityInWorld((Entity)bomb);
        }
    }

    private boolean getVexFlag(int mask) {
        byte i = (Byte)EntityDataManager.of(this).get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = ((Byte)EntityDataManager.of(this).get(VEX_FLAGS)).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        EntityDataManager.of(this).set(VEX_FLAGS, (Object)((byte)(i & 0xFF)));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 100) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(EnumParticleTypes.FLAME);
            }
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.isDead) {
            return;
        }
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        int i1 = MathHelper.floor_double((double)(this.posY + 0.1));
        double l1 = this.posX;
        double i2 = this.posZ;
        boolean flag = false;
        int Brangeatm = this.BGrange;
        boolean offsetT = false;
        for (int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                for (int j = -1; j <= this.BGheight + 1; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = new BlockPos(i3, k, l);
                    IBlockState iblockstate = BlockStates.get(this.worldObj, blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getBlockHardness(this.worldObj, blockpos);
                    if (!(bHard <= this.getBlockH()) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister || block == SRPBlocks.BiomeHeart || block == SRPBlocks.ColonyHeart || block == SRPBlocks.ParasiteRubbleDense || block == SRPBlocks.ParasiteCanisterActive || this.blockException(name = RegistryNames.get(block).toString()) || block == Blocks.air || !block.canEntityDestroy(iblockstate, (IBlockAccess)this.worldObj, blockpos, (Entity)this) || !ForgeEventFactory.onEntityDestroyBlock((EntityLivingBase)this, (BlockPos)blockpos, (IBlockState)iblockstate)) continue;
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

    class AIChargeAttack
    extends EntityAIBase {
        public AIChargeAttack() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            if (EntityFlam.this.targetP != null && !EntityFlam.this.getMoveHelper().isUpdating() && EntityFlam.this.rand.nextInt(7) == 0 && !EntityFlam.this.moveM) {
                return EntityFlam.this.getDistanceSq(EntityFlam.this.targetP) > 4.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityFlam.this.isCharging() && EntityFlam.this.targetP != null && !EntityFlam.this.moveM;
        }

        public void startExecuting() {
            if (EntityFlam.this.getAttackTarget() != null && EntityFlam.this.getAttackTarget().isEntityAlive()) {
                EntityFlam.this.targetP = EntityFlam.this.getAttackTarget().getPosition();
            }
            EntityFlam.this.moveHelper.setMoveTo((double)EntityFlam.this.targetP.getX() + 0.5, (double)(EntityFlam.this.targetP.getY() + 2), (double)EntityFlam.this.targetP.getZ() + 0.5, 0.8);
            EntityFlam.this.setCharging(true);
        }

        public void resetTask() {
            EntityFlam.this.setCharging(false);
        }

        public void updateTask() {
            double d0;
            if (EntityFlam.this.getAttackTarget() != null && EntityFlam.this.getAttackTarget().isEntityAlive()) {
                EntityFlam.this.targetP = EntityFlam.this.getAttackTarget().getPosition();
            }
            if (EntityFlam.this.targetP != null && (d0 = EntityFlam.this.getDistanceSq(EntityFlam.this.targetP)) < 9.0) {
                EntityFlam.this.moveHelper.setMoveTo((double)EntityFlam.this.targetP.getX() + 0.5, (double)(EntityFlam.this.targetP.getY() + 2), (double)EntityFlam.this.targetP.getZ() + 0.5, 0.8);
            }
        }
    }

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            return !EntityFlam.this.getMoveHelper().isUpdating() && EntityFlam.this.rand.nextInt(7) == 0 && !EntityFlam.this.moveM;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityFlam.this);
            int flag = 1;
            double speed = 1.0;
            if (EntityFlam.this.getAttackTarget() != null && EntityFlam.this.getAttackTarget().isEntityAlive()) {
                EntityFlam.this.targetP = EntityFlam.this.getAttackTarget().getPosition();
            }
            if (EntityFlam.this.targetP != null) {
                blockpos = EntityFlam.this.targetP;
                flag = 2;
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityFlam.this.rand.nextInt(15) - 7, EntityFlam.this.rand.nextInt(11) - 5, EntityFlam.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityFlam.this.rand.nextInt(6) - 2, EntityFlam.this.rand.nextInt(7) - 2, EntityFlam.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityFlam.this.rand.nextInt(4) + 3, EntityFlam.this.rand.nextInt(5) + 4, EntityFlam.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityFlam.this.worldObj, blockpos1)) continue;
                EntityFlam.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityFlam.this.getAttackTarget() != null) break;
                EntityFlam.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityFlam vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO && !EntityFlam.this.moveM) {
                double d0 = this.posX - EntityFlam.this.posX;
                double d1 = this.posY - EntityFlam.this.posY;
                double d2 = this.posZ - EntityFlam.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityFlam.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityFlam.this.motionX *= 0.5;
                    EntityFlam.this.motionY *= 0.5;
                    EntityFlam.this.motionZ *= 0.5;
                } else {
                    EntityFlam.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityFlam.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityFlam.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityFlam.this.getAttackTarget() == null) {
                        EntityFlam.this.renderYawOffset = EntityFlam.this.rotationYaw = -((float)MathHelper.atan2((double)EntityFlam.this.motionX, (double)EntityFlam.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityFlam.this.getAttackTarget().posX - EntityFlam.this.posX;
                        double d5 = EntityFlam.this.getAttackTarget().posZ - EntityFlam.this.posZ;
                        EntityFlam.this.renderYawOffset = EntityFlam.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

