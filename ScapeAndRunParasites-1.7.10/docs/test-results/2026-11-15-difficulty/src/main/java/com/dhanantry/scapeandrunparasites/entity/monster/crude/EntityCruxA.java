/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.item.EntityFallingBlock
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCruxA
extends EntityPCrude
implements EntityCutomAttack {
    private float attackTimerM;
    private boolean upM;
    private float attackTimerR;
    private boolean upR;
    private int extraDamageCap;
    private int currentDamageTimes;
    private double baseDamage;
    private byte cooldownBack;
    private boolean canBack;
    private int limit;
    private int border;
    private double targetX;
    private double targetZ;
    private double mottY;
    private boolean skillThrow;

    public EntityCruxA(World worldIn) {
        super(worldIn);
        this.setSize(1.13333f, 3.3f);
        this.stepHeight = 1.0f;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, false, false, null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, false, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        this.type = (byte)41;
        this.skillThrow = false;
        this.currentDamageTimes = 0;
        this.extraDamageCap = SRPConfigMobs.cruxaDamageCap;
        this.baseDamage = -1.0;
        this.cooldownBack = 0;
        this.canBack = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 62;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.11));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 60, 20, 12, true, 1));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatusAOE(this, 1.3, true, 0.0, 3.0));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.CRUXA_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.CRUXA_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.24);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.CRUXA_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.CRUXA_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.upM) {
            this.attackTimerM = (float)((double)this.attackTimerM + 0.2);
            if ((double)this.attackTimerM > 0.9) {
                this.upM = false;
            }
        } else {
            this.attackTimerM = (float)((double)this.attackTimerM - 0.1);
        }
        if (this.upR) {
            this.attackTimerR = (float)((double)this.attackTimerR + 0.4);
            if ((double)this.attackTimerR > 2.2) {
                this.upR = false;
            }
        } else {
            this.attackTimerR = (float)((double)this.attackTimerR - 0.2);
        }
    }

    private void popBack() {
        if (this.canBack) {
            if (this.srpTicks == 10) {
                this.cooldownBack = (byte)(this.cooldownBack + 1);
            }
            if (this.cooldownBack < 20) {
                return;
            }
            this.canBack = false;
            this.playSound(SRPSounds.MOBEXPLOTION, 3.0f, 1.0f);
            this.cooldownBack = 0;
            if (this.worldObj.isRemote) {
                int i;
                for (i = 0; i <= 180; ++i) {
                    if (i % 5 != 0) continue;
                    this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1, 5.0, 9.0);
                }
                for (i = 0; i <= 80; ++i) {
                    if (i % 5 == 0) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                    }
                    if (i % 5 != 0) continue;
                    this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
                }
                return;
            }
            int range = 4;
            double i1 = MathHelper.floor_double((double)(this.posY + 0.1));
            double l1 = this.posX;
            double i2 = this.posZ;
            for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
                for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                    double i3 = l1 + (double)k2;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = new BlockPos(i3, i1, l);
                    Block block = BlockStates.get(this.worldObj, blockpos).getBlock();
                    Block blockDown = BlockStates.get(this.worldObj, blockpos.down()).getBlock();
                    if (block != Blocks.air || blockDown == Blocks.air || !BlockStates.get(this.worldObj, blockpos.down()).isFullBlock() || blockDown == SRPBlocks.InfestedStain || this.worldObj.rand.nextInt(4) != 0) continue;
                    BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.goreFer).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.FLAT)));
                }
            }
            for (int i = 0; i < 7 && SRPConfig.paraGore; ++i) {
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
                d3 = d3 * (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)) * 7.0;
                d4 = d4 * d7 * 12.0;
                d5 = d5 * d7 * 7.0;
                EntityGore bomb = new EntityGore(this.worldObj);
                bomb.setType((byte)1);
                bomb.copyLocationAndAnglesFrom((Entity)this);
                bomb.setMotion(d3, d4, d5, 0.28, 0.55);
                bomb.posY += 3.5;
                this.worldObj.spawnEntityInWorld((Entity)bomb);
            }
        } else {
            if (this.srpTicks == 10) {
                this.cooldownBack = (byte)(this.cooldownBack + 1);
            }
            if (this.cooldownBack > 15) {
                this.cooldownBack = 0;
                this.canBack = true;
            }
        }
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        if (this.extraDamageCap > this.currentDamageTimes) {
            return;
        }
        ++this.currentDamageTimes;
        if (this.baseDamage == -1.0) {
            this.baseDamage = this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * SRPConfigMobs.cruxaDamageGain;
        }
        double getD = this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(getD + this.baseDamage);
    }

    public float getEyeHeight() {
        return 3.3f;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean getBack() {
        return this.canBack;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.CRUX_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.CRUX_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.CRUX_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.upM = true;
        this.attackTimerM = 0.0f;
        this.worldObj.setEntityState((Entity)this, (byte)22);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE, 2.0f, 1.0f);
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0, entityIn.posY + 1.0, entityIn.posZ + 1.0), 1.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob instanceof EntityParasiteBase) {
                if (this.getAttackTarget() != mob) continue;
                this.setAttackTarget(null);
                return false;
            }
            if (mob == this || !this.canEntityBeSeen((Entity)mob) || !this.attackEntityAsMob((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (SRPConfigSystems.rageEnable) {
            this.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1333320, 0, false, false));
        }
        return floo;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setDouble("parasitecruxdamage", this.baseDamage);
        compound.setInteger("parasitecruxtimes", this.currentDamageTimes);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("parasitecruxdamage", 99)) {
            this.baseDamage = compound.getDouble("parasitecruxdamage");
        }
        if (compound.hasKey("parasitecruxtimes", 99)) {
            this.currentDamageTimes = compound.getInteger("parasitecruxtimes");
        }
    }

    @SideOnly(value=Side.CLIENT)
    public float getAttackTimerM() {
        return this.attackTimerM;
    }

    @SideOnly(value=Side.CLIENT)
    public float getAttackTimerR() {
        return this.attackTimerR;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 22) {
            this.upM = true;
            this.attackTimerM = 0.0f;
        } else if (id == 23) {
            this.upR = true;
            this.attackTimerR = 0.0f;
        } else if (id == 24) {
            this.canBack = true;
        } else if (id == 25) {
            this.canBack = false;
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillThrow;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillThrow = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.throwBlock();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void throwBlock() {
        EntityLivingBase entitylivingbase = this.getAttackTarget();
        if (entitylivingbase != null) {
            this.targetX = entitylivingbase.posX;
            this.targetZ = entitylivingbase.posZ;
            if (this.getDistanceSqToEntity((Entity)entitylivingbase) < 144.0 || entitylivingbase.posY < this.posY || entitylivingbase.posY > this.posY + 3.0) {
                this.skillThrow = true;
                this.setParasiteStatus(0);
                this.border = 0;
                this.limit = 0;
                return;
            }
        } else {
            this.skillThrow = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
            return;
        }
        this.mottY = this.getDistanceSqToEntity((Entity)entitylivingbase);
        if (this.ticksExisted % 20 != 0) {
            return;
        }
        ++this.border;
        IBlockState b = this.blockNotAvailable();
        if (b == null) {
            ++this.limit;
            if (this.limit >= 5) {
                this.skillThrow = true;
                this.setParasiteStatus(0);
                this.border = 0;
                this.limit = 0;
            }
            return;
        }
        this.skillBreakBlocks();
        this.setParasiteStatus(10);
        this.getNavigator().tryMoveToXYZ(this.targetX, entitylivingbase.posY, this.targetZ, 0.0);
        this.worldObj.setEntityState((Entity)this, (byte)23);
        BlockPos pos = EntityQueries.position(this);
        Vec3d vec3d = this.getLook(1.0f);
        EntityFallingBlock entityfallingblock = new EntityFallingBlock(this.worldObj, this.posX + vec3d.xCoord, this.posY + (double)this.getEyeHeight() - 0.7, this.posZ + vec3d.zCoord, b);
        entityfallingblock.fallTime = -100;
        this.targetX += Math.random() * 2.0 - 1.0;
        this.targetZ += Math.random() * 2.0 - 1.0;
        double d0 = this.targetX - this.posX;
        double d1 = this.targetZ - this.posZ;
        double f = MathHelper.sqrt_double((double)(d0 * d0 + d1 * d1));
        entityfallingblock.motionY = this.mottY * 0.0;
        if (entitylivingbase.posY >= this.posY + 2.0) {
            entityfallingblock.motionY += 0.5;
        }
        if (entitylivingbase.posY <= this.posY - 2.0) {
            entityfallingblock.motionY += -0.5;
        }
        double mmx = d0 / f * (this.mottY * 0.009) * 0.9 + entityfallingblock.motionX * 0.3;
        double mmz = d1 / f * (this.mottY * 0.009) * 0.9 + entityfallingblock.motionZ * 0.3;
        entityfallingblock.motionX += mmx;
        entityfallingblock.motionZ += mmz;
        EntityDamage damage = new EntityDamage(this.worldObj, entityfallingblock.posX, entityfallingblock.posY, entityfallingblock.posZ, 0.0f, (EntityLivingBase)this, 10.0f + b.getBlockHardness(this.worldObj, EntityQueries.position(this)), false, 0.0f);
        damage.setFollower((Entity)entityfallingblock);
        this.worldObj.spawnEntityInWorld((Entity)entityfallingblock);
        this.worldObj.spawnEntityInWorld((Entity)damage);
        if (this.limit >= 5 || this.border > 8) {
            this.skillThrow = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
        }
    }

    private IBlockState blockNotAvailable() {
        BlockPos pos;
        IBlockState b = null;
        int range = 2;
        int newX = this.rand.nextInt(range) + 1;
        int newZ = this.rand.nextInt(range) + 1;
        if (this.rand.nextBoolean()) {
            newX *= -1;
        }
        if (this.rand.nextBoolean()) {
            newZ *= -1;
        }
        if ((pos = ParasiteEventEntity.getFloor(this.worldObj, new BlockPos(this.posX + (double)newX, this.posY, this.posZ + (double)newZ), 4)) != null) {
            if (this.checkBlock(BlockStates.get(this.worldObj, pos = pos.down()).getBlock(), BlockStates.get(this.worldObj, pos), pos)) {
                return null;
            }
            b = BlockStates.get(this.worldObj, pos);
            BlockStates.set(this.worldObj, pos, BlockStates.defaultState(Blocks.air));
        }
        return b;
    }

    private boolean checkBlock(Block in, IBlockState state, BlockPos pos) {
        return in == Blocks.air || in instanceof BlockLiquid || !state.isFullBlock() || state.getBlockHardness(this.worldObj, pos) <= 0.0f || this.blockException(RegistryNames.get(in).toString());
    }
}

