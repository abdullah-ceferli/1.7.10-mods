/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEndGateway
 *  net.minecraft.block.BlockEndPortalFrame
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockPortal
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIAttackMelee
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 */
package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndGateway;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityWaveShock
extends EntityParasiteBase {
    private int raaa;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int duration;
    private boolean canTarget;
    EntityParasiteBase caster;

    public EntityWaveShock(World worldIn) {
        super(worldIn);
        this.setSize(3.1f, 0.2f);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.killcount = -10.0;
        this.MiniDamage = 0.1f;
        this.canTarget = true;
        this.duration = 1;
    }

    public EntityWaveShock(World worldIn, EntityParasiteBase father) {
        this(worldIn);
        this.caster = father;
    }

    protected void initEntityAI() {
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMelee((EntityCreature)this, 1.0, false));
    }

    @Override
    public void applyBonuses(SRPSaveData saveData, World world) {
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20.0);
    }

    public void setDamages(double baseDamage, float min, int range, int durationF) {
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(baseDamage);
        this.MiniDamage = min;
        this.raaa = range;
        this.duration = durationF;
    }

    @Override
    public int getParasiteIDRegister() {
        return 213;
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote) {
            IBlockState state = BlockStates.get(this.worldObj, EntityQueries.position(this).down());
            if (state.getBlock() != Blocks.air) {
                int id = BlockStates.stateId((IBlockState)state);
                for (int i = 0; i < 35; ++i) {
                    com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.BLOCK_CRACK, this.posX + (double)this.rand.nextFloat() * ((double)this.width * 1.2) * 2.0 - (double)this.width * 1.2, this.posY, this.posZ + (double)this.rand.nextFloat() * ((double)this.width * 1.2) * 2.0 - (double)this.width * 1.2, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() + 140.0, this.rand.nextGaussian() * 0.02, new int[]{id});
                }
            }
        } else {
            if (this.targetX == 0.0 || this.caster == null) {
                this.setDead();
                return;
            }
            if (BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockLiquid) {
                this.setDead();
                return;
            }
            if (this.ticksExisted > 20) {
                if (this.posX == this.prevPosX || this.posZ == this.prevPosZ) {
                    this.setDead();
                }
                if (this.ticksExisted > 20 * this.duration) {
                    this.setDead();
                    return;
                }
            }
            this.skillBreakBlocks();
            float f = this.width / 2.0f;
            float f1 = this.height;
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f), 1.5, 0.2, 1.5);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                this.attackEntityAsMob((Entity)mob);
            }
            if (this.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 2.0) {
                this.getMoveHelper().setMoveTo(this.targetX, this.targetY, this.targetZ, 0.6);
            }
            if (this.posX == this.targetX && this.posZ == this.targetZ) {
                this.setDead();
            }
        }
    }

    protected void jump() {
        this.setDead();
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if (this.caster == null) {
            return false;
        }
        boolean flag = this.caster.attackEntityAsMob(entityIn);
        if (flag) {
            entityIn.motionY += 0.64645;
        }
        return flag;
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
    }

    protected void collideWithNearbyEntities() {
    }

    public AxisAlignedBB getBoundingBox() {
        return Boxes.create(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        if (this.ticksExisted > 40 || this.caster == null || entitylivingbaseIn == null) {
            this.setDead();
            return;
        }
        if (this.canTarget) {
            this.targetX = entitylivingbaseIn.posX;
            this.targetY = this.caster.posY;
            this.targetZ = entitylivingbaseIn.posZ;
            this.blockH = this.caster.getBlockH();
            this.BGrange = 2;
            this.BGheight = 3;
            this.canTarget = false;
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.isDead || this.caster == null) {
            return;
        }
        int blocksbroke = 0;
        if (!ForgeEventFactory.getMobGriefingEvent((World)this.worldObj, (Entity)this)) {
            return;
        }
        int i1 = MathHelper.floor_double((double)this.posY);
        double l1 = this.posX;
        double i2 = this.posZ;
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = 0;
        for (int k2 = -1 * this.BGrange; k2 <= 1 * this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= 1 * this.BGrange; ++l2) {
                for (int j = offsetT; j <= this.BGheight + offsetT; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = new BlockPos(i3, k, l);
                    IBlockState iblockstate = BlockStates.get(this.worldObj, blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getBlockHardness(this.worldObj, blockpos);
                    if (!(bHard <= this.blockH) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister || block == SRPBlocks.BiomeHeart || block == SRPBlocks.ColonyHeart || block == SRPBlocks.ParasiteRubbleDense || block == SRPBlocks.ParasiteCanisterActive || block == SRPBlocks.dodN || block instanceof BlockLiquid || block instanceof BlockPortal || block instanceof BlockEndGateway || block instanceof BlockEndPortalFrame || iblockstate.getMaterial() == Material.portal || this.blockException(name = RegistryNames.get(block).toString()) || block == Blocks.air || !block.canEntityDestroy(iblockstate, (IBlockAccess)this.worldObj, blockpos, (Entity)this) || !ForgeEventFactory.onEntityDestroyBlock((EntityLivingBase)this, (BlockPos)blockpos, (IBlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        int meta = BlockStates.meta(block, iblockstate);
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.caster.addToBlockInv(name + ";" + meta);
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
}

